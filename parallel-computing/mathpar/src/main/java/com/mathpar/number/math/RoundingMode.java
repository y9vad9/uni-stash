
package com.mathpar.number.math;

import com.mathpar.number.NumberR;

public enum RoundingMode {





    // Corresponding BigDecimal rounding constant



    UP(NumberR.ROUND_UP),


    DOWN(NumberR.ROUND_DOWN),


    CEILING(NumberR.ROUND_CEILING),


    FLOOR(NumberR.ROUND_FLOOR),

    HALF_UP(NumberR.ROUND_HALF_UP),

    HALF_DOWN(NumberR.ROUND_HALF_DOWN),

    HALF_EVEN(NumberR.ROUND_HALF_EVEN),

    UNNECESSARY(NumberR.ROUND_UNNECESSARY);
   public final int oldMode;
    /**
     * Constructor
     *
     * @param oldMode The <tt>BigDecimal</tt> constant corresponding to
     *        this mode
     */
    private RoundingMode(int oldMode) {
        this.oldMode = oldMode;
    }

    /**
     * Returns the <tt>RoundingMode</tt> object corresponding to a
     * legacy integer rounding mode constant in {@link BigDecimal}.
     *
     * @param  rm legacy integer rounding mode to convert
     * @return <tt>RoundingMode</tt> corresponding to the given integer.
     * @throws IllegalArgumentException integer is out of range
     */
    public static RoundingMode valueOf(int rm) {
	switch(rm) {

	case NumberR.ROUND_UP:
	    return UP;

	case NumberR.ROUND_DOWN:
	    return DOWN;

	case NumberR.ROUND_CEILING:
	    return CEILING;

	case NumberR.ROUND_FLOOR:
	    return FLOOR;

	case NumberR.ROUND_HALF_UP:
	    return HALF_UP;

	case NumberR.ROUND_HALF_DOWN:
	    return HALF_DOWN;

	case NumberR.ROUND_HALF_EVEN:
	    return HALF_EVEN;

	case NumberR.ROUND_UNNECESSARY:
	    return UNNECESSARY;

	default:
	    throw new IllegalArgumentException("argument out of range");
	}
    }
}
