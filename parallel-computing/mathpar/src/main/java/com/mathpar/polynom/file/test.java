
package com.mathpar.polynom.file;

import com.mathpar.number.Ring;
import java.io.*;
import java.util.Random;

import com.mathpar.number.NumberZ;

/**
 *
 * @author Pozdnikin Alexey
 */
public class test {

  static void print(String s){
     System.out.println(s);
  }

  public static void main(String[] args) throws Exception{
    long time=0;
    Runtime r = Runtime.getRuntime();
    print("количество байт памяти доступной = "+r.totalMemory());
    r.gc();
    /*long mem1 = r.totalMemory();
    print("количество байт памяти свободной = "+r.freeMemory());
    */
    BasePolynomDir directory = new BasePolynomDir();
    //System.out.println(fname.getPolynomDirFile().getAbsoluteFile());
    int maxpowers1[] = new int[2];
    maxpowers1[0]=2;
    maxpowers1[1]=2;
    int maxpowers2[] = new int[2];
    maxpowers2[0]=2;
    maxpowers2[1]=2;
    //maxpowers2[2]=2;
    //maxpowers2[3]=3;
    Random rnd = new Random(2);

    /***************** test divRecurse *********************
    Ring rx = new Ring("Z[x]");
    Polynom f = new Polynom(rx, "8x^6+26x^5+43x^4+58x^3+38x^2+23x+12");
    Polynom g = new Polynom(rx, "4x^3+7x^2+x+4");
    System.out.println("f = "+ f);
    System.out.println("g = "+ g);
  //  Polynom fdivg = f.divRecurse(g);
  //  System.out.println("f/g = "+ fdivg);
    */
//    Ring ring=Ring.ringZxyz;
//    Polynom pol = new Polynom(maxpowers1, 100, 8, rnd, NumberR.ONE, ring);
//    print(" polynom = " + pol.toString());
//    //print("байт памяти занимает полином  = "+(mem1 - r.freeMemory()));
//
//    //long mem2 = r.totalMemory();
//    File tofpol = new File(directory.getPolynomDirFile(), "tofpol");
//    FPolynom ff = pol.toFPolynom(tofpol, NumberZ.ONE);
//    print(" fpol = " + );
    //print("байт памяти занимает файловый полином  = "+(mem2 - r.freeMemory()));


    FPolynom.itsCoeffOne = NumberZ.ONE;
    Ring ring=Ring.ringZxyz;

    print("******* <1> Генерация случайных файловых полиномов ******** ");

    File file1 = new File(directory.getPolynomDirFile(), "file1");
    FPolynom fpol1 = new FPolynom(file1, maxpowers1, 100, 8, rnd, NumberZ.ONE);
    print(" fpol1 = "+fpol1.toPolynom());

    File file2 = new File(directory.getPolynomDirFile(), "file2");
    FPolynom fpol2 = new FPolynom(file2, maxpowers2, 100, 8, rnd, NumberZ.ONE);
    print(" fpol2 = "+fpol2.toPolynom());


    //print("количество байт памяти потраченной на генерацию полинома  = "+(mem1 - r.freeMemory()));

    print("**************** <2> Сумма полиномов ********************** ");

    File file3 = new File(directory.getPolynomDirFile(), "file3");
    FPolynom.filenameResult = file3;
    FPolynom.itsCoeffOne = NumberZ.ONE;
    //print(" add = " + FPolynom.toString((FPolynom)fpol1.add(fpol2)));

    time = System.currentTimeMillis();
    fpol1.add(fpol2);
    time =  System.currentTimeMillis() - time;
    print("время файлового сложения = "+time);


//    r.gc();
//    print("количество байт памяти свободной = "+r.freeMemory());
//    //long mem = r.freeMemory();
//    print("генерация...");
//    Ring ring=Ring.ringZxyz;
//    Polynom pol1 = new Polynom(maxpowers1, 100, 8, rnd, NumberZ.ONE,ring);
//    //print(" pol1 = " + pol1.toString(1));
//    Polynom pol2 = new Polynom(maxpowers2, 100, 8, rnd, NumberZ.ONE,ring);
//    //print(" pol2 = " + pol2.toString(1));
//
//    //print(" sum = " + pol1.add(pol2).toString(1));
//    print("сложение...");
//    time = System.currentTimeMillis();
//    pol1.add(pol2,ring);
//    time = System.currentTimeMillis()-time;
//    print("время сложения в ОП = "+time);
//    //print("затраченный объем памяти = "+(mem - r.freeMemory()));


    print("************** <3> Произведение полиномов ***************** ");
    r.gc();
    print("количество байт памяти свободной = "+r.freeMemory());
    File file4 = new File(directory.getPolynomDirFile(), "file4");
    FPolynom.filenameResult = file4;
    //print(" mul = " + FPolynom.toString((FPolynom)fpol1.multiply(fpol2)));

    time = System.currentTimeMillis();
    fpol1.multiply(fpol2, ring);
    time =  System.currentTimeMillis() - time;
    print("время файлового умножения = "+time);

    /*r.gc();
    mem = r.freeMemory();
    time = System.currentTimeMillis();
    pol1.mulSS(pol2);
    time = System.currentTimeMillis()-time;
    print("время умножения в ОП = "+time);
    print("затраченный объем памяти = "+(mem - r.freeMemory()));
    */
  }

}
