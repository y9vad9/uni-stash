package com.mathpar.polynom.gbasis.util;

import com.mathpar.func.F;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.number.VectorS;
import com.mathpar.polynom.Polynom;
import com.mathpar.polynom.gbasis.algorithms.Buchberger;
import com.mathpar.polynom.gbasis.algorithms.Faugere;
import com.mathpar.polynom.gbasis.algorithms.SNLESolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Utility class for running gbasis package related commands.
 *
 * @author ivan
 */
public final class CommandsRunner {
    private CommandsRunner() {
    }

    private static enum AlgType {
        FAUGERE_F4,
        BUCHBERGER_SIMPLE
    }

    /**
     * For \gbasis(p1, p2, ..., pN) command execution. Computes Groebner basis
     * with Faugere's F4 algorithm.
     *
     * @param ring      ring
     * @param arguments array of Polynomials -- input polynomial ideal
     * @return Vector of Polynomials with Groebner basis
     */
    public static VectorS runGbasis(final Ring ring, final Element[] arguments) {
        Element[] arguments1 = ((arguments.length==1)&(arguments[0] instanceof VectorS))? ((VectorS)arguments[0]).V: arguments;
        return runGb(ring, arguments1, AlgType.FAUGERE_F4);
    }

    /**
     * For \gbasisB(p1, p2, ..., pN) command execution. Computes Groebner basis
     * with the simples version of Buchberger algorithm.
     *
     * @param ring      ring
     * @param arguments array of Polynomials -- input polynomial ideal
     * @return Vector of Polynomials with Groebner basis
     */
    public static VectorS runGbasisB(final Ring ring, final Element[] arguments) {
          Element[] arguments1 = ((arguments.length==1)&(arguments[0] instanceof VectorS))? ((VectorS)arguments[0]).V: arguments;
        return runGb(ring, arguments1, AlgType.BUCHBERGER_SIMPLE);
    }

    /**
     * For \solveNAE(p1, p2, ..., pN) command execution.
     *
     * @param ring      ring
     * @param arguments array with Polynomials -- input polynomial system.
     * @return Vector of Vectors where each inner vector is a tuple of solutions
     * or the rest of the system if there is no single variable polynomial left at some step.
     * @throws IllegalArgumentException if input is incorrect or solver can't solve given system.
     * @throws ArithmeticException      if there was an error during one variable
     *                                  polynomial roots computation.
     */
    public static VectorS runSolveNAE(final Ring ring, final Element[] arguments) {
        List<Polynom> system = checkAndConvertVectorOfPolynomials(ring, arguments,
                "Arguments must be polynomials");
        SNLESolver solver = new SNLESolver(ring, system);
        List<SortedMap<Polynom, Element>> solution = solver.solve();
        Element[] solutions = new Element[solution.size()];
        int solutionSize = solution.get(0).size();
        for (int i = 0; i < solutions.length; i++) {
            solutions[i] = new VectorS(solution.get(i).values().toArray(new Element[solutionSize]));
        }
        return new VectorS(solutions);
    }

    /**
     * For \reduceByGB(p, [g1, g2, ..., gN]) command execution.
     *
     * @param ring ring
     * @param args
     * @return
     */
    public static Polynom runReduceByGb(final Ring ring, final Element[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("\\reduceByGB function takes 2 arguments.");
        }
        if (!(args[0] instanceof Polynom)) {
            throw new IllegalArgumentException("First argument must be a polynomial.");
        }
        if (!(args[1] instanceof VectorS)) {
            throw new IllegalArgumentException("Second argument must be a vector.");
        }
        Polynom reducible = (Polynom) args[0];
        List<Polynom> gb = checkAndConvertVectorOfPolynomials(ring,
                ((VectorS) args[1]).V,
                "Second argument must be an array of polynomials");

        return Buchberger.rem(ring, reducible, gb);
        //Faugere f4 = new Faugere(ring, gb);
//        List<Polynom> reduced = f4.reductionWithHT(
//                new ArrayList<Polynom>(Arrays.asList(reducible)), gb);
//        return reduced.isEmpty() ? reducible : reduced.get(0);
    }

    /**
     * @param ring      ring
     * @param args      array of Polynomials -- input polynomial ideal
     * @param algorithm algorithm to use
     * @return Vector of Polynomials with Groebner basis
     */
    private static VectorS runGb(final Ring ring, final Element[] args,
                                 final AlgType algorithm) {
        List<Polynom> ideal = checkAndConvertVectorOfPolynomials(ring, args,
                "Groebner basis needs polynomials as arguments");
        List<Polynom> result = new ArrayList<Polynom>();
        switch (algorithm) {
            case BUCHBERGER_SIMPLE:
                result = new Buchberger(ring, ideal).gbasis();
                break;
            case FAUGERE_F4:
                result = new Faugere(ring, ideal).gbasis();
                break;
            default:
                break;
        }
        return new VectorS(result.toArray(new Polynom[result.size()]));
    }

    /**
     * Converts array of polynomials to
     *
     * @param ring   ring
     * @param args   Vector of Polynomials
     * @param errMsg error message if some of arguments isn't polynomial
     * @return list containig polynomials from given array
     */
    private static List<Polynom> checkAndConvertVectorOfPolynomials(
            final Ring ring, final Element[] args, final String errMsg) {
        List<Polynom> result = new ArrayList<>();
        Element[] polynomials = args;
        if (args[0] instanceof VectorS) {
            polynomials = ((VectorS) args[0]).V;
        }
        for (Element e : polynomials) {
            if (e instanceof F) {
                e = ((F) e.expand(ring)).X[0];
            }
            if (e instanceof Polynom) {
                result.add((Polynom) e);
            } else {
                throw new IllegalArgumentException(errMsg + " (error on: "
                        + e.toString(ring) + ")");
            }
        }
        return result;
    }
}
