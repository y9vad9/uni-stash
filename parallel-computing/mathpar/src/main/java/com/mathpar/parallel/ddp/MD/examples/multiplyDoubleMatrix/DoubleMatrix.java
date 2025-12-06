package com.mathpar.parallel.ddp.MD.examples.multiplyDoubleMatrix;
import java.io.Serializable;
import java.util.Random;
import java.lang.*;

public class DoubleMatrix implements Serializable{
    double [][]data;
    int n;
    public int GetSize(){
        return n;
    }
    DoubleMatrix(int size){
        n=size;
        data=new double[n][n];
    }
    public DoubleMatrix(){
        return;
    }
    public void InitRandom(int size){
        n=size;
        data=new double[n][n];
        Random rand=new Random();
        for (int i=0; i<n; i++)
            for (int j=0; j<n; j++)
                data[i][j]=(rand.nextDouble()+0.01)*100;
    }
    public void Print(){
        for (int i=0; i<n; i++){
            for (int j=0; j<n; j++){
                System.out.print(data[i][j]+" ");
            }
            System.out.println("");
        }
        System.out.println("");
    }
    public DoubleMatrix[] Split(){
        /* |0 2|
         * |1 3|
         */
        DoubleMatrix[] res=new DoubleMatrix[4];
        int ns=n/2;
        int []di={0,ns,0,ns};
        int []dj={0,0,ns,ns};
        for (int k=0; k<4;k++){
            DoubleMatrix cur=new DoubleMatrix(ns);
            for (int i=0; i<ns; i++){
                for (int j=0; j<ns; j++)
                    cur.data[i][j]=data[i+di[k]][j+dj[k]];
            }
            res[k]=cur;
        }
        return res;
    }
    public DoubleMatrix Split(int numb){
        /* |0 2|
         * |1 3|
         */
        DoubleMatrix res=new DoubleMatrix();
        int ns=n/2;
        int []di={0,ns,0,ns};
        int []dj={0,0,ns,ns};
        int k=numb;
        DoubleMatrix cur=new DoubleMatrix(ns);
        for (int i=0; i<ns; i++){
            for (int j=0; j<ns; j++)
                cur.data[i][j]=data[i+di[k]][j+dj[k]];
        }
        return cur;
    }
    public void FillPart(int numb, DoubleMatrix m){
        int ns=m.n;
        int []di={0,ns,0,ns};
        int []dj={0,0,ns,ns};
        if (data==null){
            data=new double[m.n*2][m.n*2];
            n=m.n*2;
        }
        for (int i=0; i<ns; i++){
            for (int j=0; j<ns; j++){
                data[i+di[numb]][j+dj[numb]]=m.data[i][j];
            }
        }

    }
    public DoubleMatrix Multon(DoubleMatrix B){
        /*
         * this=A
         * A*B=C
         */
        DoubleMatrix C=new DoubleMatrix(n);
        for (int i=0; i<n; i++){
            for (int j=0; j<n; j++){
                C.data[i][j]=0;
                for (int k=0; k<n; k++){
                    C.data[i][j]+=data[i][k]*B.data[k][j];
                }
            }
        }
        return C;
    }
    public void SetMinus(){
        for (int i=0; i<n; i++){
            for( int j=0; j<n; j++)
                data[i][j]=-data[i][j];
        }
    }
    public DoubleMatrix GetSub(){
        DoubleMatrix res=new DoubleMatrix(n);
        for (int i=0; i<n; i++)
            for (int j=0; j<n; j++)
                res.data[i][j]=-data[i][j];
        return res;
    }
    public DoubleMatrix Add(DoubleMatrix B){
        DoubleMatrix C=new DoubleMatrix(n);
        for (int i=0; i<n; i++)
            for (int j=0; j<n; j++)
                C.data[i][j]=data[i][j]+B.data[i][j];
        return C;
    }
    private DoubleMatrix funcGetRev(DoubleMatrix d){
        DoubleMatrix res=new DoubleMatrix(d.n);
        if (d.n==1){
            res.data[0][0]=1/d.data[0][0];
        }
        else {
            DoubleMatrix  []v=d.Split();
            DoubleMatrix revA=funcGetRev(v[0]);
            DoubleMatrix revAs=revA.GetSub();
            DoubleMatrix X=revAs.Multon(v[2]);
            DoubleMatrix Y=v[1].Multon(revAs);
            DoubleMatrix Z=funcGetRev(v[3].Add(Y.Multon(v[2])));
            DoubleMatrix W=Z.Multon(Y);
            res.FillPart(0,revA.Add(X.Multon(W)));
            res.FillPart(1, W);
            res.FillPart(2, X.Multon(Z));
            res.FillPart(3, Z);
        }
        return res;
    }
    public DoubleMatrix GetRev(){
        return funcGetRev(this);
    }
    public double []ToDoubleArray(){
        double []res=new double[n*n];
        for (int i=0; i<n; i++)
            for (int j=0; j<n; j++)
                res[i*n+j]=data[i][j];
        return res;
    }
    public void FillFromDoubleArray(double []ar){
        n=(int)java.lang.Math.sqrt(ar.length+0.3);
        data=new double[n][n];
        for (int i=0; i<n; i++)
            for (int j=0; j<n; j++)
                data[i][j]=ar[i*n+j];
    }
    public boolean Compare(DoubleMatrix m){
        boolean res=true;
        for (int i=0; i<n; i++)
            for (int j=0; j<n; j++)
                if (java.lang.Math.abs(data[i][j]-m.data[i][j])>1e-3)
                    res=false;
        return res;
    }
    boolean IsOnesMatrix(){
        for (int i=0; i<n; i++){
            for (int j=0; j<n; j++){
                if (i==j){
                    if (java.lang.Math.abs(data[i][j]-1)>1e-3)
                          return false;
                }
                else {
                    if (java.lang.Math.abs(data[i][j])>1e-3)
                          return false;
                }
            }
        }
        return true;
    }
}
