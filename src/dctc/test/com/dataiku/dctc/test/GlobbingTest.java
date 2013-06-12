package com.dataiku.dctc.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import com.dataiku.dctc.Globbing;

public class GlobbingTest {
    @org.junit.Test
    public void match() {
        assertTrue(Globbing.match("*", "foo"));
        assertTrue(Globbing.match("*", ""));
        assertTrue(Globbing.match("f*o", "foo"));
        assertTrue(Globbing.match("f*o", "fo"));
        assertTrue(Globbing.match("f?o", "fao"));
        assertFalse(Globbing.match("f?o", "fo"));
        assertTrue(Globbing.match("", ""));
        assertFalse(Globbing.match("f", ""));
        assertFalse(Globbing.match("", "f"));
        assertTrue(Globbing.match("[a]", "a"));
        assertTrue(Globbing.match("[abc]", "a"));
        assertTrue(Globbing.match("[abc]", "b"));
        assertTrue(Globbing.match("[abc]", "c"));
        assertTrue(Globbing.match("[a-c]", "a"));
        assertTrue(Globbing.match("[a-c]", "b"));
        assertTrue(Globbing.match("[a-c]", "c"));
        assertTrue(Globbing.match("[][]", "]"));
        assertTrue(Globbing.match("[][]", "["));
        assertTrue(Globbing.match("[!!]", "^"));
        assertFalse(Globbing.match("[!!]", "!"));
        assertTrue(Globbing.match("[^^]", "!"));
        assertFalse(Globbing.match("[][]", "^"));
        assertTrue(Globbing.match("[][]", "["));
        assertTrue(Globbing.match("[a-zA-Z]", "A"));
        assertFalse(Globbing.match("[a-zA-Z]", "-"));
        assertTrue(Globbing.match("[a-]", "-"));
        assertTrue(Globbing.match("[a-]", "a"));
        assertTrue(Globbing.match("[-a]", "a"));
        assertTrue(Globbing.match("[-a]", "-"));
        assertTrue(Globbing.match("[][-]", "-"));
        assertTrue(Globbing.match("[][-]", "]"));
        assertTrue(Globbing.match("[][-]", "["));
        assertFalse(Globbing.match("[][-]", "\\"));
        assertFalse(Globbing.match("[!a-c]", "a"));
        assertFalse(Globbing.match("[!a-c]", "b"));
        assertFalse(Globbing.match("[!a-c]", "c"));
        assertTrue(Globbing.match("[!a-c]", "d"));
        assertFalse(Globbing.match("[^a-c]", "a"));
        assertFalse(Globbing.match("[^a-c]", "b"));
        assertFalse(Globbing.match("[^a-c]", "c"));
        assertTrue(Globbing.match("[^a-c]", "d"));
        assertTrue(Globbing.match("*[a-c]*", "cd"));
        assertTrue(Globbing.match("[c]*", "c"));
        assertTrue(Globbing.match("[a-c]*", "c"));
        assertTrue(Globbing.match("*[a-c]*", "dc"));
        assertFalse(Globbing.match("*[a-c]*", "d"));
        assertTrue(Globbing.match("\\*", "*"));
        assertTrue(Globbing.match("\\[", "["));
        assertTrue(Globbing.match("\\-", "-"));
        assertTrue(Globbing.match("\\?", "?"));
        assertTrue(Globbing.match("\\a", "a"));
        assertFalse(Globbing.match("\\*", "a"));
        assertFalse(Globbing.match("\\?", "a"));
        assertTrue(Globbing.match("\\[a-b]", "[a-b]"));
    }

    @org.junit.Test
    public void matchPath() {
        assertTrue(Globbing.matchPath("*/*.c", "foobar/baz.c", "/"));
        assertTrue(Globbing.matchPath("?/*.c", "f/baz.c", "/"));
        assertTrue(Globbing.matchPath("foobar/baz.c", "foobar/baz.c", "/"));
        assertFalse(Globbing.matchPath("foobar/quz.c", "foobar/baz.c", "/"));
        assertFalse(Globbing.matchPath("*.c", "foobar/baz.c", "/"));
        assertFalse(Globbing.matchPath("*/*.c", "baz.c", "/"));
        assertFalse(Globbing.matchPath("/*/*.c", "baz.c", "/"));
        assertTrue(Globbing.matchPath("/*/*.c", "/foo/baz.c", "/"));
        assertTrue(Globbing.matchPath("/*/*.c", "/foo/baz.c", "/"));
        assertFalse(Globbing.matchPath("/*.c", "/foo/baz.c", "/"));
    }
}
