package com.dataiku.dctc.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dip.utils.PrettyString;

public class Alias {
    public void addAlias(Map<String, String> settings) {
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
        if (cmd.startsWith(GlobalConstants.ESCAPE_CHARACTER)) {
            cmdargs.set(0, cmd.substring(1));
            return;
        }
        if (unusedAlias.containsKey(cmd)) {
            cmdargs.remove(0);
            int idx = 0;
            // Split on spaces or tabulation
            for (String s: pat.split(alias.get(cmd))) {
                cmdargs.add(idx, s);
                ++idx;
            }
            unusedAlias.remove(cmd);
            resolve(cmdargs, unusedAlias);
        }
    }
    public String[] resolve(String[] cmdargs) {
        if (!PrettyString.isInteractif()) {
            return cmdargs;
        }
        List<String> args = new ArrayList<String>(Arrays.asList(cmdargs));

        resolve(args, cloneAlias());

        return args.toArray(new String[0]);
    }
    public Map<String, String> getAlias() {
        return alias;
    }

    private Map<String, String> cloneAlias() {
        Map<String, String> res = new HashMap<String, String>();
        res.putAll(alias);

        return res;
    }
    private Map<String, String> alias = new HashMap<String, String>();
    static Pattern pat = Pattern.compile("[ \t]+");
}
