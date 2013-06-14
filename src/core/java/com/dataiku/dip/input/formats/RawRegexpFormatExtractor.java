package com.dataiku.dip.input.formats;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.dataiku.dip.datalayer.Column;
import com.dataiku.dip.datalayer.ColumnFactory;
import com.dataiku.dip.datalayer.ProcessorOutput;
import com.dataiku.dip.datalayer.Row;
import com.dataiku.dip.datalayer.RowFactory;
import com.dataiku.dip.input.StreamInputSplitProgressListener;
import com.dataiku.dip.input.stream.EnrichedInputStream;
import com.dataiku.dip.input.stream.StreamsInputSplit;
import com.google.common.io.CountingInputStream;

public class RawRegexpFormatExtractor extends AbstractFormatExtractor  {
    private String charset;
    private Pattern pattern;
    private List<String> captureNames = new ArrayList<String>();
    public RawRegexpFormatExtractor(String charset, String regex, List<String> captureNames) {
        this.charset = charset;
        this.pattern = Pattern.compile(regex);
        this.captureNames = captureNames;
    }

    public boolean find(String line) {
        Matcher m = pattern.matcher(line);
        return m.find();
    }
    
    @Override
    public boolean run(StreamsInputSplit in, ProcessorOutput out, ProcessorOutput err,
            ColumnFactory cf, RowFactory rf, StreamInputSplitProgressListener listener,
            ExtractionLimit limit) throws Exception {

        List<Column> columns = new ArrayList<Column>();
        for (String columnName : captureNames) {
            columns.add(cf.column(columnName));
        }

        while (true) {
            EnrichedInputStream stream = in.nextStream();
            if (stream == null) break;

            InputStream is = stream.stream();
            CountingInputStream cis = new CountingInputStream(is);

            BufferedReader br = new BufferedReader(new InputStreamReader(cis, charset));
            boolean broken = false;
            try {
                long nlines = 0;
                Matcher m = pattern.matcher(""); 

                while (true) {
                    String line = br.readLine();
                    if (line == null) {
                        break;
                    }
                    line = line.trim();
                    m.reset(line);
                    
                    if (m.find()) {
//                        System.out.println("DID PARSE" + line);
                        Row r = rf.row();
                        for (int i = 0; i < m.groupCount(); i++) {
                            String group = m.group(i+1);
                            r.put(columns.get(i), group);
                        }
                        out.emitRow(r);
                    } else {
//                        System.err.println("Did not parse " + line);
                        Row r = rf.row();
                        r.put(cf.column("reject"), line);
                        err.emitRow(r);
                    }
                    nlines++;
                    if (limit != null && nlines >= limit.maxRecords) {
                        broken = true;
                        break;
                    }
                    
                    if (listener != null && nlines % 50 == 0) {
                        synchronized (listener) {
                            listener.setErrorRecords(0);
                            listener.setReadBytes(cis.getCount());
                            listener.setReadRecords(nlines);
                        }
                    }
                }
                /* Set the final listener data */
                if (listener != null) {
                    synchronized (listener) {
                        listener.setErrorRecords(0);
                        listener.setReadBytes(cis.getCount());
                        listener.setReadRecords(nlines);
                    }
                }
            } finally {
                br.close();
            }
            if (broken) {
                out.lastRowEmitted();
                return false;
            }
        }
        out.lastRowEmitted();
        return true;
    }

    Logger logger = Logger.getLogger("csv");
}
