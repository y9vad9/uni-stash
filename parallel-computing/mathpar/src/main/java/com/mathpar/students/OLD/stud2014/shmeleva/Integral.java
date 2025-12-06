/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.students.OLD.stud2014.shmeleva;

import com.mathpar.func.F;
import com.mathpar.number.*;
import com.mathpar.polynom.*;




/**
 * Программный класс для вычисления неопределенных интегралов
 * Подинтегральные функции  - полиномы от многих переменных
 *
 * @author Shmeleva.A.A.
 * @version 1.0
 * @years 2011
 */
public class Integral {


    /**
     * Метод приближенного вычисления определенного интеграла по формуле Симпсона
     * @param p - полином от одной переменной
     * @param a - нижний предел интегрирования
     * @param b - верхний предел интегрирования
     * @param n - количество шагов заданных пользователем
     * @param ring - кольцо
     * @return площадь фигуры по формуле Симпсона
     */
    public Element simpson(Polynom p, Element a, Element b, int n, Ring ring) {
        if ((p.coeffs.length != 0) && (p.powers.length != 0)) {
            Element h = (b.subtract(a, ring)).divide(new NumberR64(n), ring);//вычисляем шаг разбиения - ((b-a)/n)
            Element[] func = new Element[n];//массив для значений вычисленной функции
            Element sum1 = NumberR64.ZERO;//по четным номерам
            Element sum2 = NumberR64.ZERO;//по нечетным номерам
            for (int i = 0; i < n; i++) {
                Element el = a.add(new NumberR64(i).multiply(h, ring), ring);//a+h
                func[i] = p.value(new Element[]{el}, ring);//вычисляем функцию в точке
                if ((i % 2 == 0) && (i != 0)) {
                    sum2 = sum2.add(func[i], ring);
                } else {
                    if (i != 0) {
                        sum1 = sum1.add(func[i], ring);
                    }
                }
            }
            Element d = h.divide(new NumberR64(3), ring);//вычисление коэффициента h/3
            Element sum3 = p.value(new Element[]{a}, ring).add(p.value(new Element[]{b}, ring), ring);//F(a)+F(b)
            sum2 = new NumberR64(2).multiply(sum2, ring);
            sum1 = new NumberR64(4).multiply(sum1, ring);
            Element sum = sum1.add(sum2, ring).add(sum3, ring);
            return d.multiply(sum, ring);
        } else {
            if (p.isZero(ring)) {//проверка на 0
                return p;
            } else {
                return p;
            }
        }
    }


    /**
     * Метод вычисления определенного интеграла по формуле Ньютона-Лейбница
     * @param p - произвольный полином многих переменных
     * @param a - нижний предел интегрирования
     * @param b - верхний предел интегрирования
     * @param k - номер переменной интегрирования
     * @param ring - кольцо
     * @return - вычисленный интеграл
     */
    public Element integral_(Polynom p, Element a, Element b, int k, Ring ring) {
        Polynom result = (Polynom) new Integral().pervB(p, k, ring);//получение первообразной
        Element[] el = new Element[ring.varPolynom.length];//Массив для подстановки
        for (int i = 0; i < el.length; i++) {//заполняет массив подстановки
            if (i == k) {
                el[k] = a;//вместо переменной интегрирования записывает верхний предел интегрирования
            } else {
                el[i] = ring.varPolynom[i];//заполняет массив подстановки из массива полиномов созданного от кольца
            }
        }
        Element valP1 = result.value(el, ring);//F(a)
        el[k] = b;
        Element valP2 = result.value(el, ring);//F(b)
        return valP2.subtract(valP1, ring);//F(b)-F(a)
    }


    /**
     * S = {INT}|[a,b]{INT}|[c,d] F(x,y)dxdy
     * процедура вычисления интеграла от полинома двух переменных
     * @param p - полином
     * @param a_one - первый верхний редел
     * @param b_one -первый нижний предел
     * @param one_var - первая переменная
     * @param two_var -вторая переменная
     * @param a_two -второй верхний предел
     * @param b_two -второй верхний предел
     * @param ring - кольцо
     * @return
     */
    public Element solve_Integralfortwovars(Polynom p, Element a_one, Element b_one, int one_var, int two_var, Element a_two, Element b_two, Ring ring) {
        Element p_y = new Integral().integral_(p, a_one, b_one, one_var, ring);//вычисление интеграла по dy
        int numbtype = p_y.numbElementType();//определяем тип полученного результата
        if (numbtype == Ring.Polynom) {
            return new Integral().integral_((Polynom) p_y, a_two, b_two, two_var, ring);
        } else {
            if (numbtype < Ring.Polynom) {
                return new Integral().integral_(new Polynom(p_y), a_two, b_two, two_var, ring);
            } else {
                return new F(F.INT, new Element[]{a_two, b_two, p_y, ring.varPolynom[two_var]});
            }
        }
    }




