package com.mathpar.polynom;

import com.mathpar.number.Array;
import java.util.Arrays;
import com.mathpar.number.Element;
import com.mathpar.number.Fraction;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;

/**
 * Факторизация полинома многих переменных
 *
 * @param Polynom p, 
 * @param Ring ringZxy
 *
 * @return FactorPol
 *
 * @author dmitry
 */
public class FactorizationManyVar {
 

    public static FactorPol Factor(Polynom q, Ring ringANYxy) {
        System.out.println("ring="+ringANYxy);
        // Найдем верхнюю границу для числа сомножителей --
        // их чило не может превышать суммы наибольших степеней переменных
        int[] maxPOws= q.degrees();//q.maxPowOfVars(ring);// в этом массиве старшие степени по каждой переменной
        int maxFactorNumb=2; // два числовых множителя ...
        for (int i = 0; i < maxPOws.length; i++) maxFactorNumb+=maxPOws[i]; // просуммируем их
        // теперь верхняя граница для числа сомножителей  в  переменной maxFactorNumb.
    //    int gh = q.powers.length / q.coeffs.length;
    //    Ring   R = ringZxy;
        Element gcdCoeffs = q.GCDNumPolCoeffs(ringANYxy);
        // НОД целочисленных коэффициентов полинома
        if ((q.coeffs[0]).isNegative()) gcdCoeffs = gcdCoeffs.negate(ringANYxy);
        boolean flagOf_One_gcdCoeffs = false;// false означает, что НОД=1
        int kl = 0;
        Polynom p= new Polynom();  //заводим новый
        //если НОД отличен от единицы, то поделим все коэф-ты полинома на НОД
        if (!gcdCoeffs.isOne(ringANYxy)) {
            kl++;
            flagOf_One_gcdCoeffs = true;  //НОД!=1
            p.coeffs=new Element[q.coeffs.length];
            p.powers=q.powers;
            for (int i = 0; i < q.coeffs.length; i++) {
                p.coeffs[i] = q.coeffs[i].divide(gcdCoeffs, ringANYxy);
            }
        } else {
           boolean field = (ringANYxy.algebra[0]&(Ring.Complex-1))>=Ring.Q;
           if (field){ gcdCoeffs=q.coeffs[0];
             if(!gcdCoeffs.isOne(ringANYxy)){  kl++;
                 flagOf_One_gcdCoeffs = true;  //НОД!=1
                 p=q.divideByNumber(gcdCoeffs, ringANYxy);
             }
           } if (!flagOf_One_gcdCoeffs) p=q ; }
        /*
         * предварительное разложение на множители, получаем множители имеющие
         * разные наборы переменных среди них ищем кратные множители, на выходе
         * получаем множители имеющие различные наборы переменных и различные
         * кратности
         */
        FactorPol poliFactor[] = //FSF(p, ringANYxy);
            new FactorPol[]{ p.FactorPol_SquareFree(ringANYxy)};

        
        
        Polynom rez1[] = new Polynom[maxFactorNumb];
        int step[] = new int[maxFactorNumb];
        /*
         * разложим каждый из полученных полиномов на множителей, которые  
         * не будут иметь кратных сомножителей
         */
        for (int i = 0; i < poliFactor.length; i++) {
            for (int u = 0; u < poliFactor[i].multin.length; u++) {
                // newVarLength---число переменных полинома
               int newVarLength = poliFactor[i].multin[u].powers.length / poliFactor[i].multin[u].coeffs.length;
                int newVar[] = new int[newVarLength];
             //   Arrays.fill(newVar, -1);
                Polynom s = poliFactor[i].multin[u].denseVariables(newVar);
                //создаем кольцо от тех переменных, которые присутствуют в полиноме
                Ring newRingANYxy =   new Ring(ringANYxy, newVar); //  2016
                if (newRingANYxy.varNames.length == 1) {
                    /*
                     * если полученный полином от одной переменной, факторизуем
                     * его с помощью факт-и для одной переменной
                     */
                    FactorPol pol = Factorization.factorOneVar(s, newRingANYxy); // #############################
                    for (int j = 0; j < pol.multin.length; j++) {
                        if(!pol.multin[j].isItNumber()){
                        rez1[kl] = pol.multin[j].danceVariableBack(newVar);
                        step[kl] = pol.powers[j] * poliFactor[i].powers[u];
                        kl++;}
                    }
                } else {
                 //   System.out.println("s="+s.toString(newR));
                    //факторизация свободного от квадратов полинома многих переменных
                    Polynom pol[] = PoliFactorPol(s, newRingANYxy); // ########################################
                    for (int j = 0; j < pol.length; j++) {
                        if(!pol[j].isItNumber()){
                        rez1[kl] =pol[j].danceVariableBack(newVar);
                        step[kl] = poliFactor[i].powers[u];
                        kl++;}
                    }
                }
            }
        }
        //сборка результата
        Polynom multin[] = new Polynom[kl];
        int powers[] = new int[kl];
        if (flagOf_One_gcdCoeffs) {
            /*
             * если есть числовой множитель отличный от единицы, то запишем его
             * как полином и поместим на первое место
             */
            multin[0] = new Polynom(new int[0], new Element[] {gcdCoeffs});
            powers[0] = 1;
            System.arraycopy(rez1, 1, multin, 1, kl - 1);
            System.arraycopy(step, 1, powers, 1, kl - 1);
        } else {
            System.arraycopy(rez1, 0, multin, 0, kl);
            System.arraycopy(step, 0, powers, 0, kl);
        }       
        return new FactorPol(powers, multin);
    }

