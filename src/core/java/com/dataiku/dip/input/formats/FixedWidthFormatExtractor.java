package com.dataiku.dip.input.formats;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

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

public class FixedWidthFormatExtractor extends AbstractFormatExtractor {

    public FixedWidthFormatExtractor(int[] columnOffsets) {
        this.columnOffsets = columnOffsets;
    }
    public FixedWidthFormatExtractor(List<Integer> columnOffsets) {
        this.columnOffsets = new int[columnOffsets.size()];
        for (int i = 0; i < columnOffsets.size(); i++) this.columnOffsets[i] = columnOffsets.get(i);
    }

    int[] columnOffsets;

    @Override
    public boolean run(StreamsInputSplit in, ProcessorOutput out, ProcessorOutput err,
            ColumnFactory cf, RowFactory rf, StreamInputSplitProgressListener listener,
            ExtractionLimit limit) throws Exception {

        while (true) {
            EnrichedInputStream stream = in.nextStream();
            if (stream == null) break;

            InputStream is = stream.stream();
            CountingInputStream cis = new CountingInputStream(is);
            // TODO Encoding
            BufferedReader br  = new BufferedReader(new InputStreamReader(cis, "utf8"));

            try {
                long nlines = 0;
                while (true) {
                    String line = br.readLine();
                    if (line == null) break;
                    Row r = rf.row();
                    for (int colIdx = 0; colIdx < columnOffsets.length; colIdx++) {
                        Column c = cf.column("col_" + colIdx);
                        int begin = columnOffsets[colIdx];
                        int end = colIdx < columnOffsets.length - 1 ? columnOffsets[colIdx+1] : line.length();
                        if (begin >= line.length()) break;
                        String s = line.substring(begin, end).trim();
                        r.put(c, s);
                    }
                    out.emitRow(r);

                    if (listener != null && nlines++ % 50 == 0) {
                        synchronized (listener) {
                            listener.setErrorRecords(0);
                            listener.setReadBytes(cis.getCount());
                            listener.setReadRecords(nlines);
                        }
                    }
                }
                out.lastRowEmitted();
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
        }
        return true;
    }

    Logger logger = Logger.getLogger("format.fixed");
}
