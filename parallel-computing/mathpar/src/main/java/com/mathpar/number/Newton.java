package com.mathpar.number;

import com.mathpar.matrix.MatrixS;
import com.mathpar.polynom.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.mathpar.matrix.MatrixS;
import java.io.File;
import java.util.AbstractMap;
/**
 * Класс, элементами которого являются расширяющиеся массивы чисел используемые
 * при восстановлении по остаткам различных математических объектов методом
 * Ньютона (по КТО) <br> Содержит методы позволяющие восстанавливать по остаткам
 * числа типа <tt>BigInteger</tt>, массивы чисел, полиномы , числовые и
 * полиномиальные матрицы.
 *
 * <p>
 * Title: ParCA</p>
 *
 * <p>
 * Description: ParCA - parallel computer algebra system</p>
 *
 * <p>
 * Copyright: Copyright (c) ParCA Tambov, 2006, 2007</p>
 *
 * <p>
 * Company: ParCA Tambov</p>
 *
 * @author Kazakov
 * @version 0.5
 */
public class Newton {

    //Переменные для кэша в кольце Z
    //кэш представляющий из себя
    /**
     * расширяющийся массив простых чисел
     */
 //   public static int[] prims;
    /**
     * массив произведений простых чисел: <tt>P1, P1*P2, P1*P2*P3, ...</tt>
     */
    public static NumberZ[] mulPrm;
    
    public static String FILE_WITH_PRIMES=System.getProperty("java.io.tmpdir", "/tmp") + File.separator + "mathpar";
   
    /**
     * процедура создает массив numbersForNewton, в котором хранятся числа
     * следующего вида:
     * в numbersForNewton[0] хранится произведение всех модулей из списка mod;
     * в numbersForNewton[i] хранятся произведения модулей и обратных к модулям
     * чисел по произведению модулей 
     * @param mod - список простых модулей
     * @return массив типа NumberZ
     */
    public static NumberZ[] arrayOfNumbersForNewton(int[] mod){
        NumberZ mulMod = NumberZ.valueOf(mod[0]);
        NumberZ[] numbersForNewton = new NumberZ[mod.length];
       for (int i = 1; i < mod.length; i++) {
            long currentMod = mod[i];
            NumberZ mulInvers = NumberZ.ONE;
            Ring ring = new Ring("Zp32");
            ring.MOD32 = currentMod;
            for (int j = 0; j < i; j++) {
                NumberZp32 nij = (new NumberZp32(mod[j])).inverse(ring);
                mulInvers = mulInvers.multiply((NumberZ) nij.toNumber(Ring.Z, new Ring("Z")));
            }
            numbersForNewton[i] = mulInvers.multiply(mulMod).mod(mulMod.multiply(NumberZ.valueOf(currentMod)));
            mulMod = mulMod.multiply(NumberZ.valueOf(currentMod));
        }
       numbersForNewton[0] = mulMod;
       return numbersForNewton;
    }
    
      /**
     * Продолжает накапливать массив произведения модулей и обратных к модулям чисел,
     * при этом в numbersForNewton[0] - хранится произведение всех модулей
     * @param mod - список всех модулей, как прошлых, так и новых
     * @param numbModOld - старый массив произведений
     * @param pos - позиция нового модуля в массиве
     * @return массив типа NumberZ
     */
    public static NumberZ[] arrayOfNumbersForNewtonProceed(int[] mod, NumberZ[] numbModOld, int pos){
        NumberZ mulMod = numbModOld[0];
        System.out.println("modlen ="+mod.length + " pos ="+pos);
        NumberZ[] numbersForNewton = new NumberZ[mod.length-pos+1];
        int k = 1;
        for (int i = pos; i < mod.length; i++) {
            
           long currentMod = mod[i];
            NumberZ mulInvers = NumberZ.ONE;
            Ring ring = new Ring("Zp32");
            ring.MOD32 = currentMod;
            for (int j = 0; j < i; j++) {
                NumberZp32 nij = (new NumberZp32(mod[j])).inverse(ring);
                mulInvers = mulInvers.multiply((NumberZ) nij.toNumber(Ring.Z, new Ring("Z")));
            }
            numbersForNewton[k] = mulInvers.multiply(mulMod).mod(mulMod.multiply(NumberZ.valueOf(currentMod)));
            mulMod = mulMod.multiply(NumberZ.valueOf(currentMod));
            k++;
        }   
       numbersForNewton[0] = mulMod;
       return numbersForNewton;
    }
   
