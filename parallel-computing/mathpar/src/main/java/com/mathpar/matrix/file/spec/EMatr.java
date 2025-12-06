
package com.mathpar.matrix.file.spec;


/**
 * <p>Title: ParCA</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: ParCA</p>
 *
 * @author Yuri Valeev
 * @version 2.0
 */
public class EMatr
    implements SpecMatr {
    /**
     * Координаты i-й 1: ei[i], ej[i].
     */
    private int[] ei;
    private int[] ej;
    /**
     * Размер матрицы m x n
     */
    private int m,n;


    public EMatr(int[] ei, int[] ej, int m, int n){
        this.ei=ei;
        this.ej=ej;
        this.m=m;
        this.n=n;
        if (ei.length!=ej.length) {
            throw new RuntimeException("ei.length!=ej.length");
        }
        if (ei.length>m || ei.length>n) {
            throw new RuntimeException("ei.length >m || >n");
        }
    }


    public int getRows(){
        return m;
    }

    public int getCols(){
        return n;
    }


    public boolean isNotZero(){
        return ei.length!=0;
    }


    /**
     * (i,j) в подблоке nb -- true или false
     * @param i int
     * @param j int
     * @param nb int
     * @return boolean
     */
    private boolean isInSubBlock(int i,int j, int nb){
        int m1=m/2;
        int n1=n/2;
        switch (nb) {
            case 0:
                return i<m1 && j<n1;
            case 1:
                return i<m1 && j>=n1;
            case 2:
                return i>=m1 && j<n1;
            case 3:
                return i>=m1 && j>=n1;
            default:
                throw new RuntimeException(String.format("Wrong block: %d",nb));
        }
    }


    /**
     * Подблок ненулевой <=> Есть подблок <=> Есть точки в подблоке nb.
     * есть -- true,
     * нет -- false.
     * @param nb int
     * @return boolean
     */
    public boolean hasSubBlock(int nb) {
        if (nb<0 || nb>3) {
            throw new RuntimeException(String.format("Wrong block: %d",nb));
        }
        //0<=nb<=3
        for (int i = 0; i < ei.length; i++) {
            if (isInSubBlock(ei[i],ej[i],nb)) {
                //есть точка в подблоке nb
                return true;
            }
        }
        //нет точек в подблоке nb
        return false;
    }




    /**
     * getSubBlock
     *
     * @param nb int
     * @return SpecMatr
     */
    public SpecMatr getSubBlock(int nb) {
        if (nb<0 || nb>3) {
            throw new RuntimeException(String.format("Wrong block: %d",nb));
        }
        //0<=nb<=3
        int m1=m/2;
        int n1=n/2;
        //длина массива ei(кол-во точек)
        int len=ei.length;
        //Сюда будут записаны точки, входящие в подблок nb
        int[] ei1=new int[len];
        int[] ej1=new int[len];
        //позиция в ei1,ej1 и кол-во записанных, и позиция после последнего
        int k=0;
        //i-- номер точки
        for (int i = 0; i < len; i++) {
            //если i-я точка входит в подблок nb
            if (isInSubBlock(ei[i],ej[i],nb)) {
                //то записать ее в ei1,ej1
                ei1[k]=convRow(ei[i],nb, m1);
                ej1[k]=convCol(ej[i],nb, n1);
                k++;
            }
            //нет, то следующая точка
        }
        //k--кол-во записанных точек
        if (k==0) {
            //не было точек <=> подблок пустой
            return new EMatr(new int[0],new int[0],m1,n1);
        } else if (k==len){
            //вырезать не нужно
            return new EMatr(ei1,ej1,m1,n1);
        } else {
            //вырезать
            int[] eires=new int[k];
            int[] ejres=new int[k];
            System.arraycopy(ei1,0,eires,0,k);
            System.arraycopy(ej1,0,ejres,0,k);
            return new EMatr(eires,ejres,m1,n1);
        }
    }



    /**
     *
     * @param row int
     * @param nb int
     * @return int
     */
    private int convRow(int row, int nb, int m1){
       if (nb<2) {
           //nb==0,1
           return row;
       } else {
           //nb=2,3
           return row-m1;
       }
    }


    /**
     *
     * @param col int
     * @param nb int
     * @param n1 int
     * @return int
     */
    private int convCol(int col, int nb, int n1){
       if (nb==0 || nb==2) {
           //nb==0,2
           return col;
       } else {
           //nb=1,3
           return col-n1;
       }
    }

    /**
     *
     * @return EMatr
     */
    public EMatr transpose(){
        return new EMatr(ej,ei,n,m);
    }



    /**
     * isEMatr
     *
     * @return boolean
     */
    public boolean isEMatr() {
        return true;
    }




    /**
     * getEMArr
     *
     * @return EMArr
     */
    public EMArr getEMArr() {
        return new EMArr(ei,ej);
    }




    /**
     * isIMatr
     *
     * @return boolean
     */
    public boolean isIMatr() {
        return false;
    }




    /**
     * getIMArr
     *
     * @return int[]
     */
    public int[] getIMArr() {
        throw new RuntimeException("Not I-matrix. It is E-matrix.");
    }


}
