/*
 * Z64  ==  Long
 */
package com.mathpar.number;

import java.util.Random;

/**
 * The This class wraps a value of the primitive type <code>long</code> in an
 * object. An object of this type contains a single field whose type is
 * <code>long</code>.
 *
 * <p>
 *
 * In addition, this class provides several methods for converting a
 * <code>long</code> to a <code>String</code> and a <code>String</code> to a
 * <code>long</code>, as well as other constants and methods useful when dealing
 * with a <code>long</code>.
 *
 * <p>
 * Implementation note: The implementations of the "bit twiddling" methods (such
 * as {@link #highestOneBit(long) highestOneBit} and
 * {@link #numberOfTrailingZeros(long) numberOfTrailingZeros}) are based on
 * material from Henry S. Warren, Jr.'s <i>Hacker's Delight</i>, (Addison
 * Wesley, 2002).
 *
 * @author Lee Boynton
 * @author Arthur van Hoff
 * @author Josh Bloch
 * @version 1.81, 11/17/05
 * @since JDK1.0
 * @version 2.0 05/05/06
 * @author Mathparca LTD
 */
public class NumberZ64 extends Element {
    /**
     * A constant holding the minimum value a <code>long</code> can have,
     * -2<sup>63</sup>.
     */
    public static final long MIN_VALUE = 0x8000000000000000L;
    /**
     * A constant holding the maximum value a <code>long</code> can have,
     * 2<sup>63</sup>-1.
     */
    public static final long MAX_VALUE = 0x7fffffffffffffffL;

    public static final NumberZ64 ZERO = new NumberZ64(0L);
    public static final NumberZ64 ONE = new NumberZ64(1L);
    public static final NumberZ64 MINUS_ONE = new NumberZ64(-1L);

    /**
     * The value of the <code>Long</code>.
     *
     * @serial
     */
    public long value;

    public NumberZ64() {
        value = 0L;
    }

    /**
     * Constructs a newly allocated <code>Long</code> object that represents the
     * specified <code>long</code> argument.
     *
     * @param value the value to be represented by the <code>Long</code> object.
     */
    public NumberZ64(long value) {
        this.value = value;
    }

    /**
     * Constructs a newly allocated <code>Long</code> object that represents the
     * <code>long</code> value indicated by the <code>String</code> parameter.
     * The string is converted to a <code>long</code> value in exactly the
     * manner used by the <code>parseLong</code> method for radix 10.
     *
     * @param s the <code>String</code> to be converted to a <code>Long</code>.
     *
     * @exception NumberFormatException if the <code>String</code> does not
     * contain a parsable <code>long</code>.
     * @see java.lang.Long#parseLong(java.lang.String, int)
     */
    public NumberZ64(boolean x) {
        this.value = x ? 1L : 0L;
    }

    public NumberZ64(String s) throws NumberFormatException {
        this.value = parseLong(s, 10);
    }

    /**
     * Returns a string representation of the first argument in the radix
     * specified by the second argument.
     * <p>
     * If the radix is smaller than <code>Character.MIN_RADIX</code> or larger
     * than <code>Character.MAX_RADIX</code>, then the radix <code>10</code> is
     * used instead.
     * <p>
     * If the first argument is negative, the first element of the result is the
     * ASCII minus sign <code>'-'</code> ( <code>'&#92;u002d'</code>). If the
     * first argument is not negative, no sign character appears in the result.
     * <p>
     * The remaining characters of the result represent the magnitude of the
     * first argument. If the magnitude is zero, it is represented by a single
     * zero character <code>'0'</code> ( <code>'&#92;u0030'</code>); otherwise,
     * the first character of the representation of the magnitude will not be
     * the zero character. The following ASCII characters are used as digits:
     * null null     <blockquote><pre>
     *   0123456789abcdefghijklmnopqrstuvwxyz
     * </pre></blockquote> These are <code>'&#92;u0030'</code> through
     * <code>'&#92;u0039'</code> and <code>'&#92;u0061'</code> through
     * <code>'&#92;u007a'</code>. If <code>radix</code> is <var>N</var>, then
     * the first <var>N</var> of these characters are used as radix-<var>N</var>
     * digits in the order shown. Thus, the digits for hexadecimal (radix 16)
     * are <code>0123456789abcdef</code>. If uppercase letters are desired, the
     * {@link java.lang.String#toUpperCase()} method may be called on the
     * result: null null     <blockquote><pre>
     * Long.toString(n, 16).toUpperCase()
     * </pre></blockquote>
     *
     * @param i a <code>long</code>to be converted to a string.
     * @param radix the radix to use in the string representation.
     *
     * @return a string representation of the argument in the specified radix.
     *
     * @see java.lang.Character#MAX_RADIX
     * @see java.lang.Character#MIN_RADIX
     */
    public static String toString(long i, int radix) {
        return java.lang.Long.toString(i, radix);
    }

    /**
     * Returns a string representation of the <code>long</code> argument as an
     * unsigned integer in base&nbsp;16.
     * <p>
     * The unsigned <code>long</code> value is the argument plus 2<sup>64</sup>
     * if the argument is negative; otherwise, it is equal to the argument. This
     * value is converted to a string of ASCII digits in hexadecimal
     * (base&nbsp;16) with no extra leading <code>0</code>s. If the unsigned
     * magnitude is zero, it is represented by a single zero character
     * <code>'0'</code> ( <code>'&#92;u0030'</code>); otherwise, the first
     * character of the representation of the unsigned magnitude will not be the
     * zero character. The following characters are used as hexadecimal digits:
     * null null     <blockquote><pre>
     * 0123456789abcdef
     * </pre></blockquote> These are the characters <code>'&#92;u0030'</code>
     * through <code>'&#92;u0039'</code> and <code>'&#92;u0061'</code> through
     * <code>'&#92;u0066'</code>. If uppercase letters are desired, the
     * {@link java.lang.String#toUpperCase()} method may be called on the
     * result: null null     <blockquote><pre>
     * Long.toHexString(n).toUpperCase()
     * </pre></blockquote>
     *
     * @param i a <code>long</code> to be converted to a string.
     *
     * @return the string representation of the unsigned <code>long</code> value
     * represented by the argument in hexadecimal (base&nbsp;16).
     *
     * @since JDK 1.0.2
     */
    public static String toHexString(long i) {
        return java.lang.Long.toHexString(i);
    }

