package com.mathpar.students.OLD.stud2014.popov;

import com.mathpar.matrix.MatrixD;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

/**
 *
 * @author Тёмка
 */
public class Test1 {

    public static int SumVnesh=0;
    public static int SumVnutr=0;

    public  static void Matrix(MatrixD I){
        Element [][] c = I.M;
        int[][] M = new int[c.length][c[0].length];
        for(int i =0; i<M.length; i++){
            for(int j=0; j<M[i].length; j++){
                M[i][j]=c[i][j].intValue();
            }
        }
        int[][] a  = new int[2][2];
        for(int i =0; i<a.length; i++){
            for(int j=0; j<a.length; j++){
            a[i][j]=1;
            PoiskVnesh(a,M);
            a[i][j]=0;
            }
        }
        for(int i =0; i<a.length; i++){
            for(int j=0; j<a.length; j++){
            a[i][j]=1;
            }
        }
        for(int i =0; i<a.length; i++){
            for(int j=0; j<a.length; j++){
            a[i][j]=0;
            PoiskVnutr(a,M);
            a[i][j]=1;
            }
        }
        System.out.println("Всего внешних углов = "+SumVnesh+ "  Всего внутрених углов = " + SumVnutr );
         double AllFigur = (SumVnesh-SumVnutr)/4.0;
        System.out.println("Всего фигур в массиве = " +Math.ceil(AllFigur));

    }
    public static void PoiskVnesh(int a[][],int M[][]) {
        int Sum=0;
            for(int i=0;i<M.length-1; i++){
                for(int j=0; j<M.length-1; j++){
                        if(a[0][0]==M[i][j]&&
                           a[0][1]==M[i][j+1]&&
                           a[1][0]==M[i+1][j]&&
                           a[1][1]==M[i+1][j+1]){
                           Sum++;
                           SumVnesh++;
                        }
                }
            }
            System.out.println("Внешних углов:  "+Sum);
    }
    public static void PoiskVnutr(int a[][],int M[][]) {
        int Sum=0;

            for(int i=0;i<M.length-1; i++){
                for(int j=0; j<M.length-1; j++){
                        if(a[0][0]==M[i][j]&&
                           a[0][1]==M[i][j+1]&&
                           a[1][0]==M[i+1][j]&&
                           a[1][1]==M[i+1][j+1]){
                           Sum++;
                           SumVnutr++;
                        }
                }
            }
            System.out.println("Внутрених углов:  "+Sum);
   }

    public static void main(String[] args) {
        Ring ring = new Ring ("R64[]");

      int[][] M  = new int[20][20];
      M[1][2]=M[2][1]=M[2][2]=M[1][1]=M[4][3]=M[3][4]=M[5][3]=M[4][4]=M[1][6]=
      M[1][7]=M[1][8]=M[1][9]=M[1][10]=M[2][6]=M[2][7]=M[2][8]=M[2][9]=M[2][12]=
      M[2][13]=M[7][1]=M[7][2]=M[7][3]=M[7][4]=M[7][5]=M[7][6]=M[7][7]=M[7][8]=
      M[8][2]=M[8][3]=M[8][4]=M[8][5]=M[8][6]=M[8][7]=M[9][3]=M[9][4]=M[9][5]=
      M[9][6]= M[9][7]=M[9][8]=M[10][2]=M[10][3]=M[10][4]=M[10][5]=M[10][6]=M[10][7]=
      M[10][8]=M[13][13]=M[14][14]=M[14][13]=M[19][19]=1;

      MatrixD I = new MatrixD (M,ring);

              for(int i=0; i<M.length; i++){
                  for(int j=0; j<M.length; j++){
                  System.out.print(" "+M[i][j]);
                  }
                      System.out.println("  ");
             }
        Matrix(I);
    }

}
