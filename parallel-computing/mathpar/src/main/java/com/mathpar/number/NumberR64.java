package com.mathpar.number;

import java.util.Locale;
import java.util.Random;
import com.mathpar.polynom.PascalTriangle;

/**
 * NumberR64 -- Obtained from Object java.lang.number.Double
 */
public class NumberR64 extends Element {
    public NumberR64() {
    }
    public static final double POZITIVE_ZERO_DOUBLE = 0d / POSITIVE_INFINITY_DOUBLE;
    public static final double NEGATIVE_ZERO_DOUBLE = 0d / NEGATIVE_INFINITY_DOUBLE;
    public static NumberR64 E = new NumberR64(Math.E);
    public static NumberR64 PI = new NumberR64(Math.PI);
    public static double doublePIdiv180= Math.PI / 180.0;
    public static double double180divPI = 180.0/ Math.PI;
    public static NumberR64 POSITIVE_ZERO = new NumberR64(0.0);      //  equals ZERO
    public static NumberR64 NEGATIVE_ZERO = new NumberR64(NEGATIVE_ZERO_DOUBLE);
    public static NumberR64 ZERO = new NumberR64(0.0);
    public static NumberR64 ONE = new NumberR64(1.0);
    public static NumberR64 MINUS_ONE = new NumberR64(-1.0);
    public static Locale local_En = new Locale("en", "EN");
    /**
     * use serialVersionUID from JDK 1.0.2 for interoperability
     */
    private static final long serialVersionUID = -9172774392245257468L;

    static {
        PascalTriangle.initCacheZ();
        PascalTriangle.initCacheR64();
    }

    /**
     * Returns a string representation of the
     * <code>double</code> argument. All characters mentioned below are ASCII
     * characters. <ul> <li>If the argument is NaN_DOUBLE, the result is the
     * string &quot;
     * <code>NaN_DOUBLE</code>&quot;. <li>Otherwise, the result is a string that
     * represents the sign and magnitude (absolute value) of the argument. If
     * the sign is negative, the first character of the result is '
     * <code>-</code>' (
     * <code>'&#92;u002D'</code>); if the sign is positive, no sign character
     * appears in the result. As for the magnitude <i>m</i>: <ul> <li>If
     * <i>m</i> is infinity, it is represented by the characters
     * <code>"Infinity"</code>; thus, positive infinity produces the result
     * <code>"Infinity"</code> and negative infinity produces the result
     * <code>"-Infinity"</code>.
     *
     * <li>If <i>m</i> is zero, it is represented by the characters
     * <code>"0.0"</code>; thus, negative zero produces the result
     * <code>"-0.0"</code> and positive zero produces the result
     * <code>"0.0"</code>.
     *
     * <li>If <i>m</i> is greater than or equal to 10<sup>-3</sup> but less than
     * 10<sup>7</sup>, then it is represented as the integer part of <i>m</i>,
     * in decimal form with no leading zeroes, followed by '
     * <code>.</code>' (
     * <code>'&#92;u002E'</code>), followed by one or more decimal digits
     * representing the fractional part of <i>m</i>.
     *
     * <li>If <i>m</i> is less than 10<sup>-3</sup> or greater than or equal to
     * 10<sup>7</sup>, then it is represented in so-called "computerized
     * scientific notation." Let <i>n</i> be the unique integer such that
     * 10<sup><i>n</i></sup> &lt;= <i>m</i> &lt; 10<sup><i>n</i>+1</sup>; then
     * let <i>a</i> be the mathematically exact quotient of <i>m</i> and
     * 10<sup><i>n</i></sup> so that 1 &lt;= <i>a</i> &lt; 10. The magnitude is
     * then represented as the integer part of <i>a</i>, as a single decimal
     * digit, followed by '
     * <code>.</code>' (
     * <code>'&#92;u002E'</code>), followed by decimal digits representing the
     * fractional part of <i>a</i>, followed by the letter '
     * <code>E</code>' (
     * <code>'&#92;u0045'</code>), followed by a representation of <i>n</i> as a
     * decimal integer, as produced by the method {@link Integer#toString(int)}.
     * </ul> </ul> How many digits must be printed for the fractional part of
     * <i>m</i> or <i>a</i>? There must be at least one digit to represent the
     * fractional part, and beyond that as many, but only as many, more digits
     * as are needed to uniquely distinguish the argument value from adjacent
     * values of type
     * <code>double</code>. That is, suppose that <i>x</i> is the exact
     * mathematical value represented by the decimal representation produced by
     * this method for a finite nonzero argument <i>d</i>. Then <i>d</i> must be
     * the
     * <code>double</code> value nearest to <i>x</i>; or if two
     * <code>double</code> values are equally close to <i>x</i>, then <i>d</i>
     * must be one of them and the least significant bit of the significand of
     * <i>d</i> must be
     * <code>0</code>. <p> To create localized string representations of a
     * floating-point value, use subclasses of {@link java.text.NumberFormat}.
     *
     * @param d the <code>double</code> to be converted.
     *
     * @return a string representation of the argument.
     */
    public static String toString(double d) {
        return Double.toString(d);
    }

    private String removal_of_excess_zeros(String s) {int len=s.length();
         int j = s.lastIndexOf("0");
         if ((j !=  len - 1)||(s.indexOf('.')==-1)) return s;
         j--;
         char cc=' ';
         if(j==-1)j=0;
         else {cc=s.charAt(j);
               while ((cc=='0')||(cc=='.')){j--; if(cc=='.'){break;} if(j==-1){j=0; break;} cc=s.charAt(j); } } 
         return s.substring(0, j+1);
    }
    
//     private String removal_of_excess_zeros(String s) {
//        StringBuilder res = new StringBuilder(s);
//        int index_last_Zero = res.lastIndexOf("0");
//        if (index_last_Zero != res.length() - 1) {
//            return s;
//        }
//        boolean fl = true;
//        while (fl) {
//            index_last_Zero = res.lastIndexOf("0");
//            char temp = res.charAt(index_last_Zero - 1);
//            if (temp != '0') {
//                if (temp == '.') {
//                    return res.toString();
//                }
//                res.deleteCharAt(res.length() - 1);
//                break;
//            }
//            res.deleteCharAt(res.length() - 1);
//        }
//        return res.toString();
//    }
     