    /**
     * Метод нахождения площади под интегральной кривой заданной Polynom - от двух переменных.
     * Геометрический смысл двойного интеграла
     * Алгоритм-
     * 1) Решения определенного интеграла
     * 2) Подстановка граничных условий
     * ------------------------------------------------
    /**
     *
     * @param p - начальная ограничивающая кривая
     * @param a_one - первый нижний предел
     * @param b_one - первый верхний предел
     * @param one_var - первая переменная
     * @param two_var - вторая переменная
     * @param a_two - второй нижний предел
     * @param b_two - второй верхний предел
     * @param ring - кольцо переменных
     * @return
     */

    public static Element solve_IntS_twoVars(Polynom p, Element a_one, Element b_one, int one_var, int two_var, Element a_two, Element b_two, Ring ring){
        return new Integral().solve_Integralfortwovars(p, a_one, b_one, one_var, two_var, a_two, b_two, ring);
    }



    /**
     * Метод вычисления первообразной для неопределенного интеграла по формуле Ньютона-Лейбница
     * @param p - полином многих переменных
     * @param n - номер переменной в кольце по которой интегриуем
     * @param r - кольцо
     * @return - результат - первообразная
     */
    public static Element pervB(Polynom p, int n, Ring r) {
        if ((n >= 0) && (n < r.varNames.length)) {//1. проверка на существование номера переменной
            if (p.isZero(r)) {//2.Проверка 0 Polynom
                return p;
            } else {
                if ((p.coeffs.length == 1) && (p.powers.length == 0)) {//3.Проверка числового Polynom
                    int[] pow = new int[r.varNames.length];//создаем массив степеней нового полинома
                    for (int i = 0; i < pow.length; i++) {//цикл по заполнению массива степеней нового полинома
                        if (i == n) {
                            pow[i] = 1;//при совпадении номера переменной интегрирования присваеваем в соответствующую позицию 1, означающую появление новой переменной
                        } else {
                            pow[i] = 0;
                        }
                    }
                    return new Polynom(pow, p.coeffs);
                }else{//все остальные случаи
                    Element[] coef = new Element[p.coeffs.length];//создаем массив коэф.нового Polynoma
                    System.arraycopy(p.coeffs, 0, coef, 0, p.coeffs.length);//копируем коэф.из входного Polynoma в новый.
                    int[] pow = new int[coef.length*r.varNames.length];//создаем массив степеней.нового Polynoma
                    int k = p.powers.length/p.coeffs.length;//количество переменных
                    int shift = 0;//счетчик
                    int d1 = 0;//шаг 1
                    int d2 = 0;//шаг 2
                    for(int h = 0 ; h < coef.length; h++){//работа со степенями нового Polynoma
                        System.arraycopy(p.powers, 0+d1, pow, 0+d2, k);//копируем из входного Polynoma в новый. Каждый раз начиная с определенной позиции
                        d1 += k;
                        d2 += r.varNames.length;
                    }
                    IntList q = new IntList();//хранение степеней по переменной интегрирования
                    for(int j = 0 ; j < pow.length; j++){//цикл по массиву степеней нового Polynoma
                        if(j == n+shift){
                          pow[j]++;//увеличиваем на 1 переменную интегрирования в массиве степеней
                          q.add(pow[j]);//добавляем в хранилище степеней по переменной интегрирования
                          shift+=r.varNames.length;//увеличиваем счетчик на количество переменных в кольце
                        }
                    }
                    //заполнение нового массива коэффициентов
                    for(int t = 0; t < coef.length; t++){
                       coef[t] = coef[t].divide(new NumberR64(q.arr[t]), r);//каждый коэф. делим на новую степень переменной интегрирования
                    }
                    return new Polynom(pow, coef);
                }
            }
        }
        return Polynom.polynomZero;//возвращаем 0 Polynom
    }


