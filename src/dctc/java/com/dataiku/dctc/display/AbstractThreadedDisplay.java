package com.dataiku.dctc.display;

import java.util.ArrayList;
import java.util.List;

import com.dataiku.dctc.copy.CopyTaskRunnable;

abstract public class AbstractThreadedDisplay implements ThreadedDisplay {
    // Could be override if more information should be displayed.
    public List<CopyTaskRunnable> work(List<CopyTaskRunnable> taskList) {
        init(taskList);
        List<CopyTaskRunnable> errorList = new ArrayList<CopyTaskRunnable>();
        while (!taskList.isEmpty()) {

            for (int i = 0; i < taskList.size(); ++i) {
                synchronized (taskList.get(i)) {
                    if (display(taskList.get(i))) {
                        if (taskList.get(i).getException() != null) {
                            errorList.add(taskList.get(i));
                        }
                        taskList.remove(i);
                    }
                }
                
            }
            sleep(100);
            resetLoop();
        }
        end();
        return errorList;
    }

    protected void resetLoop() {
    }
    protected void init(List<CopyTaskRunnable> taskList) {
    }
    protected void end() {
    }
    // Return true if need to remove from the list.
    protected abstract boolean display(CopyTaskRunnable task);

    protected void sleep(int millisecond) {
        try {
            Thread.sleep(millisecond);
        } catch (InterruptedException e) {
        }
    }
}
