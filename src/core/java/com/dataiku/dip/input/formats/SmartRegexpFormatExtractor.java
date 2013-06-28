package com.dataiku.dip.input.formats;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.dataiku.dip.datalayer.Column;
import com.dataiku.dip.datalayer.ColumnFactory;
import com.dataiku.dip.datalayer.ProcessorOutput;
import com.dataiku.dip.datalayer.Row;
import com.dataiku.dip.datalayer.RowFactory;
import com.dataiku.dip.input.StreamInputSplitProgressListener;
import com.dataiku.dip.input.stream.EnrichedInputStream;
import com.dataiku.dip.input.stream.StreamsInputSplit;
import com.dataiku.dip.utils.RegexpFieldsBuilder;
import com.google.common.io.CountingInputStream;

public class SmartRegexpFormatExtractor extends AbstractFormatExtractor  {
    private RegexpFieldsBuilder regexpFieldsBuilder;
    public SmartRegexpFormatExtractor(RegexpFieldsBuilder regexpFieldsBuilder) {
        this.regexpFieldsBuilder = regexpFieldsBuilder;
    }

    @Override
    public boolean run(StreamsInputSplit in, ProcessorOutput out, ProcessorOutput err,
            ColumnFactory cf, RowFactory rf, StreamInputSplitProgressListener listener,
            ExtractionLimit limit) throws Exception {

        List<Column> columns = new ArrayList<Column>();
        for (String columnName : regexpFieldsBuilder.getColumnNames()) {
            columns.add(cf.column(columnName));
        }

        while (true) {
            EnrichedInputStream stream = in.nextStream();
            if (stream == null) break;

            InputStream is = stream.stream();
            CountingInputStream cis = new CountingInputStream(is);
            // TODO CHARSET
            BufferedReader br = new BufferedReader(new InputStreamReader(cis, "utf8"));

            try {
                long nlines = 0;
                while (true) {
                    String line = br.readLine();
                    if (line == null) {
                        break;
                    }
                    line = line.trim();
                    List<String> values = regexpFieldsBuilder.exec(line);

                    if (values == null) {
                        System.err.println("Did not parse " + line);
                        Row r = rf.row();
                        r.put(cf.column("reject"), line);
                        err.emitRow(r);
                    } else {
                        Row r = rf.row();
                        for (int i = 0 ; i < values.size(); i++) {
                            r.put(columns.get(i), values.get(i));
                        }
                        out.emitRow(r);
                    }
                    if (listener != null && nlines++ % 50 == 0) {
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
            // TODO: properly close streams
        }
        out.lastRowEmitted();
        return true;
    }

    // private Logger logger = Logger.getLogger("csv");
}
