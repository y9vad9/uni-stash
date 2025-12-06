package com.mathpar.students.OLD.stud2014.arharova;
import com.mathpar.number.*;
import com.mathpar.polynom.*;
import com.mathpar.splines.*;
import com.mathpar.students.OLD.stud2014.khvorov.ObjectMassAndCentreObjectMass;
/**
 *
 * @author arharova
 */
public class SurfaceMechanics {
/**
     * процедура, считающая неопределенный интеграл от функции, выраженной полиномом
     * @param p - полином от трех переменных
     * @param k - номер переменной
     * @param ring - кольцо от трех переменных
     * @return интеграл от полинома
     */
    public Polynom threeIntegral(Polynom p, int k, Ring ring) {
        Element one = ring.numberONE;//единица
        int n = p.powers.length / p.coeffs.length;//количество переменных
        if (p.isItNumber()) {
            return p.multiply(ring.varPolynom[k], ring);
        }
        if (p.isZero(ring)) {
                return p;
            }
        if(k>=p.powers.length){
          return p.multiply(ring.varPolynom[k], ring);

        }

        if ((p.coeffs.length != 0) && (p.powers.length != 0)) {
            int[] pow = new int[p.powers.length];//новый массив степеней
            System.arraycopy(p.powers, 0, pow, 0, p.powers.length);
            Element[] coef = new Element[p.coeffs.length];//новый массив коэффициентов
            int h = 0;
            while (k < p.powers.length) {
                pow[k] = p.powers[k] + 1;
                coef[h] = (one.divide(new NumberR64(pow[k]), ring)).multiply(p.coeffs[h], ring);
                k += n;
                h++;
            }

            return new Polynom(pow, coef);
        }
             else {
                int[] pow = new int[1];
                Element[] coef = new Element[p.coeffs.length];
                pow[0] = 1;
                coef[0] = p.coeffs[0];
                return new Polynom(pow, coef);
            }

        }



    /**
     * процедура подстановки пределов интегрирования вместо первой переменной
     * @param p - полином от трех переменных
     * @param num - номер переменной
     * @param elem1 - первый предел интегрирования
     * @param elem2 - второй предел интегрирования
     * @param ring - кольцо от трех переменных
     * @return результат подстановки
     */
    public Element integralFirstVar(Polynom p, int num, Element elem1, Element elem2, Ring ring) {
        p = p.ordering(ring);
        if (num == 0) {
            Element res1 = p.value(new Element[]{elem2, ring.varPolynom[1], ring.varPolynom[2]}, ring);
            Element res2 = p.value(new Element[]{elem1, ring.varPolynom[1], ring.varPolynom[2]}, ring);
            return (res1 instanceof Polynom) ? res1.subtract(res2, ring) : new Polynom(res1.subtract(res2, ring));

        }
        if (num == 1) {
            Element res1 = p.value(new Element[]{ring.varPolynom[0], elem2, ring.varPolynom[2]}, ring);
            Element res2 = p.value(new Element[]{ring.varPolynom[0], elem1, ring.varPolynom[2]}, ring);
            return (res1 instanceof Polynom) ? res1.subtract(res2, ring) : new Polynom(res1.subtract(res2, ring));
        }

        Element res1 = p.value(new Element[]{ring.varPolynom[0], ring.varPolynom[1], elem2}, ring);
        Element res2 = p.value(new Element[]{ring.varPolynom[0], ring.varPolynom[1], elem1}, ring);
        return (res1 instanceof Polynom) ? res1.subtract(res2, ring) : new Polynom(res1.subtract(res2, ring));
    }