    /**
     * Nulling digits in the number which written in string s. 
     * Not nulled number of digits   equal "digitNumber"
     * @param s Some number
     * @param digitNumber --the number of digits, which can be nulled.
     * @return Rounded number.
     */
       private String nullingDigits(String s, int digitNumber) {
           int ee=Math.max(s.indexOf('e'),s.indexOf('E'));
           if(ee==-1) ee=s.length();
           int beg=ee-digitNumber;
           if (beg<1)beg=1;
           char[] x= s.toCharArray(); 
           char first=x[beg];
           for (int i = beg; i < ee; i++) x[i]='0';
           String res=""; beg--;
           if(first>'4'){
               if(x[beg]=='9'){x[beg]='0'; beg--;
                  while((beg>-1)&&(x[beg]=='9')){x[beg]='0'; beg--;}
                  if (beg==-1)res="1"; else x[beg]=(char)(x[beg]+1);
               }
                else{x[beg]=(char)(x[beg]+1);}
           }
           return res+String.copyValueOf(x);
       }
    
    
//         }
//        StringBuilder res = new StringBuilder(s);
//        int index_last_Zero = res.lastIndexOf("0");
//        if (index_last_Zero != res.length() - 1) {
//            return s;
//        }
//        boolean fl = true;
//        while (fl) {
//            index_last_Zero = res.lastIndexOf("0");
//            char temp = res.charAt(index_last_Zero - 1);
//            if (temp != '0') {
//                if (temp == '.') {
//                    return res.toString();
//                }
//                res.deleteCharAt(res.length() - 1);
//                break;
//            }
//            res.deleteCharAt(res.length() - 1);
//        }
//        return res.toString();
//    }

    /**
     * Returns a hexadecimal string representation of the
     * <code>double</code> argument. All characters mentioned below are ASCII
     * characters.
     *
     * <ul> <li>If the argument is NaN_DOUBLE, the result is the string &quot;
     * <code>NaN_DOUBLE</code>&quot;. <li>Otherwise, the result is a string that
     * represents the sign and magnitude of the argument. If the sign is
     * negative, the first character of the result is '
     * <code>-</code>' (
     * <code>'&#92;u002D'</code>); if the sign is positive, no sign character
     * appears in the result. As for the magnitude <i>m</i>:
     *
     * <ul> <li>If <i>m</i> is infinity, it is represented by the string
     * <code>"Infinity"</code>; thus, positive infinity produces the result
     * <code>"Infinity"</code> and negative infinity produces the result
     * <code>"-Infinity"</code>.
     *
     * <li>If <i>m</i> is zero, it is represented by the string
     * <code>"0x0.0p0"</code>; thus, negative zero produces the result
     * <code>"-0x0.0p0"</code> and positive zero produces the result
     * <code>"0x0.0p0"</code>.
     *
     * <li>If <i>m</i> is a
     * <code>double</code> value with a normalized representation, substrings
     * are used to represent the significand and exponent fields. The
     * significand is represented by the characters
     * <code>&quot;0x1.&quot;</code> followed by a lowercase hexadecimal
     * representation of the rest of the significand as a fraction. Trailing
     * zeros in the hexadecimal representation are removed unless all the digits
     * are zero, in which case a single zero is used. Next, the exponent is
     * represented by
     * <code>&quot;p&quot;</code> followed by a decimal string of the unbiased
     * exponent as if produced by a call to
     * {@link Integer#toString(int) Integer.toString} on the exponent value.
     *
     * <li>If <i>m</i> is a
     * <code>double</code> value with a subnormal representation, the
     * significand is represented by the characters
     * <code>&quot;0x0.&quot;</code> followed by a hexadecimal representation of
     * the rest of the significand as a fraction. Trailing zeros in the
     * hexadecimal representation are removed. Next, the exponent is represented
     * by
     * <code>&quot;p-1022&quot;</code>. Note that there must be at least one
     * nonzero digit in a subnormal significand.
     *
     * </ul>
     *
     * </ul>
     *
     * <table border> <caption><h3>Examples</h3></caption>
     * <tr><th>Floating-point Value</th><th>Hexadecimal String</th>
     * <tr><td><code>1.0</code></td>	<td><code>0x1.0p0</code></td>
     * <tr><td><code>-1.0</code></td>	<td><code>-0x1.0p0</code></td>
     * <tr><td><code>2.0</code></td>	<td><code>0x1.0p1</code></td>
     * <tr><td><code>3.0</code></td>	<td><code>0x1.8p1</code></td>
     * <tr><td><code>0.5</code></td>	<td><code>0x1.0p-1</code></td>
     * <tr><td><code>0.25</code></td>	<td><code>0x1.0p-2</code></td>
     * <tr><td><code>Double.MAX_VALUE</code></td>
     * <td><code>0x1.fffffffffffffp1023</code></td>
     * <tr><td><code>Minimum Normal Value</code></td>
     * <td><code>0x1.0p-1022</code></td>
     * <tr><td><code>Maximum Subnormal Value</code></td>
     * <td><code>0x0.fffffffffffffp-1022</code></td>
     * <tr><td><code>Double.MIN_VALUE</code></td>
     * <td><code>0x0.0000000000001p-1022</code></td> </table>
     *
     * @param d the <code>double</code> to be converted.
     *
     * @return a hex string representation of the argument.
     *
     * @since 1.5
     * @author Joseph D. Darcy
     */
    public static String toHexString(double d) {
        return Double.toHexString(d);
    }

    public static NumberR64 valueOf(String s) throws NumberFormatException {
        return (new NumberR64(Double.valueOf(s)));
    }

    /**
     * Returns a new
     * <code>double</code> initialized to the value represented by the specified
     * <code>String</code>, as performed by the
     * <code>valueOf</code> method of class
     * <code>Double</code>.
     *
     * @param s the string to be parsed.
     *
     * @return the <code>double</code> value represented by the string argument.
     *
     * @exception NumberFormatException if the string does not contain a
     * parsable <code>double</code>.
     * @see java.lang.Double#valueOf(String)
     * @since 1.2
     */
    public static double parseDouble(String s) throws NumberFormatException {
        return Double.parseDouble(s);
    }

    /**
     * Returns
     * <code>true</code> if the specified number is a Not-a-Number (NaN_DOUBLE)
     * value,
     * <code>false</code> otherwise.
     *
     * @param v the value to be tested.
     *
     * @return  <code>true</code> if the value of the argument is NaN_DOUBLE;
     * <code>false</code> otherwise.
     */
    static public boolean isNaN(double v) {
        return (v != v);
    }

