
package com.mathpar.polynom;

import java.util.Arrays;
import com.mathpar.number.Ring;
import com.mathpar.number.*;

public class CreateMassPol {
    public static int number=Integer.MAX_VALUE;
    public static int counter=0;
    public static boolean q=true;
    public static int numberVariable=0; // число переменных в полиноме

 public static Polynom[][] Method(Polynom p, int f[][],Ring R) {
     number=Integer.MAX_VALUE;
     int[] degrees=p.degrees();
     int len=degrees.length;
     numberVariable=degrees[len-1]; // число переменных в полиноме  (немые переменные уже удалили)
     int max[]=new int[len-1];
     int razmer=1;
     for(int i=0; i<degrees.length-1; i++) { max[i]=degrees[i]+1; razmer*=max[i];}
     int rez[]=new int [len-1];
     Polynom mas[][]=new Polynom[razmer][degrees[len-1]];
     boolean y=form(0,max,p,rez, mas,f,R); // ########################################
     Polynom rezalt[][]=new Polynom[razmer][number];
     for(int i=0; i<razmer; i++)
             for(int j=0; j<number; j++) rezalt[i][j]=mas[i][j];
     return rezalt;
 }
 
    /**
     * 
     */ 
    public static boolean form(int s, int maxStep[], Polynom a, int rez[], Polynom mas[][], int f[][], Ring ring) {
        int len = maxStep.length + 1;
        if (s < len - 2) {
            int n = 1;
            int z = 0;
            while (z < maxStep[s]) {
                boolean b = true;
                while (b) {
                    rez[s] = n;
                    Element x[] = new Element[s + 1];
                    for (int j = 0; j < s + 1; j++)  x[j] = new NumberZ(rez[j]);
                    Element new_mas_x[] = new Element[ring.varNames.length];
                    System.arraycopy(x, 0, new_mas_x, 0, x.length);
                    for (int j = x.length; j < ring.varNames.length; j++) new_mas_x[j] = ring.varPolynom[j];  
                    Polynom p = (Polynom) a.value(new_mas_x, ring);
                    if (numberVariable != p.powers[len - 1]) {n++; continue;}
                    Element gcd = p.GCDHPolCoeffs(ring);
                    if (gcd.isItNumber()){
                       Element gcdNumb=(gcd instanceof Polynom)? ((Polynom)gcd).coeffs[0] : gcd;
                       if(gcdNumb instanceof Fraction) gcdNumb= ((Fraction)gcd).num ;
                       if (gcdNumb.isOne(ring))b = false;
                    } 
                    n++;
                }
                s++;
                if (form(s, maxStep, a, rez, mas, f, ring)) { z++;} else { z = 1; }
                s--;
            }
        } else {
            int n = 1;
            int r = 0;
            while (r < maxStep[len - 2]) {
                boolean b = true;
                FactorPol pol = null;
                int len_polynom = 0;
                while (b) {
                    rez[len - 2] = n;
                    Element x[] = new Element[len - 1];
                    for (int j = 0; j < len - 1; j++)  x[j] = new NumberZ(rez[j]);
                    Element new_mas_x[] = new Element[x.length + 1];
                    System.arraycopy(x, 0, new_mas_x, 0, x.length);
                    //    System.out.println("---"+Arrays.toString(new_mas_x));
                    int powers[] = new int[x.length + 1];
                    powers[x.length] = 1;
                    //     System.out.println("AS="+Arrays.toString(powers));
                    Polynom wer = new Polynom(powers, new Element[] {NumberZ.ONE}, ring);
                    //    System.out.println("wer="+wer.toString(ring));
                    new_mas_x[x.length] = wer;
                    //     System.out.println("---"+Arrays.toString(new_mas_x));
                    new_mas_x[x.length] = ring.varPolynom[x.length];  //new Polynom(ring.varNames[x.length], ring);
                    Polynom p = (Polynom) a.value(new_mas_x, ring);
                    //       System.out.println("p="+p.toString(ring));
                    Element gcd = p.GCDHPolCoeffs(ring);
                    //   System.out.println("gsd="+gcd);
                    if (gcd.isOne(ring)) {
                        b = false;
                        int newVarLength = p.powers.length / p.coeffs.length;
                        int newVar[] = new int[newVarLength];
                        //     for (int j = 0; j < newVarLength; j++) newVar[j] = -1;
                        Polynom poly = p.denseVariables(newVar);
                        Ring newR = new Ring(ring, newVar);
                        if (poly.powers[0] != numberVariable) { n++; break;}
                        pol = Factorization.factorOneVar(poly, newR);
                        pol.sorting(newR);
                        /*если получится множитель в кратной степени, то представим его в виде
                         * произведения множителей в первой степени
                         */
                        pol = PowerstoPolynom(pol);
                        for (int j = 0; j < pol.multin.length; j++) 
                            pol.multin[j] = pol.multin[j].danceVariableBack(newVar);                       
                        len_polynom = pol.multin.length;
                        if (number > len_polynom) {number = len_polynom; r = 0; counter = 0; q = false;} 
                        else   q = true;
                        int ypk = 0;
                        if (number == 1) {
                            System.out.println("  если один множитель, то выброси исключение====  ");
                            number = Integer.MAX_VALUE;
                            ypk /= 0;
                        }// если один множитель, то выброси исключение
                        if (number == len_polynom) {
                            for (int i = 0; i < pol.multin.length; i++) mas[counter][i] = pol.multin[i];
                            r++;
                            for (int i = 0; i < rez.length; i++)  f[counter][i] = rez[i];
                            counter++;
                        }  
                      }   
                    n++; 
                }
            }
            return q;
        }
        return q;
    }

    private static FactorPol PowerstoPolynom(FactorPol pol) {
        int len=pol.multin.length;
        int length_pow=0;
        for(int i=0; i<len; i++) length_pow+=pol.powers[i];
        if(len!=length_pow){
           Polynom multin[]=new Polynom[length_pow];
           int powers[]=new int[length_pow];
           int k=0;
           Arrays.fill(powers, 1);
           for(int i=0; i<len; i++){
             for(int j=0; j<pol.powers[i]; j++){
             multin[k]=pol.multin[i];
             k++;
             }
           }
           return new FactorPol(powers, multin);
        }
        return pol;
    }
}



