
/*Функция раскладывает полином на множители.
 *
 */

package com.mathpar.polynom;



import com.mathpar.number.*;

public class FactorizationPolynomialWithoutMultipleFactors {

    /**Разложение полинома в Z на множители.
     * Полином не должне иметь сомножителей которые имеют кратности.
     * Factorization of the polynomial without multiple factors
     *@param t  PolynomZ in Раскладываемый полином,
     *@param ringZ -кольцо Z[x] полинома t
     *@return   PolynomZ[] MPZ массив полиномов полученных в результате разложения
     */
    public static Polynom[] Factor(Polynom t, Ring ringZ) {
      //  System.out.println("t="+t);
        if(t.powers[0]<2) return new Polynom[]{t};
        Polynom in = (Polynom) t.clone();
        //System.out.toString(in.toString());
        int nm=5;               //кол-во переборов для берликэмпа
        boolean minus=false;    //Коэффициент старшего монома <0
        if(in.coeffs[0].signum()==-1) { //Если коэффициент старшего монома <0
            in.negateThis(ringZ);            //инвертируем полином
            minus=true;
        }
        int prime[] = {7, 11, 13, 17, 19, 23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97,101,103,107,109,113,127,131,137,139,149,151,157,163,167,173,179,181,191,193,197,199,211,223,227,229,233,239,241,251};
        Element NodCF = in.GCDNumPolCoeffs(ringZ);    //НОД коэфициентов, полином сокращаем на НОД
        in = in.divideByNumber(NodCF, ringZ);
    //   for(int i = 0; i < in.coeffs.length; i++){
    //        in.coeffs[i]=in.coeffs[i].divide(NodCF, ring);}
        NumberZ MaxC = (NumberZ)in.coeffs[0]; //коэффициент большей степени полинома
    //    long start=System.currentTimeMillis();
        NumberZ B = MaxCoefInFactor(in);//Находим потенциально самый большой коэффициент в разложении
     //   long end=System.currentTimeMillis();
        //System.out.println("time B = "+(end-start));
        //System.out.println("B="+B);         //вывод для контроля
        Polynom MasPol[][]=new Polynom[nm][]; //Массив разложений полиномов в Zp
        long selectedPrimes[] = new long[nm];           //Массив чисел кольца для MasPol
        int i=0,k=0;
         Ring ringP32=new Ring("Zp32[x]", ringZ); // кольцо для малых модулей
       // Ring rZx= new Ring("Z[x]");                 //создаем кольцо
        while(k<nm){                            //Ищем разложение в Zp nm раз
            if(i==prime.length){ nm=k;  break;}     //если найдено <nm разложенией в Zp то работаем с ними
           ringP32.setMOD32(prime[i]);
       //     System.out.println("maxC="+MaxC+"        in="+NoGCD_F_DFZp32(in, ringP32));
            if( !(new NumberZp32(MaxC,ringP32).isZero(ringP32)) && NoGCD_F_DFZp32(in,ringP32)){
         //       System.out.println("#01");         //Если первый коэфициент не делится нацело модулем и
                MasPol[k] = Factor_p(in,ringP32);  //НОД полинома по модулю p с его 1 производной =1
             //  System.out.println("#02");
                selectedPrimes[k]=prime[i];  // ring.MOD32= prime[i];            //раскладываем и пишем в массив
               // System.out.println("# = "+k+"  *  p = "+selectedPrimes[k]+"  *  N = "+Array.toString(MasPol[k])+"   i="+i);
  //             in=new Polynom(s,ring);
                k++;                        //Еще 1 разложение
            } i++;                            //счетчик итераций
        }
//        for (int j = 0; j < MasPol.length; j++) {
//         System.out.println(j+":   "+Array.toString(MasPol[j],ringZ));
//        }
        int N=0;                         //№ по которому будем поднимать
        for(i=1;i<nm;i++){               //Выбираем Zp покоторому будем поднимать
            if(MasPol[N].length > MasPol[i].length)  N=i;
        }//System.out.println("-----------------------"+N);
//        N=0;
        if(MasPol[N].length==1){                //Если есть Разложение в Zp = 1
            Polynom MPZ[] =new Polynom[1];    //Значит полином неразложим в Z продолжать нет смысла
            MPZ[0]=in;                          //Выводим входной полином
            MPZ[0]=MPZ[0].multiplyByNumber(NodCF,ringP32);    //умножаем первый множитель на NodCF
            if(minus) {                             //если входной полином был отрицательным
                MPZ[0].negateThis(ringZ);                //инвертируем первый моном
            }
        return MPZ;
        }
//         System.out.println("N="+N);
//        System.out.println("in="+in);
//        System.out.println("массив по которому будем восстанавливать "+Array.toString(MasPol[N]));
        Polynom[] MPZ=GeneralLifting(in,MasPol[N],selectedPrimes[N],B,MaxC,ringZ);//Сам подъём
  //       System.out.println("GeneralLifting=====MPZ="+Array.toString(MPZ));
        Polynom v=Polynom.polynom_one(NumberZ.ONE);
        int len=MPZ.length;
        for(int i1=0; i1<len; i1++) v=v.multiply(MPZ[i1], ringZ);
        if(v.compareTo(in, ringZ)!=0){
        Polynom rezalt[]=new Polynom[len+1];
        System.arraycopy(MPZ, 0, rezalt, 0, len);
        rezalt[len]=in.divideExact(v, ringZ);
        return rezalt;
}
//System.out.println("MPZ="+Array.toString(MPZ));

if(MPZ.length==0) MPZ=new Polynom[]{(Polynom.polynomFromNumber(NodCF,ringZ))};
        MPZ[0]=MPZ[0].multiplyByNumber(NodCF,ringZ);       //умножаем первый множитель на NodCF
        if(minus) {                             //если входной полином был отрицательным
            MPZ[0].negateThis(ringZ);                //инвертируем первый моном
        }

        return MPZ;
    }