    /**
     * Returns
     * <code>true</code> if the specified number is infinitely large in
     * magnitude,
     * <code>false</code> otherwise.
     *
     * @param v the value to be tested.
     *
     * @return  <code>true</code> if the value of the argument is positive
     * infinity or negative infinity; <code>false</code> otherwise.
     */
    static public boolean isInfinite(double v) {
        return (v == POSITIVE_INFINITY_DOUBLE) || (v == NEGATIVE_INFINITY_DOUBLE);
    }

    /**
     * Constructs a newly allocated
     * <code>Double</code> object that represents the primitive
     * <code>double</code> argument.
     *
     * @param value the value to be represented by the <code>Double</code>.
     */
    public NumberR64(double value) {
        this.value = value;
    }

    /**
     * Constructs a newly allocated
     * <code>Double</code> object that represents the floating-point value of
     * type
     * <code>double</code> represented by the string. The string is converted to
     * a
     * <code>double</code> value as if by the
     * <code>valueOf</code> method.
     *
     * @param s a string to be converted to a <code>Double</code>.
     *
     * @exception NumberFormatException if the string does not contain a
     * parsable number.
     * @see java.lang.Double#valueOf(java.lang.String)
     */
    public NumberR64(String s) throws NumberFormatException {
        // REMIND: this is inefficient
        value = valueOf(s).doubleValue();
    }

    /**
     * Returns
     * <code>true</code> if this
     * <code>Double</code> value is a Not-a-Number (NaN_DOUBLE),
     * <code>false</code> otherwise.
     *
     * @return  <code>true</code> if the value represented by this object is
     * NaN_DOUBLE; <code>false</code> otherwise.
     */
    @Override
    public boolean isNaN() {
        return isNaN(value);
    }

    /**
     * Returns a string representation of this
     * <code>Double</code> object. The primitive
     * <code>double</code> value represented by this object is converted to a
     * string exactly as if by the method
     * <code>toString</code> of one argument.
     *
     * @return a <code>String</code> representation of this object.
     *
     * @see java.lang.Double#toString(double)
     */
    @Override
    public String toString() {
        return String.format(local_En, "%." + 2 + "f", value);
    }

     
    @Override
    public String toString(Ring r) {
        if (isInfinite()) return toString().replace("Infinity", "\\infty");
        String ss=String.format(local_En, "%." + ((r.FLOATPOS<0)?0:r.FLOATPOS) + "f", value);
        return (r.FLOATPOS>-1) ? removal_of_excess_zeros(ss): nullingDigits(ss,-r.FLOATPOS);          
    }
    
//     @Override
//    public String toString(Ring r) {
//        if (isInfinite()) {
//            return toString().replace("Infinity", "\\infty");
//        }
//        return (r.FLOATPOS <= 1) ? String.format(local_En, "%." + r.FLOATPOS + "f", value) : removal_of_excess_zeros(String.format(local_En, "%." + r.FLOATPOS + "f", value));
//    }

    @Override
    public String toString(int i, Ring r) {
        return toString(r);
    }

    /**
     * Returns the value of this
     * <code>Double</code> as a
     * <code>byte</code> (by casting to a
     * <code>byte</code>).
     *
     * @return the <code>double</code> value represented by this object
     * converted to type <code>byte</code>
     *
     * @since JDK1.1
     */
    public byte byteValue() {
        return (byte) value;
    }

    /**
     * Returns the value of this
     * <code>Double</code> as a
     * <code>short</code> (by casting to a
     * <code>short</code>).
     *
     * @return the <code>double</code> value represented by this object
     * converted to type <code>short</code>
     *
     * @since JDK1.1
     */
    public short shortValue() {
        return (short) value;
    }

    /**
     * Returns the value of this
     * <code>Double</code> as an
     * <code>int</code> (by casting to type
     * <code>int</code>).
     *
     * @return the <code>double</code> value represented by this object
     * converted to type <code>int</code>
     */
    @Override
    public int intValue() {
        return (int) value;
    }

    /**
     * Returns the value of this
     * <code>Double</code> as a
     * <code>long</code> (by casting to type
     * <code>long</code>).
     *
     * @return the <code>double</code> value represented by this object
     * converted to type <code>long</code>
     */
    @Override
    public long longValue() {
        return (long) value;
    }

    /**
     * Returns the
     * <code>float</code> value of this
     * <code>Double</code> object.
     *
     * @return the <code>double</code> value represented by this object
     * converted to type <code>float</code>
     *
     * @since JDK1.0
     */
    public float floatValue() {
        return (float) value;
    }

    /**
     * Returns the
     * <code>double</code> value of this
     * <code>Double</code> object.
     *
     * @return the <code>double</code> value represented by this object
     */
    @Override
    public double doubleValue() {
        return value;
    }

    /**
     * Returns a hash code for this
     * <code>Double</code> object. The result is the exclusive OR of the two
     * halves of the
     * <code>long</code> integer bit representation, exactly as produced by the
     * method {@link #doubleToLongBits(double)}, of the primitive
     * <code>double</code> value represented by this
     * <code>Double</code> object. That is, the hash code is the value of the
     * expression: null null null null null null null null null null null     <blockquote><pre>
     * (int)(v^(v&gt;&gt;&gt;32))
     * </pre></blockquote> where
     * <code>v</code> is defined by: null null null null null null null null
     * null null null     <blockquote><pre>
     * long v = Double.doubleToLongBits(this.doubleValue());
     * </pre></blockquote>
     *
     * @return a <code>hash code</code> value for this object.
     */
    @Override
    public int hashCode() {
        long bits = doubleToLongBits(value);
        return (int) (bits ^ (bits >>> 32));
    }

