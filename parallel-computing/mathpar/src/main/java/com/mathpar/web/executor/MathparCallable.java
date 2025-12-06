package com.mathpar.web.executor;

import com.mathpar.func.Page;
import com.mathpar.parallel.webCluster.engine.AlgorithmsConfig;
import java.util.concurrent.Callable;

public class MathparCallable implements Callable<MathparResult> {
    private final Page page;
    private final String task;
    private final int sectionId;

    public MathparCallable(Page page, String task, int sectionId) {
        this.page = page;
        this.task = task;
        this.sectionId = sectionId;
    }

    @Override
    public MathparResult call() throws Exception {
        String result = page.execution(task, sectionId);                
        String latex = page.strToTexStr(page.data.section[0] + "\nout:\n"
                + page.data.section[1], false);
   //     latex=latex.replaceAll("\\\\unicode\\{xB0\\}", "^{\\\\circ}\\\\!")
  //              .replaceAll("'([^\n;]*?)'", "\\\\hbox{$1}");
        return new MathparResult(result, latex);
    }
}