    /**Готовит пару полиномов для подъема, после того как будут получены все сомножители в Z
     *проверяет делением, на входной полином, выбирает "правильные" сомножители, а остаток запускает рекурсивно
     *@param in PolynomZ - входной полином
     *@param Mass[] PolynomL - разложение полинома по модулю p
     *@param p long - модуль разложения
     *@param B NumberZ - оценка верхней границы коэффициентов мономов в разложении
     *@param MaxC - коэфициент монома с максимальной степенью
     *@return rez[] PolynomZ - Разложение полинома в Zp
     */
  public  static  Polynom[] GeneralLifting(Polynom in, Polynom[] Mass, long p, NumberZ B,NumberZ MaxC, Ring ring){
        Ring rZx= new Ring("Z[x]",ring);
        int n_rez = 0;              //Массив для ответов
        Ring ringZP32x=new Ring("Zp32[x]",ring); ringZP32x.setMOD32(p);
    //    System.out.println("p===============================p================="+p);
        
        Polynom div[]=new Polynom[3];       //массив под результаты деления
        Polynom d=(Polynom) in.clone();                  //копия in
        Polynom dn=(Polynom) in.clone();                 //2 копия in
        
        Polynom rez[]=new Polynom[Mass.length];   //для ответов
        Polynom U=new Polynom();      //поднятый полином
        Polynom in_l=in.toPolynom(Ring.Zp32,ringZP32x); //in mod p
        in_l=FactorizationZp32OneVar.norm(in_l,ringZP32x);    //нормируем
        Polynom uw[]=new Polynom[2];  //создаем полиномы для работы
             //убить!!!
        int ML = 0;                 //Длинна массива Mass
        for (int i=1;i<=Mass.length;i++){//кол-во элементов в сочетании
            int length_Mass=Mass.length;
   //         System.out.println("i+++++@@@@@@@@@@@@@@@@@@@============="+i);
   //         System.out.println("MASS===="+Array.toString(Mass));
            int pointer[]=new int [i];  //создаем массив "указателей"
            for(int n=0;n<i;n++){       //заполняем их числами
                pointer[n]=n;
            }                   //указатели созданы
            long t=CombiInt(length_Mass,i);//кол-во сочетаний для данного кол-ва "указателей"
   //          System.out.println("*** i = "+i+"   ## t = "+t+"  Mass.length = "+Mass.length);
//System.out.println(" TTTTTTT="+t);
            ML=length_Mass;
            circ:  for(int j=0;j<t;j++){               //перебор !!!!!!!!!!!
         //       System.out.println(" TTTTTTT="+t+"   "+j);
                if(i>1 && ML>i && ML<2*i)
                continue;
          //      System.out.println("+++Massss="+Array.toString(Mass)+"   "+Array.toString(pointer));
                for(int ii=0;ii<pointer.length;ii++){
                  if(Mass[pointer[ii]]==null){pointer=NextCombination(pointer,length_Mass-1,pointer.length-1);
           //           System.out.println("wwww");
                  continue circ;}}
                if(pointer[pointer.length-1]==Mass.length-1){int nullNumb=0;
                    for (int k = 0; k < Mass.length; k++) if(Mass[k]==null)nullNumb++;
                    if(Mass.length==nullNumb+pointer.length){ Polynom pp=rez[0];
                        for (int k = 1; k < n_rez; k++)  pp=pp.multiply(rez[k], ring); 
                        Polynom[] ppp=in.divAndRem(pp,rZx);
                        
                        rez[n_rez]=ppp[0].divideByNumber(ppp[2].coeffs[0], ring); 
                //        System.out.println(n_rez+"   "+in+"   "+pp+"rez[n_rez]+++++++====="+rez[n_rez]+"  "+ppp[0]+"   "+ppp[1]+"  "+ppp[2]);
                        Mass=new Polynom[0]; n_rez++; break circ;
                    }
                }
                Polynom uw0=Mass[pointer[0]];
                for(int n=1;n<pointer.length;n++)uw0=uw0.mulSS(Mass[pointer[n]],ringZP32x); // U
                uw[0]=uw0; uw[1]= (in_l.toPolynom(ringZP32x)).divideRQZpx(uw0,ringZP32x);
                     //            System.out.println("_____+++++++++++_____________uw[1]="+uw[1]+"    uw[0]="+uw[0]);
                if (uw[1].isItNumber()||uw[0].isItNumber()){          //если в сочетании есть null то...
// //                 System.out.println("Mass+++++++++++="+Arrays.toString(pointer)+"   "+(Mass.length-1)+"    "+(pointer.length-1));
//                    System.out.println("!!WHOU CAN BE NUMBER   ?????????");
//                 System.out.println("uw[1]="+uw[1]+"    uw[0]="+uw[0]);
                    pointer=NextCombination(pointer,Mass.length-1,pointer.length-1);//следующее сочетание
                    continue;                   //следующая итерация
                }
   // System.out.println("+++++++  uw[1]="+uw[1]+"    uw[0]="+uw[0]);

                U=LinearLifting(uw[1],uw[0],in,p,B,MaxC,d,ringZP32x); //поднимаем пару возвращаем U @@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        //         System.out.println("U = "+U );
                U=U.toPolynom(rZx);
      //          System.out.println("поднятый U = "+U.toString(rZx));
                //dn=NotFactoR(U,d);              //проверочное деление
//System.out.println("+++++++  d="+d+"    U="+U);
                ringZP32x.MOD=new NumberZ(ringZP32x.MOD32);
               // U=U.Mod(ring.MOD, ring);
        //        System.out.println("ffff="+d+"        U="+U);
               // System.out.println(rZx);
                div=d.divAndRem(U,rZx);   /*  ????  */                //делим d на U

          //     System.out.println("div=divideand Remainder="+Array.toString(div));
               // System.out.println(rZx);
                if(div[1].isZero(rZx)){   //если делится нацело
 // System.out.println("rem=zero! ura!+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++  d="+d+"    U="+U);
                    d=PolDivNumbExect(div[0],div[2].coeffs[0],rZx);
            //        System.out.println("PolDivNumbExect= div[0]="+div[0]+"   div[2] ="+div[2] +"  d="+d);
                    dn=d;
             //System.out.println(rZx);
                    Mass=NullEM(Mass,pointer);  //зануляет "поднятые" сомножители из массива
                    ML=ML-i;                    //Уменьшение длинны на кол-во поднятых элементов
                    rez[n_rez]=U;               //заносим U в массив ответов
                    n_rez++;                    //увеличиваем счетчик результатов
                    if (d.isOne(rZx)){//если остаток = 1 т.е. все разделилось
                        break;                        //тогда все множители найдены,возвращаем результат
                    }
                }else{ 
//                    System.out.println("rem=zero! oufull!___________________________ "+rZx);
//                 System.out.println("rem="+div[1]+"++++++++++++++++++++++++++++++++  d="+d+"    U="+U+"  dn="+dn);
//                  System.out.println("Massss="+Array.toString(Mass)+"   "+Array.toString(pointer));
                    d=dn;
                }
 //               System.out.println("Mass+++++++++++="+Arrays.toString(pointer)+"   "+(length_Mass-1)+"    "+(pointer.length-1));

            pointer=NextCombination(pointer,Mass.length-1,pointer.length-1);   //следующее сочетание
  //          System.out.println("pointwer="+Arrays.toString(pointer));
            }                           //следующее сочетание
   //         System.out.println("FFFF="+Arrays.toString(Mass));
            Mass=CleanM(Mass);          //"Чистим" массив от null
        }                               //кол-во элементов в сочетании
      // System.out.println("n_rez="+n_rez);
   //    System.out.println("rez="+Array.toString(rez));
        Polynom out[]=new Polynom[n_rez];//создаем "выходной" массив

        System.arraycopy(rez,0,out,0,n_rez);//копируем из рабочего в "выходной"
    //    System.out.println("out============"+Array.toString(out)+"   "+div[0]);
       // out[n_rez]=div[0];

               return out;                     //возвращаем корректное разложение
    }

