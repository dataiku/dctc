package com.dataiku.dctc;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.RawLocalFileSystem;
import org.apache.log4j.Logger;

import com.dataiku.dip.utils.DKUtils;
import com.google.common.collect.Lists;

public class HadoopLoader {
    private static List<File> expand(File directory, String pattern) {
        List<File> files = new ArrayList<File>();

        if (directory.isDirectory()) {
            for (File f : directory.listFiles()) {
                if (f.getName().matches(pattern)) {
                    files.add(f);
                }
            }
        }
        return files;
    }
    
    public static boolean hadoopEnabled() {
        if (hadoopEnabled != null) return hadoopEnabled;
        try {
            getConfigLocations();
            hadoopEnabled = true;
        } catch (Exception e) {
            hadoopEnabled  = false;
        }
        return hadoopEnabled;
    }
    private static Boolean hadoopEnabled = null;

    private static String getHadoopHome() {
        String hadoopHome = System.getenv("HADOOP_HOME");
        if (hadoopHome == null) {
            hadoopHome = System.getenv("HADOOP_PREFIX");
        }
        if (hadoopHome == null) {
            if (isCDH4()) {
                hadoopHome = "/etc/hadoop";
            } else if (isMAPR()) {
                hadoopHome = "/opt/mapr/hadoop/hadoop-0.20.2/";
            }
        }
        if (hadoopHome == null) {
            throw new IllegalArgumentException("Cannot find hadoop home: need HADOOP_HOME or HADOOP_PREFIX environment variable");
        }
        if (!hadoopHome.endsWith("/"))  {
            hadoopHome += "/";
        }
        return hadoopHome;
    }

    private static void addSoftwareLibrary(File file) throws Exception {
        if (file.exists()) {
            logger.info("Adding library : " + file);
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class<?>[]{URL.class});
            method.setAccessible(true);
            method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
        } else {
            logger.warn("Did not find JAR to load: " + file);
        }
    }

    public static boolean isMAPR() {
        return new File("/opt/mapr").isDirectory();
    }

    public static boolean isCDH4() {
        if (new File("/usr/lib/hadoop/cloudera").isDirectory()) {
            logger.info("Detected Cloudera distribution");
            try {
                String content = FileUtils.readFileToString(new File("/usr/lib/hadoop/cloudera/cdh_version.properties"));
                if (content.indexOf("cdh4") > 0 ) {
                    return true;
                }
            } catch (Exception e) {}
        }
        return false;
    }

    public static List<File> getHadoopCodeJARs() {
        if (isCDH4()) {
            return Lists.newArrayList(
                    new File("/usr/lib/hadoop/lib/protobuf-java-2.4.0a.jar"),
                    new File("/usr/lib/hadoop/lib/guava-11.0.2.jar"),
                    new File("/usr/lib/hadoop/lib/slf4j-api-1.6.1.jar"),
                    new File("/usr/lib/hadoop/lib/slf4j-log4j12-1.6.1.jar"),
                    new File("/usr/lib/hadoop/lib/commons-configuration-1.6.jar"),
                    new File("/usr/lib/hadoop-hdfs/hadoop-hdfs.jar"),
                    new File( "/usr/lib/hadoop/hadoop-common.jar"),
                    new File( "/usr/lib/hadoop/hadoop-auth.jar"));
        } else if (isMAPR()) {
            return Lists.newArrayList(
                    new File("/opt/mapr/hadoop/hadoop-0.20.2/lib/zookeeper-3.3.6.jar"),
                    new File("/opt/mapr/hadoop/hadoop-0.20.2/lib/maprfs-0.1.jar"),
                    new File("/opt/mapr/hadoop/hadoop-0.20.2/lib/hadoop-0.20.2-dev-core.jar"),
                    new File("/opt/mapr/hadoop/hadoop-0.20.2/lib/maprfs-test-0.1.jar"));
        } else {
            List<File> files =  expand(new File(getHadoopHome()), "hadoop-core-.*.jar");
            files.addAll(expand(new File(getHadoopHome(), "lib"), "protobuf-java-*.jar"));
            files.addAll(expand(new File(getHadoopHome(), "lib"), "guava-*.jar"));
            files.addAll(expand(new File(getHadoopHome(), "lib"), "sl4j-api-*.jar"));
            files.addAll(expand(new File(getHadoopHome(), "lib"), "sl4j-log4j-*.jar"));
            files.addAll(expand(new File(getHadoopHome(), "lib"), "commons-configuration-*.jar"));
            files.addAll(expand(new File(getHadoopHome(), "lib"), "commons-configuration-*.jar"));
            return files;
        }
    }

    public static void addLibraries() {
        if (librariesAdded) return;
        librariesAdded = true;

        try {
            if (System.getenv("DCTC_AUTO_LOAD_HADOOP") != null) {
                logger.info("Loading Jars for DCTC");
                for (File f : getHadoopCodeJARs()) {
                    addSoftwareLibrary(f);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to set distribution-specific parameters", e);
        }
        try {
            /* Always load MAPR native library */
            if (isMAPR()) {
                logger.info("Loading native MAPR library");
                System.setProperty("java.library.path", "/opt/mapr/lib");
                /* Force Java to reload library path .*/
                // From : http://blog.cedarsoft.com/2010/11/setting-java-library-path-programmatically/
                Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
                fieldSysPath.setAccessible( true );
                fieldSysPath.set( null, null );
            }
        } catch (Exception e) {
            logger.warn("Failed to set distribution-specific parameters", e);
        }

        logger.info("Loading Hadoop configuration locations");
        try {
            for (File f : getConfigLocations()) {
                addSoftwareLibrary(f);
            }
        } catch (Exception e) {
            logger.warn("Failed to set distribution-specific parameters", e);
        }
    }

    private static List<File> getConfigLocations() {
        List<File> configLocations = new ArrayList<File>();
        try {
            // FIXME: Should not be UTF8 but native FS encoding ??
          String hadoopClasspath = new String(DKUtils.execAndGetOutput(new String[]{"hadoop", "classpath"}, null), "UTF-8");

            for (String chunk : hadoopClasspath.split(":")) {
                if (chunk.contains("conf")) {
                    configLocations.add(new File(chunk));
                }
            }
        } catch (Exception e) {
            logger.info("Could not read Hadoop classpath, retrying with HADOOP_HOME", e);
            configLocations.add(new File(getHadoopHome()));
        }
        return configLocations;
    }

    public static FileSystem getFS() throws IOException{
        HadoopLoader.addLibraries();
        FileSystem fs =  FileSystem.get(new Configuration(true));
        if (fs instanceof RawLocalFileSystem || fs  instanceof LocalFileSystem){
            logger.info("Wrong configuration, was created with " + new Configuration(true).toString());
            logger.info("fs.defaultFS=" + new Configuration(true).get("fs.defaultFS"));
            throw new IOException("HDFS initialization returned a LocalFileSystem. Maybe you need to configure your HDFS location?");
        }
        return fs;
    }

    private static boolean librariesAdded = false;
    private static Logger logger = Logger.getLogger("dku.hadoop");
}