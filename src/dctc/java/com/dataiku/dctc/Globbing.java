package com.dataiku.dctc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dataiku.dctc.file.GFile;

public class Globbing {
    static public boolean match(String pattern, String filename) {
        return match(pattern, 0, filename, 0, false);
    }
    static public boolean matchPath(String pathPattern
                                    , String pathName
                                    , String fileSeparator) {
        String[] pattern = pathPattern.split(fileSeparator);
        String[] path = pathName.split(fileSeparator);

        if (path.length != pattern.length) {
            return false;
        }
        for (int i = 0; i < pattern.length; ++i) {
            if (!match(pattern[i], path[i])) {
                return false;
            }
        }

        return true;
    }
    static public List<GFile> resolve(GFile globbing,
                                      boolean showHidden) throws IOException {
        List<GFile> res = new ArrayList<GFile>();
        boolean first = true;

        if (hasGlobbing(globbing.givenName())) {
            String path = globbing.getAbsolutePath();
            String[] split = path.split(globbing.fileSeparator());
            String prevPath = "";

            for (int i = 0; i < split.length; ++i) {
                String splitElt = split[i];
                if (splitElt.isEmpty()) {
                    // We have a path with // in, skip it.
                    continue;
                }

                String prevprev = prevPath;
                prevPath += "/" + splitElt;

                if (hasGlobbing(splitElt)) {
                    if (first) {
                        first = false;
                        GFile globResolve
                            = globbing.createInstanceFor(prevprev);

                        for (GFile f: globResolve.glist()) {
                            if (showHidden || !f.isHidden()) {
                                if (matchPath(prevPath
                                              , f.getAbsolutePath()
                                              , "/")) {
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
                        List<GFile> prevList = res;
                        res = new ArrayList<GFile>();
                        for (GFile parentElt: prevList) {
                            for (GFile nextElt: parentElt.glist()) {
                                if (showHidden || !nextElt.isHidden()) {
                                    if (matchPath(prevPath
                                                  , nextElt.getAbsolutePath()
                                                  , "/")) {
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
        // letter is the letter to match
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
            }
            else if (pattern.charAt(i) == letter) {
                // Single letter, check if it
                return true;
            }
        }

        // Not found.
        return false;
    }
    static private boolean matchBracket(String pattern, char letter) {
        if (pattern.charAt(0) == '!' // Common and wide use negative character
            || pattern.charAt(0) == '^') { // Also used for portability.
            return !matchRawBracket(pattern.substring(1), letter);
        }
        else {
            return matchRawBracket(pattern, letter);
        }
    }
    // FIXME: Should use StringBuilder instead of String
    // HINT: reverse, charAt, setLength
    static private boolean match(String pattern
                                 , int pidx
                                 , String filename
                                 , int fidx
                                 , boolean escape) {
        // Recursion of the globbing matching
        if (!escape) {
            if (pattern.length() == pidx && filename.length() == fidx) {
                return true;
            }
            else if (pattern.length() == pidx || filename.length() == fidx) {
                if (pidx < pattern.length() && pattern.charAt(pidx) == star) {
                    return match(pattern, pidx + 1, filename, fidx, false);
                }

                return false;
            }
            else if (pattern.charAt(pidx) == question) {
                return match(pattern
                             , pidx + 1
                             , filename
                             , fidx + 1
                             , false);
            }
            else if (pattern.charAt(pidx) == openBracket) {
                int idx = pattern.indexOf("]", 2);

                if (idx != -1) {
                    return matchBracket(pattern.substring(pidx + 1, idx)
                                        , filename.charAt(fidx))
                        && match(pattern
                                 , idx + 1
                                 , filename
                                 , fidx + 1
                                 , false);
                }
            }
            else if (pattern.charAt(pidx) == star) {
                return match(pattern, pidx, filename, fidx + 1, false)
                    || match(pattern, pidx + 1, filename, fidx, false)
                    || match(pattern
                             , pidx + 1
                             , filename
                             , fidx + 1
                             , false);
            }
        }
        // Character is escaped or the current character is not a
        // special one.
        if (pattern.charAt(pidx) == backSlash) {
            return match(pattern, pidx + 1, filename, fidx, true);
        }

        return pattern.charAt(pidx) == filename.charAt(fidx)
            && match(pattern, pidx + 1, filename, fidx + 1, false);
    }

    // Attributes
    static final char star = '*';
    static final char question = '?';
    static final char openBracket = '[';
    static final char dash = '-';
    static final char backSlash = '\\';
}
