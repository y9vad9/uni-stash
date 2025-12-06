package com.mathpar.web.entity;

public class FileRequest {
    public static final String ACT_DELETE = "delete";

    private String action;
    private String filename;

    public FileRequest() {
    }

    public FileRequest(String filename) {
        this.filename = filename;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "FileRequest{" + "filename=" + filename + '}';
    }
}
