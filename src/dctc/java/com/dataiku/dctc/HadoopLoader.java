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
    /** 
     * Returns whether Hadoop is enabled on this DSS instance
     */
    public static boolean hadoopEnabled() {
        if (hadoopEnabled != null) return hadoopEnabled;
        String envVar = System.getenv("DKU_HADOOP_ENABLED");
        if (envVar != null && envVar.equalsIgnoreCase("true")) {
            hadoopEnabled = true;
        } else if (envVar != null && envVar.equalsIgnoreCase("false")) {
            hadoopEnabled = false;
        } else {
            try {
                getConfigLocations();
                hadoopEnabled = true;
            } catch (Exception e) {
                hadoopEnabled  = false;
            }
        }
        return hadoopEnabled;
    }
    

    public static FileSystem getFS() throws IOException{
        HadoopLoader.addLibraries();
        FileSystem fs =  FileSystem.get(new Configuration(true));
        if (fs instanceof RawLocalFileSystem || fs  instanceof LocalFileSystem){
            logger.info("Wrong configuration, was created with " + new Configuration(true).toString());
            logger.info("fs.defaultFS=" + new Configuration(true).get("fs.defaultFS"));
            //throw new IOException("HDFS initialization returned a LocalFileSystem. Maybe you need to configure your HDFS location?");
        }
        return fs;
    }
    
    public static boolean isMAPR() {
        return new File("/opt/mapr").isDirectory();
    }

    public static boolean isCDH4Package() {
        if (new File("/usr/lib/hadoop/cloudera").isDirectory()) {
            logger.info("Detected Cloudera distribution using packages");
            try {
                String content = FileUtils.readFileToString(new File("/usr/lib/hadoop/cloudera/cdh_version.properties"));
                if (content.indexOf("cdh4") > 0 ) {
                    return true;
                }
            } catch (Exception e) {}
        }
        return false;
    }

    public static boolean isCDH4Parcel() {
        // TODO - this path can be customized in /etc/cloudera-scm-agent/config.ini
        if (new File("/opt/cloudera/parcels/CDH/lib/hadoop/cloudera").isDirectory()) {
            logger.info("Detected Cloudera distribution using parcels");
            try {
                String content = FileUtils.readFileToString(new File("/opt/cloudera/parcels/CDH/lib/hadoop/cloudera/cdh_version.properties"));
                if (content.indexOf("cdh4") > 0 ) {
                    return true;
                }
            } catch (Exception e) {}
        }
        return false;
    }

    public static boolean isCDH4() {
        return isCDH4Parcel() || isCDH4Package();
    }

    public static List<File> getHadoopCodeJARs() {
        if (isCDH4Parcel()) {
            String dir = "/opt/cloudera/parcels/CDH/lib/";
            return Lists.newArrayList(
                    new File(dir + "hadoop/lib/protobuf-java-2.4.0a.jar"),
                    new File(dir + "hadoop/lib/guava-11.0.2.jar"),
                    new File(dir + "hadoop/lib/slf4j-api-1.6.1.jar"),
                    new File(dir + "hadoop/lib/slf4j-log4j12-1.6.1.jar"),
                    new File(dir + "hadoop/lib/commons-configuration-1.6.jar"),
                    new File(dir + "hadoop-mapreduce/hadoop-mapreduce-client-core.jar"),
                    new File(dir + "hadoop/lib/avro-1.7.4.jar"),
                    new File(dir + "hadoop-hdfs/hadoop-hdfs.jar"),
                    new File(dir + "hadoop/hadoop-common.jar"),
                    new File(dir + "hadoop/hadoop-auth.jar"));
        } else if (isCDH4Package()) {
            return Lists.newArrayList(
                    new File("/usr/lib/hadoop/lib/protobuf-java-2.4.0a.jar"),
                    new File("/usr/lib/hadoop/lib/guava-11.0.2.jar"),
                    new File("/usr/lib/hadoop/lib/slf4j-api-1.6.1.jar"),
                    new File("/usr/lib/hadoop/lib/slf4j-log4j12-1.6.1.jar"),
                    new File("/usr/lib/hadoop/lib/commons-configuration-1.6.jar"),
                    new File("/usr/lib/hadoop-hdfs/hadoop-hdfs.jar"),
                    new File("/usr/lib/hadoop/hadoop-common.jar"),
                    new File("/usr/lib/hadoop-mapreduce/hadoop-mapreduce-client-core.jar"),
                    new File("/usr/lib/hadoop/lib/avro-1.7.4.jar"),
                    new File("/usr/lib/hadoop/hadoop-auth.jar"));
        } else if (isMAPR()) {
            // TODO : Sequence file support on Mapr
            return Lists.newArrayList(
                    new File("/opt/mapr/hadoop/hadoop-0.20.2/lib/zookeeper-3.3.6.jar"),
                    new File("/opt/mapr/hadoop/hadoop-0.20.2/lib/maprfs-0.1.jar"),
                    new File("/opt/mapr/hadoop/hadoop-0.20.2/lib/hadoop-0.20.2-dev-core.jar"),
                    new File("/opt/mapr/hadoop/hadoop-0.20.2/lib/maprfs-test-0.1.jar"));
        } else {
            // Custom distribution 
            String[] hadoopClassPath = getHadoopClassPathFromBin();

            String[] filesToLookFor = new String[] {
                    "hadoop-core-.*.jar",
                    "hadoop-common-.*jar",
                    "hadoop-auth-.*.jar",
                    "protobuf-java-.*.jar",
                    "guava-.*.jar",
                    "slf4j-api-.*.jar",
                    "slf4j-log4j.*.jar",
                    "commons-configuration-.*.jar"
            };

            if (hadoopClassPath != null) {

                List<File> files = new ArrayList<File>();

                for(String path : hadoopClassPath) {
                    if (path.endsWith("*")) {
                        path = path.substring(0, path.length()-1);
                    } else if (path.endsWith("*.jar")) {
                        path = path.substring(0, path.length()-4);
                    }
                    File dir = new File(path);


                    for(String fileToLookFor : filesToLookFor) {
                        files.addAll(expand(dir, fileToLookFor));
                    }
                }

                return files;

            } else {

                String hadoopHome = getHadoopHomeUsingEnvVars();

                if (hadoopHome ==null ){
                    throw new IllegalArgumentException("Could not locate Hadoop home, using either 'hadoop classpath', $HADOOP_HOME OR $HADOOP_PREFIX");
                }
                logger.info("Detected custom Hadoop distribution in " + hadoopHome);
                List<File> files =  expand(new File(hadoopHome), "hadoop-core-.*.jar");
                files.addAll(expand(new File(hadoopHome), "hadoop-common-.*jar"));
                files.addAll(expand(new File(hadoopHome, "lib"), "hadoop-auth-.*.jar"));
                files.addAll(expand(new File(hadoopHome, "lib"), "protobuf-java-.*.jar"));
                files.addAll(expand(new File(hadoopHome, "lib"), "guava-.*.jar"));
                files.addAll(expand(new File(hadoopHome, "lib"), "slf4j-api-.*.jar"));
                files.addAll(expand(new File(hadoopHome, "lib"), "slf4j-log4j.*.jar"));
                files.addAll(expand(new File(hadoopHome, "lib"), "commons-configuration-.*.jar"));
                files.addAll(expand(new File(hadoopHome, "lib"), "commons-configuration-.*.jar"));
                return files;
            }
        }
    }

    /**
     * Load the required libraries to make Hadoop work.
     * 
     * For DSS, it means Hadoop conf and MapR native libraries.
     * Additionally, for dctc, we load JARs
     */
    public static void addLibraries() {
        if (librariesAdded) return;
        librariesAdded = true;

        try {
            if (System.getenv("DCTC_AUTO_LOAD_HADOOP") != null) {
                logger.info("Loading Jars for DCTC");
                for (File f : getHadoopCodeJARs()) {
                    loadJar(f);
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
                loadJar(f);
            }
        } catch (Exception e) {
            logger.warn("Failed to set distribution-specific parameters", e);
        }
    }

    
    /** Try to find Hadoop configuration locations, using "hadoop classpath" and env vars as a fallback */
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
            logger.debug("Could not read Hadoop classpath using command, retrying with HADOOP_HOME: " + e.getMessage());
            String hadoopHome = getHadoopHomeUsingEnvVars();
            if (hadoopHome == null) {
                throw new IllegalArgumentException("Could not locate Hadoop conf, using either 'hadoop classpath', $HADOOP_HOME OR $HADOOP_PREFIX");
            } else {
                configLocations.add(new File(hadoopHome, "conf"));
            }
        }
        logger.info("Detected Hadoop configuration in " + org.apache.commons.lang.StringUtils.join(configLocations, ";"));
        return configLocations;
    }

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


    /** 
     * Return null if fails.
     */
    private static String[] getHadoopClassPathFromBin() {
        try {
            String hadoopClasspath = new String(DKUtils.execAndGetOutput(new String[]{"hadoop", "classpath"}, null), "UTF-8");
            return hadoopClasspath.split(":");
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Return null if fails
     */
    private static String getHadoopHomeUsingEnvVars() {
        String hadoopHome = System.getenv("HADOOP_HOME");
        if (hadoopHome == null) {
            hadoopHome = System.getenv("HADOOP_PREFIX");
        }
        return hadoopHome;
    }

    private static void loadJar(File file) throws Exception {
        if (file.exists()) {
            logger.info("Adding library : " + file);
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class<?>[]{URL.class});
            method.setAccessible(true);
            method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
        } else {
            logger.warn("Did not find JAR to load: " + file);
        }
    }

    private static Boolean hadoopEnabled = null;
    private static boolean librariesAdded = false;
    private static Logger logger = Logger.getLogger("dku.hadoop");
}