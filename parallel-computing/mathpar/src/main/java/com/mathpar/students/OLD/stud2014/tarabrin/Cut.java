/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.tarabrin;

import com.mathpar.polynom.*;
import com.mathpar.number.*;
import java.util.ArrayList;

/**
 *
 * @author Тарабрин Владимир 25 группа ТГУ'11
 * вычисление массива точек сечения двух полиномов s1 и s2
 *
 */
public class Cut {

    public Cut() {
    }

    public static Element[][] cut(Polynom s1, Polynom s2, NumberR64 t, NumberR64 xmin, NumberR64 xmax, NumberR64 ymin,
            NumberR64 ymax, Ring ring) {

        Element[][] xy = Cut.cutS(s1, s2, t, xmin, xmax, ymin, ymax, ring);

        ArrayList<Element> x1 = new ArrayList<Element>();
        ArrayList<Element> x2 = new ArrayList<Element>();
        ArrayList<Element> x3 = new ArrayList<Element>();
        ArrayList<Element> x4 = new ArrayList<Element>();
        ArrayList<Element> y1 = new ArrayList<Element>();
        ArrayList<Element> y2 = new ArrayList<Element>();
        ArrayList<Element> y3 = new ArrayList<Element>();
        ArrayList<Element> y4 = new ArrayList<Element>();

        ArrayList<ArrayList<Element>> lres = new ArrayList<ArrayList<Element>>();


        for (int i = 0; i < xy.length; i++) {
            Element y = ymin.add(((new NumberR64(i)).multiply(t, ring)), ring);
            switch (xy[i].length) {
                case (3): {
                    if (i == 0 || i == 1) {
                        ArrayList<Element> X1 = new ArrayList<Element>();
                        ArrayList<Element> X2 = new ArrayList<Element>();
                        ArrayList<Element> X3 = new ArrayList<Element>();
                        ArrayList<Element> Y1 = new ArrayList<Element>();
                        ArrayList<Element> Y2 = new ArrayList<Element>();
                        ArrayList<Element> Y3 = new ArrayList<Element>();
                        X1.add(xy[i][0]);
                        Y1.add(y);
                        X2.add(xy[i][1]);
                        Y2.add(y);
                        X3.add(xy[i][2]);
                        Y3.add(y);
                        lres.add(X1);
                        lres.add(Y1);
                        lres.add(X2);
                        lres.add(Y2);
                        lres.add(X3);
                        lres.add(Y3);
                        break;
                    }


                    if(xy[i-1].length ==0){
                        ArrayList<Element> X1 = new ArrayList<Element>();
                        ArrayList<Element> Y1 = new ArrayList<Element>();
                        ArrayList<Element> X2 = new ArrayList<Element>();
                        ArrayList<Element> Y2 = new ArrayList<Element>();
                        ArrayList<Element> X3 = new ArrayList<Element>();
                        ArrayList<Element> Y3 = new ArrayList<Element>();
                        X1.add(xy[i][0]);
                        X2.add(xy[i][1]);
                        X3.add(xy[i][2]);
                        Y1.add(y);
                        Y2.add(y);
                        Y3.add(y);
                        lres.add(X1);
                        lres.add(Y1);
                        lres.add(X2);
                        lres.add(Y2);
                        lres.add(X3);
                        lres.add(Y3);
                        break;
                    }


                    if (xy[i - 1].length == 1) {
                        int poz = 0;
                        for (int k = 0; k < lres.size() - 1; k++) {
                            if ((k + 2) % 2 == 0) {
                                Boolean u1 = xy[i - 1][0].compareTo(lres.get(k).get(lres.get(k).size() - 1), 0, ring);
                                if (u1) {
                                    poz = k;
                                }
                            }
                        }
                        Element d1 = (xy[i][0].subtract(lres.get(poz).get(lres.get(poz).size() - 1), ring)).abs(ring);
                        Element d2 = (xy[i][1].subtract(lres.get(poz).get(lres.get(poz).size() - 1), ring)).abs(ring);
                        Element d3 = (xy[i][2].subtract(lres.get(poz).get(lres.get(poz).size() - 1), ring)).abs(ring);
                        Element[] sort = new Element[]{d1, d2, d3};
                        sort = Array.sortUp(sort, ring);
                        Boolean u1= false;
                        Boolean u2= false;
                        Boolean u3= false;

                        if (d1.compareTo(sort[0], 0, ring)) {
                            lres.get(poz).add(xy[i][0]);
                            lres.get(poz + 1).add(y);
                            ArrayList<Element> X2 = new ArrayList<Element>();
                            ArrayList<Element> Y2 = new ArrayList<Element>();
                            ArrayList<Element> X3 = new ArrayList<Element>();
                            ArrayList<Element> Y3 = new ArrayList<Element>();
                            X2.add(xy[i][1]);
                            Y2.add(y);
                            lres.add(X2);
                            lres.add(Y2);
                            X3.add(xy[i][2]);
                            Y3.add(y);
                            lres.add(X3);
                            lres.add(Y3);
                            u1=true;

                        }
                        if (d2.compareTo(sort[0], 0, ring)) {
                            lres.get(poz).add(xy[i][1]);
                            lres.get(poz + 1).add(y);
                            ArrayList<Element> X2 = new ArrayList<Element>();
                            ArrayList<Element> Y2 = new ArrayList<Element>();
                            ArrayList<Element> X3 = new ArrayList<Element>();
                            ArrayList<Element> Y3 = new ArrayList<Element>();
                            X2.add(xy[i][0]);
                            Y2.add(y);
                            lres.add(X2);
                            lres.add(Y2);
                            X3.add(xy[i][2]);
                            Y3.add(y);
                            lres.add(X3);
                            lres.add(Y3);
                            u2=true;


                        }
                        if (d3.compareTo(sort[0], 0, ring)) {
                            lres.get(poz).add(xy[i][2]);
                            lres.get(poz + 1).add(y);
                            ArrayList<Element> X2 = new ArrayList<Element>();
                            ArrayList<Element> Y2 = new ArrayList<Element>();
                            ArrayList<Element> X3 = new ArrayList<Element>();
                            ArrayList<Element> Y3 = new ArrayList<Element>();
                            X2.add(xy[i][1]);
                            Y2.add(y);
                            lres.add(X2);
                            lres.add(Y2);
                            X3.add(xy[i][0]);
                            Y3.add(y);
                            lres.add(X3);
                            lres.add(Y3);
                            u3 = true;


                        }
                        if(u2){
                            int  c =i;
                            for(int k =i; k <= xy.length-1; k++){
                                if(xy[k].length ==3){
                                    c=k;
                                }else{
                                    break;
                                }
                            }
                           for(int k=i; k<=c; k++){
                               Element d11 = (xy[k][0].subtract(lres.get(poz).get(lres.get(poz).size()-1), ring)).abs(ring);
                               Element d12 = (xy[k][0].subtract(lres.get(poz+2).get(lres.get(poz+2).size()-1), ring)).abs(ring);
                               Element d13 = (xy[k][0].subtract(lres.get(poz+4).get(lres.get(poz+4).size()-1), ring)).abs(ring);
                               Element[] sort11 = new Element[]{d11,d12,d13};
                               sort11 = Array.sortUp(sort11, ring);
                               if(sort11[0].compareTo(d11, 0, ring)){
                                   lres.get(poz).add(xy[i][0]);
                                   lres.get(poz+1).add(y);
                               }
                               if(sort11[0].compareTo(d12, 0, ring)){
                                   lres.get(poz+2).add(xy[i][0]);
                                   lres.get(poz+3).add(y);
                               }
                               if(sort11[0].compareTo(d13, 0, ring)){
                                   lres.get(poz+4).add(xy[i][0]);
                                   lres.get(poz+5).add(y);
                               }
                           }
                            Element x = lres.get(poz).get(lres.get(poz).size()-1);
                            Element a = lres.get(poz+2).get(lres.get(poz+2).size()-1);
                            Element fx = lres.get(poz+1).get(lres.get(poz+1).size()-1);
                            Element fa = lres.get(poz+3).get(lres.get(poz+3).size()-1);

                        }


                        break;

                    }


                    if (xy[i - 1].length == 2) {




                        break;
                    }



                    if (xy[i - 1].length == 3) {
                        int poz = 0;
                        int poz1 = 0;
                        int poz2 = 0;
                        Boolean u1= false;
                        Boolean u2 = false;
                        Boolean u3 = false;
                        Boolean f1 = false;
                        Boolean f2 = false;
                        Boolean f3 = false;
                        for (int k = 0; k < lres.size() - 1; k++) {
                            if ((k + 2) % 2 == 0) {
                                u1 = xy[i - 1][0].compareTo(lres.get(k).get(lres.get(k).size() - 1), 0, ring);
                                if (u1) {
                                    poz = k;
                                    f1=true;
                                }
                                u2 = xy[i - 1][1].compareTo(lres.get(k).get(lres.get(k).size() - 1), 0, ring);
                                if (u2) {
                                    poz1 = k;
                                    f2=true;
                                }
                                 u3 = xy[i - 1][2].compareTo(lres.get(k).get(lres.get(k).size() - 1), 0, ring);
                                if (u3) {
                                    poz2 = k;
                                    f3 = true;
                                }
                            }
                        }
                        if (f1 & f2 & f3) { // если все предыдущие 3 корня распределились по 3 кривым.
                            Element d11 = (xy[i][0].subtract(lres.get(poz).get(lres.get(poz).size() - 1), ring)).abs(ring);
                            Element d12 = (xy[i][1].subtract(lres.get(poz).get(lres.get(poz).size() - 1), ring)).abs(ring);//
                            Element d13 = (xy[i][2].subtract(lres.get(poz).get(lres.get(poz).size() - 1), ring)).abs(ring);
                            Element[] sort1 = new Element[]{d11, d12, d13};
                            sort1 = Array.sortUp(sort1, ring);
                            if (sort1[0].compareTo(d11, 0, ring)) {
                                lres.get(poz).add(xy[i][0]);
                                lres.get(poz + 1).add(y);

                            }
                            if (sort1[0].compareTo(d12, 0, ring)) {
                                lres.get(poz).add(xy[i][1]);
                                lres.get(poz + 1).add(y);
                            }
                            if (sort1[0].compareTo(d13, 0, ring)) {
                                lres.get(poz).add(xy[i][2]);
                                lres.get(poz + 1).add(y);
                            }
                            Element d21 = (xy[i][0].subtract(lres.get(poz1).get(lres.get(poz1).size() - 1), ring)).abs(ring);
                            Element d22 = (xy[i][1].subtract(lres.get(poz1).get(lres.get(poz1).size() - 1), ring)).abs(ring);//
                            Element d23 = (xy[i][2].subtract(lres.get(poz1).get(lres.get(poz1).size() - 1), ring)).abs(ring);
                            Element[] sort2 = new Element[]{d21, d22, d23};
                            sort2 = Array.sortUp(sort2, ring);
                            if (sort2[0].compareTo(d21, 0, ring)) {
                                lres.get(poz1).add(xy[i][0]);
                                lres.get(poz1 + 1).add(y);
                            }
                            if (sort2[0].compareTo(d22, 0, ring)) {
                                lres.get(poz1).add(xy[i][1]);
                                lres.get(poz1 + 1).add(y);
                            }
                            if (sort2[0].compareTo(d23, 0, ring)) {
                                lres.get(poz1).add(xy[i][2]);
                                lres.get(poz1 + 1).add(y);
                            }
                            Element d31 = (xy[i][0].subtract(lres.get(poz2).get(lres.get(poz2).size() - 1), ring)).abs(ring);
                            Element d32 = (xy[i][1].subtract(lres.get(poz2).get(lres.get(poz2).size() - 1), ring)).abs(ring);//
                            Element d33 = (xy[i][2].subtract(lres.get(poz2).get(lres.get(poz2).size() - 1), ring)).abs(ring);
                            Element[] sort3 = new Element[]{d31, d32, d33};
                            sort3 = Array.sortUp(sort3, ring);
                            if (sort3[0].compareTo(d31, 0, ring)) {
                                lres.get(poz2).add(xy[i][0]);
                                lres.get(poz2 + 1).add(y);
                            }

                            if (sort3[0].compareTo(d32, 0, ring)) {
                                lres.get(poz2).add(xy[i][1]);
                                lres.get(poz2 + 1).add(y);
                            }

                            if (sort3[0].compareTo(d33, 0, ring)) {
                                lres.get(poz2).add(xy[i][2]);
                                lres.get(poz2 + 1).add(y);
                            }

                            break;

                        }
                    }




                }
                case (2): {
                    if (i == 0) {
                        break;
                    }
                    if (i == 1) {
                        ArrayList<Element> X1 = new ArrayList<Element>();
                        ArrayList<Element> Y1 = new ArrayList<Element>();
                        ArrayList<Element> X2 = new ArrayList<Element>();
                        ArrayList<Element> Y2 = new ArrayList<Element>();
                        X1.add(xy[i][0]);
                        Y1.add(y);
                        X2.add(xy[i][1]);
                        Y2.add(y);
                        lres.add(X1);
                        lres.add(Y1);
                        lres.add(X2);
                        lres.add(Y2);
                        break;
                    }
                    if (xy[i - 1].length == 0) {





                        if ((xy.length != i) && xy[i + 1].length == 1) {
                            ArrayList<Element> X1 = new ArrayList<Element>();
                            ArrayList<Element> Y1 = new ArrayList<Element>();
                            X1.add(xy[i][0]);
                            Y1.add(y);
                            ArrayList<Element> X2 = new ArrayList<Element>();
                            ArrayList<Element> Y2 = new ArrayList<Element>();
                            X2.add(xy[i][1]);
                            Y2.add(y);
                            lres.add(X1);
                            lres.add(Y1);
                            lres.add(X2);
                            lres.add(Y2);
                            break;

                        }


                        if ((xy.length != i) && xy[i + 1].length == 2) {
                            Element p1 = proiz(xy[i + 1][0], xy[i][0], y.add(t, ring), y, ring);
                            Element p2 = proiz(xy[i + 1][1], xy[i][1], y.add(t, ring), y, ring);
                            if ((p1.isNegative()) & (!p2.isNegative())) {
                                int c = i;
                                for (int k = i; k < xy.length; k++) {
                                    if (xy[k].length == 2) {
                                        c = k;

                                    } else {
                                        break;
                                    }
                                }
                                ArrayList<Element> X1 = new ArrayList<Element>();
                                ArrayList<Element> Y1 = new ArrayList<Element>();
                                ArrayList<Element> buf1 = new ArrayList<Element>();
                                ArrayList<Element> buf2 = new ArrayList<Element>();
                                ArrayList<Element> buf3 = new ArrayList<Element>();
                                ArrayList<Element> buf4 = new ArrayList<Element>();

                                for (int k = i; k <= c; k++) {
                                    X1.add(xy[k][0]);
                                    Y1.add(ymin.add((new NumberR64(k)).multiply(t, ring), ring));
                                }
                                for (int k = i; k <= c; k++) {

                                    buf1.add(xy[k][1]);
                                    buf2.add(ymin.add((new NumberR64(k)).multiply(t, ring), ring));


                                }
                                for (int k = X1.size() - 1; k >= 0; k--) {
                                    buf3.add(X1.get(k));
                                    buf4.add(Y1.get(k));
                                }
                                for (int k = 0; k <= buf2.size() - 1; k++) {
                                    buf3.add(buf1.get(k));
                                    buf4.add(buf2.get(k));

                                }
                                X1 = buf3; //начало справа конец слева на графике.
                                Y1 = buf4;
                                buf1 = new ArrayList<Element>();
                                buf2 = new ArrayList<Element>();
                                lres.add(X1);
                                lres.add(Y1);
                                i = c;
                                break;
                            } else {
                                ArrayList<Element> X1 = new ArrayList<Element>();
                                ArrayList<Element> Y1 = new ArrayList<Element>();
                                ArrayList<Element> X2 = new ArrayList<Element>();
                                ArrayList<Element> Y2 = new ArrayList<Element>();
                                X1.add(xy[i][0]);
                                X2.add(xy[i][1]);
                                Y1.add(y);
                                Y2.add(y);
                                lres.add(X1);
                                lres.add(Y1);
                                lres.add(X2);
                                lres.add(Y2);
                                break;
                            }
                        }


                    }


                    if (xy[i - 1].length == 3) {
                        int poz = 0;
                        int poz1 = 0;
                        int poz2 = 0;
                        Boolean u1 = false;
                        Boolean u2 = false;
                        Boolean u3 = false;
                        for (int k = 0; k <= lres.size() - 1; k++) {
                            if ((k + 2) % 2 == 0) {
                                Boolean f1 = xy[i - 1][0].compareTo(lres.get(k).get(lres.get(k).size() - 1), 0, ring);
                                Boolean f2 = xy[i - 1][1].compareTo(lres.get(k).get(lres.get(k).size() - 1), 0, ring);
                                Boolean f3 = xy[i - 1][2].compareTo(lres.get(k).get(lres.get(k).size() - 1), 0, ring);
                                if (f1) {
                                    poz = k;
                                    u1 = true;
                                }
                                if (f2) {
                                    poz1 = k;
                                    u2 = true;
                                }
                                if (f3) {
                                    poz2 = k;
                                    u3 = true;
                                }
                            }
                        }
                        // if(u1&u2&u3){

                        Element d11 = (xy[i][0].subtract(lres.get(poz).get(lres.get(poz).size() - 1), ring)).abs(ring);
                        Element d12 = (xy[i][0].subtract(lres.get(poz1).get(lres.get(poz1).size() - 1), ring)).abs(ring);
                        Element d13 = (xy[i][0].subtract(lres.get(poz2).get(lres.get(poz2).size() - 1), ring)).abs(ring);
                        Boolean f11 = false;
                        Boolean f12 = false;
                        Boolean f13 = false;


                        Element[] sort1 = new Element[]{d11, d12, d13};
                        sort1 = Array.sortUp(sort1, ring);
                        if (d11.compareTo(sort1[0], 0, ring)) {
                            lres.get(poz).add(xy[i][0]);
                            lres.get(poz + 1).add(y);
                            f11 = true;
                        }
                        if (d12.compareTo(sort1[0], 0, ring)) {
                            lres.get(poz1).add(xy[i][0]);
                            lres.get(poz1 + 1).add(y);
                            f12 = true;
                        }
                        if (d13.compareTo(sort1[0], 0, ring)) {
                            lres.get(poz2).add(xy[i][0]);
                            lres.get(poz2 + 1).add(y);
                            f13 = true;
                        }

                        Element d21 = (xy[i][1].subtract(lres.get(poz).get(lres.get(poz).size() - 1), ring)).abs(ring);
                        Element d22 = (xy[i][1].subtract(lres.get(poz1).get(lres.get(poz1).size() - 1), ring)).abs(ring);
                        Element d23 = (xy[i][1].subtract(lres.get(poz2).get(lres.get(poz2).size() - 1), ring)).abs(ring);
                        Boolean f21 = false;
                        Boolean f22 = false;
                        Boolean f23 = false;


                        Element[] sort2 = new Element[]{d21, d22, d23};
                        sort1 = Array.sortUp(sort1, ring);
                        if (d21.compareTo(sort2[0], 0, ring)) {
                            lres.get(poz).add(xy[i][1]);
                            lres.get(poz + 1).add(y);
                            f21 = true;
                        }
                        if (d22.compareTo(sort2[0], 0, ring)) {
                            lres.get(poz1).add(xy[i][1]);
                            lres.get(poz1 + 1).add(y);
                            f22 = true;
                        }
                        if (d23.compareTo(sort2[0], 0, ring)) {
                            lres.get(poz2).add(xy[i][1]);
                            lres.get(poz2 + 1).add(y);
                            f23 = true;
                        }

                        Element[] sort11 = new Element[]{x1.get(x1.size() - 1), x2.get(x2.size() - 1), x3.get(x3.size() - 1)};
                        sort11 = Array.sortUp(sort11, ring);
                        if ((sort11[0].compareTo(x1.get(x1.size() - 1), 0, ring)) & sort11[1].compareTo(x2.get(x2.size() - 1), 0, ring)) {

                            if (f11 & f23) {
                                Element p1 = proiz(x1.get(x1.size() - 1), x1.get(x1.size() - 1 - 1), y1.get(y1.size() - 1), y1.get(y1.size() - 1 - 1), ring);
                                Element p2 = proiz(x1.get(x1.size() - 1), x2.get(x2.size() - 1), y1.get(y1.size() - 1), y2.get(y2.size() - 1), ring);
                                Element p3 = proiz(x3.get(x3.size() - 1), x3.get(x3.size() - 1 - 1), y3.get(y3.size() - 1), y3.get(y3.size() - 1 - 1), ring);
                                if ((!p1.isNegative()) & p2.isNegative() & (!p3.isNegative())) {
                                    for (int k = x2.size() - 1; k >= 0; k--) {
                                        x1.add(x2.get(k));
                                        y1.add(y2.get(k));
                                    }
                                    for (int k = 0; k < x3.size() - 1; k++) {
                                        x1.add(x3.get(k));
                                        y1.add(y3.get(k));
                                    }
                                    x2 = null;
                                    y2 = null;
                                    x3 = null;
                                    y3 = null;
                                    break;
                                }
                                if ((p1.isNegative()) & (!p2.isNegative()) & (p3.isNegative())) {
                                    for (int k = x2.size() - 1; k >= 0; k--) {
                                        x3.add(x2.get(k));
                                        y3.add(y2.get(k));
                                    }
                                    for (int k = 0; k <= x1.size() - 1; k++) {
                                        x3.add(x1.get(k));
                                        y3.add(y1.get(k));
                                    }
                                    x1 = null;
                                    x2 = null;
                                    y1 = null;
                                    y2 = null;
                                    x1 = x3;
                                    y1 = y3;
                                    x3 = null;
                                    y3 = null;
                                    break;
                                }


                            }
                        }



                    }

                    if (xy[i - 1].length == 1) {
                        int poz = 0;
                        int poz1 = 0;
                        Boolean f1 = false;
                        Boolean f2 = false;
                        Boolean f3 = false;
                        Boolean f4 = false;


                        for (int k = 0; k <= lres.size() - 1; k++) {

                            if ((k + 2) % 2 == 0) {
                                f1 = xy[i - 1][0].compareTo((lres.get(k)).get(lres.get(k).size() - 1), 0, ring);
                                f2 = xy[i - 1][0].compareTo((lres.get(k)).get(0), 0, ring);
                                if (f1) {
                                    poz = k;
                                    f3 = true;
                                }
                                if (f2) {
                                    poz1 = k;
                                    f4 = true;
                                }
                            }

                        }
                        Boolean u1 = false;
                        Boolean u2 = false;
                        int Poz =0;
                        int Poz1=0;
                        int Poz2=0;
                        if(xy[i-2].length ==2){
                            for(int k =0; k<=lres.size() -1; k++){
                                for (int kk =0; kk<lres.get(k).size()-1; kk++){
                                    u1 = lres.get(k).get(kk).compareTo(xy[i-2][0], 0, ring);
                                    u2 = lres.get(k).get(kk).compareTo(xy[i-2][1], 0, ring);
                                    if (f1 | f2 ) {
                                        Poz = k;

                                    }
                                    if(f1 ){
                                       Poz1 = kk;
                                    }
                                    if(f2){
                                        Poz2 = kk;
                                    }

                                }
                            }
                            if ((i !=2) && xy[i - 2].length == 2) {
                            Element p1 = proiz(xy[i + 1][0], xy[i][0], y.add(t, ring), y, ring);
                            Element p2 = proiz(xy[i + 1][1], xy[i][1], y.add(t, ring), y, ring);
                            if ((p1.isNegative()) & (!p2.isNegative())) {
                                int c = i;
                                for (int k = i; k < xy.length; k++) {
                                    if (xy[k].length == 2) {
                                        c = k;

                                    } else {
                                        break;
                                    }
                                }
                                ArrayList<Element> X1 = new ArrayList<Element>();
                                ArrayList<Element> Y1 = new ArrayList<Element>();
                                ArrayList<Element> buf1 = new ArrayList<Element>();
                                ArrayList<Element> buf2 = new ArrayList<Element>();
                                ArrayList<Element> buf3 = new ArrayList<Element>();
                                ArrayList<Element> buf4 = new ArrayList<Element>();
                                X1.add(xy[i-2][0]);
                                Y1.add(y.subtract((new NumberR64(2)).multiply(t), ring));

                                for (int k = i; k <= c; k++) {
                                    X1.add(xy[k][0]);
                                    Y1.add(ymin.add((new NumberR64(k)).multiply(t, ring), ring));
                                }
                                for (int k = i; k <= c; k++) {
                                    buf1.add(xy[k][1]);
                                    buf2.add(ymin.add((new NumberR64(k)).multiply(t, ring), ring));

                                }
                                for (int k = X1.size() - 1; k >= 0; k--) {
                                    buf3.add(X1.get(k));
                                    buf4.add(Y1.get(k));
                                }
                                for (int k = 0; k <= buf2.size() - 1; k++) {
                                    buf3.add(buf1.get(k));
                                    buf4.add(buf2.get(k));

                                }
                                X1 = buf3;
                                Y1 = buf4;
                                buf1 = new ArrayList<Element>();
                                buf2 = new ArrayList<Element>();

                               lres.add(X1);
                               lres.add(Y1);




                                i = c;
                                break;

                            } else {
                                ArrayList<Element> X1 = new ArrayList<Element>();
                                ArrayList<Element> Y1 = new ArrayList<Element>();
                                ArrayList<Element> X2 = new ArrayList<Element>();
                                ArrayList<Element> Y2 = new ArrayList<Element>();
                                X1.add(xy[i][0]);
                                X2.add(xy[i][1]);
                                Y1.add(y);
                                Y2.add(y);
                                lres.add(X1);
                                lres.add(Y1);
                                lres.add(X2);
                                lres.add(Y2);
                                break;


                            }


                            }


                            break;
                        }

                        if ((xy.length != i) && (xy[i + 1].length == 1)) {
                            if (f3) {
                                Element d1 = xy[i][0].subtract(lres.get(poz).get(lres.get(poz).size() - 1), ring);
                                Element d2 = xy[i][1].subtract(lres.get(poz).get(lres.get(poz).size() - 1), ring);
                                if (d1.compareTo(d2, -2, ring)) {
                                    lres.get(poz).add(xy[i][0]);
                                    lres.get(poz + 1).add(y);
                                    lres.get(poz).add(xy[i][1]);
                                    lres.get(poz + 1).add(y);
                                } else {
                                    lres.get(poz).add(xy[i][1]);
                                    lres.get(poz + 1).add(y);
                                    lres.get(poz).add(xy[i][0]);
                                    lres.get(poz + 1).add(y);

                                }
                                break;

                            }

                        }

                        if ((xy.length != i) && xy[i + 1].length == 2) {
                            Element p1 = proiz(xy[i + 1][0], xy[i][0], y.add(t, ring), y, ring);
                            Element p2 = proiz(xy[i + 1][1], xy[i][1], y.add(t, ring), y, ring);

//                            if(p2.isZero(ring) &(!p1.isNegative())){
//                                int c = i;
//                                for (int k = i; k < xy.length; k++) {
//                                    if (xy[k].length == 2) {
//                                        c = k;
//
//                                    } else {
//                                        break;
//                                    }
//                                }
//                                ArrayList<Element> X1 = new ArrayList<Element>();
//                                ArrayList<Element> Y1 = new ArrayList<Element>();
//                                ArrayList<Element> buf1 = new ArrayList<Element>();
//                                ArrayList<Element> buf2 = new ArrayList<Element>();
//                                ArrayList<Element> buf3 = new ArrayList<Element>();
//                                ArrayList<Element> buf4 = new ArrayList<Element>();
//                                buf1=lres.get(poz);
//                                buf2=lres.get(poz+1);
//
//                                for (int k = i; k <= c; k++) {
//                                    X1.add(xy[k][0]);
//                                    Y1.add(ymin.add((new NumberR64(k)).multiply(t, ring), ring));
//                                }
//                                for (int k = i; k <= c; k++) {
//                                    buf1.add(xy[k][1]);
//                                    buf2.add(ymin.add((new NumberR64(k)).multiply(t, ring), ring));
//
//                                }
//                                for (int k = buf1.numberOfVar() - 1; k >= 0; k--) {
//                                   X1.add(buf1.get(k));
//                                   Y1.add(buf2.get(k));
//                                }
//
//                                buf1 = new ArrayList<Element>();
//                                buf2 = new ArrayList<Element>();
//                                lres.get(poz).clear();
//                                lres.get(poz + 1).clear();
//                                for (int k = 0; k <= X1.numberOfVar() - 1; k++) {
//                                    lres.get(poz).add(X1.get(k));
//                                    lres.get(poz + 1).add(Y1.get(k));
//                                }
//
//
//                                i = c;
//                                break;
//                            }

                            if ((p1.isNegative()) & (!p2.isNegative())) {
                                int c = i;
                                for (int k = i; k < xy.length; k++) {
                                    if (xy[k].length == 2) {
                                        c = k;

                                    } else {
                                        break;
                                    }
                                }
                                ArrayList<Element> X1 = new ArrayList<Element>();
                                ArrayList<Element> Y1 = new ArrayList<Element>();
                                ArrayList<Element> buf1 = new ArrayList<Element>();
                                ArrayList<Element> buf2 = new ArrayList<Element>();
                                ArrayList<Element> buf3 = new ArrayList<Element>();
                                ArrayList<Element> buf4 = new ArrayList<Element>();
                                for (int k = 0; k <= lres.get(poz).size() - 1; k++) {
                                    X1.add((lres.get(poz)).get(k));
                                    Y1.add(lres.get(poz + 1).get(k));
                                }

                                for (int k = i; k <= c; k++) {
                                    X1.add(xy[k][0]);
                                    Y1.add(ymin.add((new NumberR64(k)).multiply(t, ring), ring));
                                }
                                for (int k = i; k <= c; k++) {
                                    buf1.add(xy[k][1]);
                                    buf2.add(ymin.add((new NumberR64(k)).multiply(t, ring), ring));

                                }
                                for (int k = X1.size() - 1; k >= 0; k--) {
                                    buf3.add(X1.get(k));
                                    buf4.add(Y1.get(k));
                                }
                                for (int k = 0; k <= buf2.size() - 1; k++) {
                                    buf3.add(buf1.get(k));
                                    buf4.add(buf2.get(k));

                                }
                                X1 = buf3;
                                Y1 = buf4;
                                buf1 = new ArrayList<Element>();
                                buf2 = new ArrayList<Element>();
                                lres.get(poz).clear();
                                lres.get(poz + 1).clear();
                                for (int k = 0; k <= X1.size() - 1; k++) {
                                    lres.get(poz).add(X1.get(k));
                                    lres.get(poz + 1).add(Y1.get(k));
                                }


                                i = c;
                                break;

                            } else {
                                ArrayList<Element> X1 = new ArrayList<Element>();
                                ArrayList<Element> Y1 = new ArrayList<Element>();
                                ArrayList<Element> X2 = new ArrayList<Element>();
                                ArrayList<Element> Y2 = new ArrayList<Element>();
                                Element d11 = (xy[i][0].subtract((lres.get(poz)).get(lres.get(poz).size() - 1), ring)).abs(ring);
                                Element d12 = (xy[i][1].subtract((lres.get(poz)).get(lres.get(poz).size() - 1), ring)).abs(ring);
                                if (d12.compareTo(d11, 2, ring)) {
                                    (lres.get(poz)).add(xy[i][0]);
                                    (lres.get(poz + 1)).add(y);

                                    X2.add(xy[i][1]);
                                    Y2.add(y);
                                    lres.add(X2);
                                    lres.add(Y2);
                                } else {
                                    (lres.get(poz)).add(xy[i][1]);
                                    (lres.get(poz + 1)).add(y);

                                    X2.add(xy[i][0]);
                                    Y2.add(y);
                                    lres.add(X2);
                                    lres.add(Y2);
                                }


                            }

                        }
                    }

                    if (xy[i - 1].length == 2) {
                        int poz = 0;
                        int poz1 = 0;
                        for (int k = 0; k <= lres.size() - 1; k++) {
                            if ((k + 2) % 2 == 0) {
                                Boolean f1 = xy[i - 1][0].compareTo(lres.get(k).get(lres.get(k).size() - 1), 0, ring);
                                if (f1) {
                                    poz = k;
                                }
                                Boolean f2 = xy[i - 1][1].compareTo(lres.get(k).get(lres.get(k).size() - 1), 0, ring);
                                if (f2) {
                                    poz1 = k;
                                }
                            }

                        }
                        Element d11 = (xy[i][0].subtract(lres.get(poz).get(lres.get(poz).size() - 1), ring)).abs(ring);
                        Element d12 = (xy[i][0].subtract(lres.get(poz1).get(lres.get(poz1).size() - 1), ring)).abs(ring);
                        if (d11.compareTo(d12, -2, ring)) {
                            lres.get(poz).add(xy[i][0]);
                            lres.get(poz + 1).add(y);
                            lres.get(poz1).add(xy[i][1]);
                            lres.get(poz1 + 1).add(y);

                        } else {
                            lres.get(poz).add(xy[i][1]);
                            lres.get(poz + 1).add(y);
                            lres.get(poz1).add(xy[i][0]);
                            lres.get(poz1 + 1).add(y);
                        }

                        break;
                    }
                    break;
                }
                case (1): { // Если пришёл один корень
                    if (i == 0) { //Если это первый проход
                        break;
                    }
                    if (i == 1) {
                        ArrayList<Element> X1 = new ArrayList<Element>();
                        ArrayList<Element> Y1 = new ArrayList<Element>();
                        X1.add(xy[i][0]);
                        Y1.add(y);
                        lres.add(X1);
                        lres.add(Y1);
                        break;
                    }
                    if ((i != xy.length - 1) && (xy[i - 1].length == 0) & (xy[i + 1].length == 0)) {
                        break;
                    }



                    if (xy[i - 1].length == 0) { // если до этого было 0 корней.
                        ArrayList<Element> X1 = new ArrayList<Element>();
                        ArrayList<Element> Y1 = new ArrayList<Element>();
                        X1.add(xy[i][0]);
                        Y1.add(y);
                        lres.add(X1);
                        lres.add(Y1);
                        break;
                    }


                    if (xy[i - 1].length == 1) { //если до этого был один корень.
                        int poz = 0;
                        for (int k = 0; k <= lres.size() - 1; k++) {
                            if ((k + 2) % 2 == 0) {
                                Boolean f1 = xy[i - 1][0].compareTo(lres.get(k).get(lres.get(k).size() - 1), 0, ring);

                                if (f1) {
                                    poz = k;
                                }
                            }
                        }
                        lres.get(poz).add(xy[i][0]);
                        lres.get(poz + 1).add(y);
                        break;
                    }

                    if (xy[i - 1].length == 2) { // если перед ним было два корня

                        if (xy[i - 2].length == 3) {//Подумать
                        }
                        if (xy[i - 2].length == 2) {
                            int poz = 0;
                            int poz1 = 0;
                            Boolean f1 = false;
                            Boolean f2 = false;
                            Boolean f3 = false;
                            Boolean f4 = false;

                            for (int k = 0; k <= lres.size() - 1; k++) {

                                if ((k + 2) % 2 == 0) {
                                    f1 = xy[i - 1][0].compareTo(lres.get(k).get(lres.get(k).size() - 1), 0, ring);
                                    f2 = xy[i - 1][1].compareTo(lres.get(k).get(lres.get(k).size() - 1), 0, ring);
                                    if (f1) {
                                        poz = k;
                                        f3 = true;
                                    }
                                    if (f2) {
                                        poz1 = k;
                                        f4 = true;
                                    }
                                }
                            }
                            Element p1 = proiz(xy[i - 1][0], xy[i - 2][0], y.subtract(t, ring), y.subtract(t.multiply(new NumberR64(2)), ring), ring);
                            Element p2 = proiz(xy[i - 1][1], xy[i - 2][1], y.subtract(t, ring), y.subtract(t.multiply(new NumberR64(2)), ring), ring);
                            ArrayList<Element> X1 = new ArrayList<Element>();
                            ArrayList<Element> Y1 = new ArrayList<Element>();
                            ArrayList<Element> X2 = new ArrayList<Element>();
                            ArrayList<Element> Y2 = new ArrayList<Element>();
                            if ((!p1.isNegative()) & p2.isNegative()) {

                                if (f4 & (!f3)) {
                                    lres.get(poz1).add(xy[i][0]);
                                    lres.get(poz1 + 1).add(y);
                                    X1.add(xy[i][0]);
                                    Y1.add(y);
                                    for (int k = 0; k <= lres.get(poz1).size() - 1; k++) {
                                        X1.add(lres.get(poz1).get(k));
                                        Y1.add(lres.get(poz1 + 1).get(k));
                                    }
                                    lres.get(poz1).clear();
                                    lres.get(poz1 + 1).clear();
                                    for (int k = 0; k <= X1.size() - 1; k++) {
                                        lres.get(poz1).add(X1.get(k));
                                        lres.get(poz1 + 1).add(Y1.get(k));
                                    }
                                    break;




                                } else {
                                    lres.get(poz).add(xy[i][0]);
                                    lres.get(poz + 1).add(y);
                                    for (int k = lres.get(poz1).size() - 1; k >= 0; k--) {
                                        lres.get(poz).add(lres.get(poz1).get(k));
                                        lres.get(poz + 1).add(lres.get(poz1 + 1).get(k));
                                    }

                                    //lres.get(poz1).clear();
                                    lres.remove(poz1);
                                    //lres.get(poz1 + 1).clear();
                                    lres.remove(poz1);
                                }
                            } else {
                                if (f4 & (!f3)) {
                                    Element d1 = (xy[i][0].subtract(lres.get(poz1).get(lres.get(poz1).size() - 1), ring)).abs(ring);
                                    Element d2 = (xy[i][0].subtract(lres.get(poz1).get(0), ring)).abs(ring);
                                    if (d1.compareTo(d2, -2, ring)) {
                                        lres.get(poz).add(xy[i][0]);
                                        lres.get(poz + 1).add(y);
                                        break;

                                    } else {
                                        ArrayList<Element> buf = new ArrayList<Element>();
                                        ArrayList<Element> buf1 = new ArrayList<Element>();
                                        for (int k = lres.get(poz1).size() - 1; k >= 0; k--) {
                                            buf.add(lres.get(poz1).get(k));
                                            buf1.add(lres.get(poz1 + 1).get(k));
                                        }
                                        lres.get(poz1).clear();
                                        lres.get(poz1 + 1).clear();


                                        for (int k = 0; k <= buf.size() - 1; k++) {
                                            lres.get(poz1).add(buf.get(k));
                                            lres.get(poz1 + 1).add(buf1.get(k));
                                        }
                                        lres.get(poz1).add(xy[i][0]);
                                        lres.get(poz1 + 1).add(y);
                                        break;
                                    }
                                }
                                Element d1 = (xy[i][0].subtract(lres.get(poz).get(lres.get(poz).size() - 1), ring)).abs(ring);
                                Element d2 = (xy[i][0].subtract(lres.get(poz1).get(lres.get(poz1).size() - 1), ring)).abs(ring);
                                if (d1.compareTo(d2, -2, ring)) {
                                    lres.get(poz).add(xy[i][0]);
                                    lres.get(poz + 1).add(y);

                                } else {

                                    lres.get(poz1).add(xy[i][0]); /// ТУТ ПРОБЛЕМА!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                    lres.get(poz1 + 1).add(y);

                                }
                            }
                            break;
                        }




                    }


                    if (xy[i - 1].length == 3) {// Если до этого было 3 корня
                        int poz = 0;
                        int poz1 = 0;
                        int poz2 = 0;
                        Boolean f1 = false;
                        Boolean f2 = false;
                        Boolean f3 = false;
                        Boolean u1 = false;
                        Boolean u2 = false;
                        Boolean u3 = false;
                        for (int k = 0; k <= lres.size() - 1; k++) {
                            if ((k + 2) % 2 == 0) {
                                f1 = xy[i - 1][0].compareTo(lres.get(k).get(lres.get(k).size() - 1), 0, ring);
                                f2 = xy[i - 1][1].compareTo(lres.get(k).get(lres.get(k).size() - 1), 0, ring);
                                f3 = xy[i - 1][2].compareTo(lres.get(k).get(lres.get(k).size() - 1), 0, ring);
                                if (f1) {
                                    poz = k;
                                    u1 = true;
                                }
                                if (f2) {
                                    poz1 = k;
                                    u2 = true;
                                }
                                if (f3) {
                                    poz2 = k;
                                    u3 = true;
                                }

                            }
                        }
                         Boolean u11 = false;Boolean u12 = false;Boolean u13 = false;
                        if (u1 & u2 & u3) {

                            Element d11 = (xy[i][0].subtract(lres.get(poz).get(lres.get(poz).size() - 1), ring)).abs(ring);
                            Element d12 = (xy[i][0].subtract(lres.get(poz1).get(lres.get(poz1).size() - 1), ring)).abs(ring);
                            Element d13 = (xy[i][0].subtract(lres.get(poz2).get(lres.get(poz2).size() - 1), ring)).abs(ring);
                            Element[] sort = new Element[]{d11, d12, d13};
                            sort = Array.sortUp(sort, ring);
                            if (sort[0].compareTo(d11, 0, ring)) {
                                lres.get(poz).add(xy[i][0]);
                                lres.get(poz + 1).add(y);
                                u11 = true;

                            }
                            if (sort[0].compareTo(d12, 0, ring)) {
                                lres.get(poz1).add(xy[i][0]);
                                lres.get(poz1 + 1).add(y);
                                u12 =true;
                            }
                            if (sort[0].compareTo(d13, 0, ring)) {
                                lres.get(poz2).add(xy[i][0]);
                                lres.get(poz2 + 1).add(y);
                                u13=true;
                            }

                        }

                        if (xy[i - 2].length == 3) { //для того чтобы вычислить значения произодных в трёх точках нужны дополнительные 3 точки.

                            if(u13){
                            Element x11 = lres.get(poz).get(lres.get(poz).size()-1);
                            Element a11 = lres.get(poz).get(lres.get(poz).size()-2);
                            Element fx11 = lres.get(poz+1).get(lres.get(poz+1).size()-1);
                            Element fa11 = lres.get(poz+1).get(lres.get(poz+1).size()-2);
                            Element x12 = lres.get(poz1).get(lres.get(poz1).size()-1);
                            Element a12 = lres.get(poz1).get(lres.get(poz1).size()-2);
                            Element fx12 = lres.get(poz1+1).get(lres.get(poz1+1).size()-1);
                            Element fa12 = lres.get(poz1+1).get(lres.get(poz1+1).size()-2);
                            Element x13 = lres.get(poz1).get(1);
                            Element a13 = lres.get(poz1).get(0);
                            Element fx13 = lres.get(poz1+1).get(1);
                            Element fa13 = lres.get(poz1+1).get(0);
                            Element x14 = lres.get(poz2).get(1);
                            Element a14 = lres.get(poz2).get(0);
                            Element fx14 = lres.get(poz2+1).get(1);
                            Element fa14 = lres.get(poz2+1).get(0);
                            Element p1 = proiz(x11,a11,fx11,fa11,ring);
                            Element p2 = proiz(x12,a12,fx12,fa12,ring);
                            Element p3 = proiz(x13,a13,fx13,fa13,ring);
                            Element p4 = proiz(x14,a14,fx14,fa14,ring);
                            if ((!p1.isNegative()) & p2.isNegative() & (p3.isNegative())&(!p4.isNegative())) {


                                for (int k = lres.get(poz1).size()-1; k >= 0; k--) {
                                    lres.get(poz).add(lres.get(poz1).get(k));
                                    lres.get(poz+1).add(lres.get(poz1+1).get(k));
                                }
                                for (int k = 0; k <= lres.get(poz2).size() - 1; k++) {
                                    lres.get(poz).add(lres.get(poz2).get(k));
                                    lres.get(poz+1).add(lres.get(poz2+1).get(k));
                                }
                               lres.remove(poz1);
                               lres.remove(poz1);
                               lres.remove(poz2-2);
                               lres.remove(poz2-2);
                                break;
                            }

                            }
                            if(u11){
                               Element x11 = lres.get(poz).get(1);
                            Element a11 = lres.get(poz).get(0);
                            Element fx11 = lres.get(poz+1).get(1);
                            Element fa11 = lres.get(poz+1).get(0);
                            Element x12 = lres.get(poz1).get(1);
                            Element a12 = lres.get(poz1).get(0);
                            Element fx12 = lres.get(poz1+1).get(1);
                            Element fa12 = lres.get(poz1+1).get(0);
                            Element x13 = lres.get(poz1).get(lres.get(poz1).size()-1);
                            Element a13 = lres.get(poz1).get(lres.get(poz1).size()-2);
                            Element fx13 = lres.get(poz1+1).get(lres.get(poz1+1).size()-1);
                            Element fa13 = lres.get(poz1+1).get(lres.get(poz1+1).size()-2);
                            Element x14 = lres.get(poz2).get(lres.get(poz2).size()-1);
                            Element a14 = lres.get(poz2).get(lres.get(poz2).size()-2);
                            Element fx14 = lres.get(poz2+1).get(lres.get(poz2+1).size()-1);
                            Element fa14 = lres.get(poz2+1).get(lres.get(poz2+1).size()-2);
                            Element p1 = proiz(x11,a11,fx11,fa11,ring);
                            Element p2 = proiz(x12,a12,fx12,fa12,ring);
                            Element p3 = proiz(x13,a13,fx13,fa13,ring);
                            Element p4 = proiz(x14,a14,fx14,fa14,ring);
                            if ((p1.isNegative()) & (!p2.isNegative()) & (!p3.isNegative())&(p4.isNegative())) {


                                for (int k = lres.get(poz1).size()-1; k >= 0; k--) {
                                    lres.get(poz2).add(lres.get(poz1).get(k));
                                    lres.get(poz2+1).add(lres.get(poz1+1).get(k));
                                }
                                for (int k = 0; k <= lres.get(poz).size() - 1; k++) {
                                    lres.get(poz2).add(lres.get(poz).get(k));
                                    lres.get(poz2+1).add(lres.get(poz+1).get(k));
                                }


                               lres.remove(poz1);
                               lres.remove(poz1);
                               lres.remove(poz-2);
                               lres.remove(poz-2);
                                break;
                            }


                            }
                        }

                        if (xy[i - 2].length == 2) {
                        }
                        if (xy[i - 2].length == 1) {
                            if(poz2>poz){
                            Element d1 = ((lres.get(poz).get(lres.get(poz).size() - 1)).subtract(lres.get(poz1).get(lres.get(poz1).size() - 1), ring)).abs(ring);
                            Element d2 = ((lres.get(poz).get(lres.get(poz).size() - 1)).subtract(lres.get(poz2).get(lres.get(poz2).size() - 1), ring)).abs(ring);

                            if (d2.compareTo(d1, 2, ring)) {
                                for (int k = 0; k <= lres.get(poz1).size() - 1; k++) {
                                    lres.get(poz).add(lres.get(poz1).get(k));
                                    lres.get(poz + 1).add(lres.get(poz1 + 1).get(k));
                                }
//                                lres.remove(poz1);
//                                lres.remove(poz1);

                            } else {
                                for (int k = 0; k <= lres.get(poz2).size() - 1; k++) {
                                    lres.get(poz).add(lres.get(poz2).get(k));
                                    lres.get(poz + 1).add(lres.get(poz2 + 1).get(k));
                                }
//                                lres.remove(poz2);
//                                lres.remove(poz2);
                            }

                            if ((lres.get(poz).get(lres.get(poz).size() - 1)).compareTo(lres.get(poz1).get(0), 0, ring)) {
                                for (int k = 0; k <= lres.get(poz2).size() - 1; k++) {
                                    lres.get(poz).add(lres.get(poz2).get(k));
                                    lres.get(poz + 1).add(lres.get(poz2 + 1).get(k));
                                }
                            } else {
                                for (int k = 0; k <= lres.get(poz1).size() - 1; k++) {
                                    lres.get(poz).add(lres.get(poz1).get(k));
                                    lres.get(poz + 1).add(lres.get(poz1 + 1).get(k));
                                }
                            }
                            if(poz1<poz2){
                                lres.remove(poz1);
                                lres.remove(poz1);
                                lres.remove(poz2-2);
                                lres.remove(poz2-2);
                            }else{
                                lres.remove(poz2);
                                lres.remove(poz2);
                                lres.remove(poz1-2);
                                lres.remove(poz1-2);
                            }
                            break;

                        }else{
                            Element d1 = ((lres.get(poz).get(lres.get(poz).size() - 1)).subtract(lres.get(poz1).get(lres.get(poz1).size() - 1), ring)).abs(ring);
                            Element d2 = ((lres.get(poz).get(lres.get(poz).size() - 1)).subtract(lres.get(poz2).get(lres.get(poz2).size() - 1), ring)).abs(ring);


                                for (int k = lres.get(poz1).size() - 1; k>=0 ; k--) {
                                    lres.get(poz2).add(lres.get(poz1).get(k));
                                    lres.get(poz2 + 1).add(lres.get(poz1 + 1).get(k));
                                }
//                                lres.remove(poz1);
//                                lres.remove(poz1);


                                for (int k = 0; k <= lres.get(poz).size() - 1; k++) {
                                    lres.get(poz2).add(lres.get(poz).get(k));
                                    lres.get(poz2 + 1).add(lres.get(poz + 1).get(k));
                                }
//                                lres.remove(poz2);
//                                lres.remove(poz2);


//                            if ((lres.get(poz).get(lres.get(poz).numberOfVar() - 1)).compareTo(lres.get(poz1).get(0), 0, ring)) {
//                                for (int k = 0; k <= lres.get(poz2).numberOfVar() - 1; k++) {
//                                    lres.get(poz).add(lres.get(poz2).get(k));
//                                    lres.get(poz + 1).add(lres.get(poz2 + 1).get(k));
//                                }
//                            } else {
//                                for (int k = 0; k <= lres.get(poz1).numberOfVar() - 1; k++) {
//                                    lres.get(poz).add(lres.get(poz1).get(k));
//                                    lres.get(poz + 1).add(lres.get(poz1 + 1).get(k));
//                                }
//                            }

                                lres.remove(poz);
                                lres.remove(poz);
                                lres.remove(poz-2);
                                lres.remove(poz-2);

                            break;


                        }
                    }
                    }
                }



                case (0): {

                    if (i == 0 || i == 1) {
                        break;
                    }



                    if (xy[i - 1].length == 2) {

                        if (xy[i - 2].length == 2) {

                            int poz = 0;
                            int poz1 = 0;
                            Boolean f1 = false;
                            Boolean f2 = false;
                            Boolean f3 = false;
                            Boolean f4 = false;
                            for (int k = 0; k <= lres.size() - 1; k++) {
                                if ((k + 2) % 2 == 0) {
                                    f1 = xy[i - 1][0].compareTo(lres.get(k).get(lres.get(k).size() - 1), 0, ring);
                                    f2 = xy[i - 1][1].compareTo(lres.get(k).get(lres.get(k).size() - 1), 0, ring);
                                    if (f1) {
                                        poz = k;
                                        f3 = true;
                                    }
                                    if (f2) {
                                        poz1 = k;
                                        f4 = true;
                                    }
                                }
                            }
                            if (f4 & (!f3)) {
                                Element p1 = proiz(lres.get(poz1).get(lres.get(poz1).size() - 1), lres.get(poz1).get(lres.get(poz1).size() - 2),
                                        lres.get(poz1 + 1).get(lres.get(poz1 + 1).size() - 1), lres.get(poz1 + 1).get(lres.get(poz1 + 1).size() - 2), ring);
                                Element p2 = proiz(lres.get(poz1).get(0), lres.get(poz1).get(1), lres.get(poz1 + 1).get(0), lres.get(poz1 + 1).get(1), ring);
                                if (p1.isNegative() & (!p2.isNegative())) {
                                    ArrayList<Element> X1 = new ArrayList<Element>();
                                    ArrayList<Element> Y1 = new ArrayList<Element>();
                                    X1.add(lres.get(poz1).get(lres.get(poz1).size() - 1));
                                    Y1.add(lres.get(poz1 + 1).get(lres.get(poz1 + 1).size() - 1));
                                    for (int k = 0; k <= lres.get(poz1).size() - 1; k++) {
                                        X1.add(lres.get(poz1).get(k));
                                        Y1.add(lres.get(poz1 + 1).get(k));
                                    }
                                    lres.get(poz1).clear();
                                    lres.get(poz1 + 1).clear();
                                    for (int k = 0; k <= X1.size() - 1; k++) {
                                        lres.get(poz1).add(X1.get(k));
                                        lres.get(poz1 + 1).add(Y1.get(k));
                                    }
                                }
                                break;
                            }

                            Element p1 = proiz(xy[i - 1][0], xy[i - 2][0], y.subtract(t, ring), y.subtract(t.multiply(new NumberR64(2)), ring), ring);
                            Element p2 = proiz(xy[i - 1][1], xy[i - 2][1], y.subtract(t, ring), y.subtract(t.multiply(new NumberR64(2)), ring), ring);
                            if ((!p1.isNegative()) & (p2.isNegative())) {
                                for (int k = lres.get(poz1).size() - 1; k >= 0; k--) {
                                    lres.get(poz).add(lres.get(poz1).get(k));
                                    lres.get(poz + 1).add(lres.get(poz1 + 1).get(k));
                                }
                                lres.remove(poz1);
                                lres.remove(poz1);
                                break;
                            }
                        }
                    }
                    break;
                }
            }



        }
        Element[][] res = new Element[lres.size()][];
        for (int i = 0; i <= lres.size() - 2; i++) {
            if ((i + 2) % 2 == 0) {
                Element[] x = new Element[lres.get(i).size()];
                Element[] y = new Element[lres.get(i + 1).size()];
                for (int k = 0; k <= x.length - 1; k++) {
                    x[k] = lres.get(i).get(k);
                    y[k] = lres.get(i + 1).get(k);
                }
                res[i] = x;
                res[i + 1] = y;
            }
        }


        return res;



    }