    /**
     * Метод вычисления определенного интеграла по формуле Ньютона-Лейбница
     * @param p - полином от многих переменных
     * @param n - номер переменной интегрирования
     * @param var - массив значений для подстановки в интеграл
     * @param r - кольцо
     * @return - вычисленный интеграл
     */
    public static Element value_integral(Polynom p, int n, Element[] var, Ring r){
        Element result = Integral.pervB(p, n, r);
        Element a1 = null;//вычисленная функция после подстановки 1 предела
        Element a2 = null;//вычисленная функция после подстановки 2 предела
        int k = 0;
        Element[] vars = r.varPolynom;
        for(int i = 0; i < vars.length; i++){
            if(i == n) {
                if(var[0] instanceof Polynom) vars[i] =  var[0];
                   else vars[i] = new Polynom(var[0]);
            }
        }
        if(result instanceof Polynom){
            a1 = ((Polynom)result).value(vars, r);//F(a)
        }
        for(int i = 0; i < vars.length; i++){
            if(i == n)  {
                if(var[1] instanceof Polynom) vars[i] =  var[1];
                    else vars[i] = new Polynom(var[1]);
            }
        }
        if(result instanceof Polynom){
            a2 = ((Polynom)result).value(vars, r);//F(b)
        }
        return a2.subtract(a1, r);//F(b)-F(a)
    }




    /**
     * процедура вычисления интеграла от полинома трех переменных
     * Геометрический смысл 3 интеграла
     * @param p  - полином
     * @param p_f - врехняя ограничевающая плоскость z = f(x,y)
     * @param a - предел
     * @param ring - кольцо
     * @return
     */
    public static Element solve_V(Polynom p, Polynom p_f, Element[] a, Ring ring) {
        Element a0 = a[0];//нижний предел по x
        Element a1 = a[1];//нижний предел по y
        Element a2 = a[2];//нижний предел по z
        Element p_z = Integral.value_integral(p, 2, new Element[]{a2, p_f}, ring);//интеграл по Z

        Element p_1 = p_f.value(new Element[]{new Polynom("x", ring), a1}, ring);//верхний предел по y

        Ring r1 = new Ring("R64[x,y]");
        r1.setDefaulRing();

        Element p_y = Integral.value_integral(((Polynom)p_z), 1, new Element[]{a1, p_1}, r1);//интеграл по Y

        Element p_2 = ((Polynom)p_1).value(new Element[]{a0}, ring);//верхний предел по x

        Ring r2 = new Ring("R64[x]");
        r2.setDefaulRing();

        return Integral.value_integral(((Polynom)p_y), 0, new Element[]{a0, p_2}, r2);
    }

    /**
     * Решение тройного интеграла
     * @param p - полином
     * @param a - предел интегрирования по каждой переменной
     * @param ring - кольцо
     * @return
     */
    public static Element solve_Integralforthreevars(Polynom p, Element[] a, Ring ring){
          Element p_z = Integral.value_integral(p, 2, new Element[]{a[4], a[5]}, ring);//интеграл по Z
          Element p_y = Integral.value_integral(((Polynom)p_z), 1, new Element[]{a[2], a[3]}, ring);//интеграл по Y
          Element p_x = Integral.value_integral(((Polynom)p_y), 0, new Element[]{a[0], a[1]}, ring);//интеграл по X
          return p_x;
    }


    /**
     * Вычисление интегралов в криволинейных координатах
     * @param p - массив полиномов интегрирования
     * @param a - массив ограничивающих точек
     * @param ring - кольцо
     * @return
     */
    public static Element solve_kr_3(Polynom[] p, Element[][] a, Ring ring) {
        Polynom[] pp = new Polynom[p.length];//массив вычисляемых функций
        for (int i = 0; i < p.length; i++) {//подстановка пределов по каждой переменной
            Element[] el = new Element[2];
            el[0+i] = ring.varPolynom[i];
            el[1-i] = a[2 - i - 1][0];
            pp[i] = (Polynom) p[i].value(el, ring);
        }
        Element[] ppp = new Element[pp.length];
        for (int j = 0; j < ppp.length; j++) {//вычисление неопределенного интеграла
            ppp[j] = new Integral().integral_(pp[j], a[j][1], a[j][2], j, ring);
        }
        Element res = ppp[0];
        for (int z = 1; z < ppp.length; z++) {
            res = res.add(ppp[z], ring);
        }
        return res;
    }



