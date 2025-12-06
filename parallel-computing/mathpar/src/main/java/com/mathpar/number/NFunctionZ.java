
package com.mathpar.number;

import java.util.Vector;

/**
 * Класс SolverZ предназначен для выполнения операций с числами типа BigDecimal
 * <p>Copyright: Copyright (c) ParCA Tambov, 2005,2006</p>
 * <p>Company: ParCA Tambov</p>
 * @author Kryuchin Oleg Vladimirovich
 * @version 0.5
 *
 */


public class NFunctionZ {
    public NFunctionZ() {
    }


    /**
     * Вычисление корня n-степени из положительного числа bi, т.е вычисление (bi)^(1/m), m - целое положтельное число.
     * Результатом является целое число, являющееся точной степенью числа 2,  которое превосходит точный корень не более чем вдвое.
     *
     * @param bi число из которого извлекается корень степени m
     * @param m показатель корня
     * @return ближайшая степень числа 2, не меньшая чем корень степени m
     */

public static NumberZ lessThenDoubledRoot(NumberZ bi, int m){
  byte[] b1=bi.toByteArray();
  byte fb=b1[0];
  int l=b1.length;
  int i=0;

  while(fb!=0){fb>>>=1; i++; }

  int k=(b1.length-1)*8+i;  //(колличество бит в  числе)
  int t=k/m;
  byte[] bb=new byte[t/8+1];
  bb[0]=(byte)(1<<t%8);
  NumberZ b2=new NumberZ(1,bb);
  return b2;
  }



    /**
     * нахождение НОД методом Евклида
     *
     * @param B0 первое число
     * @param B1 второе число
     * @return НОД
     */
    public static NumberZ evklid_(NumberZ B0, NumberZ B1) {
        NumberZ B2;
        int u = 0;
        do {
            u++;
            B2 = B0.remainder(B1);
            B0 = B1;
            B1 = B2;
        } while (!B1.equals(NumberZ.ZERO));
        return B0;
    }

    /**
     * Возведение в степень   ,,,,??????????????????? Убрать!
     *
     * @param base основание степени
     * @param factor показатель степени - число типа int
     * @return степень = (base)^(factor)
     */
    public static NumberZ pow(NumberZ base, int factor) {
        NumberZ b = base;
        NumberZ f = new NumberZ(factor);
        for (NumberZ i = NumberZ.ONE; i.compareTo(f) == -1; i = inc(i))
            b = b.multiply(base);
        return b;
    }

    /**   УБРАТЬ И ЗАВЕСТИ ТАБЛИЦУ ПЕРВЫХ 30 факториалов
     * Вычисление факториала
     *
     * @param base BigInteger основание фактроиала
     * @return BigInteger результат вычисления
     */
    public static NumberZ fact(NumberZ base) {
        NumberZ f = NumberZ.ONE;
        for (NumberZ i = NumberZ.ONE; i.compareTo(base)!=1; i = inc(i))
            f = f.multiply(i);
        return f;
    }

    /**
     * Инкременация (увеличение на 1)
     *
     * @param b число, которе будет инкременировано
     * @return результат инкременации = b+1
     */
    public static NumberZ inc(NumberZ b) {
        return b.add(NumberZ.ONE);
    }

    /**
     * Декременация (уменьшение на 1)
     *
     * @param b число, которе будет декременировано
     * @return результат инкременации = b-1
     */
    public static NumberZ dec(NumberZ b) {
        return b.subtract(NumberZ.ONE);
    }

    /**     УБРАТЬ  !!!!!!!!!!!!!!!!!!!!
     * Нахождение всех чисел на которые аргумент делется без остака
     * @param a BigInteger делимое
     * @return BigInteger[] массив делителей
     */
    public static NumberZ[] FindDivision(NumberZ a) {
     Vector v = new Vector();
     for (NumberZ i=NumberZ.ONE; i.compareTo(a.abs())!=1; i = i.add(NumberZ.ONE))
       if (a.remainder(i).equals(NumberZ.ZERO)) {
         v.add(i);
         v.add(i.negate());
       }
     NumberZ[] d = new NumberZ[v.size()];
     for (int i=0; i<d.length; i++) d[i] = (NumberZ)v.get(i);
     return d;
   }

   /**
     * Нахождение всех чисел на которые аргумент делется без остака
     * @param a long делимое
     * @return long[] массив делителей    УБРАТЬ !!!!!!!!!!!!!!!!!!!!!!
     */
    public static long[] FindDivision(long a) {
     Vector v = new Vector();
     for (long i=1; i<=Math.abs(a); i++)
       if (a%i==0) {
         v.add(new NumberZ64(i));
         v.add(new NumberZ64(-i));
       }
     long[] d = new long[v.size()];
     for (int i=0; i<d.length; i++) d[i] = ((NumberZ64)v.get(i)).longValue();
     return d;
   }




}
