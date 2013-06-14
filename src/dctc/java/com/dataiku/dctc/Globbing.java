package com.dataiku.dctc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dataiku.dctc.file.GeneralizedFile;

public class Globbing {
    static public boolean match(String pattern, String filename) {
        return match(pattern, filename, false);
    }
    static public boolean matchPath(String pathPattern, String pathName, String fileSeparator) {
        String[] pattern = pathPattern.split(fileSeparator);
        String[] path = pathName.split(fileSeparator);
        if (path.length != pattern.length) {
            return false;
        }
        for (int i = 0; i < pattern.length; ++i) {
            if (!match(pattern[i], path[i], false)) {
                return false;
            }
        }
        return true;
    }
    static public boolean partialPathMatch(String pathPattern, String pathName, String fileSeparator) {
        String[] pattern = pathPattern.split(fileSeparator);
        String[] path = pathName.split(fileSeparator);
        if (path.length > pattern.length) {
            return false;
        }
        for (int i = 0; i < pattern.length; ++i) {
            if (!match(pattern[i], path[i], false)) {
                return false;
            }
        }
        return true;
    }
    static public List<GeneralizedFile> resolve(GeneralizedFile globbing, boolean showHidden) throws IOException {
        List<GeneralizedFile> res = new ArrayList<GeneralizedFile>();
        boolean first = true;
        if (hasGlobbing(globbing.givenName())) {
            String path = globbing.getAbsolutePath();
            String[] split = path.split(globbing.fileSeparator());
            String prevPath = "";
            for (int i = 0; i < split.length; ++i) {
                if (split[i].isEmpty()) {
                    continue;
                }
                if (hasGlobbing(split[i])) {
                    if (first) {
                        first = false;
                        GeneralizedFile globResolve = globbing.createInstanceFor(prevPath);
                        prevPath += "/" + split[i];
                        for (GeneralizedFile f: globResolve.glist()) {
                            if (showHidden || !f.isHidden()) {
                                if (matchPath(prevPath, f.getAbsolutePath(), "/")) {
                                    res.add(f);
                                }
                            }
                        }
                        if (res.size() == 0) {
                            res.add(globResolve);
                            return res;
                        }
                    } else {
                        prevPath += "/" + split[i];
                        List<GeneralizedFile> prevList = res;
                        res = new ArrayList<GeneralizedFile>();
                        for (GeneralizedFile parentElt: prevList) {
                            for (GeneralizedFile nextElt: parentElt.glist()) {
                                if (showHidden || !nextElt.isHidden()) {
                                    if (matchPath(prevPath, nextElt.getAbsolutePath(), "/")) {
                                        res.add(nextElt);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    prevPath += "/" + split[i];
                }
            }
        } else {
            res.add(globbing);
        }
        return res;
    }
    static public boolean hasGlobbing(String f) {
        f = f.replaceAll("\\\\", "");
        return f.indexOf(star) != -1;
    }

    // Private
    static private boolean matchRawBracket(String pattern, char letter) {
        for (int i = 0; i < pattern.length(); ++i) {
            if (i + 2 < pattern.length() && pattern.charAt(i + 1) == dash) {
                if (pattern.charAt(i) <= letter
                    && letter <= pattern.charAt(i + 2)) {
                    return true;
                } else {
                    i += 2;
                }
            } else if (pattern.charAt(i) == letter) {
                return true;
            }
        }
        return false;
    }
    static private boolean matchBracket(String pattern, char letter) {
        if (pattern.charAt(0) == '!'
            || pattern.charAt(0) == '^') {
            return !matchRawBracket(pattern.substring(1), letter);
        }
        return matchRawBracket(pattern, letter);
    }
    static private boolean match(String pattern, String filename, boolean escape) {
        if (!escape) {
            if (pattern.length() == 0 && filename.length() == 0) {
                return true;
            } else if (pattern.length() == 0 || filename.length() == 0) {
                if (pattern.length() != 0 && pattern.charAt(0) == star) {
                    return match(pattern.substring(1), filename, false);
                }
                return false;
            } else if (pattern.charAt(0) == question) {
                return match(pattern.substring(1), filename.substring(1), false);
            } else if (pattern.charAt(0) == openBracket) {
                int idx = pattern.indexOf("]", 2);
                if (idx != -1) {
                    return matchBracket(pattern.substring(1, idx), filename.charAt(0))
                        && match(pattern.substring(idx + 1), filename.substring(1), false);
                }
            } else if (pattern.charAt(0) == star) {
                return match(pattern, filename.substring(1), false)
                    || match(pattern.substring(1), filename, false)
                    || match(pattern.substring(1), filename.substring(1), false);
            }
        }
        // Character is escaped or the current character is not a
        // special one.
        if (pattern.charAt(0) == backSlash) {
            return match(pattern.substring(1), filename, true);
        }
        return pattern.charAt(0) == filename.charAt(0)
            && match(pattern.substring(1), filename.substring(1), false);
    }

    // Attributes
    static final char star = '*';
    static final char question = '?';
    static final char openBracket = '[';
    static final char dash = '-';
    static final char backSlash = '\\';
}
