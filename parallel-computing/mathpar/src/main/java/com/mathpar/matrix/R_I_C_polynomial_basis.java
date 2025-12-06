package com.mathpar.matrix;

import com.mathpar.number.Array;
import com.mathpar.number.Element;
import com.mathpar.number.Fraction;
import com.mathpar.number.NumberZ;
import com.mathpar.number.Ring;
import com.mathpar.number.VectorS;
import com.mathpar.polynom.Polynom;
import com.mathpar.polynom.PolynomOneVar;

/**
 * Класс, содержащий методы нахождения рационального, целого и какнонического базисов
 * неопределенной системы линейных уравнений.
 * РЕШАЕМ ТОЧНО ПОЛИНОМИАЛЬНЫЕ СИСТЕМЫ и СТРОИМ ЦЕЛЫЕ БАЗИСЫ
 * @author oksana Sazhneva
 */
public class R_I_C_polynomial_basis {

  /**
   * метод, удаляющий из матрицы столбцы с номерами, указанными в массиве
   * @param Mat - входная матрица
   * @param cols - массив номеров столбцов, которые будут удалены
   * @param ring - кольцо
   * @return - матрица, без указанных столбцов
   */
  public static MatrixS delColumns(MatrixS Mat, int[] cols, Ring ring) {
    VectorS[] fores = new VectorS[Mat.colNumb - cols.length];
    int k = 0;
    for (int i = 0; i < Mat.colNumb; i++) {
      boolean flag = false;
      for (int j = 0; j < cols.length; j++) {
        if (i == cols[j]) {
          flag = true;
        }
      }
      if (!flag) {
        fores[k] = getColumn(Mat, i, ring);
        k++;
      }
    }
    MatrixS res = new MatrixS(fores, ring);
    return res;
  }

  /**
   * метод возвращающий n-ый столбец из матрицы
   * @param Mat - входная матрица
   * @param n - номер столбца
   * @return - столбец матрицы в виде VectorS
   */
  public static VectorS getColumn(MatrixS Mat, int n, Ring ring) {
    VectorS res = new VectorS(Mat.size);
    if (n > Mat.colNumb - 1) {
      for (int i = 0; i < res.V.length; i++) {
        res.V[i] = NumberZ.ZERO;
      }
    } else {
      for (int i = 0; i < Mat.size; i++) {
        res.V[i] = Mat.getElement(i, n, ring);
      }
    }
    return res;
  }

  /**
   * процедура нахождения подвектора вектора v
   * @param v - вектор
   * @param n - номер первой координаты подвектора
   * @param m - номер последней координаты включительно
   * @return вектор, состоящий из координат вектора v с n-ой до m-ой
   */
  public static VectorS getSubVectorS(VectorS v, int n, int m) {
    int s = m - n + 1;
    VectorS res = new VectorS(s);
    if ((v.V.length == 0) || (v.V.length < s)) {
      return v;
    } else {
      int k = 0;
      for (int i = n; i <= m; i++) {
        res.V[k] = v.V[i];
        k++;
      }
    }
    return res;
  }

  /**
   * возвращает матрицу свободных членов
   * @param Mat - входная матрица
   * @return матрицу свободных членов типа MatrixS
   */
  public static MatrixS MatrixS_mod_x(MatrixS Mat, Ring ring) {
    Element[][] ress = new Element[Mat.M.length][];
    if (Mat.isZero(ring)) {
      return Mat;
    }
    for (int i = 0; i < Mat.M.length; i++) {
      int len_i = Mat.M[i].length;
      Element[] Mstr = Mat.M[i];
      Element[] MstrRes = new Element[len_i];
      ress[i] = MstrRes;
      for (int j = 0; j < len_i; j++) {
        Polynom m;
        Element g = Mstr[j];
        Element resE = null;
        if (g instanceof Fraction) {
          g = ((Fraction) g).num;
        }
        resE = (g instanceof Polynom) ? ((Polynom) g).constantTermOneVar(ring) : g;
        MstrRes[j] = resE.toNewRing(ring.algebra[0], ring);
      }
    }
    MatrixS res = new MatrixS(ress, ring);
    return res;
  }

  /**
   * возвращает вектор свободных членов
   * @param Mat - входной вектор
   * @return вектор свободных членов типа VectorS
   */
  public static VectorS VectorS_free_coeffs(VectorS vec, Ring ring) {
    VectorS res = new VectorS(vec.V.length);
    for (int i = 0; i < vec.V.length; i++) {
      if (vec.V[i].isZero(ring)) {
        res.V[i] = vec.V[i];
      } else {
        res.V[i] = ((Polynom) vec.V[i]).constantTermOneVar(ring);
      }
    }
    return res;
  }

  /**
   * метод, нахдения вектора по модулю х
   * @param vec - вектор
   * @param ring - кольцо
   * @return вектор по модулю х
   */
  public static VectorS VectorSmod_x(VectorS vec, Ring ring) {
    if (vec.isZero(ring)) {
      return vec;
    }
    int n = vec.V.length;
    VectorS z = new VectorS(n);
    Element[] ZZ = z.V;
    for (int i = 0; i < n; i++) {
      Polynom v;
      if (vec.V[i] instanceof Polynom) {
        v = (Polynom) vec.V[i];
        v = v.toPolynom(ring);
        ZZ[i] = PolynomOneVar.mod_x(v, ring);
      } else {
        ZZ[i] = vec.V[i];
      }

    }
    return z;
  }

  /**
   * метод, определяющий, принадлежит ли число j массиву
   * @param j - число
   * @param arr - массив
   * @return  - true - если принадлежит,false - если нет
   */
  public static boolean inArray(int j, int[] arr) {
    if (arr == null) {
      return false;
    }
    for (int i = 0; i < arr.length; i++) {
      if (j == arr[i]) {
        return true;
      }
    }
    return false;
  }

