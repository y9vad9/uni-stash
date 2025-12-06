package com.mathpar.polynom.gbasis.datastructures;

import com.mathpar.polynom.gbasis.util.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Array;
import com.mathpar.number.Element;
import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;
import com.mathpar.polynom.Term;

/**
 * Represents list of polynomials as a matrix.
 *
 * @author Ivan Borisov
 * @see MatrixS
 * @see Term
 */
public class PolyListDefault implements PolyList {
    private final Ring ring;
    private final TermsContainer terms;
    private Element[][] rows;
    private int[][] colInd;

    /**
     * Constructs PolyMatrix with empty polynomial list.
     *
     * @param ring ring.
     */
    public PolyListDefault(Ring ring) {
        this.ring = ring;
        terms = new TermsList(ring);
        rows = new Element[][] {};
        colInd = new int[][] {};
    }

    /**
     * Constructs PolyMatrix from list of polynomials.
     *
     * @param ring ring.
     * @param pols list of polynomials.
     */
    public PolyListDefault(Ring ring, List<Polynom> pols) {
        this.ring = ring;
        terms = new TermsList(ring, Utils.getAllTerms(pols), true);
        int rowsCnt = pols.size();
        rows = new Element[rowsCnt][];
        colInd = new int[rowsCnt][];
        // Fill rows and columnIndices.
        for (int i = 0; i < rowsCnt; i++) {
            Polynom currentPol = pols.get(i);
            Element[] currRow = currentPol.coeffs;
            int currRowLen = currRow.length;
            int[] currColIndRow = new int[currRowLen];
            // Fill row of terms column indexes.
            for (int j = 0; j < currRowLen; j++) {
                currColIndRow[j] = terms.indexOf(currentPol.term(j));
            }
            rows[i] = currRow;
            colInd[i] = currColIndRow;
        }
    }

    /**
     * Fof debug purpose only.
     *
     * @return string with polynomials in this PolyListDefault.
     */
    private String debug() {
        return Element.colToStr(ring, toList());
    }

    /**
     * @return number of polynomials in this PolyListDefault.
     */
    @Override
    public int size() {
        return rows.length;
    }

    /**
     * Adds empty rows to this PolyListDefault.
     *
     * @param columnCnt number of empty rows to add.
     */
    private void reserveRowsAtTheEnd(int columnCnt) {
        int oldLen = rows.length;
        Element[][] newRows = new Element[oldLen + columnCnt][];
        int[][] newCols = new int[oldLen + columnCnt][];

        System.arraycopy(rows, 0, newRows, 0, oldLen);
        System.arraycopy(colInd, 0, newCols, 0, oldLen);

        rows = newRows;
        colInd = newCols;
    }

    /**
     * Adds given polynomial to this PolyListDefault.
     *
     * @param polynom
     */
    @Override
    public void add(Polynom polynom) {
        int oldLen = rows.length;
        Element[][] newRows = new Element[oldLen + 1][];
        int[][] newCols = new int[oldLen + 1][];

        System.arraycopy(rows, 0, newRows, 0, oldLen);
        System.arraycopy(colInd, 0, newCols, 0, oldLen);

        List<Term> polTerms = Utils.getAllTerms(polynom);
        int[][] addRes = terms.addAll(polTerms);
        // Fill new row of terms column indexes.
        int[] newCol = new int[polynom.coeffs.length];
        for (int i = 0, len = newCol.length; i < len; i++) {
            newCol[i] = terms.indexOf(polTerms.get(i));
        }
        // Update terms indexes except last new columnt.
        applyMap(newCols, addRes, 1);

        newRows[oldLen] = polynom.coeffs;
        newCols[oldLen] = newCol;

        rows = newRows;
        colInd = newCols;
    }

    /**
     * @return set with highest terms of all polynomials from this
     * PolyListDefault.
     */
    @Override
    public Set<Term> ht() {
        Set<Term> ht = new LinkedHashSet<Term>();
        int size = size();
        for (int i = 0; i < size; i++) {
            ht.add(term(i, 0));
        }
        return ht;
    }

