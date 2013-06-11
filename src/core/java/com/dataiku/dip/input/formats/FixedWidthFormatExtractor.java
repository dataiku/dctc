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
    public FixedWidthFormatExtractor(List<Integer> columnOffsets, int skipBefore, boolean parseHeader, int skipAfter, String charset) {
        this.columnOffsets = new int[columnOffsets.size()];
        for (int i = 0; i < columnOffsets.size(); i++) this.columnOffsets[i] = columnOffsets.get(i);
        this.skipBefore = skipBefore;
        this.parseHeader = parseHeader;
        this.skipAfter = skipAfter;
        this.charset = charset;
    }

    private int skipBefore;
    private boolean parseHeader;
    private int skipAfter;
    private int[] columnOffsets;
    private String charset;

    @Override
    public boolean run(StreamsInputSplit in, ProcessorOutput out, ProcessorOutput err,
            ColumnFactory cf, RowFactory rf, StreamInputSplitProgressListener listener,
            ExtractionLimit limit) throws Exception {

        long totalBytes = 0, totalRecords = 0;
        while (true) {
            EnrichedInputStream stream = in.nextStream();
            if (stream == null) break;

            InputStream is = stream.stream();
            CountingInputStream cis = new CountingInputStream(is);
            BufferedReader br  = new BufferedReader(new InputStreamReader(cis, charset));

            List<Column> headerColumns = null;
            long fileLines = 0;

            try {
                while (true) {
                    String line = br.readLine();
                    if (line == null) break;
                    if (limit != null) {
                        if (limit.maxBytes > 0 && limit.maxBytes < totalBytes + cis.getCount()) return false;
                        if (limit.maxRecords > 0 && limit.maxRecords <= totalRecords) return false;
                    }

                    if (fileLines < skipBefore) {
                        // Skip
                    } else if (fileLines == skipBefore && parseHeader) {
                        headerColumns = new ArrayList<Column>();
                        for (int colIdx = 0; colIdx < columnOffsets.length; colIdx++) {
                            int begin = columnOffsets[colIdx];
                            int end = colIdx < columnOffsets.length - 1 ? columnOffsets[colIdx+1] : line.length();
                            if (begin >= line.length()) break;
                            String s = line.substring(begin, end).trim();
                            headerColumns.add(cf.column(s));
                        }
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
                        if (fileLines >= skipBefore + skipAfter + (parseHeader?1:0)) {
                            totalRecords++;
                            out.emitRow(r);
                        }
                    }
                    fileLines++;

                    if (listener != null && totalRecords++ % 500 == 0) {
                        listener.setData(totalBytes + cis.getCount(), totalRecords, 0);
                    }
                }
                totalBytes += cis.getCount();
                if (listener != null) {
                    listener.setData(totalBytes, totalRecords, 0);
                }
            } finally {
                br.close();
                cis.close();
            }
        }
        out.lastRowEmitted();
        return true;
    }

    private static Logger logger = Logger.getLogger("dku.format.fixed");
}