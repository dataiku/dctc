package com.dataiku.dctc.copy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.dataiku.dctc.file.FileManipulation;
import com.dataiku.dctc.file.GeneralizedFile;

public class SyncComputer {
    public static abstract class Filter {
        public abstract boolean accept(GeneralizedFile src, GeneralizedFile dst, String root) throws IOException;
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

        private void createDst(GeneralizedFile src, GeneralizedFile dst) throws IOException {
            if (src.isDirectory() && !dst.isDirectory()) {
                dst.mkdirs();
            }
        }
        private static boolean hashIsDifferent(GeneralizedFile src, GeneralizedFile dst) throws IOException {
            return !(src.hasHash() && dst.hasHash()
                    && src.supportHashAlgorithm(dst.getHashAlgorithm())
                    && src.getHash().equals(dst.getHash()));
        }
        private static boolean dateIsDifferent(GeneralizedFile src, GeneralizedFile dst) throws IOException {
            return !(src.hasDate() && dst.hasDate()
                    && src.getDate() == dst.getDate());
        }

        @Override
        public boolean accept(GeneralizedFile src, GeneralizedFile dstRoot, String destination) throws IOException {
            GeneralizedFile dst = dstRoot.createSubFile(destination, src.fileSeparator());
            if (dst.isDirectory()) {
                return false;
            }

            if (dst.exists()) {
                if (type == Type.EXISTS_ONLY) {
                    return false;
                } else if (type == Type.HASH_BASED) {
                    if (!hashIsDifferent(src, dst)) return false;
                } else if (type == Type.TIME_ONLY) {
                    if (src.hasDate() && dst.hasDate() &&  src.getDate() <= dst.getDate()) {
                        return false;
                    }
                } else if (type == Type.SIZE_ONLY) {
                    if (dst.getSize() == src.getSize()) {
                        return false;
                    }
                } else  if (type == Type.TIME_AND_SIZE){
                    if (dst.getSize() == src.getSize() && !dateIsDifferent(src, dst)) {
                        return false;
                    }
                } else {
                    throw new Error("Unknown incremental type " + type);
                }
                logger.debug("dst already exists but required s1=" + src.getSize() + " ds=" + dst.getSize());
            } else {
                logger.debug("dst " + dst.getAbsoluteAddress() + " does not exist");
            }
            createDst(src, dst);
            return true;
        }
    }

    public SyncComputer(List<GeneralizedFile> sources, GeneralizedFile target) {
        this.sources = sources;
        this.target = target;
    }

    public boolean includeLastPathElementInTarget;
    public boolean recurseInSources = true;
    public boolean compressTargets;
    public boolean uncompressSources;
    public boolean deleteSources;
    public Filter filter;

    private List<GeneralizedFile> sources;
    private GeneralizedFile target;

    public List<CopyTask> computeTasksList() throws IOException {
        checkArgs(sources, target);

        // Compute the recursive target list, so that subsequent existence checks on subfiles work
        if (target.isDirectory()) {
            target.grecursiveList();
        }

        for (GeneralizedFile source: sources) {
            logger.info("Check " + source.getAbsoluteAddress());
            if (!source.exists()) {
                throw new FileNotFoundException(source.getAbsoluteAddress());
            }

            if (source.isDirectory()) {
                if (recurseInSources) {
                    List<? extends GeneralizedFile> subfiles = source.grecursiveList();
                    for (GeneralizedFile subfile: subfiles) {
                        if (subfile.givenName().equals(source.givenName())) {
                            continue;
                        }
                        String dstRoot;
                        if (includeLastPathElementInTarget &&
                                (target.exists() || sources.size() > 1)) {
                            dstRoot = FileManipulation.getSonSubPath(source.givenName(),
                                    subfile.givenName(),
                                    source.fileSeparator());
                        } else {
                            dstRoot = FileManipulation.getSonPath(source.givenName(),
                                    subfile.givenName(),
                                    source.fileSeparator());
                        }
                        addCandidate(subfile, target, dstRoot);
                    }
                }
            } else if (source.isFile()) {
                if (target.isDirectory() || target.givenName().endsWith(target.fileSeparator())) {
                    addCandidate(source, target, source.getFileName());
                } else {
                    addCandidate(source, target, "");
                }
            } else {
                throw new IOException("Source is neither a file nor a directory " + source.getAbsoluteAddress());
            }
        }
        return taskList;
    }


    private boolean checkArgs(List<GeneralizedFile> src, GeneralizedFile dst) throws IOException {
        String prevSrcAddress = null;
        String dstAddress = dst.getAbsoluteAddress();
        Collections.sort(src);
        for (int i = 0; i < src.size(); ++i) {
            String srcAddress = src.get(i).getAbsoluteAddress();

            if (srcAddress.equals(dstAddress)) {
                throw new IOException("`" + srcAddress + "' and `" + dstAddress + "' are the same file.");
            }
            if (i != 0 && prevSrcAddress.equals(srcAddress)) {
                throw new IOException("source file '" + srcAddress + "' specified more than once.");
            }
            prevSrcAddress = srcAddress;
        }
        //       if (dst.exists() && !dst.isDirectory()) {
        //           // src has at least, one element (checked by earlyCheck()).
        //           if (src.length > 1 && !dst.isDirectory() && !archive()) {
        //               error(dstAddress, "is not a directory or the destination is not compressed.", 2);
        //               return true;
        //           }
        //       }
        return false;
    }

    private void addCandidate(GeneralizedFile src, GeneralizedFile dst, String root) throws IOException {
        // Syncing empty folders still causes trouble ...
        if (src.isDirectory()) return;

        if (compressTargets && !src.isDirectory() && !root.endsWith(".gz")) {
            root += ".gz";
        }
        if (uncompressSources && root.endsWith(".gz")) {
            root = root.substring(0, root.length() - 3);
        }

        logger.debug("Checking candidate " + src.getAbsoluteAddress() + " with " + filter);
        if (filter == null || filter.accept(src, dst, root)) {
            logger.debug("Adding task " + src.getAbsoluteAddress());
            taskList.add(new CopyTask(src, dst, root, deleteSources));
        } else {
            logger.debug("Won't add it");
        }
    }

    // Attributes
    private List<CopyTask> taskList = new ArrayList<CopyTask>();
    private static Logger logger = Logger.getLogger("dctc.sync");
}
