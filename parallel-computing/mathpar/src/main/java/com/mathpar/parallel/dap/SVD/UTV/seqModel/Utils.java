package com.mathpar.parallel.dap.SVD.UTV.seqModel;

import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

import java.util.LinkedList;
import java.util.List;

public class Utils {

    public static void putRow(MatrixS matrix, Element[] row, int rowNumber, Ring ring){
        // TODO optimize it!
        for(int col = 0; col < row.length; col++){
            matrix.putElement(row[col], rowNumber, col);
        }
    }

    public static void putColumn(MatrixS matrix, Element[] col, int colNumber, Ring ring){
        // TODO optimize it!
        for (int row = 0; row < col.length; row++) {
            matrix.putElement(col[row], row, colNumber);
        }
    }

    public static MatrixS matrixByRows(Element[] ...rows){
        // TODO optimize it
        int[][] cols = new int[rows.length][rows[0].length];
        for (int row = 0; row < rows.length; row++) {
            for (int col = 0; col < rows[0].length; col++) {
                cols[row][col] = col;
            }
        }
        return new MatrixS(rows.length, rows[0].length, rows, cols);
    }

    public static MatrixS matrixByColumn(Element[] ...cols){
        // TODO optimize it
        Element[][] rows = new Element[cols[0].length][cols.length];
        int[][] c = new int[cols[0].length][cols.length];

        for (int row = 0; row < rows.length; row++) {
            for (int col = 0; col < rows[0].length; col++) {
                rows[row][col] = cols[col][row];
                c[row][col] = col;
            }
        }
        return new MatrixS(rows.length, rows[0].length, rows, c);
    }

    public static Element[] getRow(MatrixS matrix, int i, Ring ring){
        Element[] row = new Element[matrix.colNumb];

        for (int col = 0; col < row.length; col++) {
            row[col] = matrix.getElement(i, col, ring);
        }

        return row;
    }

    public static Element[] getCol(MatrixS matrix, int j, Ring ring){
        Element[] col = new Element[matrix.size];

        for (int row = 0; row < col.length; row++) {
            col[row] = matrix.getElement(row, j, ring);
        }

        return col;
    }

    // Array chunks[N][3] has next structure:
    // chunks[i][0] == M[i][i-1] element
    // chunks[i][1] == matrix with size kxk
    // chunks[i][2] == M[i+k-1][i+k] element
    public static MatrixS gather3DiagonalMatrix(Element[][] chunks, int size, Ring ring){
        MatrixS result;
        Element[][] elements = new Element[size][3];
        int[][] cols = new int[size][3];
        int chunkSize = ((MatrixS)chunks[0][1]).size;

        for (int chunk = 0; chunk < chunks.length; chunk++) {
            MatrixS m = (MatrixS) chunks[chunk][1];
            int currentRow = chunk*chunkSize;

            Pair<Element[], int[]> firstRow = createFirstRow(chunks[chunk], currentRow, ring);
            elements[currentRow] = firstRow.value1;
            cols[currentRow] = firstRow.value2;

            for (int row = 1; row <  chunkSize - 1; row++) {
                for (int col = 0; col < 3; col++) {
                    elements[row + currentRow][col] = m.getElement(row,row + col - 1 , ring);
                    cols[row + currentRow][col] = currentRow + row + col - 1;
                }
            }

            Pair<Element[], int[]> lastRow = createLastRow(chunks[chunk], currentRow + chunkSize - 1, ring);
            elements[currentRow + chunkSize - 1] = lastRow.value1;
            cols[currentRow + chunkSize - 1] = lastRow.value2;

        }
        result = new MatrixS(size, size, elements, cols);

        return result;
    }

    public static MatrixS composeLeftMatrix(int size, MatrixS[][] matrices, Ring ring){
        MatrixS result = MatrixS.scalarMatrix(size, ring.numberONE(), ring);

        for (MatrixS[] matrix : matrices) {
            result = Utils.multiply(matrix, result, size - 1, ring);
        }

        return result;
    }

    public static MatrixS composeRightMatrix(int size, MatrixS[][] matrices, Ring ring){
        MatrixS result = MatrixS.scalarMatrix(size, ring.numberONE(), ring);

        for (MatrixS[] matrix : matrices) {
            result = Utils.multiply(result, matrix, size - 1, ring);
        }

        return result;
    }

