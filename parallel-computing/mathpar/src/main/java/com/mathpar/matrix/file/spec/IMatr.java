
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
public class IMatr
    implements SpecMatr {
    /**
     * Координаты i-й 1: ei[i], ei[i].
     */
    private int[] ei;
    /**
     * Размер матрицы n x n
     */
    private int n;


    public IMatr(int[] ei, int n){
        this.ei=ei;
        this.n=n;
        if (ei.length>n) {
            throw new RuntimeException("ei.length >n");
        }
    }

    public int getRows(){
        return n;
    }

    public int getCols(){
        return n;
    }


    public boolean isNotZero(){
        return ei.length!=0;
    }


    /**
     * (i,i) в подблоке nb -- true или false
     * @param i int
     * @param nb int
     * @return boolean
     */
    private boolean isInSubBlock(int i, int nb){
        int n1=n/2;
        switch (nb) {
            case 0:
                return i<n1;
            case 1:
            case 2:
                return false;
            case 3:
                return i>=n1;
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
        if (nb==1 || nb==2) {
            //1-го и 2-го подблоков нет, они=0
            return false;
        }
        //nb==0 или nb==3
        for (int i = 0; i < ei.length; i++) {
            if (isInSubBlock(ei[i],nb)) {
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
        int n1=n/2;
        if (nb==1 || nb==2) {
            //1-го и 2-го подблоков нет, они=0
            return new IMatr(new int[0],n1);
        }
        //nb==0 или nb==3
        //длина массива ei(кол-во точек)
        int len=ei.length;
        //Сюда будут записаны точки, входящие в подблок nb
        int[] ei1=new int[len];
        //позиция в ei1, и кол-во записанных, и позиция после последнего
        int k=0;
        //i-- номер точки
        for (int i = 0; i < len; i++) {
            //если i-я точка входит в подблок nb
            if (isInSubBlock(ei[i],nb)) {
                //то записать ее в ei1
                ei1[k]=convRow(ei[i],nb,n1);
                k++;
            }
            //нет, то следующая точка
        }
        //k--кол-во записанных точек
        if (k==0) {
            //не было точек <=> подблок пустой
            return new IMatr(new int[0],n1);
        } else if (k==len){
            //вырезать не нужно
            return new IMatr(ei1,n1);
        } else {
            //вырезать
            int[] eires=new int[k];
            System.arraycopy(ei1,0,eires,0,k);
            return new IMatr(eires,n1);
        }
    }



    /**
     *
     * @param row int
     * @param nb int
     * @return int
     */
    private int convRow(int row, int nb, int m1){
       if (nb==0) {
           //nb==0
           return row;
       } else {
           //nb=3
           return row-m1;
       }
    }



    /**
     * isEMatr
     *
     * @return boolean
     */
    public boolean isEMatr() {
        return false;
    }




    /**
     * getEMArr
     *
     * @return EMArr
     */
    public EMArr getEMArr() {
        throw new RuntimeException("Not E-matrix. It is I-matrix.");
    }




    /**
     * isIMatr
     *
     * @return boolean
     */
    public boolean isIMatr() {
        return true;
    }




    /**
     * getIMArr
     *
     * @return int[]
     */
    public int[] getIMArr() {
        return ei;
    }


}