    /**
     * Returns a string representation of the <code>long</code> argument as an
     * unsigned integer in base&nbsp;8.
     * <p>
     * The unsigned <code>long</code> value is the argument plus 2<sup>64</sup>
     * if the argument is negative; otherwise, it is equal to the argument. This
     * value is converted to a string of ASCII digits in octal (base&nbsp;8)
     * with no extra leading <code>0</code>s.
     * <p>
     * If the unsigned magnitude is zero, it is represented by a single zero
     * character <code>'0'</code> ( <code>'&#92;u0030'</code>); otherwise, the
     * first character of the representation of the unsigned magnitude will not
     * be the zero character. The following characters are used as octal digits:
     * null null     <blockquote><pre>
     * 01234567
     * </pre></blockquote> These are the characters <code>'&#92;u0030'</code>
     * through <code>'&#92;u0037'</code>.
     *
     * @param i a <code>long</code> to be converted to a string.
     *
     * @return the string representation of the unsigned <code>long</code> value
     * represented by the argument in octal (base&nbsp;8).
     *
     * @since JDK 1.0.2
     */
    public static String toOctalString(long i) {
        return java.lang.Long.toOctalString(i);
    }

    /**
     * Returns a string representation of the <code>long</code> argument as an
     * unsigned integer in base&nbsp;2.
     * <p>
     * The unsigned <code>long</code> value is the argument plus 2<sup>64</sup>
     * if the argument is negative; otherwise, it is equal to the argument. This
     * value is converted to a string of ASCII digits in binary (base&nbsp;2)
     * with no extra leading <code>0</code>s. If the unsigned magnitude is zero,
     * it is represented by a single zero character <code>'0'</code> (
     * <code>'&#92;u0030'</code>); otherwise, the first character of the
     * representation of the unsigned magnitude will not be the zero character.
     * The characters <code>'0'</code> ( <code>'&#92;u0030'</code>) and
     * <code>'1'</code> ( <code>'&#92;u0031'</code>) are used as binary digits.
     *
     * @param i a <code>long</code> to be converted to a string.
     *
     * @return the string representation of the unsigned <code>long</code> value
     * represented by the argument in binary (base&nbsp;2).
     *
     * @since JDK 1.0.2
     */
    public static String toBinaryString(long i) {
        return java.lang.Long.toBinaryString(i);
    }

    /**
     * Returns a <code>String</code> object representing the specified
     * <code>long</code>. The argument is converted to signed decimal
     * representation and returned as a string, exactly as if the argument and
     * the radix 10 were given as arguments to the {@link
     * #toString(long, int)} method.
     *
     * @param i a <code>long</code> to be converted.
     *
     * @return a string representation of the argument in base&nbsp;10.
     */
    public static String toString(long i) {
        return java.lang.Long.toString(i);
    }

    /**
     * Double which  equals number 1/2 in the power of this positive value (from 1 to 1023)
     * @return double value
     */
    public double get2inPowMinThis(){
        if ((value<0 )||(value>1023))return Double.NaN;
        return Double.longBitsToDouble( 0x3FF0000000000000L- 0x0010000000000000L*value ); }
    
    // Requires positive x
    static int stringSize(long x) {
        long p = 10;
        for (int i = 1; i < 19; i++) {
            if (x < p) {
                return i;
            }
            p = 10 * p;
        }
        return 19;
    }

    /**
     * Parses the string argument as a signed <code>long</code> in the radix
     * specified by the second argument. The characters in the string must all
     * be digits of the specified radix (as determined by whether
     * {@link java.lang.Character#digit(char, int)} returns a nonnegative
     * value), except that the first character may be an ASCII minus sign
     * <code>'-'</code> ( <code>'&#92;u002D'</code>) to indicate a negative
     * value. The resulting <code>long</code> value is returned.
     * <p>
     * Note that neither the character <code>L</code> (
     * <code>'&#92;u004C'</code>) nor <code>l</code> (
     * <code>'&#92;u006C'</code>) is permitted to appear at the end of the
     * string as a type indicator, as would be permitted in Java programming
     * language source code - except that either <code>L</code> or
     * <code>l</code> may appear as a digit for a radix greater than 22.
     * <p>
     * An exception of type <code>NumberFormatException</code> is thrown if any
     * of the following situations occurs: <ul> <li>The first argument is
     * <code>null</code> or is a string of length zero. <li>The
     * <code>radix</code> is either smaller than {@link
     * java.lang.Character#MIN_RADIX} or larger than {@link
     * java.lang.Character#MAX_RADIX}. <li>Any character of the string is not a
     * digit of the specified radix, except that the first character may be a
     * minus sign <code>'-'</code> ( <code>'&#92;u002d'</code>) provided that
     * the string is longer than length 1. <li>The value represented by the
     * string is not a value of type <code>long</code>. </ul><p>
     * Examples: null null     <blockquote><pre>
     * parseLong("0", 10) returns 0L
     * parseLong("473", 10) returns 473L
     * parseLong("-0", 10) returns 0L
     * parseLong("-FF", 16) returns -255L
     * parseLong("1100110", 2) returns 102L
     * parseLong("99", 8) throws a NumberFormatException
     * parseLong("Hazelnut", 10) throws a NumberFormatException
     * parseLong("Hazelnut", 36) returns 1356099454469L
     * </pre></blockquote>
     *
     * @param s the <code>String</code> containing the <code>long</code>
     * representation to be parsed.
     * @param radix the radix to be used while parsing <code>s</code>.
     *
     * @return the <code>long</code> represented by the string argument in the
     * specified radix.
     *
     * @exception NumberFormatException if the string does not contain a
     * parsable <code>long</code>.
     */
    public static long parseLong(String s, int radix)
            throws NumberFormatException {
        return java.lang.Long.parseLong(s, radix);
    }

    /**
     * Parses the string argument as a signed decimal <code>long</code>. The
     * characters in the string must all be decimal digits, except that the
     * first character may be an ASCII minus sign <code>'-'</code> (
     * <code>&#92;u002D'</code>) to indicate a negative value. The resulting
     * <code>long</code> value is returned, exactly as if the argument and the
     * radix <code>10</code> were given as arguments to the {@link
     * #parseLong(java.lang.String, int)} method.
     * <p>
     * Note that neither the character <code>L</code> (
     * <code>'&#92;u004C'</code>) nor <code>l</code> (
     * <code>'&#92;u006C'</code>) is permitted to appear at the end of the
     * string as a type indicator, as would be permitted in Java programming
     * language source code.
     *
     * @param s a <code>String</code> containing the <code>long</code>
     * representation to be parsed
     *
     * @return the <code>long</code> represented by the argument in decimal.
     *
     * @exception NumberFormatException if the string does not contain a
     * parsable <code>long</code>.
     */
    public static long parseLong(String s) throws NumberFormatException {
        return parseLong(s, 10);
    }