  /**
   * метод, приводящий полином с рациональными коэффициентами к общему знаменателю
   * @param q1 - полином
   * @return массив Element[] на первом месте дробь Fraction=a/ОЗ , где a-это НОД числлителей
   * а на втором - Polynom - получившийся полином
   */
  public static Element[] CommonCoeffDenom(Polynom q1) {
    Ring ring = new Ring("Z[x]");
    Element[] res = new Element[2];
    if (q1.coeffs.length == 0) {
      res[0] = Fraction.Z_ZERO;
      res[1] = q1;
      return res;
    }
    if (q1.coeffs.length == 1) {
      res[0] = Fraction.Z_ONE;
      res[1] = q1;
      return res;
    } else {
      Fraction[] coefs = new Fraction[q1.coeffs.length];
      for (int i = 0; i < coefs.length; i++) {
        coefs[i] = (Fraction) q1.coeffs[i];
      }
      //заполняем массив числителей
      Element[] c1 = new Element[q1.coeffs.length];
      for (int i = 0; i < q1.coeffs.length; i++) {
        c1[i] = coefs[i].num;
      }
      //полином, коэффициенты которого равны числителям коэффициентов q1
      Polynom q11 = new Polynom(q1.powers, c1);//полином, коэффициенты которого равны числителям коэффициентов q1
      Element oz_num = q11.GCDNumPolCoeffs(ring);//a

      //массив домножителей
      Element[] dom = new Element[q1.coeffs.length];
      //заполняем массив знаменателей
      Element[] c2 = new Element[q1.coeffs.length];
      for (int i = 0; i < q1.coeffs.length; i++) {
        c2[i] = coefs[i].denom;
      }
      //находим нок
      Element oz_denom = NumberZ.ONE;
      for (int i = 0; i < c2.length; i++) {
        oz_denom = oz_denom.LCM(c2[i], ring);
      }
      for (int i = 0; i < c2.length; i++) {
        dom[i] = oz_denom.divide(c2[i], ring);
      }
      Element[] res_coef = new Element[q1.coeffs.length];
      for (int i = 0; i < res_coef.length; i++) {
        res_coef[i] = q11.coeffs[i].divide(oz_num, ring).multiply(dom[i], ring);
      }
      Polynom result = new Polynom(q1.powers, res_coef);
      Fraction result1 = new Fraction(oz_num, oz_denom);
      res[0] = result1;
      res[1] = result;
      return res;
    }

  }

  /**
   * метод, находящий НОД Fraction компонент вектора
   * @param v - вектор
   * @return НОД его компонент
   */
  public static Fraction GCD(VectorS v, Ring ring) {
    VectorS v1 = VecCommonDenom(v, ring);
    Polynom[] r = new Polynom[v.V.length];
    Polynom den = new Polynom("1", ring);
    for (int i = 0; i < v1.V.length; i++) {
      Fraction v11;
      if (v1.V[i] instanceof Fraction) {
        v11 = (Fraction) v1.V[i];
      } else {
        v11 = new Fraction(v1.V[i], new Polynom("1", ring));
      }
      Polynom v12;
      if (v11.num instanceof Polynom) {
        v12 = (Polynom) v11.num;
      } else {
        v12 = new Polynom(v11.num);
      }
      boolean test = true;
      if (test) {
        if (!v11.denom.isOne(ring)) {
          if (v11.denom instanceof Polynom) {
            den = (Polynom) v11.denom;
          } else {
            den = new Polynom(v11.denom);
          }
        }
        test = false;
      }
      r[i] = v12;
    }
    int k = 0;
    while (r[k].isZero(ring)) {
      k = k + 1;
    }
    Polynom gcd = r[k];
    for (int i = k + 1; i < r.length; i++) {
      if (!r[i].isZero(ring)) {
        gcd = gcd.gcd(r[i], ring);
      }
    }
    Fraction res = new Fraction(gcd, den);
    return res;
  }

  /**
   * Метод Ванга восстановления полиномиальной дроби в кольце Q[x] по модулю и остатку
   * @param u - остаток
   * @param m - модуль
   * @param ring - кольцо Q[x]
   * @return  массив полиномов от одной переменной, содержащий числитель и знаменатель полученной дроби
   */
  public static PolynomOneVar[] Vang(PolynomOneVar u, PolynomOneVar m, Ring ring) {
    PolynomOneVar[] res = new PolynomOneVar[2];
    PolynomOneVar a1 = m;
    PolynomOneVar a2 = u;
    PolynomOneVar b1 = new PolynomOneVar(Polynom.polynomZero);
    PolynomOneVar b2 = new PolynomOneVar(ring.polynomONE);
    int deg = m.degree() / 2;
    while (a2.degree() > deg) {
      if (b2.degree() > deg) {
        return null;
      } else {
        System.out.println("a1=" + a1);
        System.out.println("a2=" + a2);
        PolynomOneVar[] div = a1.divAndRem(a2, ring);
        PolynomOneVar q = new PolynomOneVar(div[0].divideExact(div[2], ring));//целая часть
        System.out.println("q=" + q);
        PolynomOneVar mult_a = new PolynomOneVar(a2.multiply(q, ring));
        Polynom ppp = b2.multiply(q, ring);
        PolynomOneVar mult_b = new PolynomOneVar(ppp);
        System.out.println("ma=" + mult_a);
        System.out.println("mb=" + mult_b);
        PolynomOneVar h1 = new PolynomOneVar(a1.subtract(mult_a, ring));
        PolynomOneVar h2 = new PolynomOneVar(b1.subtract(mult_b, ring));
        a1 = a2;
        b1 = b2;
        a2 = h1;
        b2 = h2;

        Element[] com_up = CommonCoeffDenom(a2);
        Element[] com_down = CommonCoeffDenom(b2);
        Fraction d1 = (Fraction) com_up[0];
        Fraction d2 = (Fraction) com_down[0];
        Element domE = d1.divide(d2, ring);
        Fraction dom = null;
        if (domE instanceof Fraction) {
          dom = (Fraction) domE;
        } else {
          dom.num = domE;
          dom.denom = NumberZ.ONE;
        }
        //  Fraction dom=d1.divide(d2, ring);
        Polynom p1 = (Polynom) com_up[1];
        Polynom p2 = (Polynom) com_down[1];
        Element up = p1.multiply(dom.num, ring);
        Element down = p2.multiply(dom.denom, ring);
        res[0] = new PolynomOneVar(new Polynom(up));
        res[1] = new PolynomOneVar(new Polynom(down));
      }
    }
    return res;
  }