    /**
     * Факторизация свободного от квадратов полинома многих переменных,
     *  
     *
     * @param Polynom p, Ring R
     *
     * @return Polynom []  массив полиномов-сомножителей для данного полинома
     */
    public static Polynom[] PoliFactorPol(Polynom p, Ring ringANYxy) {
        if(p.isItNumber())return new Polynom[] {p};
        int len = p.powers.length / p.coeffs.length;
        //Находим максимальные степени переменных полинома р, запишем их в массив maxPowersPol
        int maxPowersPol[] = p.degrees();
        // razmer - число точек для подстановки в полином p, для получения полиномов от одной переменной
        int razmer = 1;
        for (int i = len - 2; i >= 0; i--) {razmer *= maxPowersPol[i] + 1; }

        int numPoints[][] = new int[razmer][len - 1]; //массив в котором хранятся точки для подстановки в полином р
        //x - массив полиномов от одной переменной, полученный путем подстановки строк массива numPoints в полином р
        Polynom x[][];
        try { //процедура заполнения массивов numPoints, x
            x = CreateMassPol.Method(p, numPoints, ringANYxy); // ##################################################
        } catch (Exception e) {/* если вышли по ошибке, то в результате разложения полинома от
             * одной переменной нашелся полином с одним множителем,
             * следовательно полином р не разложим на множители и его возвращаем   */
          System.out.println(" PoliFactorPol hasError class Factorizmany vars e="+e);
           return new Polynom[]{p};
        }
        int n = 1;
        int n2 = 1;
        int measure = x[0].length;
        Polynom otvet[] = null;
        for (int i = len - 2; i >= 0; i--) {
            razmer /= (maxPowersPol[i] + 1);//число блоков полиномов для подъема
           /*
             * берем блок полиномов для подъема, из него восстановим один
             * полином в котором на одну переменную больше
             */
            Polynom blockPol[][] = new Polynom[maxPowersPol[i] + 1][measure];//маcсив в который будем записывать полиномы составляющие блок
            n *= maxPowersPol[i] + 1;
            for (int j = 0; j < razmer; j++) {
                Element maspodst[] = new Element[maxPowersPol[i] + 1]; //числа для формулы Лагранжа
                Element ff_podst[] = new Element[i];
                /*
                 * ff_podst - массив чисел который будем использовать для
                 * проверки восстановленного полинома(является ли он множителем
                 * первоночального или нет)
                 */
        
                for (int k = 0; k < i; k++) ff_podst[k] = new NumberZ(numPoints[j * n][k]);   
                for (int k = 0; k < 10; k++) {
                    
                }
                for (int k = 0; k < maxPowersPol[i] + 1; k++) maspodst[k] = new NumberZ(numPoints[j * n + k * n2][i]);               
                //заполнение блока полиномами
                for (int k = 0; k < maxPowersPol[i] + 1; k++) System.arraycopy(x[k * n2 + j * n], 0, blockPol[k], 0, measure);                
                Polynom p_clone = (Polynom) p.clone();
         /* */  Polynom lag[] = Lagrang_new(maspodst, ringANYxy, i);// получение числителя в формуле Лагранжа
                    //массив который будем подставлять в полином для проверки на множитель
                Element new_mas_x[] = new Element[ringANYxy.varNames.length];
                System.arraycopy(ff_podst, 0, new_mas_x, 0, ff_podst.length);
                for (int g = ff_podst.length; g < ringANYxy.varNames.length; g++) { 
                    new_mas_x[g] = ringANYxy.varPolynom[g];
                }
                //получение значения полинома в точке
                Polynom ps = (Polynom) p_clone.value(new_mas_x, ringANYxy);
                Polynom far[] = ProverkaMatrix(blockPol, lag, ps, ringANYxy);
                System.arraycopy(far, 0, x[j * n], 0, far.length);
                otvet = far;
            }
            n2 *= maxPowersPol[i] + 1;
        }
        return otvet;
    }