    /**
     * Returns a <code>Long</code> object holding the value extracted from the
     * specified <code>String</code> when parsed with the radix given by the
     * second argument. The first argument is interpreted as representing a
     * signed <code>long</code> in the radix specified by the second argument,
     * exactly as if the arguments were given to the {@link
     * #parseLong(java.lang.String, int)} method. The result is a
     * <code>Long</code> object that represents the <code>long</code> value
     * specified by the string.
     * <p>
     * In other words, this method returns a <code>Long</code> object equal to
     * the value of:
     *
     * <blockquote><code>
     * new Long(Long.parseLong(s, radix))
     * </code></blockquote>
     *
     * @param s the string to be parsed
     * @param radix the radix to be used in interpreting <code>s</code>
     *
     * @return a <code>Long</code> object holding the value represented by the
     * string argument in the specified radix.
     *
     * @exception NumberFormatException If the <code>String</code> does not
     * contain a parsable <code>long</code>.
     */
    public static NumberZ64 valueOf(String s, int radix) throws NumberFormatException {
        return new NumberZ64(parseLong(s, radix));
    }

    /**
     * Returns a <code>Long</code> object holding the value of the specified
     * <code>String</code>. The argument is interpreted as representing a signed
     * decimal <code>long</code>, exactly as if the argument were given to the {@link
     * #parseLong(java.lang.String)} method. The result is a <code>Long</code>
     * object that represents the integer value specified by the string.
     * <p>
     * In other words, this method returns a <code>Long</code> object equal to
     * the value of:
     *
     * <blockquote><pre>
     * new Long(Long.parseLong(s))
     * </pre></blockquote>
     *
     * @param s the string to be parsed.
     *
     * @return a <code>Long</code> object holding the value represented by the
     * string argument.
     *
     * @exception NumberFormatException If the string cannot be parsed as * a
     * <code>long</code>.
     */
    public static NumberZ64 valueOf(String s) throws NumberFormatException {
        return new NumberZ64(parseLong(s, 10));
    }

    private static class LongCache {

        private LongCache() {
        }
        static final NumberZ64 cache[] = new NumberZ64[-(-128) + 127 + 1];

        static {
            for (int i = 0; i < cache.length; i++) {
                cache[i] = new NumberZ64(i - 128);
            }
        }
    }

    /**
     * Returns a <tt>Long</tt> instance representing the specified <tt>long</tt>
     * value. If a new <tt>Long</tt> instance is not required, this method
     * should generally be used in preference to the constructor
     * {@link #Long(long)}, as this method is likely to yield significantly
     * better space and time performance by caching frequently requested values.
     *
     * @param l a long value.
     *
     * @return a <tt>Long</tt> instance representing <tt>l</tt>.
     *
     * @since 1.5
     */
    public static NumberZ64 valueOf(long l) {
        final int offset = 128;
        if (l >= -128 && l <= 127) { // will cache
            return LongCache.cache[(int) l + offset];
        }
        return new NumberZ64(l);
    }

    /**
     * Decodes a <code>String</code> into a <code>Long</code>. Accepts decimal,
     * hexadecimal, and octal numbers given by the following grammar:
     *
     * <blockquote> <dl> <dt><i>DecodableString:</i> <dd><i>Sign<sub>opt</sub>
     * DecimalNumeral</i> <dd><i>Sign<sub>opt</sub></i>
     * <code>0x</code> <i>HexDigits</i> <dd><i>Sign<sub>opt</sub></i>
     * <code>0X</code> <i>HexDigits</i> <dd><i>Sign<sub>opt</sub></i>
     * <code>#</code> <i>HexDigits</i> <dd><i>Sign<sub>opt</sub></i>
     * <code>0</code> <i>OctalDigits</i>
     * <p>
     * <dt><i>Sign:</i> <dd><code>-</code>
     * </dl> </blockquote>
     *
     * <i>DecimalNumeral</i>, <i>HexDigits</i>, and <i>OctalDigits</i> are
     * defined in <a
     * href="http://java.sun.com/docs/books/jls/second_edition/html/lexical.doc.html#48282">&sect;3.10.1</a>
     * of the <a href="http://java.sun.com/docs/books/jls/html/">Java Language
     * Specification</a>.
     * <p>
     * The sequence of characters following an (optional) negative sign and/or
     * radix specifier (&quot; <code>0x</code>&quot;, &quot;
     * <code>0X</code>&quot;, &quot; <code>#</code>&quot;, or leading zero) is
     * parsed as by the <code>Long.parseLong</code> method with the indicated
     * radix (10, 16, or 8). This sequence of characters must represent a
     * positive value or a {@link
     * NumberFormatException} will be thrown. The result is negated if first
     * character of the specified <code>String</code> is the minus sign. No
     * whitespace characters are permitted in the <code>String</code>.
     *
     * @param nm the <code>String</code> to decode.
     *
     * @return a <code>Long</code> object holding the <code>long</code> value
     * represented by <code>nm</code>
     *
     * @exception NumberFormatException if the <code>String</code> does not
     * contain a parsable <code>long</code>.
     * @see java.lang.Long#parseLong(String, int)
     * @since 1.2
     */
    public static NumberZ64 decode(String nm) throws NumberFormatException {
        int radix = 10;
        int index = 0;
        boolean negative = false;
        NumberZ64 result;

        // Handle minus sign, if present
        if (nm.startsWith("-")) {
            negative = true;
            index++;
        }

        // Handle radix specifier, if present
        if (nm.startsWith("0x", index) || nm.startsWith("0X", index)) {
            index += 2;
            radix = 16;
        } else if (nm.startsWith("#", index)) {
            index++;
            radix = 16;
        } else if (nm.startsWith("0", index) && nm.length() > 1 + index) {
            index++;
            radix = 8;
        }

        if (nm.startsWith("-", index)) {
            throw new NumberFormatException("Negative sign in wrong position");
        }

        try {
            result = NumberZ64.valueOf(nm.substring(index), radix);
            result = negative ? new NumberZ64(-result.longValue()) : result;
        } catch (NumberFormatException e) {
            // If number is Long.MIN_VALUE, we'll end up here. The next line
            // handles this case, and causes any genuine format error to be
            // rethrown.
            String constant = negative ? new String("-" + nm.substring(index))
                    : nm.substring(index);
            result = NumberZ64.valueOf(constant, radix);
        }
        return result;
    }

