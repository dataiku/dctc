package com.dataiku.dip.shaker.services.smartdate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

public class DateFormatGuesser {
    //21/Jan/2013:15:59:59 +0100

    static List<String> splitWithSeps(String str, Pattern p) {
        int lastMatch = 0;
        List<String> ret = new ArrayList<String>();

        Matcher m = p.matcher(str);

        while (m.find()) {
            ret.add(str.substring(lastMatch, m.start()));
            ret.add(m.group());
            lastMatch = m.end();
        }
        ret.add(str.substring(lastMatch));
        return ret;
    }

    static class FormatChunk {
        FormatChunk(String pattern) {
            this.pattern = pattern;
        }
        String pattern;
        boolean end;
        List<FormatChunk> successors = new ArrayList<DateFormatGuesser.FormatChunk>();
    }

    void recurse(List<FormatChunk> prevCandidates, String format, List<String> chunks, int curIdx, boolean monthDone, boolean dayDone, boolean hoursDone, boolean minutesDone, boolean secondsDone) {
        FormatChunk newFormat = new FormatChunk(format);
        prevCandidates.add(newFormat);
        if (curIdx + 1 >= chunks.size()) {
            newFormat.end = true;
            return;
        }
        observeRec(newFormat.successors, chunks, curIdx + 1, monthDone, dayDone, hoursDone, minutesDone, secondsDone);
    }

    static Set<String> enMonthNames = new HashSet<String>();
    static Set<String> enDayNames = new HashSet<String>();

    static {
        enMonthNames.add("Jan");
        enMonthNames.add("Feb");
        enMonthNames.add("Mar");
        enMonthNames.add("Apr");
        enMonthNames.add("May");
        enMonthNames.add("Jun");
        enMonthNames.add("Jul");
        enMonthNames.add("Aug");
        enMonthNames.add("Sep");
        enMonthNames.add("Oct");
        enMonthNames.add("Nov");
        enMonthNames.add("Dec");
        enDayNames.add("Mon");
        enDayNames.add("Tue");
        enDayNames.add("Wed");
        enDayNames.add("Thu");
        enDayNames.add("Fri");
        enDayNames.add("Sat");
        enDayNames.add("Sun");
    }

    void observeRec(List<FormatChunk> oCandidates, List<String> chunks, int curIdx, boolean monthDone, boolean dayDone, boolean hoursDone, boolean minutesDone, boolean secondDone) {
        String c = chunks.get(curIdx);

        if (c.length() <= 1) {
            if (c.length() > 0 && StringUtils.isNumeric(c)) {
                // Could be month, day, hour, minute, second
                int ic = Integer.parseInt(c);
                if (!monthDone && ic > 0 && ic <= 12) recurse(oCandidates, "MM", chunks, curIdx, true, dayDone, hoursDone, minutesDone, secondDone);
                if (!dayDone && ic > 0 && ic <= 31) recurse(oCandidates, "dd", chunks, curIdx, monthDone, true, hoursDone, minutesDone, secondDone);
                if (!hoursDone && ic >= 0 && ic <= 23) recurse(oCandidates, "HH", chunks, curIdx, monthDone, dayDone, true, minutesDone, secondDone);
                // We have never seen a format where minute comes before hour or second before minute !
                if (hoursDone && !minutesDone && ic >= 0 && ic <= 60) recurse(oCandidates, "mm", chunks, curIdx, monthDone, dayDone, hoursDone, true, secondDone);
                if (minutesDone && !secondDone && ic >= 0 && ic <= 61) recurse(oCandidates, "ss", chunks, curIdx, monthDone, dayDone, hoursDone, minutesDone, true);
            } else {
                // Separator
                recurse(oCandidates, c, chunks,  curIdx, monthDone, dayDone, hoursDone, minutesDone, secondDone);
            }
            return;
        }

        if (c.length() == 4 && StringUtils.isNumeric(c)) {
            recurse(oCandidates, "yyyy", chunks, curIdx, monthDone, dayDone, hoursDone, minutesDone, secondDone);
        } else if (c.length() == 3 && StringUtils.isNumeric(c)) {
            int ic = Integer.parseInt(c);
            if (ic >= 0 && ic <= 999 && hoursDone && minutesDone && secondDone) {
                recurse(oCandidates, "SSS", chunks, curIdx, monthDone, dayDone, hoursDone, minutesDone, secondDone);
            }
        } else if (c.length() == 2 && StringUtils.isNumeric(c)) {
            int ic = Integer.parseInt(c);
            if (!monthDone && ic > 0 && ic <= 12) recurse(oCandidates, "MM", chunks, curIdx, true, dayDone, hoursDone, minutesDone, secondDone);
            if (!dayDone && ic > 0 && ic <= 31) recurse(oCandidates, "dd", chunks, curIdx, monthDone, true, hoursDone, minutesDone, secondDone);
            if (!hoursDone && ic >= 0 && ic <= 23) recurse(oCandidates, "HH", chunks, curIdx, monthDone, dayDone, true, minutesDone, secondDone);
            // We have never seen a format where minute comes before hour or second before minute !
            if (hoursDone && !minutesDone && ic >= 0 && ic <= 60) recurse(oCandidates, "mm", chunks, curIdx, monthDone, dayDone, hoursDone, true, secondDone);
            if (minutesDone && !secondDone && ic >= 0 && ic <= 61) recurse(oCandidates, "ss", chunks, curIdx, monthDone, dayDone, hoursDone, minutesDone, true);

        } else if (c.length() == 3 && enMonthNames.contains(c)) {
            recurse(oCandidates, "MMM", chunks, curIdx, monthDone, dayDone, hoursDone, minutesDone, secondDone);
        } else if (c.length() == 3 && enDayNames.contains(c)) {
            recurse(oCandidates, "EEE", chunks, curIdx,monthDone, dayDone, hoursDone, minutesDone, secondDone);
        } else if (c.length() == 5 && (c.charAt(0) == '+' || c.charAt(0) == '-')) {
            recurse(oCandidates, "Z", chunks, curIdx, monthDone, dayDone, hoursDone, minutesDone, secondDone);
        }
    }

