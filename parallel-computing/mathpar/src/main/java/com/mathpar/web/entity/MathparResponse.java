package com.mathpar.web.entity;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Entity for Mathpar JSON responses.
 *
 * @author ivan
 */
// TODO: оставить только ответ, убрать файлы и прочее.
public class MathparResponse implements IMathparResponse {
    /**
     * Input task.
     */
    private String task;
    /**
     * Section ID.
     */
    private int sectionId;
    private ResponseStatus status;
    /**
     * Result text.
     */
    private String result;
    /**
     * LaTeX output.
     */
    private String latex;
    /**
     * Warning message.
     */
    private String warning;
    /**
     * Error message.
     */
    private String error;
    /**
     * Stacktrace for unhandled exception.
     */
    private String stacktrace;
    /**
     * Uploaded files listing.
     */
    private String[] filelist;

    public MathparResponse() {
    }

    public MathparResponse(ResponseStatus status) {
        this.status = status;
    }

    public static MathparResponse ok() {
        return new MathparResponse(ResponseStatus.OK);
    }

    public static MathparResponse ok(String result) {
        return ok().result(result);
    }

    public static MathparResponse ok(String result, String latex) {
        return ok(result).latex(latex);
    }

    public static MathparResponse warning(String warningMsg) {
        MathparResponse result = new MathparResponse(ResponseStatus.WARNING);
        result.setWarningMsg(warningMsg);
        return result;
    }

    public static MathparResponse error() {
        return new MathparResponse(ResponseStatus.ERROR);
    }

    public static MathparResponse error(String errorMsg) {
        MathparResponse result = error();
        result.setErrorMsg(errorMsg);
        return result;
    }

    public static MathparResponse error(String errorMsg, Throwable throwable) {
        return error(errorMsg).stacktrace(throwable);
    }

    public MathparResponse result(String result) {
        this.result = result;
        return this;
    }

    public MathparResponse latex(String latex) {
        this.latex = latex;
        return this;
    }

    public MathparResponse stacktrace(Throwable throwable) {
        Writer sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        pw.flush();
        this.stacktrace = sw.toString();
        return this;
    }

    public MathparResponse task(String task) {
        this.task = task;
        return this;
    }

    public MathparResponse sectionId(int sectionId) {
        this.sectionId = sectionId;
        return this;
    }

    public MathparResponse filenames(String[] filenames) {
        this.filelist = filenames;
        return this;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getWarningMsg() {
        return warning;
    }

    public void setWarningMsg(String warningMsg) {
        this.warning = warningMsg;
    }

    public String getErrorMsg() {
        return error;
    }

    public void setErrorMsg(String errorMsg) {
        this.error = errorMsg;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    public String getLatex() {
        return latex;
    }

    public void setLatex(String latex) {
        this.latex = latex;
    }

    public String[] getFilenames() {
        return filelist;
    }

    public void setFilenames(String[] filenames) {
        this.filelist = filenames;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "MathparResponse{" + "task=" + task + ", sectionId=" + sectionId
                + ", status=" + status + ", result=" + result + ", latex=" + latex
                + ", warning=" + warning + ", error=" + error + ", stacktrace=" + stacktrace + '}';
    }
}