    /**
     *
     * @param func[][] - двумерный массив...
     * 0 - функции по X
     * 1 - функции по Y
     * @param a - двумерный массив...
     * 0 - значения области ограничения, на первом месте стоит полином по Y, а на остальных местах значения по X
     * 1 - значения области ограничения, на первом месте стоит полином по X, а на остальных местах значения по Y
     * @param n - количество функций интегрирования
     * @param ring
     * @return
     */
    public static Element solve_kr_4(Polynom[][] func, Element[][] a, Ring ring) {
        Element[][] b = new Element[a.length][];//массив новых пределов
        for (int i = 0; i < a.length; i++) {
            if (i == 0) {
                int j = 0;
                int d = 0;
                b[i] = new Element[a[i].length/3];
                while (j < a[i].length) {
                    b[i][d] = a[i][j].D(i, ring);
                    j += 3;
                    d++;
                }
            } else {
                int j = 2;
                int d = 0;
                b[i] = new Element[a[i].length/3];
                while (j < a[i].length) {
                    if(a[i][j].isZero(ring)){
                      b[i][d] = new Polynom("y", ring).D(i, ring);
                    j += 3;
                    d++;
                    }else{
                    b[i][d] = a[i][j].D(i, ring);
                    j += 3;
                    d++;
                    }
                }
            }
        }
        Element[][] arr_pol = new Element[func.length][];//массив вычисленных интегралов по каждому отрезку

        for (int i = 0; i < func.length; i++) {
            if (i == 0) {
                int j = 0;
                int g = 0;
                 int h = 0;
                arr_pol[i] = new Element[b[i].length];
                while (j < b[i].length) {
                    Element[] arg = new Element[2];
                    arg[0] = new Polynom("x", ring);
                    arg[1] = a[i][g];
                    arr_pol[i][h] = func[i][0].value(arg, ring).multiply(b[i][j], ring);
                    g+=3;
                    h++;
                    j++;
                }
            } else {
                int j = 0;
                int g = 2;
                 int h = 0;
                arr_pol[i] = new Element[b[i].length];
                while (j < b[i].length) {
                    Element[] arg = new Element[2];
                    arg[0] = new Polynom("y", ring);
                    arg[1] = a[i][g];
                    arr_pol[i][h] = func[i][0].value(arg, ring).multiply(b[i][j], ring);
                    g+=3;
                    h++;
                    j++;
                }
            }
        }
        Element res = Polynom.polynomZero;
        for (int i = 0; i < arr_pol.length; i++) {
            if (i == 0) {
                int j = 0;
                int g = 1;
                while (j < arr_pol[i].length) {
                    if(((Polynom)arr_pol[i][j]).isZero(ring)){
                        res = res.add(res, ring);
                    }else{
                    res = res.add(new Integral().integral_((Polynom)arr_pol[i][j], a[i][g], a[i][g+1], i, ring), ring);
                    }
                    j++;
                    g+=3;
                }
            } else {
                int j = 0;
                 int g = 0;
                while (j < arr_pol[i].length) {
                     res = res.add(new Integral().integral_((Polynom)arr_pol[i][j], a[i][g], a[i][g+1], i, ring), ring);
                     j++;
                     g+=3;
                }
            }
        }
        return res;
    }

    /**
     * INT|L {P(x,y)dx+Q(x,y)dy} = INT|a,b{(P(x(t),y(t))x'(t)+Q(x(t),y(t))y'(t))dt}
     * @param p - P(x,y)
     * @param q - Q(x,y)
     * @param t1 - нижний предел интегрирования
     * @param t2 - верхний предел интегрирования
     * @param pol - массив для подстановок (x = x(t), y = y(t))
     * @return
     */
    public static Element curvilinear_integral(F p, F q, Element t1, Element t2, Polynom[] pol){
        Element f1 = p.valueOf(pol, new Ring("R64[t]"));//P(x(t),y(t))
        Element f2 = q.valueOf(pol, new Ring("R64[t]"));//Q(x(t),y(t))
        Element x = pol[0].D(0, new Ring("R64[t]"));//x'(t)
        Element y = pol[1].D(0, new Ring("R64[t]"));//y'(t)
        Polynom integral = (Polynom)f1.multiply(x, new Ring("R64[t]")).add(f2.multiply(y, new Ring("R64[t]")), new Ring("R64[t]"));
        System.out.println("integral == " + integral);
        return new Integral().integral_(integral, t1, t2, 0, new Ring("R64[t]"));
    }

