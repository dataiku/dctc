package com.dataiku.dip.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.mutable.MutableInt;

public class NumArrays {

    /* Find the position of the maximum value in an array */

    public static int maxIndex(int[] array) {
        int maxVal = Integer.MIN_VALUE;
        int maxPos = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > maxVal) {
                maxVal = array[i];
                maxPos = i;
            }
        }
        return maxPos;
    }
    public static int maxIndex(long[] array) {
        long maxVal = Integer.MIN_VALUE;
        int maxPos = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > maxVal) {
                maxVal = array[i];
                maxPos = i;
            }
        }
        return maxPos;
    }
    public static int maxIndex(double[] array) {
        double maxVal = Integer.MIN_VALUE;
        int maxPos = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > maxVal) {
                maxVal = array[i];
                maxPos = i;
            }
        }
        return maxPos;
    }

    /* Sum the elements in an array */

    public static int sum(int[] array) {
        int ret = 0;
        for (int i = 0; i < array.length; i++) ret += array[i];
        return ret;
    }
    public static long sum(long[] array) {
        long ret = 0;
        for (int i = 0; i < array.length; i++) ret += array[i];
        return ret;
    }
    public static double sum(double[] array) {
        double ret = 0.0;
        for (int i = 0; i < array.length; i++) ret += array[i];
        return ret;
    }

    /* Sum the squares of the elements in an array */
    
    public static long sumSquare(int[] array) {
        long ret = 0;
        for (int i = 0; i < array.length; i++) ret += (long)array[i]*(long)array[i]; 
        return ret;
    }
    public static long sumSquare(long[] array) {
        long ret = 0;
        for (int i = 0; i < array.length; i++) ret += (long)array[i]*(long)array[i]; 
        return ret;
    }
    public static double sumSquare(double[] array) {
        double ret = 0;
        for (int i = 0; i < array.length; i++) ret += array[i] * array[i];
        return ret;
    }



    /**
     * Returns an array of array[2].
     * Each sub array is [unique value, number of times it appears].
     *
     * Additionally, the result is sorted by increasing value
     *
     * For example,
     *   distinctValuesCounts([7,2,1,2,2,2,7]) --> [[1,1][2,4][7,2] ]
     */
    public static int[][] distinctValuesCounts(int[] array) {
        Map<Integer, MutableInt> map = new HashMap<Integer, MutableInt>();
        for (int i = 0; i < array.length; i++) {
            int x = array[i];
            MutableInt v = map.get(x);
            if (v == null) {
                v = new MutableInt(0);
                map.put(x, v);
            }
            v.increment();
        }
        List<Integer> values = new ArrayList<Integer>();
        values.addAll(map.keySet());
        Collections.sort(values);

        int[][] ret = new int[values.size()][];
        for (int i = 0; i < values.size(); i++) {
            int v = values.get(i);
            int count = map.get(v).intValue();
            ret[i] = new int[]{v,count};
        }
        return ret;
    }

}
