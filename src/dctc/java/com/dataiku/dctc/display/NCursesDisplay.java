package com.dataiku.dctc.display;

import java.io.IOException;
import java.util.List;

import jcurses.system.CharColor;
import jcurses.system.Toolkit;

import com.dataiku.dctc.configuration.GlobalConf;
import com.dataiku.dctc.copy.CopyTaskRunnable;

public class NCursesDisplay extends AbstractTransferRateDisplay {
    @Override
    protected final void init(List<CopyTaskRunnable> tasks) {
        Toolkit.init();
        Runtime.getRuntime().addShutdownHook
            (
             new Thread()
             {
                 public void run() {
                     // Kill NCurses properly if needed.
                     Toolkit.shutdown();
                 }
             } );
    }
    @Override
    protected final void done() {
        Toolkit.shutdown();
    }
    @Override
    protected final void beginLoop(int taskSize) {
        Toolkit.clearScreen(color);
        currentLine = 0;
        center("Dataiku Cloud Transport Client", 0, color);
        ++currentLine;
        if (Toolkit.getScreenHeight() > GlobalConf.getThreadLimit() + 3) {
            Toolkit.printString(Integer.toString(taskSize) + " remaining file(s).", 0,
                                currentLine, color);
            ++currentLine;
        }
    }
    @Override
    protected final void endLoop() {
        int line = GlobalConf.getThreadLimit() + 2;
        if (fail) {
            Toolkit.printString("Errors have append.", 0, line, color);
            ++line;
        }
        prettyDisplay("done: " + Size.getReadableSize(doneTransfer()) + "\t\t"
                      +  Size.getReadableSize(getBnd()) + "/s",
                      doneTransfer(),
                      wholeSize(),
                      line);
    }
    @Override
    protected final void done(CopyTaskRunnable task) {
    }
    @Override
    protected final void fail(CopyTaskRunnable task) {
        fail = true;
    }
    @Override
    protected final void started(CopyTaskRunnable task) {
        if (currentLine >= GlobalConf.getThreadLimit() + 2) {
            return;
        }
        String str = Size.getReadableSize(task.read())
            + "/"
            + Size.getReadableSize(task.getInSize())
            + "\t\t";
        try {
            str += task.print();
        } catch (IOException e) {
            str += task.getInputFile().givenName();
        }
        prettyDisplay(str, task.read(), task.getInSize(), currentLine++);
    }

    private void prettyDisplay(String str, long transferred, long total, int line) {
        int pourcent;
        if (total == 0) {
            pourcent = 1;
        } else {
            pourcent = Math.min(str.length() - 1, (int) ((transferred * str.length()) / total));
        }

        Toolkit.printString(str.substring(0, pourcent), 0, line, doneColor);
        Toolkit.printString(str.substring(pourcent, str.length()), pourcent, line, color);
    }
    private void center(String msg, int column, CharColor color) {
        Toolkit.printString(msg, Math.max(0, Toolkit.getScreenWidth() / 2 - msg.length() / 2),
                            column, color);
    }

    private int currentLine;
    private CharColor color = new CharColor(CharColor.BLACK, CharColor.WHITE);
    private CharColor doneColor = new CharColor(CharColor.BLACK, CharColor.RED);
    private boolean fail;
}
