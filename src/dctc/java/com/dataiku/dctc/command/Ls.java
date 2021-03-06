package com.dataiku.dctc.command;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.Globbing;
import com.dataiku.dctc.clo.OptionAgregator;
import com.dataiku.dctc.command.abs.Command;
import com.dataiku.dctc.configuration.GlobalConf;
import com.dataiku.dctc.display.Size;
import com.dataiku.dctc.file.Acl;
import com.dataiku.dctc.file.BucketBasedFile;
import com.dataiku.dctc.file.PathManip;
import com.dataiku.dctc.file.GFile;
import com.dataiku.dip.utils.IndentedWriter;
import com.dataiku.dip.utils.PrettyString;

public class Ls extends Command {
    public String tagline() {
        return "List the contents of folders.";
    }
    public void longDescription(IndentedWriter printer) {
        printer.print("List the contents of folders, with detailed attributes"
                      + " if requested");
    }
    // Public
    @Override
    public void perform(String[] args) {
        List<GFile> arguments = getArgs(args);
        if (arguments != null) {
            if (arguments.size() == 0) {
                String[] dot = { "." };
                perform(build(dot));
            }
            else {
                perform(arguments);
            }
        }

        return;
    }
    @Override
    public void perform(List<GFile> args) {
        optimizeBucketRecursion(args, recursion());

        try {
            for (int i = 0; i < args.size(); ++i) {
                if (!args.get(i).exists()) {
                    error("cannot access " + args.get(i).givenName()
                          + ": No such file or directory", 2);
                    args.remove(i);
                    --i; // Do not increment the following loop.
                }
            }
            // We have only existing file.
            if (sort()) {
                Collections.sort(args);
            }

            if (recursion()) {
                recursivePerform(args);
            }
            else {
                nonRecursivePerform(args);
            }
        }
        catch (IOException e) {
            errorWithHandlingOfKnownExceptions(null, "", e, 2);
            // FIXME: This code isn't really clear.
        }
    }
    private void optimizeBucketRecursion(List<GFile> args
                                         , boolean recursion) {
        for (GFile arg: args) {
            if (arg instanceof BucketBasedFile) {
                ((BucketBasedFile) arg).setAutoRecursion(recursion);
            }
        }
    }
    public void recursivePerform(List<GFile> args) throws IOException {
        int nbPrinted = printList(args, true);
        printRecursiveDirectoryList(args, nbPrinted == 0);
    }
    private void printRecursiveDirectoryList(List<GFile> args
                                             , boolean isFirst)
        throws IOException {
        for (GFile arg: args) {
            @SuppressWarnings("unchecked")
            List<GFile> sons = (List<GFile>) arg.grecursiveList();
            Collections.sort(sons);
            List<PrintTask> print = new ArrayList<PrintTask>();

            print(print);
            if (sons.isEmpty()) {
                return;
            }
            while (true) {
                print = new ArrayList<PrintTask>();
                GFile dir = sons.get(0); sons.remove(0);
                for (int i = 0; i < sons.size(); ++i) {
                    GFile son = sons.get(i);
                    if (PathManip.isDirectSon(dir.givenName()
                                              , son.givenName()
                                              , dir.fileSeparator())) {
                        print.add(new PrintTask(son, son.getFileName()));
                        if (son.isFile()) {
                            sons.remove(i);
                            --i;
                        }
                    }
                }
                header(dir);
                print(print);
                if (sons.isEmpty()) {
                    break;
                }
                else {
                    System.out.println();
                }
            }
        }
    }
    public void nonRecursivePerform(List<GFile> args) throws IOException {
        int nbPrinted = printList(args, true);
        printDirectoryList(args, nbPrinted == 0);
    }
    private int printList(List<GFile> args
                          , boolean onlyFile) throws IOException {
        List<GFile> toPrint = new ArrayList<GFile>();
        int nbPrinted = 0;

        for (GFile arg: args) {
            if (!(onlyFile && arg.isDirectory())) {
                if (!hide(arg, true)) {
                    ++nbPrinted;
                    toPrint.add(arg);
                }
            }
        }

        givenName(toPrint);

        return nbPrinted;
    }
    private void printDirectoryList(List<GFile> args,
                                    boolean isFirst) throws IOException {
        boolean header = args.size() > 1 || !isFirst;

        for (GFile arg: args) {
            if (!arg.isDirectory()) {
                continue;
            }
            if (isFirst) {
                isFirst = false;
            }
            else {
                System.out.println();
            }
            if (arg.isDirectory()) {
                printDirectory(arg, header);
            }
        }
    }
    private void printDirectory(GFile arg,
                                boolean header) throws IOException {
        List<GFile> toPrint = new ArrayList<GFile>();

        assert arg.isDirectory()
            : "arg.isDirectory()";

        if (header) {
            header(arg);
        }
        List<? extends GFile> files = arg.glist();
        if (sort()) {
            Collections.sort(files);
        }
        for (GFile file: files) {
            if (!hide(file, false)) {
                toPrint.add(file);
            }
        }
        fileName(toPrint);
    }
    private void fileName(List<GFile> files) throws IOException {
        List<PrintTask> tasks = new ArrayList<PrintTask>();
        for (GFile file: files) {
            tasks.add(new PrintTask(file, file.getFileName()));
        }
        print(tasks);
    }
    private void givenName(List<GFile> files) throws IOException {
        List<PrintTask> tasks = new ArrayList<PrintTask>();
        for (GFile file: files) {
            tasks.add(new PrintTask(file, file.givenName()));
        }
        print(tasks);
    }
    private void print(List<PrintTask> tasks) throws IOException {
        if (listing()) {
            printAsList(tasks);
        }
        else if (columnPrint()) {
            columnPrint(tasks);
        }
        else {
            prettyPrint(tasks);
        }
    }
    private void columnPrint(List<PrintTask> tasks) throws IOException {
        for (PrintTask task: tasks) {
            printName(task);
            System.out.println();
        }
    }
    private void printAsList(List<PrintTask> tasks) throws IOException {
        printTotalSize(tasks);
        long maxSize = 0;
        for (PrintTask task: tasks) {
            maxSize = Math.max(maxSize, task.first.getSize());
        }
        int maxSizeLength = getPrettySize(maxSize).length();
        for (PrintTask task: tasks) {
            printAcl(task);
            System.out.print(" ");
            printSize(task, maxSizeLength);
            System.out.print(" ");
            printDate(task.first);
            System.out.print(" ");
            printName(task);
            System.out.println();
        }
    }
    private String getPrettySize(long size) {
        if (humanReadable()) {
            return Size.getReadableSize(size, "#0.0");
        }
        else {
            return Long.toString(size);
        }
    }
    private void printTotalSize(List<PrintTask> tasks) throws IOException {
        long size = 0;
        for (PrintTask task: tasks) {
            size += task.first.getSize();
        }
        if (size != 0) {
            System.out.print("total ");
            if (humanReadable()) {
                System.out.println(Size.getReadableSize(size, "#0.0"));
            } else {
                System.out.println(size);
            }
        }
    }
    private void printName(PrintTask task) throws IOException {
        printName(task.first, task.second);
    }
    private void printAcl(PrintTask task) throws IOException {
        if (task.first.hasAcl()) {
            Acl acl = task.first.getAcl();
            System.out.print(acl.getMode());
        } else {
            System.out.print("     ");
        }
    }
    private void printSize(PrintTask task,
                           int maxSizeLength) throws IOException {
        String size = getPrettySize(task.first.getSize());
        for (int i = size.length(); i < maxSizeLength; ++i) {
            System.out.print(" ");
        }
        System.out.print(size);
    }
    private void printDate(GFile f) {
        Date date = new Date();
        try {
            date.setTime(f.getDate());
        } catch (IOException e) {
            System.out.print("                   ");
            return;
        }
        if (humanReadable()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy MMM d h:mm");
            String[/*year/month/day/hour/minutes*/] split
                = PathManip.split(dateFormat.format(date), " ", 4);
            System.out.print(split[0] + " " + split[1]);
            if (split[2].length() == 1) {
                System.out.print("  ");
            }
            else {
                System.out.print(" ");
            }
            System.out.print(split[2]);
            for (int i = split[3].length(); i < 6; ++i) {
                System.out.print(" ");
            }
            System.out.print(split[3]);
        }
        else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            System.out.print(dateFormat.format(date));
        }
    }
    private void prettyPrint(List<PrintTask> tasks) throws IOException {
        if (columnPrint()) {
            for (PrintTask file: tasks) {
                printName(file.first, file.second);
                System.out.println();
            }
            tasks.clear();
            return;
        }
        int nbLine = 1;
        int nbCol = tasks.size();
        List<Integer> colLength = new ArrayList<Integer>();
        for (int i = 0; i < nbCol; ++i) {
            colLength.add(0);
        }

        while (nbLine < tasks.size()) {
            // Compute the number of column.
            nbCol = tasks.size() / nbLine;
            // Add the last incomplete line, if needed.
            if (tasks.size() % nbLine != 0) {
                ++nbCol;
            }
            for (int i = 0; i < nbCol; ++i) {
                colLength.set(i, 0);
            }
            while (colLength.size() > nbCol) {
                colLength.remove(nbCol);
            }
            for (int j = 0; j < nbLine; ++j) {
                for (int i = 0; i < nbCol; ++i) {
                    int idx = i * nbLine + j;
                    if (idx >= tasks.size()) {
                        continue;
                    }
                    colLength.set(i
                                  , Math.max(colLength.get(i)
                                             , tasks.get(idx).second.length()
                                             + 2));
                }
            }
            int lineLength = 4;
            for (int i = 0; i < nbCol; ++i) {
                lineLength += colLength.get(i);
            }
            if (lineLength <= GlobalConf.getColNumber(getYell())) {
                break;
            }
            ++nbLine;
        }
        int ct = 0;
        for (int i = 0; i < nbCol; ++i) {
            for (int j = 0; j < nbLine; ++j, ++ct) {
                if (ct >= tasks.size()) {
                    break;
                }
                colLength.set(i
                              , Math.max(tasks.get(ct).second.length()
                                         , colLength.get(i)));
            }
            if (ct >= tasks.size()) {
                break;
            }
        }
        ct = 0;
        int idx = 0;
        int completeLine = tasks.size() - nbLine * (nbCol - 1);
        for (int j = 0; j < nbLine; ++j) {
            idx = j;
            if (j == completeLine) {
                --nbCol;
            }
            for (int i = 0; i < nbCol; ++i) {
                idx = i * nbLine + j;
                if (idx >= tasks.size()) {
                    break;
                }
                PrintTask file = tasks.get(idx);
                // Print the file name
                printName(file.first, file.second);
                if ((ct + 1) % nbCol == 0) {
                    System.out.println();
                } else {
                    // Align, don't put trailing white space.
                    for (int k = file.second.length();
                         k < colLength.get(ct);
                         ++k) {
                        System.out.print(" ");
                    }
                }
                ct = (ct + 1) % nbCol;
                idx += nbCol + 2;
            }
        }
        tasks.clear();
        if (ct != 0) {
            System.out.println();
        }
    }

    @Override
    public String cmdname() {
        return "ls";
    }
    /// Getters
    public boolean hidden() {
        if (hidden == null) {
            hidden = hasOption('a') || hasOption('f');
        }
        return hidden;
    }
    public boolean humanReadable() {
        if (humanReadable == null) {
            humanReadable = hasOption('h');
        }
        return humanReadable;
    }
    public boolean listing() {
        if (listing == null) {
            listing = hasOption('l');
        }
        return listing;
    }
    public boolean recursion() {
        if (recursion == null) {
            recursion = hasOption('r');
        }
        return recursion;
    }
    public boolean temp() {
        if (temp == null) {
            temp = hasOption('e');
        }
        return temp;
    }
    public boolean sort() {
        if (sort == null) {
            sort = !hasOption('f') && !hasOption('U');
        }
        return sort;
    }
    public boolean columnPrint() {
        if (columnPrint == null) {
            columnPrint = hasOption('1') || !PrettyString.isInteractif();
        }
        return columnPrint;
    }

    /// Setters
    public Ls hidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }
    public Ls humanReadable(boolean humanReadable) {
        this.humanReadable = humanReadable;
        return this;
    }
    public Ls listing(boolean listing) {
        this.listing = listing;
        return this;
    }
    public Ls recursion(boolean recursion) {
        this.recursion = recursion;
        return this;
    }
    public Ls temp(boolean temp) {
        this.temp = temp;
        return this;
    }
    public Ls columnPrint(boolean columnPrint) {
        this.columnPrint = columnPrint;
        return this;
    }
    public void sort(boolean sort) {
        this.sort = sort;
    }

    // Protected
    @Override
    protected void setOptions(List<OptionAgregator> opts) {
        opts.clear();
        opts.add(stdOption('?'
                           , "help"
                           , "Display this help message."));
        opts.add(stdOption("rR"
                           , "recursive"
                           , "Recursive display of path given as arguments."));
        opts.add(stdOption('a'
                           , "all"
                           , "Do not hide entries starting with."));
        opts.add(stdOption('U'
                           , "Do not sort; list entries in directory order."));
        opts.add(stdOption('l'
                           , "Use long listing format."));
        opts.add(stdOption('h'
                           , "human-readable"
                           , "With -l, print sizes in human readable format."));
        opts.add(stdOption('e'
                           , "temp"
                           , "Hide temporary files (*~, #*)."));
        opts.add(stdOption('G'
                           , "color"
                           , "Colorize the output."));
        opts.add(stdOption('1'
                           , "List one file per line."));
        opts.add(stdOption('f'
                           , "do not sort, enable -aU."));
        opts.add(stdOption('d'
                           , "directory"
                           , "List directory entries instead of contents."));
    }
    @Override
    protected String proto() {
        return "[OPT...] [PATH...]";
    }

    // Private
    private void header(GFile f) {
        System.out.println(f.givenName() + ":");
    }

    private boolean hide(GFile f
                         , boolean forcePrint) throws IOException {
        return !(forcePrint
                 || ((hidden()
                      || !f.isHidden())
                     && (!temp()
                         || !f.isTempFile())));
    }
    private void initColor() {
        if (color == null) {
            color = new HashMap<String, String>();
            extensionColor = new HashMap<String, String>();
            colorSpe = new HashMap<String, String>();
            String envColor = System.getenv("LS_COLORS");
            if (envColor == null) {
                envColor = GlobalConstants.envColor;
            }
            String[] colorSplit = envColor.split(":");
            for (String c: colorSplit) {
                String[] split = PathManip.split(c, "=", 2);
                if (split[0].startsWith("*")) {
                    extensionColor.put(split[0].substring(1), split[1]);
                }
                else if (Globbing.hasGlobbing(split[0])) {
                    color.put(split[0], split[1]);
                }
                else {
                    colorSpe.put(split[0], split[1]);
                }
            }
        }
    }
    private void colorName(String color, String f) {
        if (color != null) {
            System.out.print("\u001B[" + color + "m");
            System.out.print(f);
            System.out.print("\u001B[0m");
        } else {
            System.out.print(f);
        }
    }
    private void printName(GFile g,
                           String f) throws IOException {
        if (color()) {
            initColor();
            for (Map.Entry<String, String> c: extensionColor.entrySet()) {
                if (g.getFileName().endsWith(c.getKey())) {
                    colorName(c.getValue(), f);
                    return;
                }
            }
            for (Map.Entry<String, String> c: color.entrySet()) {
                if (Globbing.match(c.getKey(), g.getFileName())) {
                    colorName(c.getValue(), f);
                    return;
                }
            }
            if (g.isDirectory()) {
                colorName(colorSpe.get("di"), f);
            }
            else {
                Acl acl = g.hasAcl() ? g.getAcl() : null;
                if (acl != null
                    && acl.getExec("user") != null
                    && acl.getExec("user")) {
                    colorName(colorSpe.get("ex"), f);
                }
                else if (g.isFile()) {
                    colorName(colorSpe.get("fi"), f);
                }
                else if (g.exists()) {
                    colorName(colorSpe.get("ln"), f);
                }
                else {
                    System.out.print(f);
                }
            }
        }
        else {
            System.out.print(f);
        }
    }
    private boolean color() {
        if (colorize == null) {
            colorize = (PrettyString.isInteractif()
                        || System.getenv("CLICOLOR_FORCE") != null) &&
                (hasOption('G'));
        }
        return colorize;
    }
    static class PrintTask implements Comparable<PrintTask> {
        public PrintTask(GFile first, String second) {
            this.first = first;
            this.second = second;
        }
        public int compareTo(PrintTask r) {
            return second
                .toLowerCase()
                .replaceAll("[^a-z]"
                            , "")
                .compareTo(r
                           .second
                           .toLowerCase()
                           .replaceAll("[^a-z]"
                                       , ""));
        }

        public GFile first;
        public String second;
    }

    // Attributes
    private Boolean hidden = null;
    private Boolean humanReadable = null;
    private Boolean listing = null;
    private Boolean recursion = null;
    private Boolean temp = null;
    private Boolean colorize = null;
    private Boolean columnPrint = null;
    private Boolean sort;
    private Map<String, String> color;
    private Map<String, String> extensionColor;
    private Map<String, String> colorSpe;
}
