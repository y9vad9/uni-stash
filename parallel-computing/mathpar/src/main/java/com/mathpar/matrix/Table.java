package com.mathpar.matrix;

import com.mathpar.func.CanonicForms;
import com.mathpar.func.F;
import com.mathpar.func.Fname;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import com.mathpar.number.Array;
import com.mathpar.number.Element;
import com.mathpar.number.NumberR64;
import com.mathpar.number.Ring;
import com.mathpar.number.VectorS;
import com.mathpar.web.exceptions.MathparException;

/**
 *
 * @author gennadi
 */
public class Table extends Element {
    /**
     * Input file encoding.
     */
    private static final String IN_ENCODING = "windows-1251";
    /**
     * Input\output column delimiter.
     */
    private static final String COLUMNS_DELIM = "\t";
    /**
     * Output newline.
     */
    private static final String OUT_NEWLINE = "\n";
    /**
     * где в первой строке -- x, во второй и прочих - функции.
     */
    public MatrixD M;
    /**
     * тут хранятся подписи осей и имен функций "t(c)" "U(В)" Ровно по одному
     * String на каждую строчку в таблице. (разрешенное количество --
     * произвольное -- недостающие считаются пустыми, а перебор игнорируется)
     */
    public String[] axesSign = null;
    /**
     * (0 или 2 или 4 NumberR64) тут хранятся границы области по x: x1 x2, y: y1
     * y2. (разрешенное число = 0, 2,4)
     */
    public NumberR64[] region = null;
    /**
     * в первой строке точки, поставленные на экране пользователем по оси x, во
     * второй и прочих - значение табличной функции в этой точке (рассчитанное).
     */
    public MatrixD P = null;
    public int[] Points = null;

    public Table() {
    }

    public Table(MatrixS S) {
        M = new MatrixD(S);
    }

    public Table(MatrixS S, String[] axesSign) {
        this.M = new MatrixD(S);
        this.axesSign = axesSign;
    }
    
    /**
     *  Tabulation the function f.
     * @param f -- function of one variable for tabulation
     * @param x0 -- first value of variable
     * @param x1-- last value of variable
     * @param n  -- number of steps
     * @param ring -- Ring
     */
    public Table(Element[] f, Element x0,  Element x1, Element n, Ring ring) { 
       M=new MatrixD(  f,  x0,   x1,   n,  ring);
       int m=f.length;
       axesSign=new String[m+1]; axesSign[0]=ring.varNames[0];
     //  axesSign[1]="y";
        for (int i = 0; i < m; i++)   axesSign[i+1]= f[i].toString(ring);
       // КРИВО = со сдвигом на 1 берутся названия функций, поэтому 
       // пока запись названий функций закомментирована
       // И ЗАЧЕМ ТЫ УМЕНЬШИЛ ИХ ЧИСЛО с m ло 2 ?? Так не Хорошо. Программа падает
    }

    public Table(MatrixS S, String[] axesSign, NumberR64[] region) {
        this.M = new MatrixD(S);
        this.axesSign = axesSign;
        this.region = region;
    }

    public Table(MatrixS S, String[] axesSign, NumberR64[] region, MatrixD P) {
        this.M = new MatrixD(S);
        this.axesSign = axesSign;
        this.region = region;
        this.P = P;
    }

    public Table(MatrixD M) {
        this.M = M;
    }

    public Table(MatrixD M, String[] axesSign) {
        this.M = M;
        this.axesSign = axesSign;
    }

    public Table(MatrixD M, String[] axesSign, NumberR64[] region) {
        this.M = M;
        this.axesSign = axesSign;
        this.region = region;
    }

    public Table(MatrixD M, String[] axesSign, NumberR64[] region, MatrixD P) {
        this.M = M;
        this.axesSign = axesSign;
        this.region = region;
        this.P = P;
    }

    public Table(Table T) {
        this.M = T.M;
        this.axesSign = T.axesSign;
        this.region = T.region;
        this.P = T.P;
        this.Points = T.Points;
    }

