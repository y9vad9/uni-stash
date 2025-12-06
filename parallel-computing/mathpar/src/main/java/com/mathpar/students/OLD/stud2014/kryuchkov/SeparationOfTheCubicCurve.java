/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.students.OLD.stud2014.kryuchkov;



/**
 *
 * @author Крючков Алексей
 * Мат.Обес. 3 курс 2011 год
 */
import com.mathpar.number.*;
import com.mathpar.polynom.*;

import java.util.ArrayList;
import java.util.Arrays;
//import kursovaya.Equals;
import com.mathpar.splines.*;

/**
 *
 * @author Крючков Алексей
 * данный класс находит области отделенные кубической кривой
 */
public class SeparationOfTheCubicCurve {

    /**
     *
     * @param p - полином
     * @param ring - кольцо
     * @return - массив решений полинома до третьей степени
     */
    public Element[] resolve(Polynom p, Ring ring) {
        if (p.isItNumber()) {
            return new Element[]{p};
        }
        if (p.powers[0] == 3) {
            return Equals.solveEquals3(p, ring);
        } else {
            if (p.powers[0] == 2) {
                return Equals.solveEquals2(p, ring);
            } else {
                return Equals.solveEquals1(p, ring);
            }
        }
    }

    /**
     *проверяет на принадлежность к границе
     * @param e - массив корней
     * @param limits - массив границ
     * @param ring - кольцо
     * @return - массив проверенных корней
     *
     */
    public Element[] selectRootsToLimits(Element[] e, Element[] limits, Ring ring) {

        ArrayList<Element> newkornymassiv = new ArrayList<Element>();
        for (int i = 0; i < e.length; i++) {
            if ((e[i].compareTo(limits[0], 1, ring)) && (limits[1].compareTo(e[i], 1, ring))) {
                newkornymassiv.add(e[i]);
            }
        }

        Element[] res = new Element[newkornymassiv.size()];
        newkornymassiv.toArray(res);
        SeparationOfTheCubicCurve.booble(res, ring);
        return res;
    }

    public static void booble(Element[] res, Ring ring) {
        for (int i = res.length; i >= 0; i--) {
            for (int j = 0; j < i; j++) {
                for (int k = j + 1; k < i; k++) {
                    if (res[j].compareTo(res[k], 2, ring)) {
                        Element min = res[j];
                        res[j] = res[k];
                        res[k] = min;

                    }
                }
            }
        }

        return;
    }

