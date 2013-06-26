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
    static public List<GeneralizedFile> resolve(GeneralizedFile globbing,
                                                boolean showHidden) throws IOException {
        List<GeneralizedFile> res = new ArrayList<GeneralizedFile>();
        boolean first = true;
        if (hasGlobbing(globbing.givenName())) {
            String path = globbing.getAbsolutePath();
            String[] split = path.split(globbing.fileSeparator());
            String prevPath = "";
            for (int i = 0; i < split.length; ++i) {
                String splitElt = split[i];
                if (splitElt.isEmpty()) {
                    continue;
                }
                prevPath += "/" + splitElt;

                if (hasGlobbing(splitElt)) {
                    if (first) {
                        first = false;
                        GeneralizedFile globResolve = globbing.createInstanceFor(prevPath);
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
                    }
                    else {
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
                }
            }
        }
        else {
            res.add(globbing);
        }
        return res;
    }
    static public boolean hasGlobbing(String f) {
        f = f.replaceAll("\\.", ""); // Delete all escaped character.
        return f.indexOf(star) != -1
            || f.indexOf(question) != -1
            || f.indexOf(openBracket) != -1;
    }

    // Private
    static private boolean matchRawBracket(String pattern, char letter) {
        // if the regex is [a-zA-Z], pattern = a-zA-Z
        for (int i = 0; i < pattern.length(); ++i) {
            if (i + 2 < pattern.length() && pattern.charAt(i + 1) == dash) {
                // a-z
                if (pattern.charAt(i) <= letter
                    && letter <= pattern.charAt(i + 2)) {
                    // the letter is present in the dash.
                    return true;
                }
                else {
                    // continue to match
                    i += 2;
                }
            } else if (pattern.charAt(i) == letter) {
                // Single letter, check if it
                return true;
            }
        }
        // Not found.
        return false;
    }
    static private boolean matchBracket(String pattern, char letter) {
        return matchRawBracket(pattern, letter) ^
            (pattern.charAt(0) == '!'
             || pattern.charAt(0) == '^');
    }
    static private boolean match(String pattern, String filename, boolean escape) {
        // Recursion of the globbing matching
        if (!escape) {
            if (pattern.length() == 0 && filename.length() == 0) {
                return true;
            }
            else if (pattern.length() == 0 || filename.length() == 0) {
                if (pattern.length() != 0 && pattern.charAt(0) == star) {
                    return match(pattern.substring(1), filename, false);
                }
                return false;
            }
            else if (pattern.charAt(0) == question) {
                return match(pattern.substring(1), filename.substring(1), false);
            }
            else if (pattern.charAt(0) == openBracket) {
                int idx = pattern.indexOf("]", 2);
                if (idx != -1) {
                    return matchBracket(pattern.substring(1, idx), filename.charAt(0))
                        && match(pattern.substring(idx + 1), filename.substring(1), false);
                }
            }
            else if (pattern.charAt(0) == star) {
                return match(pattern, filename.substring(1), false)
                    || match(pattern.substring(1), filename, false)
                    || match(pattern.substring(1), filename.substring(1), false);
            }
            assert pattern.charAt(0) == openBracket
                : "pattern.charAt(0) == openBracket";
            assert pattern.indexOf("]", 2) == -1
                : "pattern.indexOf(\"]\", 2) == -1";
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
