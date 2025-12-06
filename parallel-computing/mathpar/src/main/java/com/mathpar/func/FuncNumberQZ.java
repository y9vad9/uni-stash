package com.mathpar.func;

import com.mathpar.number.*;

/**
 * Возвращает значение тригонометрических функций: если угол задается целым
 * числом, счиитает как в градусах, если угол задаем дробью, т.е. вводится дробь
 * перед Pi, то считает как в радианах Вычисляет обратные тригонометрические
 * функции и результат выводит как в градусах, так и в радианах в зависимости от
 * задачи Вычисляет корни и возводит в любую дробную степень целое число, если
 * это возможно
 *
 * @author Rayetskaya Liliana (correct by Smirnov Roman)
 */
public class FuncNumberQZ {

    /**
     * Наше входное колечко, в зависимости от того Z[] | Q[] будут производиться
     * вычисления и устанавливаться переменные
     */
    public Ring inputRing;

    /**
     * Конструктор от кольца
     *
     * @param r - входное кольцо
     */
    public FuncNumberQZ(Ring r) {
        this.inputRing = r;
        zM1 = (inputRing.algebra[0] == Ring.Q) ? new Fraction(-1) : NumberZ.MINUS_ONE;
        z0 = (inputRing.algebra[0] == Ring.Q) ? Fraction.Z_ZERO : NumberZ.ZERO;
        z1 = (inputRing.algebra[0] == Ring.Q) ? Fraction.Z_ONE : NumberZ.ONE;
        q1D2 = (inputRing.algebra[0] == Ring.Q) ? new Fraction(1, 2) : new F(F.DIVIDE, new Element[] {NumberZ.ONE, new NumberZ(2)});
        qM1D2 = (inputRing.algebra[0] == Ring.Q) ? new Fraction(-1, 2) : new F(F.DIVIDE, new Element[] {NumberZ.MINUS_ONE, new NumberZ(2)});
        z2 = (inputRing.algebra[0] == Ring.Q) ? new Fraction(2) : new NumberZ(2);
        z3 = (inputRing.algebra[0] == Ring.Q) ? new Fraction(3) : new NumberZ(3);
        z4 = (inputRing.algebra[0] == Ring.Q) ? new Fraction(4) : new NumberZ(4);
        z5 = (inputRing.algebra[0] == Ring.Q) ? new Fraction(5) : new NumberZ(5);
        z6 = (inputRing.algebra[0] == Ring.Q) ? new Fraction(6) : new NumberZ(6);
        z8 = (inputRing.algebra[0] == Ring.Q) ? new Fraction(8) : new NumberZ(8);
        sqrt_3 = new F(F.SQRT, new Element[] {z3});
        neg_sqrt_3 = new F(F.MULTIPLY, new Element[] {zM1, sqrt_3});
        sqrt_2 = new F(F.SQRT, new Element[] {z2});
        neg_sqrt_2 = new F(F.MULTIPLY, new Element[] {zM1, sqrt_2});
        sqrt_5 = new F(F.SQRT, new Element[] {z5});
        sqrt_6 = new F(F.SQRT, new Element[] {z6});
        // sqrt(2)/2
        sqrt_2_div_2 = new F(F.DIVIDE, new Element[] {sqrt_2, z2});
        // - sqrt(2)/2
        neg_sqrt_2_div_2 = new F(F.DIVIDE, new Element[] {
            new F(F.MULTIPLY, new Element[] {zM1, sqrt_2}), z2});
        // sqrt(3) / 2
        sqrt_3_div_2 = new F(F.DIVIDE, new Element[] {sqrt_3, z2});
        // -sqrt(3) / 2
        neg_sqrt_3_div_2 = new F(F.DIVIDE, new Element[] {
            new F(F.MULTIPLY, new Element[] {zM1, sqrt_3}), z2});
        // sqrt(3) / 3
        sqrt_3_div_3 = new F(F.DIVIDE, new Element[] {sqrt_3, z3});
        // - sqrt(3) / 3
        neg_sqrt_3_div_3 = new F(F.DIVIDE, new Element[] {
            new F(F.MULTIPLY, new Element[] {zM1, sqrt_3}), z3});
        // (sqrt(6)-sqrt(2))/4
        min_sqrt_2_plus_sqrt_6_DIV_4 = new F(F.DIVIDE, new Element[] {
            new F(F.SUBTRACT, new Element[] {sqrt_6, sqrt_2}), z4});
        // (sqrt(2)-sqrt(6))/4
        neg_min_sqrt_2_plus_sqrt_6_DIV_4 = new F(F.DIVIDE,
                new Element[] {new F(F.SUBTRACT, new Element[] {sqrt_2, sqrt_6}), z4});
        // (sqrt(2)+sqrt(6))/4
        sqrt_2_plus_sqrt_6_DIV_4 = new F(F.DIVIDE, new Element[] {
            new F(F.ADD, new Element[] {sqrt_2, sqrt_6}), z4});
        //(-sqrt(2)-sqrt(6))/4
        neg_sqrt_2_plus_sqrt_6_DIV_4 = new F(F.DIVIDE, new Element[] {
            new F(F.SUBTRACT, new Element[] {neg_sqrt_2, sqrt_6}), z4});
        // (sqrt(5)-1)/4
        min_1_plus_sqrt_5_DIV_4 = new F(F.DIVIDE, new Element[] {
            new F(F.SUBTRACT, new Element[] {sqrt_5, z1}), z4});
        // (1-sqrt(5))/4
        neg_min_1_plus_sqrt_5_DIV_4 = new F(F.DIVIDE, new Element[] {
            new F(F.SUBTRACT, new Element[] {z1, sqrt_5}), z4});
        // (1+sqrt(5))/4
        pl_1_plus_sqrt_5_DIV_4 = new F(F.DIVIDE, new Element[] {
            new F(F.ADD, new Element[] {z1, sqrt_5}), z4});
        //(-1-sqrt(5)) /4
        neg_pl_1_plus_sqrt_5_DIV_4 = new F(F.DIVIDE, new Element[] {
            new F(F.SUBTRACT, new Element[] {zM1, sqrt_5}), z4});
        // sqrt((5-sqrt(5))/8)
        SQRT_5_dev_8_MIN_sqrt_5_dev_8 = new F(F.SQRT, new Element[] {
            new F(F.DIVIDE, new Element[] {new F(F.SUBTRACT,
                new Element[] {z5, sqrt_5}), z8})});
        // -sqrt((5-sqrt(5))/8)
        neg_SQRT_5_dev_8_MIN_sqrt_5_dev_8 = new F(F.MULTIPLY,
                new Element[] {zM1, new F(F.SQRT, new Element[] {new F(F.DIVIDE,
                            new Element[] {new F(F.SUBTRACT,
                                        new Element[] {z5, sqrt_5}), z8})})});
        // sqrt((5+sqrt(5))/8)
        SQRT_5_dev_8_PLUS_sqrt_5_dev_8 = new F(F.SQRT, new Element[] {
            new F(F.DIVIDE, new Element[] {new F(F.ADD,
                new Element[] {z5, sqrt_5}), z8})});
        // -sqrt((5+sqrt(5))/8)
        neg_SQRT_5_dev_8_PLUS_sqrt_5_dev_8 = new F(F.MULTIPLY,
                new Element[] {zM1, new F(F.SQRT, new Element[] {new F(F.DIVIDE,
                            new Element[] {new F(F.ADD,
                                        new Element[] {z5, sqrt_5}), z8})})});
        // 2-sqrt(3)
        pl_2_min_sqrt_3 = new F(F.SUBTRACT,
                new Element[] {z2, sqrt_3});
        //sqrt(3)-2
        neg_pl_2_min_sqrt_3 = new F(F.SUBTRACT,
                new Element[] {sqrt_3, z2});
        //2+sqrt(3)
        pl_2_plus_sqrt_3 = new F(F.ADD, new Element[] {z2, sqrt_3});
        //-2-sqrt(3)
        neg_pl_2_plus_sqrt_3 = new F(F.SUBTRACT, new Element[] {z2.negate(inputRing), sqrt_3});
        //sqrt(5-2sqrt(5))
        SQRT_pl_5_min_2_sqrt_5 = new F(F.SQRT, new Element[] {
            new F(F.SUBTRACT, new Element[] {z5, new F(F.MULTIPLY,
                new Element[] {z2, sqrt_5})})});
        //- sqrt(5-2sqrt(5))
        neg_SQRT_pl_5_min_2_sqrt_5 = new F(F.MULTIPLY,
                new Element[] {zM1, SQRT_pl_5_min_2_sqrt_5});
        //sqrt(5+2sqrt(5))
        SQRT_pl_5_plus_2_sqrt_5 = new F(F.SQRT,
                new Element[] {new F(F.ADD, new Element[] {z5, new F(F.MULTIPLY,
                            new Element[] {z2, sqrt_5})})});
        // - sqrt(5+2sqrt(5))
        neg_SQRT_pl_5_plus_2_sqrt_5 = new F(F.MULTIPLY,
                new Element[] {zM1, SQRT_pl_5_plus_2_sqrt_5});
        SQRT_1_plus_2_sqrt_5_div_5 = new F(F.SQRT,
                new Element[] {new F(F.ADD, new Element[] {z1, new F(F.DIVIDE,
                            new Element[] {new F(F.MULTIPLY,
                                        new Element[] {z2, sqrt_5}), z5})})});
        neg_SQRT_1_plus_2_sqrt_5_div_5
                = ((F) SQRT_1_plus_2_sqrt_5_div_5.negate(inputRing));
        SQRT_1_min_2_sqrt_5_div_5 = new F(F.SQRT, new Element[] {
            new F(F.SUBTRACT, new Element[] {z1, new F(F.DIVIDE,
                new Element[] {
                    new F(F.MULTIPLY, new Element[] {z2, sqrt_5}), z5})})});
        neg_SQRT_1_min_2_sqrt_5_div_5
                = ((F) SQRT_1_min_2_sqrt_5_div_5.negate(inputRing));
        /**
         * Таблица значений, которые может принимать SIN
         */
        TablZnachForSIN = new Element[] {z0,
            min_sqrt_2_plus_sqrt_6_DIV_4,
            min_1_plus_sqrt_5_DIV_4,
            q1D2,
            SQRT_5_dev_8_MIN_sqrt_5_dev_8,
            sqrt_2_div_2,
            pl_1_plus_sqrt_5_DIV_4,
            sqrt_3_div_2,
            SQRT_5_dev_8_PLUS_sqrt_5_dev_8,
            sqrt_2_plus_sqrt_6_DIV_4,
            z1,
            neg_min_sqrt_2_plus_sqrt_6_DIV_4,
            neg_min_1_plus_sqrt_5_DIV_4,
            qM1D2,
            neg_SQRT_5_dev_8_MIN_sqrt_5_dev_8,
            neg_sqrt_2_div_2,
            neg_pl_1_plus_sqrt_5_DIV_4,
            neg_sqrt_3_div_2,
            neg_SQRT_5_dev_8_PLUS_sqrt_5_dev_8,
            neg_sqrt_2_plus_sqrt_6_DIV_4,
            zM1
        };
        /**
         * Таблица значений, которые может принимать COS
         */
        TablZnachForCOS = new Element[] {z1,
            zM1,
            sqrt_2_plus_sqrt_6_DIV_4,
            SQRT_5_dev_8_PLUS_sqrt_5_dev_8,
            sqrt_3_div_2,
            pl_1_plus_sqrt_5_DIV_4,
            sqrt_2_div_2,
            SQRT_5_dev_8_MIN_sqrt_5_dev_8,
            q1D2,
            min_1_plus_sqrt_5_DIV_4,
            min_sqrt_2_plus_sqrt_6_DIV_4,
            z0,
            neg_min_sqrt_2_plus_sqrt_6_DIV_4,
            neg_min_1_plus_sqrt_5_DIV_4,
            qM1D2,
            neg_SQRT_5_dev_8_MIN_sqrt_5_dev_8,
            neg_sqrt_2_div_2,
            neg_pl_1_plus_sqrt_5_DIV_4,
            neg_sqrt_3_div_2,
            neg_SQRT_5_dev_8_PLUS_sqrt_5_dev_8,
            neg_sqrt_2_plus_sqrt_6_DIV_4
        };
        TablZnachForTG = new Element[] {z0,
            pl_2_min_sqrt_3,
            SQRT_1_min_2_sqrt_5_div_5,
            sqrt_3_div_3,
            SQRT_pl_5_min_2_sqrt_5,
            z1,
            SQRT_1_plus_2_sqrt_5_div_5,
            sqrt_3,
            SQRT_pl_5_plus_2_sqrt_5,
            pl_2_plus_sqrt_3,
            NumberR.POSITIVE_INFINITY,
            NumberR.NEGATIVE_INFINITY,
            neg_pl_2_plus_sqrt_3,
            neg_SQRT_pl_5_plus_2_sqrt_5,
            neg_sqrt_3,
            neg_SQRT_1_plus_2_sqrt_5_div_5,
            zM1,
            neg_SQRT_pl_5_min_2_sqrt_5,
            neg_sqrt_3_div_3,
            neg_SQRT_1_min_2_sqrt_5_div_5,
            neg_pl_2_min_sqrt_3
        };
        TablZnachForCTG = new Element[] {NumberR.POSITIVE_INFINITY,
            NumberR.NEGATIVE_INFINITY,
            pl_2_plus_sqrt_3,
            SQRT_pl_5_plus_2_sqrt_5,
            sqrt_3,
            SQRT_1_plus_2_sqrt_5_div_5,
            z1,
            SQRT_pl_5_min_2_sqrt_5,
            sqrt_3_div_3,
            SQRT_1_min_2_sqrt_5_div_5,
            pl_2_min_sqrt_3,
            z0,
            neg_pl_2_min_sqrt_3,
            neg_SQRT_1_min_2_sqrt_5_div_5,
            neg_sqrt_3_div_3,
            neg_SQRT_pl_5_min_2_sqrt_5,
            zM1,
            neg_SQRT_1_plus_2_sqrt_5_div_5,
            neg_sqrt_3,
            neg_SQRT_pl_5_plus_2_sqrt_5,
            neg_pl_2_plus_sqrt_3
        };
    }
    Element zM1, z0, z1, q1D2, qM1D2, z2, z3, z4, z5, z6, z8, sqrt_3, neg_sqrt_3, sqrt_2, neg_sqrt_2,
            sqrt_5, sqrt_6, sqrt_2_div_2, neg_sqrt_2_div_2, sqrt_3_div_2, neg_sqrt_3_div_2, sqrt_3_div_3,
            neg_sqrt_3_div_3, min_sqrt_2_plus_sqrt_6_DIV_4, neg_min_sqrt_2_plus_sqrt_6_DIV_4, sqrt_2_plus_sqrt_6_DIV_4,
            neg_sqrt_2_plus_sqrt_6_DIV_4, min_1_plus_sqrt_5_DIV_4, neg_min_1_plus_sqrt_5_DIV_4, pl_1_plus_sqrt_5_DIV_4,
            neg_pl_1_plus_sqrt_5_DIV_4, SQRT_5_dev_8_MIN_sqrt_5_dev_8, neg_SQRT_5_dev_8_MIN_sqrt_5_dev_8, SQRT_5_dev_8_PLUS_sqrt_5_dev_8,
            neg_SQRT_5_dev_8_PLUS_sqrt_5_dev_8, pl_2_min_sqrt_3, neg_pl_2_min_sqrt_3, pl_2_plus_sqrt_3, neg_pl_2_plus_sqrt_3,
            SQRT_pl_5_min_2_sqrt_5, neg_SQRT_pl_5_min_2_sqrt_5, SQRT_pl_5_plus_2_sqrt_5, neg_SQRT_pl_5_plus_2_sqrt_5,
            SQRT_1_plus_2_sqrt_5_div_5, neg_SQRT_1_plus_2_sqrt_5_div_5, SQRT_1_min_2_sqrt_5_div_5, neg_SQRT_1_min_2_sqrt_5_div_5;
    Element[] TablZnachForSIN, TablZnachForCOS, TablZnachForTG, TablZnachForCTG;
    /**
     * Таблица углов, которые может принимать ARC_SIN
     */
    static Element[] TablGradFor_SIN = new Element[] {NumberZ.ZERO,
        new NumberZ(15),
        new NumberZ(18),
        new NumberZ(30),
        new NumberZ(36),
        new NumberZ(45),
        new NumberZ(54),
        new NumberZ(60),
        new NumberZ(72),
        new NumberZ(75),
        new NumberZ(90),
        new NumberZ(345),
        new NumberZ(342),
        new NumberZ(330),
        new NumberZ(324),
        new NumberZ(315),
        new NumberZ(306),
        new NumberZ(300),
        new NumberZ(288),
        new NumberZ(285),
        new NumberZ(270)
    };
    /**
     * Таблица углов, которые может принимать ARC_COS
     */
    static Element[] TablGradFor_COS = new Element[] {NumberZ.ZERO,
        new NumberZ(180),
        new NumberZ(15),
        new NumberZ(18),
        new NumberZ(30),
        new NumberZ(36),
        new NumberZ(45),
        new NumberZ(54),
        new NumberZ(60),
        new NumberZ(72),
        new NumberZ(75),
        new NumberZ(90),
        new NumberZ(105),
        new NumberZ(108),
        new NumberZ(120),
        new NumberZ(126),
        new NumberZ(135),
        new NumberZ(144),
        new NumberZ(150),
        new NumberZ(162),
        new NumberZ(165)
    };
    /**
     * Таблица углов, которые может принимать ARC_TG
     */
    static Element[] TablGradFor_TG = new Element[] {NumberZ.ZERO,
        new NumberZ(15),
        new NumberZ(18),
        new NumberZ(30),
        new NumberZ(36),
        new NumberZ(45),
        new NumberZ(54),
        new NumberZ(60),
        new NumberZ(72),
        new NumberZ(75),
        new NumberZ(90),
        new NumberZ(270),
        new NumberZ(105),
        new NumberZ(108),
        new NumberZ(120),
        new NumberZ(126),
        new NumberZ(135),
        new NumberZ(144),
        new NumberZ(150),
        new NumberZ(162),
        new NumberZ(165),};
    /**
     * Таблица углов, которые может принимать ARC_CTG
     */
    static Element[] TablGradFor_CTG = new Element[] {NumberZ.ZERO,
        new NumberZ(180),
        new NumberZ(15),
        new NumberZ(18),
        new NumberZ(30),
        new NumberZ(36),
        new NumberZ(45),
        new NumberZ(54),
        new NumberZ(60),
        new NumberZ(72),
        new NumberZ(75),
        new NumberZ(90),
        new NumberZ(105),
        new NumberZ(108),
        new NumberZ(120),
        new NumberZ(126),
        new NumberZ(135),
        new NumberZ(144),
        new NumberZ(150),
        new NumberZ(162),
        new NumberZ(165)
    };