    public Table(F T, List<Element> expr) {
        CanonicForms Cfs = new CanonicForms(Ring.ringR64xyzt, true);
        F fInput = new F(T.name, T.X);
        fInput = (F) Cfs.UnconvertToElement(Cfs.substituteValueToFnameAndCorrectPolynoms(fInput, expr));
        if ((fInput.X.length >= 1) && (fInput.X[0] != null)) {
            if (fInput.X[0] instanceof MatrixD) {
                this.M = ((MatrixD) fInput.X[0]);
            } else if (fInput.X[0] instanceof Table) {
                this.M = new MatrixD(((Table) fInput.X[0]).M.M);
            }
        } else {
            this.M = null;
        }
        if ((fInput.X.length >= 2) && (fInput.X[1] != null)) {
            this.axesSign = new String[((VectorS) fInput.X[1]).V.length];
            for (int i = 0; i < axesSign.length; i++) {
                this.axesSign[i] = ((VectorS) fInput.X[1]).V[i].toString();
            }
        } else {
            this.axesSign = null;
        }
        if ((fInput.X.length >= 3) && (fInput.X[2] != null)) {
            this.region = new NumberR64[((VectorS) fInput.X[2]).V.length];
            for (int i = 0; i < region.length; i++) {
                this.region[i] = new NumberR64(((VectorS) fInput.X[2]).V[i].doubleValue());
            }
        } else {
            this.region = null;
        }
        this.P = null;
        this.Points = null;
    }

    public Table(Table T, String[] axesSign) {
        this.M = T.M;
        this.axesSign = axesSign;
    }

    public Table(Table T, String[] axesSign, NumberR64[] region) {
        this.M = T.M;
        this.axesSign = axesSign;
        this.region = region;
    }

    public Table(Table T, String[] axesSign, NumberR64[] region, MatrixD P) {
        this.M = T.M;
        this.axesSign = axesSign;
        this.region = region;
        this.P = P;
    }

    @Override
    public String toString() {
        return toString(Ring.ringR64xyzt);
    }

    @Override
    public String toString(Ring ring) {
        MatrixD TT = new MatrixD(new Element[][] {});
        Element[][] MM = new Element[][] {};
        if (this.M != null) {
            TT = this.M.transpose(ring);
            MM = TT.M;
        }
        String[] str;
        if ((axesSign == null) || axesSign.length == 0) {
            return TT.toString(ring);
        } else {
            str = axesSign;
        }
        int len = MM[0].length;
        int rows = MM.length;
        Element[] tit = new Element[len];
        for (int i = 0; i < len; i++) {
            tit[i] = (i < str.length) ? new Fname(str[i]) : new Fname(" ");
        }
        Element[][] Mnew = new Element[rows + 1][];
        Mnew[0] = tit;
        System.arraycopy(MM, 0, Mnew, 1, MM.length);
        return new MatrixD(Mnew).toString(ring);
    }

    /**
     * Установка значений min и max параметров по осям графика
     */
    public void setWindowsXY() {
        Element[][] el = this.M.M;
        NumberR64 minX = NumberR64.ZERO;
        NumberR64 maxX = NumberR64.ZERO;
        NumberR64 minY = NumberR64.ZERO;
        NumberR64 maxY = NumberR64.ZERO;
        if (el[0].length != 0) {
            minX = new NumberR64(el[0][0].doubleValue());
            maxX = new NumberR64(el[0][0].doubleValue());
        }
        if (el[1].length != 0) {
            minY = new NumberR64(el[1][0].doubleValue());
            maxY = new NumberR64(el[1][0].doubleValue());
        }
        for (int i = 0; i < el[0].length; i++) {
            NumberR64 x = new NumberR64(el[0][i].doubleValue());
            if (minX.compareTo(x) == 1) {
                minX = x;
            }
            if (maxX.compareTo(x) == -1) {
                maxX = x;
            }
        }
        for (int i = 1; i < el.length; i++) {
            for (int j = 0; j < el[i].length; j++) {
                NumberR64 y = new NumberR64(el[i][j].doubleValue());
                if (minY.compareTo(y) == 1) {
                    minY = y;
                }
                if (maxY.compareTo(y) == -1) {
                    maxY = y;
                }
            }
        }
        this.region = new NumberR64[] {minX, maxX, minY, maxY};
    }

