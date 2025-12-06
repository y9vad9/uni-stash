package com.mathpar.polynom.gbasis.playground;

import static org.apache.logging.log4j.LogManager.getLogger;

import java.util.ArrayList;
import java.util.List;
import com.mathpar.number.Element;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;
import com.mathpar.polynom.gbasis.algorithms.FaugereNew;
import com.mathpar.polynom.gbasis.Gbasis;
import com.mathpar.polynom.gbasis.util.Utils;
import org.apache.logging.log4j.Logger;

public class ModuloPlayground {
    private static final Logger LOG = getLogger(ModuloPlayground.class);

    public static void doTest(Ring ring, Ring ringMod, List<Polynom> pols, boolean modulo) {
        List<Polynom> pMod = new ArrayList<Polynom>(pols.size());
        for (int i = 0, sz = pols.size(); i < sz; i++) {
            pMod.add(pols.get(i).toPolynomMod(Ring.Zp, ringMod));
        }

        Ring rMod = modulo ? ringMod : ring;
        List<Polynom> test = modulo ? pMod : pols;

        if (modulo) {
            LOG.trace("modulo: {}", Element.colToStr(rMod, test));
        }

        int newVarIdx = Utils.getIndexOfNewHighestVarForHomogenization(pols);
        LOG.trace("new var index: {}", newVarIdx);
        List<Polynom> homogenized = Utils.homogenize(test, newVarIdx);

        LOG.trace("Homogenized input: {}", Element.colToStr(rMod, homogenized));
        Gbasis gb = new FaugereNew(rMod, homogenized);
        List<Polynom> result = gb.gbasis();
        LOG.trace("Result: {}", Element.colToStr(rMod, result));
        LOG.trace("Dehomogenized result: {}",
                Element.colToStr(ring, Utils.dehomogenize(result, newVarIdx)));
        if (modulo) {
            List<Polynom> recovered = new ArrayList<Polynom>(result.size());
            for (int i = 0, sz = result.size(); i < sz; i++) {
                Polynom rec = (Polynom) result.get(i).clone();
                for (int j = 0, len = rec.coeffs.length; j < len; j++) {
                    rec.coeffs[j] = com.mathpar.number.Newton.recoveryNewton(new int[] {101},
                            new int[] {rec.coeffs[j].intValue()});//[0];
                }
                recovered.add(rec);
            }
            LOG.trace("Recovered: {}", Element.colToStr(ring, recovered));
            LOG.trace("Dehomogenized recovered: {}",
                    Element.colToStr(ring, Utils.dehomogenize(recovered, newVarIdx)));
        }
    }

    public static void main(String[] args) {
//        Ring r = new Ring("Z[z, y, x]Z[t]");
//        Ring rp = new Ring("Zp[z, y, x]Zp[t]");
//        rp.setMOD(new NumberZ("101"));
//
//        List<Polynom> test = Utils.polList(r,
//                "x + 2*y + 2*z - 1",
//                "x^2 - x + 2*y^2 + 2*z^2",
//                "2*x*y + 2*y*z - y");

        /*
         "7x-420z^3+158z^2+8z-7",
         "7y+210z^3-79z^2+3z",
         "84z^4-40z^3+z^2+z"
         */
//        doTest(r, rp, test, true);
        Ring ring = new Ring("Q[x, y, z]");
        test1(ring, new NumberZ[] {new NumberZ("1")});
    }

    public static void test1(Ring ring, NumberZ[] modules) {
        for (int i = 0, sz = modules.length; i < sz; i++) {
            Ring ringMod = new Ring(ring, Ring.Zp);
            NumberZ currModulo = modules[i];
            ringMod.setMOD(currModulo);
            LOG.trace("ring: {}", ring);
            LOG.trace("ringMod: {}", ringMod);
            LOG.trace("ringMod.MOD: {}", ringMod.MOD);
        }
    }
}