    /**      ЛИНЕЙНЫЙ ПОДЪЕМ
     *Поднимает полином из Zp в Z по Лемме Гензеля пока p^t не превысит B
     *после чего смещает границы коэффициентов от {0,p} до {-p/2,p/2}
     *находит НОД коэфициентов полученного полинома и сокращает на него
     *@param w,u PolynomL - пара поднимаемых полиномов
     *@param in PolynomZ - входной полином
     *@param p long - модуль
     *@param B NumberZ - оценка верхней границы коэффициентов мономов в разложении
     *@param MaxC NumberZ - коэфициент монома с максимальной степенью
     *@return u1 PolynomZ - сомножитель в Z
     */

    static Polynom LinearLifting(Polynom w2, Polynom u2,Polynom in,long p, NumberZ B,NumberZ MaxC, Polynom d, Ring ringZP32x){
        Polynom w=w2.toPolynom(Ring.Z, ringZP32x);  Polynom u=u2.toPolynom(Ring.Z, ringZP32x);
    //    Element one = w.one(ringZP32x); // this is Zp32
        ringZP32x.setMOD32(p);
     //    System.out.println("In Lin ---------------------------- Lift u="+u+"     w="+w);
        Polynom f_uw,u1,w1,uz,wz,prov,uc,wc,ui,wi,a,b;
        Polynom[] dv;                  //результат деления
        NumberZ p1= NumberZ.ONE;              //модуль подъема
        Ring rZx=  new Ring("Z[x]",ringZP32x);
        u=u.multiplyByNumber( MaxC,rZx).toPolynomMod(Ring.Zp32, ringZP32x);
        Polynom[] EGCD=u.extendedGCDinEDx(w2,ringZP32x);  //Расширенный алгоритм эвклида
                                                        
      //       System.out.println("u ExtGCD w mod p ="+Array.toString(EGCD));
        ui=u.toPolynom(Ring.Z,rZx); u1=ui;                        //конвертация в PolynomZ
        wi=w.toPolynom(Ring.Z,rZx); w1=wi;                        //конвертация в PolynomZ
        a=EGCD[1].toPolynom(Ring.Z,rZx);                         //конвертация в PolynomZ
        b=EGCD[2].toPolynom(Ring.Z,rZx);                         //конвертация в PolynomZ
        NumberZ pb=NumberZ.valueOf(p);                        //NumberZ=p
        ringZP32x=new Ring("Zp[x]"); ringZP32x.setMOD(pb);
        while(p1.compareTo(B)==-1){                                  //поднимаем до максимальной оценки
            p1=p1.multiply(pb);                                    //p^t
           prov=w1.multiply(u1,rZx); //prov = w1*u1
       //     System.out.println("1)   in="+in+"     prov=w1*u1="+prov+"   in-prov=  "+in.subtract(prov,rZx)+"p1=- - - - - - - - - - - - - -"+p1);
            f_uw=PolDivNumbExect(in.subtract(prov,rZx),p1,rZx);          //f_uw = (in_m-w1*u1*MaxCoeff)/p^t
            f_uw=f_uw.Mod(p1,rZx);                       //f_uw (mod p)=(in-u1*w1)/p^t
            wz=a.mulSS(f_uw,rZx);                   //f_uw*a
            uz=b.mulSS(f_uw,rZx);                   //f_uw*b
            dv=wz.divAndRemRQZpx(wi,rZx);              //f_uw*a/w
            wc=dv[1].Mod(pb,rZx);                   //остаток деления по модулю p^t
            uc=uz.add(dv[0].mulSS(u1,rZx),rZx);         //частное *u1 +f_uw*b
            uc=uc.Mod(pb,rZx);
            u1=(Polynom)u1.add(uc.multiply(p1,rZx),rZx);      //u1=u1+uc
            w1=(Polynom)w1.add(wc.multiply(p1,rZx),rZx);      //w1=w1+wc
     //   System.out.println("8) uc="+uc+"     wc="+wc);
     //   System.out.println("9)   u1="+u1+"     w1="+w1);
          if (uc.isZero(rZx)) 
             if (d.divAndRem(u1,rZx)[1].isZero(rZx)) 
              {//System.out.println("uc.isZero(rZx) u1="+u1);
                  break;}
        }
        Element nod;                       //НОД мономов
        if(u1.coeffs.length>1){ //если кол-во мономов >1
            nod = u1.GCDNumPolCoeffs(rZx);
            u1=u1.polDivNumb(nod,rZx);         //сокращаем их на НОД
    //     System.out.println("11)   nod="+ nod+"  u= u1 div numb nod ====="+u1);    
        }else u1.coeffs[0]=NumberZ.ONE;    //если в полиноме 1 моном
         //     System.out.println("END Lin-Lif  u1=   "+u1+"   p="+p);                             //то он = x
        return u1.toPolynom(Ring.Z,rZx);    //возвращаем u1
    }