  /**
   * метод, вычисляющий границу р-адического подъема (степень полинома)
   * @param A - подматрица исходной матрицы( с рангом r) размером r x (r-1)
   * @param c1 - столбец исходной матицы, дополняющий матрицу А до квадратной размером r x r
   * @param c - столбец c исходной системы уравнений
   * @return число - границу р-адического подъема
   */
  public static int P_border(MatrixS A, VectorS c1, VectorS c, Ring ring) {
    int p = 0;
    int n1 = 0;
    int n2 = 0;
    int m1 = 0;
    //вычисляем норму матрицы А как наибольшую из норм ее элементов
    for (int i = 0; i < A.colNumb; i++) {//по столбцам
      int n3 = 0;
      for (int j = 0; j < A.size; j++) {//по элементам столбцов
        Element el = A.getElement(j, i, ring);
        if (el instanceof Polynom) {
          Polynom el1 = (Polynom) el;
          if (el1.powers.length != 0) {
            int s = el1.powers[0];
            if (s > n3) {
              n3 = s;
            }
          }
        }
      }
      if (i == 0) {
        m1 = n3;
      }
      n1 = n1 + n3;
      if (m1 > n3) {
        m1 = n3;
      }
    }
    //вычисляем норму вектора с1
    for (int i = 0; i < c1.V.length; i++) {
      if (c1.V[i] instanceof Polynom) {
        Polynom el = (Polynom) c1.V[i];
        if (el.powers.length != 0) {
          int s = el.powers[0];
          if (s > n2) {
            n2 = s;
          }
        }
      }
    }
    int p1 = n1 + n2;
    int m;
    if (m1 < n2) {
      m = m1;
    } else {
      m = n2;
    }
    //вычисляем норму вектора с
    for (int i = 0; i < c.V.length; i++) {
      if (c.V[i] instanceof Polynom) {
        Polynom el = (Polynom) c.V[i];
        if (el.powers.length != 0) {
          int s = el.powers[0];
          if (s > n2) {
            n2 = s;
          }
        }
      }
    }
    int p2 = p1 + n2 - m;
    p = p1 + p2;


    return p;
  }

  /**
   * Метод, вычисляющий расширенный НОД нескольких многочленов(не дробей!)
   * @param v - вектор, координаты которого многочлены, НОД которых неоюходимо вычислить
   * @return - вектор res,длиной (v.V.length+1),содержащий на месте [0]-НОД многочленов,
   * на остальных местах домножители многочленов, такие что:
   * res[0] = v.V[0]*res[1] + ... + v.V[n]*res[n+1]
   */
  public static VectorS extGCD(VectorS v) {
    Ring ring = new Ring("Q[x]");//работаем в поле Q так как в нем возможно
    //производить сразу деление на последнюю координату вектора, полученного при применении процедуры
    //extendedGCD. В Z необходимо действовать строго по формуле описанной в комментариях к процедуре.
    Element two = new Polynom("2", ring);
    VectorS res = new VectorS(v.V.length + 1);//конечный массив
    for (int i = 0; i < res.length(); i++) {
      res.V[i] = Fraction.Z_ONE;
    }//объявляем массив состоящим из единиц
    Element gcd = v.V[0];
    for (int i = 1; i < v.V.length; i++) {
      Element vi = v.V[i];
      if (isNumber(vi, ring) == 0 || isNumber(gcd, ring) == 0) {//если один из элементов число
        if (isNumber(vi, ring) == 0 && isNumber(gcd, ring) == 0) {//если оба элемента числа
          //если следующий элемент равен нулю
          if (vi.isZero(ring)) {
            res.V[0] = gcd;
            res.V[i + 1] = NumberZ.ONE;
          } else {
            Element g1 = gcd.GCD(vi, ring);
            res.V[0] = g1;
            //коэффициенты расширенного алгоритма вычисляются следующим обраом: НОД/(2*полином)
            for (int j = 1; j <= i; j++) {
              Fraction p = CancelFract(new Fraction(g1, two.multiply(vi, ring)));
              Fraction p1 = (Fraction) res.V[j];
              res.V[j] = CancelFract((Fraction) p1.multiply(p, ring));
              //res.V[j]=res.V[j].multiply(p, ring);
            }
            Fraction p1 = CancelFract(new Fraction(g1, two.multiply(gcd, ring)));
            res.V[i + 1] = p1;
            gcd = res.V[0];
          }
        } else {
          //в этом случае коэффициент при полиноме будет равен нулю
          //а при числе a/число
          Element pol = Fraction.Z_ONE;
          Element num = Fraction.Z_ONE;
          if (isNumber(vi, ring) == 0) {
            pol = gcd;
            num = vi;//если предыдущий нод оказался полиномом
            if (vi.isZero(ring)) {
              res.V[0] = pol;
              res.V[i + 1] = NumberZ.ONE;
            } else {
              Polynom gcd_new = (Polynom) pol;
              Element gcd_coef = gcd_new.GCDNumPolCoeffs(ring);
              gcd = gcd_coef.GCD(num, ring);
              res.V[0] = gcd;
              for (int j = 1; j <= i; j++) {
                res.V[j] = Fraction.Z_ZERO;
              }
              Fraction p = CancelFract(new Fraction(gcd, num));
              res.V[i + 1] = p;
            }
          }
          if (isNumber(gcd, ring) == 0) {
            pol = vi;
            num = gcd;//если предыдущий нод число
            if (gcd.isZero(ring)) {
              res.V[0] = pol;
              res.V[i + 1] = NumberZ.ONE;
            } else {
              Polynom gcd_new;
              if (pol instanceof Fraction) {
                gcd_new = (Polynom) ((Fraction) pol).num;
              } else {
                gcd_new = (Polynom) pol;
              }
              //Polynom gcd_new = (Polynom)pol;
              Element gcd_coef = gcd_new.GCDNumPolCoeffs(ring);
              gcd = gcd_coef.GCD(num, ring);
              res.V[0] = gcd;
              for (int j = 1; j <= i; j++) {
                Fraction p = CancelFract(new Fraction(gcd, num));
                Fraction p1 = (Fraction) res.V[j];
                res.V[j] = CancelFract((Fraction) p1.multiply(p, ring));
              }
              res.V[i + 1] = Fraction.Z_ZERO;
            }
          }
        }
      } else {
        Polynom gcd1;
        Polynom vi1;
        if (gcd instanceof Fraction) {
          Fraction g = (Fraction) gcd;
          gcd1 = (Polynom) g.num;
        } else {
          gcd1 = (Polynom) gcd;
        }
        if (vi instanceof Fraction) {
          Fraction g = (Fraction) vi;
          vi1 = (Polynom) g.num;
        } else {
          vi1 = (Polynom) vi;
        }

        VectorS ext = gcd1.extendedGCD(vi1, ring);
        res.V[0] = ext.V[0];//НОД изменился
        for (int j = 1; j <= i; j++) {
          Fraction p = CancelFract(new Fraction(ext.V[1], ext.V[3].multiply(ext.V[5], ring)));
          Fraction p1 = (Fraction) res.V[j];
          res.V[j] = CancelFract((Fraction) p1.multiply(p, ring));
        }
        Fraction p1 = CancelFract(new Fraction(ext.V[2], ext.V[4].multiply(ext.V[5], ring)));
        Fraction p2 = (Fraction) res.V[i + 1];
        res.V[i + 1] = CancelFract((Fraction) p2.multiply(p1, ring));
        gcd = res.V[0];

      }
    }

    return res;
  }

