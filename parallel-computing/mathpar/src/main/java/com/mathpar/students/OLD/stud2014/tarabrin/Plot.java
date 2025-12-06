package com.mathpar.students.OLD.stud2014.tarabrin;

import com.mathpar.func.*;
import com.mathpar.number.*;
import com.mathpar.polynom.*;

/**
 *
 * @author Администратор
 *
 */
public class Plot {

    public Plot() {
    }

    public void graf(Polynom s1, Polynom s2, NumberR64 t, NumberR64 xmin, NumberR64 xmax, NumberR64 ymin,
            NumberR64 ymax, Ring ring) {
        ring.setDefaulRing();
        Element[][] xy = Cut.cut(s1, s2, t, xmin, xmax, ymin.subtract(t.multiply(new NumberR64(2))), ymax.add(t.multiply(new NumberR64(2))), ring);
        Polynom[][] pol = new Polynom[xy.length][];
        for (int i = 0; i <= pol.length - 1; i++) {
            if ((i + 2) % 2 == 0) {
                Polynom[] px = new Polynom[xy[i].length];
                Polynom[] py = new Polynom[xy[i + 1].length];
                for (int j = 0; j <= xy[i].length - 1; j++) {
                    px[j] = new Polynom(xy[i][j]);
                    py[j] = new Polynom(xy[i + 1][j]);
                }
                pol[i] = px;
                pol[i + 1] = py;
            }
        }
        F[] f = new F[pol.length];
        for (int i = 0; i <= pol.length - 1; i++) {
            f[i] = new F(F.VECTORS, pol[i]);
        }
        F gv = new F(F.VECTORS, new Element[]{new Polynom(xmin), new Polynom(xmax), new Polynom(ymin), new Polynom(ymax)});
        F tt = new F(F.VECTORS, f);
        F tp = new F(F.TABLEPLOT, new F[]{tt, gv});
        tp.showGraphics(null, true, false,false, null); 
    }
}
