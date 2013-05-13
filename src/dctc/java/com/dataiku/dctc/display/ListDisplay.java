package com.dataiku.dctc.display;


import java.io.IOException;

import com.dataiku.dctc.copy.CopyTaskRunnable;

public class ListDisplay extends AbstractThreadedDisplay {
    protected final boolean display(CopyTaskRunnable task) {
        if (task.isDone()) {
            try {
                if (task.getException() != null) {
                    System.out.print("failed: ");
                }
                System.out.println(task.print());
            } catch (IOException e) {
                System.out.println(task.getInputFile().givenName());
            }
            return true;
        }
        return false;
    }
}