    /**
     * функция возврашает значение триконометрической функции, в зависимости от
     * номера функции и угла
     *
     * @param funcNumb функция, которую будем считать
     * @param angle угол, от которого будем считать значение функции
     *
     * @return значение функции
     */
    public Element trigFunc(int funcNumb, Element angle) {
        Element x = null;
        if (angle instanceof Fraction) {
            x = trigFunc_Rad(funcNumb, (Fraction) angle);
        }
        if (angle instanceof NumberZ) {
            x = trigFunc_Grad(funcNumb, (NumberZ) angle);
        }
        return x;
    }

    /**
     * функция возвращает значение тригонометрическойфункции если угол задан в
     * радианах Pi считаем как символ и работаем с коэффициентом перед Pi, т.е с
     * задаваемой дробью
     *
     * @param funcNumb функция, которую будем считать
     * @param rad коэффициент перед Pi угола от которого будем считать значение
     * функции, заданный в радианах
     *
     * @return значение функции от данного угла
     */
    public Element trigFunc_Rad(int funcNumb, Fraction rad) {
        Element hh = rad.multiply(new Fraction(180), inputRing);
        if (hh instanceof Fraction) {
            return null;//new F(funcNumb, hh);
        } //!!!!!!!!!!!!!!!!!!!!!!!!!!!  ЛАТАТЬ !!!!!!!!!!!!!!!!
        NumberZ gg;
        if (hh instanceof F) {// небольшая заплатка, связанная с путаницейй концепций вычисления
            hh = hh.expand(inputRing);
            for (int i = 0; i < ((F) hh).X.length; i++) {
                if (((F) hh).X[i].isItNumber()) {
                    hh = ((F) hh).X[i];
                    break;
                }
            }
        }
        gg = new NumberZ(hh.intValue());
        return trigFunc_Grad(funcNumb, gg);

    }

