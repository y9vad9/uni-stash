package com.mathpar.web.entity;

public class SpaceMemoryResponse implements IMathparResponse {
    private String space;
    private String memory;

    public SpaceMemoryResponse(String space, String memory) {
        this.space = space;
        this.memory = memory;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }
}
