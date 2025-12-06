// © Разработчик Смирнов Роман Антонович (White_Raven), ТГУ'10.
package com.mathpar.number;

/** Класс созданный для того,что бы существовали числа типа a-0, a+0
 * @author Смирнов Роман Антонович ( WhiteR@ven).
 */
public final class NumberLimit extends Element {

    /**
     * Поле коструктора . Обычное число типа Double
     */
    public Double intVal;
    /**
     * Поле коструктора . значение типа int
     * >0 есть +0 ; <0 есть -0 ; 0 есть обычный Double
     */
    public int r_or_l; // >0 есть +0 ; <0 есть -0 ; 0 есть обычный Double
    /**
     *  Неопределенность
     */
    public static final NumberLimit NAN = new NumberLimit(Double.NaN, 0);
    /**
     *  Плюс бесконечность
     */
    public static final NumberLimit POSITIVE_INFINITY = new NumberLimit(Double.POSITIVE_INFINITY, 0);
    /**
     * Минус бесконечност
     */
    public static final NumberLimit NEGATIVE_INFINITY = new NumberLimit(Double.NEGATIVE_INFINITY, 0);
    /**
     * Плюс ноль
     */
    public static final NumberLimit POSITIVE_ZERO = new NumberLimit(Double.valueOf(0), 1);// +0
    /**
     * Минус ноль
     */
    public static final NumberLimit NEGATIVE_ZERO = new NumberLimit(Double.valueOf(0), -1);// -0

    public NumberLimit() {
    }

    /**
     * Конструктор от одного числа типа Double, т.е. без +0 и -0
     */
    public NumberLimit(Double a) {
        intVal = a;
        this.r_or_l = 0;
    }

    /**
     * Cтандартый коструктор для содания чисел типом NumberLimit
     * @param b>0 есть +0 , b< 0 есть -0, b=0 обычный Double
     */
    public NumberLimit(Double a, int b) {
        intVal = a;
        this.r_or_l = b;
    }

    /**
     * Конструктор от для создания NumberLimit от интов (int)
     * @param a - число типа int
     * @param b>0 есть +0 , b< 0 есть -0, b=0 обычный Double
     */
    public NumberLimit(int a, int b) {
        intVal = Double.valueOf(a);
        this.r_or_l = b;
    }

    /**
     * Конструктор от одного числа типа int, т.е. без +0 и -0
     */
    public NumberLimit(int a) {
        intVal = Double.valueOf(a);
        this.r_or_l = 0;
    }

    /**
     * Конструктор от для создания NumberLimit от Element
     * @param b>0 есть +0 , b< 0 есть -0, b=0 обычный Double
     */
    public NumberLimit(Element a, int b) {
        intVal = a.doubleValue();
        this.r_or_l = b;
    }

    /**
     * Конструктор от одного числа типа Element, т.е. без +0 и -0
     */
    public NumberLimit(Element a) {
        intVal = a.doubleValue();
        this.r_or_l = 0;
    }