    /**
     * Привордим угол к интервалу [0 , 360]
     *
     * @param degree
     *
     * @return
     */
    private static int Pi2_degree(int degree) {
        degree = degree % 360;
        return (degree >= 0) ? degree : 360 + degree;
    }

    /**
     * функция возвращает значение тригонометрической функции если угол задан в
     * градусах
     *
     * @param funcNumb функция, которую будем считать
     * @param degreeZ угол, от которого будем считать значение функции, заданный
     * в градусах
     *
     * @return значение функции от данного угла
     */
    public Element trigFunc_Grad(int funcNumb, NumberZ degreeZ) {
        int degree = degreeZ.remainder(new NumberZ(360)).intValue();
        degree = Pi2_degree(degree);
        switch (funcNumb) {
            case F.SIN:
                switch (degree) {
                    case 0:
                    case 180:
                        return z0;
                    case 15:
                    case 165:
                        return min_sqrt_2_plus_sqrt_6_DIV_4;
                    case 18:
                    case 162:
                        return min_1_plus_sqrt_5_DIV_4;
                    case 30:
                    case 150:
                        return q1D2;
                    case 36:
                    case 144:
                        return SQRT_5_dev_8_MIN_sqrt_5_dev_8;
                    case 45:
                    case 135:
                        return sqrt_2_div_2;
                    case 54:
                    case 126:
                        return pl_1_plus_sqrt_5_DIV_4;
                    case 60:
                    case 120:
                        return sqrt_3_div_2;
                    case 72:
                    case 108:
                        return SQRT_5_dev_8_PLUS_sqrt_5_dev_8;
                    case 75:
                    case 105:
                        return sqrt_2_plus_sqrt_6_DIV_4;
                    case 90:
                        return z1;
                    case 195:
                    case 345:
                        return neg_min_sqrt_2_plus_sqrt_6_DIV_4;
                    case 198:
                    case 342:
                        return neg_min_1_plus_sqrt_5_DIV_4;
                    case 210:
                    case 330:
                        return qM1D2;
                    case 216:
                    case 324:
                        return neg_SQRT_5_dev_8_MIN_sqrt_5_dev_8;
                    case 225:
                    case 315:
                        return neg_sqrt_2_div_2;
                    case 234:
                    case 306:
                        return neg_pl_1_plus_sqrt_5_DIV_4;
                    case 240:
                    case 300:
                        return neg_sqrt_3_div_2;
                    case 252:
                    case 288:
                        return neg_SQRT_5_dev_8_PLUS_sqrt_5_dev_8;
                    case 255:
                    case 285:
                        return neg_sqrt_2_plus_sqrt_6_DIV_4;
                    case 270:
                        return zM1;
                    default:
                        return null;
                }
            case F.COS:
                switch (degree) {
                    case 0:
                        return z1;
                    case 180:
                        return zM1;
                    case 15:
                    case 345:
                        return sqrt_2_plus_sqrt_6_DIV_4;
                    case 18:
                    case 342:
                        return SQRT_5_dev_8_PLUS_sqrt_5_dev_8;
                    case 30:
                    case 330:
                        return sqrt_3_div_2;
                    case 36:
                    case 324:
                        return pl_1_plus_sqrt_5_DIV_4;
                    case 45:
                    case 315:
                        return sqrt_2_div_2;
                    case 54:
                    case 306:
                        return SQRT_5_dev_8_MIN_sqrt_5_dev_8;
                    case 60:
                    case 300:
                        return q1D2;
                    case 72:
                    case 288:
                        return min_1_plus_sqrt_5_DIV_4;
                    case 75:
                    case 285:
                        return min_sqrt_2_plus_sqrt_6_DIV_4;
                    case 90:
                    case 270:
                        return z0;
                    case 105:
                    case 255:
                        return neg_min_sqrt_2_plus_sqrt_6_DIV_4;
                    case 108:
                    case 252:
                        return neg_min_1_plus_sqrt_5_DIV_4;
                    case 120:
                    case 240:
                        return qM1D2;
                    case 126:
                    case 234:
                        return neg_SQRT_5_dev_8_MIN_sqrt_5_dev_8;
                    case 135:
                    case 225:
                        return neg_sqrt_2_div_2;
                    case 144:
                    case 216:
                        return neg_pl_1_plus_sqrt_5_DIV_4;
                    case 150:
                    case 210:
                        return neg_sqrt_3_div_2;
                    case 162:
                    case 198:
                        return neg_SQRT_5_dev_8_PLUS_sqrt_5_dev_8;
                    case 165:
                    case 195:
                        return neg_sqrt_2_plus_sqrt_6_DIV_4;
                    default:
                        return null;
                }
            case F.TG:
                switch (degree) {
                    case 0:
                    case 180:
                        return z0;
                    case 15:
                    case 195:
                        return pl_2_min_sqrt_3;
                    case 18:
                    case 198:
                        return SQRT_1_min_2_sqrt_5_div_5;
                    case 30:
                    case 210:
                        return sqrt_3_div_3;
                    case 36:
                    case 216:
                        return SQRT_pl_5_min_2_sqrt_5;
                    case 45:
                    case 225:
                        return z1;
                    case 54:
                    case 234:
                        return SQRT_1_plus_2_sqrt_5_div_5;
                    case 60:
                    case 240:
                        return sqrt_3;
                    case 72:
                    case 252:
                        return SQRT_pl_5_plus_2_sqrt_5;
                    case 75:
                    case 255:
                        return pl_2_plus_sqrt_3;
                    case 90:
                        return NumberR.POSITIVE_INFINITY;
                    case 270:
                        return NumberR.NEGATIVE_INFINITY;
                    case 105:
                    case 285:
                        return neg_pl_2_plus_sqrt_3;
                    case 108:
                    case 288:
                        return neg_SQRT_pl_5_plus_2_sqrt_5;
                    case 120:
                    case 300:
                        return neg_sqrt_3;
                    case 126:
                    case 306:
                        return neg_SQRT_1_plus_2_sqrt_5_div_5;
                    case 135:
                    case 315:
                        return zM1;
                    case 144:
                    case 324:
                        return neg_SQRT_pl_5_min_2_sqrt_5;
                    case 150:
                    case 330:
                        return neg_sqrt_3_div_3;
                    case 162:
                    case 342:
                        return neg_SQRT_1_min_2_sqrt_5_div_5;
                    case 165:
                    case 345:
                        return neg_pl_2_min_sqrt_3;
                    default:
                        return null;
                }
            case F.CTG:
                switch (degree) {
                    case 0:
                        return NumberR.POSITIVE_INFINITY;
                    case 180:
                        return NumberR.NEGATIVE_INFINITY;
                    case 15:
                    case 195:
                        return pl_2_plus_sqrt_3;
                    case 18:
                    case 198:
                        return SQRT_pl_5_plus_2_sqrt_5;
                    case 30:
                    case 210:
                        return sqrt_3;
                    case 36:
                    case 216:
                        return SQRT_1_plus_2_sqrt_5_div_5;
                    case 45:
                    case 225:
                        return z1;
                    case 54:
                    case 234:
                        return SQRT_pl_5_min_2_sqrt_5;
                    case 60:
                    case 240:
                        return sqrt_3_div_3;
                    case 72:
                    case 252:
                        return SQRT_1_min_2_sqrt_5_div_5;
                    case 75:
                    case 255:
                        return pl_2_min_sqrt_3;
                    case 90:
                    case 270:
                        return z0;
                    case 105:
                    case 285:
                        return neg_pl_2_min_sqrt_3;
                    case 108:
                    case 288:
                        return neg_SQRT_1_min_2_sqrt_5_div_5;
                    case 120:
                    case 300:
                        return neg_sqrt_3_div_3;
                    case 126:
                    case 306:
                        return neg_SQRT_pl_5_min_2_sqrt_5;
                    case 135:
                    case 315:
                        return zM1;
                    case 144:
                    case 324:
                        return SQRT_1_plus_2_sqrt_5_div_5;
                    case 150:
                    case 330:
                        return neg_sqrt_3;
                    case 162:
                    case 342:
                        return neg_SQRT_pl_5_plus_2_sqrt_5;
                    case 165:
                    case 345:
                        return neg_pl_2_plus_sqrt_3;
                    default:
                        return null;
                }
            default:
                return null;
        }
    }

