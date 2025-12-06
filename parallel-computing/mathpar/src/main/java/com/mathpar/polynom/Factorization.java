package com.mathpar.polynom;
import com.mathpar.number.*;
import com.mathpar.number.Element;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;

public class Factorization {
    public static int number = Integer.MAX_VALUE;
    public static int counter = 0;
    public static boolean q = true;
    public static int numberVariable = 0;

    /**
     *
     * Факторизация полинома от одной переменной
     */
    public static FactorPol factorOneVar(Polynom p, Ring ring) {
        if (p.powers[0] == 1) {
            return new FactorPol(new int[] {1}, new Polynom[] {p});
        }
        if(p.powers.length == 1){
            return new FactorPol(new int[] {p.powers[0]}, new Polynom[] {new Polynom(new int[]{1}, new Element[]{new NumberZ(1)})});
        }
        int g = p.powers[0];
        for (int i = 1; i < p.powers.length; i++) {
            if (g > p.powers[i]) {
                g = p.powers[i];
            }
        }
        if (g != 0) {
            for (int i = 0; i < p.powers.length; i++) {
                p.powers[i] = p.powers[i] - g;
            }
        }
        if (p.isItNumber()) {
            return new FactorPol(new int[] {1}, new Polynom[] {p});
        }
        int maxStepP = p.powers[0] + 1;
        Polynom monom[] = new Polynom[maxStepP];
        int StepMonom[] = new int[maxStepP];
        Element coeffs = p.GCDNumPolCoeffs(ring);
        if (coeffs.isOne(ring) && p.coeffs[0].compareTo(NumberZ.ZERO, ring) == -1) {
            coeffs = coeffs.negate(ring);
        }
        if (coeffs != new NumberZ(1)) {
            for (int i = 0; i < p.coeffs.length; i++) {
                p.coeffs[i] = p.coeffs[i].divide(coeffs, ring);
            }
        }
      FactorPol pol = p.FactorPol_SquareFree(ring);
        int currentLen = 0;
        for (int i = 0; i < pol.multin.length; i++) {
            if (!pol.multin[i].isOne(ring)) {
                Polynom factors[] = FactorizationPolynomialWithoutMultipleFactors.Factor(pol.multin[i], ring);
                if (factors[0].isOne(ring)) factors[0] = pol.multin[i];  
                int len = factors.length;
                System.arraycopy(factors, 0, monom, currentLen, len);
                for (int s = 0; s < len; s++) StepMonom[currentLen + s] = pol.powers[i];
                currentLen += len;
            }
        }
        int mas = 0;
        for (int i = 0; i < maxStepP; i++) {
            if (monom[i] != null) {
                mas++;
            }
        }
        if (g != 0) {
            mas++;
        }
        Polynom monom1[] = new Polynom[mas];
        int StepMonom1[] = new int[mas];
        if (g != 0) {
            monom1[0] = new Polynom(new int[] {1}, new Element[] {NumberZ.ONE});
            StepMonom1[0] = g;
            System.arraycopy(monom, 0, monom1, 1, mas - 1);

            System.arraycopy(StepMonom, 0, StepMonom1, 1, mas - 1);

        } else {
            System.arraycopy(monom, 0, monom1, 0, mas);
            System.arraycopy(StepMonom, 0, StepMonom1, 0, mas);
        }
        FactorPol poli = new FactorPol(StepMonom1, monom1);
        return Minuc_one(StepMonom1, monom1, coeffs, ring);
    }

