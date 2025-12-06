package com.mathpar.polynom.file;

import java.util.*;
import java.io.ObjectInputStream;
import java.io.*;

import com.mathpar.number.*;
import com.mathpar.polynom.file.util.FileUtils;
import com.mathpar.polynom.*;

/**
 * <p>Copyright: Copyright (c) ParCA3 Tambov, 2009</p>
 * @author Pozdnikin Alexey
 */
public class FPolynom extends Element implements Cloneable, Serializable{

    private static final long serialVersionUID = -5640511188375919262L;
    public File filename; // полный путь и имя файла, в который будет записан файловый полином
  public static File filenameResult; // полный путь и имя файла, в который будет записан файловый полином, полученный в арифметических операциях над файловыми полиномами
  public static Element itsCoeffOne;
  public static long freeMemory;
  public static long quantum;
  public static BasePolynomDir basedir;

  /**
  * константа, равная минимальному числу мономов в полиноме,
  * с которым операция умножения будет производиться в оперативной памяти
  * (константа подбирается экспериментальным путем)
  */
  //public static final int borderMonomNumb = 10;


  /**
  * константа, определяющая файловый полином равный нулю
  * @return директория null
  */
  public static final FPolynom ZERO = new FPolynom(null);


  /****************** реализация абстрактных методов ****************/
  /** Zero polynomial with coefficients of the type Element s
   *  Полином является нулевым, при p.coeffs.length=1, coeffs[0]=0
   * @param s with type Element
   * @return Zero polynomial
   */
    public static FPolynom polynom_zero(Element zero) {
      return ZERO;
    }

    /** Polynomial one with coefficients of the type Element s
     *  Полином является единицей, при p.coeffs.length=1, coeffs[0]=1
     * @param s with type Element
     * @return One polynomial
     */
    public static FPolynom polynom_one(Element one) {
      return (new Polynom(new int[0], new Element[] {one})).toFPolynom(filenameResult, itsCoeffOne, quantum);
    }

    /** Polynomial one
     * @return Polynomial one
     */
    public FPolynom myOne() {
      try{
        File fvar = new File(filename.getParentFile(), filename.getName()+"_var");
        ObjectOutputStream rw_var = new ObjectOutputStream(new FileOutputStream(fvar));
        rw_var.writeLong(1);
        rw_var.writeInt(0);
        ObjectOutputStream rw_pol = new ObjectOutputStream(new FileOutputStream(filename));
        rw_pol.writeObject(1);
      }catch(Exception e){e.printStackTrace();}
      return new FPolynom(filename);
    }

    /** Polynomial zero
     * @return Polynomial zero
     */
    public FPolynom myZero() {
      return ZERO;
    }

    /** Polynomial one
     * @return Polynomial one
     */
    public FPolynom myMinus_one() {
      return (new Polynom(new int[0], new Element[] {minus_one()})).toFPolynom(filename, itsCoeffOne, quantum);
    }

    public Element one() {
      return Polynom.polynom_one(itsCoeffOne);
    }

    public Element zero() {
      return Polynom.polynom_zero(itsCoeffOne);
    }

    public Element minus_one() {
      return Polynom.polynom_one(itsCoeffOne).negate(Ring.ringR64xyzt);
    }


  public FPolynom(){
    //this.filename = filename;
  }

  /**
   * конструктор создает файловый полином
   * @param directory директория на диске, где находиться файловый полином
   */
  public FPolynom(File FileName){
    this.filename = FileName;
    //this.itsCoeffOne = itsCoeffOne;
  }


   /**
   * конструктор генерирует случайный полином и записывает его в файл
   * @param root
   * @param maxpowers массив максимальных степеней полинома, не может быть меньше двух
   * @param density
   * @param rnd
   * @param mod модуль кольца полинома
   * @throws java.io.IOException
   */
  public FPolynom(File filename,int[] maxpowers, double density, int bits, Random rnd, Element itsCoeffOne) throws Exception{
      int nbits = bits/2;
      // если файл с таким уже существует в данной директории, то он будет удален
      // и на его место запишется новый
      if(filename.exists())filename.delete();

      // вычисляем плотность для полиномов, из произведения которых будет получен полином
      double den = Math.sqrt(density*100);

      // определяем длину будущих массивов (приближенно делим пополам)
      int flength = maxpowers.length /2;
      int glength = maxpowers.length;

      // создаем массив максимальных степеней для полинома g
      int g_maxpowers[] = new int[glength];
      // создаем массив максимальных степеней для полинома f
      int f_maxpowers[] = new int[flength];

      // переменные для хранения количества мономов
      // у полиномов gpolynom и fpolynom в отдельности при плотности den
      double Mg=1, Mf=1;

      // переменные для хранения количества мономов
      // у полиномов gpolynom и fpolynom в отдельности при плотности 100%
      double Maxg=1, Maxf=1;

      // заполняем массив максимальных степеней будущего полинома fpolynom
      for(int i=0; i<flength; i++){
        f_maxpowers[i] = maxpowers[i];
        Maxf = Maxf*(maxpowers[i]+1);
        Mf = (Maxf*den/100);
      }
      Mf = (int)Mf;

      // заполняем массив максимальных степеней будущего полинома gpolynom
      for(int i=0; i<flength; i++){g_maxpowers[i]=0;}
      for(int i=flength; i<glength; i++){
        g_maxpowers[i] = maxpowers[i];
        Maxg = Maxg*(maxpowers[i]+1);
        Mg = (Maxg*den/100);
      }
      Mg = (int)Mg;

      // количество мономов, которое необходимо получить, при заданной плотности
      double M=1;
      for(int i=0; i<maxpowers.length; i++){
        M = (M*(maxpowers[i]+1)*den/100);
      }
      M = (int)M;

      // проверка на количество мономов, которое мы должны получить при заданной плотности
      // если мономов не хватает то плотность повышается, до необходимой величины
      long Mgf = (long)(M-Mg*Mf);
      double gden = 0, fden = 0;
      long fs = 0;
      if(Mgf!=0){
        gden = 100/Maxg; // плотность, необходимая для одного монома
        if(den+gden>100){
          gden = 100-den;
          Mgf = Mgf-(long)(Maxg-Mg);
        }
        fden = 100/Maxf;
        fs=Mgf;
//        if(fden>100){
//          fs =(int)((fden-100)/Math.round(100/Maxf));
//          fden = 100;
//        }
      }

      //пересчет количества мономов, которое получиться в полиноме

      Ring ring = new Ring(itsCoeffOne.numbElementType(), 1);
      // создаем полином g с нужным числом мономов
      Polynom gpolynom = new Polynom(g_maxpowers, (int)(den+gden), nbits, rnd, itsCoeffOne, ring); // ?? den+gden -не превысит ли 100

      // разбиваем полученный полином g на мономы, записанные в отдельные полиномы
      Polynom gmonoms[] = new Polynom[gpolynom.coeffs.length];
      gmonoms=gpolynom.getmonoms();//(itsCoeffOne);

      // создаем полином f
      Polynom fpolynom = Polynom.polynom_one(itsCoeffOne);

      // инициализируем объект класса ObjectOutputStream, с помощью которого будет производиться запись
      // коэффициентов и степеней полинома в файл
      DataOutputStream rw_pol = DOS(filename);

      // инициализируем объект класса ObjectOutputStream, с помощью которого будет производиться запись
      // информации о полиноме (число мономов, число переменных) в файл
      File fvar = new File(filename.getParentFile(), filename.getName()+"_var");
      RandomAccessFile rw_var = new RandomAccessFile(fvar, "rw");

      int ttt = toInt(itsCoeffOne);
      rw_var.writeInt(toInt(itsCoeffOne)); // записываем в файл тип коэффициентов
      rw_var.writeInt(new Integer(maxpowers.length)); // записываем в файл количество переменных
      //rw_var.writeInt(new Integer(maxpowers.length)); // записываем в файл количество переменных
      rw_var.writeLong(new Long((long)M)); // записываем в файл количество мономов
      //System.out.println("кол-во мономов = "+(long)M);

      long numMonoms = 0;
      long numpart = 0;
      int index;
      int[] powers = new int[maxpowers.length];
      long sumbytes=0;
      Polynom polSum = Polynom.polynom_zero(itsCoeffOne);
      // записываем в файл произведение двух случайных полиномов
      for(int k=0; k<gmonoms.length; k++){
        index = 0;
        if(fs!=0){
          fpolynom = new Polynom(f_maxpowers, (int)(den+fden), nbits, rnd, itsCoeffOne, ring);
          fs--;
        }
        else fpolynom = new Polynom(f_maxpowers, (int)den, nbits, rnd, itsCoeffOne, ring);
        Polynom p = fpolynom.mulSS(gmonoms[k], ring);
        polSum = polSum.add(p, ring);
        numMonoms = numMonoms+p.coeffs.length;
        byte[] arr = FileUtils.toByteArray(polSum);
        if(arr.length >= quantum || k==gmonoms.length-1){
          //System.out.println("p = "+polSum+"  numberOfVar = "+arr.length);
          //we convert the polynomial to the array of bytes
          rw_pol.write(arr);
          sumbytes = sumbytes+arr.length;
          rw_var.writeLong(sumbytes);
          polSum = Polynom.polynom_zero(itsCoeffOne);
          numpart = numpart+1;
        }
      }

      rw_var.writeLong(numpart);

      rw_var.seek(8);
      rw_var.writeLong(numMonoms); // количество мономов в полиноме

      System.out.println("numpartCONSTR = "+numpart);

      // освобождаем буфер, закрываем файл
      rw_pol.flush();
      //System.out.println(" lengthF "+filename.length());
      rw_pol.close();
      rw_var.close();

      this.filename = filename;
    }

    public static int toInt(Element e){
      if(e instanceof NumberZ) return 0;
      else if (e instanceof NumberR) return 1;
      else if (e instanceof NumberZ64) return 2;
      else if (e instanceof NumberR64) return 3;
      else throw new RuntimeException("Illegal Element: "+e);
    }

    public static Element toElement(int itype){
      switch(itype){
          case 0: return NumberZ.ONE;
          case 1: return NumberR.ONE;
          case 2: return NumberZ64.ONE;
          case 3: return NumberR64.ONE;
          default: throw new RuntimeException("Illegal int type: "+itype);
      }
    }

    /**
     * вычисляет сумму двух файловых полиномов
     * @param fpolynom - один из полиномов
     * @param fresult
     * @param mod
     * @return
     */
    public FPolynom add(Element fpolynom){
      return add((FPolynom)fpolynom);
    }

    /**
     * вычисляет сумму двух файловых полиномов
     * @param fpolynom - один из полиномов
     * @param fresult
     * @param mod
     * @return
     */
    public FPolynom add(FPolynom fpolynom){
      return add(filename, fpolynom.filename, filenameResult);
    }


    /**
     * вычисляет сумму двух файловых полиномов
     * @param dir1 директория, в которой содержиться первый полином
     * @param dir2 директория, в которой содержиться второй полином
     * @param dir_result директория, в которую будет помещен полином результата суммы
     * @return
     */
    private FPolynom add(String dir1, String dir2){
      return add(dir1, dir2, filenameResult);
    }


    /**
     * вычисляет разность двух файловых полиномов
     * @param fpolynom
     * @param fresult
     * @param mod
     * @return
     */
    public FPolynom subtract(Element fpolynom){
      return subtract(fpolynom);
    }

    /**
     * вычисляет разность двух файловых полиномов
     * @param fpolynom
     * @param fresult
     * @param mod
     * @return
     */
    public FPolynom subtract(FPolynom fpolynom){
      return subtract(this.filename, fpolynom.filename, filenameResult);
    }


    /**
     * процедура умножения двух файловых полиномов
     * @param fpolynom
     * @param fresult
     * @param mod
     * @return
     */
    public FPolynom multiply(FPolynom fpolynom, File fresult) throws Exception{
      return mul(this, fpolynom, fresult,  0, this.getByteLength(), 0, fpolynom.getByteLength(), this.getNumParts(), fpolynom.getNumParts());
    }

    /* нужно передать int[] proc
    public FPolynom multiply(Element fpol){
      FPolynom fresult = new FPolynom(filenameResult);
      try{
        fresult = this.multiply((FPolynom)fpol, filenameResult);
      }catch(Exception e){e.printStackTrace();}
      return fresult;
    }

    */

   /*
    public FPolynom divide(FPolynom fpolynom, File fresult) throws Exception{
      return div(this, fpolynom, fresult,  0, this.getMonomNumber(), 0, fpolynom.getMonomNumber());
    }


    public FPolynom divide(Element fpol) {
      FPolynom fresult = new FPolynom(filenameResult);
      try{
        fresult = this.divide((FPolynom)fpol, filenameResult);
      }catch(Exception e){e.printStackTrace();}
      return fresult;
    }
*/


    /**
     * создает копию файлового полинома, в заданной директории
     * @param dir директория хранения копии файла
     * @return
     */
    public FPolynom copy(File nameCopy) throws IOException{
      FileUtils.copyFile(this.filename, nameCopy);
      return new FPolynom(nameCopy);
    }

    public String toString(int i) {
      return toString();
    }

    /*public static String toString(FPolynom fpol) throws IOException{
      try{
         Ring RR = new Ring("Z[x,y,z,u,v,w," +
                           "x7,x8,x9,x10,x11,x12,x13,x14,x15,x16,x17,x18,x19,x20]");
        return FPolynom.toString(fpol, RR);
      }catch (PolynomException ex) {
        ex.printStackTrace();
        return new String(" ");
      }
    }*/

    /**
     * процедура для вывода на экран полинома, записанного в файл
     * @param ring
     * @return
     * @throws java.io.IOException
     */
//    public static String toString(FPolynom fpol) throws Exception{
//      return FPolynom.toString(fpol, 0, fpol.getNumMonoms());
//    }

    /**
     * процедура для вывода на экран части полинома, записанного в файл
     * @param start - начиная с какого монома
     * @param num - количество мономов
     * @throws java.io.IOException
     */
//    public static String toString(FPolynom fpol, long start, long num) throws Exception{
//      Polynom pol = fpol.toPolynomOnMonoms(start, num);
//      return Polynom.toString(pol);
//    }



    /**
     * переводит файловый полином в полином типа PolynomL
     * @return
     * @throws java.io.IOException
     */
    public Polynom toPolynom() throws Exception{
      //System.out.println("ttttt = "+(int)this.getByteLength());
      return toPolynom(0, (int)this.getByteLength());
    }

