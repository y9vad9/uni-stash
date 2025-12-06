package com.mathpar.web.executor;

public class MathparResult {
    public final String result;
    public final String latex;

    public MathparResult(String result, String latex) {
        this.result = result;
        this.latex = latex;
    }

    @Override
    public String toString() {
        return "MathparResult{" + "result=" + result + '}';
    }
}
