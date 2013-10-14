package com.dataiku.dip.input.formats;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;

import com.dataiku.dip.datalayer.Column;
import com.dataiku.dip.datalayer.ColumnFactory;
import com.dataiku.dip.datalayer.ProcessorOutput;
import com.dataiku.dip.datalayer.Row;
import com.dataiku.dip.datalayer.RowFactory;
import com.dataiku.dip.input.stream.EnrichedInputStream;
import com.dataiku.dip.input.stream.StreamsInputSplit;
import com.dataiku.dip.utils.DKULogger;
import com.google.common.io.CountingInputStream;

public class BasicCSVFormatExtractor extends AbstractBasicFormatExtractor  {
    public BasicCSVFormatExtractor(BasicCSVFormatConfig conf) {
        this.conf = conf;
    }

    private BasicCSVFormatConfig conf;


    long log_count = 0;
    long max_log = 100;
    protected void log_line_warn(String msg) {
        log_count ++;
        if (log_count == max_log) {
            logger.warn("Maximum log count (other info ignore)");
        } else if (log_count < max_log) {
            logger.warn(msg);
        }
    }

    @Override
    public boolean run(StreamsInputSplit in, ProcessorOutput out, ProcessorOutput err,
            ColumnFactory cf, RowFactory rf,  ExtractionLimit limit) throws Exception {
        long totalBytes = 0, totalRecords = 0;
        while (true) {
            EnrichedInputStream stream = in.nextStream();
            if (stream == null) break;

            logger.info("CSV starting to process one stream: " + stream.desc() +  "  " + stream.size());

            InputStream is = stream.decompressedHeadStream(limit != null ? limit.maxBytes : -1); 
            CountingInputStream cis = new CountingInputStream(is);

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

                long fileLines = 0, nintern = 0;

                while (true){
                    String[] line = reader.readNext();
                    if (line == null) break;
                    if (limit != null) {
                        if (limit.maxBytes > 0 && limit.maxBytes < totalBytes + cis.getCount()) return false;
                        if (limit.maxRecords > 0 && limit.maxRecords <= totalRecords) return false;
                    }

                    if (fileLines < conf.skipRowsBeforeHeader) {
                        // Do nothing
                    } else if (fileLines == conf.skipRowsBeforeHeader && conf.parseHeaderRow) {
                        if (line[0].startsWith("#")) {
                            line[0] = line[0].substring(1);
                        }
                        for (String ch : line) {
                            ch = ch.trim();
                            // Sometimes, people leave holes in the header ...
                            if (ch.isEmpty()) {
                                ch = "col_" + columns.size();
                            }
                            Column cd = cf.column(ch);
                            columns.add(cd);
                        }
                    } else {
                        if (columns.size() > 0 && line.length != columns.size() && Math.abs(line.length - columns.size()) > 2) {
                            log_line_warn("Line has an unexpected number of columns, line has " + line.length +
                                    " columns, extractor has " + columns.size());
                        }

                        if (line.length > columns.size()) {
                            for (int i = columns.size() ; i < line.length; i++) {
                                String name = null;
                                {
                                    name = "col_" + i;
                                }
                                Column cd = cf.column(name);
                                columns.add(cd);
                            }
                        }
                        Row r = rf.row();
                        for (int i = 0; i < line.length; i++) {
                            line[i] = line[i].trim();  // trim returns a reference and does not reallocate if there is no whitespace to trim
                            if (line[i].length() > 3000) {
                                log_line_warn("Unusually large column (quoting issue ?) : " + line[i]);
                            }
                            String s = line[i];

                            /* Replace common strings by their intern versions */
                            if (s.equals("null")) { s = "null"; ++nintern; }
                            else if (s.equals("NULL")) { s = "NULL"; ++nintern; }
                            else if (s.equals("\\N")) { s = "\\N"; ++nintern; }
                            else if (s.equals("true")) { s = "true"; ++nintern; }
                            else if (s.equals("false")) { s = "false"; ++nintern; }
                            else if (s.equals("Y")) { s = "Y"; ++nintern; }
                            else if (s.equals("N")) { s = "N"; ++nintern; }
                            else if (s.equals("0")) { s = "0"; ++nintern; }

                            r.put(columns.get(i), s);
                        }
                        if (fileLines >= conf.skipRowsBeforeHeader + conf.skipRowsAfterHeader+ (conf.parseHeaderRow?1:0)) {
                            totalRecords++;
                            out.emitRow(r);
                        }
                    }
                    fileLines++;

                    if (totalRecords % 20000 == 0) {
                        Runtime runtime = Runtime.getRuntime();
                        double p = ((double) runtime.totalMemory()) / runtime.maxMemory() * 100;
                        logger.info("CSV Emitted " + fileLines + " lines from file, " + totalRecords + " total, " +
                                columns.size() + " columns - interned: " + nintern + " MEM: " + p + "%");
                    }
                }
                totalBytes += cis.getCount();
            } finally {
                long before = System.currentTimeMillis();
                reader.close();
                IOUtils.closeQuietly(cis);
                long now = System.currentTimeMillis();
                if (now - before > 100) {
                    logger.info("Stream " + stream.desc() + " closed after " + (now -before)+  "ms");
                }
            }
        }
        return true;
    }

    private static Logger logger = DKULogger.getLogger("dku.format.csv");
}