    /**
     * Нахождение НОК чисел Element znam[]
     *
     * @param Element znam[], Ring R
     *
     * @return nok
     */
    public static Element NOK(Element znam[], Ring R) {
        Element nok = znam[0];
        for (int i = 0; i < znam.length - 2; i++) {
            nok = (nok.multiply((znam[i + 1]), R).divide((nok).GCD(znam[i + 1], R), R));
        }
        return nok;
    }
 
    /*
     * Процедура перебора и восстановления полиномов
     */

    private static Polynom[] ProverkaMatrix(Polynom y1[][], Polynom z[], Polynom inPol, Ring R) {       
        int k = y1[0].length;                        //кол-во столбцов
        int kolstrok = y1.length;
        int sIndesMain = k;
        int c[] = new int[kolstrok];
        Polynom mnog[] = new Polynom[kolstrok];
        Polynom rez[] = new Polynom[k];
        int p = 0;
        while (sIndesMain >= 0) { 
            for (int i = 0; i < kolstrok; i++) {
                mnog[i] = y1[i][c[i]];            // по c[] формируем массив полиномов
            //    System.out.print ("("+i+")"+c[i]);
            } // System.out.println();
       //     Polynom d;
            Polynom polynomXY = null;
            boolean flak = false;
            //проверка может ли данный блок полиномов быть поднят
            boolean flags = proove(mnog);
            if (flags) {
              //  d = creat_new_polynom(mnog, z, R);// процедура восстановления. имеем массив полиномов(из них получаем коэф-ты) и полиномы лагранжа
                polynomXY = creat_new_polynom(mnog, z, R).deleteZeroCoeff(R);
                try {
//                    System.out.println("ff="+ff);
//                         System.out.println("error="+error);
//                         System.out.println("R="+R);
                    Polynom g[] = inPol.divAndRem(polynomXY, R);
  //                  System.out.println("g="+Array.toString(g));
                    if (g[1].isItNumber())   flak = true;
                } catch (Exception e) { // System.out.println("ERROR==========="+e);
                flak = false;}
            }
            if (flak) {rez[p] = polynomXY;
                /*
                 * Найденные множители ставим в последний столбец, с целью
                 * уменьшения числа переборов множителей.
                 */
 // temp               y1 = replace(y1, c, p);
                p++;
                if (p == (k - 1)) {
                    rez[p] = inPol;
                    for (int jj = 0; jj < p; jj++)   rez[p] = rez[p].divideExact(rez[jj], R);
                //    System.out.println("out HERE 00");
                    return rez;
                }
                for (int ii = 0; ii < c.length; ii++)  c[ii]+= 1;
            }
            if (c[0]== c.length){ //System.out.println("out HERE 01");  
                return rez;}
//            if ((k - 1 - p) != 0) {
//                while (s >= 0) {
//                    if (s - 1 < 0) {break;}
//                    if (c[s - 1] == (k - 1 - p)) { s--;} else { break;}
//                }
//                if (s - 1 < 0) {break;}
//                c[s - 1] += 1; s++;
//                while (s <= kolstrok) {c[s - 1] = 0; s++;}  s--;
//            }
        sIndesMain--;} // System.out.println("out HERE 02");
        return rez;
    }

    /**
     * Найденные множители ставим в последний столбец, с целью уменьшения числа
     * переборов множителей.
     *
     * @param Polynom mas[][], int x[], int s
     *
     * @return Polynom mas [][]
     */
    private static Polynom[][] replace(Polynom[][] mas, int[] x, int s) {
        int len = mas[0].length;
        for (int i = 0; i < x.length; i++) {
            if (x[i] != len - 1 - s) {
                Polynom u = mas[i][x[i]];
                mas[i][x[i]] = mas[i][len - 1 - s];
                mas[i][len - 1 - s] = u;
            }
        }
        return mas;
    }

