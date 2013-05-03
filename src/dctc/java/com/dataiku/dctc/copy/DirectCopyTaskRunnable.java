package com.dataiku.dctc.copy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.log4j.Logger;

import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.file.GeneralizedFile;

// This shouldn't be used as is.
public class DirectCopyTaskRunnable extends CopyTaskRunnable {
    DirectCopyTaskRunnable(GeneralizedFile in, GeneralizedFile out, boolean deleteSrc) {
        super(in);

        this.out = out;
        this.deleteSrc = deleteSrc;
    }

    @Override
    public void work() throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Copying " + print());
        }
        if (out.directCopy(in)) {
            inc(in.getSize());
            return;
        }
        if (in.isDirectory()) {
            inc(GlobalConstants.FOUR_KIO);
            return;
        }

        if (out.hasOutputStream()) {
            out.mkpath();

            if (!out.allocate(in.getSize())) {
                throw new IOException("Can't transfer " + in.getAbsoluteAddress() + ": file too big for the destination file system");
            }
            InputStream i = null;
            OutputStream o = null;
            i = in.inputStream();
            o = out.outputStream();
            if (compress()) {
                o = new GZIPOutputStream(o);
            } else if (uncompress()) {
                try {
                    i = new GZIPInputStream(i);
                } catch (IOException e) {
                    if (!in.exists()) {
                        throw e;
                    }
                }
            }

            byte[] b = new byte[GlobalConstants.ONE_MIO];
            int s;
            while (true) {
                s = i.read(b);
                if (s == -1) {
                    break;
                }

                inc(s);
                o.write(b, 0, s);
            }
            i.close();
            o.close();
            if (deleteSrc) {
                in.delete();
            }
        } else {
            readIndirection = true;
            inputStream = new CountingInputStream(in.inputStream());
            out.copy(inputStream, in.getSize());
        }
    }

    @Override
    public long read() {
        if (readIndirection && inputStream != null) {
            return inputStream.getByteCount();
        } else {
            return super.read();
        }
    }
    @Override
    public String print() {
        return in.givenName() + " -> " + out.givenName();
    }
    private boolean compress() {
        return !in.givenName().endsWith(".gz") && out.givenName().endsWith(".gz");
    }
    private boolean uncompress() {
        return in.givenName().endsWith(".gz") && !out.givenName().endsWith(".gz");
    }

    private CountingInputStream inputStream;
    private boolean readIndirection = false;
    private GeneralizedFile out;
    private boolean deleteSrc;
    private static Logger logger = Logger.getLogger("dctc.copy");
}