    /**
     * Compares this object against the specified object. The result is
     * <code>true</code> if and only if the argument is not
     * <code>null</code> and is a
     * <code>Double</code> object that represents a
     * <code>double</code> that has the same value as the
     * <code>double</code> represented by this object. For this purpose, two
     * <code>double</code> values are considered to be the same if and only if
     * the method {@link
     * #doubleToLongBits(double)} returns the identical
     * <code>long</code> value when applied to each. <p> Note that in most
     * cases, for two instances of class
     * <code>Double</code>,
     * <code>d1</code> and
     * <code>d2</code>, the value of
     * <code>d1.equals(d2)</code> is
     * <code>true</code> if and only if null null null null null null null null
     * null null null     <blockquote><pre>
     *   d1.doubleValue()&nbsp;== d2.doubleValue()
     * </pre></blockquote> <p> also has the value
     * <code>true</code>. However, there are two exceptions: <ul> <li>If
     * <code>d1</code> and
     * <code>d2</code> both represent
     * <code>Double.NaN_DOUBLE</code>, then the
     * <code>equals</code> method returns
     * <code>true</code>, even though
     * <code>Double.NaN_DOUBLE==Double.NaN_DOUBLE</code> has the value
     * <code>false</code>. <li>If
     * <code>d1</code> represents
     * <code>+0.0</code> while
     * <code>d2</code> represents
     * <code>-0.0</code>, or vice versa, the
     * <code>equal</code> test has the value
     * <code>false</code>, even though
     * <code>+0.0==-0.0</code> has the value
     * <code>true</code>. </ul> This definition allows hash tables to operate
     * properly.
     *
     * @param obj the object to compare with.
     *
     * @return  <code>true</code> if the objects are the same; <code>false</code>
     * otherwise.
     *
     * @see java.lang.Double#doubleToLongBits(double)
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof NumberR64)
                && (doubleToLongBits(((NumberR64) obj).value)
                == doubleToLongBits(value));
    }

    /**
     * Returns a representation of the specified floating-point value according
     * to the IEEE 754 floating-point "double format" bit layout. <p> Bit 63
     * (the bit that is selected by the mask
     * <code>0x8000000000000000L</code>) represents the sign of the
     * floating-point number. Bits 62-52 (the bits that are selected by the mask
     * <code>0x7ff0000000000000L</code>) represent the exponent. Bits 51-0 (the
     * bits that are selected by the mask
     * <code>0x000fffffffffffffL</code>) represent the significand (sometimes
     * called the mantissa) of the floating-point number. <p> If the argument is
     * positive infinity, the result is
     * <code>0x7ff0000000000000L</code>. <p> If the argument is negative
     * infinity, the result is
     * <code>0xfff0000000000000L</code>. <p> If the argument is NaN_DOUBLE, the
     * result is
     * <code>0x7ff8000000000000L</code>. <p> In all cases, the result is a
     * <code>long</code> integer that, when given to the
     * {@link #longBitsToDouble(long)} method, will produce a floating-point
     * value the same as the argument to
     * <code>doubleToLongBits</code> (except all NaN_DOUBLE values are collapsed
     * to a single &quot;canonical&quot; NaN_DOUBLE value).
     *
     * @param value a <code>double</code> precision floating-point number.
     *
     * @return the bits that represent the floating-point number.
     */
    public static long doubleToLongBits(double value) {
        return java.lang.Double.doubleToLongBits(value);
    }

    /**
     * Returns a representation of the specified floating-point value according
     * to the IEEE 754 floating-point "double format" bit layout, preserving
     * Not-a-Number (NaN_DOUBLE) values. <p> Bit 63 (the bit that is selected by
     * the mask
     * <code>0x8000000000000000L</code>) represents the sign of the
     * floating-point number. Bits 62-52 (the bits that are selected by the mask
     * <code>0x7ff0000000000000L</code>) represent the exponent. Bits 51-0 (the
     * bits that are selected by the mask
     * <code>0x000fffffffffffffL</code>) represent the significand (sometimes
     * called the mantissa) of the floating-point number. <p> If the argument is
     * positive infinity, the result is
     * <code>0x7ff0000000000000L</code>. <p> If the argument is negative
     * infinity, the result is
     * <code>0xfff0000000000000L</code>. <p> If the argument is NaN_DOUBLE, the
     * result is the
     * <code>long</code> integer representing the actual NaN_DOUBLE value.
     * Unlike the
     * <code>doubleToLongBits</code> method,
     * <code>doubleToRawLongBits</code> does not collapse all the bit patterns
     * encoding a NaN_DOUBLE to a single &quot;canonical&quot; NaN_DOUBLE value.
     * <p> In all cases, the result is a
     * <code>long</code> integer that, when given to the
     * {@link #longBitsToDouble(long)} method, will produce a floating-point
     * value the same as the argument to
     * <code>doubleToRawLongBits</code>.
     *
     * @param value a <code>double</code> precision floating-point number.
     *
     * @return the bits that represent the floating-point number.
     *
     * @since 1.3
     */
    public static native long doubleToRawLongBits(double value);

    /**
     * Returns the
     * <code>double</code> value corresponding to a given bit representation.
     * The argument is considered to be a representation of a floating-point
     * value according to the IEEE 754 floating-point "double format" bit
     * layout. <p> If the argument is
     * <code>0x7ff0000000000000L</code>, the result is positive infinity. <p> If
     * the argument is
     * <code>0xfff0000000000000L</code>, the result is negative infinity. <p> If
     * the argument is any value in the range
     * <code>0x7ff0000000000001L</code> through
     * <code>0x7fffffffffffffffL</code> or in the range
     * <code>0xfff0000000000001L</code> through
     * <code>0xffffffffffffffffL</code>, the result is a NaN_DOUBLE. No IEEE 754
     * floating-point operation provided by Java can distinguish between two
     * NaN_DOUBLE values of the same type with different bit patterns. Distinct
     * values of NaN_DOUBLE are only distinguishable by use of the
     * <code>Double.doubleToRawLongBits</code> method. <p> In all other cases,
     * let <i>s</i>, <i>e</i>, and <i>m</i> be three values that can be computed
     * from the argument: null null null null null null null null null null null     <blockquote><pre>
     * int s = ((bits &gt;&gt; 63) == 0) ? 1 : -1;
     * int e = (int)((bits &gt;&gt; 52) & 0x7ffL);
     * long m = (e == 0) ?
     *                 (bits & 0xfffffffffffffL) &lt;&lt; 1 :
     *                 (bits & 0xfffffffffffffL) | 0x10000000000000L;
     * </pre></blockquote> Then the floating-point result equals the value of
     * the mathematical expression
     * <i>s</i>&middot;<i>m</i>&middot;2<sup><i>e</i>-1075</sup>. <p> Note that
     * this method may not be able to return a
     * <code>double</code> NaN_DOUBLE with exactly same bit pattern as the
     * <code>long</code> argument. IEEE 754 distinguishes between two kinds of
     * NaNs, quiet NaNs and <i>signaling NaNs</i>. The differences between the
     * two kinds of NaN_DOUBLE are generally not visible in Java. Arithmetic
     * operations on signaling NaNs turn them into quiet NaNs with a different,
     * but often similar, bit pattern. However, on some processors merely
     * copying a signaling NaN_DOUBLE also performs that conversion. In
     * particular, copying a signaling NaN_DOUBLE to return it to the calling
     * method may perform this conversion. So
     * <code>longBitsToDouble</code> may not be able to return a
     * <code>double</code> with a signaling NaN_DOUBLE bit pattern.
     * Consequently, for some
     * <code>long</code> values,
     * <code>doubleToRawLongBits(longBitsToDouble(start))</code> may <i>not</i>
     * equal
     * <code>start</code>. Moreover, which particular bit patterns represent
     * signaling NaNs is platform dependent; although all NaN_DOUBLE bit
     * patterns, quiet or signaling, must be in the NaN_DOUBLE range identified
     * above.
     *
     * @param bits any <code>long</code> integer.
     *
     * @return the <code>double</code> floating-point value with the same bit
     * pattern.
     */
    public static native double longBitsToDouble(long bits);

