package com.dataiku.dip.input.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.lang.NotImplementedException;

import com.dataiku.dip.partitioning.Partition;

/**
 * An EnrichedInputStream that auto detects compression and archiving 
 */
public abstract class AutoEnrichedInputStream implements EnrichedInputStream {
    private long size;
    private String desc;
    private String filename;

    protected abstract InputStream getBasicInputStream() throws IOException;
    protected abstract InputStream getBasicHeadInputStream(long size) throws IOException;

    public AutoEnrichedInputStream(long basicInputStreamSize, String filename, String desc) {
        this.size = basicInputStreamSize;
        this.filename = filename;
        this.desc = desc;
    }

    public boolean repeatable() {
        return false;
    }

    @Override
    public String desc() {
        return desc;
    }

    public long size() {
        return size;
    }

    public Map<String, String> metas() {
        return new HashMap<String, String>();
    }


    public InputStream decompressedStream() throws IOException {
        if (isArchive()) {
            /* Return the first stream in the archive */
            ArchiveInputStream archiveStream = archiveContent();
            ArchiveEntry entry = archiveStream.getNextEntry();
            if (entry == null) return null;
            else return archiveStream;
        } else {
            switch (getCompression()) {
            case GZIP:  return new GZIPInputStream(getBasicInputStream());
            case BZIP2:  return new BZip2CompressorInputStream(getBasicInputStream());
            case NONE: return getBasicInputStream();
            default:
                throw new NotImplementedException();
            }
        }
    }

    @Override
    public InputStream decompressedHeadStream(long targetSize) throws IOException {
        if (isArchive()) {
            /* Return the first file stream in the archive */
            ArchiveInputStream archiveStream = archiveContent();
            while (true) {
                ArchiveEntry entry = archiveStream.getNextEntry();
                if (entry == null) return null;
                if (entry.isDirectory()) continue;
                return archiveStream;
            }
        } else {
            switch (getCompression()) {
            case GZIP:  return new GZIPInputStream(getBasicHeadInputStream(targetSize));
            case BZIP2:  return new BZip2CompressorInputStream(getBasicHeadInputStream(targetSize));
            case NONE: return getBasicHeadInputStream(targetSize);
            default:
                throw new NotImplementedException();
            }
        }
    }


    @Override
    public Partition getPartition() {
        return null;
    }

    @Override
    public Compression getCompression() {
        if (filename.equals(null)) return Compression.NONE;
        if (filename.toLowerCase().endsWith(".gz")) return Compression.GZIP;
        if (filename.toLowerCase().endsWith(".bz2")) return Compression.BZIP2;
        return Compression.NONE;
    }

    @Override
    public boolean isArchive() {
        if (filename.equals(null)) return false;
        return filename.toLowerCase().endsWith(".zip") || filename.toLowerCase().endsWith(".tar") ||
                filename.toLowerCase().endsWith(".tar.gz") || filename.toLowerCase().endsWith(".tar.bz2");
    }

    @Override
    public ArchiveInputStream archiveContent() throws IOException {
        if (filename.toLowerCase().endsWith(".zip")) {
            return new ZipArchiveInputStream(getBasicInputStream());
        } else if (filename.toLowerCase().endsWith(".tar")) {
            return new TarArchiveInputStream(getBasicInputStream());
        }  else if (filename.toLowerCase().endsWith(".tar.gz")) {
            return new TarArchiveInputStream(new GZIPInputStream(getBasicInputStream()));
        } else if (filename.toLowerCase().endsWith(".tar.bz2")) {
            return new TarArchiveInputStream(new BZip2CompressorInputStream(getBasicInputStream()));
        } else {
            throw new IllegalArgumentException("archiveContent for " + filename);
        }
    }

    @Override
    public InputStream rawStream() throws IOException {
        return getBasicInputStream();
    }
    @Override
    public InputStream rawHeadStream(long targetSize) throws IOException {
        return getBasicHeadInputStream(targetSize);
    }
}