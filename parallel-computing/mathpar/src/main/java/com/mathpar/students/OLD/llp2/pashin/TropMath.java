/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.llp2.pashin;

import java.util.Random;

import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.MatrixS;
import mpi.*;
import com.mathpar.number.*;
import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;


/**
 *
 * @author kell
 */
public class TropMath {
    public static void main(String[] args){
//        int n = 5;
//        int m = 5;
//        int den = 100;
//        Random rnd = new Random();
//        Ring ring = new Ring("R64MaxPlus[x]");
//
//        MatrixD a= new MatrixD(randomLowTriangularArr(n,m,den, new int[]{5, 5},rnd,ring));
//        MatrixD b = new MatrixD(MatrixS.randomScalarArr2d(n, 1, den, new int[]{5, 5}, rnd, ring));
//
//
//
//       // MatrixD b= new MatrixD(randomLowTriangularArr(n,1,den, new int[]{5, 5},rnd,ring));
//
//        System.out.println("a = " + a);
//        System.out.println("b = " + b);
//        System.out.println("res = " + frwd_subst(a,b, ring));
//
//
//        MatrixD c= new MatrixD(randomUpperTriangularArr(n,m,den, new int[]{5, 5},rnd,ring));
//        MatrixS D = new MatrixS(n, 1, den, new int[]{5, 5}, rnd, Ring.oneOfType(ring.algebra[0]), ring);
//        MatrixD d = new MatrixD(D.toScalarArray(ring));
//
//        System.out.println("a = " + c);
//        System.out.println("b = " + d);
//        System.out.println("res = " + back_subst(c,d, ring));
//
//
//        MatrixD e= new MatrixD(randomDiagArr(n,m,den, new int[]{5, 5},rnd,ring));
//        MatrixS F = new MatrixS(n, 1, den, new int[]{5, 5}, rnd, Ring.oneOfType(ring.algebra[0]), ring);
//        MatrixD f = new MatrixD(D.toScalarArray(ring));
//
//        System.out.println("e = " + e);
//        System.out.println("f = " + f);
//        System.out.println("res = " + closure(c,d, ring));


        //Vector V =new VectorS(arr);

        //System.out.println("b = " + d.multiply(d, ring));
        Ring ring = new Ring("R[x]");
        Element[][] aa = new Element[][]{{new NumberR(10),new NumberR(10),new NumberR(20)},
                                         {new NumberR(20),new NumberR(25),new NumberR(40)},
                                         {new NumberR(30),new NumberR(50),new NumberR(61)}};
        MatrixD a = new MatrixD(aa);
        System.out.println(""+a.toString(ring));
        System.out.println("-----------------");
        MatrixD rez = ldm2(a);
        System.out.println(""+rez.toString(ring));
    }

    /**
     * Forward substitution
     * @throws MPIException
     */
    public static MatrixD  frwd_subst(MatrixD l, MatrixD b, Ring ring){
        int n = l.M.length;
        int den = 100;
        Random rnd = new Random();

        System.out.println(ring);

        MatrixD x = new MatrixD(MatrixS.randomScalarArr2d(n, 1, den, new int[]{5, 5}, rnd, ring));
              //количество строк матрицы

        for(int i=0; i<n; i++){
            x.M[i][0]=b.M[i][0];
            for(int j=1; j<i-1; j++ ){
                x.M[i][0]=x.M[i][0].add(l.M[i][j].multiply(x.M[i][0], ring), ring);
            }
        }
        return x;
    }


    /**
     * Back substitution
     * @throws MPIException
     */
    public static MatrixD  back_subst(MatrixD m, MatrixD b, Ring ring){
        int n = m.M.length;
        int den = 100;
        Random rnd = new Random();

        MatrixD x = new MatrixD(MatrixS.randomScalarArr2d(n, 1, den, new int[]{5, 5}, rnd, ring));
                 //количество строк матрицы
        for(int i=n-1; i>1; i--){
            x.M[i][0]=b.M[i][0];
            for(int j=n; j<i+1; j-- ){
                x.M[i][0]=x.M[i][0].add(m.M[i][j].multiply(x.M[i][0], ring), ring);
            }
        }
        return x;
    }



//Closure of a diagonal matrix

