package com.amazonaws.services.s3.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.dataiku.dctc.GlobalConstants;

public class AmazonS3OutputStream extends OutputStream {
    public AmazonS3OutputStream(AmazonS3 s3, String bucket, String path, long allocate) {
        this.bucket = bucket;
        this.path = path;
        this.s3 = s3;
        partNumber = 0;
        multiPartInit = s3.initiateMultipartUpload(new InitiateMultipartUploadRequest(bucket, path));
        buff = new byte[(int) Math.min(Math.max(allocate / 1000, GlobalConstants.FIVE_MIO), GlobalConstants.FIVE_TIO / 1000)];
        buffSize = 0;
        partETag = new ArrayList<PartETag>();
    }
    @Override
    public void write(int arg) throws IOException {
        buff[buffSize] = (byte) arg;
        ++buffSize;
        if (buffSize >= GlobalConstants.FIVE_MIO) {
            send(false);
            buffSize = 0;
        }
    }
    @Override
    public final void close() {
        try {
            send(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void send(boolean isLast) throws IOException {
        try {
            UploadPartRequest part = new UploadPartRequest();
            part.withBucketName(bucket)
                .withKey(path)
                .withInputStream(new ByteArrayInputStream(buff, 0, buffSize))
                .withUploadId(multiPartInit.getUploadId())
                .withPartNumber(++partNumber).withFileOffset(0)
                .withPartSize(buffSize);

            partETag.add(s3.uploadPart(part).getPartETag());
            buffSize = 0;
            if (isLast) {
                CompleteMultipartUploadRequest complete = new CompleteMultipartUploadRequest(bucket, path, multiPartInit.getUploadId(), partETag);
                s3.completeMultipartUpload(complete);
            }
        } catch (Exception e) {
            s3.abortMultipartUpload(new AbortMultipartUploadRequest(bucket, path, multiPartInit.getUploadId()));
            e.printStackTrace();
            throw new IOException();
        }
    }

    private byte[] buff;
    private int buffSize;
    private AmazonS3 s3;
    private String bucket;
    private String path;
    private int partNumber;
    private InitiateMultipartUploadResult multiPartInit;
    private List<PartETag> partETag;
}
