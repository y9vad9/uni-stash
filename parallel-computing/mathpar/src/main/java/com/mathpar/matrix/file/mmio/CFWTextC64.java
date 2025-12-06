
package com.mathpar.matrix.file.mmio;

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
public class CFWTextC64 extends CFWText{

    /**
     * NumberC64 --> "double" "double"
     * @param el Element
     * @return String
     */
    protected String scalarToString(Element el){
        Complex c=(Complex)el;
        return c.re+" "+c.im;
    }

}
