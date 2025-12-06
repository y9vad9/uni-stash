/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.students.OLD.stud2014.storogev;

/**
 *
 * @author dryouch
 */
public class Main {
    public static double det2(double[][] x){
        double result=x[0][0]*x[1][1]-x[1][0]*x[0][1];
        return result;
    }
    public static void first(ETD x,double [][] y){

        double E=1;
        double det=0;
        if(Math.abs(det2(y))!=0) {
            E=det2(y);
            det=1/det2(y);
        }
        x.D[0][0]=1/y[0][0];
        x.D[1][1]=det/(y[0][0]);
        x.D[1][0]=0;
        x.D[0][1]=0;

        x.L[0][0]=y[0][0];
        x.L[1][0]=y[1][0];
        x.L[0][1]=0;
        x.L[1][1]=E;

        x.U[0][0]=y[0][0];
        x.U[0][1]=y[0][1];
        x.U[1][0]=0;
        x.U[1][1]=E;

        x.P[0][0]=1;
        x.P[1][1]=1;

        x.Q[0][0]=1;
        x.Q[1][1]=1;
    }

    public static void second(ETD x,double [][] y){
        double E=1;
        double det=0;
        if(Math.abs(det2(y))!=0) {
            E=det2(y);
            det=det2(y);
        }
        x.D[0][0]=1/y[0][1];
        x.D[1][1]=-det/(y[0][1]);
        x.D[1][0]=0;
        x.D[0][1]=0;

        x.L[0][0]=y[0][1];
        x.L[1][0]=y[1][1];

        x.L[0][1]=0;
        x.L[1][1]=E;

        x.U[0][0]=y[0][1];
        x.U[0][1]=0;
        x.U[1][0]=0;
        x.U[1][1]=E;

        x.P[0][0]=1;
        x.P[1][1]=1;

        x.Q[0][1]=1;
        x.Q[1][0]=1;
    }

    public static void third(ETD x,double [][] y){
        double E=1;
        double det=0;
        if(Math.abs(det2(y))!=0) {
            E=det2(y);
            det=det2(y);
        }
        x.D[0][0]=1/y[1][0];
        x.D[1][1]=-det/(y[1][0]);
        x.D[1][0]=0;
        x.D[0][1]=0;


        x.L[0][0]=y[1][0];
        x.L[1][0]=0;
        x.L[0][1]=0;
        x.L[1][1]=E;

        x.U[0][0]=y[1][0];
        x.U[0][1]=y[1][1];
        x.U[1][0]=0;
        x.U[1][1]=E;

        x.P[0][1]=1;
        x.P[1][0]=1;

        x.Q[0][0]=1;
        x.Q[1][1]=1;
    }

    public static void forth(ETD x,double [][] y){
        double E=1;
        double det=0;
        if(Math.abs(det2(y))!=0) {
            E=det2(y);
            det=det2(y);
        }
        x.D[0][0]=1/y[1][1];
        x.D[1][1]=0;
        x.D[1][0]=0;
        x.D[0][1]=0;


        x.L[0][0]=y[1][1];
        x.L[1][0]=0;
        x.L[0][1]=0;
        x.L[1][1]=1;

        x.U[0][0]=y[1][0];
        x.U[0][1]=0;
        x.U[1][0]=0;
        x.U[1][1]=1;

        x.P[0][1]=1;
        x.P[1][0]=1;

        x.Q[0][1]=1;
        x.Q[1][0]=1;
    }

    public static void oneXtwo(ETD x,double [][] y){
        x.D=new double[1][2];
        x.L=new double[1][1];
        x.U=new double[2][2];
        x.P=new double[1][1];
        x.Q=new double[2][2];

        if(y[0][0]!=0){x.D[0][0]=1/y[0][0];
        x.D[0][1]=0;

        x.L[0][0]=y[0][0];

        x.U[0][0]=y[0][0];
        x.U[0][1]=y[0][1];
        x.U[1][0]=0;
        x.U[1][1]=1;
        x.Q[0][0]=1;
        x.Q[0][1]=0;
        x.Q[1][0]=0;
        x.Q[1][1]=1;
        x.P[0][0]=1;
        }else{
          x.L[0][0]=y[0][1];
          x.D[0][0]=1/y[0][1];
          x.D[0][0]=0;
          x.U[0][0]=y[0][1];
          x.U[1][1]=1;
          x.Q[0][0]=0;
          x.Q[0][1]=1;
          x.Q[1][0]=1;
          x.Q[1][1]=0;

        }


    }

