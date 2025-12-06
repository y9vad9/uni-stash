
package com.mathpar.matrix.file.ops;

import java.io.*;
import com.mathpar.matrix.file.dm.MatrixZ;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;

/**
 * <p>Title: ParCA</p>
 *
 * <p>Description: ParCA - parallel computer algebra system</p>
 *
 * <p>Copyright: Copyright (c) ParCA Tambov, 2005,2006,2007,2008</p>
 *
 * <p>Company: ParCA Tambov</p>
 *
 * @author Yuri Valeev
 * @version 0.5
 */
public class MatrixOpsZ extends MatrixOps {
    
    
    public  Object add(Object m1, Object m2, Ring ring){
        return null;
    }
    
    public  Object multCU(Object m1, Object m2, Ring ring){
        return null;
    }
    
    @Override
    public Object readMatrFromFile(File from) throws IOException{
        ObjectInputStream in=new ObjectInputStream(new FileInputStream(from));
        //rows(int),cols(int),M(BI[][])
        int rows = in.readInt();
        int cols = in.readInt();
        NumberZ[][] M = readBIarr2d(in);
        in.close();
        return new MatrixZ(M);
    }

    @Override
    public void writeMatrToFile(Object m, File to) throws IOException{
        ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(to));
        //записать в файл:
        //rows(int),cols(int),M(BI[][])
        MatrixZ matr=(MatrixZ)m;
        out.writeInt(matr.M.length);
        out.writeInt(matr.M[0].length);
        writeBIarr2d(matr.M, out);
        out.close();
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
    public boolean eqMatrs(Object m1, Object m2, long mod, Ring ring){
        return ((MatrixZ)m1).equals((MatrixZ)m2);
    }

    @Override
    public boolean isZERO(Object m, long mod, Ring ring){
        return isZERO((MatrixZ)m);
    }


    @Override
    public Object negate(Object m, long mod,Ring ring){
        return negate((MatrixZ)m);
    }

    @Override
    public Object add(Object m1, Object m2, long mod, Ring ring){
        return ((MatrixZ)m1).add((MatrixZ)m2);
    }

    @Override
    public Object subtract(Object m1, Object m2, long mod, Ring ring){
        return ((MatrixZ)m1).subtract((MatrixZ)m2);
    }

    @Override
    public Object multCU(Object m1, Object m2, long mod, Ring ring){
        return ((MatrixZ)m1).multCU((MatrixZ)m2);
    }

    @Override
    public Object random(int m, int n, RandomParams params, Ring ring){
        return new MatrixZ(m, n, params.den, params.nbits, params.rnd);
    }

    @Override
    public Object zero(int m, int n,Ring ring){
        return new MatrixZ(m, n);
    }

    @Override
    public Object one(int n, Ring ring){
        return MatrixZ.ONE(n);
    }


    @Override
    public Object join(Object[] matrs) {
        //в массиве matrs найдем 1-й k такой, что matrs[k]!=null
        int k=findFirstNotNull(matrs);
        if (k==-1) {
            throw new RuntimeException("All matrixes = null");
        }

        int mk=((MatrixZ)matrs[k]).M.length;
        int nk=((MatrixZ)matrs[k]).M[0].length;
        int m=2*mk;
        int n=2*nk;

        NumberZ[][] res = new NumberZ[m][n];
        NumberZ[][] M;
        if (matrs[0]!=null) {
            M=((MatrixZ)matrs[0]).M;
            for (int i = 0; i < mk; i++){
                for (int j = 0; j < nk; j++) {
                    res[i][j] = M[i][j];
                }
            }
        } else {
            for (int i = 0; i < mk; i++){
                for (int j = 0; j < nk; j++) {
                    res[i][j] = NumberZ.ZERO;
                }
            }
        }
        if (matrs[1]!=null) {
            M=((MatrixZ)matrs[1]).M;
            for (int i = 0; i < mk; i++){
                for (int j = 0; j < nk; j++) {
                    res[i][j+nk] = M[i][j];
                }
            }
        } else {
            for (int i = 0; i < mk; i++){
                for (int j = 0; j < nk; j++) {
                    res[i][j+nk] = NumberZ.ZERO;
                }
            }
        }

        if (matrs[2]!=null) {
            M=((MatrixZ)matrs[2]).M;
            for (int i = 0; i < mk; i++){
                for (int j = 0; j < nk; j++) {
                    res[i+mk][j] = M[i][j];
                }
            }
        } else {
            for (int i = 0; i < mk; i++){
                for (int j = 0; j < nk; j++) {
                    res[i+mk][j] = NumberZ.ZERO;
                }
            }
        }

        if (matrs[3]!=null) {
            M=((MatrixZ)matrs[3]).M;
            for (int i = 0; i < mk; i++){
                for (int j = 0; j < nk; j++) {
                    res[i+mk][j+nk] = M[i][j];
                }
            }
        } else {
            for (int i = 0; i < mk; i++){
                for (int j = 0; j < nk; j++) {
                    res[i+mk][j+nk] = NumberZ.ZERO;
                }
            }
        }

        return new MatrixZ(res);
    }