    public static  int[] primesForTask(NumberZ line, int numBlock, Ring ring) throws IOException{
        NFunctionZ32.doFileOfIntPrimes();
        NumberZ temp = NumberZ.ONE;
        int k = 0;
        int l = 0;
        int[] block = NFunctionZ32.readBlockOfPrimesFromBack(numBlock, ring);
        for (int i = block.length-1; line.compareTo(temp,2,ring); i--) {
           temp = temp.multiply(new NumberZ(block[i]));
           l = i;
           k++;
        }
           int[] primes = new int[k];
           System.arraycopy(block, l, primes, 0, k);
           return primes;
    }
     /*
    массив 32-битных модулей, который "покрывает" число, 
    содержащее numberOfBits бит
    numBlock=1 => модули берутся, начиная с самого большого
    */
    public static  int[] primesForTask(long numberOfBits, int numBlock, Ring ring) throws IOException{
        NFunctionZ32.doFileOfIntPrimes();
        NumberZ temp = NumberZ.ONE;
        int k = 0;
        int l = 0;
        int[] block = NFunctionZ32.readBlockOfPrimesFromBack(numBlock, ring);
        
        /*
        for (int i = block.length-1; (line.subtract(temp)).compareTo(ring.numberZERO) == 1; i--) {
           temp = temp.multiply(new NumberZ(block[i]));
           l = i;
           k++;
        }*/
        int i=0, nbBlock=31, msk=1<<30, b;
         long dif=numberOfBits;
         while(dif>0){
             b=block[i]; 
             while((b&msk)>>nbBlock==0){
                 nbBlock--;
             }
             dif-=nbBlock;
             i++;
         }
        
           int[] primes = new int[i];
           System.arraycopy(block, l, primes, 0, i);
           //System.out.println("primes = "+Array.toString(primes));
           return primes;
    }
    
     public static  int[] primesForAdjoint(int adamarBitCount, int numBlock, Ring ring) throws IOException{
        NFunctionZ32.doFileOfIntPrimes();
        int line = 1;
        NumberZ p = new NumberZ(1);
        int k = 0;
        int l = 0;
        int[] block = NFunctionZ32.readBlockOfPrimesFromBack(numBlock, ring);
        for (int i = block.length-1; line < adamarBitCount; i--) {
           p = p.multiply(new NumberZ(block[i]));
           line = p.bitCount();
           l = i;
           k++; 
        }
           int [] primes = new int[k];
           System.arraycopy(block,l , primes, 0, k);
        
        return primes;
    }
    
    public static NumberZ primesForMultMatrix(MatrixS A, MatrixS B, Ring ring){
        NumberZ line = (NumberZ)(A.max(ring).multiply(B.max(ring), ring)).multiply(new NumberZ(A.size), ring);
        return line;
    }
    
    
    public static NumberZ[] arrayOfNumbersForNewton(NumberZ[] mod){
        NumberZ mulMod =mod[0];
        NumberZ[] numbersForNewton = new NumberZ[mod.length];
       for (int i = 1; i < mod.length; i++) {
            NumberZ currentMod = mod[i];
            NumberZ mulInvers = NumberZ.ONE;
            Ring ring = Ring.ringZpX;
            ring.setMOD(currentMod);
            for (int j = 0; j < i; j++) {
                NumberZp el = (NumberZp) mod[j].toNumber(Ring.Zp, ring);
                Element nij = el.inverse(ring);
                mulInvers = mulInvers.multiply((NumberZ) nij.toNumber(Ring.Z, Ring.ringZxyz));
            }
            numbersForNewton[i] = mulInvers.multiply(mulMod).mod(mulMod.multiply(currentMod));
            mulMod = mulMod.multiply(currentMod);
        }
       numbersForNewton[0] = mulMod;
       return numbersForNewton;
    }
    
