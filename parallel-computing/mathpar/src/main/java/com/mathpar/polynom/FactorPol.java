package com.mathpar.polynom;

import com.mathpar.func.F;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import com.mathpar.number.*;

/**
 * Description: Класс FactorPol является факторизованным представлением полинома
 * над некоторым полем (полем целых чисел) Коэффициэнты полинома - типа Element.
 *
 * Он состоих из массива степеней и полиномов Например (x+5)^2(x^2-5x-6)^5

 В классе реализованы операции *, /, ^, value, toPolynom и toString Так же
 имеются методы преобразования в факторизованные полиномы над другими кольцами
 и с другими коэффициэнтами.
 */
public class FactorPol extends Element {
  //  private static final long serialVersionUID = -4404752564620149248L;
    /**
     * массив полиномов, хранящийся в отсортированном виде. Сортировка
     * производится по возрастанию старших степеней.
     *
     * Например полиномы (x^6-8), (x^2+x-9), (x^12-9x^5+4x^2-8) будут храниться
     * в следующем порядке (x^2+x-9), (x^6-8), (x^12-9x^5+4x^2-8)
     */
    public Polynom[] multin;
    /**
     * массив спепеней, хранящийся в отсортированном виде. Сортировка
     * производится в зависимости от старших степеней масива полиномов и не
     * зависит от самих степеней
     */
    public int[] powers;
    public static final FactorPol ZERO
            = new FactorPol(new int[] {0}, new Polynom[] {Polynom.polynomZero});
    public static final FactorPol ONE
            = new FactorPol(new int[] {1},
            new Polynom[] {Polynom.polynomFromNot0Number(NumberZ.ONE)});

    public FactorPol() {
    }

    /**
     * конструктор от степеней и полиномов. Полиномы, передаваемые в него должны
     * стать внутренними полиномами факторизованного (свернутого) полинома
     *
     * @param powers массив степеней
     * @param multin массив полиномов
     */
    public FactorPol(int[] powers, Polynom[] multin) {
        this.multin = multin;
        this.powers = powers;
    }

    public FactorPol(Polynom pol) {
        this.multin = new Polynom[] {pol};
        this.powers = new int[] {1};
    }

    public FactorPol(Element a, Polynom pol, Ring ring) {
        this.multin = new Polynom[] {Polynom.polynomFromNumber(a, ring), pol};
        this.powers = new int[] {1, 1};
    }

    public FactorPol(Element a, Polynom pol, int deg, Ring ring) {
        this.multin = new Polynom[] {Polynom.polynomFromNumber(a, ring), pol};
        this.powers = new int[] {1, deg};
    }

    public FactorPol(Polynom pol, int deg) {
        this.multin = new Polynom[] {pol};
        this.powers = new int[] {deg};
    }

    public FactorPol(Element a, Ring ring) {
        if (a instanceof Polynom) {
            this.multin = new Polynom[] {((Polynom) a)};
            this.powers = new int[] {1};
        } else {
            this.multin = new Polynom[] {Polynom.polynomFromNumber(a, ring)};
            this.powers = new int[] {1};
        }
    }

    /**
     * Создание полинома при помощи случайной генерации. Для генерации каждого
     * внутреннего полинома вызывается генератор из класса PolynomZ
     *
     * @param max int - максимальная степень внутренних полиномов (передается в
     * конструктор PolynomR)
     * @param density int - разряженность внутренних полиномов (передается в
     * конструктор PolynomR)
     * @param nbits int - количество бит в коэффициэнтах внутреннего полинома
     * (передается в конструктор PolynomR)
     * @param count int - количество внутренних полиномов в внешнем (длина
     * массива полиномов и степеней)
     * @param difpow boolean - максимальная степень внешнего (свернутого)
     * полинома (если = 0, то степень = 1)
     */
    public FactorPol(int max, int density, int nbits, int count, int difpow, Ring r) {
        multin = new Polynom[count];
        powers = new int[count];
        for (int i = 0; i < count; i++) {
            multin[i] = new Polynom(new int[] {max, density, nbits},
                    new Random(), r);
            if (difpow > 0) {
                powers[i] = new Random().nextInt();
            } else {
                powers[i] = 1;
            }
        }
    }

    /**
     * конструктор от строки. Передается строковое представление полинома,
     * который должен быть представлени в правильном виде со всеми скобками и
     * степенями. Например (x-5)^2(x^4+8x^3-7)^4 //// возможно, что это только
     * для R64 ??????????????
     *
     * @param S строковое представление полинома
     */
    public FactorPol(String S, Ring ring) {
        int i = 0;
        int k = 0;
        int deep = 0;
        int start = 0;
        int pn = 0;
        int mn = 0;
        try {
            for (int j = 0; j < S.length(); j++) {
                if (S.charAt(j) == '(') {
                    i++;
                }
            }
            multin = new Polynom[i];
            powers = new int[i];
            i = 0;
            while (i < S.length()) {
                switch (S.charAt(i)) {
                    case '(':
                        switch (deep) {
                            case -1:
                                powers[pn] = new Integer(S.substring(start + 1, i)).
                                        intValue();
                                pn++;
                                start = i;
                                deep = 1;
                                break;
                            case 0:
                                if (i > 0) {
                                    powers[pn] = 1;
                                    pn++;
                                }
                                start = i;
                                deep = 1;
                                break;
                            default:
                                deep++;
                        }
                        break;
                    case ')':
                        deep--;
                        if (deep == 0) {
                            multin[mn] = new Polynom(S.substring(start + 1, i), ring);
                            mn++;
                        }
                        break;
                    case '^':
                        if (deep == 0) {
                            start = i;
                            deep--;
                        }
                        break;
                }
                i++;
            }
            if (deep == -1) {
                powers[pn] = new Integer(S.substring(start + 1, S.length())).
                        intValue();
            } else {
                powers[pn] = 1;
            }
            FactorPol P = sorting(powers, this.multin, ring);
            this.multin = P.multin;
            powers = P.powers;
        } catch (Exception ex) {
        }
    }

    /**
     * Единичный FactorPol = (1)^1
     */
    @Override
    public FactorPol myOne(Ring ring) {
        return new FactorPol(new int[] {1},
                new Polynom[] {multin[0].myOne(ring)});
    }

    public static FactorPol myOne(Polynom p, Ring ring) {
        Polynom one = p.myOne(ring);
        return new FactorPol(new int[] {1},
                new Polynom[] {one});
    }

    public static FactorPol myZero(Polynom p, Ring ring) {
        Polynom zero = p.myZero(ring);
        return new FactorPol(new int[] {1},
                new Polynom[] {zero});
    }

    /**
     * Нулевой FactorPol = (0)^1
     */
    @Override
    public FactorPol myZero(Ring ring) {
        return new FactorPol(new int[] {0}, new Polynom[] {Polynom.polynomZero});
    }