    /**
     * На вход даем массив элементов x[], по этому массиву создаем числитель в
     * формуле Лагранжа(это полининомы rezalt[]) и знаменатель (это числа
     * znam[]). Массив lagr содержит полиномы с помощью которых мы будем
     * восстанавливать исходный полином, и на последнем месте стоит gcd
     * элементов из znam[]
     *
     * @param Element x[], Ring R
     *
     * @return Polynom lagr[]
     */
    public static Polynom[] Lagrang_new(Element x[], Ring R, int index) {
        int length = x.length;
        NumberZ v = NumberZ.ONE;
        Polynom rezalt[] = new Polynom[length];
        Element znam[] = new Element[length + 1];
        Arrays.fill(znam, NumberZ.ONE);
        if (length == 2) {
            int p[] = {1, 0};
            Element d[] = {v, x[1].negate(R)};
            Element d1[] = {v, x[0].negate(R)};
            rezalt[0] = new Polynom(p, d);
            rezalt[1] = new Polynom(p, d1);
            znam[0] = x[0].subtract(x[1], R);
            znam[1] = x[1].subtract(x[0], R);
        } else {
            for (int i = 0; i < length; i++) {
                Element n[] = new Element[length - 1];
                if (i == 0) {
                    System.arraycopy(x, 1, n, 0, length - 1);
                } else {
                    System.arraycopy(x, 0, n, 0, i);
                    System.arraycopy(x, i + 1, n, i, length - i - 1);
                }
                rezalt[i] = multiply_D(n, R, index);
                for (int j = 0; j < length; j++) {
                    if (x[j] != x[i]) {
                        znam[i] = znam[i].multiply((x[i]).subtract(x[j], R), R);
                    }
                }
            }
        }
        Element gcd = NOK(znam, R);
        Polynom lagr[] = new Polynom[length + 1];
        for (int i = 0; i < length; i++) {
            lagr[i] = (Polynom) (gcd.divide(znam[i], R)).multiply(rezalt[i], R);
        }
        lagr[length] = new Polynom(gcd);
        return lagr;
    }

    /**   ????????????????????????????  !!!!
     * Быстрое умножение полиномов, не используем умножение на первый моном.
     * Используется для получения полиномов вида: (x-a)(x-b)(x-c)...(x-n)
     *
     * @param массив Element poradok[]{a,b,c,...,n}, Ring R
     *
     * @return Polynom
     */
    public static Polynom multiply_D(Element[] poradok, Ring R, int index) {
        int length = poradok.length;
        for (int i = 0; i < length; i++) {
            poradok[i] = poradok[i].negate(R);
        }
        Element coeffs[] = null;
        Element power[];
        for (int i = 0; i < length - 1; i++) {
            if (i == 0) {
                power = new Element[2];
                power[0] = NumberZ.ONE;
                power[1] = poradok[0];
                coeffs = new Element[i + 3];
                Arrays.fill(coeffs, NumberZ.ZERO);
                System.arraycopy(power, 0, coeffs, 0, 2);
                for (int j = 0; j < 2; j++) {
                    power[j] = power[j].multiply(poradok[i + 1], R);
                }
                for (int j = 1; j < i + 3; j++) {
                    coeffs[j] = coeffs[j].add(power[j - 1], R);
                }
            } else {
                power = new Element[i + 2];
                System.arraycopy(coeffs, 0, power, 0, i + 2);
                coeffs = new Element[i + 3];
                Arrays.fill(coeffs, NumberZ.ZERO);
                System.arraycopy(power, 0, coeffs, 0, power.length);
                for (int j = 0; j < i + 2; j++) {
                    power[j] = power[j].multiply(poradok[i + 1], R);
                }
                for (int j = 1; j < i + 3; j++) {
                    coeffs[j] = coeffs[j].add(power[j - 1], R);
                }
            }
        }
        int powers[] = new int[length + 1];
        for (int i = length; i >= 0; i--) {
            powers[length - i] = i;
        }
        int new_powers[] = new int[(index + 1) * coeffs.length];
        for (int i = 0; i < powers.length; i++) {
            new_powers[index + (index + 1) * i] = powers[i];
        }
        return new Polynom(new_powers, coeffs);
    }

