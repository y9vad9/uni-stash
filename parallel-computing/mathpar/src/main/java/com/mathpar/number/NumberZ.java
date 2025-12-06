package com.mathpar.number;

import com.mathpar.func.*;
import java.io.*; 
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.mathpar.number.math.*;
import com.mathpar.polynom.*;

/**
 * THIS CLASS WAS DONE ON THE BASE OF 2004-year VERSION OF JAVA BIGINTEREG
 *
 * Immutable arbitrary-precision integers. All operations behave as if
 * BigIntegers were represented in two's-complement notation (like Java's
 * primitive integer types). BigInteger provides analogues to all of Java's
 * primitive integer operators, and all relevant methods from java.lang.Math.
 * Additionally, BigInteger provides operations for modular arithmetic, GCD
 * calculation, primality testing, prime generation, bit manipulation, and a few
 * other miscellaneous operations. <p> Semantics of arithmetic operations
 * exactly mimic those of Java's integer arithmetic operators, as defined in
 * <i>The Java Language Specification</i>. For example, division by zero throws
 * an <tt>ArithmeticException</tt>, and division of a negative by a positive
 * yields a negative (or zero) remainder. All of the details in the Spec
 * concerning overflow are ignored, as BigIntegers are made as large as
 * necessary to accommodate the results of an operation. <p> Semantics of shift
 * operations extend those of Java's shift operators to allow for negative shift
 * distances. A right-shift with a negative shift distance results in a left
 * shift, and vice-versa. The unsigned right shift operator (&gt;&gt;&gt;) is
 * omitted, as this operation makes little sense in combination with the
 * "infinite word size" abstraction provided by this class. <p> Semantics of
 * bitwise logical operations exactly mimic those of Java's bitwise integer
 * operators. The binary operators (<tt>and</tt>, <tt>or</tt>, <tt>xor</tt>)
 * implicitly perform sign extension on the shorter of the two operands prior to
 * performing the operation. <p> Comparison operations perform signed integer
 * comparisons, analogous to those performed by Java's relational and equality
 * operators. <p> Modular arithmetic operations are provided to compute
 * residues, perform exponentiation, and compute multiplicative inverses. These
 * methods always return a non-negative result, between <tt>0</tt> and
 * <tt>(modulus - 1)</tt>, inclusive. <p> Bit operations operate on a single bit
 * of the two's-complement representation of their operand. If necessary, the
 * operand is sign- extended so that it contains the designated bit. None of the
 * single-bit operations can produce a BigInteger with a different sign from the
 * BigInteger being operated on, as they affect only a single bit, and the
 * "infinite word size" abstraction provided by this class ensures that there
 * are infinitely many "virtual sign bits" preceding each BigInteger. <p> For
 * the sake of brevity and clarity, pseudo-code is used throughout the
 * descriptions of BigInteger methods. The pseudo-code expression <tt>(i +
 * j)</tt> is shorthand for "a BigInteger whose value is that of the BigInteger
 * <tt>i</tt> plus that of the BigInteger <tt>j</tt>." The pseudo-code
 * expression <tt>(i == j)</tt> is shorthand for "<tt>true</tt> if and only if
 * the BigInteger <tt>i</tt> represents the same value as the BigInteger
 * <tt>j</tt>." Other pseudo-code expressions are interpreted similarly. <p> All
 * methods and constructors in this class throw
 * <CODE>NullPointerException</CODE> when passed a null object reference for any
 * input parameter.
 *
 * @see BigDecimal
 * @version 1.68, 04/29/04
 * @author Josh Bloch
 * @author Michael McCloskey
 * @since JDK1.1
 */
public class NumberZ extends Element {
    
    public NumberZ clone() {
        NumberZ res=new NumberZ();
        res.bitCount=bitCount;
        res.bitLength=bitLength;
        res.firstNonzeroByteNum=firstNonzeroByteNum;
        res.firstNonzeroIntNum=firstNonzeroIntNum;
        res.lowestSetBit=lowestSetBit;
        res.mag=new int[mag.length];
        System.arraycopy(mag,0, res.mag, 0, mag.length);
        res.signum=signum;
        res.value=value;
        return res;
    }
    
    public NumberZ() {
    }
    /**
     * The signum of this BigInteger: -1 for negative, 0 for zero, or 1 for
     * positive. Note that the BigInteger zero <i>must</i> have a signum of 0.
     * This is necessary to ensures that there is exactly one representation for
     * each BigInteger value.
     *
     * @serial
     */
    public int signum;
    /**
     * The magnitude of this BigInteger, in <i>big-endian</i> order: the zeroth
     * element of this array is the most-significant int of the magnitude. The
     * magnitude must be "minimal" in that the most-significant int
     * (<tt>mag[0]</tt>) must be non-zero. This is necessary to ensure that
     * there is exactly one representation for each BigInteger value. Note that
     * this implies that the BigInteger zero has a zero-length mag array.
     */
    public int[] mag;
    // These "redundant fields" are initialized with recognizable nonsense
    // values, and cached the first time they are needed (or never, if they
    // aren't needed).
    /**
     * Plus-Minus-Infinity coded for signum grater the NUMBER_SCALE.
     */
    private static final byte[] oneByte = new byte[] {1};
    /**
     * The bitCount of this BigInteger, as returned by bitCount(), or -1 (either
     * value is acceptable).
     *
     * @serial
     * @see #bitCount
     */
    private int bitCount = -1;
    /**
     * The bitLength of this BigInteger, as returned by bitLength(), or -1
     * (either value is acceptable).
     *
     * @serial
     * @see #bitLength
     */
    private int bitLength = -1;
    /**
     * The lowest set bit of this BigInteger, as returned by getLowestSetBit(),
     * or -2 (either value is acceptable).
     *
     * @serial
     * @see #getLowestSetBit
     */
    private int lowestSetBit = -2;
    /**
     * The index of the lowest-order byte in the magnitude of this BigInteger
     * that contains a nonzero byte, or -2 (either value is acceptable). The
     * least significant byte has int-number 0, the next byte in order of
     * increasing significance has byte-number 1, and so forth.
     *
     * @serial
     */
    private int firstNonzeroByteNum = -2;
    /**
     * The index of the lowest-order int in the magnitude of this BigInteger
     * that contains a nonzero int, or -2 (either value is acceptable). The
     * least significant int has int-number 0, the next int in order of
     * increasing significance has int-number 1, and so forth.
     */
    private int firstNonzeroIntNum = -2;
    /**
     * This mask is used to obtain the value of an int as if it were unsigned.
     */
    private final static long LONG_MASK = 0xffffffffL;
    /**
     * Initialize static constant array when class is loaded.
     */
  //  private final static int MAX_CONSTANT = 16; // synchron with Element
    
    
       @Override
    public Element[] negConst() {
        return NEGCONST;
    }
    @Override
    public Element[] posConst() {
        return POSCONST;
    }
    /**
     * The BigInteger constant zero.
     *
     * @since 1.2
     */
    public static NumberZ ZERO = new NumberZ(new int[0], 0);

    /**
     * The BigInteger constant two. (Not exported.)
     */
    public static final NumberZ Z_180 = new NumberZ(180);
    public static final NumberZ Z_239 = new NumberZ(239);
    /**
     * The BigInteger constant 90.
     *
     * @since 1.5
     */
    public static final NumberZ Z_90 = new NumberZ(90);
   /**
     * The BigInteger constant 268435399L is a biggest long prime number.
     *
     * @since 2015
     */
    public static final NumberZ PRIME268435399 = new NumberZ(268435399L);

    /**
     * Создание массива биномиальных коэффициентов
     */
//    static {
//        PascalTriangle.initCacheZ();
//    }   
    public static final NumberZ POSCONST[] = new NumberZ[MAX_CONSTANT + 1];   
    public static final NumberZ NEGCONST[] = new NumberZ[MAX_CONSTANT + 1];

    static {
        for (int i = 1; i <= MAX_CONSTANT; i++) {
            int[] magnitude = new int[1];
            magnitude[0] = i;
             POSCONST[i] = new NumberZ(magnitude, 1);
             NEGCONST[i] = new NumberZ(magnitude, -1);
        }
        POSCONST[0]=ZERO;
        PascalTriangle.initCacheZ();
    }
    /**
     * The BigInteger constant one.
     *
     * @since 1.2
     */
    public static final NumberZ ONE = POSCONST[1];
    public static final NumberZ TWO = POSCONST[2];
    public static final NumberZ TEN = POSCONST[10];
    /**
     * Translates a byte array containing the two's-complement binary
     * representation of a BigInteger into a BigInteger. The input array is
     * assumed to be in <i>big-endian</i> byte-order: the most significant byte
     * is in the zeroth element.
     *
     * @param val big-endian two's-complement binary representation of
     * BigInteger.
     *
     * @throws NumberFormatException <tt>val</tt> is zero bytes long.
     */
    public NumberZ(byte[] val) {
        if (val.length == 0) {
            throw new NumberFormatException("Zero length BigInteger");
        }

        if (val[0] < 0) {
            mag = makePositive(val);
            signum = -1;
        } else {
            mag = stripLeadingZeroBytes(val);
            signum = (mag.length == 0 ? 0 : 1);
        }
    }

    /**
     * This private constructor translates an int array containing the
     * two's-complement binary representation of a BigInteger into a BigInteger.
     * The input array is assumed to be in <i>big-endian</i> int-order: the most
     * significant int is in the zeroth element.
     */
    public NumberZ(int[] val) {
        if (val.length == 0) {
            throw new NumberFormatException("Zero length BigInteger");
        }

        if (val[0] < 0) {
            mag = makePositive(val);
            signum = -1;
        } else {
            mag = trustedStripLeadingZeroInts(val);
            signum = (mag.length == 0 ? 0 : 1);
        }
    }

    /**
     * Translates the sign-magnitude representation of a BigInteger into a
     * BigInteger. The sign is represented as an integer signum value: -1 for
     * negative, 0 for zero, or 1 for positive. The magnitude is a byte array in
     * <i>big-endian</i> byte-order: the most significant byte is in the zeroth
     * element. A zero-length magnitude array is permissible, and will result
     * inin a BigInteger value of 0, whether signum is -1, 0 or 1.
     *
     * @param signum signum of the number (-1 for negative, 0 for zero, 1 for
     * positive).
     * @param magnitude big-endian binary representation of the magnitude of the
     * number.
     *
     * @throws NumberFormatException <tt>signum</tt> is not one of the three
     * legal values (-1, 0, and 1), or <tt>signum</tt> is 0 and
     * <tt>magnitude</tt> contains one or more non-zero bytes.
     */
    public NumberZ(int signum, byte[] magnitude) {
        this.mag = stripLeadingZeroBytes(magnitude);

        if (this.mag.length == 0) {
            this.signum = 0;
        } else {
            if (signum == 0) {
                throw (new NumberFormatException("signum-magnitude mismatch"));
            }
            this.signum = signum;
        }
    }

    /**
     * A constructor for internal use that translates the sign-magnitude
     * representation of a BigInteger into a BigInteger. It checks the arguments
     * and copies the magnitude so this constructor would be safe for external
     * use.
     */
 public   NumberZ(int signum, int[] magnitude) {
        this.mag = stripLeadingZeroInts(magnitude);

        if (signum < -1 || signum > 1) {
            throw (new NumberFormatException("Invalid signum value"));
        }

        if (this.mag.length == 0) {
            this.signum = 0;
        } else {
            if (signum == 0) {
                throw (new NumberFormatException("signum-magnitude mismatch"));
            }
            this.signum = signum;
        }
    }

    /**
     * Translates the String representation of a BigInteger in the specified
     * radix into a BigInteger. The String representation consists of an
     * optional minus sign followed by a sequence of one or more digits in the
     * specified radix. The character-to-digit mapping is provided by
     * <tt>Character.digit</tt>. The String may not contain any extraneous
     * characters (whitespace, for example).
     *
     * @param val String representation of BigInteger.
     * @param radix radix to be used in interpreting <tt>val</tt>.
     *
     * @throws NumberFormatException <tt>val</tt> is not a valid representation
     * of a BigInteger in the specified radix, or <tt>radix</tt> is outside the
     * range from {@link Character#MIN_RADIX} to {@link Character#MAX_RADIX},
     * inclusive.
     * @see Character#digit
     */
    public NumberZ(String val, int radix) {
        int cursor = 0, numDigits;
        int len = val.length();

        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
            throw new NumberFormatException("Radix out of range");
        }
        if (val.length() == 0) {
            throw new NumberFormatException("Zero length BigInteger");
        }

        // Check for minus sign
        signum = 1;
        int index = val.lastIndexOf("-");
        if (index != -1) {
            if (index == 0) {
                if (val.length() == 1) {
                    throw new NumberFormatException("Zero length BigInteger");
                }
                signum = -1;
                cursor = 1;
            } else {
                throw new NumberFormatException("Illegal embedded minus sign");
            }
        }

        // Skip leading zeros and compute number of digits in magnitude
        while (cursor < len && Character.digit(val.charAt(cursor), radix) == 0) {
            cursor++;
        }
        if (cursor == len) {
            signum = 0;
            mag = ZERO.mag;
            return;
        } else {
            numDigits = len - cursor;
        }

        // Pre-allocate array of expected size. May be too large but can
        // never be too small. Typically exact.
        int numBits = (int) (((numDigits * bitsPerDigit[radix]) >>> 10) + 1);
        int numWords = (numBits + 31) / 32;
        mag = new int[numWords];

        // Process first (potentially short) digit group
        int firstGroupLen = numDigits % digitsPerInt[radix];
        if (firstGroupLen == 0) {
            firstGroupLen = digitsPerInt[radix];
        }
        String group = val.substring(cursor, cursor += firstGroupLen);
        mag[mag.length - 1] = Integer.parseInt(group, radix);
        if (mag[mag.length - 1] < 0) {
            throw new NumberFormatException("Illegal digit");
        }

