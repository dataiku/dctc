package com.dataiku.dctc.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.AmazonS3OutputStream;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.sns.model.NotFoundException;
import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.file.FileBuilder.Protocol;

public class S3File extends BucketBasedFile {
    public S3File(String path, AmazonS3 s3) {
        path = FileManipulation.trimBegin(path, fileSeparator());
        this.s3 = s3;
        String[] split = FileManipulation.split(path, fileSeparator(), 2);
        this.bucket = split[0];
        this.path = split[1];
    }

    /** Create a new S3File for which we already have the meta (ie, it's guaranteed to be a file) */
    public S3File(S3ObjectSummary sum, AmazonS3 s3) {
        this.s3 = s3;
        this.type = Type.FILE;
        this.objectSummary = sum;
        this.bucket = sum.getBucketName();
        this.path = sum.getKey();
    }

    protected S3File(String directoryPath, AmazonS3 s3, List<S3File> sons) {
        this.s3 = s3;
        this.type = Type.PATH_IN_BUCKET;
        grecursiveList = sons;
        glist = new ArrayList<S3File>();
        for (S3File s: sons) {
            if (FileManipulation.isDirectSon(directoryPath, s.getAbsolutePath(), fileSeparator())) {
                glist.add(s);
            } else {
                String son = FileManipulation.getDirectSon(directoryPath,
                        s.getAbsolutePath(),
                        fileSeparator());
                boolean br = false;
                for (S3File elt: glist) {
                    if (elt.getAbsolutePath().equals(son)) {
                        br = true;
                        break;
                    }
                }
                if (!br) {
                    glist.add(new S3File(son, s3, getChildrenOf(son)));
                }
            }
        }
        directoryPath = FileManipulation.trimBegin(directoryPath, "/");
        String[] split = FileManipulation.split(directoryPath, fileSeparator(), 2);
        this.bucket = split[0];
        this.path = split[1];
    }

    /** Create a new S3File for which we already know it does not exist */
    private static S3File newNotFound(S3File parent, String absolutePathWithBucket) {
        S3File out = new S3File(absolutePathWithBucket, parent.s3);
        out.type = Type.NOT_FOUND;
        return out;
    }

    @Override
    public S3File createInstanceFor(String path) {
        return new S3File(path, s3);
    }
    @Override
    public S3File createSubFile(String path, String separator) {
        /* If this file is resolved, and we already have the list, then maybe we can reuse a storage object / type */
        String subNameWithBucket = FileManipulation.concat(getAbsolutePath(),
                path, fileSeparator(), separator);

        if (type == Type.PATH_IN_BUCKET && recursiveFileList != null) {
            for (S3ObjectSummary sum : recursiveFileList) {
                if ((bucket + "/" + sum.getKey()).equals(subNameWithBucket)) {
                    return new S3File(sum, s3);
                }
            }
            /* Not found --> So we know that it is a not found, create it ! */
            return newNotFound(this, subNameWithBucket);
        }
        return createInstanceFor(subNameWithBucket);
    }

    @Override
    public String getProtocol() {
        return Protocol.S3.getCanonicalName();
    }
    @Override
    public boolean canGetPartialFile() {
        return true;
    }
    @Override
    public List<S3File> glist() throws IOException {
        resolve();
        if (type == Type.NOT_FOUND) {
            throw new NotFoundException(getAbsoluteAddress());
        } else if (glist != null) {
            return glist;
        } else if (type == Type.ROOT) {
            /* Return the list of buckets, already in the list */
            glist = new ArrayList<S3File>();
            for (String f: bucketList) {
                glist.add(new S3File(f, s3));
            }
            return glist;
        } else if (type == Type.PATH_IN_BUCKET) {
            glist = new ArrayList<S3File>();
            for (S3ObjectSummary f: recursiveFileList) {
                if (FileManipulation.isDirectSon(path, f.getKey(), fileSeparator())) {
                    glist.add(new S3File(f, s3));
                } else {
                    String directSon = FileManipulation.getDirectSon(path, f.getKey(), fileSeparator());
                    String son = "/" + bucket + "/" + directSon;
                    boolean br = false;
                    for (S3File file: glist) {
                        if (file.getAbsolutePath().equals(son)) {
                            br = true;
                            break;
                        }
                    }
                    if (!br) {
                        glist.add(new S3File(son, s3, getChildrenOf(son)));
                    }
                }
            }
            return glist;
        } else if (type == Type.FILE) {
            throw new IOException("can't list " + getAbsoluteAddress() + ": is a file");
        }
        throw new Error("not reached");
    }
    @Override
    public List<S3File> grecursiveList() throws IOException {
        resolve();
        if (grecursiveList != null) {
            return grecursiveList;
        }
        grecursiveList = new ArrayList<S3File>();
        if (type == Type.ROOT) {
            for (String bucket: bucketList) {
                S3File l = createInstanceFor(bucket);
                l.resolve();
                for (S3ObjectSummary so: l.recursiveFileList) {
                    grecursiveList.add(new S3File(so, s3));
                }
            }
        }
        else {
            for (S3ObjectSummary so : recursiveFileList) {
                grecursiveList.add(new S3File(so, s3));
            }
        }
        return grecursiveList;
    }
    @Override
    public InputStream getRange(long begin, long length) throws IOException {
        try {
            GetObjectRequest req = new GetObjectRequest(bucket, path);
            req.setRange(begin, begin + length);
            S3Object obj = s3.getObject(req);
            return obj.getObjectContent();
        } catch (AmazonS3Exception access) {
            if (isFileNotFound(access)) {
                throw new FileNotFoundException(getAbsoluteAddress());
            } else {
                throw wrapProperly("getObject", access);
            }
        }
    }
    @Override
    public InputStream inputStream() throws IOException {
        try {
            S3Object obj = s3.getObject(new GetObjectRequest(bucket, path));
            return obj.getObjectContent();
        } catch (AmazonS3Exception access) {
            if (isFileNotFound(access)) {
                throw new FileNotFoundException(getAbsoluteAddress());
            } else {
                throw wrapProperly("getObject", access);
            }
        } catch (AmazonClientException e) {
            if (e.getMessage().startsWith("Unable to unmarshall response")) {
                throw new IOException("Is a directory (Unable to unmarshall s3 response)");
            } else {
                throw e;
            }
        }
    }
    @Override
    public InputStream getLastLines(long lineNumber) throws IOException {
        assert false: "Not Implemented.";
        return null;
    }

