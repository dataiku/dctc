package com.dataiku.dctc.copy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.dataiku.dctc.file.PathManip;
import com.dataiku.dctc.file.GFile;

public class SyncComputer {
    public static abstract class Filter {
        public abstract boolean accept(GFile src, GFile dst,
                                       String root) throws IOException;
    }

    public static class IncrementalFilter extends Filter {
        public static enum Type {
            EXISTS_ONLY,
            HASH_BASED,
            TIME_ONLY,
            SIZE_ONLY,
            TIME_AND_SIZE
        }
        public Type type = Type.SIZE_ONLY;

        private void createDst(GFile src, GFile dst) throws IOException {
            if (src.isDirectory() && !(dst.exists() && dst.isFile())) {
                dst.mkdirs();
            }
        }
        private static boolean hashIsDifferent(GFile src,
                                               GFile dst) throws IOException {
            return !(src.hasHash() && dst.hasHash()
                    && src.supportHashAlgorithm(dst.getHashAlgorithm())
                    && src.getHash().equals(dst.getHash()));
        }
        private static boolean dateIsDifferent(GFile src,
                                               GFile dst) throws IOException {
            return !(src.hasDate() && dst.hasDate()
                    && src.getDate() == dst.getDate());
        }

        @Override
        public boolean accept(GFile src, GFile dstRoot,
                              String destination) throws IOException {
            GFile dst = dstRoot.createSubFile(destination, src.fileSeparator());
            logger.debug("accept " + src.getAbsolutePath() + " to " + dstRoot.getAbsolutePath() + " " + destination + " dst.isDir = " + dst.isDirectory() + " dst.exists" + dst.exists());
            if (dst.isDirectory()) {
                return false;
            }
            
            

            if (dst.exists()) {
                switch (type) {
                case EXISTS_ONLY:
                    return  false;
                case HASH_BASED:
                    if (!hashIsDifferent(src, dst)) {
                        return false;
                    }
                    break;
                case TIME_ONLY:
                    if (src.hasDate()
                        && dst.hasDate()
                        &&  src.getDate() <= dst.getDate()) {
                        return false;
                    }
                    break;
                case SIZE_ONLY:
                    if (dst.getSize() == src.getSize()) {
                        return false;
                    }
                    break;
                case TIME_AND_SIZE:
                    if (dst.getSize() == src.getSize()
                        && !dateIsDifferent(src, dst)) {
                        return false;
                    }
                    break;
                default:
                    throw new Error("Shouldn't append.");
                }
                logger.debug("dst already exists but required s1="
                             + src.getSize() + " ds=" + dst.getSize());
            } else {
                logger.debug("dst "
                             + dst.getAbsoluteAddress()
                             + " does not exist");
            }
            createDst(src, dst);
            return true;
        }
    }

    public SyncComputer(List<GFile> sources, GFile target) {
        this.sources = sources;
        this.target = target;
    }

    public boolean includeLastPathElementInTarget;
    public boolean recurseInSources = true;
    public boolean compressTargets;
    public boolean uncompressSources;
    public boolean deleteSources;
    public Filter filter;

    private List<GFile> sources;
    private GFile target;
    
    public List<GFile> getResolvedSources() {
        assert(sources != null);
        return sources;
    }

    public List<CopyTask> computeTasksList() throws IOException {
        checkArgs(sources, target);

        // Compute the recursive target list, so that subsequent
        // existence checks on subfiles work
        if (target.isDirectory()) {
            target.grecursiveList();
        }

        for (GFile source: sources) {
            logger.info("Check " + source.getAbsoluteAddress());
            if (!source.exists()) {
                throw new FileNotFoundException(source.getAbsoluteAddress());
            }

            if (source.isDirectory()) {
                if (recurseInSources) {
                    List<? extends GFile> subfiles = source.grecursiveList();
                    for (GFile subfile: subfiles) {
                        if (subfile.givenName().equals(source.givenName())) {
                            continue;
                        }
                        String dstRoot
                            = PathManip.getSonPath(source.givenName()
                                                   , subfile.givenName()
                                                   , source.fileSeparator());
                        if (includeLastPathElementInTarget &&
                            (target.exists() || sources.size() > 1)) {
                            dstRoot
                                = PathManip.concat(source.getFileName()
                                                   , dstRoot
                                                   , source.fileSeparator());
                        }
                        addCandidate(subfile, target, dstRoot);
                    }
                }
            } else if (source.isFile()) {
                if (target.isDirectory()
                    || target.givenName().endsWith(target.fileSeparator())) {
                    addCandidate(source, target, source.getFileName());
                } else {
                    addCandidate(source, target, "");
                }
            } else {
                throw new
                    IOException("Source is neither a file nor a directory "
                                + source.getAbsoluteAddress()); // FIXME: quote
            }
        }
        return taskList;
    }


    private boolean checkArgs(List<GFile> src, GFile dst) throws IOException {
        String prevSrcAddress = null;
        String dstAddress = dst.getAbsoluteAddress();
        Collections.sort(src);
        for (int i = 0; i < src.size(); ++i) {
            String srcAddress = src.get(i).getAbsoluteAddress();

            if (srcAddress.equals(dstAddress)) {
                throw new IOException("`"
                                      + srcAddress
                                      + "' and `"
                                      + dstAddress
                                      + "' are the same file.");
            }
            if (i != 0 && prevSrcAddress.equals(srcAddress)) {
                throw new IOException("source file '"
                                      + srcAddress
                                      + "' specified more than once.");
            }
            prevSrcAddress = srcAddress;
        }
        return false;
    }

    private void addCandidate(GFile src
                              , GFile dst
                              , String root) throws IOException {
        // Syncing empty folders still causes trouble ...
        // FIXME: False.
        if (src.isDirectory()) {
            return;
            // FIXME: Should be added as for a file. The copy will
            // create the path, then the directory himself. This bug
            // was *DELETED* 2-3 months ago, before this
            // implementation.
        }

        if (compressTargets && !src.isDirectory() && !root.endsWith(".gz")) {
            root += ".gz";
        }
        if (uncompressSources && root.endsWith(".gz")) {
            root = root.substring(0, root.length() - 3);
        }

        logger.debug("Checking candidate "
                     + src.getAbsoluteAddress()
                     + " with " + filter); // FIXME: No logger.
        if (filter == null
            || filter.accept(src, dst, root)) {
            logger.debug("Adding task " + src.getAbsoluteAddress());
            taskList.add(new CopyTask(src, dst, root, deleteSources));
        } else {
            logger.debug("Won't add it");
        }
    }

    // Attributes
    private List<CopyTask> taskList = new ArrayList<CopyTask>();
    private static Logger logger = Logger.getLogger("dctc.sync");
    // FIXME: Delete all loggers
}