  /**
   * метод, проверяющий является ли элемент числом или это полином
   * @param el - элемент, который необходимо проверить
   * @param ring - кольцо
   * @return 1-если это полином, 0-если число
   */
  public static int isNumber(Element el, Ring ring) {
    int res = 0;
    String s = el.toString(ring);
    String[] vars = new String[ring.varNames.length];
    for (int i = 0; i < vars.length; i++) {
      vars[i] = ring.varNames[i];
    }//заполняем массив строк именами переменных кольца
    for (int i = 0; i < vars.length; i++) {
      if (s.contains(vars[i])) {
        res = 1;
        return res;
      }
    }
    return res;
  }

  /**
   * метод, сокращающий числитель и знаменатель дроби на их нод
   * @param f - дробь с числовыми или полиномиальными числителем и знаменателем
   * @return сокращенную дробь
   */
  public static Fraction CancelFract(Fraction f) {
    Ring ring = new Ring("Z[x]");
    Polynom num;
    Polynom denom;
    if (f.isZero(ring)) {
      return f;
    }
    if (f.num instanceof Polynom) {
      num = ((Polynom) f.num).toPolynom(ring);
    } else {
      num = (new Polynom(f.num)).toPolynom(ring);
    }
    if (f.denom instanceof Polynom) {
      denom = ((Polynom) f.denom).toPolynom(ring);
    } else {
      denom = (new Polynom(f.denom)).toPolynom(ring);
    }
    Element gcd_num = num.GCDNumPolCoeffs(ring);
    Element gcd_denom = denom.GCDNumPolCoeffs(ring);
    Element gcd = gcd_num.GCD(gcd_denom, ring);
    f.num = num.divideByNumber(gcd, ring);
    f.denom = denom.divideByNumber(gcd, ring);

    return f;
  }

  /**
   * Метод, который приводит вектор с координатами типа Fraction к виду, в котором
   * все знаменатели координат одинаковы
   * @param v - входной вектор
   * @return - вектор в новом виде
   */
  public static VectorS VecCommonDenom(VectorS v, Ring ring) {
    VectorS res = new VectorS(v.V.length);
    Fraction[] vec = new Fraction[v.V.length];
    for (int i = 0; i < vec.length; i++) {
      if (v.V[i] instanceof Fraction) {
        vec[i] = (Fraction) v.V[i];
      } else {
        if (v.V[i].isZero(ring)) {
          vec[i] = new Fraction(NumberZ.ZERO, NumberZ.ONE);
        } else {
          vec[i] = new Fraction(v.V[i], NumberZ.ONE);
        }
      }
    }
    Element[] com = CommonDenom(vec);
    for (int i = 0; i < vec.length; i++) {
      Element num = (vec[i].num).multiply(com[i + 1], ring);
      Fraction r;
      if (num.isZero(ring)) {
        r = Fraction.Z_ZERO;
      } else {
        r = new Fraction(num, com[0]);
      }
      res.V[i] = r;
    }
    return res;
  }

  /**
   * метод приводящий несколько полиномиальных дробей к общему знаменателю
   * @param pols - массив дробей вида a/b , где a и b - полиномы
   * @return массив элементов, на нулевом месте которого общий знаменатель, на остальных
   * домножители соответствующих числителей
   */
  public static Element[] CommonDenom(Fraction[] pols) {
    Ring ring = new Ring("Z[x]");
    Element[] res = new Element[pols.length + 1];//результат
    for (int i = 0; i < res.length; i++) {
      res[i] = NumberZ.ONE;
    }
    Element[] denoms = new Element[pols.length];//массив знаменателей
    for (int i = 0; i < denoms.length; i++) {//заполняем массив знаменателей
      if (pols[i].isZero(ring)) {
        denoms[i] = NumberZ.ONE;
      } else {
        denoms[i] = pols[i].denom;
      }
    }
    if (denoms.length == 1) {
      res[0] = denoms[0];
      res[1] = NumberZ.ONE;
    }//если всего одна дробь в массиве
    else {
      Element Common = denoms[0];
      for (int i = 1; i < denoms.length; i++) {
        Element d1;
        Element d2;
        Element gcd;
        if (Common.isZero(ring)) {
          d1 = NumberZ.ONE;
          d2 = NumberZ.ONE;
          gcd = denoms[i];
        }
        if (Common.isOne(ring)) {
          d1 = NumberZ.ONE;
          d2 = denoms[i];
          gcd = NumberZ.ONE;
        } else {
          gcd = Common.GCD(denoms[i], ring);
          d1 = Common.divide(gcd, ring);
          d2 = denoms[i].divide(gcd, ring);
        }
        res[i + 1] = d1;
        for (int j = 1; j < i; j++) {
          res[j + 1] = res[j + 1].multiply(d2, ring);
        }
        Common = gcd.multiply(d1, ring).multiply(d2, ring);//полученный на данном этапе ОЗ
        res[0] = Common;
      }
    }
    return res;
  }