    /** Recovery NumberZ[] array using matrix "rem" of their remainders of division by mod.
     *  Matrix rem has size n x m. The  number of column  m equals the length of array mod.
     *  The number of rows n  equals the length of  the resulting array of NumberZ integers.
     *  The row number i of matrix rem consists of the all remainders of i-th element.
     * 
     * @param mod -- modules (prime modules)
     * @param rem -- array of remainders
     * @return --- array of NumberZ integers
     */
    public static NumberZ[] recoveryNewtonArray(int[] mod, int[][] rem){
        NumberZ[] arr  = arrayOfNumbersForNewton(mod);
        NumberZ[] res = new NumberZ[rem.length];
        for(int i = 0; i< rem.length; i++){
            res[i] = recoveryNewtonWithoutArr(mod, rem[i], arr);
        }
        return res;
    }
    /**
     * восстановление числа типа NumberZ по его остаткам типа int методом
     * Ньютона
     * @param mod - список простых модулей
     * @param rem - остатки исходного числа при деление на эти модули
     * @param ring - кольцо Z
     * @return восстановленное число
     */
    public static NumberZ recoveryNewton(int[] mod, int[] rem ) {
        NumberZ[] numbNewton = arrayOfNumbersForNewton(mod);
        return recoveryNewtonWithoutArr( mod,  rem ,  numbNewton ); 
    }
    public static NumberZ recoveryNewtonWithoutArr(int[] mod, int[] rem ,NumberZ[] numbNewton ) {
        NumberZ c = NumberZ.valueOf(rem[0]);
        if (rem.length == 1)    return c;
        long cMod;
        for (int i = 1; i < rem.length; i++) {
            long currentMod = mod[i];
            cMod = c.mod(NumberZ.valueOf(currentMod)).longValue();
            c = c.add(NumberZ.valueOf((rem[i] - cMod)).multiply(numbNewton[i]));
        }
        c = c.Mod(numbNewton[0],Ring.ringZxyz);
        return c;
   }
    public static NumberZ recoveryNewton(NumberZ[] mod, NumberZ[] rem ) {
        NumberZ[] numbNewton = arrayOfNumbersForNewton(mod);
        return recoveryNewtonWithoutArr( mod,  rem ,  numbNewton ); 
    }
    public static NumberZ recoveryNewtonWithoutArr(NumberZ[] mod, NumberZ[] rem ,NumberZ[] numbNewton ) {
        NumberZ c = (NumberZ) rem[0].toNewRing(Ring.Z, Ring.ringZxyz);
//        if (rem.length == 1)    return c;
        NumberZ cMod;
        for (int i = 1; i < rem.length; i++) {
            cMod = c.mod(mod[i]);
            c = c.add(rem[i].subtract(cMod).multiply(numbNewton[i]));
        }
        c = c.Mod(numbNewton[0],Ring.ringZxyz);
        return c;
   }
   
  
    /**
     * Восстановление полинома Z[x] типа PolynomZ по его остаткам типа
     * PolynomZ64 методом Ньютона
     *
     * @param missingIndx int[] массив индексов пропущенных простых чисел
     * массива prims, используемых при восстановлении коэффициентов полинома
     * @param remArr PolynomZ64[] остатки исходного полинома при делении его в
     * кольце Lp[x] над простыми числами p
     *
     * @return PolynomZ восстановленный полином <br> <b> Пример использования
     * </b> <br>      <CODE> import number.Newton;                          <br>
     * class Example{ <br>
     * <ul> public static void main(String[] args){
     * <ul> Newton.initCache(); <br>
     * int[] missingIndx = {5, 7, 9 ,11}; <br>
     * PolynomZ64[] remArr = {pol_1, ..., pol_10}; <br>
     * PolynomZ pol = Newton.recoveryNewtonPolZ(missingIndx, remArr); </ul>
     * } </ul>
     * } </CODE> <br> В этом примере восстанавливается полином от одной
     * переменной по 10 остаткам по простым модулям массива <tt>prims</tt>
     * взятых в прормежутке <tt>[0; 13]</tt> с пропуском четырех из них, при
     * этом используется переход от массива полиномов к матрице их коэффициентов
     * с последующим использованием метода <tt>recoveryNewtonArr()</tt>
     *
     */
    public static Polynom recoveryNewtonPolynomZ(int[] missingIndx, Polynom[] remArr) {
        //если остаток один, то он и возвращается
        if (missingIndx != null && missingIndx.length == 1) {
            return remArr[0].toPolynom(Ring.Z, Ring.ringR64xyzt);
        }
        int maxPower, minPower;
        boolean zero = false;
        //определение максимальной и минимальной степени в полиномах массива remArr
        //проверка первого в массиве полинома на равенство числовому моному
        if (remArr[0].powers.length == 0) {
            maxPower = 0;
            minPower = 0;
        } else {
            maxPower = remArr[0].powers[0];
            minPower = remArr[0].powers[remArr[0].powers.length - 1];
        }
        if (minPower == 0) {
            zero = true;
        }
        for (int i = 1; i < remArr.length; i++) {
            if (remArr[i].powers.length != 0 && maxPower < remArr[i].powers[0]) {
                maxPower = remArr[i].powers[0];
            }
            if (!zero) {
                if (remArr[i].powers.length != 0) {
                    if (minPower > remArr[i].powers[remArr[i].powers.length - 1]) {
                        minPower = remArr[i].powers[remArr[i].powers.length - 1];
                    }
                } else {
                    minPower = 0;
                    zero = true;
                }
            }
        }
        //построение матрицы коэффициентов
        int[][] matrCoeff = new int[maxPower - minPower + 1][remArr.length];
        for (int i = 0; i < remArr.length; i++) {
            for (int j = 0; j < remArr[i].coeffs.length; j++) {
                if (remArr[i].powers.length != 0) {
                    matrCoeff[remArr[i].powers[j]
                            - minPower][i] = remArr[i].coeffs[j].intValue();
                } else {
                    matrCoeff[0][i] = (remArr[i].coeffs[0]).intValue();
                }
            }
        }
        /* Получаем массив восстановленных коэффициентов, в котором i + minPower = степень переменной
         при этом коэффициенте, где i - номер коэффициента в массиве newCoeff.  */
         int[] primes= new int[remArr.length];
        System.arraycopy(NFunctionZ32.primes, 0, primes, 0, remArr.length);
        NumberZ[] recoveryCoeff = recoveryNewtonArray(primes, matrCoeff);
        //преобразование полученного массива
        int[] powers = new int[recoveryCoeff.length - 1];
        NumberZ[] coeffs = new NumberZ[recoveryCoeff.length - 1];
        int n = 0;
        for (int i = recoveryCoeff.length - 2; i > -1; i--) {
            if (!recoveryCoeff[i].equals(NumberZ.ZERO)) {
                powers[n] = i + minPower;
                coeffs[n] = recoveryCoeff[i];
                n++;
            }
        }
        int[] newPowers = new int[n];
        NumberZ[] newCoeffs = new NumberZ[n];
        System.arraycopy(powers, 0, newPowers, 0, n);
        System.arraycopy(coeffs, 0, newCoeffs, 0, n);
        Polynom res = new Polynom(newPowers, newCoeffs);
        return res;
    }    
   