    /**
     *основная процедура заполнения областей
     * @param limits- массив границ
     * @param p - полином
     * @param h - шаг
     * @param n - количество шагов
     * @param ring - кольцо
     * @return - трехмерный массив состоящий из двух областей
     * Область №k =======================================
    | y1| x1 | y2 | x2 |
     * где (y1,x1) точка начала отрезка,(y2,x2)-конец отрезка
     */
    public Element[][][] regionInSegments(Element[] limits, Polynom p, Element h, Ring ring) {
        Element xmin = limits[0];
        Element xmax = limits[1];
        Element ymin = limits[2];
        Element ymax = limits[3];
        int z = p.powers.length / p.coeffs.length;
        int n = limits[3].subtract(limits[2], ring).divide(h, ring).intValue();//количество шагов
        ArrayList<Element[]> o1 = new ArrayList<Element[]>();
        ArrayList<Element[]> o2 = new ArrayList<Element[]>();
        Element k = ymin;//точка отсчета






        Element[][] massive = new Element[n][];
        Polynom p1 = new Polynom();
        p1 = (Polynom) p.value(new Element[]{ring.varPolynom[0], ring.numberZERO}, ring);
        Element korny123[] = resolve(p1, ring);
        SeparationOfTheCubicCurve.booble(korny123, ring);
        //System.out.println(" cjhn  ="+Array.toString(korny123, ring));

        boolean[] arrayFlag = new boolean[n];
        boolean Flag = true;//начальный флаг в точке (xmin,ymin)



        Element polmin = p.value(new Element[]{xmin, ymin}, ring);//фиксировали полином в точке
        if (polmin.isZero(ring)) {                                                        //xmin,ymin
            if (polmin.compareTo(ring.numberZERO, 2, ring)) {
                Flag = true; //догоримся что true =  первой области
            } else {
                Flag = false;//а false = второй области
            }
        } else {
            Polynom px = (Polynom) p.value(new Element[]{ring.varPolynom[0], ymin}, ring);
            Element[] masEquals = resolve(px,ring);//решение по х полагая что у=ymin
            SeparationOfTheCubicCurve.booble(masEquals,ring);
            Element b = ring.numberZERO; // точка b - точка в которой будем проверять знак
            for(int t = 0; t<masEquals.length;t++ ){
                if(masEquals[t].compareTo(xmin, -1, ring)){
                    int u = 0;
                    if((t+1)<masEquals.length && masEquals[t+1].compareTo(xmin, 2, ring)&&u==0){
                      b = (masEquals[t+1].subtract(xmin, ring)).divide(ring.posConst[2], ring);
                      u++;
                    }

                    }
                }
            if(b.isZero(ring)){
                        b = (xmax.subtract(xmin, ring)).divide(ring.posConst[2], ring);
            }

            Element polSignum =  p.value(new Element[]{b, ymin}, ring);
            if(polSignum.compareTo(ring.numberZERO, 2, ring)){
                Flag = false;
            }
            else{ Flag = true;}
        }

     /**
      * закончили формирование начального флага
      *
      * формируеи arrayFlag[0]
      *
     */
       Polynom pleft = (Polynom) p.value(new Element[]{xmin, ring.varPolynom[1]}, ring);
        pleft = new SeparationOfTheCubicCurve().deleteNotVariables(pleft, ring);
        Element[] equalsToY = resolve(pleft, ring);//решение по у, x=const
         int u = equalsToY.length;//количество решени
         Element F = ymin.add(h, ring);
        if(u>0){

          Element r1 = equalsToY[0];
          if(r1.compareTo(ymin, 1, ring)&&F.compareTo(r1, 2, ring)){
              u--;
              if(u>0){
                   Element r2 = equalsToY[1];
                   if(r2.compareTo(ymin, 1, ring)&&F.compareTo(r2, 2, ring)){
                      u--;
                      if(u>0){
                           Element r3 = equalsToY[0];
                           if(r3.compareTo(ymin, 1, ring)&&F.compareTo(r3, 2, ring)){
                               u--;
                               arrayFlag[0]= !Flag;//3 корня на отрезке
                           }else{ arrayFlag[0]=Flag;}//2 корня принадлежат отрезку
                      }else{ arrayFlag[0]=Flag;}//2 корня
                   }else{ arrayFlag[0]=!Flag;}//1 корень
              }else{ arrayFlag[0]=!Flag;}//1 корень
          }else{ arrayFlag[0]=Flag;}//нет корней на интервале
       }else{ arrayFlag[0]=Flag;}//нет корней

      /**
      *
      *
      *
      *
      * полагая что при ф-ии > 0 - I область
      * теперь пробежимя по левой границе и просмотрим флажки на каждом отрезке
      * вида [ymin+i*h;y+(i+1)*h] где h- шаг
      */



        //System.out.println("решение по у=" + Array.toString(equalsToY));
      //  System.out.println(" "+arrayFlag[0]);
   for(int i =1;i<n;i++){
       Element yh =new NumberZ(i);
       yh = ymin.add(yh.multiply(h, ring),ring);//начало интервала
       Element yH = new NumberZ(i+1);
       yH = ymin.add(yH.multiply(h, ring),ring);//конц интервала
       if(u>0){

          Element r1 = equalsToY[0];
          if(r1.compareTo(yh, 1, ring)&&yH.compareTo(r1, 2, ring)){
              u--;
              if(u>0){
                   Element r2 = equalsToY[1];
                   if(r2.compareTo(yh, 1, ring)&&yH.compareTo(r2, 2, ring)){
                      u--;
                      if(u>0){
                           Element r3 = equalsToY[0];
                           if(r3.compareTo(yh, 1, ring)&&yH.compareTo(r3, 2, ring)){
                               u--;
                               arrayFlag[i] = arrayFlag[i-1];//3 корня на отрезке
                           }else{ arrayFlag[i]=arrayFlag[i-1];}//2 корня принадлежат отрезку
                      }else{ arrayFlag[i]=arrayFlag[i-1];}//2 корня
                   }else{ arrayFlag[i]=!arrayFlag[i-1];}//1 корень
              }else{ arrayFlag[i]=!arrayFlag[i-1];}//1 корень
          }else{ arrayFlag[i]=arrayFlag[i-1];}//нет корней на интервале
       }else{ arrayFlag[i]=arrayFlag[i-1];}//нет корней
   }

/**
 * заполнили массив флажков
 */
for(int l = 0; l<n;l++){
        System.out.println(" "+arrayFlag[l]);
        }





        //решаем относительно х,полагаем что у=const
        for (int y = 0; y < n; y++) {

            //Polynom p1 = new Polynom();
            p1 = (Polynom) p.value(new Element[]{ring.varPolynom[0], k}, ring);

            Element[] korny = new Element[0];
            korny = resolve(p1, ring);

            if (korny != null) {

                korny = selectRootsToLimits(korny, limits, ring);//получен массив
                if (p1.powers[0] == 3 && korny.length == 2) {
                    Element zero = new NumberR64().ZERO;
                    if (korny[0].compareTo(korny[1], 0, ring)) {
                        korny = new Element[korny.length - 1];
                        korny[0] = zero;
                    }
                }

                if (z == 1 && p.powers[0] == 2) {
                    Element val = korny[0];
                    korny = new Element[korny.length - 1];
                    korny[0] = val;
                }
//                if(z==1&& p.powers[0]==3){
//                   Element val =  korny[0];
//                   korny = new Element[korny.length - 1];
//                    korny[0] = val;
//            }
            }

            if (korny.length != 0) {
                int g = new SeparationOfTheCubicCurve().findFunctionOnTheInterval(p, xmin, k, h, ring);
                if (korny.length == 1) {
                    if (g % 2 == 1) {
                        massive[y] = new Element[4];
                        massive[y][0] = k;
                        massive[y][1] = xmin;
                        massive[y][2] = k;
                        massive[y][3] = korny[0];
                        o2.add(massive[y]);
                        Element[] el = new Element[4];
                        el[0] = k;
                        el[1] = korny[0];
                        el[2] = k;
                        el[3] = xmax;
                        o1.add(el);
                    } else {
                        massive[y] = new Element[4];
                        massive[y][0] = k;
                        massive[y][1] = xmin;
                        massive[y][2] = k;
                        massive[y][3] = korny[0];
                        o1.add(massive[y]);
                        Element[] el = new Element[4];
                        el[0] = k;
                        el[1] = korny[0];
                        el[2] = k;
                        el[3] = xmax;
                        o2.add(el);

                    }
                }

                if (korny.length == 2) {
                    int len = 1 + korny.length;
                    massive[y] = new Element[len];

                    for (int i = 0; i < korny.length; i++) {
                        massive[y][i] = korny[i];
                    }
                    massive[y][len - 1] = k;
                    Element[] el = new Element[massive[y].length + 1];
                    if (g % 2 == 1) {
                        el[0] = k;
                        el[1] = massive[y][0];
                        el[2] = k;
                        el[3] = massive[y][1];
                        o2.add(el);
                        Element[] el1 = new Element[8];
                        el1[0] = k;
                        el1[1] = xmin;
                        el1[2] = k;
                        el1[3] = massive[y][0];
                        el1[4] = k;
                        el1[5] = massive[y][1];
                        el1[6] = k;
                        el1[7] = xmax;
                        o1.add(el1);
                    } else {
                        el[0] = k;
                        el[1] = massive[y][0];
                        el[2] = k;
                        el[3] = massive[y][1];
                        o1.add(el);
                        Element[] el1 = new Element[8];
                        el1[0] = k;
                        el1[1] = xmin;
                        el1[2] = k;
                        el1[3] = massive[y][0];
                        el1[4] = k;
                        el1[5] = massive[y][1];
                        el1[6] = k;
                        el1[7] = xmax;
                        o2.add(el1);
                    }
                }
                if (korny.length == 3) {
                    int len = 1 + korny.length;
                    massive[y] = new Element[len];

                    for (int i = 0; i < korny.length; i++) {
                        massive[y][i] = korny[i];
                    }
                    massive[y][len - 1] = k;
                    Element[] el = new Element[8];
                    if (g % 2 == 1) {
                        el[0] = k;
                        el[1] = massive[y][0];
                        el[2] = k;
                        el[3] = massive[y][1];
                        el[4] = k;
                        el[5] = massive[y][2];
                        el[6] = k;
                        el[7] = xmax;
                        o2.add(el);
                        Element[] el1 = new Element[8];
                        el1[0] = k;
                        el1[1] = xmin;
                        el1[2] = k;
                        el1[3] = massive[y][0];
                        el1[4] = k;
                        el1[5] = massive[y][1];
                        el1[6] = k;
                        el1[7] = massive[y][2];
                        o1.add(el1);
                    } else {
                        el[0] = k;
                        el[1] = massive[y][1];
                        el[2] = k;
                        el[3] = massive[y][2];
                        el[4] = k;
                        el[5] = massive[y][0];
                        el[6] = k;
                        el[7] = xmax;
                        o1.add(el);
                        Element[] el1 = new Element[8];
                        el1[0] = k;
                        el1[1] = xmin;
                        el1[2] = k;
                        el1[3] = massive[y][1];
                        el1[4] = k;
                        el1[5] = massive[y][2];
                        el1[6] = k;
                        el1[7] = massive[y][0];
                        o2.add(el1);
                    }


                }
            } else {
                Element[] el1 = new Element[4];
                el1[0] = k;
                el1[1] = xmin;
                el1[2] = k;
                el1[3] = xmax;
                o1.add(el1);


            }
            k = k.add(h, ring);//прибавляем шаг по х
        }


        Element[][] res1 = new Element[o1.size()][];
        for (int i2 = 0; i2 < res1.length; i2++) {
            res1[i2] = new Element[o1.get(i2).length];
            Element[] mas = o1.get(i2);
            for (int i1 = 0; i1 < mas.length; i1++) {
                res1[i2][i1] = mas[i1];
            }
        }
        Element[][] res2 = new Element[o2.size()][];
        for (int i2 = 0; i2 < res2.length; i2++) {
            res2[i2] = new Element[o2.get(i2).length];
            Element[] mas = o2.get(i2);
            for (int i1 = 0; i1 < mas.length; i1++) {
                res2[i2][i1] = mas[i1];
            }
        }
        Element[][][] result = new Element[2][][];
        result[0] = res1;
        result[1] = res2;
        return result;
    }