    /**
     * переводит часть файлового полинома в полином
     * @param start позиция начала полинома
     * @param num количество мономов
     * @return
     * @throws java.io.IOException
     */
//    public Polynom toPolynomOnMonoms(long skipBytes, long num) throws IOException{
//
//      if(filename==null) throw new IOException("No file!"); // если файлового полинома с указанным именем, в указанной директории не существует
//
//      DataInputStream r_pol = DIS(filename);
//      // инициализируем объект класса ObjectOutputStream, с помощью которого будет производиться запись
//      // информации о полиноме (число мономов, число переменных) в файл
//      File fvar = new File(filename.getParentFile(), filename.getName()+"_var");
//      DataInputStream r_var = DIS(fvar);
//
//      int type = r_var.readInt();
//      int vars = r_var.readInt();
//      long numMonoms = r_var.readLong(); // количество мономов в полиноме
//
//      if(0>=(filename.length()-skipBytes)) throw new IOException("Wrong parametres of input!"); // проверка на количество мономов в полиноме
//
//      long numberOfVar = 0;
//      while(numberOfVar<skipBytes){
//        numberOfVar = r_var.readLong();
//      }
//
//      r_pol.skip(numberOfVar); // пропустим, если необходимо, некоторое количество байт
//
//      long numbytes = r_var.readLong()-numberOfVar;
//      Monom monom;
//      Element[] coeffs = new Element[(int)num]; // массив коэффициентов полинома
//      int[] powers = new int[(int)(vars*num)]; //массив степеней полинома
//
//      long readbytes = numbytes;
//
//      try{
//        for(int i=0; i<num; i++) {
//          byte mon[] = new byte[(int)readbytes];
//          r_pol.read(mon);
//          monom = (Monom)FileUtils.fromByteArr(mon);
//          //monom.printMonom();
//          coeffs[i] = monom.coef;
//          System.arraycopy(monom.powers, 0, powers, i*vars, vars);
//          if(i!=num-1){
//            readbytes = numbytes;
//            numbytes = r_var.readLong();
//            readbytes = (numbytes-readbytes);
//          }
//        }
//      }catch(Exception e){throw new IOException("Error read file!", e);}
//
//      r_pol.close();
//      r_var.close();
//      return new Polynom(powers, coeffs);
//
//    }


    /**
     * переводит часть файлового полинома в полином
     * @param start позиция начала полинома
     * @param num количество мономов
     * @return
     * @throws java.io.IOException
     */
    public Polynom toPolynom(long skipBytes, long bytes) throws Exception{

      Polynom polresult = Polynom.polynom_zero(itsCoeffOne);

      if(filename==null) throw new IOException("No file!"); // если файлового полинома с указанным именем, в указанной директории не существует

      DataInputStream r_pol = DIS(filename);
      // инициализируем объект класса ObjectOutputStream, с помощью которого будет производиться запись
      // информации о полиноме (число мономов, число переменных) в файл
      File fvar = new File(filename.getParentFile(), filename.getName()+"_var");
      DataInputStream r_var = DIS(fvar);

      int type = r_var.readInt();
      int vars = r_var.readInt();
      long numMonoms = r_var.readLong(); // количество мономов в полиноме

      if(0>=(filename.length()-skipBytes)) throw new IOException("Wrong parametres of polynomyal!"); // проверка на количество мономов в полиноме

      long size = 0;
      while(skipBytes>size){
        size = r_var.readLong();
      }

      if(size!=0){
        r_pol.skip(size);
        skipBytes=size;
      }

      //ArrayList<Polynom> polynom = new ArrayList<Polynom>();
      long allReadbytes=0; // общее количество считанных байт
      try{
        do{
          size = r_var.readLong()-allReadbytes-skipBytes;
          allReadbytes += size;
          byte b[] = new byte[(int)size];
          r_pol.read(b);
          Polynom pol = (Polynom)FileUtils.fromByteArr(b);
          polresult = polresult.add(pol, true, new Ring(itsCoeffOne.numbElementType(), 1));
        }while(bytes>allReadbytes); // пока не считаем весь полином
      }catch(Exception e){throw new Exception("Error: Ошибка перевода файлового полинома в оперативную память!", e);}

      r_var.close();
      r_pol.close();
      return polresult;
    }


    // return type of coefficients
    public Element getType(){
      int type = 0;
      try{
        // инициализируем объект класса ObjectOutputStream, с помощью которого будет производиться запись
        // информации о полиноме (число мономов, число переменных) в файл
        File fvar = new File(filename.getParentFile(), filename.getName()+"_var");
        RandomAccessFile r_var = new RandomAccessFile(fvar, "r");
        type = r_var.readInt();
        r_var.close();
        } catch(Exception e){e.printStackTrace();}
      return toElement(type);
    }

    // return number of variables
    public int getNumVars(){
      int numvars = 0;
      try{
        // инициализируем объект класса ObjectOutputStream, с помощью которого будет производиться запись
        // информации о полиноме (число мономов, число переменных) в файл
        File fvar = new File(filename.getParentFile(), filename.getName()+"_var");
        RandomAccessFile r_var = new RandomAccessFile(fvar, "r");
        r_var.seek(4);
        numvars = r_var.readInt();
        r_var.close();
      } catch(Exception e){e.printStackTrace();}
      return numvars;
    }

    // return number of monomials
    public long getNumMonoms(){
      long nummonoms = 0;
      try{
        // инициализируем объект класса ObjectOutputStream, с помощью которого будет производиться запись
        // информации о полиноме (число мономов, число переменных) в файл
        File fvar = new File(filename.getParentFile(), filename.getName()+"_var");
        RandomAccessFile r_var = new RandomAccessFile(fvar, "r");
        r_var.seek(8);
        nummonoms = r_var.readLong();
        r_var.close();
      } catch(Exception e){e.printStackTrace();}
      return nummonoms;
    }

    // return number of parts in polynomial
    public long getNumParts(){
      long numparts = 0;
      try{
        // инициализируем объект класса ObjectOutputStream, с помощью которого будет производиться запись
        // информации о полиноме (число мономов, число переменных) в файл
        File fvar = new File(filename.getParentFile(), filename.getName()+"_var");
        RandomAccessFile r_var = new RandomAccessFile(fvar, "r");
        r_var.seek(fvar.length()-8);
        numparts = r_var.readLong();
        r_var.close();
      } catch(Exception e){e.printStackTrace();}
      return numparts;
    }

    /*
    public long[] getInformatoin(){
      long numvars = 0;
      long nummonoms = 0;
      try{
        // инициализируем объект класса ObjectOutputStream, с помощью которого будет производиться запись
        // информации о полиноме (число мономов, число переменных) в файл
        File fvar = new File(filename.getParentFile(), filename.getName()+"_var");
        RandomAccessFile r_var = new RandomAccessFile(fvar, "r");
        nummonoms = r_var.readLong();
        numvars = r_var.readInt();
        r_var.close();
      } catch(Exception e){e.printStackTrace();}
      return new long[]{nummonoms, numvars};
    }
    */

//    /**
//     * определяет число байт, которое можно считать из заданного количества байт,
//     * чтобы бы при этом попасть на конец монома
//     * @param approximateMiddle
//     * @return
//     */
//    public long middlePolynom(long skipBytes, long approximateMiddle) throws Exception{
//      File fvar = new File(filename.getParentFile(), filename.getName()+"_var");
//      long numberOfVar = 0;
//      try{
//        DataInputStream r_var = DIS(fvar);
//        r_var.skip(16);
//        approximateMiddle += skipBytes;
//        while(approximateMiddle>numberOfVar)
//            numberOfVar = r_var.readLong();
//        r_var.close();
//      } catch(IOException e){throw new IOException("Error: Ошибка чтения из файла!", e);}
//      //if(this.getByteLength()==numberOfVar) return quantum;
//      return numberOfVar-skipBytes;
//    }


    /**
     * определяет число байт, которое можно считать из заданного количества байт,
     * чтобы бы при этом попасть на конец монома
     * @param approximateMiddle
     * @return
     */
    public long middlePolynom(long skipBytes, long numParts) throws Exception{
      File fvar = new File(filename.getParentFile(), filename.getName()+"_var");
      long num = numParts;
      long middle = num/2;
      long size = 0, skip=0;
      try{
        DataInputStream r_var = DIS(fvar);
        r_var.skip(16);
        while(skipBytes>0 && skipBytes!=skip) {skip = r_var.readLong();}
        for(int i=0; i<middle; i++) size = r_var.readLong();
        r_var.close();
      } catch(IOException e){throw new Exception("Error: Ошибка чтения из файла!", e);}
      //if(this.getByteLength()==numberOfVar) return quantum;
      return size-skip;
    }



    /**
     * процедура возвращает число байт, которое занимает файловый полином на диске
     * @return - размер файлового полинома в байтах
     */
    public long getByteLength(){
      return this.filename.length();
    }


    public static long getMemoryForMultyply(FPolynom fpol1, FPolynom fpol2, long s1, long  n1, long s2, long n2) throws Exception{
     //long time = System.currentTimeMillis();
      long n=0;
      File fvar1 = new File(fpol1.filename.getParentFile(), fpol1.filename.getName()+"_var");
      File fvar2 = new File(fpol2.filename.getParentFile(), fpol2.filename.getName()+"_var");
      try{
        DataInputStream r_var1 = DIS(fvar1);
        DataInputStream r_var2 = DIS(fvar2);
        r_var1.skip(16); r_var2.skip(16);

        long size=0;

        while(s1>size) {size = r_var1.readLong();}

        size=0;
        while(s2>size) {size = r_var2.readLong();}

        long m1=0, m2=0;

        size=0;
        do{ size = r_var1.readLong(); m1++;} while(n1>size);

        size=0;
        do{
          size = r_var2.readLong();
          m2++;
        } while(n2>size);

        long m = m1*m2;
        n = m*((n1/m1)+(n2/m2));
        r_var1.close();
        r_var2.close();
      } catch(IOException e){throw new Exception("Error: Ошибка чтения из файла!", e);}
      //System.out.println("time = "+(System.currentTimeMillis()-time));
      //System.out.println("NNNNNNNN = "+n);
      return n;
    }

    public static long getMemoryForMultyply2(long s1, long  n1, long s2, long n2) throws Exception{
      long length1 = n1-s1;
      long length2 = n2-s2;
      long n = length1+length2;
      return n;
    }


     // функция для быстрой инициализации потока для чтения обекта из файла
    private static ObjectInputStream OIS(File filename)throws IOException{
      return new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)));
    }
    // функция для быстрой инициализации потока для записи объекта в файл
    private static ObjectOutputStream OOS(File filename)throws Exception{
      return new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
    }

     // функция для быстрой инициализации потока для чтения обекта из файла
    private static DataInputStream DIS(File filename)throws IOException{
      return new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
    }
    // функция для быстрой инициализации потока для записи объекта в файл
    private static DataOutputStream DOS(File filename)throws Exception{
      return new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
    }

    // функция для быстрой инициализации потока для чтения обекта из файла
    private static DataInputStream DIS(String filename)throws IOException{
      return new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
    }
    // функция для быстрой инициализации потока для записи объекта в файл
    private static DataOutputStream DOS(String filename)throws Exception{
      return new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
    }

      // ДЛЯ ТЕСТОВ С СИСТЕМОЙ КОМПЬЮТЕРНОЙ АЛГЕБРЫ FORM
//    public void toTextFile(File dir_txt) throws Exception{
//      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
//      DataInputStream r_pol = DIS(filename);
//
//      // создаем объект класса ObjectInputStream для чтения
//      File fvar = new File(filename.getParentFile(), filename.getName()+"_var");
//      DataInputStream r_var = DIS(fvar);
//
//      int ring = r_var.readInt();
//      r_var.readInt();
//      r_var.readLong();
//
//
//      FileWriter rw_pol = new FileWriter(dir_txt);
//      BufferedWriter bw = new BufferedWriter(rw_pol);
//      //r_var.readLong();
//
//      String polString = "";
//      //long readbytes = 0;
//      long filelength = filename.length();
//
//      long numBytes = r_var.readLong();
//      byte[] b = new byte[(int)numBytes];
//      r_pol.read(b);
//      Polynom pathPol = (Polynom)FileUtils.fromByteArr(b);
//      polString  = Polynom.toStringforFORM(pathPol, getRing(ring));
//      bw.write(polString); bw.flush();
//      polString = "";
//
//      long sumBytes = numBytes;
//      while(sumBytes<filelength){
//        numBytes = r_var.readLong()-sumBytes;
//        b = new byte[(int)numBytes];
//        r_pol.read(b);
//        pathPol = (Polynom)FileUtils.fromByteArr(b);
//        if(pathPol.signum()==1) polString=" +";
//        else polString=" -";
//        polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring));
//        bw.write(polString); bw.flush();
//        polString = "";
//        sumBytes = sumBytes+numBytes;
//      }
//
//      bw.close();
//      r_var.close();
//    }


