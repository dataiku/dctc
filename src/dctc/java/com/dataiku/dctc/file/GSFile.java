package com.dataiku.dctc.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.amazonaws.services.sns.model.NotFoundException;
import com.dataiku.dctc.exception.UserException;
import com.dataiku.dctc.GlobalConstants;
import com.dataiku.dctc.file.FileBuilder.Protocol;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.Storage.Builder;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.Buckets;
import com.google.api.services.storage.model.Objects;
import com.google.api.services.storage.model.StorageObject;
import com.google.common.primitives.UnsignedLong;

public class GSFile extends BucketBasedFile {
    // Constructors
    private void __init(String userMail, String keyPath, String path) {
        this.userMail = userMail;
        this.keyPath = keyPath;
        String[] split = FileManipulation.split(path, fileSeparator(), 2);
        this.bucket = split[0];
        this.path = split[1];
    }
    public GSFile(String userMail, String keyPath, String path) {
        __init(userMail, keyPath, path);

        httpTransport = new NetHttpTransport();
        try {
            cred = new GoogleCredential.Builder()
            .setJsonFactory(new JacksonFactory())
            .setTransport(httpTransport)
            .setServiceAccountId(userMail)
            .setServiceAccountScopes(STORAGE_SCOPE)
            .setServiceAccountPrivateKeyFromP12File(new File(keyPath))
            .build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not create credentials for GCS", e);
        }
    }

    private GSFile(GoogleCredential cred, Storage storage, HttpTransport httpTransport,
                String userMail, String keyPath, String path) {
        __init(userMail, keyPath, path);
        this.cred = cred;
        this.storage = storage;
        this.httpTransport = httpTransport;
    }

    /** Internal constructor that takes a fully-resolved file. */
    private GSFile(GSFile parent, StorageObject object) throws IOException {
        __init(parent.userMail, parent.keyPath, parent.bucket + "/" + object.getName());
        this.cred = parent.cred;
        this.storage = parent.storage;
        this.httpTransport = parent.httpTransport;
        this.path = object.getName();
        type = Type.FILE;
        fileStorageObject = object;
        assert (exists());
    }

    private static GSFile newNotFound(GSFile parent, String absolutePath) {
        GSFile ret = new GSFile(parent.cred, parent.storage, parent.httpTransport, parent.userMail, parent.keyPath,
                absolutePath);
        ret.type = Type.NOT_FOUND;
        return ret;
    }
    @Override
    public final List<GSFile> createInstanceFor(List<String> paths) {
        if (paths != null) {
            List<GSFile> res = new ArrayList<GSFile>();
            for (int i = 0; i < paths.size(); ++i) {
                res.add(createInstanceFor(paths.get(i)));
            }
            return res;
        } else {
            return null;
        }
    }
    @Override
    public GSFile createInstanceFor(String path) {
        return new GSFile(cred, storage, httpTransport, userMail, keyPath, path);
    }
    @Override
    public GSFile createSubFile(String path, String separator) throws IOException {
        /* If this file is resolved, and we already have the list, then maybe we can reuse a storage object / type */
        String subName = FileManipulation.concat(getAbsolutePath(),
                path,
                fileSeparator(),
                separator);

        if (type == Type.NOT_FOUND && bucket.length() > 0) {
            /* I am a non-existing path in a bucket, so a child of mine is also a non existing path */
            return newNotFound(this, subName);
        }

        if (type == Type.DIR && recursiveFileList != null) {
            for (StorageObject so : recursiveFileList) {
                if (("/" + bucket + "/" + so.getName()).equals(subName)) {
                    return new GSFile(this, so);
                }
            }
            /* Not found --> So we know that it is a not found, create it ! */
            return newNotFound(this, subName);
        }
        return createInstanceFor(subName);
    }

