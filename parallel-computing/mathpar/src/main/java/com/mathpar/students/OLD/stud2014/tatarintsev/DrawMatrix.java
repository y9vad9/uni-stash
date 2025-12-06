/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mathpar.students.OLD.stud2014.tatarintsev;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import javax.swing.JFrame;
import com.mathpar.matrix.MatrixD;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.number.NumberR64;
import com.mathpar.number.Ring;

/**
 *
 * @author heckfy
 */
public class DrawMatrix extends JComponent {
    public Element A;
    public int width = 800;
    public int height = 600;

    public DrawMatrix(Element A, int width_in, int height_in) {

        this.A = A;
        this.height = height_in;
        this.width = width_in;

    }

    public void draw() {
        final JFrame f = new JFrame();
        f.setDefaultCloseOperation(f.EXIT_ON_CLOSE);
        f.setVisible(true);
        f.setBounds(30, 40, width, height);
        f.getContentPane().add(this);
        f.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                height = (int) f.getBounds().getHeight();
                width = (int) f.getBounds().getWidth();
                f.repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (A instanceof MatrixS) {
            Graphics2D g2d = (Graphics2D) g;
            Color c;
            g2d.setBackground(Color.WHITE);
            Ring r = new Ring("R64[x]");
            int wi = this.width / ((MatrixS) A).col.length;
            int hi = this.height / ((MatrixS) A).size;
            Element h = ((MatrixS) A).max(r);
            while (((MatrixS) A).colNumb > this.height || ((MatrixS) A).size > this.width) {
                A = shift(((MatrixS) A), r);
            }
            if (h != new NumberR64(0)) {
                for (int i = 0; i < ((MatrixS) A).M.length; i++) {
                    for (int j = 0; j < ((MatrixS) A).M[i].length; j++) {
                        //1-b[i][j]/h;
                        //Вычисляется цвет для соответствующего элемента.
                        NumberR64 a1 = (NumberR64) (((MatrixD) A).M[i][j].abs(r)).divide(h.abs(r), r);
                        NumberR64 s = (NumberR64) new NumberR64("1").subtract(a1.abs(r), r);
                        float t = s.floatValue();
                        if (((MatrixS) A).M[i][j].compareTo(new NumberR64(0), r) == 1) {
                            c = new Color((float) 1, t, (float) 1);
                            g2d.setColor(c);
                        } else {
                            c = new Color(t, (float) 1, (float) 1);
                            g2d.setColor(c);
                        }
                        g2d.fillRect(((MatrixS) A).col[i][j] * wi, i * hi, wi, hi);
                    }
                }
            } else {
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, this.width, this.height);

            }
        } else if (A instanceof MatrixD) {
            Graphics2D g2d = (Graphics2D) g;
            Color c;
            Ring r = new Ring("R64[x]");
            Element h = ((MatrixD) A).max(r);
            Element l = ((MatrixD) A).min(r);
            NumberR64 k = (NumberR64) h.subtract(l, r);
            if (((MatrixD) A).M.length > this.height || ((MatrixD) A).M[0].length > this.width) {
                A = shift(((MatrixD) A), r);
            }
            int wi = this.width / ((MatrixD) A).M.length;
            int hi = this.height / ((MatrixD) A).M[0].length;
            if (k != new NumberR64(0)) {
                for (int i = 0; i < ((MatrixD) A).M.length; i++) {
                    for (int j = 0; j < ((MatrixD) A).M[0].length; j++) {
                        //1 - (a[i][j]-l) / (h-l);
                        //Вычисляется цвет для соответствующего элемента.
                        NumberR64 a1 = (NumberR64) (((MatrixD) A).M[i][j].abs(r)).subtract(l.abs(r), r);
                        NumberR64 a2 = (NumberR64) (h.abs(r).subtract(l.abs(r), r));
                        NumberR64 a3 = (NumberR64) a1.divide(a2, r).abs(r);
                        NumberR64 s = (NumberR64) new NumberR64("1").subtract(a3.abs(r), r);
                        float t = s.floatValue();
                        if (((MatrixD) A).M[i][j].compareTo(new NumberR64(0), r) == 1) {
                            c = new Color((float) 1, t, (float) 1);
                            g2d.setColor(c);
                        } else {
                            c = new Color(t, (float) 1, (float) 1);
                            g2d.setColor(c);
                        }
                        g2d.fillRect(j * wi, i * hi, wi, hi);
                    }
                }
            } else {
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, this.width, this.height);
            }
        }
    }
    //Уменьшает размерность матрицы. Для MatrixS.

    public MatrixS shift(MatrixS B, Ring r) {
        int kx = (B.colNumb / this.width) << 1;
        int ky = (B.size / this.height) << 1;
        Element a[][] = new Element[B.colNumb][B.size];
        Element Bm[][] = new Element[B.colNumb / kx][B.size / ky];
        for (int i = 0; i < B.colNumb / kx; i++) {
            for (int j = 0; j < B.size / ky; j++) {
                Element p = r.numberZERO;
                for (int i1 = i * kx; i1 < (i + 1) * kx; i1++) {
                    for (int j2 = j * ky; j2 < (j + 1) * ky; j2++) {
                        a[i][j] = B.getElement(i, j, r);
                        p = p.add(a[i][j], r);
                    }
                }
                Bm[i][j] = p.divide(new NumberR64(kx * ky), r);
            }
        }
        MatrixS MB = new MatrixS(Bm, r);
        return MB;
    }
    //Уменьшает размерность матрицы. Для MatrixD.

    public MatrixD shift(MatrixD A, Ring r) {
        int kx = (A.M.length / this.width) << 1;
        int ky = (A.M[0].length / this.height) << 1;
        Element Am[][] = new Element[A.M.length / kx][A.M[0].length / ky];
        for (int i = 0; i < A.M.length / kx; i++) {
            for (int j = 0; j < A.M[0].length / ky; j++) {
                Element p = r.numberZERO;
                for (int i1 = i * kx; i1 < (i + 1) * kx; i1++) {
                    for (int j2 = j * ky; j2 < (j + 1) * ky; j2++) {
                        p = p.add(A.M[i][j], r);
                    }
                }
                Am[i][j] = p.divide(new NumberR64(kx * ky), r);
            }
        }
        MatrixD MA = new MatrixD(Am);
        return MA;
    }
}