    void collect(List<String> o, String curStr, FormatChunk curChk) {
        String newStr = curStr + curChk.pattern;
        if (curChk.successors.size() == 0 && curChk.end) {
            o.add(newStr);
        } else {
            for (FormatChunk next : curChk.successors) {
                collect(o, newStr, next);
            }
        }
    }

    public static class DetectedFormat implements Comparable<DetectedFormat>{
        String format;
        int nbOK;
        int trust;
        @Override
        public int compareTo(DetectedFormat o) {
            if (nbOK < o.nbOK) return 1;
            if (nbOK > o.nbOK) return -1;
            if (trust < o.trust) return 1;
            if (trust > o.trust) return -1;
            return 0;
        }
        public String getFormat() {
            return format;
        }
    }
    Pattern separators = Pattern.compile("[,\\- :/]");
    Map<String, DetectedFormat> formats = new HashMap<String, DetectedFormat>();

    public void addObservation(String observation) {
        List<String> chunks = splitWithSeps(observation,  separators);

        // Repair damage we might have caused to "12:23:12 -0100"
        for (int i = 0; i < chunks.size() - 2; i++) {
            //			System.out.println("Repairing  (" + chunks.get(i) + ") (" + chunks.get(i+1) +")");

            if (chunks.get(i).equals(" ") && chunks.get(i+1).equals("") && chunks.get(i+2).equals("-")) {
                System.out.println("yes");
                chunks.set(i+3, "-" + chunks.get(i+3));
                chunks.remove(i+1);
                chunks.remove(i+1);
            }
        }
        System.out.println(StringUtils.join(chunks, "__"));
        List<FormatChunk> roots = new ArrayList<DateFormatGuesser.FormatChunk>();
        observeRec(roots, chunks, 0, false, false, false, false, false);
        List<String> candidates =  new ArrayList<String>();
        for (FormatChunk root : roots) {
            collect(candidates, "", root);
        }
        
        /* Manually add some unsplitted formats */
        if (observation.length() == 8 && StringUtils.isNumeric(observation)) {
            int first= Integer.parseInt(observation.substring(0, 4));
            int second = Integer.parseInt(observation.substring(4, 6));
            int third = Integer.parseInt(observation.substring(6, 8));
            
            if (first < 3000 && second <= 12 && third <= 31) {
                candidates.add("yyyyMMdd");
            }
            if (first < 3000 && second <= 31 && third <= 12) {
                candidates.add("yyyyddMM");
            }
        }
        
        
        for (String candidate : candidates) {
            DetectedFormat df = formats.get(candidate);
            if (df == null) {
                df = createDetectedFormat(candidate);
                formats.put(candidate, df);
            }
            df.nbOK++;
            System.out.println("CAND " + candidate);
        }
    }

    private DetectedFormat createDetectedFormat(String candidate) {
        DetectedFormat df = new DetectedFormat();
        df.format = candidate;
        System.out.println(candidate.indexOf("dd"));
        System.out.println(candidate.indexOf("HH"));

        if (candidate.equals("EEE, dd MMM yyyy HH:mm:ss Z")) {
            df.trust = 2;
        } else if (isBefore(candidate, "HH", "dd") || hasAAndNotB(candidate, "HH", "dd") ||
                  hasAAndNotB(candidate, "yyyy", "dd") || hasAAndNotB(candidate, "yyyy", "MM")) {
            df.trust = 0;
        } else {
            df.trust = 1;
        }
        return df;
    }

    private static boolean isBefore(String candidate, String a, String b) {
        return candidate.indexOf(a) >= 0 && candidate.indexOf(b) >= 0 && candidate.indexOf(a) < candidate.indexOf(b);
    }
    private static boolean hasAAndNotB(String candidate, String a, String b) {
        return candidate.indexOf(a) >= 0 && candidate.indexOf(b) < 0;
    }

    public List<DetectedFormat> getResults() {
        List<DetectedFormat> ret = Lists.newArrayList(formats.values());
        Collections.sort(ret);
        return ret;
    }

    public void clearObservation() {
        formats.clear();
    }

    public List<DetectedFormat> guess(List<String> observations) {
        for (String observation : observations) {
            addObservation(observation);
        }
        return getResults();
    }
}