    public F cleanFraction(F f) {
        Element[] X = new Element[f.X.length];
        for (int i = 0; i < f.X.length; i++) {
            if (f.X[i] instanceof F) {
                X[i] = cleanFraction((F) f.X[i]);
            } else {
                if (f.X[i] instanceof Fraction) {
                    if (((Fraction) f.X[i]).denom.isOne(inputRing)) {
                        X[i] = ((Fraction) f.X[i]).num;
                    } else {
                        X[i] = f.X[i];
                    }
                } else {
                    X[i] = f.X[i];
                }
            }

        }
        return new F(f.name, X);
    }

    /**
     * Обратные функции:
     */
    /**
     * Вычисляет обратную тригонометрическую функцю, результат в градусной мере
     *
     * @param znach значение, от которого нужно посчитать обратную функцию
     * @param funcNumb функция, для которой будем искать обратную
     *
     * @return значение обратной функции, выведенное в градусной мере
     */
    public Element arc_trigFunc_Grad(Element znach, int funcNumb, Ring ring) {
        switch (funcNumb) {
            case F.SIN: {
                if (znach instanceof F) {
                    for (int i = 0; i < TablZnachForSIN.length; i++) {
                        if (TablZnachForSIN[i] instanceof F) {
                            if (((F) znach).compareTo(TablZnachForSIN[i], ring) == 0) {
                                return TablGradFor_SIN[i];
                            }
                        }
                    }
                }
                if (znach instanceof NumberZ) {
                    for (int i = 0; i < TablZnachForSIN.length; i++) {
                        if (TablZnachForSIN[i] instanceof NumberZ) {
                            if (((NumberZ) znach).compareTo(TablZnachForSIN[i], ring) == 0) {
                                return TablGradFor_SIN[i];
                            }
                        }
                    }
                }
                if (znach instanceof Fraction) {
                    for (int i = 0; i < TablZnachForSIN.length; i++) {
                        if (TablZnachForSIN[i] instanceof Fraction) {
                            if (((Fraction) znach).compareTo(TablZnachForSIN[i], ring) == 0) {
                                return TablGradFor_SIN[i];
                            }
                        }
                    }
                }
            }
            break;
            case F.COS: {
                if (znach instanceof F) {
                    for (int i = 0; i < TablZnachForCOS.length; i++) {
                        if (TablZnachForCOS[i] instanceof F) {
                            if (((F) znach).compareTo(TablZnachForCOS[i], ring) == 0) {
                                return TablGradFor_COS[i];
                            }
                        }
                    }
                }
                if (znach instanceof NumberZ) {
                    for (int i = 0; i < TablZnachForCOS.length; i++) {
                        if (TablZnachForCOS[i] instanceof NumberZ) {
                            if (((NumberZ) znach).compareTo(TablZnachForCOS[i], ring) == 0) {
                                return TablGradFor_COS[i];
                            }
                        }
                    }
                }
                if (znach instanceof Fraction) {
                    for (int i = 0; i < TablZnachForCOS.length; i++) {
                        if (TablZnachForCOS[i] instanceof Fraction) {
                            if (((Fraction) znach).compareTo(TablZnachForCOS[i], ring) == 0) {
                                return TablGradFor_COS[i];
                            }
                        }
                    }
                }
            }
            break;
            case F.TG: {
                if (znach instanceof F) {
                    for (int i = 0; i < TablZnachForTG.length; i++) {
                        if (TablZnachForTG[i] instanceof F) {
                            if (((F) znach).compareTo(TablZnachForTG[i], ring) == 0) {
                                return TablGradFor_TG[i];
                            }
                        }
                    }
                }
                if (znach instanceof NumberZ) {
                    for (int i = 0; i < TablZnachForCOS.length; i++) {
                        if (TablZnachForTG[i] instanceof NumberZ) {
                            if (((NumberZ) znach).compareTo(TablZnachForTG[i], ring) == 0) {
                                return TablGradFor_TG[i];
                            }
                        }
                    }
                }
                if (znach instanceof Fraction) {
                    for (int i = 0; i < TablZnachForTG.length; i++) {
                        if (TablZnachForTG[i] instanceof Fraction ) {
                            if (((Fraction) znach).compareTo(TablZnachForTG[i], ring) == 0) {
                                return TablGradFor_TG[i];
                            }
                        }
                    }
                }
                if (znach instanceof NumberR) {
                    for (int i = 0; i < TablZnachForTG.length; i++) {
                        if (TablZnachForTG[i] instanceof NumberR) {
                            if (((NumberR) znach).compareTo(TablZnachForTG[i], ring) == 0) {
                                return TablGradFor_TG[i];
                            }
                        }
                    }
                }
            }
            break;
            case F.CTG: {
                if (znach instanceof F) {
                    for (int i = 0; i < TablZnachForCTG.length; i++) {
                        if (TablZnachForCTG[i] instanceof F) {
                            if (((F) znach).compareTo(TablZnachForCTG[i], ring) == 0) {
                                return TablGradFor_CTG[i];
                            }
                        }
                    }
                }
                if (znach instanceof NumberZ) {
                    for (int i = 0; i < TablZnachForCTG.length; i++) {
                        if (TablZnachForCTG[i] instanceof NumberZ) {
                            if (((NumberZ) znach).compareTo(TablZnachForCTG[i], ring) == 0) {
                                return TablGradFor_CTG[i];
                            }
                        }
                    }
                }
                if (znach instanceof Fraction) {
                    for (int i = 0; i < TablZnachForCTG.length; i++) {
                        if (TablZnachForCTG[i] instanceof Fraction) {
                            if (((Fraction) znach).compareTo(TablZnachForCTG[i], ring) == 0) {
                                return TablGradFor_CTG[i];
                            }
                        }
                    }
                }
                if (znach instanceof NumberR) {
                    for (int i = 0; i < TablZnachForCTG.length; i++) {
                        if (TablZnachForCTG[i] instanceof NumberR) {
                            if (((NumberR) znach).compareTo(TablZnachForCTG[i], ring) == 0) {
                                return TablGradFor_CTG[i];
                            }
                        }
                    }
                }
            }
            break;
        }
        return null;
    }