//    public void toFormFileSum1Pol(FPolynom fpol2, File dir_frm) throws Exception{
//      FileWriter rw_pol = new FileWriter(dir_frm);
//      BufferedWriter bw = new BufferedWriter(rw_pol);
//      bw.write("Symbols f1, f2, x, y;\n  Local sum = (f1+f2);  \n \n  \n  id f1 = ");
//
//
//      //первый полином
//      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
//      DataInputStream r_pol = DIS(filename);
//
//      // создаем объект класса ObjectInputStream для чтения
//      File fvar = new File(filename.getParentFile(), filename.getName()+"_var");
//      DataInputStream r_var = DIS(fvar);
//
//      int ring = r_var.readInt();
//      r_var.readInt();
//      r_var.readLong();
//
//      //r_var.readLong();
//
//      String polString = "";
//      //long readbytes = 0;
//      long filelength = filename.length();
//
//      long numBytes = r_var.readLong();
//      byte[] b = new byte[(int)numBytes];
//      r_pol.read(b);
//      Polynom pathPol = (Polynom)FileUtils.fromByteArr(b);
//
//      polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring));
//      bw.write(polString); bw.flush();
//      polString = "";
//
//      long sumBytes = numBytes;
//      while(sumBytes<filelength){
//        numBytes = r_var.readLong()-sumBytes;
//        b = new byte[(int)numBytes];
//        r_pol.read(b);
//        pathPol = (Polynom)FileUtils.fromByteArr(b);
//        if(pathPol.signum()==1) polString=" +";
//        else polString=" -";
//        polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring));
//        bw.write(polString); bw.flush();
//        polString = "";
//        sumBytes = sumBytes+numBytes;
//      }
//
//      //второй полином
//      bw.write(";\n\n id f2 = ");bw.flush();
//
//
//      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
//      DataInputStream r_pol2 = DIS(fpol2.filename);
//
//      // создаем объект класса ObjectInputStream для чтения
//      File fvar2 = new File(fpol2.filename.getParentFile(), fpol2.filename.getName()+"_var");
//      DataInputStream r_var2 = DIS(fvar2);
//
//      int ring2 = r_var2.readInt();
//      r_var2.readInt();
//      r_var2.readLong();
//
//      //r_var.readLong();
//
//      //String polString = "";
//      //long readbytes = 0;
//
//      numBytes = r_var2.readLong();
//      b = new byte[(int)numBytes];
//      r_pol2.read(b);
//      pathPol = (Polynom)FileUtils.fromByteArr(b);
//
//      polString  = Polynom.toStringforFORM(pathPol, getRing(ring2));
//      bw.write(polString); bw.flush();
//      polString = "";
//
//      filelength = fpol2.filename.length();
//
//      sumBytes = numBytes;
//      while(sumBytes<filelength){
//        numBytes = r_var2.readLong()-sumBytes;
//        b = new byte[(int)numBytes];
//        r_pol2.read(b);
//        pathPol = (Polynom)FileUtils.fromByteArr(b);
//        if(pathPol.signum()==1) polString=" +";
//        else polString=" -";
//        polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring2));
//        bw.write(polString); bw.flush();
//        polString = "";
//        sumBytes = sumBytes+numBytes;
//      }
//
//      bw.write(";\n\n \n \n .end");
//
//      bw.close();
//      r_var.close();
//    }
//
//    public void toFormFileMul1Pol(FPolynom fpol2, File dir_frm) throws Exception{
//      FileWriter rw_pol = new FileWriter(dir_frm);
//      BufferedWriter bw = new BufferedWriter(rw_pol);
//      bw.write("Symbols f1, f2, x, y;\n \n Local mul = (f1*f2);\n  id f1 = ");
//
//
//      //первый полином
//      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
//      DataInputStream r_pol = DIS(filename);
//
//      // создаем объект класса ObjectInputStream для чтения
//      File fvar = new File(filename.getParentFile(), filename.getName()+"_var");
//      DataInputStream r_var = DIS(fvar);
//
//      int ring = r_var.readInt();
//      r_var.readInt();
//      r_var.readLong();
//
//      //r_var.readLong();
//
//      String polString = "";
//      //long readbytes = 0;
//      long filelength = filename.length();
//
//      long numBytes = r_var.readLong();
//      byte[] b = new byte[(int)numBytes];
//      r_pol.read(b);
//      Polynom pathPol = (Polynom)FileUtils.fromByteArr(b);
//
//      polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring));
//      bw.write(polString); bw.flush();
//      polString = "";
//
//      long sumBytes = numBytes;
//      while(sumBytes<filelength){
//        numBytes = r_var.readLong()-sumBytes;
//        b = new byte[(int)numBytes];
//        r_pol.read(b);
//        pathPol = (Polynom)FileUtils.fromByteArr(b);
//        if(pathPol.signum()==1) polString=" +";
//        else polString=" -";
//        polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring));
//        bw.write(polString); bw.flush();
//        polString = "";
//        sumBytes = sumBytes+numBytes;
//      }
//
//      //второй полином
//      bw.write(";\n\n  id f2 = ");bw.flush();
//
//
//      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
//      DataInputStream r_pol2 = DIS(fpol2.filename);
//
//      // создаем объект класса ObjectInputStream для чтения
//      File fvar2 = new File(fpol2.filename.getParentFile(), fpol2.filename.getName()+"_var");
//      DataInputStream r_var2 = DIS(fvar2);
//
//      int ring2 = r_var2.readInt();
//      r_var2.readInt();
//      r_var2.readLong();
//
//      //r_var.readLong();
//
//      //String polString = "";
//      //long readbytes = 0;
//
//      numBytes = r_var2.readLong();
//      b = new byte[(int)numBytes];
//      r_pol2.read(b);
//      pathPol = (Polynom)FileUtils.fromByteArr(b);
//
//      polString  = Polynom.toStringforFORM(pathPol, getRing(ring2));
//      bw.write(polString); bw.flush();
//      polString = "";
//
//      filelength = fpol2.filename.length();
//
//      sumBytes = numBytes;
//      while(sumBytes<filelength){
//        numBytes = r_var2.readLong()-sumBytes;
//        b = new byte[(int)numBytes];
//        r_pol2.read(b);
//        pathPol = (Polynom)FileUtils.fromByteArr(b);
//        if(pathPol.signum()==1) polString=" +";
//        else polString=" -";
//        polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring2));
//        bw.write(polString); bw.flush();
//        polString = "";
//        sumBytes = sumBytes+numBytes;
//      }
//
//      bw.write(";\n\n \n .end");
//
//      bw.close();
//      r_var.close();
//    }
//
//
//    public void toFormFileSum2Pol(FPolynom fpol2, File dir_frm) throws Exception{
//      FileWriter rw_pol = new FileWriter(dir_frm);
//      BufferedWriter bw = new BufferedWriter(rw_pol);
//      bw.write("#procedure sum(pol1,pol2) \n Local sum = (pol1+pol2); \n #endprocedure \n Symbols x, y; \n #do i = 0, 10 \n Local pol1 = ");
//
//
//      //первый полином
//      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
//      DataInputStream r_pol = DIS(filename);
//
//      // создаем объект класса ObjectInputStream для чтения
//      File fvar = new File(filename.getParentFile(), filename.getName()+"_var");
//      DataInputStream r_var = DIS(fvar);
//
//      int ring = r_var.readInt();
//      r_var.readInt();
//      r_var.readLong();
//
//      //r_var.readLong();
//
//      String polString = "";
//      //long readbytes = 0;
//      long filelength = filename.length();
//
//      long numBytes = r_var.readLong();
//      byte[] b = new byte[(int)numBytes];
//      r_pol.read(b);
//      Polynom pathPol = (Polynom)FileUtils.fromByteArr(b);
//
//      polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring));
//      bw.write(polString); bw.flush();
//      polString = "";
//
//      long sumBytes = numBytes;
//      while(sumBytes<filelength){
//        numBytes = r_var.readLong()-sumBytes;
//        b = new byte[(int)numBytes];
//        r_pol.read(b);
//        pathPol = (Polynom)FileUtils.fromByteArr(b);
//        if(pathPol.signum()==1) polString=" +";
//        else polString=" -";
//        polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring));
//        bw.write(polString); bw.flush();
//        polString = "";
//        sumBytes = sumBytes+numBytes;
//      }
//
//      //второй полином
//      bw.write(";\n\n Local pol2 = ");bw.flush();
//
//
//      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
//      DataInputStream r_pol2 = DIS(fpol2.filename);
//
//      // создаем объект класса ObjectInputStream для чтения
//      File fvar2 = new File(fpol2.filename.getParentFile(), fpol2.filename.getName()+"_var");
//      DataInputStream r_var2 = DIS(fvar2);
//
//      int ring2 = r_var2.readInt();
//      r_var2.readInt();
//      r_var2.readLong();
//
//      //r_var.readLong();
//
//      //String polString = "";
//      //long readbytes = 0;
//
//      numBytes = r_var2.readLong();
//      b = new byte[(int)numBytes];
//      r_pol2.read(b);
//      pathPol = (Polynom)FileUtils.fromByteArr(b);
//
//      polString  = Polynom.toStringforFORM(pathPol, getRing(ring2));
//      bw.write(polString); bw.flush();
//      polString = "";
//
//      filelength = fpol2.filename.length();
//
//      sumBytes = numBytes;
//      while(sumBytes<filelength){
//        numBytes = r_var2.readLong()-sumBytes;
//        b = new byte[(int)numBytes];
//        r_pol2.read(b);
//        pathPol = (Polynom)FileUtils.fromByteArr(b);
//        if(pathPol.signum()==1) polString=" +";
//        else polString=" -";
//        polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring2));
//        bw.write(polString); bw.flush();
//        polString = "";
//        sumBytes = sumBytes+numBytes;
//      }
//
//      bw.write(";\n  \n #call sum(pol1,pol2);\n #enddo; \n .end");
//
//      bw.close();
//      r_var.close();
//    }
//
//
//     public void toFormFileMul2Pol(FPolynom fpol2, File dir_frm) throws Exception{
//      FileWriter rw_pol = new FileWriter(dir_frm);
//      BufferedWriter bw = new BufferedWriter(rw_pol);
//      bw.write("#procedure mul(pol1,pol2) \n Local mul = (pol1*pol2); \n #endprocedure \n Symbols x, y; \n\n Local pol1 = ");
//
//
//      //первый полином
//      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
//      DataInputStream r_pol = DIS(filename);
//
//      // создаем объект класса ObjectInputStream для чтения
//      File fvar = new File(filename.getParentFile(), filename.getName()+"_var");
//      DataInputStream r_var = DIS(fvar);
//
//      int ring = r_var.readInt();
//      r_var.readInt();
//      r_var.readLong();
//
//      //r_var.readLong();
//
//      String polString = "";
//      //long readbytes = 0;
//      long filelength = filename.length();
//
//      long numBytes = r_var.readLong();
//      byte[] b = new byte[(int)numBytes];
//      r_pol.read(b);
//      Polynom pathPol = (Polynom)FileUtils.fromByteArr(b);
//
//      polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring));
//      bw.write(polString); bw.flush();
//      polString = "";
//
//      long sumBytes = numBytes;
//      while(sumBytes<filelength){
//        numBytes = r_var.readLong()-sumBytes;
//        b = new byte[(int)numBytes];
//        r_pol.read(b);
//        pathPol = (Polynom)FileUtils.fromByteArr(b);
//        if(pathPol.signum()==1) polString=" +";
//        else polString=" -";
//        polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring));
//        bw.write(polString); bw.flush();
//        polString = "";
//        sumBytes = sumBytes+numBytes;
//      }
//
//      //второй полином
//      bw.write(";\n\n Local pol2 = ");bw.flush();
//
//
//      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
//      DataInputStream r_pol2 = DIS(fpol2.filename);
//
//      // создаем объект класса ObjectInputStream для чтения
//      File fvar2 = new File(fpol2.filename.getParentFile(), fpol2.filename.getName()+"_var");
//      DataInputStream r_var2 = DIS(fvar2);
//
//      int ring2 = r_var2.readInt();
//      r_var2.readInt();
//      r_var2.readLong();
//
//      //r_var.readLong();
//
//      //String polString = "";
//      //long readbytes = 0;
//
//      numBytes = r_var2.readLong();
//      b = new byte[(int)numBytes];
//      r_pol2.read(b);
//      pathPol = (Polynom)FileUtils.fromByteArr(b);
//
//      polString  = Polynom.toStringforFORM(pathPol, getRing(ring2));
//      bw.write(polString); bw.flush();
//      polString = "";
//
//      filelength = fpol2.filename.length();
//
//      sumBytes = numBytes;
//      while(sumBytes<filelength){
//        numBytes = r_var2.readLong()-sumBytes;
//        b = new byte[(int)numBytes];
//        r_pol2.read(b);
//        pathPol = (Polynom)FileUtils.fromByteArr(b);
//        if(pathPol.signum()==1) polString=" +";
//        else polString=" -";
//        polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring2));
//        bw.write(polString); bw.flush();
//        polString = "";
//        sumBytes = sumBytes+numBytes;
//      }
//
//      bw.write(";\n\n #call mul(pol1,pol2);\n Print; \n .end");
//
//      bw.close();
//      r_var.close();
//    }
//
//
//    public void toFormFile(FPolynom fpol2, FPolynom fpol3, FPolynom fpol4, FPolynom fpol5, FPolynom fpol6, File dir_frm) throws Exception{
//      FileWriter rw_pol = new FileWriter(dir_frm);
//      BufferedWriter bw = new BufferedWriter(rw_pol);
//      bw.write("Symbols f1, f2, f3, f4, f5, f6, x, y;\n \n Local mul = f1*f2+f3*f4+f5*f6;\n  id f1 = ");
//
//
//      //первый полином
//      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
//      DataInputStream r_pol = DIS(filename);
//
//      // создаем объект класса ObjectInputStream для чтения
//      File fvar = new File(filename.getParentFile(), filename.getName()+"_var");
//      DataInputStream r_var = DIS(fvar);
//
//      int ring = r_var.readInt();
//      r_var.readInt();
//      r_var.readLong();
//
//      //r_var.readLong();
//
//      String polString = "";
//      //long readbytes = 0;
//      long filelength = filename.length();
//
//      long numBytes = r_var.readLong();
//      byte[] b = new byte[(int)numBytes];
//      r_pol.read(b);
//      Polynom pathPol = (Polynom)FileUtils.fromByteArr(b);
//
//      polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring));
//      bw.write(polString); bw.flush();
//      polString = "";
//
//      long sumBytes = numBytes;
//      while(sumBytes<filelength){
//        numBytes = r_var.readLong()-sumBytes;
//        b = new byte[(int)numBytes];
//        r_pol.read(b);
//        pathPol = (Polynom)FileUtils.fromByteArr(b);
//        if(pathPol.signum()==1) polString=" +";
//        else polString=" -";
//        polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring));
//        bw.write(polString); bw.flush();
//        polString = "";
//        sumBytes = sumBytes+numBytes;
//      }
//
//      //второй полином
//      bw.write(";\n\n id f2 = ");bw.flush();
//
//
//      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
//      DataInputStream r_pol2 = DIS(fpol2.filename);
//
//      // создаем объект класса ObjectInputStream для чтения
//      File fvar2 = new File(fpol2.filename.getParentFile(), fpol2.filename.getName()+"_var");
//      DataInputStream r_var2 = DIS(fvar2);
//
//      int ring2 = r_var2.readInt();
//      r_var2.readInt();
//      r_var2.readLong();
//
//      //r_var.readLong();
//
//      //String polString = "";
//      //long readbytes = 0;
//
//      numBytes = r_var2.readLong();
//      b = new byte[(int)numBytes];
//      r_pol2.read(b);
//      pathPol = (Polynom)FileUtils.fromByteArr(b);
//
//      polString  = Polynom.toStringforFORM(pathPol, getRing(ring2));
//      bw.write(polString); bw.flush();
//      polString = "";
//
//      filelength = fpol2.filename.length();
//
//      sumBytes = numBytes;
//      while(sumBytes<filelength){
//        numBytes = r_var2.readLong()-sumBytes;
//        b = new byte[(int)numBytes];
//        r_pol2.read(b);
//        pathPol = (Polynom)FileUtils.fromByteArr(b);
//        if(pathPol.signum()==1) polString=" +";
//        else polString=" -";
//        polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring2));
//        bw.write(polString); bw.flush();
//        polString = "";
//        sumBytes = sumBytes+numBytes;
//      }
//
//      //третий полином
//      bw.write(";\n\n id f3 = ");bw.flush();
//
//
//      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
//      DataInputStream r_pol3 = DIS(fpol3.filename);
//
//      // создаем объект класса ObjectInputStream для чтения
//      File fvar3 = new File(fpol3.filename.getParentFile(), fpol3.filename.getName()+"_var");
//      DataInputStream r_var3 = DIS(fvar3);
//
//      int ring3 = r_var3.readInt();
//      r_var3.readInt();
//      r_var3.readLong();
//
//      //r_var.readLong();
//
//      //String polString = "";
//      //long readbytes = 0;
//
//      numBytes = r_var3.readLong();
//      b = new byte[(int)numBytes];
//      r_pol3.read(b);
//      pathPol = (Polynom)FileUtils.fromByteArr(b);
//
//      polString  = Polynom.toStringforFORM(pathPol, getRing(ring3));
//      bw.write(polString); bw.flush();
//      polString = "";
//
//      filelength = fpol3.filename.length();
//
//      sumBytes = numBytes;
//      while(sumBytes<filelength){
//        numBytes = r_var3.readLong()-sumBytes;
//        b = new byte[(int)numBytes];
//        r_pol3.read(b);
//        pathPol = (Polynom)FileUtils.fromByteArr(b);
//        if(pathPol.signum()==1) polString=" +";
//        else polString=" -";
//        polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring3));
//        bw.write(polString); bw.flush();
//        polString = "";
//        sumBytes = sumBytes+numBytes;
//      }
//
//      //четвертый полином
//      bw.write(";\n\n id f4 = ");bw.flush();
//
//
//      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
//      DataInputStream r_pol4 = DIS(fpol4.filename);
//
//      // создаем объект класса ObjectInputStream для чтения
//      File fvar4 = new File(fpol4.filename.getParentFile(), fpol4.filename.getName()+"_var");
//      DataInputStream r_var4 = DIS(fvar4);
//
//      int ring4 = r_var4.readInt();
//      r_var4.readInt();
//      r_var4.readLong();
//
//      //r_var.readLong();
//
//      //String polString = "";
//      //long readbytes = 0;
//
//      numBytes = r_var4.readLong();
//      b = new byte[(int)numBytes];
//      r_pol4.read(b);
//      pathPol = (Polynom)FileUtils.fromByteArr(b);
//
//      polString  = Polynom.toStringforFORM(pathPol, getRing(ring4));
//      bw.write(polString); bw.flush();
//      polString = "";
//
//      filelength = fpol4.filename.length();
//
//      sumBytes = numBytes;
//      while(sumBytes<filelength){
//        numBytes = r_var4.readLong()-sumBytes;
//        b = new byte[(int)numBytes];
//        r_pol4.read(b);
//        pathPol = (Polynom)FileUtils.fromByteArr(b);
//        if(pathPol.signum()==1) polString=" +";
//        else polString=" -";
//        polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring4));
//        bw.write(polString); bw.flush();
//        polString = "";
//        sumBytes = sumBytes+numBytes;
//      }
//
//      //пятый полином
//      bw.write(";\n\n id f5 = ");bw.flush();
//
//
//      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
//      DataInputStream r_pol5 = DIS(fpol5.filename);
//
//      // создаем объект класса ObjectInputStream для чтения
//      File fvar5 = new File(fpol5.filename.getParentFile(), fpol5.filename.getName()+"_var");
//      DataInputStream r_var5 = DIS(fvar5);
//
//      int ring5 = r_var5.readInt();
//      r_var5.readInt();
//      r_var5.readLong();
//
//      //r_var.readLong();
//
//      //String polString = "";
//      //long readbytes = 0;
//
//      numBytes = r_var5.readLong();
//      b = new byte[(int)numBytes];
//      r_pol5.read(b);
//      pathPol = (Polynom)FileUtils.fromByteArr(b);
//
//      polString  = Polynom.toStringforFORM(pathPol, getRing(ring5));
//      bw.write(polString); bw.flush();
//      polString = "";
//
//      filelength = fpol5.filename.length();
//
//      sumBytes = numBytes;
//      while(sumBytes<filelength){
//        numBytes = r_var5.readLong()-sumBytes;
//        b = new byte[(int)numBytes];
//        r_pol5.read(b);
//        pathPol = (Polynom)FileUtils.fromByteArr(b);
//        if(pathPol.signum()==1) polString=" +";
//        else polString=" -";
//        polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring5));
//        bw.write(polString); bw.flush();
//        polString = "";
//        sumBytes = sumBytes+numBytes;
//      }
//
//
//      //шестой полином
//      bw.write(";\n\n id f6 = ");bw.flush();
//
//
//      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
//      DataInputStream r_pol6 = DIS(fpol6.filename);
//
//      // создаем объект класса ObjectInputStream для чтения
//      File fvar6 = new File(fpol6.filename.getParentFile(), fpol6.filename.getName()+"_var");
//      DataInputStream r_var6 = DIS(fvar6);
//
//      int ring6 = r_var6.readInt();
//      r_var6.readInt();
//      r_var6.readLong();
//
//      //r_var.readLong();
//
//      //String polString = "";
//      //long readbytes = 0;
//
//      numBytes = r_var6.readLong();
//      b = new byte[(int)numBytes];
//      r_pol6.read(b);
//      pathPol = (Polynom)FileUtils.fromByteArr(b);
//
//      polString  = Polynom.toStringforFORM(pathPol, getRing(ring2));
//      bw.write(polString); bw.flush();
//      polString = "";
//
//      filelength = fpol6.filename.length();
//
//      sumBytes = numBytes;
//      while(sumBytes<filelength){
//        numBytes = r_var6.readLong()-sumBytes;
//        b = new byte[(int)numBytes];
//        r_pol6.read(b);
//        pathPol = (Polynom)FileUtils.fromByteArr(b);
//        if(pathPol.signum()==1) polString=" +";
//        else polString=" -";
//        polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring6));
//        bw.write(polString); bw.flush();
//        polString = "";
//        sumBytes = sumBytes+numBytes;
//      }
//
//
//      bw.write(";\n\n \n .end");
//
//      bw.close();
//      r_var.close();
//    }
//
//    public void toFormFile1(FPolynom fpol2, FPolynom fpol3, FPolynom fpol4, FPolynom fpol5, FPolynom fpol6, File dir_frm) throws Exception{
//      FileWriter rw_pol = new FileWriter(dir_frm);
//      BufferedWriter bw = new BufferedWriter(rw_pol);
//      bw.write("Symbols f1, f2, f3, f4, f5, f6, x, y;\n Off statistics;\n Local mul = f1*f2+f3*f4+f5*f6;\n  id f1 = ");
//
//
//      //первый полином
//      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
//      DataInputStream r_pol = DIS(filename);
//
//      // создаем объект класса ObjectInputStream для чтения
//      File fvar = new File(filename.getParentFile(), filename.getName()+"_var");
//      DataInputStream r_var = DIS(fvar);
//
//      int ring = r_var.readInt();
//      r_var.readInt();
//      r_var.readLong();
//
//      //r_var.readLong();
//
//      String polString = "";
//      //long readbytes = 0;
//      long filelength = filename.length();
//
//      long numBytes = r_var.readLong();
//      byte[] b = new byte[(int)numBytes];
//      r_pol.read(b);
//      Polynom pathPol = (Polynom)FileUtils.fromByteArr(b);
//
//      polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring));
//      bw.write(polString); bw.flush();
//      polString = "";
//
//      long sumBytes = numBytes;
//      while(sumBytes<filelength){
//        numBytes = r_var.readLong()-sumBytes;
//        b = new byte[(int)numBytes];
//        r_pol.read(b);
//        pathPol = (Polynom)FileUtils.fromByteArr(b);
//        if(pathPol.signum()==1) polString=" +";
//        else polString=" -";
//        polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring));
//        bw.write(polString); bw.flush();
//        polString = "";
//        sumBytes = sumBytes+numBytes;
//      }
//
//      //второй полином
//      bw.write(";\n\n id f2 = ");bw.flush();
//
//
//      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
//      DataInputStream r_pol2 = DIS(fpol2.filename);
//
//      // создаем объект класса ObjectInputStream для чтения
//      File fvar2 = new File(fpol2.filename.getParentFile(), fpol2.filename.getName()+"_var");
//      DataInputStream r_var2 = DIS(fvar2);
//
//      int ring2 = r_var2.readInt();
//      r_var2.readInt();
//      r_var2.readLong();
//
//      //r_var.readLong();
//
//      //String polString = "";
//      //long readbytes = 0;
//
//      numBytes = r_var2.readLong();
//      b = new byte[(int)numBytes];
//      r_pol2.read(b);
//      pathPol = (Polynom)FileUtils.fromByteArr(b);
//
//      polString  = Polynom.toStringforFORM(pathPol, getRing(ring2));
//      bw.write(polString); bw.flush();
//      polString = "";
//
//      filelength = fpol2.filename.length();
//
//      sumBytes = numBytes;
//      while(sumBytes<filelength){
//        numBytes = r_var2.readLong()-sumBytes;
//        b = new byte[(int)numBytes];
//        r_pol2.read(b);
//        pathPol = (Polynom)FileUtils.fromByteArr(b);
//        if(pathPol.signum()==1) polString=" +";
//        else polString=" -";
//        polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring2));
//        bw.write(polString); bw.flush();
//        polString = "";
//        sumBytes = sumBytes+numBytes;
//      }
//
//      //третий полином
//      bw.write(";\n\n id f3 = ");bw.flush();
//
//
//      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
//      DataInputStream r_pol3 = DIS(fpol3.filename);
//
//      // создаем объект класса ObjectInputStream для чтения
//      File fvar3 = new File(fpol3.filename.getParentFile(), fpol3.filename.getName()+"_var");
//      DataInputStream r_var3 = DIS(fvar3);
//
//      int ring3 = r_var3.readInt();
//      r_var3.readInt();
//      r_var3.readLong();
//
//      //r_var.readLong();
//
//      //String polString = "";
//      //long readbytes = 0;
//
//      numBytes = r_var3.readLong();
//      b = new byte[(int)numBytes];
//      r_pol3.read(b);
//      pathPol = (Polynom)FileUtils.fromByteArr(b);
//
//      polString  = Polynom.toStringforFORM(pathPol, getRing(ring3));
//      bw.write(polString); bw.flush();
//      polString = "";
//
//      filelength = fpol3.filename.length();
//
//      sumBytes = numBytes;
//      while(sumBytes<filelength){
//        numBytes = r_var3.readLong()-sumBytes;
//        b = new byte[(int)numBytes];
//        r_pol3.read(b);
//        pathPol = (Polynom)FileUtils.fromByteArr(b);
//        if(pathPol.signum()==1) polString=" +";
//        else polString=" -";
//        polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring3));
//        bw.write(polString); bw.flush();
//        polString = "";
//        sumBytes = sumBytes+numBytes;
//      }
//
//      //четвертый полином
//      bw.write(";\n\n id f4 = ");bw.flush();
//
//
//      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
//      DataInputStream r_pol4 = DIS(fpol4.filename);
//
//      // создаем объект класса ObjectInputStream для чтения
//      File fvar4 = new File(fpol4.filename.getParentFile(), fpol4.filename.getName()+"_var");
//      DataInputStream r_var4 = DIS(fvar4);
//
//      int ring4 = r_var4.readInt();
//      r_var4.readInt();
//      r_var4.readLong();
//
//      //r_var.readLong();
//
//      //String polString = "";
//      //long readbytes = 0;
//
//      numBytes = r_var4.readLong();
//      b = new byte[(int)numBytes];
//      r_pol4.read(b);
//      pathPol = (Polynom)FileUtils.fromByteArr(b);
//
//      polString  = Polynom.toStringforFORM(pathPol, getRing(ring4));
//      bw.write(polString); bw.flush();
//      polString = "";
//
//      filelength = fpol4.filename.length();
//
//      sumBytes = numBytes;
//      while(sumBytes<filelength){
//        numBytes = r_var4.readLong()-sumBytes;
//        b = new byte[(int)numBytes];
//        r_pol4.read(b);
//        pathPol = (Polynom)FileUtils.fromByteArr(b);
//        if(pathPol.signum()==1) polString=" +";
//        else polString=" -";
//        polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring4));
//        bw.write(polString); bw.flush();
//        polString = "";
//        sumBytes = sumBytes+numBytes;
//      }
//
//      //пятый полином
//      bw.write(";\n\n id f5 = ");bw.flush();
//
//
//      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
//      DataInputStream r_pol5 = DIS(fpol5.filename);
//
//      // создаем объект класса ObjectInputStream для чтения
//      File fvar5 = new File(fpol5.filename.getParentFile(), fpol5.filename.getName()+"_var");
//      DataInputStream r_var5 = DIS(fvar5);
//
//      int ring5 = r_var5.readInt();
//      r_var5.readInt();
//      r_var5.readLong();
//
//      //r_var.readLong();
//
//      //String polString = "";
//      //long readbytes = 0;
//
//      numBytes = r_var5.readLong();
//      b = new byte[(int)numBytes];
//      r_pol5.read(b);
//      pathPol = (Polynom)FileUtils.fromByteArr(b);
//
//      polString  = Polynom.toStringforFORM(pathPol, getRing(ring5));
//      bw.write(polString); bw.flush();
//      polString = "";
//
//      filelength = fpol5.filename.length();
//
//      sumBytes = numBytes;
//      while(sumBytes<filelength){
//        numBytes = r_var5.readLong()-sumBytes;
//        b = new byte[(int)numBytes];
//        r_pol5.read(b);
//        pathPol = (Polynom)FileUtils.fromByteArr(b);
//        if(pathPol.signum()==1) polString=" +";
//        else polString=" -";
//        polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring5));
//        bw.write(polString); bw.flush();
//        polString = "";
//        sumBytes = sumBytes+numBytes;
//      }
//
//
//      //шестой полином
//      bw.write(";\n\n id f6 = ");bw.flush();
//
//
//      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
//      DataInputStream r_pol6 = DIS(fpol6.filename);
//
//      // создаем объект класса ObjectInputStream для чтения
//      File fvar6 = new File(fpol6.filename.getParentFile(), fpol6.filename.getName()+"_var");
//      DataInputStream r_var6 = DIS(fvar6);
//
//      int ring6 = r_var6.readInt();
//      r_var6.readInt();
//      r_var6.readLong();
//
//      //r_var.readLong();
//
//      //String polString = "";
//      //long readbytes = 0;
//
//      numBytes = r_var6.readLong();
//      b = new byte[(int)numBytes];
//      r_pol6.read(b);
//      pathPol = (Polynom)FileUtils.fromByteArr(b);
//
//      polString  = Polynom.toStringforFORM(pathPol, getRing(ring2));
//      bw.write(polString); bw.flush();
//      polString = "";
//
//      filelength = fpol6.filename.length();
//
//      sumBytes = numBytes;
//      while(sumBytes<filelength){
//        numBytes = r_var6.readLong()-sumBytes;
//        b = new byte[(int)numBytes];
//        r_pol6.read(b);
//        pathPol = (Polynom)FileUtils.fromByteArr(b);
//        if(pathPol.signum()==1) polString=" +";
//        else polString=" -";
//        polString  = polString+Polynom.toStringforFORM(pathPol, getRing(ring6));
//        bw.write(polString); bw.flush();
//        polString = "";
//        sumBytes = sumBytes+numBytes;
//      }
//
//
//      bw.write(";\n \n .end");
//
//      bw.close();
//      r_var.close();
//    }

    /**
     * Сложение двух файловых полиномов
     * @param dir1 - директория первого полинома
     * @param dir2 - директория второго полинома
     * @param dirSumResult - директория, в которую будет записан результат суммы двух полиномов
     * @return
     */
    public static FPolynom add(File dir1, File dir2, File dir_result){
      FPolynom polresult = new FPolynom(dir_result);
      try{

      if(dir2==null){ // если второй полином равен нулю, то создаем копию первого
        FileUtils.copyFile(dir1, dir_result);
        return new FPolynom(dir_result);
      }

      if(dir1==null){ // если первый полином равен нулю, то создаем копию второго
        FileUtils.copyFile(dir2, dir_result);
        return new FPolynom(dir_result);
      }

      //создаем объект класса ObjectOutputStream для записи результата суммы двух полиномов
      DataOutputStream rw_pol = DOS(dir_result);

      //создаем объект класса ObjectOutputStream для записи
      File fvar = new File(dir_result.getParentFile(), dir_result.getName()+"_var");
      RandomAccessFile rw_var = new RandomAccessFile(fvar, "rw");

      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
      DataInputStream r_pol1 = DIS(dir1);

      // создаем объект класса ObjectInputStream для чтения
      File fvar1 = new File(dir1+"_var");
      DataInputStream r_var1 = DIS(fvar1);

      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
      DataInputStream r_pol2 = DIS(dir2);

      // создаем объект класса ObjectInputStream для чтения
      File fvar2 = new File(dir2+"_var");
      DataInputStream r_var2 = DIS(fvar2);

      //ByteArrayOutputStream baos = new ByteArrayOutputStream();
      //DataOutputStream dos = new DataOutputStream(baos);

      // инициализация переменных, необходимых входе операции сложения

      Element itsCoeffOne1 = toElement(r_var1.readInt());
      Element itsCoeffOne2 = toElement(r_var2.readInt());

      // количество переменных в полиномах
      int var1 = r_var1.readInt();
      int var2 = r_var2.readInt();
      int var = Math.max(var1, var2);
      // массив для временного хранения степеней одного монома первого полинома
      int pow1[] = new int[var];
      // массив для временного хранения степеней одного монома второго полинома
      int pow2[] = new int[var];

      // количество коэффициентов в полиномах
      long coeffs_length1 = r_var1.readLong();
      long coeffs_length2 = r_var2.readLong();

      rw_var.writeInt(toInt(itsCoeffOne1));
      rw_var.writeInt(var);
      rw_var.writeLong(coeffs_length1+coeffs_length1);

      // пременная для временного хранения коэффициента одного монома первого полинома
      Element cfs1 = Polynom.polynom_zero(itsCoeffOne1);
      // переменная для временного хранения коэффициента одного монома второго полинома
      Element cfs2 = Polynom.polynom_zero(itsCoeffOne1);

      // считывание из файловых полиномов первых частей полиномов
      long numBytes1 = r_var1.readLong();
      byte[] b1 = new byte[(int)numBytes1];
      r_pol1.read(b1);


      Polynom polynom1 = (Polynom)FileUtils.fromByteArr(b1);
      cfs1 = polynom1.coeffs[0];
      System.arraycopy(polynom1.powers, 0, pow1, 0, var1);
      //for(int j=0; j<var1; j++) pow1[j] = r_pol1.readInt();

      long numBytes2 = r_var2.readLong();
      byte[] b2 = new byte[(int)numBytes2];
      for(int k=0; k<numBytes2; k++) b2[k] = r_pol2.readByte();

      long sumBytes1 = numBytes1;
      long sumBytes2 = numBytes2;
      long sumBytes = 0;

      Polynom polynom2 = (Polynom)FileUtils.fromByteArr(b2);
      cfs2 = polynom2.coeffs[0];
      System.arraycopy(polynom2.powers, 0, pow2, 0, var2);
      //for(int k=0; k<var2; k++) pow2[k] = r_pol2.readInt();

      // переменные write1, write2 для подсчета числа записанных мономов,
      // из первого и второго полинома соответственно
      int write1=0, write2=0;
      int numpows1 = var1, numcfs1 = 0;
      int numpows2 = var2, numcfs2 = 0;

      /* 1) Вступление (запись мономов из полинома с большим числом переменных,
       до тех пор пока число переменных с ненулевыми степенями не подравняется) */

      Ring ring = new Ring(itsCoeffOne.numbElementType(), 1);

      Polynom polbuf = Polynom.polynom_zero(itsCoeffOne);

      met1: if (var1>var2){
        for (int i=var1-1; i>var2-1; i--){
          while (pow1[i]!=0){

            polbuf = polbuf.add(new Polynom(pow1, new Element[]{cfs1}), true, ring);
            byte[] b = FileUtils.toByteArray(polbuf);
            write1++; numcfs1++;

            if(b.length>=quantum && write1<coeffs_length1){
              rw_pol.write(b);// пишем часть полинома в байтовый поток
              sumBytes += b.length;
              rw_var.writeLong(sumBytes); // пишем в файл количество байт, которое понадобилось для коэффициента
              polbuf = Polynom.polynom_zero(itsCoeffOne);
            }

            //for(int j=0; j<var1; j++){ rw_pol.writeInt(pow1[j]);}
            if(/*b.length>=numBytes1*/  polynom1.coeffs.length==numcfs1){

              numBytes1 = r_var1.readLong()-sumBytes1;
              sumBytes1 += numBytes1;
              b = new byte[(int)numBytes1];
              r_pol1.read(b);

              polynom1 = (Polynom)FileUtils.fromByteArr(b);
              //cfs1 = polynom1.coeffs[0];
              //System.arraycopy(polynom1.powers, 0, pow1, 0, var1);
              numcfs1=0; numpows1=0;
            }

            cfs1 = polynom1.coeffs[numcfs1];
            System.arraycopy(polynom1.powers, numpows1, pow1, 0, var1);

            numpows1 = numpows1+var1;

            if(write1>=coeffs_length1){break met1;}
         }
        }
      }


      met1: if (var2>var1){
        for (int i=var2-1; i>var1-1; i--){
          while (pow2[i]!=0){

            polbuf = polbuf.add(new Polynom(pow2, new Element[]{cfs2}), true, ring);
            byte[] b = FileUtils.toByteArray(polbuf);
            write1++; numcfs2++;

            if(b.length>=quantum && write2<coeffs_length2){
              rw_pol.write(b);// пишем часть полинома в байтовый поток
              sumBytes += b.length;
              rw_var.writeLong(sumBytes); // пишем в файл количество байт, которое понадобилось для коэффициента
              polbuf = Polynom.polynom_zero(itsCoeffOne);
            }

            if(polynom2.coeffs.length==numcfs2){

              numBytes2 = r_var1.readLong()-sumBytes2;
              sumBytes2 += numBytes1;
              b = new byte[(int)numBytes2];
              r_pol2.read(b);

              polynom2 = (Polynom)FileUtils.fromByteArr(b);
              //cfs2 = polynom2.coeffs[0];
              //System.arraycopy(polynom2.powers, 0, pow2, 0, var2);
              numcfs2=0; numpows2=0;
            }

            cfs2 = polynom2.coeffs[numcfs2];
            System.arraycopy(polynom2.powers, numpows2, pow2, 0, var2);

            numpows2 = numpows2+var2;

            if(write2>=coeffs_length2){break met1;}
         }
        }
      }

      int num_cfs_zero = 0; // количество совпавших коэффициентов, т.е. дающих в сумме 0
      int last = Math.min(var1, var2);
      boolean f=true; // флаг, чтобы знать на выходе из цикла, был ли записан считанный моном, false - не был записан, true - был записан

      /* 2) Общая часть (сравнение, сложение и запись посортированных мономов
         выход из цикла, как только один из файловых полиномов закончиться) */
      while ((write1<coeffs_length1)&&(write2<coeffs_length2)) {

      met:for (int i = last-1; i >= 0; i--) {
            if (pow1[i]+pow2[i]==0 && i!=0) {last--; continue met;} //отбрасываем нулевые степени при сравнении мономов

            byte[] b = FileUtils.toByteArray(polbuf);

            if(b.length>=quantum && !f){
              rw_pol.write(b);// пишем часть полинома в байтовый поток
              sumBytes += b.length;
              rw_var.writeLong(sumBytes); // пишем в файл количество байт, которое понадобилось для коэффициента
              polbuf = Polynom.polynom_zero(itsCoeffOne);
              f = true;
            }

            if(pow1[i]==pow2[i] && i==0){ // когда степени равны
              Element addcfs = cfs1.add(cfs2, ring);
              write1++; write2++;
              numcfs1++; numcfs2++;
              if(addcfs!=(Polynom.polynom_zero(itsCoeffOne))){ // если сумма коэффициентов не равна 0
                polbuf = polbuf.add(new Polynom(pow1, new Element[]{addcfs}), true, ring);
                num_cfs_zero++;
                //for(int j=0; j<var; j++){rw_pol.writeInt(pow1[j]);}
              }else{num_cfs_zero=num_cfs_zero+2;}
              // чтение следующих мономов из файла

              if(polynom1.coeffs.length==numcfs1 && write1<coeffs_length1){
                numBytes1 = r_var1.readLong()-sumBytes1;
                //System.out.println("numBytes1  = =  "+numBytes1);
                sumBytes1 += numBytes1;
                //System.out.println("numBytes1  = =  "+sumBytes1);
                b1 = new byte[(int)numBytes1];
                r_pol1.read(b1);
                polynom1 = (Polynom)FileUtils.fromByteArr(b1);
                //cfs1 = polynom1.coeffs[0];
                //System.arraycopy(polynom1.powers, 0, pow1, 0, var1);
                numcfs1=0; numpows1=0;
              }
              if(polynom2.coeffs.length==numcfs2 && write2<coeffs_length2){
                numBytes2 = r_var2.readLong()-sumBytes2;
                sumBytes2 += numBytes2;
                b2 = new byte[(int)numBytes2];
                r_pol2.read(b2);
                polynom2 = (Polynom)FileUtils.fromByteArr(b2);
                //cfs1 = polynom1.coeffs[0];
                //System.arraycopy(polynom1.powers, 0, pow1, 0, var1);
                numcfs2=0; numpows2=0;
              }

              if(polynom1.coeffs.length>numcfs1){
                cfs1 = polynom1.coeffs[numcfs1];
                System.arraycopy(polynom1.powers, numpows1, pow1, 0, var1);
                numpows1 = numpows1+var1;
              }

              if(polynom2.coeffs.length>numcfs2){
                cfs2 = polynom2.coeffs[numcfs2];
                System.arraycopy(polynom2.powers, numpows2, pow2, 0, var2);
                numpows2 = numpows2+var2;
              }
              f = false; break;
          }else
            if (pow1[i] > pow2[i]) { // когда степени первого монома больше второго

            polbuf = polbuf.add(new Polynom(pow1, new Element[]{cfs1}), true, ring);
            numcfs1++;
            //sumBytes += b.length;
            //rw_var.writeLong(sumBytes); // пишем в файл количество байт, которое понадобилось для коэффициента

            write1++;
            //for(int j=0; j<var; j++){rw_pol.writeInt(pow1[j]);}
            if(polynom1.coeffs.length==numcfs1 && write1<coeffs_length1){
              numBytes1 = r_var1.readLong()-sumBytes1;
              sumBytes1 += numBytes1;
              b1 = new byte[(int)numBytes1];
              r_pol1.read(b1);
              polynom1 = (Polynom)FileUtils.fromByteArr(b1);
              numcfs1=0; numpows1=0;
            }

            if(polynom1.coeffs.length>numcfs1){
              cfs1 = polynom1.coeffs[numcfs1];
              System.arraycopy(polynom1.powers, numpows1, pow1, 0, var1);
              numpows1 = numpows1+var1;
            }

            f = false; break;
          } else
          if (pow1[i] < pow2[i]) { // когда степени первого монома меньше второго

            polbuf = polbuf.add(new Polynom(pow2, new Element[]{cfs2}), true, ring);
            numcfs2++;
            //byte[] b = FileUtils.toByteArray(monom2);
            //rw_pol.write(b);// пишем коэффициент в поток
            //sumBytes += b.length;
            //rw_var.writeLong(sumBytes); // пишем в файл количество байт, которое понадобилось для коэффициента

            write2++;
            //for(int j=0; j<var; j++){rw_pol.writeInt(pow2[j]);}
            if(polynom2.coeffs.length==numcfs2 && write2<coeffs_length2){
              numBytes2 = r_var2.readLong()-sumBytes2;
              sumBytes2 += numBytes2;
              b2 = new byte[(int)numBytes2];
              r_pol2.read(b2);
              polynom2 = (Polynom)FileUtils.fromByteArr(b2);
              numcfs2=0; numpows2=0;
            }

            if(polynom2.coeffs.length>numcfs2){
              cfs2 = polynom2.coeffs[numcfs2];
              System.arraycopy(polynom2.powers, numpows2, pow2, 0, var2);
              numpows2 = numpows2+var2;
            }
            f = false; break;
          }
        }
      }

      /* 3) Заключение (дозаписываем оставшиеся мономы, одного из полиномов)*/
     while(write1<coeffs_length1){
        if(f){
          if(polynom1.coeffs.length==numcfs1){
            numBytes1 = r_var1.readLong()-sumBytes1;
            sumBytes1 += numBytes1;
            b1 = new byte[(int)numBytes1];
            //for(int k=0; k<numBytes1; k++) b1[k] = r_pol1.readByte();
            r_pol1.read(b1);
            polynom1 = (Polynom)FileUtils.fromByteArr(b1);
            numcfs1=0; numpows1=0;
          }
          cfs1 = polynom1.coeffs[numcfs1];
          System.arraycopy(polynom1.powers, numpows1, pow1, 0, var1);
          numpows1 = numpows1+var1;
        }

        polbuf = polbuf.add(new Polynom(pow1, new Element[]{cfs1}), true, ring);
        numcfs1++;
        write1++;
        f = true;
        byte[] b = FileUtils.toByteArray(polbuf);

        if(b.length>=quantum){
          rw_pol.write(b);// пишем monom в поток
          sumBytes += b.length;
          rw_var.writeLong(sumBytes); // пишем в файл количество байт, которое понадобилось для коэффициента
          polbuf = Polynom.polynom_zero(itsCoeffOne);
        }

      }

      while(write2<coeffs_length2){
        if(f){
          if(polynom2.coeffs.length==numcfs2){
            numBytes2 = r_var2.readLong()-sumBytes2;
            sumBytes2 += numBytes2;
            b2 = new byte[(int)numBytes2];
            //for(int k=0; k<numBytes1; k++) b1[k] = r_pol1.readByte();
            r_pol2.read(b2);
            polynom2 = (Polynom)FileUtils.fromByteArr(b2);
            numcfs2=0; numpows2=0;
          }
          cfs2 = polynom2.coeffs[numcfs2];
          System.arraycopy(polynom2.powers, numpows2, pow2, 0, var2);
          numpows2 = numpows2+var2;
        }

        polbuf = polbuf.add(new Polynom(pow2, new Element[]{cfs2}), true, ring);
        numcfs2++;
        write2++;
        f = true;
        byte[] b = FileUtils.toByteArray(polbuf);

        if(b.length>=quantum){
          rw_pol.write(b);// пишем monom в поток
          sumBytes += b.length;
          rw_var.writeLong(sumBytes); // пишем в файл количество байт, которое понадобилось для коэффициента
          polbuf = Polynom.polynom_zero(itsCoeffOne);
        }

      }

      if(polbuf!=Polynom.polynom_zero(itsCoeffOne)){
        byte[] b = FileUtils.toByteArray(polbuf);
        rw_pol.write(b);// пишем часть полинома в байтовый поток
        sumBytes += b.length;
        rw_var.writeLong(sumBytes);
      }

      rw_var.seek(8);
      rw_var.writeLong(coeffs_length1+coeffs_length2-num_cfs_zero); // количество мономов в полиноме
      //rw_var.writeLong(coeffs_length1+coeffs_length2-num_cfs_zero);
      //rw_var.writeInt(var);
      //rw_var.writeObject(itsCoeffOne1);

      // закрываем файлы

      r_pol1.close();
      r_pol2.close();
      r_var1.close();
      r_var2.close();
      rw_pol.close();
      rw_var.close();

      // если сумма полиномов равна нулю,
      // т.е. файл, содержащий коэффициенты равен нулю
      // то каталог, содержащий файловый полином удаляется
      if(dir_result.length()==0){
        FileUtils.delete(dir_result);
        polresult = ZERO;
      }

      }catch(Exception e){
        e.printStackTrace();
      }
      return polresult;
    }



    /**
     *
     * @param dir1
     * @param dir2
     * @param dir_result
     * @param mod
     * @return
     */
   public static FPolynom add(String dir1, String dir2, File dir_result){
      FPolynom polresult = new FPolynom(dir_result);
      try{

      if(dir2==null){ // если второй полином равен нулю, то создаем копию первого
        FileUtils.copyFile(dir1, dir_result);
        return new FPolynom(dir_result);
      }

      if(dir1==null){ // если первый полином равен нулю, то создаем копию второго
        FileUtils.copyFile(dir2, dir_result);
        return new FPolynom(dir_result);
      }

      //создаем объект класса ObjectOutputStream для записи результата суммы двух полиномов
      DataOutputStream rw_pol = DOS(dir_result);

      //создаем объект класса ObjectOutputStream для записи
      File fvar = new File(dir_result.getParentFile(), dir_result.getName()+"_var");
      RandomAccessFile rw_var = new RandomAccessFile(fvar, "rw");

      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
      DataInputStream r_pol1 = DIS(dir1);

      // создаем объект класса ObjectInputStream для чтения
      File fvar1 = new File(dir1+"_var");
      DataInputStream r_var1 = DIS(fvar1);

      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
      DataInputStream r_pol2 = DIS(dir2);

      // создаем объект класса ObjectInputStream для чтения
      File fvar2 = new File(dir2+"_var");
      DataInputStream r_var2 = DIS(fvar2);

      //ByteArrayOutputStream baos = new ByteArrayOutputStream();
      //DataOutputStream dos = new DataOutputStream(baos);

      // инициализация переменных, необходимых входе операции сложения

      Element itsCoeffOne1 = toElement(r_var1.readInt());
      Element itsCoeffOne2 = toElement(r_var2.readInt());

      // количество переменных в полиномах
      int var1 = r_var1.readInt();
      int var2 = r_var2.readInt();
      int var = Math.max(var1, var2);
      // массив для временного хранения степеней одного монома первого полинома
      int pow1[] = new int[var];
      // массив для временного хранения степеней одного монома второго полинома
      int pow2[] = new int[var];

      // количество коэффициентов в полиномах
      long coeffs_length1 = r_var1.readLong();
      long coeffs_length2 = r_var2.readLong();

      rw_var.writeInt(toInt(itsCoeffOne1));
      rw_var.writeInt(var);
      rw_var.writeLong(coeffs_length1+coeffs_length1);

      // пременная для временного хранения коэффициента одного монома первого полинома
      Element cfs1 = Polynom.polynom_zero(itsCoeffOne);
      // переменная для временного хранения коэффициента одного монома второго полинома
      Element cfs2 = Polynom.polynom_zero(itsCoeffOne);

      // считывание из файловых полиномов первых частей полиномов
      long numBytes1 = r_var1.readLong();
      byte[] b1 = new byte[(int)numBytes1];
      r_pol1.read(b1);


      Polynom polynom1 = (Polynom)FileUtils.fromByteArr(b1);
      cfs1 = polynom1.coeffs[0];
      System.arraycopy(polynom1.powers, 0, pow1, 0, var1);
      //for(int j=0; j<var1; j++) pow1[j] = r_pol1.readInt();

      long numBytes2 = r_var2.readLong();
      byte[] b2 = new byte[(int)numBytes2];
      for(int k=0; k<numBytes2; k++) b2[k] = r_pol2.readByte();

      long sumBytes1 = numBytes1;
      long sumBytes2 = numBytes2;
      long sumBytes = 0;

      Polynom polynom2 = (Polynom)FileUtils.fromByteArr(b2);
      cfs2 = polynom2.coeffs[0];
      System.arraycopy(polynom2.powers, 0, pow2, 0, var2);
      //for(int k=0; k<var2; k++) pow2[k] = r_pol2.readInt();

      // переменные write1, write2 для подсчета числа записанных мономов,
      // из первого и второго полинома соответственно
      int write1=0, write2=0;
      int numpows1 = var1, numcfs1 = 0;
      int numpows2 = var2, numcfs2 = 0;

      /* 1) Вступление (запись мономов из полинома с большим числом переменных,
       до тех пор пока число переменных с ненулевыми степенями не подравняется) */

      Polynom polbuf = Polynom.polynom_zero(itsCoeffOne);
      Ring ring = new Ring(itsCoeffOne.numbElementType(), 1);

      met1: if (var1>var2){
        for (int i=var1-1; i>var2-1; i--){
          while (pow1[i]!=0){

            polbuf = polbuf.add(new Polynom(pow1, new Element[]{cfs1}), true, ring);
            byte[] b = FileUtils.toByteArray(polbuf);
            write1++; numcfs1++;

            if(b.length>=quantum && write1<coeffs_length1){
              rw_pol.write(b);// пишем часть полинома в байтовый поток
              sumBytes += b.length;
              rw_var.writeLong(sumBytes); // пишем в файл количество байт, которое понадобилось для коэффициента
              polbuf = Polynom.polynom_zero(itsCoeffOne);
            }

            //for(int j=0; j<var1; j++){ rw_pol.writeInt(pow1[j]);}
            if(/*b.length>=numBytes1*/  polynom1.coeffs.length==numcfs1){

              numBytes1 = r_var1.readLong()-sumBytes1;
              sumBytes1 += numBytes1;
              b = new byte[(int)numBytes1];
              r_pol1.read(b);

              polynom1 = (Polynom)FileUtils.fromByteArr(b);
              //cfs1 = polynom1.coeffs[0];
              //System.arraycopy(polynom1.powers, 0, pow1, 0, var1);
              numcfs1=0; numpows1=0;
            }

            cfs1 = polynom1.coeffs[numcfs1];
            System.arraycopy(polynom1.powers, numpows1, pow1, 0, var1);

            numpows1 = numpows1+var1;

            if(write1>=coeffs_length1){break met1;}
         }
        }
      }


      met1: if (var2>var1){
        for (int i=var2-1; i>var1-1; i--){
          while (pow2[i]!=0){

            polbuf = polbuf.add(new Polynom(pow2, new Element[]{cfs2}), true, ring);
            byte[] b = FileUtils.toByteArray(polbuf);
            write1++; numcfs2++;

            if(b.length>=quantum && write2<coeffs_length2){
              rw_pol.write(b);// пишем часть полинома в байтовый поток
              sumBytes += b.length;
              rw_var.writeLong(sumBytes); // пишем в файл количество байт, которое понадобилось для коэффициента
              polbuf = Polynom.polynom_zero(itsCoeffOne);
            }

            if(polynom2.coeffs.length==numcfs2){

              numBytes2 = r_var1.readLong()-sumBytes2;
              sumBytes2 += numBytes1;
              b = new byte[(int)numBytes2];
              r_pol2.read(b);

              polynom2 = (Polynom)FileUtils.fromByteArr(b);
              //cfs2 = polynom2.coeffs[0];
              //System.arraycopy(polynom2.powers, 0, pow2, 0, var2);
              numcfs2=0; numpows2=0;
            }

            cfs2 = polynom2.coeffs[numcfs2];
            System.arraycopy(polynom2.powers, numpows2, pow2, 0, var2);

            numpows2 = numpows2+var2;

            if(write2>=coeffs_length2){break met1;}
         }
        }
      }

      int num_cfs_zero = 0; // количество совпавших коэффициентов, т.е. дающих в сумме 0
      int last = Math.min(var1, var2);
      boolean f=true; // флаг, чтобы знать на выходе из цикла, был ли записан считанный моном

      /* 2) Общая часть (сравнение, сложение и запись посортированных мономов
         выход из цикла, как только один из файловых полиномов закончиться) */
      while ((write1<coeffs_length1)&&(write2<coeffs_length2)) {

      met:for (int i = last-1; i >= 0; i--) {
            if (pow1[i]+pow2[i]==0 && i!=0) {last--; continue met;} //отбрасываем нулевые степени при сравнении мономов

            byte[] b = FileUtils.toByteArray(polbuf);

            if(b.length>=quantum && !f){
              rw_pol.write(b);// пишем часть полинома в байтовый поток
              sumBytes += b.length;
              rw_var.writeLong(sumBytes); // пишем в файл количество байт, которое понадобилось для коэффициента
              polbuf = Polynom.polynom_zero(itsCoeffOne);
              f = true;
            }


            if(pow1[i]==pow2[i] && i==0){ // когда степени равны
            Element addcfs = cfs1.add(cfs2, ring); write1++; write2++; numcfs1++; numcfs2++;
            if(addcfs!=(Polynom.polynom_zero(itsCoeffOne))){ // если сумма коэффициентов не равна 0

              polbuf = polbuf.add(new Polynom(pow1, new Element[]{addcfs}), true, ring);

              num_cfs_zero++;
              //for(int j=0; j<var; j++){rw_pol.writeInt(pow1[j]);}
            }else{num_cfs_zero=num_cfs_zero+2;}
            // чтение следующих мономов из файла

            if(polynom1.coeffs.length==numcfs1 && write1<coeffs_length1){
              numBytes1 = r_var1.readLong()-sumBytes1;
              //System.out.println("numBytes1  = =  "+numBytes1);
              sumBytes1 += numBytes1;
              //System.out.println("numBytes1  = =  "+sumBytes1);
              b1 = new byte[(int)numBytes1];
              r_pol1.read(b1);
              polynom1 = (Polynom)FileUtils.fromByteArr(b1);
              //cfs1 = polynom1.coeffs[0];
              //System.arraycopy(polynom1.powers, 0, pow1, 0, var1);
              numcfs1=0; numpows1=0;
            }
            if(polynom2.coeffs.length==numcfs2 && write2<coeffs_length2){
              numBytes2 = r_var2.readLong()-sumBytes2;
              sumBytes2 += numBytes2;
              b2 = new byte[(int)numBytes2];
              r_pol2.read(b2);
              polynom2 = (Polynom)FileUtils.fromByteArr(b2);
              //cfs1 = polynom1.coeffs[0];
              //System.arraycopy(polynom1.powers, 0, pow1, 0, var1);
              numcfs2=0; numpows2=0;
            }

            if(polynom1.coeffs.length>numcfs1){
              cfs1 = polynom1.coeffs[numcfs1];
              System.arraycopy(polynom1.powers, numpows1, pow1, 0, var1);
              numpows1 = numpows1+var1;
            }

            if(polynom2.coeffs.length>numcfs2){
              cfs2 = polynom2.coeffs[numcfs2];
              System.arraycopy(polynom2.powers, numpows2, pow2, 0, var2);
              numpows2 = numpows2+var2;
            }
            f = false; break;
          }else
            if (pow1[i] > pow2[i]) { // когда степени первого монома больше второго

            polbuf = polbuf.add(new Polynom(pow1, new Element[]{cfs1}), true, ring);
            numcfs1++;
            //sumBytes += b.length;
            //rw_var.writeLong(sumBytes); // пишем в файл количество байт, которое понадобилось для коэффициента

            write1++;
            //for(int j=0; j<var; j++){rw_pol.writeInt(pow1[j]);}
            if(polynom1.coeffs.length==numcfs1 && write1<coeffs_length1){
              numBytes1 = r_var1.readLong()-sumBytes1;
              sumBytes1 += numBytes1;
              b1 = new byte[(int)numBytes1];
              r_pol1.read(b1);
              polynom1 = (Polynom)FileUtils.fromByteArr(b1);
              numcfs1=0; numpows1=0;
            }

            if(polynom1.coeffs.length>numcfs1){
              cfs1 = polynom1.coeffs[numcfs1];
              System.arraycopy(polynom1.powers, numpows1, pow1, 0, var1);
              numpows1 = numpows1+var1;
            }

            f = false; break;
          } else
          if (pow1[i] < pow2[i]) { // когда степени первого монома меньше второго

            polbuf = polbuf.add(new Polynom(pow2, new Element[]{cfs2}), true, ring);
            numcfs2++;
            //byte[] b = FileUtils.toByteArray(monom2);
            //rw_pol.write(b);// пишем коэффициент в поток
            //sumBytes += b.length;
            //rw_var.writeLong(sumBytes); // пишем в файл количество байт, которое понадобилось для коэффициента

            write2++;
            //for(int j=0; j<var; j++){rw_pol.writeInt(pow2[j]);}
            if(polynom2.coeffs.length==numcfs2 && write2<coeffs_length2){
              numBytes2 = r_var2.readLong()-sumBytes2;
              sumBytes2 += numBytes2;
              b2 = new byte[(int)numBytes2];
              r_pol2.read(b2);
              polynom2 = (Polynom)FileUtils.fromByteArr(b2);
              numcfs2=0; numpows2=0;
            }

            if(polynom2.coeffs.length>numcfs2){
              cfs2 = polynom2.coeffs[numcfs2];
              System.arraycopy(polynom2.powers, numpows2, pow2, 0, var2);
              numpows2 = numpows2+var2;
            }
            f = false; break;
          }


        }
      }

     /* 3) Заключение (дозаписываем оставшиеся мономы, одного из полиномов)*/
     while(write1<coeffs_length1){
        if(f){
          if(polynom1.coeffs.length==numcfs1){
            numBytes1 = r_var1.readLong()-sumBytes1;
            sumBytes1 += numBytes1;
            b1 = new byte[(int)numBytes1];
            //for(int k=0; k<numBytes1; k++) b1[k] = r_pol1.readByte();
            r_pol1.read(b1);
            polynom1 = (Polynom)FileUtils.fromByteArr(b1);
            numcfs1=0; numpows1=0;
          }
          cfs1 = polynom1.coeffs[numcfs1];
          System.arraycopy(polynom1.powers, numpows1, pow1, 0, var1);
          numpows1 = numpows1+var1;
        }

        polbuf = polbuf.add(new Polynom(pow1, new Element[]{cfs1}), true, ring);
        numcfs1++;
        write1++;
        f = true;
        byte[] b = FileUtils.toByteArray(polbuf);

        if(b.length>=quantum){
          rw_pol.write(b);// пишем monom в поток
          sumBytes += b.length;
          rw_var.writeLong(sumBytes); // пишем в файл количество байт, которое понадобилось для коэффициента
          polbuf = Polynom.polynom_zero(itsCoeffOne);
        }

      }

      while(write2<coeffs_length2){
        if(f){
          if(polynom2.coeffs.length==numcfs2){
            numBytes2 = r_var2.readLong()-sumBytes2;
            sumBytes2 += numBytes2;
            b2 = new byte[(int)numBytes2];
            //for(int k=0; k<numBytes1; k++) b1[k] = r_pol1.readByte();
            r_pol2.read(b2);
            polynom2 = (Polynom)FileUtils.fromByteArr(b2);
            numcfs2=0; numpows2=0;
          }
          cfs2 = polynom2.coeffs[numcfs2];
          System.arraycopy(polynom2.powers, numpows2, pow2, 0, var2);
          numpows2 = numpows2+var2;
        }

        polbuf = polbuf.add(new Polynom(pow2, new Element[]{cfs2}), true, ring);
        numcfs2++;
        write2++;
        f = true;
        byte[] b = FileUtils.toByteArray(polbuf);

        if(b.length>=quantum){
          rw_pol.write(b);// пишем monom в поток
          sumBytes += b.length;
          rw_var.writeLong(sumBytes); // пишем в файл количество байт, которое понадобилось для коэффициента
          polbuf = Polynom.polynom_zero(itsCoeffOne);
        }

      }

      if(polbuf!=Polynom.polynom_zero(itsCoeffOne)){
        byte[] b = FileUtils.toByteArray(polbuf);
        rw_pol.write(b);// пишем часть полинома в байтовый поток
        sumBytes += b.length;
        rw_var.writeLong(sumBytes);
      }

      rw_var.seek(8);
      rw_var.writeLong(coeffs_length1+coeffs_length2-num_cfs_zero); // количество мономов в полиноме
      //rw_var.writeLong(coeffs_length1+coeffs_length2-num_cfs_zero);
      //rw_var.writeInt(var);
      //rw_var.writeObject(itsCoeffOne1);

      // закрываем файлы

      r_pol1.close();
      r_pol2.close();
      r_var1.close();
      r_var2.close();
      rw_pol.close();
      rw_var.close();

      // если сумма полиномов равна нулю,
      // т.е. файл, содержащий коэффициенты равен нулю
      // то каталог, содержащий файловый полином удаляется
      if(dir_result.length()==0){
        FileUtils.delete(dir_result);
        polresult = ZERO;
      }

      }catch(Exception e){
        e.printStackTrace();
      }
      return polresult;
    }

    /**
     * разность двух файловых полиномов
     * @param dir1
     * @param dir2
     * @param dir_result
     * @param mod
     * @return
     */
     private FPolynom subtract(File dir1, File dir2, File dir_result){
      FPolynom polresult = new FPolynom(dir_result);
      try{

      if(dir2==null){ // если второй полином равен нулю, то создаем копию первого
        FileUtils.copyFile(dir1, dir_result);
        return new FPolynom(dir_result);
      }

      if(dir1==null){ // если первый полином равен нулю, то создаем копию второго
        FileUtils.copyFile(dir2, dir_result);
        return new FPolynom(dir_result);
      }

      //создаем объект класса ObjectOutputStream для записи результата суммы двух полиномов
      DataOutputStream rw_pol = DOS(dir_result);

      //создаем объект класса ObjectOutputStream для записи
      File fvar = new File(dir_result.getParentFile(), dir_result.getName()+"_var");
      RandomAccessFile rw_var = new RandomAccessFile(fvar, "rw");

      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
      DataInputStream r_pol1 = DIS(dir1);

      // создаем объект класса ObjectInputStream для чтения
      File fvar1 = new File(dir1+"_var");
      DataInputStream r_var1 = DIS(fvar1);

      // создаем объект класса ObjectInputStream для чтения коэффициентов и степеней полинома
      DataInputStream r_pol2 = DIS(dir2);

      // создаем объект класса ObjectInputStream для чтения
      File fvar2 = new File(dir2+"_var");
      DataInputStream r_var2 = DIS(fvar2);

      //ByteArrayOutputStream baos = new ByteArrayOutputStream();
      //DataOutputStream dos = new DataOutputStream(baos);

      // инициализация переменных, необходимых входе операции сложения

      Element itsCoeffOne1 = toElement(r_var1.readInt());
      Element itsCoeffOne2 = toElement(r_var2.readInt());

      // количество переменных в полиномах
      int var1 = r_var1.readInt();
      int var2 = r_var2.readInt();
      int var = Math.max(var1, var2);
      // массив для временного хранения степеней одного монома первого полинома
      int pow1[] = new int[var];
      // массив для временного хранения степеней одного монома второго полинома
      int pow2[] = new int[var];

      // количество коэффициентов в полиномах
      long coeffs_length1 = r_var1.readLong();
      long coeffs_length2 = r_var2.readLong();

      rw_var.writeInt(toInt(itsCoeffOne1));
      rw_var.writeInt(var);
      rw_var.writeLong(coeffs_length1+coeffs_length1);

      // пременная для временного хранения коэффициента одного монома первого полинома
      Element cfs1 = Polynom.polynom_zero(itsCoeffOne1);
      // переменная для временного хранения коэффициента одного монома второго полинома
      Element cfs2 = Polynom.polynom_zero(itsCoeffOne1);

      // считывание из файловых полиномов первых частей полиномов
      long numBytes1 = r_var1.readLong();
      byte[] b1 = new byte[(int)numBytes1];
      r_pol1.read(b1);


      Polynom polynom1 = (Polynom)FileUtils.fromByteArr(b1);
      cfs1 = polynom1.coeffs[0];
      System.arraycopy(polynom1.powers, 0, pow1, 0, var1);
      //for(int j=0; j<var1; j++) pow1[j] = r_pol1.readInt();

      long numBytes2 = r_var2.readLong();
      byte[] b2 = new byte[(int)numBytes2];
      for(int k=0; k<numBytes2; k++) b2[k] = r_pol2.readByte();

      long sumBytes1 = numBytes1;
      long sumBytes2 = numBytes2;
      long sumBytes = 0;

      Polynom polynom2 = (Polynom)FileUtils.fromByteArr(b2);
      cfs2 = polynom2.coeffs[0];
      System.arraycopy(polynom2.powers, 0, pow2, 0, var2);
      //for(int k=0; k<var2; k++) pow2[k] = r_pol2.readInt();

      // переменные write1, write2 для подсчета числа записанных мономов,
      // из первого и второго полинома соответственно
      int write1=0, write2=0;
      int numpows1 = var1, numcfs1 = 0;
      int numpows2 = var2, numcfs2 = 0;

      /* 1) Вступление (запись мономов из полинома с большим числом переменных,
       до тех пор пока число переменных с ненулевыми степенями не подравняется) */

      Ring ring = new Ring(itsCoeffOne.numbElementType(), 1);

      Polynom polbuf = Polynom.polynom_zero(itsCoeffOne);

      met1: if (var1>var2){
        for (int i=var1-1; i>var2-1; i--){
          while (pow1[i]!=0){

            polbuf = polbuf.add(new Polynom(pow1, new Element[]{cfs1}), true, ring);
            byte[] b = FileUtils.toByteArray(polbuf);
            write1++; numcfs1++;

            if(b.length>=quantum && write1<coeffs_length1){
              rw_pol.write(b);// пишем часть полинома в байтовый поток
              sumBytes += b.length;
              rw_var.writeLong(sumBytes); // пишем в файл количество байт, которое понадобилось для коэффициента
              polbuf = Polynom.polynom_zero(itsCoeffOne);
            }

            //for(int j=0; j<var1; j++){ rw_pol.writeInt(pow1[j]);}
            if(/*b.length>=numBytes1*/  polynom1.coeffs.length==numcfs1){

              numBytes1 = r_var1.readLong()-sumBytes1;
              sumBytes1 += numBytes1;
              b = new byte[(int)numBytes1];
              r_pol1.read(b);

              polynom1 = (Polynom)FileUtils.fromByteArr(b);
              //cfs1 = polynom1.coeffs[0];
              //System.arraycopy(polynom1.powers, 0, pow1, 0, var1);
              numcfs1=0; numpows1=0;
            }

            cfs1 = polynom1.coeffs[numcfs1];
            System.arraycopy(polynom1.powers, numpows1, pow1, 0, var1);

            numpows1 = numpows1+var1;

            if(write1>=coeffs_length1){break met1;}
         }
        }
      }


      met1: if (var2>var1){
        for (int i=var2-1; i>var1-1; i--){
          while (pow2[i]!=0){

            polbuf = polbuf.add(new Polynom(pow2, new Element[]{cfs2}), true, ring);
            byte[] b = FileUtils.toByteArray(polbuf);
            write1++; numcfs2++;

            if(b.length>=quantum && write2<coeffs_length2){
              rw_pol.write(b);// пишем часть полинома в байтовый поток
              sumBytes += b.length;
              rw_var.writeLong(sumBytes); // пишем в файл количество байт, которое понадобилось для коэффициента
              polbuf = Polynom.polynom_zero(itsCoeffOne);
            }

            if(polynom2.coeffs.length==numcfs2){

              numBytes2 = r_var1.readLong()-sumBytes2;
              sumBytes2 += numBytes1;
              b = new byte[(int)numBytes2];
              r_pol2.read(b);

              polynom2 = (Polynom)FileUtils.fromByteArr(b);
              //cfs2 = polynom2.coeffs[0];
              //System.arraycopy(polynom2.powers, 0, pow2, 0, var2);
              numcfs2=0; numpows2=0;
            }

            cfs2 = polynom2.coeffs[numcfs2];
            System.arraycopy(polynom2.powers, numpows2, pow2, 0, var2);

            numpows2 = numpows2+var2;

            if(write2>=coeffs_length2){break met1;}
         }
        }
      }

      int num_cfs_zero = 0; // количество совпавших коэффициентов, т.е. дающих в сумме 0
      int last = Math.min(var1, var2);
      boolean f=true; // флаг, чтобы знать на выходе из цикла, был ли записан считанный моном, false - не был записан, true - был записан

      /* 2) Общая часть (сравнение, сложение и запись посортированных мономов
         выход из цикла, как только один из файловых полиномов закончиться) */
      while ((write1<coeffs_length1)&&(write2<coeffs_length2)) {

      met:for (int i = last-1; i >= 0; i--) {
            if (pow1[i]+pow2[i]==0 && i!=0) {last--; continue met;} //отбрасываем нулевые степени при сравнении мономов

            byte[] b = FileUtils.toByteArray(polbuf);

            if(b.length>=quantum && !f){
              rw_pol.write(b);// пишем часть полинома в байтовый поток
              sumBytes += b.length;
              rw_var.writeLong(sumBytes); // пишем в файл количество байт, которое понадобилось для коэффициента
              polbuf = Polynom.polynom_zero(itsCoeffOne);
              f = true;
            }

            if(pow1[i]==pow2[i] && i==0){ // когда степени равны
              Element subcfs = cfs1.subtract(cfs2, ring);
              write1++; write2++;
              numcfs1++; numcfs2++;
              if(subcfs!=(Polynom.polynom_zero(itsCoeffOne))){ // если сумма коэффициентов не равна 0
                polbuf = polbuf.add(new Polynom(pow1, new Element[]{subcfs}), true, ring);
                num_cfs_zero++;
                //for(int j=0; j<var; j++){rw_pol.writeInt(pow1[j]);}
              }else{num_cfs_zero=num_cfs_zero+2;}
              // чтение следующих мономов из файла

              if(polynom1.coeffs.length==numcfs1 && write1<coeffs_length1){
                numBytes1 = r_var1.readLong()-sumBytes1;
                //System.out.println("numBytes1  = =  "+numBytes1);
                sumBytes1 += numBytes1;
                //System.out.println("numBytes1  = =  "+sumBytes1);
                b1 = new byte[(int)numBytes1];
                r_pol1.read(b1);
                polynom1 = (Polynom)FileUtils.fromByteArr(b1);
                //cfs1 = polynom1.coeffs[0];
                //System.arraycopy(polynom1.powers, 0, pow1, 0, var1);
                numcfs1=0; numpows1=0;
              }
              if(polynom2.coeffs.length==numcfs2 && write2<coeffs_length2){
                numBytes2 = r_var2.readLong()-sumBytes2;
                sumBytes2 += numBytes2;
                b2 = new byte[(int)numBytes2];
                r_pol2.read(b2);
                polynom2 = (Polynom)FileUtils.fromByteArr(b2);
                //cfs1 = polynom1.coeffs[0];
                //System.arraycopy(polynom1.powers, 0, pow1, 0, var1);
                numcfs2=0; numpows2=0;
              }

              if(polynom1.coeffs.length>numcfs1){
                cfs1 = polynom1.coeffs[numcfs1];
                System.arraycopy(polynom1.powers, numpows1, pow1, 0, var1);
                numpows1 = numpows1+var1;
              }

              if(polynom2.coeffs.length>numcfs2){
                cfs2 = polynom2.coeffs[numcfs2];
                System.arraycopy(polynom2.powers, numpows2, pow2, 0, var2);
                numpows2 = numpows2+var2;
              }
              f = false; break;
          }else
            if (pow1[i] > pow2[i]) { // когда степени первого монома больше второго

            polbuf = polbuf.add(new Polynom(pow1, new Element[]{cfs1}), true, ring);
            numcfs1++;
            //sumBytes += b.length;
            //rw_var.writeLong(sumBytes); // пишем в файл количество байт, которое понадобилось для коэффициента

            write1++;
            //for(int j=0; j<var; j++){rw_pol.writeInt(pow1[j]);}
            if(polynom1.coeffs.length==numcfs1 && write1<coeffs_length1){
              numBytes1 = r_var1.readLong()-sumBytes1;
              sumBytes1 += numBytes1;
              b1 = new byte[(int)numBytes1];
              r_pol1.read(b1);
              polynom1 = (Polynom)FileUtils.fromByteArr(b1);
              numcfs1=0; numpows1=0;
            }

            if(polynom1.coeffs.length>numcfs1){
              cfs1 = polynom1.coeffs[numcfs1];
              System.arraycopy(polynom1.powers, numpows1, pow1, 0, var1);
              numpows1 = numpows1+var1;
            }

            f = false; break;
          } else
          if (pow1[i] < pow2[i]) { // когда степени первого монома меньше второго

            polbuf = polbuf.add(new Polynom(pow2, new Element[]{cfs2}), true, ring);
            numcfs2++;
            //byte[] b = FileUtils.toByteArray(monom2);
            //rw_pol.write(b);// пишем коэффициент в поток
            //sumBytes += b.length;
            //rw_var.writeLong(sumBytes); // пишем в файл количество байт, которое понадобилось для коэффициента

            write2++;
            //for(int j=0; j<var; j++){rw_pol.writeInt(pow2[j]);}
            if(polynom2.coeffs.length==numcfs2 && write2<coeffs_length2){
              numBytes2 = r_var2.readLong()-sumBytes2;
              sumBytes2 += numBytes2;
              b2 = new byte[(int)numBytes2];
              r_pol2.read(b2);
              polynom2 = (Polynom)FileUtils.fromByteArr(b2);
              numcfs2=0; numpows2=0;
            }

            if(polynom2.coeffs.length>numcfs2){
              cfs2 = polynom2.coeffs[numcfs2];
              System.arraycopy(polynom2.powers, numpows2, pow2, 0, var2);
              numpows2 = numpows2+var2;
            }
            f = false; break;
          }
        }
      }

      /* 3) Заключение (дозаписываем оставшиеся мономы, одного из полиномов)*/
     while(write1<coeffs_length1){
        if(f){
          if(polynom1.coeffs.length==numcfs1){
            numBytes1 = r_var1.readLong()-sumBytes1;
            sumBytes1 += numBytes1;
            b1 = new byte[(int)numBytes1];
            //for(int k=0; k<numBytes1; k++) b1[k] = r_pol1.readByte();
            r_pol1.read(b1);
            polynom1 = (Polynom)FileUtils.fromByteArr(b1);
            numcfs1=0; numpows1=0;
          }
          cfs1 = polynom1.coeffs[numcfs1];
          System.arraycopy(polynom1.powers, numpows1, pow1, 0, var1);
          numpows1 = numpows1+var1;
        }

        polbuf = polbuf.add(new Polynom(pow1, new Element[]{cfs1}), true, ring);
        numcfs1++;
        write1++;
        f = true;
        byte[] b = FileUtils.toByteArray(polbuf);

        if(b.length>=quantum){
          rw_pol.write(b);// пишем monom в поток
          sumBytes += b.length;
          rw_var.writeLong(sumBytes); // пишем в файл количество байт, которое понадобилось для коэффициента
          polbuf = Polynom.polynom_zero(itsCoeffOne);
        }

      }

      while(write2<coeffs_length2){
        if(f){
          if(polynom2.coeffs.length==numcfs2){
            numBytes2 = r_var2.readLong()-sumBytes2;
            sumBytes2 += numBytes2;
            b2 = new byte[(int)numBytes2];
            //for(int k=0; k<numBytes1; k++) b1[k] = r_pol1.readByte();
            r_pol2.read(b2);
            polynom2 = (Polynom)FileUtils.fromByteArr(b2);
            numcfs2=0; numpows2=0;
          }
          cfs2 = polynom2.coeffs[numcfs2];
          System.arraycopy(polynom2.powers, numpows2, pow2, 0, var2);
          numpows2 = numpows2+var2;
        }

        polbuf = polbuf.add(new Polynom(pow2, new Element[]{cfs2}), true, ring);
        numcfs2++;
        write2++;
        f = true;
        byte[] b = FileUtils.toByteArray(polbuf);

        if(b.length>=quantum){
          rw_pol.write(b);// пишем monom в поток
          sumBytes += b.length;
          rw_var.writeLong(sumBytes); // пишем в файл количество байт, которое понадобилось для коэффициента
          polbuf = Polynom.polynom_zero(itsCoeffOne);
        }

      }

      if(polbuf!=Polynom.polynom_zero(itsCoeffOne)){
        byte[] b = FileUtils.toByteArray(polbuf);
        rw_pol.write(b);// пишем часть полинома в байтовый поток
        sumBytes += b.length;
        rw_var.writeLong(sumBytes);
      }

      rw_var.seek(8);
      rw_var.writeLong(coeffs_length1+coeffs_length2-num_cfs_zero); // количество мономов в полиноме
      //rw_var.writeLong(coeffs_length1+coeffs_length2-num_cfs_zero);
      //rw_var.writeInt(var);
      //rw_var.writeObject(itsCoeffOne1);

      // закрываем файлы

      r_pol1.close();
      r_pol2.close();
      r_var1.close();
      r_var2.close();
      rw_pol.close();
      rw_var.close();

      // если сумма полиномов равна нулю,
      // т.е. файл, содержащий коэффициенты равен нулю
      // то каталог, содержащий файловый полином удаляется
      if(dir_result.length()==0){
        FileUtils.delete(dir_result);
        polresult = ZERO;
      }

      }catch(Exception e){
        e.printStackTrace();
      }
      return polresult;
    }

    /**
     * произведение файловых полиномов
     * @param fpol1
     * @param fpol2
     * @param fres
     * @param start1
     * @param end1
     * @param start2
     * @param end2
     * @return
     * @throws java.lang.Exception
     */
     private static FPolynom mul(FPolynom fpol1, FPolynom fpol2, File fres, long start1, long  end1, long start2, long end2, long numparts1, long numparts2) throws Exception{

      File bufres = fres;
      FPolynom result = new FPolynom(fres);

      Ring ring = new Ring(itsCoeffOne.numbElementType(), 1);

      String fileA=fres.getAbsolutePath(), fileB=fres.getAbsolutePath();
      if(numparts1==1 && numparts2==1)
          fpol1.toPolynom(start1, end1).mulSS(fpol2.toPolynom(start2, end2), ring).toFPolynom(fres, itsCoeffOne, quantum);
      else{
        long s1=start1, s2=start2, e1=end1, e2=end2, s11=0, s22=0, e11=end1, e22=end2;
          if(numparts1>=numparts2){
            e1 = fpol1.middlePolynom(start1, numparts1/*start1, e1/2*/);
            s11=e1+start1;
            e11=end1-e1;
            s22=start2;
            numparts1= numparts1/2;
          }
          else{
            e2 = fpol2.middlePolynom(start2, numparts2/*start2, e2/2*/);
            s22=e2+start2;
            e22=end2-e2;
            s11=start1;
            numparts2= numparts2/2;
          }

          fileA = File.createTempFile("pol", "").getAbsolutePath();// fileA + "a";
          bufres = new File(fileA);
          //System.out.println(" mul1:" + s11+"   "+ e11 +"   " + s22 +"   " + e22);
          mul(fpol1, fpol2, bufres, s11, e11, s22, e22, numparts1, numparts2);
          //System.out.println(" A = " + (new FPolynom(bufres)).toPolynom());

          fileB = File.createTempFile("pol", "").getAbsolutePath();// fileB + "b";
          bufres = new File(fileB);
          //System.out.println(" mul2:" + s1+"   "+ e1 +"   " + s2 +"   " + e2);
          mul(fpol1, fpol2, bufres, s1, e1, s2, e2, numparts1, numparts2);
          //System.out.println(" B = " + (new FPolynom(bufres)).toPolynom());

          FPolynom.add(fileA, fileB, fres);

          //System.out.println(" add = " + (new FPolynom(fres)).toPolynom());

          FileUtils.deleteFiles(fileA); FileUtils.deleteFiles(fileA+"_var");
          FileUtils.deleteFiles(fileB); FileUtils.deleteFiles(fileB+"_var");
      }

      return result;
    }

    /*private FPolynom mul( FPolynom fpol1, FPolynom fpol2, File fres, long start1, long end1, long start2, long end2) throws Exception{
      File bufres = fres;
      FPolynom result = new FPolynom(fres);

        String namedirA, namedirB;
        long length1 = end1-start1;
        long length2 = end2-start2;
        if( Math.max(length1, length2)<=borderMonomNumb){
          fpol1.toPolynom(start1, length1).mulSS(fpol2.toPolynom(start2, length2)).toFPolynom(fres, itsCoeffOne);
        }
        else{
          long s1=start1, s2=start2, e1=0, e2=0, s11=0, s22=0, e11=length1+start1, e22=length2+start2;
          if(length1>=length2){
            e1 = length1/2+start1;
            e2 = length2+start2;
            s11 = length1/2+start1;//e1
            s22 = start2;// s2
          }
          else{
            e1 = length1+start1;
            e2 = length2/2+start2;
            s11 = start1;//s1
            s22 = length2/2+start2;// e2
          }
          namedirA = fres.getAbsolutePath()+"a";
          bufres = new File(namedirA);
          bufres.delete();
          mul(fpol1, fpol2, bufres, s1, e1, s2, e2);

          namedirB = fres.getAbsolutePath()+"b";
          bufres = new File(namedirB);
          bufres.delete();
          mul(fpol1, fpol2, bufres, s11, e11, s22, e22);

          add(namedirA, namedirB, fres);
          FileUtils.deleteFiles(namedirA+"_var");
          FileUtils.deleteFiles(namedirA);
          FileUtils.deleteFiles(namedirB);
          FileUtils.deleteFiles(namedirB+"_var");
        }
      return result;
    }
   */

    /*public static int[][] divideProcessOnTwoParts(int[] process){
      int part1 = process.length / 2;
      int part2 = part1+Math.round(process.length % 2);
      int[][] proc = new int[2][part2];
      System.arraycopy(proc[0], 0, process, 0, part1);
      System.arraycopy(proc[1], 0, process, part1, part2);
      return proc;
    }
    */




