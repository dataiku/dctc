package com.dataiku.dctc.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

public class HttpFile extends AbstractGFile {

    public HttpFile(String path) {
        this.path = path;
    }
    @Override
    public HttpFile createInstanceFor(String path) {
        return new HttpFile(path);
    }
    private HttpFile create(String path) {
        HttpFile res = new HttpFile(path);
        res.exists = true;

        return res;
    }
    @Override
    public InputStream getLastLines(long lineNumber) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public InputStream getLastBytes(long byteNumber) throws IOException {
        return null;
    }
    @Override
    public InputStream getRange(long begin, long length) throws IOException {
        return null;
    }
    @Override
    public List<HttpFile> glist() throws IOException {
        if (list == null) {
            list = new ArrayList<HttpFile>();
            Document doc = Jsoup.connect(givenName()).get();
            recur(doc.body().childNodes());
        }
        return list;
    }
    private void recur(List<Node> childs) {
        for (Node child: childs) {
            String href = child.attr("href");
            if (!href.isEmpty()) {
                if (!href.startsWith("/")) {
                    if (href.startsWith(givenName())) {
                        list.add(create(href.substring(7)));
                    } else if (!href.startsWith("http://")) {
                        list.add(create(FileManipulation.concat(path, href, "/")));
                    }
                } else {
                    if (!href.startsWith("//")) {
                        if (FileManipulation.getDepth(path, "/") < 1) {
                            list.add(create(FileManipulation.concat(path, href, "/")));
                        }
                    }
                }
            }
            recur(child.childNodes());
        }
    }

    @Override
    public List<HttpFile> grecursiveList() throws IOException {
        if (recursiveList == null) {
            recursiveList = new ArrayList<HttpFile>();
            for (HttpFile son: glist()) {
                if (son.path.endsWith("/") || son.path.endsWith("html")) {
                    for (HttpFile s: son.grecursiveList()) {
                        recursiveList.add(s);
                    }
                } else {
                    recursiveList.add(son);
                }
            }
        }
        recursiveList.add(this);
        return recursiveList;
    }
    @Override
    public void mkpath() throws IOException {
    }

    @Override
    public GeneralizedFile createSubFile(String path, String fileSeparator) {
        throw new NotImplementedException();
    }
    @Override
    public boolean exists() throws IOException {
        if (exists == null) {
            setResponse();
            exists = response.getStatusLine().getStatusCode() == 200;
        }

        return exists;
    }
    @Override
    public boolean isDirectory() throws IOException {
        return exists() && (path.endsWith("/")
                            || path.endsWith("html"));
    }
    @Override
    public boolean isFile() throws IOException {
        return exists();
    }
    @Override
    public String getAbsolutePath() {
        return path;
            }
    @Override
    public String givenName() {
        return givenPath();
    }
    @Override
    public String givenPath() {
        return getProtocol() + "://" + path;

    }
    @Override
    public String getProtocol() {
        return "http";
    }
    @Override
    public void mkdirs() throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public void mkdir() throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public InputStream inputStream() throws IOException {
        setResponse();
        return response.getEntity().getContent();
    }
    @Override
    public OutputStream outputStream() throws IOException {
        throw new NotImplementedException();
    }
    @Override
    public boolean delete() throws IOException {
        return false;
    }
    @Override
    public boolean hasHash() {
        return false;
    }
    @Override
    public long maxFileSize() {
        return Long.MAX_VALUE;
    }
    @Override
    public String getHash() throws IOException {
        return getProtocol();
    }
    @Override
    public long getDate() throws IOException {
        return -1;
    }
    @Override
    public long getSize() throws IOException {
        return 0;
        //setResponse();
        //return response.getEntity().getContentLength();
    }
    @Override
    public boolean hasDate() {
        return false;
    }

    private void setResponse() throws IOException {
        HttpGet request = new HttpGet(givenName());
        HttpClient httpClient = new DefaultHttpClient();
        response = httpClient.execute(request);
    }

    // Attributes
    private String path;
    private Boolean exists;
    private HttpResponse response;
    private List<HttpFile> list;
    private List<HttpFile> recursiveList;
}
