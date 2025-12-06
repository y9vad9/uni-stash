package com.mathpar.students.OLD.stud2014.titov;


import com.mathpar.func.F;
import com.mathpar.func.FvalOf;
import com.mathpar.number.NumberR64;
import com.mathpar.number.Ring;
import com.mathpar.number.Element;
import com.mathpar.number.NumberR;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author РїРє
 */
public class SolveLEX {

    public static void main(String[] args) {
        Ring r = new Ring("R[x]");
        SolveLEX s = new SolveLEX();
        System.out.println(s.solve(new F("(x-2)(x-7)", r), new NumberR(3), new NumberR(9), r));
    }
public Element solve (F f , Element left, Element right, Ring r){


        r.FLOATPOS=30;
        Element Eps=new NumberR64(0.0000000001);
        F func = f;
        F dfunc=(F)func.D(r);
        FvalOf fv=new FvalOf(r);
        Element x0=right ;//РїСЂР°РІР°СЏ РіСЂР°РЅРёС†Р°
        Element x1=left;//Р»РµРІР°СЏ РіСЂР°РЅРёС†Р°
        Element[] var=new Element[1];
        var[0]=x1;
        Element s1=new NumberR64(100);
        Element s2=new NumberR64(100);
        int k=0;//СЃС‡РµС‚С‡РёРє С€Р°РіРѕРІ
         while((s1.signum()>0)||(s2.signum()>0)){
             var[0]=x1;
             Element f1=(Element)fv.valOf(func,var); //  func.value(var, r);//Р·Р°С‡РµРЅРёРµ РІ С‚РѕС‡РєРµ С…1
             if(k%2==0){
                x0=x1;
                Element znam=fv.valOf(dfunc,var);//dfunc.value(var, r);//Р·РЅР°С‡РµРЅРёРµ РїСЂРѕРёР·РІРѕРґРЅРѕР№ РІ С‚РѕС‡РєРµ С…1
                x1=x1.subtract(f1.divide(znam, r), r);

            }else{
                Element Xi=x1;
                var[0]=x0;
                Element f0=fv.valOf(func,var);//(NumberR64)func.value(var, r);//Р·РЅР°С‡РµРЅРёРµ РІ С‚РѕС‡РєРµ С…0
                Element chisl=x1.subtract(x0, r);
                chisl=chisl.multiply(f1, r);
                Element znam=f1.subtract(f0, r);
                Xi=x1.subtract(chisl.divide(znam, r), r);
                x0=x1;
                x1=Xi;
            }
            k++;
            var[0]=x1;
            f1=fv.valOf(func,var);//(NumberR64)func.value(var, r);//Р·РЅР°С‡РµРЅРёРµ РІ С‚РѕС‡РєРµ РҐ1
            var[0]=x0;
            Element f0=fv.valOf(func,var);//(NumberR64)func.value(var, r);//Р·РЅР°С‡РµРЅРёРµ РІ С‚РѕС‡РєРµ С…0
            s2=f0.subtract(f1, r);
            s2=s2.abs(r);//РґР»РёРЅР° РѕС‚СЂРµР·РєР° (f0,f1)
            s2=s2.subtract(Eps, r);

            s1=x0.subtract(x1, r);
            s1=s1.abs(r);//РґР»РёРЅР° РѕС‚СЂРµР·РєР° (С…0,С…1)
            s1=s1.subtract(Eps, r);
            //System.out.println(k+"С‹Р№ С€Р°Рі:");
            //System.out.println("x="+x1.toString(r)+" y="+f1.toString(r));

         }
         if (x1.compareTo(left,r)<0 | x1.compareTo(right, r)>0){
             r.exception.append("В данном интервале корень не найден!");
             return Element.NAN;
         }
        return x1;
}

}
