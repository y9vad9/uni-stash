package com.mathpar.students.OLD.curse5_2015.gladyshev;

import com.mathpar.parallel.ddp.engine.*;

class factory extends AbstractFactoryOfObjects {

    public AbstractTask CreateTask(int type) {
        return new task();
    }

    public void InitGraphs() {
        graph g = new graph();
        AddGraphOfTask(0, g);
    }

}