        // Process remaining digit groups
        int superRadix = intRadix[radix];
        int groupVal = 0;
        while (cursor < val.length()) {
            group = val.substring(cursor, cursor += digitsPerInt[radix]);
            groupVal = Integer.parseInt(group, radix);
            if (groupVal < 0) {
                throw new NumberFormatException("Illegal digit");
            }
            destructiveMulAdd(mag, superRadix, groupVal);
        }
        // Required for cases where the array was overallocated.
        mag = trustedStripLeadingZeroInts(mag);
    }

    // Constructs a new BigInteger using a char array with radix=10
    NumberZ(char[] val) {
        int cursor = 0, numDigits;
        int len = val.length;

        // Check for leading minus sign
        signum = 1;
        if (val[0] == '-') {
            if (len == 1) {
                throw new NumberFormatException("Zero length BigInteger");
            }
            signum = -1;
            cursor = 1;
        }

        // Skip leading zeros and compute number of digits in magnitude
        while (cursor < len && Character.digit(val[cursor], 10) == 0) {
            cursor++;
        }
        if (cursor == len) {
            signum = 0;
            mag = ZERO.mag;
            return;
        } else {
            numDigits = len - cursor;
        }

        // Pre-allocate array of expected size
        int numWords;
        if (len < 10) {
            numWords = 1;
        } else {
            int numBits = (int) (((numDigits * bitsPerDigit[10]) >>> 10) + 1);
            numWords = (numBits + 31) / 32;
        }
        mag = new int[numWords];

        // Process first (potentially short) digit group
        int firstGroupLen = numDigits % digitsPerInt[10];
        if (firstGroupLen == 0) {
            firstGroupLen = digitsPerInt[10];
        }
        mag[mag.length - 1] = parseInt(val, cursor, cursor += firstGroupLen);

        // Process remaining digit groups
        while (cursor < len) {
            int groupVal = parseInt(val, cursor, cursor += digitsPerInt[10]);
            destructiveMulAdd(mag, intRadix[10], groupVal);
        }
        mag = trustedStripLeadingZeroInts(mag);
    }

    // Create an integer with the digits between the two indexes
    // Assumes start < end. The result may be negative, but it
    // is to be treated as an unsigned value.
    private int parseInt(char[] source, int start, int end) {
        int result = Character.digit(source[start++], 10);
        if (result == -1) {
            throw new NumberFormatException(new String(source));
        }

        for (int index = start; index < end; index++) {
            int nextVal = Character.digit(source[index], 10);
            if (nextVal == -1) {
                throw new NumberFormatException(new String(source));
            }
            result = 10 * result + nextVal;
        }

        return result;
    }
    // bitsPerDigit in the given radix times 1024
    // Rounded up to avoid underallocation.
    private static long bitsPerDigit[] = {
        0, 0,
        1024, 1624, 2048, 2378, 2648, 2875,
        3072, 3247, 3402, 3543, 3672,
        3790, 3899, 4001, 4096, 4186, 4271,
        4350, 4426, 4498, 4567, 4633,
        4696, 4756, 4814, 4870, 4923, 4975,
        5025, 5074, 5120, 5166, 5210,
        5253, 5295};

    // Multiply x array times word y in place, and add word z
    private static void destructiveMulAdd(int[] x, int y, int z) {
        // Perform the multiplication word by word
        long ylong = y & LONG_MASK;
        long zlong = z & LONG_MASK;
        int len = x.length;

        long product = 0;
        long carry = 0;
        for (int i = len - 1; i >= 0; i--) {
            product = ylong * (x[i] & LONG_MASK) + carry;
            x[i] = (int) product;
            carry = product >>> 32;
        }

        // Perform the addition
        long sum = (x[len - 1] & LONG_MASK) + zlong;
        x[len - 1] = (int) sum;
        carry = sum >>> 32;
        for (int i = len - 2; i >= 0; i--) {
            sum = (x[i] & LONG_MASK) + carry;
            x[i] = (int) sum;
            carry = sum >>> 32;
        }
    }

    /**
     * Translates the decimal String representation of a BigInteger into a
     * BigInteger. The String representation consists of an optional minus sign
     * followed by a sequence of one or more decimal digits. The
     * character-to-digit mapping is provided by <tt>Character.digit</tt>. The
     * String may not contain any extraneous characters (whitespace, for
     * example).
     *
     * @param val decimal String representation of BigInteger.
     *
     * @throws NumberFormatException <tt>val</tt> is not a valid representation
     * of a BigInteger.
     * @see Character#digit
     */
    public NumberZ(String val) {
        this(val, 10);
    }

    public NumberZ(String val, Ring ring) {
        this(val, 10);
    }

    /**
     * Constructs a randomly generated BigInteger, uniformly distributed over
     * the range <tt>0</tt> to <tt>(2<sup>numBits</sup> - 1)</tt>, inclusive.
     * The uniformity of the distribution assumes that a fair source of random
     * bits is provided in <tt>rnd</tt>. Note that this constructor always
     * constructs a non-negative BigInteger.
     *
     * @param numBits maximum bitLength of the new BigInteger.
     * @param rnd source of randomness to be used in computing the new
     * BigInteger.
     *
     * @throws IllegalArgumentException <tt>numBits</tt> is negative.
     * @see #bitLength
     */
    public NumberZ(int numBits, Random rnd) {
        this(1, randomBits(numBits, rnd));
    }

    private static byte[] randomBits(int numBits, Random rnd) {
        if (numBits < 0) {
            throw new IllegalArgumentException("numBits must be non-negative");
        }
        int numBytes = (numBits + 7) / 8;
        byte[] randomBits = new byte[numBytes];

        // Generate random bytes and mask out any excess bits
        if (numBytes > 0) {
            rnd.nextBytes(randomBits);
            int excessBits = 8 * numBytes - numBits;
            randomBits[0] &= (1 << (8 - excessBits)) - 1;
        }
        return randomBits;
    }

    /**
     * Constructs a randomly generated positive BigInteger that is probably
     * prime, with the specified bitLength.<p>
     *
     * It is recommended that the {@link #probablePrime probablePrime} method be
     * used in preference to this constructor unless there is a compelling need
     * to specify a certainty.
     *
     * @param bitLength bitLength of the returned BigInteger.
     * @param certainty a measure of the uncertainty that the caller is willing
     * to tolerate. The probability that the new BigInteger represents a prime
     * number will exceed <tt>(1 - 1/2<sup>certainty</sup></tt>). The execution
     * time of this constructor is proportional to the value of this parameter.
     * @param rnd source of random bits used to select candidates to be tested
     * for primality.
     *
     * @throws ArithmeticException <tt>bitLength &lt; 2</tt>.
     * @see #bitLength
     */
    public NumberZ(int bitLength, int certainty, Random rnd) {
        NumberZ prime;

        if (bitLength < 2) {
            throw new ArithmeticException("bitLength < 2");
        }
        // The cutoff of 95 was chosen empirically for best performance
        prime = (bitLength < 95 ? smallPrime(bitLength, certainty, rnd)
                : largePrime(bitLength, certainty, rnd));
        signum = 1;
        mag = prime.mag;
    }
    // Minimum size in bits that the requested prime number has
    // before we use the large prime number generating algorithms
    private static final int SMALL_PRIME_THRESHOLD = 95;
    // Certainty required to meet the spec of probablePrime
    private static final int DEFAULT_PRIME_CERTAINTY = 100;

    /**
     * Returns a positive BigInteger that is probably prime, with the specified
     * bitLength. The probability that a BigInteger returned by this method is
     * composite does not exceed 2<sup>-100</sup>.
     *
     * @param bitLength bitLength of the returned BigInteger.
     * @param rnd source of random bits used to select candidates to be tested
     * for primality.
     *
     * @return a BigInteger of <tt>bitLength</tt> bits that is probably prime
     *
     * @throws ArithmeticException <tt>bitLength &lt; 2</tt>.
     * @see #bitLength
     */
    public static NumberZ probablePrime(int bitLength, Random rnd) {
        if (bitLength < 2) {
            throw new ArithmeticException("bitLength < 2");
        }

        // The cutoff of 95 was chosen empirically for best performance
        return (bitLength < SMALL_PRIME_THRESHOLD ? smallPrime(bitLength,
                DEFAULT_PRIME_CERTAINTY,
                rnd) : largePrime(
                bitLength, DEFAULT_PRIME_CERTAINTY, rnd));
    }

    /**
     * Find a random number of the specified bitLength that is probably prime.
     * This method is used for smaller primes, its performance degrades on
     * larger bitlengths.
     *
     * This method assumes bitLength > 1.
     */
    private static NumberZ smallPrime(int bitLength, int certainty, Random rnd) {
        int magLen = (bitLength + 31) >>> 5;
        int temp[] = new int[magLen];
        int highBit = 1 << ((bitLength + 31) & 0x1f); // High bit of high int
        int highMask = (highBit << 1) - 1; // Bits to keep in high int

        while (true) {
            // Construct a candidate
            for (int i = 0; i < magLen; i++) {
                temp[i] = rnd.nextInt();
            }
            temp[0] = (temp[0] & highMask) | highBit; // Ensure exact length
            if (bitLength > 2) {
                temp[magLen - 1] |= 1; // Make odd if bitlen > 2
            }

            NumberZ p = new NumberZ(temp, 1);

            // Do cheap "pre-test" if applicable
            if (bitLength > 6) {
                long r = p.remainder(SMALL_PRIME_PRODUCT).longValue();
                if ((r % 3 == 0) || (r % 5 == 0) || (r % 7 == 0)
                        || (r % 11 == 0) || (r % 13 == 0) || (r % 17 == 0) || (r
                        % 19 == 0) || (r % 23 == 0) || (r % 29 == 0) || (r % 31
                        == 0) || (r % 37 == 0) || (r % 41 == 0)) {
                    continue; // Candidate is composite; try another
                }
            }

            // All candidates of bitLength 2 and 3 are prime by this point
            if (bitLength < 4) {
                return p;
            }

            // Do expensive test if we survive pre-test (or it's inapplicable)
            if (p.primeToCertainty(certainty)) {
                return p;
            }
        }
    }
    private static final NumberZ SMALL_PRIME_PRODUCT = valueOf(3L * 5 * 7 * 11
            * 13 * 17 * 19 * 23 * 29 * 31 * 37 * 41);

    /**
     * Find a random number of the specified bitLength that is probably prime.
     * This method is more appropriate for larger bitlengths since it uses a
     * sieve to eliminate most composites before using a more expensive test.
     */
    private static NumberZ largePrime(int bitLength, int certainty, Random rnd) {
        NumberZ p;
        p = new NumberZ(bitLength, rnd).setBit(bitLength - 1);
        p.mag[p.mag.length - 1] &= 0xfffffffe;

        // Use a sieve length likely to contain the next prime number
        int searchLen = (bitLength / 20) * 64;
        BitSieve searchSieve = new BitSieve(p, searchLen);
        NumberZ candidate = searchSieve.retrieve(p, certainty);

        while ((candidate == null) || (candidate.bitLength() != bitLength)) {
            p = p.add(NumberZ.valueOf(2 * searchLen));
            if (p.bitLength() != bitLength) {
                p = new NumberZ(bitLength, rnd).setBit(bitLength - 1);
            }
            p.mag[p.mag.length - 1] &= 0xfffffffe;
            searchSieve = new BitSieve(p, searchLen);
            candidate = searchSieve.retrieve(p, certainty);
        }
        return candidate;
    }

    /**
     * Returns the first integer greater than this
     * <code>BigInteger</code> that is probably prime. The probability that the
     * number returned by this method is composite does not exceed
     * 2<sup>-100</sup>. This method will never skip over a prime when
     * searching: if it returns <tt>p</tt>, there is no prime <tt>q</tt> such
     * that <tt>this &lt; q &lt; p</tt>.
     *
     * @return the first integer greater than this <code>BigInteger</code> that
     * is probably prime.
     *
     * @throws ArithmeticException <tt>this &lt; 0</tt>.
     * @since 1.5
     */
    public NumberZ nextProbablePrime() {
        if (this.signum < 0) {
            throw new ArithmeticException("start < 0: " + this);
        }

        // Handle trivial cases
        if ((this.signum == 0) || this.equals(ONE)) {
            return TWO;
        }

        NumberZ result = this.add(ONE);

        // Fastpath for small numbers
        if (result.bitLength() < SMALL_PRIME_THRESHOLD) {

            // Ensure an odd number
            if (!result.testBit(0)) {
                result = result.add(ONE);
            }

            while (true) {
                // Do cheap "pre-test" if applicable
                if (result.bitLength() > 6) {
                    long r = result.remainder(SMALL_PRIME_PRODUCT).longValue();
                    if ((r % 3 == 0) || (r % 5 == 0) || (r % 7 == 0) || (r % 11
                            == 0) || (r % 13 == 0) || (r % 17 == 0) || (r % 19
                            == 0) || (r % 23 == 0) || (r % 29 == 0) || (r % 31
                            == 0) || (r % 37 == 0) || (r % 41 == 0)) {
                        result = result.add(TWO);
                        continue; // Candidate is composite; try another
                    }
                }

                // All candidates of bitLength 2 and 3 are prime by this point
                if (result.bitLength() < 4) {
                    return result;
                }

                // The expensive test
                if (result.primeToCertainty(DEFAULT_PRIME_CERTAINTY)) {
                    return result;
                }

                result = result.add(TWO);
            }
        }

        // Start at previous even number
        if (result.testBit(0)) {
            result = result.subtract(ONE);
        }

        // Looking for the next large prime
        int searchLen = (result.bitLength() / 20) * 64;

        while (true) {
            BitSieve searchSieve = new BitSieve(result, searchLen);
            NumberZ candidate = searchSieve.retrieve(result,
                    DEFAULT_PRIME_CERTAINTY);
            if (candidate != null) {
                return candidate;
            }
            result = result.add(NumberZ.valueOf(2 * searchLen));
        }
    }

    /**
     * Returns <tt>true</tt> if this BigInteger is probably prime,
     * <tt>false</tt> if it's definitely composite.
     *
     * This method assumes bitLength > 2.
     *
     * @param certainty a measure of the uncertainty that the caller is willing
     * to tolerate: if the call returns <tt>true</tt> the probability that this
     * BigInteger is prime exceeds <tt>(1 - 1/2<sup>certainty</sup>)</tt>. The
     * execution time of this method is proportional to the value of this
     * parameter.
     *
     * @return <tt>true</tt> if this BigInteger is probably prime,
     * <tt>false</tt> if it's definitely composite.
     */
    public boolean primeToCertainty(int certainty) {
        int rounds = 0;
        int n = (Math.min(certainty, Integer.MAX_VALUE - 1) + 1) / 2;

        // The relationship between the certainty and the number of rounds
        // we perform is given in the draft standard ANSI X9.80, "PRIME
        // NUMBER GENERATION, PRIMALITY TESTING, AND PRIMALITY CERTIFICATES".
        int sizeInBits = this.bitLength();
        if (sizeInBits < 100) {
            rounds = 50;
            rounds = n < rounds ? n : rounds;
            return passesMillerRabin(rounds);
        }

        if (sizeInBits < 256) {
            rounds = 27;
        } else if (sizeInBits < 512) {
            rounds = 15;
        } else if (sizeInBits < 768) {
            rounds = 8;
        } else if (sizeInBits < 1024) {
            rounds = 4;
        } else {
            rounds = 2;
        }
        rounds = n < rounds ? n : rounds;

        return passesMillerRabin(rounds) && passesLucasLehmer();
    }

    /**
     * Returns true iff this BigInteger is a Lucas-Lehmer probable prime.
     *
     * The following assumptions are made: This BigInteger is a positive, odd
     * number.
     */
    private boolean passesLucasLehmer() {
        NumberZ thisPlusOne = this.add(ONE);

        // Step 1
        int d = 5;
        while (jacobiSymbol(d, this) != -1) {
            // 5, -7, 9, -11, ...
            d = (d < 0) ? Math.abs(d) + 2 : -(d + 2);
        }

        // Step 2
        NumberZ u = lucasLehmerSequence(d, thisPlusOne, this);

        // Step 3
        return u.mod(this).equals(ZERO);
    }

    /**
     * Computes Jacobi(p,n). Assumes n positive, odd, n>=3.
     */
    private static int jacobiSymbol(int p, NumberZ n) {
        if (p == 0) {
            return 0;
        }

        // Algorithm and comments adapted from Colin Plumb's C library.
        int j = 1;
        int u = n.mag[n.mag.length - 1];

        // Make p positive
        if (p < 0) {
            p = -p;
            int n8 = u & 7;
            if ((n8 == 3) || (n8 == 7)) {
                j = -j; // 3 (011) or 7 (111) mod 8
            }
        }

        // Get rid of factors of 2 in p
        while ((p & 3) == 0) {
            p >>= 2;
        }
        if ((p & 1) == 0) {
            p >>= 1;
            if (((u ^ (u >> 1)) & 2) != 0) {
                j = -j; // 3 (011) or 5 (101) mod 8
            }
        }
        if (p == 1) {
            return j;
        }
        // Then, apply quadratic reciprocity
        if ((p & u & 2) != 0) { // p = u = 3 (mod 4)?
            j = -j;
        }
        // And reduce u mod p
        u = n.mod(NumberZ.valueOf(p)).intValue();

        // Now compute Jacobi(u,p), u < p
        while (u != 0) {
            while ((u & 3) == 0) {
                u >>= 2;
            }
            if ((u & 1) == 0) {
                u >>= 1;
                if (((p ^ (p >> 1)) & 2) != 0) {
                    j = -j; // 3 (011) or 5 (101) mod 8
                }
            }
            if (u == 1) {
                return j;
            }
            // Now both u and p are odd, so use quadratic reciprocity
            assert (u < p);
            int t = u;
            u = p;
            p = t;
            if ((u & p & 2) != 0) { // u = p = 3 (mod 4)?
                j = -j;
            }
            // Now u >= p, so it can be reduced
            u %= p;
        }
        return 0;
    }

    private static NumberZ lucasLehmerSequence(int z, NumberZ k,
            NumberZ n) {
        NumberZ d = NumberZ.valueOf(z);
        NumberZ u = ONE;
        NumberZ u2;
        NumberZ v = ONE;
        NumberZ v2;

        for (int i = k.bitLength() - 2; i >= 0; i--) {
            u2 = u.multiply(v).mod(n);

            v2 = v.square().add(d.multiply(u.square())).mod(n);
            if (v2.testBit(0)) {
                v2 = n.subtract(v2);
                v2.signum = -v2.signum;
            }
            v2 = v2.shiftRight(1);

            u = u2;
            v = v2;
            if (k.testBit(i)) {
                u2 = u.add(v).mod(n);
                if (u2.testBit(0)) {
                    u2 = n.subtract(u2);
                    u2.signum = -u2.signum;
                }
                u2 = u2.shiftRight(1);

                v2 = v.add(d.multiply(u)).mod(n);
                if (v2.testBit(0)) {
                    v2 = n.subtract(v2);
                    v2.signum = -v2.signum;
                }
                v2 = v2.shiftRight(1);

                u = u2;
                v = v2;
            }
        }
        return u;
    }

    /**
     * Returns true iff this BigInteger passes the specified number of
     * Miller-Rabin tests. This test is taken from the DSA spec (NIST FIPS
     * 186-2).
     *
     * The following assumptions are made: This BigInteger is a positive, odd
     * number greater than 2. iterations<=50.
     */
    private boolean passesMillerRabin(int iterations) {
        // Find a and m such that m is odd and this == 1 + 2**a * m
        NumberZ thisMinusOne = this.subtract(ONE);
        NumberZ m = thisMinusOne;
        int a = m.getLowestSetBit();
        m = m.shiftRight(a);

        // Do the tests
        Random rnd = new Random();
        for (int i = 0; i < iterations; i++) {
            // Generate a uniform random on (1, this)
            NumberZ b;
            do {
                b = new NumberZ(this.bitLength(), rnd);
            } while (b.compareTo(ONE) <= 0 || b.compareTo(this) >= 0);

            int j = 0;
            NumberZ z = b.modPow(m, this);
            while (!((j == 0 && z.equals(ONE)) || z.equals(thisMinusOne))) {
                if (j > 0 && z.equals(ONE) || ++j == a) {
                    return false;
                }
                z = z.modPow(TWO, this);
            }
        }
        return true;
    }

    /**
     * This private constructor differs from its public cousin with the
     * arguments reversed in two ways: it assumes that its arguments are
     * correct, and it doesn't copy the magnitude array.
     */
    public NumberZ(int[] magnitude, int signum) {
        this.signum = (magnitude.length == 0 ? 0 : signum);
        this.mag = magnitude;
    }

    /**
     * This private constructor is for internal use and assumes that its
     * arguments are correct.
     */
    private NumberZ(byte[] magnitude, int signum) {
        this.signum = (magnitude.length == 0 ? 0 : signum);
        this.mag = stripLeadingZeroBytes(magnitude);
    }

    /**
     * This private constructor is for internal use in converting from a
     * MutableBigInteger object into a BigInteger.
     */
    public NumberZ(MutableBigInteger val, int sign) {
        if (val.offset > 0 || val.value.length != val.intLen) {
            mag = new int[val.intLen];
            for (int i = 0; i < val.intLen; i++) {
                mag[i] = val.value[val.offset + i];
            }
        } else {
            mag = val.value;
        }

        this.signum = (val.intLen == 0) ? 0 : sign;
    }

    //Static Factory Methods
    /**
     * Returns a BigInteger whose value is equal to that of the specified
     * <code>long</code>. This "static factory method" is provided in preference
     * to a (
     * <code>long</code>) constructor because it allows for reuse of frequently
     * used BigIntegers.
     *
     * @param val value of the BigInteger to return.
     *
     * @return a BigInteger with the specified value.
     */
    public static NumberZ valueOf(long val) {
        // If -MAX_CONSTANT < val < MAX_CONSTANT, return stashed constant
        if (val == 0) {
            return ZERO;
        }
        if (val > 0 && val <= MAX_CONSTANT) {
            return POSCONST[(int) val];
        } else if (val < 0 && val >= -MAX_CONSTANT) {
            return NEGCONST[(int) -val];
        }

        return new NumberZ(val);
    }

    public NumberZ(int val) {
        if (val == 0) {
            signum = 0;
            mag = new int[0];
        } else {
            mag = new int[1];
            if (val < 0) {
                signum = -1;
                mag[0] = -val;
            } else {
                signum = 1;
                mag[0] = val;
            }
            // signum = (val < 0) ? -1 : 1;
        }
    }

    /**
     * Constructs a BigInteger .
     */
    public NumberZ(long val) {
        if (val == 0) {
            signum = 0;
            mag = new int[0];
        } else {
            if (val < 0) {
                signum = -1;
                val = -val;
            } else {
                signum = 1;
            }

            int highWord = (int) (val >>> 32);
            if (highWord == 0) {
                mag = new int[1];
                mag[0] = (int) val;
            } else {
                mag = new int[2];
                mag[0] = highWord;
                mag[1] = (int) val;
            }
        }
    }

    /**
     * Returns a BigInteger with the given two's complement representation.
     * Assumes that the input array will not be modified (the returned
     * BigInteger will reference the input array if feasible).
     */
    private static NumberZ valueOf(int val[]) {
        return (val[0] > 0 ? new NumberZ(val, 1) : new NumberZ(val));
    }
     // Arithmetic Operations

    /**
     * Returns a BigInteger whose value is <tt>(this + val)</tt>.
     *
     * @param val value to be added to this BigInteger.
     *
     * @return <tt>this + val</tt>
     */
    public NumberZ add(NumberZ val) {
        int[] resultMag;
        if (val.signum == 0) {
            return this;
        }
        if (signum == 0) {
            return val;
        }
        if (val.signum == signum) {
            return new NumberZ(add(mag, val.mag), signum);
        }

        int cmp = intArrayCmp(mag, val.mag);
        if (cmp == 0) {
            return ZERO;
        }
        resultMag = (cmp > 0 ? subtract(mag, val.mag)
                : subtract(val.mag, mag));
        resultMag = trustedStripLeadingZeroInts(resultMag);

        return new NumberZ(resultMag, cmp * signum);
    }

    /**
     * Adds the contents of the int arrays x and y. This method allocates a new
     * int array to hold the answer and returns a reference to that array.
     */
    private static int[] add(int[] x, int[] y) {
        // If x is shorter, swap the two arrays
        if (x.length < y.length) {
            int[] tmp = x;
            x = y;
            y = tmp;
        }

        int xIndex = x.length;
        int yIndex = y.length;
        int result[] = new int[xIndex];
        long sum = 0;

        // Add common parts of both numbers
        while (yIndex > 0) {
            sum = (x[--xIndex] & LONG_MASK) + (y[--yIndex] & LONG_MASK) + (sum
                    >>> 32);
            result[xIndex] = (int) sum;
        }

        // Copy remainder of longer number while carry propagation is required
        boolean carry = (sum >>> 32 != 0);
        while (xIndex > 0 && carry) {
            carry = ((result[--xIndex] = x[xIndex] + 1) == 0);
        }

        // Copy remainder of longer number
        while (xIndex > 0) {
            result[--xIndex] = x[xIndex];
        }

        // Grow result if necessary
        if (carry) {
            int newLen = result.length + 1;
            int temp[] = new int[newLen];
            for (int i = 1; i < newLen; i++) {
                temp[i] = result[i - 1];
            }
            temp[0] = 0x01;
            result = temp;
        }
        return result;
    }

    /**
     * Returns a BigInteger whose value is <tt>(this - val)</tt>.
     *
     * @param val value to be subtracted from this BigInteger.
     *
     * @return <tt>this - val</tt>
     */
    public NumberZ subtract(NumberZ val) {
        int[] resultMag;
        if (val.signum == 0) {
            return this;
        }
        if (signum == 0) {
            return val.negate();
        }
        if (val.signum != signum) {
            return new NumberZ(add(mag, val.mag), signum);
        }

        int cmp = intArrayCmp(mag, val.mag);
        if (cmp == 0) {
            return ZERO;
        }
        resultMag = (cmp > 0 ? subtract(mag, val.mag)
                : subtract(val.mag, mag));
        resultMag = trustedStripLeadingZeroInts(resultMag);
        return new NumberZ(resultMag, cmp * signum);
    }

    /**
     * Subtracts the contents of the second int arrays (little) from the first
     * (big). The first int array (big) must represent a larger number than the
     * second. This method allocates the space necessary to hold the answer.
     */
    private static int[] subtract(int[] big, int[] little) {
        int bigIndex = big.length;
        int result[] = new int[bigIndex];
        int littleIndex = little.length;
        long difference = 0;

        // Subtract common parts of both numbers
        while (littleIndex > 0) {
            difference = (big[--bigIndex] & LONG_MASK) - (little[--littleIndex]
                    & LONG_MASK) + (difference >> 32);
            result[bigIndex] = (int) difference;
        }

        // Subtract remainder of longer number while borrow propagates
        boolean borrow = (difference >> 32 != 0);
        while (bigIndex > 0 && borrow) {
            borrow = ((result[--bigIndex] = big[bigIndex] - 1) == -1);
        }

        // Copy remainder of longer number
        while (bigIndex > 0) {
            result[--bigIndex] = big[bigIndex];
        }

        return result;
    }

    /**
     * Returns a BigInteger whose value is <tt>(this * val)</tt>.
     *
     * @param val value to be multiplied by this BigInteger.
     *
     * @return <tt>this * val</tt>
     */
    public NumberZ multiply(NumberZ val) {
        if (signum == 0 || val.signum == 0) {
            return ZERO;
        }
        int[] result = multiplyToLen(mag, mag.length, val.mag, val.mag.length, null);
        result = trustedStripLeadingZeroInts(result);
        return new NumberZ(result, signum * val.signum);
    }

    /**
     * Multiplies int arrays x and y to the specified lengths and places the
     * result into z.
     */
    private int[] multiplyToLen(int[] x, int xlen, int[] y, int ylen, int[] z) {
        int xstart = xlen - 1;
        int ystart = ylen - 1;

        if (z == null || z.length < (xlen + ylen)) {
            z = new int[xlen + ylen];
        }

        long carry = 0;
        for (int j = ystart, k = ystart + 1 + xstart; j >= 0; j--, k--) {
            long product = (y[j] & LONG_MASK) * (x[xstart] & LONG_MASK) + carry;
            z[k] = (int) product;
            carry = product >>> 32;
        }
        z[xstart] = (int) carry;

        for (int i = xstart - 1; i >= 0; i--) {
            carry = 0;
            for (int j = ystart, k = ystart + 1 + i; j >= 0; j--, k--) {
                long product = (y[j] & LONG_MASK) * (x[i] & LONG_MASK) + (z[k]
                        & LONG_MASK) + carry;
                z[k] = (int) product;
                carry = product >>> 32;
            }
            z[i] = (int) carry;
        }
        return z;
    }

    /**
     * Returns a BigInteger whose value is <tt>(this<sup>2</sup>)</tt>.
     *
     * @return <tt>this<sup>2</sup></tt>
     */
    private NumberZ square() {
        if (signum == 0) {
            return ZERO;
        }
        int[] z = squareToLen(mag, mag.length, null);
        return new NumberZ(trustedStripLeadingZeroInts(z), 1);
    }

    /**
     * Squares the contents of the int array x. The result is placed into the
     * int array z. The contents of x are not changed.
     */
    private static final int[] squareToLen(int[] x, int len, int[] z) {
        /*
         * The algorithm used here is adapted from Colin Plumb's C library.
         * Technique: Consider the partial products in the multiplication
         * of "abcde" by itself:
         *
         *               a  b  c  d  e
         *            *  a  b  c  d  e
         *          ==================
         *              ae be ce de ee
         *           ad bd cd dd de
         *        ac bc cc cd ce
         *     ab bb bc bd be
         *  aa ab ac ad ae
         *
         * Note that everything above the main diagonal:
         *              ae be ce de = (abcd) * e
         *           ad bd cd       = (abc) * d
         *        ac bc             = (ab) * c
         *     ab                   = (a) * b
         *
         * is a copy of everything below the main diagonal:
         *                       de
         *                 cd ce
         *           bc bd be
         *     ab ac ad ae
         *
         * Thus, the sum is 2 * (off the diagonal) + diagonal.
         *
         * This is accumulated beginning with the diagonal (which
         * consist of the squares of the digits of the input), which is then
         * divided by two, the off-diagonal added, and multiplied by two
         * again.  The low bit is simply a copy of the low bit of the
         * input, so it doesn't need special care.
         */
        int zlen = len << 1;
        if (z == null || z.length < zlen) {
            z = new int[zlen];
        }

        // Store the squares, right shifted one bit (i.e., divided by 2)
        int lastProductLowWord = 0;
        for (int j = 0, i = 0; j < len; j++) {
            long piece = (x[j] & LONG_MASK);
            long product = piece * piece;
            z[i++] = (lastProductLowWord << 31) | (int) (product >>> 33);
            z[i++] = (int) (product >>> 1);
            lastProductLowWord = (int) product;
        }

        // Add in off-diagonal sums
        for (int i = len, offset = 1; i > 0; i--, offset += 2) {
            int t = x[i - 1];
            t = mulAdd(z, x, offset, i - 1, t);
            addOne(z, offset - 1, i, t);
        }

        // Shift back up and set low bit
        primitiveLeftShift(z, zlen, 1);
        z[zlen - 1] |= x[len - 1] & 1;

        return z;
    }

    /**
     * Returns a BigInteger whose value is <tt>(this / val)</tt>.
     *
     * @param val value by which this BigInteger is to be divided.
     *
     * @return <tt>this / val</tt>
     *
     * @throws ArithmeticException <tt>val==0</tt>
     */
    public NumberZ divide(NumberZ val) {
        MutableBigInteger q = new MutableBigInteger(),
                r = new MutableBigInteger(),
                a = new MutableBigInteger(this.mag),
                b = new MutableBigInteger(val.mag);

        a.divide(b, q, r);
        return new NumberZ(q, this.signum * val.signum);
    }

    @Override
    public Element divideToFraction(Element val, Ring ring) {
        Element numerator = null;
        Element denominator = null;
        if (val instanceof NumberZ) {
            if (signum==0 & ((NumberZ)val).signum==0) return NAN;
            Element gcd = GCD(val, ring);
            numerator = divide((NumberZ) gcd);
            denominator = ((NumberZ) val).divide((NumberZ) gcd);
        }
        if(numerator==null | denominator==null) return new Fraction(this, val).cancel(ring);
        Fraction fr = (denominator.isNegative())
                ? new Fraction(numerator.negate(ring), denominator.negate(ring))
                : new Fraction(numerator, denominator);
        if (denominator.isOne(ring)) {
            return numerator;
        }
        return fr;
    }

    /**
     * Returns an array of two BigIntegers containing <tt>(this / val)</tt>
     * followed by <tt>(this % val)</tt>.
     *
     * @param val value by which this BigInteger is to be divided, and the
     * remainder computed.
     *
     * @return an array of two BigIntegers: the quotient <tt>(this / val)</tt>
     * is the initial element, and the remainder <tt>(this % val)</tt> is the
     * final element.
     *
     * @throws ArithmeticException <tt>val==0</tt>
     */
    public NumberZ[] divideAndRemainder(NumberZ val) {
        NumberZ[] result = new NumberZ[2];
        MutableBigInteger q = new MutableBigInteger(),
                r = new MutableBigInteger(),
                a = new MutableBigInteger(this.mag),
                b = new MutableBigInteger(val.mag);
        a.divide(b, q, r);
        result[0] = new NumberZ(q, this.signum * val.signum);
        result[1] = new NumberZ(r, this.signum);
        return result;
    }