    /**
     * Установка значений min и max параметров по осям
     */
    public void setWindowsY() {
        Element[][] el = this.M.M;
        NumberR64 minX = this.region[0];
        NumberR64 maxX = this.region[1];
        NumberR64 minY = NumberR64.ZERO;
        NumberR64 maxY = NumberR64.ZERO;
        if (this.region != null) {
            minY = this.region[2];
            maxY = this.region[3];
        }
        for (int i = 1; i < el.length; i++) {
            for (int j = 0; j < el[i].length; j++) {
                NumberR64 y = new NumberR64(el[i][j].doubleValue());
                if (minY.compareTo(y) == 1) {
                    minY = y;
                }
                if (maxY.compareTo(y) == -1) {
                    maxY = y;
                }
            }
        }
        this.region = new NumberR64[] {minX, maxX, minY, maxY};
    }

    public void replot(F replot, List<Element> expr) {
        CanonicForms Cfs = new CanonicForms(Ring.ringR64xyzt, true);
        F fInput = new F(replot.name, replot.X);
        fInput = (F) Cfs.UnconvertToElement(Cfs.substituteValueToFnameAndCorrectPolynoms(fInput,expr));
        Element[] p = ((VectorS) fInput.X[0]).V;
        int[] pp = new int[p.length];
        for (int i = 0; i < pp.length; i++) {
            pp[i] = p[i].intValue();
        }
        Element[] d = ((VectorS) fInput.X[1]).V;
        int[] dd = new int[d.length];
        for (int i = 0; i < dd.length; i++) {
            dd[i] = d[i].intValue();
        }
        Element[] c = ((VectorS) fInput.X[2]).V;
        int[] cc = new int[c.length];
        for (int i = 0; i < cc.length; i++) {
            cc[i] = c[i].intValue();
        }
        cc = Array.sortUp(cc);
        int[] oldpp = (int[]) this.Points;
        this.Points = Array.jointPQdelD(oldpp, pp, dd, 0);
        this.setWindowsXY();
        double xcor = 0;
        double zoom1;
        int xCorrect = 0;
        zoom1 = Math.abs(800 / (this.region[1].doubleValue() - this.region[0].doubleValue()));
        if ((this.region[0].doubleValue() > 0) && (this.region[1].doubleValue() > 0)) {
            xcor = 436;
            xCorrect = (int) (this.region[0].doubleValue() * zoom1);
        }
        if (this.region[0].doubleValue() == 0) {
            xcor = 436;
            xCorrect = 0;//(int) (this.region[0].doubleValue() * zoom1);
        }
        if (this.region[1].doubleValue() == 0) {
            xcor = 1236;
            xCorrect = 0;//(int) (this.region[0].doubleValue() * zoom1);
        }
        if ((this.region[0].doubleValue() < 0) && (this.region[1].doubleValue() > 0)) {
            xcor = (int) (Math.abs(this.region[0].doubleValue())
                    / (this.region[1].doubleValue() - this.region[0].doubleValue()) * 800 + 336);//436
            xCorrect = 0;//(int) (this.region[1].doubleValue() * zoom1);
        }
        if ((this.region[0].doubleValue() < 0) && (this.region[1].doubleValue() < 0)) {
            xcor = 1236;
            xCorrect = (int) (this.region[1].doubleValue() * zoom1);
        }
        if (cc.length != 0) {
            //левая координата по X
            double x1 = ((cc[0] - xcor + xCorrect) / zoom1);
            //правая координата по X
            double x2 = ((cc[cc.length - 1] - xcor + xCorrect) / zoom1);
            this.region[0] = new NumberR64(x1);
            this.region[1] = new NumberR64(x2);
            Element[] v = M.M[0];
            int start = 0;
            int end = 0;
            for (int i = 0; i < v.length; i++) {
                if (v[i].doubleValue() <= x2) {
                    end = i;
                }
            }
            for (int i = 0; i < v.length; i++) {
                if (v[i].doubleValue() >= x1) {
                    start = i;
                    break;
                }
            }
            //Расчет новой длины набора x и y
            int len = end - start;
            Element[][] funcs = new Element[M.M.length][];
            for (int j = 0; j < M.M.length; j++) {
                Element[] el = new Element[len + 1];
                System.arraycopy(M.M[j], start, el, 0, el.length);
                funcs[j] = el;
            }
            this.M = new MatrixD(funcs);
            this.setWindowsY();
        }
        Element[][] ppp = new Element[1][];
        ppp[0] = new Element[this.Points.length];
        //Объект для хранения отфильтрованных значений
        ArrayList<Element> pointLine = new ArrayList<Element>();
        for (int i = 0; i < ppp[0].length; i++) {
            //Фильтровка точек линий пересечения по точкам обрезки
            if (cc.length != 0) {
                if ((this.Points[i] > cc[0]) && (this.Points[i] < cc[cc.length - 1])) {
                    pointLine.add(new NumberR64(this.Points[i]));
                }
            } else {
                ppp[0][i] = new NumberR64(this.Points[i]);
            }
        }
        if (cc.length != 0) {
            ppp[0] = new Element[pointLine.size()];
            pointLine.toArray(ppp[0]);
        }
        this.P = new MatrixD(ppp);
    }