    public int getIntvalue() {
        return (int) value;
    }

    /**
     * Returns the value of this <code>Long</code> as a <code>byte</code>.
     */
    public byte byteValue() {
        return (byte) value;
    }

    /**
     * Returns the value of this <code>Long</code> as a <code>short</code>.
     */
    public short shortValue() {
        return (short) value;
    }

    /**
     * Returns the value of this <code>Long</code> as an <code>int</code>.
     */
    public int intValue() {
        return (int) value;
    }

    /**
     * Returns the value of this <code>Long</code> as a <code>long</code> value.
     */
    @Override
    public long longValue() {
        return value;
    }

    /**
     * Returns the value of this <code>Long</code> as a <code>float</code>.
     */
    public float floatValue() {
        return (float) value;
    }

    /**
     * Returns the value of this <code>Long</code> as a <code>double</code>.
     */
    public double doubleValue() {
        return (double) value;
    }

    /**
     * Returns a <code>String</code> object representing this
     * <code>Long</code>'s value. The value is converted to signed decimal
     * representation and returned as a string, exactly as if the
     * <code>long</code> value were given as an argument to the
     * {@link java.lang.Long#toString(long)} method.
     *
     * @return a string representation of the value of this object in
     * base&nbsp;10.
     */
    @Override
    public String toString() {
        long v = value;
        return String.valueOf(v);
    }

    @Override
    public String toString(Ring r) {
        return toString();
    }

    /**
     * Returns a hash code for this <code>Long</code>. The result is the
     * exclusive OR of the two halves of the primitive <code>long</code> value
     * held by this <code>Long</code> object. That is, the hashcode is the value
     * of the expression: null null     <blockquote><pre>
     * (int)(this.longValue()^(this.longValue()&gt;&gt;&gt;32))
     * </pre></blockquote>
     *
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }

    /**
     * Compares this object to the specified object. The result is
     * <code>true</code> if and only if the argument is not <code>null</code>
     * and is a <code>Long</code> object that contains the same
     * <code>long</code> value as this object.
     *
     * @param obj the object to compare with.
     *
     * @return  <code>true</code> if the objects are the same; <code>false</code>
     * otherwise.
     */
    @Override
    public boolean equals(Element obj, Ring ring) {
        if (obj instanceof NumberZ64) {
            return value == ((NumberZ64) obj).longValue();
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NumberZ64) {
            return equals((NumberZ64) obj, null);
        }
        return false;
    }

    /**
     * Determines the <code>long</code> value of the system property with the
     * specified name.
     * <p>
     * The first argument is treated as the name of a system property. System
     * properties are accessible through the {@link
     * java.lang.System#getProperty(java.lang.String)} method. The string value
     * of this property is then interpreted as a <code>long</code> value and a
     * <code>Long</code> object representing this value is returned. Details of
     * possible numeric formats can be found with the definition of
     * <code>getProperty</code>.
     * <p>
     * If there is no property with the specified name, if the specified name is
     * empty or <code>null</code>, or if the property does not have the correct
     * numeric format, then <code>null</code> is returned.
     * <p>
     * In other words, this method returns a <code>Long</code> object equal to
     * the value of: null null     <blockquote><code>
     * getLong(nm, null)
     * </code></blockquote>
     *
     * @param nm property name.
     *
     * @return the <code>Long</code> value of the property.
     *
     * @see java.lang.System#getProperty(java.lang.String)
     * @see java.lang.System#getProperty(java.lang.String, java.lang.String)
     */
    public static NumberZ64 getLong(String nm) {
        return getLong(nm, null);
    }

    /**
     * Determines the <code>long</code> value of the system property with the
     * specified name.
     * <p>
     * The first argument is treated as the name of a system property. System
     * properties are accessible through the {@link
     * java.lang.System#getProperty(java.lang.String)} method. The string value
     * of this property is then interpreted as a <code>long</code> value and a
     * <code>Long</code> object representing this value is returned. Details of
     * possible numeric formats can be found with the definition of
     * <code>getProperty</code>.
     * <p>
     * The second argument is the default value. A <code>Long</code> object that
     * represents the value of the second argument is returned if there is no
     * property of the specified name, if the property does not have the correct
     * numeric format, or if the specified name is empty or null.
     * <p>
     * In other words, this method returns a <code>Long</code> object equal to
     * the value of: null null     <blockquote><code>
     * getLong(nm, new Long(val))
     * </code></blockquote> but in practice it may be implemented in a manner
     * such as: null null     <blockquote><pre>
     * Long result = getLong(nm, null);
     * return (result == null) ? new Long(val) : result;
     * </pre></blockquote> to avoid the unnecessary allocation of a
     * <code>Long</code> object when the default value is not needed.
     *
     * @param nm property name.
     * @param val default value.
     *
     * @return the <code>Long</code> value of the property.
     *
     * @see java.lang.System#getProperty(java.lang.String)
     * @see java.lang.System#getProperty(java.lang.String, java.lang.String)
     */
    public static NumberZ64 getLong(String nm, long val) {
        NumberZ64 result = NumberZ64.getLong(nm, null);
        return (result == null) ? new NumberZ64(val) : result;
    }

