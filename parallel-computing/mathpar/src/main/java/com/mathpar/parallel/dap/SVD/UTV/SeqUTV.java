package com.mathpar.parallel.dap.SVD.UTV;


import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.Utils;
import org.javatuples.Pair;
import org.javatuples.Triplet;

public class SeqUTV {


    public static Triplet<MatrixS[][], MatrixS, MatrixS[][]> computeExtendedResult(MatrixS matrix, Ring ring){
        Triplet<MatrixS[], MatrixS, MatrixS[]> tempUTV;
        MatrixS[][] U = new MatrixS[matrix.size - 2][];
        MatrixS T = matrix;
        MatrixS[][] V = new MatrixS[matrix.size - 2][];

        for(int iteration = 0; iteration < matrix.size - 2; iteration++){

            tempUTV = eliminateRowAndColumnExtendedResult(T, iteration, ring);

            U[iteration] = tempUTV.getValue0();
            T = tempUTV.getValue1();
            V[iteration] = tempUTV.getValue2();
        }

        return new Triplet<>(U, T, V);
    }

    public static MatrixS[] compute(MatrixS matrix, Ring ring){
        MatrixS U, T, V;
        Triplet<MatrixS[][], MatrixS, MatrixS[][]> UTV = computeExtendedResult(matrix, ring);

        U = Utils.composeLeftMatrix(matrix.size, UTV.getValue0(), ring);
        T = UTV.getValue1();
        V = Utils.composeRightMatrix(matrix.size, UTV.getValue2(), ring);

        return new MatrixS[]{U, T, V};
    }



    public static Triplet<MatrixS[], MatrixS, MatrixS[]> eliminateRowAndColumnExtendedResult(MatrixS matrix, int iteration, Ring ring){

        Pair<MatrixS[], MatrixS> UT = eliminateColumnExtendedResult(matrix, iteration,  iteration+2, ring);
        Pair<MatrixS, MatrixS[]> TV = eliminateRowExtendedResult(UT.getValue1(), iteration, iteration+2, ring);

        return new Triplet<>(UT.getValue0(), TV.getValue0(), TV.getValue1());
    }

    public static MatrixS[] eliminateRowAndColumn(MatrixS matrix, int iteration, Ring ring){

        MatrixS[] UT = eliminateColumn(matrix, iteration, iteration+2, ring);
        MatrixS[] TV = eliminateRow(UT[1], iteration, iteration+2, ring);

        return new MatrixS[]{UT[0], TV[0], TV[1]};
    }

    public static Pair<MatrixS[], MatrixS> eliminateColumnExtendedResult(MatrixS matrix, int col, int stopTopShift, Ring ring){
        MatrixS T = matrix;
        MatrixS[] arrayU = new MatrixS[matrix.size - stopTopShift];

        for(int startPos = matrix.size - 1; startPos >= stopTopShift; startPos--){
            MatrixS tempU = getRotationMatrixLeft(T, startPos, col, ring);

            arrayU[matrix.size - startPos - 1] = tempU;
            T = Utils.multiplyLeft2x2(tempU, T, startPos, ring);
        }

        return new Pair<>(arrayU, T);
    }

    public static MatrixS[] eliminateColumn(MatrixS matrix, int col, int stopTopShift, Ring ring){
        Pair<MatrixS[], MatrixS> result = eliminateColumnExtendedResult(matrix, col, stopTopShift, ring);

        MatrixS U = Utils.composeLeftMatrix(matrix.size, new MatrixS[][]{result.getValue0()}, ring);
        MatrixS T = result.getValue1();

        return new MatrixS[]{U, T};
    }

    public static Pair<MatrixS, MatrixS[]> eliminateRowExtendedResult(MatrixS matrix, int row, int stopLeftShift, Ring ring){
        MatrixS T = matrix;
        MatrixS[] arrayV = new MatrixS[T.size - stopLeftShift];

        for(int startPos = T.size - 1; startPos >= stopLeftShift; startPos--){
            MatrixS tempV = getRotationMatrixRight(T, row, startPos, ring);

            arrayV[T.size - 1 - startPos] = tempV;
            T = Utils.multiplyRight2x2(T, tempV, startPos, ring);
        }

        return new Pair<>(T, arrayV);
    }

    public static MatrixS[] eliminateRow(MatrixS matrix, int row, int stopLeftShift, Ring ring){
        Pair<MatrixS, MatrixS[]> result = eliminateRowExtendedResult(matrix, row, stopLeftShift, ring);

        MatrixS T = result.getValue0();
        MatrixS V = Utils.composeRightMatrix(matrix.size, new MatrixS[][]{result.getValue1()}, ring);

        return new MatrixS[]{T, V};
    }

    private static MatrixS getRotationMatrixLeft(MatrixS matrix, int row, int col, Ring ring){
        Element a = matrix.getElement(row - 1, col, ring);
        Element b = matrix.getElement(row, col, ring);

        MatrixS tempU = rotationLeft(a, b, ring);

//        return MatrixS.embedDiagonal(tempU, matrix.size, row -1, ring);
        return tempU;
    }

    public static MatrixS rotationLeft(Element a, Element b, Ring ring){
        Element abSqrt = (a.pow(2, ring).add(b.pow(2, ring), ring)).sqrt(ring);
        Element sin = b.negate(ring).divide(abSqrt, ring);
        Element cos = a.divide(abSqrt, ring);
        return  MatrixS.rotationMatrix(sin, cos, ring);
    }

    private static MatrixS getRotationMatrixRight(MatrixS matrix, int row, int col, Ring ring){
        Element c = matrix.getElement(row, col - 1, ring);
        Element d = matrix.getElement(row, col, ring);

        MatrixS tempV = rotationRight(c, d, ring);

//        return MatrixS.embedDiagonal(tempV, matrix.size, col -1, ring);
        return tempV;
    }

    public static MatrixS rotationRight(Element a, Element b, Ring ring){

        Element abSqrt = (a.pow(2, ring).add(b.pow(2, ring), ring)).sqrt(ring);
        Element sin = b.divide(abSqrt, ring);
        Element cos = a.divide(abSqrt, ring);
        return  MatrixS.rotationMatrix(sin, cos, ring);
    }

}