    /**
     * Вычисляет обратную тригонометрической функцию,результат в радианной мере
     *
     * @param znach значение, от которого нужно посчитать обратную функцию
     * @param funcNumb функция, для которой будем искать обратную
     *
     * @return значение обратной функции, выведенное в радианной мере
     */
    public Element arc_trigFunc_Rad(Element znach, int funcNumb,
            Ring ring) {
        NumberZ hh = (NumberZ) arc_trigFunc_Grad(znach, funcNumb, ring);
        if (hh == null) {
            switch (funcNumb) {
                case F.SIN:
                    return new F(F.ARCSIN, new Element[] {znach});
                case F.COS:
                    return new F(F.ARCCOS, new Element[] {znach});
                case F.TG:
                    return new F(F.ARCTG, new Element[] {znach});
                case F.CTG:
                    return new F(F.ARCCTG, new Element[] {znach});
                default:
                    return null;
            }
        }
        if (ring.RADIAN == Element.TRUE) {
            return new F(F.MULTIPLY, new Element[] {new Fraction(hh,
                new NumberZ(180)).cancel(ring),
                new Fname("\\pi")}).expand(ring);
        }
        return hh;
    }

    /**
     * извлечение квакдратного корня из целого числа
     *
     * @param z целое число
     *
     * @return если корень квадратный из данного числа целое число, то
     * возвращаем корень квадратный из данного числа, а если нет,
     * то   ВОЗВРАЩАЕМ null, если число NumberZ и
     * возвраща null в  остальных случаях.
     */
    public static Element sqrt(Element z) {
        if (!(z instanceof NumberZ) ){ return null;}
        if (z.isZero(Ring.ringZxyz)) return z;
            NumberR64 z_R64 = (NumberR64) z.toNumber(Ring.R64, new Ring("R64[x]"));
            NumberR64 sqrt_z_R64 = z_R64.powTheFirst(1, 2, new Ring("R64[x]"));
            NumberZ draft_sqrt = (NumberZ) sqrt_z_R64.toNumber(Ring.Z,
                    new Ring("Z[x]"));
            NumberZ draft_sqrt_pow2 = draft_sqrt.multiply(draft_sqrt);
            NumberZ delta
                    = (((NumberZ) z).subtract(draft_sqrt_pow2)).divide(draft_sqrt.add(draft_sqrt));
            NumberZ r = ((NumberZ) z).subtract(draft_sqrt_pow2);
            if (r.compareTo(NumberZ.ZERO) == 0) {
                return draft_sqrt;
            } else {
                if (delta.compareTo(NumberZ.ZERO) == 0) {
                    return null;
                }
            }
            if (delta.compareTo(NumberZ.ZERO) != 0) {
                while (delta.compareTo(NumberZ.ZERO) != 0) {
                    draft_sqrt = draft_sqrt.add(delta);
                    draft_sqrt_pow2 = draft_sqrt.multiply(draft_sqrt);
                    if (draft_sqrt_pow2.compareTo(z) == 0) {
                        return draft_sqrt;
                    } else {
                        delta = (((NumberZ) z).subtract(draft_sqrt_pow2)).
                                divide(draft_sqrt.add(draft_sqrt));
                    }
                }
                NumberZ c = draft_sqrt.subtract(NumberZ.ONE).
                        multiply(draft_sqrt.subtract(NumberZ.ONE));
                if (c.compareTo(z) == 0) {
                    return (draft_sqrt.subtract(NumberZ.ONE));
                } return null;
            } else   return draft_sqrt;
     }

