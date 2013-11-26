package com.dataiku.dip.utils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

public class DKUtils {
    public static <T> T lastElement(T[] array) {
        return array[array.length - 1];
    }

    public static void unsafeSleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }

    public static void setStdoutNotBuffered( ){
        System.setOut(new PrintStream(StreamUtils.readFD(FileDescriptor.in, "UTF-8")));
    }

    /* Warning: not optimized */
    public static String isoFormat(long ts) {
        return ISODateTimeFormat.basicDateTime().withZone(DateTimeZone.UTC).print(ts);
    }
    
    /* Warning: not optimized */
    public static String isoFormatReadableByDateFormat(long ts) {
        return ISODateTimeFormat.dateHourMinuteSecondMillis().withZoneUTC().print(ts) + "Z";
    }
    
    /* Warning: not optimized */
    public static String isoFormatPretty(long ts) {
        return ISODateTimeFormat.dateHourMinuteSecondMillis().withZone(DateTimeZone.UTC).print(ts);
    }

    public static String indent(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append(" ");
        }
        return sb.toString();
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
            logger.info("Closing " + conn);
            conn.close();
            logger.info("Conn " + conn + " is now " + conn.isClosed());
            
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
        return tout.getOutput().toByteArray();
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
        return tout.getOutput().toByteArray();
    }


    public static void execAndWriteOutput(String[] args, String[] env, File cwd, File output) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(args, env, cwd);
        FileOutputStream fos = new FileOutputStream(output);
        CopyStreamEater tout = new CopyStreamEater(p.getInputStream(), fos);
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
    }


    public static void execAndWriteOutput(String[] args, String[] env, File cwd, OutputStream output) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(args, env, cwd);
        CopyStreamEater tout = new CopyStreamEater(p.getInputStream(),output);
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
    }


    /* Eat a stream and log its output */
    static public class LoggingStreamEater extends Thread {
        public LoggingStreamEater(InputStream is, org.apache.log4j.Level level) {
            this.is = is;
            this.level = level;

        }
        @Override
        public void run() {
            Thread.currentThread().setName("Exec-" + Thread.currentThread().getId());
            InheritableNDC.inheritNDC();
            try {
                BufferedReader br = StreamUtils.readStream(is);
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
        InputStream is;
        private static Logger logger = Logger.getLogger("process");
    }


    public static class CopyStreamEater extends Thread {
        public CopyStreamEater(InputStream is, OutputStream os) {
            this.is = is;
            this.os = os;
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
                    os.write(buf, 0, i);
                }
                is.close();
            } catch (IOException e) {
                logger.error("", e);
            }
        }
        OutputStream os;
        InputStream is;
        private static Logger logger = Logger.getLogger("process");

    }

    /* Eat a stream and gather its output */
    public static class GatheringStreamEater extends CopyStreamEater {
        public GatheringStreamEater(InputStream is) {
            super(is, new ByteArrayOutputStream());
            this.is = is;
        }

        public ByteArrayOutputStream getOutput() {
            return (ByteArrayOutputStream) os;
        }
    }

    public static String tailFile(File f, int nlines) throws IOException{
        long skip = Math.max(0L, f.length() - (nlines * 2000L));
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
                throw new IllegalArgumentException("Illegal param: " + val + ", expected key=value");
            }
            String paramValue = val.substring(chunks[0].length() + 1);
            map.put(chunks[0], paramValue);
        }
        return map;
    }

    public static Map<String, String> parseKVStringArray(Collection<String> array) {
        return parseKVStringArray(array.toArray(new String[0]));
    }

    public static byte[] getResourceFileContent(String file) throws IOException {
        InputStream  is = DKUtils.class.getClassLoader().getResourceAsStream(file);
        if (is == null) {
            throw new Error("Can't open resource file " + file);
        }
        return  IOUtils.toByteArray(is);
    }
    public static String getResourceFileContentUTF8(String file) throws IOException {
        InputStream  is = DKUtils.class.getClassLoader().getResourceAsStream(file);
        if (is == null) {
            throw new Error("Can't open resource file " + file);
        }
        return  IOUtils.toString(is, "utf8");
    }

    public static <T> Map<String, T> listToMap(List<T> list, String memberOrFunction) {
        Map<String, T> ret = new HashMap<String, T>();
        if (list.isEmpty()) {
            return ret;
        }
        Class<?> tclazz = list.get(0).getClass();
        try {
            if (memberOrFunction.endsWith("()")) {
                Method m = tclazz.getMethod(memberOrFunction.replace("()", ""));
                for (T obj : list) {
                    String key = (String)m.invoke(obj);
                    ret.put(key, obj);
                }
            } else {
                Field f  = tclazz.getField(memberOrFunction);
                for (T obj : list) {
                    String key = (String)f.get(obj);
                    ret.put(key, obj);
                }
            }
        } catch (Exception e) {
            throw new Error(e);
        }
        return ret;
    }
    
    public static void killProcessTree(Process p) throws IOException {
        /* java.lang.Process.destroy() does SIGKILL on the process ... but it does not kill 
         * the children. And here we have dku>java.
         * So we find out the pid (by dirty reflection hacks) and use pkill to kill the 
         * whole process tree.
         */
        int pid = 0;
        if(p.getClass().getName().equals("java.lang.UNIXProcess")) {
            /* get the PID on unix/linux systems */
            try {
                Field f = p.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                pid = f.getInt(p);
            } catch (Throwable e) {
            }
        }
        if (pid > 0) {
            logger.info("Killing pid " + pid);
            Runtime.getRuntime().exec("pkill -KILL -P " + pid);
        } else {
            throw new IOException("Unable to find pid of process to kill");
        }
    }

    private static Logger logger = Logger.getLogger("dku.utils");
}
