package com.mathpar.parallel.dap.SVD.UTV.seqModel.transport;

public interface Transport {

    Object receiveObject(int source, int destination, Tag tag);
    Object[] receiveObjectArray(int source, int destination, Tag tag);
    Object[][] receiveObject2dArray(int source, int destination, Tag tag);

    void sendObject(Object o, int source, int destination, Tag tag);
    void sendObjectArray(Object[] array, int source, int destination, Tag tag);
    void sendObject2dArray(Object[][] array, int source, int destination, Tag tag);

    Object scatter(Object[] data, int source, int currentRank);

    enum Tag{
        ANY, LEFT_MATRIX, RIGHT_MATRIX, COLUMN, ROW, LEFT_2x2_MATRIX, RIGHT_2x2_MATRIX, ELEMENT,
        COLUMN_WITH_EXTRA_ELEMENT, ROW_WITH_EXTRA_ELEMENT, MATRIX_ACCUMULATION,
        DIAGONAL_ACCUMULATION, ACCUMULATION_LINE, ACCUMULATION_RESULT, INIT_DATA, SCATTER
    }
}