    /**
     * Получение полинома путем подстановки в полином х чисел из массива m[].
     * После восстановления мы получим некоторый полином, который должны
     * проверить(является ли этот полином множителем исходного?) для этого мы
     * поделим полином полученный с помощью этой процедуры на восстановленный.
     *
     * @param Element m[], Polynom x, Ring R
     *
     * @return Polynom
     */
    public static Polynom calc(Element m[], Polynom x, Ring R) {
        int h = m.length;
        int number = R.varNames.length;
        for (int i = 0; i < h; i++) {
            int u = 0;
            for (int j = i; j < x.powers.length; j += number) {
                if (x.powers[j] != 0) {
                    x.coeffs[u] = x.coeffs[u].multiply(m[i].pow(x.powers[j], R), R);
                    x.powers[j] = 0;
                }
                u++;
            }
        }
        int pow[] = new int[(number - h) * x.coeffs.length];
        int y = 0;
        for (int i = h; i < number; i++) {
            int u = y;
            for (int j = i; j < x.powers.length; j += number) {
                pow[u] = x.powers[j];
                u += number - h;
            }
            y++;
        }
        return term(pow, x.coeffs, number - h, R);//new Polynom(new Polynom(pow,x.coeffs).toString());
    }

    public static Polynom term(int pow[], Element x[], int h, Ring R) {
        int y = 0;
        for (int i = 0; i < x.length; i++) {
            if (pow[i * h] == -1) {
                continue;
            }
            int m[] = new int[h];
            for (int j = 0; j < h; j++) {
                m[j] = pow[i * h + j];
            }
            for (int j = i + 1; j < x.length; j++) {
                if (pow[j * h] == -1) {
                    continue;
                }
                boolean flag = true;
                for (int u = 0; u < h; u++) {
                    if (m[u] != pow[j * h + u]) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    x[i] = x[i].add(x[j], R);
                    for (int u = 0; u < h; u++) {
                        pow[j * h + u] = -1;
                    }
                    y++;
                }
            }
        }
        int new_powers[] = new int[(x.length - y) * h];
        Element new_coeffs[] = new Element[x.length - y];

        if (y == 0) {
            return new Polynom(pow, x);
        } else {
            int u = 0;
            for (int i = 0; i < pow.length; i++) {
                if (pow[i] != -1) {
                    new_powers[u] = pow[i];
                    if (u % h == 0) {
                        new_coeffs[u / h] = x[i / h];
                    }
                    u++;
                }
            }
        }
        return new Polynom(new_powers, new_coeffs);
    }

    /**
     * Процедура восстановления полинома, на входе имеем массив f, и массив
     * mnog[] полиномов Лагранжа.
     *
     * @param Polynom f[], Polynom mnog[], Ring R
     *
     * @return Polynom pol_rez
     */
    private static Polynom creat_new_polynom(Polynom[] f, Polynom mnog[], Ring R) {
        int length = f.length;
        int h = f[0].powers.length / f[0].coeffs.length;
        int power[] = new int[h];
        Element coef[] = new Element[length];
        Polynom pol_rez = new Polynom(new int[0], new Element[] {NumberZ.ZERO});
        for (int i = 0; i < length; i++) {
            while (f[i].powers.length != 0) {
                Arrays.fill(coef, NumberZ.ZERO);
                System.arraycopy(f[i].powers, 0, power, 0, h);
                coef[i] = f[i].coeffs[0];
                int p[] = new int[f[i].powers.length - h];
                System.arraycopy(f[i].powers, h, p, 0, f[i].powers.length - h);
                Element coefd[] = new Element[f[i].coeffs.length - 1];
                System.arraycopy(f[i].coeffs, 1, coefd, 0, f[i].coeffs.length - 1);
                //обрезать полином
                if (f[i].coeffs.length == 1) {
                    f[i] = Polynom.polynom_zero(NumberZ.ONE);
                } else {
                    f[i] = new Polynom(p, coefd);
                }
                for (int j = i + 1; j < length; j++) {

                    int v = -1;
                    if (f[j].powers.length == 0) {
                    } else {
                        for (int u = 0; u < f[j].coeffs.length; u++) {
                            boolean flag = true;
                            for (int m = 0; m < h; m++) {
                                if (power[m] != f[j].powers[m + u * h]) {
                                    flag = false;
                                    break;
                                }
                            }
                            if (flag) {
                                v = u;
                                break;
                            }
                        }
                    }
                    if (v != -1) {
                        coef[j] = f[j].coeffs[v];
                        if (f[j].coeffs.length == 1) {
                            f[j] = new Polynom(new int[0], new Element[] {NumberZ.ZERO});
                        } else {
                            int p1[] = new int[f[j].powers.length - h];
                            Element coefd1[] = new Element[f[j].coeffs.length - 1];
                            if (v == 0) {
                                System.arraycopy(f[j].powers, h, p1, 0, f[j].powers.length - h);
                                System.arraycopy(f[j].coeffs, 1, coefd1, 0, f[j].coeffs.length - 1);
                            } else {
                                if (v == (f[j].coeffs.length - 1)) {
                                    System.arraycopy(f[j].powers, 0, p1, 0, f[j].powers.length - h);
                                    System.arraycopy(f[j].coeffs, 0, coefd1, 0, f[j].coeffs.length - 1);
                                } else {
                                    System.arraycopy(f[j].powers, 0, p1, 0, v * h);
                                    System.arraycopy(f[j].powers, (v + 1) * h, p1, h * v, f[j].powers.length - (v + 1) * h);
                                    System.arraycopy(f[j].coeffs, 0, coefd1, 0, v);
                                    System.arraycopy(f[j].coeffs, v + 1, coefd1, v, f[j].coeffs.length - (v + 1));
                                }
                            }

                            f[j] = new Polynom(p1, coefd1);
                        }
                    }
                }
                Polynom rez = Formyla(coef, mnog, R);
                Polynom v = create(power, rez, R);
                v = v.deleteZeroCoeff(R);
                pol_rez = pol_rez.add(v, R);

            }
        }
        return pol_rez;
    }

