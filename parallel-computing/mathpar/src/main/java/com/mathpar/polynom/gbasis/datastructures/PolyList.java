package com.mathpar.polynom.gbasis.datastructures;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import com.mathpar.matrix.MatrixS;
import com.mathpar.number.Element;
import com.mathpar.polynom.Polynom;
import com.mathpar.polynom.Term;

public interface PolyList {
    int size();

    void add(Polynom polynom);

    Set<Term> ht();

    void copyToAndRemovePols(PolyList dest, int... indices);

    void remove(int... indices);

    void toEchelonForm(boolean useGbEchelonForm);

    void addAll(Collection<Polynom> pols);

    TermsContainer terms();

    MatrixS toMatrixS();

    List<Polynom> toList();

    void multiply(int index, Term term);

    Term lcmHT(int index1, int index2);

    Term ht(int index);

    Polynom get(int index);

    void addTerm(Term term);

    void appendFrom(PolyList source, int... sourceIndices);

    Term term(int index, int column);

    Element coeff(int index, int column);

    int rowSize(int index);

    Element[] row(int index);

    int[] rowOfColInd(int index);

    void set(int index, Element[] row);

    void setColIndex(int index, int col, int newIndex);

    void setRowOfColInd(int index, int[] newIndices);

    void computeLrPairAndAddTo(int index1, int index2, PolyList dest);

    void cancelRow(int index);
}