  /**
   * Метод, вычисляющий рациональный базис неоднородной системы уравнений над полиномами из поля Q[x]
   * система задана уравнением: Ay=c
   * @param A - матрица A,строки и столбцы которой линейно независимы
   * @param c - вектор значений, ненулевой
   * @return массив решений системы, являющийся рациональным базисом данной системы
   */
  public static VectorS[] RationalBasis(MatrixS A, VectorS c) {
    //в качестве простого элемента кольца рассматриваем х
    //объявляем переменные
    Ring ring = new Ring("Q[x]");
    Polynom x = ring.varPolynom[0];
    Element one = NumberZ.ONE;
    PolynomOneVar one_pol = new PolynomOneVar(new Polynom(one));
    //определяем ранг матрицы A, исходя из условия, что все ее строки и столбцы линейно независимы
    int rankA = A.rank(ring);
    //проверяем, не будет ли ранг матрицы А меньше ранга матрицы (А,с),и если это так возвращаем null
    MatrixS A_c = A.appendColumn(c, ring);
    int rankA_c = A_c.rank(ring);
    if (rankA < rankA_c) {
      return null;
    }
    //исходя из ранга матрицы, разбиваем ее на блоки A0 и A1
    MatrixS A_0 = A.getSubMatrix(0, rankA - 1, 0, rankA - 1);
    int[] del = new int[rankA];
    MatrixS A_1;
    Polynom nol = Polynom.polynomZero;
    Element[] if1 = new Element[A.size];
    for (int i = 0; i < A.size; i++) {
      if1[i] = nol;
    }
    if (rankA == A.colNumb && rankA == A.size) {
      A_1 = new MatrixS(new VectorS(if1));
    } else {
      for (int i = 0; i < rankA; i++) {
        del[i] = i;
      }
      A_1 = delColumns(A, del, ring);
    }
    //проверяем, чему равен определитель А_0
    Element detA_0 = A_0.det(ring);
    System.out.println("det A_0=" + detA_0);
    if (detA_0.compareTo(NumberZ.ZERO, 0, ring)) {//если определитель равен нулю,
      //то путем перестановок получаем матрицу у которой определитель будет не равен нулю
      while (detA_0.compareTo(NumberZ.ZERO, 0, ring)) {//до тех пор пока определитель равен 0
        VectorS k_col = getColumn(A, 0, ring);//берем нулевой столбец исходной матрицы
        A = delColumns(A, new int[0], ring);//удаляем из матрицы нулевой столбец
        A = A.appendColumn(k_col, ring);//и ставим его в конец матрицы
        A_0 = A.getSubMatrix(0, rankA - 1, 0, rankA - 1);//вычисляем новую матрицу А_0
        detA_0 = A_0.det(ring);//вычисляем определитель матрицы А_0
      }
      System.out.println("new A=" + A);
      System.out.println("detA_0" + detA_0);
    }
    int J[] = null;

    //проверяем, существует ли матрица А_1, если нет, то будем вычислять единственный ответ
    if (A_1.isZero(ring)) {
      //вычисляем матрицу (B,b) mod x
      Element[] det11 = new Element[1];
      det11[0] = one;
      MatrixS A_0x1 = MatrixS_mod_x(A_0, ring);
      //обратная матрица к А_0х
      MatrixS adjA_0x1 = A_0x1.adjointDet(det11, ring);
      System.out.println("a=" + adjA_0x1);
      VectorS B_b1 = adjA_0x1.multiplyByColumn(VectorS_free_coeffs(c, ring), ring);
      System.out.println("b=" + B_b1);
      Element b_last1 = B_b1.V[B_b1.V.length - 1];
      //проверяем последний элемент матрицы B_b
      if (b_last1.isZero(ring)) {
        J = new int[1];
        J[0] = 0;
        if (B_b1.isZERO(ring)) {
          System.out.println("Последний столбец матрицы B_b равен нулю!");
          System.out.println("Дальнейшее решение по данному методу невозможно!");
          return null;
        }
      }
    } //------------------------------------------------------
    //вычисляем матрицу (B,b) mod x если матрица А_1 определена
    else {
      Element[] det = new Element[1];
      det[0] = one;
      MatrixS A_0x = MatrixS_mod_x(A_0, ring);
      MatrixS A_1x = MatrixS_mod_x(A_1, ring);
      MatrixS A_1xc = A_1x.appendColumn(VectorS_free_coeffs(c, ring), ring);
      //обратная матрица к А_0х
      MatrixS adjA_0x = A_0x.adjointDet(det, ring);
      MatrixS B_b = adjA_0x.multiply(A_1xc, ring);
      System.out.println("b=" + B_b);
      //проверяем, равен ли последний элемент нулю
      VectorS b = getColumn(B_b, rankA - 1, ring);
      Element b_last = b.V[b.V.length - 1];
      //проверяем последний элемент матрицы B_b
      if (b_last.isZero(ring)) {
        if (b.isZERO(ring)) {
          System.out.println("Последний столбец матрицы B_b равен нулю!");
          System.out.println("Дальнейшее решение по данному методу невозможно!");
          return null;
        }
        System.out.println("Последний элемент матрицы B_b равен нулю!");
        return null;
      }
      //заполняем множество номеров нулевых столбцов
      int[] j1 = new int[B_b.colNumb - 1];
      int k = 0;
      for (int i = 0; i < B_b.colNumb; i++) {
        MatrixS el = B_b.getSubMatrix(B_b.size - 1, B_b.size - 1, i, i);
        if (el.isZero(ring)) {
          j1[k] = i;
          k = k++;
        }
      }
      J = new int[k];
      for (int i = 0; i < J.length; i++) {
        J[i] = j1[i];
      }

    }
    //ring = new Ring("Z[x]");
    //определяем блок вектора с - с_0
    VectorS c_0 = getSubVectorS(c, 0, rankA - 1);
    //определяем сколько векторов мы должны получить в результате
    int res_len = A.colNumb - (rankA - 1);
    VectorS[] end_result = new VectorS[res_len];
    //вычисляем матрицу A_0 без последнего столбца по модулю х
    MatrixS A_0_1 = MatrixS_mod_x(delColumns(A_0, new int[]{rankA - 1}, ring), ring);
    //определяем сколько координат будут иметь векторы результата
    for (int i = 0; i < end_result.length; i++) {
      end_result[i] = new VectorS(A.M[0].length);
      for (int l = 0; l < end_result[i].V.length; l++) {
        end_result[i].V[l] = Fraction.Z_ZERO;
      }
    }
    //начинаем вычисление с помощью цикла
    for (int j = 0; j < end_result.length; j++) {
      VectorS col;
      MatrixS A0x;
      int col_num = A_0_1.colNumb + j;
      //если номер столбца не принадлежит J
      if (!inArray(j, J)) {
        col = getColumn(A, col_num, ring);
      } //если номер столбца принадлежит J
      else {
        VectorS col1 = getColumn(A, col_num, ring);
        VectorS col2 = getColumn(A, rankA - 1, ring);
        col = col1.add(col2, ring);
      }
      //определяем границу р-адического подъема
      int p_border = P_border(A.getSubMatrix(0, A.size - 1, 0, rankA - 2), col, c, ring);
      //вычисляем матрицу Аj
      MatrixS Aj = delColumns(A_0, new int[]{rankA - 1}, ring).appendColumn(col, ring);
      //вычисляем матрицу А_j по модулю х
      MatrixS Ajx = A_0_1.appendColumn(VectorS_free_coeffs(col, ring), ring);
      //вычисляем к ней обратную матрицу
      Element[] det1 = new Element[1];
      det1[0] = one;
      MatrixS adjAj = Ajx.adjointDet(det1, ring);
      Element determ = det1[0];//определитель полученной матрицы
      //вычисляем решение с помощью р-адического подъема
      VectorS[] res = new VectorS[p_border + 1];//массив, в который будет записан результат р-адического подъема
      VectorS[] cs = new VectorS[p_border + 2];//вспомогательный массив
      cs[0] = c;
      for (int i = 0; i <= p_border; i++) {
        VectorS cx = cs[i];
        VectorS y = VectorSmod_x(adjAj.multiplyByColumn(cx, ring), ring);
        VectorS left = (VectorS)cx.multiply(determ, ring);
        VectorS right = Aj.multiplyByColumn(y, ring);
        VectorS skob = (left.subtract(right, ring)).divide(x, ring);
        cs[i + 1] = skob;
        res[i] = y;
      }
      //записываем полученные решения
      res_len = rankA;
      PolynomOneVar[] result = new PolynomOneVar[res_len];
      for (int i = 0; i < res_len; i++) {
        result[i] = new PolynomOneVar(Polynom.polynomZero);
      }
      //ring=new Ring("Q[x]");
      for (int i = 0; i < res_len; i++) {
        for (int l = 0; l < p_border + 1; l++) {
          PolynomOneVar v = new PolynomOneVar(Polynom.polynomFromNumber(res[l].V[i], ring));
          PolynomOneVar x_j = new PolynomOneVar((Polynom) x.pow(l, ring));
          Polynom r1 = v.multiply(x_j, ring);
          PolynomOneVar vx = new PolynomOneVar(r1);
          Element r2 = determ.pow(l + 1, ring);
          vx.coeffs[0] = (vx.coeffs[0].toNewRing(ring.algebra[0], ring)).divide(r2, ring);
          result[i] = new PolynomOneVar(result[i].add(vx, ring));
        }
      }
      System.out.println("ar=" + Array.toString(result));


      //восстанавливаем дробь с помощью алгоритма Ванга
      Element x_p = x.pow(p_border, ring);
      Polynom m1 = (Polynom) x_p;
      PolynomOneVar m = new PolynomOneVar(m1);
      for (int l = 0; l < result.length; l++) {
        PolynomOneVar[] vang = Vang(result[l], m, ring);
        Fraction v_j = new Fraction(vang[0].coeffs[0], vang[1].coeffs[0]);
        //полученные дроби ставятся на свои места в вектор
        if (l < (rankA - 1)) {
          end_result[j].V[l] = v_j;
        } else {
          end_result[j].V[col_num] = v_j;
        }
      }
      //ring=new Ring("Z[x]");
    }


    return end_result;
  }

