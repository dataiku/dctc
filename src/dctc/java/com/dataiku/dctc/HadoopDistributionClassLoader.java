package com.dataiku.dctc;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.dataiku.dip.hadoop.HadoopUtils;

public class HadoopDistributionClassLoader {
    private static void addSoftwareLibrary(File file) throws Exception {
        if (file.exists()) { 
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file.toURI().toURL()});
        } else {
            logger.warn("Did not find JAR to load : " + file);
        }
    }
    
    private static void addSoftwareLibrary(File directory, String pattern) throws Exception {
        if (directory.isDirectory()) {
            for (File f : directory.listFiles()) {
                if (f.getName().matches(pattern)) {
                    addSoftwareLibrary(f);
                }
            }
        } else {
            logger.warn("Did not find directory to load from " + directory);
        }
    }

    public static String guessHadoopHome() {
        if (isCDH4()) {
            return "/etc/hadoop";
        } else if (isMAPR()) {
            return "/opt/mapr/hadoop/hadoop-0.20.2/";
        } else {
            return null;
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
   
    private static boolean librariesAdded = false;


    public static void addLibraries() {
        if (librariesAdded) return;
        librariesAdded = true;
        
        logger.info("Loading Hadoop libraries");
        try {
            if (isCDH4()) {
                logger.info("Detected CDH4 distribution");
                for (String path : new String[]{
                        "/usr/lib/hadoop/lib/protobuf-java-2.4.0a.jar",
                        "/usr/lib/hadoop/lib/guava-11.0.2.jar",
                        "/usr/lib/hadoop/lib/slf4j-api-1.6.1.jar",
                        "/usr/lib/hadoop-hdfs/hadoop-hdfs.jar",
                        "/usr/lib/hadoop/hadoop-common.jar",
                "/usr/lib/hadoop/hadoop-auth.jar"}) {
                    File f = new File(path);
                    if (f.exists()) {
                        addSoftwareLibrary(f);
                    } else {
                        logger.warn("Did not find jar to load " + f);
                    }
                }
            } else if (isMAPR()) {
                logger.info("Detected MAPR distribution");
                System.setProperty("java.library.path", "/opt/mapr/lib");
                /* Force Java to reload library path .*/
                // From : http://blog.cedarsoft.com/2010/11/setting-java-library-path-programmatically/
                Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
                fieldSysPath.setAccessible( true );
                fieldSysPath.set( null, null );
                
                addSoftwareLibrary(new File("/opt/mapr/hadoop/hadoop-0.20.2/lib/zookeeper-3.3.6.jar"));
                addSoftwareLibrary(new File("/opt/mapr/hadoop/hadoop-0.20.2/lib"), "maprfs-.*.jar");
                addSoftwareLibrary(new File("/opt/mapr/hadoop/hadoop-0.20.2/lib/hadoop-0.20.2-dev-core.jar"));
            } else {
                logger.info("Generic generic Hadoop distribution");
                addSoftwareLibrary(new File(HadoopUtils.getHadoopHome()), "hadoop-core-.*.jar");
            }
        } catch (Exception e) {
            logger.warn("Failed to set distribution-specific parameters", e);
        }
    }
    private static Logger logger = Logger.getLogger("dctc.hadoop");
}
