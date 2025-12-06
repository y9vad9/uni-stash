/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package  com.mathpar.matrix.file.ops;
import  com.mathpar.matrix.*;
import  com.mathpar.matrix.MatrixD;
import  com.mathpar.matrix.file.spec.EMArr;
import  com.mathpar.matrix.file.spec.EMatr;
import  com.mathpar.matrix.file.spec.IMatr;
import com.mathpar.number.Element;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import  java.io.IOException;
import  java.io.ObjectInputStream;
import  java.io.ObjectOutputStream;
import  java.util.Random;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MatrixOpsD extends MatrixOps {

    public Element elem;
    public Element one;
    public Element zero;


 /*   public MatrixOpsD(Element elem,Ring ring){
        this.elem=elem;
        one=ring.numberONE();
        zero=ring.numberONE().zero(ring);
    }
    
    public MatrixOpsD(){      
        
    }*/

    public MatrixOpsD(Element elem, Ring ring) {
        //To change body of generated methods, choose Tools | Templates.
    }

    public MatrixOpsD() {
        //To change body of generated methods, choose Tools | Templates.
    }



    public Object readMatrFromFile(File from) throws IOException{
        ObjectInputStream in=new ObjectInputStream(new FileInputStream(from));
        try {
            //rows(int),cols(int),matr(MatrixD)
            int rows=in.readInt();
            int cols=in.readInt();
            MatrixD M = (MatrixD) in.readObject();
            in.close();
            return M;
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(String.format("Illegal object in file '%s'", from));
        } catch (ClassCastException ex) {
            throw new RuntimeException(String.format("Illegal object in file '%s'", from));
        }
    }

    public void writeMatrToFile(Object m, File to) throws IOException{
        ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(to));
        //записать в файл:
        //rows(int),cols(int),matr(MatrixD)
        MatrixD matr=(MatrixD)m;
        //rows=cols=кол-во строк в MatrixD
        int rows=matr.M.length;
        int cols=rows;
        out.writeInt(rows);
        out.writeInt(cols);
        out.writeObject(matr);
        out.close();
    }

    public int[] getMatrSizeFromFile(File from) throws IOException{
        ObjectInputStream in=new ObjectInputStream(new FileInputStream(from));
        int rows=in.readInt();
        int cols=in.readInt();
        in.close();
        return new int[]{rows,cols};
    }

    public boolean eqMatrs(Object m1, Object m2, long mod, Ring ring){
        return ((MatrixD)m1).equals((MatrixD)m2,ring);
    }

    public boolean isZERO(Object m, long mod, Ring ring){
        return ((MatrixD)m).isZero(ring);
    }



    public Object negate(Object m, long mod, Ring ring){
        return ((MatrixD)m).negate(ring);
    }

    public Object add(Object m1, Object m2, long mod, Ring ring){
        return ((MatrixD)m1).add((MatrixD)m2,new NumberZ(mod),ring);
    }
    
    public Object add(Object m1, Object m2,  Ring ring){
        return ((MatrixD)m1).add((MatrixD)m2,ring);
    }
    
    public Object subtract(Object m1, Object m2, long mod, Ring ring){
        return ((MatrixD)m1).subtract((MatrixD)m2,ring);
    }
    
    public Object subtract(Object m1, Object m2, Ring ring){
        return ((MatrixD)m1).subtract((MatrixD)m2,ring);
    }
    
    public Object multCU(Object m1, Object m2, long mod, Ring ring){
        return ((MatrixD)m1).multCU((MatrixD)m2,new NumberZ(mod),ring);
    }
    
    public Object multCU(Object m1, Object m2,  Ring ring){
        return ((MatrixD)m1).multiply((MatrixD)m2,ring);
    }
//    public  Object multiplyDiv(Object m1, Object m2, Object div, long mod, Ring ring){
//        return ((MatrixD)m1).multiplyDivRecursive((MatrixD)m2, (Element)div,ring);
//    }
//    public  Object multiplyDivMul(Object m1, Object m2, Object div, Object mult, long mod, Ring ring){
//        return ((MatrixD)m1).multiplyDivMulRecursive((MatrixD)m2, (Element)div, (Element)mult,ring);
//    }