    /**
     * извлекаем корень n-ой степени из целого числа
     *
     * @param z целое число
     * @param n та степень, корень которой мы хотим извлеч
     *
     * @return если корень n-ой степени из данного числа целое число, то
     * возвращаем корень n-ой степени из данного числа, а если нет, то функция
     * покажет это (НАДО ДУМАТЬ, ЧТО ВЕРНЕТСЯ null)
     */
    public static NumberZ rootN(NumberZ z, int n) {
        NumberR64 z_R64 = (NumberR64) z.toNumber(Ring.R64, new Ring("R64[x]"));
        NumberR64 sqrt_n_z_R64 = z_R64.powTheFirst(1, n, new Ring("Z[x]"));
        NumberZ draft_sqrt_n = (NumberZ) sqrt_n_z_R64.toNumber(Ring.Z,
                new Ring("Z[x]"));
        NumberZ g_n = NumberZ.ONE;
        NumberZ g = NumberZ.ONE;
        for (int i = 0; i < n; i++) {
            if (i == n - 1) {
                g_n = g;
            }
            g = g.multiply(draft_sqrt_n);
        }
        NumberZ draft_sqrt_pown_n = g;
        NumberZ delta = (z.subtract(draft_sqrt_pown_n)).
                divide(g_n.multiply(new NumberZ(n)));
        NumberZ r = z.subtract(draft_sqrt_pown_n);
        if (r.compareTo(NumberZ.ZERO) == 0) {
            return draft_sqrt_n;
        } else {
            if (delta.compareTo(NumberZ.ZERO) == 0) {
                return null;
            }
        }
        if (delta.compareTo(NumberZ.ZERO) != 0) {
            while (delta.compareTo(NumberZ.ZERO) != 0) {
                draft_sqrt_n = draft_sqrt_n.add(delta);
                g_n = NumberZ.ONE;
                g = NumberZ.ONE;
                for (int i = 0; i < n; i++) {
                    if (i == n - 1) {
                        g_n = g;
                    }
                    g = g.multiply(draft_sqrt_n);
                }
                draft_sqrt_pown_n = g;
                if (draft_sqrt_pown_n.compareTo(z) == 0) {
                    return draft_sqrt_n;
                } else {
                    delta = (z.subtract(draft_sqrt_pown_n)).
                            divide(g_n.multiply(new NumberZ(n)));
                }
            }
            g = NumberZ.ONE;
            for (int i = 0; i < n; i++) {
                g = g.multiply(draft_sqrt_n.subtract(NumberZ.ONE));
            }
            if (g.compareTo(z) == 0) {
                return (draft_sqrt_n.subtract(NumberZ.ONE));
            }
            return null;
        } else {
            return draft_sqrt_n;
        }
    }