    /**
     * процедура подстановки пределов интегрирования вместо второй переменной
     * @param p - полином от трех переменных
     * @param num1 - номер переменной
     * @param num2 - номер проинтегрированной переменной
     * @param elem1 - первый предел интегрирования
     * @param elem2 - второй предел интегрирования
     * @param ring - кольцо от трех переменных
     * @return результат подстановки
     */
    public Element integralSecondVar(Polynom p, int num1, int num2, Element elem1, Element elem2, Ring ring) {
        p = p.ordering(ring);
        if (num1 == 0) {//переменная, в которую ведется подстановка
            if (num2 == 1) {//замороженная переменная
                Element res1 = p.value(new Element[]{elem2, ring.numberZERO, ring.varPolynom[2]}, ring);
                Element res2 = p.value(new Element[]{elem1, ring.numberZERO, ring.varPolynom[2]}, ring);
                return (res1 instanceof Polynom) ? res1.subtract(res2, ring) : new Polynom(res1.subtract(res2, ring));
            }

            Element res1 = p.value(new Element[]{elem2, ring.varPolynom[1], ring.numberZERO}, ring);
            Element res2 = p.value(new Element[]{elem1, ring.varPolynom[1], ring.numberZERO}, ring);
            return (res1 instanceof Polynom) ? res1.subtract(res2, ring) : new Polynom(res1.subtract(res2, ring));

        }
        if (num1 == 1) {//переменная, в которую ведется подстановка
            if (num2 == 0) {//замороженная переменная
                Element res1 = p.value(new Element[]{ring.numberZERO, elem2, ring.varPolynom[2]}, ring);
                Element res2 = p.value(new Element[]{ring.numberZERO, elem1, ring.varPolynom[2]}, ring);
                return (res1 instanceof Polynom) ? res1.subtract(res2, ring) : new Polynom(res1.subtract(res2, ring));

            }

            Element res1 = p.value(new Element[]{ring.varPolynom[0], elem2, ring.numberZERO}, ring);
            Element res2 = p.value(new Element[]{ring.varPolynom[0], elem1, ring.numberZERO}, ring);
            return (res1 instanceof Polynom) ? res1.subtract(res2, ring) : new Polynom(res1.subtract(res2, ring));

        }
        if (num2 == 0) {//замороженная переменная
            Element res1 = p.value(new Element[]{ring.numberZERO, ring.varPolynom[1], elem2}, ring);
            Element res2 = p.value(new Element[]{ring.numberZERO, ring.varPolynom[1], elem1}, ring);
            return (res1 instanceof Polynom) ? res1.subtract(res2, ring) : new Polynom(res1.subtract(res2, ring));
        }
        Element res1 = p.value(new Element[]{ring.varPolynom[0], ring.numberZERO, elem2}, ring);
        Element res2 = p.value(new Element[]{ring.varPolynom[0], ring.numberZERO, elem1}, ring);
        return (res1 instanceof Polynom) ? res1.subtract(res2, ring) : new Polynom(res1.subtract(res2, ring));

    }

    /**
     * процедура подстановки пределов интегрирования вместо третьей переменной
     * @param p - полином от трех переменных
     * @param num - номер переменной
     * @param elem1 - первый предел интегрирования
     * @param elem2 - второй предел интегрирования
     * @param ring - кольцо от трех переменных
     * @return результат подстановки
     */
    public Element integralThirdVar(Polynom p, int num, Element elem1, Element elem2, Ring ring) {
        p = p.ordering(ring);
        if (num == 0) {
            Element res1 = p.value(new Element[]{elem2, ring.numberZERO, ring.numberZERO}, ring);
            Element res2 = p.value(new Element[]{elem1, ring.numberZERO, ring.numberZERO}, ring);
            return res1.subtract(res2, ring);
        }
        if (num == 1) {
            Element res1 = p.value(new Element[]{ring.numberZERO, elem2, ring.numberZERO}, ring);
            Element res2 = p.value(new Element[]{ring.numberZERO, elem1, ring.numberZERO}, ring);
            return res1.subtract(res2, ring);

        }
        Element res1 = p.value(new Element[]{ring.numberZERO, ring.numberZERO, elem2}, ring);
        Element res2 = p.value(new Element[]{ring.numberZERO, ring.numberZERO, elem1}, ring);
        return res1.subtract(res2, ring);

    }

    /**
     * процедура нахождения массы тела
     * @param p - полином от трех переменных
     * @param b - номера переменных
     * @param c - пределы интегрирования, которые могут быть выражены:
     * по z: полином от двух переменных или от одной переменной или число
     * по y: полином от одной переменных или число
     * по х: число
     * @param ring - кольцо от трех переменных
     * @return масса тела
     */
    public Element objectMass(Polynom p, int[] b, Element[] c, Ring ring) {
        Polynom p1 = (Polynom) threeIntegral(p, b[0], ring);//вычисление интеграла от полинома по первой переменной
        Polynom I_one = (Polynom) integralFirstVar(p1, b[0], c[0], c[1], ring);//подстановка пределов интегрирования вместо первой переменной
        Polynom res1 = threeIntegral(I_one, b[1], ring);//вычисление интеграла от полинома по второй переменной
        Polynom I_two = (Polynom) integralSecondVar(res1, b[1], b[0], c[2], c[3], ring);//подстановка пределов интегрирования вместо второй переменной
        Polynom res2 = threeIntegral(I_two, b[2], ring);//вычисление интеграла от полинома по третей переменной
        Element I_three = integralThirdVar(res2, b[2], c[4], c[5], ring);//подстановка пределов интегрирования вместо третей переменной
        return I_three;
    }

