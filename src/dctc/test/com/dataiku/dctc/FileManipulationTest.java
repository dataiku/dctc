package com.dataiku.dctc;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.dataiku.dctc.file.PathManip;

public class FileManipulationTest {
    public void eq(String a, String b) {
        if (!a.equals(b)) {
            System.out.println(a + " " + b);
        }
        assertTrue(a.equals(b));
    }
    @org.junit.Test
    public void extension() {
        eq("bar", PathManip.extension("foo.bar", "."));
        eq("bar", PathManip.extension("foo.bar"));
        eq("",    PathManip.extension("foo", "."));
        eq("o",   PathManip.extension("bfo", "f"));
    }
    @org.junit.Test
    public void translatePath() {
        eq("bar/baz", PathManip.translatePath("bar\\baz", "\\", "/"));
        eq("barbbaz", PathManip.translatePath("bar/baz", "/", "b"));
    }
    @org.junit.Test
    public void concat() {
        eq("foo/bar",   PathManip.concat("foo", "bar", "/"));
        eq("foo/bar",   PathManip.concat("foo/", "bar", "/"));
        eq("foo/bar",   PathManip.concat("foo", "/bar", "/"));
        eq("foo/bar",   PathManip.concat("foo/", "/bar", "/"));
        eq("foo/bar", PathManip.concat("foo//", "//bar", "/"));
    }
    @org.junit.Test
    public void trimEnd() {
        eq("foo", PathManip.trimEnd("foo", "/"));
        eq("foo", PathManip.trimEnd("foo///", "/"));
        eq("f",   PathManip.trimEnd("f", "o"));
        eq("",    PathManip.trimEnd("foo", "foo"));
    }
    @org.junit.Test
    public void trimBegin() {
        eq("foo", PathManip.trimBegin("foo", "/"));
        eq("foo", PathManip.trimBegin("//foo", "/"));
        eq("foo", PathManip.trimBegin("/foo", "/"));
        eq("oo",  PathManip.trimBegin("foo", "f"));
        eq("",    PathManip.trimBegin("foo", "foo"));
    }
    @org.junit.Test
    public void split()
    {
        String[] split = PathManip.split("foo", "foo", 2);
        eq("", split[0]);
        eq("", split[1]);

        split = PathManip.split("fof", "o", 2);
        eq("f", split[0]);
        eq("f", split[1]);

        split = PathManip.split("foof", "o", 4);
        eq("f", split[0]);
        eq("",  split[1]);
        eq("f", split[2]);
        eq("",  split[3]);

        split = PathManip.split("ofoofo", "f", 2);
        eq("o", split[0]);
        eq("oofo", split[1]);
    }
    @org.junit.Test
    public void envSplit()
    {
        String[] split = PathManip.invSplit("foo", "foo", 2);
        eq("", split[0]);
        eq("", split[1]);

        split = PathManip.invSplit("fof", "o", 2);
        eq("f", split[0]);
        eq("f", split[1]);

        split = PathManip.invSplit("foof", "o", 4);
        eq("f", split[0]);
        eq("",  split[1]);
        eq("f", split[2]);
        eq("",  split[3]);

        split = PathManip.invSplit("ofoofo", "f", 2);
        eq("o", split[0]);
        eq("ofoo", split[1]);
    }
    @org.junit.Test
    public void getPath() {
        eq("foo",     PathManip.getPath("foo/bar", "/"));
        eq("/",       PathManip.getPath("/foo/", "/"));
        eq("/oo/bar", PathManip.getPath("/oo/bar/baz", "/"));
        eq("/o",      PathManip.getPath("/o/o", "/"));
        eq("foo/a",   PathManip.getPath("foo/a/b", "/"));
    }
    @org.junit.Test
    public void getFileName() {
        eq("foo", PathManip.getFileName("/bar/foo", "/"));
        eq("o",   PathManip.getFileName("/o", "/"));
        eq("bar", PathManip.getFileName("/foo/bar/", "/"));
        eq("bar", PathManip.getFileName("/foo/bar", "/"));
    }
    @org.junit.Test
    public void getSonPath() {
        eq("foo/ba", PathManip.getSonPath("blabla", "blabla/foo/ba", "/"));
        eq("foo/ba/", PathManip.getSonPath("blabla", "blabla/foo/ba/", "/"));
        eq("foo/ba", PathManip.getSonPath("/blabla", "/blabla/foo/ba", "/"));
        eq("foo/ba/", PathManip.getSonPath("/blabla", "/blabla/foo/ba/", "/"));
    }
    @org.junit.Test
    public void isHidden() {
        assertTrue(PathManip.isHidden(".foo", "/"));
        assertTrue(PathManip.isHidden("/bar/.foo", "/"));
        assertTrue(PathManip.isHidden("/bar/..foo", "/"));
        assertFalse(PathManip.isHidden("/bar/foo", "/"));
        assertFalse(PathManip.isHidden("foo", "/"));
    }
    @org.junit.Test
    public void isTempFile() {
        assertTrue(PathManip.isTempFile("foo~", "/"));
        assertTrue(PathManip.isTempFile("/foo/bar~", "/"));
        assertTrue(PathManip.isTempFile("/fooo/.#foo", "/"));
        assertTrue(PathManip.isTempFile("/ofo/#bar", "/"));
    }
    @org.junit.Test
    public void isDirectSon() {
        assertTrue(PathManip.isDirectSon("/oo", "/oo/bar", "/"));
    }
}
