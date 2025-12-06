
package com.mathpar.matrix.file.mmio;

import java.io.File;


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
public class GeneratorCFRTextC64 implements GeneratorCFR{
    public CoordFileReader getCFR(File in) throws MMIOException{
        return new CFRTextC64(in);
    }
}