    // Public
    @Override
    public boolean isEmpty() throws IOException {
        assert isDirectory();
        return false; // By definition, on Google, a directory can only exist if it's not empty
    }
    @Override
    public String getProtocol() {
         return Protocol.GS.getCanonicalName();
    }
    private List<String> list() throws IOException {
        resolve();
        if (type == Type.NOT_FOUND) {
            throw new NotFoundException(getAbsoluteAddress());
        } else if (type == Type.ROOT) {
            /* Return the list of buckets */
            return bucketsList;
        } else if (type == Type.DIR) {
            if (list == null) {
                list = new ArrayList<String>();
                for (StorageObject f: recursiveFileList) {
                    if (FileManipulation.isDirectSon(path, f.getName(), fileSeparator())) {
                        list.add(f.getId());
                    } else {
                        String directSon = bucket + "/" + FileManipulation.getDirectSon(path, f.getName(), fileSeparator());
                        if (!list.contains(directSon)) {
                            list.add(directSon);
                        }
                    }
                }
            }
            return list;
        } else if (type == Type.FILE) {
            throw new IOException("can't list " + getAbsoluteAddress() + ": is a file");
        }
        throw new Error("not reached");
    }
    @Override
    public List<GSFile> glist() throws IOException {
        return createInstanceFor(list());
    }

