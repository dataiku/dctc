package com.dataiku.dctc.archive;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.dataiku.dctc.AutoGZip;
import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.exception.UserException;
import com.dataiku.dctc.file.GFile;

public class ArchiveFactory {
    public static InputArchiveIterable build(InputStream stream,
                                             String streamName)
        throws IOException {
        if (streamName.endsWith("." + GlobalConstants.ZIP)) {
            return new ZipInputArchiveIterable(stream);
        }
        else if (streamName.endsWith(".tar")) {
            return new TarInputArchiveIterable(stream);
        }
        else if (streamName.endsWith(".tar.gz2")
                 || streamName.endsWith("tar.gz")) {
            return new TarInputArchiveIterable(AutoGZip.buildInput(streamName
                                                                   , stream));
        }

        throw new UserException("Unknown archive file extension for '"
                                + streamName + "'. Expected 'zip', 'tar',"
                                + " 'tar.gz', or 'tar.gz2'"); // FIXME: Clean this
    }
    public static InputArchiveIterable buildInput(GFile file)
        throws IOException {
        return build(file.inputStream(), file.givenName());
    }
    public static OutputArchiveIterable build(OutputStream stream
                                              , String streamName) {
        if (streamName.endsWith("." + GlobalConstants.ZIP)) {
            return new ZipOutputArchiveIterable(stream);
        }

        throw new UserException("Unknown archive file extension for '"
                                + streamName + "'. Expected 'zip'");
    }
    public static OutputArchiveIterable buildOutput(GFile file)
        throws IOException {
        return build(file.outputStream(), file.givenName());
    }
}