    public static void twoXone(ETD x,double [][] y){
        x.D=new double[2][1];
        x.U=new double[1][1];
        x.L=new double[2][2];
        x.P=E(2);
        x.Q=E(1);
        if(y[0][0]!=0){
            x.D[0][0]=1/y[0][0];
            x.D[1][0]=0;

            x.U[0][0]=y[0][0];

            x.L[0][0]=y[0][0];
            x.L[0][1]=0;
            x.L[1][0]=y[1][0];
            x.L[1][1]=1;}else{

            x.L[0][0]=y[1][0];
            x.L[1][1]=1;
            x.D[0][0]=1/y[1][0];
            x.D[0][0]=0;
            x.U[0][0]=y[0][1];
            x.P[0][0]=0;
            x.P[0][1]=1;
            x.P[1][0]=1;
            x.P[1][1]=0;
        }
    }

    public static void isNu(ETD x,double [][] y){
        x.D[0][0]=0;
        x.D[1][1]=0;
        x.D[1][0]=0;
        x.D[0][1]=0;

        x.L[0][0]=1;
        x.L[1][0]=0;
        x.L[0][1]=0;
        x.L[1][1]=1;

        x.U[0][0]=1;
        x.U[0][1]=0;
        x.U[1][0]=0;
        x.U[1][1]=1;

        x.P[0][1]=1;
        x.P[1][0]=1;

        x.Q[0][1]=1;
        x.Q[1][0]=1;
    }

    public static void isNole(ETD x,double [][] y){
        x.D[0][0]=0;

        x.L[0][0]=1;

        x.U[0][0]=1;

        x.P[0][0]=1;

        x.Q[0][0]=1;
    }

    public static boolean isnull(double[][] x){
        boolean fl=true;
        for(int i=0;i<x.length;i++){
            for(int j=0;j<x[0].length;j++){
                if(x[i][j]!=0){
                    fl=false;
                    break;
                }
            }
            if(!fl){
                break;
            }
        }
        return fl;
    }

    public static void isOne(ETD x,double [][] y){
        x.D[0][0]=1/y[0][0];

        x.L[0][0]=y[0][0];

        x.U[0][0]=y[0][0];

        x.P[0][0]=1;

        x.Q[0][0]=1;
    }

    public static double[][] transpose(double[][] x){
        double[][] y=new double[x.length][x.length];
        for(int i=0;i<x.length;i++){
            for(int j=0;j<x.length;j++){
                y[i][j]=x[j][i];
            }
        }
        return y;
    }

    public static double[][] inverseL(double[][]x){
        double[][] y=new double[x.length][x.length];
        for(int i=0;i<x.length;i++) y[i][i]=1/x[i][i];
        for(int p=1;p<x.length;p++){
            for(int j=0;j<x.length-p;j++){
                int i=j+p;
                System.out.println("");
                for(int q=0;q<x.length;q++){
                    if(q!=i) y[i][j]+= x[i][q]*y[q][j];
                }
                 y[i][j]=-y[i][j]/x[i][i];
            }
        }
       return y;
    }

    public static double[][] inverseU(double[][]x){
        double[][] y=new double[x.length][x.length];
        for(int i=0;i<x.length;i++) y[i][i]=1/x[i][i];
        for(int p=1;p<x.length;p++){
            for(int i=0;i<x.length-p;i++){
                int j=i+p;
                System.out.println("");
                for(int q=0;q<x.length;q++){
                    if(q!=i) y[i][j]+= x[i][q]*y[q][j];
                }
                 y[i][j]=-y[i][j]/x[i][i];
            }
        }
       return y;
    }

