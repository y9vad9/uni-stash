package com.mathpar.parallel.dap.SVD.UTV.seqModel;

import com.mathpar.matrix.MatrixS;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.Accumulator;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.DataInitializer;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.processor.RunnableProcessor;
import com.mathpar.parallel.dap.SVD.UTV.seqModel.setup.ProcessorsSetup;

import java.util.List;


public class Dispatcher {
    private ProcessorsSetup setup;
    private List<RunnableProcessor> processors;
    private MatrixS source;

    private int T_POSITION;

    public Dispatcher(ProcessorsSetup setup, MatrixS source) {
        this.setup = setup;
        this.source = source;

        T_POSITION = 0;
    }


    public MatrixS[] execute(){
        initProcessors();
        setInitData(source);

        compute();

        return getResults();
    }

    private void initProcessors(){
        this.processors = setup.getProcessorForSimulation();
    }

    private void setInitData(MatrixS source){
        ((DataInitializer) this.processors.get(T_POSITION)).setInitData(source);
    }


    private void compute(){
        int i = 0;
        while(!isComputationFinished()){
            for (RunnableProcessor p: processors) {
                p.tick();
            }
            i++;
//            processors.stream().forEach(Processor::tick);
        }

        System.out.println("Done");
    }

    private MatrixS[]  getResults(){
        return ((Accumulator) processors.get(T_POSITION)).getResult();
    }

    private boolean isComputationFinished(){
        return processors.get(T_POSITION).isDone();
    }
}
