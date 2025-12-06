/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.students.OLD.stud2014.storogev;

/**
 *
 * @author dryouch
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//package Storogev;

/**
 *
 * @author dryouch
 */
public class ETD {
public double[][]P;
public double[][]L;
public double[][]D;
public double[][]U;
public double[][]Q;
public double[][]dr;
public int r=0;
public int N;
public int M;

public ETD(int n,int m){
    N=n;
    M=m;
    P=new double[n][m];
    L=new double[n][m];
    D=new double[n][m];
    U=new double[n][m];
    Q=new double[n][m];
//    for(int i=0;i<n;i++){
//        P[i][i]=1;
//        Q[i][i]=1;
//    }
}
}