//    public Object multEL(Object m1, EMatr m2, long mod, Ring ring){
//        EMArr arr=m2.getEMArr();
//        return ((MatrixD)m1).multiplyLeftE(arr.ei, arr.ej);
//    }
//
//    public Object multIL(Object m1, IMatr m2, long mod, Ring ring){
//        return ((MatrixD)m1).multiplyLeftI(m2.getIMArr());
//    }
//
//    public Object EnotTS_min_dI(Object m1, EMatr m2, Object d, long mod, Ring ring){
//        EMArr arr=m2.getEMArr();
//        return ((MatrixD)m1).ES_min_dI((Element)d,arr.ej, arr.ei,ring);
//    }

    public Object multNum(Object m1, Object mult, long mod, Ring ring){
        return ((MatrixD)m1).multiplyByScalar((Element)mult,ring);
    }
    public Object divNum(Object m1, Object div, long mod, Ring ring){
         return ((MatrixD)m1).divideByNumber((Element)div,ring);
    }
//    public Object multDivNum(Object m1, Object mult, Object div, long mod, Ring ring){
//        return ((MatrixD)m1).multiplyDivide((Element)mult, (Element)div,ring);
//    }

//    public Object divMultNum(Object m1, Object div, Object mult, long mod, Ring ring){
//        return ((MatrixD)m1).divideMultiply((Element)div, (Element)mult,ring);
//    }


    public Object random(int m, int n, RandomParams params,Ring ring){
        Element[][] rndArr=randomScalarArr2d(m,n,params.den,params.randomType,
                                            params.rnd,ring.numberONE,ring.numberZERO,ring);
        return new MatrixD(rndArr);
    }

    public Object random(int m, int n, int nbits){
        Element[][] rndArr=randomScalarArr2d(m,n,nbits);
        return new MatrixD(rndArr);
    }
    
    public Object zero(int m, int n,Ring ring){
        return MatrixD.zeroMatrix(m);
    }

    public Object one(int n, Ring ring){
        return MatrixD.scalarMatrix(n,one,ring);
    }


//    public Object oneMultD(int n, Object d,Ring ring){
//        return MatrixD.scalarMatrix(n, (Element)d,ring);
//    }

    public Object negateNum(Object d,Ring ring){
        return ((Element)d).negate(ring);
    }


    public MatrixD[] split(MatrixD m){
        //MatrixZ[] возвращается как Object[]
        return ((MatrixD)m).split((MatrixD)m);
    }


    public Object join(Object[] matrs) {
        //создать zero
        int nzI=findFirstNotNull(matrs);
        if (nzI==-1) {
            throw new RuntimeException("All nulls!");
        }
        MatrixD matr=(MatrixD)matrs[nzI];
        int rows=matr.M.length;
        MatrixD zero=MatrixD.zeroMatrix(rows);

        //Object[] --> MatrixD[]
        int n=matrs.length;
        MatrixD[] ms=new MatrixD[n];
        for (int i = 0; i < n; i++) {
            if (matrs[i]!=null) {
                ms[i]=(MatrixD)matrs[i];
            } else {
                ms[i]=zero;
            }
        }
        MatrixD res=MatrixD.join(ms);
        return res;
    }


    private static int findFirstNotNull(Object[] arr){
        for (int i = 0; i < arr.length; i++) {
            if (arr[i]!=null) {
                return i;
            }
        }
        return -1;
    }



    private Element[][] randomScalarArr2d(int r, int c, int density,
                                         int[] randomType,
                                         java.util.Random ran,
                                         Element one,
                                         Element zero,Ring ring) {
        int m1;
        Element[][] M = new Element[r][c];
        if (density == 10000) {
            for (int i = 0; i < r; i++)
                for (int j = 0; j < c; j++)
                    M[i][j] = ring.numberONE.random(randomType, ran, ring);
            return M;
        }
        for (int i = 0; i <= r - 1; i++) {
            for (int j = 0; j <= c - 1; j++) {
                m1 = (Math.round(ran.nextFloat() * 10000) /
                      (10000 - density + 1));
                if (m1 == 0) {
                    M[i][j] = zero;
                } else {
                    M[i][j] = elem.random(randomType, ran, ring);
                }
            }
        }
        return M;
    }
    
    private Element[][] randomScalarArr2d(int r, int c, int nbits) {
        int m1;
        Element[][] M = new Element[r][c];        
        for (int i = 0; i < r; i++)
            for (int j = 0; j < c; j++)
                M[i][j] = new NumberZ(nbits,new Random());
        return M;        
    }

    @Override
    public Object[] split(Object m) {
        if (m instanceof MatrixD) {
           return split((MatrixD) m);
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }



}