    /**
     *"Пирамида"
     *Находит следующее сочетание элементов массива
     *@param pointer[] int - массив указателей
     *@param Max int - максимально возможный элемент
     *@return Mass[] int - следующее сочетание
     */
     public static int[] NextCombination(int[] pointer,int Max,int Num){

      //  int d=Max-Num;      //диапазон для указателя
        if (pointer[Num]==Max){//максимум для данного указателя
            if(Num>0){        //если указатель не последний
                pointer=NextCombination(pointer,Max-1,Num-1);   //запустить с ним процедуру рекурсивно
                pointer[Num]=pointer[Num-1]+1;    //указатель сделать на единицу больше предыдущего
            }
        }else{              //если указатель не достиг максимально возможного значения
            pointer[Num]++;                  //увеличить его на 1
        }
        return pointer;
    }


    /**Сокращение коэффициентов полинома в a раз, алгоритм
     *не проводит проверку на целочисленную делимость
     *@param in PolynomL - Сокращаемый полином
     *@param a long - число на которое сокращаем
     *@return in PolynomZ - полученный полином
     */
    public static Polynom PolDivNumbExect(Polynom in, Element a, Ring ring){
        Element cf[] = new Element[in.coeffs.length];
        int pw[] = in.powers;
        for(int i=0;i<in.coeffs.length;i++){
            cf[i]=in.coeffs[i].divide(a,ring);
        }
        return new Polynom(pw,cf);
    }

