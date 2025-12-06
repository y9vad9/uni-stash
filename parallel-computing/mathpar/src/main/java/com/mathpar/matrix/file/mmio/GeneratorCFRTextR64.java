
package com.mathpar.matrix.file.mmio;

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
public class GeneratorCFRTextR64 implements GeneratorCFR{
    public CoordFileReader getCFR(File in) throws MMIOException{
        return new CFRTextR64(in);
    }
}