    public static Element[][] cutS(Polynom s1, Polynom s2, NumberR64 t, NumberR64 xmin, NumberR64 xmax, NumberR64 ymin,
            NumberR64 ymax, Ring ring) {
        ring.setDefaulRing();
        Element s = (ymax.subtract(ymin, ring)).divide(t, ring);
        int d = (s.intValue()) + 1;
        Polynom cut = s1.subtract(s2, ring); //само сечение
        Element[][] res = new Element[d][];
        int k = 0;
        for (NumberR64 i = ymin; i.compareTo(ymax, -2, ring); i = i.add(t)) {
            if (k < d) {
                Polynom sx = (Polynom) cut.value(new Element[]{new Polynom(ring.varNames[0], ring), i}, ring);
                Element[] solveS = new Equals().solve(sx, ring);
                Element[] solve = new Cut().checkE(solveS, xmin, xmax, ring);
                res[k] = solve;
                k++;
            }
        }

        if (res[res.length - 1] == null) {
            res[res.length - 1] = new Element[]{};

        }
        return res;
    }

    public static Element proiz(Element x, Element a, Element fx, Element fa, Ring ring) {
        if (fx.compareTo(fa, 0, ring)) {
            return NumberR64.ZERO;
        }
        Element xa = x.subtract(a, ring);
        Element ff = fx.subtract(fa, ring);
        Element res = xa.divide(ff, ring);
        return res;
    }