    /**Возвращает TRUE когда НОД(полинома по модулю p и его 1 производной = const)
     *@param in PolynomZ - полином
     *@param p int - модуль
     *@return boolean
     */
    public static boolean NoGCD_F_DFZp32(Polynom in, Ring ring){

         Polynom Pol = in.toPolynom(Ring.Zp32,ring);
        Polynom PolD = Pol.D(0,ring);
      //  Polynom PolD = Pol.PolynomDifX(ring);
       // System.out.println("PolD="+PolD);

        if(PolD.compareTo(Polynom.polynomFromNumber(NumberZp32.ZERO, ring),ring)==0)return false;
        Pol= Pol.gcd(PolD,ring);
        if(Pol.powers.length == 0)return true;
        return false;
    }

    /**Возвращает TRUE если остаток от деления максимального монома на модуль
     *неравен 0, и FALSE  в противном случае.
     *@param x NumberZ
     *@param y int
     *@return boolean
     */
    static boolean DivQuality(NumberZ x, Ring ring){
        NumberZp32 z = new NumberZp32(x,ring);
        if (z.isZero(ring))return false;
        return true;
    }

/**Получение максимально возможного коэффициента полинома
 *@param x PolynomZ
 *@return M NumberZ
 */
    public static NumberZ MaxCoefInFactor(Polynom x){
        Ring ring=Ring.ringZxyz;
        int n=x.coeffs.length;              //кол-во мономов в полиноме
        int m=x.powers[0];                  //max степень полинома
        NumberZ c1,c2;
       //  SumMod  --- ЭТО АБСОЛЮТНО ПЛОХО!!
        NumberZ norm_u=SumMod(x); //sqrt(a1^2 + a2^2 +...+an^2)
        c1 =(NumberZ)Element.binomial( ring, m-1, (m+1)/2);
       // c1=Combi(m-1,(m+1)/2);
       // c2=Combi(m-1,((m+1)/2)-1);
        c2 =(NumberZ)Element.binomial( ring, m-1, (m+1)/2-1);
        c1=c1.add(c2);
        NumberZ M = (NumberZ)norm_u.multiply(c1.multiply(x.coeffs[0].abs(ring),ring),ring);
        M.add(M);                           //возвращаем 2*М
        return M.abs();
    }

/**Нахождение сочетания из x элементов по y
 *@param x,y int
 *@return z NumberZ
 */
    static NumberZ Combi(int x, int y){
        NumberZ z = Factorial(x).divide(Factorial(y).multiply(Factorial(x-y)));
        return z;
    }

