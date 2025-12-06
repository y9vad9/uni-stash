/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.polynom;

import java.util.Arrays;
import java.util.Vector;
import com.mathpar.number.*;

/**
 *
 * @author dmitry
 */
public class PolGCD {
    public static boolean prov(Polynom x, int index, Ring ring){
        int h[]=x.listOfVariables();
        if(h.length==1){
            return false;
        }else{
        for(int i=0; i<h.length; i++) if(h[i]>=index) return true;
        }
    return false;
    }
    public static int[] NOD(Polynom x, int numbervar, Ring ring){
        int g[]=x.listOfVariables();
        if(g.length==1) return new int[]{-1};
           int len=x.powers.length;
           int length=x.coeffs.length;
           int h=len/length;
           if(h<(numbervar+1)) return new int[]{-1};
           int pow_x[]=new int[length];
           int t=1;
           pow_x[0]=x.powers[numbervar];
           for(int i=numbervar+h; i<len; i+=h){
               boolean flag=true;
                for(int j=0; j<t; j++)
                    if(pow_x[j] == x.powers[i]){
                        flag=false; break;
                        }
                if(flag) {pow_x[t]=x.powers[i]; t++;}
           }
           int pow_xf[]=new int[t];
           System.arraycopy(pow_x, 0, pow_xf, 0, t);
    return pow_xf;
    }
    public static Polynom Polynomial_GCD(Polynom x, int numbervar, int degvar1, int degvar2, Ring ring) {
           int len=x.powers.length;
           int length=x.coeffs.length;
           int h=len/length;
           int pow_x1[]=new int[len];
           Element coef_x1[]=new Element[length];
           int pow_x2[]=new int[len];
           Element coef_x2[]=new Element[length];
           int t=0;
           int t2=0;
           int tp=0;
           int tp2=0;
              for(int i=numbervar; i<len; i+=h){
                  if(x.powers[i]==degvar1){
                   coef_x1[t]=x.coeffs[i/h];
                   t++;
                   for(int j=0; j<h; j++) if (j !=numbervar) {pow_x1[tp]=x.powers[i-i%h+j];   tp++;}else{pow_x1[tp]=0;   tp++;}
                  }else
                  if(x.powers[i]==degvar2){
                     coef_x2[t2]=x.coeffs[i/h];
                   t2++;
                   for(int j=0; j<h; j++) if (j !=numbervar) {pow_x2[tp2]=x.powers[i-i%h+j];   tp2++;}else{pow_x2[tp2]=0;   tp2++;}
                  }
              }
           int pow_xf[]=new int[tp];
           System.arraycopy(pow_x1, 0, pow_xf, 0, tp);
           Element coef_xf[]=new Element[t];
           System.arraycopy(coef_x1, 0, coef_xf, 0, t);
           Polynom first=new Polynom(pow_xf,coef_xf);
                first=first.ordering(ring);
                first=first.deleteZeroCoeff(ring);
                first=first.normalNumbVar(ring);
                int pow_xf2[]=new int[tp2];
           System.arraycopy(pow_x2, 0, pow_xf2, 0, tp2);
           Element coef_xf2[]=new Element[t2];
           System.arraycopy(coef_x2, 0, coef_xf2, 0, t2);
           Polynom second=new Polynom(pow_xf2,coef_xf2);
                second=second.ordering(ring);
                second=second.deleteZeroCoeff(ring);
                second=second.normalNumbVar(ring);
              //  System.out.println(first);
            //    System.out.println("----------------");
          //      System.out.println(second);
    return first.gcd(second, ring);
    }
    public static Polynom Polynomial_GCDone(Polynom x, int numbervar, int degvar1, Ring ring) {
           int len=x.powers.length;
           int length=x.coeffs.length;
           int h=len/length;
           int pow_x1[]=new int[len];
           Element coef_x1[]=new Element[length];
           int t=0;
           int tp=0;
              for(int i=numbervar; i<len; i+=h){
                  if(x.powers[i]==degvar1){
                   coef_x1[t]=x.coeffs[i/h];
                   t++;
                   for(int j=0; j<h; j++) if (j !=numbervar) {pow_x1[tp]=x.powers[i-i%h+j];   tp++;} else{pow_x1[tp]=0;   tp++;}
                  }
              }
           int pow_xf[]=new int[tp];
           System.arraycopy(pow_x1, 0, pow_xf, 0, tp);
           Element coef_xf[]=new Element[t];
           System.arraycopy(coef_x1, 0, coef_xf, 0, t);
           Polynom first=new Polynom(pow_xf,coef_xf);
                first=first.ordering(ring);
                first=first.deleteZeroCoeff(ring);
                first=first.normalNumbVar(ring);
    return first;
    }
    public static Polynom Polynomial_GCD_three(Polynom x, int numbervar, int degvar1, int degvar2, int degvar3, Ring ring) {
           int len=x.powers.length;
           int length=x.coeffs.length;
           int h=len/length;
           int pow_x1[]=new int[len];
           Element coef_x1[]=new Element[length];
           int pow_x2[]=new int[len];
           Element coef_x2[]=new Element[length];
           int pow_x3[]=new int[len];
           Element coef_x3[]=new Element[length];
           int t=0;
           int t2=0;
           int tp=0;
           int tp2=0;
           int tp3=0;
           int tp23=0;
              for(int i=numbervar; i<len; i+=h){
                  if(x.powers[i]==degvar1){
                   coef_x1[t]=x.coeffs[i/h];
                   t++;
                   for(int j=0; j<h; j++) if (j !=numbervar) {pow_x1[tp]=x.powers[i-i%h+j];   tp++;}else{pow_x1[tp]=0;   tp++;}
                  }else{
                  if(x.powers[i]==degvar2){
                     coef_x2[t2]=x.coeffs[i/h];
                   t2++;
                   for(int j=0; j<h; j++) if (j !=numbervar) {pow_x2[tp2]=x.powers[i-i%h+j];   tp2++;}else{pow_x2[tp2]=0;   tp2++;}
                  }else{
                       if(x.powers[i]==degvar3){
                     coef_x3[tp3]=x.coeffs[i/h];
                   tp3++;
                   for(int j=0; j<h; j++) if (j !=numbervar) {pow_x3[tp23]=x.powers[i-i%h+j];   tp23++;}else{pow_x3[tp23]=0;   tp23++;}
                  }
                  }
              }         }
           int pow_xf[]=new int[tp];
           System.arraycopy(pow_x1, 0, pow_xf, 0, tp);
           Element coef_xf[]=new Element[t];
           System.arraycopy(coef_x1, 0, coef_xf, 0, t);
           Polynom first=new Polynom(pow_xf,coef_xf);
                first=first.ordering(ring);
                first=first.deleteZeroCoeff(ring);
                first=first.normalNumbVar(ring);
                int pow_xf2[]=new int[tp2];
           System.arraycopy(pow_x2, 0, pow_xf2, 0, tp2);
           Element coef_xf2[]=new Element[t2];
           System.arraycopy(coef_x2, 0, coef_xf2, 0, t2);
           Polynom second=new Polynom(pow_xf2,coef_xf2);
                second=second.ordering(ring);
                second=second.deleteZeroCoeff(ring);
                second=second.normalNumbVar(ring);
      //          System.out.println(first+"    "+second);
                first=first.gcd(second, ring);
                if(!first.isOne(ring)){
                     int pow_xf3[]=new int[tp23];
           System.arraycopy(pow_x3, 0, pow_xf3, 0, tp23);
           Element coef_xf3[]=new Element[tp3];
           System.arraycopy(coef_x3, 0, coef_xf3, 0, tp3);
           Polynom s=new Polynom(pow_xf,coef_xf);
                s=s.ordering(ring);
                s=s.deleteZeroCoeff(ring);
                s=s.normalNumbVar(ring);
                return s.gcd(first, ring);
                }else return first;
    }
    public static Polynom[] PrepareFactor(Polynom x, Ring R) {
        int h=x.powers.length/x.coeffs.length;
        int number=1;
        for(int i=0; i<h; i++) number*=2;//максимально возможное число множителей
        Polynom rezalt[]=new Polynom[number];
        rezalt[0]=x;
        int n=1;
        number=1;
        for(int i=0; i<h; i++){
            //System.out.println("i="+i);
            for(int j=0; j<n; j++){
               // long sta=System.currentTimeMillis();
                int deg[]=PolGCD.NOD(rezalt[j], i, R);
               // System.out.println("f="+(System.currentTimeMillis()-sta));
              if(deg[0]==-1) continue;
             //   System.out.println("deg="+Arrays.toString(deg));
                Polynom first=PolGCD.Polynomial_GCD(rezalt[j],i, deg[0],deg[1], R);
                //System.out.println("FF="+first);
                for(int j1=2; j1<deg.length; j1++){
                   if(!first.isOne(R))
                    first=first.gcd(PolGCD.Polynomial_GCDone(rezalt[j], i, deg[j1], R), R); else break;
                }
                if(!first.isOne(R)){
                   // System.out.println("rez="+"   "+first);
                    if(first.coeffs[0].signum()==-1) {first=(Polynom)first.negate(R); //System.out.println("first="+first);
                    }
                   rezalt[number]=rezalt[j].divideExact(first, R);
                    //System.out.println("res=="+rezalt[number]);
                   rezalt[j]=first;
                   number++;
                }
            }
            n=number;
        }
        Polynom otvet[]=new Polynom[number];
        System.arraycopy(rezalt, 0, otvet, 0, number);
//        for(int j=0; j<otvet.length; j++){
//           Element a= otvet[j].coeffs[0].inverse(R);
//           otvet[j]=((Polynom)otvet[j].multiply(a, R)).Mod(R.MOD, R);
//        }
     //   System.out.println("A="+Arrays.toString(otvet));
        return otvet;
    }

