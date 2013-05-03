package com.dataiku.dctc.exception;

public class UserException extends RuntimeException {
    public UserException(String msg) {
        this.msg = msg;
    }
    private static final long serialVersionUID = -8128734685445208856L;
    public String getMessage() {
        return msg;
    }

    final private String msg;
}
