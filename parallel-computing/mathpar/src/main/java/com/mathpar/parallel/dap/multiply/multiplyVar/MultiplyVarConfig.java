package com.mathpar.parallel.dap.multiply.multiplyVar;

import com.mathpar.log.MpiLogger;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;

import java.io.Serializable;

public class MultiplyVarConfig implements DropConfig<MatrixS>, Serializable {

    private static final MpiLogger LOGGER = MpiLogger.getLogger(MultiplyVarConfig.class);

    public static final int INPUT = 0xe;
    public static final int NONE = -1;
    private static final int NUMBER_OF_MAIN_COMPONENTS = 8;
    private byte[] config;

    public MultiplyVarConfig(byte[] config) {

        this.config = config;

    }

    @Override
    public byte[] getData() {
        return config;
    }

    public boolean isMultiplicationBlockNegated(){
        return isNegated(0);
    }

    public boolean isAdditionBlockNegated(){
        return isNegated(1);
    }

    public boolean hasAdditionalData(){
        boolean isSet = false;

        for (int i = 8; i < 12; i++) {
            if (isSet(i))
                return true;
        }

        return isSet;
    }

    private boolean isNegated(int block){
        return config[12 + block] == 1;
    }

    public int dataSource(int inputIndex){

        return (config[inputIndex] & 0xf) - 1;
    }

    public boolean isInput(int inputIndex){
        return dataSource(inputIndex) == INPUT;
    }

    public boolean isCopy(int inputIndex){
        int source = dataSource(inputIndex);
        return source != INPUT && source != NONE;
    }

    public boolean isTransposed(int inputIndex){
        byte data = config[inputIndex];
        return transpose(data) == data;
    }

    public boolean isSet(int inputIndex){
        return dataSource(inputIndex) != NONE;
    }

    static boolean isTwoBlocksColOrRow(byte[] matrix){
        int position = (matrix[0] & 0xff) >> 6;

        MultiplyVarMatrix.Position value =  MultiplyVarMatrix.Position.values()[position];
        if(value != MultiplyVarMatrix.Position.QUARTER) return false;

        boolean[] set = new boolean[4];
        for(int i = 0; i < 4; i++){
            int source = (matrix[i] & 0xf) - 1;
            set[i] = source != NONE;
        }

        return (set[0] && set[1] && !set[2] && !set[3] || !set[0] && !set[1] && set[2] && set[3]
                || !set[0] && set[1] && !set[2] && set[3] || set[0] && !set[1] && set[2] && !set[3]);
    }
    static byte transpose(byte data){
        return (byte) (data | (1 << 4));
    }

    static byte waitForInput(byte data){
        return (byte) (data | 0xf);
    }

    static byte copyOf(byte data, int source){
        if (source < 0 || source > 11) throw new IllegalArgumentException("Only [0...11] values available. Actual source="+source);
        return (byte) ((data & 0xf0) | (source + 1));
    }

    static byte setDiagonalElement(byte data, int element){
        byte result = data;

        if(element == 1){
            result |= 1 << 5;
        }else{
            result &= ~(1 << 5);
        }

        return result;
    }

    public Element getDiagonalElement(int inputIndex, Ring ring){
        byte data = config[inputIndex];
        return data == setDiagonalElement(data, 1) ? ring.numberONE() : ring.numberZERO();
    }

    static byte setPosition(byte data, int position){
        return (byte) ((data & 0b00111111) | (position << 6));
    }


    public MultiplyVarMatrix.Position getPosition(int inputIndex){
        int position = (config[inputIndex] & 0xff) >> 6;
        return MultiplyVarMatrix.Position.values()[position];
    }

    public int getIndexOfLargestMatrix(){
        int index = 0;
        for(int matrix = 0; matrix < NUMBER_OF_MAIN_COMPONENTS; matrix+=4){
            MultiplyVarMatrix.Position position = getPosition(matrix);
            if( position == MultiplyVarMatrix.Position.FULL){
                return matrix;
            }
        }

        MultiplyVarMatrix.Position position = getPosition(index);
        if(position != MultiplyVarMatrix.Position.CENTER){
            for(int i = index; i < index + 4; i++){
                if(isSet(i)){
                    return i;
                }
            }
        }

        return index;
    }

    public static class Builder implements EmptyConfiguration, MultiplicationBlock,
            NegatedMultiplicationBlock, MultiplicationBlockSecondMatrix, AdditionBlock, NegatedAdditionBlock, Finalizer, MultiplicationBlockFinalizer {
        private byte[] data;

        private Builder(){
            data = new byte[14];
        }

        public static EmptyConfiguration startWith(){
            return new Builder();
        }


        @Override
        public MultiplicationBlock multiplicationBlock() {
            return this;
        }

        @Override
        public NegatedAdditionBlock negate() {
            data[13] = 1;
            return this;
        }

        @Override
        public NegatedMultiplicationBlock negateMultiplication() {
            data[12] = 1;
            return this;
        }

        @Override
        public AdditionBlock additionBlock() {
            return this;
        }

        @Override
        public MultiplyVarConfig build() {
            return new MultiplyVarConfig(data);
        }
        @Override
        public Finalizer set(MultiplyVarMatrix matrixBlock) {
            setMatrix(2, matrixBlock);
            return this;
        }
        @Override
        public MultiplicationBlockSecondMatrix setFirst(MultiplyVarMatrix matrixBlock) {
            setMatrix(0, matrixBlock);
            return this;
        }
        @Override
        public MultiplicationBlockFinalizer setSecond(MultiplyVarMatrix matrixBlock) {
            setMatrix(1, matrixBlock);
            return this;
        }


        private void setMatrix(int matrixShift, MultiplyVarMatrix matrix){
            int shift = matrixShift * 4; // four blocks for each matrix
            byte[] matrixData = matrix.getData();
            System.arraycopy(matrixData, 0, data, shift, 4);
        }
    }

