package com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.accumulator;

import com.mathpar.matrix.MatrixS;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.Processor;

import java.util.*;
import java.util.stream.Collectors;

public class MatrixStorage {
    private List<List<MatrixS>> accumulator;
    private List<MatrixS> subMatrices;
    private int currentLine = -1;

    public MatrixStorage(){
        accumulator = new ArrayList<>();
        subMatrices = new LinkedList<>();
    }

    public void add(MatrixS[] matrices){

        if(currentLine != -1){
            accumulator
                    .get(currentLine)
                    .addAll(Arrays.asList(matrices));

            currentLine = -1;
        }else{
            accumulator.add(Arrays.asList(matrices));
        }
    }

    public void add(MatrixS matrix){
        List<MatrixS> list = new LinkedList<>();
        list.add(matrix);
        accumulator.add(list);
        currentLine = accumulator.size() - 1;
    }

    public void add(MatrixS[][] matrices){
        Arrays.stream(matrices).forEach(x -> accumulator.add(Arrays.asList(x)));
    }

    public MatrixS[][] getResult(){
        MatrixS[][] r = new MatrixS[accumulator.size()][];

        for (int i = 0; i < r.length; i++) {
            r[i] = accumulator.get(i).toArray(new MatrixS[0]);
        }

        return r;
    }

}