    /**
     * Updates column indexes with given map.
     *
     * @param cols matrix column indexes.
     * @param map 2D int array, where element at 0-th position is an array with
     * map ({@code map[0][i] => map[0][i+1]}, where i - old position, i+1 - new
     * position).
     */
    private static void applyMap(int[][] cols, int[][] map) {
        applyMap(cols, map, 0);
    }

    /**
     * Updates column indexes with given map.
     *
     * @param cols matrix column indexes.
     * @param map 2D int array, where element at 0-th position is an array with
     * map ({@code map[0][i] => map[0][i+1]}, where i - old position, i+1 - new
     * position).
     * @param numToSkip number of rows to skip.
     */
    private static void applyMap(int[][] cols, int[][] map, int numToSkip) {
        int rowsNumberToApply = cols.length - numToSkip;
        // Don't apply map if
        if (map[1][0] == -1) {
            return;
        }
        if (map[0].length > 0) {
            for (int i = 0; i < rowsNumberToApply; i++) {
                for (int j = 0, len = cols[i].length; j < len; j++) {
                    cols[i][j] = map[0][cols[i][j]];
                }
            }
        }
    }

    /**
     * Copies polynomials at given positions from this PolyListDefault to
     * {@code dest} and removes them from this PolyListDefault.
     *
     * @param dest PolyListDefault object where to copy polynomials.
     * @param indices positions of polynomials .
     */
    public void copyToAndRemovePols(PolyListDefault dest, int... indices) {
        appendPols(this, dest, indices);
        remove(indices);
    }

    /**
     * Removes polynomials at given positions from this PolyListDefault.
     *
     * @param indices positions of polynomials in this PolyListDefault.
     */
    @Override
    public void remove(int... indices) {
        int polysPosLen = indices.length;
        for (int i = 0; i < polysPosLen; i++) {
            int polPos = indices[i];
            rows[polPos] = null;
            colInd[polPos] = null;
        }
        int size = size();
        Element[][] newRows = new Element[size - polysPosLen][];
        int[][] newColInd = new int[size - polysPosLen][];
        int notNullRowIdx = 0;
        // Copy not-null rows.
        for (int i = 0; i < size; i++) {
            if (rows[i] != null) {
                newRows[notNullRowIdx] = rows[i];
                newColInd[notNullRowIdx] = colInd[i];
                notNullRowIdx++;
            }
        }
        rows = newRows;
        colInd = newColInd;
    }

    /**
     * Updates this PolyListDefault with echelon form.
     *
     * @param useGbEchelonForm use special MatrixS.toGroebnerEchelonForm() if
     * true, use MatrixS.toEchelonForm() otherwise.
     */
    @Override
    public void toEchelonForm(boolean useGbEchelonForm) {
        MatrixS rowEchelonMatrix = useGbEchelonForm
                ? new MatrixS(rows, colInd).toGroebnerEchelonForm(ring)
                : new MatrixS(rows, colInd).toEchelonForm(ring);
        rowEchelonMatrix.sort();
        // Count non-empty rows
        int nonEmptyRows = 0;
        for (int i = 0, len = rowEchelonMatrix.M.length; i < len; i++) {
            if (rowEchelonMatrix.M[i].length == 0) {
                break;
            }
            nonEmptyRows++;
        }
        rows = new Element[nonEmptyRows][];
        colInd = new int[nonEmptyRows][];
        System.arraycopy(rowEchelonMatrix.M, 0, rows, 0, nonEmptyRows);
        System.arraycopy(rowEchelonMatrix.col, 0, colInd, 0, nonEmptyRows);
    }

    /**
     * Adds all polynomials from given collection to this PolyListDefault.
     *
     * @param pols collection of polynomials.
     */
    @Override
    public void addAll(Collection<Polynom> pols) {
        for (Polynom p : pols) {
            add(p);
        }
    }

