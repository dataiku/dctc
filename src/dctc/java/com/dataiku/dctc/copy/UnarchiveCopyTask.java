package com.dataiku.dctc.copy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.archive.ArchiveFactory;
import com.dataiku.dctc.archive.InputArchiveEntry;
import com.dataiku.dctc.archive.InputArchiveIterator;
import com.dataiku.dctc.file.GFile;

public class UnarchiveCopyTask extends CopyTaskRunnable {
    public UnarchiveCopyTask(GFile input, GFile outputDir) throws IOException { // FIXME
        super(input);

        if (!outputDir.isDirectory() && outputDir.exists()) {
            throw new IllegalArgumentException(outputDir.givenName() + "is not a directory.");
        }

        this.out = outputDir;
    }

    @Override
    public void work() throws IOException {
        byte[] buf = new byte[GlobalConstants.ONE_MIO];
        InputArchiveIterator i = ArchiveFactory.buildInput(in).iterator();
        InputArchiveEntry elt = i.next();
        while (elt != null) {
            if (!elt.isDirectory()) {
                GFile o = out.createSubFile(elt.getName(), "/");
                o.mkpath();
                if (o.hasOutputStream()) {
                    InputStream inputStream = elt.getContentStream();
                    if (inputStream == null) {
                        throw new IOException("failed to uncompress" + elt.getName() + ", did not get an input stream");
                    } else {
                        OutputStream outputStream = o.outputStream();
                        int s;
                        while (true) {
                            s = inputStream.read(buf);
                            if (s == -1) {
                                break;
                            }
                            inc(s);
                            outputStream.write(buf, 0, s);
                        }
                        elt.closeEntry();
                        outputStream.close();
                    }
                } else {
                    o.copy(elt.getContentStream(), elt.getSize());
                    inc(elt.getCompressSize());
                }
            } else {
            }
            elt = i.next();
        }
    }

    @Override
    public String print() {
        return in.givenName() + " -* " + out.givenName();
    }

    private GFile out;
}