    private static FactorPol Minuc_one(int[] StepMonom1, Polynom[] monom1, Element coeffs,Ring ring) {
        int lenght=StepMonom1.length;
        int y=1;
        for(int i=0; i<lenght; i++)
          if(StepMonom1[i]%2==0) {if(monom1[i].coeffs[0].intValue()<0)
             monom1[i]=monom1[i].multiply(Polynom.polynomFromNumber(NumberZ.MINUS_ONE,ring),ring);}
          else{ if(monom1[i].coeffs[0].intValue()<0) {monom1[i]=monom1[i]
                  .multiply(Polynom.polynomFromNumber(NumberZ.MINUS_ONE,ring),ring); y=y*(-1);}
          }
        if(coeffs.intValue()!=1 && y==1) {
                int step2[]=new int[lenght+1];
               Polynom monom2[]=new Polynom[lenght+1];
               step2[0]=1;
               System.arraycopy(StepMonom1, 0, step2, 1, lenght);
               monom2[0]=new Polynom(new int[0], new Element[]{coeffs});
               System.arraycopy(monom1, 0, monom2, 1, lenght);
               return new FactorPol(step2,monom2);
        }
   return  new FactorPol(StepMonom1,monom1);}

//    private static FactorPol Minuc_one(int[] StepMonom1, Polynom[] monom1, Element coeffs, Ring ring) {
//        int lenght = StepMonom1.length;
//        int y = 1;
//        for (int i = 0; i < lenght; i++) {
//            if (StepMonom1[i] % 2 == 0) {
//                if (monom1[i].coeffs[0].intValue() < 0) {
//                    monom1[i] = monom1[i].multiply(Polynom.polynomFromNumber(NumberZ.MINUS_ONE, ring), ring);
//                }
//            } else {
//                if (monom1[i].coeffs[0].intValue() < 0) {
//                    monom1[i] = monom1[i].multiply(Polynom.polynomFromNumber(NumberZ.MINUS_ONE, ring), ring);
//                    y *= -1;
//                }
//            }
//        }
//        if (coeffs.intValue() != 1 && y == 1) {
//            int step2[] = new int[lenght + 1];
//            Polynom monom2[] = new Polynom[lenght + 1];
//            step2[0] = 1;
//            System.arraycopy(StepMonom1, 0, step2, 1, lenght);
//            monom2[0] = new Polynom(new int[0], new Element[] {coeffs});
//            System.arraycopy(monom1, 0, monom2, 1, lenght);
//            return new FactorPol(step2, monom2);
//        }
//        if (y == -1) {
//            int step[] = new int[lenght + 1];
//            Polynom monom[] = new Polynom[lenght + 1];
//            step[0] = 1;
//            System.arraycopy(StepMonom1, 0, step, 1, lenght);
//            monom[0] = new Polynom(new int[0], new Element[] {new NumberZ(-1)});
//            if (coeffs.intValue() != 1) {
//                monom[0] = monom[0].multiply(new Polynom(coeffs), ring);
//            }
//            System.arraycopy(monom1, 0, monom, 1, lenght);
//            return new FactorPol(step, monom);
//        }
//        return new FactorPol(StepMonom1, monom1);
//    }

    public static Polynom[][] Method(Polynom p, int f[][], Ring R) {
        int len_p = p.powers.length / p.coeffs.length;
        int maxStep[] = get_Over(p, len_p);
        numberVariable = maxStep[maxStep.length - 1];
        int max[] = new int[maxStep.length - 1];
        int razmer = 1;
        for (int i = 0; i < maxStep.length - 1; i++) {
            razmer *= maxStep[i] + 1;
            max[i] = maxStep[i] + 1;
        }
        int len = maxStep.length;

        Polynom mas[][] = new Polynom[razmer][maxStep[len - 1]];

        Polynom rezalt[][] = new Polynom[razmer][number];
        for (int i = 0; i < razmer; i++) {
            System.arraycopy(mas[i], 0, rezalt[i], 0, number);
        }
        return rezalt;
    }

    public static Polynom finding(Polynom p, Element mas[], Ring ring) {
        int h = mas.length;
        int pow_len = p.powers.length;
        int coef_len = p.coeffs.length;
        Element coef[] = new Element[coef_len];
        System.arraycopy(p.coeffs, 0, coef, 0, coef_len);
        int colper = pow_len / coef_len;
        int power[] = new int[pow_len];
        System.arraycopy(p.powers, 0, power, 0, pow_len);
        for (int i = 0; i < h; i++) {
            int num = i;
            for (int j = 0; j < coef_len; j++) {
                coef[j] = coef[j].multiply(mas[i].pow(power[num + j * colper], ring), ring);
                power[num + j * colper] = 0;
            }
        }
        return new Polynom(new Polynom(power, coef).toString(), ring);
    }

