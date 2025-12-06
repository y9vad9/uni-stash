/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.kryuchkov.tropicSolve;

import com.mathpar.matrix.*;
import com.mathpar.number.*;

/**
 *
 * @author alexey
 */
public class testsolve {

    public static int[] addToJ(MatrixS A, VectorS b, Ring ring) {
        int col = A.colNumb;
        int str = A.size;

        int[] J = new int[col];
        J[0] = 1;

        Element[][] Amass = new Element[str][col];
        Amass = A.M;
        int lenB = b.V.length;
        Element[] Bmass = new Element[lenB];
        Bmass = b.V;
        Element[] strTempMass = new Element[col];
        int k = 1;
        for (int i = 1; i < str; i++) {

            for (int j = k; j < col; j++) {
                strTempMass[j] = Amass[i][j].multiply(Bmass[j], ring);
            }

            if (checkAddToJ(strTempMass, k, ring)) {
                System.out.println("да неужели!!!!");
                J[k] = ++k;
            }
        }
        return J;
    }

    public static boolean checkAddToJ(Element[] mass, int pos, Ring ring) {

        Element[] twomin = twoMinStrSystem(mass, pos, ring);
        Element min1 = twomin[0];
        Element min2 = twomin[2];
        int pos1 = (int) twomin[1].value;

        if (min1.compareTo(min2) != 0 && (pos1 + 1) == pos) return true;
        return false;


    }

    public static Element[] twoMinStrSystem(Element[] mass, int pos, Ring ring) {

        int pos1 = pos;
        int pos2 = pos + 1;
        Element min1 = mass[pos1];
        Element min2 = mass[pos2];
        int n = mass.length;
        for (int i = pos; i < n; i++) {
            if (min1.compareTo(mass[i], ring) == -1) {
                min2 = (Element) min1.clone();
                min1 = mass[i];
                pos2 = pos1;
                pos1 = i;
            }
            if (i > pos && min2.compareTo(mass[i], ring) == -1 && mass[i].compareTo(min1, ring) >= 0) {
                min2 = mass[i];
                pos2 = i;
            }
        }

        Element[] res = new Element[] {min1, new NumberZ(pos1), min2, new NumberZ(pos2)};
        return res;
    }


    public static VectorS generateFirstSolve(MatrixS A, Ring ring){
        Element[][] Amatr = A.M;
        Element[] res = new Element[A.colNumb];

        Element[] minElements = twoMinStrSystem(Amatr[A.colNumb-1], 0, ring);
        Element min1 = minElements[0];
        int pos1 = (int)minElements[1].value;
        Element min2 = minElements[2];
        Element  t = min2.subtract(min1, ring);

        if(!t.isZero(ring)) res[pos1] = t;

        VectorS result = new VectorS(res);

        return result;
    }

    public static boolean checkSolve(MatrixS A,VectorS B, int l,Ring ring){
        Element[][] matrix = A.M;
        Element[] solution = B.V;

        Element[] multElements = new Element[A.colNumb];
        int n = solution.length;
        for(int i=0;i<n;i++){
            multElements[i] = matrix[l][i].multiply(solution[i], ring);
        }
        Element[] mins = twoMinStrSystem(solution, 0, ring);
        if(mins[0].compareTo(mins[2], ring)==0) return true;
        return false;
    }



    public static VectorS tropicalLinerSystemsFiniteCoefficients(MatrixS A, Ring ring) {

        VectorS B = generateFirstSolve(A, ring);
        //l- самая верхняя строка, когда решение является тропическим по Григорьеву
        int l = A.colNumb;
        int m = l;

        for(int k = l-1;k>=0;k--){
            //для первых m-l строк B не является решением
            for(int i=l-1;i>=0;i--){
                if(checkSolve(A, B, i, ring)) l = i;
            }

            //l=m решение найдено
            if(l==0) return B;

            /*взять подматрицу содержащую m, m-1, .. ,l, l+1 строки   */
            MatrixS subMatrix = A.getSubMatrix(l-1, m, 0, A.colNumb);

            /* сформировать множество J для данной подматрицы*/
            int[] J = addToJ(subMatrix, B, ring);
            /* анализируя множество  J построить решение или доказать что его нет*/

            /*если в J входят все элементы то решений нет*/
            if(J[m-1]!=0) return null;
            /*если в J входит только один элемент*/
            if(J[1]==0){
               Element[][] massSubMatrix =  subMatrix.M;
               Element[] min = twoMinStrSystem(massSubMatrix[0], 1, ring);
               /*y1= x1+min{a1,j + xj} (2<j<m) - (a1,1 +x1)*/
               Element y = B.V[0].add(min[0].subtract(B.V[0].add(massSubMatrix[0][0], ring), ring), ring);
               B.V[0] = y;
               return B;
            }
            /* если решение существует для данной подматрицы
               уменьшаем счетчик k = l
               рассматриваем новую подматрицу
             */

        }


        return null;
    }

