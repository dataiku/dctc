package com.dataiku.dip.utils;

import com.vividsolutions.jts.util.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class JSONTest {


    public static class NameList {
        List<String> names;
    }

    @Test
    public void testSanitizationSimple() {
        NameList nameList = JSON.parse("{\n" +
        "    \"names\" : [\"Jose Bove\", \"Jean Val Jean\"]\n" +
        "}", NameList.class);
        List<String> names = new ArrayList<String>();
        names.add("Jose Bove");
        names.add("Jean Val Jean");
        Assert.equals(names, nameList.names);
    }


    @Test
    public void testSanitizationTrailingComma() {
        NameList nameList = JSON.parse("{\n" +
                "    \"names\" : [\"Jose Bove\", \"Jean Val Jean\", ]\n" +
                "}", NameList.class);
        List<String> names = new ArrayList<String>();
        names.add("Jose Bove");
        names.add("Jean Val Jean");
        Assert.equals(names, nameList.names);
    }

    @Test
    public void testSanitizationCommaNull() {
        NameList nameList = JSON.parse("{\n" +
                "    \"names\" : [\"Jose Bove\", \"Jean Val Jean\", null]\n" +
                "}", NameList.class);
        List<String> names = new ArrayList<String>();
        names.add("Jose Bove");
        names.add("Jean Val Jean");
        names.add(null);
        Assert.equals(names, nameList.names);
    }


    @Test
    public void testSanitizationCommaInString() {
        NameList nameList = JSON.parse("{\n" +
                "    \"names\" : [\"Jose, Bove\", \"Jean Val Jean\"]\n" +
                "}", NameList.class);
        List<String> names = new ArrayList<String>();
        names.add("Jose, Bove");
        names.add("Jean Val Jean");
        Assert.equals(names, nameList.names);
    }



    @Test
    public void testSanitizationEscapeQuoteChar() {
        NameList nameList = JSON.parse("{\n" +
                "    \"names\" : [\"Jose, Bo\\\"ve\", \"Jean Val Jean\"]\n" +
                "}", NameList.class);
        List<String> names = new ArrayList<String>();
        names.add("Jose, Bo\"ve");
        names.add("Jean Val Jean");
        Assert.equals(names, nameList.names);
    }

}
