package com.dataiku.dctc.file;

import java.io.IOException;
import java.util.List;

/**
 * A small abstraction for file systems that have a notion of buckets
 * and paths within a bucket; Each file object has a "state",
 * depending on whether we have stat() this file or not.
 */
public abstract class BucketBasedFile extends AbstractGFile {
    BucketBasedFile(boolean autoRecur) {
        this.autoRecur = autoRecur;
    }
    /**
     * Computes the type of this file and relevant info:
     * - If type is ROOT, should cache the list of buckets;
     * - If type is PATH_IN_BUCKET, should cache the recursive list
     *     with metadata info;
     * - If type is FILE, should cache the metadata info for this file.
     *
     * Type should not be UNRESOLVED anymore after this method (ie,
     * use finally)
     */
    protected abstract void resolve() throws IOException;

    @Override
    public boolean exists() throws IOException {
        resolve();
        // If failure, it should have already thrown
        assert (type != Type.UNRESOLVED)
            : "(type != Type.UNRESOLVED)";
        assert (type != Type.FAILURE)
            : "(type != Type.FAILURE)";

        return !(type == Type.NOT_FOUND || type == Type.BUCKET_EXISTS);
    }
    @Override
    public boolean isDirectory() throws IOException {
        if (path == null || path.length() == 0) {
            return true;
        }
        resolve();
        // If failure, it should have already thrown
        assert (type != Type.FAILURE)
            : "(type != Type.FAILURE)";

        return type == Type.ROOT || type == Type.DIR;
    }
    @Override
    public boolean isFile() throws IOException {
        resolve();
         // If failure, it should have already thrown
        assert (type != Type.FAILURE)
            : "(type != Type.FAILURE)";

        return type == Type.FILE;
    }
    @Override
    public String givenName() {
        return getProtocol()
            + "://"
            + PathManip.concat(bucket, path, fileSeparator());
    }
    @Override
    public String givenPath() {
        return bucket + "/" + path;
    }
    @Override
    public String getAbsolutePath() {
        return fileSeparator()
            + PathManip.concat(bucket, path, fileSeparator());
    }
    @Override
    public String getAbsoluteAddress() {
        return getProtocol() + ":/" + getAbsolutePath();
    }
    @Override
    public boolean hasAcl() {
        return true;
    }

    public void setAutoRecursion(boolean autoRecursion) {
        // If false, the implementation should limit the listing to the
        // current depth of the directory hierarchy.
        autoRecur = autoRecursion;
    }

    // protected
    protected boolean contains(List<? extends BucketBasedFile> l, String path) {
        for (BucketBasedFile file: l) {
            if (file.path.equals(path)) {
                return true;
            }
        }
        return false;
    }

    protected enum Type {
        BUCKET_EXISTS // Is a valid path, and the bucket exists
        ,DIR
        ,FAILURE
        ,FILE
        ,NOT_FOUND
        ,ROOT
        ,UNRESOLVED
    };

    protected Type type = Type.UNRESOLVED;
    protected String bucket;
    protected String path;
    protected boolean autoRecur = true;
}