    private static int findFirstNotNull(Object[] arr){
        for (int i = 0; i < arr.length; i++) {
            if (arr[i]!=null) {
                return i;
            }
        }
        return -1;
    }



    public Object[] split(Object m){
        //MatrixZ[] возвращается как Object[]
        return split((MatrixZ)m);
    }


    /**
     * Процедура разбиения матрицы на 4 подблока.
     * @param m MatrixZ
     * @return MatrixZ[]
     */
    private static MatrixZ[] split(MatrixZ m) {
        MatrixZ[] res = new MatrixZ[4];
        NumberZ[][] M = m.M;
        int n1 = M.length;
        int n2 = M[0].length;

        int len1 = n1 / 2;
        int len2 = n2 / 2;
        int len12 = n1 - len1;
        int len22 = n2 - len2;

        NumberZ[][] bl = new NumberZ[len1][len2];
        for (int i = 0; i < len1; i++)
            for (int j = 0; j < len2; j++)
                bl[i][j] = M[i][j];
        res[0] = new MatrixZ(bl);
        bl = new NumberZ[len1][len22];
        for (int i = 0; i < len1; i++)
            for (int j = 0; j < len22; j++)
                bl[i][j] = M[i][len2+j];
        res[1] = new MatrixZ(bl);
        bl = new NumberZ[len12][len2];
        for (int i = 0; i < len12; i++)
            for (int j = 0; j < len2; j++)
                bl[i][j] = M[len1+i][j];
        res[2] = new MatrixZ(bl);
        bl = new NumberZ[len12][len22];
        for (int i = 0; i < len12; i++)
            for (int j = 0; j < len22; j++)
                bl[i][j] = M[len1+i][len2+j];
        res[3] = new MatrixZ(bl);

        return res;
    }


    /**
     * Сравнение с нулем матрицы.
     * @param matr MatrixZ
     * @return boolean
     */
    public static boolean isZERO(MatrixZ matr){
        NumberZ[][] M=matr.M;
        int m = M.length;
        int n = M[0].length;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (M[i][j].signum() != 0) {
                    return false;
                }
            }
        }
        return true;
   }

   /**
    * Процедура изменения знака всех элементов матрицы на
    * противоположный.
    *
    * @param matr MatrixZ
    * @return MatrixZ
    */
   public static MatrixZ negate(MatrixZ matr){
        NumberZ[][] M = matr.M;
        int m = M.length;
        int n = M[0].length;
        NumberZ[][] res = new NumberZ[m][n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                res[i][j] = (NumberZ)M[i][j].negate();
        return new MatrixZ(res);
    }



    private static void writeBIarr2d(NumberZ[][] arr, ObjectOutputStream out)
        throws IOException{
        int m=arr.length;
        int n=arr[0].length;
        //в out:
        //m(int), n(int), BI[m][n]
        out.writeInt(m);
        out.writeInt(n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                NumberZ b=arr[i][j];
                //b-->out
                out.writeInt(b.signum);
                out.writeObject(b.mag);
            }
        }
    }



    private static NumberZ[][] readBIarr2d(ObjectInputStream in)
        throws IOException{
        try {
            //m(int), n(int), BI[m][n]
            int m = in.readInt();
            int n = in.readInt();

            NumberZ[][] arr = new NumberZ[m][n];

            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    //arr[i][j]=(signum,mag) <-- in
                    int signum = in.readInt();
                    int[] mag = (int[]) in.readObject();
                    arr[i][j] = new NumberZ(mag, signum);
                }
            }
            return arr;
        }
        catch (ClassNotFoundException ex) {
            throw new RuntimeException("Error");
        }
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