/*
    private FPolynom div( FPolynom fpol1, FPolynom fpol2, File fres, int start1, int end1, int start2, int end2) throws Exception{

      double betta = 0.5;

      // инициализация объектов для чтения из файлов
      ObjectInputStream r_pol1 = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fpol1.filename)));
      File fvar1 = new File(fpol1.filename.getParentFile(), fpol1.filename.getName()+"_var");
      ObjectInputStream r_var1 = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fvar1)));

      ObjectInputStream r_pol2 = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fpol2.filename)));
      File fvar2 = new File(fpol2.filename.getParentFile(), fpol2.filename.getName()+"_var");
      ObjectInputStream r_var2 = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fvar2)));

      // количество мономов в полиноме 1
      long monomNumber1 = r_var1.readLong();
      // количество переменных в полиноме 1
      int numvaribles1 = r_var1.readInt();

      // количество мономов в полиноме 2
      long monomNumber2 = r_var2.readLong();
      // количество переменных в полиноме 2
      int numvaribles2 = r_var2.readInt();

      try{
      Element itsCoeffOne1 = (Element)r_var1.readObject();
      }catch(Exception e){e.printStackTrace();}

      try{
      Element itsCoeffOne2 = (Element)r_var1.readObject();
      }catch(Exception e){e.printStackTrace();}

      // инициализация массивов коэффициентов и степеней полиномов
      Element coeffs1[] = new Element[(int)monomNumber1];
      Element coeffs2[] = new Element[(int)monomNumber2];

      int powers1[] = new int[numvaribles1];
      int powers2[] = new int[numvaribles2];

      // считываем первые мономы полиномов, чтобы узнать их максимальные степени

      // длина первого коэффициента первого полинома
      int l = r_var1.readInt();
      byte[] b = new byte[l];
      for(int k=0; k<l; k++){
          try{
            b[k] = r_pol1.readByte();
          }catch(Exception e){throw new IOException("Ошибка чтения коэффициентов из файла!", e);}
        }
      // первый коэффициент первого полинома
      coeffs1[0] = (Element)FileUtils.fromByteArr(b);
      // первая степень первого полинома
      powers1[0] = r_pol1.readInt();

      // длина первого коэффициента второго полинома
      l = r_var2.readInt();
      b = new byte[l];
      for(int k=0; k<l; k++){
        try{
          b[k] = r_pol2.readByte();
        }catch(Exception e){throw new IOException("Ошибка чтения коэффициентов из файла!", e);}
      }
      // первый коэффициент второго полинома
      coeffs2[0] = (Element)FileUtils.fromByteArr(b);
      // первая степень второго полинома
      powers2[0] = r_pol2.readInt();

      //double d = betta*n/2-1;
      int n1 = (powers1[0]+2)/2;
      int n2 = powers2[0]+1;
      if(n1==n2 && n1==Integer.highestOneBit(n1)){ // если n1 - степень двойки, т.е. n1=2^k
        // считываем первую половину полинома делимого и делителя


      }
      else
      if(powers2[0]<powers1[0] || powers1[0]<2*powers2[0]){

      }
      if(powers1[0]>2*powers2[0]){

      }



      return result;
    }
*/

    /***************** нереализованные абстрактные методы *****************/
            @Override
            public int intValue() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public long longValue() {
                throw new UnsupportedOperationException("Not supported yet.");
            }


            @Override
            public double doubleValue() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

