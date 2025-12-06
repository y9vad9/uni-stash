
package com.mathpar.matrix.file.ops;

import com.mathpar.matrix.file.dm.MatrixL;
import java.io.*;

import com.mathpar.number.Element;
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
public class MatrixOpsL extends MatrixOps{

    public  Object add(Object m1, Object m2, Ring ring){
        return null;
    }
    public  Object multCU(Object m1, Object m2, Ring ring){
        return null;
    }
    
    public Object readMatrFromFile(File from) throws IOException{
        ObjectInputStream in=new ObjectInputStream(new FileInputStream(from));
        try {
            //rows(int),cols(int),M(long[][])
            int rows=in.readInt();
            int cols=in.readInt();
            long[][] M = (long[][]) in.readObject();
            in.close();
            return new MatrixL(M);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(String.format("Illegal object in file '%s'", from));
        } catch (ClassCastException ex) {
            throw new RuntimeException(String.format("Illegal object in file '%s'", from));
        }
    }

    public void writeMatrToFile(Object m, File to) throws IOException {

        ObjectOutputStream out = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(to);
            out = new ObjectOutputStream(fos);
        //записать в файл:
            //rows(int),cols(int),M(long[][])
            MatrixL matr = (MatrixL) m;
            out.writeInt(matr.M.length);
            out.writeInt(matr.M[0].length);
            out.writeObject(matr.M);
            fos.flush();

            fos.getChannel().close();
            fos.close();
            out.flush();
            out.close();


        } finally {
            if (out != null) {
                  fos.flush();

            fos.getChannel().close();
            fos.close();
                out.flush();
                out.close();
            }
        }
    }

    @Override
    public int[] getMatrSizeFromFile(File from) throws IOException{
        ObjectInputStream in=new ObjectInputStream(new FileInputStream(from));
        int rows=in.readInt();
        int cols=in.readInt();
        in.close();
        return new int[]{rows,cols};
    }

    @Override
    public boolean eqMatrs(Object m1, Object m2, long mod,Ring ring){
        return ((MatrixL)m1).equals((MatrixL)m2,mod);
    }

    @Override
    public boolean isZERO(Object m, long mod,Ring ring){
        return ((MatrixL)m).isZERO(mod);
    }


    @Override
    public Object negate(Object m, long mod, Ring ring){
        return ((MatrixL)m).negate(mod);
    }

    @Override
    public Object add(Object m1, Object m2, long mod,Ring ring){
        return ((MatrixL)m1).add((MatrixL)m2,mod);
    }

    @Override
    public Object subtract(Object m1, Object m2, long mod,Ring ring){
        return ((MatrixL)m1).subtract((MatrixL)m2,mod);
    }

    @Override
    public Object multCU(Object m1, Object m2, long mod,Ring ring){
        return ((MatrixL)m1).multCU((MatrixL)m2,mod);
    }

    @Override
    public Object random(int m, int n, RandomParams params,Ring ring){
        return new MatrixL(m, n, params.den, params.mod, params.rnd);
    }

    @Override
    public Object zero(int m, int n,Ring ring){
        return new MatrixL(m, n);
    }

    @Override
    public Object one(int n, Ring ring){
        return MatrixL.ONE(n);
    }


    @Override
    public Object join(Object[] matrs) {
        //в массиве matrs найдем 1-й k такой, что matrs[k]!=null
        int k=findFirstNotNull(matrs);
        if (k==-1) {
            throw new RuntimeException("All matrixes = null");
        }

        int mk=((MatrixL)matrs[k]).M.length;
        int nk=((MatrixL)matrs[k]).M[0].length;
        int m=2*mk;
        int n=2*nk;

        long[][] res = new long[m][n];
        long[][] M;
        if (matrs[0]!=null) {
            M=((MatrixL)matrs[0]).M;
            for (int i = 0; i < mk; i++){
                for (int j = 0; j < nk; j++) {
                    res[i][j] = M[i][j];
                }
            }
        }
        if (matrs[1]!=null) {
            M=((MatrixL)matrs[1]).M;
            for (int i = 0; i < mk; i++){
                for (int j = 0; j < nk; j++) {
                    res[i][j+nk] = M[i][j];
                }
            }
        }
        if (matrs[2]!=null) {
            M=((MatrixL)matrs[2]).M;
            for (int i = 0; i < mk; i++){
                for (int j = 0; j < nk; j++) {
                    res[i+mk][j] = M[i][j];
                }
            }
        }
        if (matrs[3]!=null) {
            M=((MatrixL)matrs[3]).M;
            for (int i = 0; i < mk; i++){
                for (int j = 0; j < nk; j++) {
                    res[i+mk][j+nk] = M[i][j];
                }
            }
        }
        return new MatrixL(res);
    }


    private static int findFirstNotNull(Object[] arr){
        for (int i = 0; i < arr.length; i++) {
            if (arr[i]!=null) {
                return i;
            }
        }
        return -1;
    }
    public Object multNum(Object m1, Object mult, long mod, Ring ring){
        return ((MatrixL)m1).multByNumber((Element)mult, mod);
    }


    @Override
    public Object[] split(Object m){
        //MatrixL[] возвращается как Object[]
        return split((MatrixL)m);
    }


    /**
     * Процедура разбиения матрицы на 4 подблока.
     * @param m MatrixL
     * @return MatrixL[]
     */
    private static MatrixL[] split(MatrixL m) {
        MatrixL[] res = new MatrixL[4];
        long[][] M = m.M;
        int n1 = M.length;
        int n2 = M[0].length;

        int len1 = n1 / 2;
        int len2 = n2 / 2;
        int len12 = n1 - len1;
        int len22 = n2 - len2;

        long[][] bl = new long[len1][len2];
        for (int i = 0; i < len1; i++)
            for (int j = 0; j < len2; j++)
                bl[i][j] = M[i][j];
        res[0] = new MatrixL(bl);
        bl = new long[len1][len22];
        for (int i = 0; i < len1; i++)
            for (int j = 0; j < len22; j++)
                bl[i][j] = M[i][len2+j];
        res[1] = new MatrixL(bl);
        bl = new long[len12][len2];
        for (int i = 0; i < len12; i++)
            for (int j = 0; j < len2; j++)
                bl[i][j] = M[len1+i][j];
        res[2] = new MatrixL(bl);
        bl = new long[len12][len22];
        for (int i = 0; i < len12; i++)
            for (int j = 0; j < len22; j++)
                bl[i][j] = M[len1+i][len2+j];
        res[3] = new MatrixL(bl);

        return res;
    }

    @Override
    public Object random(int m, int n, int nbits) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object subtract(Object m1, Object m2, Ring ring) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }



}
