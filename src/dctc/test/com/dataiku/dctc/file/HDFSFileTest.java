package com.dataiku.dctc.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.dataiku.dip.hadoop.HadoopUtils;

@Ignore("Can't run this test as a unit test at the moment, as it requires a Hadoop cluster")
public class HDFSFileTest {
    @Before
    public void setUp() {
        Logger.getLogger("org.apache.hadoop.conf.Configuration").setLevel(Level.INFO);
    }
    
    private Configuration getConf() {
        Configuration conf = new Configuration();
        conf.addResource(new Path(HadoopUtils.getCoreSiteLocation()));
        return conf;
    }
    
    private String testFolder() throws Exception {
        return "/tmp/dip-tests/" + this.getClass().getCanonicalName();
    }
    
    private String tmpFolder() throws Exception {
        Thread.sleep(5);
        return "/tmp/dip-tests/" + this.getClass().getCanonicalName() + "/" + System.currentTimeMillis();
    }
    
    @Test
    public void a()  throws IOException {
        HdfsFile file = new HdfsFile("/", "pouet", getConf());
        assertTrue(file.isDirectory());
        assertFalse(file.isEmpty());
        assertTrue(file.list().size() > 0);
    }
    
    @Test
    public void newDirectory()  throws Exception {
        HdfsFile file = new HdfsFile("/", "pouet", getConf());
        
        String testFolder = testFolder();
        long ms = System.currentTimeMillis();
        String tmpFolder = testFolder + "/" + ms;
        
        HdfsFile file2 = file.createSubFile(tmpFolder, "/");
        assertFalse(file2.exists());
        assertFalse(file2.isDirectory());
        assertFalse(file2.isFile());
        
        try {
            file2.isEmpty();
            assertFalse("Exception not thrown", true);
        } catch (IOException e) {
        }
        try {
            file2.getSize();
            assertFalse("Exception not thrown", true);
        } catch (IOException e) {
        }

        
        file2.mkdirs();
        assertTrue(file2.isDirectory());
        assertFalse(file2.isFile());
        assertFalse(file2.isHidden());
        assertTrue(file2.isEmpty());
        assertTrue(file2.list().size() == 0);
        assertEquals(4096, file2.getSize());

        assertEquals(""+ms, file2.getFileName());
        assertEquals(tmpFolder, file2.getAbsolutePath());
        assertEquals("hdfs://" + tmpFolder, file2.getAbsoluteAddress());
        
        String tmpFolder2 = tmpFolder();
        HdfsFile file3 = file.createSubFile(tmpFolder2, "/");
        file3.mkdir();
        assertTrue(file3.isDirectory());
    }
    
    
    @Test
    public void newDirectoryBad()  throws Exception {
        HdfsFile file = new HdfsFile("/", "pouet", getConf());
        
        String tmpFolder = tmpFolder();
        String fooFolder = tmpFolder + "/foo";
        
        HdfsFile fooFolderFile = file.createSubFile(fooFolder, "/");
        try {
            fooFolderFile.mkdir();
            assertFalse("Exception not thrown", true);
        } catch (IOException e) {
        }
        
        fooFolderFile.mkpath();
        
        HdfsFile tmpFolderFile = file.createSubFile(tmpFolder, "/");
        assertTrue(tmpFolderFile.exists());
        assertTrue(tmpFolderFile.isDirectory());
        assertFalse(fooFolderFile.exists());
        assertFalse(fooFolderFile.isDirectory());
        
        fooFolderFile.mkdir();
        assertTrue(fooFolderFile.exists());
        assertTrue(fooFolderFile.isDirectory());
    }
    
    @Test
    public void writeFile()  throws Exception {
        HdfsFile folder = new HdfsFile(tmpFolder(), "pouet", getConf());
        
        HdfsFile file = folder.createSubFile("file0", "/");
        
        assertFalse(file.exists());
        assertTrue(file.hasOutputStream());
        
        OutputStream os = file.outputStream();
        IOUtils.write("lolilol", os);
        os.close();
        
        assertTrue(file.exists());
        
        assertEquals("lolilol", IOUtils.toString(file.inputStream(), "utf8"));
        assertEquals(7, file.getSize());
    }
    
}
