/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.Osipova;

import com.mathpar.func.Fname;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import com.mathpar.number.Element;
import com.mathpar.number.NumberR64;
import com.mathpar.number.Ring;
import com.mathpar.number.VectorS;

/**
 *
 * @author osipova
 */
public class Diagram {
    public static Element[] a = null;
    public static int h = 600;

    /*
     *рисуем круговую диаграмму
     */
    public static void paint(String path, Element[] value, Element[] name, Ring r) throws IOException {
        for(int i = 0; i < name.length; i++){
            if(name[i].toString(r).length() > 5){
                System.out.println("Error!");
                return;
            }
        }
        a = value;
        BufferedImage img = new BufferedImage(800, 800, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = img.createGraphics();
        Element sum = r.numberZERO;
        for (int i = 0; i < a.length; i++) {
            sum = sum.add(a[i], r);
        }
        VectorS testA = new VectorS(a);

        VectorS testB = new VectorS();

        testB = testA;

        Element one = r.numberONE();

        testB = (VectorS)testB.multiply(new NumberR64(360), r);

        testB = testB.divide(sum, r);

        try {
            g2d.setColor(Color.white);
            g2d.fillRect(0, 0, 800, 800);
        } finally {
        }
        int KurAngel = 0;
        int b2 = 0;
        for (int i = 0; i < a.length; i++) {

            try {
                // g2d.setColor(new Color(i * 255 / n, 100, 100));
                Element ggg = testB.V[i];
                b2 = ggg.intValue();
                g2d.setColor(new Color(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)));
                if (i == a.length - 1) {
                    b2 = 360 - KurAngel;
                }
                g2d.fillArc(100, 100, 600, 600, KurAngel, b2);
                int AngWord = KurAngel + b2 / 2;
                int x = 400 + (int) (350 * Math.cos(AngWord * Math.PI / 180));
                int y = 400 - (int) (350 * Math.sin(AngWord * Math.PI / 180));
                ggg=testA.V[i];
                int cur=ggg.intValue();
                String s = "" + name[i].toString(r)+"(" + cur +")";
                g2d.setColor(Color.BLACK);

                g2d.drawString(s, x, y); // подписываем числа
                KurAngel += b2;
            } finally {
            }
        }
        ImageIO.write(img, "PNG", new File("/home/osipova/" + path));

    }

    /*
     *   рисуем столбцевую диаграмму
     */
    public static void paint1(String path, Element[] value, Element[] name, Ring r) throws IOException {
        for(int i = 0; i < name.length; i++){
            if(name[i].toString(r).length() > 5){
                System.out.println("Error!");
                return;
            }
        }
        a = value;


        BufferedImage img = new BufferedImage(800, 800, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = img.createGraphics();

        /*
         * генерируем массив чисел от 10 до 100 и находим наибольший
         */

        Element max = new NumberR64(-100);
        for (int i = 0; i < a.length; i++) {
            if (a[i].compareTo(max, r) == 1) {
                max = a[i];
            }
        }
        VectorS testA = new VectorS(a);

        VectorS testB = new VectorS();

        testB = testA;

        Element one = r.numberONE();


        /*
         * отношение всех элементов относительного наибольшего
         */

        // VectorS testB = new VectorS(n, new Element(0));

        testB = (VectorS)testA.multiply(new NumberR64(h).divide(max, r), r);

        try {
            g2d.setColor(Color.white);
            g2d.fillRect(0, 0, 800, 800);
        } finally {
        }

        for (int i = 0; i < a.length; i++) {
            try {
                g2d.setColor(new Color(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)));
                Element ggg = testB.V[i];
                int b = ggg.intValue();
                int yword=100 + h - b;
                g2d.fillRect(100 + (i * h / a.length), 100 + h - b, h / a.length - 1, b);
                String s = "" + name[i].toString(r);
                g2d.setColor(Color.BLACK);
                g2d.drawString(s, 100 + (i * h / a.length) + h / (2 * a.length), 125 + h);//подписываем числа
                ggg = testA.V[i];
                b = ggg.intValue();
                s = "" + b;
                g2d.drawString(s, 75, yword);//подписываем числа
                g2d.drawLine(100, yword, 100 + (i * h / a.length),yword );
            } finally {
            }
        }
        ImageIO.write(img, "PNG", new File("/home/osipova/" + path));

    }

    public static void main(String[] args) throws Exception {
        Ring r = new Ring("R[x]");

        Element[] value1 = new Element[] {new NumberR64(5), new NumberR64(60), new NumberR64(30), new NumberR64(70), new NumberR64(55)};
        Element[] name1 = new Element[] {new Fname("a"), new Fname("b"), new Fname("c"), new Fname("d"), new Fname("f")};

        System.out.println("Круговая диаграмма");
        paint("1.png", value1, name1, r);


        Element[] value2 = new Element[] {new NumberR64(5), new NumberR64(60), new NumberR64(30), new NumberR64(70), new NumberR64(55)};
        Element[] name2 = new Element[] {new Fname("a"), new Fname("b"), new Fname("c"), new Fname("d"),new Fname("f")};

        System.out.println("Столбцевая диаграмма");
        paint1("2.png", value2, name2, r);

    }
}