    public static Polynom[] PrepareFactorM(Polynom x, Ring R) {
        int h=x.powers.length/x.coeffs.length;
        Polynom first=null;
        int number=1;
        for(int i=0; i<h; i++) number*=2;//максимально возможное число множителей
        Polynom rezalt[]=new Polynom[number];
        rezalt[0]=x;
        int n=1;
        number=1;
        for(int i=0; i<h; i++){
            long dw=System.currentTimeMillis();
            for(int j=0; j<n; j++){  // System.out.println("="+rezalt[j].toString(R) +"    "+ i);
                int deg[]=PolGCD.NOD(rezalt[j], i, R);
              if(deg[0]==-1) continue;

                first=PolGCD.Polynomial_GCD(rezalt[j],i, deg[0],deg[1], R);
                for(int j1=2; j1<deg.length; j1++){
                   if(!first.isOne(R))   first=first.gcd(PolGCD.Polynomial_GCDone(rezalt[j], i, deg[j1], R), R); else break;
                }
                if(!first.isOne(R)){

                   rezalt[number]=rezalt[j].divideExact(first, R);
                   rezalt[j]=first;
                        //              System.out.println("f="+first.toString(R)+"    "+rezalt[number].toString(R));
                   number++;

                }
            }
            n=number;
        //    System.out.println(i+"="+(System.currentTimeMillis()-dw));
        }
        Polynom otvet[]=new Polynom[number];
        System.arraycopy(rezalt, 0, otvet, 0, number);
        for(int j=0; j<otvet.length; j++){
           Element a= otvet[j].coeffs[0].inverse(R);
           otvet[j]=((Polynom)otvet[j].multiply(a, R)).Mod(R.MOD, R);
        }
     //   System.out.println("A="+Arrays.toString(otvet));
        return otvet;
    }
    public static Polynom[] FSFModular(Polynom x, int primes[], Ring r, Ring r1) {
        NumberZ coef=(NumberZ)x.coeffs[0];
          for(int i=1; i<x.coeffs.length; i++){
             if(x.coeffs[i].compareTo(coef, r) ==1) coef=(NumberZ)x.coeffs[i];
          }
          int y=1;
          NumberZ d=new NumberZ(primes[0]);
          while(d.compareTo(coef, r)==-1){
             d=d.multiply(new NumberZ(primes[y]));
             y++;
          }
          System.out.println("y="+y);
          y=3;
          Polynom fs[][]=new Polynom[y][];
        for(int i=0; i<y; i++){          //   System.out.println("y"+coef);
           r1.setMOD32(primes[i]);
           Polynom res=(Polynom)((Polynom)x.clone()).toNewRing(r1.algebra[0], r1);
           //System.out.println("f="+res+"   "+primes[i]);
           //System.out.println("======================");
           fs[i]=PolGCD.PrepareFactorM(res, r1);
            //System.out.println("f="+Arrays.toString(fs[i]));


            //System.out.println("======================"+primes[i]);
        }

        int b=0;
        int kl=0;
        for(int yy=0; yy<fs[0].length; yy++) b+=fs[0][yy].coeffs.length;
        Element coef_fs[][]=new Element[y][b];
        int fpoint[][]=new int[2][b];
        int g1=0;
        for(int i=0; i<fs[0].length; i++){
          for(int j=0; j<fs[0][i].coeffs.length; j++){
              fpoint[0][g1]=j;
              fpoint[1][g1]=i;
              g1++;
          }
        }
        g1=0;
        System.out.println("www="+Arrays.toString(fpoint[0])+Arrays.toString(fpoint[1]));
        for(int i=0; i<y; i++){
        for(int j=0; j<fs[0].length; j++){
           System.arraycopy(fs[i][j].coeffs, 0, coef_fs[i],kl, fs[i][j].coeffs.length);
           kl+=fs[i][j].coeffs.length;
        }
        System.out.println("A="+Arrays.toString(coef_fs[i]));
        kl=0;
        }
     //   System.out.println("aaa="+coef_fs[0][2]+"    "+coef_fs[2][0]);
        int s=6; //кол-во процессоров
        int a=b/s;
        int c=b%s;
        System.out.println("a="+a+"  c="+c+"   b="+b+"   y="+y);
        if(c!=0){
          for(int i=0; i<c; i++){
             Element coefs[][]=new Element[a+1][y];
             int point[][]=new int[2][a+1];
             System.arraycopy(fpoint[0], g1, point[0], 0, a+1);
             System.arraycopy(fpoint[1], g1, point[1], 0, a+1);
             g1+=a+1;
        System.out.println("www1="+Arrays.toString(point[0])+Arrays.toString(point[1]));
             for(int j=0; j<y; j++)
              for(int l=0; l<a+1; l++)
                 coefs[l][j]=coef_fs[j][l+i*(a+1)];
             for(int j=0; j<coefs.length; j++) System.out.println("AS="+Arrays.toString(coefs[j]));
              System.out.println("======================");
          }
          b=c*(a+1);
            System.out.println("+++++++++++++++++++++++++");
          for(int i=0; i<s-c; i++){
              Element coefs[][]=new Element[a][y];

             int point[][]=new int[2][a];
             System.arraycopy(fpoint[0], g1, point[0], 0, a);
             System.arraycopy(fpoint[1], g1, point[1], 0, a);
             g1+=a;
        System.out.println("www1="+Arrays.toString(point[0])+Arrays.toString(point[1]));
             for(int j=0; j<y; j++)
              for(int l=0; l<a; l++)
                 coefs[l][j]=coef_fs[j][l+i*(a)+b];
             for(int j=0; j<coefs.length; j++) System.out.println("AS="+Arrays.toString(coefs[j]));
              System.out.println("======================");
          }
        }

        long tttr=System.currentTimeMillis();
        Polynom v[]=new Polynom[y];
        int pd[]=new int[y];
        Polynom res[]=new Polynom[fs[0].length];
        for(int j=0; j<fs[0].length; j++){
        for(int i=0; i<y; i++){ v[i]=fs[i][j];
         pd[i]=primes[i];
        }

        res[j]=Newton.recoveryNewtonPolynom(pd, v, r1);
       // System.out.println("res="+res[j]);
        }
        System.out.println("QW="+(System.currentTimeMillis()-tttr));
        return res;
    }
       public static Polynom[] NewPrepareFactor(Polynom x, Ring R) {
        int h=x.powers.length/x.coeffs.length;
        Polynom first=null;
        int number=1;
        for(int i=0; i<h; i++) number*=2;//максимально возможное число множителей
        Polynom rezalt[]=new Polynom[number];
        rezalt[0]=x;
        int n=1;
        number=1;
        for(int i=0; i<h; i++){
            //System.out.println("i="+i);
            for(int j=0; j<n; j++){

                int deg[]=PolGCD.NOD(rezalt[j], i, R);

              if(deg[0]==-1) continue;

                first=PolGCD.Polynomial_GCD(rezalt[j],i, deg[deg.length-2],deg[deg.length-1], R);
                    first=(Polynom)first.divide(first.GCDNumPolCoeffs(R), R);

                if(!first.isOne(R)){
                   // System.out.println("rez="+"   "+first);

                    long m=System.currentTimeMillis();
                   rezalt[number]=rezalt[j].divideExact(first, R);
                 //  System.out.println("t="+(System.currentTimeMillis()-m));
                    //System.out.println("res=="+rezalt[number]);
                   rezalt[j]=first;
                   number++;
                }

            }
            n=number;
        }
        Polynom otvet[]=new Polynom[number];
        System.arraycopy(rezalt, 0, otvet, 0, number);
        return otvet;
    }
 static void  NewPrepareFactor1(Polynom x, Vector<Polynom> multin, Ring R) {
        int h=x.powers.length/x.coeffs.length;
        Polynom first=null;
        int number=1;
        int s=multin.size();
        multin.add(x);
        int n=1;
        for(int i=0; i<h; i++){
            for(int j=s; j<n; j++){
                int deg[]=PolGCD.NOD(multin.get(j), i, R);
                if(deg[0]==-1) continue;
                first=PolGCD.Polynomial_GCD(multin.get(j),i, deg[0],deg[deg.length-1], R);
                if(!first.isOne(R))
                    for(int g=1; g<deg.length-1; g++){
                    Polynom first1=PolGCD.Polynomial_GCDone(multin.get(j), i, g, R);
                    first=first.gcd(first1, R);
                    if(first.isOne(R)) break;
                    }
                first=(Polynom)first.divide(first.GCDNumPolCoeffs(R), R);
                if(!first.isOne(R)){
                   multin.add(multin.get(j).divideExact(first, R));
                   multin.set(j, first);
                   number++;
                }
            }
            n=number;
        }
    }
}
