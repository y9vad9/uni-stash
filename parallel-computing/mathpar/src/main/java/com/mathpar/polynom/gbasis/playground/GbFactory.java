package com.mathpar.polynom.gbasis.playground;

import java.util.List;
import com.mathpar.number.Ring;
import com.mathpar.polynom.Polynom;
import com.mathpar.polynom.gbasis.algorithms.Buchberger;
import com.mathpar.polynom.gbasis.algorithms.Faugere;
import com.mathpar.polynom.gbasis.algorithms.FaugereNew;
import com.mathpar.polynom.gbasis.Gbasis;

public final class GbFactory {
    public Gbasis newBuchberger(Ring ring, List<Polynom> ideal) {
        return new Buchberger(ring, ideal);
    }

    public Gbasis newF4(Ring ring, List<Polynom> ideal) {
        return new Faugere(ring, ideal);
    }

    public Gbasis newF4new(Ring ring, List<Polynom> ideal) {
        return new FaugereNew(ring, ideal);
    }
}
