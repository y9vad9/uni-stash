/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.parallel.webCluster.algorithms.multPolynom;

import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;
import java.util.Random;

/**
 *
 * @author r1d1
 */
public class test {
    public static void main(String[] args) {
        int []randomTypes={4,6,100,20};
        Polynom a=new Polynom(randomTypes,new Random(),Ring.ringZxyz);
        Polynom b=new Polynom(randomTypes,new Random(),Ring.ringZxyz);
        System.out.println(a.toString(Ring.ringZxyz));
        System.out.println(b.toString(Ring.ringZxyz));
        Polynom a1=a.subPolynom(0,a.coeffs.length/2);
        Polynom a2=a.subPolynom(a.coeffs.length/2,a.coeffs.length);
        
        System.out.println(a1.toString(Ring.ringZxyz));
        System.out.println(a2.toString(Ring.ringZxyz));
        
        Polynom b1=b.subPolynom(0,b.coeffs.length/2);
        Polynom b2=b.subPolynom(b.coeffs.length/2,b.coeffs.length);
        Polynom trueC=a.multiply(b, Ring.ringZxyz);
        Polynom a1b1=a1.multiply(b1, Ring.ringZxyz);
        Polynom a1b2=a1.multiply(b2, Ring.ringZxyz);
        Polynom a2b1=a2.multiply(b1, Ring.ringZxyz);
        Polynom a2b2=a2.multiply(b2, Ring.ringZxyz);
        Polynom testC=a1b1.add(a1b2, Ring.ringZxyz).add(a2b1, Ring.ringZxyz).add(a2b2, Ring.ringZxyz);
        
        
        System.out.println(trueC.toString(Ring.ringZxyz));
        System.out.println(testC.toString(Ring.ringZxyz));
        if (trueC.equals(testC, Ring.ringZxyz)){
            System.out.println("OK!!!!");
        }
        else{
            System.out.println("FALSE!!!");
        }
    }
}