  /**
   * метод нахождения целого базиса по рациональному
   * @param r_basis - рациональный базис
   * @return - целый базис данного множества
   */
  public static VectorS[] IntegerBasis(VectorS[] r_basis) {
    VectorS[] res = new VectorS[r_basis.length];//вектор числителей r_basis
    for (int i = 0; i < res.length; i++) {
      res[i] = new VectorS(r_basis[i].V.length);
    }
    VectorS[] res_end = new VectorS[r_basis.length];//вектор результата
    for (int i = 0; i < res_end.length; i++) {
      res_end[i] = new VectorS(r_basis[i].V.length);
    }
    int len = r_basis.length;
    if (len == 0) {
      return null;
    } else {
      Ring ring = new Ring("Q[x]");//работаем в поле Q так как в нем возможно
      //производить сразу деление на последнюю координату вектора, полученного при применении процедуры
      //extendedGCD. В Z необходимо действовать строго по формуле описанной в комментариях к процедуре.
      VectorS denoms = new VectorS(len);//вектор знаменателей r_basis
      for (int i = 0; i < len; i++) {
        //приводим векторы рационального базиса к общему знаменателю
        VectorS r_new = (VecCommonDenom(r_basis[i], ring));
        for (int j = 0; j < res[i].V.length; j++) {
          Fraction r = (Fraction) r_new.V[j];
          if (j == 0) {
            denoms.V[i] = r.denom;
          }
          res[i].V[j] = r.num;
        }
      }
      VectorS ext = extGCD(denoms);//находим расширенный нод числителей
      //System.out.println("extGCD="+ext);
      if (ext.V[0].isOne(ring)) {//если нод числителей равен единице
        int k = 1;//счетчик
        //находим номер полинома, при котором коэффициент расширенного нод не нулевой
        while (ext.V[k].isZero(ring)) {
          k = k + 1;
        }
        VectorS z_k = new VectorS(r_basis[0].V.length);//первый полученный вектор базиса
        for (int i = 0; i < z_k.V.length; i++) {
          z_k.V[i] = Fraction.Z_ZERO;
        }
        for (int i = 0; i < res.length; i++) {//заполняем k-ый полученный вектор по формуле
          for (int j = 0; j < res[i].V.length; j++) {
            Fraction x_i = new Fraction(res[i].V[j], NumberZ.ONE);//x_i
            Fraction x_u = (Fraction) x_i.multiply((Fraction) ext.V[i + 1], ring);//x_i*u_i
            z_k.V[j] = ((Fraction) z_k.V[j]).add(x_u, ring);
          }
        }
        z_k = VecCommonDenom(z_k, ring);
        int l = 0;
        //вычисляем остальные векторы базиса
        for (int i = 0; i < len; i++) {
          if (i == k - 1) {
            res_end[k - 1] = z_k;
          } else {
            VectorS xi = res[i];//x_i
            VectorS ress = new VectorS(xi.V.length);
            for (int j = 0; j < xi.V.length; j++) {
              Fraction den = new Fraction(denoms.V[i], NumberZ.ONE);
              Fraction den_1 = (Fraction) den.subtract(Fraction.Z_ONE, ring);
              Fraction z_kk = (Fraction) z_k.V[j];
              Fraction zx = (Fraction) z_kk.multiply(den_1, ring);
              Fraction xii = new Fraction(xi.V[j], NumberZ.ONE);
              Fraction res1 = (Fraction) xii.subtract(zx, ring);
              ress.V[j] = res1;
            }
            res_end[i] = ress;
          }
        }

      } else {
        return null;
      }

      return res_end;
    }
  }

