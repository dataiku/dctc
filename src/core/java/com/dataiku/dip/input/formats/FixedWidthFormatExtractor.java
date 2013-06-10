package com.dataiku.dip.input.formats;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
    public FixedWidthFormatExtractor(List<Integer> columnOffsets, int skipBefore, boolean parseHeader, int skipAfter) {
        this.columnOffsets = new int[columnOffsets.size()];
        for (int i = 0; i < columnOffsets.size(); i++) this.columnOffsets[i] = columnOffsets.get(i);
        this.skipBefore = skipBefore;
        this.parseHeader = parseHeader;
        this.skipAfter = skipAfter;
    }

    private int skipBefore;
    private boolean parseHeader;
    private int skipAfter;
    int[] columnOffsets;

    @Override
    public boolean run(StreamsInputSplit in, ProcessorOutput out, ProcessorOutput err,
            ColumnFactory cf, RowFactory rf, StreamInputSplitProgressListener listener,
            ExtractionLimit limit) throws Exception {

        long totalBytes = 0, nlines = 0;
        while (true) {
            EnrichedInputStream stream = in.nextStream();
            if (stream == null) break;

            InputStream is = stream.stream();
            CountingInputStream cis = new CountingInputStream(is);
            // TODO Encoding
            BufferedReader br  = new BufferedReader(new InputStreamReader(cis, "utf8"));

            List<Column> headerColumns = null;

            try {
                while (true) {
                    String line = br.readLine();
                    if (line == null) break;
                    if (limit != null) {
                        if (limit.maxBytes > 0 && limit.maxBytes < totalBytes + cis.getCount()) return false;
                        if (limit.maxRecords > 0 && limit.maxRecords <= nlines) return false;
                    }

                    if (nlines == skipBefore && parseHeader) {
                        headerColumns = new ArrayList<Column>();
                        for (int colIdx = 0; colIdx < columnOffsets.length; colIdx++) {
                            int begin = columnOffsets[colIdx];
                            int end = colIdx < columnOffsets.length - 1 ? columnOffsets[colIdx+1] : line.length();
                            if (begin >= line.length()) break;
                            String s = line.substring(begin, end).trim();
                            headerColumns.add(cf.column(s));
                        }
                        nlines++;
                    } else {
                        Row r = rf.row();
                        for (int colIdx = 0; colIdx < columnOffsets.length; colIdx++) {
                            Column c = null;
                            if (headerColumns == null) {
                                c = cf.column("col_" + colIdx);
                            } else {
                                c = headerColumns.get(colIdx);
                            }
                            int begin = columnOffsets[colIdx];
                            int end = colIdx < columnOffsets.length - 1 ? columnOffsets[colIdx+1] : line.length();
                            if (begin >= line.length()) break;
                            String s = line.substring(begin, end).trim();
                            r.put(c, s);
                        }
                        if (nlines > skipBefore + skipAfter) {
                            out.emitRow(r);
                        }

                        if (listener != null && nlines++ % 50 == 0) {
                            synchronized (listener) {
                                listener.setErrorRecords(0);
                                listener.setReadBytes(totalBytes + cis.getCount());
                                listener.setReadRecords(nlines);
                            }
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
                br.close();
            }
        }
        out.lastRowEmitted();
        return true;
    }

    private static Logger logger = Logger.getLogger("dku.format.fixed");
}