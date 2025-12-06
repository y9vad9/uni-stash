/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.students.OLD.stud2014.lukashin;

import com.mathpar.number.Element;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import com.mathpar.polynom.PascalTriangle;
import com.mathpar.polynom.Polynom;

/**
 *
 * @author Лукашин Владислав
 */
public class Sum {

    public Sum() {
    }

    public static void main(String ar[]){
        Ring r = new Ring("Z[x,y,z]");
        Sum sum=new Sum();
        Polynom rr=sum.SummingOfPolynomial(new Polynom("2x^6+2y+zx",r),new Polynom("x",r), -1, 1, r);
        Polynom rr1=sum.SummingOfPolynomial(new Polynom("2x^2+2yx+zx+z",r),new Polynom("xy",r), -5, 5, r);
        Polynom rr2=sum.SummingOfPolynomial(new Polynom("xy+y^3z+zy",r),new Polynom("xz",r), -5, -1, r);
        Polynom rr3=sum.SummingOfPolynomial(new Polynom("xy+y^2z+zy",r),new Polynom("z",r), 5, 1, r);
        Polynom rr4=sum.SummingOfPolynomial(new Polynom("xy+y^2z+zy",r),new Polynom("x",r), new int[]{-1},new int[]{1}, r);
        Polynom rr5=sum.SummingOfPolynomial(new Polynom("xy+y^2z+zy",r),new Polynom("xz",r), new int[]{-1},new int[]{3}, r);
        Polynom rr6=sum.SummingOfPolynomial(new Polynom("xy+y^2z+zy",r),new Polynom("xz",r), new int[]{-1,2},new int[]{1,3}, r);
        System.out.println("rr="+rr);
        System.out.println("rr1="+rr1);
        System.out.println("rr2="+rr2);
        System.out.println("rr3="+rr3);
        System.out.println("rr4="+rr4);
        System.out.println("rr5="+rr5);
        System.out.println("rr6="+rr6);
    }
    /**
     * считает конечную сумму полинома с помощью биномиальных коеффициентов,
     * если это возможно; иначе считает простым суммированием
     * @param f-полином, который нужно просуммировать
     * @param x-переменная по которой идет суммирование
     * @param beg-начальное значение суммы
     * @param end-конечное значение суммы
     * @param r-кольцо
     * @return
     */

    public  Polynom SummingOfPolynomial(Polynom f,Polynom x,int beg,int end, Ring r){
        //******проверка интервала суммирования*******
        if (beg>end){
           int p = beg;
           beg=end;
           end=p;
       }

        f=(Polynom) f.clone();
        int [] stx=  new int[0];
        int s =0;
        //********выявление переменных, по которым ведется суммирование*******
        for (int i=0;i<x.powers.length;i++)
            if(x.powers[i]!=0){
                int [] st = new int [stx.length+1];
                System.arraycopy(stx, 0, st, 0, stx.length);
                stx = st;
                stx[s]=i;
                s+=1;
            }
        int numbvar=f.powers.length/f.coeffs.length;//кл-во переменных
        Element coef;
        int numcoef;
        //*********вычисление суммы полинома по переменным**********
        for (int i = 0;i<stx.length;i++)
            for (int j = stx[i]; j<f.powers.length;j+=numbvar){
                numcoef=j/numbvar;
                if(f.powers[j]!=0){
                    coef= IntervalSplit(f.powers[j], beg, end,r);
                    f.powers[j]=0;
                    f.coeffs[numcoef]=f.coeffs[numcoef].multiply(coef, r);
                }
                else f.coeffs[numcoef]=f.coeffs[numcoef].multiply(new NumberZ(end-beg+1), r);
            }
        //************форматирование вывода**************
        for (int i = 0 ; i<f.coeffs.length;i++){
            if (f.coeffs[i].equals(NumberZ.ZERO, r)){
                for(int j =0;j<numbvar;j++){
                    f.powers[i*numbvar+j]=0;
                }
            }
        }
        Polynom [] f1=f.getmonoms();
        f=Polynom.polynomZero;
        for (int i=0;i<f1.length;i++)f=f.add(f1[i], r);
        f=f.deleteZeroCoeff(r);
        return f;

    }
    /**
     * Считает конечную сумму полинома по переменным.
     * Для сумм по разным переменным использует разные интервалы суммирования
     * @param f-полином, который нужно просуммировать
     * @param x-переменная по которой идет суммирование
     * @param beg-начальные значения сумм
     * @param end-конечные значения сумм
     * @param r-кольцо
     * @return
     */
    public  Polynom SummingOfPolynomial(Polynom f, Polynom x , int [] beg,int [] end, Ring r){
        f=(Polynom) f.clone();
        x=(Polynom) x.clone();
        int count=0;
        //*******нахождение количества переменных, по которым ведется суммирование******
        for (int i = 0; i<x.powers.length;i++){
            if (x.powers[i]!=0){count++;
            }
        }
        //*******если имеем одну переменную или один интервал суммирования
        //(при нескольких переменных), то обращаемся к методу
        //SummingOfPolynomial(Polynom f,Polynom x,int beg,int end, Ring r)*****
        if (count==1||beg.length==1||end.length==1)return SummingOfPolynomial(f, x, beg[0], end[0], r);
        //******если количество переменных отличается от количества интервалов или количество начальных
//значений не совпадает с количеством конечных, то выводим сообщение об ошибке******
        if (beg.length!=end.length||count!=beg.length){
            System.out.println("неправильно заданы интервалы суммирования");
            return null;
        }
        //******считаем сумму полинома по переменным, причем для каждой переменной
        //свой интервал суммирования**********
        else{
        int j=0;
        int [] powers= new int [x.powers.length];
        Element[] coeffs= new Element[x.coeffs.length];
        for (int i = 0; i<x.powers.length;i++){
            if (x.powers[i]!=0){
                powers[i]=1;
                f=SummingOfPolynomial(f, new Polynom(powers,coeffs), beg[j], end[j], r);
                powers[i]=0;
                j++;
            }
        }
        }
        return f;

    }

