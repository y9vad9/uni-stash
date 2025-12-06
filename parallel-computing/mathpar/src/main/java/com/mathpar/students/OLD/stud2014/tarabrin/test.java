package com.mathpar.students.OLD.stud2014.tarabrin;


import com.mathpar.number.*;
import com.mathpar.polynom.*;




/**
 *
 * @author Администратор
 */
public class test {

    public static void main(String[] args) {
        Ring ring = new Ring("R64[x,y]");
        ring.setDefaulRing();


//        Polynom s1 = new Polynom("(x+5)(x-2)(x+1)", ring); //забавный график)))))
//        Polynom s2 = new Polynom("(y-8)(y+2)(y-3)", ring);
//        new Plot().graf(s1, s2, new NumberR64(0.05), new NumberR64(-10), new NumberR64(10), new NumberR64(-10), new NumberR64(10), ring);
// Element[][] gg= new  Element[5][];
//  Element[]  row = new Element[4];
//  gg[0]=row;
//         Polynom s1 = new Polynom("x^2y + 5xy + y^2x^2 + 7x + 9x^2 + x^2y^2", ring); // при точности меньше 1,13 не рисует. проверить
//        Polynom s2 = new Polynom("10x^2 + y", ring);

//         Polynom s1 = new Polynom("xy+y^2x+y^2+5x", ring);
//        Polynom s2 = new Polynom("1", ring);
////         Polynom s1 = new Polynom("x^2", ring);
////        Polynom s2 = new Polynom("y", ring);
//
////
//        new Plot().graf(s1, s2, new NumberR64(0.1), new NumberR64(-10), new NumberR64(10), new NumberR64(-10), new NumberR64(10), ring);
//
//        Element[][] pp = Cut.cut(s1, s2, new NumberR64(0.1), new NumberR64(-10), new NumberR64(10), new NumberR64(-3), new NumberR64(5), ring);
//        for (int i = 0; i < pp.length; i++) {
//            System.out.println("" + Array.toString(pp[i]));
//            System.out.println("" + pp[i].length);
//
//        }
//        System.out.println("" + pp.length);








//        Polynom s1 = new Polynom("(x+5)(x-2)(x+1)", ring);
//        Polynom s2 = new Polynom("(y-8)(y+2)(y-3)", ring);
////        new Plot().graf(s1, s2, new NumberR64(1), new NumberR64(-10), new NumberR64(10), new NumberR64(-10), new NumberR64(10), ring);
////        Element[][] z = new Cut().cutS(s1, s2, new NumberR64(1), new NumberR64(-10), new NumberR64(10), new NumberR64(-10), new NumberR64(10), ring);
//
//        Element[][] pp = new Cut().cutS(s1, s2, new NumberR64(0.1), new NumberR64(-10), new NumberR64(10), new NumberR64(-10), new NumberR64(10), ring);
//        for (int i =0; i<9; i++){
//            System.out.println(""+Array.toString(pp[i]));
//            System.out.println(""+pp[i].length);
//        }
//       new Plot().graf(s1, s2, new NumberR64(0.1), new NumberR64(-10), new NumberR64(10), new NumberR64(-10), new NumberR64(10), ring);

//        Polynom s1 = new Polynom("x^2", ring); //гипербола
//        Polynom s2 = new Polynom("y", ring);
//        // new Plot().graf(s1, s2, new NumberR64(1), new NumberR64(-10), new NumberR64(10), new NumberR64(-10), new NumberR64(10), ring);
//        // Element[][] z = new Cut().cutS(s1, s2, new NumberR64(1), new NumberR64(-10), new NumberR64(10), new NumberR64(-10), new NumberR64(10), ring);
//
//        Element[][] pp = new Cut().cutS(s1, s2, new NumberR64(0.1), new NumberR64(-10), new NumberR64(10), new NumberR64(-10), new NumberR64(10), ring);
//        for (int i = 0; i < 9; i++) {
//            System.out.println("" + Array.toString(pp[i]));
//            System.out.println("" + pp[i].length);
//        }
//        new Plot().graf(s1, s2, new NumberR64(0.1), new NumberR64(-10), new NumberR64(10), new NumberR64(-10), new NumberR64(10), ring);
//


        Polynom s1 = new Polynom("x^2y^3 + x - y^2", ring);
        Polynom s2 = new Polynom("x^3y - y^3", ring);


        new Plot().graf(s1, s2, new NumberR64(0.1), new NumberR64(-10), new NumberR64(10), new NumberR64(-10), new NumberR64(10), ring);

//        Polynom s1 = new Polynom("(x^2)0.5-(y^2)0.5", ring);
//        Polynom s2 = new Polynom("1", ring);
//        new Plot().graf(s1, s2, new NumberR64(0.1), new NumberR64(-10), new NumberR64(10), new NumberR64(-10), new NumberR64(10), ring);

//        Polynom s1 = new Polynom("(x+y)^3", ring);
//        Polynom s2 = new Polynom("(x-y)^2", ring);
//        new Plot().graf(s1, s2, new NumberR64(0.1), new NumberR64(-10), new NumberR64(10), new NumberR64(-10), new NumberR64(10), ring);

//           Polynom s1 = new Polynom("5.2x^2", ring);
//           Element s = new Cut().proiz(s1,new NumberR64(3.1), ring);
//           System.out.println("s1 "+s1+" s "+s);


//          Polynom s1 = new Polynom("x", ring);
//          Polynom s2 = new Polynom("9", ring);
//          new Plot().graf(s1, s2, new NumberR64(0.1), new NumberR64(-10), new NumberR64(10), new NumberR64(-10), new NumberR64(10), ring);
//


    }
}
