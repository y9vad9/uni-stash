
package com.mathpar.matrix.file.ops;

import com.mathpar.matrix.file.spec.EMatr;
import com.mathpar.matrix.file.spec.IMatr;
import java.io.*;
import com.mathpar.number.Ring;

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
public abstract class MatrixOps {

    public abstract Object readMatrFromFile(File from) throws IOException;
    public abstract void writeMatrToFile(Object m, File to) throws IOException;
    public abstract int[] getMatrSizeFromFile(File from) throws IOException;

    public abstract boolean eqMatrs(Object m1, Object m2, long mod, Ring ring);
    public abstract boolean isZERO(Object m, long mod, Ring ring);

    public abstract Object negate(Object m, long mod, Ring ring);
    public abstract Object add(Object m1, Object m2, long mod, Ring ring);
    public abstract Object add(Object m1, Object m2, Ring ring);
    public abstract Object multCU(Object m1, Object m2, Ring ring);
    public abstract Object subtract(Object m1, Object m2, long mod, Ring ring);
    public abstract Object subtract(Object m1, Object m2, Ring ring);
    public abstract Object multCU(Object m1, Object m2, long mod, Ring ring);
    

    public  Object multiplyDiv(Object m1, Object m2, Object div, long mod, Ring ring){
        throw new RuntimeException("Not implemented.");
    }
    public  Object multiplyDivMul(Object m1, Object m2, Object div, Object mult, long mod, Ring ring){
        throw new RuntimeException("Not implemented.");
    }
    public Object multEL(Object m1, EMatr m2, long mod, Ring ring){
        throw new RuntimeException("Not implemented.");
    }
    public Object multIL(Object m1, IMatr m2, long mod, Ring ring){
        throw new RuntimeException("Not implemented.");
    }
    public Object EnotTS_min_dI(Object m1, EMatr m2, Object d, long mod, Ring ring){
        throw new RuntimeException("Not implemented.");
    }
    public Object multNum(Object m1, Object mult, long mod, Ring ring){
        throw new RuntimeException("Not implemented.");
    }
    public Object divNum(Object m1, Object div, long mod, Ring ring){
        throw new RuntimeException("Not implemented.");
    }
    public Object multDivNum(Object m1, Object mult, Object div, long mod, Ring ring){
        throw new RuntimeException("Not implemented.");
    }
    public Object divMultNum(Object m1, Object div, Object mult, long mod, Ring ring){
        throw new RuntimeException("Not implemented.");
    }

    public abstract Object random(int m, int n, RandomParams params,Ring ring);
    
    public abstract Object random(int m, int n, int nbits);
    
    public abstract Object zero(int m, int n,Ring ring);
    public abstract Object one(int n,Ring ring);
    public Object oneMultD(int n, Object d,Ring ring){
        throw new RuntimeException("Not implemented.");
    }
    public Object negateNum(Object d,Ring ring){
        throw new RuntimeException("Not implemented.");
    }


    public abstract Object join(Object[] matrs);
    public abstract Object[] split(Object m);
}
