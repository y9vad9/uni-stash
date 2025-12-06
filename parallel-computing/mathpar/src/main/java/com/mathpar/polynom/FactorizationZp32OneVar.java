
/*Разложение полинома на множители по модулю числа p
 (FindQ)
 Полученный полином (PolynomL) преобразуется в 1-мерный массив содержащий
 коэффициенты (индексы=степеням). Далее строим матрицу Q[n][n] где  n=мах
 степень полинома. Первая строка всегда равна (1,0,0,...,0) и представляет
 полином x^0 mod u(x)=1. Вторая строка представляет x^p mod u(x), и, в общем
 x^{p*k} mod u(x), k={1,...,n-1}.
 Если u(x)=x^n + u_{n-1}*x^{n-1} +...+ u_1*x + u_0

 и если x^k = x_{k,n-1}*x^n-1 +...+ a_{k,1}*x + a_{k,0} (по модулю u(x)),

 то x^k+1 = a_{k,n-1}*x^n +...+ a_{k,1}*x^2 + a_{k,0}*x
 = a_{k,n-1}(-u_{n-1}*x^{n-1} -...- u_1*x-u_0) + a_{k,n-2}*x{n-1}+...+ a_{k,0}*x
 = a_{k+1,n-1}*x^{n-1} +...+ a_{k+1,1}*x + a_{k+1,0},
 где a_{k+1,j} = a_{k,j-1} - a_{k,n-1}*u_j.
 (QsubI)
 Q=Q-I где I единичная матрица размера n*n.
 (Triugol)
 На следующем шаге процедуры Берлекампа требуется найти линейно-независимые
 векторы v^[1], v^[2],...v^[r]. Алгоритм для этого основан на том наблюдении,
 что любой столбец матрицы А можно добавить к любому другому столбцу матрицы
 без изменения ранга матрицы. Таким образом, может быть использована следующая
 хорошо известная процедура "триангуляризации".
 Матрица Q размера n*n, элементы которой принадлежат полю и имеют индексы
 0<i,j<n. Этот алгоритм дает r векторов v^[1]...v^[r] линейно независимых над
 полем и удовлетворяющих условию v^[j]*Q = (0,...,0).

 N1. (Инициализация) Установить с_0 = с_1 =...= с_{n-1} = -1, r = 0. (Во время
 вычислений с_j >= 0 будет выполняться только тогда, когда q_{c_{j}j}=-1, а все
 другие элементы строки с_j будут нулевыми.)
 N2. (Цикл по k) Выполнить шаг N3 для k = 0,1,...,n-1, затем завершить работу
 алгоритма.
 N3. (Проверка зависимости строк) Если существует некоторое j из интервала
 0<j<n, такое, что а_kj <> 0 и c_j<0, то выполнить следующее. Умножить столбец
 j матрицы Q на -1/q_{kj} (так, чтобы q_{kj} стало равным -1), добавить
 умноженный на q_{ki} j-й столбец к i-му столбцу для всех i<>j и наконец
 установить с_j = k. С другой стороны, если не существует такого j из 0<=j<n,
 что q_{kj}<>0 и c_j<0? следует установить r=r+1 и вывести вектор
 v^[r]=(v_0,v_1,...,v_{n-1}), определяемый правилом
            q_ks, если c_s = j>=0;
    v_j = { 1,    если j=k;
            0, в противном случае.
 (NumVect) находим r - число линейно независимых векторов. В ответе получим r
 полиномов. Если r=1 то полином неразложим и программа вернет сам полином.
 (PolMass) Конвертируем ненулевые строки матрицы Q в массив PolynomL.
 (ForGCD)
 Вычислить НОД(u(x), v^[2](x)-s) для 0<=s<p, где v^[2], если использование
 v^[2](x) не приводит к разложению u(x) на r множителей, то дальнейшие множители
 могут быть получены посредством вычислений НОД(v^[k](x)-s,w(x)) для 0<=s<p и
 всех найденных множителей w(x) при к=3,4,..., пока не будут найдены все r
 множителей.

 */



package com.mathpar.polynom;

//import java.math.BigInteger;
//import matrix.*;
//import factorpol.*;
import com.mathpar.number.Ring;
import com.mathpar.number.*;


