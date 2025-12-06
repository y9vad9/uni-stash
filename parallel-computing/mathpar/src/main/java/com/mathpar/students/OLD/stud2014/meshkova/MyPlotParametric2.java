/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mathpar.students.OLD.stud2014.meshkova;

import com.mathpar.func.F;
import com.mathpar.number.Element;
import com.mathpar.number.NumberR64;
import com.mathpar.number.Ring;

/**
 *
 * @author Татьяна
 */
public class MyPlotParametric2 {
    public static void main(String[] args) {
        String[] f1 = new String[3];
        String[] f2 = new String[3];
        f1[0] = "u";
        f1[1] = "v";
        f1[2] = "u^2 + v^2 + 5";
        f2[0] = "u";
        f2[1] = "v";
        f2[2] = "-u^2-v^2 + 5";

        findPoint(f2, f1, 0.02);
    }
    public static double FunctionParam(String S, double u, double v) {
        Ring ring = new Ring("R64[u,v]");//new Ring("R64[u,v]");
       // ring.setDefaulRing();
        ring.createVarPolynoms();
        F f = new F(S, ring);
        Element g = f.valueOf(new NumberR64[]{new NumberR64(u), new NumberR64(v)}, ring);

        return g.doubleValue();
    }
    static double[] findPoint(String[] f1, String[] f2, double eps) {
        /*
        ограничения на параметры f1
        */
        double min_u = -6;
    	double max_u = 6;
    	double min_v = -6;
    	double max_v = 6; // 2*Math.PI;
        /*
        ограничения на параметры f2
        */
        double max_u2 = 6;
        double min_u2 = -6;
        double max_v2 = 6;
        double min_v2 = -6;

        double new_u = 0;
    	double new_v = 0;
        double u1 = min_u + Math.random()*(max_u - min_u);
        double v1 = min_v + Math.random()*(max_v - min_v);
        double u2 = min_u2 + Math.random()*(max_u2 - min_u2);
        double v2 = min_v2 + Math.random()*(max_v2 - min_v2);
        double minDistance;
        double step_u1 = (max_u - min_u)/100;
        double step_v1 = (max_v - min_v)/100;
        double step_u2 = (max_u2 - min_u2)/100;
        double step_v2 = (max_v2 - min_v2)/100;
        double[] newPoint = new double[3];
        double[] randomPoint1 = new double[3];
        for(int i=0; i<3; i++) {
            randomPoint1[i] = FunctionParam(f1[i], u1, v1);
        }
        double[] randomPoint2 = new double[3];
        for(int i=0; i<3; i++) {
        	randomPoint2[i] = FunctionParam(f2[i], u2, v2);
        }

        minDistance = distance(randomPoint1, randomPoint2);
    	new_u = u1;
    	new_v = v1;
        int counter = 0;
        do {

        	double temp[] = new double[3]; // временная точка
        	double tmp; // новое расстояние
        	new_u = u1;
        	// ++++++++
        	if((u1+step_u1)<max_u) {
        		for(int i=0; i<3; i++) {
        			temp[i] = FunctionParam(f1[i], u1 + step_u1, v1);
        		}
        	}
        	tmp = distance(temp, randomPoint2);
        	if(tmp<minDistance) {
        		u1 = u1 + step_u1;
        		minDistance = tmp;
        		for(int i=0; i<3; i++) {
        			randomPoint1[i] = temp[i];
        		}
        	}

        	//-----------

        	if((u1-step_u1)>min_u) {
        		for(int i=0; i<3; i++) {
        			temp[i] = FunctionParam(f1[i], u1-step_u1, v1);
        		}
        	}
        	tmp = distance(temp, randomPoint2);
        	if(tmp<minDistance) {
        		u1 = u1 - step_u1;
        		minDistance = tmp;
        		for(int i=0; i<3; i++) {
        			randomPoint1[i] = temp[i];
        		}
        	}

        	if(Math.abs(u1 - new_u) < Math.pow(10, -10)) {
        		step_u1 = step_u1/2;
        	}

        	new_v = v1;

        	//+++++++++++++

        	if((v1+step_v1)<max_v) {
        		for(int i=0; i<3; i++) {
        			temp[i] = FunctionParam(f1[i], u1, v1+step_v1);
        		}
        	}
        	tmp = distance(temp, randomPoint2);
        	if(tmp<minDistance) {
        		v1 = v1 + step_v1;
        		minDistance = tmp;
        		for(int i=0; i<3; i++) {
        			randomPoint1[i] = temp[i];
        		}
        	}

        	//--------------

        	if((v1-step_v1)>min_v) {
        		for(int i=0; i<3; i++) {
        			temp[i] = FunctionParam(f1[i], u1, v1-step_v1);
        		}
        	}
        	tmp = distance(temp, randomPoint2);
        	if(tmp<minDistance) {
        		v1 = v1 - step_v1;
        		minDistance = tmp;
        		for(int i=0; i<3; i++) {
        			randomPoint1[i] = temp[i];
        		}
        	}

        	if(Math.abs(v1 - new_v) < Math.pow(10, -10)) {
        		step_v1 = step_v1/2;
        	}

        	new_u = u2;

        	/*
                точка на поверхности f2
                */
        	//++++++++++++++++++++
           	if((u2+step_u2)<max_u2) {
        		for(int i=0; i<3; i++) {
        			temp[i] = FunctionParam(f2[i], u2+step_u2, v2);
        		}
        	}
        	tmp = distance(temp, randomPoint1);
        	if(tmp<minDistance) {
        		u2 = u2+step_u2;
        		minDistance = tmp;
        		for(int i=0; i<3; i++) {
        			randomPoint2[i] = temp[i];
        		}
        	}

        	//--------------

        	if((u2-step_u2)>min_u2) {
        		for(int i=0; i<3; i++) {
        			temp[i] = FunctionParam(f2[i], u2-step_u2, v2);
        		}
        	}
        	tmp = distance(temp, randomPoint1);
        	if(tmp<minDistance) {
        		u2 = u2 - step_u2;
        		minDistance = tmp;
        		for(int i=0; i<3; i++) {
        			randomPoint2[i] = temp[i];
        		}
        	}

           	if(Math.abs(u2 - new_u) < Math.pow(10, -10) ) {
        		step_u2 = step_u2/2;
        	}
        	//u2 = new_u;
           	new_v = v2;
        	//++++++++++++++++++

        	if((v2+step_v2)<max_v2) {
        		for(int i=0; i<3; i++) {
        			temp[i] = FunctionParam(f2[i], u2, v2+step_v2);
        		}
        	}
        	tmp = distance(temp, randomPoint1);
        	if(tmp<minDistance) {
        		v2 = v2 + step_v2;
        		minDistance = tmp;
        		for(int i=0; i<3; i++) {
        			randomPoint2[i] = temp[i];
        		}
        	}

        	//------------------

        	if((v2-step_v2)>min_v2) {
        		for(int i=0; i<3; i++) {
        			temp[i] = FunctionParam(f2[i], u2, v2-step_v2);
        		}
        	}
        	tmp = distance(temp, randomPoint1);
        	if(tmp<minDistance) {
        		v2 = v2 - step_v2;
        		minDistance = tmp;
        		for(int i=0; i<3; i++) {
        			randomPoint2[i] = temp[i];
        		}
        	}

        	if(Math.abs(v2 - new_v) < Math.pow(10, -10)) {
        		step_v2 = step_v2/2;
        	}
        	//v2 = new_v;
                counter++;
              //  System.out.println(counter);
        	if(counter > 100000) {
                    System.out.println("Точек пересечения не найдено!");
                    return null;
                }
        } while(minDistance > eps);  //точность вычислений

        for(int i=0; i<3; i++) {
        	System.out.println(randomPoint1[i]);
        }
        return randomPoint1;
    }

    static double distance(double[] a, double[] b) {
        double result = 0;
        for(int i=0; i<3; i++) {
            result += (b[i] - a[i])*(b[i] - a[i]);
        }
        return Math.sqrt(result);
    }
}
