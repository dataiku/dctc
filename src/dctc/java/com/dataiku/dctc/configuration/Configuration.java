package com.dataiku.dctc.configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.dataiku.dctc.exception.UserException;
import com.dataiku.dctc.file.FileManipulation;
import static com.dataiku.dctc.PrettyString.nl;
import com.dataiku.dip.utils.Params;

public class Configuration {
    public void appendConfTo(String file) throws IOException {
        if (conf.size() == 0) {
            return;
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        String eol = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();

        sb.append(eol);
        sb.append("# Added by dctc wizard (" + dateFormat.format(date) + ")." + eol);
        Iterator<?> iterator = conf.entrySet().iterator();
        iterator.hasNext();
        while (true) {
            @SuppressWarnings("unchecked")
            Map.Entry<String, Map<String, String>> e = (Entry<String, Map<String, String>>) iterator.next();
            sb.append("[" + e.getKey() + "]" + eol);
            for (Map.Entry<String, String> confEntry : e.getValue().entrySet()) {
                sb.append(confEntry.getKey() + " = " + confEntry.getValue() + eol);
            }
            if (iterator.hasNext()) {
                sb.append(eol);
            } else {
                break;
            }
        }

       FileWriter fw = new FileWriter(file,true);
       fw.write(sb.toString());
       fw.close();
   }
    public void parse(File file) throws IOException {
        if (!file.exists()) {
            create(file);
        }
        if (!file.exists()) return;

        BufferedReader stream =  new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line;
        Map<String, String> protocol = null;
        try {
            while((line = stream.readLine()) != null) {
                line = line.trim();
                if (!line.startsWith("#") && !line.isEmpty()) {
                    if (line.startsWith("[")) {
                        int closingElt = line.lastIndexOf(']');
                        if (closingElt > 1) {
                            protocol = new HashMap<String, String>();
                            String protocolName = line.substring(1, closingElt);
                            conf.put(protocolName, protocol);
                            nonValidSection.add(protocolName);
                        }
                        else {
                            if (closingElt == -1) {
                                stream.close();
                                throw new UserException("dctc conf: Element beginning by [ must be closed by a ] character.");
                            }
                            else {
                                stream.close();
                                throw new UserException("dctc conf: Protocol element is empty.");
                            }
                        }
                    }
                    else {
                        if (protocol == null) {
                            stream.close();
                            throw new UserException("dctc conf: variables must be defined in a protocol.");
                        }
                        String[] split = FileManipulation.split(line, "=", 2);
                        String key = split[0].trim();
                        String value = split[1].trim();

                        protocol.put(key, value);
                    }
                }

            }
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    public Map<String, Map<String, String>> getSections() {
        return conf;
    }
    public Map<String, String> getOrCreateSection(String section) {
        return getNotNull(section, conf);
    }
    private Map<String, String> getNotNull(String section, Map<String, Map<String, String>> from) {
        Map<String, String> sectionData = from.get(section);
        if (sectionData == null) {
            sectionData = new HashMap<String, String>();
            from.put(section, sectionData);
        }
        return sectionData;
    }
    public void put(String proto, String accountName, String keyName, String keyValue) {
        getNotNull(proto, conf).put(accountName + "." + keyName , keyValue);
    }
    public Params getSectionAsParams(String section) {
        return new Params(getOrCreateSection(section));
    }
    private void create(File f) {
        try {
            System.err.println("First run, creating configuration file");
            File parent = new File(f.getParent());
            parent.mkdirs();
            BufferedWriter w = new BufferedWriter(new FileWriter(f.getAbsoluteFile()));
            w.write(nl("# This file was generated by dctc",
                       "[alias]",
                       "  ls=ls -G",
                       "",
                       "[global]",
                       "  display=auto",
                       "",
                       "# Write here your own configuration"));
            w.close();
        }
        catch (IOException e) {
            System.err.println("dctc configuration: could not auto-create configuration file: "
                               + f.getAbsoluteFile() + "(" + e.getMessage() + ")");
            return;
        }
    }
    public void valid(String section) {
        nonValidSection.remove(section);
    }
    public Set<String> getNonValidSection() {
        return nonValidSection;
    }

    // Attributes
    private Set<String> nonValidSection = new HashSet<String>();
    private Map<String, Map<String, String>> conf = new HashMap<String, Map<String, String>>();
}
