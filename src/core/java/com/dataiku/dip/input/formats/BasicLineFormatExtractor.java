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

public class BasicLineFormatExtractor extends AbstractFormatExtractor {
    public BasicLineFormatExtractor(String charset) {
        this.charset = charset;
    }
    private String charset;

    @Override
    public boolean run(StreamsInputSplit in, ProcessorOutput out, ProcessorOutput err,
            ColumnFactory cf, RowFactory rf, StreamInputSplitProgressListener listener,
            ExtractionLimit limit) throws Exception {
        Column c = cf.column("line");

        while (true) {
            EnrichedInputStream stream = in.nextStream();
            if (stream == null) break;

            InputStream is = stream.decompressedStream();
            CountingInputStream cis = new CountingInputStream(is);
            BufferedReader br = new BufferedReader(new InputStreamReader(cis, charset));
            try {
                while (true) {
                    String str = br.readLine();
                    if (str == null) break;
                    Row r = rf.row();
                    r.put(c, str);
                    out.emitRow(r);
                }
            } finally {
                br.close();
            }
        }
        return true;
    }
}
