package com.mathpar.web.executor;

import com.mathpar.func.Page;
import com.mathpar.web.exceptions.MathparException;

import java.util.List;
import java.util.concurrent.*;

public class ParametricPlot3dTimeoutRunner {
    public List<double[]> run(Page page, String task) {
        return run(page, task, 0);
    }

    public List<double[]> run(Page page, String task, int sectionId) {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final long timeout = page.ring.TIMEOUT;
        Future<List<double[]>> future = executor.submit(new ParametricPlot3dCallable(page, task, sectionId));
        try {
            return future.get(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof MathparException) {
                throw (MathparException) cause;
            } else {
                throw new MathparException("Unexpected exception: " + cause.getMessage(), cause);
            }
        } catch (TimeoutException ex) {
            future.cancel(true);
            throw new MathparException("Timeout after " + timeout +
                    " seconds (try to increase TIMEOUT value).", ex);
        } finally {
            executor.shutdownNow();
        }

        return null; // Shouldn't come here.
    }
}