    /**
     * Compares two
     * <code>Double</code> objects numerically. There are two ways in which
     * comparisons performed by this method differ from those performed by the
     * Java language numerical comparison operators (
     * <code>&lt;, &lt;=, ==, &gt;= &gt;</code>) when applied to primitive
     * <code>double</code> values: <ul><li>
     * <code>Double.NaN_DOUBLE</code> is considered by this method to be equal
     * to itself and greater than all other
     * <code>double</code> values (including
     * <code>Double.POSITIVE_INFINITY_DOUBLE</code>). <li>
     * <code>0.0d</code> is considered by this method to be greater than
     * <code>-0.0d</code>. </ul> This ensures that the <i>natural ordering</i>
     * of <tt>Double</tt> objects imposed by this method is <i>consistent with
     * equals</i>.
     *
     * @param anotherDouble the <code>Double</code> to be compared.
     *
     * @return the value <code>0</code> if <code>anotherDouble</code> is
     * numerically equal to this <code>Double</code>; a value less * * * * *
     * than <code>0</code> if this <code>Double</code> is numerically less      * than <code>anotherDouble</code>; and a value greater
     * than <code>0</code> if this <code>Double</code> is numerically greater *
     * * than <code>anotherDouble</code>.
     *
     * @since 1.2
     */
    public int compareTo(NumberR64 anotherDouble) {
//        double c = value - anotherDouble.value;
//        if(c == 0.0) return 0;
//        if(c > 0.0) return 1;
//        return -1;
        return NumberR64.compare(value, anotherDouble.value);
    }

    public static int compareTo(Element p, Element pp) {
        return ((NumberR64) p).compareTo((NumberR64) pp);
    }

    @Override
    public Boolean isNegative() {
        return value < 0;
    }

    /**
     * Compares the two specified
     * <code>double</code> values. The sign of the integer value returned is the
     * same as that of the integer that would be returned by the call:
     * <pre>
     *    new Double(d1).compareTo(new Double(d2))
     * </pre>
     *
     * @param d1 the first <code>double</code> to compare
     * @param d2 the second <code>double</code> to compare
     *
     * @return the value <code>0</code> if <code>d1</code> is numerically equal
     * to <code>d2</code>; a value less than <code>0</code> if <code>d1</code>
     * is numerically less than <code>d2</code>; and a value greater * * * *
     * than <code>0</code> if <code>d1</code> is numerically greater than
     * <code>d2</code>.
     *
     * @since 1.4
     */
    public static int compare(double d1, double d2) {
        if (d1 < d2) {
            return -1;		 // Neither val is NaN_DOUBLE, thisVal is smaller
        }
        if (d1 > d2) {
            return 1;		 // Neither val is NaN_DOUBLE, thisVal is larger
        }
        long thisBits = NumberR64.doubleToLongBits(d1);
        long anotherBits = NumberR64.doubleToLongBits(d2);

        return (thisBits == anotherBits ? 0 : // Values are equal
                (thisBits < anotherBits ? -1 : // (-0.0, 0.0) or (!NaN_DOUBLE, NaN_DOUBLE)
                1));                          // (0.0, -0.0) or (NaN_DOUBLE, !NaN_DOUBLE)
    }

    @Override
    public Element myOne(Ring ring) {
        return ONE;
    }

    @Override
    public Element myMinus_one(Ring ring) {
        return new NumberR64(-1.0);
    }

    @Override
    public Element myMinus_one() {
        return new NumberR64(-1.0);
    }

    @Override
    public Element myZero(Ring ring) {
        return ZERO;
    }

    @Override
    public Element myZero() {
        return ZERO;
    }

    @Override
    public Element minus_one(Ring ring) {
        return new NumberR64(-1.0);
    }

    @Override
    public Element one(Ring ring) {
        return ONE;
    }

    @Override
    public Element zero(Ring ring) {
        return ZERO;
    }

    @Override
    public NumberR64 random(int[] randomType, Random rnd, Ring ring) {
        //int nbits = randomType[randomType.length - 1];
        return new NumberR64(rnd.nextDouble());
    }

    public NumberR BigDecimalValue() {
        return new NumberR(value);
    }

    public NumberZ BigIntegerValue() {
        return (new NumberR(value).NumberRtoNumberZ());
    }
    // public number divide(number x, int i, RoundingMode r){return new NumberL(((Double)c).divide((Double)x.c));}

    /**
     * Math.sqrt(this.doubleValue())
     * @param num
     * @param denom
     * @param ring
     * @return
     */
    @Override
    public NumberR64 powTheFirst(int num, int denom, Ring ring) {
        return new NumberR64((num == 1 & denom == 2) ? sqrt(ring).value
                : (denom != 0) ? Math.pow(this.doubleValue(), ((double) num) / (double) denom) : 0D);

    }

    /**
     * Вычисление 1-го значения корня n-ой степени
     *
     * @return 1-е значение корня n-ой степени
     */
    @Override
    public NumberR64 rootTheFirst(int n, Ring ring) {
        NumberR64 res = new NumberR64(Math.pow(Math.abs(value), (1.0d / (double) n)));
        return (value < 0) ? res.negate(ring) : res;
    }

    /**
     * Вычисление n значений корня n-ой степени
     *
     * @param n
     *
     * @return n значений корня n-ой степени
     */
    @Override
    public Complex[] root(int n, Ring ring) {
        return (new Complex(this.value)).root(n, ring);
    }

    @Override
    public NumberR64 negate(Ring ring) {
        return new NumberR64(-value);
    }

