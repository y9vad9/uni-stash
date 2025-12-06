/*
 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.gladyshev;

/**
 *
 * @author Alex
 */
import com.mathpar.func.F;
import java.awt.*;
import java.awt.event.*;

import com.mathpar.number.Element;
import com.mathpar.number.NumberR;
import com.mathpar.number.NumberR64;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;

public class Paint extends Frame implements WindowListener {

    public void windowClosing(WindowEvent we) {
        System.exit(0);
    }

    public void windowClosed(WindowEvent we) {
    }

    public void windowDeactivated(WindowEvent we) {
    }

    public void windowActivated(WindowEvent we) {
    }

    public void windowIconified(WindowEvent we) {
    }

    public void windowDeiconified(WindowEvent we) {
    }

    public void windowOpened(WindowEvent we) {
    }

    public Paint(F func1, F func2, double aa, double bb) {
        setSize(800, 600);
        addWindowListener(this);
       // setVisible(true);
        f1 = func1;
        f2 = func2;
        a = aa;
        b = bb;
    }
    Ring ring = new Ring("R[x]");
    F f1;
    F f2;
    double a;
    double b;
    double stepx;
    double stepy;
    boolean flag;
    boolean flag2;
    double[] values1 = new double[600];
    double[] values2 = new double[600];
    double min;
    double max;
    int ind[] = new int[1000];
    double values3[] = new double[0];

    void init() {
        Element[] m = new Element[1];
        stepx = (b - a)/600;
        for(int i=0; i<600; i++) {
            m[0] = (NumberR64)new NumberR64(a+stepx*i);
            if(a+stepx*i == 0) {
                m[0] = new NumberZ("0");
            }
            values1[i] = f1.valueOf(m, ring).doubleValue();
            values2[i] = f2.valueOf(m, ring).doubleValue();
        }
        int e = 0;
        temp(values1, values2);
        for(int i=0; i<ind.length; i++) {
            System.out.println("ind = "+ind[i]);
            if(ind[i] == -1) {
                e = i;
                break;
            }
        }
        values3 = new double[e];
        for(int i=0; i<values3.length; i++) {
            values3[i] = a+stepx*ind[i];
            System.out.println("point = "+values3[i]);
            m[0] = (NumberR64)new NumberR64(values3[i]);
            //System.out.println("result1 = "+f1.valueOf(m, ring).doubleValue());
            //m[0] = (NumberR64)new NumberR64(values3[i]);
            //System.out.println("result1 = "+f1.valueOf(m, ring).doubleValue());
            System.out.println("value = "+Math.abs(f1.valueOf(m, ring).doubleValue() - f2.valueOf(m, ring).doubleValue()));
        }
        if (a/stepx >= 100) {
            flag = false;
        } else {
            flag = true;
        }
        min = min(values1);
        max = max(values2);
        stepy = (max  - min)/300;
        if(min <= 0) {
            flag2 = false;
        } else {
            flag2 = true;
        }
        setVisible(true);
    }
    double min(double[] array) {
        double min = array[0];
        for(int i=1; i<array.length; i++) {
            if(array[i] <= min) {
                min = array[i];
            }
        }
        return min;
    }
    double max(double[] array) {
        double max = array[0];
        for(int i=1; i<array.length; i++) {
            if(array[i] >= max) {
                max = array[i];
            }
        }
        return max;
    }
    int[][] resize(int[][] array) {
        int[][] aa = new int[array.length + 10][2];
        return aa;
    }
    public void paint(Graphics g) {//перерисовка, если стирается изображение
        for(int i=0; i<values1.length; i++) {

            g.setColor(Color.DARK_GRAY);
            g.drawLine(100+i, -(int)((values1[i] - min)/stepy) + 400, 100+i, (int)((max - values2[i])/stepy) + 100);
        }
        for(int i=0; i<values1.length; i++) {
            g.setColor(Color.BLACK);
            g.fillOval(100 + i, -(int)((values1[i] - min)/stepy) + 400, 4, 4);
        }
        for(int i=0; i<values2.length; i++) {
            g.fillOval(100 + i, (int)((max - values2[i])/stepy) + 100, 4, 4);
        }
        if(flag2) {
            g.drawLine(0, -(int)(min/stepy) + 400, 800, -(int)(min/stepy)+400);
        } else {
            g.drawLine(0, (int)(min/stepy)+400, 800, (int)(min/stepy)+400);
        }
        //g.drawLine(0, 100, 100, 100);
        g.drawString(""+max, 50, 100);
        //g.drawLine(0, 400, 100, 400);
        g.drawString(""+min, 50, 400);
        if(flag) {
            g.drawLine(100 - (int)(a/stepy), 0, 100 - (int)(a/stepy), 600);
        }


        int[][] coo = new int[100][2];
        int pointer = 0;
        //while(coo[pointer][0] != 0) {

        //}
    }

    void temp(double[] v1, double[] v2) {
        int i = 0;
        int indt = 0;
        do {
            int result = 0;
            double min = Math.abs(v1[0] - v2[0]);
            for(; i<v1.length; i++) {
                if(Math.abs(v2[i] - v1[i]) < 0.1) {
                    break;
                }
            }
            int j = i;
            for(; j<v1.length; j++) {
                if(Math.abs(v2[j] - v1[j]) > 0.1) {
                    break;
                }
            }
            if( (i != (v1.length)) ) {
                result = i;
                for(; i<=j; i++) {
                    if(Math.abs(v1[i] - v2[i]) < min) {
                        min = Math.abs(v1[i] - v2[i]);
                        result = i;
                    }
                }
                ind[indt] = result;
                indt++;
            } else if(Math.abs(v1[i-1] - v2[i-1]) < 0.1) {
               ind[indt] = i;
               return;
            } else {
                ind[indt] = -1;
            }
        } while(ind[indt] != -1);
        return;
    }


    public static void main(String[] args) {
        Ring r = new Ring("R[x]");
        F fun1 = new F("\\cos(x)", r);
        F fun2 = new F("\\sin(x)",r);
        double A = 2;
        double B = 10;
        Paint g = new Paint(fun1, fun2, A, B);
        g.init();

    }
}