public class FactorizationZp32OneVar {

//******************* раскладывает полином на множители
     /*
      *Разложение полинома в Zp на множители по методу Берликемпа
      *@param in PolynomL раскладываемый полином
      *       p int простой модуль
      *@return Mnoz PolynomL[] массив полиномов полученных при разложении
      *
      */



     public static Polynom[] Berlecamp(Polynom in, Ring ring)  {
         int u[][] = convert(in); //конвертация in в двухстрочный массив коэффициентов и степеней
         int Q[][] = findQ(u,ring); //получение матрицы Q
         Q = QsubI(Q,ring); //вычитание единичной матрицы
         int vect[][] = Triugol(Q,ring ); //Приведение матрицы к треугольному виду
         int r = NumVect(vect); //Вычисление числа неприводимых множителей
        //    System.out.println("Berlec001=="+r+"");
         if (r == 1) { //если полином неразложим
             Polynom Mnoz[] = new Polynom[1]; //вывести входной полином
             Mnoz[0] = in;
             return Mnoz;
         }
        //   System.out.println("Berlec002  ");
         //Ring rZx = null;
         //rZx = new Ring("Z[x]");
         Polynom PolOut[] = PolMass(in, vect, r); // конвертирует вектора в массив Polynom
        //  System.out.println("pol_out="+Array.toString(PolOut));
        Polynom Mnoz[] = ForGCD(PolOut, r,ring); // разбивает полином на множители
   //    System.out.println("Berlec=="+Array.toString(Mnoz));
         return Mnoz;
     }

    /*Преобразует PolynomL в 2-мерный массив, где:
     *u[номер монома][0]=степень
     *u[номер монома][1]=коэффициент
     *@param in PolynomL преобразуемый полином
     *@return u int[][] полученный полином
     */
    public static int[][] convert(Polynom in) { //преборазование из PolynomL в 2-мерный массив
        int n = in.powers.length; //n = кол-во мономов
        int u[][] = new int[n][2]; //создаем матрицу n*2
        for (int i = 0; i < n; i++) { //заполняем ее:
            u[i][0] = in.powers[i]; //-степени
            u[i][1] = in.coeffs[i].intValue(); //-коэффициенты
        }
        return u; //возвращаем u
    }


    /*
         Нахождение матрицы Q
         где каждая строка x^(p*i)mod in(x) i = {0,1,2,....}
     *@param u int[кол-во мономов][2], p int простое число кольца
     *@return Q int[n][n], где n - маx степень в полиноме u
     */
    public static int[][] findQ(int u[][], Ring ring) {
        int p = (int)ring.MOD32;
        int n = u[0][0]; //максимальная степень полинома, она же размерность матрицы Q
        int Q[][] = new int[n][n];
        int u1[] = FactorizationZp32OneVar.NumMon(u);
        int tmp[] = new int[n]; // массив для хранения временных результатов
        int t[]; //как указатель для обмена tmp и Q[k]
        //Заполнение Q
        Q[0][0] = 1; //первая строка всегда {1,0,0,...,0}
        t = tmp; // t->tmp
        for (int k = 1; k < n; k++) { //перебор строк
            tmp = Q[k - 1]; //сохраняем в tmp предыдущую строчку
            for (int i = 0; i < p; i++) { //перебор до p*i шага,где i = {1,2,3,...}
                Q[k][0] = ring( -tmp[n - 1] * u1[0], p); //нахождение Q[k][0]
                for (int j = 1; j < n; j++) { //перебор в строке k
                    Q[k][j] = ring(tmp[j - 1] - tmp[n - 1] * u1[j], p); //нахождение Q[k][j], где j={1,2,3,...}
                }
                tmp = Q[k];
                Q[k] = t; //tmp <--> Q[k]
                t = tmp;
            } //после p раз
            tmp = Q[k];
            Q[k] = t; //Q[k] <--> tmp
            t = tmp;
        }
        return Q; //возврат Q
    }


    /*
     *Вычитание из исходной матрицы единичной
     *@param Q int[][] квадратная матрица
     *       p int простое число кольца
     *@return Q int[][] квадратная матрица у которой числа лежащие на главной диагонали < на 1
     */
    public static int[][] QsubI(int Q[][], Ring ring) {
        int n = Q.length; //находим размер матрицы
        for (int i = 0; i < n; i++) { //перебор значений гл. диагонали
            Q[i][i]--; //уменьшение значений гл. диагонали на 1
            if (Q[i][i] < 0) {
                Q[i][i] += (int)ring.MOD32; //если значение < 0, прибавить к нему p
            }
        }
        return Q; //возврат значения Q
    }


