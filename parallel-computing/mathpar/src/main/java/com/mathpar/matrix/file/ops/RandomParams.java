
package com.mathpar.matrix.file.ops;

import java.util.*;

/**
 * <p>Title: ParCA</p>
 *
 * <p>Description: ParCA - parallel computer algebra system</p>
 *
 * <p>Copyright: Copyright (c) ParCA Tambov, 2005,2006,2007</p>
 *
 * <p>Company: ParCA Tambov</p>
 *
 * @author Yuri Valeev
 * @version 0.5
 */
public class RandomParams {
    public int den;
    public double dden;
    public long mod;
    public int nbits;
    public int[] maxpowers;
    public Random rnd;
    public int[] randomType;


    public RandomParams(int den, double dden, long mod, int nbits, int[] maxpowers,
                        Random rnd, int[] randomType){
        this.den=den;
        this.dden=dden;
        this.mod=mod;
        this.nbits=nbits;
        this.maxpowers=maxpowers;
        this.rnd=rnd;
        this.randomType=randomType;
    }

}