    /**
     * Метод вычисления определенного интеграла по формуле Ньютона-Лейбница
     * @param p - произвольный полином многих переменных
     * @param a - нижний предел интегрирования
     * @param b - верхний предел интегрирования
     * @param k - номер переменной интегрирования
     * @param ring - кольцо
     * @return - вычисленный интеграл
     */
    public Element integral_3d(Polynom p, Element a, Element b, int k, Ring ring) {
        Element[] el = new Element[ring.varPolynom.length];//Массив для подстановки
        for (int i = 0; i < el.length; i++) {//заполняет массив подстановки
            if (i == k) {
                el[k] = a;//вместо переменной интегрирования записывает верхний предел интегрирования
            } else {
                el[i] = ring.varPolynom[i];//заполняет массив подстановки из массива полиномов созданного от кольца
            }
        }
        Polynom result = null;
        if (p.coeffs.length == 1) {
            result = (Polynom) new Integral().pervB(p, k, ring);//получение первообразной
            result.coeffs[0] = p.coeffs[0];
            Element valP1 = new F(F.DIVIDE, new Element[]{result, new NumberR64(result.powers[0])}).valueOf(el, ring);//F(a)
            el[k] = b;
            Element valP2 = new F(F.DIVIDE, new Element[]{result, new NumberR64(result.powers[0])}).valueOf(el, ring);//F(b)
            return valP2.subtract(valP1, ring);//F(b)-F(a)
        } else {
            result = (Polynom) new Integral().pervB(p, k, ring);//получение первообразной
            Element valP1 = result.value(el, ring);//F(a)
            el[k] = b;
            Element valP2 = result.value(el, ring);//F(b)
            return valP2.subtract(valP1, ring);//F(b)-F(a)
        }
    }

    /**
     * INT|L{f(x,y,z)dl} = INT|t1,t2{f(x(t),y(t),z(t))*SQRT{x'(t)^2+y'(t)^2+z'(t)^2}}
     * @param x
     * @param y
     * @param z
     * @param t1
     * @param t2
     * @param pol
     * @return
     */
    public static Element curvilinear_integral3D(F f, Element t1, Element t2, F[] pol) {
        Element f1 = f.valueOf(pol, new Ring("R64[t]"));//f(x(t),y(t),z(t))
        Element x = pol[0].D(0, new Ring("R64[t]"));//x'(t)
        Element y = pol[1].D(0, new Ring("R64[t]"));//y'(t)
        Element z = pol[2].D(0, new Ring("R64[t]"));//z'(t)
        Element sqr = new F(F.SQRT, new Element[]{new F(F.ADD, new Element[]{x.multiply(x, new Ring("R64[t]")), y.multiply(y, new Ring("R64[t]")), z.multiply(z, new Ring("R64[t]"))})});
        sqr = new F(F.SQRT, new Element[]{new NumberR64(2)});
        if (sqr instanceof F) {
            if (((F) sqr).name == F.SQRT) {
                if (((F) sqr).X[0].numbElementType() < Ring.Polynom) {
                    sqr = new NumberR64(Math.sqrt(((F) sqr).X[0].doubleValue()));//aqrt(x'(t)^2+y'(t)^2+z'(t)^2)
                }
            }
        }
        Polynom m = new Polynom("t^2", new Ring("R64[t]"));
        Polynom integral = (Polynom)m.multiply(sqr, new Ring("R64[t]"));
        System.out.println("integral == " + integral);
        return new Integral().integral_3d(integral, t1, t2, 0, new Ring("R64[t]"));

    }

    /**
     * INT|L{f(x,y)dl} = INT|t1,t2{f(x,y(t))*SQRT{1+y'(t)^2}}
     * @param f
     * @param t1
     * @param t2
     * @param pol
     * @return
     */
    public static Element curvilinear_integral2D(F f, Element t1, Element t2, Polynom[] pol){
        Element f1 = f.valueOf(pol, new Ring("R64[t]"));//f(x(t),y(t))
        Element y = pol[1].D(0, new Ring("R64[t]"));//y'(t)
        F sqr = new F(F.SQRT,
                new Element[]{
            new F(F.ADD, new Element[]{
                new Ring("R64[t]").numberONE,
                y.multiply(y, new Ring("R64[t]"))})});
        //для простого случая
        Element sum = new Ring("R64[t]").numberZERO;
        for(int i = 0; i < ((F)sqr.X[0]).X.length; i++){
            sum = sum.add(((F)sqr.X[0]).X[i], new Ring("R64[t]"));
        }
        Element sqrVal = NumberR64.valueOf(Math.sqrt(((Polynom)sum).coeffs[0].doubleValue()));
        Polynom integral = (Polynom)f1.multiply(sqrVal, new Ring("R64[t]"));
        System.out.println("integral == " + integral);
        return new Integral().integral_(integral, t1, t2, 0, new Ring("R64[t]"));
    }


}