    @Override
    public List<GSFile> grecursiveList() throws IOException {
        resolve();
        grecursiveList = new ArrayList<GSFile>();
        if (type == Type.ROOT) {
            for (String bucket: bucketsList) {
                GSFile l = createInstanceFor(bucket);
                l.resolve();
                for (StorageObject so: l.recursiveFileList) {
                    grecursiveList.add(new GSFile(this, so));
                }
            }
        }
        else {
            for (StorageObject so : recursiveFileList) {
                grecursiveList.add(new GSFile(this, so));
            }
        }
        return grecursiveList;
    }
    @Override
    public void mkdirs() throws IOException {
        if (type != Type.BUCKET_EXISTS) {
            throw new UserException("Cannot create Google Storage bucket. Please go to https://cloud.google.com/console to create a bucket");
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
    public boolean hasOutputStream() {
        return false;
    }
    @Override
    public InputStream inputStream() throws IOException {
        initRequestFactory();
        HttpRequest request = requestFactory.buildGetRequest(url());
        HttpResponse response = request.execute();
        return response.getContent();
    }
    @Override
    public OutputStream outputStream() throws IOException {
        throw new IllegalArgumentException("GoogleFile doesn't support OutputStream creation.");
    }
    @Override
    public boolean copy(InputStream contentStream, long size) throws IOException {
        InputStreamContent mediaContent = new InputStreamContent("text/plain",
                new BufferedInputStream(contentStream));

        String url = "http://storage.googleapis.com/" + URLEncoder.encode(FileManipulation.concat(bucket, path, fileSeparator()), "UTF-8");

        initRequestFactory();
        HttpRequest req = requestFactory.buildPutRequest(new GenericUrl(url), mediaContent);
        HttpResponse response = req.execute();

        if (!response.isSuccessStatusCode()) {
            throw new IOException("Failed to upload GCS file bucket=" + bucket + " path= "  + path + " with size " + size +
                    " code is " + response.getStatusCode() + " message=" + response.getStatusMessage() +
                    " body=" + IOUtils.toString(response.getContent()));
        }
        return true;
    }
    @Override
    public boolean directCopy(GeneralizedFile ginput) throws IOException {
        return false;
    }
    @Override
    public boolean directMove(GeneralizedFile ginput) throws IOException {
        return false;
    }
    @Override
    public boolean delete() throws IOException {
        initRequestFactory();
        HttpRequest request = requestFactory.buildDeleteRequest(url());
        HttpResponse response = request.execute();
        if (response.getStatusCode() != 200) {
            throw new IOException("Deletion failed for " + getAbsoluteAddress() +
                    ": code=" + response.getStatusCode() + " message=" + response.getStatusMessage());
        }
        return true;
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
            assert(fileStorageObject != null);
            return fileStorageObject.getMd5Hash();
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
            assert(fileStorageObject != null);
            return fileStorageObject.getUpdated().getValue();
        }
    }
    @Override
    public long getSize() throws IOException {
        resolve();
        if (type != Type.FILE) {
            return GlobalConstants.FOUR_KIO; // Directory size
        } else {
            assert(fileStorageObject != null);
            return fileStorageObject.getSize().longValue();
        }
    }

    // Local Methods
    /// Private Methods
    private void initStorage() {
        if (storage == null) {
            JacksonFactory jackson = new JacksonFactory();
            Builder builder = new Storage.Builder(httpTransport, jackson, cred).setApplicationName("dctc");
            storage = builder.build();
        }
    }

    @Override
    protected void resolve() throws IOException {
        if (type != Type.UNRESOLVED) return;

        try {
            logger.debug("Resolving data for " + getAbsoluteAddress());
            initStorage();

            if (bucket.length() == 0) {
                type = Type.ROOT;
                /* List the buckets */
                String projectId = userMail.substring(0, Math.min(userMail.indexOf("@"), userMail.indexOf("-") > 0 ? userMail.indexOf("-") : Integer.MAX_VALUE));
                UnsignedLong ul = UnsignedLong.asUnsigned(Long.parseLong(projectId));
                Buckets bucketsObj = storage.buckets().list(projectId).execute();
                bucketsList = new ArrayList<String>();
                for (Bucket bucket : bucketsObj.getItems()) {
                    bucketsList.add(bucket.getId());
                }
            } else {

                if (path.length() > 0) {
                    /* Check if I am a file */
                    try {
                        /* We cannot use the objects().get(bucket, path) API because it fails: the server
                         * throws an error, and the client FAILS TO PARSE IT.
                         * So, we list ourselves and take the first result if it matches the path
                         */
                        Objects objs = storage.objects().list(bucket).setPrefix(path).execute();
                        if (objs.getItems() != null && objs.getItems().size() > 0
                                && objs.getItems().get(0).getName().equals(path)) {
                            type = Type.FILE;
                            fileStorageObject = objs.getItems().get(0);
                        }
                    } catch (GoogleJsonResponseException e) {
                        // Ignore exceptions here ...
                    }
                }

                if (type == Type.UNRESOLVED) {
                    /* Ok, I'm not a file, check a folder */
                    try {
                        recursiveListFromPathInternal();
                        if (recursiveFileList.size() > 0) {
                            /* In GCS, a path only exists if it has children, by definition */
                            type = Type.DIR;
                        } else if (path.length() == 0) {
                            /* There is an exception : the root of a bucket always exists, if we did not have an error */
                            type = Type.DIR;
                        } else {
                            type = Type.NOT_FOUND;
                        }
                    } catch (FileNotFoundException e) {
                        type = Type.NOT_FOUND;
                    }
                }
                // Too bad
                if (type == Type.UNRESOLVED) {
                    type = Type.NOT_FOUND;
                }
            }
        } catch (IOException e) {
            type = Type.FAILURE;
            throw e;
        }
    }

    private void recursiveListFromPathInternal() throws IOException {
        assert(type != Type.ROOT);
        assert(recursiveFileList == null);
        assert(bucket.length() > 0);
        String pageToken = null;

        recursiveFileList = new ArrayList<StorageObject>();
        try {
            do {
                Objects objects = storage.objects().list(bucket).setPrefix(path).setPageToken(pageToken).execute();
                pageToken = objects.getNextPageToken();
                List<StorageObject> objs = objects.getItems();

                if (objs != null) {
                    for(StorageObject o: objs) {
                        if (FileManipulation.isSon(path, o.getName(), fileSeparator())) {
                            recursiveFileList.add(o);
                        }
                    }
                }
            } while (pageToken != null);
        } catch (GoogleJsonResponseException e) {
            if (e.getStatusCode() == 404) {
                throw new FileNotFoundException(getAbsoluteAddress());
            } else {
                throw new IOException("Google Cloud Storage error code=" + e.getStatusCode() + ": " + e.getMessage(), e);
            }
        }
        // Others exception balloon up
    }

    private void initRequestFactory() throws IOException {
        if (requestFactory == null) {
            requestFactory = httpTransport.createRequestFactory(cred);
        }
    }

    private GenericUrl url() throws UnsupportedEncodingException {
        return new GenericUrl("https://storage.googleapis.com/" + URLEncoder.encode(FileManipulation.concat(bucket, path, "/"), "UTF-8"));
    }
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

    // Attributes
    private String userMail;
    private String keyPath;
    private GoogleCredential cred;
    private Storage storage;

    // Resolved stuff
    private StorageObject fileStorageObject;
    private List<StorageObject> recursiveFileList;
    private List<String> bucketsList;
    private List<GSFile> grecursiveList;
    private List<String> list;

    // Backend stuff
    private HttpTransport httpTransport;
    private HttpRequestFactory requestFactory = null;

    private static final String STORAGE_SCOPE = "https://www.googleapis.com/auth/devstorage.read_write";

    @Override
    public InputStream getLastLines(long lineNumber) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public InputStream getLastBytes(long byteNumber) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public InputStream getRange(long begin, long length) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    private static Logger logger = Logger.getLogger("dctc.file.google");
}