    /**
     * процедура нахождения координат центра массы тела
     * @param p - полином от трех переменных
     * @param m - масса тела
     * @param b - номера переменных
     * @param c - пределы интегрирования, которые могут быть выражены:
     * по z: полином от двух переменных или от одной переменной или число
     * по y: полином от одной переменных или число
     * по х: число
     * @param ring - кольцо от трех переменных
     * @return координаты центра массы
     */
    public Element[] centreObjectMass(Polynom p, Element m, int[] b, Element[] c, Ring ring) {
        Element one = ring.numberONE;//единица
        Element temp = one.divide(m, ring);
        Polynom x = p.multiply(ring.varPolynom[0], ring);
        Polynom y = p.multiply(ring.varPolynom[1], ring);
        Polynom z = p.multiply(ring.varPolynom[2], ring);
        Element x0 = temp.multiply((Element) objectMass(x, b, c, ring), ring);//первая координата центра массы тела
        Element y0 = temp.multiply((Element) objectMass(y, b, c, ring), ring);//вторая координата центра массы тела
        Element z0 = temp.multiply((Element) objectMass(z, b, c, ring), ring);//третья координата центра массы тела
        Element[] centr = new Element[]{x0, y0, z0};//координаты центра массы тела
        return centr;
    }



    /**
     * процедура нахождения общей массы
     * @param p - полином от трех переменных
     * @param c - пределы интегрирования каждого сплайна
     * @param pol - массив сплайнов(полиномов)
     * @param b - номера переменных
     * @param ring - кольцо от трех переменных
     * @return общая масса
     */
    public Element generalMass(Polynom p, Element[][] c, Polynom[][] pol1,Polynom[][] pol2, int[] b, Ring ring) {
        Element genMass = new NumberR64(0);
        for (int i = 0; i < pol1.length; i++) {
            for (int j = 0; j < pol1[i].length; j++) {
                c[i][1] = pol1[i][j];
                c[i][0] = pol2[i][j];
                Element mass = objectMass(p, b, c[i], ring);
                genMass = genMass.add(mass, ring);


            }
        }
        return genMass;
    }

    /**
     * процедура нахождения общего центра массы
     * @param p - полином от трех переменных
     * @param genMass - общая масса тела
     * @param c - пределы интегрирования каждого сплайна
     * @param pol - массив сплайнов
     * @param b - номера переменных
     * @param ring - кольцо от трех переменных
     * @return координаты центра
     */
    public Element[] generalCentreObjectMass(Polynom p, Element genMass, Element[][] c, Polynom[][] pol1,Polynom[][]pol2, int[] b, Ring ring) {
        Element[] temp = new Element[]{new NumberR64(0), new NumberR64(0), new NumberR64(0)};
        for (int i = 0; i < pol1.length; i++) {
            for (int j = 0; j < pol1[i].length; j++) {
                c[i][1] = (Element) pol1[i][j];
                c[i][0] = (Element) pol2[i][j];
                Element temp1 = objectMass(p, b, c[i], ring);//масса тела одной части
                Element[] temp2 = centreObjectMass(p, temp1, b, c[i], ring);//координаты центра массы одной части
                Element[] temp3 = new Element[]{temp1.multiply(temp2[0], ring), temp1.multiply(temp2[1], ring), temp1.multiply(temp2[2], ring)};
                temp = new Element[]{temp[0].add(temp3[0], ring), temp[1].add(temp3[1], ring), temp[2].add(temp3[2], ring)};
            }
        }
        Element[] generalcentre = new Element[]{temp[0].divide(genMass, ring), temp[1].divide(genMass, ring), temp[2].divide(genMass, ring)};
        return generalcentre;
    }

