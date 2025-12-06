package com.mathpar.polynom.gbasis.datastructures;

import com.mathpar.polynom.gbasis.util.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Array;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;
import com.mathpar.polynom.Term;

/**
 * Stores terms pointers as is unlike PolyListDefault which stores.
 *
 * @author Ivan Borisov
 */
public class PolyListInplace implements PolyList {
    private final Ring ring;
    private final TermsContainer terms;
    private Element[][] rowsCoeffs;
    private Term[][] rowsTerms;

    public PolyListInplace(Ring ring) {
        this.ring = ring;
        terms = new TermsList(ring);
        rowsCoeffs = new Element[][] {};
        rowsTerms = new Term[][] {};
    }

    public PolyListInplace(Ring ring, List<Polynom> pols) {
        this.ring = ring;
        terms = new TermsList(ring, Utils.getAllTerms(pols), true);
        int rowsCnt = pols.size();
        rowsCoeffs = new Element[rowsCnt][];
        rowsTerms = new Term[rowsCnt][];
        for (int i = 0; i < rowsCnt; i++) {
            Polynom currPol = pols.get(i);
            Element[] currRow = currPol.coeffs;
            int currRowLen = currRow.length;
            Term[] currRowTerms = new Term[currRowLen];
            for (int j = 0; j < currRowLen; j++) {
                currRowTerms[j] = currPol.term(j);
            }
            rowsCoeffs[i] = currRow;
            rowsTerms[i] = currRowTerms;
        }
    }

    @Override
    public int size() {
        return rowsCoeffs.length;
    }

    @Override
    public void add(Polynom polynom) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Term> ht() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void copyToAndRemovePols(PolyList dest, int... indices) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void remove(int... indices) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void toEchelonForm(boolean useGbEchelonForm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addAll(Collection<Polynom> pols) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TermsContainer terms() {
        return terms;
    }

    public Element[][] rows() {
        int size = size();
        Element[][] res = new Element[size][];
        for (int i = 0; i < size; i++) {
            res[i] = Array.copyOf(rowsCoeffs[i], rowsCoeffs[i].length);
        }
        return res;
    }

    public Term[] rowTerms(int index) {
        return this.rowsTerms[index];
    }

    @Override
    public MatrixS toMatrixS() {
        int[][] colIndices = new int[size()][];
        for (int i = 0; i < rowsTerms.length; i++) {
            colIndices[i] = new int[rowsTerms[i].length];
            for (int j = 0; j < rowsTerms[i].length; j++) {
                colIndices[i][j] = terms.indexOf(rowsTerms[i][j]);
            }
        }
        return new MatrixS(rows(), colIndices);
    }

    @Override
    public List<Polynom> toList() {
        List<Polynom> res = new ArrayList<Polynom>();
        for (int i = 0; i < rowsCoeffs.length; i++) {
            res.add(get(i));
        }
        return res;
    }

    @Override
    public void multiply(int index, Term term) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Term lcmHT(int index1, int index2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Term ht(int index) {
        return rowsTerms[index][0];
    }

    @Override
    public Polynom get(int index) {
        Polynom res = new Polynom(ring);
        for (int i = 0; i < rowsCoeffs[index].length; i++) {
            res = res.add(new Polynom(
                    rowsTerms[index][i].powers(),
                    new Element[] {rowsCoeffs[index][i]}), ring);
        }
        return res;
    }

    @Override
    public void addTerm(Term term) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void appendFrom(PolyList source, int... sourceIndices) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Term term(int index, int column) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Element coeff(int index, int column) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int rowSize(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Element[] row(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int[] rowOfColInd(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void set(int index, Element[] row) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setColIndex(int index, int col, int newIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRowOfColInd(int index, int[] newIndices) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void computeLrPairAndAddTo(int index1, int index2, PolyList dest) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cancelRow(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