    public static double[][] inverseD(double[][]x){
        double[][] result=new double[x.length][x.length];
        for(int i=0;i<x.length;i++) result[i][i]=1/x[i][i];
        return result;
    }

    public static double[][] multy(double[][] x,double[][] y){
        double[][] result=new double[x.length][y[0].length];
        for(int i=0;i<x.length;i++){
            for(int j=0;j<y[0].length;j++){
                for(int k=0;k<x[0].length;k++) result[i][j]+=x[i][k]*y[k][j];
            }
        }
        return result;
    }

    public static double[][] subs(double[][] x,double[][] y){
        double[][] result=new double[x.length][y.length];
        for(int i=0;i<x.length;i++){
            for(int j=0;j<x.length;j++){
                result[i][j]=x[i][j]-y[i][j];
            }
        }
        return result;
    }

    public static double[][] add(double[][] x,double[][] y){
        double[][] result=new double[x.length][x[0].length];
        for(int i=0;i<x.length;i++){
            for(int j=0;j<x[0].length;j++){
                result[i][j]=x[i][j]+y[i][j];
            }
        }
        return result;
    }

    public static double[][] copy(double[][] a,int startI,int endI,int startJ,int endJ){
        if(Math.min(endJ-startJ, endI-startI)<1) {
            double[][] result=new double[0][0];
            return result;
        }

        double[][] result=new double[endI-startI][endJ-startJ];
        int p=0;
        int q;
        for(int i=startI;i<endI;i++){
            q=0;
            for(int j=startJ;j<endJ;j++){
                result[p][q]=a[i][j];
                q++;
            }
            p++;
        }
        return result;
    }

    public static void paste(double[][] x, double[][] y, int startI,int startJ){
        int p=0,q=0;
        for(int i=startI;i<startI+y.length;i++){
            for(int j=startJ;j<startJ+y[0].length;j++){
                x[i][j]=y[p][q];
                q++;
            }
            p++;
            q=0;

        }
    }

    public static double[][] E(int n){
        double[][] result=new double[n][n];
        for(int i=0;i<n;i++) result[i][i]=1;
        return result;
    }