    @Override
    public NumberR64 abs(Ring r) {
        return new NumberR64(Math.abs((value)));
    }

    public NumberR64 add(NumberR64 x) {
        return new NumberR64(value + x.value);
    }

    @Override
    public Element add(Element x, Ring ring) {
        if (x == NAN) {
            return NAN;
        }
        if ((x == NEGATIVE_INFINITY) || (x == POSITIVE_INFINITY)) {
            return x;
        }
        if (x.numbElementType() > com.mathpar.number.Ring.R64) {
            return x.add(this, ring);
        }
        return add((NumberR64) x.toNumber(Ring.R64, ring));
    }

    public NumberR64 subtract(NumberR64 x) {
        return new NumberR64(value - x.value);
    }

    @Override
    public Element subtract(Element x, Ring ring) {
        if (x == NAN) {
            return NAN;
        }
        if (x == NEGATIVE_INFINITY || x == POSITIVE_INFINITY) {
            return x.negate(ring);
        }
        if (x.numbElementType() > Ring.R64) {
            return x.negate(ring).add(this, ring);
        }
        return subtract((NumberR64) x.toNumber(Ring.R64, ring));
    }

    public NumberR64 multiply(NumberR64 x) {
        return new NumberR64(value * x.value);
    }

    @Override
    public Element multiply(Element x, Ring ring) {
        if (x == NAN) {
            return NAN;
        }
        if ((x == NEGATIVE_INFINITY) || (x == POSITIVE_INFINITY)) {
            return (this.isZero(ring)) ? NAN : this.isNegative() ? x.negate(ring) : x;
        }
        if (x.numbElementType() > com.mathpar.number.Ring.R64) {
            return x.multiply(this, ring);
        }
        return multiply((NumberR64) x.toNumber(Ring.R64, ring));
    }

    @Override
    public NumberR64 multiply(Element x, int i, Ring ring) {
        return (NumberR64) multiply(x, ring);
    }

    /**
     * inversion of NumberR64 element
     *
     * @param ring
     *
     * @return
     */
    @Override
    public Element inverse(Ring ring) {
        return new NumberR64(1.0 / value);
    }

    public NumberR64 divide(NumberR64 x) {
        return new NumberR64(value / x.value);
    }
    @Override
    public Element divideToFraction(Element x, Ring ring) {
        return divide(x,ring);
    }
    @Override
    public Element divide(Element x, Ring ring) {
        if (x instanceof NumberR64) return divide((NumberR64) x);
        if (x == NAN) { return NAN; }
        if ((x == NEGATIVE_INFINITY) || (x == POSITIVE_INFINITY))  return ZERO;
        int type = x.numbElementType();
        if (type > Ring.R64) { if (type >= Ring.Polynom) {
                return new Fraction(this, x).cancel(ring);
            } else {return this.toNumber(type, ring).divide(x, ring);
            }
        }
        return divide((NumberR64) x.toNumber(Ring.R64, ring));
    }


//        switch (type) {
//            case com.mathpar.number.Ring.Polynom:
//            case com.mathpar.number.Ring.F:
//                return new F(com.mathpar.func.F.DIVIDE, new Element[] {this, x});
//            default:
//                if (type < numbElementType()) {
//                    return divide((NumberR) (x.toNumber(Ring.R, ring)), ring.MC);
//                } else  return (this.toNumber(type, ring)).divide(x, ring);
//        }
//    }
    
    
    
    
    @Override
    public int signum() {
        double y = value;
        if (y > 0) {
            return 1;
        } else if (y < 0) {
            return (-1);
        }
        return 0;
    }

    @Override
    public boolean equals(Element x, Ring ring) {
        int t = x.numbElementType();
        if ((t > Ring.R64MinMax) || (t < Ring.R64)) {
            return false;
        }
        if (value == ((NumberR64) x).value) {
            return true;
        }
        double y = value - ((NumberR64) x).value;
        return (Math.abs(y) < ring.MachineEpsilonR64.value) ? true : false;
    }

    @Override
    public int compareTo(Element x) {
        if(x instanceof NumberR64)
           return NumberR64.compare(value, ((NumberR64)x).value);
       return compareTo(x,  Ring.ringR64xyzt);
    }

    @Override
    public int compareTo(Element x, Ring ring) {Element xx=x;
        if (x instanceof Element){
          if (x == NAN) return Integer.MAX_VALUE; 
          else if (x == POSITIVE_INFINITY) return (this == POSITIVE_INFINITY) ? 0 : -1; 
          else if (x == NEGATIVE_INFINITY) return (this == NEGATIVE_INFINITY) ? 0 : 1;
        }
        if(x instanceof Complex){Complex cc=(Complex)x;
          if(cc.im.isZero(ring)) xx=cc.re; else return -x.compareTo(this,ring);}
        if (xx.isInfinite()) {return -xx.compareTo(this, ring);}   
        int xType = xx.numbElementType();
        if((xType>=Ring.R)&& (xType<=Ring.RMinMax))return -xx.compareTo(this,ring);
        if(xType<=Ring.R64MinMax){xx=xx.toNumber(Ring.R64, ring);
           double y = value - ((NumberR64) xx).value;
           if (Math.abs(y) < ring.MachineEpsilonR64.value) return 0;
           return (y > 0) ? 1 : -1;
        }
        return -xx.compareTo(this,ring);
    }

    @Override
    public Boolean isZero(Ring r) {
        double e = r.MachineEpsilonR64.value;
        return ((value < e) && (value > -e)) ? true : false;
    }

    @Override
    public Boolean isOne(Ring r) {
        double v = value - 1.0;
        double e = r.MachineEpsilonR64.value;
        return ((v < e) && (v > -e)) ? true : false;
    }

    @Override
    public Boolean isMinusOne(Ring r) {
        double v = value + 1.0;
        double e = r.MachineEpsilonR64.value;
        return ((v < e) && (v > -e)) ? true : false;
    }

    public static NumberR64 valueOf(NumberZ x) {
        return new NumberR64(x.doubleValue());
    }

    public static NumberR64 valueOf(int x) {
        return new NumberR64((double) x);
    }

    public static NumberR64 valueOf(long x) {
        NumberZ64 z = new NumberZ64(x);
        return new NumberR64(z.doubleValue());
    }

    public static NumberR64 valueOf(NumberR x) {
        return new NumberR64(x.doubleValue());
    }

