package com.dataiku.dctc.file;

import org.apache.commons.lang.StringUtils;
import static com.dataiku.dctc.PrettyString.quoted;

public class FileManipulation {
    public static String extension(String file,
                                   String extensionSeparator) {
        int dot = file.lastIndexOf(extensionSeparator);
        if (dot == -1) {
            return "";
        } else {
            String res = file.substring(dot + 1, file.length());

            return res;
        }
    }
    public static String extension(String file) {
        // Common Extension separator
        return extension(file, ".");
    }
    public static String translatePath(String src,
                                       String srcSeparator,
                                       String dstSeparator) {
        return StringUtils.replace(src, srcSeparator, dstSeparator);
    }
    public static String concat(String prefix,
                                String suffix,
                                String separator) {
        if (prefix.isEmpty()) {
            return suffix;
        } else if (suffix.isEmpty()) {
            return prefix;
        }
        return trimEnd(prefix, separator) + separator + trimBegin(suffix, separator);
    }
    // Concat prefix to suffix with suffix separator translate to
    // prefix separator
    public static String concat(String prefix,
                                String suffix,
                                String prefixSeparator,
                                String suffixSeparator) {
        return concat(prefix, translatePath(suffix, suffixSeparator,
                                            prefixSeparator),
                      prefixSeparator);
    }
    public static String trimEnd(String str, String c) {
         return str.replaceAll(c + "+$", "");
    }
    public static String trimBegin(String str, String c) {
        return str.replaceAll("^" + c + "+", "");
    }
    // This function split the @str string in n parts.
    public static String[] split(String str,
                                 String separator,
                                 final int n,
                                 boolean fill) {
        String[] res = new String[n];
        res[0] = str;

        final int len = separator.length();

        for (int i = 1; i < n; ++i) {
            int index = res[i - 1].indexOf(separator);
            if (index == -1) {
                if (fill) {
                    for (int j = i; j < n; ++j) {
                        res[j] = "";
                    }
                }
                break;
            }
            res[i] = res[i - 1].substring(index + len);
            res[i - 1] = res[i - 1].substring(0, index);
        }
        return res;
    }
    public static String[] split(String str, String separator, final int n) {
        return split(str, separator, n, true);
    }
    public static String[] invSplit(String str, String separator, final int n) {
        String[] res = new String[n];
        res[0] = str;

        final int len = separator.length();
        for (int i = 1; i < n; ++i) {
            int index = res[i -1].lastIndexOf(separator);
            if (index == -1) {
                for (int j = i; j < n; ++j) {
                    res[j] = "";
                }
                break;
            }
            String newPart = res[i - 1].substring(index + len);
            res[i] = res[i - 1].substring(0, index);
            res[i - 1] = newPart;
        }
        return res;
    }

    public static String getPath(String path,
                                 String separator,
                                 String rootSeparator) {
        path = trimEnd(path, separator);
        if (path.isEmpty()) {
            return rootSeparator;
        }

        final int index = path.lastIndexOf(separator);

        if (index < 1) {
            return rootSeparator;
        }
        return path.substring(0, index);
    }
    public static String getPath(String path, String separator) {
        return getPath(path, separator, separator);
    }
    public static String getFileName(String path,
                                     String separator) {
        path = trimEnd(path, separator);

        if (path.isEmpty()) {
            return separator;
        }
        final int index = path.lastIndexOf(separator);

        if (index == -1) {
            return path;
        }
        return path.substring(index + separator.length(), path.length());
    }
    public static String getSonPath(String parent, String son, String separator) {
        /* Handle the specific case of '.' */
        if (!isSon(parent, son, separator)) {
            throw new Error("Fatal error in getSonPath: parent=" + parent + " son=" + son + " separator=" + separator);
        }

        int len = parent.length();
        if (!parent.endsWith(separator)) {
            len += separator.length();
        }

        return son.substring(len, son.length());
    }
    public static boolean isHidden(String path, String separator) {
        return getFileName(path, separator).startsWith(".");
    }
    public static boolean isTempFile(String path, String separator) {
        String fileName = getFileName(path, separator);
        return fileName.endsWith("~") || fileName.startsWith("#")
            || fileName.startsWith(".#");
    }
    // Test if @parent is the son of the parent
    public static boolean isDirectSon(String parent, String son, String separator) {
        parent = trimEnd(parent, separator);
        son = trimEnd(son, separator);

        if (parent.isEmpty()) {
            return getDepth(trimBegin(son, separator), separator) < 1;
        }

        return son.startsWith(parent) // has the same prefix?
            && !(son.indexOf(separator, parent.length() + separator.length()) != -1); // Just one more separator?
    }
    public static boolean isSon(String parent, String son, String separator) {
        parent = trimEnd(parent, separator);
        son = trimEnd(son, separator);

        if (parent.isEmpty()) {
            return true;
        }
        parent += "/";

        return son.startsWith(parent); // has the same prefix?
    }
    // if isSon(parent, subPath) get the value for isDirectSon(parent, path) == true
    public static String getDirectSon(String parent, String subPath, String separator) {
        parent = trimEnd(parent, separator);
        subPath = trimEnd(subPath, separator);

        assert isSon(parent, subPath, separator)
            : "isSon(" + quoted(parent) + ", " + quoted(subPath) + ", " + separator + ")";

        int index = subPath.indexOf(separator, parent.length() + separator.length());
        if (index == -1) {
            return subPath; // subPath is the direct son.
        }
        return subPath.substring(0, index);
    }
    public static int getDepth(String file, String separator) {
        String[] split = file.split(separator);
        return split.length;
    }
    public static boolean isAbsolute(String path, String fileSeparator) {
        return path.startsWith(fileSeparator);
    }
    public static boolean isRelative(String path, String fileSeparator) {
        return !isAbsolute(path, fileSeparator);
    }
    public static boolean hasParent(String path, String fileSeparator) {
        path = trimBegin(trimEnd(path, fileSeparator), fileSeparator);
        return StringUtils.countMatches(path, fileSeparator) >= 1;
    }
    public static String makeRelative(String path, String absolutePattern) {
        return path.replaceFirst(absolutePattern, "");
    }
    public static boolean contains(String str, String pattern) {
        return str.indexOf(pattern) != -1;
    }
}
