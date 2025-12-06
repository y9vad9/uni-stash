
package com.mathpar.matrix.file.mmio;


import java.io.File;
import com.mathpar.number.Element;
import java.io.*;


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
public abstract class CFWText
    implements CoordFileWriter {

    /**
     * Записать заголовок.
     *
     * @param out File
     * @param m int
     * @param n int
     * @param nz int
     * @todo Implement this parallel.matrix.MultFMatrix.file.mmio.CoordFileWriter method
     */
    public void writeHeader(File out, int m, int n, int nz)  throws MMIOException{
        try {
            FileWriter fwr = new FileWriter(out, true);
            fwr.write("%%Matrix Coordinate Format\n");
            fwr.write(m+" "+n+" "+nz+"\n");
            fwr.close();
        } catch (IOException ex) {
            throw new MMIOException("I/O error",ex);
        }
    }




    /**
     * Записать строку.
     *
     * @param out File
     * @param i int
     * @param j int
     * @param el Element
     * @todo Implement this parallel.matrix.MultFMatrix.file.mmio.CoordFileWriter method
     */
    public void writeLine(File out, int i, int j, Element el)  throws MMIOException{
        try {
            FileWriter fwr = new FileWriter(out, true);
            fwr.write(i+" "+j+" "+scalarToString(el)+"\n");
            fwr.close();
        } catch (IOException ex) {
            throw new MMIOException("I/O error",ex);
        }
    }



    protected abstract String scalarToString(Element el);

}
