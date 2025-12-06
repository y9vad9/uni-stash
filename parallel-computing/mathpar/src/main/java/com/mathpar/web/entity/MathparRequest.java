package com.mathpar.web.entity;

public class MathparRequest {
    private int sectionId;
    private String task;

    public MathparRequest() {
    }

    public MathparRequest(int sectionId, String task) {
        this.sectionId = sectionId;
        this.task = task;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    @Override
    public String toString() {
        return "MathparRequest{" + "sectionId=" + sectionId + ", task=" + task + '}';
    }
}
