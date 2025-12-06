/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.students.OLD.stud2014.shmeleva;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import com.mathpar.func.F;
import com.mathpar.number.Element;
import com.mathpar.number.NumberR64;
import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;

/**
 *
 * @author shmeleva
 */
public class test_integral {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//         Ring ring111 = new Ring("R64[x]");
//         ring111.setDefaulRing();
////        //===========================Number 1=======================================
////        Polynom p11111 = new Polynom("2x+1",ring111);
////        Element t = Integral.integral(p11111,0, ring111);
////        System.out.println("t = " + t.toString(ring111));
////        Element solve_S = Integral.solve_IntS_oneVars(p11111, new Element[]{new NumberR64(0),new NumberR64(2)}, ring111);
////        System.out.println("S for [" +p11111.toString(ring111) +"]  Polynom = " + solve_S);
////        //===========================Number 2=======================================
////        Polynom p21111 = new Polynom("100",ring111);
////        Element t1 = Integral.integral(p21111,0, ring111);
////        System.out.println("t = " + t1.toString(ring111));
////        Element solve_S1 = Integral.solve_IntS_oneVars(p21111, new Element[]{new NumberR64(-1),new NumberR64(5)}, ring111);
////        System.out.println("S for [" +p21111.toString(ring111) +"]  Polynom = " + solve_S1);
//        //======================================================================
//        //======================================================================
//        //======================================================================
//        //======================================================================
//        //======================================================================
//        //======================================================================
//        //======================================================================
//          Ring ring = new Ring("R64[x,y,z]");
//          ring.setDefaulRing();
//          Polynom pol1 = new Polynom("5xy", ring);
//          int k = 0;
//          Element result = new Integral().pervB(pol1, k, ring);
//          System.out.println("integral ___  po " + k + " = " + result.toString(ring));
//
//          Ring ring1 = new Ring("R64[x,y]");
//          ring1.setDefaulRing();
//          Polynom pol2 = new Polynom("0", ring1);
//          int n = 10;
//          Element a = new NumberR64(0);
//          Element b = new NumberR64(1);
//          Element result1 = new Integral().simpson(pol2,a,b, n, ring1);
//          System.out.println("integral ___  Simpson  = " + result1.toString(ring1));
//          System.out.println("   " + new Integral().solve_Integralfortwovars(pol1, a, b, 0, 1, a, b, ring));
//          System.out.println("====================№1==========================");
//          Ring ring_for_S = new Ring("R64[x,y]");
//          ring_for_S.setDefaulRing();
//          Polynom p_for_S = new Polynom("x^2-xy", ring_for_S);
//          Element a_x2 = new NumberR64(2);
//          Element a_x3 = new NumberR64(3);
//          Element a_x0 = new Polynom("4-2y", ring_for_S);
//          Element a_x1 = new Polynom("9-3y", ring_for_S);
//          Element result_1 = Integral.solve_IntS_twoVars(p_for_S, a_x0, a_x1, 0, 1, a_x2, a_x3, ring_for_S);
//          System.out.println("S1 = " + result_1 + " кв.ед");
//          System.out.println("====================№2==========================");
//          Polynom p_for_S1 = new Polynom("x^2-xy-5", ring_for_S);
//          Element a_x21 = new NumberR64(0);
//          Element a_x31 = new NumberR64(1);
//          Element a_x01 = new Polynom("-5", ring_for_S);
//          Element a_x11 = new Polynom("-y-4", ring_for_S);
//          Element result_11 = Integral.solve_IntS_twoVars(p_for_S1, a_x01, a_x11, 0, 1, a_x21, a_x31, ring_for_S);
//          System.out.println("S2 = " + result_11 + " кв.ед");
//          System.out.println("====================№3==========================");
//          Polynom p_for_S2 = new Polynom("55x-y^2", ring_for_S);
//          Element a_x22 = new NumberR64(-10);
//          Element a_x32 = new NumberR64(10);
//          Element a_x02 = new Polynom("-550-y^2", ring_for_S);
//          Element a_x12 = new Polynom("550-y^2", ring_for_S);
//          Element result_12 = Integral.solve_IntS_twoVars(p_for_S2, a_x02, a_x12, 0, 1, a_x22, a_x32, ring_for_S);
//          System.out.println("S3 = " + result_12 + " кв.ед");
//          System.out.println("====================№4==========================");
//          Polynom p_for_S3 = new Polynom("xy+2", ring_for_S);
//          Element a_x23 = new NumberR64(-5);
//          Element a_x33 = new NumberR64(10);
//          Element a_x03 = new Polynom("-5y+2", ring_for_S);
//          Element a_x13 = new Polynom("10y+2", ring_for_S);
//          Element result_13 = Integral.solve_IntS_twoVars(p_for_S3, a_x03, a_x13, 0, 1, a_x23, a_x33, ring_for_S);
//          System.out.println("S4 = " + result_13 + " кв.ед");
//          System.out.println("====================№5==========================");
//          Polynom p_for_S4 = new Polynom("x^3y^3+x^2y^2+xy-666", ring_for_S);
//          Element a_x24 = new NumberR64(1);
//          Element a_x34 = new NumberR64(0);
//          Element a_x04 = new Polynom("-y^3+y^2-y-666", ring_for_S);
//          Element a_x14 = new Polynom("-666", ring_for_S);
//          Element result_14 = Integral.solve_IntS_twoVars(p_for_S4, a_x04, a_x14, 0, 1, a_x24, a_x34, ring_for_S);
//          System.out.println("S5 = " + result_14 + " кв.ед");
//          System.out.println("====================№6==========================");
//          Ring ring11 = new Ring("R64[x,y]");
//          ring11.setDefaulRing();
//          Polynom pol21 = new Polynom("2x", ring1);
//          int n1 = 10;
//          Element a111 = new NumberR64(2);
//          Element b111 = new NumberR64(3.5);
//          Element result111 = new Integral().simpson(pol21,a111,b111, n1, ring11);
//          Polynom pol11 = new Polynom("2x", ring11);
//          int k1 = 0;
//          Element result11 = new Integral().integral_(pol11, a111,b111,k1, ring11);
//          System.out.println("integral ___  po " + k1 + " = " + result11.toString(ring11));
//          System.out.println("integral ___  Simpson  = " + result111.toString(ring11));
//
//          /*===================================================================*/
//        System.out.println("================Пример №1========================");
//        Ring r1 = new Ring("R64[x,y,z,v,w]");
//        r1.setDefaulRing();
//        Polynom p1 = new Polynom("2", r1);
//        Element res1 = Integral.pervB(p1, 1, r1);
//
//        System.out.println("Определенный интеграл = " + res1.toString(r1));
//        System.out.println("================Пример №2========================");
//        Ring r2 = new Ring("R64[x,y,z,v,w]");
//        r2.setDefaulRing();
//        Polynom p2 = new Polynom("2xy^3+4z-6xzv+50w^7+34y", r2);
//        Element res2 = Integral.pervB(p2, 1, r2);
//        System.out.println("Определенный интеграл = " + res2.toString(r2));
//        System.out.println("=== " + Integral.value_integral(p2, 1, new Element[]{new NumberR64(2),new NumberR64(3)}, r2));
//        System.out.println("================Пример №3========================");
//        Ring r3 = new Ring("R64[x,y,z,v,w]");
//        r3.setDefaulRing();
//        Polynom p3 = new Polynom("4z-6x+2y", r2);
//        Element res3 = Integral.pervB(p3, 1, r3);
//        System.out.println("Определенный интеграл = " + res3.toString(r3));
//        System.out.println("=== " + Integral.value_integral(p3, 1, new Element[]{new NumberR64(100),new NumberR64(200)}, r3));
//
//        System.out.println("================Пример №4========================");
//        Ring r31 = new Ring("R64[x,y,z,v,w]");
//        r31.setDefaulRing();
//        Polynom p31 = new Polynom("9x-xy+3z", r3);
//        Element res31 = Integral.pervB(p31, 1, r31);
//        System.out.println("Определенный интеграл = " + res31.toString(r31));
//        System.out.println("=== " + Integral.value_integral(p31, 1, new Element[]{new NumberR64(2),new NumberR64(3)}, r31));
//         System.out.println("================Пример №5========================");
//        Ring r41 = new Ring("R64[x,y,z,v,w]");
//        r41.setDefaulRing();
//        Polynom p41 = new Polynom("-5zw-5vxy", r31);
//        Element res41 = Integral.pervB(p31, 1, r41);
//        System.out.println("Определенный интеграл = " + res41.toString(r41));
//        System.out.println("=== " + Integral.value_integral(p41, 1, new Element[]{new NumberR64(2),new NumberR64(3)}, r41));
//        System.out.println("================Пример №6========================");
//        Ring r51 = new Ring("R64[x,y,z,v,w]");
//        r51.setDefaulRing();
//        Polynom p51 = new Polynom("-z^8w+xy^2-7", r41);
//        Element res51 = Integral.pervB(p31, 1, r51);
//        System.out.println("Определенный интеграл = " + res51.toString(r51));
//        System.out.println("=== " + Integral.value_integral(p51, 1, new Element[]{new NumberR64(2),new NumberR64(3)}, r51));
//        System.out.println("================Пример №7========================");
//        Ring r61 = new Ring("R64[x,y,z,v,w]");
//        r61.setDefaulRing();
//        Polynom p61 = new Polynom("x^2+y^2-9", r51);
//        Element res61 = Integral.pervB(p61, 1, r61);
//        System.out.println("Определенный интеграл = " + res61.toString(r61));
//        System.out.println("=== " + Integral.value_integral(p61, 1, new Element[]{new NumberR64(0),new NumberR64(10)}, r61));
//        System.out.println("================Пример №8========================");
//        Ring r71 = new Ring("R64[x,y,z,v,w]");
//        r71.setDefaulRing();
//        Polynom p71 = new Polynom("x^2+y^2-z^2+w^2", r61);
//        Element res71 = Integral.pervB(p71, 1, r71);
//        System.out.println("Определенный интеграл = " + res71.toString(r71));
//        System.out.println("=== " + Integral.value_integral(p71, 1, new Element[]{new NumberR64(-1),new NumberR64(1)}, r71));
//        System.out.println("================Пример №9========================");
//        Ring r81 = new Ring("R64[x,y,z,v,w]");
//        r81.setDefaulRing();
//        Polynom p81 = new Polynom("x^2+y^2-z^2+w^2", r81);
//        Element res81 = Integral.pervB(p81, 1, r81);
//        System.out.println("Определенный интеграл = " + res81.toString(r81));
//        System.out.println("=== " + Integral.value_integral(p81, 1, new Element[]{new NumberR64(1),new NumberR64(10)}, r81));
//        System.out.println("==============Физический смысл тройного интеграла №1====================");
//        Ring r4 = new Ring("R64[x,y,z]");
//        r4.setDefaulRing();
//        Polynom p_33 = new Polynom("1+x+y", r4);
//        Polynom p_f33 = new Polynom("1-x-y", r4);//ограничевающая плоскость по Z
//        Element[] a33 = new Element[]{new NumberR64(0),new NumberR64(0),new NumberR64(0)};//ограничевающие плоскости
//        Element result_for_3 = Integral.solve_V(p_33, p_f33, a33, ring);
//        System.out.println("V = " + result_for_3.toString(r4));
//
//        System.out.println("==============Тройной интеграл====================");
//        Ring r5 = new Ring("R64[x,y,z]");
//        r5.setDefaulRing();
//        Polynom p_331 = new Polynom("1+x+y+3z^2", r5);
//        Element[] a331 = new Element[]{new NumberR64(0),new NumberR64(1),new NumberR64(0),new NumberR64(2),new NumberR64(0),new NumberR64(3)};
//        Element result_for_31 = Integral.solve_Integralforthreevars(p_331, a331, ring);
//        System.out.println("тройной интеграл = " + result_for_31.toString(r5));
//
//        System.out.println("==============Криволинейные координаты====================");
//        Ring r6 = new Ring("R64[x,y]");
//        r6.setDefaulRing();
//        Polynom[] p_3316 = new Polynom[2];
//        p_3316[0] = new Polynom("x^2+y", r6);
//        p_3316[1] = new Polynom("-y^2-x", r6);
//        Element[][] a6 = new Element[2][];
//        a6[0]=new Element[3];
//        a6[1]=new Element[3];
//                a6[0][0] = new NumberR64(1);
//                a6[0][1] = new NumberR64(1);
//                a6[0][2] = new NumberR64(3);
//                a6[1][0] = new NumberR64(5);
//                a6[1][1] = new NumberR64(2);
//                a6[1][2] = new NumberR64(5);
//
//
//
//        Element result_for_316 = Integral.solve_kr_3(p_3316, a6, r6);
//        System.out.println("Интеграл:криволинейные координаты = " + result_for_316.toString(r6));
//
//         System.out.println("==============Криволинейные координаты====================");
//        Ring r7 = new Ring("R64[x,y]");
//        r7.setDefaulRing();
//        Polynom[][] p_33167 = new Polynom[2][];
//        p_33167[0] = new Polynom[1];
//        p_33167[0][0] = new Polynom("x^2+y^2", r7);
//        p_33167[1] = new Polynom[1];
//        p_33167[1][0] = new Polynom("x^2+y^2", r7);
//        Element[][] a_33167 = new Element[2][];
//
//        a_33167[0] = new Element[9];
//        a_33167[0][0] = Polynom.polynomZero;
//        a_33167[0][1] = new NumberR64(0);
//        a_33167[0][2] = new NumberR64(2);
//        a_33167[0][3] = new Polynom("2x-4", ring);
//        a_33167[0][4] = new NumberR64(2);
//        a_33167[0][5] = new NumberR64(4);
//        a_33167[0][6] = new Polynom("4", ring);
//        a_33167[0][7] = new NumberR64(4);
//        a_33167[0][8] = new NumberR64(0);
//
//        a_33167[1] = new Element[3];
//        a_33167[1][0] = new NumberR64(4);
//        a_33167[1][1] = new NumberR64(0);
//        a_33167[1][2] = Polynom.polynomZero;
//
//
//        Element result_for_3167 = Integral.solve_kr_4(p_33167, a_33167, r7);
//        System.out.println("Интеграл:криволинейные координаты = " + result_for_3167.toString(r7));
        //================================================================
        F ff1 = new F("x^2+y^2",new Ring("R64[x,y]"));
        F ff2 = new F("x^4+4y",new Ring("R64[x,y]"));
        Polynom[] ppp1 = new Polynom[]{new Polynom("t^2", new Ring("R64[t]")),new Polynom("5t", new Ring("R64[t]"))};
        Element res_kr = Integral.curvilinear_integral(ff1, ff2, new NumberR64(0), new NumberR64(2), ppp1);
        System.out.println("res_kr == " + res_kr);
        ///////////////////////////////////////////
        F ff3 = new F("z^2/(x^2+y^2)",new Ring("R64[x,y,z]"));
        F[] ppp2 = new F[]{new F("\\cos(t)", new Ring("R64[t]")),new F("\\sin(t)", new Ring("R64[t]")),new F("t", new Ring("R64[t]"))};
        Element res_kr1 = Integral.curvilinear_integral3D(ff3, new NumberR64(0), new NumberR64(6), ppp2);
        System.out.println("res_kr3D == " + res_kr1);
        ///////////////////////////////////////////
        F ff4 = new F("x-y",new Ring("R64[x,y]"));
        Polynom[] ppp3 = new Polynom[]{
            new Polynom("t", new Ring("R64[t]")),
            new Polynom("0.75t", new Ring("R64[t]"))
        };
        Element res_kr2 = Integral.curvilinear_integral2D(ff4, new NumberR64(0), new NumberR64(4), ppp3);
        System.out.println("res_kr2D == " + res_kr2);
    }

}
