package com.mathpar.number;

public class IndexedList {

    public Element[] list;
    public int[] ind;

    public IndexedList() {
    }

    public IndexedList(Element[] list, int[] ind) {
        this.list = list;
        this.ind = ind;
    }

    public IndexedList(Element[] list) {
        this.list = list;
        this.ind = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            ind[i] = 1;
        }
    }

    public String toString(Ring r) {
        String s = "[";
        for (int i = 0; i < list.length - 1; i++) {
            s = s + "(" + ind[i] + ": " + list[i].toString(r) + "), ";
            if (i % 5 == 0) {
                s += "\n";
            }
        }
        s = s + "(" + ind[list.length - 1] + ": " + list[list.length - 1].
                toString(r) + ")]";
        return s;

    }
}