  /**
   * метод, находящий канонический базис однородной системы Ах=0
   * исходя из целого базиса неоднородной системы Ах=с. В результате решение неоднородной системы
   * можно представить в виде z=z_i + n1*v1 +n2*v2 + ... +nm*vm?, где
   * z_i - одно из целых решений
   * vj - векторы канонического базиса
   * nj - векторы из поля Q[x]
   * @param integer_basis - целый базис системы Ах=с
   * @return канонический базис системы Ах=0
   */
  public static VectorS[] CanonicBasis(VectorS[] i_basis) {
    Ring ring = new Ring("Q[x]");
    //если целый базис имеет один вектор, то канонический базис нулевой
    if (i_basis.length == 1) {
      VectorS nul = new VectorS(i_basis[0].V.length);
      for (int i = 0; i < nul.V.length; i++) {
        nul.V[i] = NumberZ.ZERO;
      }
      return new VectorS[]{nul};
    }
    VectorS[] v = new VectorS[i_basis.length - 1];//вектор первого этапа
    for (int i = 0; i < v.length; i++) {
      v[i] = i_basis[i + 1].subtract(i_basis[0], ring);
    }
    //сокращаем все векторы на нод компонент
    for (int i = 0; i < v.length; i++) {
      Element e = GCD(v[i], ring);
      v[i] = v[i].divide(e, ring);
    }
    //записываем вспомогательный массив нулевых векторов
    VectorS[] v1 = new VectorS[v.length];
    for (int i = 0; i < v1.length; i++) {
      v1[i] = new VectorS(v[i].V.length);
      for (int j = 0; j < v[i].V.length; j++) {
        v1[i].V[j] = NumberZ.ZERO;
      }
    }
    //начинаем приведение матрицы состоящей из векторов к треугольному виду
    for (int i = 0; i < v.length - 1; i++) {
      //создаем массив, состоящий из i-ых координат векторов
      Element[] c_i = new Element[v.length];
      //заполняем его координатами
      for (int j = i; j < c_i.length; j++) {
        c_i[j] = v[j].V[i];
      }
      //находим расширенный нод этих координат
      VectorS ext_i = extGCD(new VectorS(c_i));
      Element g_i = ext_i.V[0];//полученный нод
      //вычисляем новый i-ый вектор матрицы
      int k = 1;
      for (int j = i; j < v.length; j++) {
        //умножаем векторы на соответствующие домножители
        VectorS h = (VectorS)v[j].multiply(ext_i.V[k], ring);
        k++;
        //складываем с уже имеющимся
        v1[i] = v1[i].add(h, ring);
      }
      v[i] = v1[i];
      //вычисляем остальные векторы
      for (int j = i + 1; j < v.length; j++) {
        Element t = v[j].V[i].divide(g_i, ring);
        VectorS t1 = (VectorS)v[i].multiply(t, ring);
        v[j] = v[j].subtract(t1, ring);
      }
    }
    //сокращаем все векторы на нод компонент
    for (int i = 0; i < v.length; i++) {
      Element e = GCD(v[i], ring);
      v[i] = v[i].divide(e, ring);
    }
    //приводим результат к виду, еогда наддиагональные элементы меньшей степени чем диагональные

    //записываем вспомогательный массив нулевых векторов
    VectorS[] v2 = new VectorS[v.length];
    for (int i = 0; i < v2.length; i++) {
      v2[i] = new VectorS(v[i].V.length);
      for (int j = 0; j < v[i].V.length; j++) {
        v2[i].V[j] = NumberZ.ZERO;
      }
    }
    //начинаем приведение матрицы состоящей из векторов к треугольному виду
    for (int i = 1; i < v.length; i++) {
      //создаем массив, состоящий из i-ых координат векторов
      Element[] c_i = new Element[v.length - i];
      //заполняем его координатами
      for (int j = i; j < c_i.length; j++) {
        c_i[j] = v[j].V[i];
      }
      //находим расширенный нод этих координат
      VectorS ext_i = extGCD(new VectorS(c_i));
      Element g_i = ext_i.V[0];//полученный нод
      //вычисляем новый i-ый вектор матрицы
      int k = 1;
      for (int j = i; j < v.length; j++) {
        //умножаем векторы на соответствующие домножители
        VectorS h = (VectorS)v[i].multiply(ext_i.V[k], ring);
        k++;
        //складываем с уже имеющимся
        v2[i] = v2[i].add(h, ring);
      }
      v[i] = v2[i];
      //вычисляем остальные векторы
      for (int j = i + 1; j < v.length; j++) {
        Element t = v[j].V[i].divide(g_i, ring);
        VectorS t1 = (VectorS)v[i].multiply(t, ring);
        v[j] = v[j].subtract(t1, ring);
      }
    }

    //сокращаем все векторы на нод компонент
    for (int i = 0; i < v.length; i++) {
      Element e = GCD(v[i], ring);
      v[i] = v[i].divide(e, ring);
    }

    return v;
  }

