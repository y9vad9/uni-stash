package com.mathpar.webWithout.tensors;

/**
 * JUST FOR TEST
 */
public class TensorTest {

    public void runTest() {
        TensorParser tensorParser = new TensorParser();
        System.out.println("running test:");
        //tensorParser.buildTensorFromString("A_{x}^{nwqe}");
        //System.out.println(tensorParser.validateString("A_{x}^{nwqe} * B_{x}^{saa}"));
        //tensorParser.getStringPresentationOfTensors("A_{x}^{nwqe} * B_{x}^{saa} * C_{x}^{a}");
        System.out.println(tensorParser.fetchOperations("A_{x}^{nwqe} + B_{x}^{saa} * C_{x}^{a} * D_{x}^{a}"));
    }
}
