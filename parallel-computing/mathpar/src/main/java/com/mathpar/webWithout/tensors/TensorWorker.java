package com.mathpar.webWithout.tensors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TensorWorker implements TensorFunctions {

    /**
     * @param firstTensor  об'єкт тензора.
     * @param secondTensor об'єкт тензора.
     * @return Сума двох тензорів.
     */
    @Override
    public Tensor addTensors(Tensor firstTensor, Tensor secondTensor) {

        if (checkIfConditionsAreMet(firstTensor, secondTensor)) {

            firstTensor.name = getNewTensorName(firstTensor.name, secondTensor.name);
            return firstTensor;
        } else {
            throw new RuntimeException("Adding conditions are not met!");
        }
    }


    /**
     * @param firstTensor  об'єкт тензора.
     * @param secondTensor об'єкт тензора.
     * @return Віднімання двох тензорів.
     */
    @Override
    public Tensor subtractTensors(Tensor firstTensor, Tensor secondTensor) {

        if (checkIfConditionsAreMet(firstTensor, secondTensor)) {

            firstTensor.name = getNewTensorName(firstTensor.name, secondTensor.name);
            return firstTensor;
        } else {
            throw new RuntimeException("Subtracting conditions are not met!");
        }
    }


    /**
     * @param firstTensor  об'єкт тензора.
     * @param secondTensor об'єкт тензора.
     * @return Добуток двох тензорів.
     */
    @Override
    public Tensor multiplyTensors(Tensor firstTensor, Tensor secondTensor) {


        Tensor resultTensor = new Tensor(getNewTensorName(firstTensor.name, secondTensor.name));

        // left bottom = 0
        // left top = 1
        // right bottom = 2
        // right top = 3

        if (firstTensor.existingIndexes[0] && secondTensor.existingIndexes[0]) {
            String resultingIndex = Arrays.toString(firstTensor.leftBottomCoefficients) + Arrays.toString(secondTensor.leftBottomCoefficients);
            char[] readyIndexes = clearUpIndexes(resultingIndex);
            resultTensor.setLeftBottomCoefficients(readyIndexes);
        }

        if (firstTensor.existingIndexes[1] && secondTensor.existingIndexes[1]) {
            String resultingIndex = Arrays.toString(firstTensor.leftTopCoefficients) + Arrays.toString(secondTensor.leftTopCoefficients);
            char[] readyIndexes = clearUpIndexes(resultingIndex);
            resultTensor.setLeftTopCoefficients(readyIndexes);
        }

        if (firstTensor.existingIndexes[2] && secondTensor.existingIndexes[2]) {
            String resultingIndex = Arrays.toString(firstTensor.rightBottomCoefficients) + Arrays.toString(secondTensor.rightBottomCoefficients);
            char[] readyIndexes = clearUpIndexes(resultingIndex);
            resultTensor.setRightBottomCoefficients(readyIndexes);
        }

        if (firstTensor.existingIndexes[3] && secondTensor.existingIndexes[3]) {
            String resultingIndex = Arrays.toString(firstTensor.rightTopCoefficients) + Arrays.toString(secondTensor.rightTopCoefficients);
            char[] readyIndexes = clearUpIndexes(resultingIndex);
            resultTensor.setRightTopCoefficients(readyIndexes);
        }

        if (firstTensor.existingIndexes[0] && !secondTensor.existingIndexes[0]) {
            resultTensor.setLeftBottomCoefficients(firstTensor.leftBottomCoefficients);
        }

        if (!firstTensor.existingIndexes[0] && secondTensor.existingIndexes[0]) {
            resultTensor.setLeftBottomCoefficients(secondTensor.leftBottomCoefficients);
        }

        if (firstTensor.existingIndexes[1] && !secondTensor.existingIndexes[1]) {
            resultTensor.setLeftTopCoefficients(firstTensor.leftTopCoefficients);
        }

        if (!firstTensor.existingIndexes[1] && secondTensor.existingIndexes[1]) {
            resultTensor.setLeftTopCoefficients(secondTensor.leftTopCoefficients);
        }

        if (firstTensor.existingIndexes[2] && !secondTensor.existingIndexes[2]) {
            resultTensor.setRightBottomCoefficients(firstTensor.rightBottomCoefficients);
        }

        if (!firstTensor.existingIndexes[2] && secondTensor.existingIndexes[2]) {
            resultTensor.setRightBottomCoefficients(secondTensor.rightBottomCoefficients);
        }

        if (firstTensor.existingIndexes[3] && !secondTensor.existingIndexes[3]) {
            resultTensor.setRightTopCoefficients(firstTensor.rightTopCoefficients);
        }

        if (!firstTensor.existingIndexes[3] && secondTensor.existingIndexes[3]) {
            resultTensor.setRightTopCoefficients(secondTensor.rightTopCoefficients);
        }


        resultTensor = convolutionTensors(resultTensor);
        resultTensor.rank = getNewTensorRank(resultTensor);
        resultTensor.existingIndexes = fetchExistingIndexes(resultTensor);

        if (resultTensor.leftBottomCoefficients == null) {
            resultTensor.setLeftBottomCoefficients(new char[]{});
            resultTensor.setLeftTopCoefficients(new char[]{});
        }

        return resultTensor;
    }


    /**
     * @param resultTensor
     * @return Перевірка заповнення індексів тензора.
     */
    private boolean[] fetchExistingIndexes(Tensor resultTensor) {

        boolean[] newConditions = new boolean[4];

        if (resultTensor.leftBottomCoefficients != null) {
            newConditions[0] = true;
        }


        if (resultTensor.leftTopCoefficients != null) {
            newConditions[1] = true;
        }


        if (resultTensor.rightBottomCoefficients != null) {
            newConditions[2] = true;
        }


        if (resultTensor.rightTopCoefficients != null) {
            newConditions[3] = true;
        }


        return newConditions;
    }


    /**
     * @param resultingIndex
     * @return Очищення індексів тензора.
     */
    private char[] clearUpIndexes(String resultingIndex) {

        List<Character> list = new ArrayList<>();
        char[] array = resultingIndex.toCharArray();

        for (int i = 0; i < array.length; i++) {

            if (array[i] != '[' && array[i] != ',' && array[i] != ' ') {

                list.add(array[i]);
            }
        }

        return arrayListToCharArray(list);
    }


    /**
     * @param resultTensor об'єкт тензора.
     * @return Обчислення рангу новоутвореного тензора.
     */
    private byte getNewTensorRank(Tensor resultTensor) {

        int length = 0;

        if (resultTensor.leftBottomCoefficients != null) {
            length += resultTensor.leftBottomCoefficients.length;
        }
        if (resultTensor.leftTopCoefficients != null) {
            length += resultTensor.leftTopCoefficients.length;
        }
        if (resultTensor.rightBottomCoefficients != null) {
            length += resultTensor.rightBottomCoefficients.length;
        }
        if (resultTensor.rightTopCoefficients != null) {
            length += resultTensor.rightTopCoefficients.length;
        }

        return (byte) length;
    }


    /**
     * @param tensor об'єкт тензора.
     * @return Згортка тензора.
     */
    @Override
    public Tensor convolutionTensors(Tensor tensor) {

        char[] sameIndexes;

        if (tensor.existingIndexes[0] && tensor.existingIndexes[1]) {

            sameIndexes = findSameIndex(tensor.leftTopCoefficients, tensor.leftBottomCoefficients);
            tensor.leftTopCoefficients = formatArray(tensor.leftTopCoefficients, sameIndexes);
            tensor.leftBottomCoefficients = formatArray(tensor.leftBottomCoefficients, sameIndexes);
            tensor.rank = (byte) (tensor.rank - sameIndexes.length * 2);
        }

        if (tensor.existingIndexes[2] && tensor.existingIndexes[3]) {

            sameIndexes = findSameIndex(tensor.rightTopCoefficients, tensor.rightBottomCoefficients);
            tensor.rightTopCoefficients = formatArray(tensor.rightTopCoefficients, sameIndexes);
            tensor.rightBottomCoefficients = formatArray(tensor.rightBottomCoefficients, sameIndexes);
            tensor.rank = (byte) (tensor.rank - getSameIndexesLength(sameIndexes) * 2);
        }


        return tensor;
    }


    /**
     * CONVOLUTION BLOCK
     * Перевірка на наявність однакових індексів для операції згортки.
     */
    private char[] formatArray(char[] coefficients, char[] sameIndexes) {

        List<Character> newIndexes = new ArrayList<Character>();

        for (int i = 0; i < coefficients.length; i++) {

            boolean ifToWrite = true;
            for (int j = 0; j < sameIndexes.length; j++) {

                if (coefficients[i] == sameIndexes[j]) {
                    ifToWrite = false;
                }
            }

            if (ifToWrite) {

                newIndexes.add(coefficients[i]);
            }
        }

        if (newIndexes.size() == 0) {
            return null;
        } else {
            return arrayListToCharArray(newIndexes);
        }
    }


    private int getSameIndexesLength(char[] sameIndexes) {
        int length = 0;

        for (int i = 0; i < sameIndexes.length; i++) {

            if (sameIndexes[i] != 0) {
                length++;
            }
        }

        return length;
    }


    private char[] findSameIndex(char[] topCoefficients, char[] bottomCoefficients) {


        List<Character> sameSymbols = new ArrayList<Character>();

        for (int i = 0; i < topCoefficients.length; i++) {

            char currentChar = topCoefficients[i];
            for (int j = 0; j < bottomCoefficients.length; j++) {

                if (bottomCoefficients[j] == currentChar) {
                    sameSymbols.add(currentChar);
                }

            }
        }

        return arrayListToCharArray(sameSymbols);
    }


    /**
     * ADDITION and SUBTRACTION BLOCK
     */
    private char getNewTensorName(char firstName, char secondName) {

        firstName = (char) (firstName + 1);

        if (firstName != secondName) {
            return firstName;
        } else {
            char secondNameOption = (char) (secondName + 1);
            return secondNameOption;
        }
    }


    private boolean checkIfConditionsAreMet(Tensor firstTensor, Tensor secondTensor) {

        boolean[] conditionsArray = new boolean[4];

        //existing indexes
        // left bottom = 0
        // left top = 1
        // right bottom = 2
        // right top = 3

        if ((firstTensor.existingIndexes[0]) && (secondTensor.existingIndexes[0])) {

            if (Arrays.equals(firstTensor.leftBottomCoefficients, secondTensor.leftBottomCoefficients)) {

                conditionsArray[0] = true;

            } else {

                conditionsArray[0] = false;
            }

        } else {
            conditionsArray[0] = !firstTensor.existingIndexes[0] && !secondTensor.existingIndexes[0];
        }

        if ((firstTensor.existingIndexes[1]) && (secondTensor.existingIndexes[1])) {

            if (Arrays.equals(firstTensor.leftTopCoefficients, secondTensor.leftTopCoefficients)) {

                conditionsArray[1] = true;

            } else {

                conditionsArray[1] = false;
            }

        } else {
            conditionsArray[1] = !firstTensor.existingIndexes[1] && !secondTensor.existingIndexes[1];
        }

        if ((firstTensor.existingIndexes[2]) && (secondTensor.existingIndexes[2])) {

            if (Arrays.equals(firstTensor.rightBottomCoefficients, secondTensor.rightBottomCoefficients)) {

                conditionsArray[2] = true;

            } else {

                conditionsArray[2] = false;
            }

        } else {
            conditionsArray[2] = !firstTensor.existingIndexes[2] && !secondTensor.existingIndexes[2];
        }

        if ((firstTensor.existingIndexes[3]) && (secondTensor.existingIndexes[3])) {

            if (Arrays.equals(firstTensor.rightTopCoefficients, secondTensor.rightTopCoefficients)) {

                conditionsArray[3] = true;

            } else {

                conditionsArray[3] = false;
            }

        } else {
            conditionsArray[3] = !firstTensor.existingIndexes[3] && !secondTensor.existingIndexes[3];
        }

        return conditionsArray[0] && conditionsArray[1] && conditionsArray[2] && conditionsArray[3];
    }


    private char[] arrayListToCharArray(List<Character> list) {

        char[] array = new char[list.size()];

        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }

        return array;
    }
}
