package com.mathpar.parallel.dap.multiply.multiplyVar;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.parallel.dap.core.Amin;
import com.mathpar.parallel.dap.multiply.MultiplyAdd;

import java.util.Arrays;


public class MultiplyVar extends MultiplyAdd {
    private static final MpiLogger LOGGER = MpiLogger.getLogger(MultiplyVar.class);
    private MultiplyVarConfig varConfig;

    public MultiplyVar(MultiplyVarConfig configuration) {
        super();
        this.varConfig = configuration;
        int mainCompAtInputToOutputFunc = 4;

        inputDataLength = 12;

        inData =  new Element[12];
        outData =  new Element[5];

        int numberOfAddComponents = configuration.hasAdditionalData()? 4 : 0;
        resultForOutFunctionLength = mainCompAtInputToOutputFunc + numberOfAddComponents;
        // prepareAdditionalDataLinks(mainCompAtInputToOutputFunc, numberOfAddComponents);

        config = varConfig.getData();
        type = 2;
    }

 /*   private void prepareAdditionalDataLinks(int mainOfInputFuncOutput, int additional){
        int outputFuncPos = arcs.length - 1;
        int[] inputFunctionOutput = arcs[0];
        int[] newOutput = new int[3*(numberOfMainComponentsAtOutput + additional)];

        System.arraycopy(inputFunctionOutput, 0, newOutput, 0, 3*numberOfMainComponentsAtOutput);
        int[] additionalComponentLinks = new int[3*additional];
        for (int i = 0; i < additional; i++) {
            additionalComponentLinks[3*i] = outputFuncPos;
            additionalComponentLinks[3*i+1] = numberOfMainComponents + i;
            additionalComponentLinks[3*i+2] = mainOfInputFuncOutput + i;
        }
        System.arraycopy(additionalComponentLinks, 0, newOutput, 3*numberOfMainComponentsAtOutput, 3*additional);
        arcs[0] = newOutput;
    }*/

    @Override
    public MatrixS[] inputFunction(Element[] input, Amin amin, Ring ring) {

        MatrixS[] result, data;

        data = Arrays.copyOf(input, inData.length, MatrixS[].class);

        result = prepareInputData(data, ring);

        return result;
    }

    public MatrixS[] prepareInputData(MatrixS[] input, Ring ring){

        copyData(input);
        transpose(input);

        return composeMatrices(input, ring);
    }

    public void copyData(Element[] data){

        for (int comp = 0; comp < data.length; comp++) {

            if(varConfig.isCopy(comp)){

                int dataSource = varConfig.dataSource(comp);

                if(data[dataSource] != null)
                    data[comp] = data[dataSource];
            }
        }

    }

    public void transpose(MatrixS[] data){

        for(int i=0; i < data.length; i++){
            boolean needTranspose = varConfig.isTransposed(i);

            if(needTranspose && data[i] != null) {
                data[i] = data[i].transpose();
            }
        }

    }

    private MatrixS[] composeMatrices(MatrixS[] blocks, Ring ring){
        MatrixS[] result = new MatrixS[blocks.length];

        for(int i = 0; i < blocks.length; i+=4){

            MultiplyVarMatrix.Position position = varConfig.getPosition(i);
            MatrixS[] split;

            if(position == MultiplyVarMatrix.Position.QUARTER){

                split = setupQuarterMatrix(blocks, i, ring);

            }else if(position == MultiplyVarMatrix.Position.CENTER){

                split = MatrixS.embedDiagonalCenter(blocks[i], ring).split();

            }else if(position == MultiplyVarMatrix.Position.FULL && blocks[i] != null){

                split = blocks[i].split();

            }else continue;

            System.arraycopy(split, 0, result, i, 4);
        }

        return result;
    }


