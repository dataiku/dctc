package com.dataiku.dctc.configuration;

import static com.dataiku.dip.utils.PrettyString.pquoted;
import static com.dataiku.dip.utils.PrettyString.scat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.dataiku.dctc.file.FileManipulation;

public class SshConfig {
    public void parse(String path) throws IOException {
        File f = new File(path);
        parse(f);
    }
    public void parse(File file) throws IOException {
        if (!file.exists()) {
            return;
        }
        BufferedReader stream = null;
        try {
            stream =  new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            String currentHost = "*";
            Map<String, String> hostParam = get(currentHost);

            while ((line = stream.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.startsWith("host") && !line.startsWith("hostname")) {
                    currentHost = line.substring(4).trim();
                    hostParam = get(currentHost);
                }
                else {
                    String[/* param/value */] parameter = FileManipulation.split(line, " ", 2,
                                                                                 false);
                    if (parameter[1] == null) {
                        parameter = FileManipulation.split(line, "	", 2, false);
                    }
                    if (parameter[1] == null) {
                        throw new IOException(scat("dctc ssh config:",
                                                   "The parameter",
                                                   pquoted(parameter[0]),
                                                   "doesn't define any value."));
                    }
                    hostParam.put(parameter[0], parameter[1]);
                }
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    // Getters
    public boolean exists(String host) {
        return config.get(host) != null;
    }
    public Map<String, String> getHostParam(String host) {
        return config.get(host.toLowerCase());
    }
    public String get(String host, String key, String defaultValue) {
        Map<String, String> hostParam = getHostParam(host);
        if (hostParam != null) {
            String res = hostParam.get(key.toLowerCase());
            if (res != null) {
                return res;
            }
        }
        return defaultValue;
    }

    // private
    private Map<String, String> get(String host) {
        Map<String, String> sectionValues = config.get(host);
        if (sectionValues == null) {
            sectionValues = new HashMap<String, String>();
            config.put(host, sectionValues);
        }
        return sectionValues;
    }

    // Attributes
    private Map<String, Map<String, String>> config = new HashMap<String, Map<String, String>>();
    //          Host        Param   Value

}
