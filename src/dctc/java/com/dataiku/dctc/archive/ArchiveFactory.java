package com.dataiku.dctc.archive;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.exception.UserException;
import com.dataiku.dctc.file.GeneralizedFile;

public class ArchiveFactory {
    public static InputArchiveIterable build(InputStream stream, String streamName){
        if (streamName.endsWith("." + GlobalConstants.ZIP)) {
            return new ZipInputArchiveIterable(stream);
        }
        throw new UserException("Unknown archive file extension for '" + streamName +"'. Expected 'zip'");
    }
    public static InputArchiveIterable buildInput(GeneralizedFile file) throws IOException {
        return build(file.inputStream(), file.givenName());
    }

    public static OutputArchiveIterable build(OutputStream stream, String streamName) {
        if (streamName.endsWith("." + GlobalConstants.ZIP)) {
            return new ZipOutputArchiveIterable(stream);
        }
        throw new UserException("Unknown archive file extension for '" + streamName + "'. Expected 'zip'");
    }
    public static OutputArchiveIterable buildOutput(GeneralizedFile file) throws IOException {
        return build(file.outputStream(), file.givenName());
    }
}