    /**
     * Данная процедура предназначена для работы с сеткой квадратов
     * Вычисляет области на множестве квадратов
     * @param pol -массив полиномов
     * @param limits - массив границ
     * @param h- шаг проверки
     * @param n -количество строк
     * @param m -количество столбцов
     * @param ring - кольцо в котором ведется вычисление
     * @return выводит две области трехмерным массивом
     */
    public Element[][][] partitionOfTheSquares(Polynom[] pol, Element[] limits, Element h, int n, int m, Ring ring) {
        Element xmin = limits[0];//
        Element xmax = limits[1];//в большом квадрате
        Element ymin = limits[2];//
        Element ymax = limits[3];//
        Element m1 = new NumberR64(m);//количество столбцов
        Element n1 = new NumberR64(n);//количество строк
        Element l1 = xmax.subtract(xmin, ring).divide(m1, ring);//шаг по х выбора областей
        Element l2 = ymax.subtract(ymin, ring).divide(n1, ring);//шаг по у выбора областей
        int mxn = n * m; //количество квадратов
        int N = l1.divide(h, ring).intValue();

        Element[][][][][] A = new Element[n][m][2][N][];
        Element maxX = xmin.add(l1, ring);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                Element[] limits1 = new Element[4];
// Element k = new NumberR64((i-1));
                int k = l1.intValue();
                int km = k * i;
                Element xkmk = new NumberR64(km - k);
                Element xr = new NumberR64(km);
                Element ykmk = new NumberR64(k * n - k);
                Element yr = new NumberR64(k * n);
                ykmk = limits1[0];
                xr = limits1[1];//в малом квадрате
                ykmk = limits1[2];//
                yr = limits1[3];//
                A[i][j] = regionInSegments(limits1, pol[i], h, ring);
            }
        }
        Boolean[][] B = new Boolean[n][m];
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1; j++) {
                Element[][][] Mr = A[i][j];
                Element[][][] Ml = A[i][j - 1];
                B[i][j] = Bool(Ml, Mr, maxX, xmin, h, ring);
            }
        }

        ArrayList<Element>[][] L = new ArrayList[2][N * n];
        for (int i = 0; i < (n * N) - 1; i++) {
            L[0][i] = new ArrayList();
            L[1][i] = new ArrayList();
            for (int j = 0; j < m; j++) {
                if (B[i][j] == true) {
                    L[0][i].addAll(Arrays.asList(A[i / N][j][0][i % N]));
                    L[1][i].addAll(Arrays.asList(A[i / N][j][1][i % N]));
                } else {
                    L[0][i].addAll(Arrays.asList(A[i / N][j][1][i % N]));
                    L[1][i].addAll(Arrays.asList(A[i / N][j][0][i % N]));
                }
            }
        }
        Element[][][] res1 = null;

        for (int i = 0; i < L[0].length; i++) {
            L[0][i].toArray(res1[0][i]);
            L[1][i].toArray(res1[1][i]);


        }



        return res1;
    }

    /**
     *Данная процедура ставит флаг true или false в квадрате
     * @param Ml - левый квадрат
     * @param Mr - правый квадрат
     * @param xmax - х максимум
     * @param xmin - х минимум
     * @param h - шаг похода
     * @param ring - кольцо в котором ведется вычисление
     * @return   выводит флаг смены полярности области
     */
    Boolean Bool(Element[][][] Ml, Element[][][] Mr, Element xmax, Element xmin, Element h, Ring ring) {
        Element Bl = null;//центральн точка с ориентациейй слева
        Element Br = null;//центральн точка с ориентациейй справа
        Element Al = null;//точка слева
        Element Cr = null;//точка справа
        Element Dl = null;//верхняя точка с ориентацией влево
        Element Dr = null;//верхняя точка с ориентацией вправо

        if (Ml[0][0][Ml[0][0].length] == xmax) {
            Bl = new NumberR64().ZERO;
            if (xmax.subtract(h, ring).compareTo(Ml[0][0][Ml[0][0].length - 2], 1, ring)) {
                Al = new NumberR64().ZERO;
            } else {
                Al = new NumberR64().ONE;
            }

        } else {
            Bl = new NumberR64().ONE;
            if (xmax.subtract(h, ring).compareTo(Ml[1][0][Ml[0][0].length - 2], 1, ring)) {
                Al = new NumberR64().ONE;
            } else {
                Al = new NumberR64().ZERO;
            }
        }
        if (xmin == Mr[0][0][1]) {
            Br = new NumberR64().ZERO;
            if (xmin.subtract(h, ring).compareTo(Mr[0][0][3], 1, ring)) {
                Cr = new NumberR64().ZERO;
            } else {
                Cr = new NumberR64().ONE;
            }
        } else {
            Br = new NumberR64().ONE;
            if (xmin.subtract(h, ring).compareTo(Mr[1][0][3], 1, ring)) {
                Cr = new NumberR64().ONE;
            } else {
                Cr = new NumberR64().ZERO;
            }
        }


        if (!((Al.compareTo(Bl, -3, ring)) && ((Cr.compareTo(Br, -3, ring))))) {
            if (Bl.compareTo(Br, -1, ring)) {
                return true;
            } else {
                return false;
            }
        } else {
            if (Ml[0][0][Ml[0][0].length] == xmax) {
                Dl = new NumberR64().ZERO;
            } else {
                Dl = new NumberR64().ONE;
            }
            if (Mr[0][1][1] == xmin) {
                Dr = new NumberR64().ZERO;
            } else {
                Dr = new NumberR64().ONE;
            }
            if ((Dr == Cr) ^ (Dl == Al)) {
                if (Al == Cr) {
                    return false;
                } else {
                    return true;
                }
            } else {
                if (Al == Cr) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    /**
     * процедура нахождения полиномов по точкам
     * @param f - массив точек
     * @return - массив полиномов
     */
    public Polynom[][] splinesOfPolynoms(double[][] f) {
        spline2D temp;
        temp = new spline2D(f, spline1D.approxType.Linear);
        return temp.getPolynoms();
    }

    /**
     * Данная процедура выводит количество линий пересечения графика полинома
     * с отрезком[y,y+h] заданным на прямой х = const
     * @param p - полиом
     * @param c - прямая х = const
     * @param y - начало отрезка
     * @param h - шаг отрезка
     * @param ring - кольцо в котором ведется вычисление
     * @return количество пересечений графика полинома с отрезком на вертикальной прямой
     */
    public int findFunctionOnTheInterval(Polynom p, Element c, Element y, Element h, Ring ring) {
        Element yh = y.add(h, ring);
        p = new SeparationOfTheCubicCurve().deleteNotVariables(p, ring);
        Element[] korny = resolve(p, ring);
        ArrayList<Element> res = new ArrayList<Element>();
        for (int i = 0; i < korny.length; i++) {
            if (korny.length != 0) {
                if ((korny[i].compareTo(y, 1, ring)) && (yh.compareTo(korny[i], 1, ring))) {
                    res.add(korny[i]);
                }
            } else {
                return 0;
            }
        }
        return res.size();
    }

    /**
     * процедура удаляет переменные полинома у которых все степени равны 0
     * @param pol - полином
     * @param ring - кольцо в котором ведется вычисления
     * @return - вывод полинома "очищенного" от переменных со степенью равной 0
     */
    public Polynom deleteNotVariables(Polynom pol, Ring ring) {
        int k = pol.powers.length / pol.coeffs.length;//количество переменных
        int[] a0 = new int[pol.coeffs.length];
//        for(int i =0;i<a0.length;i++){
//            a0[i] = -1;
//        }
        int h = 0;
        for (int i = 0; i < k; i++) {
            if (pol.powers[i] == 0) {
                int j = 0;
                for (j = i + k; j < pol.powers.length; j += k) {

                    if (pol.powers[j] != 0) {
                        break;
                    }

                }

                if (j >= pol.powers.length) {
                    a0[h] = i;
                    h++;
                }

            }
        }
        int[] a = new int[h];
        System.arraycopy(a0, 0, a, 0, h);
        //  System.out.println("h= "+h+"     a = "+Array.toString(a));
        int[] b = new int[a0.length - h];
        int j = 0;
        int t = 0;
        for (int i = 0; i < pol.coeffs.length; i++) {
            if (j < h && i == a[j]) {
                j++;
            } else {
                b[t] = i;
                t++;

            }
        }


        int pow1[] = new int[h * pol.coeffs.length];
        for (int i = 0; i < h; i++) {
            int m = b[i];
            for (j = 0; j < pow1.length; j += h) {
                pow1[j] = pol.powers[m];
                m += k;
            }
        }



        pol = new Polynom(pow1, pol.coeffs);






        return pol;
    }
}
