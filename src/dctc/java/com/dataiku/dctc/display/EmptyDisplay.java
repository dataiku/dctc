package com.dataiku.dctc.display;

import com.dataiku.dctc.copy.CopyTaskRunnable;

class EmptyDisplay extends AbstractThreadedDisplay {
    protected final boolean display(CopyTaskRunnable task) {
        return task.isDone();
    }
}
