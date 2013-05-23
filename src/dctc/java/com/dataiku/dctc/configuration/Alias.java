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
    public void resolve(List<String> cmdargs, Map<String, String> unusedAlias) {
        if (cmdargs.size() == 0) {
            return;
        }

        String cmd = cmdargs.get(0);
        if (cmd.startsWith(".")) {
            cmdargs.set(0, cmd.substring(1));
            return;
        }
        if (unusedAlias.containsKey(cmd)) {
            cmdargs.remove(0);
            int idx = 0;
            for (String space: alias.get(cmd).split(" ")) {
                for (String tab: space.split("	")) {
                    cmdargs.add(idx, tab);
                    ++idx;
                }
            }
            unusedAlias.remove(cmd);
            resolve(cmdargs, unusedAlias);
        }
    }
    public String[] resolve(String[] cmdargs) {
        List<String> args = new ArrayList<String>();
        for (String cmdarg: cmdargs) {
            args.add(cmdarg);
        }
        resolve(args, cloneAlias());
        String[] res = new String[args.size()];
        for (int i = 0; i < args.size(); ++i) {
            res[i] = args.get(i);
        }
        return res;
    }
    private Map<String, String> cloneAlias() {
        Map<String, String> res = new HashMap<String, String>();
        res.putAll(alias);
        return res;
    }
    private Map<String, String> alias = new HashMap<String, String>();
}