    /**
     *
     * @param reader input Reader
     *
     * @return Table parsed from given {@literal Reader}.
     *
     * @throws IOException in case of exceptions during reading
     */
    public static Table fromReader(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String currLine;
        // Read header.
        currLine = br.readLine();
        String[] header = currLine.split(COLUMNS_DELIM);
        int columnCnt = header.length;
        // Replace comma delimited units in header with parentheses surrounded ones.
        for (int i = 0; i < columnCnt; i++) {
            String currHeader = header[i];
            int commaIdx = currHeader.indexOf(',');
            if (commaIdx >= 0) {
                String[] headerParts = currHeader.split(",");
                header[i] = String.format("%s (%s)", headerParts[0].trim(),
                        headerParts[1].trim());
            }
        }
        // Read data rows.
        int rowsCnt = 0;
        List<List<Element>> tableTmp = new ArrayList<List<Element>>(columnCnt);
        for (int i = 0; i < columnCnt; i++) {
            tableTmp.add(new ArrayList<Element>());
        }
        while ((currLine = br.readLine()) != null) {
            String[] rowsStr = currLine.split(COLUMNS_DELIM);
            for (int j = 0, len = rowsStr.length; j < len; j++) {
                try {
                    double value = Double.parseDouble(rowsStr[j]);
                    tableTmp.get(j).add(new NumberR64(value));
                } catch (NumberFormatException nfe) {
                    throw nfe;
                }
            }
            rowsCnt++;
        }
        // Convert Lists to MatrixD.
        Element[][] tableMatrix = new Element[columnCnt][];
        for (int i = 0; i < columnCnt; i++) {
            tableMatrix[i] = tableTmp.get(i).toArray(new Element[rowsCnt]);
        }
        return new Table(new MatrixD(tableMatrix), header);
    }

    public static Table fromFile(File file)
            throws UnsupportedEncodingException, FileNotFoundException, IOException {
        return fromReader(new InputStreamReader(new FileInputStream(file), IN_ENCODING));
    }

    public static Table fromFile(String path)
            throws UnsupportedEncodingException, FileNotFoundException, IOException {
        return fromFile(new File(path));
    }

    public static Table fromInputStream(InputStream inputStream)
            throws UnsupportedEncodingException, IOException {
        return fromReader(new InputStreamReader(inputStream, IN_ENCODING));
    }

    public static Table fromString(String string) throws IOException {
        try {
            return fromReader(new StringReader(string));
        } catch (IOException t) {
            throw new IllegalArgumentException("Can't parse table from string:\n" + string, t);
        }
    }

    /**
     * Reads table for this page from file.
     *
     * @param uploadDir directory with user's uploaded files.
     * @param fileName Fname with file name.
     *
     * @return table from given filename.
     *
     * @throws java.io.UnsupportedEncodingException
     * @throws java.io.FileNotFoundException
     */
    public static Table fromFname(File uploadDir, Element fileName) {
        String name = ((Fname) fileName).name;
        try {
            File file = new File(uploadDir, name);
            return fromFile(file);
        } catch (FileNotFoundException fnf) {
            throw new MathparException("File \"" + name
                    + "\" not found. Check if the file was uploaded and you've typed correct name.", fnf);
        } catch (IOException ioe) {
            throw new MathparException("Can't parse table in file " + name, ioe);
        }
    }

    public int tableSize() {
        return M.M.length;
    }

    public Element getFunction(int index) {
        return new VectorS(M.M[index]);
    }

    public void setFunction(Element row, int index) {
        if (row instanceof VectorS) {
            M.M[index] = ((VectorS) row).V;
            return;
        }
        M.M[index][0] = row;
    }
}