/**
 * Result is a two integer numbers: qu
 * @param val
 * @param ring
 * @return 
 */
    @Override
    public Element[] divideAndRemainder(Element val,Ring ring) {
       if(val instanceof NumberZ){
        NumberZ[] result = new NumberZ[2];
        MutableBigInteger q = new MutableBigInteger(),
                r = new MutableBigInteger(),
                a = new MutableBigInteger(this.mag),
                b = new MutableBigInteger(((NumberZ)val).mag);
        a.divide(b, q, r);
        result[0] = new NumberZ(q, this.signum * ((NumberZ)val).signum);
        result[1] = new NumberZ(r, this.signum);
        return result;
       }
       if(val.isItNumber()){
       return new Element[]{this.divide(val,ring),ring.numberZERO};
       }
       return new Element[] { ring.numberZERO,this};
    }
    
    
//    @Override  
//    public Element[] quotientAndProperFraction(Element denom, Ring ring) {
//        if (!denom.isItNumber())return new Element[]{ZERO, new Fraction(this,denom)};
//        if (!(denom instanceof NumberZ)) denom= denom.toNumber(Ring.Z, ring);
//        Element[] res=divideAndRemainder(denom,ring), res2=new Element[2];
//        if (!res[1].isZero(ring)){res2[0]=res[0]; res2[1]=new Fraction(res[1], denom); 
//                                  return res2;}
//        return res;
//    }

    /**
     * Returns a BigInteger whose value is <tt>(this % val)</tt>.
     *
     * @param val value by which this BigInteger is to be divided, and the
     * remainder computed.
     *
     * @return <tt>this % val</tt>
     *
     * @throws ArithmeticException <tt>val==0</tt>
     */
    public NumberZ remainder(NumberZ val) {
        MutableBigInteger q = new MutableBigInteger(),
                r = new MutableBigInteger(),
                a = new MutableBigInteger(this.mag),
                b = new MutableBigInteger(val.mag);

        a.divide(b, q, r);
        return new NumberZ(r, this.signum);
    }

    /**
     * Returns a BigInteger whose value is <tt>(this<sup>exponent</sup>)</tt>.
     * Note that <tt>exponent</tt> is an integer rather than a BigInteger.
     *
     * @param exponent exponent to which this BigInteger is to be raised.
     *
     * @return <tt>this<sup>exponent</sup></tt>
     *
     * @throws ArithmeticException <tt>exponent</tt> is negative. (This would
     * cause the operation to yield a non-integer value.)
     */
    @Override
    public Element pow(int exponent, Ring ring) {  
        if (exponent < 0) {NumberZ res=pow(-exponent); 
           return (res.isNegative())?   new Fraction(MINUS_ONE,res.negate()): new Fraction( ONE,res );}
        return pow(exponent);
    }

    public NumberZ pow(int exponent) {
        if (exponent < 0) {
            throw new ArithmeticException("Negative exponent");
        }
        if (signum == 0) {
            return (exponent == 0 ? ONE : this);
        }

        // Perform exponentiation using repeated squaring trick
        int newSign = (signum < 0 && (exponent & 1) == 1 ? -1 : 1);
        int[] baseToPow2 = this.mag;
        int[] result = {
            1};

        while (exponent != 0) {
            if ((exponent & 1) == 1) {
                result = multiplyToLen(result, result.length,
                        baseToPow2, baseToPow2.length, null);
                result = trustedStripLeadingZeroInts(result);
            }
            if ((exponent >>>= 1) != 0) {
                baseToPow2 = squareToLen(baseToPow2, baseToPow2.length, null);
                baseToPow2 = trustedStripLeadingZeroInts(baseToPow2);
            }
        }
        return new NumberZ(result, newSign);
    }

    @Override
    public Element powTheFirst(int n, int m, Ring ring) {
        if (m == 1) {
            return pow(n);
        }
        return null;
    }

    /**
     * Returns a BigInteger whose value is the greatest common divisor of
     * <tt>abs(this)</tt> and <tt>abs(val)</tt>. Returns 0 if <tt>this==0
     * &amp;&amp; val==0</tt>.
     *
     * @param val value with which the GCD is to be computed.
     *
     * @return <tt>GCD(abs(this), abs(val))</tt>
     */
    public NumberZ gcd(NumberZ val) {
        if (val.signum == 0) {
            return this.abs();
        } else if (this.signum == 0) {
            return val.abs();
        }

        MutableBigInteger a = new MutableBigInteger(this);
        MutableBigInteger b = new MutableBigInteger(val);
        MutableBigInteger result = a.hybridGCD(b);

        return new NumberZ(result, 1);
    }

    /**
     * Left shift int array a up to len by n bits. Returns the array that
     * results from the shift since space may have to be reallocated.
     */
    private static int[] leftShift(int[] a, int len, int n) {
        int nInts = n >>> 5;
        int nBits = n & 0x1F;
        int bitsInHighWord = bitLen(a[0]);

        // If shift can be done without recopy, do so
        if (n <= (32 - bitsInHighWord)) {
            primitiveLeftShift(a, len, nBits);
            return a;
        } else { // Array must be resized
            if (nBits <= (32 - bitsInHighWord)) {
                int result[] = new int[nInts + len];
                System.arraycopy(a, 0, result, 0, len);
                primitiveLeftShift(result, result.length, nBits);
                return result;
            } else {
                int result[] = new int[nInts + len + 1];
                System.arraycopy(a, 0, result, 0, len);
                primitiveRightShift(result, result.length, 32 - nBits);
                return result;
            }
        }
    }

    // shifts a up to len right n bits assumes no leading zeros, 0<n<32
    static void primitiveRightShift(int[] a, int len, int n) {
        int n2 = 32 - n;
        for (int i = len - 1, c = a[i]; i > 0; i--) {
            int b = c;
            c = a[i - 1];
            a[i] = (c << n2) | (b >>> n);
        }
        a[0] >>>= n;
    }

    // shifts a up to len left n bits assumes no leading zeros, 0<=n<32
    public static void primitiveLeftShift(int[] a, int len, int n) {
        if (len == 0 || n == 0) {
            return;
        }

        int n2 = 32 - n;
        for (int i = 0, c = a[i], m = i + len - 1; i < m; i++) {
            int b = c;
            c = a[i + 1];
            a[i] = (b << n) | (c >>> n2);
        }
        a[len - 1] <<= n;
    }

    /**
     * Calculate bitlength of contents of the first len elements an int array,
     * assuming there are no leading zero ints.
     */
    private static int bitLength(int[] val, int len) {
        if (len == 0) {
            return 0;
        }
        return ((len - 1) << 5) + bitLen(val[0]);
    }

    /**
     * Returns a BigInteger whose value is the absolute value of this
     * BigInteger.
     *
     * @return <tt>abs(this)</tt>
     */
    @Override
    public Element abs(Ring ring) {
        return (signum >= 0 ? this : this.negate());
    }

    public NumberZ abs() {
        return (signum >= 0 ? this : this.negate());
    }

    /**
     * Returns a BigInteger whose value is <tt>(-this)</tt>.
     *
     * @return <tt>-this</tt>
     */
    public NumberZ negate() {
        return new NumberZ(this.mag, -this.signum);
    }

    @Override
    public Element negate(Ring ring) {
        return new NumberZ(this.mag, -this.signum);
    }