    public interface EmptyConfiguration{
        MultiplicationBlock multiplicationBlock();
    }

    public interface MultiplicationBlock{
        NegatedMultiplicationBlock negateMultiplication();
        MultiplicationBlockSecondMatrix setFirst(MultiplyVarMatrix matrixBlock);
    }

    public interface NegatedMultiplicationBlock{
        MultiplicationBlockSecondMatrix setFirst(MultiplyVarMatrix matrixBlock);
    }

    public interface MultiplicationBlockSecondMatrix{
        MultiplicationBlockFinalizer setSecond(MultiplyVarMatrix matrixBlock);
    }

    public interface AdditionBlock{
        NegatedAdditionBlock negate();
        Finalizer set(MultiplyVarMatrix matrixBlock);
    }

    public interface NegatedAdditionBlock{
        Finalizer set(MultiplyVarMatrix matrixBlock);
    }

    public interface Finalizer{
        MultiplyVarConfig build();
    }

    public interface MultiplicationBlockFinalizer{
        AdditionBlock additionBlock();
        MultiplyVarConfig build();
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean hasAdd = hasAdditionalData();
        char[] names = _names();
        char[][] view = _createView(hasAdd);

        _putSigns(view);

        int[] matrixStart = new int[]{4, 14, 26};


        for(int matrix = 0; matrix < 12; matrix+=4){

            if(!hasAdd && matrix > 7)
                continue;

            int startCol = matrixStart[matrix/4];
            MultiplyVarMatrix.Position position = getPosition(matrix);

            if(position == MultiplyVarMatrix.Position.QUARTER){

                _setQuarter(view, matrix, startCol, names);

            } else {

                view[3][startCol + 3] = _matrixLabel(matrix, names);

                if(isTransposed(matrix))
                    _setTransposeLabel(view, 3, startCol + 3);

                if(position == MultiplyVarMatrix.Position.CENTER)
                    for (int row = 0; row < view.length; row++)
                        if(row > 4 || row < 2)
                            view[row][startCol + row] = _elementLabel(matrix);


            }

        }


        for (char[] line: view) {
            sb.append(line);
            sb.append('\n');
        }

        return sb.toString();
    }

    private void _setTransposeLabel(char[][] view, int row, int col){
        view[row - 1][col + 1] = 'T';
    }

    private char _matrixLabel(int index, char[] names){
        int source = dataSource(index);
        char result = ' ';
        if(source == INPUT)
            result = names[index];
        else if(source != NONE) {
            result = names[source];
        }

        return result;
    }

    private char _elementLabel(int index){
        int elem = (config[index] & (1 << 5) ) >> 5;

        return elem == 1 ? '1' : '0';
    }

    private char[] _names(){
        int uniqueMatrix = 0;
        char[] names = new char[12];
        for (int block = 0; block < 12; block++) {
            int source = dataSource(block);

            if(source == INPUT)
                names[block] = (char) (65 + (uniqueMatrix++));
        }

        return names;
    }

    private char[][] _createView(boolean withAdditional){
        char[][] view = new char[7][];
        for (int i = 0; i < view.length; i++) {
            view[i] = new char[34];
            for (int j = 0; j < view[i].length; j++) {
                view[i][j] = ' ';
            }
        }

        int[] lines = new int[]{3, 11, 13, 21, 25, 33};
        for (int row = 0; row < view.length; row++) {

            for (int col = 0; col < lines.length; col++) {

                if(!withAdditional && col > 3)
                    continue;

                view[row][lines[col]] = '|';
            }
        }

        return view;
    }

    private void _putSigns(char[][] view){
        if(isMultiplicationBlockNegated()){
            view[3][1] = '-';
        }

        if(hasAdditionalData())
            if(isAdditionBlockNegated()){
                view[3][23] = '-';
            }else{
                view[3][23] = '+';
            }
    }

    private void _setQuarter(char[][] view, int matrix, int startCol, char[] names){

        for(int quarter = 0; quarter < 4; quarter++){

            if(isSet(matrix+quarter)){
                int row = quarter > 1 ? 5 : 1;
                int col = startCol + (quarter % 2 == 0 ? 1 : 5);
                view[row][col] = _matrixLabel(matrix+quarter, names);

                if(isTransposed(matrix))
                    _setTransposeLabel(view, row, col);

            } else if (quarter % 3 == 0){
                int start = 0;
                if(quarter == 3){
                    start = 4;
                }

                for(int r = start; r < start + 3; r++){
                    view[r][startCol + r] = _elementLabel(matrix+quarter);
                }
            }
        }

    }
}
