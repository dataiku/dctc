package com.dataiku.dctc.configuration;

import java.util.Arrays;
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
        // unusedAlias variable avoid the loop inside the alias
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
            // Split on spaces or tabulation
            for (String space: alias.get(cmd).split(" ")) {
                for (String tab: space.split("	")) { // <- It's a tabulation
                    cmdargs.add(idx, tab);
                    ++idx;
                }
            }
            unusedAlias.remove(cmd);
            resolve(cmdargs, unusedAlias);
        }
    }
    public String[] resolve(String[] cmdargs) {
        List<String> args = Arrays.asList(cmdargs);

        resolve(args, cloneAlias());

        return args.toArray(new String[0]);
    }
    private Map<String, String> cloneAlias() {
        Map<String, String> res = new HashMap<String, String>();
        res.putAll(alias);
        return res;
    }
    private Map<String, String> alias = new HashMap<String, String>();
}
