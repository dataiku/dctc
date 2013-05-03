package com.dataiku.dip.input.formats;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.dataiku.dip.datalayer.Column;
import com.dataiku.dip.datalayer.ColumnFactory;
import com.dataiku.dip.datalayer.ProcessorOutput;
import com.dataiku.dip.datalayer.Row;
import com.dataiku.dip.datalayer.RowFactory;
import com.dataiku.dip.input.StreamInputSplitProgressListener;
import com.dataiku.dip.input.stream.EnrichedInputStream;
import com.dataiku.dip.input.stream.StreamsInputSplit;

public class LineFormatExtractor extends AbstractFormatExtractor {
    @Override
    public boolean run(StreamsInputSplit in, ProcessorOutput out, ProcessorOutput err,
            ColumnFactory cf, RowFactory rf, StreamInputSplitProgressListener listener,
            ExtractionLimit limit) throws Exception {
        Column c = cf.column("line");

        EnrichedInputStream stream;
        while ((stream = in.nextStream()) != null) {
            BufferedReader is = new BufferedReader(new InputStreamReader(stream.stream()));
            try {
                while (true) {
                    String str = is.readLine();
                    if (str == null) break;
                    Row r = rf.row();
                    r.put(c, str);
                    out.emitRow(r);
                }
            } finally {
                is.close();
            }
        }
        out.lastRowEmitted();
        return true;
    }
}
