//package com.mathpar.webWithout.tensors;
//
//import static org.junit.Assert.*;
//import static org.junit.Assert.assertArrayEquals;
//import static org.junit.Assert.assertEquals;
//
//import org.junit.Before;
//import org.junit.Test;
//
//public class TensorWorkerTest {
//
//    Tensor firstTensor;
//    Tensor secondTensor;
//    Tensor thirdTensor;
//    Tensor fourthTensor;
//    Tensor fifthTensor;
//
//
//
//    TensorWorker tensorWorker = new TensorWorker();
//
//
//    @Before
//    public void initTensors(){
//
//        firstTensor = new Tensor('C');
//        secondTensor = new Tensor('D');
//        thirdTensor = new Tensor('E');
//        fourthTensor = new Tensor('A');
//        fifthTensor = new Tensor('F');
//
//        char[] TopCoefficientsBasic = {'x','a','m','n','o'};
//        char[] BottomCoefficientsBasic = {'m','o','f','l','k'};
//
//        char[] TopCoefficientsResult = {'x','a','n'};
//        char[] BottomCoefficientsResult = {'f','l','k'};
//
//        char[] ShortTopCoefficients = {'x','m'};
//        char[] ShortBottomCoefficients = {'m'};
//
//        //1
//        firstTensor.setRightTopCoefficients(ShortTopCoefficients);
//        firstTensor.setRightBottomCoefficients(ShortBottomCoefficients);
//        firstTensor.setLeftTopCoefficients(ShortTopCoefficients);
//        firstTensor.setLeftBottomCoefficients(ShortBottomCoefficients);
//
//        //2
//        secondTensor.setRightTopCoefficients(ShortTopCoefficients);
//        secondTensor.setRightBottomCoefficients(ShortBottomCoefficients);
//        secondTensor.setLeftTopCoefficients(ShortTopCoefficients);
//        secondTensor.setLeftBottomCoefficients(ShortBottomCoefficients);
//
//
//        //3
//        thirdTensor.setRightTopCoefficients(ShortTopCoefficients);
//        thirdTensor.setRightBottomCoefficients(ShortBottomCoefficients);
//        thirdTensor.setLeftTopCoefficients(ShortTopCoefficients);
//        thirdTensor.setLeftBottomCoefficients(ShortBottomCoefficients);
//
//
//        //4
//        fourthTensor.setRightTopCoefficients(TopCoefficientsBasic);
//        fourthTensor.setRightBottomCoefficients(BottomCoefficientsBasic);
//        fourthTensor.setLeftTopCoefficients(TopCoefficientsBasic);
//        fourthTensor.setLeftBottomCoefficients(BottomCoefficientsBasic);
//
//
//        //5
//        fifthTensor.setRightTopCoefficients(TopCoefficientsResult);
//        fifthTensor.setRightBottomCoefficients(BottomCoefficientsResult);
//        fifthTensor.setLeftTopCoefficients(TopCoefficientsResult);
//        fifthTensor.setLeftBottomCoefficients(BottomCoefficientsResult);
//
//
//    }
//
//
//    @Test
//    public void addTensors() {
//
//        Tensor resultTensor = tensorWorker.addTensors(firstTensor, secondTensor);
//        if(resultTensor != null){
//            assertEquals(thirdTensor.name, resultTensor.name);
//            assertEquals(thirdTensor.rank, resultTensor.rank);
//            assertArrayEquals(thirdTensor.existingIndexes, resultTensor.existingIndexes);
//        }
//    }
//
//
//    @Test
//    public void subtractTensors() {
//
//        Tensor resultTensor = tensorWorker.subtractTensors(firstTensor, secondTensor);
//        if(resultTensor != null) {
//            assertEquals(thirdTensor.name, resultTensor.name);
//            assertEquals(thirdTensor.rank, resultTensor.rank);
//            assertArrayEquals(thirdTensor.existingIndexes, resultTensor.existingIndexes);
//        }
//    }
//
//
//    @Test
//    public void convolutionTensors() {
//
//        Tensor resultTensor = tensorWorker.convolutionTensors(fourthTensor);
//
//       // assertEquals(fifthTensor.name, resultTensor.name);
//        assertEquals(fifthTensor.rank, resultTensor.rank);
//        assertArrayEquals(fifthTensor.existingIndexes, resultTensor.existingIndexes);
//        /*
//
//        if(resultTensor.existingIndexes[3]) {
//
//            for (int i = 0; i < resultTensor.rightTopCoefficients.length; i++) {
//                if(fifthTensor.rightTopCoefficients[i] != resultTensor.rightTopCoefficients[i]) {
//                    ifValuesEqual = false;
//                }
//            }
//        }
//
//
//        if(resultTensor.existingIndexes[2]) {
//
//            for (int i = 0; i < resultTensor.rightBottomCoefficients.length; i++) {
//                if(fifthTensor.rightBottomCoefficients[i] != resultTensor.rightBottomCoefficients[i]) {
//                    ifValuesEqual = false;
//                }
//            }
//        }
//
//
//        if(resultTensor.existingIndexes[1]) {
//
//            for (int i = 0; i < resultTensor.leftTopCoefficients.length; i++) {
//                if (fifthTensor.leftTopCoefficients[i] != resultTensor.leftTopCoefficients[i]) {
//                    ifValuesEqual = false;
//                }
//            }
//        }
//
//
//        if(resultTensor.existingIndexes[0]) {
//            for (int i = 0; i < resultTensor.leftBottomCoefficients.length; i++) {
//                if(fifthTensor.leftBottomCoefficients[i] != resultTensor.leftBottomCoefficients[i]) {
//                    ifValuesEqual = false;
//                }
//            }
//        }
//
//        assertEquals(true, ifValuesEqual);
//
//        */
//
//    }
//
//
//    @Test
//    public void multiplyTensors() {
//
//        //3
//        thirdTensor.setRightTopCoefficients(new char[]{'x'});
//        thirdTensor.setRightBottomCoefficients(new char[]{'m'});
//        thirdTensor.setLeftTopCoefficients(new char[]{'x'});
//        thirdTensor.setLeftBottomCoefficients(new char[]{'m'});
//
//        //4
//        fourthTensor.setRightTopCoefficients(new char[]{'a'});
//        fourthTensor.setRightBottomCoefficients(new char[]{'b'});
//        fourthTensor.setLeftTopCoefficients(new char[]{'m'});
//        fourthTensor.setLeftBottomCoefficients(new char[]{'x'});
//
//
//        //5
//        Tensor fifthTensor = new Tensor('F');
//        fifthTensor.setRightTopCoefficients(new char[]{'x','a'});
//        fifthTensor.setRightBottomCoefficients(new char[]{'m','b'});
//
//        Tensor resultTensor = tensorWorker.multiplyTensors(thirdTensor, fourthTensor);
//
//        assertEquals(fifthTensor.name, resultTensor.name);
//        assertEquals(fifthTensor.rank, resultTensor.rank);
//        assertArrayEquals(fifthTensor.existingIndexes, resultTensor.existingIndexes);
//
//        Tensor.printTensor(resultTensor);
//        Tensor.printTensor(fifthTensor);
//
//
//    }
//
//}
