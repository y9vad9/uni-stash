package com.mathpar.students.OLD.llp2.students;


import com.mathpar.number.*;
import com.mathpar.matrix.*;

public class Test__tugareva {

    public static void main(String[] args) {
        System.out.println("This is a test for tugareva.java");
        Ring ring = new Ring("C64[x]");


        /**int M[][] = {{8, 3}, //работает
        {-10, -3}};
        MatrixS N = new MatrixS(M, ring);
        System.out.println("A=" + N);
        System.out.println("fpol111=" + tugareva.method(N, ring).toString(ring));*/

        /**int M1[][] = {{1, 2},//работает
        {1, 2}};
        MatrixS N1 = new MatrixS(M1, ring);
        System.out.println("A=" + N1);
        System.out.println("fpol111=" + tugareva.method(N1, ring).toString(ring));*/

       /** int M2[][] = {{1, 2, 3},//работает
        {1, 2, 7}};
        MatrixS N2 = new MatrixS(M2, ring);
        System.out.println("A=" + N2);
        System.out.println(tugareva.method(N2, ring).toString(ring));*/

        /**int M3[][] = {{2, 3},//работает
        {1, 0}};
        MatrixS N3 = new MatrixS(M3, ring);
        System.out.println("A=" + N3);
        System.out.println("fpol111=" + tugareva.method(N3, ring).toString(ring));*/

        /**int M4[][] = {{1, 1, 0},
        {0, 1, 1},
        {1, 0, 1}};
        MatrixS N4 = new MatrixS(M4, ring);
        System.out.println("A=" + N4);
        System.out.println("fpol111=" + tugareva.method(N4, ring).toString(ring));*/

        /**int M5[][] = {{0, 1, 0},//работает
        {-4, 4, 0},
        {-2, 1, 2}};
        MatrixS N5 = new MatrixS(M5, ring);
        System.out.println("A=" + N5);
        System.out.println(tugareva.method(N5, ring).toString(ring));*/

        int M6[][] = {{1, 0, 0},//работает
        {0, 0, 0},
        {0, 0, 0}};
        MatrixS N6 = new MatrixS(M6, ring);
        int k = N6.rank(ring);
        System.out.println("A=" + N6);
        System.out.println("fpol=" + tugareva.JordanForm(N6, ring).toString(ring));

        /**int M7[][] = {{4, -2, 2},//работает
        {2, 0, 2},
        {-1, 1, 1}};
        MatrixS N7 = new MatrixS(M7, ring);
        System.out.println("A=" + N7);
        System.out.println(tugareva.method(N7, ring).toString(ring));*/

       /** int M8[][] = {{3, -5, 2},
        {5, -8, 3},
        {6, 17, 3}};
        MatrixS N8 = new MatrixS(M8, ring);
        System.out.println("A=" + N8);
        System.out.println("fpol111=" + tugareva.method(N8, ring).toString(ring));*/

        /**int M9[][] = {{1,1,1,0},{-1,3,0,1},{-1,0,-1,1},{0,-1,-1,1}};//работает
        MatrixS N9 = new MatrixS(M9, ring);
        System.out.println("A=" + N9);
        System.out.println("fpol111=" + tugareva.method(N9, ring).toString(ring));*/

       /** int M10[][] = {{0,1,0,0,0},{0,0,1,0,0},{0,0,0,1,0},{0,0,0,0,1},{1,-1,-2,2,1}};
        MatrixS N10 = new MatrixS(M10, ring);
        System.out.println("A=" + N10);
        System.out.println("fpol111=" + tugareva.method(N10, ring).toString(ring));*/

        /**int M11[][] = {{0,1,0},{-4,4,0},{-2,1,2}};//работает
        MatrixS N11 = new MatrixS(M11, ring);
        System.out.println("A=" + N11);
        System.out.println("fpol111=" + tugareva.method(N11, ring).toString(ring));*/

        /**int M12[][] = {{2,6,-15},{1,1,-5},{1,2,-6}};//работает
        MatrixS N12 = new MatrixS(M12, ring);
        System.out.println("A=" + N12);
        System.out.println("fpol111=" + tugareva.method(N12, ring).toString(ring));*/

        /**int M13[][] = {{9,-6,-2},{18,-12,-3},{18,-9,-6}};//работает
        MatrixS N13 = new MatrixS(M13, ring);
        System.out.println("A=" + N13);
        System.out.println("fpol111=" + tugareva.method(N13, ring).toString(ring));*/

        /**int M14[][] = {{4,6,-15},{1,3,-5},{1,2,-4}};//работает
        MatrixS N14 = new MatrixS(M14, ring);
        System.out.println("A=" + N14);
        System.out.println("fpol111=" + tugareva.method(N14, ring).toString(ring));*/

        /**int M15[][] = {{0,-4,0},{1,-4,0},{1,-2,2}};//работает
        MatrixS N15 = new MatrixS(M15, ring);
        System.out.println("A=" + N15);
        System.out.println("fpol111=" + tugareva.method(N15, ring).toString(ring));*/

        /**int M16[][] = {{12,-6,-2},{18,-9,-3},{18,-9,-3}};//работает
        MatrixS N16 = new MatrixS(M16, ring);
        System.out.println("A=" + N16);
        System.out.println("fpol111=" + tugareva.method(N16, ring).toString(ring));*/

       /** int M17[][] = {{4,-5,2},{5,-7,3},{6,-9,4}};//работает
        MatrixS N17 = new MatrixS(M17, ring);
        System.out.println("A=" + N17);
        System.out.println("fpol111=" + tugareva.method(N17, ring).toString(ring));*/

//        int M18[][] = {{5,-3,2},{6,-4,4},{4,-4,5}};//работает
//        MatrixS N18 = new MatrixS(M18, ring);
//        System.out.println("A=" + N18);
//        System.out.println("fpol111=" + tugareva.method(N18, ring).toString(ring));

        /**int M19[][] = {{1,-3,3},{-2,-6,13},{-1,-4,8}};//работает
        MatrixS N19 = new MatrixS(M19, ring);
        System.out.println("A=" + N19);
        System.out.println("fpol111=" + tugareva.method(N19, ring).toString(ring));*/

        /**int M20[][] = {{7,-12,6},{10,-19,10},{12,-24,13}};//работает
        MatrixS N20 = new MatrixS(M20, ring);
        System.out.println("A=" + N20);
        System.out.println("fpol111=" + tugareva.method(N20, ring).toString(ring));*/

//        int M21[][] = {{1,-3,4},{4,-7,8},{6,-7,7}};//работает
//        MatrixS N21 = new MatrixS(M21, ring);
//        System.out.println("A=" + N21);
//        System.out.println("fpol111=" + tugareva.method(N21, ring).toString(ring));

//        int M22[][] = {{3,-1,0},{6,-3,2},{8,-6,5}};//работает
//        MatrixS N22 = new MatrixS(M22, ring);
//        System.out.println("A=" + N22);
//        System.out.println("fpol111=" + tugareva.method(N22, ring).toString(ring));


    }
}
