
package com.mathpar.matrix.file.spec;


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
public interface SpecMatr {
    public int getRows();
    public int getCols();
    public boolean hasSubBlock(int nb);
    public SpecMatr getSubBlock(int nb);
    public boolean isNotZero();

    public boolean isEMatr();
    public EMArr getEMArr();

    public boolean isIMatr();
    public int[] getIMArr();
}