    /**
     * процедура сжимающая и сдвигающая полином от двух переменных
     * @param p - полином от двух переменных
     * @param k1 - коэффициент сжатия по х
     * @param k2 - коэффициент сжатия по у
     * @param b1 - перенос по х
     * @param b2 - перенос по у
     * @param ring - кольцо
     * @return измененный полином
     */
    public Polynom transfer2D(Polynom p, Element k1, Element k2, Element b1, Element b2, Ring ring) {
        Element x = (ring.varPolynom[0].multiply(k1, ring)).add(b1, ring);//x = k1x + b1
        Element y = (ring.varPolynom[1].multiply(k2, ring)).add(b2, ring);//y = k2y + b2
        p = p.ordering(ring);
        Polynom p1 = (Polynom) p.value(new Element[]{x, y}, ring);
        return p1;
    }
   /**
     * процедура сжимающая и сдвигающая полином от трех переменных
     * @param p - полином от трех переменных
     * @param k1 - коэффициент сжатия по х
     * @param k2 - коэффициент сжатия по y
     * @param k3 - коэффициент сжатия по z
     * @param b1 - сдвиг по х
     * @param b2 - сдвиг по y
     * @param b3 - сдвиг по z
     * @param ring - кольцо
     * @return полином
     */
   public Polynom transfer3D(Polynom p, Element k1, Element k2, Element k3, Element b1, Element b2, Element b3, Ring ring) {
        Element x = (ring.varPolynom[0].multiply(k1, ring)).add(b1, ring);//x = k1x + b1
        Element y = (ring.varPolynom[1].multiply(k2, ring)).add(b2, ring);//y = k2y + b2
        Element z = (ring.varPolynom[2].multiply(k3, ring)).add(b3, ring);//z = k3z + b3
        p = p.ordering(ring);
        Polynom p1 = (Polynom) p.value(new Element[]{x, y, z}, ring);
        return p1;
    }

    /**
     * процедура построения массива сплайнов по точкам, а также сжатия и сдвига этих сплайнов
     * @param B_x - сдвиг по х
     * @param B_y - сдвиг по у
     * @param Len_x - длина по х
     * @param Len_y - длина по у
     * @param f - массив точек
     * @param ring - кольцо
     * @return массив сплайнов
     */
    public Polynom[][] splines2D(Element B_x, Element B_y, Element Len_x, Element Len_y, double[][] f, Ring ring){
        spline2D temp;
        temp = new spline2D(f, spline1D.approxType.Linear);
        Polynom[][] p = temp.getPolynoms();
        System.out.println(Array.toString(p,ring));
        Polynom[][]pol = new Polynom[p.length][p.length];
        int n = p.length;
        Element r = new NumberR64(n);
        Element K_x = Len_x.divide(r, ring);
        Element K_y = Len_y.divide(r, ring);
        for(int i = 0; i<p.length; i++){
          for(int j = 0; j< p[i].length; j++){
            pol[i][j] = new ObjectMassAndCentreObjectMass().transfer2D(p[i][j], K_x, K_y, B_x, B_y, ring);
          }
        }
        return pol;
    }
    /**
     * процедура, сжимающая и сдвигающая плотность
     * @param B_x - сдвиг по х
     * @param B_y - сдвиг по у
     * @param B_z - сдвиг по z
     * @param Len_x - длина по х
     * @param Len_y - длина по у
     * @param Len_z - длина по z
     * @param f - трехмерный массив точек
     * @param pol - массив сплайнов
     * @param ring - кольцо
     * @return  плотность
     */
    public Polynom splines3D(Element B_x, Element B_y, Element B_z, Element Len_x, Element Len_y, Element Len_z, double [][][] f, Polynom[][] pol, Ring ring){
       spline3D sp3d;
       sp3d = new spline3D(f);
       int i = 0;
       int j = 0;
       int k = 0;
       Polynom p = sp3d.getPolynom(i, j, k);
       int n = pol.length;
       Element r = new NumberR64(n);
       Element K_x = Len_x.divide(r, ring);
       Element K_y = Len_y.divide(r, ring);
       Element K_z = Len_z.divide(r, ring);
       p = transfer3D(p, K_x, K_y, K_z, B_x, B_y, B_z, ring);
       return p;
}
}
