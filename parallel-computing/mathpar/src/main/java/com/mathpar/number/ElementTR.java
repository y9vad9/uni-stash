package com.mathpar.number;

/**
 *
 * @author Смирнов Роман
 */
public class ElementTR extends Element{

Element T;
Element R;

    @Override
    public int compareTo(Element x, Ring ring) {
        return T.compareTo(x, ring);
    }

    @Override
    public Boolean isZero(Ring ring) {
        return T.isZero(ring);
    }

    @Override
    public Boolean isOne(Ring ring) {
        return T.isOne(ring);
    }

    @Override
    public boolean equals(Element x, Ring r) {
         return T.equals(x, r);
    }

    public int compareTo(Element o) {
         return T.compareTo(o);
    }





}