    public static MatrixS subInverseMatrix(MatrixS matrix, Ring ring) {

        int column = matrix.colNumb;
        int rows = matrix.size;
        Element temp;

        for (int i = 0; i < rows; i++) {
            for (int j = i; j < column; j++) {
                if (i == j) {
                    matrix.M[i][j] = matrix.M[i][j].inverse(ring);
                } else {
                    temp = matrix.M[i][j].inverse(ring);
                    matrix.M[i][j] = matrix.M[j][i].inverse(ring);
                    matrix.M[j][i] = temp;
                }
            }
        }

        return matrix;
    }

    public static void subInverseVector(VectorS vector, Ring ring) {
        int size = vector.length();
        for (int i = 0; i < size; i++) {
            vector.V[i] = vector.V[i].inverse(ring);
        }
    }

    //(A*b^(-1))^(-1)
    public static VectorS tropicSolveLinearVect(MatrixS A, VectorS B, Ring ring) {
        VectorS answer = new VectorS();
        answer = (VectorS) B.clone();
        subInverseVector(answer, ring);
        System.out.println("-----------------" + B.toString(ring));
        answer = A.multiplyByColumn(answer, ring);
        subInverseVector(answer, ring);
        return answer;
    }
    /**
     * замена местеми k и l столбцы
     * @param matrix
     * @param k
     * @param l
     * @return
     */
    public static MatrixS permutationOfColumns(MatrixS matrix,int k,int l){

        Element temp;
        for(int i=0;i<matrix.size;i++){
           temp = matrix.M[i][k];
           matrix.M[i][k] = matrix.M[i][l];
           matrix.M[i][l] = temp;
        }

        return matrix;
    }

    /**
     * замена местеми k и l елементы
     * @param vector
     * @param l
     * @param k
     * @return
     */
    public static VectorS permutationOfElements(VectorS vector,int l,int k){
        Element temp = vector.V[l];
        vector.V[l] = vector.V[k];
        vector.V[k] = temp;
        return vector;
    }


    public static void main(String[] args) {
        Ring ring1 = new Ring("NumberZMinPlus[x,y]");
        Ring ring = new Ring("Z[x,y]");

        NumberZMinPlus[] mass = new NumberZMinPlus[] {new NumberZMinPlus(new NumberZ(3)), new NumberZMinPlus(new NumberZ(1)), new NumberZMinPlus(new NumberZ(1)), new NumberZMinPlus(new NumberZ(4))};

        // boolean a = FlagAddToJ(mass, 2, ring1);
        //  System.out.println(a);

        Element a11 = new NumberZMinPlus("1", ring);
        Element a12 = new NumberZMinPlus("2", ring);
        Element a13 = new NumberZMinPlus("2", ring);
        Element a14 = new NumberZMinPlus("2", ring);

        Element a21 = new NumberZMinPlus("1", ring);
        Element a22 = new NumberZMinPlus("1", ring);
        Element a23 = new NumberZMinPlus("2", ring);
        Element a24 = new NumberZMinPlus("2", ring);

        Element a31 = new NumberZMinPlus("3", ring);
        Element a32 = new NumberZMinPlus("3", ring);
        Element a33 = new NumberZMinPlus("2", ring);
        Element a34 = new NumberZMinPlus("1", ring);

        Element a41 = new NumberZMinPlus("3", ring);
        Element a42 = new NumberZMinPlus("3", ring);
        Element a43 = new NumberZMinPlus("1", ring);
        Element a44 = new NumberZMinPlus("0", ring);

        Element[][] a = {{a11, a12, a13, a14},
            {a21, a22, a23, a24},
            {a31, a32, a33, a34},
            {a41, a42, a43, a44}};
        MatrixS A = new MatrixS(a, ring);

        Element b1 = new NumberZMinPlus("1", ring);
        Element b2 = new NumberZMinPlus("1", ring);
        Element b3 = new NumberZMinPlus("1", ring);
        Element b4 = new NumberZMinPlus("2", ring);

        Element[] b = {b1, b2, b3, b4};
        VectorS B = new VectorS(b);

        int[] J = addToJ(A, B, ring);
        System.out.println("J=" + Array.toString(J));


        System.out.println("Elements = " + Array.toString(twoMinStrSystem(b, 0, ring), ring));

        System.out.println("matrix ="+A.toString(ring));

       // System.out.println("podmatrix ="+A.getSubMatrix(1, 3, 0, 3).toString(ring));




        System.out.println("sdfsdfsd="+permutationOfColumns(A,0,2));

      //  System.out.println("sdfsdfsd="+permutationOfRows(A,1,0));
        A.permutationOfColumns(2, 0);
        System.out.println("====="+A);

    }
}