    public static MatrixD closure(MatrixD m, MatrixD b, Ring ring){
        int n = m.M.length;         //количество строк матрицы
        int den = 100;
        Random rnd = new Random();

        MatrixD x = new MatrixD(MatrixS.randomScalarArr2d(n, 1, den, new int[]{5, 5}, rnd, ring));

        for(int i=0; i<n; i++){
                x.M[i][0]=m.M[i][i].multiply(b.M[i][0], ring);
        }
        return x;
    }



    public static MatrixD ldm(MatrixD a){
        int N = 5;
        int M = 1;
        int den = 100;
        Random rnd = new Random();
        Ring ring = new Ring("R[x]");
        NumberR d=new NumberR();


        MatrixS V = new MatrixS(N, M, den, new int[]{5, 5}, rnd, Ring.oneOfType(ring.algebra[0]), ring);
        MatrixD v = new MatrixD(V.toScalarArray(ring));
        int n = a.M.length;         //количество строк матрицы

        //MatrixS C = new MatrixS(new Element[n][n], ring);
        //MatrixD c = new MatrixD(C.toScalarArray(ring));

        MatrixD c = new MatrixD(new Element[n][n]);

        for(int i=0; i<n; i++){
           for(int j=0; j<n; j++){
                c.M[i][j]=a.M[i][j];
           }
        }

        for(int j=0; j<n; j++){
            for(int i=0; i<j; i++){
                v.M[i][0]=a.M[i][j];
            }
            for(int k=0; k<j-1; k++){
               for(int i=k+1; i<j; i++){
                   v.M[i][0]=v.M[i][0].add(a.M[i][k].multiply(v.M[k][0], ring), ring);
               }
            }
            for(int i=0; i<j-1; i++){
                a.M[i][j]=a.M[i][j].multiply(v.M[i][0], ring);  //*
            }
            a.M[j][j]=v.M[j][0];
            for(int k=0; k<j-1; k++){
                for(int i=j+1; i<n; i++){
                    a.M[i][j]=a.M[i][j].add(a.M[i][k].multiply(v.M[k][0], ring), ring);
                }
            }
            d= (NumberR)v.M[j][0]; //*
            for(int i=j+1; i<n; i++){
                a.M[i][j]=a.M[i][j].multiply(d, ring);
            }
        }
        return a;
    }

    public static MatrixD ldm2(MatrixD a){
        int N = 5;
        int M = 1;
        int den = 100;
        Random rnd = new Random();
        Ring ring = new Ring("R[x]");
        //NumberRMaxPlus d=new NumberRMaxPlus();
        NumberR d = new NumberR();

        MatrixS V = new MatrixS(N, M, den, new int[]{5, 5}, rnd, Ring.oneOfType(ring.algebra[0]), ring);
        MatrixD v = new MatrixD(V.toScalarArray(ring));
        int n = a.M.length;         //количество строк матрицы

        //MatrixS C = new MatrixS(new Element[n][n], ring);
        //MatrixD c = new MatrixD(C.toScalarArray(ring));
        MatrixD c = new MatrixD(new Element[n][n]);

        for(int i=0; i<n; i++){
           for(int j=0; j<n; j++){
                c.M[i][j]=a.M[i][j];
           }
        }

        for(int j=0; j<n; j++){
            for(int i=0; i<=j; i++){
                v.M[i][0]=a.M[i][j];
            }
            for(int k=0; k<=j-1; k++){
               for(int i=k+1; i<=j; i++){
                   v.M[i][0]=v.M[i][0].subtract(a.M[i][k].multiply(v.M[k][0], ring), ring);
               }
            }
            for(int i=0; i<=j-1; i++){
                a.M[i][j]=v.M[i][0].divide(a.M[i][i], ring);  //*
            }
            a.M[j][j]=v.M[j][0];
            for(int k=0; k<=j-1; k++){
                for(int i=j+1; i<n; i++){
                    a.M[i][j]=a.M[i][j].subtract(a.M[i][k].multiply(v.M[k][0], ring), ring);
                }
            }
            //d=new NumberRMaxPlus((NumberR)v.M[j][0]); //*
            d=(NumberR)v.M[j][0];
            for(int i=j+1; i<n; i++){
                a.M[i][j]=a.M[i][j].divide(d, ring);
            }
        }
        return a;
    }

