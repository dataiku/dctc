package com.dataiku.dip.utils;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonSyntaxException;
import org.json.JSONException;
import org.junit.Assert;
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
        List<String> names = ImmutableList.of("Jose Bove", "Jean Val Jean");
        Assert.assertEquals(names, nameList.names);
    }


    @Test
    public void testSanitizationTrailingComma() {
        NameList nameList = JSON.parse("{\n" +
                "    \"names\" : [\"Jose Bove\", \"Jean Val Jean\", ]\n" +
                "}", NameList.class);
        List<String> names = ImmutableList.of("Jose Bove", "Jean Val Jean");
        Assert.assertEquals(names, nameList.names);
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
        Assert.assertEquals(names, nameList.names);
    }


    @Test
    public void testSanitizationCommaInString() {
        NameList nameList = JSON.parse("{\n" +
                "    \"names\" : [\"Jose, Bove\", \"Jean Val Jean\"]\n" +
                "}", NameList.class);
        List<String> names = ImmutableList.of("Jose, Bove", "Jean Val Jean");
        Assert.assertEquals(names, nameList.names);
    }


    @Test
    public void testSanitizationEscapeQuoteChar() {
        NameList nameList = JSON.parse("{\n" +
                "    \"names\" : [\"Jose, Bo\\\"ve\", \"Jean Val Jean\"]\n" +
                "}", NameList.class);
        List<String> names = ImmutableList.of("Jose, Bo\"ve", "Jean Val Jean");
        Assert.assertEquals(names, nameList.names);
    }

    @Test
    public void testMultilineComments() {
        NameList nameList = JSON.parse("{ \n" +
                "    \"names\" : [\"Jose Bove\", \"Jean Val Jean\"] /* this is a \n comment */\n" +
                "}", NameList.class);
        List<String> names = ImmutableList.of("Jose Bove", "Jean Val Jean");
        Assert.assertEquals(names, nameList.names);
    }

    @Test
    public void testOnelineComments() {
        NameList nameList = JSON.parse("{\n" +
                "    \"names\" : [\"Jose Bove\", \"Jean Val Jean\"] // this is a comment \n" +
                "}", NameList.class);
        List<String> names = new ArrayList<String>();
        names.add("Jose Bove");
        names.add("Jean Val Jean");
        Assert.assertEquals(names, nameList.names);
    }

    @Test
    public void testQuoteInOneLineComments() {
        NameList nameList = JSON.parse("{\n" +
                "    \"names\" : [\"Jose Bove\", \"Jean Val Jean\"] // this \"is a comment \n" +
                "}", NameList.class);
        List<String> names = ImmutableList.of("Jose Bove", "Jean Val Jean");
        Assert.assertEquals(names, nameList.names);
    }

    @Test
    public void testQuoteInMultilineComments() {
        NameList nameList = JSON.parse("{\n" +
                "    \"names\" : [\"Jose Bove\", \"Jean Val Jean\"] /* this \"is a \n comment */\n" +
                "}", NameList.class);
        List<String> names = ImmutableList.of("Jose Bove", "Jean Val Jean");
        Assert.assertEquals(names, nameList.names);
    }

    @Test
    public void testNeverEndingComment() {
        boolean throwsJSONException = false;
        try {
            JSON.parse("{\n" +
                "    \"names\" : [\"Jose Bove\", \"Jean Val Jean\"] /* this \"is a \n comment \n" +
             "}", NameList.class);
        }
        catch(JsonSyntaxException e) {
           throwsJSONException = (e.getCause().getClass() == JSONException.class);
        }
        finally {
            Assert.assertTrue(throwsJSONException);
        }

    }

}
