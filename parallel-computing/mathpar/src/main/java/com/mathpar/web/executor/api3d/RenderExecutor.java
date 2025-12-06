/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.web.executor.api3d;

import com.mathpar.func.Page;
import java.util.List;
import java.util.concurrent.*;
import com.mathpar.web.exceptions.MathparException;

/**
 *
 * @author Artem Sabitov (a.r.sabitov@gmail.com)
 */
public class RenderExecutor {

    public List<double[]> run(Page page, String task) {
        return run(page, task, 0);
    }

    public List<double[]> run(Page page, String task, int sectionId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        long timeout = page.ring.TIMEOUT;
        
        Future<List<double[]>> future = executor.submit(new RenderMultipleSurface(page, task, sectionId));

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