    /**
     * Returns the <code>long</code> value of the system property with the
     * specified name. The first argument is treated as the name of a system
     * property. System properties are accessible through the
     * {@link java.lang.System#getProperty(java.lang.String)} method. The string
     * value of this property is then interpreted as a <code>long</code> value,
     * as per the <code>Long.decode</code> method, and a <code>Long</code>
     * object representing this value is returned.
     * <p>
     * <ul>
     * <li>If the property value begins with the two ASCII characters
     * <code>0x</code> or the ASCII character <code>#</code>, not followed by a
     * minus sign, then the rest of it is parsed as a hexadecimal integer
     * exactly as for the method {@link #valueOf(java.lang.String, int)} with
     * radix 16. <li>If the property value begins with the ASCII character
     * <code>0</code> followed by another character, it is parsed as an octal
     * integer exactly as by the method {@link
     * #valueOf(java.lang.String, int)} with radix 8. <li>Otherwise the property
     * value is parsed as a decimal integer exactly as by the method
     * {@link #valueOf(java.lang.String, int)} with radix 10. </ul>
     * <p>
     * Note that, in every case, neither <code>L</code> (
     * <code>'&#92;u004C'</code>) nor <code>l</code> (
     * <code>'&#92;u006C'</code>) is permitted to appear at the end of the
     * property value as a type indicator, as would be permitted in Java
     * programming language source code.
     * <p>
     * The second argument is the default value. The default value is returned
     * if there is no property of the specified name, if the property does not
     * have the correct numeric format, or if the specified name is empty or
     * <code>null</code>.
     *
     * @param nm property name.
     * @param val default value.
     *
     * @return the <code>Long</code> value of the property.
     *
     * @see java.lang.System#getProperty(java.lang.String)
     * @see java.lang.System#getProperty(java.lang.String, java.lang.String)
     * @see java.lang.Long#decode
     */
    public static NumberZ64 getLong(String nm, NumberZ64 val) {
        String v = null;
        try {
            v = System.getProperty(nm);
        } catch (IllegalArgumentException e) {
        } catch (NullPointerException e) {
        }
        if (v != null) {
            try {
                return NumberZ64.decode(v);
            } catch (NumberFormatException e) {
            }
        }
        return val;
    }

    /**
     * Compares two <code>Long</code> objects numerically.
     *
     * @param anotherLong the <code>Long</code> to be compared.
     *
     * @return	the value <code>0</code> if this <code>Long</code> is equal to
     * the argument <code>Long</code>; a value less than <code>0</code> if *
     * this <code>Long</code> is numerically less than the * argument
     * <code>Long</code>; and a value greater than <code>0</code> if this
     * <code>Long</code> is numerically greater than the argument
     * <code>Long</code> (signed comparison).
     *
     * @since 1.2
     */
    @Override
    public int compareTo(Element val, Ring ring) {
        if ((val == NAN) || (this == NAN)) {
            return Integer.MAX_VALUE;
        }
        if (val == POSITIVE_INFINITY) {
            return (this == POSITIVE_INFINITY) ? 0 : -1;
        }
        if (this == POSITIVE_INFINITY) {
            return 1;
        }
        if (val == NEGATIVE_INFINITY) {
            return (this == NEGATIVE_INFINITY) ? 0 : 1;
        }
        if (this == NEGATIVE_INFINITY) {
            return -1;
        }
        if (val.numbElementType() > com.mathpar.number.Ring.Z64) {
            return 1;
        }
        if (val.numbElementType() < com.mathpar.number.Ring.Z64) {
            return -1;
        }
        return compareTo((NumberZ64) val);
    }

    public int compareTo(Element o) {
        return compareTo(o, null);
    }

    public int compareTo(NumberZ64 anotherLong) {
        long thisVal = this.value;
        long anotherVal = anotherLong.value;
        return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
    }
    /**
     * The number of bits used to represent a <tt>long</tt> value in two's
     * complement binary form.
     *
     * @since 1.5
     */
    public static final int SIZE = 64;

    /**
     * Returns a <tt>long</tt> value with at most a single one-bit, in the
     * position of the highest-order ("leftmost") one-bit in the specified
     * <tt>long</tt> value. Returns zero if the specified value has no one-bits
     * in its two's complement binary representation, that is, if it is equal to
     * zero.
     *
     * @return a <tt>long</tt> value with a single one-bit, in the position of
     * the highest-order one-bit in the specified value, or zero if the
     * specified value is itself equal to zero.
     *
     * @since 1.5
     */
    public static long highestOneBit(long i) {
        // HD, Figure 3-1
        i |= (i >> 1);
        i |= (i >> 2);
        i |= (i >> 4);
        i |= (i >> 8);
        i |= (i >> 16);
        i |= (i >> 32);
        return i - (i >>> 1);
    }

    /**
     * Returns a <tt>long</tt> value with at most a single one-bit, in the
     * position of the lowest-order ("rightmost") one-bit in the specified
     * <tt>long</tt> value. Returns zero if the specified value has no one-bits
     * in its two's complement binary representation, that is, if it is equal to
     * zero.
     *
     * @return a <tt>long</tt> value with a single one-bit, in the position of
     * the lowest-order one-bit in the specified value, or zero if the specified
     * value is itself equal to zero.
     *
     * @since 1.5
     */
    public static long lowestOneBit(long i) {
        // HD, Section 2-1
        return i & -i;
    }

    /**
     * Returns the number of zero bits preceding the highest-order ("leftmost")
     * one-bit in the two's complement binary representation of the specified
     * <tt>long</tt> value. Returns 64 if the specified value has no one-bits in
     * its two's complement representation, in other words if it is equal to
     * zero.
     *
     * <p>
     * Note that this method is closely related to the logarithm base 2. For all
     * positive <tt>long</tt> values x: <ul> <li>floor(log<sub>2</sub>(x)) =
     * <tt>63 - numberOfLeadingZeros(x)</tt> <li>ceil(log<sub>2</sub>(x)) =
     * <tt>64 - numberOfLeadingZeros(x - 1)</tt> </ul>
     *
     * @return the number of zero bits preceding the highest-order ("leftmost")
     * one-bit in the two's complement binary representation of the specified
     * <tt>long</tt> value, or 64 if the value is equal to zero.
     *
     * @since 1.5
     */
    public static int numberOfLeadingZeros(long i) {
        // HD, Figure 5-6
        if (i == 0) {
            return 64;
        }
        int n = 1;
        int x = (int) (i >>> 32);
        if (x == 0) {
            n += 32;
            x = (int) i;
        }
        if (x >>> 16 == 0) {
            n += 16;
            x <<= 16;
        }
        if (x >>> 24 == 0) {
            n += 8;
            x <<= 8;
        }
        if (x >>> 28 == 0) {
            n += 4;
            x <<= 4;
        }
        if (x >>> 30 == 0) {
            n += 2;
            x <<= 2;
        }
        n -= x >>> 31;
        return n;
    }

