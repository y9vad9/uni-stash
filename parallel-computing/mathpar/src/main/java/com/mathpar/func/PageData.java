package com.mathpar.func;

import java.util.ArrayList;
import com.mathpar.number.Element;
import com.mathpar.number.IntList;

/**
 *
 * @author Dubovitsky Evgeny
 */
public class PageData {
    /**
     * Секция страницы. Section[0] - source code; section[1] - result.
     * .... разрешено только 0 и 1 ? или можно  брать любую секцию из Page ... вряд ли...
     */
    public StringBuffer[] section;
    /**
     * Список всех сохраненных выражений на странице.
     */
    public ArrayList<Element> funcs;
    /**
     * Список действий.
     */
    public ArrayList<IntList> transProg;
    /**
     * Список имен процедур на странице.
     */
    public ArrayList<String> procNames;
    /**
     * Список аргументов процедур на странице.
     */
    public ArrayList<String> argsOfProc;

    public PageData() {
        init();
    }

    /**
     * Initializes this PageData with input\output from given array of
     * StringBuffers (0-th element -- input, 1-st element -- output).
     *
     * @param section
     */
    public void init(StringBuffer[] section) {
        funcs = new ArrayList<Element>(16);
        transProg = new ArrayList<IntList>(2);
        procNames = new ArrayList<String>(2);
        argsOfProc = new ArrayList<String>(2);
        this.section = section;
    }

    /**
     * Initializes this PageData with empty input\output.
     */
    public void init() {
        init(new StringBuffer[] {new StringBuffer(), new StringBuffer()});
    }

    public void init(String input) {
        init(new StringBuffer[] {new StringBuffer(input), new StringBuffer()});
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (section[0].toString().length() > 0) {
            sb.append("Source code:\n").append(section[0]);
        }
        if (!funcs.isEmpty()) {
            sb.append("\n\nOperator's trees:");
            for (int i = 0; i < funcs.size(); i++) {
                Fname fn = (Fname) funcs.get(i);
                sb.append("\n\nname = ").append(fn.name);
                for (int j = 0; j < fn.X.length; j++) {
                    sb.append("\n").append(fn.X[j]);
                }
            }
        }
        if (!transProg.isEmpty()) {
            sb.append("\n\nProgramm:");
            int[] mass;
            for (int i = 0; i < transProg.size(); i++) {
                sb.append("\n");
                mass = transProg.get(i).toArray();
                for (int j = 0; j < mass.length; j++) {
                    sb.append(mass[j]).append(" ");
                }
            }
        }
        if (section[1].toString().length() > 0) {
            sb.append("\n\nResult:\n").append(section[1]);
        }
        return sb.toString();
    }
}
