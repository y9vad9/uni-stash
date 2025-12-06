package com.mathpar.number;

/**
 * Класс содержит методы для восстановления чисел по методу Лагранжа.
 *
 * @author aleksey
 */
public class Lagrange {

    int[] primes;//массив простых модулей m_0, m_1, ..., m_{n-1}
    public NumberZ module; //module = primes[0]*primes[1]*...*primes[n-1]
    NumberZ[] mul;//mul[i] = mulPrimes[i]*inverseMulPrimes[i]

    private NumberZ calcModule() {
        NumberZ res = NumberZ.valueOf(primes[0]);
        for (int i = 1; i < primes.length; i++) {
            res = res.multiply(NumberZ.
                    valueOf(primes[i]));
        }
        return res;
    }

    /**
     * Контсруктор класса Lagrange. Очень долго конструирует. Если хотите
     * восстанавливать много чисел, то создайте класс один раз.
     *
     * @param primes -- массив простых чисел, по которым брались остатки от
     * деления`
     */
    public Lagrange(int[] primes) {
        this.primes = primes;
        module = calcModule();
        NumberZ[] mulPrimes,//массив произведений, mulPrimes[i] = m_0 m_1 ... m_{i-1} m_{i+1} ... m{n-1}
                inverseMulPrimes;//inverseMulPrimes[i] * mulPrimes[i] = 1 mod primes[i]

        mulPrimes = new NumberZ[primes.length];
        inverseMulPrimes = new NumberZ[primes.length];
        mul = new NumberZ[primes.length];
        for (int i = 0; i < primes.length; i++) {
            mulPrimes[i] = module.divide(NumberZ.valueOf(primes[i]));
            inverseMulPrimes[i] = NumberZ.valueOf(NFunctionZ32.p_Inverse(
                    mulPrimes[i].mod(NumberZ.valueOf(primes[i])), primes[i]));

            mul[i] = mulPrimes[i].multiply(inverseMulPrimes[i]);
        }
        mulPrimes = null;
        inverseMulPrimes = null;
    }

    /**
     * Восстановление числа по его остаткам rem[]. Возращает new
     * BigInteger[2]{number, module}
     *
     * @param rem
     * @return
     */
    public NumberZ[] recoveryLagrange(int[] rem) {
        NumberZ number = NumberZ.ZERO;
        for (int i = 0; i < mul.length; i++) {
            number = NumberZ.valueOf(rem[i]).
                    multiply(mul[i]).add(number);
        }
        return new NumberZ[]{number.mod(module), module};
    }

    /**
     * Восстановление вектора чисел по их остаткам. В rem[i][j] записан остаток
     * от деления числа с номером j на простой модуль primes[i].
     *
     * @param rem
     * @return
     */
    public NumberZ[] recoveryLagrange(int rem[][]) {
        NumberZ[] res = new NumberZ[rem[0].length];
        for (int i = 0; i < res.length; i++) {
            int tmprem[] = new int[rem.length];
            for (int j = 0; j < rem.length; j++) {
                try {
                    tmprem[j] = rem[j][i];
                } catch (ArrayIndexOutOfBoundsException ex) {
                    tmprem[j] = 0;
                }


            }
            res[i] = recoveryLagrange(tmprem)[0];
        }
        return res;
    }
}