    /**
     *
     * @param el[] массив элементов длинны <=3
     * @param xmin нижняя граница
     * @param xmax верхняя граница
     * @param ring
     * @return возвращает массив значений, полученный из исходного путём отброса
     * значений выходящих за границу области (xmin, xmax)
     */
    public Element[] checkE(Element[] el, NumberR64 xmin, NumberR64 xmax, Ring ring) {
        Element[] res = new Element[]{};

        switch (el.length) {

            case (3): {
                ArrayList<Element> buf = new ArrayList<Element>();

                if (el[0].compareTo(xmin, 1, ring) & xmax.compareTo(el[0], 1, ring)) {
                    buf.add(el[0]);
                }
                if (el[1].compareTo(xmin, 1, ring) & xmax.compareTo(el[1], 1, ring)) {
                    buf.add(el[1]);
                }
                if (el[2].compareTo(xmin, 1, ring) & xmax.compareTo(el[2], 1, ring)) {
                    buf.add(el[2]);
                }
                res = new Element[buf.size()];
                for (int k = 0; k < buf.size(); k++) {
                    res[k] = buf.get(k);
                }
                break;

            }
            case (2): {
                ArrayList<Element> buf = new ArrayList<Element>();

                if (el[0].compareTo(xmin, 1, ring) & xmax.compareTo(el[0], 1, ring)) {
                    buf.add(el[0]);
                }
                if (el[1].compareTo(xmin, 1, ring) & xmax.compareTo(el[1], 1, ring)) {
                    buf.add(el[1]);
                }

                res = new Element[buf.size()];
                for (int k = 0; k < buf.size(); k++) {
                    res[k] = buf.get(k);
                }
                break;
            }
            case (1): {
                ArrayList<Element> buf = new ArrayList<Element>();

                if (el[0].compareTo(xmin, 1, ring) & xmax.compareTo(el[0], 1, ring)) {

                    res = new Element[]{el[0]};
                }
                break;
            }
        }
        return res;
    }

    /**
     *
     * @param s массиив из двух полиномов
     * @param ring
     * @return возвращает true если максимальная степень полиномов <=3; false если максимальная степень полиномов >3
     */
    public Boolean checkS(Polynom[] s, Ring ring) {
        Boolean b1 = ((s[0].powers[0] <= 3) && (s[0].powers[1] <= 3));
        Boolean b2 = ((s[1].powers[0] <= 3) && (s[1].powers[1] <= 3));
        if (b1 && b2) {
            return true;
        } else {
            return false;
        }
    }
}