    public static MatrixS multiplyRight2x2(MatrixS matrix, MatrixS matrix2x2, int eliminatedCol, Ring ring){
        // TODO optimize it!
        if(matrix2x2.size != 2 || matrix2x2.colNumb != 2) throw new IllegalArgumentException("Second parameter must be matrix 2x2");
        if(eliminatedCol > matrix.colNumb - 1 || eliminatedCol < 0 )
            throw new IllegalArgumentException("Column offset must be in range [1, "+ (matrix.colNumb - 1) + "] Actual = "+eliminatedCol);
        MatrixS result = matrix.copy();

        MatrixS colsMatrix = matrixByColumn(matrix.getCol(eliminatedCol - 1, ring), matrix.getCol(eliminatedCol, ring));

        MatrixS colsResult = colsMatrix.multiply(matrix2x2, ring);

        putColumn(result, colsResult.getCol(0, ring), eliminatedCol - 1, ring);
        putColumn(result, colsResult.getCol(1, ring), eliminatedCol, ring);

        return result;
    }

    public static MatrixS multiplyLeft2x2(MatrixS matrix2x2, MatrixS matrix, int eliminatedRow, Ring ring){
        // TODO optimize it!
        if(matrix2x2.size != 2 || matrix2x2.colNumb != 2) throw new IllegalArgumentException("First parameter must be matrix 2x2");
        if(eliminatedRow > matrix.colNumb - 1 || eliminatedRow < 1 )
            throw new IllegalArgumentException("Row offset must be in range [1, "+ (matrix.size - 1) + "] Actual = "+eliminatedRow);
        MatrixS result = matrix.copy();

        MatrixS rowsMatrix = matrixByRows(matrix.getRow(eliminatedRow - 1, ring), matrix.getRow(eliminatedRow, ring));

        MatrixS rowResult = matrix2x2.multiply(rowsMatrix, ring);

        putRow(result, rowResult.getRow(0, ring), eliminatedRow - 1, ring);
        putRow(result, rowResult.getRow(1, ring), eliminatedRow, ring);

        return result;
    }

    public static MatrixS multiply(MatrixS[] givens2x2Matrices, MatrixS matrix, Ring ring){
        return multiply(givens2x2Matrices, matrix, matrix.size - 1, ring);
    }

    public static MatrixS multiply(MatrixS[] givens2x2Matrices, MatrixS matrix, int startDownRow, Ring ring){
        // TODO optimize it!
        MatrixS result = matrix;
        int row = startDownRow;
        for (MatrixS left: givens2x2Matrices){
            result = multiplyLeft2x2(left, result, row, ring);
            row--;
            if(row < 0) break;
        }
        return result;
    }

    public static MatrixS multiply(MatrixS matrix, MatrixS[] givens2x2Matrices, Ring ring){
        return multiply(matrix, givens2x2Matrices, matrix.colNumb - 1, ring);
    }

    public static MatrixS multiply(MatrixS matrix, MatrixS[] givens2x2Matrices, int startRightCol, Ring ring){
        // TODO optimize it!
        MatrixS result = matrix;
        int col = startRightCol;
        for (MatrixS right: givens2x2Matrices){
            result = multiplyRight2x2(result, right, col, ring);
            col--;
            if(col < 0) break;
        }
        return result;
    }

    public static Element[] multiplyLastRow(MatrixS left, Element[] row, MatrixS matrix, Ring ring){
        Element[] row1 = matrix.getRow(matrix.size - 1, ring);
        MatrixS rowMatrix = Utils.matrixByRows(row1, row);

        MatrixS result = left.multiply(rowMatrix, ring);

        Element[] resultDataRow = result.getRow(0, ring);
        Utils.putRow(matrix, resultDataRow, matrix.size -1, ring);
        return result.getRow(1, ring);
    }

    public static Element[] multiplyLastColumn(MatrixS matrix, Element[] column, MatrixS right2x2, Ring ring){
        Element[] col1 = matrix.getCol(matrix.colNumb -1, ring);
        MatrixS colMatrix = Utils.matrixByColumn(col1, column);

        MatrixS result = colMatrix.multiply(right2x2, ring);

        Utils.putColumn(matrix, result.getCol(0, ring), matrix.colNumb -1, ring);
        return result.getCol(1, ring);
    }

    private static Pair<Element[], int[]> createFirstRow(Element[] chunk,int row, Ring ring){
        Element[] elements;
        int[] cols;
        Element left = chunk[0];
        int shift = 0;
        MatrixS matrix = (MatrixS) chunk[1];

        if(left == null){
            elements = new Element[2];
            cols = new int[2];
        }else{
            elements = new Element[3];
            cols = new int[3];
            elements[0] = left;
            cols[0] = row-1;
            shift = 1;
        }
        elements[shift] = matrix.getElement(0, 0, ring);
        elements[shift + 1] = matrix.getElement(0, 1, ring);

        cols[shift] = row;
        cols[shift + 1] = row + 1;

        return new Pair<>(elements, cols);
    }

