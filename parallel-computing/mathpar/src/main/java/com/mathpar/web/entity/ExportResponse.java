package com.mathpar.web.entity;

public class ExportResponse implements IMathparResponse {
    private String filename;
    private ResponseStatus status;
    private String error;

    public ExportResponse() {
    }

    public ExportResponse(String filename) {
        this.filename = filename;
    }

    public ExportResponse(String filename, String error) {
        this.filename = filename;
        this.status = ResponseStatus.ERROR;
        this.error = error;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
