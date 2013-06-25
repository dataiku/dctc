package com.dataiku.dctc.dispatch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.dataiku.dctc.file.GeneralizedFile;
import com.dataiku.dctc.file.LocalFile;
import com.dataiku.dip.input.Format;
import com.dataiku.dip.output.CSVOutputFormatter;
import com.dataiku.dip.output.OutputFormatter;

public class TmpSplitStreamFactory extends SplitStreamFactory {
    public TmpSplitStreamFactory(GeneralizedFile dir, String prefix, String postfix, SplitFunction fct,
                                String selectedColumn, Format format, boolean compress) {
        super(dir, prefix, postfix, fct, selectedColumn, format, compress);
        this.map = new HashMap<String, TmpOutput>();
    }

    @Override
    protected Output newStream(String splitIndex) throws IOException {
        GeneralizedFile out = dir.createSubFile(prefix
                + splitIndex + suffix,
                dir.fileSeparator());
        out.mkpath();
        OutputFormatter formatter = new CSVOutputFormatter(',', true, false);
        File tmpFile = File.createTempFile("Foo", "bar.toto");
        tmpFile.deleteOnExit();

        Output output = new Output(new FileOutputStream(tmpFile), formatter);
        TmpOutput tmpOutput = new TmpOutput(output, tmpFile);
        map.put(splitIndex, tmpOutput);

        return tmpOutput.output;
    }
    @Override
    public synchronized void close() {
        // Do the real job here.
        for (Map.Entry<String, TmpOutput> elt : map.entrySet()) {
            LocalFile f = new LocalFile(elt.getValue().localTmp.getAbsolutePath());
            if (f.exists()) {
                try {
                    GeneralizedFile out = dir.createSubFile(prefix + elt.getKey() + suffix, "/");
                    out.copy(f);
                } catch (IOException ex) {
                    System.err.println("dctc TmpSplitStreamFactory: " + ex.getMessage());
                }
                f.delete();
            }
        }
    }

    public static class TmpOutput {
        TmpOutput(Output output, File localTmp) {
            this.output = output;
            this.localTmp = localTmp;
        }
        public Output output;
        public File localTmp;
    }

    // Attributes
    private Map<String, TmpOutput> map;
}
