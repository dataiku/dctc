package com.dataiku.dctc.dispatch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.dataiku.dctc.file.GFile;
import com.dataiku.dctc.file.LocalFile;
import com.dataiku.dip.input.Format;
import com.dataiku.dip.output.CSVOutputFormatter;
import com.dataiku.dip.output.OutputFormatter;

public class TmpSplitStreamFactory extends SplitStreamFactory {
    public TmpSplitStreamFactory(GFile dir
                                 , String prefix
                                 , String postfix
                                 , SplitFunction fct
                                 , String selectedColumn
                                 , Format format
                                 , boolean compress) {
        super(dir, prefix, postfix, fct, selectedColumn, format, compress);
        this.map = new HashMap<String, TmpOutput>();
    }

    @Override
    protected Output newStream(String splitIndex) throws IOException {
        GFile out = dir.createSubFile(prefix
                                      + splitIndex
                                      + suffix
                                      , dir.fileSeparator());
        out.mkpath();
        Format inputFormat = new Format("csv");
        inputFormat.addParam("separator", ",");
        inputFormat.addParam("parseHeaderRow", "true");
        OutputFormatter formatter = new CSVOutputFormatter(inputFormat);
        File tmpFile = File.createTempFile("Foo", "bar.toto");
        tmpFile.deleteOnExit();

        Output output = new Output(new FileOutputStream(tmpFile
                                                        ), formatter);
        TmpOutput tmpOutput
            = new TmpOutput().withOutput(output).withLocalTmp(tmpFile);
        map.put(splitIndex, tmpOutput);

        return tmpOutput.output;
    }
    @Override
    public synchronized void close() {
        // Do the real job here.
        for (Map.Entry<String, TmpOutput> elt : map.entrySet()) {
            LocalFile f
                = new LocalFile(elt.getValue().localTmp.getAbsolutePath());
            if (f.exists()) {
                try {
                    GFile out = dir.createSubFile(prefix
                                                  + elt.getKey()
                                                  + suffix, "/");
                    out.copy(f);
                } catch (IOException ex) {
                    // FIXME: Should use YellPolicy
                    System.err.println("dctc TmpSplitStreamFactory: "
                                       + ex.getMessage());
                }
                f.delete();
            }
        }
    }

    public static class TmpOutput {
        public Output getOutput() {
            return output;
        }
        public void setOutput(Output output) {
            this.output = output;
        }
        public TmpOutput withOutput(Output output) {
            setOutput(output);
            return this;
        }

        public File getLocalTmp() {
            return localTmp;
        }
        public void setLocalTmp(File localTmp) {
            this.localTmp = localTmp;
        }
        public TmpOutput withLocalTmp(File localTmp) {
            setLocalTmp(localTmp);
            return this;
        }

        // Attributes
        private File localTmp;
        private Output output;
    }

    // Attributes
    private Map<String, TmpOutput> map;
}
