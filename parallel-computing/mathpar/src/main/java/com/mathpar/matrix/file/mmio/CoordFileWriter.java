
package com.mathpar.matrix.file.mmio;

import java.io.*;
import com.mathpar.number.*;


/**
 * <p>Title: ParCA</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: ParCA</p>
 *
 * @author Yuri Valeev
 * @version 2.0
 */
public interface CoordFileWriter {
    /**
     * Записать заголовок.
     * @param out File
     * @param m int
     * @param n int
     * @param nz int
     * @throws MMIOException
     */
    public void writeHeader(File out, int m, int n, int nz) throws MMIOException;

    /**
     * Записать строку.
     * @param out File
     * @param i int
     * @param j int
     * @param el Element
     * @throws MMIOException
     */
    public void writeLine(File out, int i, int j, Element el) throws MMIOException;
}