    /**Нахождение факториала (NumberZ) n! = 1*2*3*...*n
     *@param x NumberZ
     *@return f NumberZ
     */
    static NumberZ Factorial(int x){
        NumberZ f = NumberZ.ONE;
        for (long i=1;i<=x;i++)
            f=f.multiply(NumberZ.valueOf(i));
        return f;
    }

    /**кол-во сочетаний из x элементов по y (Int) z=x!/y!*(x-y)!
     *@param x int
     *@param y int
     *@return z int
     */
    public static Long CombiInt(int x, int y){
        long z= FactorialInt(x)/(FactorialInt(y)*FactorialInt(x-y));
        return z;
    }

    /**Нахождение факториала (Long)  n! = 1*2*3*...*n
     *@param x int
     *@return f long
     */
    static Long FactorialInt(int x){
        long f=1;
        for (int i=1;i<=x;i++)
            f=f*i;
        return f;
    }

/**Нахождение ||x|| = (x1^2+x2^2+...+xn^2)^1/2
 *@param x[] NumberZ
 *@return z NumberZ
 */
    static NumberZ SumMod(Polynom x){Ring ring=Ring.ringZxyz;
        NumberZ z=NumberZ.ZERO;
        for (int i=0;i<x.coeffs.length;i++){
            z = (NumberZ)z.add((x.coeffs[i]).multiply(x.coeffs[i],ring),ring);
        }
        double sq=z.doubleValue();    sq=Math.ceil(Math.sqrt(sq));
        long min=Long.MAX_VALUE;
       double md=(double)min;
        int num=0;                          //счетчик
        while (sq>md){                      //если квадратный корень > long.MAS_VALUE
            sq=sq/md; num++;
        }
        long t=(long)sq;
        z=NumberZ.valueOf(t);
        z=(NumberZ)z.multiply(NumberZ.valueOf(min).pow(num,ring),ring);
        return z;
    }

/**Разложение полинома in по модулю p.
 *@param    in PolynomL - полином в Z
 *@param    p int - модуль
 *@return   d[] PolynomL - Массив полиномов разложение in в Zp
 */
    static Polynom[] Factor_p(Polynom in, Ring ring) {
        Polynom Pol=in.toPolynom(Ring.Zp32,ring);          //PolynomZ --> PolynomL
        Pol=FactorizationZp32OneVar.norm(Pol,ring);              //нормируем in
        Polynom d[]=FactorizationZp32OneVar.Berlecamp(Pol,ring);//Разложение в Zp Берлекампом
        return d;
    }



/**
 * Удаляет элементы равные null из массива Mass
 *@param Mass[] PolynomL
 *@return Mass[] PolynomL
 */
    private static Polynom[] CleanM(Polynom[] Mass) {
        int n=0;
        for(int i=0;i<Mass.length;i++){      //считаем ненулевые
            if(Mass[i]!=null)n++;
        }
        Polynom NM[]=new Polynom[n];
        int j=0;                             //счетчик для NM
        for(int i=0;i<Mass.length;i++){      //формируем новый массив из ненулевых
            if (Mass[i]!=null){
                NM[j]=Mass[i];
                j++;
            }
        }
        return NM;
    }

/**зануляет элементы с номерами из pointer[] в массиве Mass
 *@param Mass[] PolynomL
 *@param pointer[] int
 *@return Mass[] PolynomL
 */
    private static Polynom[] NullEM(Polynom[] Mass, int[] pointer) {
        for(int n=0;n<pointer.length;n++){      //зануляем ненужные
            Mass[pointer[n]]=null;
        }
        return Mass;
    }

/**Проверяет наличие элементов null в массиве Mass[pointer[]]
 *true  - если таковой есть
 *false - если нет
 *@param Mass[] PolynomL - проверяемый массив
 *@param pointer[] int - "указатели"
 *@return boolean
 */
    private static boolean BC(Polynom[] Mass, int[] pointer) {
  //      System.out.println("Arrays_Mass="+Arrays.toString(Mass));
   //     System.out.println("Arrays_pointer="+Arrays.toString(pointer));
        for(int i=0;i<pointer.length;i++){
   //         System.out.println("ttt=    "+pointer[i]);
   //          System.out.println("vvv="+Mass[pointer[i]]);
            if(Mass[pointer[i]]==null){
                return true;
            }
        }
        return false;
    }

}