    /**
     * возводим целое число в дробную степень
     *
     * @param z целое число
     * @param pNum числительдроби, в которую хотим возвести целое число
     * @param pDenom знаменатель дроби, в которую хотим возвести данное целое
     * число
     *
     * @return если результат возведения целое число, то выводим это число, а
     * если нет, программа это покажет
     */
    public static NumberZ powND(NumberZ z, int pNum, int pDenom) {
        NumberZ draft_sqrt_n = rootN(z, pDenom);
        NumberZ g = NumberZ.ONE;
        if (draft_sqrt_n == null) {
            return null;
        } else {
            for (int i = 0; i < pNum; i++) {
                g = g.multiply(draft_sqrt_n);
            }
        }
        return g;
    }

    public static Fraction powND(Fraction q, int k, int n) {
        Element num = null;
        Element denom = null;
        if (k * n < 0) {
            num = q.denom;
            denom = q.num;
        } else {
            num = q.num;
            denom = q.denom;
        }
        NumberZ num_Z = (NumberZ) num.toNumber(Ring.Z, Ring.ringR64xyzt);
        NumberZ denom_Z = (NumberZ) denom.toNumber(Ring.Z, Ring.ringR64xyzt);
        NumberZ num_Z_pow_k_n = powND(num_Z, Math.abs(k), Math.abs(n));
        NumberZ denom_Z_pow_k_n = powND(denom_Z, Math.abs(k), Math.abs(n));
        if (num_Z_pow_k_n == null || denom_Z_pow_k_n == null) {
            return null;
        }
        Fraction q_pow_k_n = new Fraction(num_Z_pow_k_n, denom_Z_pow_k_n);
        return q_pow_k_n;
    }

    /**
     * извлечение корня квадратного из данной дроби
     *
     * @param q дробь
     *
     * @return корень квадратный из данной дроби
     */
    public Element sqrt(Fraction q) {
        Element a = q.num;
        Element b = q.denom;
        Element c = sqrt(a);
        Element d = sqrt(b);
        if (c == null) {
            c = new F(F.SQRT, new Element[] {a});
        }
        if (d == null) {
            d = new F(F.SQRT, new Element[] {b});
        }
        if ((c instanceof NumberZ) && (d instanceof NumberZ)) {
            return new Fraction((NumberZ) c, (NumberZ) d);
        } else {
            return (d.isOne(inputRing)) ? c : new F(F.DIVIDE,
                    new Element[] {c, d});
        }
    }
}