    private static Pair<Element[], int[]> createLastRow(Element[] chunk,int row, Ring ring){
        Element[] elements;
        int[] cols;
        Element right = chunk[2];
        MatrixS matrix = (MatrixS) chunk[1];
        int chunkSize = matrix.size;

        if(right == null){
            elements = new Element[2];
            cols = new int[2];
        }else{
            elements = new Element[3];
            cols = new int[3];
            elements[2] = right;
            cols[2] = row + 1;
        }
        elements[0] = matrix.getElement(chunkSize - 1, chunkSize - 2, ring);
        elements[1] = matrix.getElement(chunkSize - 1, chunkSize - 1, ring);

        cols[0] = row - 1;
        cols[1] = row;

        return new Pair<>(elements, cols);
    }


//    public MatrixS genRowMatrix(int blocks, int blockSize, int elementaryBlockNum, Ring ring){
//        MatrixS[] matrices = genMatrixLine(blocks, blockSize, elementaryBlockNum, ring);
//
//        return joinInRowMatrix(matrices, ring);
//    }
//
//
//    public MatrixS genColMatrix(int blocks, int blockSize, int elementaryBlockNum, Ring ring){
//        MatrixS[] matrices = genMatrixLine(blocks, blockSize, elementaryBlockNum, ring);
//
//        return joinInColMatrix(matrices, ring);
//    }

    public static MatrixS rowLineMatrix(int blocks, int blockSize, int elementaryBlockNum, Ring ring){
        Element[][] elements = new Element[blockSize][1];
        int[][] cols = new int[blockSize][1];
        Element one = ring.numberONE();

        for (int row = 0; row < elements.length; row++) {
            elements[row][0] = one;
            cols[row][0] = elementaryBlockNum*blockSize + row;
        }

        return new MatrixS(blockSize, blocks*blockSize, elements, cols);
    }

    public static MatrixS colLineMatrix(int blocks, int blockSize, int elementaryBlockNum, Ring ring){

        Element[][] elements = new Element[blockSize*blocks][0];
        int[][] cols = new int[blockSize*blocks][0];
        Element one = ring.numberONE();
        int offset = elementaryBlockNum*blockSize;
        for (int row = 0; row < blockSize; row++) {
            elements[offset + row] = new Element[]{one};
            cols[offset + row] = new int[]{row};
        }

        return new MatrixS(blocks*blockSize, blockSize, elements, cols);
    }

    public static MatrixS collectByRowLines(MatrixS[] lines){
        int lineSize = lines[0].size;
        int size = lines.length * lineSize;
        Element[][] elements = new Element[size][];
        int[][] cols = new int[size][];

        for(int row = 0; row < size; row++){
            Element[] elArray = lines[row/lineSize].M[row%lineSize];
            elements[row] = new Element[elArray.length];
            System.arraycopy(elArray, 0, elements[row], 0, elArray.length);


            int[] colArray = lines[row/lineSize].col[row%lineSize];
            cols[row] = new int[colArray.length];
            System.arraycopy(colArray, 0, cols[row], 0, colArray.length);
        }


        return new MatrixS(size, size, elements, cols);
    }

    public static MatrixS collectByColLines(MatrixS[] lines){
        int size = lines[0].size;
        int blockSize = lines[0].colNumb;
        Element[][] elements = new Element[size][];
        int[][] cols = new int[size][];

        for (int row = 0; row < size; row++) {
            int elemSize = 0;

            for (MatrixS line : lines) {
                elemSize += line.M[row].length;
            }

            elements[row] = new Element[elemSize];
            cols[row] = new int[elemSize];

            int i = 0;
            for (int block = 0; block < lines.length; block++) {
                int[] colsOfRow = lines[block].col[row];

                for (int col = 0; col < colsOfRow.length; col++, i++) {
                    elements[row][i] = lines[block].M[row][col];
                    cols[row][i] = blockSize*block + colsOfRow[col];
                }
            }
        }


        return new MatrixS(size, size, elements, cols);
    }

//    public MatrixS joinInRowMatrix(MatrixS[] blocks, Ring ring){
//        return new MatrixS();
//    }
//
//    public MatrixS joinInColMatrix(MatrixS[] blocks, Ring ring){
//        return new MatrixS();
//    }




    private static class Pair<V1, V2>{
        V1 value1;
        V2 value2;

        Pair(V1 value1, V2 value2) {
            this.value1 = value1;
            this.value2 = value2;
        }
    }

    public static void main(String[] args){
//        System.out.println(Utils.rowLineMatrix(4, 3, 3, new Ring("R64[]")));
//        MatrixS colsMatrix = Utils.colLineMatrix(4, 3, 1, new Ring("R64[]"));
//        System.out.println(colsMatrix);
        Ring ring = new Ring("R64[]");

        MatrixS[] one = new MatrixS[]{
                Utils.colLineMatrix(4, 3, 0, ring),
                Utils.colLineMatrix(4, 3, 1, ring),
                Utils.colLineMatrix(4, 3, 2, ring),
                Utils.colLineMatrix(4, 3, 3, ring)
        };

        System.out.println(Utils.collectByColLines(one));
    }
}
