
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
public class CFRTextR64
    extends CFRText {

    public CFRTextR64(InputStream is){
        super(is);
    }


    public CFRTextR64(Reader r){
        super(r);
    }

    public CFRTextR64(File in) throws MMIOException{
        super(in);
    }


    /**
     * Конвертирует строку a_ij --> Element.
     * "double" --> NumberR64
     * @param str String
     * @return Element
     * @throws MMIOException
     */
    protected Element toScalar(String str) throws MMIOException {
        double d = 0.0;
        try {
            d = Double.parseDouble(str);
        } catch (NumberFormatException ex) {
            throw new MMIOException("Must be double", ex);
        }
        return new NumberR64(d);
    }
}
