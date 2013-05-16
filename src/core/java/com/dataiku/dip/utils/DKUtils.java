package com.dataiku.dip.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

public class DKUtils {
    public static void unsafeSleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }

    public static void setStdoutNotBuffered( ){
        System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(FileDescriptor.in))));
    }

    /* Warning: not optimized */
    public static String isoFormat(long ts) {
        return ISODateTimeFormat.basicDateTime().withZone(DateTimeZone.UTC).print(ts);
    }
    /* Warning: not optimized */
    public static String isoFormatPretty(long ts) {
        return ISODateTimeFormat.dateHourMinuteSecondMillis().withZone(DateTimeZone.UTC).print(ts);
    }

    public static String indent(int indent) {
        String s = "";
        for (int i = 0; i < indent; i++) {
            s += "  ";
        }
        return s;
    }

    public static void unsafeClose(Connection conn) {
        try {
            conn.close();
        } catch (Exception e) {
            logger.warn("Could not safely close SQL connection " + conn , e);
        }
    }

    public static void unsafeRollbackAndClose(Connection conn) {
        try {
            conn.rollback();
            conn.close();
        } catch (Exception e) {
            logger.warn("Could not safely close SQL connection " + conn , e);
        }
    }

    /* Execute and log, returns return code */
    public static int execAndLog(String[] args, String[] env) throws IOException,
    InterruptedException {
        Process p = Runtime.getRuntime().exec(args, env);
        Thread tout = new LoggingStreamEater(p.getInputStream(), org.apache.log4j.Level.INFO);
        tout.start();
        Thread terr = new LoggingStreamEater(p.getErrorStream(), org.apache.log4j.Level.WARN);
        terr.start();
        int rv = p.waitFor();
        tout.join();
        terr.join();
        return rv;
    }

    /* Execute and log, throws if return coded non zero */
    public static void execAndLogThrows(String[] args, String[] env,
            File cwd) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(args, env, cwd);
        Thread tout = new LoggingStreamEater(p.getInputStream(), org.apache.log4j.Level.INFO);
        tout.start();
        Thread terr = new LoggingStreamEater(p.getErrorStream(), org.apache.log4j.Level.INFO);
        terr.start();
        int rv = p.waitFor();
        if (rv != 0) {
            throw new IOException("Return code is non-zero: " + rv);
        }
        tout.join();
        terr.join();
    }

    /* Execute and returns stdout - logs stderr - throws if return code is non zero */
    public static byte[] execAndGetOutput(String[] args, String[] env) throws IOException,
    InterruptedException {
        Process p = Runtime.getRuntime().exec(args, env);
        GatheringStreamEater tout = new GatheringStreamEater(p.getInputStream());
        tout.start();
        Thread terr = new LoggingStreamEater(p.getErrorStream(), org.apache.log4j.Level.INFO);
        terr.start();
        int rv = p.waitFor();
        if (rv != 0) {
            //System.out.println(tout.sb.toString());
            throw new IOException("Return code is non-zero: " + rv);
        }
        tout.join();
        terr.join();
        return tout.baos.toByteArray();
    }

    /* Execute and returns stdout - logs stderr - throws if return code is non zero */
    public static byte[] execAndGetOutput(String[] args, String[] env, File cwd) throws IOException,
    InterruptedException {
        Process p = Runtime.getRuntime().exec(args, env, cwd);
        GatheringStreamEater tout = new GatheringStreamEater(p.getInputStream());
        tout.start();
        Thread terr = new LoggingStreamEater(p.getErrorStream(), org.apache.log4j.Level.INFO);
        terr.start();
        int rv = p.waitFor();
        if (rv != 0) {
            //System.out.println(tout.sb.toString());
            throw new IOException("Return code is non-zero: " + rv);
        }
        tout.join();
        terr.join();
        return tout.baos.toByteArray();
    }

    /* Eat a stream and log its output */
    static class LoggingStreamEater extends Thread {
        LoggingStreamEater(InputStream is, org.apache.log4j.Level level) {
            this.is = is;
            this.level = level;

        }
        @Override
        public void run() {
            Thread.currentThread().setName("Exec-" + Thread.currentThread().getId());
            InheritableNDC.inheritNDC();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                while (true) {
                    String line = br.readLine();
                    if (line == null) break;
                    logger.log(level, line);
                }
                br.close();
            } catch (IOException e) {
                logger.error("", e);
            }
        }
        private org.apache.log4j.Level level;
        private InputStream is;
        private static Logger logger = Logger.getLogger("process");
    }

    /* Eat a stream and gather its output */
    static class GatheringStreamEater extends Thread {
        GatheringStreamEater(InputStream is) {
            this.is = is;
        }
        @Override
        public void run() {
            Thread.currentThread().setName("Exec-" + Thread.currentThread().getId());
            InheritableNDC.inheritNDC();
            try {
                byte[] buf = new byte[4096];
                while (true) {
                    int i = is.read(buf);
                    if (i <= 0) break;
                    baos.write(buf, 0, i);
                }
                is.close();
            } catch (IOException e) {
                logger.error("", e);
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        private InputStream is;
        private static Logger logger = Logger.getLogger("process");
    }

    public static String tailFile(File f, int nlines) throws IOException{
        long skip = Math.max(0, f.length() - (nlines * 2000));
        FileInputStream fis = new FileInputStream(f);
        fis.skip(skip);

        List<String> lines = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(fis, "utf8"));
        try {
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                lines.add(line);
            }
        } finally {
            br.close();
        }
        int keptLines = Math.min(lines.size(), nlines);
        StringBuilder sb = new StringBuilder();
        for (int i = lines.size() - keptLines; i < lines.size(); i++) {
            sb.append(lines.get(i));
            sb.append('\n');
        }
        return sb.toString();
    }

    /** 
     * Parse an array of "key=value" strings to a map.
     * TODO: Should handle quoting of the value
     */
    public static Map<String, String> parseKVStringArray(String[] array) {
        Map<String, String> map = new HashMap<String, String>();
        for (String val : array) {
            String[] chunks =val.split("=");
            if (chunks.length < 2) {
                throw new IllegalArgumentException("Illegal param : " + val + ", expected key=value");
            }
            String paramValue = val.substring(chunks[0].length() + 1);
            map.put(chunks[0], paramValue);
        }
        return map;
    }

    public static Map<String, String> parseKVStringArray(Collection<String> array) {
        return parseKVStringArray(array.toArray(new String[0]));
    }
    
    private static Logger logger = Logger.getLogger("dku.utils");
}