    /**
     * @return all terms.
     */
    @Override
    public TermsContainer terms() {
        return terms;
    }

    /**
     * @return rows of matrix - coefficients of polynomials.
     */
    public Element[][] rows() {
        int rowsLen = rows.length;
        Element[][] res = new Element[rowsLen][];
        for (int i = 0; i < rowsLen; i++) {
            res[i] = Array.copyOf(rows[i], rows[i].length);
        }
        return res;
    }

    /**
     * @return terms column indexes.
     */
    public int[][] columnIndices() {
        int rowsLen = rows.length;
        int[][] res = new int[rowsLen][];
        for (int i = 0; i < rowsLen; i++) {
            res[i] = Array.copyOf(colInd[i], colInd[i].length);
        }
        return res;
    }

    /**
     * @return matrix representation of polynomials.
     */
    @Override
    public MatrixS toMatrixS() {
        return new MatrixS(rows(), columnIndices());
    }

    /**
     * @return result of conversion of this PolyListDefault to List of
     * polynomials.
     */
    @Override
    public List<Polynom> toList() {
        List<Polynom> res = new ArrayList<Polynom>();
        for (int i = 0; i < rows.length; i++) {
            res.add(get(i));
        }
        return res;
    }

    /**
     * Multiplies polynomial from given position on given term.
     *
     * @param index position of polynomial.
     * @param term term to multiply on.
     */
    @Override
    public void multiply(int index, Term term) {
        // Don't multiply on term with only zero powers.
        if (term.hasZeroPowers()) {
            return;
        }
        List<Term> newTerms = new ArrayList<Term>();
        int rowLen = rows[index].length;
        // Mutiply polynomial term by term.
        for (int i = 0; i < rowLen; i++) {
            newTerms.add(terms.get(colInd[index][i]).multiply(term).trim());
        }
        // Insert new terms in list of all terms and update indexes.
        applyMap(colInd, terms.addAll(newTerms));
        // Replace row of old indexes with new.
        for (int i = 0; i < rowLen; i++) {
            colInd[index][i] = terms.indexOf(newTerms.get(i));
        }
    }

    /**
     * @param index1 position of first polynomial.
     * @param index2 position of second polynomial.
     *
     * @return lcm of highest terms of polynomials at given positions.
     */
    @Override
    public Term lcmHT(int index1, int index2) {
        return ht(index1).lcm(ht(index2));
    }

    /**
     * @param index position of polynomial.
     *
     * @return highest term of polynomial at given position.
     */
    @Override
    public Term ht(int index) {
        return terms.get(colInd[index][0]);
    }

    /**
     * @param index position of polynomial.
     *
     * @return polynomial at given position.
     */
    @Override
    public Polynom get(int index) {
        Polynom res = new Polynom(ring);
        for (int i = 0; i < rows[index].length; i++) {
            res = res.add(new Polynom(
                    terms.get(colInd[index][i]).powers(),
                    new Element[] {rows[index][i]}), ring);
        }
        return res;
    }

