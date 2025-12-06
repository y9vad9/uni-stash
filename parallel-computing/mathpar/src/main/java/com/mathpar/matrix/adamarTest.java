/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.matrix;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import com.mathpar.number.Element;
import com.mathpar.number.NumberZ;
import com.mathpar.number.NumberZp;
import com.mathpar.number.NumberZp32;
import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;

/**
 *
 * @author andy
 */
public class adamarTest {
    public static void main(String[] args) {




        Ring r = new Ring("Z[x,y]");

        NumberZ z=new NumberZ("645765776575765765765757657657657657657657655765675765765765765");
        System.out.println("z="+z.toString(r));
        byte[] na=numberz2bytearr(z);
        System.out.println("count="+na.length);
        NumberZ z2=bytearr2numberz(na);
//        System.out.println("z2="+z2.NUMBER_SCALE);
        System.out.println("z2="+z2.signum);
        System.out.println("z2="+z2.mag.length);

        MatrixS a = new MatrixS(4,4,100,new int[]{2,2,100,8},new Random(),r.numberONE(),r);

        byte[] t=matrix2bytearr(a);
        System.out.println("count="+t.length);
        MatrixS m=bytearr2matrix(t);
        System.out.println("m="+m.toString(r));
//        Polynom det=(Polynom)a.det(r);
//        Element tmp=r.numberZERO;
//        for (int i=0; i<det.coeffs.length; i++){
//            if(!tmp.compareTo(det.coeffs[i], 2, r)) tmp=det.coeffs[i];
//        }
//
        System.out.println("a="+a.toString(r));
//        System.out.println("tmp="+tmp.toString(r));
        a.adamar(r);

        int[][] data=new int[][]{{5,7,11,13,17},{1,2,3,4},{1,2,3}};
        //data=                      l1,       l2,         l3
        int processor=0;
        int[] indexes=indexFor(processor,2, data);
        calcModules(a, data, indexes, 30, r);
    }


    public static int [] indexFor(int proc,int count_proc, int[][] data){
        int g_count=1;
        for (int i=0; i<data.length; i++) g_count=g_count*data[i].length;

        int cfp=g_count/count_proc;
        int rfp=g_count%count_proc;
        System.out.println("cfp="+cfp);
        int step=0;
        if(proc<=rfp){
         step=proc*(cfp+1)-1;
        }else{
            step=(cfp+1)*rfp+(proc-rfp)*cfp;
        }
        int[] indexes=new int[data.length];
        int k=data.length-1;
        for (int i=data.length-1; i>0; i--){
            if(i==data.length-1){
              int temp=step;
              if(proc<=rfp) temp++;
              indexes[i-1]=temp/data[k].length;
              indexes[i]=temp%data[k].length;
            }else{
              indexes[i-1]=indexes[i]/data[k].length;
              indexes[i]=indexes[i]%data[k].length;
            }
            k--;
        }
      //  for (int i=0; i<indexes.length; i++) System.out.println("indexes["+i+"]="+indexes[i]);

        return indexes;
    }


    public static MatrixS[] calcModules(MatrixS m, int[][] data, int[] indexes, int counts, Ring r) {
        MatrixS[] res = new MatrixS[counts];
        for (int k = 0; k < counts; k++) {
            Ring rm = new Ring("Zp32[x,y]");
            rm.setMOD32(data[0][indexes[0]]);
            System.out.println("mod="+data[0][indexes[0]]);
            res[k] = (MatrixS) m.toNewRing(rm.algebra[0], rm);
            NumberZp32[] vars = new NumberZp32[indexes.length - 1];
            for (int h = 0; h < vars.length; h++) {
                vars[h] = new NumberZp32(data[h + 1][indexes[h + 1]]);
                System.out.println("vars(h)=" + vars[h].toString(rm));
            }

            for (int i = 0; i < m.M.length; i++) {
                for (int j = 0; j < m.M[i].length; j++) {
                    res[k].M[i][j] = new Polynom(((Polynom) res[k].M[i][j]).value(vars, rm));
                }
            }
            System.out.println("res[k]=" + res[k].toString(rm));
            nextIndexes(data, indexes);

        }


        return res;
    }

    public static void nextIndexes(int data[][], int[] indexes){
        int k=data.length-1;
        for (int i=data.length-1; i>0; i--){
            if(i==data.length-1){
              indexes[i-1]=indexes[i-1]+(indexes[i]+1)/data[k].length;
              indexes[i]=(indexes[i]+1)%data[k].length;
            }else{
              indexes[i-1]=indexes[i-1]+(indexes[i])/data[k].length;
              indexes[i]=(indexes[i])%data[k].length;
            }
            k--;
        }
      //  for (int i=0; i<indexes.length; i++) System.out.println("indexes["+i+"]="+indexes[i]);
    }


     public static byte[] matrix2bytearr(MatrixS m){
        try{
         ByteArrayOutputStream baos=new ByteArrayOutputStream();
        ObjectOutputStream os=new ObjectOutputStream(baos);
        os.writeObject(m);
        os.close();
        byte[] arr=baos.toByteArray();
        return arr;
        }catch(Exception e){return null;}
    }
     public static MatrixS bytearr2matrix(byte[] arr){
        try{
         ByteArrayInputStream bais=new ByteArrayInputStream(arr);
         ObjectInputStream is=new ObjectInputStream(bais);
         MatrixS m=(MatrixS)is.readObject();
         return m;
        }catch(Exception e){return null;}
    }


      public static byte[] numberz2bytearr(NumberZ z){
        try{
         ByteArrayOutputStream baos=new ByteArrayOutputStream();
        ObjectOutputStream os=new ObjectOutputStream(baos);
        os.writeObject(z);
        os.close();
        byte[] arr=baos.toByteArray();
        return arr;
        }catch(Exception e){return null;}
    }
     public static NumberZ bytearr2numberz(byte[] arr){
        try{
         ByteArrayInputStream bais=new ByteArrayInputStream(arr);
         ObjectInputStream is=new ObjectInputStream(bais);
         NumberZ m=(NumberZ)is.readObject();
         return m;
        }catch(Exception e){return null;}
    }

}