    /**
     * находит конечную сумму степени, используя биномиальные коэффициенты
     * например:
     * \sum_{x=0}^{n} x^2=\sum_{x=0}^{n} (2*\binomial(x,2)+\binomial(x,1))=
     * =2*\binomial(n+1,3)+\binomial(n+1,2)
     * коэф-ты перед биномиальными коэф-тами берутся след. образом:
     * i!*A[A.length][i]
     * пример матрицы A для x^7:
     * /1 0  0   0   0   0  0\
     * |1 1  0   0   0   0  0|
     * |1 3  1   0   0   0  0|
     * |1 7  6   1   0   0  0|
     * |1 15 25  10  1   0  0|
     * |1 31 90  65  15  1  0|
     * \1 63 301 350 140 21 1/
     * @param pow-степень переменной,по которой ведется суммирование
     * @param beg-начальное значение суммы
     * @param end-конечное значение суммы
     * @return
     */
    private  static Element SummingUsingBinomial(int pow,int beg,int end,Ring r){
       Element res0 = NumberZ.ZERO;
       Element res1 = NumberZ.ZERO;
       Element res = NumberZ.ZERO;
        int [][]a=new int[pow][pow];/////////создаем матрицу из которой будем
        for(int i = 0;i<pow;i++)a[i][0]=1;///////брать коэффициенты
        h:for(int i = 1; i<pow;i++)///////    по формуле a[i][j]=(j+1)*a[i-1][j]+a[i-1][j-1]
            for (int j = 1; j<pow;j++){///////
                  a[i][j]=(j+1)*a[i-1][j]+a[i-1][j-1];///////
                  if(i==j)continue h;///////
            }/////////
        for (int i = 0 ; i<pow;i++){
            if (end>i)res0 = Element.factorial(r,i+1).multiply(new NumberZ(a[pow-1][i]),r).
                    multiply(PascalTriangle.binomialZ(end+1, i+2),r).add(res0,r);
            if (beg-1>i)res1 = Element.factorial(r,i+1).multiply(new NumberZ(a[pow-1][i]),r).
                    multiply(PascalTriangle.binomialZ(beg, i+2),r).add(res1,r);

        }

        res=res0.subtract(res1,r);
        return res;

    }
   /**
    * находит конечную сумму степени суммированием в цикле
    * @param pow-степень переменной,по которой ведется суммирование
    * @param beg-начальное значение суммы
    * @param end-конечное значение суммы
    * @return результат суммирования
    */
    private static int Metod2(int pow,int beg,int end){//суммирование степени
        int res=0;
        for(int i=beg;i<=end;i++){
            res+=Math.pow(i, pow);
        }
        return res;
    }
    /**
     * <p>разбвает отрезок(beg,end)
     * если beg отрицательный, а end положительный</p>
     * Затам передает управление методам, которые считают сумму
     * степени натурального числа
     * @param pow-степень переменной, по которой ведется суммирование
     * @param beg-начальное значение суммы
     * @param end-конечное значение суммы
     * @return результат суммирования
     */
    private static Element IntervalSplit(int pow,int beg,int end,Ring r){//
        Element res;
        if(beg>0)res =SummingUsingBinomial(pow, beg, end,r);
        else if(end>0){
            int res1=Metod2(pow, beg, 0);
            res =SummingUsingBinomial(pow, 0, end,r);
            res=res.add(new NumberZ(res1),r);
        }

            else res=new NumberZ(Metod2(pow, beg, end));
        return res;
    }
}