    public static ETD recur(double[][] a){
       ETD Y=new ETD(a.length,a[0].length);

       if(a.length>2){
            double[][] A=new double[a.length/2][a.length/2];
            double[][] B=new double[a.length/2][a.length/2];
            double[][] C=new double[a.length/2][a.length/2];
            double[][] D=new double[a.length/2][a.length/2];
            A=copy(a, 0, a.length/2, 0, a.length/2);
            B=copy(a,0, a.length/2, a.length/2, a.length);
            C=copy(a, a.length/2, a.length, 0, a.length/2);
            D=copy(a,a.length/2,a.length,a.length/2,a.length);

            ETD lduA=recur(A);
            int r=0;
            for(int i=0;i<lduA.D.length;i++) if(lduA.D[i][i]!=0) lduA.r++;
            if(lduA.r==lduA.D.length){
                double[][] D2=new double[a.length/2][a.length/2];
                double[][] pasteL21=new double[a.length/2][a.length/2];
                double[][] pasteU12=new double[a.length/2][a.length/2];
                pasteU12=multy( inverseD(lduA.D), multy(inverseL(lduA.L), multy(transpose(lduA.P),B)));
                pasteL21=multy(C,multy(transpose(lduA.Q),multy(inverseU(lduA.U),inverseD(lduA.D))));
                D2=multy(lduA.D,pasteU12);
                D2=multy(pasteL21,D2);
                D2=subs(D,D2);
                ETD lduD2=recur(D2);
                pasteL21=multy(transpose(lduD2.P), pasteL21);
                pasteU12=multy(pasteU12,transpose(lduD2.Q));


                paste(Y.D,lduA.D,0,0);
                paste(Y.D,lduD2.D,a.length/2,a.length/2);

                paste(Y.P,lduA.P,0,0);
                paste(Y.P,lduD2.P,a.length/2,a.length/2);

                paste(Y.Q,lduA.Q,0,0);
                paste(Y.Q,lduD2.Q,a.length/2,a.length/2);

                paste(Y.L,lduA.L,0,0);
                paste(Y.L,pasteL21,a.length/2,0);
                paste(Y.L,lduD2.L,a.length/2,a.length/2);

                paste(Y.U,lduA.U,0,0);
                paste(Y.U,pasteU12,0,a.length/2);
                paste(Y.U,lduD2.U,a.length/2,a.length/2);

           }else{
                lduA.dr=copy(lduA.D,0,lduA.r,0,lduA.r);
                double[][] c0=new double[a.length/2][lduA.r];
                double[][] c1=new double[a.length/2][a.length/2-lduA.r];
                c0=copy(a, a.length/2, a.length, 0, lduA.r);
                c1=copy(a, a.length/2, a.length, lduA.r, a.length/2);
                double[][] u0=new double[lduA.r][lduA.r];
                double[][] v0=new double[lduA.r][a.length/2-lduA.r];
                u0=copy(lduA.U,0,lduA.r,0,lduA.r);
                v0=copy(lduA.U,0,lduA.r,lduA.r,lduA.U[0].length);
                double[][] curC;
                curC=multy(c0,v0);
                curC=add(curC,c1);
                c0=multy(c0,u0);
                /*???????????????????????????????*/
                double[][] b0=new double[lduA.r][a.length/2];
                double[][] b1=new double[a.length/2 -lduA.r][a.length/2];
                b0=copy(a, 0, lduA.r, a.length/2, a.length);
                b1=copy(a, lduA.r, a.length/2, a.length/2, a.length);
                double[][] l0=new double[lduA.r][lduA.r];
                double[][] m0=new double[lduA.r][a.length/2-lduA.r];
                l0=copy(lduA.L,0,lduA.r,0,lduA.r);
                m0=copy(lduA.L,lduA.r,a.length/2,0,lduA.r);
                double[][] curB=new double[a.length/2-lduA.r][a.length/2 ];
                curB=multy(m0,b0);
                b0=multy(l0,b0);
                b1=add(curB,b1);

                paste(B,b0,0,0);
                paste(B,b1,b0.length,0);
                B=multy(lduA.P,B);
                b0=copy(B,0,b0.length,0,b0[0].length);
                b1=copy(B,b0.length,B.length,0,B[0].length);

                paste(C,c0,0,0);
                paste(C,curC,0,c1[0].length);
                C=multy(C,lduA.Q);
                c0=copy(C,0,C.length,0,c0[0].length);
                c1=copy(C,0,C.length,c0[0].length,C[0].length);

                if((isnull(b1))&&(isnull(c1))){
                   ETD lduD=recur(D);
                   double[][] p3=new double[a.length][a[0].length];
                   double[][] q3=new double[a.length][a[0].length];
                   double[][] PQ=new double[a.length][a[0].length];
                   for(int i=0;i<lduA.r;i++) PQ[i][i]=1;
                   paste(PQ,E(D.length),a.length,lduA.r);
                   paste(PQ,E(a.length/2-lduA.r),lduA.r,lduA.r+D.length);

                   paste(p3,lduA.P,0,0);
                   paste(p3,lduD.P,a.length/2,a.length/2);

                   paste(q3,lduA.Q,0,0);
                   paste(q3,lduD.Q,a.length/2,a.length/2);

                   p3=multy(p3, PQ);
                   q3=multy(PQ,q3);


                   double[][] l21=multy(transpose(lduD.P), c0);
                   l21=multy(l21,lduA.dr);
                   paste(Y.L,l0,0,0);
                   paste(Y.L,l21,lduA.r,0);
                   paste(Y.L,lduD.L,lduA.r,lduA.r);
                   paste(Y.L,m0,lduA.r+a.length/2,0);
                   paste(Y.L,E(a.length/2 -lduA.r),lduA.r+a.length/2,lduA.r+a.length/2);

                   double[][] u12=multy(lduA.dr,b0);
                   u12=multy(u12,transpose(lduD.Q));
                   paste(Y.U,u0,0,0);
                   paste(Y.U,u12,0,lduA.r);
                   paste(Y.U,v0,0,lduA.r+u12[0].length);
                   paste(Y.U,lduD.U,lduA.r,lduA.r);
                   paste(Y.U,E(a.length-lduA.r-lduD.U.length),lduA.r+lduD.U.length,lduA.r+u12[0].length);

                   paste(Y.D,lduA.dr,0,0);
                   paste(Y.D,lduD.D,lduA.r,lduA.r);
                }else{
                   ETD lduC1=recur(c1);
                   ETD lduB1=recur(b1);
                   for(int i=0;i<Math.min(lduB1.D.length, lduB1.D[0].length);i++)if(lduB1.D[i][i]!=0)lduB1.r++;
                   for(int i=0;i<Math.min(lduC1.D.length, lduC1.D[0].length);i++)if(lduC1.D[i][i]!=0)lduC1.r++;
                   lduB1.dr=copy(lduB1.D,0,lduB1.r,0,lduB1.r);
                   lduC1.dr=copy(lduC1.D,0,lduC1.r,0,lduC1.r);

                   double[][] p1=E(a.length);
                   paste(p1,lduA.P,0,0);
                   /*
                    * ñòðàííî!!!!
                    */
                   double[][] p2=E(a.length);
                   int lengthI=p2.length - lduC1.P.length-lduB1.P.length;
                   paste(p2,lduC1.P,lengthI+lduB1.P.length,lengthI+lduB1.P.length);
                   paste(p2,lduB1.P,lengthI,lengthI);

                   double[][] q1=E(a.length);
                   paste(q1,lduA.Q,0,0);

                   double[][] q2=E(a.length);
                   lengthI=q2.length - lduC1.Q.length-lduB1.Q.length;
                   paste(q2,lduC1.Q,lengthI+lduB1.Q.length,lengthI+lduB1.Q.length);
                   paste(q2,lduB1.Q,lengthI,lengthI);

                   double[][] p3=multy(p1,p2);
                   double[][] q3=multy(q2,q1);

                   double[][] Dsh=multy(transpose(lduC1.P),inverseL(lduC1.L));
                   Dsh=multy(Dsh,D);
                   Dsh=multy(Dsh,inverseU(lduB1.U));
                   Dsh=multy(Dsh,transpose(lduB1.Q));
                   double[][] v1v4=multy(v0,transpose(lduC1.Q));
                   double[][] v5v6=multy(inverseD(lduA.dr),multy(b0,transpose(lduB1.Q)));
                   double[][] m1m4=multy(transpose(lduB1.P),m0);
                   double[][] m5m6=multy(transpose(lduC1.P),multy(c0,inverseD(lduA.dr)));

                   double[][] v1=copy(v1v4,0,v1v4.length,0,lduC1.r);
                   double[][] v4=copy(v1v4,0,v1v4.length,lduC1.r,v1v4[0].length);
                   double[][] v5=copy(v5v6,0,v5v6.length,0,lduB1.r);
                   double[][] v6=copy(v5v6,0,v5v6.length,lduB1.r,v5v6[0].length);

                   double[][] m6=copy(m5m6,lduC1.r,m5m6.length,0,m5m6[0].length);
                   double[][] m2=copy(lduC1.L,lduC1.r,lduC1.L.length,0,lduC1.r);
                   double[][] v3=copy(lduB1.U,0,lduB1.r,lduB1.r,lduB1.U[0].length);

                   double[][] D1sh=copy(Dsh,0,lduC1.r,0,lduB1.r);
                   double[][] D2sh=copy(Dsh,lduC1.r,Dsh.length,0,lduB1.r);
                   double[][] D3sh=copy(Dsh,0,lduC1.r,lduB1.r,Dsh[0].length);
                   double[][] D4sh=copy(Dsh,lduC1.r,Dsh.length,lduB1.r,Dsh[0].length);


                   double[][] m7=multy(D2sh,inverseD(lduB1.dr));
                   double[][] u3sh=copy(lduB1.U,0,lduB1.r,0,lduB1.r);
                   double[][] v7=multy(inverseD(lduC1.dr),multy(D1sh,u3sh));
                   double[][] v8=add(multy(D1sh,v3),D3sh);
                   v8=multy(inverseD(lduC1.dr),v8);

                   ETD lduD4sh=recur(D4sh);
                   double[][] p4=E(a.length);
                   paste(p4,lduD4sh.P,a.length-D4sh.length,a.length-D4sh.length);
                   double[][] q4=E(a.length);
                   paste(q4,lduD4sh.Q,a.length-D4sh.length,a.length-D4sh.length);
                   double[][] p5=multy(p3,p4);
                   double[][] q5=multy(q4,q3);
                   m6=multy(transpose(lduD4sh.P),m6);
                   m7=multy(transpose(lduD4sh.P),m7);
                   m2=multy(transpose(lduD4sh.P),m2);
                   v6=multy(v6,transpose(lduD4sh.Q));
                   v8=multy(v8,transpose(lduD4sh.Q));
                   v3=multy(v3,transpose(lduD4sh.Q));
                   Y.D=E(a.length);
                   paste(Y.D,lduA.dr,0,0);
                   paste(Y.D,lduB1.dr,lduA.r,lduA.r);
                   //!!!!!!!!!!!!!!!!!!!!!!!!!!!
                   paste(Y.D,lduC1.dr,lduA.r+lduB1.r,lduA.r+lduB1.r);
                   paste(Y.D,lduD4sh.D,lduA.r+lduB1.r+lduC1.r,lduA.r+lduB1.r+lduC1.r);

                   double[][] p6=new double[a.length][a.length];
                   double[][] q6=new double[a.length][a.length];

                   paste(p6,E(lduA.r),0,0);
                   paste(p6,E(lduB1.r),lduA.r,a.length/2);
                   paste(p6,E(lduC1.r),a.length/2,lduA.r);
                   for(int i=0;i<Math.min(lduD4sh.D.length, lduD4sh.D[0].length);i++)if(lduD4sh.D[i][i]!=0)lduD4sh.r++;
                   paste(p6,E(lduD4sh.r),a.length/2+lduC1.r,a.length/2+lduB1.r);

                   paste(q6,E(lduA.r),0,0);
                   paste(q6,E(lduB1.r),lduA.r,lduA.r);
                   if(a.length>(lduA.r+lduB1.r+lduC1.r+lduD4sh.D.length))
                   paste(q6,E(lduC1.r),lduA.r+lduB1.r,(lduA.r+lduB1.r+lduC1.r+lduD4sh.D.length));
                   paste(q6,E(lduD4sh.r),a.length-lduC1.r-lduD4sh.D.length,lduA.r+lduB1.r);
                   paste(q6,E(lduD4sh.r),a.length-lduD4sh.D.length,lduA.r+lduB1.r+lduC1.r);

//                   Y.D=multy(p6,Y.D);
//                   Y.D=multy(Y.D,q6);

                   p6=multy(p5,transpose(p6));
                   q6=multy(transpose(q6),q5);
                   Y.P=p6;
                   Y.Q=q6;
                   Y.L=E(a.length);
                   paste(Y.L,l0,0,0);
                   double[][] m5=copy(m5m6,0,lduC1.r,0,lduC1.r);
                   paste(Y.L,m5,lduA.r,0);
                   double[][] m1=copy(m1m4,0,lduB1.r,0,m1m4[0].length);
                   paste(Y.L,m1,lduA.r+m5.length,0);
                   paste(Y.L,m6,lduA.r+m5.length+m1.length,0);
                   double[][] m4=copy(m1m4,lduB1.r,m1m4.length,0,m1m4[0].length);
                   paste(Y.L,m4,lduA.r+m5.length+m1.length+m6.length,0);
                   double[][] l2sh=copy(lduC1.L,0,lduC1.r,0,lduC1.r);
                   paste(Y.L,l2sh,l0.length,l0.length);
                   paste(Y.L,m7,l0.length+m5.length+m1.length,l0.length);
                   double[][] l3sh=copy(lduB1.L,0,lduB1.r,0,lduB1.r);
                   paste(Y.L,l3sh,l0.length+l2sh.length,l0.length+l2sh.length);
                   paste(Y.L,m2,l0.length+l2sh.length+m1.length,l0.length+l2sh.length);
                   double[][] m3=copy(lduB1.L, lduB1.r, lduB1.L.length,0,lduB1.L[0].length);
                   paste(Y.L,m3,l0.length+l2sh.length+m1.length+m2.length,l0.length+l2sh.length);
                   paste(Y.L,lduD4sh.L,l0.length+l2sh.length+l3sh.length,l0.length+l2sh.length+l3sh.length);

                   Y.U=E(a.length);
                   paste(Y.U,u0,0,0);
                   paste(Y.U,v1,0,u0.length);
                   paste(Y.U,v5,0,u0.length+v1.length);
                   paste(Y.U,v6,0,u0.length+v1.length+v5.length);
                   paste(Y.U,v4,0,u0.length+v1.length+v5.length+v6.length);
                   double[][] u2sh=copy(lduC1.U,0,lduC1.r,0,lduC1.r);
                   double[][] v2=copy(lduC1.U,0,lduC1.r,lduC1.r,lduC1.U[0].length);
                   paste(Y.U,u2sh,u0.length,u0.length);
                   paste(Y.U,v7,u0.length,u0.length+v1.length);
                   paste(Y.U,v8,u0.length,u0.length+v1.length+v7.length);
                   paste(Y.U,v2,u0.length,u0.length+v1.length+v7.length+v8.length);
                   double[][] u3=copy(lduB1.U,0,lduB1.r,0,lduB1.r);
                   paste(Y.U,u3,u0.length+u2sh.length,u0.length+u2sh.length);
                   paste(Y.U,v3,u0.length+u2sh.length,u0.length+u2sh.length+u3.length);
                   paste(Y.U,lduD4sh.U,u0.length+u2sh.length+u3.length,u0.length+u2sh.length+u3.length);



//                   Y.L=multy(p6,Y.L);
//                   Y.L=multy(Y.L,transpose(p6));
//
//                   Y.Q=multy(q6,Y.Q);
//                   Y.Q=multy(Y.Q,transpose(q6));

                   /*????????????????????????????????*/
                }


           }
       }else{
           if((a.length==2)&&(a[0].length==2)){
        if(a[0][0]!=0){first(Y,a);}
        else if(a[0][1]!=0){second(Y,a);}
             else if(a[1][0]!=0){third(Y,a);}
                  else if(a[1][1]!=0){forth(Y,a);}
                       else isNu(Y,a);
        }else if((a.length==1)&&(a[0].length==2)){
              oneXtwo(Y,a);
            }else if((a.length==2)&&(a[0].length==1)){
              twoXone(Y,a);
            }else if(a[0][0]==0){
                    isNole(Y,a);
            }else {
               isOne(Y,a);
            }
        }

       return Y;
   }

    public static void main(String[] args) {
        int n=4;
        double[][] a=new double[n][n];
//        for(int i=0;i<n;i++){
//            for(int j=0;j<n;j++){
//                Random rmd=new Random();
//                a[i][j]=10*rmd.nextInt();
//            }
//        }

        a[0][0]=1;
        a[0][1]=1;
        a[0][2]=1;
        a[0][3]=1;

        a[1][0]=1;
        a[1][1]=1;
        a[1][2]=3;
        a[1][3]=4;

        a[2][0]=1;
        a[2][1]=2;
        a[2][2]=3;
        a[2][3]=5;

        a[3][0]=1;
        a[3][1]=1;
        a[3][2]=4;
        a[3][3]=6;



        ETD result=new ETD(n,n);
        result=recur(a);
        double[][] hr=multy(result.P,result.L);
                hr=multy(hr,multy(result.D,result.U));
                hr=multy(hr,result.Q);
        System.out.println("dsasdas");
    }

}