    /**
     * Returns the number of zero bits following the lowest-order ("rightmost")
     * one-bit in the two's complement binary representation of the specified
     * <tt>long</tt> value. Returns 64 if the specified value has no one-bits in
     * its two's complement representation, in other words if it is equal to
     * zero.
     *
     * @return the number of zero bits following the lowest-order ("rightmost")
     * one-bit in the two's complement binary representation of the specified
     * <tt>long</tt> value, or 64 if the value is equal to zero.
     *
     * @since 1.5
     */
    public static int numberOfTrailingZeros(long i) {
        // HD, Figure 5-14
        int x, y;
        if (i == 0) {
            return 64;
        }
        int n = 63;
        y = (int) i;
        if (y != 0) {
            n = n - 32;
            x = y;
        } else {
            x = (int) (i >>> 32);
        }
        y = x << 16;
        if (y != 0) {
            n = n - 16;
            x = y;
        }
        y = x << 8;
        if (y != 0) {
            n = n - 8;
            x = y;
        }
        y = x << 4;
        if (y != 0) {
            n = n - 4;
            x = y;
        }
        y = x << 2;
        if (y != 0) {
            n = n - 2;
            x = y;
        }
        return n - ((x << 1) >>> 31);
    }

    /**
     * Returns the number of one-bits in the two's complement binary
     * representation of the specified <tt>long</tt> value. This function is
     * sometimes referred to as the <i>population count</i>.
     *
     * @return the number of one-bits in the two's complement binary
     * representation of the specified <tt>long</tt> value.
     *
     * @since 1.5
     */
    public static int bitCount(long i) {
        // HD, Figure 5-14
        i = i - ((i >>> 1) & 0x5555555555555555L);
        i = (i & 0x3333333333333333L) + ((i >>> 2) & 0x3333333333333333L);
        i = (i + (i >>> 4)) & 0x0f0f0f0f0f0f0f0fL;
        i = i + (i >>> 8);
        i = i + (i >>> 16);
        i = i + (i >>> 32);
        return (int) i & 0x7f;
    }

    /**
     * Returns the value obtained by rotating the two's complement binary
     * representation of the specified <tt>long</tt> value left by the specified
     * number of bits. (Bits shifted out of the left hand, or high-order, side
     * reenter on the right, or low-order.)
     *
     * <p>
     * Note that left rotation with a negative distance is equivalent to right
     * rotation: <tt>rotateLeft(val, -distance) == rotateRight(val,
     * distance)</tt>. Note also that rotation by any multiple of 64 is a no-op,
     * so all but the last six bits of the rotation distance can be ignored,
     * even if the distance is negative: <tt>rotateLeft(val, distance) ==
     * rotateLeft(val, distance & 0x3F)</tt>.
     *
     * @return the value obtained by rotating the two's complement binary
     * representation of the specified <tt>long</tt> value left by the specified
     * number of bits.
     *
     * @since 1.5
     */
    public static long rotateLeft(long i, int distance) {
        return (i << distance) | (i >>> -distance);
    }

    /**
     * Returns the value obtained by rotating the two's complement binary
     * representation of the specified <tt>long</tt> value right by the
     * specified number of bits. (Bits shifted out of the right hand, or
     * low-order, side reenter on the left, or high-order.)
     *
     * <p>
     * Note that right rotation with a negative distance is equivalent to left
     * rotation: <tt>rotateRight(val, -distance) == rotateLeft(val,
     * distance)</tt>. Note also that rotation by any multiple of 64 is a no-op,
     * so all but the last six bits of the rotation distance can be ignored,
     * even if the distance is negative: <tt>rotateRight(val, distance) ==
     * rotateRight(val, distance & 0x3F)</tt>.
     *
     * @return the value obtained by rotating the two's complement binary
     * representation of the specified <tt>long</tt> value right by the
     * specified number of bits.
     *
     * @since 1.5
     */
    public static long rotateRight(long i, int distance) {
        return (i >>> distance) | (i << -distance);
    }

    /**
     * Returns the value obtained by reversing the order of the bits in the
     * two's complement binary representation of the specified <tt>long</tt>
     * value.
     *
     * @return the value obtained by reversing order of the bits in the
     * specified <tt>long</tt> value.
     *
     * @since 1.5
     */
    public static long reverse(long i) {
        // HD, Figure 7-1
        i = (i & 0x5555555555555555L) << 1 | (i >>> 1) & 0x5555555555555555L;
        i = (i & 0x3333333333333333L) << 2 | (i >>> 2) & 0x3333333333333333L;
        i = (i & 0x0f0f0f0f0f0f0f0fL) << 4 | (i >>> 4) & 0x0f0f0f0f0f0f0f0fL;
        i = (i & 0x00ff00ff00ff00ffL) << 8 | (i >>> 8) & 0x00ff00ff00ff00ffL;
        i = (i << 48) | ((i & 0xffff0000L) << 16)
                | ((i >>> 16) & 0xffff0000L) | (i >>> 48);
        return i;
    }

    /**
     * Returns the signum function of the specified <tt>long</tt> value. (The
     * return value is -1 if the specified value is negative; 0 if the specified
     * value is zero; and 1 if the specified value is positive.)
     *
     * @return the signum function of the specified <tt>long</tt> value.
     *
     * @since 1.5
     */
    public static int signum(long i) {
        // HD, Section 2-7
        return (int) ((i >> 63) | (-i >>> 63));
    }

    /**
     * Returns the value obtained by reversing the order of the bytes in the
     * two's complement representation of the specified <tt>long</tt> value.
     *
     * @return the value obtained by reversing the bytes in the specified
     * <tt>long</tt> value.
     *
     * @since 1.5
     */
    public static long reverseBytes(long i) {
        i = (i & 0x00ff00ff00ff00ffL) << 8 | (i >>> 8) & 0x00ff00ff00ff00ffL;
        return (i << 48) | ((i & 0xffff0000L) << 16)
                | ((i >>> 16) & 0xffff0000L) | (i >>> 48);
    }

    public Element myMinus_one() {
        return MINUS_ONE;
    }

    @Override
    public Element myMinus_one(Ring ring) {
        return MINUS_ONE;
    }

    @Override
    public Element myOne(Ring ring) {
        return ONE;
    }

    @Override
    public Element myZero(Ring ring) {
        return ZERO;
    }

    @Override
    public Element myZero() {
        return ZERO;
    }

    public Element minus_one() {
        return MINUS_ONE;
    }

    @Override
    public Element one(Ring ring) {
        return ONE;
    }

    @Override
    public Element zero(Ring ring) {
        return ZERO;
    }

    public NumberZ64 random(int[] randomType, Random rnd, Element itsCoeffOne) {
        int nbits = randomType[randomType.length - 2];
        return new NumberZ64((long) rnd.nextInt(1 << nbits));
    }

    public NumberR BigDecimalValue() {
        return new NumberR(value);
    }

    public NumberZ BigIntegerValue() {
        return (new NumberR(value).NumberRtoNumberZ());
    }

