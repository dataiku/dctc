package com.dataiku.dctc.split;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import com.dataiku.dip.datalayer.Column;
import com.dataiku.dip.datalayer.Row;

public class HashFunction implements SplitFunction {
    public HashFunction(int outputSize) {
        this.outputSize = outputSize;
    }
    
    public String split(Row row, Column column) {
        assert(column != null);
        String splitData = row.get(column);

        if (splitData == null) {
            splitData = "__null_value_to_hash__";
        }
        String md5Digest = DigestUtils.md5Hex(splitData);
        if (map.containsKey(md5Digest)) {
            return Integer.toString(map.get(md5Digest));
        } else {
            ct = (ct + 1) % outputSize;
            map.put(md5Digest, ct);
            return Integer.toString(ct);
        }
    }
    private int ct = 0;
    private int outputSize;
    private Map<String, Integer> map = new HashMap<String, Integer>();
}