    private MatrixS[] setupQuarterMatrix(MatrixS[] source, int shift, Ring ring){
        MatrixS[] result = new MatrixS[4];
        MatrixS zero = null;
        MatrixS one = null;

        for(int i=shift; i < shift+4; i++){
            if(source[i] !=null){
                Element element = varConfig.getDiagonalElement(i, ring);
                one = MatrixS.scalarMatrix(source[i].size, element, ring);
                zero = MatrixS.zeroMatrix(source[i].size);
                break;
            }
        }

        for(int i=0; i < 4; i++){

            if(!varConfig.isSet(shift+i)){
                result[i] = (i % 3 == 0) ? one : zero;
            }else{
                result[i] = source[shift + i];
            }
        }

        return result;
    }

  /*  @Override
    public boolean hasFullInputData(){
        return checkInputDataExistenceInRange(0, inputDataLength);
    }

    @Override
    public boolean hasMainInputData(){
        return checkInputDataExistenceInRange(0, numberOfMainComponents);
    }

    @Override
    public boolean hasAdditionalInputData(){
        return checkInputDataExistenceInRange(numberOfMainComponents, inputDataLength, canHaveAdditionalData());
    }
    @Override
    public boolean canHaveAdditionalData(){
        return varConfig.hasAdditionalData();
    }

    private boolean checkInputDataExistenceInRange(int start, int end){
        return checkInputDataExistenceInRange(start, end, true);
    }

    private boolean checkInputDataExistenceInRange(int start, int end, boolean positiveAnswer){

        if (end > inData.length) throw new IllegalArgumentException("start="+start+" end="+end+" data.length="+inData.length);

        for (int i = start; i < end; i++) {

            if((varConfig.isInput(i) && inData[i] == null)
//                    || (varConfig.isCopy(i) && inData[varConfig.dataSource(i)] == null ) // check if source of copy presented
            ){
                return false;
            }
        }
        return positiveAnswer;
    }
*/
    @Override
    public MatrixS[] outputFunction(Element[] input, Ring ring) {
        MatrixS[] result;
        MatrixS[] data = Arrays.copyOf(input, input.length, MatrixS[].class);

        result = applyConfigToOutputData(data, ring);

        return result;
    }

    @Override
    public void sequentialCalc(Ring ring) {
        MatrixS[] result;
        MatrixS[] input = new MatrixS[8];
        MatrixS[] data = Arrays.copyOf(inData, inputDataLength, MatrixS[].class);

        data = prepareInputData(data, ring);

        MatrixS A = MatrixS.join(new MatrixS[]{data[0],data[1],data[2],data[3]});
        MatrixS B = MatrixS.join(new MatrixS[]{data[4],data[5],data[6],data[7]});

        MatrixS AB = A.multiply(B, ring);
        System.arraycopy(AB.split(), 0, input, 0, 4);
        System.arraycopy(data, 8, input, 4, 4);

        result = applyConfigToOutputData(input, ring);

        System.arraycopy(result, 0, outData, 0, result.length);
    }

    private MatrixS[] applyConfigToOutputData(MatrixS[] data, Ring ring){

        MatrixS AB = MatrixS.join(new MatrixS[]{data[0], data[1], data[2], data[3]});

        if(varConfig.isMultiplicationBlockNegated()){
            AB = AB.negate(ring);
        }

        if(varConfig.hasAdditionalData()){
            MatrixS C = MatrixS.join(new MatrixS[]{data[4],data[5],data[6],data[7]});

            if(varConfig.isAdditionBlockNegated()){
                C = C.negate(ring);
            }

            AB = AB.add(C, ring);
        }

        MatrixS[] split = AB.split();

        return new MatrixS[]{AB, split[0], split[1], split[2], split[3]};
    }

    @Override
    public boolean isItLeaf() {
        int index = varConfig.getIndexOfLargestMatrix();
        MatrixS matrix = (MatrixS) inData[index];

        return matrix.size <= leafSize;
    }

    @Override
    public String toString() {
        return super.toString() + "\n"+varConfig;
    }

    public void _setInData(Element[] elements){
        System.arraycopy(elements, 0, inData, 0, inData.length);
    }

    public Element[] _getOutputData(){
        return outData;
    }
}