    public static NumberR64 valueOf(double x) {
        return new NumberR64(x);
    }

    public NumberR64 valOf(NumberZ x) {
        return new NumberR64(x.doubleValue());
    }

    @Override
    public NumberR64 valOf(double x, Ring ring) {
        return new NumberR64(x);
    }

    @Override
    public NumberR64 valOf(int x, Ring ring) {
        return new NumberR64((double) x);
    }

    @Override
    public NumberR64 valOf(long x, Ring ring) {
        NumberZ64 z = new NumberZ64(x);
        return new NumberR64(z.doubleValue());
    }

    public NumberR64 valOf(NumberR x) {
        return new NumberR64(x.doubleValue());
    }

    @Override
    public NumberR64 valOf(String s, Ring ring) {
        return valueOf(s);
    }

    /**
     * Transforms this number of NumberR64 type to given type defined in Ring.
     *
     * @param numberType new type
     *
     * @return this transormed to the new type
     */
    @Override
    public Element toNumber(int numberType, Ring ring) {
        if (numberType < Ring.Complex) {
            switch (numberType) {
                case Ring.Z:
                    return (new NumberR(value)).NumberRtoNumberZ();
                case Ring.Z64:
                    return new NumberZ64(longValue());
                case Ring.Zp32:
                    return new NumberZp32(longValue());
                case Ring.Zp:
                    return new NumberZp((new NumberR(value)).NumberRtoNumberZ(), ring);
                case Ring.R:
                    return new NumberR(value);
                case Ring.R64:
                    return this;
                case Ring.R128:
                    return new NumberR128(value);
                case Ring.C64:
                    return new Complex(value);
                case Ring.Q:
                    double denom = Math.pow(10, ring.FLOATPOS);
                    double dd = value * denom;
                    NumberZ n = (new NumberR(dd)).NumberRtoNumberZ();
                    NumberZ d = (new NumberR(denom)).NumberRtoNumberZ();
                    return new Fraction(n, d).cancel(ring);
                case Ring.R64MaxPlus:
                    return new NumberR64MaxPlus(this);
                case Ring.R64MinPlus:
                    return new NumberR64MinPlus(this);
                case Ring.R64MaxMult:
                    return new NumberR64MaxMult(this);
                case Ring.R64MinMult:
                    return new NumberR64MinMult(this);
                case Ring.R64MaxMin:
                    return new NumberR64MaxMin(this);
                case Ring.R64MinMax:
                    return new NumberR64MinMax(this);
                case Ring.RMaxPlus:
                    return new NumberRMaxPlus(new NumberR(value));
                case Ring.RMinPlus:
                    return new NumberRMinPlus(new NumberR(value));
                case Ring.RMaxMult:
                    return new NumberRMaxMult(new NumberR(value));
                case Ring.RMinMult:
                    return new NumberRMinMult(new NumberR(value));
                case Ring.RMaxMin:
                    return new NumberRMaxMin(new NumberR(value));
                case Ring.RMinMax:
                    return new NumberRMinMax(new NumberR(value));
                case Ring.ZMaxPlus:
                    return new NumberZMaxPlus(new NumberZ(longValue()));
                case Ring.ZMinPlus:
                    return new NumberZMinPlus(new NumberZ(longValue()));
                case Ring.ZMaxMult:
                    return new NumberZMaxMult(new NumberZ(longValue()));
                case Ring.ZMinMult:
                    return new NumberZMinMult(new NumberZ(longValue()));
                case Ring.ZMaxMin:
                    return new NumberZMaxMin(new NumberZ(longValue()));
                case Ring.ZMinMax:
                    return new NumberZMinMax(new NumberZ(longValue()));
            }
        } else if (numberType < Ring.Polynom) {
            Element re = toNumber(numberType - Ring.Complex, ring);
            return new Complex(re, re.zero(ring));
        }
        return null;
    }

    public NumberR64 valOf(Element x) {
        return null;
    }

    @Override
    public int numbElementType() {
        return Ring.R64;
    }

    @Override
    public Element D(Ring ring) {
        return ZERO;
    }

    @Override
    public Element D(int num, Ring ring) {
        return ZERO;
    }

    @Override
    public Element id(Ring r) {
        return this;
    }

    @Override
    public Element ln(Ring r) {
        return new NumberR64(Math.log(doubleValue()));
    }

    @Override
    public Element lg(Ring r) {
        return new NumberR64(Math.log10(doubleValue()));
    }

    @Override
    public Element exp(Ring r) {
        return new NumberR64(Math.exp(doubleValue()));
    }

    @Override
    public Element sqrt(Ring r) {
       NumberR64 res = this.negate(r);
       return (this.isNegative())?
               (r.isComplex())? new Complex(new NumberR64(0),new NumberR64(Math.sqrt(res.value))): NAN
               :  new NumberR64(Math.sqrt(value));
    }

    @Override
    public Element sin(Ring r) {
        double arg = (r.RADIAN.value == 0) ? doubleValue() * doublePIdiv180 : doubleValue();
        return new NumberR64(Math.sin(arg));
    }

    @Override
    public Element cos(Ring r) {
        double arg = (r.RADIAN.value == 0) ? doubleValue() * doublePIdiv180 : doubleValue();
        return new NumberR64(Math.cos(arg));
    }

    @Override
    public Element tan(Ring r) {
        double arg = (r.RADIAN.value == 0) ? doubleValue() * doublePIdiv180 : doubleValue();
        return new NumberR64(Math.tan(arg));
    }

    @Override
    public Element ctg(Ring r) {
        return new NumberR64 (1.0/ tan(r).doubleValue() );
    }

    @Override
    public Element arcsn(Ring r) {
        double arg = Math.asin(doubleValue());
        return (r.RADIAN.value == 0) ? new NumberR64(arg *double180divPI ) : new NumberR64(arg);
    }

    @Override
    public Element arccs(Ring r) {
        double arg = Math.acos(doubleValue());
        return (r.RADIAN.value == 0) ? new NumberR64(arg *double180divPI) : new NumberR64(arg);
    }

    @Override
    public Element arctn(Ring r) {
        double arg = Math.atan(doubleValue());
        return (r.RADIAN.value == 0) ? new NumberR64(arg *double180divPI) : new NumberR64(arg);
    }

    @Override
    public Element arcctn(Ring r) {
        double arg = Math.PI / 2 - Math.atan(doubleValue());
        return (r.RADIAN.value == 0) ? new NumberR64(arg *double180divPI) : new NumberR64(arg);
    }