    /**
     * Восстановление полинома (а именно его числовых коэффициентов) типа
     * Polynom по его остаткам типа Polynom методом Ньютона
     *
     * @param modArr int[] числовые модули -- простые числа
     * @param remArr Polynom[] остатки исходного полинома
     *
     * @return Polynom восстановленный полином      *
     */
    public static Polynom recoveryNewtonPolynom(int[] mod, Polynom[] rem, Ring ring) {
        if (rem.length == 1) return rem[0].toPolynom(Ring.Z, Ring.ringR64xyzt);
        Polynom vectorOfRem = new Polynom();
        vectorOfRem = vectorOfRem.toVectorPolynoms(ring, rem);
        Element[] coeffs = new Element[vectorOfRem.coeffs.length];
        for (int i = 0; i < vectorOfRem.coeffs.length; i++) {
            Element ww[] = ((VectorS) vectorOfRem.coeffs[i]).V;
            int[] rem_i = new int[ww.length];
            for (int j = 0; j < ww.length; j++) 
                     rem_i[j] = ww[j].intValue();
            coeffs[i] = recoveryNewton(mod, rem_i); 
        }
        return new Polynom(vectorOfRem.powers, coeffs);
    }
    public static MatrixS recoveryNewtonMatrixS(MatrixS[] matrices, NumberZ[] mods){
        NumberZ[] arr = arrayOfNumbersForNewton(mods);
        return recoveryNewtonMatrixSWithoutArr(matrices, mods, arr);
    }
    public static MatrixS recoveryNewtonMatrixSWithoutArr(MatrixS[] matrices, NumberZ[] mods, NumberZ[] numbNewton ){
        if(matrices==null || matrices.length == 0){
            return null;
        }
        int size = 0;
        int maxcolnumb = 0;
        for (MatrixS matrix : matrices) {
            maxcolnumb = Math.max(maxcolnumb, matrix.colNumb);
            size = Math.max(size, matrix.size);
        }
        Element zero = NumberZ.ZERO;
        Element[][] Mm = new Element[size][];
        Element[][] tmp_restore = new NumberZ[maxcolnumb][matrices.length];
        boolean[] has_col = new boolean[maxcolnumb];
        int cols_count = 0;
        int[][] Cc = new int[size][];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < tmp_restore.length; j++) {
                for (int k = 0; k < tmp_restore[j].length; k++) {
                    tmp_restore[j][k] = zero;
                }
                has_col[j] = false;
            }
            cols_count = 0;
            for (int j = 0; j < matrices.length; j++) {
                MatrixS m = matrices[j];
                if(i<m.size){
                    for (int k = 0; k < m.col[i].length; k++) {
                        int col_num = m.col[i][k];
                        if(!has_col[col_num]){
                            cols_count++;
                            has_col[col_num]= true;
                        }
                        tmp_restore[col_num][j] = m.M[i][k];
                    }
                }
            }
            int cols_position = 0;
            Element[] M_recovered = new Element[cols_count];
            int[] cols_recovered = new int[cols_count];
            for (int j = 0; cols_position < cols_count && j < has_col.length; j++) {
                if(has_col[j]){
                    M_recovered[cols_position] = recoveryNewtonWithoutArr(mods, (NumberZ[]) tmp_restore[j], numbNewton);
                    cols_recovered[cols_position] = j;
                    cols_position++;
                }
            }
            Mm[i] = M_recovered;
            Cc[i] = cols_recovered;
        }
        return new MatrixS(size,maxcolnumb,Mm, Cc);
    }
    
    static NumberZ [][]r=null;
    
    private static Ring zRing=new Ring("Z[x,y,z]");
    
    private static NumberZ[] ExtendedEuclid(NumberZ a, NumberZ b){   
        NumberZ r=a.mod(b);
        NumberZ []res=new NumberZ[3];
        if (r.isZero(zRing)){                        
            res[0]=b;
            res[1]=new NumberZ(0);
            res[2]=new NumberZ(1);
            return res;
        }        
        NumberZ []curRes=ExtendedEuclid(b,a.mod(b));
        res[0]=curRes[0];
        res[1]=curRes[2];
        res[2]=curRes[1].subtract((a.divide(b)).multiply(curRes[2]));
        return res;
    }

    private static NumberZ InverseElemByMod(NumberZ a, NumberZ n){
        NumberZ d;
        NumberZ []res;
        res=ExtendedEuclid(a,n);                
        return (res[1].mod(n).add(n)).mod(n);
    }

    public static void initRArray(ArrayList<NumberZ> mods){
        r=new NumberZ[mods.size()][mods.size()];
        for (int i=0; i<mods.size(); i++){
            for (int j=0; j<mods.size(); j++){
                if (i!=j){
                    r[i][j]=InverseElemByMod(mods.get(i), mods.get(j));
                }
            }
        }
    }
    
    public static NumberZ garnerRestore(NumberZ[] rems, ArrayList<NumberZ> mods){
        /*BigInteger result = BigInteger.ZERO,
			mult = BigInteger.ONE;
		int x[] = new int[SZ];
		for (int i=0; i<SZ; ++i) {
			x[i] = a[i];
			for (int j=0; j<i; ++j) {
				long cur = (x[i] - x[j]) * 1l * r[j][i];
				x[i] = (int)( (cur % pr[i] + pr[i]) % pr[i] );					
			}
			result = result.add( mult.multiply( BigInteger.valueOf( x[i] ) ) );
			mult = mult.multiply( BigInteger.valueOf( pr[i] ) );
		}
 
		if (can_be_negative)
			if (result.compareTo( mult.shiftRight(1) ) >= 0)
				result = result.subtract( mult );
 
		return result;*/
        NumberZ result=new NumberZ(0);
        NumberZ mult=new NumberZ(1);
        NumberZ []x=new NumberZ[mods.size()];
        for (int i=0; i<mods.size(); i++){
            x[i]=(NumberZ)rems[i].clone();
            for (int j=0; j<i; j++){
                NumberZ cur=(x[i].subtract(x[j])).multiply(r[j][i]);
                x[i]=(cur.mod(mods.get(i)).add(mods.get(i))).mod(mods.get(i));
            }
            result=result.add(mult.multiply(x[i]));
            mult=mult.multiply(mods.get(i));
        }
        return result;
    }
    
    public static void main(String[] args) {
     // Newton.recoveryNewton
//     int[] mod = {5,7,11};
//    int[] rem = {0,0,6}; 
//    NumberZ[] a = Newton.recoveryNewton(mod,rem); 
//     System.out.println("a = "+a[0]);

        //recoveryNewtonPolynom
        int[] rem1 = new int[]{2,1,1};
        int[] mod1 = new int[]{3,5,7};
        NumberZ num = recoveryNewton(mod1, rem1);
        System.out.println("num="+num);
        System.out.println("HELLO");
        Ring ring = new Ring("Z[x,y,z]");
//    
        Polynom p1 = new Polynom("y+2x+2", ring);
        Polynom p2 = new Polynom("3x^2y+3", ring);
        Polynom p3 = new Polynom("3x^2y+6y+x+1", ring);
        int[] mod = {13, 17, 23};
        Polynom rem[] = {p1, p2, p3};
        Polynom f = recoveryNewtonPolynom(mod, rem, ring);
        System.out.println("f = " + f.toString());
        System.out.println(3913 % 13 + "yx^2 " + 1938 % 13 + "y " + 1887 % 13 + "x " + 2485 % 13);
        System.out.println(3913 % 17 + "yx^2 " + 1938 % 17 + "y " + 1887 % 17 + "x " + 2485 % 17);
        System.out.println(3913 % 23 + "yx^2 " + 1938 % 23 + "y " + 1887 % 23 + "x " + 2485 % 23);
 
 
    }

}
