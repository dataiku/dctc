package com.dataiku.dctc.dispatch;

import java.io.IOException;

import org.apache.commons.io.input.CountingInputStream;

import com.dataiku.dctc.copy.CopyTaskRunnable;
import com.dataiku.dctc.file.GFile;
import com.dataiku.dctc.file.GeneralizedFileInputSplit;
import com.dataiku.dip.datalayer.streamimpl.StreamRowFactory;
import com.dataiku.dip.input.Format;
import com.dataiku.dip.input.formats.BasicFormatExtractorFactory;
import com.dataiku.dip.input.formats.FormatExtractor;

public class SplitTask extends CopyTaskRunnable {
    SplitTask(GFile in, SplitStreamFactory fact, Format format) {
        super(in);
        this.fact = fact;
        this.format = format;
    }

    @Override
    public final void work() throws IOException {
        if (format == null) {
            format = new Format("csv").withParam("separator", ",");
        }
        FormatExtractor formatExtractor
            = BasicFormatExtractorFactory.build(format);

        SplitProcessorOutput out = new SplitProcessorOutput(fact);

        GeneralizedFileInputSplit inputStream = new GeneralizedFileInputSplit(in);
        try {
            formatExtractor.run(inputStream
                                , out
                                , null
                                , out.getColumnFactory()
                                , new StreamRowFactory()
                                , null
                                , null);
            out.lastRowEmitted();
        }
        catch (Exception e) {
            throw new IOException("Error while extracting", e);
        }
    }

    @Override
    public long read() {
        if (countable == null) return 0;
        return countable.getByteCount();
    }
    @Override
    public String print() {
        return in.givenName();
    }

    private SplitStreamFactory fact;
    private CountingInputStream countable;
    private Format format;
}