    /**
     * имея на входе 2 массива полиномов, возвращает массив FactorPol, в котором
     * 0: это числитель,1:это знаменатель. 1/x + 2/y + 3/ z
     * (yz)(2xz)(3xy)-числитель FactorPol (xyz)-знаменатель FactorPol
     *
     * @param num Polynom[]
     * @param denom Polynom[]
     *
     * @return FactorPol[]
     *
     * @author Roman
     */
    public static FactorPol[] resD(Polynom[] num, Polynom[] denom, Ring ring) {
        Element one = num[0].one(ring); //1
        Element zero = num[0].zero(ring); //0
        FactorPol[] factR = new FactorPol[2];
        int[] powDen = new int[denom.length];
        for (int i = 0; i < powDen.length; i++) {
            powDen[i] = 1;
        }
        int[] powNum = new int[num.length];
        for (int i = 0; i < powNum.length; i++) {
            powNum[i] = 1;
        }
        Polynom[] chis = new Polynom[num.length];
        int k = 0; //счетчик по массиву в числителе
        Polynom numer = new Polynom(new int[0], new Element[] {zero});
        factR[1] = new FactorPol(powDen, denom); //знаменатель в виде FactorPol
        for (int i = 0; i < num.length; i++) {
            Polynom pr = new Polynom(new int[0], new Element[] {one});
            if (denom[i] != num[0].zero(ring)) {
                for (int j = 0; j < denom.length; j++) {
                    pr = pr.mulSS(denom[j], ring); //вычисляем произведение в знаменателе
                }
                pr = pr.divideExact(denom[i], ring); //деление на своего
                Polynom spr = num[i].mulSS(pr, ring); //умножение на своего из числителя
                numer = numer.add(spr, ring); //суммирование
                chis[k] = spr;
            }
            k++;
        }
        factR[0] = new FactorPol(powNum, chis); //числитель в виде FactorPol
        return factR;
    }

    /**
     * мультипликативные операции с развернутым полиномом. Вызываются при
     * операциях умножения или деления на развернутый полином. Происходит
     * проверка, не является ли развернутый полином идентичным какому либо из
     * внутреннних полиномов свернутого. Если идентичен, то степень при нем
     * инкременируется (при делении - декреминируется). В противном случае он
     * дописывается к уже существующим
     *
     * @param P полином, участвующий в операции
     * @param powOfP степень, в которую возведен полином P
     *
     * @return резултат опреации
     */
    private Element multiply(Polynom P, int powOfP, Ring ring) {
        int[] pows = new int[] {powOfP};
        Polynom[] pol = new Polynom[] {P};
        FactorPol fpP = new FactorPol(pows, pol);
        return multiply(fpP, ring);
    }

    /**
     * мультипликативные опрерации с свернутым полиномом. Вызывается при
     * умножении или делении на свернутый полином. Все внутреннии полиномы
     * свернутого полинома-арумента последовательно проверяются на идентичность
     * со всеми полиномами класса. В случае обнаружения идентичности к степеням
     * при внутренних полиномах класса прибавляются степени полинома-аргумента.
     * Те внутреннии полиномы аргумента, идентичных к котроым не было обнаружено
     * записываются в конец массива класса
     *
     * @param P полином, участвующий в операции
     * @param operation тип операции
     *
     * @return резултат опреации
     */
    private FactorPol multyconvolute(FactorPol P, int operation, Ring ring) {
        Polynom[] _multin;
        int[] pows;
        List<Polynom> v = new ArrayList<Polynom>();
        int[] deg = new int[this.multin.length + P.multin.length];
        for (int i = 0; i < this.multin.length; i++) {
            v.add(this.multin[i]);
            deg[i] = this.powers[i];
        }
        int k = this.multin.length;
        int one = 0;
        for (int i = 0; i < P.multin.length; i++) {
            boolean eq = false;
            for (int j = 0; !eq && j < this.multin.length; j++) {
                if (P.multin[i].equals(this.multin[j])) {
                    deg[j] += operation * P.powers[i];
                    if (deg[j] == 0) {
                        one++;
                    }
                    eq = true;
                }
            }
            if (!eq) {
                v.add(P.multin[i]);
                deg[k] = operation * P.powers[i];
                k++;
            }
        }
        _multin = new Polynom[v.size() - one];
        pows = new int[v.size() - one];
        one = 0;
        for (int i = 0; i < _multin.length; i++) {
            if (deg[i] == 0) {
                one++;
            }
            _multin[i] = v.get(i + one);
            pows[i] = deg[i + one];
        }
        return sorting(pows, _multin, ring);
    }

    /**
     * умножение на развернутый полином. Вызывается метод multiply, в который
     * передается полином и 1
     *
     * @param P полином-множитель
     *
     * @return произведение полиномов
     */
    public Element multiply(Polynom P, Ring ring) {
        return multiply(P, 1, ring);
    }

    /**
     * умножение на свернутый полином. Вызывается метод multyconvolute, в
     * который передаются полином и 1, означающая, что при идентичности степени
     * будут складываться
     *
     * @param P полином-множитель
     *
     * @return произведение полиномов
     */
    @Override
    public Element multiply(Element b, Ring ring) { if(b.isOne(ring))return this;
        if (b.isItNumber()) {
            Polynom pp = (Polynom.polynomFromNumber(b.toNumber(ring.algebra[0], ring), ring));
            return multiply(pp, ring);
        }
        if (b instanceof Polynom) {
            return multiply((Polynom) b, 1, ring);
        }
        if (b instanceof FactorPol) {
            return multiply((FactorPol) b, 1, ring);
        }
        return  toPolynomOrFraction(ring).multiply(b, ring);
    }
    @Override
    public Element add(Element b, Ring ring) { 
        Element f=toPolynomOrFraction(ring);
        return f.add(b, ring);
    }
    @Override
        public Element subtract(Element b, Ring ring) { 
        Element f=toPolynomOrFraction(ring);
        return f.subtract(b, ring);
    }
    /**
     * умножение на свернутый полином. Вызывается метод multyconvolute, в
     * который передаются полином и 1, означающая, что при идентичности степени
     * будут складываться
     *
     * @param P полином-множитель
     *
     * @return произведение полиномов
     */
    public FactorPol multiply(FactorPol b, Ring ring) {
        int[][] pow = new int[1][0];
        Element[] res = com.mathpar.number.Array.jointUp(this.multin, b.multin, this.powers, b.powers, pow, ring);
        //  System.out.println(" After Join==  " +Array.toString(res)); // #####
        int l = res.length;
        Polynom one = b.multin[0].myOne(ring);
        int start = 0;
        if ((l > 1) && (res[1].signum() < 0)) {
            one = (Polynom) res[0].multiply(res[1], ring);
            if (one.isOne(ring)) {
                start = 2;
            } else {
                start = 1;
            }
        }
        Polynom[] res1 = new Polynom[l - start];
        int[] pow1 = new int[l - start];
        int j = 0;
        if (start == 1) {
            res1[0] = one;
            pow1[0] = 1;
            j = 1;
        }
        int i = (start == 0) ? 0 : 2;
        for (; i < l; i++) {
            res1[j] = (Polynom) res[i];
            pow1[j++] = pow[0][i];
        }
        return new FactorPol(pow1, res1);
    }

    public FactorPol divide(FactorPol b, Ring ring) {
        int bN = b.powers.length;
        int[] bb = new int[bN];
        for (int i = 0; i < bN; i++) {
            bb[i] = -b.powers[i];
        }
        return multiply(new FactorPol(bb, b.multin), ring);
    }

    /**
     * возведение в степень. Все вншние степени (степени при внутренних
     * полиномах) умножаются на factor
     *
     * @param factor показатель степени
     *
     * @return степень
     */
    @Override
    public FactorPol pow(int factor, Ring ring) {
        int[] pows = new int[this.powers.length];
        for (int i = 0; i < pows.length; i++) {
            pows[i] = this.powers[i] * factor;
        }
        return sorting(pows, multin, ring);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FactorPol ? equals((FactorPol) obj) : false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Arrays.deepHashCode(this.multin);
        hash = 73 * hash + Arrays.hashCode(this.powers);
        return hash;
    }