//            @Override
//            public Element abs() {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//            @Override
//            public Element multiply(Element x, int variant) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//            @Override
//            public FPolynom multiply(Element x) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//            /*@Override
//            public Element divide(Element x) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//            */
//            @Override
//            public Element[] quotientAndProperFraction(Element x) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//
//            @Override
//            public Element mod(Element m) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//            @Override
//            public Element GCD(Element x) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//            @Override
//            public Element[] extendedGCD(Element x) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//            @Override
//            public Element pow(int n, int m) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//            @Override
//            public Element negate() {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }

            @Override
            public int signum() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

//            @Override
//            public int compareTo(Element x) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//            @Override
//            public boolean isZero() {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//            @Override
//            public boolean equals(Element x) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//            @Override
//            public Element random(int[] randomType, Random rnd, Element itsCoeffOne) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }


            public Element random(double[] randomType, Random rnd, Element itsCoeffOne) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

//            @Override
//            public Element valOf(int x) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//            @Override
//            public Element valOf(long x) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//            @Override
//            public Element valOf(double x) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }

//            @Override
//            public Element valOf(String x) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//
//    @Override
//    public Element toNumber(Element x) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public Element divide(Element x) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }


    public int compareTo(Element o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int compareTo(Element x, Ring ring) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean isZero(Ring ring) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean equals(Element x, Ring r) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean isOne(Ring ring) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


  /******************************************************************/

}
