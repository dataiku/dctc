package com.dataiku.dip.input.formats;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.dataiku.dip.datalayer.Column;
import com.dataiku.dip.datalayer.ColumnFactory;
import com.dataiku.dip.datalayer.ProcessorOutput;
import com.dataiku.dip.datalayer.Row;
import com.dataiku.dip.datalayer.RowFactory;
import com.dataiku.dip.input.StreamInputSplitProgressListener;
import com.dataiku.dip.input.stream.EnrichedInputStream;
import com.dataiku.dip.input.stream.StreamsInputSplit;
import com.google.common.io.CountingInputStream;

public class LineFormatExtractor extends AbstractFormatExtractor {
    public LineFormatExtractor(String charset) {
        assert charset != null
            : "charset != null";
        this.charset = charset;
    }
    private String charset;

    @Override
    public boolean run(StreamsInputSplit in, ProcessorOutput out, ProcessorOutput err,
                       ColumnFactory cf, RowFactory rf, StreamInputSplitProgressListener listener,
                       ExtractionLimit limit) throws Exception {
        Column c = cf.column("line");

        long totalBytes = 0, nlines = 0;

        while (true) {
            EnrichedInputStream stream = in.nextStream();
            if (stream == null) break;

            InputStream is = stream.stream();
            CountingInputStream cis = new CountingInputStream(is);
            BufferedReader br = new BufferedReader(new InputStreamReader(cis, charset));
            try {
                while (true) {
                    String str = br.readLine();
                    if (str == null) break;
                    if (limit != null) {
                        if (limit.maxBytes > 0 && limit.maxBytes < totalBytes + cis.getCount()) return false;
                        if (limit.maxRecords > 0 && limit.maxRecords <= nlines) return false;
                    }

                    Row r = rf.row();
                    r.put(c, str);
                    out.emitRow(r);
                    nlines++;
                    if (listener != null && nlines % 500 == 0) {
                        listener.setData(totalBytes + cis.getCount(), nlines, 0);
                    }
                }
                if (listener != null) {
                    listener.setData(totalBytes + cis.getCount(), nlines, 0);
                    totalBytes += cis.getCount();
                }
            } finally {
                br.close();
            }
        }
        out.lastRowEmitted();
        return true;
    }
}