    /**
     * Appends polynomials at given positions from one PolyListDefault to the
     * end of another.
     *
     * @param source from where to copy.
     * @param dest where to copy.
     * @param sourceIndices positions of polynomials in source.
     */
    public static void appendPols(PolyListDefault source, PolyListDefault dest,
            int... sourceIndices) {
        if (sourceIndices.length == 0) {
            return;
        }
        int toAddCnt = sourceIndices.length;
        int destLastIdx = dest.size();
        dest.reserveRowsAtTheEnd(toAddCnt);
        for (int i = 0; i < toAddCnt; i++) {
            int currPolPos = sourceIndices[i];
            // Copy coeffs.
            dest.set(destLastIdx + i, source.row(currPolPos));
            // Copy terms without applying map (skip all rows).
            int srcRowLen = source.rowSize(currPolPos);
            for (int j = 0; j < srcRowLen; j++) {
                dest.addTerm(source.term(currPolPos, j), toAddCnt);
            }
        }
        // Fill new column indexes
        for (int i = 0; i < toAddCnt; i++) {
            int currPolPos = sourceIndices[i];
            int[] newDestRowOfColInd = new int[source.rowSize(currPolPos)];
            for (int j = 0; j < newDestRowOfColInd.length; j++) {
                newDestRowOfColInd[j] = dest.terms().indexOf(
                        source.term(currPolPos, j));
            }
            dest.setRowOfColInd(destLastIdx + i, newDestRowOfColInd);
        }
        for (int i = 0; i < destLastIdx; i++) {
            for (int j = 0; j < dest.rowSize(i); j++) {
                dest.setColIndex(i, j,
                        dest.terms().indexOf(dest.term(i, j)));
            }
        }
    }

    @Override
    public Term term(int row, int column) {
        return terms.get(colInd[row][column]);
    }

    @Override
    public Element coeff(int row, int column) {
        return rows[row][column];
    }

    /**
     * Adds given term to this PolyListDefault applying correspongind
     * permutations map.
     *
     * @param term
     */
    @Override
    public void addTerm(Term term) {
        addTerm(term, 0);
    }

    /**
     * Adds given term to this PolyListDefault applying corresponding
     * permutations map.
     *
     * @param term term to add.
     * @param rowsToSkip number of rows to skip.
     */
    public void addTerm(Term term, int rowsToSkip) {
        applyMap(colInd, terms.add(term), rowsToSkip);
    }

    /**
     * @param index row number.
     *
     * @return length of row with given number.
     */
    @Override
    public int rowSize(int index) {
        return rows[index].length;
    }

    /**
     * @param index row number.
     *
     * @return copy of row of coefficients with given number.
     */
    @Override
    public Element[] row(int index) {
        return Array.copyOf(rows[index], rows[index].length);
    }

    /**
     * @param index row number.
     *
     * @return copy of row of columnt indexes with given number.
     */
    @Override
    public int[] rowOfColInd(int index) {
        return Array.copyOf(colInd[index], colInd[index].length);
    }

    /**
     * Sets coefficients row at given index.
     *
     * @param index
     * @param row
     */
    @Override
    public void set(int index, Element[] row) {
        rows[index] = row;
    }

    @Override
    public void setColIndex(int index, int col, int newIndex) {
        colInd[index][col] = newIndex;
    }

    @Override
    public void setRowOfColInd(int index, int[] newIndices) {
        colInd[index] = newIndices;
    }

    @Override
    public String toString() {
        return debug();
    }

    /**
     * Adds left and right parts of S-polynomial of polynomials at given
     * positions from {@code src} to {@code dest}.
     *
     * @param src PolyListDefault from where to get polynomials.
     * @param dest PolyListDefault where pair is added.
     * @param index1 position of first polynomial.
     * @param index2 position of second polynomial.
     */
    public static void computeAndAddLRPair(PolyListDefault src, PolyListDefault dest,
            int index1, int index2) {
        Term lcm = src.lcmHT(index1, index2);
        appendPols(src, dest, index1, index2);
        dest.multiply(dest.size() - 2, lcm.div(dest.ht(dest.size() - 2)));
        dest.multiply(dest.size() - 1, lcm.div(dest.ht(dest.size() - 1)));
    }

    /**
     * Cancels polynomial at given row number.
     *
     * @param index index of cancelling polynomial.
     */
    @Override
    public void cancelRow(int index) {
        Element.arrayCancel(rows[index], ring);
    }

    /* TODO: implement this. */
    @Override
    public void copyToAndRemovePols(PolyList dest, int... indices) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void appendFrom(PolyList source, int... sourceIndices) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void computeLrPairAndAddTo(int index1, int index2, PolyList dest) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
