package com.dataiku.dctc.copy;

import java.io.IOException;

public interface CopyTaskRunnableFactory {
    public CopyTaskRunnable build(CopyTask task) throws IOException;
    public void done() throws IOException;
}