    public static int[] get_Over(Polynom p, int colper) {
        int mas[] = new int[colper];
        for (int i = 0; i < colper; i++) {
            for (int j = 0 + i; j < p.powers.length; j += colper) {
                if (mas[i] < p.powers[j]) {
                    mas[i] = p.powers[j];
                }
            }
        }
        return mas;
    }
//    /**
//     *   Something do FORM with thrown exception when only ONE VARIABLE ????????
//     * @param s
//     * @param maxStep
//     * @param a  -- is a polynomial for factorization
//     * @param rez
//     * @param mas
//     * @param f
//     * @param ring - Ring
//     * @return 
//     */
//    public static boolean form_(int s, int maxStep[], Polynom a, int rez[],
//            Polynom mas[][], int f[][], Ring ring) {
//        int len = maxStep.length + 1;
//        if (s < len - 2) {
//            int n = 1;
//            int z = 0;
//            while (z < maxStep[s]) {
//                boolean b = true;
//                while (b) {
//                    rez[s] = n;
//                    Element x[] = new Element[s + 1];
//                    for (int j = 0; j < s + 1; j++) {
//                        x[j] = new NumberZ(rez[j]);
//                    }
//                    Element new_mas_x[] = new Element[ring.varNames.length];
//                    System.arraycopy(x, 0, new_mas_x, 0, x.length);
//                    for (int j = x.length; j < ring.varNames.length; j++) {
//                        new_mas_x[j] = new Polynom(ring.varNames[j], ring);
//
//                    }
//                    Polynom p = (Polynom) a.value(new_mas_x, ring);
//                    if (numberVariable != p.powers[len - 1]) {n++; continue;}
//                    Element gcd = p.GCDHPolCoeffs(ring);
//                    if (gcd.isOne(ring)) {b = false;n++;} 
//                    else {n++; }
//                } s++;
//                if (form_(s, maxStep, a, rez, mas, f, ring)) { z++; } 
//                else {z = 1;}
//                s--;
//            }
//        } else {
//            int n = 1;
//            int r = 0;
//            while (r < maxStep[len - 2]) {
//                boolean b = true;
//                FactorPol pol;
//                int len_polynom;
//                while (b) {
//                    rez[len - 2] = n;
//                    Element x[] = new Element[len - 1];
//                    for (int j = 0; j < len - 1; j++) {
//                        x[j] = new NumberZ(rez[j]);
//                    }
//                    Element new_mas_x[] = new Element[x.length + 1];
//                    System.arraycopy(x, 0, new_mas_x, 0, x.length);
//                    new_mas_x[x.length] = new Polynom(ring.varNames[x.length], ring);
//                    Polynom p = (Polynom) a.value(new_mas_x, ring);
//                    Element gcd = p.GCDHPolCoeffs(ring);
//                    if (gcd.isOne(ring)) {
//                        b = false;
//                        // int newVarLength = p.powers.length / p.coeffs.length;
//                        int newVar[] = new int[p.powers.length / p.coeffs.length];
//                        //    for (int j = 0; j < newVarLength; j++) {newVar[j] = -1;  }
//                        Polynom poly = p.denseVariables(newVar);
//                        if (poly.powers[0] != numberVariable) {n++; break; }
//                        pol = factorOneVar(poly, ring);
//                        pol.sorting(ring);
//                        len_polynom = pol.multin.length;
//                        if (number > len_polynom) {number = len_polynom;
//                            r = 0; counter = 0;  q = false;
//                        } else {q = true;}
//                        if (number == 1) {
//                            // если один множитель, то выброси исключение  ?????????????????????????????????
//                            number = Integer.MAX_VALUE;
//                            throw new ArithmeticException("Division by zero.");
//                        }
//                        if (number == len_polynom) {
//                            for (int i = 0; i < pol.multin.length; i++) {
//                                if (pol.multin[i].coeffs.length == 1) {
//                                    mas[counter][i] = new Polynom(new int[] {pol.powers[i]}, new Element[] {pol.multin[i].coeffs[0]});
//                                    continue;
//                                }
//
//                                mas[counter][i] = pol.multin[i];
//                            }
//                            r++;
//                            System.arraycopy(rez, 0, f[counter], 0, rez.length);
//                            counter++;
//                        }
//                        n++;
//                    } else {
//                        n++;
//                    }
//                }
//            }
//            return q;
//        }
//        return q;
//    }

//    private static Polynom getpolynom(Polynom polynom, int y, Ring ring) {
//        int num = polynom.coeffs.length;
//        int powers[] = new int[num * ring.varNames.length];
//        int u = 0;
//        for (int i = y; i < powers.length; i += ring.varNames.length) {
//            powers[i] = polynom.powers[u];
//            u++;
//        }
//        return new Polynom(powers, polynom.coeffs);
//    }
//    
    public static void main(String[] args) {
        Ring ring= new Ring("C[x]");
        Polynom p=new Polynom("x-1",ring);
        p.coeffs[1]= new Complex (new NumberR("128"), 
                new NumberR("17"), ring);
          System.out.println("out="+p.factor(ring));
    }

    public static class number {
        public number() {
        }
    }
}