    @Override
    public Element sh(Ring r) {
        return new NumberR64(Math.sinh(doubleValue()));
    }

    @Override
    public Element ch(Ring r) {
        return new NumberR64(Math.cosh(doubleValue()));
    }

    @Override
    public Element th(Ring r) {
        return new NumberR64(Math.tanh(doubleValue()));
    }

    /**
     * Функция для вычисления функции cth(x) - гиперболического котангенса.
     * Используется представление данной функции через 1/th()
     *
     * @return ctg()
     */
    @Override
    public Element cth(Ring r) {
        return new NumberR64(1.0/th(r).doubleValue());
    }

    /**
     * Процедура вычисления Arsh(x) по формуле Arsh(x)=Ln(x+sqrt(x^2+1))
     */
    @Override
    public Element arsh(Ring r) {
        return new NumberR64(Math.log(doubleValue() + Math.sqrt(doubleValue() * doubleValue() + 1)));
    }

    /**
     * Процедура вычисления Arch(x) по формуле Ln(x+sqrt(x^2-1))
     */
    @Override
    public Element arch(Ring r) {
        return new NumberR64(Math.log(doubleValue() + Math.sqrt(doubleValue() * doubleValue() - 1)));
    }

    /**
     * Процедура вычисления Arth(x) по формуле Arth(x)=Ln((1+x)/(1-x))
     */
    @Override
    public Element arth(Ring r) {
        return new NumberR64(Math.log((doubleValue() + 1) / (1 - doubleValue())));
    }

    /**
     * Процедура вычисления Arcth(x) по формуле Arcth(x)=Ln((1+x)/(x-1))
     */
    @Override
    public Element arcth(Ring r) {
        return new NumberR64(Math.log((doubleValue() + 1) / (doubleValue() - 1)));
    }

    /**
     * Unit step in the place 0
     *
     * @return 1 if x>0; 0 if x<0; 1/2 if x=0.
     */
    @Override
    public NumberR64 unitstep(Ring r) {
        int s = signum();
        return (s == 1) ? NumberR64.ONE : (s == -1) ? NumberR64.ZERO : new NumberR64(0.5);
    }

    /**
     * Unit step in the place a
     *
     * @param a
     *
     * @return 1 if x>a; 0 if x<a; 1/2 if x=a.
     */
    public NumberR64 unitstep(NumberR64 a) {
        return (value - a.value < 0) ? NumberR64.ZERO : NumberR64.ONE;
    }

    /**
     * Unit step in the place a
     *
     * @param a
     *
     * @return 1 if x>=a; 0 if x<a;
     */
    @Override
    public NumberR64 unitstep(Element a, Ring r) {
        NumberR64 aa = (NumberR64) ((a instanceof NumberR64) ? a : a.toNumber(Ring.R64, r));
        return (value - aa.value < 0) ? NumberR64.ZERO : NumberR64.ONE;
    }

    public static NumberR64 fact(Element a) {
        double r = a.doubleValue();
        double x = 1.0, res = 1.0;
        while (x <= r) {
            res *= x;
            x += 1.0;
        }
        return new NumberR64(r);
    }

    public static NumberR64 binomial(Element a, Element b) {
        double ra = a.doubleValue();
        double rb = b.doubleValue();
        double x = 2.0, num = ra, den = 1.0, y = --ra;
        while (x <= rb) {
            den *= x++;
            num *= y--;

        }
        return new NumberR64(num / den);
    }

    @Override
    public Element toNewRing(int Algebra, Ring r) {
        return toNumber(Algebra, r);
    }

    @Override
    public Element GCD(Element x, Ring r) {
        return ONE;
    }

    @Override
    public Element pow(int s, Ring r) {
        return new NumberR64(Math.pow(this.value, s));
    }

    @Override
    public Element pi(Ring r) {
        return PI;
    }

    @Override
    public Element e(Ring r) {
        return E;
    }

    @Override
    public Element factorial(Ring r) { double xx= Math.floor(value);
        if(xx<0)return NAN;
        if(xx<2)return ONE;
        double yy=1.0;
        try {while (xx>1.0) {yy*=xx; xx--;} }
        catch(ArithmeticException ex) {return POSITIVE_INFINITY;}    
        return new NumberR64(yy);
    }

   
    /**NumberR64 negative constant
     * Initialize   constant array when class is loaded.
     * @return NumberR64 negative constant 
     */
    @Override
    public  Element[] negConst() {
        NumberR64 negConst[] = new NumberR64[MAX_CONSTANT + 1];
        for (int i = 1; i <= Element.MAX_CONSTANT; i++) {negConst[i] = new NumberR64(-i); }
        return negConst;
    }    
    /**NumberR64 positive constant
     * Initialize   constant array when class is loaded.
     * @return NumberR64 positive constant 
     */
    @Override
    public  Element[] posConst() {
        NumberR64 posConst[] = new NumberR64[MAX_CONSTANT + 1];
        for (int i = 0; i <= Element.MAX_CONSTANT; i++) {posConst[i] = new NumberR64(i); }
        return posConst;
    }

    @Override
    public Element integrate(int num, Ring ring) {
        return ring.varPolynom[num].multiply(this, ring);
    }

    @Override
    public Element rootOf(int pow, Ring ring) {
        return new NumberR64(Math.pow(value, (1.0 / (double) pow)));
    }

    @Override
    public NumberR64 round(Ring ring) {
        return new NumberR64(Math.rint(value));
    }

    @Override
    public NumberR64 ceil(Ring ring) {
        return new NumberR64(Math.ceil(value));
    }

    @Override
    public NumberR64 floor(Ring ring) {
        return new NumberR64(Math.floor(value));
    }

    @Override
    public NumberR64 mod(Element mod, Ring ring) {
        double modv = ((NumberR64) ((mod.numbElementType() == com.mathpar.number.Ring.R64) ? mod
                : mod.toNumber(Ring.R64, ring))).value;
        double dd = Math.floor(value / modv);
        return new NumberR64(value - dd * modv);
    }

    public NumberR64 mod(NumberR64 mod, Ring ring) {
        double modv = mod.value;
        double dd = Math.floor(value / modv);
        return new NumberR64(value - dd * modv);
    }

    @Override
    public boolean isEven() {
        NumberR64 div2 = divide(NumberR64.valueOf(2));
        return (div2.value - Math.round(div2.value) == 0);
    }
}