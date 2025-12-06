package com.mathpar.web.entity;

import java.util.ArrayList;
import java.util.List;

public class MathparCheckRequest {
    public long taskId;
    public int subtaskNumber;
    public List<MathparSection> userSolutionSections;

    public MathparCheckRequest() {
    }

     public List<String> getUserAnswer() {
        List<String> res = new ArrayList<>(userSolutionSections.size());
        for (MathparSection s : userSolutionSections) {
            res.add(s.getAnswer());
        }
        return res;
    }   
    
    public List<String> getUserSolutions() {
        List<String> res = new ArrayList<>(userSolutionSections.size());
        for (MathparSection s : userSolutionSections) {
            res.add(s.getTask());
        }
        return res;
    }
}