    @Override
    public InputStream getLastBytes(long byteNumber) throws IOException {
        return getRange(getSize() - byteNumber ,byteNumber);
    }

    public OutputStream outputStream() throws IOException {
        return new AmazonS3OutputStream(s3, bucket, path, allocate);
    }
    @Override
    public boolean copy(GeneralizedFile input) throws IOException {
        IOUtils.copyLarge(input.inputStream(), outputStream());
        return true;
    }
    @Override
    public boolean directCopy(GeneralizedFile ginput) throws IOException {
        if (!(ginput instanceof S3File)) {
            return false;
        }
        S3File input = (S3File) ginput;
        CopyObjectRequest request = new CopyObjectRequest(input.bucket, input.path, bucket, path);
        s3.copyObject(request);
        return true;
    }
    @Override
    public boolean directMove(GeneralizedFile ginput) throws IOException {
        if (directCopy(ginput)) {
            ginput.delete();
            return true;
        }
        return false;
    }
    @Override
    public boolean copy(InputStream contentStream, long size) throws IOException {
        s3.putObject(bucket, path, contentStream, new ObjectMetadata());
        return true;
    }
    @Override
    public void mkdirs() throws IOException {
        if (!exists()) {
            try {
                s3.createBucket(bucket);
            } catch (AmazonS3Exception e) {
                throw wrapProperly("createBucket", e);
            }
        }
    }
    @Override
    public void mkdir() throws IOException {
        mkdirs();
    }
    @Override
    public void mkpath() throws IOException {
        mkdirs();
    }
    @Override
    public boolean delete() {
        if (FileManipulation.getDepth(getAbsolutePath(), fileSeparator()) == 1) {
            // We have just the bucket.
            try {
                s3.deleteBucket(bucket);
            } catch (AmazonClientException e) {
                return false;
            }
            return true;
        } else {
            s3.deleteObject(bucket, path);
            return true;
        }
    }
    @Override
    public boolean hasHash() {
        return true;
    }
    @Override
    public long maxFileSize() {
        return GlobalConstants.FIVE_TIO;
    }
    @Override
    public String getHash() throws IOException {
        resolve();
        if (type != Type.FILE) {
            throw new IOException("Can't hash " + getAbsoluteAddress() + ": not a file");
        } else {
            if (objectMeta != null) return objectMeta.getETag();
            else if (objectSummary != null) return objectSummary.getETag();
            else throw new Error("No meta and no summary");
        }
    }
    @Override
    public String getHashAlgorithm() {
        return "MD5";
    }
    @Override
    public long getDate() throws IOException {
        resolve();
        if (type != Type.FILE) {
            throw new IOException("Can't get date of " + getAbsoluteAddress() + ": not a file");
        } else {
            if (objectMeta != null) return objectMeta.getLastModified().getTime();
            else if (objectSummary != null) return objectSummary.getLastModified().getTime();
            else throw new Error("No meta and no summary");
        }
    }
    @Override
    public long getSize() throws IOException {
        resolve();
        if (type != Type.FILE) {
            return GlobalConstants.FOUR_KIO;
        } else {
            if (objectMeta != null) return objectMeta.getContentLength();
            else if (objectSummary != null) return objectSummary.getSize();
            else throw new Error("No meta and no summary");
        }
    }
    @Override
    public boolean allocate(long size) {
        if (size < maxFileSize()) {
            this.allocate = size;
            return true;
        }
        return false;
    }
    // Local Methods
    @Override
    public Acl getAcl() throws IOException {
        Acl acl = new Acl();
        if (isDirectory()) {
            acl.setFileType("d");
        }
        else {
            acl.setFileType("-");
        }
        if (exists()) {
            acl.setRead("user", true);
            acl.setWrite("user", true);
            acl.setExec("user", false);
        }
        return acl;
    }

