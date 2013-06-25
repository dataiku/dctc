package com.dataiku.dctc;

import static org.junit.Assert.assertTrue;
import com.dataiku.dip.utils.StreamReplacement;

public class StringReplacementTest {
    @org.junit.Test
    public void test() {
        StreamReplacement sr = new StreamReplacement("fooo", "ba");
        assertTrue(eq("bar ", sr.transform("bar foo", true))); // Check the buffer
        assertTrue(eq("bar", sr.transform("or", false))); // Check overlap and the transformation
        assertTrue(eq("", sr.transform("", false))); // Check if the buffer is empty
    }

    private boolean eq(String a, String b) {
        return a.equals(b);
    }
}
