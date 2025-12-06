package com.mathpar.polynom.gbasis.playground;

import static org.apache.logging.log4j.LogManager.getLogger;

import java.util.List;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;
import com.mathpar.polynom.gbasis.algorithms.Faugere;
import com.mathpar.polynom.gbasis.Gbasis;
import com.mathpar.polynom.gbasis.util.Utils;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ivan
 */
public class Playground {
    private static final Logger LOG = getLogger(Playground.class);

    public static void main(String[] args) {
        Ring ring = new Ring("Z[x, y, z]");

        List<Polynom> testIdeal = Utils.polList(ring,
                "x^4y^3+2xy^2+3x+1",
                "x^3y^2+x^2",
                "x^4y+z^2+x y^4+3");
        // Answer should be
        // "z^2-x^4+3x^2-10x+9",   "y-9x^4-3x^3-x^2-81x+27",    "x^5+9x^2-6x+1"
        Gbasis gb = new Faugere(ring, testIdeal);
        List<Polynom> result = gb.gbasis();
        LOG.info(Element.colToStr(ring, result));
    }
}
