package com.mathpar.webWithout.tensors;

public interface TensorFunctions {

    Tensor addTensors(Tensor firstTensor, Tensor secondTensor);

    Tensor subtractTensors(Tensor firstTensor, Tensor secondTensor);

    Tensor multiplyTensors(Tensor firstTensor, Tensor secondTensor);

    // delete the same indexes in tensor on the left OR on the right
    Tensor convolutionTensors(Tensor tensor);
}
