package com.mathpar.webWithout.tensors;

import java.util.Arrays;
import java.util.Objects;

public class Tensor {

    //Name is just one big letter from english alphabet
    char name;

    //rank is the number of all indexes in tensor symbolic display way
    byte rank;

    // left bottom = 0
    // left top = 1
    // right bottom = 2
    // right top = 3
    boolean[] existingIndexes = new boolean[4];

    //First array on the each side is the bottom coefficients
    //Second array on the each side is the top coefficients
    char[] leftBottomCoefficients;
    char[] leftTopCoefficients;
    char[] rightBottomCoefficients;
    char[] rightTopCoefficients;


    public Tensor(char name) {
        this.name = name;
    }


    public void setLeftBottomCoefficients(char[] leftBottomCoefficients) {
        this.leftBottomCoefficients = leftBottomCoefficients;
        if(leftBottomCoefficients == null){
            existingIndexes[0] = false;
        }else{
            existingIndexes[0] = true;
            rank = (byte) (rank + Objects.requireNonNull(leftBottomCoefficients).length);
        }

    }


    public void setLeftTopCoefficients(char[] leftTopCoefficients) {
        this.leftTopCoefficients = leftTopCoefficients;

        if(leftTopCoefficients == null){
            existingIndexes[1] = false;
        }else{
            existingIndexes[1] = true;
            rank = (byte) (rank + leftTopCoefficients.length);
        }

    }


    public void setRightBottomCoefficients(char[] rightBottomCoefficients) {
        this.rightBottomCoefficients = rightBottomCoefficients;

        if(rightBottomCoefficients == null){
            existingIndexes[2] = false;
        }else{
            existingIndexes[2] = true;
            rank = (byte) (rank + rightBottomCoefficients.length);
        }
    }


    public void setRightTopCoefficients(char[] rightTopCoefficients) {
        this.rightTopCoefficients = rightTopCoefficients;

        if(rightTopCoefficients == null){
            existingIndexes[3] = false;
        }else{
            existingIndexes[3] = true;
            rank = (byte) (rank + rightTopCoefficients.length);
        }

    }


    public static void printTensor(Tensor tensor){

        System.out.println("\n");

        //Every tensor has  it's name and rank
        System.out.println("The tensor has a name:" + tensor.name);
        System.out.println("The rank of tensor is: " + tensor.rank);

        //left bottom index
        if(tensor.existingIndexes[0]){

            System.out.println("Left bottom indexes:" + Arrays.toString(tensor.leftBottomCoefficients));
        }

        //left top index
        if(tensor.existingIndexes[1]) {
            System.out.println("Left top indexes:" + Arrays.toString(tensor.leftTopCoefficients));
        }


        //right bottom index
        if(tensor.existingIndexes[2]){
            System.out.println("Right bottom indexes:" + Arrays.toString(tensor.rightBottomCoefficients));
        }

        //right top index
        if(tensor.existingIndexes[3]) {
            System.out.println("Right top indexes:" + Arrays.toString(tensor.rightTopCoefficients));
        }

        System.out.println("\n");
    }
}