    /*
     *Приведение матрицы к треугольному виду
     *
     *@param Q int[][] квадратная матрица
     *       p int целое число кольца
     *@return v int[][] массив линейно независимых векторов
     */
    public static int[][] Triugol(int Q[][], Ring ring) {
        int p = (int)ring.MOD32;
        int r = 0; //кол-во линейно независимых векторов
        int n = Q.length; //размер матрицы Q
        int c[] = new int[n]; //датчик по столбцам
        for (int i = 0; i < n; i++) {
            c[i] = -1;
        } //
        int v[][] = new int[n][n]; //"Триангуализованная" матрица
        v[0][0] = 1;
        int sum; //счетчик суммы элементов в строке
        boolean was = false; //отмечает нулевую строку
        int uu; //1/Q[k][j] по модулю p)

        xxx:for (int k = 1; k < n; k++) { //перебор строк
            sum = 0; //счетчик суммы элементов в строке зануляется
            for (int j = 0; j < n; j++) { //перебор элементов строки
                sum = sum + Q[k][j]; //суммируем
                if (Q[k][j] != 0 && c[j] < 0) { //если в столбце небыло преобразований
                    uu = NumberZp32.inverse(Q[k][j],p);
                    for (int i = 0; i < n; i++) {
                        Q[i][j] = ring(Q[i][j] * ( -uu), p); //умножение всего столбца j на Q[k][j] (Q[k][j]=-1)
                    }
                    int z; //переменная для хранения Q[k][i]
                    for (int i = 0; i < n; i++) { //перебор всех столбцрв кроме i=j столбцам
                        if (i != j) {
                            z = Q[k][i];
                            for (int l = k; l < n; l++) { //перебор элементов столбца
                                Q[l][i] = ring(Q[l][j] * z + Q[l][i], p);
                            }
                        }
                    }
                    c[j] = k; //пометка строки
                    continue xxx;
                } //получен вектор
            } //перебор элементов строки
            r++;
            for (int j = 0; j < n; j++) { //перебор элементов строки
                if (j == k) {
                    v[r][j] = 1; //v[r][j]=1 если j=k
                } else {
                    int s;
                    for (s = 0; s < n && c[s] != j; s++) {
                        ;
                    }
                    if (s < n) {
                        v[r][j] = Q[k][s]; //копируем в v элемент из Q из s-ого столбца
                    }
                }
            }
        } //перебор строк
        r++; //незабываем единичный вектор {1,0,0,...,0}
        return v;
    }


    /*Конвертация 2-мерного массива в массив Polynom
     *@param vect int[][] "триангулизированныя" матрица
     *       r int кол-во полученных векторов
     *@return x Polynom[] Ленейно-независимые вектора
     */
    public static Polynom[] PolMass(Polynom in, int vect[][], int r) {
       // System.out.println("ARRAY==========in="+in);
        Polynom x[] = new Polynom[r]; //создаем массив размерностью n
        //Тут пойдет конвертация полученных векторов в PolynomL
        int z;
        int pows[];
        Element coefs[];
        int n = vect[0].length;
        x[0] = in;
        for (int i = 1; i < r; i++) { //перебор по векторам
            z = 0;
            for (int j = 0; j < n; j++) { //подсчет числа мономов в полиноме
                if (vect[i][j] != 0) {
                    z++;
                }
            }
            pows = new int[z]; //создаем массив степеней
            coefs = new Element[z]; //создаем массив коэффициентов
            int a = 0;
            for (int j = n - 1; j >= 0; j--) { //заполнение массивов для конструктора
                if (vect[i][j] != 0) { //если элемент матрицы <>0
                    pows[a] = j; //степень
                    coefs[a] = new NumberZp32(vect[i][j]); //коэффициент
                    a++; //следующий элемент
                }
            }
            x[i] = new Polynom(pows, coefs); //сам конструктор
        }
     //   System.out.println("ARRAY="+Array.toString(x));
        return x;
    }

