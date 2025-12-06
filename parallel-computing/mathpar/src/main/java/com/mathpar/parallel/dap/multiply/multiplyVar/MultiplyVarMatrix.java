package com.mathpar.parallel.dap.multiply.multiplyVar;

import com.mathpar.log.MpiLogger;

public class MultiplyVarMatrix {

    private static final MpiLogger LOGGER = MpiLogger.getLogger(MultiplyVarMatrix.class);

    private byte[] data;

    private MultiplyVarMatrix(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public static void main(String[] args){
        MultiplyVarMatrix m =  MultiplyVarMatrix.Builder.newBuilder().fillDiagonal(FillElement.ONE)
                .addQuarter(Quarter.I).set().done()
                .addQuarter(Quarter.II).copyOf(1).done()
                .build();
    }

    public static class Builder implements EmptyMatrixBlock, DiagonalFilledMatrixBlock, QuarterMatrix, Finalizer{
        protected byte[] data;
        private Position position;
        private FillElement fillElement;
        private FillElement defaultDiagonalElement = FillElement.ONE;

        private Builder() {
            this.data = new byte[4];
        }

        public static EmptyMatrixBlock newBuilder(){
            return new Builder();
        }


        @Override
        public DiagonalFilledMatrixBlock fillDiagonal(FillElement element) {
            fillElement = element;
            return this;
        }

        @Override
        public MultiplyVarMatrixBlock.QuarterEmpty addQuarter(Quarter quarter) {
            position = Position.QUARTER;
            return MultiplyVarMatrixBlock.QuarterBuilder.getInstance(this, quarter);
        }

        @Override
        public MultiplyVarMatrixBlock.Empty addFull() {
            position = Position.FULL;
            return MultiplyVarMatrixBlock.SimpleBuilder.getInstance(this);
        }

        @Override
        public MultiplyVarMatrixBlock.Empty addCenter() {
            position = Position.CENTER;
            return MultiplyVarMatrixBlock.SimpleBuilder.getInstance(this);
        }

        @Override
        public MultiplyVarMatrix build() {
            setPosition();
            setDiagonalElement();

            return new MultiplyVarMatrix(data);
        }

        private void setDiagonalElement(){


            if(position == Position.FULL){
                fillElement = FillElement.ZERO;
            }

            boolean isColumnOrRowLikeMatrix = MultiplyVarConfig.isTwoBlocksColOrRow(data);
            if(position == Position.QUARTER && fillElement == null && isColumnOrRowLikeMatrix){
                fillElement = FillElement.ZERO;
            }

            if(fillElement == null){
                fillElement = defaultDiagonalElement;
            }

            int fill = fillElement.ordinal();

            for(int i = 0; i < data.length; i++){
                data[i] = MultiplyVarConfig.setDiagonalElement(data[i], fill);
            }

        }

        private void setPosition(){
            int value = position.ordinal();

            for(int i = 0; i < data.length; i++){
                data[i] = MultiplyVarConfig.setPosition(data[i], value);
            }

        }

        public Finalizer set(MultiplyVarMatrixBlock matrixBlock){

            data[0] = matrixBlock.getData();

            return this;
        }

        public QuarterMatrix setQuarter(Quarter quarter, MultiplyVarMatrixBlock innerBlock){

            byte block = innerBlock.getData();
            int position = quarter.ordinal();

            data[position] = block;

            return this;
        }

    }


    public interface EmptyMatrixBlock{
        DiagonalFilledMatrixBlock fillDiagonal(FillElement element);
        MultiplyVarMatrixBlock.QuarterEmpty addQuarter(Quarter quarter);
        MultiplyVarMatrixBlock.Empty addFull();
        MultiplyVarMatrixBlock.Empty addCenter();
    }

    public interface DiagonalFilledMatrixBlock{
        MultiplyVarMatrixBlock.QuarterEmpty addQuarter(Quarter quarter);
        MultiplyVarMatrixBlock.Empty addFull();
        MultiplyVarMatrixBlock.Empty addCenter();
    }

    public interface QuarterMatrix {
        MultiplyVarMatrixBlock.QuarterEmpty addQuarter(Quarter quarter);
        MultiplyVarMatrix build();
    }

    public interface Finalizer{
        MultiplyVarMatrix build();
    }


    public enum Quarter{
        I, II, III, IV
    }

    public enum FillElement{
        ZERO, ONE
    }

    enum Position{
        FULL, CENTER, QUARTER
    }

}
