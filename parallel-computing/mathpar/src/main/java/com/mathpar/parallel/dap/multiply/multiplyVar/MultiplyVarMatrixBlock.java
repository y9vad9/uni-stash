package com.mathpar.parallel.dap.multiply.multiplyVar;

public class MultiplyVarMatrixBlock {

    private byte data;

    private MultiplyVarMatrixBlock(byte data) {
        this.data = data;
    }

    public byte getData() {
        return data;
    }

    static abstract class Builder<ParentBlock, T, F>{

        protected MultiplyVarMatrix.Builder matrixBuilder;
        protected MultiplyVarMatrix.Quarter quarter;

        private byte data;

        public Builder(MultiplyVarMatrix.Builder matrixBuilder) {
            this(matrixBuilder, null);
        }

        public Builder(MultiplyVarMatrix.Builder matrixBuilder, MultiplyVarMatrix.Quarter quarter) {
            this.matrixBuilder = matrixBuilder;
            this.quarter = quarter;
        }

        public  T transpose() {

            data = MultiplyVarConfig.transpose(data);

            return (T) this;
        }

        public F copyOf(int n) {

            data = MultiplyVarConfig.copyOf(data, n);

            return (F) this;
        }

        public F set() {

            data = MultiplyVarConfig.waitForInput(data);

            return (F) this;
        }

        public MultiplyVarMatrixBlock build() {
            return new MultiplyVarMatrixBlock(data);
        }

        public abstract ParentBlock done();
    }

    static class QuarterBuilder extends Builder<MultiplyVarMatrix.QuarterMatrix, QuarterTransposed, QuarterFinalizer>
            implements QuarterEmpty, QuarterTransposed, QuarterFinalizer{

        private QuarterBuilder(MultiplyVarMatrix.Builder matrixBuilder, MultiplyVarMatrix.Quarter quarter) {
            super(matrixBuilder, quarter);
        }

        public static QuarterEmpty getInstance(MultiplyVarMatrix.Builder matrixBuilder, MultiplyVarMatrix.Quarter quarter){
            return new QuarterBuilder(matrixBuilder, quarter);
        }

        @Override
        public QuarterTransposed transpose() {
            return super.transpose();
        }

        @Override
        public QuarterFinalizer copyOf(int n) {
            return super.copyOf(n);
        }

        @Override
        public QuarterFinalizer set() {
            return super.set();
        }

        @Override
        public MultiplyVarMatrixBlock build() {
            return super.build();
        }

        @Override
        public MultiplyVarMatrix.QuarterMatrix done() {

            MultiplyVarMatrixBlock matrixBlock = build();
            return this.matrixBuilder.setQuarter(this.quarter, matrixBlock);
        }
    }

    static class SimpleBuilder extends Builder<MultiplyVarMatrix.Finalizer, Transposed, Finalizer>
            implements Empty, Transposed, Finalizer {

        private SimpleBuilder(MultiplyVarMatrix.Builder matrixBuilder) {
            super(matrixBuilder);
        }

        public static Empty getInstance(MultiplyVarMatrix.Builder matrixBuilder){
            return new SimpleBuilder(matrixBuilder);
        }

        @Override
        public Transposed transpose() {
            return super.transpose();
        }

        @Override
        public Finalizer copyOf(int n) {
            return super.copyOf(n);
        }

        @Override
        public Finalizer set() {
            return super.set();
        }

        @Override
        public MultiplyVarMatrixBlock build() {
            return super.build();
        }

        @Override
        public MultiplyVarMatrix.Finalizer done() {
            MultiplyVarMatrixBlock result = build();
            return this.matrixBuilder.set(result);
        }
    }



    public interface Empty {
        Transposed transpose();
        Finalizer copyOf(int n);
        Finalizer set();
    }

    public interface Transposed {
        Finalizer copyOf(int n);
        Finalizer set();
    }

    public interface Finalizer{
        MultiplyVarMatrixBlock build();
        MultiplyVarMatrix.Finalizer done();
    }

    public interface QuarterEmpty {
        QuarterTransposed transpose();
        QuarterFinalizer copyOf(int n);
        QuarterFinalizer set();
    }

    public interface QuarterTransposed {
        QuarterFinalizer copyOf(int n);
        QuarterFinalizer set();
    }

    public interface QuarterFinalizer{
        MultiplyVarMatrixBlock build();
        MultiplyVarMatrix.QuarterMatrix done();
    }
}
