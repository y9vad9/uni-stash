package com.mathpar.polynom.gbasis.util;

import com.mathpar.polynom.gbasis.algorithms.Buchberger;
import com.mathpar.polynom.gbasis.algorithms.Faugere;
import com.mathpar.number.Ring;
import com.mathpar.polynom.gbasis.Gbasis;

/**
 * Performance tests for gbasis
 *
 * @author ivan
 */
public class PerfTest {
    private static final class Test {
        private static final int ITERATIONS_NUM = 10;
        private Gbasis alg;
        private String title;

        Test(String title, Gbasis algorithm) {
            this.title = title;
            this.alg = algorithm;
        }

        public void run() {
            long timeBefore = System.nanoTime();
            for (int i = 0; i < ITERATIONS_NUM; i++) {
                alg.gbasis();
            }
            long timeAfter = System.nanoTime() - timeBefore;
            double timeInSeconds = timeAfter * 1e-9d / ITERATIONS_NUM;
            System.out.println(String.format("%s\t%f", title, timeInSeconds));
        }
    }
    private static final Test[] TESTS_BUCHBERGER = new Test[] {
        new Test("katsura3", new Buchberger(
        new Ring("Q[z, y, x]"),
        new String[] {
            "x + 2*y + 2*z - 1",
            "x^2 - x + 2*y^2 + 2*z^2",
            "2*x*y + 2*y*z - y"
        })),
        new Test("katsura4", new Buchberger(
        new Ring("Q[d, c, b, a]"),
        new String[] {
            "a + 2*b + 2*c + 2*d - 1",
            "a^2 - a + 2*b^2 + 2*c^2 + 2*d^2",
            "2*a*b + 2*b*c - b + 2*c*d",
            "2*a*c + b^2 + 2*b*d - c"
        })),
        new Test("cyclic4", new Buchberger(
        new Ring("Q[d, c, b, a]"),
        new String[] {
            "abcd - 1",
            "abc + abd + acd + bcd",
            "ab + ad + bd + cd",
            "a + b + c + d"
        })),
        new Test("ex1", new Buchberger(
        new Ring("Q[x, y, z]"),
        new String[] {
            "y^2 + 2yx^2",
            "yx + 2x^3 -1"
        })),
        new Test("ex2", new Buchberger(
        new Ring("Q[x, y]"),
        new String[] {
            "x^2 - y^2",
            "x^2 + y"
        })),
        new Test("ex3", new Buchberger(
        new Ring("Z[z, y, x]"),
        new String[] {
            "y - x^2",
            "z - x^3"
        })),
        new Test("mathematica1", new Buchberger(
        new Ring("Q[x, y, z]"),
        new String[] {
            "y^2 - 2x^2",
            "xy - 3"
        })),
        new Test("mathematica2", new Buchberger(
        new Ring("Q[x, y, z]"),
        new String[] {
            "x + y",
            "x^2 - 1",
            "y^2 - 2x"
        })),
        new Test("cox1", new Buchberger(
        new Ring("Q[w, z, y, x]"),
        new String[] {
            "3x - 6y - 2z",
            "2x - 4y + 4w",
            "x - 2y - z - w"
        })),
        new Test("cox2", new Buchberger(
        new Ring("Q[z, y, x]"),
        new String[] {
            "x^2 + y^2 + z^2 - 1",
            "x^2 + z^2 - y",
            "x - z"
        })),
        new Test("sage1", new Buchberger(
        new Ring("Z[z, y, x]"),
        new String[] {
            "x^6 + 4*x^4*y^2 + 4*x^2*y^4",
            "x^2*y^2"
        })),
        new Test("sage2", new Buchberger(
        new Ring("Z[z, y, x]"),
        new String[] {
            "x^2*y - z",
            "y^2*z - x",
            "z^2*x - y"
        })),
        new Test("sage3", new Buchberger(
        new Ring("Q[z, y, x]"),
        new String[] {
            "z*x+y^3",
            "z+y^3",
            "z+x*y"
        })),
        new Test("sage001", new Buchberger(
        new Ring("Q[z, y, x]"),
        new String[] {
            "-4*x*y^2*z + y*z",
            "-x*z + 21*y^2"
        })),
        new Test("sage002", new Buchberger(
        new Ring("Q[z, y, x]"),
        new String[] {
            "x*y^2 - x*y*z^2",
            "-z"
        })),
        new Test("sage003", new Buchberger(
        new Ring("Q[z, y, x]"),
        new String[] {
            "-x^3",
            "-16*x*z + y^2"
        })),
        new Test("sage004", new Buchberger(
        new Ring("Q[z, y, x]"),
        new String[] {
            "3*x^2*z + x*y",
            "-2*z^2 - 7*z"
        })),
        new Test("sage005", new Buchberger(
        new Ring("Q[z, y, x]"),
        new String[] {
            "x^2*z^2 - x",
            "-70*x*y - 2*y"
        })),
        new Test("sage006", new Buchberger(
        new Ring("Q[z, y, x]"),
        new String[] {
            "-x*y^2*z - 2*x*z^2 + 3*y*z^2",
            "x"
        })),
        new Test("sage007", new Buchberger(
        new Ring("Q[z, y, x]"),
        new String[] {
            "-x*y^3 + 6*x*y^2*z + z^3",
            "y^2"
        })),
        new Test("sage008", new Buchberger(
        new Ring("Q[z, y, x]"),
        new String[] {
            "-10*x^2 + x",
            "-2*z - 37"
        })),
        new Test("sage009", new Buchberger(
        new Ring("Q[z, y, x]"),
        new String[] {
            "-3*y^4 - y^3*z",
            "y^2 - z"
        })),
        new Test("sage010", new Buchberger(
        new Ring("Q[z, y, x]"),
        new String[] {
            "-x^2*z^2 + 5*x*y^2*z",
            "5*x*z - 3*z^2"
        })),
        new Test("cocoa1", new Buchberger(
        new Ring("Q[z, y, x]"),
        new String[] {
            "x-y+z-2",
            "3x-z+6",
            "x+y-1"
        }))
    };
    private static final Test[] TESTS_FAUGERE_F4 = new Test[] {
        new Test("katsura3", new Faugere(
        new Ring("Z[z, y, x]"),
        new String[] {
            "x + 2*y + 2*z - 1",
            "x^2 - x + 2*y^2 + 2*z^2",
            "2*x*y + 2*y*z - y"
        })),
        new Test("katsura4", new Faugere(
        new Ring("Z[d, c, b, a]"),
        new String[] {
            "a + 2*b + 2*c + 2*d - 1",
            "a^2 - a + 2*b^2 + 2*c^2 + 2*d^2",
            "2*a*b + 2*b*c - b + 2*c*d",
            "2*a*c + b^2 + 2*b*d - c"
        })),
        new Test("cyclic4", new Faugere(
        new Ring("Z[d, c, b, a]"),
        new String[] {
            "abcd - 1",
            "abc + abd + acd + bcd",
            "ab + ad + bd + cd",
            "a + b + c + d"
        })),
        new Test("ex1", new Faugere(
        new Ring("Z[x, y, z]"),
        new String[] {
            "y^2 + 2yx^2",
            "yx + 2x^3 -1"
        })),
        new Test("ex2", new Faugere(
        new Ring("Z[x, y]"),
        new String[] {
            "x^2 - y^2",
            "x^2 + y"
        })),
        new Test("ex3", new Faugere(
        new Ring("Z[z, y, x]"),
        new String[] {
            "y - x^2",
            "z - x^3"
        })),
        new Test("mathematica1", new Faugere(
        new Ring("Z[x, y, z]"),
        new String[] {
            "y^2 - 2x^2",
            "xy - 3"
        })),
        new Test("mathematica2", new Faugere(
        new Ring("Z[x, y, z]"),
        new String[] {
            "x + y",
            "x^2 - 1",
            "y^2 - 2x"
        })),
        new Test("cox1", new Faugere(
        new Ring("Z[w, z, y, x]"),
        new String[] {
            "3x - 6y - 2z",
            "2x - 4y + 4w",
            "x - 2y - z - w"
        })),
        new Test("cox2", new Faugere(
        new Ring("Z[z, y, x]"),
        new String[] {
            "x^2 + y^2 + z^2 - 1",
            "x^2 + z^2 - y",
            "x - z"
        })),
        new Test("sage1", new Faugere(
        new Ring("Z[z, y, x]"),
        new String[] {
            "x^6 + 4*x^4*y^2 + 4*x^2*y^4",
            "x^2*y^2"
        })),
        new Test("sage2", new Faugere(
        new Ring("Z[z, y, x]"),
        new String[] {
            "x^2*y - z",
            "y^2*z - x",
            "z^2*x - y"
        })),
        new Test("sage3", new Faugere(
        new Ring("Z[z, y, x]"),
        new String[] {
            "z*x+y^3",
            "z+y^3",
            "z+x*y"
        })),
        new Test("sage001", new Faugere(
        new Ring("Z[z, y, x]"),
        new String[] {
            "-4*x*y^2*z + y*z",
            "-x*z + 21*y^2"
        })),
        new Test("sage002", new Faugere(
        new Ring("Z[z, y, x]"),
        new String[] {
            "x*y^2 - x*y*z^2",
            "-z"
        })),
        new Test("sage003", new Faugere(
        new Ring("Z[z, y, x]"),
        new String[] {
            "-x^3",
            "-16*x*z + y^2"
        })),
        new Test("sage004", new Faugere(
        new Ring("Z[z, y, x]"),
        new String[] {
            "3*x^2*z + x*y",
            "-2*z^2 - 7*z"
        })),
        new Test("sage005", new Faugere(
        new Ring("Z[z, y, x]"),
        new String[] {
            "x^2*z^2 - x",
            "-70*x*y - 2*y"
        })),
        new Test("sage006", new Faugere(
        new Ring("Z[z, y, x]"),
        new String[] {
            "-x*y^2*z - 2*x*z^2 + 3*y*z^2",
            "x"
        })),
        new Test("sage007", new Faugere(
        new Ring("Z[z, y, x]"),
        new String[] {
            "-x*y^3 + 6*x*y^2*z + z^3",
            "y^2"
        })),
        new Test("sage008", new Faugere(
        new Ring("Z[z, y, x]"),
        new String[] {
            "-10*x^2 + x",
            "-2*z - 37"
        })),
        new Test("sage009", new Faugere(
        new Ring("Z[z, y, x]"),
        new String[] {
            "-3*y^4 - y^3*z",
            "y^2 - z"
        })),
        new Test("sage010", new Faugere(
        new Ring("Z[z, y, x]"),
        new String[] {
            "-x^2*z^2 + 5*x*y^2*z",
            "5*x*z - 3*z^2"
        })),
        new Test("cocoa1", new Faugere(
        new Ring("Z[z, y, x]"),
        new String[] {
            "x-y+z-2",
            "3x-z+6",
            "x+y-1"
        }))
    };

    private PerfTest() {
    }

    public static void main(String[] args) {
        System.out.println("====== Buchberger");
        for (Test test : TESTS_BUCHBERGER) {
            test.run();
        }

        System.out.println("====== F4 simple");
        for (Test test : TESTS_FAUGERE_F4) {
            test.run();
        }
    }
}