    public NumberZ64 negate() {
        return new NumberZ64(-value);
    }

    @Override
    public Element negate(Ring r) {
        return new NumberZ64(-value);
    }

    @Override
    public int numbElementType() {
        return Ring.Z64;
    }

    @Override
    public VectorS extendedGCD(Element x, Ring ring) { 
        if (x instanceof NumberZ64)
              return new VectorS(extendedGCD((NumberZ64) x));
        else if (x instanceof NumberZ)
                return new VectorS(((NumberZ)this.toNewRing(Ring.Z, ring)).extendedGCD((NumberZ)x));
        else {ring.exception.append("Not correct first argument in extended GCD");  return null;}             
    }

      /**
     * Процедура, возвращающая   НОД двух чисел a=this и b:
     * и порождающие идеала (x и y), такие, что НОД(a,b) = a x + b y; 
     * @param b
     * @param ring
     * @return {НОД(a,b), x,  y}
     */
    public NumberZ64[] extendedGCD(NumberZ64 bb) {long a=this.value, b=bb.value;
   long x=0,y=1, u=1,v = 0;
    while (a != 0){
       // q, r = b//a, b%a
       // m, n = x-u*q, y-v*q
       // b,a, x,y, u,v = a,r, u,v, m,n}
        long q= b/a;
        long r=b%a;;
        long m=x-u*q;
        long n=y-v*q;
        b=a;
        a=r;
        x=u;
        y=v;
        u=m;
        v=n;}
    return (b<0)?  new NumberZ64[]{new NumberZ64(-b) , new NumberZ64(-x), new NumberZ64(-y)} : 
                               new NumberZ64[]{new NumberZ64(b) , new NumberZ64(x), new NumberZ64(y)};
    }
     
    
    @Override
    public Element abs(Ring ring) {
        return new NumberZ64(Math.abs((value)));
    }
    //============ Арифметика с любыми элементами ===============

    @Override
//    public Element add(Element x, Ring ring) {
//       return (x.numbElementType() < number.Ring.NumberOfSimpleTypes) ?
//         add((NumberZ) x.toNumber(Ring.Z,ring)):
//          x.add(this, ring);
//       }
    public Element add(Element x, Ring ring) {
        if (x == NAN) {
            return NAN;
        }
        if ((x == NEGATIVE_INFINITY) || (x == POSITIVE_INFINITY)) {
            return x;
        }
        int X_Type = x.numbElementType();
        if (X_Type == Ring.Z64) {
            return add((NumberZ64) x);
        }
        if (X_Type > com.mathpar.number.Ring.Z64) {
            return x.add(this, ring);
        }
        return add((NumberZ64) (x.toNumber(Ring.Z64, ring)));
    }

    @Override
    public Element subtract(Element x, Ring ring) {
        if (x == NAN) {
            return NAN;
        }
        if ((x == NEGATIVE_INFINITY) || (x == POSITIVE_INFINITY)) {
            return x.negate(ring);
        }
        int X_Type = x.numbElementType();
        if (X_Type == Ring.Z64) {
            return subtract((NumberZ64) x);
        }
        if (X_Type < Ring.Z64) {
            return subtract((NumberZ64) (x.toNumber(Ring.Z64, ring)));
        }
        if (X_Type < Ring.Polynom) {
            return x.negate(ring).add(this, ring);
        }
        switch (X_Type) {
            case Ring.Polynom:
                return x.negate(ring).add(this, ring);
            case Ring.F:
                return new com.mathpar.func.F(com.mathpar.func.F.SUBTRACT, new Element[] {this, x});
            default:
                return subtract((NumberZ64) (x.toNumber(Ring.Z64, ring)));
        }
    }
//    @Override
//    public Element multiply(Element x, Ring ring) {
//       return (x.numbElementType() < number.Ring.NumberOfSimpleTypes) ?
//         multiply((NumberZ) x.toNumber(Ring.Z, ring)):
//          x.multiply(this, ring);
//    }

    @Override
    public Element multiply(Element x, Ring ring) {
        if (x == NAN) {
            return NAN;
        }
        if ((x == NEGATIVE_INFINITY) || (x == POSITIVE_INFINITY)) {
            return (this.isZero(ring)) ? NAN : this.isNegative() ? x.inverse(ring) : x;
        }
        if (x instanceof NumberZ64) {
            return multiply((NumberZ64) x);
        }
        if (x.numbElementType() > com.mathpar.number.Ring.Z64) {
            return x.multiply(this, ring);
        }
        return multiply((NumberZ64) (x.toNumber(Ring.Z64, ring)));
    }

    @Override
    public Element divide(Element x, Ring ring) {
        if (x == NAN) {
            return NAN;
        }
        if ((x == NEGATIVE_INFINITY) || (x == POSITIVE_INFINITY)) {
            return ZERO;
        }
        if (x instanceof NumberZ64) {
            return divide((NumberZ64) x);
        }
        return this.toNewRing(x.numbElementType(), ring).divide(x, ring);
    }
//================================================================

    public Element add(NumberZ64 x) {
        return (new NumberZ64((value + x.value)));
    }

    public Element subtract(NumberZ64 x) {
        return (new NumberZ64((value - (x).value)));
    }

    public Element multiply(NumberZ64 x) {
        return (new NumberZ64((value * (x).value)));
    }

    @Override
    public Element multiply(Element x, int i, Ring ring) {
        return (new NumberZ64((value * ((NumberZ64) x).value)));
    }

    public Element divide(NumberZ64 x) {
        return (new NumberZ64((value / x.value)));
    }

    @Override
    public NumberZ64[] divideAndRemainder(Element x, Ring ring) {
        int type = x.numbElementType();
        NumberZ64 n = (type == Ring.Z64) ? (NumberZ64)x
                : (type < Ring.NumberOfSimpleTypes) ? (NumberZ64) x.toNewRing(Ring.Z64, ring) : null;
        if (n == null) return null;
        long q = this.value / n.value;
        long r = this.value - q * n.value;
        return new NumberZ64[] {new NumberZ64(q), new NumberZ64(r)};
    }

    public NumberZ64 modInverse(Element m, Ring ring) {

        return new NumberZ64(NFunctionZ32.p_Inverse(this.value, ring.MOD32));
    }

    public NumberZ64 modPow(Element exp, Element m, Ring ring) {
        return ZERO;
    }

    @Override
    public NumberZ64 mod(Element x, Ring ring) {
        return new NumberZ64(this.value % ring.MOD32);
    }