    @Override
    public boolean equals(Element x, Ring ring) {
        if (!(x instanceof FactorPol)) {
            return false;
        } else {
            FactorPol P = (FactorPol) x;
            if (P.multin.length == multin.length) {
                for (int i = 0; i < multin.length; i++) {
                    if (!multin[i].equals(P.multin[i], ring) || powers[i] != P.powers[i]) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * проверка равенства свернутых полиномов. Происходит последовательное
     * сравнение внутрених полиномов и степеней. При первой неидентичности
     * проверка прекращается и возвращается false. После полногй проверки
     * возвращается true.
     *
     * @param P сравниваемый полином
     *
     * @return результат сравнения
     */
    public boolean equals(FactorPol P) {
        if (P.multin.length == multin.length) {
            for (int i = 0; i < multin.length; i++) {
                if (!multin[i].equals(P.multin[i]) || powers[i] != P.powers[i]) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Нормальная форма для полиномов над полем. Числовой множитель может быть
     * только один - число со знаком и степенью 1.
     */
    public void normalFormInField(Ring ring) {
        normalForm(ring);// Приведем к общей нормальной форме, отсортируем полиномы
        int len = powers.length;
        if (len == 0) {
            return;//Пустой список возвращаем
        }
        Polynom first = multin[0];
        if (first.isZero(ring)) {
            return;
        }
        if (!first.isItNumber()) {
            return;//Если нет числовых сомножителей -выходим
        }
        int i = 0;
        Element one = multin[0].one(ring);
        Element num = one, denom = one;
        // Соберем числитель и знаменатель числового дробного сомножителя
        for (; i < len; i++) {
            if (multin[i].isItNumber()) {
                int power = powers[i];
                if (power < 0) {
                    denom = denom.multiply(multin[i].coeffs[0].pow(-power, ring), ring);
                } else {
                    num = num.multiply(multin[i].coeffs[0].pow(power, ring), ring);
                }
            } else {
                break;
            }
        }
        if (i == 1) {
            return;//Если только один числовой сомножитель -выходим
        }
        Element number = num.divide(denom, ring);
        int numbAdd = 0;
        if (!number.isOne(ring)) {
            numbAdd = 1;
        }
        int newLen = len - i + numbAdd;
        Polynom[] newMultin = new Polynom[newLen];
        int[] newPowers = new int[newLen];
        int j = 0; // pointer for new arrays
        if (numbAdd == 1) { // The first will be number "number"
            newMultin[0] = Polynom.polynomFromNumber(number, ring);
            newPowers[0] = 1;
            j = 1;
        }
        for (int m = i; m < len; m++) {
            newMultin[j] = multin[m];
            newPowers[j++] = powers[m];
        }
        this.multin = newMultin;
        this.powers = newPowers;
    }

    /**
     * Приведение к номальной форме с дробью: На первом и втором местах могут
     * стоять числитель со знаком в степени (+1) и знаменатель без знака в
     * степени (-1) для числовой дроби-сомножителя. Сомножители-полиномы
     * отсортированы по возростанию полиномов. Старший коэффициент у всех
     * полиномов положительный. Например: (-1)^1 (2)^{-1} (x)^{-3} (2x+3)^7
     * (6x^5+4)^{-3}....
     *
     */
    public void normalFormWithFraction(Ring ring) {
        normalForm(ring);// Приведем к общей нормальной форме, отсортируем полиномы
        int len = powers.length;
        if (len == 0) {
            return;//Пустой список возвращаем
        }
        Polynom first = multin[0];
        if (!first.isItNumber()) {
            return;//Если нет числовых сомножителей -выходим
        }
        int i = 0;
        Polynom one = Polynom.polynomFromNumber(multin[0].one(ring), ring);
        Polynom num = one, denom = one;
        // Соберем числитель и знаменатель числового дробного сомножителя
        for (; i < len; i++) {
            if (multin[i].isItNumber()) {
                int power = powers[i];
                if (power < 0) {
                    denom = denom.multiply((Polynom) multin[i].pow(-power, ring), ring);
                } else {
                    num = num.multiply((Polynom) multin[i].pow(power, ring), ring);
                }
            } else {
                break;
            }
        }
        if (i == 1) {
            return;//Если только один числовой сомножитель -выходим
        }         // Создадим новый FactorPol нужного размера
        int newLen = len - i;
        int numbAdd = 0;
        Fraction frac = new Fraction(num, denom);
        Element fracE = frac.cancel(ring); // сократим числовую дробь
        if (fracE instanceof Fraction) {
            num = (Polynom) ((Fraction) fracE).num;
            denom = (Polynom) ((Fraction) fracE).denom;
        } else {
            num = (Polynom) fracE;
            denom = ring.polynomONE;
        }
        if (num.compareTo(one, ring) != 0) {
            newLen++;
            numbAdd++;
        }
        if (denom.compareTo(one, ring) != 0) {
            newLen++;
            numbAdd += 2;
        }
        Polynom[] newMultin = new Polynom[newLen];
        int[] newPowers = new int[newLen];
        int j;
        // Запишем все числовые сомножители (1- num, 2- denom, 3 - num и denom)
        if (numbAdd == 1) {
            newMultin[0] = num;
            newPowers[0] = 1;
            j = 1;
        } else if (numbAdd == 2) {
            newMultin[0] = denom;
            newPowers[0] = -1;
            j = 1;
        } else {
            j = 2;
            if (num.compareTo(denom, ring) <= 0) {
                newMultin[0] = num;
                newPowers[0] = 1;
                newMultin[1] = denom;
                newPowers[1] = -1;
            } else {
                newMultin[0] = denom;
                newPowers[0] = -1;
                newMultin[1] = num;
                newPowers[1] = 1;
            }
        }
        // Перепишем все полиномы-сомножители
        for (int m = i; m < len; m++) {
            newMultin[j] = multin[m];
            newPowers[j++] = powers[m];
        }

        len = powers.length;
        if (len == 0) {
            return;// пустой список возвращаем
        }
        int sign = 1; // sign of the product
        // определяем знак и приводим старшие коеффициенты к положительному знаку.
        for (int pos = 0; pos < len; pos++) {
            if ((multin[pos].coeffs.length != 0)
                    && (multin[pos].coeffs[0].signum() < 0)) {
                multin[pos] = (Polynom) multin[pos].negate(ring);
                if ((powers[pos] & 1) == 1) {
                    sign *= (-1);
                }
            }
        }
        int[] nP = com.mathpar.number.Array.sortPosUp(multin, ring); // сортируем полиномы по возрастанию
        // Складываем степени у соседних одинаковых полиномов,
        // а позиции исчезающих полиномов помечаем (-1)

        int numberOfDeletedPol = 0;
        j = 0;
        int k;
        Polynom pOne = Polynom.polynomFromNumber(ring.numberONE, ring);
        while ((j < len)) {
            k = j + 1;
            int pos = nP[j];
            while ((k < len) && (multin[pos].equals(multin[nP[k]]))) {
                powers[pos] += powers[nP[k]];
                nP[k] = -1;/*
                 * G
                 */ numberOfDeletedPol++;
                k++;
            }
            j = k;
        }
        // Удалим единицу
        for (j = 0; j < len; j++) {
            int pos = nP[j];
            if (pos == -1) {
                continue;
            }
            int ppp = multin[pos].compareTo(pOne, ring);
            if (ppp < 0) {
                continue;
            } else if (ppp == 0) {
                nP[j] = -1;
                numberOfDeletedPol++;
            } else {
                break;
            }
        }
        // Создадим новый FactorPol нужного размера
        newLen = len - numberOfDeletedPol;
        if (sign == -1) {
            newLen++;
        }
        newMultin = new Polynom[newLen];
        newPowers = new int[newLen];
        int s = 0;// счетчик новых позиций сомножителей
        // На первую позицию поставим (-1), если знак минус
        if (sign == -1) {
            newMultin[0] = Polynom.polynomFromNumber(multin[0].one(ring).negate(ring), ring);
            newPowers[0] = 1;
            s++;
        }
        for (int m = 0; m < len; m++) // Перепишем все сомножители в порядке возрастания
        {
            if (nP[m] != -1) {
                newMultin[s] = multin[nP[m]];
                newPowers[s] = powers[nP[m]];
                s++;
            }
        }
        this.multin = newMultin;
        this.powers = newPowers;
    }

    /**
     * Удаление в multin 0 коэффициентов
     *
     * @param ring
     */
    public void deletZeroCoeffsInMultins(Ring ring) {
        for (int i = 0; i < multin.length; i++) {
            this.multin[i] = multin[i].deleteZeroCoeff(ring);
        }
    }

    /**
     * Приведение к номальной форме (самый общий случай):
     *
     * Все сомножители отсортированы по возрастанию, нет повторяющихся оснований
     * и нет сомножителя равного 1. На первом месте может стоять сомножитель
     * (-1) Старший коэффициент у всех сомножителей положительный. Например:
     * (-1)(2)^{-2} (x)^{-3} (2x+3)^7 (6x^5+4)^{-3}....
     *
     */
    public void normalForm(Ring ring) {
        if (multin.length == 1) {
            if (multin[0].isItNumber())  return;
            if (multin[0].coeffs[0].isNegative()) {
                Polynom p = (Polynom) multin[0].negate(ring);
                if (powers[0] % 2 == 0) {multin[0] = p;}
                else {
                    multin = new Polynom[2];
                    multin[0] = Polynom.polynom_one(ring.numberMINUS_ONE);
                    multin[1] = p;
                    int k = powers[0];
                    powers = new int[2];
                    powers[0] = k;//было 1
                    powers[1] = k;
                }

            }
            return;
        } //
        //System.out.println("==== ring.MachineEpsilonR64 " + ring.MachineEpsilonR64);
        int len = powers.length;
        if (len == 0)  return;// пустой список возвращаем
        int sign = 1; // sign of the product
        // определяем знак и приводим старшие коеффициенты к положительному знаку.
        for (int pos = 0; pos < len; pos++) {
            if ((multin[pos].coeffs.length != 0)
                    && (multin[pos].coeffs[0].signum() < 0)) {
                multin[pos] = (Polynom) multin[pos].negate(ring);
                if ((powers[pos] & 1) == 1) {
                    sign *= (-1);
                }
            }
        }
        int[] nP = com.mathpar.number.Array.sortPosUp(multin, ring); // тут хранятся новые позиции
        // сортируем полиномы по возрастанию
        // Складываем степени у соседних одинаковых полиномов,
        // а позиции исчезающих полиномов помечаем (-1)

        int numberOfDeletedPol = 0;
        int j = 0;
        int k;
        Polynom pOne = Polynom.polynomFromNumber(ring.numberONE, ring);
        // System.out.println("++++ ring.MachineEpsilonR64 " + ring.MachineEpsilonR64);
        while ((j < len)) {
            k = j + 1;
            int pos = nP[j];
            while ((k < len) && (multin[pos].equals(multin[nP[k]]))) {
                //        System.out.println("k = " + k);
                powers[pos] += powers[nP[k]];
                nP[k] = -1;/*
                 * G
                 */ numberOfDeletedPol++;
                k++;
            }
            j = k;
        }
        // Удалим единицу
        for (j = 0; j < len; j++) {
            int pos = nP[j];
            if (pos == -1) {continue;}
            if (multin[pos].isOne(ring)) {nP[j] = -1;
                numberOfDeletedPol++;        }
        }
        // Создадим новый FactorPol нужного размера
        int newLen = len - numberOfDeletedPol;
        if (sign == -1) {newLen++; }
        Polynom[] newMultin = new Polynom[newLen];
        int[] newPowers = new int[newLen];
        int s = 0;// счетчик новых позиций сомножителей
        // На первую позицию поставим (-1), если знак минус
        if (sign == -1) {
            newMultin[0] = Polynom.polynomFromNumber(multin[0].one(ring).negate(ring), ring);
            newPowers[0] = 1;
            s++;
        }
        for (int m = 0; m < len; m++) // Перепишем все сомножители в порядке возрастания
        {
            if (nP[m] != -1) {
                newMultin[s] = multin[nP[m]];
                newPowers[s] = powers[nP[m]];
                s++;
            }
        }
        this.multin = newMultin;
        this.powers = newPowers;
    }

    /**
     * сортировка по возрастанию внутренних полиномов. Полином выстраиваются в
     * зависимости от велечины внутренних полиномов, самые маленькие стоят в
     * начале. Сортировка не зависит от внешних степеней.
     *
     * @param powers массив степени
     * @param multin массив полиномов
     *
     * @return свернутый отсортированный полином
     */
    public static FactorPol sorting(int[] powers, Polynom[] multin, Ring ring) {
        Polynom p;
        int deg;
        for (int i = 0; i < multin.length; i++) {
            for (int j = i + 1; j < multin.length; j++) {
                if (multin[i].compareTo(multin[j], ring) == 1) {
                    p = new Polynom(multin[i].powers, multin[i].coeffs);
                    multin[i] = new Polynom(multin[j].powers, multin[j].coeffs);
                    multin[j] = new Polynom(p.powers, p.coeffs);
                    deg = powers[i];
                    powers[i] = powers[j];
                    powers[j] = deg;
                }
            }
        }
        return new FactorPol(powers, multin);
    }

    /**
     * Вычисление значения в точке. Для каждого внутренненего полинома
     * происходит вызов метода value из класса Polynom. Полученный результат
     * возводится во внешнюю степень. Результаты перемножаются
     *
     * @param X double число, в котором вычисляется значение
     * @param ring Ring
     *
     * @return double результат - значение полинома в точке
     */
    public NumberZ value(NumberZ X, Ring ring) {
        if (multin.length == 0) {
            return NumberZ.ZERO;
        }
        NumberZ f = NumberZ.ONE;
        for (int i = 0; i < multin.length; i++) {
            f = (NumberZ) f.multiply(multin[i].valueOf(X, ring).powTheFirst(powers[i], 1, ring), ring);
        }
        return f;
    }

    @Override
    public Element value(Element[] X, Ring ring) {
        if (multin.length == 0) {
            return ring.numberZERO;
        }
        Element f = ring.numberONE;
        for (int i = 0; i < multin.length; i++) {
            f = f.multiply(multin[i].value(X, ring).powTheFirst(powers[i], 1, ring), ring);
        }
        return f;
    }

    public Element value(Element X, Ring ring) {
        if (multin.length == 0) {
            return ring.numberZERO;
        }
        Element f = ring.numberONE;
        for (int i = 0; i < multin.length; i++) {
            f = f.multiply(multin[i].valueOf(X, ring).powTheFirst(powers[i], 1, ring), ring);
        }
        return f;
    }

    /**
     * конвертирование полинома
     *
     * @return полином в разложенном виде
     */
    public Element toPolynomOrFraction(Ring ring) {
        Polynom PN = Polynom.polynom_one(ring.numberONE);
        Polynom PD = Polynom.polynom_one(ring.numberONE);
        for (int i = 0; i < multin.length; i++) {
            if (powers[i] >= 0) {
                PN = PN.mulSS(multin[i].powBinS(powers[i], ring), ring);
            } else {
                PD = PD.mulSS(multin[i].powBinS(-powers[i], ring), ring);
            }
        } 
        return (PD.isOne(ring))? PN: (PD.isItNumber())? 
                PN.divideByNumber(PD.coeffs[0], ring): new Fraction(PN,PD);
    }

    @Override
    public FactorPol toNewRing(int Algebra, Ring r) {
        int len = this.multin.length;
        Polynom[] PB = new Polynom[len];
        Polynom[] mult = this.multin;
        for (int i = 0; i < len; i++) {
            PB[i] = (Polynom) (mult[i].toNewRing(Algebra, r));
        }
        return new FactorPol(this.powers, PB);
    }

    /**
     * вывод полинома в разложенном виде
     *
     * @return полином в строковом виде
     */
    @Override
    public String toString(Ring R) {
        String S = "";
        for (int k = 0; k < multin.length; k++) {
            if (powers[k] != 0) {
                S = S + '(' + multin[k].toString(R) + ')';
                if (powers[k] != 1) {
                    S = S + '^' + powers[k];
                }
            }
        }
        if (S.equals("")) {return "1";} else {return S;}
    }

    @Override
    public String toString() {
        return toString(Ring.ringR64xyzt);
    }

    /**
     * вывод полинома в разложенном виде после его разложения на линейные
     * сомножители и где коэффициенты есть Element типа - NumberC64
     *
     * @return полином в строковом виде
     *
     * @author Ribakov Mixail
     */
    public String toStringToFactorOfPol() {
        Ring R = null;
        try {
            R = new Ring("Z[x,y,z,t,p,v]");
        } catch (Exception ex) {
        }
        String S = "";
        for (int k = 0; k < multin.length; k++) {
            if (powers[k] != 0) {
                S = S + '(' + Polynom.toString(multin[k], R) + ')';
                if (powers[k] != 1) {
                    S = S + '^' + powers[k];
                }
            }
        }
        if (S.equals("")) {
            return "1";
        } else {
            return S;
        }
    }

    /**
     * возвращает модернизированный полином на вход подается FactorPol в котором
     * ищется равенство входящих в него полиномов если найдены идентичные, то
     * происходит перезапись одного и прибавление степени и уничтожение равного
     * ему
     *
     * @param factInput FactorPol
     *
     * @return FactorPol
     *
     * @author Ribakov Mixail
     */
    public static FactorPol correctRoundFP(FactorPol factInput, Ring ring) {
        int i = 0;
        int k = 0; //счетчик по количеству обнуленных полиномов
        while (i < factInput.multin.length) {
            if (factInput.multin[i] != null) { //если сам сравниваемый полином не null
                for (int j = 0; j < factInput.multin.length; j++) {
                    if ((factInput.multin[j] != null) && (i != j)
                            && factInput.multin[i].aproxEqual(factInput.multin[j], ring) == true) { //если полиномы идентичны
                        factInput.powers[i] += 1;
                        factInput.multin[j] = null;
                        factInput.powers[j] = 0;
                        k++;
                    }
                }
            }
            i++;
        }
        Polynom[] pol = new Polynom[k]; //массив полиномов
        int[] pow = new int[k]; //массив степеней полиномов
        for (int t = 0; t < k; t++) {
            if (factInput.multin[t] != null) { //если не null, то записываем полином и его степень в массивы
                pol[t] = factInput.multin[t];
                pow[t] = factInput.powers[t];
            }
        }
        FactorPol factOutput = new FactorPol(pow, pol); //создаем преобразованный полином
        // System.out.println("FactorPol после преобразования =>" +  factOutput.toString());
        return factOutput;
    }

    /**
     * на вход подаеются массивы из коэффициентов и FactorPol
     *
     * @param res1 int[]
     * @param fact1 FactorPol
     * @param res2 int[]
     * @param fact2 FactorPol
     *
     * @return FactorPol
     */
    public FactorPol makeTable(int[] res1, FactorPol fact1, int[] res2,
            FactorPol fact2, Ring ring) {
        int sh1 = 0;
        int sh2;
        int k = 0; //счетчик по null
        int klength = 0;
        for (int i = 0; i < fact1.multin.length; i++) { //по первому FactorPol
            sh2 = 0;
            for (int j = 0; j < fact2.multin.length; j++) { //по второму FactorPol
                if (fact2.multin[j] != null) {
                    if (fact1.multin[i].aproxEqual(fact2.multin[j], ring) == true) { //если результат сравнения 2-ух полиномов равен true
                        if (fact1.powers[i] > fact2.powers[j]) {
                            fact2.multin[j] = null; //обнуляем сравниваемый полином
                            for (int d = 0; d < fact2.powers[j]; d++) {
                                res1[sh1 + d] += res2[sh2 + d]; //его коэффициент прибавляем к 1
                                res2[sh2 + d] = -1; //обнуляем коэффициент сравниваемого полинома
                                k++;
                            }
                            klength++;
                        } else {
                            fact1.multin[i] = null; //обнуляем сравниваемый полином
                            for (int d = 0; d < fact1.powers[i]; d++) {
                                res2[sh2 + d] = res1[sh1 + d] + res2[sh2 + d]; //его коэффициент прибавляем к 1
                                res1[sh1 + d] = -1; //обнуляем коэффициент сравниваемого полинома
                                k++;
                            }
                            klength++;
                        }
                    }
                }
                sh2 += fact2.powers[j];
            }
            sh1 += fact1.powers[i];
        }
        int[] masCoeff = new int[res1.length + res2.length - k]; //массив коэффициентов
        int[] masCoeffs = new int[res1.length + res2.length]; //вспомогательный массив коэффициентов
        System.arraycopy(res1, 0, masCoeffs, 0, res1.length);
        System.arraycopy(res2, 0, masCoeffs, res1.length,
                res2.length);
        int h = 0;
        for (int i = 0; i < masCoeffs.length; i++) { //заполнение нового массива коэффициентов
            if (masCoeffs[i] != -1) {
                masCoeff[h] = masCoeffs[i];
                h++;
            }
        }
        Polynom[] polN = new Polynom[fact1.multin.length + fact2.multin.length]; //вспомогательный массив полиномов
        System.arraycopy(fact1.multin, 0, polN, 0, fact1.multin.length);
        System.arraycopy(fact2.multin, 0, polN, fact1.multin.length,
                fact2.multin.length);
        Polynom[] factPol = new Polynom[fact1.multin.length
                + fact2.multin.length - klength]; //массив полиномов
        int t = 0;
        for (int i = 0; i < polN.length; i++) { //заполнение нового массива полиномов
            if (polN[i] != null) {
                factPol[t] = polN[i];
                t++;
            }
        }
        int[] factPow = new int[fact1.powers.length + fact2.powers.length
                - klength]; //массив степеней полиномов
        int[] factPowN = new int[fact1.powers.length + fact2.powers.length]; //вспомогательный массив степеней полиномов
        System.arraycopy(fact1.powers, 0, factPowN, 0, fact1.powers.length);
        System.arraycopy(fact2.powers, 0, factPowN, fact1.powers.length,
                fact2.powers.length);
        int l = 0;
        for (int i = 0; i < factPowN.length; i++) { //заполнение нового массива степеней
            if (polN[i] != null) {
                factPow[l] = factPowN[i];
                l++;
            }
        }
        FactorPol factResult = new FactorPol(factPow, factPol);
        int dl = 0; //длина нового массива полиномов
        for (int i = 0; i < factResult.powers.length; i++) { //цикл по массиву степеней factResult
            dl += factResult.powers[i];
        }
        Polynom[] masPol = new Polynom[dl];
        int shift = 0;
        for (int i = 0; i < factResult.multin.length; i++) {
            Element one = factResult.multin[0].one(ring);
            Polynom pr = new Polynom(new int[0], new Element[] {one});
            for (int j = 0; j < factResult.powers[i]; j++) {
                pr = pr.mulSS(factResult.multin[i], ring);
                masPol[shift] = pr;
                shift++;
            }
        }

        return factResult;
    }

    /**
     * Возвращает преобразованный FactorPol//в случае если встречается
     * (x+1)...(x+4)...(x-0)...преобразует к виду -(x+1)...(x+4)...(x)...
     *
     * @return FactorPol
     */
    public FactorPol withZero(Ring ring) {
        int[] power = new int[powers.length];
        System.arraycopy(powers, 0, power, 0, powers.length);
        Polynom zero = multin[0].myZero(ring);
        Polynom[] pol = new Polynom[multin.length];
        int[] shift = new int[multin.length];
        for (int i = 0; i < multin.length; i++) {
            for (int j = 0; j < multin[i].coeffs.length; j++) {
                if (multin[i].coeffs[j].equals(zero.coeffs[0]) == true) {
                    shift[i] += 1;
                }
            }
        }
        for (int i = 0; i < multin.length; i++) {
            int k = 0;
            int[] pow = new int[multin[i].powers.length - shift[i]]; //степени полинома
            Element[] coeff = new Element[multin[i].coeffs.length - shift[i]]; //коэффициенты полинома
            for (int j = 0; j < multin[i].coeffs.length; j++) {
                if (multin[i].coeffs[j].equals(zero.coeffs[0]) != true) {
                    coeff[k] = multin[i].coeffs[j];
                    pow[k] = multin[i].powers[j];
                    k++;
                }
            }
            pol[i] = new Polynom(pow, coeff);
        }

        FactorPol fct = new FactorPol(power, pol);
        return fct;
    }

    public void sortFromMaxToMinRoot(int var, Ring ring) {
        Polynom temp;
        int deg;
        m1:
        for (int i = 0; i < multin.length; i++) {
            for (int j = i + 1; j < multin.length; j++) {
                if (this.multin[i].isItNumber() || this.multin[i].powers[var] > 1) {
                    continue m1;
                }
                if (this.multin[j].powers[var] > 1) {
                    temp = multin[i];
                    multin[i] = multin[j];
                    multin[j] = temp;
                    deg = powers[i];
                    powers[i] = powers[j];
                    powers[j] = deg;
                    continue m1;
                }
                if (this.multin[i].powers[var] == 1 && this.multin[j].powers[var] == 1) {
                    Element p1 = (this.multin[i].coeffs.length == 2)
                            ? this.multin[i].coeffs[1].negate(ring).divide(this.multin[i].coeffs[0], ring)
                            : ring.numberZERO;
                    Element p2 = (this.multin[j].coeffs.length == 2)
                            ? this.multin[j].coeffs[1].negate(ring).divide(this.multin[j].coeffs[0], ring)
                            : ring.numberZERO;
                    if (p1.compareTo(p2, ring) == -1) {
                        temp = multin[i];
                        multin[i] = multin[j];
                        multin[j] = temp;
                        deg = powers[i];
                        powers[i] = powers[j];
                        powers[j] = deg;
                    }
                }
            }
        }
    }

    /**
     * сортировка по возрастанию внутренних полиномов. Полином выстраиваются в
     * зависимости от велечины внутренних полиномов, самые маленькие стоят в
     * начале. Сортировка не зависит от внешних степеней.
     *
     * @param powers массив степени
     * @param pol массив полиномов
     *
     * @return свернутый отсортированный полином
     */
    public void sorting(Ring ring) {
        Polynom temp;
        int deg;
        for (int i = 0; i < multin.length; i++) {
            for (int j = 0; j < multin.length; j++) {
                if (multin[i].compareTo(multin[j], ring) == 1) {
                    /*
                     * p = new PolynomR(pol[i].powers, pol[i].coeffs); pol[i] =
                     * new PolynomR(pol[j].powers, pol[j].coeffs); pol[j] = new
                     * PolynomR(p.powers, p.coeffs);
                     */
                    temp = multin[i];
                    multin[i] = multin[j];
                    multin[j] = temp;
                    deg = powers[i];
                    powers[i] = powers[j];
                    powers[j] = deg;
                }
            }
        }
    }

    /**
     * мультипликативные опрерации с свернутым полиномом. Вызывается при
     * умножении или делении на свернутый полином. Все внутреннии полиномы
     * свернутого полинома-арумента последовательно проверяются на идентичность
     * со всеми полиномами класса. В случае обнаружения идентичности к степеням
     * при внутренних полиномах класса прибавляются степени полинома-аргумента.
     * Те внутреннии полиномы аргумента, идентичных к котроым не было обнаружено
     * записываются в конец массива класса multiply/divide (factorPol,int
     * operator)
     *
     * @param P полином, участвующий в операции
     * @param operation тип операции
     *
     * @return резултат опреации
     */
    public FactorPol mult_div1(FactorPol P, int operation, Ring ring) {
        Polynom[] pol;
        int[] pows;
        List<Polynom> v = new ArrayList<Polynom>();
        int[] deg = new int[this.multin.length + P.multin.length];
        for (int i = 0; i < this.multin.length; i++) {
            v.add(this.multin[i]);
            deg[i] = this.powers[i];
        }
        int k = this.multin.length;
        int one = 0;
        for (int i = 0; i < P.multin.length; i++) {
            boolean eq = false;
            for (int j = 0; !eq && j < this.multin.length; j++) {
                if (P.multin[i].equals(this.multin[j])) {
                    deg[j] += operation * P.powers[i];
                    if (deg[j] == 0) {
                        one++;
                    }
                    eq = true;
                }
            }
            if (!eq) {
                v.add(P.multin[i]);
                deg[k] = operation * P.powers[i];
                k++;
            }
        }
        pol = new Polynom[v.size() - one];
        pows = new int[v.size() - one];
        one = 0;
        for (int i = 0; i < pol.length; i++) {
            if (deg[i] == 0) {
                one++;
            }
            pol[i] = v.get(i + one);
            pows[i] = deg[i + one];
        }
        FactorPol d = new FactorPol(pows, pol);
        d.sorting(ring);
        return d;
    }

    public FactorPol multiply1(int pow, Polynom P, Ring ring) {
        int[] pows = new int[] {pow};
        Polynom[] pol = new Polynom[] {new Polynom(P.powers, P.coeffs)};
        FactorPol w = new FactorPol(pows, pol);
        return mult_div1(w, 1, ring);
    }

    public FactorPol multiply_1(Polynom P, Ring ring) {
        int pow = 1;
        return multiply1(pow, P, ring);
    }

    /**
     * преобразует FactorPol после того как он был получен после развала
     * некоторого полинома на простые сомножители
     *
     * @return FactorPol
     */
    public FactorPol prMNK(Ring ring) {
        //замена - на +
        for (int i = 0; i < multin.length; i++) {
            if (multin[i].coeffs.length != 1) { //если длина массива коэффициентов не равна 1 ...т.е. там не числовой полином
                if (multin[i].coeffs[1].isZero(ring) == false) { //коэффициент не равен 0
                    if ((multin[i].coeffs[1].compareTo(new Complex(NumberZ.ZERO, NumberZ.ZERO), ring) == -1
                            || (multin[i].coeffs[1].compareTo(new Complex(NumberZ.ZERO, NumberZ.ZERO), ring) == 1))) { //>0...<0
                        multin[i].coeffs[1] = multin[i].coeffs[1].negate(ring); //0-coeffs
                    }
                }
            }
        }
        return this;
    }

    /**
     * вывод полинома в разложенном виде
     *
     * @return полином в строковом виде
     */
    public String toString_1(Ring R) {
        String S = "";
        for (int k = 0; k < multin.length; k++) {
            if (powers[k] != 0) {
                S = S + '(' + multin[k].toString(R) + ')';
                if (powers[k] != 1) {
                    S = S + '^' + powers[k];
                }
            }
        }
        if (S.equals("")) {
            return "1";
        } else {
            return S;
        }
    }
/**
 *    ============111111+11   nen   null   11
 * @param f
 * @param ring
 * @return 
 */

    public int compareTo(FactorPol f, Ring ring) {
        if (multin.length > f.multin.length) {
            return 1;
        }
        if (multin.length < f.multin.length) {
            return -1;
        }
        if ((multin.length == f.multin.length) && (powers.length == f.powers.length)) {
            for (int i = multin.length - 1; i >= 0; i--) {
                if (multin[i].compareTo(f.multin[i], ring) == 0) {
                    if (powers[i] > f.powers[i]) {
                        return 1;
                    }
                    if (powers[i] < f.powers[i]) {
                        return -1;
                    }
                }
                int comp=multin[i].compareTo(f.multin[i], ring);
                if(comp!=0)return comp;
            }
        }
        return 0;
    }

    public int compareTo(Element o) {
        if (o instanceof FactorPol) {
            FactorPol f = (FactorPol) o;
            if (multin.length > f.multin.length) {
                return 1;
            }
            if (multin.length < f.multin.length) {
                return -1;
            }
            if ((multin.length == f.multin.length) && (powers.length == f.powers.length)) {
                for (int i = multin.length - 1; i >= 0; i--) {
                    if (multin[i].compareTo(f.multin[i]) == 0) {
                        if (powers[i] > f.powers[i]) {
                            return 1;
                        }
                        if (powers[i] < f.powers[i]) {
                            return -1;
                        }
                    }
                    if (multin[i].compareTo(f.multin[i]) == 1) {
                        return 1;
                    }
                    if (multin[i].compareTo(f.multin[i]) == -1) {
                        return -1;
                    }
                }
            }
            return 0;
        }
        return -1;
    }

    public int koeff() {
        int k = 0;
        for (int i = 0; i < multin.length; i++) {
            if (multin[i].powers.length == 0) {
                k = multin[i].coeffs[0].intValue();
            }
        }
        return k;
    }

    public int koeff_mas() {
        int k = 1;
        for (int i = 0; i < multin.length; i++) {
            if (multin[i].powers.length == 0) {
                k *= multin[i].coeffs[0].intValue();
            }
        }
        return k;
    }

    /**
     *
     * @return
     */
    public Polynom[] pol() {
        int k = 0;
        List<Polynom> arpol = new ArrayList<Polynom>();
        for (int i = 0; i < multin.length; i++) {
            if (multin[i].powers.length != 0) {
                arpol.add(multin[i]);
            }
        }
        Polynom[] pol = new Polynom[arpol.size()];
        for (int i = 0; i < pol.length; i++) {
            pol[i] = arpol.get(i);
        }
        return pol;
    }

    /**
     * возвращает FactorPol в котором только полиномы
     *
     * @return
     */
    public FactorPol polynom_Mas() {
        int k = 0;
        List<Polynom> arpol = new ArrayList<Polynom>();
        IntList arpow = new IntList();
        int j = 0;
        for (int i = 0; i < multin.length; i++) {
            if (multin[i].powers.length != 0) {
                arpol.add(multin[i]);
                arpow.arr[j] = powers[i];
                j++;
            }
        }
        Polynom[] pol = new Polynom[arpol.size()];
        int[] polpow = new int[arpow.arr.length];
        for (int i = 0; i < pol.length; i++) {
            pol[i] = arpol.get(i);
            polpow[i] = arpow.arr[i];
        }
        return new FactorPol(polpow, pol);
    }

    @Override
    public int numbElementType() {
        return com.mathpar.number.Ring.FactorPol;
    }

    @Override
    public Boolean isZero(Ring ring) {
        return ((multin.length==1)&&(multin[0].isZero(ring))||(multin.length==0))?true:false;
    }

 
    @Override
    public int compareTo(Element x, Ring ring) {
        if (x instanceof F) {
            return x.compareTo(this, ring);
        }
        return compareTo(((FactorPol) x), ring);
    }
/** Free term of numerator divided by free term of denominator (in general position)
 *  We get number if this element is number.
 * @param numberType -- the number type of resulting number
 * @param ring -- Ring
 * @return -- number
 */
    @Override
    public Element toNumber(int numberType, Ring ring) {
        int n=multin.length;     
        Element res=(n>0)? multin[0].toNumber(numberType, ring).pow(powers[0], ring):
                           Ring.zeroOfType(numberType) ;
        for (int i = 1; i < n; i++) {
           res=res.multiply(multin[i].toNumber(numberType, ring).pow(powers[i], ring),ring);
        }
        return res; 
    }

    @Override
    public Boolean isOne(Ring ring) {
        return ((multin.length==1)&&(multin[0].isOne(ring)))?true:false;
    }

        @Override
    public Element divideToFraction(Element p, Ring ring) {
        Element res=divide(p, ring);
        return (res==null)? new Fraction(this,p) : res;
    }

    @Override
    public Element divide(Element p, Ring ring) {
        if (p instanceof Polynom) {
            FactorPol fp = new FactorPol(new int[]{-1}, new Polynom[]{(Polynom) p});
            return multiply(fp, ring);
        } else if (p instanceof FactorPol) {
            return divide(((FactorPol) p), ring);
        } else {
            return this.toPolynomOrFraction(ring).divide(p, ring);
        }
    }
/** Make Fraction of two FactorPol
 *  Numerator of this fraction obtained with all multipliers with positive degree 
 *  Denominator of this fraction obtained with all multipliers with nrgative degree 
 * @param f
 * @param ring
 * @return 1) this, if not any negative powers
 *         2) Fraction of two FactorPol, which has no negative powers
 */
    public Element toFractionOfPositiveFPorFP( Ring ring) {FactorPol f=this;
        int k = 0;
        int h = 0;
        FactorPol facNum = null;
        FactorPol facDenum = null;
        for (int i = 0; i < f.multin.length; i++) {
            if (f.powers[i] < 0) { k++;
            } else { h++; }
        } if(k==0) return this;
        Polynom[] pol1 = new Polynom[k];
        int[] pow1 = new int[k];
        Polynom[] pol2 = new Polynom[h];
        int[] pow2 = new int[h];
        int kk = 0;
        int mm = 0;
        for (int i = 0; i < f.multin.length; i++) {
            if (f.powers[i] < 0) {
                pow1[mm] = -f.powers[i];
                pol1[mm] = f.multin[i];
                mm++;
            } else {
                pow2[kk] = f.powers[i];
                pol2[kk] = f.multin[i];
                kk++;
            }
        }
        FactorPol f1 = new FactorPol(pow1, pol1);
        FactorPol f2 = new FactorPol(pow2, pol2);
        Fraction ff = new Fraction(f2, f1);
        return ff;
    }

    /**
     * Удаляем сомножитель с индексом ind.
     * @param ind
     */
    public FactorPol deliteMultin(int ind) {
        Polynom[] pol = new Polynom[multin.length - 1];
        int[] pow = new int[powers.length - 1];
        int k = 0;
        for (int i = 0; i < multin.length; i++) {
            if (i != ind) {
                pol[k] = multin[i];
                pow[k] = powers[i];
                k++;
            }
        }
        return new FactorPol(pow, pol);
    }

    public FactorPol makeFactorPolWithOneMult(int ind) {
        return new FactorPol(multin[ind], powers[ind]);
    }
    /**
     * Разбивает FactorPol на 2 FactorPol 1 FactorPol - все полиномы в
     * положительных степенях 2 FactorPol - все полиномы в отрицательных
     * степенях
     *
     * @param fp
     *
     * @return
     */
    public FactorPol[] toNumDenom(FactorPol fp) {
        ArrayList<Element> pol1 = new ArrayList<Element>();//временное хранилище для полиномов
        IntList pow1 = new IntList();//временное хранилище для степеней полиномов

        ArrayList<Element> pol2 = new ArrayList<Element>();//временное хранилище для полиномов
        IntList pow2 = new IntList();//временное хранилище для степеней полиномов
        for (int i = 0; i < fp.multin.length; i++) {
            if (fp.powers[i] > 0) {
                pol1.add(fp.multin[i]);
                pow1.add(fp.powers[i]);
            } else {
                pol2.add(fp.multin[i]);
                pow2.add(fp.powers[i]);
            }
        }
        Polynom[] m1 = new Polynom[pol1.size()];
        pol1.toArray(m1);
        int[] pw1 = new int[pow1.size];
        System.arraycopy(pow1.arr, 0, pw1, 0, pow1.size);
        FactorPol fp1 = new FactorPol(pw1, m1);

        Polynom[] m2 = new Polynom[pol2.size()];
        pol2.toArray(m2);
        int[] pw2 = new int[pow2.size];
        System.arraycopy(pow2.arr, 0, pw2, 0, pow2.size);
        FactorPol fp2 = new FactorPol(pw2, m2);
        return new FactorPol[] {fp1, fp2};
    }

    public FactorPol deleteOne(Ring ring) {
        if (multin[0].isOne(ring)) {
            int length = multin.length - 1;
            Polynom[] multin1 = new Polynom[length];
            int[] pows = new int[length];
            System.arraycopy(powers, 1, pows, 0, length);
            System.arraycopy(multin, 1, multin1, 0, length);
            return new FactorPol(pows, multin1);
        } else {
            return this;
        }
    }

    /**
     * Собирает сопряженные
     *
     * @param ring
     *
     * @return
     */
    public FactorPol factor_notSOPR(Ring ring) {
        List<Polynom> arr = new ArrayList<Polynom>();
        IntList pow = new IntList();
        int k = 0;
        for (int i = 0; i < multin.length; i++) {
            for (int j = 0; j < multin.length; j++) {
                if (i != j) {
                    if ((multin[i] != null) && (multin[j] != null)) {
                        if ((multin[i] instanceof Polynom) && (multin[j] instanceof Polynom)) {
                            if ((!multin[i].isItNumber()) && (!multin[j].isItNumber())) {
                                if ((multin[i].coeffs.length == 2) && (multin[j].coeffs.length == 2)) {
                                    if (multin[i].coeffs[1] instanceof Complex && multin[j].coeffs[1] instanceof Complex) {
                                        Complex c1 = (Complex) multin[i].coeffs[1];
                                        Complex c2 = (Complex) multin[j].coeffs[1];
                                        if (!c1.im.isZero(ring) && !c2.im.isZero(ring)) {
                                            if (c1.re.equals(c2.re, ring)) {
                                                if ((c1.im).add(c2.im, ring).isZero(ring)) {
                                                    Polynom pp = multin[i].multiply(multin[j], ring).
                                                            deleteZeroCoeff(ring);
                                                    arr.add(pp);
                                                    pow.arr[k] = powers[i];
                                                    k++;
                                                    multin[i] = null;
                                                    multin[j] = null;
                                                }
                                            }
                                        } else {
                                            if (c1.im.isZero(ring)) {
                                                arr.add(multin[i]);
                                                pow.arr[k] = powers[i];
                                                k++;
                                                multin[i] = null;
                                            }
                                            if (c2.im.isZero(ring)) {
                                                arr.add(multin[j]);
                                                pow.arr[k] = powers[j];
                                                k++;
                                                multin[j] = null;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        for (int z = 0; z < multin.length; z++) {
            if (multin[z] != null) {
                arr.add(multin[z]);
                pow.arr[k] = powers[z];
                k++;
            }
        }
        Polynom[] b = new Polynom[arr.size()];
        arr.toArray(b);
        int[] p = new int[arr.size()];
        System.arraycopy(pow.arr, 0, p, 0, arr.size());
        FactorPol f = new FactorPol(p, b);
        return f;
    }

    /**
     * Производная от FactorPol -- сумма (F.ADD) FactorPol.
     *
     * @param num
     * @param r
     *
     * @return
     */
    @Override
    public Element D(int num, Ring r) {
        int nonZeroCnt = 0;
        int multinCnt = multin.length;
        // Assume that number is at the very beginning.
        boolean hasNumberMultin = multin[0].isItNumber();
        if (hasNumberMultin && multinCnt == 1) { // Only constant.
            return r.numberZERO;
        }
        Element[] tmpSumArgs = new Element[multinCnt];
        for (int i = hasNumberMultin ? 1 : 0; i < multinCnt; i++) {
            int[] tmpPowers;
            Polynom[] tmpMultins;
            Polynom deriv = multin[i].D(num, r).truncate();
            if (deriv.isZero(r))  continue;
            if ((powers[i] > 1)||(powers[i] < 0)){
                if (hasNumberMultin) {
                    tmpPowers = powers.clone();
                    tmpPowers[i]--;
                    tmpMultins = multin.clone();
                    tmpMultins[0] = multin[0].multiply(
                            new Polynom(NumberZ.valueOf(powers[i])), r);
                    tmpMultins[i] = deriv;
                } else {
                    tmpPowers = new int[multinCnt + 1];
                    System.arraycopy(powers, 0, tmpPowers, 1, multinCnt);
                    tmpPowers[0] = 1;
                    tmpPowers[i + 1]--;
                    tmpMultins = new Polynom[multinCnt + 1];
                    System.arraycopy(multin, 0, tmpMultins, 1, multinCnt);
                    tmpMultins[0] =deriv.multiplyByNumber(NumberZ.valueOf(powers[i]),r);
                  //  tmpMultins[i + 1] = deriv;
                }
            } else {
                tmpPowers = powers.clone();
                tmpMultins = multin.clone();
                tmpMultins[i] = deriv;
            }
            FactorPol tmpFactorPol = new FactorPol(tmpPowers, tmpMultins);
            tmpFactorPol.normalForm(r);
            tmpSumArgs[nonZeroCnt] = tmpFactorPol;
            nonZeroCnt++;
        }
        if (nonZeroCnt == 1) {
            return tmpSumArgs[0];
        }
        Element[] sumArgs = new Element[nonZeroCnt];
        System.arraycopy(tmpSumArgs, 0, sumArgs, 0, nonZeroCnt);
        return new F(F.ADD, sumArgs);
    }
/** is It Number?
 * 
 * @return true/false
 */
    @Override
    public boolean isItNumber() {
            for (int i = 0; i < multin.length; i++) {
                if(!multin[i].isItNumber()) return false;
            }
        return true;
    }
    @Override
    public boolean isItNumber(Ring ring) {return  isItNumber();}
    
    @Override
    public Element Im(Ring ring) {
        Element p=this.toPolynomOrFraction(ring);
        return p.Im(ring); }
     @Override
    public Element Re(Ring ring) {
        Element p=this.toPolynomOrFraction(ring);
        return p.Re(ring); }
 
    @Override
    public Element GCD(Element x, Ring ring) {
        Element th=this.toPolynomOrFraction(ring);
        Element xx=(x instanceof FactorPol)? ((FactorPol)x).toPolynomOrFraction(ring):x;
        return th.GCD(xx, ring);
    }

}
