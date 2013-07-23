package com.dataiku.dctc.dispatch;

import java.io.IOException;

import com.dataiku.dctc.copy.CopyTask;
import com.dataiku.dctc.copy.CopyTaskRunnable;
import com.dataiku.dctc.copy.CopyTaskRunnableFactory;
import com.dataiku.dctc.file.GFile;
import com.dataiku.dip.input.Format;

public class SplitFactory implements CopyTaskRunnableFactory {
    public SplitFactory(GFile dst
                        , String prefix
                        , String postfix
                        , SplitFunction fct
                        , String column
                        , Format format
                        , boolean compress) {
        if (dst.hasOutputStream()) {
            streamFact = new SplitStreamFactory(dst
                                                , prefix
                                                , postfix
                                                , fct
                                                , column
                                                , format
                                                , compress);
        }
        else {
            streamFact = new TmpSplitStreamFactory(dst
                                                   , prefix
                                                   , postfix
                                                   , fct
                                                   , column
                                                   , format
                                                   , compress);
        }
        this.format = format;
    }
    public CopyTaskRunnable build(CopyTask task) {
        return new SplitTask(task.src, streamFact, format);
    }
    public void close() throws IOException {
        streamFact.close();
    }
    public void done() throws IOException {
        close();
    }

    // Attributes
    private SplitStreamFactory streamFact;
    private Format format;
}