    /**
     * Сдвигает остаток от деления на модуль в в интервал [0..MOD-1]
     *
     * @return (value < 0)? new NumberZ64(value-MOD) : this;
     */
    public NumberZ64 mod(long mod) {
        long vv = value % mod;
        return new NumberZ64((vv < 0) ? vv + mod : vv);
    }

    /**
     * Сдвигает остаток от деления на нечетный модуль в в интервал
     * [-MOD/2..MOD/2]
     *
     * @return (value > MOD/2)? new NumberZ64(value-MOD): ....
     */
    public NumberZ64 Mod(long mod) {
        long vv = value % mod;
        long m1 = (mod >> 1);
        return (vv < -m1) ? new NumberZ64(vv + mod) : new NumberZ64((vv > m1) ? vv - mod : vv);
    }

    public String toString(int i) {
        return (toString());
    }

    @Override
    public int signum() {
        long y = value;
        if (y > 0) {
            y = 1;
        } else if (y < 0) {
            y = -1;
        }
        return (int) y;
    }

    @Override
    public Boolean isZero(Ring ring) {
        return (value == 0L);
    }

    @Override
    public Boolean isOne(Ring ring) {
        return (value == 1L);
    }

    @Override
    public Boolean isMinusOne(Ring ring) {
        return (value == -1L);
    }

    public static NumberZ64 valueOf(NumberZ x) {
        return new NumberZ64(x.longValue());
    }
    //public static Long valueOf(long x){ return new Long(x);}

    public static NumberZ64 valueOf(int x) {
        return new NumberZ64((long) x);
    }

    public static NumberZ64 valueOf(double x) {
        NumberR64 z = new NumberR64(x);
        return new NumberZ64(z.longValue());
    }

    public static NumberZ64 valueOf(NumberR x) {
        return new NumberZ64(x.longValue());
    }

    public NumberZ64 valOf(NumberZ x) {
        return new NumberZ64(x.longValue());
    }

    public NumberZ64 valOf(long x) {
        return new NumberZ64(x);
    }
    
    public NumberZ64 valOf(long x, Ring ring) {
        return new NumberZ64(x);
    }


    public NumberZ64 valOf(int x) {
        return new NumberZ64((long) x);
    }

    public NumberZ64 valOf(double x) {
        NumberR64 z = new NumberR64(x);
        return new NumberZ64(z.longValue());
    }

    public NumberZ64 valOf(NumberR x) {
        return new NumberZ64(x.longValue());
    }

    public NumberZ64 valOf(String x) {
        return valueOf(x);
    }

    public NumberZ64 GCD(NumberZ64 x) {
        return new NumberZ64(gcd(value, x.value));
    }

    @Override
    public Element GCD(Element x, Ring ring) {
        int type = x.numbElementType();
        return (type < Ring.Q)
                ? (type == 0) ? GCD((NumberZ64) x)
                : (new NumberZ(this.value)).GCD(x.toNumber(Ring.Z, ring), ring)
                : ONE;
    }

    public static long gcd(long a, long b) {
        if (a < 0) {
            a = -a;
        }
        if (b < 0) {
            b = -b;
        }
        while (a > 0 && b > 0) {
            if (a > b) {
                a = a % b;
            } else {
                b = b % a;
            }
        }
        if (a == 0) {
            return b;
        } else {
            return a;
        }
    }

    public Element D() {
        return ZERO;
    }

    public Element D(int num) {
        return ZERO;
    }

    public NumberZ64 B_NOT() {
        return (value == 0) ?  TRUE: FALSE ;
    }

    public NumberZ64 B_AND(NumberZ64 x) {
        return ((value != 0) & (x.value != 0)) ? TRUE : FALSE;
    }

    public NumberZ64 B_OR(NumberZ64 x) {
        return ((value == 0) & (x.value == 0)) ? FALSE : TRUE;
    }

    /**
     * Transform this number of NumberZ64 type to the new type fixed by
     * "numberType" defined by Ring.type
     *
     * @param numberType - new type
     *
     * @return this transormed to the new type
     */
    public Element toNumber(int numberType, Ring ring) {
        switch (numberType) {
            case Ring.Z:
                return new NumberZ(value);
            case Ring.Z64:
                return this;
            case Ring.Zp32:
                return new NumberZp32(value, ring);
            case Ring.Zp:
                return new NumberZp(new NumberZ(value), ring);
            case Ring.R:
                return new NumberR(value);
            case Ring.R64:
                return new NumberR64(value);
            case Ring.R128:
                return new NumberR128(value);
//       case Ring.Fraction:
//            return new Fraction(this);
//       case Ring.Complex_:
//            return new Complex(this);
        }
        return this;
    }

    @Override
    public Element toNewRing(int Algebra, Ring r) {
        if ( Algebra >= Ring.Polynom ) return this;
        return toNumber(Algebra, r);
    }

    /**
     * Is this number is even?
     *
     * @return true - in the case of even, false - in the case of odd
     */
    @Override
    public boolean isEven() {
        return (value & 1) == 0;
    }
    /**
     * Is this number is even?
     *
     * @return true - in the case of even, false - in the case of odd
     */
    @Override
    public Boolean isNegative() {
        return value < 0;
    }
    /**
     * Is this number is odd?
     *
     * @return true - in the case of odd, false - in the case of even
     */
    public boolean isOdd() {
        return (value & 1) != 0;
    }
    
    
     /**NumberR64 negative constant
     * Initialize   constant array when class is loaded.
     * @return NumberR64 negative constant 
     */
    @Override
    public  Element[] negConst() {
        NumberZ64 negConst[] = new NumberZ64[MAX_CONSTANT + 1];
        for (int i = 1; i <= Element.MAX_CONSTANT; i++) {negConst[i] = new NumberZ64(-i); }
        return negConst;
    }    
    /**NumberR64 positive constant
     * Initialize   constant array when class is loaded.
     * @return NumberR64 positive constant 
     */
    @Override
    public  Element[] posConst() {
        NumberZ64 posConst[] = new NumberZ64[MAX_CONSTANT + 1];
        for (int i = 1; i <= Element.MAX_CONSTANT; i++) {posConst[i] = new NumberZ64(i); }
        posConst[0]=NumberZ64.ZERO;
        return posConst;
    }
    
    /**
     * use serialVersionUID from JDK 1.0.2 for interoperability
     */
    private static final long serialVersionUID = 4290774380558885855L;
}
