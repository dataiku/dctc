package com.dataiku.dctc.display;

import java.util.List;

import com.dataiku.dctc.copy.CopyTaskRunnable;

public interface ThreadedDisplay {
    // Could be override if more information should be displayed.
    public List<CopyTaskRunnable> work(List<CopyTaskRunnable> taskList);
}
