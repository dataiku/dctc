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

import org.apache.commons.cli.Options;

import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.Globbing;
import com.dataiku.dctc.configuration.GlobalConf;
import com.dataiku.dctc.display.Size;
import com.dataiku.dctc.file.Acl;
import com.dataiku.dctc.file.FileManipulation;
import com.dataiku.dctc.file.GeneralizedFile;

public class Ls extends Command {
    public String tagline() {
        return "List the contents of folders";
    }
    public String longDescription() {
        return "List the contents of folders, with detailed attributes if requested";
    }

    // Public
    @Override
    public void perform(String[] args) {
        List<GeneralizedFile> arguments = getArgs(args);
        if (arguments != null) {
            if (arguments.size() == 0) {
                String[] dot = { "." };
                perform(build(dot), true);
            } else {
                perform(arguments, true);
            }
        }
        return;
        // Set options.
    }
    @Override
    public void perform(List<GeneralizedFile> args) {
        perform(args, true);
    }
    @Override
    public String cmdname() {
        return "ls";
    }
    /// Getters
    public boolean hidden() {
        if (hidden == null) {
            hidden = hasOption("a");
        }
        return hidden;
    }
    public boolean humanReadable() {
        if (humanReadable == null) {
            humanReadable = hasOption("h");
        }
        return humanReadable;
    }
    public boolean listing() {
        if (listing == null) {
            listing = hasOption("l");
        }
        return listing;
    }
    public boolean recursion() {
        if (recursion == null) {
            recursion = hasOption("R") || hasOption("r");
        }
        return recursion;
    }
    public boolean temp() {
        if (temp == null) {
            temp = hasOption("e");
        }
        return temp;
    }
    public boolean sort() {
        return !hasOption("f");
    }
    public boolean columnPrint() {
        if (columnPrint == null) {
            columnPrint = hasOption("1") || !GlobalConf.isInteractif();
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

    // Protected
    @Override
    protected Options setOptions() {
        Options options = new Options();
        options.addOption("R", "recursive", false, "Recursive display of path given as arguments.");
        options.addOption("r", false, "Recursive display of path given as arguments.");
        options.addOption("a", "all", false, "Do not hide entries starting with.");
        options.addOption("U", false, "Do not sort; list entries in directory order.");
        options.addOption("l", false, "Use long listing format.");
        options.addOption("h", "human-readable", false,
                          "With -l, print sizes in human readable format.");
        options.addOption("e", "temp", false, "Hide temporary files (*~, #*).");
        options.addOption("G", "color", false, "Colorize the output.");
        options.addOption("1", false, "List one file per line.");
        options.addOption("f", false, "Do not sort.");
        return options;
    }
    @Override
    protected String proto() {
        return "dctc ls [OPT...] [PATH...]";
    }

    // Private
    @SuppressWarnings("unchecked")
    private void perform(List<GeneralizedFile> files, boolean head) {
        List<GeneralizedFile> dirs = new ArrayList<GeneralizedFile>();
        List<GeneralizedFile> fs = new ArrayList<GeneralizedFile>();

        for (GeneralizedFile file: files) {
            try {
                if (file.exists()) {
                    if (file.isFile() || !head) {
                        fs.add(file);
                    }
                    if (file.isDirectory()) {
                        dirs.add(file);
                    }
                } else {
                    error(file.givenName(), "Not Found", 1);
                }
            } catch (IOException e) {
                error(file.givenName(), e.getMessage(), e, 2);
            }
        }
        int i = 0;
        if (fs.size() != 0 || !head) {
            printSize(fs);
        }
        if (sort()) {
            Collections.sort(fs);
        }
        for (GeneralizedFile file: fs) {
            ++i;
            if (!head) {
                print(file, file.getFileName(), true);
            } else {
                print(file, file.givenName(), true);
            }
        }

        cleanPrint(i != 0 && dirs.size() != 0);
        i = 0;

        for (GeneralizedFile dir: dirs) {
            cleanPrint(++i != 1);

            List<GeneralizedFile> list;
            try {
                list = (List<GeneralizedFile>) dir.glist();
            } catch (IOException e) {
                error(e.getMessage(), 2);
                continue;
            }
            if (dirs.size() > 1 || fs.size() > 0 || recursion()) {
                header(dir);
            }
            if (!recursion()) {
                printSize(list);
                if (sort()) {
                    Collections.sort(list);
                }
                for (int j = 0; j < list.size(); ++j) {
                    GeneralizedFile subfile = list.get(j);
                    print(subfile, subfile.getFileName(), false);
                    if (j + 1 > list.size()) {
                        System.out.println();
                    }
                }
                cleanPrint(false);
            } else {
                perform(list, false);
            }
        }
    }

    private void header(GeneralizedFile f) {
        System.out.println(f.givenName() + ":");
    }

    private boolean hide(GeneralizedFile f, boolean forcePrint) {
        try {
            return !(forcePrint || ((hidden() || !f.isHidden()) && (!temp() || !f.isTempFile())));
        } catch (IOException e) {
            error(f.givenName(), "failed ", e, 1);
            return false;
        }
    }
    private void printSize(List<? extends GeneralizedFile> fs) {
        if (listing()) {
            boolean hideTotal = true;
            long size = 0;
            for (GeneralizedFile f: fs) {
                if (!hide(f, false)) {
                    try {
                        size += f.getSize();
                    } catch (IOException e) {
                        error(f.givenName(), "failed to get the size", e, 1);
                    }
                    hideTotal = false;
                }
            }
            if (!hideTotal) {
                System.out.print("total size ");
                if (humanReadable()) {
                    System.out.println(Size.getReadableSize(size, "#0.0"));
                }
                else {
                    System.out.println(size);
                }
            }
        }
    }
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private void printDate(GeneralizedFile f) {
        Date date = new Date();
        try {
            date.setTime(f.getDate());
        } catch (IOException e) {
            System.out.print("                   ");
            return;
        }
        
        System.out.print(dateFormat.format(date));
    }

    private void print(GeneralizedFile file, String f, boolean forcePrint) {
        if (!hide(file, forcePrint)) {
            if (listing()) {
                // Print directly.
                /// Date
                if (file.hasAcl()) {
                    Acl acl;
                    try {
                        acl = file.getAcl();
                        System.out.print(acl.getMode().substring(0, 4) + " ");
                    } catch (IOException e) {
                        error(file.givenName(), "failed to get Acl", e, 2);
                        System.out.print("     ");
                    }
                } else {
                    System.out.print("     ");
                }
                printDate(file);
                System.out.print("	");
                /// Size
                try {
                    long size = file.getSize();
                    if (humanReadable()) {
                        System.out.print(Size.getReadableSize(size, "#0.0"));
                    } else {
                        System.out.print(size);
                    }
                } catch (IOException e) {
                    System.out.print("    ");
                }
                /// File Name
                System.out.print("	");
                printName(file, f);
                System.out.println();
            } else {
                fileList.add(new PrintTask(file, f));
            }
        }
    }
    private void initColor() {
        if (color == null) {
            color = new HashMap<String, String>();
            colorSpe = new HashMap<String, String>();
            extensionColor = new HashMap<String, String>();
            String envColor = System.getenv("LS_COLORS");
            if (envColor == null) {
                envColor = GlobalConstants.envColor;
            }
            String[] colorSplit = envColor.split(":");
            for (String c: colorSplit) {
                String[] split = FileManipulation.split(c, "=", 2);
                if (split[0].startsWith("*.")) {
                    if (split[0].matches("\\*.[A-z0-9]*")) {
                        extensionColor.put(split[0].substring(2), split[1]);
                    } else {
                        color.put(split[0], split[1]);
                    }
                } else {
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
    private void printName(GeneralizedFile g, String f) {
        if (color()) {
            initColor();
            try {
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
                } else if (g.hasAcl() && g.getAcl().getExec("user") != null && g.getAcl().getExec("user")) {
                    colorName(colorSpe.get("ex"), f);
                } else if (g.isFile()) {
                    colorName(colorSpe.get("fi"), f);
                } else if (g.exists()) {
                    colorName(colorSpe.get("ln"), f);
                } else {
                    System.out.print(f);
                }
            } catch (IOException e) {
                System.out.print(f);
            }
        } else {
            System.out.print(f);
        }
    }
    private void cleanPrint(boolean hasPrint) {
        if (sort()) {
            Collections.sort(fileList);
        }
        if (columnPrint()) {
            for (PrintTask file: fileList) {
                printName(file.first, file.second);
                System.out.println();
            }
            fileList.clear();
            return;
        }
        int nbLine = 1;
        int nbCol = fileList.size();
        List<Integer> colLength = new ArrayList<Integer>();
        for (int i = 0; i < nbCol; ++i) {
            colLength.add(0);
        }

        while (nbLine < fileList.size()) {
            // Compute the number of column.
            nbCol = fileList.size() / nbLine;
            // Add the last incomplete line, if needed.
            if (fileList.size() % nbLine != 0) {
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
                    if (idx >= fileList.size()) {
                        continue;
                    }
                    colLength.set(i, Math.max(colLength.get(i), fileList.get(idx).second.length() + 2));
                }
            }
            int lineLength = 4;
            for (int i = 0; i < nbCol; ++i) {
                lineLength += colLength.get(i);
            }
            if (lineLength <= GlobalConf.getColNumber()) {
                break;
            }
            ++nbLine;
        }
        int ct = 0;
        for (int i = 0; i < nbCol; ++i) {
            for (int j = 0; j < nbLine; ++j, ++ct) {
                if (ct >= fileList.size()) {
                    break;
                }
                colLength.set(i, Math.max(fileList.get(ct).second.length(), colLength.get(i)));
            }
            if (ct >= fileList.size()) {
                break;
            }
        }
        ct = 0;
        int idx = 0;
        int completeLine = fileList.size() - nbLine * (nbCol - 1);
        for (int j = 0; j < nbLine; ++j) {
            idx = j;
            if (j == completeLine) {
                --nbCol;
            }
            for (int i = 0; i < nbCol; ++i) {
                idx = i * nbLine + j;
                if (idx >= fileList.size()) {
                    break;
                }
                PrintTask file = fileList.get(idx);
                // Print the file name
                printName(file.first, file.second);
                if ((ct + 1) % nbCol == 0) {
                    System.out.println();
                } else {
                    // Align, don't put trailing white space.
                    for (int k = file.second.length(); k < colLength.get(ct); ++k) {
                        System.out.print(" ");
                    }
                }
                ct = (ct + 1) % nbCol;
                idx += nbCol + 2;
            }
        }
        if (hasPrint) {
            System.out.println();
        }
        fileList.clear();
        if (ct != 0) {
            System.out.println();
        }
    }
    private boolean color() {
        if (colorize == null) {
            colorize = (GlobalConf.isInteractif() || System.getenv("CLICOLOR_FORCE") != null) &&
                (hasOption("G"));
        }
        return colorize;
    }
    static class PrintTask implements Comparable<PrintTask> {
        public PrintTask(GeneralizedFile first, String second) {
            this.first = first;
            this.second = second;
        }
        public int compareTo(PrintTask r) {
            return second.toLowerCase().replaceAll("[^a-z]", "").compareTo(r.second.toLowerCase().replaceAll("[^a-z]", ""));
        }
        public boolean equals(Object o) {
            if (!(o instanceof PrintTask)) {
                return false;
            }
            PrintTask task = (PrintTask) o;
            return task.second.equals(this.second);
        }

        public GeneralizedFile first;
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
    private Map<String, String> extensionColor;
    private Map<String, String> color;
    private Map<String, String> colorSpe;
    private List<PrintTask> fileList = new ArrayList<PrintTask>();
}
