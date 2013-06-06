package com.dataiku.dip.input.formats;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;

import com.dataiku.dip.datalayer.Column;
import com.dataiku.dip.datalayer.ColumnFactory;
import com.dataiku.dip.datalayer.ProcessorOutput;
import com.dataiku.dip.datalayer.Row;
import com.dataiku.dip.datalayer.RowFactory;
import com.dataiku.dip.input.StreamInputSplitProgressListener;
import com.dataiku.dip.input.stream.EnrichedInputStream;
import com.dataiku.dip.input.stream.StreamsInputSplit;
import com.google.common.io.CountingInputStream;

public class CSVFormatExtractor extends AbstractFormatExtractor  {
    public CSVFormatExtractor(CSVFormatConfig conf) {
        this.conf = conf;
    }

    private CSVFormatConfig conf;

    @Override
    public boolean run(StreamsInputSplit in, ProcessorOutput out, ProcessorOutput err,
            ColumnFactory cf, RowFactory rf, StreamInputSplitProgressListener listener,
            ExtractionLimit limit) throws Exception {
        logger.info("CSV running with separator : '" + conf.separator + "'");
        
        long totalBytes = 0, nlines = 0;
        while (true) {
            EnrichedInputStream stream = in.nextStream();

            if (stream == null) break;
            logger.info("CSV starting to process one stream: " + stream.size());

            InputStream is = stream.stream();
            CountingInputStream cis = new CountingInputStream(is);

            /* INCREDIBLY CRUDE HACK TO SET A STATIC FINAL FIELD !! HAHAHA ! */
            //            Field modifiersField = Field.class.getDeclaredField("modifiers");
            //            modifiersField.setAccessible(true);
            //            Field irsField = CSVParser.class.getField("INITIAL_READ_SIZE");
            //            modifiersField.setInt(irsField, irsField.getModifiers() & ~Modifier.FINAL);
            //            irsField.set(null, 2048);

            CSVReader reader = null;
            if (conf.escapeChar != null) {
                reader = new CSVReader(new InputStreamReader(cis, conf.charset), conf.separator,
                    conf.quoteChar, conf.escapeChar);
            } else {
                reader = new CSVReader(new InputStreamReader(cis, conf.charset), conf.separator,
                        conf.quoteChar);
                
            }
            try {
                List<Column> columns = new ArrayList<Column>();
                for (int i = 0; i < conf.skipRowsBeforeHeader; i++) {
                    String[] line = reader.readNext();
                    if (line == null) {
                        out.lastRowEmitted();
                        break;
                    }
                }
                if (conf.parseHeaderRow) {
                    String[] line = reader.readNext();
                    if (line == null) {
                        out.lastRowEmitted();
                    }
                    if (line[0].startsWith("#")) {
                        line[0] = line[0].substring(1);
                    }
                    for (String ch : line) {
                        ch = ch.trim();
                        Column cd = cf.column(ch);
                        columns.add(cd);
                    }
                }
                for (int i = 0; i < conf.skipRowsAfterHeader; i++) {
                    String[] line = reader.readNext();
                    if (line == null) {
                        out.lastRowEmitted();
                        break;
                    }
                }

                long nintern = 0;
                while (true) {
                    String[] line = reader.readNext();
                    if (line == null) break;
                    if (limit != null) {
                        if (limit.maxBytes > 0 && limit.maxBytes < cis.getCount()) return false;
                        if (limit.maxRecords > 0 && limit.maxRecords < nlines) return false;
                    }

                    if (columns.size() > 0 && line.length != columns.size()) {
//                        logger.info("Line has a changing number of columns, line has " + line.length + " columns, but I have " + columns.size());
                    }

                    if (line.length > columns.size()) {
                        for (int i = columns.size() ; i < line.length; i++) {
                            String name = null;
                            if (schema() != null && schema().getColumns().size() > i) {
                                name = schema().getColumns().get(i).getName();
                            } else {
                                name = "col_" + i;
                            }
                            Column cd = cf.column(name);
                            columns.add(cd);
                        }
                    }
                    Row r = rf.row();
                    for (int i = 0; i < line.length; i++) {
                        line[i] = line[i].trim();  // trim returns a reference and does not reallocate if there is no whitespace to trim
                        if (line[i].length() > 1000) {
                            System.out.println("LARGE :" + line[i]);
                        }
                        String s = line[i];

                        /* Replace common strings by their intern versions */
                        if (s.equals("null")) { s = "null"; ++nintern; }
                        else if (s.equals("true")) { s = "true"; ++nintern; }
                        else if (s.equals("false")) { s = "false"; ++nintern; }
                        else if (s.equals("Y")) { s = "Y"; ++nintern; }
                        else if (s.equals("N")) { s = "N"; ++nintern; }
                        else if (s.equals("0")) { s = "0"; ++nintern; }

                        r.put(columns.get(i), s);
                    }
                    out.emitRow(r);

                    if (nlines++ % 2000 == 0) {
                        Runtime runtime = Runtime.getRuntime();
                        double p = ((double) runtime.totalMemory()) / runtime.maxMemory() * 100;
                        logger.info("CSV Emitted " + nlines + " lines-  " + columns.size() + " columns - interned: " + nintern + " MEM: "
                                + p + "%");
                    }

                    if (listener != null && nlines % 500 == 0) {
                        synchronized (listener) {
                            //logger.info("Setting listener " + (totalBytes + cis.getCount()));
                            listener.setErrorRecords(0);
                            listener.setReadBytes(totalBytes + cis.getCount());
                            listener.setReadRecords(nlines);
                        }
                    }
                }
                /* Set the final listener data */
                if (listener != null) {
                    synchronized (listener) {
                        listener.setErrorRecords(0);
                        listener.setReadBytes(totalBytes + cis.getCount());
                        listener.setReadRecords(nlines);
                    }
                    totalBytes += cis.getCount();
                }
            } finally {
                reader.close();
            }
        }
        out.lastRowEmitted();
        return true;
    }

    Logger logger = Logger.getLogger("csv");
}
