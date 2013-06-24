package com.dataiku.dctc;

import static org.junit.Assert.assertTrue;
import com.dataiku.dip.utils.StreamReplacement;

public class StringReplacementTest {
    @org.junit.Test
    public void test() {
        StreamReplacement sr = new StreamReplacement("fooo", "ba");
        assertTrue("bar ".equals(sr.transform("bar foo", true))); // Check the buffer
        assertTrue("bar".equals(sr.transform("or", false))); // Check overlap and the trannsformation
        assertTrue("".equals(sr.transform("", false))); // Check if the buffer is empty
    }
}
