/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.stat.FMD.MultFMatrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import com.mathpar.matrix.file.dense.FileMatrixL;
import mpi.MPI;
import mpi.MPIException;

/**
 *
 * @author vladimir
 */
@Deprecated
public class Logger implements AutoCloseable {

    private static boolean enabled = true;

    private final PrintWriter pw;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy hh:mm:ss");
    private int rank;

    public Logger(String path_to_file) throws FileNotFoundException {
        try {
            this.rank = MPI.COMM_WORLD.getRank();
        } catch (MPIException ex) {
            this.rank = -1;
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
        }

        pw = new PrintWriter(new File(path_to_file));
    }

    public void debug(final String message) {
        if (!enabled) {
            return;
        }
            StringBuilder sb = new StringBuilder();
            sb.append("debug ");
            sb.append(dateFormat.format(new Date()));
            sb.append(" | I`m ");
            sb.append(rank);
            sb.append(" : ");
            sb.append(message);
            pw.print(sb.toString());
            pw.print("\n");
            pw.flush();

    }
    public void line() {
        if (!enabled) {
            return;
        }
            StringBuilder sb = new StringBuilder();
            sb.append("debug ");
            sb.append(dateFormat.format(new Date()));
            sb.append(" | I`m ");
            sb.append(rank);
            sb.append(" : ");
            sb.append("-----------------------------------------------");
            pw.print(sb.toString());
            pw.print("\n");
            pw.flush();

    }

    public void error(final String message) {
        if (!enabled) {
            return;
        }
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("ERROR ");
            sb.append(dateFormat.format(new Date()));
            sb.append(" | I`m " + MPI.COMM_WORLD.getRank() + " : ");
            sb.append(message);
            pw.print(sb.toString());
            pw.print("\n");
            pw.flush();
        } catch (MPIException ex) {
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void error(final String message, final Exception e) {
        if (!enabled) {
            return;
        }
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("ERROR ");
            sb.append(dateFormat.format(new Date()));
            sb.append(" | I`m " + MPI.COMM_WORLD.getRank() + " : ");
            sb.append(message);
            pw.print(e.toString());
            pw.print(sb.toString());
            pw.print("\n");
            pw.flush();
        } catch (MPIException ex) {
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void printMatrix(final FileMatrixL fml) {
        try {
            if (!enabled) {
                return;
            }
            StringBuilder sb = new StringBuilder();
                sb.append("PRINT_MATRIX ");
            sb.append(dateFormat.format(new Date()));
            sb.append(" | I`m ");
            sb.append(rank);
            sb.append(" : \n");
            for (long[] M : fml.toMatrixL().M) {
                sb.append(Arrays.toString(M));
                sb.append("\n");
            }
            sb.append("\n");
            pw.print(sb.toString());
            pw.flush();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void printMatrix(final int[][] l) {
        if (!enabled) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("PRINT_MATRIX ");
        sb.append(dateFormat.format(new Date()));
        sb.append(" | I`m ");
        sb.append(rank);
        sb.append(" : \n");
        for (int[] M : l) {
            sb.append(Arrays.toString(M));
            sb.append("\n");
        }
        sb.append("\n");
        pw.print(sb.toString());
        pw.flush();

    }

    public void printMatrix(final int[] l) {
        if (!enabled) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("ERROR ");
        sb.append(dateFormat.format(new Date()));
        sb.append(" | I`m ");
        sb.append(rank);
        sb.append(" : \n");

        sb.append(Arrays.toString(l));
        sb.append("\n");

        sb.append("\n");
        pw.print(sb.toString());
        pw.flush();

    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled() {
        Logger.enabled = true;
    }

    public void setDisabled() {
        Logger.enabled = false;
    }



    @Override
    public void close() {
        pw.flush();
        pw.close();
    }

}
