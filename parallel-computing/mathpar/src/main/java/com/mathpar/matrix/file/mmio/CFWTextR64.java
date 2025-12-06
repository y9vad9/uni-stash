
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
public class CFWTextR64 extends CFWText{
    /**
     * NumberR64 --> "double"
     * @param el Element
     * @return String
     */
    protected String scalarToString(Element el){
        return ((NumberR64)el).toString();
    }
}
