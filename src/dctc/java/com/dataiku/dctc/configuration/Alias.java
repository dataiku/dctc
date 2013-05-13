package com.dataiku.dctc.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Alias {
    public void setAlias(Map<String, String> settings) {
        for (Entry<String, String> setting: settings.entrySet()) {
            alias.put(setting.getKey(), setting.getValue());
        }
    }
    public String[] resolve(String[] cmdargs) {
        List<String> args = new ArrayList<String>();
        if(cmdargs.length == 0) {
            return cmdargs;
        }

        if (alias.containsKey(cmdargs[0])) {
            for (String space: alias.get(cmdargs[0]).split(" ")) {
                for (String tab: space.split("	")) {
                    args.add(tab);
                }
            }
        }

        for (int i = 1; i < cmdargs.length; ++i) {
            args.add(cmdargs[i]);
        }

        String[] res = new String[args.size()];
        for (int i = 0; i < args.size(); ++i) {
            res[i] = args.get(i);
        }

        return res;
    }
    private Map<String, String> alias = new HashMap<String, String>();
}
