package com.mathpar.number;
import java.util.Arrays;

  /**
 * @author sasha
 */
  public class SubsetZ {

    public static final int plus_INFINITY = Integer.MAX_VALUE;
    public static final int minus_INFINITY = Integer.MIN_VALUE;

     /* Множество задается посредством динамического массива интервалов вида {a,b},
     где a - начало интервала, b - конец интервала. В случае a=b - точка.
     Пример: {1,10,12,12,34,plus_INFINITY} -> {[1-10],12,[34-oo]} */

    IntList set;

    public SubsetZ(){set=new IntList();}
    public SubsetZ(IntList list){set=list;}
    public SubsetZ(int[] arr){set=new IntList();  set.add(arr);}
    public IntList toIntList(){return set;}
    public int[] toArray(){return set.toArray();}

    public int[] toFullArray(){
        int[] Arr = set.toArray();
        int car = cardinalNumber();
        int[] res = new int [car];
        int j = 0;
        for(int i=0;i<Arr.length;i+=2){
            for(int k =Arr[i];k<=Arr[i+1];k++){
                res[j++] = k;
            }
        }
        return res;
    }


    public int[] ToArrayElements(){
     int length = cardinalNumber();
     int[] arr = new int[length];
     int[] border = set.toArray();
     int j=0;
     for(int i=border[0]; i<=border[1]; i++){
       arr[j] = i;
       j++;
     }
     return arr;
   }

    /** Метод подсчета общего количества целых чисел в интервалах
     *
     * @param a
     * @return
     */
    public int cardinalNumber( ) {IntList a=set;
        int b = 0;
        for (int i = 0; i < a.size; i += 2) {
            b += a.arr[i + 1] - a.arr[i] + 1;
        }
        return b;
    }

   public boolean equals(SubsetZ i2) {
       if(i2.cardinalNumber()!=this.cardinalNumber())
           return false;
       else {
          int i = 0;
          while(i<this.set.size&&i<i2.set.size) {
             if(this.set.arr[i]==i2.set.arr[i]) i++;
             else  break;
          }
          if(i==i2.set.size) return true;
          else return false;
       }
  }

// Метод слияния соседних интервалов, когда конец одного подходит к началу другого
   private IntList junction(IntList res) {
        IntList res2 = new IntList(res.size);
        int i = 0;
        int j;
        while (i < res.size) {
            j = i + 2;
            while (j < res.size && res.arr[j] == res.arr[j-1]+1) j+= 2;
            res2.add(res.arr[i]);
            res2.add(res.arr[j - 1]);
            i = j;
        }
        return res2;
   }

  // Объединение двух множеств
    public SubsetZ union(SubsetZ i2) {
          IntList a1 = set;
          IntList a2 = i2.set;
          IntList result = new IntList();
          int i = 0;
          int j = 0;

          while (i < a1.size && j < a2.size) {

              if (a1.arr[i] < a2.arr[j]) {
                  if (j % 2 == 0) result.add(a1.arr[i]);
                      i++;}

              if (a1.arr[i] > a2.arr[j]) {
                  if (i % 2 == 0) result.add(a2.arr[j]);
                      j++;}

              if (a1.arr[i] == a2.arr[j]) {
                  if (i % 2 == j % 2)
                      result.add(a1.arr[i]);
                      i++; j++;}
          }

          if (i == a1.size && a2.arr[j] > a1.arr[i - 2]) {
              System.arraycopy(a2.arr, j, result.arr, result.size, a2.size - j);
              result.size = result.size + a2.size - j;
          }
          if (j == a2.size && a1.arr[i] > a2.arr[j - 2]) {
              System.arraycopy(a1.arr, i, result.arr, result.size, a1.size - i);
              result.size = result.size + a1.size - i;
          }

          IntList res = junction(result);
          return new SubsetZ(res);
      }

   // Пересечение
    public SubsetZ intersection(SubsetZ i2) {
          IntList a1 = set;
          IntList a2 = i2.set;
          IntList result = new IntList();
          int i = 0;
          int j = 0;

          while (i < a1.size && j < a2.size) {

              if (a1.arr[i] < a2.arr[j]) {
                  if (j % 2 == 1) result.add(a1.arr[i]);
                      i++; }

              if (a1.arr[i] > a2.arr[j]) {
                  if (i % 2 == 1) result.add(a2.arr[j]);
                      j++; }

              if (a1.arr[i] == a2.arr[j]) {
                  if (i % 2 == j % 2) result.add(a1.arr[i]);
                  else {
                    result.add(a1.arr[i]);
                    result.add(a1.arr[i]); }
                  i++; j++;
              }
          }
         return new SubsetZ(result);
    }

 // Разность двух множеств
 public SubsetZ subtraction(SubsetZ i2) {
          IntList a1 = set;
          IntList a2 = i2.set;
          a1 = junction(a1);
          a2 = junction(a2);
          IntList result = new IntList();
          int i = 0;
          int j = 0;

          while (i != a1.size - 1 || j != a2.size - 1) {

              if (a1.arr[i] < a2.arr[j]) {
                  if (j % 2 == 0) result.add(a1.arr[i]);
                  if (i < a1.size - 1) i++;
              }

              if (a1.arr[i] > a2.arr[j]) {
                  if (i % 2 == 1) {
                      if (j % 2 == 0) result.add(a2.arr[j] - 1);
                      else result.add(a2.arr[j] + 1);
                  }
                  if (j < a2.size - 1) j++;
                  else break;
              }

              if (a1.arr[i] == a2.arr[j]) {
                  if (i % 2 == j % 2) {
                      if (j < a2.size - 1) j++;
                  } else {
                      if (i % 2 == 1) result.add(a1.arr[i] - 1);
                  }
                  if (i < a1.size - 1) i++;
                  else break;
              }
          }

          if (a1.arr[i] > a2.arr[j]) {
             if ((result.size==0)||(result.arr[result.size - 1] != a2.arr[j] + 1)) result.add(a2.arr[j] + 1);
             System.arraycopy(a1.arr, i, result.arr, result.size, a1.size - i);
             result.size = result.size + a1.size - i;
          }
          return new SubsetZ(result);
      }

// Симметрическая разность
   public SubsetZ symmetricSubtraction(SubsetZ i2) {
       return (this.union(i2)).subtraction(this.intersection(i2));
   }

  // Дополнение исходного множества до i2
   public SubsetZ complement(SubsetZ i2) {
      return i2.subtraction(this);

   }

//  Разделение конечного множества интервалов на нужное количество частей.
    public  SubsetZ[] divideOnParts(int nchast) {
        IntList procs=set;
        IntList[] res = new IntList[nchast];
        SubsetZ[] RES=new SubsetZ[nchast];
        int countProcs = cardinalNumber();
        int part = countProcs / nchast;
        int ost = countProcs % nchast;
        int i = 0;
        int j = 1;
        int begin = procs.arr[i];
        int end = procs.arr[j];
        for (int k = 0; k < nchast; k++) {
            IntList result = new IntList();
            res[k] = result;
            int chast = part;
            if (k < ost) {
                chast++;
            }

            while (chast != 0) {
                if (end - begin + 1 <= chast) {
                    result.add(begin);
                    result.add(end);
                    chast = chast - (end - begin + 1);
                    i += 2;
                    j += 2;
                    begin = procs.arr[i];
                    end = procs.arr[j];
                } else {
                    result.add(begin);
                    result.add(begin + chast - 1);
                    begin += chast;
                    chast = 0;
                }
            }
            Arrays.sort(result.arr, 0, result.size);
        }
         for (int k = 0; k < nchast; k++) RES[k]=new SubsetZ(res[k]);
        return RES;
    }

 /* Разделение конечного множества интервалов на нужное количество частей заранее определенной длины.
   * Длины частей задаются в массиве arr.
   */
   public SubsetZ[] divideOnPartsOf(int nchast, int[] arr) {
          IntList procs = set;
          IntList[] res = new IntList[nchast];
          SubsetZ[] RES = new SubsetZ[nchast];
          int i = 0;
          int j = 1;
          int begin = procs.arr[i];
          int end = procs.arr[j];
          for (int k = 0; k < nchast; k++) {
              int chast = arr[k];
              IntList result = new IntList();
              res[k] = result;
                  while (chast != 0) {
                      if (end - begin + 1 <= chast) {
                          result.add(begin);
                          result.add(end);
                          chast = chast - (end - begin + 1);
                          i += 2;
                          j += 2;
                          begin = procs.arr[i];
                          end = procs.arr[j];
                      } else {
                          result.add(begin);
                          result.add(begin + chast - 1);
                          begin += chast;
                          chast = 0;
                      }
                  }
                  Arrays.sort(result.arr, 0, result.size);
          }
          for (int l = 0; l < nchast; l++) {
              RES[l] = new SubsetZ(res[l]);
          }
          return RES;
      }

    public String intervalToString() {
        IntList a=this.set;
        int k=0;
        StringBuffer res=new StringBuffer("Множество включает:    ");

        if(a.arr[0]==minus_INFINITY&&a.arr[1]==plus_INFINITY) {
           res.append("( -"+'\u221e'+","+'\u221e');
           res.append(")  Всего элементов:  " + '\u221e');
           return res.toString();
        }
        else {

         for (int i=0; i < (a.size / 2); i++) {
            if (i!=0)
                res.append("],[");
            else {
                if(a.arr[0]==minus_INFINITY)
                    res.append("{(");else
                res.append("{[");
            }

            if (a.arr[2 * i + 1] - a.arr[2 * i] == 0) res.append(a.arr[2 * i]);
            else {
                if (a.arr[2*i]==minus_INFINITY) res.append("-"+'\u221e'+","+a.arr[2*i+1]);
                if (a.arr[2*i+1]==plus_INFINITY) res.append(a.arr[2 * i] + ","+'\u221e');
                if (a.arr[2*i]!=minus_INFINITY && a.arr[2*i+1]!=plus_INFINITY)
                      res.append(a.arr[2 * i] + "," + a.arr[2 * i + 1] );
            }
        }

       if(a.arr[0]==minus_INFINITY||a.arr[a.size-1]==plus_INFINITY){
           if(a.arr[a.size-1]==plus_INFINITY)
             res.append(")}  Всего элементов: " + '\u221e');
           else res.append("]}  Всего элементов: " + '\u221e');
       }
       else
          res.append("]}  Всего элементов: " + (new SubsetZ(a)).cardinalNumber());
       return res.toString();
        }
    }
  }






