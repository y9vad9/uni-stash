package com.mathpar.web.entity;

public class MathparSection {
    private int sectionId;
    private String task;
    private String answer;
    private String latex;

    public MathparSection() {
    }

    public MathparSection(int sectionId, String task, String answer, String latex) {
        this.sectionId = sectionId;
        this.task = task;
        this.answer = answer;
        this.latex = latex;
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getLatex() {
        return latex;
    }

    public void setLatex(String latex) {
        this.latex = latex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MathparSection that = (MathparSection) o;

        if (sectionId != that.sectionId) return false;
        if (latex != null ? !latex.equals(that.latex) : that.latex != null) return false;
        if (answer != null ? !answer.equals(that.answer) : that.answer != null)
            return false;
        if (task != null ? !task.equals(that.task) : that.task != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result1 = sectionId;
        result1 = 31 * result1 + (task != null ? task.hashCode() : 0);
        result1 = 31 * result1 + (answer != null ? answer.hashCode() : 0);
        result1 = 31 * result1 + (latex != null ? latex.hashCode() : 0);
        return result1;
    }

    @Override
    public String toString() {
        return "MathparSection{" +
                "sectionId=" + sectionId +
                ", task='" + task + '\'' +
                ", answer='" + answer + '\'' +
                ", latex='" + latex + '\'' +
                '}';
    }
}