  /** Method for constructin of random lower triangular matrix of  polynomials or numbers
     *

     * @param r -- row numbers
     * @param c -- column numbers
     * @param density -- is an integer of range 0,1...10000.
     * @param randomType -- array of:
     *   [maxPowers_1_var,.., maxPowers-last_var,  type of coeffs, density of polynomials, nbits]
     *   The density is an integer of range 0,1...100.
     * @param ran -- Random issue
     * @param one -- one of the matrix elements ring
     * @return array2d of Elements
     */
    public static Element[][] randomLowTriangularArr(int r, int c, int Density,
            int[] randomType,
            Random ran,
            Ring ring) {

        Element one = Ring.oneOfType(ring.algebra[0]);
        //System.out.println("" + Array.toString(randomType));
        if (randomType.length > 2) {
            one = Polynom.polynomFromNumber(one, ring);
        }
        double density = (Density >= 0) ? Density / 100.0 : 0.01 / (Density);
        Element zero = one.myZero(ring);
        Element[][] M = new Element[r][c];
        if (density == 1.0) {
            for (int i = 0; i < r; i++) {
                for (int j = 0; j < c; j++) {
                    M[i][j]= (i<=j)?zero:one.random(randomType, ran, ring);
                }
            }
            return M;
        }
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                double m1 = ran.nextDouble();
                if(i<=j){
                   M[i][j] =  zero;

                }else{
                   M[i][j] = (m1 > density) ? zero : one.random(randomType, ran, ring);
                }
            }
        }
        return M;
    }


    /** Method for constructin of random upper triangular matrix of  polynomials or numbers
     *

     * @param r -- row numbers
     * @param c -- column numbers
     * @param density -- is an integer of range 0,1...10000.
     * @param randomType -- array of:
     *   [maxPowers_1_var,.., maxPowers-last_var,  type of coeffs, density of polynomials, nbits]
     *   The density is an integer of range 0,1...100.
     * @param ran -- Random issue
     * @param one -- one of the matrix elements ring
     * @return array2d of Elements
     */
    public static Element[][] randomUpperTriangularArr(int r, int c, int Density,
            int[] randomType,
            Random ran,
            Ring ring) {
        Element one = Ring.oneOfType(ring.algebra[0]);
        //System.out.println("" + Array.toString(randomType));
        if (randomType.length > 2) {
            one = Polynom.polynomFromNumber(one, ring);
        }
        double density = (Density >= 0) ? Density / 100.0 : 0.01 / (Density);
        Element zero = one.zero(ring);
        Element[][] M = new Element[r][c];
        if (density == 1.0) {
            for (int i = 0; i < r; i++) {
                for (int j = 0; j < c; j++) {
                    M[i][j]= (i>=j)?zero:one.random(randomType, ran, ring);
                }
            }
            return M;
        }
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                double m1 = ran.nextDouble();
                if(i>=j){
                    M[i][j] =  zero;
                }else{
                    M[i][j] = (m1 > density) ? zero : one.random(randomType, ran, ring);
                }
            }
        }
        return M;
    }



    /** Method for constructin of random upper triangular matrix of  polynomials or numbers
     *

     * @param r -- row numbers
     * @param c -- column numbers
     * @param density -- is an integer of range 0,1...10000.
     * @param randomType -- array of:
     *   [maxPowers_1_var,.., maxPowers-last_var,  type of coeffs, density of polynomials, nbits]
     *   The density is an integer of range 0,1...100.
     * @param ran -- Random issue
     * @param one -- one of the matrix elements ring
     * @return array2d of Elements
     */
    public static Element[][] randomDiagArr(int r, int c, int Density,
            int[] randomType,
            Random ran,
            Ring ring) {
        Element one = Ring.oneOfType(ring.algebra[0]);
        //System.out.println("" + Array.toString(randomType));
        if (randomType.length > 2) {
            one = Polynom.polynomFromNumber(one, ring);
        }
        double density = (Density >= 0) ? Density / 100.0 : 0.01 / (Density);
        Element zero = one.zero(ring);
        Element[][] M = new Element[r][c];
        if (density == 1.0) {
            for (int i = 0; i < r; i++) {
                for (int j = 0; j < c; j++) {
                    M[i][j]= (i!=j)?zero:one.random(randomType, ran, ring);
                }
            }
            return M;
        }
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                double m1 = ran.nextDouble();
                if(i!=j){
                    M[i][j] =  zero;
                }else{
                    M[i][j] = (m1 > density) ? zero : one.random(randomType, ran, ring);
                }
            }
        }
        return M;
    }

}