    /**
     * получение полинома Лагранжа, который потом будем подставлять вместо
     * коэф-та
     *
     * @param Element coef[] - массив коэф-в, Polynom mnog[] - полиномы формулы
     * Лагранжа, Ring R
     *
     * @return s-полином Лагранжа
     */
    public static Polynom Formyla(Element coef[], Polynom mnog[], Ring R) {
        Polynom z1[] = new Polynom[mnog.length];
        for (int i = 0; i < mnog.length; i++)  z1[i] = (Polynom) mnog[i].clone();
        int length = coef.length;
        //умножаем полиномы Лагранжа на числа из coef[]
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < z1[i].coeffs.length; j++) {
                z1[i].coeffs[j] = z1[i].coeffs[j].multiply(coef[i], R);
            }
        }
        Polynom s = z1[0];
        for (int i = 1; i < length; i++)   s = s.add(z1[i], R);
        s = s.divideExact(mnog[length], R);
        return s;
    }

    /**
     * Процедура подстановки восстан-го полинома вместо коэф-та выбранного
     * монома
     */
    private static Polynom create(int[] pow_m, Polynom pol, Ring R) {
        Element g[] = {NumberZ.ONE};
        Polynom s = new Polynom(pow_m, g);
        s = s.multiply(pol, R);
        return s;
    }

    /**
     * Процедура проверки массива полиномов на возможность подъема. Если в блоке
     * есть полиномы с разным числом переменных, то возвращаем false.
     *
     * @param Polynom mnog[]
     *
     * @return boolean flag
     */
    private static boolean proove(Polynom[] mnog) {
        int h = 0;
        int length = mnog.length;
        int n = mnog[0].powers.length / mnog[0].coeffs.length;
        boolean flag = true;
        //подсчитали число живых переменных h
        for (int i = 0; i < n; i++) {
            for (int j = i; j < mnog[0].powers.length; j += n) {
                if (mnog[0].powers[j] != 0) {
                    flag = false;
                    break;
                }
            }
            if (!flag) {
                h++;
            }
        }
        for (int i = 1; i < length; i++) {
            boolean flags = true;
            int v = 0;
            for (int j = 0; j < n; j++) {
                for (int k = j; k < mnog[i].powers.length; k += n) {
                    if (mnog[i].powers[k] != 0) {
                        flags = false;
                        break;
                    }
                }
                if (!flags) {
                    v++;
                }
            }
            if (h != v) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Ring ring=new Ring("Z[x,y,z,t]");//  Ring.ringZxyz;
//     //   Polynom p=new Polynom(" \\i   x^3+   \\i   y^2 ", ring);
//        Polynom p=new Polynom("(x+t )( 3x^2+t)", ring);
//             System.out.println("in="+p.toString(ring));
//      Element fp=                  p.factor(ring);
//        int vv=3 ;
//      Polynom ggg=  p.GCDHPolCoeffs(vv,ring);
//          System.out.println("in="+p.toString(ring));
//        System.out.println("ggg="+ggg.toString(ring)+"   "+vv);
//   System.out.println("fp="+fp.toString(ring) );
  Ring r = new Ring("C[x]");
        Polynom p = new Polynom("-22\\i x-3\\i", r);
        Polynom pp = new Polynom("22\\i x+3\\i", r);
     Element  h= p.factorOfPol_inQ(false, r);
   //     System.out.println(""+ h);
                
                
    }
}
