package com.dataiku.dctc;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.dataiku.dctc.file.FileManipulation;

public class FileManipulationTest {
    public void eq(String a, String b) {
        if (!a.equals(b)) {
            System.out.println(a + " " + b);
        }
        assertTrue(a.equals(b));
    }
    @org.junit.Test
    public void extension() {
        eq("bar", FileManipulation.extension("foo.bar", "."));
        eq("bar", FileManipulation.extension("foo.bar"));
        eq("",    FileManipulation.extension("foo", "."));
        eq("o",   FileManipulation.extension("bfo", "f"));
    }
    @org.junit.Test
    public void translatePath() {
        eq("bar/baz", FileManipulation.translatePath("bar\\baz", "\\", "/"));
        eq("barbbaz", FileManipulation.translatePath("bar/baz", "/", "b"));
    }
    @org.junit.Test
    public void concat() {
        eq("foo/bar",   FileManipulation.concat("foo", "bar", "/"));
        eq("foo/bar",   FileManipulation.concat("foo/", "bar", "/"));
        eq("foo/bar",   FileManipulation.concat("foo", "/bar", "/"));
        eq("foo/bar",   FileManipulation.concat("foo/", "/bar", "/"));
        eq("foo/bar", FileManipulation.concat("foo//", "//bar", "/"));
    }
    @org.junit.Test
    public void trimEnd() {
        eq("foo", FileManipulation.trimEnd("foo", "/"));
        eq("foo", FileManipulation.trimEnd("foo///", "/"));
        eq("f",   FileManipulation.trimEnd("f", "o"));
        eq("",    FileManipulation.trimEnd("foo", "foo"));
    }
    @org.junit.Test
    public void trimBegin() {
        eq("foo", FileManipulation.trimBegin("foo", "/"));
        eq("foo", FileManipulation.trimBegin("//foo", "/"));
        eq("foo", FileManipulation.trimBegin("/foo", "/"));
        eq("oo",  FileManipulation.trimBegin("foo", "f"));
        eq("",    FileManipulation.trimBegin("foo", "foo"));
    }
    @org.junit.Test
    public void split()
    {
        String[] split = FileManipulation.split("foo", "foo", 2);
        eq("", split[0]);
        eq("", split[1]);

        split = FileManipulation.split("fof", "o", 2);
        eq("f", split[0]);
        eq("f", split[1]);

        split = FileManipulation.split("foof", "o", 4);
        eq("f", split[0]);
        eq("",  split[1]);
        eq("f", split[2]);
        eq("",  split[3]);

        split = FileManipulation.split("ofoofo", "f", 2);
        eq("o", split[0]);
        eq("oofo", split[1]);
    }
    @org.junit.Test
    public void envSplit()
    {
        String[] split = FileManipulation.invSplit("foo", "foo", 2);
        eq("", split[0]);
        eq("", split[1]);

        split = FileManipulation.invSplit("fof", "o", 2);
        eq("f", split[0]);
        eq("f", split[1]);

        split = FileManipulation.invSplit("foof", "o", 4);
        eq("f", split[0]);
        eq("",  split[1]);
        eq("f", split[2]);
        eq("",  split[3]);

        split = FileManipulation.invSplit("ofoofo", "f", 2);
        eq("o", split[0]);
        eq("ofoo", split[1]);
    }
    @org.junit.Test
    public void getPath() {
        eq("foo",     FileManipulation.getPath("foo/bar", "/"));
        eq("/",       FileManipulation.getPath("/foo/", "/"));
        eq("/oo/bar", FileManipulation.getPath("/oo/bar/baz", "/"));
        eq("/o",      FileManipulation.getPath("/o/o", "/"));
        eq("foo/a",   FileManipulation.getPath("foo/a/b", "/"));
    }
    @org.junit.Test
    public void getFileName() {
        eq("foo", FileManipulation.getFileName("/bar/foo", "/"));
        eq("o",   FileManipulation.getFileName("/o", "/"));
        eq("bar", FileManipulation.getFileName("/foo/bar/", "/"));
        eq("bar", FileManipulation.getFileName("/foo/bar", "/"));
    }
    @org.junit.Test
    public void getSonPath() {
        eq("foo/ba", FileManipulation.getSonPath("blabla", "blabla/foo/ba", "/"));
        eq("foo/ba/", FileManipulation.getSonPath("blabla", "blabla/foo/ba/", "/"));
        eq("foo/ba", FileManipulation.getSonPath("/blabla", "/blabla/foo/ba", "/"));
        eq("foo/ba/", FileManipulation.getSonPath("/blabla", "/blabla/foo/ba/", "/"));
    }
    @org.junit.Test
    public void isHidden() {
        assertTrue(FileManipulation.isHidden(".foo", "/"));
        assertTrue(FileManipulation.isHidden("/bar/.foo", "/"));
        assertTrue(FileManipulation.isHidden("/bar/..foo", "/"));
        assertFalse(FileManipulation.isHidden("/bar/foo", "/"));
        assertFalse(FileManipulation.isHidden("foo", "/"));
    }
    @org.junit.Test
    public void isTempFile() {
        assertTrue(FileManipulation.isTempFile("foo~", "/"));
        assertTrue(FileManipulation.isTempFile("/foo/bar~", "/"));
        assertTrue(FileManipulation.isTempFile("/fooo/.#foo", "/"));
        assertTrue(FileManipulation.isTempFile("/ofo/#bar", "/"));
    }
    @org.junit.Test
    public void isDirectSon() {
        assertTrue(FileManipulation.isDirectSon("/oo", "/oo/bar", "/"));
    }
}