    @Override
    protected void resolve() throws IOException {
        if (type != Type.UNRESOLVED) return;

        if (bucket.length() == 0) {
            type = Type.ROOT;
            List<Bucket> buckets = null;
            try {
                buckets = s3.listBuckets();
            } catch (AmazonS3Exception e) {
                throw wrapProperly("list buckets", e);
            }
            bucketList = new ArrayList<String>();
            for (int i = 0; i < buckets.size(); ++i) {
                Bucket b = buckets.get(i);
                bucketList.add(b.getName());
            }
        } else {
            if (path.length() > 0) {
                // Check if it's a file
                try {
                    objectMeta = s3.getObjectMetadata(bucket, path);
                    type = Type.FILE;
                } catch (Exception e) {
                    // Don't act on exception here, because it could just be a path
                }
            }
            if (type == Type.UNRESOLVED) {
                // Not a file. What about a folder ?

                recursiveFileList = new ArrayList<S3ObjectSummary>();
                try {
                    ObjectListing list = s3.listObjects(new ListObjectsRequest()
                    .withBucketName(bucket).withPrefix(path.length() > 0  ? path : null));
                    while (true) {
                        for (S3ObjectSummary sum : list.getObjectSummaries()) {
                            recursiveFileList.add(sum);
                        }
                        if (list.isTruncated()) {
                            list = s3.listNextBatchOfObjects(list);
                        } else {
                            break;
                        }
                    }
                    if (recursiveFileList.size() == 0) {
                        type = Type.NOT_FOUND;
                        S3File root = new S3File("", s3);
                        for (S3File s3Bucket: root.glist()) {
                            if (s3Bucket.getFileName().equals(bucket)) {
                                type = Type.PATH_IN_BUCKET;
                                break;
                            }
                        }

                    }
                    else {
                        type = Type.PATH_IN_BUCKET;
                    }
                } catch (AmazonS3Exception e) {
                    if (isFileNotFound(e)) {
                        type = Type.NOT_FOUND;
                    } else {
                        type = Type.FAILURE;
                        throw wrapProperly("list bucket " + bucket, e);
                    }
                }
            }
        }
    }

    private boolean isFileNotFound(AmazonS3Exception e) {
        if (e.getStatusCode() == 404
                && (e.getMessage().equals("Not Found") || e.getErrorCode().equals("NoSuchBucket") || 
                        e.getErrorCode().equals("NoSuchKey"))) {
            return true;
        }
        return false;
    }

    private IOException wrapProperly(String failedCall, AmazonS3Exception e) {
        return new IOException("Failed to " + failedCall + ": status=" + e.getStatusCode() +
                " code=" + e.getErrorCode() + " message=" + e.getMessage(),e );
    }
    private IOException wrapProperly(String failedCall, AmazonClientException e) {
        return new IOException("Failed to " + failedCall + ": message=" + e.getMessage());
    }

    /// Public
    public AmazonS3 getAmazonS3() {
        return s3;
    }
    public void put(GeneralizedFile file) throws IOException {
        s3.putObject(bucket,
                path,
                file.inputStream(),
                new ObjectMetadata());
    }

    // Private
    public List<S3File> getChildrenOf(String path) {
        List<S3File> res = new ArrayList<S3File>();
        try {
            grecursiveList();
            if (!isDirectory()) {
                return res;
            }
            if (grecursiveList != null) {
                for (S3File e: grecursiveList) {
                    if (FileManipulation.isSon(path, e.getAbsolutePath(), fileSeparator())) {
                        res.add(e);
                    } else {
                    }
                }
            }
        } catch (IOException e) {
        }
        return res;
    }

    // Static attributes
    private AmazonS3 s3;

    // Resolver output
    ObjectMetadata objectMeta; // For FILE only
    S3ObjectSummary objectSummary; // For FILE only
    List<S3ObjectSummary> recursiveFileList; // For PATH_IN_BUCKET type only
    long allocate;
    List<S3File> glist;
    List<String> bucketList;
    List<S3File> grecursiveList;
}
