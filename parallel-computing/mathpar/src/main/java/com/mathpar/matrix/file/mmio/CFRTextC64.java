
package com.mathpar.matrix.file.mmio;


import com.mathpar.number.*;
import java.io.*;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CFRTextC64
    extends CFRText {

    public CFRTextC64(InputStream is){
        super(is);
    }

    protected CFRTextC64(Reader r){
        super(r);
    }


    public CFRTextC64(File in) throws MMIOException{
        super(in);
    }


    /**
     * Конвертирует строку a_ij --> Element.
     *
     * "double" "double" --> NumberC64
     *
     * @param str String
     * @return Element
     * @throws MMIOException
     */
    protected Element toScalar(String str) throws MMIOException {
        String[] reim=str.split("\\s+",2);
        if (reim.length<2) {
            throw new MMIOException("Must be: 'double' 'double'");
        }
        double re,im;
        try {
            re = Double.parseDouble(reim[0]);
            im = Double.parseDouble(reim[1]);
        } catch (NumberFormatException ex) {
            throw new MMIOException("Must be: 'double' 'double'",ex);
        }
        return new Complex(re,im);
    }
}