    /*получение сомножителей полинома
     *@param in Polynom полином который раскладываем
     *       PolOut Polynom[] ЛН вектора
     *       r int кол-во ЛН Векторов
     *@return rezult Polynom массив векторов
     */
    public static Polynom[] ForGCD(Polynom[] PolOut, int r, Ring ring)  {
        Polynom rezult[] = new Polynom[r]; //массив полученных полиномов (ответ !!!)
        Polynom otv[] = null;
        Ring rZx = ring ;//new Ring("Z[x]");
        otv = PerebGSD(PolOut[0], PolOut[1],rZx); //c PolOut[max_l] и будем делать 1 перебор
        rezult[0] = otv[0];
        int n_rezult = 1;
        for (int i = 1; i < otv.length; i++) { //копируем полученные данные
            n_rezult = InMassGCD(rezult, otv[i], n_rezult, ring);
            if (InMass(rezult, otv[i],ring)==false){rezult[n_rezult] = otv[i];n_rezult++;}
        }
        if (n_rezult < r) {
          for (int i = 1; i < r; i++) { //с PolOut[i]
            for (int j = 0; j < n_rezult; j++) { //сравнить все rezult[j]
              otv = PerebGSD(rezult[j], PolOut[i], rZx); //получение НОДа               if (otv.length > 0) { //если НОДы есть
                for (int k = 0; k < otv.length; k++) { //добавляем
                  if (InMass(rezult, otv[k], ring) == false) { //если такого нет в rezult
                    n_rezult = InMassGCD(rezult, otv[k], n_rezult, ring); //Ищем его НОД с другими
                    if (InMass(rezult, otv[k], ring) == false) { //все равно нет
                      rezult[n_rezult] = otv[k]; //добавляем его
                      n_rezult++;
                    }
                  }
                }
              }
            } //end j
          } //end i
        return rezult;
    }

    /*Ищет НОД>1 в массиве полиномов Mass с переданным полином x ,
     *и отсутствие нода в массиве Mass и добавляет его
     */
    public static int InMassGCD(Polynom[] Mass, Polynom x, int n_rezult, Ring ring
                                ) {//Ring ring= Ring.ringZxyz;
        Polynom tmp = new Polynom();
        for (int i = 0; i < n_rezult; i++) { //перебор полиномов массива
            tmp = x.gcd(Mass[i], ring); //ищем его НОД с х

           // System.out.println("tmp="+tmp);
            //System.out.println(" tttt=  "+tmp.coeffs.length+x.powers[0]);
         //   Polynom vvv=new Polynom(new int[0],  new Element[] {new NumberZp32(1)}) , ring));
           // System.out.println("tmp="+tmp+"   "+ Polynom.polynom_one(NumberZp32.ONE));
            if (tmp.compareTo(
                    Polynom.polynom_one(NumberZp32.ONE)
                      ,ring) != 0 &&
                Mass[i].powers[0] > x.powers[0]) { //если НОД > 1 и Маss[i]>x

                Mass[i] = x; //Mass[i]=x
                if (InMass(Mass, tmp,ring) == false) {
                    Mass[n_rezult] = tmp;
                    n_rezult++;
                }
            }
        }
        return n_rezult;
    }

    /*проверяет имеется ли элемент x  в массиве Mass
     */
    public static boolean InMass(Polynom[] Mass, Polynom x,Ring ring) {
        for (int i = 0; i < Mass.length; i++) {
            if (Mass[i] != null && Mass[i].compareTo(x,ring) == 0) {
                return true; //уже есть
            }
        }
        return false; //такого нет
    }

    /*Возвращает массив содержащий НОДы >1 полученные при GSD(x,y-s), где s = {1,2,...,p-1}
     *@param x,y Polynom перебираемые полиномы
     *       p int число кольца
     *@return itog Polynom[] массив полиномов отличных от 1
     */
    public static Polynom[] PerebGSD(Polynom x, Polynom y, Ring ring) {
    //    System.out.println("x="+x+"     y="+y+"   R="+ring.MOD32);
        int p = (int)ring.MOD32;
        Polynom[] rz = new Polynom[p]; // здесь сохраниться весь результат
        Polynom ext = new Polynom(); //для работы
        Polynom s = Polynom.polynomFromNumber(NumberZp32.ONE, ring); //единичный полином
        int k = 0; //кол-во результатов перебора > 1
        Ring rZx = ring;//new Ring("Z[x]");
        for (int i = 0; i < p; i++) {
        //NumberZ c0 = (NumberZ)x.coeffs[0];
            ext = x.gcd(y,rZx);
            if (!ext.isOne(ring)) { //если результат <> 1 записать его
                rz[k] = ext;  k++;  }
            y = y.subtract(s,rZx);
        }
        Polynom itog[] = new Polynom[k]; // этот массив и будет возвращен
        for (int i = 0; i < k; i++) {

  //System.out.println("rz="+rz[i]);

            itog[i] = norm(rz[i],ring); //копируем максимальный полином в результаты
//System.out.println("itog="+itog[i]);
        }
   // System.out.println("itog="+Array.toString(itog));
        return itog;
    }