    /**
     * Конструктор для создания чисел типа NumberLimit из строки
     * @param s переменная типа String
     */
    public NumberLimit(String s) {
        StringBuffer buf = new StringBuffer(s);
        int sum_signum_n = 0;
        int sum_signum_p = 0;
        while (buf.indexOf("-") != -1) {
            buf.deleteCharAt(buf.indexOf("-"));
            sum_signum_n++;
        }
        while (buf.indexOf("+") != -1) {
            buf.deleteCharAt(buf.indexOf("+"));
            sum_signum_p++;
        }
        if ((sum_signum_p == 0) & (sum_signum_n == 0)) {
            intVal = Double.valueOf(s);
            this.r_or_l = 0;
        } else {
            if (s == "-0") {
                intVal = Double.valueOf(0);
                this.r_or_l = -1;
            } else {
                if (s == "+0") {
                    intVal = Double.valueOf(0);
                    this.r_or_l = 1;
                } else {
                    switch (sum_signum_n) {
                        case 0: {
                            intVal = Double.valueOf(s.substring(0, s.indexOf("+")));
                            this.r_or_l = 1;
                            break;
                        }
                        case 1: {
                            if (sum_signum_p == 1) {
                                intVal = Double.valueOf(s.substring(0, s.indexOf("+")));
                                this.r_or_l = 1;
                                break;
                            }
                            if (s.trim().indexOf("-") == 0) {
                                intVal = Double.valueOf(s);
                                this.r_or_l = 0;
                                break;
                            } else {
                                intVal = Double.valueOf(s.substring(0, s.indexOf("-")));
                                this.r_or_l = -1;
                            }
                            break;
                        }
                        case 2: {
                            intVal = Double.valueOf(s.substring(0, s.lastIndexOf("-")));
                            this.r_or_l = -1;
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public int left() {
        return (r_or_l < 0) ? -1 : (r_or_l == 0) ? 0 : 1;
    }

    /**
     * Является ли бесконечностью (в данном случае не важно какой !!!!!)
     * @return true | false
     */
    public boolean isINFINITY() {
        return intVal.isInfinite();
    }

    /**
     * Является ли минус бесконечностью
     * @return true | false
     */
    @Override
    public boolean isNegativeInfinity() {
        return Double.compare(intVal, Double.NEGATIVE_INFINITY) == 0;
    }

    /**
     * Являеться ли плюс бесконечностью
     * @return true | false
     */
    @Override
    public boolean isPositiveInfinity() {
        return Double.compare(intVal, Double.POSITIVE_INFINITY) == 0;
    }

    /**
     * Неопределенность ???
     * @return true | false
     */
    public boolean isNAN() {
        return intVal.isNaN();
    }

    /**
     * Меняем число на противоположное , при этом направление движение остается тем же
     * @return
     */
    public NumberLimit negate() {
        return new NumberLimit(-intVal.doubleValue(), r_or_l);
    }

    /**
     * Меняем "лево с правом" , при этом значение остается неизменным
     * @return
     */
    public NumberLimit negateRoL() {
        return new NumberLimit(intVal, -r_or_l);
    }

    @Override
    public int signum() {
        return (intVal < 0) ? -1 : 1;
    }

    /**
     * Меняем число на противоположное а "лево с правом"
     * @return
     */
    public NumberLimit negateAll() {
        return new NumberLimit(-intVal.doubleValue(), -r_or_l);
    }

    @Override
    public double doubleValue() {
        return intVal.doubleValue();
    }

    @Override
    public long longValue() {
        return intVal.longValue();
    }

    @Override
    public int intValue() {
        return intVal.intValue();
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        if (this.r_or_l == 0) {
            return this.intVal.toString();
        } else {
            if (this.intVal.compareTo(Double.valueOf(0)) == 0) {
                if (this.r_or_l > 0) {
                    return "+0";
                }
                if (this.r_or_l < 0) {
                    return "-0";
                }
            }
            if (this.r_or_l > 0) {
                return res.append(this.intVal.toString() + "+0").toString();
            }
            if (this.r_or_l < 0) {
                return res.append(this.intVal.toString() + "-0").toString();
            }
        }
        return res.toString();
    }

    /**
     * Transform this number of NumberR64  type to the new type
     * fixed by "numberType" defined by Ring.type
     * @param numberType - new type
     * @return this transormed to the new type
     */
    @Override
    public Element toNumber(int numberType, Ring ring) {
        switch (numberType) {
            case Ring.Z:
                return (new NumberR(intVal)).NumberRtoNumberZ();
            case Ring.Z64:
                return new NumberZ64(longValue());
            case Ring.Zp32:
                return new NumberZp32(longValue(), ring);
            case Ring.Zp:
                return new NumberZp(new NumberZ(longValue()), ring);
            case Ring.R:
                return new NumberR(intVal);
            case Ring.R64:
                return this;
            case Ring.R128:
                return new NumberR128(intVal);
//       case Ring.Fraction:
//            return new Fraction(this);
//       case Ring.Complex_:
//            return new Complex(this);
        }
        return null;
    }

    public NumberLimit D(int num) {
        return new NumberLimit(Double.valueOf(0), r_or_l);
    }

    public NumberLimit D() {
        return D(0);
    }

    public boolean isOne() {
        return intVal == 1 && r_or_l == 0;
    }

    public NumberLimit valOf(Element x) {
        return null;
    }

    @Override
    public Boolean isZero(Ring r) {
        return (Double.compare(intVal, 0) != 0) ? false : true;
    }

    @Override
    public boolean equals(Element x, Ring r) {
        return ((double) intVal) == ((double) ((NumberLimit) x).intVal);
    }


    public int compareTo(Element o) {
        if (o instanceof NumberLimit) {
            NumberLimit nl = (NumberLimit) o;
            int res = Double.compare(intVal, nl.intVal);
            return (res == 0) & (r_or_l == nl.r_or_l) ? 0 : r_or_l;
        }
        return -1;
    }

    @Override
    public int compareTo(Element x, Ring ring) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean isOne(Ring ring) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
