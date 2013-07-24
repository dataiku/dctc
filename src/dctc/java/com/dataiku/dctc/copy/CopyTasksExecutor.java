package com.dataiku.dctc.copy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.dataiku.dctc.command.policy.YellPolicy;
import com.dataiku.dctc.display.ThreadedDisplay;
import static com.dataiku.dip.utils.PrettyString.pquoted;

public class CopyTasksExecutor {
    public CopyTasksExecutor(CopyTaskRunnableFactory fact, ThreadedDisplay display, int threadLimit) {
        this.fact = fact;
        this.display = display;
        this.nbthread = threadLimit;
    }

    public void run(List<CopyTask> tasks, boolean archive) throws IOException {
        if (tasks.isEmpty()) {
            return;
        }

        // If the stream is compressed, we need to serialize the
        // output. Then serialize each io.
        if (archive) {
            this.nbthread = 1;
        }

        List<CopyTaskRunnable> taskList = new ArrayList<CopyTaskRunnable>();

        // Create the pool thread.
        final ThreadGroup threadGroup = new ThreadGroup("workers");
        ExecutorService exec
            = Executors.newFixedThreadPool(getNbthread(), new ThreadFactory() {
                    public Thread newThread(Runnable r) {
                        return new Thread(threadGroup, r);
                    }
                });

        // Create the task list.
        for (CopyTask task: tasks) {
            taskList.add(fact.build(task));
        }

        // Execute.
        for (Runnable r: taskList) {
            exec.execute(r);
        }
        errors = display.work(taskList);

        exec.shutdown();
        fact.done();
    }

    public int getNbthread() {
        return nbthread;
    }
    public CopyTasksExecutor setNbthread(int nbthread) {
        this.nbthread = nbthread;
        return this;
    }
    public CopyTasksExecutor autoSetNbthread() {
        nbthread = Runtime.getRuntime().availableProcessors();
        return this;
    }
    public boolean hasFail() {
        return errors != null && errors.size() > 0;
    }
    public void displayErrors(YellPolicy yell) {
        for (CopyTaskRunnable runnable: errors) {
            if (runnable.getException() != null) {
                error(runnable.getInputFile().givenName(), runnable.getException(), yell);
            }
        }
    }

    // Private
    private void error(String fileName, Exception exception, YellPolicy yell) {
        yell.yell("copy task executor", pquoted(fileName) + ": Unexpected error" , exception);
    }

    // Attributes
    private int nbthread;
    private CopyTaskRunnableFactory fact;
    private ThreadedDisplay display;
    private List<CopyTaskRunnable> errors;
}