    /*Возвращает кол-во ненулевых строк массива
     *@param vect int[][] массив
     *@return r int кол-во строк сумма элементов которых >0
     */
    public static int NumVect(int vect[][]) {
   //     System.out.println("vect="+Array.toString(vect));
        int r = 0;
        int sum;
        for (int i = 0; i < vect.length; i++) {
            sum = 0;
            for (int j = 0; j < vect[i].length; j++) {
                sum = sum + vect[i][j];
            }
            if (sum > 0) {
                r++;
            }
        }
        return r;
    }

    /*возвращает полином деленый на коэффициент старшей степени
     *@param pl    полином
     *@param ring кольцo
     *@return pl Polynom полином с 1 в старшей степени
     */
    public static Polynom norm(Polynom p1, Ring ring) {
          if (p1.coeffs[0].longValue() != 1) {
            NumberZp32 a = (NumberZp32)p1.coeffs[0].inverse(ring);
            p1.coeffs[0] = NumberZp32.ONE;
            for (int i = 1; i < p1.coeffs.length; i++) {
                p1.coeffs[i] = ((NumberZp32)(p1.coeffs[i])).multiply(a,ring).Mod(ring);
            }
        }
        return p1;
    }


    /*возвращает переданный результат х в интервале кольца т.е. от 0 до р-1
     */

    public static int ring(int x, int p) {
        x = x % p;
        if (x < 0) {
            x = x + p;
        }
        return x;
    }

//************************************************

     /*
      Достраивает массив полинома до полного,
      т.е. вставляет в массив степени и коэфициенты мономов равных 0
      */
     public static int[] NumMon(int u[][]) {
         int n = u[0][0] + 1;
         int[] u1 = new int[n];

         for (int i = 0; i < n; i++) {
             for (int j = 0; j < u.length; j++) {
                 if (u[j][0] == i) {
                     u1[i] = u[j][1];
                 }
             }
         }
         return u1;
     }

    // Обратное к a по простому модулю  p.
    public static long p_Inverse(long a, long p) {
        int aa = (int) (a % p);
        int pp = (int) p;
        if (aa == 1) {
            return 1;
        } else if (aa == -1) {
            return -1;
        } else {
            int xy[] = new int[2];
            xy[0] = 0;
            xy[1] = 1;
            int qq = 0;
            m_Inverse(pp, aa, qq, xy);
            return (long) xy[0];
        }
    }

    // /////////////////////////////////////////////////////////////////////////////
// вычисляет обратное к b по простому модулю m (случай b=1 не проверяется)
//   xy[0] содержит b^{-1} mod p. При обращении q1, xy[0] и xy[1] нужно занулить
////////////////////////////////////////////////////////////////////////////////
    public static void m_Inverse(int m, int b, int q1, int[] xy) {
        int r, q, temp;
        r = m % b;
        q = (m - r) / b;
        if (r == -1) {
            xy[0] = -1;
            xy[1] = q;
        } else
        if (r == 1) {
            xy[0] = 1;
            xy[1] = -q;
        } else {
            m_Inverse(b, r, q, xy);
        }
        temp = xy[0];
        xy[0] = xy[1];
        xy[1] = temp - q1 * xy[1];
    }


// /////////////////////////////////////////////////////////////
// вычисляет обратное к a по простому модулю p  (общий случай) //
    public static int p_Inverse(int a, int p) {
        if (a == 1) {
            return 1;
        } else if (a == -1) {
            return -1;
        } else {
            int xy[] = new int[2];
            xy[0] = 0;
            xy[1] = 1;
            int qq = 0;
            m_Inverse(p, a, qq, xy);
            return xy[0];
        }
    }

}
