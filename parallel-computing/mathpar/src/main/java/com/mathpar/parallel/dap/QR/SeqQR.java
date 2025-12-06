package com.mathpar.parallel.dap.QR;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.Arrays;

public class SeqQR {


    public static MatrixS[] compute(MatrixS matrix, Ring ring){
        MatrixS Q = MatrixS.scalarMatrix(matrix.size, ring.numberONE(), ring);
        MatrixS R = matrix.copy();
        R.sortUpColumns();

        Triplet<MatrixS, MatrixS, Integer> result;
        int col = 0;

        while(col != R.size - 1){
            result = eliminateColUnderDiagonal(Q, R, col, ring);

            Q = result.getValue0();
            R = result.getValue1();
            col = result.getValue2();
        }

        return new MatrixS[]{Q.transpose(), R};
    }


    private static Triplet<MatrixS, MatrixS, Integer> eliminateColUnderDiagonal(MatrixS Q, MatrixS R, int col, Ring ring){
        int lowerIndex, upperIndex, nextCol;
        int[] searchResult = nextIndexOfNonZeroElement(R, col, R.size - 1, ring);
        upperIndex = searchResult[0];
        nextCol = searchResult[1];

        while (upperIndex != col) {
            lowerIndex = upperIndex;
            searchResult = nextIndexOfNonZeroElement(R, col, upperIndex - 1, ring);
            upperIndex = searchResult[0];
            nextCol = Math.min(searchResult[1], nextCol);

            if( lowerIndex == upperIndex) break;

            Pair<RotationMatrixType, MatrixS> rotationMatrix = getRotationMatrix(R, upperIndex, lowerIndex, col, ring);
            RotationMatrixType rotationMatrixType = rotationMatrix.getValue0();
            MatrixS subQ = rotationMatrix.getValue1();

            switch(rotationMatrixType){
                case ONE:
                    continue;
                case PERMUTATION:
                    exchangeRows(Q, upperIndex, lowerIndex, ring);
                    exchangeRows(R, upperIndex, lowerIndex, ring);
                    break;
                case ROTATION:
                    Q = multiplyRotationMatrix(subQ, Q, upperIndex, lowerIndex, ring);
                    R = multiplyRotationMatrix(subQ, R, upperIndex, lowerIndex, ring);
                    break;
            }
            // ensure that after multiplication/permutation columns would not be skipped
            if(R.col[lowerIndex].length != 0){
                nextCol = Math.min(R.col[lowerIndex][0], nextCol);
            }
        }

        return new Triplet<>(Q, R, nextCol);
    }


    private static void exchangeRows(MatrixS source, int upperIndex, int lowerIndex, Ring ring){
        Element[] elements = source.M[lowerIndex];
        source.M[lowerIndex] = Arrays.stream(source.M[upperIndex]).map(el -> el.negate(ring)).toArray(Element[]::new);
        source.M[upperIndex] = elements;


        int[] cols = source.col[upperIndex];
        source.col[upperIndex] = source.col[lowerIndex];
        source.col[lowerIndex] = cols;
    }

    private static int[] nextIndexOfNonZeroElement(MatrixS matrix, int col, int startRow, Ring ring){
        if(col >= matrix.colNumb) throw new IllegalArgumentException("Col argument is out range");
        int minCol = matrix.size -1;

        if(startRow <= col) return new int[]{startRow, minCol};
        int currentCol;
        int[] currentRow;
        int currentIndex = startRow;

        while(currentIndex != col){
            currentRow = matrix.col[currentIndex];
            // skip row if no elements
            if (currentRow.length == 0 ){
                currentIndex--;
                continue;
            }

            currentCol = currentRow[0];

            if(currentCol > col){
                minCol = Math.min(currentCol, minCol);
            }
            else
            if(currentCol == col){
                Element element = matrix.M[currentIndex][0];

//                if(currentRow.length > 1){
//                    minCol = Math.min(currentRow[1], minCol);
//                }

                if(!element.isZero(ring)){
                    break;
                }
            }

            currentIndex--;
        }

        return new int[]{currentIndex, minCol};
    }

    enum RotationMatrixType {
        ONE, PERMUTATION, ROTATION
    }

    private static Pair<RotationMatrixType, MatrixS> getRotationMatrix(MatrixS matrix, int rowA, int rowB, int col, Ring ring){
        Element a = matrix.getElement(rowA, col, ring);
        Element b = matrix.getElement(rowB, col, ring);
        RotationMatrixType type;
        Element sin, cos;
        if(b.isZero(ring)) {
            sin = ring.numberZERO();
            cos = ring.numberONE();
            type = RotationMatrixType.ONE;
        }
        else if (a.isZero(ring)) {
            sin = ring.numberONE().negate(ring);
            cos = ring.numberZERO();
            type = RotationMatrixType.PERMUTATION;
        }
        else {
            Element abSqrt = (a.pow(2, ring).add(b.pow(2, ring), ring)).sqrt(ring);
            sin = b.negate(ring).divide(abSqrt, ring);
            cos = a.divide(abSqrt, ring);
            type = RotationMatrixType.ROTATION;
        }

        return new Pair<>(type, MatrixS.rotationMatrix(sin, cos, ring));
    }

    private static MatrixS multiplyRotationMatrix(MatrixS rotationMatrix, MatrixS matrix, int rowA, int rowB, Ring ring){
        MatrixS rowMatrix = createTwoRowsMatrix(matrix, rowA, rowB);

        MatrixS result = rotationMatrix.multiplySorted(rowMatrix, ring);

        return embedResultOfTwoRowsMatrix(matrix, result, rowA, rowB);
    }

    private static MatrixS createTwoRowsMatrix(MatrixS matrix, int rowA, int rowB){
        Element[][] M = new Element[2][];
        M[0] = matrix.M[rowA];
        M[1] = matrix.M[rowB];

        int[][] cols = new int[2][];
        cols[0] = matrix.col[rowA];
        cols[1] = matrix.col[rowB];

        return new MatrixS(2, matrix.colNumb, M, cols);
    }

    private static MatrixS embedResultOfTwoRowsMatrix(MatrixS matrix, MatrixS resultMatrix, int rowA, int rowB){
        if(resultMatrix.size != 2) throw new IllegalArgumentException("resultMatrix argument must be size 2xN");

        matrix.M[rowA] = resultMatrix.M[0];
        matrix.M[rowB] = resultMatrix.M[1];
        resultMatrix.M[0] = null;
        resultMatrix.M[1] = null;

        matrix.col[rowA] = resultMatrix.col[0];
        matrix.col[rowB] = resultMatrix.col[1];
        resultMatrix.col[0] = null;
        resultMatrix.col[1] = null;

        return matrix;
    }
}