  public static void main(String[] args) {
    Ring ring = new Ring("Z[x]");

    Polynom s = new Polynom("2", ring);

    //cоздаем матрицу A
    Element[][] d = new Element[][]{{new PolynomOneVar(new Polynom("x+3", ring)), new PolynomOneVar(new Polynom("2x+1", ring)), new PolynomOneVar(new Polynom("-x-1", ring)), new PolynomOneVar(new Polynom("1", ring)), new PolynomOneVar(new Polynom("x-1", ring))},
      {new PolynomOneVar(new Polynom("x-2", ring)), new PolynomOneVar(new Polynom("x+5", ring)), new PolynomOneVar(new Polynom("x", ring)), new PolynomOneVar(new Polynom("x-1", ring)), new PolynomOneVar(new Polynom("x+1", ring))},
      {new PolynomOneVar(new Polynom("x+1", ring)), new PolynomOneVar(new Polynom("x", ring)), new PolynomOneVar(new Polynom("x+3", ring)), new PolynomOneVar(new Polynom("x-1", ring)), new PolynomOneVar(new Polynom("x-2", ring))}};
    MatrixS A = new MatrixS(d, ring);
    System.out.println("A=" + A.toString(ring));

    //создаем вектор с
    VectorS c = new VectorS(new Element[]{new PolynomOneVar(new Polynom("x", ring)), new PolynomOneVar(new Polynom("1", ring)), new PolynomOneVar(new Polynom("x-1", ring))});
    System.out.println("c=" + c.toString(ring));
    //находим рациональный базис
    VectorS[] result_rational = RationalBasis(A, c);
    //VectorS p_ad1=result_rational[0];
    //VectorS p_ad2=result_rational[1];
    //VectorS p_ad3=result_rational[2];
    System.out.println("RationalBasis=" + Array.toString(result_rational));
    //находим целый базис
    VectorS[] result_integer = IntegerBasis(result_rational);
    System.out.println("IntegerBasis=" + Array.toString(result_integer));
    VectorS i1 = (VectorS)result_integer[0].multiply(new Polynom("1", ring), ring);
//        VectorS i2=result_integer[1].multiply(new Polynom("1",ring), ring);
    //      VectorS i3=result_integer[2].multiply(new Polynom("0",ring), ring);
    //находим канонический базис
    VectorS[] result_canonic = CanonicBasis(result_integer);
    System.out.println("CanonicBasis=" + Array.toString(result_canonic));
    VectorS c1 = (VectorS)result_canonic[0].multiply(new Polynom("3x+9", ring), ring);
    VectorS c2 = (VectorS)result_canonic[1].multiply(new Polynom("4", ring), ring);
    //System.out.println("c1="+c1);
    //System.out.println("c2="+c2);
    System.out.println("CONTROL_______________________________________");

    VectorS bas = i1.add(c1, ring).add(c2, ring);
    System.out.println("basis=" + bas);
    VectorS m = new VectorS(new Fraction[]{new Fraction(new Polynom("x+3", ring), NumberZ.ONE), new Fraction(new Polynom("2x+1", ring), NumberZ.ONE), new Fraction(new Polynom("-x-1", ring), NumberZ.ONE), Fraction.Z_ONE, new Fraction(new Polynom("x-1", ring), NumberZ.ONE)});
    //System.out.println("A_0="+m);
//        System.out.println("c_0="+CancelFract((((Fraction)m.V[0]).multiply((Fraction)bas.V[0], ring)).add((Fraction)m.V[1].multiply((Fraction)bas.V[1], ring), ring).add((Fraction)m.V[2].multiply((Fraction)bas.V[2], ring), ring).add((Fraction)m.V[3].multiply((Fraction)bas.V[3], ring), ring).add((Fraction)m.V[4].multiply((Fraction)bas.V[4], ring), ring)));
//        VectorS m1=new VectorS(new Fraction[]{new Fraction(new Polynom("x-2", ring),new Polynom("1", ring)), new Fraction(new Polynom("x+5", ring),new Polynom("1", ring)), new Fraction(new Polynom("x", ring),new Polynom("1", ring)), new Fraction(new Polynom("x-1", ring),new Polynom("1", ring)), new Fraction(new Polynom("x+1", ring),new Polynom("1", ring))});
//        //System.out.println("A_1="+m1);
//        System.out.println("c_1="+CancelFract((((Fraction)m1.V[0]).multiply((Fraction)bas.V[0], ring)).add((Fraction)m1.V[1].multiply((Fraction)bas.V[1], ring), ring).add((Fraction)m1.V[2].multiply((Fraction)bas.V[2], ring), ring).add((Fraction)m1.V[3].multiply((Fraction)bas.V[3], ring), ring).add((Fraction)m1.V[4].multiply((Fraction)bas.V[4], ring), ring)));
//        VectorS m2=new VectorS(new Fraction[]{new Fraction(new Polynom("x+1", ring),new Polynom("1", ring)), new Fraction(new Polynom("x", ring),new Polynom("1", ring)), new Fraction(new Polynom("x+3", ring),new Polynom("1", ring)), new Fraction(new Polynom("x-1", ring),new Polynom("1", ring)), new Fraction(new Polynom("x-2", ring),new Polynom("1", ring))});
//        //System.out.println("A_2="+m2);
//        System.out.println("c_2="+CancelFract((((Fraction)m2.V[0]).multiply((Fraction)bas.V[0], ring)).add((Fraction)m2.V[1].multiply((Fraction)bas.V[1], ring), ring).add((Fraction)m2.V[2].multiply((Fraction)bas.V[2], ring), ring).add((Fraction)m2.V[3].multiply((Fraction)bas.V[3], ring), ring).add((Fraction)m2.V[4].multiply((Fraction)bas.V[4], ring), ring)));
//

  }
}