//    /**
//     * Returns a BigInteger whose value is <tt>(-this)</tt>.
//     *
//     * @return <tt>-this</tt>
//     */
//    @Override
//    public NumberZ negate() {
//        return new NumberZ(this.mag, -this.signum);
//    }
    /**
     * Returns the signum function of this BigInteger.
     *
     * @return -1, 0 or 1 as the value of this BigInteger is negative, zero or
     * positive.
     */
    @Override
    public int signum() {
        return this.signum;
    }

    // Modular Arithmetic Operations
    /**
     * Returns a BigInteger whose value is <tt>(this mod m</tt>). This method
     * differs from <tt>remainder</tt> in that it always returns a
     * <i>non-negative</i> BigInteger.
     *
     * @param MOD the modulus.
     *
     * @return <tt>this mod m</tt>
     *
     * @throws ArithmeticException <tt>m &lt;= 0</tt>
     * @see #remainder
     */
    public NumberZ mod(NumberZ MOD) {
        if (MOD.signum <= 0) {
            throw new ArithmeticException("BigInteger: modulus not positive");
        }
        NumberZ result = this.remainder(MOD);
        return (result.signum >= 0 ? result : result.add(MOD));
    }

    @Override
    public NumberZ Mod(Element MOD, Ring ring) {
        NumberZ MODdiv2 = ((NumberZ) MOD).shiftRight(1);
        NumberZ result = this.remainder((NumberZ) MOD);
        if (result.compareTo(MODdiv2) == 1) {
            return result.subtract((NumberZ) MOD);
        }
        NumberZ rs = result.add((NumberZ) MOD);
        if (rs.compareTo(MODdiv2) == 1) {
            return result;
        } else {
            return rs;
        }
    }

    @Override
    public NumberZ Mod(Ring ring) {
        return Mod(ring.MOD, ring);
    }
    //  public NumberZ Mod(Element MOD) {return Mod((NumberZ) MOD);}

    @Override
    public NumberZ mod(Element MOD, Ring ring) {
        return mod((NumberZ) MOD);
    }

    /**
     * Returns a BigInteger whose value is <tt>(this<sup>exponent</sup> mod
     * m)</tt>. (Unlike <tt>pow</tt>, this method permits negative exponents.)
     *
     * @param exponent the exponent.
     * @param m the modulus.
     *
     * @return <tt>this<sup>exponent</sup> mod m</tt>
     *
     * @throws ArithmeticException <tt>m &lt;= 0</tt>
     * @see #modInverse
     */
    public NumberZ modPow(NumberZ exponent, NumberZ m) {
        if (m.signum <= 0) {
            throw new ArithmeticException("BigInteger: modulus not positive");
        }

        // Trivial cases
        if (exponent.signum == 0) {
            return (m.equals(ONE) ? ZERO : ONE);
        }

        if (this.equals(ONE)) {
            return (m.equals(ONE) ? ZERO : ONE);
        }

        if (this.equals(ZERO) && exponent.signum >= 0) {
            return ZERO;
        }

        if (this.equals(negConst[1]) && (!exponent.testBit(0))) {
            return (m.equals(ONE) ? ZERO : ONE);
        }

        boolean invertResult;
        if ((invertResult = (exponent.signum < 0))) {
            exponent = exponent.negate();
        }

        NumberZ base = (this.signum < 0 || this.compareTo(m) >= 0
                ? this.mod(m) : this);
        NumberZ result;
        if (m.testBit(0)) { // odd modulus
            result = base.oddModPow(exponent, m);
        } else {
            /*
             * Even modulus.  Tear it into an "odd part" (m1) and power of two
             * (m2), exponentiate mod m1, manually exponentiate mod m2, and
             * use Chinese Remainder Theorem to combine results.
             */

            // Tear m apart into odd part (m1) and power of 2 (m2)
            int p = m.getLowestSetBit(); // Max pow of 2 that divides m

            NumberZ m1 = m.shiftRight(p); // m/2**p
            NumberZ m2 = ONE.shiftLeft(p); // 2**p

            // Calculate new base from m1
            NumberZ base2 = (this.signum < 0 || this.compareTo(m1) >= 0
                    ? this.mod(m1) : this);

            // Caculate (base ** exponent) mod m1.
            NumberZ a1 = (m1.equals(ONE) ? ZERO : base2.oddModPow(exponent, m1));

            // Calculate (this ** exponent) mod m2
            NumberZ a2 = base.modPow2(exponent, p);

            // Combine results using Chinese Remainder Theorem
            NumberZ y1 = m2.modInverse(m1);
            NumberZ y2 = m1.modInverse(m2);

            result = a1.multiply(m2).multiply(y1).add(a2.multiply(m1).multiply(
                    y2)).mod(m);
        }

        return (invertResult ? result.modInverse(m) : result);
    }
    static int[] bnExpModThreshTable = {
        7, 25, 81, 241, 673, 1793,
        Integer.MAX_VALUE}; // Sentinel

    /**
     * Returns a BigInteger whose value is x to the power of y mod z. Assumes: z
     * is odd && x < z.
     */
    private NumberZ oddModPow(NumberZ y, NumberZ z) {
        /*
         * The algorithm is adapted from Colin Plumb's C library.
         *
         * The window algorithm:
         * The idea is to keep a running product of b1 = n^(high-order bits of exp)
         * and then keep appending exponent bits to it.  The following patterns
         * apply to a 3-bit window (k = 3):
         * To append   0: square
         * To append   1: square, multiply by n^1
         * To append  10: square, multiply by n^1, square
         * To append  11: square, square, multiply by n^3
         * To append 100: square, multiply by n^1, square, square
         * To append 101: square, square, square, multiply by n^5
         * To append 110: square, square, multiply by n^3, square
         * To append 111: square, square, square, multiply by n^7
         *
         * Since each pattern involves only one multiply, the longer the pattern
         * the better, except that a 0 (no multiplies) can be appended directly.
         * We precompute a table of odd powers of n, up to 2^k, and can then
         * multiply k bits of exponent at a time.  Actually, assuming random
         * exponents, there is on average one zero bit between needs to
         * multiply (1/2 of the time there's none, 1/4 of the time there's 1,
         * 1/8 of the time, there's 2, 1/32 of the time, there's 3, etc.), so
         * you have to do one multiply per k+1 bits of exponent.
         *
         * The loop walks down the exponent, squaring the result buffer as
         * it goes.  There is a wbits+1 bit lookahead buffer, buf, that is
         * filled with the upcoming exponent bits.  (What is read after the
         * end of the exponent is unimportant, but it is filled with zero here.)
         * When the most-significant bit of this buffer becomes set, i.e.
         * (buf & tblmask) != 0, we have to decide what pattern to multiply
         * by, and when to do it.  We decide, remember to do it in future
         * after a suitable number of squarings have passed (e.g. a pattern
         * of "100" in the buffer requires that we multiply by n^1 immediately;
         * a pattern of "110" calls for multiplying by n^3 after one more
         * squaring), clear the buffer, and continue.
         *
         * When we start, there is one more optimization: the result buffer
         * is implcitly one, so squaring it or multiplying by it can be
         * optimized away.  Further, if we start with a pattern like "100"
         * in the lookahead window, rather than placing n into the buffer
         * and then starting to square it, we have already computed n^2
         * to compute the odd-powers table, so we can place that into
         * the buffer and save a squaring.
         *
         * This means that if you have a k-bit window, to compute n^z,
         * where z is the high k bits of the exponent, 1/2 of the time
         * it requires no squarings.  1/4 of the time, it requires 1
         * squaring, ... 1/2^(k-1) of the time, it reqires k-2 squarings.
         * And the remaining 1/2^(k-1) of the time, the top k bits are a
         * 1 followed by k-1 0 bits, so it again only requires k-2
         * squarings, not k-1.  The average of these is 1.  Add that
         * to the one squaring we have to do to compute the table,
         * and you'll see that a k-bit window saves k-2 squarings
         * as well as reducing the multiplies.  (It actually doesn't
         * hurt in the case k = 1, either.)
         */
        // Special case for exponent of one
        if (y.equals(ONE)) {
            return this;
        }

        // Special case for base of zero
        if (signum == 0) {
            return ZERO;
        }

        int[] base = mag.clone();
        int[] exp = y.mag;
        int[] mod = z.mag;
        int modLen = mod.length;

        // Select an appropriate window size
        int wbits = 0;
        int ebits = bitLength(exp, exp.length);
        // if exponent is 65537 (0x10001), use minimum window size
        if ((ebits != 17) || (exp[0] != 65537)) {
            while (ebits > bnExpModThreshTable[wbits]) {
                wbits++;
            }
        }

        // Calculate appropriate table size
        int tblmask = 1 << wbits;

        // Allocate table for precomputed odd powers of base in Montgomery form
        int[][] table = new int[tblmask][];
        for (int i = 0; i < tblmask; i++) {
            table[i] = new int[modLen];
        }

        // Compute the modular inverse
        int inv = -MutableBigInteger.inverseMod32(mod[modLen - 1]);

        // Convert base to Montgomery form
        int[] a = leftShift(base, base.length, modLen << 5);

        MutableBigInteger q = new MutableBigInteger(),
                r = new MutableBigInteger(),
                a2 = new MutableBigInteger(a),
                b2 = new MutableBigInteger(mod);

        a2.divide(b2, q, r);
        table[0] = r.toIntArray();

        // Pad table[0] with leading zeros so its length is at least modLen
        if (table[0].length < modLen) {
            int offset = modLen - table[0].length;
            int[] t2 = new int[modLen];
            for (int i = 0; i < table[0].length; i++) {
                t2[i + offset] = table[0][i];
            }
            table[0] = t2;
        }

        // Set b to the square of the base
        int[] b = squareToLen(table[0], modLen, null);
        b = montReduce(b, mod, modLen, inv);

        // Set t to high half of b
        int[] t = new int[modLen];
        for (int i = 0; i < modLen; i++) {
            t[i] = b[i];
        }

        // Fill in the table with odd powers of the base
        for (int i = 1; i < tblmask; i++) {
            int[] prod = multiplyToLen(t, modLen, table[i - 1], modLen, null);
            table[i] = montReduce(prod, mod, modLen, inv);
        }

        // Pre load the window that slides over the exponent
        int bitpos = 1 << ((ebits - 1) & (32 - 1));

        int buf = 0;
        int elen = exp.length;
        int eIndex = 0;
        for (int i = 0; i <= wbits; i++) {
            buf = (buf << 1) | (((exp[eIndex] & bitpos) != 0) ? 1 : 0);
            bitpos >>>= 1;
            if (bitpos == 0) {
                eIndex++;
                bitpos = 1 << (32 - 1);
                elen--;
            }
        }

        int multpos = ebits;

        // The first iteration, which is hoisted out of the main loop
        ebits--;
        boolean isone = true;

        multpos = ebits - wbits;
        while ((buf & 1) == 0) {
            buf >>>= 1;
            multpos++;
        }

        int[] mult = table[buf >>> 1];

        buf = 0;
        if (multpos == ebits) {
            isone = false;
        }

        // The main loop
        while (true) {
            ebits--;
            // Advance the window
            buf <<= 1;

            if (elen != 0) {
                buf |= ((exp[eIndex] & bitpos) != 0) ? 1 : 0;
                bitpos >>>= 1;
                if (bitpos == 0) {
                    eIndex++;
                    bitpos = 1 << (32 - 1);
                    elen--;
                }
            }

            // Examine the window for pending multiplies
            if ((buf & tblmask) != 0) {
                multpos = ebits - wbits;
                while ((buf & 1) == 0) {
                    buf >>>= 1;
                    multpos++;
                }
                mult = table[buf >>> 1];
                buf = 0;
            }

            // Perform multiply
            if (ebits == multpos) {
                if (isone) {
                    b = mult.clone();
                    isone = false;
                } else {
                    t = b;
                    a = multiplyToLen(t, modLen, mult, modLen, a);
                    a = montReduce(a, mod, modLen, inv);
                    t = a;
                    a = b;
                    b = t;
                }
            }

            // Check if done
            if (ebits == 0) {
                break;
            }

            // Square the input
            if (!isone) {
                t = b;
                a = squareToLen(t, modLen, a);
                a = montReduce(a, mod, modLen, inv);
                t = a;
                a = b;
                b = t;
            }
        }

        // Convert result out of Montgomery form and return
        int[] t2 = new int[2 * modLen];
        for (int i = 0; i < modLen; i++) {
            t2[i + modLen] = b[i];
        }

        b = montReduce(t2, mod, modLen, inv);

        t2 = new int[modLen];
        for (int i = 0; i < modLen; i++) {
            t2[i] = b[i];
        }

        return new NumberZ(1, t2);
    }

    /**
     * Montgomery reduce n, modulo mod. This reduces modulo mod and divides by
     * 2^(32*mlen). Adapted from Colin Plumb's C library.
     */
    private static int[] montReduce(int[] n, int[] mod, int mlen, int inv) {
        int c = 0;
        int len = mlen;
        int offset = 0;

        do {
            int nEnd = n[n.length - 1 - offset];
            int carry = mulAdd(n, mod, offset, mlen, inv * nEnd);
            c += addOne(n, offset, mlen, carry);
            offset++;
        } while (--len > 0);

        while (c > 0) {
            c += subN(n, mod, mlen);
        }

        while (intArrayCmpToLen(n, mod, mlen) >= 0) {
            subN(n, mod, mlen);
        }

        return n;
    }

    /*
     * Returns -1, 0 or +1 as big-endian unsigned int array arg1 is less than,
     * equal to, or greater than arg2 up to length len.
     */
    private static int intArrayCmpToLen(int[] arg1, int[] arg2, int len) {
        for (int i = 0; i < len; i++) {
            long b1 = arg1[i] & LONG_MASK;
            long b2 = arg2[i] & LONG_MASK;
            if (b1 < b2) {
                return -1;
            }
            if (b1 > b2) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Subtracts two numbers of same length, returning borrow.
     */
    private static int subN(int[] a, int[] b, int len) {
        long sum = 0;

        while (--len >= 0) {
            sum = (a[len] & LONG_MASK) - (b[len] & LONG_MASK) + (sum >> 32);
            a[len] = (int) sum;
        }

        return (int) (sum >> 32);
    }

    /**
     * Multiply an array by one word k and add to result, return the carry
     */
    static int mulAdd(int[] out, int[] in, int offset, int len, int k) {
        long kLong = k & LONG_MASK;
        long carry = 0;

        offset = out.length - offset - 1;
        for (int j = len - 1; j >= 0; j--) {
            long product = (in[j] & LONG_MASK) * kLong + (out[offset]
                    & LONG_MASK) + carry;
            out[offset--] = (int) product;
            carry = product >>> 32;
        }
        return (int) carry;
    }

    /**
     * Add one word to the number a mlen words into a. Return the resulting
     * carry.
     */
    static int addOne(int[] a, int offset, int mlen, int carry) {
        offset = a.length - 1 - mlen - offset;
        long t = (a[offset] & LONG_MASK) + (carry & LONG_MASK);

        a[offset] = (int) t;
        if ((t >>> 32) == 0) {
            return 0;
        }
        while (--mlen >= 0) {
            if (--offset < 0) { // Carry out of number
                return 1;
            } else {
                a[offset]++;
                if (a[offset] != 0) {
                    return 0;
                }
            }
        }
        return 1;
    }

    /**
     * Returns a BigInteger whose value is (this ** exponent) mod (2**p)
     */
    private NumberZ modPow2(NumberZ exponent, int p) {
        /*
         * Perform exponentiation using repeated squaring trick, chopping off
         * high order bits as indicated by modulus.
         */
        NumberZ result = valueOf(1);
        NumberZ baseToPow2 = this.mod2(p);
        int expOffset = 0;

        int limit = exponent.bitLength();

        if (this.testBit(0)) {
            limit = (p - 1) < limit ? (p - 1) : limit;
        }

        while (expOffset < limit) {
            if (exponent.testBit(expOffset)) {
                result = result.multiply(baseToPow2).mod2(p);
            }
            expOffset++;
            if (expOffset < limit) {
                baseToPow2 = baseToPow2.square().mod2(p);
            }
        }

        return result;
    }

    /**
     * Returns a BigInteger whose value is this mod(2**p). Assumes that this
     * BigInteger &gt;= 0 and p &gt; 0.
     */
    private NumberZ mod2(int p) {
        if (bitLength() <= p) {
            return this;
        }
        // Copy remaining ints of mag
        int numInts = (p + 31) / 32;
        int[] mag = new int[numInts];
        for (int i = 0; i < numInts; i++) {
            mag[i] = this.mag[i + (this.mag.length - numInts)];
        }
        // Mask out any excess bits
        int excessBits = (numInts << 5) - p;
        mag[0] &= (1L << (32 - excessBits)) - 1;

        return (mag[0] == 0 ? new NumberZ(1, mag) : new NumberZ(mag, 1));
    }

    /**
     * Returns a BigInteger whose value is <tt>(this<sup>-1</sup> mod m)</tt>.
     *
     * @param m the modulus.
     *
     * @return <tt>this<sup>-1</sup> mod m</tt>.
     *
     * @throws ArithmeticException <tt> m &lt;= 0</tt>, or this BigInteger has
     * no multiplicative inverse mod m (that is, this BigInteger is not
     * <i>relatively prime</i> to m).
     */
    public NumberZ modInverse(NumberZ m) {
        if (m.signum != 1) {
            throw new ArithmeticException("BigInteger: modulus not positive");
        }

        if (m.equals(ONE)) {
            return ZERO;
        }

        // Calculate (this mod m)
        NumberZ modVal = this;
        if (signum < 0 || (intArrayCmp(mag, m.mag) >= 0)) {
            modVal = this.mod(m);
        }

        if (modVal.equals(ONE)) {
            return ONE;
        }

        MutableBigInteger a = new MutableBigInteger(modVal);
        MutableBigInteger b = new MutableBigInteger(m);

        MutableBigInteger result = a.mutableModInverse(b);
        return new NumberZ(result, 1);
    }

    // Shift Operations
    /**
     * Returns a BigInteger whose value is <tt>(this &lt;&lt; n)</tt>. The shift
     * distance, <tt>n</tt>, may be negative, in which case this method performs
     * a right shift. (Computes <tt>floor(this * 2<sup>n</sup>)</tt>.)
     *
     * @param n shift distance, in bits.
     *
     * @return <tt>this &lt;&lt; n</tt>
     *
     * @see #shiftRight
     */
    public NumberZ shiftLeft(int n) {
        if (signum == 0) {
            return ZERO;
        }
        if (n == 0) {
            return this;
        }
        if (n < 0) {
            return shiftRight(-n);
        }

        int nInts = n >>> 5;
        int nBits = n & 0x1f;
        int magLen = mag.length;
        int newMag[] = null;

        if (nBits == 0) {
            newMag = new int[magLen + nInts];
            for (int i = 0; i < magLen; i++) {
                newMag[i] = mag[i];
            }
        } else {
            int i = 0;
            int nBits2 = 32 - nBits;
            int highBits = mag[0] >>> nBits2;
            if (highBits != 0) {
                newMag = new int[magLen + nInts + 1];
                newMag[i++] = highBits;
            } else {
                newMag = new int[magLen + nInts];
            }
            int j = 0;
            while (j < magLen - 1) {
                newMag[i++] = mag[j++] << nBits | mag[j] >>> nBits2;
            }
            newMag[i] = mag[j] << nBits;
        }

        return new NumberZ(newMag, signum);
    }

    /**
     * Returns a BigInteger whose value is <tt>(this &gt;&gt; n)</tt>. Sign
     * extension is performed. The shift distance, <tt>n</tt>, may be negative,
     * in which case this method performs a left shift. (Computes <tt>floor(this
     * / 2<sup>n</sup>)</tt>.)
     *
     * @param n shift distance, in bits.
     *
     * @return <tt>this &gt;&gt; n</tt>
     *
     * @see #shiftLeft
     */
    public NumberZ shiftRight(int n) {
        if (n == 0) {
            return this;
        }
        if (n < 0) {
            return shiftLeft(-n);
        }

        int nInts = n >>> 5;
        int nBits = n & 0x1f;
        int magLen = mag.length;
        int newMag[] = null;

        // Special case: entire contents shifted off the end
        if (nInts >= magLen) {
            return (signum >= 0 ? ZERO : NEGCONST[1]);
        }

        if (nBits == 0) {
            int newMagLen = magLen - nInts;
            newMag = new int[newMagLen];
            for (int i = 0; i < newMagLen; i++) {
                newMag[i] = mag[i];
            }
        } else {
            int i = 0;
            int highBits = mag[0] >>> nBits;
            if (highBits != 0) {
                newMag = new int[magLen - nInts];
                newMag[i++] = highBits;
            } else {
                newMag = new int[magLen - nInts - 1];
            }

            int nBits2 = 32 - nBits;
            int j = 0;
            while (j < magLen - nInts - 1) {
                newMag[i++] = (mag[j++] << nBits2) | (mag[j] >>> nBits);
            }
        }

        if (signum < 0) {
            // Find out whether any one-bits were shifted off the end.
            boolean onesLost = false;
            for (int i = magLen - 1, j = magLen - nInts; i >= j && !onesLost;
                    i--) {
                onesLost = (mag[i] != 0);
            }
            if (!onesLost && nBits != 0) {
                onesLost = (mag[magLen - nInts - 1] << (32 - nBits) != 0);
            }

            if (onesLost) {
                newMag = javaIncrement(newMag);
            }
        }

        return new NumberZ(newMag, signum);
    }

    int[] javaIncrement(int[] val) {
        boolean done = false;
        int lastSum = 0;
        for (int i = val.length - 1; i >= 0 && lastSum == 0; i--) {
            lastSum = (val[i] += 1);
        }
        if (lastSum == 0) {
            val = new int[val.length + 1];
            val[0] = 1;
        }
        return val;
    }

    // Bitwise Operations
    /**
     * Returns a BigInteger whose value is <tt>(this &amp; val)</tt>. (This
     * method returns a negative BigInteger if and only if this and val are both
     * negative.)
     *
     * @param val value to be AND'ed with this BigInteger.
     *
     * @return <tt>this &amp; val</tt>
     */
    public NumberZ and(NumberZ val) {
        int[] result = new int[Math.max(intLength(), val.intLength())];
        for (int i = 0; i < result.length; i++) {
            result[i] = getInt(result.length - i - 1)
                    & val.getInt(result.length - i - 1);
        }

        return valueOf(result);
    }

    /**
     * Returns a BigInteger whose value is <tt>(this | val)</tt>. (This method
     * returns a negative BigInteger if and only if either this or val is
     * negative.)
     *
     * @param val value to be OR'ed with this BigInteger.
     *
     * @return <tt>this | val</tt>
     */
    public NumberZ or(NumberZ val) {
        int[] result = new int[Math.max(intLength(), val.intLength())];
        for (int i = 0; i < result.length; i++) {
            result[i] = getInt(result.length - i - 1)
                    | val.getInt(result.length - i - 1);
        }

        return valueOf(result);
    }

    /**
     * Returns a BigInteger whose value is <tt>(this ^ val)</tt>. (This method
     * returns a negative BigInteger if and only if exactly one of this and val
     * are negative.)
     *
     * @param val value to be XOR'ed with this BigInteger.
     *
     * @return <tt>this ^ val</tt>
     */
    public NumberZ xor(NumberZ val) {
        int[] result = new int[Math.max(intLength(), val.intLength())];
        for (int i = 0; i < result.length; i++) {
            result[i] = getInt(result.length - i - 1)
                    ^ val.getInt(result.length - i - 1);
        }

        return valueOf(result);
    }

    /**
     * Returns a BigInteger whose value is <tt>(~this)</tt>. (This method
     * returns a negative value if and only if this BigInteger is non-negative.)
     *
     * @return <tt>~this</tt>
     */
    public NumberZ not() {
        int[] result = new int[intLength()];
        for (int i = 0; i < result.length; i++) {
            result[i] = ~getInt(result.length - i - 1);
        }

        return valueOf(result);
    }

    /**
     * Returns a BigInteger whose value is <tt>(this &amp; ~val)</tt>. This
     * method, which is equivalent to <tt>and(val.not())</tt>, is provided as a
     * convenience for masking operations. (This method returns a negative
     * BigInteger if and only if <tt>this</tt> is negative and <tt>val</tt> is
     * positive.)
     *
     * @param val value to be complemented and AND'ed with this BigInteger.
     *
     * @return <tt>this &amp; ~val</tt>
     */
    public NumberZ andNot(NumberZ val) {
        int[] result = new int[Math.max(intLength(), val.intLength())];
        for (int i = 0; i < result.length; i++) {
            result[i] = getInt(result.length - i - 1)
                    & ~val.getInt(result.length - i - 1);
        }

        return valueOf(result);
    }

    // Single Bit Operations
    /**
     * Returns <tt>true</tt> if and only if the designated bit is set. (Computes
     * <tt>((this &amp; (1&lt;&lt;n)) != 0)</tt>.)
     *
     * @param n index of bit to test.
     *
     * @return <tt>true</tt> if and only if the designated bit is set.
     *
     * @throws ArithmeticException <tt>n</tt> is negative.
     */
    public boolean testBit(int n) {
        if (n < 0) {
            throw new ArithmeticException("Negative bit address");
        }

        return (getInt(n / 32) & (1 << (n % 32))) != 0;
    }

    /**
     * Is this number is even?
     *
     * @return true - in the case of even, false - in the case of odd
     */
    @Override
    public boolean isEven() {if (mag.length==0) return true;
        return  (mag[0] & 1) == 0;
    }

    /**
     * Is this number is odd?
     *
     * @return true - in the case of odd, false - in the case of even
     */
    public boolean isOdd() {
        return (mag[0] & 1) != 0;
    }

    /**
     * Returns a BigInteger whose value is equivalent to this BigInteger with
     * the designated bit set. (Computes <tt>(this | (1&lt;&lt;n))</tt>.)
     *
     * @param n index of bit to set.
     *
     * @return <tt>this | (1&lt;&lt;n)</tt>
     *
     * @throws ArithmeticException <tt>n</tt> is negative.
     */
    public NumberZ setBit(int n) {
        if (n < 0) {
            throw new ArithmeticException("Negative bit address");
        }

        int intNum = n / 32;
        int[] result = new int[Math.max(intLength(), intNum + 2)];

        for (int i = 0; i < result.length; i++) {
            result[result.length - i - 1] = getInt(i);
        }

        result[result.length - intNum - 1] |= (1 << (n % 32));

        return valueOf(result);
    }

    /**
     * Returns a BigInteger whose value is equivalent to this BigInteger with
     * the designated bit cleared. (Computes <tt>(this &amp;
     * ~(1&lt;&lt;n))</tt>.)
     *
     * @param n index of bit to clear.
     *
     * @return <tt>this & ~(1&lt;&lt;n)</tt>
     *
     * @throws ArithmeticException <tt>n</tt> is negative.
     */
    public NumberZ clearBit(int n) {
        if (n < 0) {
            throw new ArithmeticException("Negative bit address");
        }

        int intNum = n / 32;
        int[] result = new int[Math.max(intLength(), (n + 1) / 32 + 1)];

        for (int i = 0; i < result.length; i++) {
            result[result.length - i - 1] = getInt(i);
        }

        result[result.length - intNum - 1] &= ~(1 << (n % 32));

        return valueOf(result);
    }

    /**
     * Returns a BigInteger whose value is equivalent to this BigInteger with
     * the designated bit flipped. (Computes <tt>(this ^ (1&lt;&lt;n))</tt>.)
     *
     * @param n index of bit to flip.
     *
     * @return <tt>this ^ (1&lt;&lt;n)</tt>
     *
     * @throws ArithmeticException <tt>n</tt> is negative.
     */
    public NumberZ flipBit(int n) {
        if (n < 0) {
            throw new ArithmeticException("Negative bit address");
        }

        int intNum = n / 32;
        int[] result = new int[Math.max(intLength(), intNum + 2)];

        for (int i = 0; i < result.length; i++) {
            result[result.length - i - 1] = getInt(i);
        }

        result[result.length - intNum - 1] ^= (1 << (n % 32));

        return valueOf(result);
    }

    /**
     * Returns the index of the rightmost (lowest-order) one bit in this
     * BigInteger (the number of zero bits to the right of the rightmost one
     * bit). Returns -1 if this BigInteger contains no one bits. (Computes
     * <tt>(this==0? -1 : log<sub>2</sub>(this &amp; -this))</tt>.)
     *
     * @return index of the rightmost one bit in this BigInteger.
     */
    public int getLowestSetBit() {
        /*
         * Initialize lowestSetBit field the first time this method is
         * executed. This method depends on the atomicity of int modifies;
         * without this guarantee, it would have to be synchronized.
         */
        if (lowestSetBit == -2) {
            if (signum == 0) {
                lowestSetBit = -1;
            } else {
                // Search for lowest order nonzero int
                int i, b;
                for (i = 0; (b = getInt(i)) == 0; i++) {
                }
                lowestSetBit = (i << 5) + trailingZeroCnt(b);
            }
        }
        return lowestSetBit;
    }

    // Miscellaneous Bit Operations
    /**
     * Returns the number of bits in the minimal two's-complement representation
     * of this BigInteger, <i>excluding</i> a sign bit. For positive
     * BigIntegers, this is equivalent to the number of bits in the ordinary
     * binary representation. (Computes <tt>(ceil(log<sub>2</sub>(this &lt; 0 ?
     * -this : this+1)))</tt>.)
     *
     * @return number of bits in the minimal two's-complement representation of
     * this BigInteger, <i>excluding</i> a sign bit.
     */
    public int bitLength() {
        /*
         * Initialize bitLength field the first time this method is executed.
         * This method depends on the atomicity of int modifies; without
         * this guarantee, it would have to be synchronized.
         */
        if (bitLength == -1) {
            if (signum == 0) {
                bitLength = 0;
            } else {
                // Calculate the bit length of the magnitude
                int magBitLength = ((mag.length - 1) << 5) + bitLen(mag[0]);

                if (signum < 0) {
                    // Check if magnitude is a power of two
                    boolean pow2 = (bitCnt(mag[0]) == 1);
                    for (int i = 1; i < mag.length && pow2; i++) {
                        pow2 = (mag[i] == 0);
                    }

                    bitLength = (pow2 ? magBitLength - 1 : magBitLength);
                } else {
                    bitLength = magBitLength;
                }
            }
        }
        return bitLength;
    }

    /**
     * bitLen(val) is the number of bits in val.
     */
    public static int bitLen(int w) {
        // Binary search - decision tree (5 tests, rarely 6)
        return (w < 1 << 15 ? (w < 1 << 7 ? (w < 1 << 3 ? (w < 1 << 1 ? (w < 1
                << 0 ? (w < 0 ? 32 : 0) : 1) : (w < 1 << 2 ? 2 : 3)) : (w < 1
                << 5 ? (w < 1 << 4 ? 4 : 5) : (w < 1 << 6 ? 6 : 7))) : (w < 1
                << 11 ? (w < 1 << 9 ? (w < 1 << 8 ? 8 : 9) : (w < 1 << 10 ? 10
                : 11)) : (w < 1
                << 13 ? (w < 1 << 12 ? 12 : 13) : (w < 1 << 14 ? 14 : 15)))) : (w < 1
                << 23 ? (w < 1 << 19 ? (w < 1 << 17 ? (w < 1 << 16 ? 16 : 17) : (w < 1
                << 18 ? 18 : 19)) : (w < 1 << 21 ? (w < 1 << 20 ? 20 : 21) : (w < 1
                << 22 ? 22 : 23))) : (w < 1 << 27 ? (w < 1 << 25 ? (w < 1 << 24
                ? 24 : 25) : (w < 1
                << 26 ? 26 : 27)) : (w < 1 << 29 ? (w < 1 << 28 ? 28 : 29) : (w < 1
                << 30 ? 30 : 31)))));
    }

    /*
     * trailingZeroTable[i] is the number of trailing zero bits in the binary
     * representation of i.
     */
    public final static byte trailingZeroTable[] = {
        -25, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        6, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        7, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        6, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0};

    /**
     * Returns the number of bits in the two's complement representation of this
     * BigInteger that differ from its sign bit. This method is useful when
     * implementing bit-vector style sets atop BigIntegers.
     *
     * @return number of bits in the two's complement representation of this
     * BigInteger that differ from its sign bit.
     */
    public int bitCount() {
        /*
         * Initialize bitCount field the first time this method is executed.
         * This method depends on the atomicity of int modifies; without
         * this guarantee, it would have to be synchronized.
         */
        if (bitCount == -1) {
            // Count the bits in the magnitude
            int magBitCount = 0;
            for (int i = 0; i < mag.length; i++) {
                magBitCount += bitCnt(mag[i]);
            }

            if (signum < 0) {
                // Count the trailing zeros in the magnitude
                int magTrailingZeroCount = 0, j;
                for (j = mag.length - 1; mag[j] == 0; j--) {
                    magTrailingZeroCount += 32;
                }
                magTrailingZeroCount +=
                        trailingZeroCnt(mag[j]);

                bitCount = magBitCount + magTrailingZeroCount - 1;
            } else {
                bitCount = magBitCount;
            }
        }
        return bitCount;
    }

    static int bitCnt(int val) {
        val -= (0xaaaaaaaa & val) >>> 1;
        val = (val & 0x33333333) + ((val >>> 2) & 0x33333333);
        val = val + (val >>> 4) & 0x0f0f0f0f;
        val += val >>> 8;
        val += val >>> 16;
        return val & 0xff;
    }

    public static int trailingZeroCnt(int val) {
        // Loop unrolled for performance
        int byteVal = val & 0xff;
        if (byteVal != 0) {
            return trailingZeroTable[byteVal];
        }
        byteVal = (val >>> 8) & 0xff;
        if (byteVal != 0) {
            return trailingZeroTable[byteVal] + 8;
        }
        byteVal = (val >>> 16) & 0xff;
        if (byteVal != 0) {
            return trailingZeroTable[byteVal] + 16;
        }
        byteVal = (val >>> 24) & 0xff;
        return trailingZeroTable[byteVal] + 24;
    }

    // Primality Testing
    /**
     * Returns <tt>true</tt> if this BigInteger is probably prime,
     * <tt>false</tt> if it's definitely composite. If <tt>certainty</tt> is
     * <tt> &lt;= 0</tt>, <tt>true</tt> is returned.
     *
     * @param certainty a measure of the uncertainty that the caller is willing
     * to tolerate: if the call returns <tt>true</tt> the probability that this
     * BigInteger is prime exceeds <tt>(1 - 1/2<sup>certainty</sup>)</tt>. The
     * execution time of this method is proportional to the value of this
     * parameter.
     *
     * @return <tt>true</tt> if this BigInteger is probably prime,
     * <tt>false</tt> if it's definitely composite.
     */
    public boolean isProbablePrime(int certainty) {
        if (certainty <= 0) {
            return true;
        }
        NumberZ w = this.abs();
        if (w.equals(TWO)) {
            return true;
        }
        if (!w.testBit(0) || w.equals(ONE)) {
            return false;
        }

        return w.primeToCertainty(certainty);
    }

    public static NumberZ add(Element t, Element a, Ring ring) {
        return (NumberZ) ((NumberZ) t).add(a, ring);
    }

    // Comparison Operations
    /**
     * Compares this BigInteger with the specified BigInteger. This method is
     * provided in preference to individual methods for each of the six boolean
     * comparison operators (&lt;, ==, &gt;, &gt;=, !=, &lt;=). The suggested
     * idiom for performing these comparisons is: <tt>(x.compareTo(y)</tt>
     * &lt;<i>op</i>&gt; <tt>0)</tt>, where &lt;<i>op</i>&gt; is one of the six
     * comparison operators.
     *
     * @param val BigInteger to which this BigInteger is to be compared.
     *
     * @return -1, 0 or 1 as this BigInteger is numerically less than, equal to,
     * or greater than <tt>val</tt>.
     */
    public int compareTo(NumberZ val) {
        return (signum == val.signum
                ? signum * intArrayCmp(mag, val.mag)
                : (signum > val.signum ? 1 : -1));
    }

//    public int compareTo(NumberZ val) {
//        if ((signum > NUMBER_SCALE) || (val.signum > NUMBER_SCALE)) {
//            if ((signum == NAN.signum) || (val.signum == NAN.signum)) {
//                return 0;
//            }
//            if (signum == POSITIVE_INFINITY.signum) {
//                return (val.signum == POSITIVE_INFINITY.signum) ? 0 : 1;
//            }
//            if (signum == NEGATIVE_INFINITY.signum) {
//                return (val.signum == NEGATIVE_INFINITY.signum) ? 0 : -1;
//            }
//            return 0;
//        }
//        return (signum == val.signum
//                ? signum * intArrayCmp(mag, val.mag)
//                : (signum > val.signum ? 1 : -1));
//    }
    @Override
    public int compareTo(Element val, Ring ring) {
        return compareTo(val);
    }

    @Override
    public int compareTo(Element val) {
        if  (val.numbElementType()==Ring.Zp) return compareTo((NumberZ) val);
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
        if (val.numbElementType() > Ring.Z + Ring.TropNumber) {
            return 1;
        }
        if (val.numbElementType() < Ring.Z) {
            return -1;
        }
        return compareTo((NumberZ) val);
    }

    @Override
    public Boolean isNegative() {
        return (signum() < 0) ? true : false;
    }

    @Override
    public Boolean isMinusOne(Ring ring) {
        return (equals(MINUS_ONE, ring)) ? true : false;
    }

    @Override
    public Boolean isOne(Ring ring) {
        return (equals(ONE, ring)) ? true : false;
    }


    /*
     * Returns -1, 0 or +1 as big-endian unsigned int array arg1 is
     * less than, equal to, or greater than arg2.
     */
    public static int intArrayCmp(int[] arg1, int[] arg2) {
        if (arg1.length < arg2.length) {
            return -1;
        }
        if (arg1.length > arg2.length) {
            return 1;
        }

        // Argument lengths are equal; compare the values
        for (int i = 0; i < arg1.length; i++) {
            long b1 = arg1[i] & LONG_MASK;
            long b2 = arg2[i] & LONG_MASK;
            if (b1 < b2) {
                return -1;
            }
            if (b1 > b2) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public Boolean isZero(Ring r) {
        return (signum() == 0);  //equals(NumberZ.ZERO);
    }

    @Override
    public boolean isNaN() {
        return this == NAN;
    }

    @Override
    public boolean isPositiveInfinity() {
        return this == POSITIVE_INFINITY;
    }

    /**
     * Compares this BigInteger with the specified Object for equality.
     *
     * @param x Object to which this BigInteger is to be compared.
     * @param r -- Ring
     *
     * @return <tt>true</tt> if and only if the specified Object is a BigInteger
     * whose value is numerically equal to this BigInteger.
     */
    @Override
    public boolean equals(Element x, Ring r) {
        // This test is just an optimization, which may or may not help
        if (x == NAN || this == NAN) {return true;   }
        if (x == POSITIVE_INFINITY) { return this == POSITIVE_INFINITY;}
        if (this == POSITIVE_INFINITY) { return false;}
        if (x == NEGATIVE_INFINITY) { return this == NEGATIVE_INFINITY;        }
        if (this == NEGATIVE_INFINITY) { return false;        }
        if (x == this) { return true; }
        int t = x.numbElementType();
        return ((x instanceof Fraction)||(t > Ring.Zp || t < Ring.Z)) ? false : equals((NumberZ) x);
    }

    public boolean equals(NumberZ xInt) {
        if (xInt.signum != signum || xInt.mag.length != mag.length) {
            return false;
        }
        for (int i = 0; i < mag.length; i++) {
            if (xInt.mag[i] != mag[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NumberZ) {
            return equals((NumberZ) obj);
        }
        return false;
    }

    /**
     * Returns the minimum of this BigInteger and <tt>val</tt>.
     *
     * @param val value with which the minimum is to be computed.
     *
     * @return the BigInteger whose value is the lesser of this BigInteger and
     * <tt>val</tt>. If they are equal, either may be returned.
     */
    public NumberZ min(NumberZ val) {
        return (compareTo(val) < 0 ? this : val);
    }

    /**
     * Returns the maximum of this BigInteger and <tt>val</tt>.
     *
     * @param val value with which the maximum is to be computed.
     *
     * @return the BigInteger whose value is the greater of this and
     * <tt>val</tt>. If they are equal, either may be returned.
     */
    public NumberZ max(NumberZ val) {
        return (compareTo(val) > 0 ? this : val);
    }



    // Hash Function
    /**
     * Returns the hash code for this BigInteger.
     *
     * @return hash code for this BigInteger.
     */
    public int hashCode() {
        int hashCode = 0;

        for (int i = 0; i < mag.length; i++) {
            hashCode = (int) (31 * hashCode + (mag[i] & LONG_MASK));
        }

        return hashCode * signum;
    }

    /**
     * Returns the String representation of this BigInteger in the given radix.
     * If the radix is outside the range from {@link
     * Character#MIN_RADIX} to {@link Character#MAX_RADIX} inclusive, it will
     * default to 10 (as is the case for <tt>Integer.toString</tt>). The
     * digit-to-character mapping provided by <tt>Character.forDigit</tt> is
     * used, and a minus sign is prepended if appropriate. (This representation
     * is compatible with the {@link #BigInteger(String, int) (String,
     * <code>int</code>)} constructor.)
     *
     * @param radix radix of the String representation.
     *
     * @return String representation of this BigInteger in the given radix.
     *
     * @see Integer#toString
     * @see Character#forDigit
     */
    @Override
    public String toString(int radix, Ring ring) {
//        if (signum > NUMBER_SCALE) {
//            String result = "";
//            switch (signum) {
//                case Integer.MAX_VALUE:
//                    result = "NAN";
//                    break;
//                case Integer.MAX_VALUE - 1:
//                    result = "\\infinity";
//                    break;
//                case Integer.MAX_VALUE - 2:
//                    result = "-\\infinity";
//                    break;
//                case Integer.MAX_VALUE - 3:
//                    result = "(+0)";
//                    break;
//                case Integer.MAX_VALUE - 4:
//                    result = "(-0)";
//                    break;
//            }
//            return result;
//        }
        if (signum == 0) {
            return "0";
        }
        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
            radix = 10;
        }

        // Compute upper bound on number of digit groups and allocate space
        int maxNumDigitGroups = (4 * mag.length + 6) / 7;
        String digitGroup[] = new String[maxNumDigitGroups];

        // Translate number to string, a digit group at a time
        NumberZ tmp = this.abs();
        int numGroups = 0;
        while (tmp.signum != 0) {
            NumberZ d = longRadix[radix];

            MutableBigInteger q = new MutableBigInteger(),
                    r = new MutableBigInteger(),
                    a = new MutableBigInteger(tmp.mag),
                    b = new MutableBigInteger(d.mag);
            a.divide(b, q, r);
            NumberZ q2 = new NumberZ(q, tmp.signum * d.signum);
            NumberZ r2 = new NumberZ(r, tmp.signum * d.signum);

            digitGroup[numGroups++] = java.lang.Long.toString(r2.longValue(),
                    radix);
            tmp = q2;
        }

        // Put sign (if any) and first digit group into result buffer
        StringBuilder buf = new StringBuilder(numGroups * digitsPerLong[radix]
                + 1);
        if (signum < 0) {
            buf.append('-');
        }
        buf.append(digitGroup[numGroups - 1]);

        // Append remaining digit groups padded with leading zeros
        for (int i = numGroups - 2; i >= 0; i--) {
            // Prepend (any) leading zeros for this digit group
            int numLeadingZeros = digitsPerLong[radix] - digitGroup[i].length();
            if (numLeadingZeros != 0) {
                buf.append(zeros[numLeadingZeros]);
            }
            buf.append(digitGroup[i]);
        }
        return buf.toString();
    }

    /* zero[i] is a string of i consecutive zeros. */
    private static String zeros[] = new String[64];

    static {
        zeros[63] =
                "000000000000000000000000000000000000000000000000000000000000000";
        for (int i = 0; i < 63; i++) {
            zeros[i] = zeros[63].substring(0, i);
        }
    }

    /**
     * Returns the decimal String representation of this BigInteger. The
     * digit-to-character mapping provided by <tt>Character.forDigit</tt> is
     * used, and a minus sign is prepended if appropriate. (This representation
     * is compatible with the {@link #BigInteger(String) (String)} constructor,
     * and allows for String concatenation with Java's + operator.)
     *
     * @return decimal String representation of this BigInteger.
     *
     * @see Character#forDigit
     * @see #BigInteger(java.lang.String)
     */
    @Override
    public String toString() {
        return toString(10, null);
    }

    @Override
    public String toString(Ring r) {
        String s = toString();
        return s;
    }

    /**
     * Returns a byte array containing the two's-complement representation of
     * this BigInteger. The byte array will be in <i>big-endian</i> byte-order:
     * the most significant byte is in the zeroth element. The array will
     * contain the minimum number of bytes required to represent this
     * BigInteger, including at least one sign bit, which is
     * <tt>(ceil((this.bitLength() + 1)/8))</tt>. (This representation is
     * compatible with the {@link #BigInteger(byte[]) (byte[])} constructor.)
     *
     * @return a byte array containing the two's-complement representation of
     * this BigInteger.
     *
     * @see #BigInteger(byte[])
     */
    public byte[] toByteArray() {
        int byteLen = bitLength() / 8 + 1;
        byte[] byteArray = new byte[byteLen];

        for (int i = byteLen - 1, bytesCopied = 4, nextInt = 0, intIndex = 0;
                i >= 0; i--) {
            if (bytesCopied == 4) {
                nextInt = getInt(intIndex++);
                bytesCopied = 1;
            } else {
                nextInt >>>= 8;
                bytesCopied++;
            }
            byteArray[i] = (byte) nextInt;
        }
        return byteArray;
    }

    /**
     * Converts this BigInteger to an
     * <code>int</code>. This conversion is analogous to a <a
     * href="http://java.sun.com/docs/books/jls/second_edition/html/conversions.doc.html#25363"><i>narrowing
     * primitive conversion</i></a> from
     * <code>long</code> to
     * <code>int</code> as defined in the <a
     * href="http://java.sun.com/docs/books/jls/html/">Java Language
     * Specification</a>: if this BigInteger is too big to fit in an
     * <code>int</code>, only the low-order 32 bits are returned. Note that this
     * conversion can lose information about the overall magnitude of the
     * BigInteger value as well as return a result with the opposite sign.
     *
     * @return this BigInteger converted to an <code>int</code>.
     */
    @Override
    public int intValue() {
        int result = 0;
        result = getInt(0);
        return result;
    }
//    public int intValue() {
//        int result = 0; result = getInt(0); return result;
//    }

    /**
     * Converts this BigInteger to a
     * <code>long</code>. This conversion is analogous to a <a
     * href="http://java.sun.com/docs/books/jls/second_edition/html/conversions.doc.html#25363"><i>narrowing
     * primitive conversion</i></a> from
     * <code>long</code> to
     * <code>int</code> as defined in the <a
     * href="http://java.sun.com/docs/books/jls/html/">Java Language
     * Specification</a>: if this BigInteger is too big to fit in a
     * <code>long</code>, only the low-order 64 bits are returned. Note that
     * this conversion can lose information about the overall magnitude of the
     * BigInteger value as well as return a result with the opposite sign.
     *
     * @return this BigInteger converted to a <code>long</code>.
     */
    @Override
    public long longValue() {
        long result = 0;
        for (int i = 1; i >= 0; i--) {
            result = (result << 32) + (getInt(i) & LONG_MASK);
        }
        return result;
    }
//    @Override
//    public long longValue(Ring ring) {
//        long result = 0;
//        for (int i = 1; i >= 0; i--) {
//            result = (result << 32) + (getInt(i) & LONG_MASK); }
//        return result;
//    }

    /**
     * Converts this BigInteger to a
     * <code>float</code>. This conversion is similar to the <a
     * href="http://java.sun.com/docs/books/jls/second_edition/html/conversions.doc.html#25363"><i>narrowing
     * primitive conversion</i></a> from
     * <code>double</code> to
     * <code>float</code> defined in the <a
     * href="http://java.sun.com/docs/books/jls/html/">Java Language
     * Specification</a>: if this BigInteger has too great a magnitude to
     * represent as a
     * <code>float</code>, it will be converted to
     * {@link Float#NEGATIVE_INFINITY} or {@link
     * Float#POSITIVE_INFINITY} as appropriate. Note that even when the return
     * value is finite, this conversion can lose information about the precision
     * of the BigInteger value.
     *
     * @return this BigInteger converted to a <code>float</code>.
     */
    public float floatValue() {
        // Somewhat inefficient, but guaranteed to work.
        return Float.valueOf(this.toString()).floatValue();
    }

    /**
     * Converts this BigInteger to a
     * <code>double</code>. This conversion is similar to the <a
     * href="http://java.sun.com/docs/books/jls/second_edition/html/conversions.doc.html#25363"><i>narrowing
     * primitive conversion</i></a> from
     * <code>double</code> to
     * <code>float</code> defined in the <a
     * href="http://java.sun.com/docs/books/jls/html/">Java Language
     * Specification</a>: if this BigInteger has too great a magnitude to
     * represent as a
     * <code>double</code>, it will be converted to
     * {@link Double#NEGATIVE_INFINITY} or {@link
     * Double#POSITIVE_INFINITY} as appropriate. Note that even when the return
     * value is finite, this conversion can lose information about the precision
     * of the BigInteger value.
     *
     * @return this BigInteger converted to a <code>double</code>.
     */
    public double doubleValue() {
        // Somewhat inefficient, but guaranteed to work.
        return java.lang.Double.valueOf(this.toString()).doubleValue();
    }

    /**
     * Returns a copy of the input array stripped of any leading zero bytes.
     */
    private static int[] stripLeadingZeroInts(int val[]) {
        int byteLength = val.length;
        int keep;

        // Find first nonzero byte
        for (keep = 0; keep < val.length && val[keep] == 0; keep++) {
            ;
        }

        int result[] = new int[val.length - keep];
        for (int i = 0; i < val.length - keep; i++) {
            result[i] = val[keep + i];
        }

        return result;
    }

    /**
     * Returns the input array stripped of any leading zero bytes. Since the
     * source is trusted the copying may be skipped.
     */
    private static int[] trustedStripLeadingZeroInts(int val[]) {
        int byteLength = val.length;
        int keep;

        // Find first nonzero byte
        for (keep = 0; keep < val.length && val[keep] == 0; keep++) {
            ;
        }

        // Only perform copy if necessary
        if (keep > 0) {
            int result[] = new int[val.length - keep];
            for (int i = 0; i < val.length - keep; i++) {
                result[i] = val[keep + i];
            }
            return result;
        }
        return val;
    }

    /**
     * Returns a copy of the input array stripped of any leading zero bytes.
     */
    private static int[] stripLeadingZeroBytes(byte a[]) {
        int byteLength = a.length;
        int keep;

        // Find first nonzero byte
        for (keep = 0; keep < a.length && a[keep] == 0; keep++) {
            ;
        }

        // Allocate new array and copy relevant part of input array
        int intLength = ((byteLength - keep) + 3) / 4;
        int[] result = new int[intLength];
        int b = byteLength - 1;
        for (int i = intLength - 1; i >= 0; i--) {
            result[i] = a[b--] & 0xff;
            int bytesRemaining = b - keep + 1;
            int bytesToTransfer = Math.min(3, bytesRemaining);
            for (int j = 8; j <= 8 * bytesToTransfer; j += 8) {
                result[i] |= ((a[b--] & 0xff) << j);
            }
        }
        return result;
    }

    /**
     * Takes an array a representing a negative 2's-complement number and
     * returns the minimal (no leading zero bytes) unsigned whose value is -a.
     */
    private static int[] makePositive(byte a[]) {
        int keep, k;
        int byteLength = a.length;

        // Find first non-sign (0xff) byte of input
        for (keep = 0; keep < byteLength && a[keep] == -1; keep++) {
            ;
        }

        /* Allocate output array.  If all non-sign bytes are 0x00, we must
         * allocate space for one extra output byte. */
        for (k = keep; k < byteLength && a[k] == 0; k++) {
            ;
        }

        int extraByte = (k == byteLength) ? 1 : 0;
        int intLength = ((byteLength - keep + extraByte) + 3) / 4;
        int result[] = new int[intLength];

        /* Copy one's complement of input into output, leaving extra
         * byte (if it exists) == 0x00 */
        int b = byteLength - 1;
        for (int i = intLength - 1; i >= 0; i--) {
            result[i] = a[b--] & 0xff;
            int numBytesToTransfer = Math.min(3, b - keep + 1);
            if (numBytesToTransfer < 0) {
                numBytesToTransfer = 0;
            }
            for (int j = 8; j <= 8 * numBytesToTransfer; j += 8) {
                result[i] |= ((a[b--] & 0xff) << j);
            }

            // Mask indicates which bits must be complemented
            int mask = -1 >>> (8 * (3 - numBytesToTransfer));
            result[i] = ~result[i] & mask;
        }

        // Add one to one's complement to generate two's complement
        for (int i = result.length - 1; i >= 0; i--) {
            result[i] = (int) ((result[i] & LONG_MASK) + 1);
            if (result[i] != 0) {
                break;
            }
        }

        return result;
    }

    /**
     * Takes an array a representing a negative 2's-complement number and
     * returns the minimal (no leading zero ints) unsigned whose value is -a.
     */
    private static int[] makePositive(int a[]) {
        int keep, j;

        // Find first non-sign (0xffffffff) int of input
        for (keep = 0; keep < a.length && a[keep] == -1; keep++) {
            ;
        }

        /* Allocate output array.  If all non-sign ints are 0x00, we must
         * allocate space for one extra output int. */
        for (j = keep; j < a.length && a[j] == 0; j++) {
            ;
        }
        int extraInt = (j == a.length ? 1 : 0);
        int result[] = new int[a.length - keep + extraInt];

        /* Copy one's complement of input into output, leaving extra
         * int (if it exists) == 0x00 */
        for (int i = keep; i < a.length; i++) {
            result[i - keep + extraInt] = ~a[i];
        }

        // Add one to one's complement to generate two's complement
        for (int i = result.length - 1; ++result[i] == 0; i--) {
            ;
        }

        return result;
    }

    /*
     * The following two arrays are used for fast String conversions.  Both
     * are indexed by radix.  The first is the number of digits of the given
     * radix that can fit in a Java long without "going negative", i.e., the
     * highest integer n such that radix**n < 2**63.  The second is the
     * "long radix" that tears each number into "long digits", each of which
     * consists of the number of digits in the corresponding element in
     * digitsPerLong (longRadix[i] = i**digitPerLong[i]).  Both arrays have
     * nonsense values in their 0 and 1 elements, as radixes 0 and 1 are not
     * used.
     */
    private static int digitsPerLong[] = {
        0, 0,
        62, 39, 31, 27, 24, 22, 20, 19, 18, 18,
        17, 17, 16, 16, 15, 15, 15, 14,
        14, 14, 14, 13, 13, 13, 13, 13, 13, 12,
        12, 12, 12, 12, 12, 12, 12};
    private static NumberZ longRadix[] = {
        null, null,
        valueOf(0x4000000000000000L),
        valueOf(0x383d9170b85ff80bL),
        valueOf(0x4000000000000000L),
        valueOf(0x6765c793fa10079dL),
        valueOf(0x41c21cb8e1000000L),
        valueOf(0x3642798750226111L),
        valueOf(0x1000000000000000L),
        valueOf(0x12bf307ae81ffd59L),
        valueOf(0xde0b6b3a7640000L),
        valueOf(0x4d28cb56c33fa539L),
        valueOf(0x1eca170c00000000L),
        valueOf(0x780c7372621bd74dL),
        valueOf(0x1e39a5057d810000L),
        valueOf(0x5b27ac993df97701L),
        valueOf(0x1000000000000000L),
        valueOf(0x27b95e997e21d9f1L),
        valueOf(0x5da0e1e53c5c8000L),
        valueOf(0xb16a458ef403f19L),
        valueOf(0x16bcc41e90000000L),
        valueOf(0x2d04b7fdd9c0ef49L),
        valueOf(0x5658597bcaa24000L),
        valueOf(0x6feb266931a75b7L),
        valueOf(0xc29e98000000000L),
        valueOf(0x14adf4b7320334b9L),
        valueOf(0x226ed36478bfa000L),
        valueOf(0x383d9170b85ff80bL),
        valueOf(0x5a3c23e39c000000L),
        valueOf(0x4e900abb53e6b71L),
        valueOf(0x7600ec618141000L),
        valueOf(0xaee5720ee830681L),
        valueOf(0x1000000000000000L),
        valueOf(0x172588ad4f5f0981L),
        valueOf(0x211e44f7d02c1000L),
        valueOf(0x2ee56725f06e5c71L),
        valueOf(0x41c21cb8e1000000L)};

    /*
     * These two arrays are the integer analogue of above.
     */
    private static int digitsPerInt[] = {
        0, 0, 30, 19, 15, 13, 11,
        11, 10, 9, 9, 8, 8, 8, 8, 7, 7, 7, 7, 7,
        7, 7, 6, 6, 6, 6,
        6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 5};
    private static int intRadix[] = {
        0, 0,
        0x40000000, 0x4546b3db, 0x40000000,
        0x48c27395, 0x159fd800,
        0x75db9c97, 0x40000000, 0x17179149,
        0x3b9aca00, 0xcc6db61,
        0x19a10000, 0x309f1021, 0x57f6c100,
        0xa2f1b6f, 0x10000000,
        0x18754571, 0x247dbc80, 0x3547667b,
        0x4c4b4000, 0x6b5a6e1d,
        0x6c20a40, 0x8d2d931, 0xb640000, 0xe8d4a51,
        0x1269ae40,
        0x17179149, 0x1cb91000, 0x23744899,
        0x2b73a840, 0x34e63b41,
        0x40000000, 0x4cfa3cc1, 0x5c13d840,
        0x6d91b519, 0x39aa400
    };

    /**
     * These routines provide access to the two's complement representation of
     * BigIntegers.
     */
    /**
     * Returns the length of the two's complement representation in ints,
     * including space for at least one sign bit.
     */
    private int intLength() {
        return bitLength() / 32 + 1;
    }

    /* Returns sign bit */
    private int signBit() {
        return (signum < 0 ? 1 : 0);
    }

    /* Returns an int of sign bits */
    private int signInt() {
        return signum < 0 ? -1 : 0;
    }

    /**
     * Returns the specified int of the little-endian two's complement
     * representation (int 0 is the least significant). The int number can be
     * arbitrarily high (values are logically preceded by infinitely many sign
     * ints).
     */
    private int getInt(int n) {
        if (n < 0) {
            return 0;
        }
        if (n >= mag.length) {
            return signInt();
        }

        int magInt = mag[mag.length - n - 1];

        return signum >= 0 ? magInt : (n <= firstNonzeroIntNum()
                ? -magInt : ~magInt);
    }

    /**
     * Returns the index of the int that contains the first nonzero int in the
     * little-endian binary representation of the magnitude (int 0 is the least
     * significant). If the magnitude is zero, return value is undefined.
     */
    private int firstNonzeroIntNum() {
        /*
         * Initialize firstNonzeroIntNum field the first time this method is
         * executed. This method depends on the atomicity of int modifies;
         * without this guarantee, it would have to be synchronized.
         */
        if (firstNonzeroIntNum == -2) {
            // Search for the first nonzero int
            int i;
            for (i = mag.length - 1; i >= 0 && mag[i] == 0; i--) {
                ;
            }
            firstNonzeroIntNum = mag.length - i - 1;
        }
        return firstNonzeroIntNum;
    }
    /**
     * use serialVersionUID from JDK 1.1. for interoperability
     */
    private static final long serialVersionUID = -8287574255936472291L;
    /**
     * Serializable fields for BigInteger.
     *
     * @serialField signum int signum of this BigInteger.
     * @serialField magnitude int[] magnitude array of this BigInteger.
     * @serialField bitCount int number of bits in this BigInteger
     * @serialField bitLength int the number of bits in the minimal
     * two's-complement representation of this BigInteger
     * @serialField lowestSetBit int lowest set bit in the twos complement
     * representation
     */
    private static final ObjectStreamField[] serialPersistentFields = {
        new ObjectStreamField("signum", Integer.TYPE),
        new ObjectStreamField("magnitude", byte[].class),
        new ObjectStreamField("bitCount", Integer.TYPE),
        new ObjectStreamField("bitLength", Integer.TYPE),
        new ObjectStreamField("firstNonzeroByteNum", Integer.TYPE),
        new ObjectStreamField("lowestSetBit", Integer.TYPE)
    };

    /**
     * Reconstitute the <tt>BigInteger</tt> instance from a stream (that is,
     * deserialize it). The magnitude is read in as an array of bytes for
     * historical reasons, but it is converted to an array of ints and the byte
     * array is discarded.
     */
    private void readObject(java.io.ObjectInputStream s) throws
            java.io.IOException, ClassNotFoundException {
        /*
         * In order to maintain compatibility with previous serialized forms,
         * the magnitude of a BigInteger is serialized as an array of bytes.
         * The magnitude field is used as a temporary store for the byte array
         * that is deserialized. The cached computation fields should be
         * transient but are serialized for compatibility reasons.
         */

        // prepare to read the alternate persistent fields
        ObjectInputStream.GetField fields = s.readFields();

        // Read the alternate persistent fields that we care about
        signum = fields.get("signum", -2);
        byte[] magnitude = (byte[]) fields.get("magnitude", null);

        // Validate signum
        if (signum < -1 || signum > 1) {
            String message = "BigInteger: Invalid signum value";
            if (fields.defaulted("signum")) {
                message = "BigInteger: Signum not present in stream";
            }
            throw new java.io.StreamCorruptedException(message);
        }
        if ((magnitude.length == 0) != (signum == 0)) {
            String message = "BigInteger: signum-magnitude mismatch";
            if (fields.defaulted("magnitude")) {
                message = "BigInteger: Magnitude not present in stream";
            }
            throw new java.io.StreamCorruptedException(message);
        }

        // Set "cached computation" fields to their initial values
        bitCount = bitLength = -1;
        lowestSetBit = firstNonzeroByteNum = firstNonzeroIntNum = -2;

        // Calculate mag field from magnitude and discard magnitude
        mag = stripLeadingZeroBytes(magnitude);
    }

    /**
     * Save the <tt>BigInteger</tt> instance to a stream. The magnitude of a
     * BigInteger is serialized as a byte array for historical reasons.
     *
     * @serialData two necessary fields are written as well as obsolete fields
     * for compatibility with older versions.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        // set the values of the Serializable fields
        ObjectOutputStream.PutField fields = s.putFields();
        fields.put("signum", signum);
        fields.put("magnitude", magSerializedForm());
        fields.put("bitCount", -1);
        fields.put("bitLength", -1);
        fields.put("lowestSetBit", -2);
        fields.put("firstNonzeroByteNum", -2);

        // save them
        s.writeFields();
    }

    /**
     * Returns the mag array as an array of bytes.
     */
    private byte[] magSerializedForm() {
        int bitLen = (mag.length == 0 ? 0 : ((mag.length - 1) << 5) + bitLen(
                mag[0]));
        int byteLen = (bitLen + 7) / 8;
        byte[] result = new byte[byteLen];

        for (int i = byteLen - 1, bytesCopied = 4, intIndex = mag.length - 1,
                nextInt = 0;
                i >= 0; i--) {
            if (bytesCopied == 4) {
                nextInt = mag[intIndex--];
                bytesCopied = 1;
            } else {
                nextInt >>>= 8;
                bytesCopied++;
            }
            result[i] = (byte) nextInt;
        }
        return result;
    }

//==============================================================================
    public NumberZ multiplyDih(NumberZ val, Ring ring) {
        NumberZkaratcuba f = new NumberZkaratcuba();
        if (this.mag.length >= val.mag.length) {
            return f.multiplyK5(this, val);
        } else {
            return f.multiplyK5(val, this);
        }
    }

    public NumberZ multiplyDih(NumberZ val, int quantSize, Ring ring) {
        NumberZkaratcuba f = new NumberZkaratcuba(quantSize, 1, 1, 1);
        return f.multiplyK5(this, val);
    }
    public static final NumberZ MINUS_ONE; // = ONE.negate();
    static{MINUS_ONE = ONE.negate(); }

    @Override
    public Element myOne(Ring ring) {
        return ONE;
    }

    @Override
    public Element myMinus_one() {
        return MINUS_ONE;
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
    public Element one(Ring ring) {
        return ONE;
    }

    @Override
    public Element minus_one(Ring ring) {
        return MINUS_ONE;
    }

    @Override
    public Element zero(Ring ring) {
        return ZERO;
    }

    public NumberR RValue() {
        return new NumberR(this);
    }

    public NumberZ ZValue() {
        return this;
    }

    @Override
    public VectorS extendedGCD(Element x, Ring ring) { 
        if (x instanceof NumberZ)
              return new VectorS(extendedGCD((NumberZ) x));
        else if (x instanceof NumberZ64)
            return new VectorS(extendedGCD(((NumberZ)x.toNewRing(Ring.Z, ring))));          
        else {ring.exception.append("Not correct first argument in extended GCD");  return null;}             
    }

    public NumberZ GCD(NumberZ x) {
        return gcd(x);
    }

    @Override
    public Element GCD(Element x, Ring ring) {
        int type = x.numbElementType();
        if (type > Ring.Z) {
            if (x instanceof Complex | x instanceof Polynom) {
                return x.GCD(this, ring);
            }
            if ((x instanceof Fraction)){
                x=((Fraction)x).cancel(ring);if (x instanceof Fraction) x=x.num(ring);
                return GCD(x,ring);   }
            Element z = x.ExpandFnameOrId();
            if ((z instanceof F) && (((F) z).name == F.MULTIPLY)) {
               Element[] XX = ((F) z).X;
               for (int i = 0; i < XX.length; i++) {
                  if (XX[i] instanceof NumberZ){return gcd((NumberZ) XX[i]);}}
            }
            return NumberZ.ONE;
        }
        NumberZ y = (type < Ring.Z) ? (NumberZ) x.toNumber(Ring.Z, ring)
                    : (NumberZ) x;
        return gcd(y);
    }

    public NumberZ LCM(NumberZ x) {
        return multiply(x).divide(GCD(x));
    }

    @Override
    public Element LCM(Element x, Ring ring) {
        if (!(x instanceof NumberZ)) {
            return  (x.isItNumber(ring))?  
                    LCM((NumberZ) x.toNumber(Ring.Z, ring)):
                    x.LCM(this, ring);
        } else {
            return LCM((NumberZ) (x));
        }
    }

    public NumberZ abs(Element x, Ring ring) {
        return ((NumberZ) x).abs();
    }

    //===========================================================
    //============ Арифметика с любыми элементами ===============
    @Override
    public Element add(Element x, Ring ring) {
        if ((x == NAN) ||  (x == NEGATIVE_INFINITY) || (x == POSITIVE_INFINITY)) { return x;  }
        if (x instanceof NumberZ) { return add((NumberZ) x); }
        int X_Type = x.numbElementType();
        if (X_Type > Ring.Z) {return x.add(this, ring);}
        return add((NumberZ) (x.toNumber(Ring.Z, ring)));
    }
    @Override
    public Element subtract(Element x, Ring ring) {
        if ((x == NAN) ||  (x == NEGATIVE_INFINITY) || (x == POSITIVE_INFINITY)) { return x;  }
        if (x instanceof NumberZ) { return subtract((NumberZ) x); }
        int X_Type = x.numbElementType();
        if (X_Type > Ring.Z) {return x.negate(ring).add(this, ring);}
        return subtract((NumberZ) (x.toNumber(Ring.Z, ring)));
    }
    @Override
    public Element multiply(Element x, Ring ring) {
        if ((x == NAN) ||  (x == NEGATIVE_INFINITY) || (x == POSITIVE_INFINITY)) { return x;  }
        if (x instanceof NumberZ) { return multiply((NumberZ) x); }
        int X_Type = x.numbElementType();
        if (X_Type > Ring.Z) {return x.multiply(this, ring);}
        return multiply((NumberZ) (x.toNumber(Ring.Z, ring)));
    }
  //  @Override
    public Element divide000(Element x, Ring ring) {
        if ((x == NAN) ||  (x == NEGATIVE_INFINITY) || (x == POSITIVE_INFINITY)) { return x;  }
        if (x.isOne(ring)) return this;
        if (x.isMinusOne(ring)) return negate(ring);
        if(signum==0){return (x.isZero(ring))? NAN: ZERO; }
        if (x instanceof NumberZ) { return divide((NumberZ) x); }
        int X_Type = x.numbElementType();
        if (X_Type > Ring.Z) {return   x.inverse(ring).multiply(this, ring); }
        return divide( (NumberZ)(x.toNumber(Ring.Z, ring)) );
    }

    @Override
    public Element divide(Element x, Ring ring) {
        if (x == NAN) return NAN;
        if (x == NEGATIVE_INFINITY || x == POSITIVE_INFINITY) return ZERO; //
        if (x.isOne(ring)) return this;
        if (x.isMinusOne(ring)) return negate(ring);
        if(signum==0){return (x.isZero(ring))? NAN: ZERO; }
        if (x instanceof NumberZ) {
          return (ring.algebra[0] == Ring.Z || ring.algebra[0] == Ring.CZ)
            ? divide((NumberZ) x)
            : divideToFraction((NumberZ) x, ring);
        }
        if (x instanceof Fraction) return ((Fraction) x).inverse(ring).multiply(this, ring);
 //       if (x.numbElementType() <= Ring.Polynom) 
 //               return  (this.toNumber(x.numbElementType(), ring)).divide(x, ring);
        return (x.numbElementType() < Ring.Z)
                ? divide( x.toNumber(Ring.Z, ring),ring)
                :(x.numbElementType() < Ring.Polynom)
                 ? this.toNumber(x.numbElementType(), ring).divide(x, ring)
                 : new Fraction(this, x);
    }
//================================================================
//================================================================

    @Override
    public Element multiply(Element x, int var, Ring ring) {
        switch (var) {
            case 1:
                return multiplyDih((NumberZ) x, ring);
            default:
                return multiply((NumberZ) x, ring);
        }
    }

    public Element modInverse(Element m) {
        if ((m == NEGATIVE_INFINITY) || (m == POSITIVE_INFINITY)) { return NAN; }
        return modInverse((NumberZ) m);
    }

    public NumberZ modPow(Element exp, Element m) { return null;}

    @Override
    public Element valOf(double x, Ring ring) {
        Element pp = NumberR.valueOf(x);
        if (pp instanceof NumberR) { return ((NumberR) pp).NumberRtoNumberZ();}
        else {return pp;}
    }

    @Override
    public NumberZ valOf(long x, Ring ring) { return valueOf(x); }

    @Override
    public NumberZ valOf(String s, Ring ring) { return new NumberZ(s); }

    /**
     * Transform this number of NumberZ type to the new type fixed by
     * "numberType" defined by Ring.type
     *
     * @param numberType - new type
     *
     * @return this transormed to the new type
     */
    @Override
    public Element toNumber(int numberType, Ring ring) {int sign=signum;
        NumberZ A=new NumberZ(sign,mag.clone());
        switch (numberType) {
            case Ring.Z:
                return A;
            case Ring.Z64:
                return new NumberZ64(longValue());
            case Ring.Zp32:
                NumberZ x = A.remainder(new NumberZ(ring.MOD32));
                return new NumberZp32(x, ring);
            case Ring.Zp: {
                NumberZ zz = A.remainder(ring.MOD);
                return new NumberZp(zz);
            }
            case Ring.R:
                return new NumberR(A);
            case Ring.R64:
                return new NumberR64(doubleValue());
            case Ring.R128:
                return new NumberR128(A);
            case Ring.Q: case Ring.CQ:
                return (new Fraction(A, NumberZ.ONE));
            case Ring.CZ:
                return new Complex(A, NumberZ.ZERO);
            case Ring.CZp32:
                return new Complex(new NumberZp32(longValue(), ring),
                        NumberZp32.ZERO);
            case Ring.CZp:
                return new Complex(new NumberZp(A, ring), NumberZp.ZERO);
            case Ring.C:
                return new Complex(new NumberR(A), NumberR.ZERO);
            case Ring.C64:
                return new Complex(new NumberR64(doubleValue()), NumberR64.ZERO);
            case Ring.C128:
                return new Complex(new NumberR128(A), NumberR128.ZERO);
            case Ring.ZMaxPlus:
                return new NumberZMaxPlus(this);
            case Ring.ZMinPlus:
                return new NumberZMinPlus(this);
            case Ring.ZMaxMult:
                return new NumberZMaxMult(A);
            case Ring.ZMinMult:
                return new NumberZMinMult(A);
            case Ring.ZMaxMin:
                return new NumberZMaxMin(A);
            case Ring.ZMinMax:
                return new NumberZMinMax(A);
            case Ring.R64MaxPlus:
                return new NumberR64MaxPlus(new NumberR64(doubleValue()));
            case Ring.R64MinPlus:
                return new NumberR64MinPlus(new NumberR64(doubleValue()));
            case Ring.R64MaxMult:
                return new NumberR64MaxMult(new NumberR64(doubleValue()));
            case Ring.R64MinMult:
                return new NumberR64MinMult(new NumberR64(doubleValue()));
            case Ring.R64MaxMin:
                return new NumberR64MaxMin(new NumberR64(doubleValue()));
            case Ring.R64MinMax:
                return new NumberR64MinMax(new NumberR64(doubleValue()));
            case Ring.RMaxPlus:
                return new NumberRMaxPlus(new NumberR(A));
            case Ring.RMinPlus:
                return new NumberRMinPlus(new NumberR(A));
            case Ring.RMaxMult:
                return new NumberRMaxMult(new NumberR(A));
            case Ring.RMinMult:
                return new NumberRMinMult(new NumberR(A));
            case Ring.RMaxMin:
                return new NumberRMaxMin(new NumberR(A));
            case Ring.RMinMax:
                return new NumberRMinMax(new NumberR(A));
        }
        return A;
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
    public int numbElementType() {
        return com.mathpar.number.Ring.Z;
    }

    /**
     * Конструктор случайного целого числа с числом бит равным предпоследнему
     * элементу в массиве randomType. Обращается к стандартному конструктору
     * класса BigInteger. Параметр itsCoeffOne не используется.
     *
     * @return случайное целое число
     */
    /**
     * Constructs a randomly generated non-negative BigInteger, uniformly
     * distributed over the range <tt>0</tt> to
     * <tt>(2<sup>numBits=randomType[length-2]</sup> - 1)</tt>, inclusive. The
     * uniformity of the distribution assumes that a fair source of random bits
     * is provided in <tt>rnd</tt>. Конструктор случайного целого числа с числом
     * бит равным предпоследнему элементу в массиве randomType. Обращается к
     * стандартному конструктору класса BigInteger. Параметр itsCoeffOne не
     * используется.
     *
     * @param randomType
     * @param itsCoeffOne не используется
     * @param ring
     * @param numBits maximum bitLength of the new BigInteger.
     * @param rnd source of randomness to be used in computing the new
     * BigInteger.
     *
     * @throws IllegalArgumentException <tt>numBits</tt> is negative.
     * @return случайное целое число
     *
     * @see #bitLength
     */
    @Override
    public NumberZ random(int[] randomType, Random rnd, Ring ring) {
        int bits=randomType[randomType.length - 1]; int base=bits; NumberZ sd=null ;
        if(bits<0) { base=-bits; sd= ONE.shiftLeft(base-1);  }
        NumberZ w= new NumberZ(base, rnd);
        return (bits<0)? w.subtract(sd): w;
    }




    public int posOfHighestBit() {
        return (mag.length == 0) ? 0 : (((mag.length - 1) << 5) + 32 - Integer.numberOfLeadingZeros(
                mag[0]));
    }

    private static NumberZ TEN_POWERS_TABLE[] = {NumberZ.ONE,
        NumberZ.valueOf(10), NumberZ.valueOf(
        100),
        NumberZ.valueOf(1000), NumberZ.valueOf(
        10000),
        NumberZ.valueOf(100000), NumberZ.valueOf(
        1000000),
        NumberZ.valueOf(10000000), NumberZ.valueOf(
        100000000),
        NumberZ.valueOf(1000000000),
        NumberZ.valueOf(10000000000L),
        NumberZ.valueOf(100000000000L),
        NumberZ.valueOf(1000000000000L),
        NumberZ.valueOf(10000000000000L),
        NumberZ.valueOf(
        100000000000000L),
        NumberZ.valueOf(
        1000000000000000L),
        NumberZ.valueOf(
        10000000000000000L),
        NumberZ.valueOf(
        100000000000000000L),
        NumberZ.valueOf(
        1000000000000000000L)
    };

    /**
     * The NumberZ which equals 10^i.
     *
     * @param i -- the power of ten
     *
     * @return the numberZ equals 10^i
     */
    public static NumberZ tenInPowerOf(int i) {
        int last = TEN_POWERS_TABLE.length;
        if (i < last) {
            return TEN_POWERS_TABLE[i];
        }
        NumberZ bb = TEN_POWERS_TABLE[--last];
        bb = bb.pow(i / last);
        bb = bb.multiply(TEN_POWERS_TABLE[i % last]);
        return bb;
    }

    public static NumberZ pow_(NumberZ z, int num, int denom, Ring ring) {
        NumberR64 t = (NumberR64) z.toNumber(Ring.R64, ring);
        NumberR64 t1 = t.powTheFirst(num, denom, ring);
        return ((Math.round(t1.doubleValue()) - t1.doubleValue()) == 0) ? (NumberZ) t1.toNumber(
                Ring.Z, ring) : null;
    }

    @Override
    public Element sqrt(Ring ring) {Element res=null;
       NumberZ th=(this.isNegative())?negate():this;
       res = FuncNumberQZ.sqrt(th);
       if(res==null){
         Element DiskMul=NumberZ.ONE;Element DiskRad=NumberZ.ONE;
         FactorPol fp=(FactorPol)th.factor(ring);
         for (int i = 0; i < fp.powers.length; i++) {
                if(fp.powers[i]%2==1)
                    DiskRad=DiskRad.multiply((fp.multin[i].coeffs[0]),ring);
                DiskMul=DiskMul.multiply((fp.multin[i].coeffs[0]).pow(fp.powers[i]/2, ring),ring);
            }
         res=(DiskMul.isOne(ring))? new F(F.SQRT,DiskRad):
                 new F(F.MULTIPLY,DiskMul,new F(F.SQRT,DiskRad));
        }
        return (this.isNegative())?
               (ring.isComplex())? new Complex(ring.numberZERO,res): NAN
               : res;
    }

    @Override
    public Element toNewRing(int Algebra, Ring r) {
        return toNumber(Algebra, r);
    }

    @Override
    public Element pi(Ring r) {
        return new Fname("\\pi");
    }

    @Override
    public Element id(Ring ring) {
        return this;
    }

    @Override
    public Element ln(Ring R) {if(equals(ONE))return NumberZ.ZERO;
        if(R.isExactRing())  return new F(F.LN, this);
        return this.toNewRing(R.algebra[0], R).ln(R);
    }
    @Override
    public Element lg(Ring r) {if(equals(ONE))return NumberZ.ZERO;
        if(r.isExactRing()){   
           int pow=0; NumberZ[] divrem = this.divideAndRemainder(NumberZ.TEN);
           while(divrem[1].equals(ZERO)){ divrem = divrem[1].divideAndRemainder(NumberZ.TEN); pow++;}
           Element rez=new NumberZ(pow);
           return (divrem[1].equals(ONE))? rez: rez.multiply(new F(F.LG,divrem[1]), r);
    }return this.toNewRing(r.algebra[0], r).lg(r);
    }

    @Override
    public Element exp(Ring r) {
        double res = Math.exp(doubleValue());
        return (res - Math.round(res) == 0) ? new NumberZ(Math.round(res))
               : new NumberR64(res);
    }

    
    
    
    @Override
    public Element factorial(Ring r) {if(signum<0) return NAN;
        if (isZero(r) || isOne(r))  return ONE;
        return multiply(subtract(ONE, r).factorial(r), r);
    }
    @Override
    public Element sin(Ring ring) {
        if (ring.RADIAN.equals(Element.FALSE, ring)) {
            return new FuncNumberQZ(ring).trigFunc_Grad(F.SIN, this);
        } else { if (isZero(ring)) return ZERO; else return new F(F.SIN, this);
        }
    }

    @Override
    public Element cos(Ring ring) {
        if (ring.RADIAN.equals(Element.FALSE, ring)) {
            return new FuncNumberQZ(ring).trigFunc_Grad(F.COS, this);
        } else {
             if (isZero(ring)) return ONE; else return new F(F.COS, this);
        }
    }

    @Override
    public Element tan(Ring ring) {
        if (ring.RADIAN.equals(Element.FALSE, ring)) {
            return new FuncNumberQZ(ring).trigFunc_Grad(F.TG, this);
        } else {
           if (isZero(ring)) return ZERO; else return new F(F.TG, this);
        }
    }

    @Override
    public Element ctg(Ring ring) {
        if (ring.RADIAN.equals(Element.FALSE, ring)) {
            return new FuncNumberQZ(ring).trigFunc_Grad(F.CTG, this);
        } else {
             if (isZero(ring)) return NAN; else return new F(F.CTG, this);
        }
    }

    @Override
    public Element arcsn(Ring ring) {
        if (ring.RADIAN.equals(Element.FALSE, ring)) {
            return new FuncNumberQZ(ring).arc_trigFunc_Grad(this, F.SIN, ring);
        } else {
            return new FuncNumberQZ(ring).arc_trigFunc_Rad(new Fraction(this, ring), F.SIN, ring);
        }
    }

    @Override
    public Element arccs(Ring ring) {
        if (ring.RADIAN.equals(Element.FALSE, ring)) {
            return new FuncNumberQZ(ring).arc_trigFunc_Grad(this, F.COS, ring);
        } else {
            return new FuncNumberQZ(ring).arc_trigFunc_Rad(new Fraction(this, ring), F.COS, ring);
        }
    }

    @Override
    public Element arctn(Ring ring) {
        if (ring.RADIAN.equals(Element.FALSE, ring)) {
            return new FuncNumberQZ(ring).arc_trigFunc_Grad(this, F.TG, ring);
        } else {
            return new FuncNumberQZ(ring).arc_trigFunc_Rad(new Fraction(this, ring), F.TG, ring);
        }
    }

    @Override
    public Element arcctn(Ring ring) {
        if (ring.RADIAN.equals(Element.FALSE, ring)) {
            return new FuncNumberQZ(ring).arc_trigFunc_Grad(this, F.CTG, ring);
        } else {
            return new FuncNumberQZ(ring).arc_trigFunc_Rad(new Fraction(this, ring), F.CTG, ring);
        }
    }

    @Override
    public Element rootOf(int pow, Ring r) { // NEED     FactorPol fp= (FactorPol)factor(r);  2016 march
        ArrayList<Integer> mn = new ArrayList<Integer>();
        int f = 0;
        double dn = Math.sqrt(this.doubleValue()) ; double dr=Math.rint(dn);
        int n = (dr>=dn)?(int)dr: ((int)dr)+1;
        int j = n;
        for (int i = 2; i <= (n / 2); i++) {
            if (j % i == 0) {
                f = 1;
                while (j % i == 0) {
                    mn.add(i);
                    j = j / i;
                }
            }

        }
        if (f == 0) {
            mn.add(n);
        }
        double inRoot = 1;
        double withoutRoot = 1;
        for (int p = 0; p < mn.size(); p++) {
            int col = 1;
            int y = mn.get(p);
            mn.remove(p);
            int index = mn.indexOf(y);
            while (index != -1) {
                col++;
                mn.remove(index);
                index = mn.indexOf(y);
            }
            if (col < pow) {
                inRoot = inRoot * Math.pow(y, col);
            } else {
                double num = ((double) col) % ((double) pow);
                if (num == 0) {
                    withoutRoot = withoutRoot * Math.pow(y, (col / pow));
                } else {
                    inRoot = inRoot * Math.pow(y, Math.abs(num));
                    withoutRoot = withoutRoot * Math.pow(y, (col - num) / pow);
                }
            }
        }
        if (withoutRoot == 1) {
            return new F(F.ROOTOF, this, new NumberZ(pow));
        }
        if (inRoot == 1) {
            return new NumberZ((int) withoutRoot);
        } else {
            return new F(F.MULTIPLY, new NumberZ((int) withoutRoot),
                    new F(F.ROOTOF, new NumberZ((int) inRoot), new NumberZ(pow)));
        }
    }

    /**
     *
     * Интеграл от константы по переменной с номером num
     *
     */
    @Override
    public Element integrate(int num, Ring ring) {
        return ring.varPolynom[num].multiply(this, ring);
    }

    @Override
    public Element rootTheFirst(int n, Ring ring) {
        double res = Math.pow(Math.abs(this.doubleValue()), 1 / (double) n);
        return (Math.abs(res - Math.round(res)) != 0)
                ? new F(F.ROOTOF, this, ring.numberONE.valOf(n, ring))
                : (signum < 0) ? new NumberZ((int) res * -1) : new NumberZ((int) res);
    }

    @Override
    public Element factor(Ring ring) {
        try {
            return NFunctionZ32.factoringLong(this.longValue());
        } catch (IOException ex) {
            Logger.getLogger(NumberZ.class.getName()).log(Level.SEVERE, null, ex);
            ring.exception.append("Exception in factor of in Z."); return null;
        }
    }

    @Override
    public Element inverse(Ring ring) {
        return(isOne(ring)|isMinusOne(ring))? this :
                (this.signum<0)?new Fraction(ring.numberMINUS_ONE,this.negate()) : new Fraction(ring.numberONE,this);
    }

    @Override
    public Element divideExact(Element el, Ring ring) {
        if (el instanceof Complex) {Complex th=new Complex(this,ring);
            return th.divideExact(el, ring);
        }
        if (el instanceof Fraction) {
            Element res = el.toNumber(Ring.Z, ring);
            return divide((NumberZ) res);
        }
        return divide((NumberZ) el);
    }

    
       /**
     * Процедура, возвращающая   НОД двух чисел a=this и b:
     * и порождающие идеала (x и y), такие, что НОД(a,b) = a x + b y; 
     * @param b
     * @param ring
     * @return {НОД(a,b), x,  y}
     */
    public NumberZ[] extendedGCD(NumberZ b) {NumberZ a=this;
   NumberZ x=ZERO,y=ONE, u=ONE,v = ZERO;
    while (a.signum != 0){
       // q, r = b//a, b%a
       // m, n = x-u*q, y-v*q
       // b,a, x,y, u,v = a,r, u,v, m,n}
        NumberZ[] qr=b.divideAndRemainder(a);
        NumberZ m=x.subtract(u.multiply(qr[0]));
        NumberZ n=y.subtract(v.multiply(qr[0]));
        b=a;
        a=qr[1];
        x=u;
        y=v;
        u=m;
        v=n;}
    return (b.isNegative())?  new NumberZ[]{b.negate(), x.negate(), y.negate()}: 
                               new NumberZ[]{b, x, y};
    }
    
       public static Element arrayExtendedGCD(Element[] a, Element[] generators, Ring ring) {
           int n=a.length;
           NumberZ[] A=new NumberZ[n];  NumberZ[] B=new NumberZ[n]; 
           for (int i = 0; i < n; i++) {A[i]=(NumberZ)a[i]; }      
           Element res= arrayExtendedGCD(A, B);
           System.arraycopy(B, 0, generators, 0, n);  
           return res;
    }
       /**
        * Array GCD with starting value gcd0.
        * @param a -- array
        * @param gcd0 -- gcd of the previous part of array (or 0 in the beginning)
        * @param ring --Ring
        * @return gcd
        */
      public static Element arrayGCD(Element[] a, Element gcd0, Ring ring) {
        int i = 0;
        for (; i < a.length; i++) {if (!a[i].isZero(ring)) {break;}} if(i==a.length)return gcd0;
        Element gcd = (gcd0.isZero(ring))? a[i]: gcd0.GCD(a[i], ring);
        if (gcd.equals(ONE) || (a[i].equals(MINUS_ONE))) {return ONE;}     
        for (; i < a.length; i++) {
            if (!a[i].isZero(ring)) {
                gcd = gcd.GCD(a[i], ring);
                if (gcd.equals(ONE) || (a[i].equals(MINUS_ONE))) {return ONE;}
            }
        }
        return gcd;
    }  
       /**
     * Calculates GCD and generators RES of this principal ideal:
     *  SUM_i (RES[i]* a[i])=gcdN
     *
     * @param a array with Elements.
     * @param ring ring
     *
     * @return gcdN - is a gcd of all the elements from array a.
     *  array RES -- generators.
     */
    public static NumberZ arrayExtendedGCD(NumberZ[] a, NumberZ[] generators) {int n=a.length; 
        NumberZ gcd=ONE;
        for (int i = 0; i < n; i++) {generators[i]=ZERO;}
        int i = 0;
        for (; i < a.length; i++) { if (a[i].signum!=0) {break;}} if (i==a.length) return NumberZ.ZERO;
        generators[i]=ONE; 
        if (a[i].equals(ONE)) {return gcd;}
        if (a[i].equals(MINUS_ONE)) {generators[i]=MINUS_ONE; return gcd;}
        gcd = a[i];i++;
        for (; i < a.length; i++) {
            if ((a[i].signum!=0)) {
                NumberZ[] gxy = gcd.extendedGCD(a[i]);
                NumberZ x=gxy[1];
                for (int j = 0; j < i; j++) generators[j]=generators[j].multiply(x);
                generators[i]=  gxy[2];
                gcd=gxy[0];
                if (gcd.equals(ONE))  {return gcd;}
            }
        }
        return gcd;
    }
    
    public void takeValue(NumberZ c){signum=c.signum; mag=c.mag;}
    
    public static NumberZ[] intVector(int[] intV){
        NumberZ[] res= new NumberZ[intV.length];
        for (int i = 0; i < intV.length; i++) {res[i]=new NumberZ(intV[i]);}
        return res;
    }
    
    /**   The Jacobi symbol of (a,b) is defined when b is odd and positive. */
  public static Element JacobiSymbol(Element aa,Element bb, Ring ring) {
      NumberZ a=(NumberZ) aa.toNumber(Ring.Z, ring); 
      NumberZ b=(NumberZ) bb.toNumber(Ring.Z, ring);
    if(b.isEven()){ring.exception.append(" JacobiSymbol:The~second~argument~must~be~odd. "); return Element.NAN;}
    if(b.isNegative()){ring.exception.append(" JacobiSymbol:The~second~argument~must~be~positive. "); return Element.NAN;}
    NumberZ N4=NumberZ.POSCONST[4];  NumberZ N8=NumberZ.POSCONST[8];  
    int s=1;
    int j=1;
    while(j==1){
       if(b.isOne(ring)){j=0;}
       a=a.mod(b);
       if((j==1)&&(a.isZero(ring))){j=0; s=0;};
       if((j==1)&&(a.isOne(ring))){j=0;};
       int g=b.mod(N8).intValue();
       int h=1; if ((g==3)||(g==5)){h=-1;}
       int k=1;
       while ((j==1)&&(a.isEven())){k=k*h; a=a.shiftRight(1);}
       int m=a.mod(N4).intValue();
       int n=b.mod(N4).intValue();
       s=s*k; if ((m==3)&&(n==3)){s=-s;}
       NumberZ c=a; a=b; b=c;
    } 
return(new NumberZ64(s));
} 
    public static void main(String[] args) {
        Ring r = new Ring("Z[x]");
        Page page=new Page (r,true); 
        NumberZ[] b = intVector(new int[]{100,50,98,-78}); 
        NumberZ[] c= new NumberZ[b.length];
        NumberZ  gg=ZERO;
        NumberZ  gcd=  arrayExtendedGCD(b, c); 
        String out=Array.toString(c, r);
        for (int i = 0; i < b.length; i++) {
            gg=gg.add(c[i].multiply(b[i]));
    }
        System.out.println(gg+"  "+gcd+"  " + out );
}
}
