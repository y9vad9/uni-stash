package com.mathpar.func;

import com.mathpar.func.parser.Parser;
import java.awt.Color;
import java.util.*;
import com.mathpar.number.*;
import com.mathpar.matrix.*;
import com.mathpar.polynom.*;
import com.mathpar.web.exceptions.MathparException;

import static com.mathpar.number.Element.NEGATIVE_INFINITY;
import static com.mathpar.number.Element.TRUE;

/**
 * Класс функций - основной класс проекта.
 *
 * У функции два динамических поля: имя (int name) и массив аргументов
 * (Element[] X).
 *
 * Так как класс Element наследуется всеми математическими объктами, включая и
 * функции, то динамически функции объединяются в деревья, у которых листьями
 * являются любые элементы, кроме функций, а все нелистовые вершины являются
 * функциями.
 *
 * Предусмотрены две операции умножения: коммутативное MULTIPLY и
 * некоммутативное MULTIPLY_NC. Отделение этих операций происходит при первом
 * разборе функции, полученной из строчного выражения: по соглашению
 * некоммутирующими объектами являются матрицы (MatrixD, MatrixS), векторы
 * (VectorS), символьные переменные (Fname), имена которых начинаются с
 * заглавной буквы, а также выражения в скобках, если присутствует хоть один
 * некоммутирующий объект.
 *
 * @author gennadi
 */
public class F extends Element implements Cloneable {
    public int name;
    public Element[] X;
    public static Element POL_ONE = new Polynom(new int[0], new Element[] {NumberZ.ONE});
    public static Element POL_MINUS_ONE = new Polynom(new int[0], new Element[] {NumberZ.MINUS_ONE});
    public static Element POL_ZERO = new Polynom(new int[0], new Element[0]);
    public static final Fname PI = new Fname("\\pi");
    // Functions names
    //% Элементароные функции
    public final static int ID = 0; // тождественная функция (y(x)=x)
    public final static int ABS = 1; // абсолютная величина
    public final static int EXP = 2;  // экспоненциальная функция
    public final static int LN = 3; // натуральный логарифм
    public final static int LG = 4; // десятичный логарифм
    public final static int LOG = 5; //  логарифм, функция 2х аргументов: 1-ый -основание логарифма (log(a,b) = log_{a}(b))
    public final static int SQRT = 6;  // корень квадратный
    public final static int CUBRT = 7; // корень кубический
    public final static int ROOTOF = 8; // корень степени n числа:    rootOf(x,n)
    public final static int SIN = 9; // синус
    public final static int COS = 10; // косинус
    public final static int TG = 11; // тангенс
    public final static int CTG = 12; // котангенс
    public final static int ARCSIN = 13; // арксинус
    public final static int ARCCOS = 14; // арккосинус
    public final static int ARCTG = 15; // арктангенс
    public final static int ARCCTG = 16; // арккотангенс
    public final static int SH = 17; // гиперболический синус
    public final static int CH = 18; // гиперболический косинус
    public final static int TH = 19; // гиперболический тангенс
    public final static int CTH = 20; // гиперболический котангенс
    public final static int ARCSH = 21; // гиперболический арксинус
    public final static int ARCCH = 22; // гиперболический арккосинус
    public final static int ARCTGH = 23; // гиперболический арктангенс
    public final static int ARCCTGH = 24; // гиперболический арккотангенс
    public final static int SC = 25; // секанс
    public final static int CSC = 26; // косеканс
    public final static int ARCSEC = 27; // арксеканс
    public final static int ARCCSC = 28; //?  32;// арккосеканс
    //% Операции над числами   
    public final static int MAX = 29; //  максимальное из двух чисел:  max(a,b)
    public final static int MIN = 30; //  минимальное из двух чисел: min(a,b)
    public final static int SIGN = 31; // знак числа, знак старшего коэффициента у полинома 
    //% Операции над целыми чиселами
    public final static int FLOOR = 32; // наибольшее целое число, которое не превосходит данное
    public final static int CEIL = 33; // наименьшее целое число, которое не меньше,  данное
    public final static int ROUND = 34; // округление к ближайшему целому числу
    public final static int DIVREM = 35; //  целая часть частного b остаток при делении двух целых чисел
    public final static int DIV = 36; // целая часть частного при делении двух целых чисел
    public final static int REM = 37; // остаток при делении двух целых чисел
    public final static int MOD = 38; //  остаток от деления двух целых чисел в интервале 0 до MOD-1
    public final static int MODCENTERED = 40; //  остаток от деления двух целых чисел в интервале -(MOD-1)/2 до (MOD-1)/2
    public final static int ARG = 41; // argument of complex number \pi >=arg(x)> -\pi

    //  номер 39 используется для булева отрицания B_NOT после номера 186
    public final static int FACTORIAL = 103; //?  23; // факториал ! постфиксная функция
    //% Операции с дробями и рациональными функциями, полиномами
    public final static int NUM = 50; // числитель дроби
    public final static int DENOM = 51; // знаменатель дроби
    public final static int PROPERFORM = 52; // выделить правильную драбь
    public final static int QUOTIENTANDREMAINDER = 53; // частное и остаток при делении числителя дроби на знаменатель
    public final static int QUOTIENT = 54; // частное  при делении числителя дроби на знаменатель или первого аргумента на второй
    public final static int REMAINDER = 55; // остаток при делении числителя дроби на знаменатель или первого аргумента на второй
    public final static int CANCEL = 56; // сократить дробь
    public final static int DEGREES = 57; // старшие степени в полиноме по всем переменным 
    public final static int LCM = 58; // наименьшее общее кратное
    public final static int EXTENDEDGCD = 59; // вычисление НОД и сомножителей аргументов в выражении НОД
    public final static int GCD = 60; // вычислене наибольшего общего делителя
    public final static int RESULTANT = 61; // результант двух полиномов по переменной, указанной вторым аргументом
    // resultant(Polynom ,Pollynom, var)
    public final static int GATHERFRACTION = 62; // сбор одинаковых дробей без раскрытия скобок в знаменателе
    public final static int HPOLCOEFFICIENT = 63; // старший коэффициент полинома
    public final static int HPOLTERM = 64; // старший терм полинома
    public final static int DEGREE = 65; //  старшая степень в полиноме по той переменной, которая во втором аргументе
    public final static int LEADINGCOEFF = 66; // the highest coefficient in polynomial (number or polynomial with less vars)
    // degree(Polynom , var)
    //%   Операции над выражениями  
    public final static int EXPAND = 67; // раскрытие всех скобок
    public final static int FACTOR = 68; // разложение на множители полиномов
    public final static int FULLEXPAND = 69; //полное разложение выражения с учетом элементарных функций
    public final static int FULLFACTOR = 70; //полная факторизация выражения с учетом элементарных функций
    public final static int FFULLFACTOR = 71; // факторизация композиции функций по всем уровням дерева 
    public final static int FACTORINC = 72; // факторизация полинома в комплексной области
    public final static int SIMPLIFY = 73;
    //упрощение композиций типа e^{ix} + e^{-ix} => 2cos(x)
    //% Матричные операции, таблицы
    public final static int FIRST_MATRIX_OP = 79;
    public final static int SUBMATRIX = 79;  //  подматрица
    public final static int DET = 80;  //   детерминант матрицы
    public final static int INVERSE = 81; // обращение матрицы
    public final static int ADJOINT = 82; // присоединенная матрица
    public final static int KERNEL = 83; // ядро оператора (нуль-пространство)
    public final static int TOECHELONFORM = 84; // эшелонная (ступенчатая) форма матрицы
    public final static int CHARPOLYNOM = 85; // характеричтический полином матрицы
    public final static int TRANSPOSE = 86; // транспонирование матрицы
    public final static int CONJUGATE = 87; // сопряженная матрица 
    //   general inverse Murra-Penrousa   
    public final static int GENINVERSE = 88; // обобщенная обратная Мурра-Пенроуза
    public final static int LDU = 89; // разложение матрицы в произведение нижней треугольной, диагональной и верхней треугольной
    public final static int BRUHATDECOMPOSITION = 90; // разложение Брюа
    public final static int ELEMENTOF = 91; // объявление наименования элементов данной матрицы или вектора
    public final static int TABLE = 92; //  задать таблицу
    public final static int TABULATION = 93; // получить таблицу с табулированными функциями (задаются функции, значение переменной, шаг и конечное значение)
    public final static int SIZE = 94; //  возвращает размер вектора или матрицы
    //    size(Vector)   size(Matrix)
    public final static int COLNUMB = 95; //  возвращает число столбцов  у матрицы   
    // colNumb(Matrix)  
    public final static int ROWNUMB = 96; //  возвращает число столбцов  у матрицы  
    //  rowNumb(Matrix) 
    public final static int CLOSURE = 97; // замыкание элемента или матрицы (1+x+x^2+...}  
    public final static int LDU_M = 98; // разложение матрицы в произведение нижней треугольной, диагональной и верхней треугольной 
    public final static int WDK = 99;
    public final static int PLDUQWDK = 100;
    public final static int LDUWDK = 101;
    public final static int RANK = 102;
    public final static int LAST_MATRIX_OP = 102;

    //% Преобразования элементов из одного кольца в другое  
    public final static int TONEWRING = 107; // преобразование выражения к новому текущему кольцу  
    public final static int TOMATRIX = 108;  // преобразование элементов матрицы из одного кольца в другое
    //  постфиксные функции 
    public final static int FIRST_POSFIX_NAME = 103;    //# первое постфиксное имя ####
    //                FACTORIAL = 103; //  факториал !  SEE at another place
    //                 COMPLEMENT = 104; //  дополнение множества '(апостроф) за номером 288
    public final static int LAST_POSFIX_NAME = 104; //# последнее постфиксное имя ####
    // Важно, что это функция от одного аргумента (учитывается в парсере)
    //% Тропическая математика
    public final static int SOLVELAETROPIC = 120; // частное решение системы AX=b в тропической алгебре
    public final static int SOLVELAITROPIC = 121; // частное решение неравенства AX>b в тропической алгебре
    public final static int BELLMANEQUATION = 122; // частное решение уравнения Белмана AX+b=X в тропической алгебре
    public final static int SEARCHLEASTDISTANCES = 123; // построение таблицы кратчайших растояний между вершинами графа
    public final static int FINDTHESHORTESTPATH = 124;  // нахождение крайтчайшего пути между двумя вершинами графа
    //% Линейное программирование
    public final static int SIMPLEXMAX = 125; // решить задачу линейного программирования на максимум целевой функции
    public final static int SIMPLEXMIN = 126; // решить задачу линейного программирования на минимум целевой функции
    public final static int TAKEROW = 127; // re 
    public final static int TAKECOLUMN = 128;  // im  
    public final static int RE = 129; // re 
    public final static int IM = 130;  // im   
//-----------------------------------------------------------------------------------------------
    // два аргумента. Это функции только для внутреннего использования
    public final static int HBOX = 150;
    //  комментарий        
    public final static int GET_CALC_RESULT = 151;
    // функция используется только в классе canonicForm
    public final static int FIRST_FUNC_TWO_ARGS = 151; //?  GET_CALC_RESULT;    //# первая функция с двумя аргументами
    public final static int POW = 152;
    // два аргумента: (1) основание, (2) показатель степени:  pow(a,b) ===   a^b
    public final static int intPOW = 153;
    //a^b, b - целое число, два аргумента: (1) основание, (2) показатель степени:  pow(a,b) ===   a^b
    public final static int KORNYAK_MATRIX = 154;
    // конструктор матрицы Корняка: kornyakMatrix([[...]], Natural);
    //    public final static int DEFINITION = 2; //?  203;
    public final static int SYLVESTER = 155;
    // построить матрицу Сильвестра - 2 варианта
    //   public final static int PRS_STURM_OLD = 156;
    // получить субрезультантгая последовательность Штурма СТАРАЯ
    public final static int PRS = 156;
    // получить субрезультантную последовательность полиномиальных остатков
    public final static int PRS_SUBRES = 157;
    // получить субрезультантную ППО по Брауну с вычислением модулей
    public final static int PRS_GENERAL = 158;
    // получить субрезультантную последовательность Евклида (0), Штурма (1), БЕЗ ЗНАКА (2)
    public final static int ALGEBRAIC_EQUATION = 159;
    // Линейное алгебраическое уравнение  Не используется
    public final static int EQUATION = 160;
    // Решение уравнения Не используется
    public final static int SEQUENCE = 161;
    // задание последовательности Не используется
    public final static int VECTORS = 162;
    public final static int AKRITAS = 163;
    // получить по двум полиномам построит матрицу требуемого вида ("универсальный Сильвестр")
    //  Инфиксные бинарные некоммутативные операции
    //%    Логические  (операторы сравнения)
    // Do not change mutual  positions of boolean functions!! 170--- 175 !! (or change in Element func <<compareTo(Element, int, Rung)>>)
    public final static int B_NE = 170;  // не равно (инфиксное)
    public final static int FIRST_INFIX_NAME = 170;  //# первое инфиксное имя ####### 
    public final static int FIRST_INEQUAL_BOOL = 170; //# первая булева функция #########
    public final static int B_LESS = 171; // меньше 
    public final static int B_LE = 172; // меньше либо равно 
    public final static int B_EQ = 173; // равно 
    public final static int B_GE = 174; // больше либо равно 
    public final static int B_GT = 175; // больше 
    public final static int LAST_INEQUAL_BOOL = 175; //# последняя булева функция ##########
    public final static int FIRST_WB = 175;
    public final static int LAST_WB = 179;  // and we have to add B_LESS  and B_EQ
    //% Oбщие алгебраические операции
    public final static int SUBTRACT = 176;
    //?  308; //  вычитание
    public final static int DIVIDE = 177;
    //?  309;    //  деление
    // Инфиксные  многих переменных
    // Инфиксные многих переменных коммутативные
    public final static int ADD = 178;
    //?  310;          // сложение, может быть много аргументов  
    public final static int FIRST_INFIX_COMMUT_ARG = 178; //# первая функция с коммутативными аргументами ###
    public final static int MULTIPLY = 179;
    //?  311;     // умножение, может быть много аргументов коммутативное 
    //%  Логические операции и операции над множествами
    public final static int B_NOT = 39;   // логическое отрицание 
    public final static int B_OR = 180; // логическая операция дизъюнкция 
    //(коммутативная, много аргументов) boolean
    public final static int B_AND = 181; // логическая операция конъюнкция 
    // (коммутативная, много аргументов) boolean
    //операции над множествами
    public final static int CUP = 182; // объединение двух множеств
    // the same as  union
    public final static int CAP = 183; // пересечение двух множеств
    // the same as Intersection
    public final static int SYMMETRIC_DIFFERENCE = 184;  // симметрическая разность двух множеств
    public final static int LAST_INFIX_COMMUT_ARG = SYMMETRIC_DIFFERENCE; //# последняя функция с коммутативными аргументами ###
    public final static int MULTIPLY_NC = 185;
    //?  301;  // некоммутативное умножение, много аргументов, операция некоммутативной алгебры
    public final static int SET_MINUS = 186; // разность двух множеств
    // from first set subtracted second set
    public final static int LAST_INFIX_NAME = SET_MINUS; //# последнее инфиксное имя  ###
    public final static int SET = 187;   // задание множества  интервалов (открытых, закрытых, полуоткрытых) на числовой оси 
    // \set((a, b], ...). Creates number set.
    public final static int VECTOR_SET = 188; // получить вектор подмножеств, типа {1 < a < 2, 0 \\le b < c}.
    public final static int COMPLEMENT = 104; // дополнение множества '(апостроф) постфиксная ф-ция
    public final static int ISZERO = 189;
    public final static int ISNEGATIVE = 190;
    public final static int ISONE = 191;
    public final static int ISEVEN = 192;
    public final static int ISINFINITE = 193;
    public final static int ISNAN = 194;

// инфиксные и коммутативные кончились
    // многих переменных
    //% Графические функции, печать выражений
    public final static int FIRST_FUNC_MANY_ARGS = 201; //# первая функция с переменным числом аргументов
    public final static int PAINTELEMENT = 202; // функция для планиметрических построений, понимающая команды
    public final static int SET2D = 203; // установить настройки для 2D графика (xmin, xmax, ymin, ymax, 'x','y','title')
    public final static int REPLOT = 204; // перерисовать построенный график 
    public final static int TABLEPLOT2 = 205;
    public final static int TABLEPLOT4 = 206;
    public final static int PLOT = 207; // построить на плоскости график функции одной переменной
    public final static int PARAMPLOT = 208; //  построить на плоскости график параметрической функции
    public final static int TABLEPLOT = 209; //  построить график табличной функции
    public final static int POINTSPLOT = 210; // построить график табличной функции отдельными точками, которые могут быть подписаны
    public final static int SHOWPLOTS = 211; //  показать фсе графики в одной системе координат 
    public final static int PLOT3D = 212; //   построить граф функции двух переменных
    public final static int PARAMPLOT3D = 213; // построить в 3D параметрически заданную функцию двух переменных
    public final static int IMPLICIT_PLOT3D = 214;
    public final static int PLOTGRAPH = 215; // нарисовать заданный граф
    //\plotGraph([[],[]],[[],[]]);
    public final static int PRINT = 216; //   печать указанных переменных
    public final static int PRINTS = 217; //   печать подобная println для PASCAL
    public final static int EXPLICIT_PLOT3D = 218; // новые функции EXPLICIT_PLOT3D
    public final static int PARAMETRIC_PLOT3D = 219; // новые функции PARAMETRIC_PLOT3D
    public final static int TEXTPLOT = 220; // textPlot()
    public final static int SHOW_3D = 221; // scene settings for rendering multiple surfaces

    //% Ряды
    public final static int SERIES = 230; //  задание ряда
    public final static int MULTIPLY_SERIES = 231; // умножение рядов
    public final static int ADD_SERIES = 232; // сложение рядов
    public final static int SUBTRACT_SERIES = 233; // вычитание рядов
    public final static int TRANSFORM = 234;  // преобразование рядов   
    //% Создание случайных объектов
    public final static int RANDOMNUMBER = 235; // получить случайное число 
    public final static int RANDOMPOLYNOM = 236; // получить случайный полином
    public final static int RANDOMMATRIX = 237; // получить случайную матрицу
    public final static int SHIFTLEFT = 238;//вращение в левую сторону  ветора и матрицы на s элементов
    public final static int SHIFTRIGHT = 239;//вращение в правую. сторону  ветора и матрицы на s элементов

    //% Решатели
    public final static int VALUE = 250; //  найти значение выражения 
    public final static int LENGTH = 251; // Евклидовая норма вектора: корень квадратный из суммы квадратов компонент 
    public final static int SOLVE = 252; // решить алгебраическое уравнение или систему из нескольких уравнений
    public final static int SOLVETRIG = 253; // решить уравнение, содержащее тригонометрическую функцию
    public final static int SOLVELAE = 254; // решить систему линейных алгебраических уравнений
    public final static int SYSTLAE = 255; // задание системы линейных алгебраических уравнений
    public final static int SYSTLDE = 256; // задание системы дифференциальных уравнений
    public final static int SOLVELDE = 257; // решить систему линейных дифференциальных уравнений
    public final static int INITCOND = 258; // задание системы начальных  условий для дифференциальных уравнений
    //Дифференциальные уравнения в частных производных
    public final static int SOLVEPDE = 259; // решить дифференциальное уравнение с частными производными 
    public final static int IC = 260; // задание начальных условий для   дифференциальных уравнений c частными производными
    //  public final static int LC = 261;
    // прямое преобразование Лапласа-Карсона  (где реализация?)
    public final static int INVERSELC = 262;
    //?  449; // обратное преобразование Лапласа-Карсона   (где реализация?)
    public final static int SOLVEEQ = 263;     // SOLVEEQ    -- БЫЛО !!!! 2016
    // МНЕ кажется, что это нужно уже удалить....
    public final static int DOMAIN_OF_FUNC = 264;
    // задание области определения функции - надо еще уточнить использование
    //f(x),{g<>0,..,h<>0},{g1>0,..,h1>0},{g2>=0,..,h2>=0} 
    public final static int d = 265; // знак производной в дифференциальных уравнениях 
    public final static int D = 266; // вычислить производную 
    // ...D(x,t,2)...2 производная по t
    public final static int LIM = 267; // Вычислить предел 
    // lim  (x, a, f(x))
    public final static int TEILOR = 268; // разложения функции в ряд Тейлора (2 или 3 аргумента);
    public final static int INT = 269; // вычислить символьный интеграл или (f(х), x) или (a, b, f(x), x)
    public final static int SUM = 270; // конечная сумма  -- соответствует символу \Sigma-большое с индексами
    public final static int PROD = 271; // конечное произведение -- соответствует символу \Pi-большое с индексами
    public final static int NINT = 272; // вычислить численное значение определенного интеграла
    // Преобразования Лапласа и расчет систем автоматического регулирования
    public final static int LAPLACETRANSFORM = 273; // преобразование Лапласа
    public final static int INVERSELAPLACETRANSFORM = 274; // обратное преобразование Лапласа
    public final static int SOLVETRANSFERFUNCTION = 275; // найти передаточную функцию для сист.Автом.Регул.
    public final static int SOLVETIMERESPONSE = 276; //     найти временной отклик для сист.Автом.Регул.
    public final static int SOLVEFREQUENCERESPONSE = 277; // найти частотный отклик для сист.Автом.Регул.
    public final static int SOLVELEX = 278;
    //  возможно, что это поиск корня полинома в указанном интервале
    public final static int SOLVEDE = 279; // решить дифференциальнoe уравнение 
    public final static int SOLVEHDE = 280; // для внутренних целей ................................
    // (второго порядка в частности, тут все спецфункции фильтруются)
    public final static int TESTMATRIX = 282;
    //% Решатели полиномиальные
    public final static int SOLVENAE = 290; // решить  систему нелинейных алгебраических уравнений
    // solveNAE(p1, ..., pN) решение полиномиальных систем
    public final static int GROEBNERB = 291; // вычислить базис Гребнера  по алгоритму Бухбергера
    // Groebner basis, Buchberger algorithm
    public final static int GROEBNER = 292; // вычислить базис Гребнера (алгоритм F4)
    // Groebner basis, default (Faugere) algorithm
    public final static int REDUCEBYGB = 293; // привести заданный полином по модулю заданного множества полиномов
    // p = \reduceByGB(f, [g1, ..., gN]);
    public final static int SUMGEOMPROGRESS = 294; // преобразовать полином с помощью формулы суммы геометрической прогрессии
    public final static int SUMOFPOL = 295; //суммирование полинома по нескольким переменным в конечных пределах
    public final static int APPROX = 296; //  апроксимация: найти полином указанной степени по методу наименьших квадратов
    public final static int SYSTEM = 297; // ввети систему уравнений и сохранить как вектор из функций
    public final static int TOVECTORDENCE = 298; // полином вывести как вектор коэффициентов без степеней, с нулевыми коэффициентами
    public final static int TOVECTORSPARCE = 299; // полином вывести как вектор коэффициентов, во втрой части которого записаны степени
    public final static int VECTORTOPOLYNOM = 300; // ввети систему полином из вектора коэффициентов с, возможно, нулевыми коэффициентами   
    public final static int SYSTEMOR = 301; // ввети систему уравнений и сохранить как вектор из функций

    //% функции теории вероятности
    public final static int DISPERSION = 302; // найти дисперсию непрерывной случайной величины
    public final static int MEANSQUAREDEVIATION = 303; // найти среднее квадратичное отклонение непрерывной случайной величины
    public final static int SIMPLIFYQUANTITY = 304; // упростить заданную дискретную случайную величину
    public final static int ADDRANDOMQUANTITY = 305; // сложение дискретных случайных величин
    public final static int MULTIPLYRANDOMQUANTITY = 306; // умножение дискретных случайных величин
    public final static int COVARIANCE = 307; // коеффициент ковариация дискретных случайных величин
    public final static int CORRELATION = 308; // коеффициент корреляция  дискретных случайных величин
    public final static int SAMPLEMEAN = 309; // среднее значение для выборки 
    public final static int SAMPLEDISPERSION = 310; // дисперсия для выборки
    public final static int COVARIANCECOEFFICIENT = 311; // коэффициент ковариации для двух выборок
    public final static int CORRELATIONCOEFFICIENT = 312; // коэффициент корреляции для двух выборок
    public final static int PLOTPOLYGONDISTRIBUTION = 313; // построить многоугольник распределения дискретной случайной величины
    public final static int PLOTDISTRIBUTIONFUNCTION = 314; // построить график функции распределения дискретной случайной величины
    public final static int MATHEXPECTATION = 315; // найти матожидание непрерывной случайной величины 
    // ----------  C L U S T E R ----------------------
//%  Параллельная компьютерная алгебра (в стадии разработки)
    public final static int MATMULTPAR = 330; // умножение матриц параллельное
    public final static int MATMULTPAR1X8 = 331; // умножение матриц параллельное по схеме 1x8
    public final static int ADJOINTPAR = 332; // вычисление присоединенной матрицы параллельное
    // \adjointPar(m)
    public final static int ECHELONFORMPAR = 333; //вычисление ступенчатой матрицы параллельное
    // \echelonFormPar(m)
    public final static int DETPAR = 334; // вычисление детерминанта матрицы параллельное
    //\detPar(m)
    public final static int KERNELPAR = 335;  // вычисление ядра оператора параллельное
    //\kernelPar(m)
    public final static int CHARPOLPAR = 336; // вычисление характеристического полинома матрицы параллельное
    //\charPolPar(m)
    public final static int ADJOINTDETPAR = 337; // вычисление присоединенной матрицы и определителя параллельное
    //     \adjointDetPar(m)
    public final static int GBASISPAR = 338; // вычисление базиса Гребнера параллельное
    //\gbasisPar([p1, p2, p3, . . . , pn])
    public final static int POLFACTORPAR = 339; //  факторизация полинома параллельная
    // \polFactorPar(<polynomial>);    
    public final static int TIME = 341; // Время выполнения задачи на кластере
    public final static int GETSTATUS = 342; // состояние задачи на кластере
    public final static int UPLOADTOCLUSTER = 343; // загрузить пакет на кластер
    public final static int RUNUPLOADEDCLASS = 344; // стартовать загруженный на кластер класс
    public final static int GETERR = 345; //  получить с кластера сообщения об ошибках
    public final static int SHOWTASKLIST = 346; // показать задания в очереди на кластере
    public final static int GETOUT = 347; //  получить с кластера сообщения о результатах вычислений
    public final static int SHOWFILELIST = 348; // показать список всех файлов для кластера
    public final static int POLMULTPAR = 349; // умножение матриц параллельное
    public final static int BELLMAN_EQUATION_PAR = 350;
    public final static int BELLMAN_INEQUALITY_PAR = 351;
    public final static int ETD_TEST_PAR = 352;
    public final static int UPLOADANDCOMPILE = 353; // стартовать загруженный на кластер класс
    //Параллельные вычисления конец  -----------------------------------------------------------------------

    //%  Операции с файлами и спамятью выражений
    public final static int TABLEFROMFILE = 360; // операция загрузки таблицы из файла
    // Loading Table from file: \tableFromFile('filename');
    public final static int FROMFILE = 361; // загрузка из файла некоторого фрагмента на языке Mathpar
    // Load Mathpar RHS expression from file: \fromFile('filename');  
    public final static int TOFILE = 362; //сохрание объекта языка Mathpar, который указывается аргументом 1, в текстовом файле, аргумент 2
    //    // save object from varName to file: \toFile(varName, 'filename');
    public final static int LS = 363; // Показать список всех файлов, которые загружены пользователем в окружение тетради
    public final static int CLEAN = 364; // стереть из памяти все введенные выражения
    // clean expressions
    //% Не элементарные функции
    public final static int UNITSTEP = 401; // единичная ступенька 
    public final static int UNITSTEPS = 402;
    //  функция единичного скачка Не используется?
    public final static int UNITBOX = 403; // функция, которая принимает значение 1 на заданном интервале
    //unitBox(x,a) =  unitStep(x(a-x))
    public final static int BINOMIAL = 404; // биномиальный коэффициент, два аргумента: (1) n,  (2) k.
    public final static int GAMMA = 405;  // Гамма функция
    public final static int GAMMA2 = 406;  // Гамма-2 функция
    public final static int BETA = 407; // Бета-функция
    public final static int BESSELJ = 408; // BesselJ-function 
    public final static int BESSELY = 409; // BesselY-function

    public final static int SPHERICALHARMONICCART = 410; //
    public final static int SPHERICALHARMONICRCART = 411; //
    public final static int SPHERICALHARMONICR = 412;  //
    public final static int SPHERICALHARMONIC = 413; //
    public final static int LEGENDREP = 414; // 
    public final static int TOCOMPLEX = 415;// to composition of ln and exp with complex arguments
    public final static int GCDNUMPOLCOEFFS = 416; // GCD of numerical polynomial coefficiens (a number)
    public final static int GCDHPOLCOEFFS = 417; //  GCD of higest variable polynomial coefficiens (a polynomial)
    public final static int DETL = 418; //  Lagutinski determinant
    public final static int FUNC_ARR_LEN = 420; //?  490; //# общее  число функций в проекте    
    public final static int MAX_F_NUMB = FUNC_ARR_LEN + 1;  // номер функции, которая является первой процедурой пользователя в программе

    //Массив имен функций 
    public final static String[] FUNC_NAMES = new String[FUNC_ARR_LEN];
    private static final HashMap<String, Integer> strMap = new HashMap<String, Integer>();

    static {
        FUNC_NAMES[DETL] = "detL";
        FUNC_NAMES[TOCOMPLEX] = "toComplex";
        FUNC_NAMES[SPHERICALHARMONICR] = "sphericalHarmonicR";
        FUNC_NAMES[LEGENDREP] = "LegendreP";
        FUNC_NAMES[SPHERICALHARMONIC] = "sphericalHarmonic";
        FUNC_NAMES[SPHERICALHARMONICRCART] = "sphericalHarmonicRCart";
        FUNC_NAMES[SPHERICALHARMONICCART] = "sphericalHarmonicCart";
        FUNC_NAMES[BESSELJ] = "BesselJ";
        FUNC_NAMES[BESSELY] = "BesselJ";
        FUNC_NAMES[MAX] = "max";
        FUNC_NAMES[MIN] = "min";
        FUNC_NAMES[DIV] = "div";
        FUNC_NAMES[REM] = "rem";
        FUNC_NAMES[DIVREM] = "divRem";
        FUNC_NAMES[DEGREE] = "degree";
        FUNC_NAMES[DEGREES] = "degrees";
        FUNC_NAMES[PROPERFORM] = "properForm";
        FUNC_NAMES[QUOTIENT] = "quotient";
        FUNC_NAMES[REMAINDER] = "remainder";
        FUNC_NAMES[RESULTANT] = "resultant";
        FUNC_NAMES[SIZE] = "size";
        FUNC_NAMES[SUBMATRIX] = "submatrix";
        FUNC_NAMES[COLNUMB] = "colNumb";
        FUNC_NAMES[ROWNUMB] = "rowNumb";
        FUNC_NAMES[LENGTH] = "length";
        FUNC_NAMES[PAINTELEMENT] = "paintElement";
        FUNC_NAMES[SIMPLEXMIN] = "SimplexMin";
        FUNC_NAMES[SIMPLEXMAX] = "SimplexMax";
        FUNC_NAMES[GATHERFRACTION] = "gatherFraction";
        FUNC_NAMES[FINDTHESHORTESTPATH] = "findTheShortestPath";
        FUNC_NAMES[SEARCHLEASTDISTANCES] = "searchLeastDistances";
        FUNC_NAMES[BELLMANEQUATION] = "BellmanEquation";
        FUNC_NAMES[SOLVELAITROPIC] = "solveLAITropic";
        FUNC_NAMES[SOLVELAETROPIC] = "solveLAETropic";

        FUNC_NAMES[BETA] = "Beta";
        FUNC_NAMES[APPROX] = "approximation";
        FUNC_NAMES[ELEMENTOF] = "elementOf";
        FUNC_NAMES[SOLVETRIG] = "solveTrig";
        // ----------  C L U S T E R ----------------------        
        FUNC_NAMES[UPLOADTOCLUSTER] = "uploadToCluster";
        FUNC_NAMES[GETERR] = "getErr";
        FUNC_NAMES[GETOUT] = "getOut";
        FUNC_NAMES[SHOWTASKLIST] = "showTaskList";
        FUNC_NAMES[RUNUPLOADEDCLASS] = "runUploadedClass";
        FUNC_NAMES[UPLOADANDCOMPILE] = "uploadAndCompile";
        FUNC_NAMES[MATMULTPAR] = "matMultPar";
        FUNC_NAMES[POLMULTPAR] = "polMultPar";
        FUNC_NAMES[MATMULTPAR1X8] = "matMultPar1x8";
        FUNC_NAMES[POLFACTORPAR] = "polFactorPar";
        FUNC_NAMES[ADJOINTPAR] = "adjointPar";
        FUNC_NAMES[ECHELONFORMPAR] = "echelonFormPar";
        FUNC_NAMES[DETPAR] = "detPar";
        FUNC_NAMES[KERNELPAR] = "kernelPar";
        FUNC_NAMES[CHARPOLPAR] = "charPolPar";
        FUNC_NAMES[ADJOINTDETPAR] = "adjointDetPar";
        FUNC_NAMES[GBASISPAR] = "gbasisPar";
        FUNC_NAMES[HPOLCOEFFICIENT] = "hPolCoefficient";
        FUNC_NAMES[HPOLTERM] = "hPolTerm";
        FUNC_NAMES[BELLMAN_EQUATION_PAR] = "BellmanEquationPar";
        FUNC_NAMES[BELLMAN_INEQUALITY_PAR] = "BellmanInequalityPar";
        FUNC_NAMES[ETD_TEST_PAR] = "ETDPar";

        FUNC_NAMES[WDK] = "WDK";
        FUNC_NAMES[PLDUQWDK] = "PlduQwk";
        FUNC_NAMES[LDUWDK] = "LDUWK";
        FUNC_NAMES[LDU_M] = "LDUm";
        FUNC_NAMES[LDU] = "LDU";
        FUNC_NAMES[BRUHATDECOMPOSITION] = "BruhatDecomposition";
        FUNC_NAMES[INVERSELC] = "inverseLC";
        FUNC_NAMES[IC] = "ic";
        FUNC_NAMES[SOLVEPDE] = "solvePDE";
        FUNC_NAMES[TONEWRING] = "toNewRing";
        FUNC_NAMES[FLOOR] = "floor";
        FUNC_NAMES[CEIL] = "ceil";
        FUNC_NAMES[ROUND] = "round";
        FUNC_NAMES[NUM] = "num";
        FUNC_NAMES[DENOM] = "denom";
        FUNC_NAMES[GETSTATUS] = "getStatus";
        FUNC_NAMES[SHOWFILELIST] = "showFileList";
        FUNC_NAMES[SET] = "set";

        FUNC_NAMES[TEILOR] = "teilor";
        FUNC_NAMES[MULTIPLY_SERIES] = "seriesMultiply";
        FUNC_NAMES[ADD_SERIES] = "seriesAdd";
        FUNC_NAMES[SUBTRACT_SERIES] = "seriesSubtract";
        FUNC_NAMES[SUMOFPOL] = "SumOfPol";
        FUNC_NAMES[SUMGEOMPROGRESS] = "SearchOfProgression";
        FUNC_NAMES[TRANSFORM] = "transform";
        FUNC_NAMES[MOD] = "mod";
        FUNC_NAMES[MODCENTERED] = "Mod";
        FUNC_NAMES[ROOTOF] = "rootOf";
        FUNC_NAMES[DET] = "det";
        FUNC_NAMES[CONJUGATE] = "conjugate";
        FUNC_NAMES[INVERSE] = "inverse";
        FUNC_NAMES[GENINVERSE] = "genInverse";
        FUNC_NAMES[CLOSURE] = "closure";
        FUNC_NAMES[ADJOINT] = "adjoit";
        FUNC_NAMES[KERNEL] = "kernel";
        FUNC_NAMES[TRANSPOSE] = "transpose";
        FUNC_NAMES[TOECHELONFORM] = "toEchelonForm";
        FUNC_NAMES[CHARPOLYNOM] = "charPolynom";
        FUNC_NAMES[TABULATION] = "tabulation";

        FUNC_NAMES[MATHEXPECTATION] = "mathExpectation";
        FUNC_NAMES[DISPERSION] = "dispersion";
        FUNC_NAMES[MEANSQUAREDEVIATION] = "meanSquareDeviation";
        FUNC_NAMES[SIMPLIFYQUANTITY] = "simplifyQU";
        FUNC_NAMES[ADDRANDOMQUANTITY] = "addQU";
        FUNC_NAMES[MULTIPLYRANDOMQUANTITY] = "multiplyQU";
        FUNC_NAMES[COVARIANCE] = "covariance";
        FUNC_NAMES[CORRELATION] = "correlation";
        FUNC_NAMES[SAMPLEMEAN] = "sampleMean";
        FUNC_NAMES[SAMPLEDISPERSION] = "sampleDispersion";
        FUNC_NAMES[COVARIANCECOEFFICIENT] = "covarianceCoefficient";
        FUNC_NAMES[CORRELATIONCOEFFICIENT] = "correlationCoefficient";
        FUNC_NAMES[PLOTPOLYGONDISTRIBUTION] = "plotPolygonDistribution";
        FUNC_NAMES[PLOTDISTRIBUTIONFUNCTION] = "plotDistributionFunction";
        FUNC_NAMES[SOLVELEX] = "solveLex";
        FUNC_NAMES[CANCEL] = "cancel";
        FUNC_NAMES[LCM] = "LCM";
        FUNC_NAMES[GCD] = "GCD";
        FUNC_NAMES[EXTENDEDGCD] = "extendedGCD";
        FUNC_NAMES[QUOTIENTANDREMAINDER] = "quotientAndRemainder";
        FUNC_NAMES[CLEAN] = "clean";
        FUNC_NAMES[PRINTS] = "printS";
        FUNC_NAMES[PRINT] = "print";
        FUNC_NAMES[MULTIPLY_NC] = "multiply";
        FUNC_NAMES[ID] = "";
        FUNC_NAMES[ABS] = "abs";
        FUNC_NAMES[ARG] = "arg";
        FUNC_NAMES[LN] = "ln";
        FUNC_NAMES[LG] = "lg";
        FUNC_NAMES[EXP] = "exp";
        FUNC_NAMES[SQRT] = "sqrt";
        FUNC_NAMES[CUBRT] = "cubrt";
        FUNC_NAMES[SIN] = "sin";
        FUNC_NAMES[COS] = "cos";
        FUNC_NAMES[TG] = "tg";
        FUNC_NAMES[CTG] = "ctg";
        FUNC_NAMES[ARCSIN] = "arcsin";
        FUNC_NAMES[ARCCOS] = "arccos";
        FUNC_NAMES[ARCTG] = "arctg";
        FUNC_NAMES[ARCCTG] = "arcctg";
        FUNC_NAMES[SH] = "sh";
        FUNC_NAMES[CH] = "ch";
        FUNC_NAMES[TH] = "th";
        FUNC_NAMES[CTH] = "cth";
        FUNC_NAMES[ARCSH] = "arcsh";
        FUNC_NAMES[ARCCH] = "arcch";
        FUNC_NAMES[ARCTGH] = "arctgh";
        FUNC_NAMES[ARCCTGH] = "arcctgh";
        FUNC_NAMES[ARCSEC] = "arcsec";
        FUNC_NAMES[ARCCSC] = "arccsc";
        FUNC_NAMES[INT] = "int";
        FUNC_NAMES[NINT] = "Nint";
        FUNC_NAMES[UNITSTEPS] = "unitSteps";
        FUNC_NAMES[UNITBOX] = "unitBox";
        FUNC_NAMES[UNITSTEP] = "unitStep";
        FUNC_NAMES[SC] = "sc";
        FUNC_NAMES[CSC] = "csc";
        FUNC_NAMES[POW] = "^";
        FUNC_NAMES[RE] = "re";
        FUNC_NAMES[IM] = "im";
        FUNC_NAMES[GET_CALC_RESULT] = "getCalcResult";
        FUNC_NAMES[KORNYAK_MATRIX] = "kornyakMatrix";
        FUNC_NAMES[LOG] = "log";
        FUNC_NAMES[FACTORIAL] = "!";
        FUNC_NAMES[GAMMA] = "Gamma";
        FUNC_NAMES[GAMMA2] = "Gamma2";
        FUNC_NAMES[BINOMIAL] = "binom";
        FUNC_NAMES[SIGN] = "sign";
        FUNC_NAMES[ADD] = "+";
        FUNC_NAMES[CUP] = "cup";
        FUNC_NAMES[CAP] = "cap";
        FUNC_NAMES[COMPLEMENT] = "'";
        FUNC_NAMES[SET_MINUS] = "setminus";
        FUNC_NAMES[SYMMETRIC_DIFFERENCE] = "triangle";
        FUNC_NAMES[MULTIPLY] = "*";
        FUNC_NAMES[SUBTRACT] = "-";
        FUNC_NAMES[DIVIDE] = "/";
        FUNC_NAMES[B_LESS] = "<"; // without backslash   --------
        FUNC_NAMES[B_LE] = "le";
        FUNC_NAMES[B_GT] = ">";  // without backslash
        FUNC_NAMES[B_GE] = "ge";
        FUNC_NAMES[B_EQ] = "=="; // without backslash
        FUNC_NAMES[B_NE] = "ne";
        FUNC_NAMES[B_NOT] = "neg";
        FUNC_NAMES[B_OR] = "lor";
        FUNC_NAMES[B_AND] = "&";  // -------------------------
        FUNC_NAMES[SYSTLAE] = "systLAE";
        FUNC_NAMES[d] = "d";
        FUNC_NAMES[D] = "D";
        FUNC_NAMES[SYSTLDE] = "systLDE";
        FUNC_NAMES[INITCOND] = "initCond";
        FUNC_NAMES[EQUATION] = "equation";
        FUNC_NAMES[SEQUENCE] = "sequence";
        FUNC_NAMES[SERIES] = "sum";
        FUNC_NAMES[SUM] = "sum";
        FUNC_NAMES[PROD] = "prod";
//        FUNC_NAMES[DEFINITION] = "definition";
        FUNC_NAMES[LIM] = "lim";
        FUNC_NAMES[SOLVE] = "solve";
        FUNC_NAMES[SOLVETRIG] = "solveTrig";
        FUNC_NAMES[GROEBNERB] = "groebnerB";
        FUNC_NAMES[GROEBNER] = "groebner";
        FUNC_NAMES[REDUCEBYGB] = "reduceByGB";
        FUNC_NAMES[DOMAIN_OF_FUNC] = "func_with_dom";
        FUNC_NAMES[VECTORS] = "Vector";
        FUNC_NAMES[VECTOR_SET] = "VectorSet";
        FUNC_NAMES[EXPAND] = "expand";
        FUNC_NAMES[FACTOR] = "factor";
        FUNC_NAMES[SET2D] = "set2D";
        FUNC_NAMES[PLOT] = "plot";
        FUNC_NAMES[PARAMPLOT] = "paramPlot";
        FUNC_NAMES[TABLEPLOT] = "tablePlot";
        FUNC_NAMES[TABLEPLOT2] = "tablePlot2";
        FUNC_NAMES[TABLEPLOT4] = "tablePlot4";
        FUNC_NAMES[TABLE] = "table";
        FUNC_NAMES[REPLOT] = "replot";
        FUNC_NAMES[PLOT3D] = "plot3d";
        FUNC_NAMES[PARAMPLOT3D] = "paramPlot3d";
        FUNC_NAMES[IMPLICIT_PLOT3D] = "implicitPlot3d";
        FUNC_NAMES[EXPLICIT_PLOT3D] = "explicitPlot3d";
        FUNC_NAMES[PARAMETRIC_PLOT3D] = "parametricPlot3d";
        FUNC_NAMES[SHOW_3D] = "show3d";
        FUNC_NAMES[SHOWPLOTS] = "showPlots";
        FUNC_NAMES[POINTSPLOT] = "pointsPlot";
        FUNC_NAMES[PLOTGRAPH] = "plotGraph";
        FUNC_NAMES[TEXTPLOT] = "textPlot";

        FUNC_NAMES[HBOX] = "hbox";
        FUNC_NAMES[VALUE] = "value";
        FUNC_NAMES[SOLVELDE] = "solveLDE";
        FUNC_NAMES[SOLVEDE] = "solveDE";
        FUNC_NAMES[SOLVEHDE] = "solveHDE";
        FUNC_NAMES[TESTMATRIX] = "testMatrix";
        FUNC_NAMES[RANDOMPOLYNOM] = "randomPolynom";
        FUNC_NAMES[RANDOMMATRIX] = "randomMatrix";
        FUNC_NAMES[RANDOMNUMBER] = "randomNumber";
        FUNC_NAMES[FACTORINC] = "factorInC";
        FUNC_NAMES[LAPLACETRANSFORM] = "laplaceTransform";
        FUNC_NAMES[INVERSELAPLACETRANSFORM] = "inverseLaplaceTransform";

        FUNC_NAMES[SOLVETRANSFERFUNCTION] = "solveTransferFunction";
        FUNC_NAMES[SOLVETIMERESPONSE] = "solveTimeResponse";
        FUNC_NAMES[SOLVEFREQUENCERESPONSE] = "solveFrequenceResponse";

        FUNC_NAMES[FULLEXPAND] = "Expand";
        FUNC_NAMES[FULLFACTOR] = "Factor";
        FUNC_NAMES[SIMPLIFY] = "simplify";
        FUNC_NAMES[FFULLFACTOR] = "FACTOR";
        FUNC_NAMES[INVERSE] = "inverse";
        FUNC_NAMES[ADJOINT] = "adjoint";
        FUNC_NAMES[SOLVELAE] = "solveLAE";
        FUNC_NAMES[SOLVENAE] = "solveNAE";
        FUNC_NAMES[SOLVEEQ] = "solveLEQ";  //  solveLEQ  БЫЛО   2016
        FUNC_NAMES[DET] = "det";
        FUNC_NAMES[KERNEL] = "kernel";
        FUNC_NAMES[TRANSPOSE] = "transpose";
        FUNC_NAMES[TOECHELONFORM] = "toEchelonForm";
        FUNC_NAMES[CHARPOLYNOM] = "charPolynom";
        FUNC_NAMES[TOMATRIX] = "toMatrix";

        FUNC_NAMES[TIME] = "time";
        FUNC_NAMES[TABLEFROMFILE] = "tableFromFile";
        FUNC_NAMES[FROMFILE] = "fromFile";
        FUNC_NAMES[TOFILE] = "toFile";
        FUNC_NAMES[LS] = "ls";
        FUNC_NAMES[SYLVESTER] = "sylvester";
        //    FUNC_NAMES[PRS_STURM_OLD] = "PRSsturmSubres";
        FUNC_NAMES[GCDNUMPOLCOEFFS] = "GCDNumPolCoeffs";
        FUNC_NAMES[GCDHPOLCOEFFS] = "GCDHPolCoeffs";

        FUNC_NAMES[PRS] = "PRS";
        FUNC_NAMES[PRS_SUBRES] = "PRSsubres";
        FUNC_NAMES[AKRITAS] = "akritas";
        FUNC_NAMES[SYSTEM] = "system";
        FUNC_NAMES[SYSTEMOR] = "systemOR";
        FUNC_NAMES[TOVECTORDENCE] = "toVectorDence";
        FUNC_NAMES[TOVECTORSPARCE] = "toVectorSparce";
        FUNC_NAMES[VECTORTOPOLYNOM] = "vectorToPolynom";
        FUNC_NAMES[ISZERO] = "isZero";
        FUNC_NAMES[ISNEGATIVE] = "isNegative";
        FUNC_NAMES[ISONE] = "isOne";
        FUNC_NAMES[ISEVEN] = "isEven";
        FUNC_NAMES[ISINFINITE] = "isInfinite";
        FUNC_NAMES[ISNAN] = "isNaN";
        FUNC_NAMES[LEADINGCOEFF] = "leadingCoeff";
        FUNC_NAMES[SHIFTLEFT] = "shiftLeft";
        FUNC_NAMES[SHIFTRIGHT] = "shiftRight";
        FUNC_NAMES[RANK] = "rank";
        FUNC_NAMES[TAKEROW] = "takeRow";
        FUNC_NAMES[TAKECOLUMN] = "takeColumn";

        for (int i = 0; i < FUNC_NAMES.length; i++) {
            if (FUNC_NAMES[i] != null) {
                strMap.put(FUNC_NAMES[i], i);
            }
        }
    }

    public F() {
    }
    public F(int name, Element... args) {
        this.name = name;
        this.X = args;
    }
    public F(int name, Element f) {
        this.name = name;
        this.X = new Element[] {f};
    }
        public F(int name, F f) {
        this.name = name;
        this.X = new Element[] {f};
    }
        public F(int name, Element el1, Element el2) {
        this.name = name;
        this.X = new Element[] {el1,el2};
    }
     public F(Element el) {
        this.name = ID;
        this.X = new Element[] {el};
    }
    public F(MatrixS mat) {
        this.name = ID;
        this.X = new Element[] {mat};
    }

    public F(String strPol, Ring r) {
        Element f1 = Parser.getF(strPol, r);
        f1 = r.CForm.simplify_init(f1);
        if (f1 instanceof F) {
            this.name = ((F) f1).name;
            this.X = ((F) f1).X;
        } else {
            this.name = F.ID;
            this.X = new Element[] {f1};
        }
        r.CForm.returnFirstStatsCForm();
    }

    public F(String strPol, Ring r, int key) {
//        boolean radianFlag = ((key & 8) == 8);
//        boolean toOneRingFlag = ((key & 4) == 4);
//        boolean substituteFlag = ((key & 2) == 2);
//        boolean expandFlag = ((key & 1) == 1);
        F f1 = Parser.getF(strPol, r);
        f1 = (F) r.CForm.simplify_init(f1);
        this.name = f1.name;
        this.X = f1.X;
    }

    public F(String strPol, Ring r, boolean[] flags) {
        F f1 = Parser.getF(strPol, r);
        f1 = (F) r.CForm.simplify_init(f1);
        this.name = f1.name;
        this.X = f1.X;
    }

       
    public F FTwo_toPow() {
        return FfromNumber(new Fraction(2));
    }

    public F FfromNumber(Element el) {
        return new F(0, new Polynom(new int[0], new Element[] {el}));
    }

    @Override
    public Element one(Ring ring) {
        return POL_ONE;
    }

    @Override
    public Element zero(Ring ring) {
        return POL_ZERO;
    }

    @Override
    public Element minus_one(Ring ring) {
        return POL_MINUS_ONE;
    }

    @Override
    public F myOne(Ring ring) {
        return new F(ID, POL_ONE);
    }

    @Override
    public F myZero(Ring ring) {
        return new F(ID, POL_ZERO);
    }

    @Override
    public F myMinus_one(Ring ring) {
        return new F(ID, POL_MINUS_ONE);
    }

    /**
     * @param str name of function as string
     *
     * @return name as integer constant, -1 if there is no function with given
     * name.
     */
    public static int getNameFromStr(String str) {
        Integer num;
        if (str.charAt(0) == '\\') {
            num = strMap.get(str.substring(1));
        } else {
            num = strMap.get(str);
        }
        return num != null ? num.intValue() : -1;
    }

    /**
     * @param name name of function as integer constant
     *
     * @return number of function's arguments for function with predefined
     * arguments count.
     */
    public static int getArgsNum(int name) {
        if (name >= 0 && name < FIRST_FUNC_TWO_ARGS) {
            return 1;
        }
        if (name >= FIRST_FUNC_TWO_ARGS && name < FIRST_FUNC_MANY_ARGS) {
            return 2;
        }
        return 0;
    }

    public Element ExpandForID() {
        F f = this;
        if (f.name == F.TEXTPLOT || f.name == F.PLOT || f.name == F.PARAMPLOT || f.name == F.TABLEPLOT || f.name == F.POINTSPLOT || f.name == F.TABLEPLOT2 || f.name == F.TABLEPLOT4) {
            if (((F) f.X[0]).name == F.VECTORS) {
                for (int i = 0; i < ((F) f.X[0]).X.length; i++) {
                    if (((F) f.X[0]).X[i] instanceof F) {
                        if (((F) ((F) f.X[0]).X[i]).name == 0) {
                            if (((F) ((F) f.X[0]).X[i]).X[0] instanceof Fname) {
                                ((F) f.X[0]).X[i] = ((Fname) ((F) ((F) f.X[0]).X[i]).X[0]).X[0];
                            }
                        }
                    }
                }
            } else {
                if (((F) f.X[0]).name == 0) {
                    f.X[0] = ((Fname) ((F) f.X[0]).X[0]).X[0];
                }
            }
        }
        return f;
    }

    /**
     *
     * Для построения всех графиков
     *
     * @param page
     * @param arrParam
     * @param parcaPort
     * @param names
     *
     */
    public void showPlots(Page page, Element[] arrParam, boolean parcaPort, String[] names) {
        ArrayList<Element> f_1 = new ArrayList<Element>();//для явных функций
        ArrayList<Element> f_2 = new ArrayList<Element>();//для параметрических
        ArrayList<Element> f_3 = new ArrayList<Element>();//для табличных
        ArrayList<Element> f_4 = new ArrayList<Element>();//для pointsPlot
        ArrayList<Element> f_5 = new ArrayList<Element>();//для pointsPlot
        ArrayList<Element> f_6 = new ArrayList<Element>();//для pointsPlot
        ArrayList<Element> f_7 = new ArrayList<Element>();//для pointsPlot
        ArrayList<String> nameFunc = new ArrayList<String>();
        ArrayList<String> nameParamFunc = new ArrayList<String>();
        ArrayList<String> nameTableFunc = new ArrayList<String>();
        ArrayList<String> nameTablePointsFunc = new ArrayList<String>();
        double x0 = 0;
        double x1 = 0;
        double y0 = 0;
        double y1 = 0;
        double t0 = 0;
        double t1 = 0;
        String noAxes = "";
        String lattice = "";
        Element[] Xmas;//массив для параметрических функций
        Element[] tableFuncs;//массив для табличных графиков
        Element[] tablePointsFuncs;//массив для pointsPlot
        Element[] pointsText;//массив для pointsPlot
        Element[] pointsK1;//массив для pointsPlot
        Element[] pointsK2;//массив для pointsPlot
        double[][] xR = new double[][] {};//таблица для X
        double[][] yR = new double[][] {};//таблица для Y
        ArrayList<Boolean> arrayLineTheTablePlot = new ArrayList<Boolean>();
        //массив стилей для табличных графиков
        ArrayList<String> arrayStyleTheTablePlot = new ArrayList<String>();
        //массив стилей для графиков заданных в явном виде
        ArrayList<String> arrayStyleThePlot = new ArrayList<String>();
        //массив стилей для параметрических графиков
        ArrayList<String> arrayStyleTheParamPlot = new ArrayList<String>();
        ArrayList<NumberR64> tParamMin = new ArrayList<NumberR64>();
        ArrayList<NumberR64> tParamMax = new ArrayList<NumberR64>();
        //Значения границ для функция - \plot()
        ArrayList<NumberR64> plotParamX = new ArrayList<NumberR64>();
        ArrayList<NumberR64> plotParamY = new ArrayList<NumberR64>();
        //Значения векторов с текстовой информацией
        ArrayList<Element[]> sArg = new ArrayList<Element[]>();
        F ff;//явные функции
        if (X.length == 2 || X.length == 1) {
            if (((F) X[0]).name == F.VECTORS) {
                for (int i = 0; i < ((F) X[0]).X.length; i++) {
                    Vector<Element> Y = new Vector<Element>();//вектор границ по осям
                    Vector<Element> T = new Vector<Element>();//вектор параметров для параметрических функций
                    Vector<Element> tables = new Vector<Element>();//вектор для табличных функций
                    Vector<Element> tablesPoints = new Vector<Element>();//вектор для pointsPlot
                    Vector<Element> text = new Vector<Element>();//вектор для pointsPlot
                    Vector<Element> k1 = new Vector<Element>();//вектор для pointsPlot
                    Vector<Element> k2 = new Vector<Element>();//вектор для pointsPlot
                    Vector<Element[]> vS = new Vector<Element[]>();//вектор для textPlot
                    boolean tableF = false;
                    F f_input = new F();
                    if (((F) this.X[0]).X[i] instanceof F) {
                        CanonicForms cfs = new CanonicForms(Ring.ringR64xyzt, true);
                        f_input = (F) cfs.UnconvertToElement(
                                cfs.substituteValueToFnameAndCorrectPolynoms(((F) ((F) this.X[0]).X[i]), page.expr));
                    }
                    if (((F) this.X[0]).X[i] instanceof Fname) {
                        f_input = ((F) ((F) ((F) this.X[0]).X[i]).X[0]);
                    }
                    switch (f_input.name) {
                        case TEXTPLOT: {
                            f_input.textPlot(page, arrParam, parcaPort, vS, false);
                            for (int iS = 0; iS < vS.size(); iS++) {
                                sArg.add(vS.get(iS));
                            }
                            break;
                        }
                        case PLOT: {
                            nameFunc.add(names[i]);
                            f_input.plot(page, arrParam, parcaPort, Y, false);
                            if (f_input.X[0] instanceof VectorS) {
                                f_1.addAll(Arrays.asList(((VectorS) f_input.X[0]).V));
                            }
                            if (f_input.X[0] instanceof Fname) {
                                f_1.add(((Fname) f_input.X[0]).X[0]);
                            } else if (f_input.X[0] instanceof F) {
                                if (((F) f_input.X[0]).name == F.VECTORS) {
                                    F f_v = ((F) f_input.X[0]);
                                    for (int k = 0; k < f_v.X.length; k++) {
                                        if (f_v.X[k] instanceof Polynom) {
                                            f_1.add(f_v.X[k]);
                                        }
                                        if (f_v.X[k] instanceof F) {
                                            f_1.add(((F) f_v.X[k]));
                                        }
                                        if (f_v.X[k] instanceof Fname) {
                                            f_1.add(((Fname) f_v.X[k]).X[0]);
                                        }
                                    }
                                } else {
                                    if (f_input.X[0] instanceof Polynom) {
                                        f_1.add(f_input.X[0]);
                                    }
                                    if (f_input.X[0] instanceof F) {
                                        f_1.add(((F) f_input.X[0]));
                                    }
                                    if (f_input.X[0] instanceof Fname) {
                                        f_1.add(((Fname) f_input.X[0]).X[0]);
                                    }
                                }
                            } else {
                                if (f_input.X[0] instanceof Polynom) {
                                    f_1.add(f_input.X[0]);
                                }
                                if (f_input.X[0].isItNumber()) {
                                    f_1.add(f_input.X[0]);
                                }
                            }
                            if (f_input.X.length == 1) {//!=2 !!!!!!!!!!!!!
                                arrayStyleThePlot.add("");
                                plotParamX.add(null);
                                plotParamY.add(null);
                            }
                            if (f_input.X.length == 2) {
                                if (f_input.X[1] instanceof F) {
                                    arrayStyleThePlot.add(((Fname) ((F) f_input.X[1]).X[0]).name);
                                } else {
                                    arrayStyleThePlot.add("");
                                }
                                if (f_input.X[1] instanceof VectorS) {
                                    plotParamX.add(new NumberR64(((VectorS) f_input.X[1]).V[0].value));
                                    plotParamY.add(new NumberR64(((VectorS) f_input.X[1]).V[1].value));
                                } else {
                                    plotParamX.add(null);
                                    plotParamY.add(null);
                                }
                            }
                            if (f_input.X.length == 3) {
                                if (f_input.X[2] instanceof F) {
                                    arrayStyleThePlot.add(((Fname) ((F) f_input.X[2]).X[0]).name);
                                } else {
                                    arrayStyleThePlot.add("");
                                }
                                if (f_input.X[1] instanceof VectorS) {
                                    plotParamX.add(new NumberR64(((VectorS) f_input.X[1]).V[0].value));
                                    plotParamY.add(new NumberR64(((VectorS) f_input.X[1]).V[1].value));
                                } else {
                                    plotParamX.add(null);
                                    plotParamY.add(null);
                                }
                            }
                            break;
                        }
                        case PARAMPLOT: {
                            nameParamFunc.add(names[i]);
                            f_input.paramPlot(page, arrParam, parcaPort, t0, t1, x0, x1, y0, y1, Y, T, false);
                            if (f_input.X[0] instanceof F) {
                                if (((F) f_input.X[0]).name == F.VECTORS) {
                                    F f_v = ((F) f_input.X[0]);
                                    for (int k = 0; k < f_v.X.length; k++) {
                                        if (f_v.X[k] instanceof Polynom) {
                                            f_2.add(f_v.X[k]);
                                        }
                                        if (f_v.X[k] instanceof F) {
                                            f_2.add(((F) f_v.X[k]));
                                        }
                                        if (f_v.X[k] instanceof Fname) {
                                            f_2.add(((Fname) f_v.X[k]).X[0]);
                                        }
                                    }
                                }
                            } else {
                                if (f_input.X[0] instanceof VectorS) {
                                    Element[] vv = ((VectorS) f_input.X[0]).V;
                                    for (int ii = 0; ii < vv.length; ii++) {
                                        f_2.add(((VectorS) f_input.X[0]).V[ii]);
                                    }
                                }
                            }
                            if (f_input.X.length != 3) {
                                arrayStyleTheParamPlot.add("");
                            } else {
                                arrayStyleTheParamPlot.add(((Fname) ((F) f_input.X[2]).X[0]).name);
                            }
                            tParamMin.add(new NumberR64(T.get(0).doubleValue()));
                            tParamMax.add(new NumberR64(T.get(1).doubleValue()));
                            break;
                        }
                        case POINTSPLOT: {
                            nameTablePointsFunc.add(names[i]);
                            f_input.pointsPlot(page, arrParam, parcaPort, x0, x1, y0, y1, xR, yR, tablesPoints, text, k1, k2, false, false);
                            f_4.add(tablesPoints.get(0));
                            Element[] a1 = new Element[text.size()];
                            for (int i1 = 0; i1 < text.size(); i1++) {
                                a1[i1] = text.get(i1);
                            }
                            f_5.add(new VectorS(a1));
                            Element[] a2 = new Element[k1.size()];
                            for (int i1 = 0; i1 < k1.size(); i1++) {
                                a2[i1] = k1.get(i1);
                            }
                            f_6.add(new VectorS(a2));
                            Element[] a3 = new Element[k2.size()];
                            for (int i1 = 0; i1 < k2.size(); i1++) {
                                a3[i1] = k2.get(i1);
                            }
                            f_7.add(new VectorS(a3));
                            tableF = true;
                            arrayLineTheTablePlot.add(false);
                            break;
                        }
                        case TABLEPLOT: {
                            nameTableFunc.add(names[i]);
                            f_input.tablePlot(page, arrParam, parcaPort, x0, x1, y0, y1, xR, yR, tables, true, false);
                            f_3.add(tables.get(0));
                            tableF = true;
                            arrayLineTheTablePlot.add(true);
                            if (f_input.X.length != 2) {
                                arrayStyleTheTablePlot.add("");
                            } else {
                                arrayStyleTheTablePlot.add(((Fname) ((F) f_input.X[1]).X[0]).name);
                            }
                            break;
                        }
                    }
                    if ((!tableF) && (Y.size() > 0)) {
                        if (Y.get(0).doubleValue() < x0) {
                            x0 = Y.get(0).doubleValue();
                        }
                        if (Y.get(1).doubleValue() > x1) {
                            x1 = Y.get(1).doubleValue();
                        }
                        if (Y.get(2).doubleValue() < y0) {
                            y0 = Y.get(2).doubleValue();
                        }
                        if (Y.get(3).doubleValue() > y1) {
                            y1 = Y.get(3).doubleValue();
                        }
                    }
                    if (!T.isEmpty()) {
                        if (T.get(0).doubleValue() < t0) {
                            t0 = T.get(0).doubleValue();
                        }
                        if (T.get(1).doubleValue() > t1) {
                            t1 = T.get(1).doubleValue();
                        }
                    }
                }
            }
        }
        Element[] aa = new Element[f_1.size()];
        f_1.toArray(aa);
        ff = new F(F.VECTORS, aa);
        Element[] bb = new Element[f_2.size()];
        f_2.toArray(bb);
        Xmas = bb;
        Element[] cc = new Element[f_3.size()];
        f_3.toArray(cc);
        tableFuncs = cc;
        Element[] dd = new Element[f_4.size()];
        f_4.toArray(dd);
        tablePointsFuncs = dd;
        Element[] ee = new Element[f_5.size()];
        f_5.toArray(ee);
        pointsText = ee;
        Element[] fff = new Element[f_6.size()];
        f_6.toArray(fff);
        pointsK1 = fff;
        Element[] gg = new Element[f_7.size()];
        f_7.toArray(gg);
        pointsK2 = gg;
        boolean[] arrLineTheTablePlot = new boolean[arrayLineTheTablePlot.size()];
        for (int o = 0; o < arrayLineTheTablePlot.size(); o++) {
            arrLineTheTablePlot[o] = arrayLineTheTablePlot.get(o);
        }
        String[] arrStyleTheTablePlot = new String[arrayStyleTheTablePlot.size()];
        for (int o = 0; o < arrayStyleTheTablePlot.size(); o++) {
            arrStyleTheTablePlot[o] = arrayStyleTheTablePlot.get(o);
        }
        String[] arrStyleThePlot = new String[arrayStyleThePlot.size()];
        for (int o = 0; o < arrayStyleThePlot.size(); o++) {
            arrStyleThePlot[o] = arrayStyleThePlot.get(o);
        }
        String[] arrStyleTheParamPlot = new String[arrayStyleTheParamPlot.size()];
        for (int o = 0; o < arrayStyleTheParamPlot.size(); o++) {
            arrStyleTheParamPlot[o] = arrayStyleTheParamPlot.get(o);
        }
        try {
            if (this.X.length == 1) {
                noAxes = "";
                lattice = "";
            }
            if (this.X.length == 2) {
                if (this.X[1] instanceof F) {
                    if (((F) this.X[1]).name == F.HBOX) {
                        Fname fn = (Fname) ((F) ((F) this.X[1]).X[0]).X[0];
                        if (fn.name.equals("lattice")) {
                            lattice = fn.name;
                        } else {
                            noAxes = fn.name;
                        }
                    }
                } else {
                    if (this.X[1].toString(Ring.ringR64xyzt).equals("lattice")) {
                        lattice = this.X[1].toString(Ring.ringR64xyzt);
                    } else {
                        noAxes = this.X[1].toString(Ring.ringR64xyzt);
                    }
                }
            }
            nameFunc.addAll(nameParamFunc);
            nameFunc.addAll(nameTableFunc);
            nameFunc.addAll(nameTablePointsFunc);
            Element[] tParamMin1 = new Element[tParamMin.size()];
            for (int o = 0; o < arrayStyleTheParamPlot.size(); o++) {
                tParamMin1[o] = tParamMin.get(o);
            }
            Element[] tParamMax1 = new Element[tParamMax.size()];
            for (int o = 0; o < arrayStyleTheParamPlot.size(); o++) {
                tParamMax1[o] = tParamMax.get(o);
            }
            //границы по оси X для функции plot()
            Element[] plotParamX1 = new Element[plotParamX.size()];
            for (int o = 0; o < plotParamX.size(); o++) {
                plotParamX1[o] = plotParamX.get(o);
            }
            //границы по оси Y для функции plot()
            Element[] plotParamY1 = new Element[plotParamY.size()];
            for (int o = 0; o < plotParamY.size(); o++) {
                plotParamY1[o] = plotParamY.get(o);
            }
            //обработка для textPlot
            Element[][] s = new Element[sArg.size()][];
            for (int ii = 0; ii < s.length; ii++) {
                s[ii] = sArg.get(ii);
            }
//            Plots b = new Plots(page, ff, Xmas, tableFuncs, x0, x1, y0, y1, t0, t1,
//                    parcaPort, arrParam, nameFunc.toArray(new String[names.length]),
//                    arrLineTheTablePlot, tablePointsFuncs, pointsText, pointsK1, pointsK2, noAxes, lattice,
//                    arrStyleTheTablePlot, arrStyleThePlot, arrStyleTheParamPlot, tParamMin1, tParamMax1, plotParamX1, plotParamY1, s);
        } catch (Exception ex) {
            throw new MathparException("Exception in 2D plot", ex);
        }
    }

    /**
     *
     * Построение графиков для textPlot
     *
     * @param parcaPort
     * @param sect
     * @param x_str
     * @param y_str
     * @param title_str
     */
    public void textPlot(Page page, Element[] arr, boolean parcaPort, Vector<Element[]> sArg, boolean isShowPlots) {
        Ring ring = page.ring;
        Element[] arrPointsParam = arr;
        Element[][] s = new Element[X.length][];
        for (int i = 0; i < X.length; i++) {
            if (X[i] instanceof F) {
                F f = ((F) X[i]);
                if (f.name == F.VECTORS) {
                    s[i] = new Element[f.X.length];
                    for (int j = 0; j < f.X.length; j++) {
                        if (f.X[j] instanceof F) {
                            F ff = ((F) f.X[j]);
                            s[i][j] = page.expandFnameOrId(ff.X[0]);
                        }
                        if (f.X[j] instanceof Polynom) {
                            Polynom ff = ((Polynom) f.X[j]);
                            if (ff.isZero(ring)) {
                                s[i][j] = new Element(0);
                            } else {
                                s[i][j] = ff.coeffs[0];
                            }
                        }
                    }
                    sArg.add(s[i]);
                }
            }
            if (X[i] instanceof VectorS) {
                VectorS f = ((VectorS) X[i]);
                s[i] = new Element[f.V.length];
                for (int j = 0; j < f.V.length; j++) {
                    if (f.V[j] instanceof F) {
                        F ff = ((F) f.V[j]);
                        s[i][j] = page.expandFnameOrId(ff.X[0]);
                    }
                    if (f.V[j] instanceof Polynom) {
                        Polynom ff = ((Polynom) f.V[j]);
                        if (ff.isZero(ring)) {
                            s[i][j] = new Element(0);
                        } else {
                            s[i][j] = ff.coeffs[0];
                        }
                    }
                    if (f.V[j].isItNumber()) {
                        s[i][j] = f.V[j];
                    }
                }
                sArg.add(s[i]);
            }
        }
        try {
            if (isShowPlots == true) {
              //  Plots b = new Plots(page, s, parcaPort, page.ring, arrPointsParam);
            }
        } catch (Exception ex) {
            throw new MathparException("Exception in 2D plot", ex);
        }
    }

    /**
     * Проверка границ frame для всех типов plots
     *
     * @param p
     *
     * @throws MathparException
     */
    public void plotException(Page p) throws MathparException {
        try {
            if (p.xMax == 0 && p.xMin == 0 && p.yMax == 0 && p.yMin == 0) {
            } else {
                if (p.xMax - p.xMin == 0) {
                    throw new MathparException("Exception in 2D plot");
                }
                if (p.yMax - p.yMin == 0) {
                    throw new MathparException("Exception in 2D plot");
                }
                if (p.xMax < p.xMin) {
                    throw new MathparException("Exception in 2D plot");
                }
                if (p.yMax < p.yMin) {
                    throw new MathparException("Exception in 2D plot");
                }
            }
        } catch (Exception ex) {
            throw new MathparException("Exception in 2D plot", ex);
        }
    }

    /**
     * Построение графиков для явных функций
     *
     * @param parcaPort
     * @param sect
     * @param x_str
     * @param y_str
     * @param title_str
     */
    public void plot(Page page, Element[] arr, boolean parcaPort, Vector<Element> Y, boolean isShowPlots) {
        Ring ring = page.ring;
        //стиль отображения линий и стрелок 
        String style = "";
        Element[] arrPointsParam = arr;
        F f_input;
        F f_input1;
        Element[] el = null;
        if (X.length == 1) {
            el = new Element[2];
            el[0] = X[0];
            el[1] = Polynom.polynom_one(NumberR64.ONE);
        }
        if (X.length == 2) {
            el = new Element[3];
            el[0] = X[0];
            el[1] = X[1];
            el[2] = Polynom.polynom_one(NumberR64.ONE);
        }
        if (X.length == 3) {
            el = new Element[4];
            el[0] = X[0];
            el[1] = X[1];
            el[2] = X[2];
            el[3] = Polynom.polynom_one(NumberR64.ONE);
        }
        CanonicForms Cfs = new CanonicForms(ring, true);
        f_input1 = new F(this.name, el);
        f_input = (F) Cfs.UnconvertToElement(Cfs.substituteValueToFnameAndCorrectPolynoms(f_input1, page.expr));
        if (f_input.X[0] instanceof VectorS) {
            Element ell[] = new Element[((VectorS) f_input.X[0]).V.length];
            System.arraycopy(((VectorS) f_input.X[0]).V, 0, ell, 0, ((VectorS) f_input.X[0]).V.length);
            f_input.X[0] = new F(VECTORS, ell);
        }
        if (f_input.X[0] instanceof VectorS) {
            Element ell[] = new Element[((VectorS) f_input.X[0]).V.length];
            System.arraycopy(((VectorS) f_input.X[0]).V, 0, ell, 0, ((VectorS) f_input.X[0]).V.length);
            f_input.X[0] = new F(VECTORS, ell);
        } else if (f_input.X[0] instanceof F) {
            if (((F) f_input.X[0]).name == F.VECTORS) {
                for (int i = 0; i < ((F) f_input.X[0]).X.length; i++) {
                    if (((F) f_input.X[0]).X[i] instanceof F) {
                        if (((F) ((F) f_input.X[0]).X[i]).name == 0) {
                            if (((F) ((F) f_input.X[0]).X[i]).X[0] instanceof Fname) {
                                ((F) f_input.X[0]).X[i] = ((Fname) ((F) ((F) f_input.X[0]).X[i]).X[0]).X[0];
                            }
                        }
                    }
                    if (((F) f_input.X[0]).X[i] instanceof Fname) {
                        ((F) f_input.X[0]).X[i] = ((Fname) ((F) f_input.X[0]).X[i]).X[0];
                    }
                }
            }
        }
        if (f_input.X[0] instanceof Fname) {
            f_input.X[0] = new F(VECTORS, ((Fname) f_input.X[0]).X[0]);
        }
        if (f_input.X[0] instanceof F) {
            if (((F) f_input.X[0]).name == 0) {
                if (((F) f_input.X[0]).X[0] instanceof Fname) {
                    f_input.X[0] = ((Fname) ((F) f_input.X[0]).X[0]).X[0];
                }
            }
        }
        if (f_input.X[0] instanceof Polynom) {
            f_input.X[0] = new F(f_input.X[0]);
        }
        if (f_input.X[0].isItNumber()) {
            f_input.X[0] = new F(f_input.X[0]);
        }
        if (f_input.X[0] instanceof F) {
            if (((F) f_input.X[0]).name != F.VECTORS) {
                f_input.X[0] = new F(VECTORS, f_input.X[0]);
            }
        }
        double x0 = 0;
        double x1 = 0;
        double y0 = 0;
        double y1 = 0;

        if (f_input.X.length == 2) {
            Y.add(new NumberR64(page.xMin));
            Y.add(new NumberR64(page.xMax));
            Y.add(new NumberR64(page.yMin));
            Y.add(new NumberR64(page.yMax));
            try {
                plotException(page);
                if (isShowPlots == true) {
                  //  Plots b = new Plots(page, ((F) f_input.X[0]), parcaPort, page.ring, arrPointsParam, style);//обработка явных функций
                }
            } catch (Exception ex) {
                throw new MathparException("Exception in 2D plot", ex);
            }
        }

        if (f_input.X.length == 3) {
            Element[] ee = new Element[] {};
            if (f_input.X[1] instanceof VectorS) {
                ee = ((VectorS) f_input.X[1]).V;
            } else {
                ee = ((F) (f_input.X[1].ExpandFnameOrId())).X;
            }
            if (ee.length == 2) {
                if (ee[0] instanceof F) {
                    x0 = ((F) ee[0]).valueOf(new Element[0], ring).doubleValue();
                } else {
                    if (ee[0] instanceof Polynom) {
                        x0 = ((Polynom) ee[0]).constantTerm(ring).doubleValue();
                    } else {
                        x0 = ee[0].doubleValue();
                    }
                }
                if (ee[1] instanceof F) {
                    x1 = ((F) ee[1]).valueOf(new Element[0], ring).doubleValue();
                } else {
                    if (ee[1] instanceof Polynom) {
                        x1 = ((Polynom) ee[1]).constantTerm(ring).doubleValue();
                    } else {
                        x1 = ee[1].doubleValue();
                    }
                }
                try {
                    if (f_input.X[0] instanceof VectorS) {
                        if (((VectorS) f_input.X[0]).V.length == 1) {
                            if (isShowPlots == true) {
                          //      Plots b = new Plots(page, ((F) ((VectorS) f_input.X[0]).V[0]),
                          //              x0, x1, parcaPort, page.ring, arrPointsParam, style);//обработка явных функций
                            }
                        } else {
                            if (isShowPlots == true) {
                              //  Plots b = new Plots(page, f_input.X[0], x0, x1,
                                   //     parcaPort, page.ring, arrPointsParam, style);//обработка явных функций
                            }
                        }
                    }
                    Y.add(new NumberR64(page.xMin));
                    Y.add(new NumberR64(page.xMax));
                    Y.add(new NumberR64(page.yMin));
                    Y.add(new NumberR64(page.yMax));
                    if (isShowPlots == true) {
                     //   Plots b = new Plots(page, ((F) f_input.X[0]), x0, x1, parcaPort,
                          //      page.ring, arrPointsParam, style);//обработка явных функций
                    }
                } catch (Exception ex) {
                    throw new MathparException("Exception in 2D plot", ex);
                }
            }
            if (f_input.X[1] instanceof F) {
                style = ((Fname) ((F) f_input.X[1]).X[0]).name;
                Y.add(new NumberR64(page.xMin));
                Y.add(new NumberR64(page.xMax));
                Y.add(new NumberR64(page.yMin));
                Y.add(new NumberR64(page.yMax));
                try {
                    plotException(page);
                    if (isShowPlots == true) {
                     //   Plots b = new Plots(page, ((F) f_input.X[0]), parcaPort, page.ring, arrPointsParam, style);//обработка явных функций
                    }
                } catch (Exception ex) {
                    throw new MathparException("Exception in 2D plot", ex);
                }
            }
        }

        if (f_input.X.length == 4) {
            try {
                Element[] ee = new Element[] {};
                if (f_input.X[1] instanceof VectorS) {
                    ee = ((VectorS) f_input.X[1]).V;
                } else {
                    ee = ((F) (f_input.X[1].ExpandFnameOrId())).X;
                }
                if (f_input.X[2] instanceof F) {
                    style = ((Fname) ((F) f_input.X[2]).X[0]).name;
                }
                if (ee.length == 2) {
                    if (ee[0] instanceof F) {
                        x0 = ((F) ee[0]).valueOf(new Element[0], ring).doubleValue();
                    } else {
                        if (ee[0] instanceof Polynom) {
                            x0 = ((Polynom) ee[0]).constantTerm(ring).doubleValue();
                        } else {
                            x0 = ee[0].doubleValue();
                        }
                    }
                    if (ee[1] instanceof F) {
                        x1 = ((F) ee[1]).valueOf(new Element[0], ring).doubleValue();
                    } else {
                        if (ee[1] instanceof Polynom) {
                            x1 = ((Polynom) ee[1]).constantTerm(ring).doubleValue();
                        } else {
                            x1 = ee[1].doubleValue();
                        }
                    }
                    if (f_input.X[0] instanceof VectorS) {
                        if (((VectorS) f_input.X[0]).V.length == 1) {
//                            Plots b = new Plots(page, ((F) ((VectorS) f_input.X[0]).V[0]),
//                                    x0, x1, parcaPort, page.ring, arrPointsParam, style);//обработка явных функций
                        } else {
//                            Plots b = new Plots(page, f_input.X[0], x0, x1,
//                                    parcaPort, page.ring, arrPointsParam, style);//обработка явных функций
                        }
                    }
                    Y.add(new NumberR64(page.xMin));
                    Y.add(new NumberR64(page.xMax));
                    Y.add(new NumberR64(page.yMin));
                    Y.add(new NumberR64(page.yMax));
                }
                if (isShowPlots == true) {
//                    Plots b = new Plots(page, ((F) f_input.X[0]), x0, x1, parcaPort,
//                            page.ring, arrPointsParam, style);
                }
            } catch (Exception ex) {
                throw new MathparException("Exception in 2D plot", ex);
            }
        }
    }

    /**
     * Построение графиков для функций заданных параметрически
     *
     * @param parcaPort
     * @param sect
     * @param x_str
     * @param y_str
     * @param title_str
     */
    public void paramPlot(Page page, Element[] arrs, boolean parcaPort,
            double t0, double t1,
            double x0, double x1, double y0, double y1, Vector Y, Vector T, boolean isShowPlots) {
        Ring ring = page.ring;
        Element[] arrPointsParam = arrs;
        F f_input;
        F f_input1;
        String style = "";
        CanonicForms Cfs = new CanonicForms(ring, true);
        if (X.length == 2) {
            Element[] el = new Element[X.length + 1];
            el[0] = X[0];
            el[1] = X[1];
            el[2] = Polynom.polynom_one(NumberR64.ONE);
            f_input1 = new F(this.name, el);
            f_input = (F) Cfs.UnconvertToElement(Cfs.substituteValueToFnameAndCorrectPolynoms(f_input1, page.expr));

        } else {
            f_input1 = new F(this.name, X);
            f_input = (F) Cfs.UnconvertToElement(Cfs.substituteValueToFnameAndCorrectPolynoms(f_input1, page.expr));
            style = ((Fname) ((F) f_input.X[2]).X[0]).name;
        }
        t0 = 0;//1 параметр
        t1 = 0;//2 параметр
//        FvalOf fvof = new FvalOf(ring);
//        if (f_input.X[1] instanceof VectorS) {
//            if (((VectorS) f_input.X[1]).V.length == 2) {
//                if (!(((VectorS) f_input.X[1]).V[0] instanceof Polynom)) {
//                    t0 = fvof.valOf(((VectorS) f_input.X[1]).V[0], new Element[0]).doubleValue();
//                } else if (((Polynom) ((VectorS) f_input.X[1]).V[0]).coeffs.length == 0) {
//                    t0 = 0;
//                } else {
//                    t0 = ((Polynom) ((VectorS) f_input.X[1]).V[0]).constantTerm(ring).doubleValue();
//                }
//                if (!(((VectorS) f_input.X[1]).V[1] instanceof Polynom)) {
//                    t1 = fvof.valOf(((VectorS) f_input.X[1]).V[1], new Element[0]).doubleValue();
//                } else if (((Polynom) ((VectorS) f_input.X[1]).V[1]).coeffs.length == 0) {
//                    t1 = 0;
//                } else {
//                    t1 = ((Polynom) ((VectorS) f_input.X[1]).V[1]).constantTerm(ring).doubleValue();
//                }
//            }
//        } else if (((F) f_input.X[1]).X.length == 2) {
//            if (((Polynom) ((F) f_input.X[1]).X[0]).coeffs.length == 0) {
//                t0 = 0;
//            } else {
//                t0 = ((Polynom) ((F) f_input.X[1]).X[0]).constantTerm(ring).doubleValue();
//            }
//            if (((Polynom) ((F) f_input.X[1]).X[1]).coeffs.length == 0) {
//                t1 = 0;
//            } else {
//                t1 = ((Polynom) ((F) f_input.X[1]).X[1]).constantTerm(ring).doubleValue();
//            }
//        }
        double a = (t1 - t0) / 100;//шаг
        Element[] arr;
        int q = 0;
        if (f_input.X[0] instanceof VectorS) {
            arr = new Element[((VectorS) f_input.X[0]).V.length];
            for (int i = 0; i < ((VectorS) f_input.X[0]).V.length; i++) {
                if (((VectorS) f_input.X[0]).V[i] instanceof F) {
                    arr[q] = ((VectorS) f_input.X[0]).V[i];
                } else {
                    if (((VectorS) f_input.X[0]).V[i] instanceof Polynom) {
                        arr[q] = ((VectorS) f_input.X[0]).V[i];
                    } else {
                        if (((VectorS) f_input.X[0]).V[i] instanceof Fname) {
                            arr[q] = ((Fname) ((VectorS) f_input.X[0]).V[i]).X[0];
                        } else {
                            if (((VectorS) f_input.X[0]).V[i] instanceof Fraction) {
                                arr[q] = ((VectorS) f_input.X[0]).V[i];
                            } else {
                                arr[q] = ((VectorS) f_input.X[0]).V[i];
                            }
                        }
                    }
                }
                q++;
            }
        } else {
            arr = new Element[((F) f_input.X[0]).X.length];
            for (int i = 0; i < ((F) f_input.X[0]).X.length; i++) {
                if (((F) f_input.X[0]).X[i] instanceof F) {
                    arr[q] = ((F) f_input.X[0]).X[i];
                } else {
                    if (((F) f_input.X[0]).X[i] instanceof Polynom) {
                        arr[q] = ((F) f_input.X[0]).X[i];
                    } else {
                        if (((F) f_input.X[0]).X[i] instanceof Fname) {
                            arr[q] = ((Fname) ((F) f_input.X[0]).X[i]).X[0];
                        } else {
                            if (((VectorS) f_input.X[0]).V[i] instanceof Fraction) {
                                arr[q] = ((VectorS) f_input.X[0]).V[i];
                            } else {
                            }
                        }
                    }
                }
                q++;
            }
        }
        for (int gg = 0; gg < arr.length; gg++) {
            arr[gg] = arr[gg].ExpandFnameOrId();
        }
        x0 = 0;
        x1 = 0;
        y0 = 0;
        y1 = 0;
        //массив переменных для подстановки
        Element[] vars = new Element[page.ring.varNames.length];
        for (int num = 1; num < vars.length; num++) {
            vars[num] = new NumberR64(0);
        }
        vars[0] = new NumberR64(t1);
        if (arr[0] instanceof Polynom) {
            x0 = (((Polynom) arr[0]).value(vars, ring)).doubleValue();
            x1 = (((Polynom) arr[0]).value(vars, ring)).doubleValue();
        } else {
            if (arr[0] instanceof Fraction) {
                x0 = (((Fraction) arr[0]).value(vars, ring)).doubleValue();
                x1 = (((Fraction) arr[0]).value(vars, ring)).doubleValue();
            } else {
                if (arr[0] instanceof F) {
                    x0 = (((F) arr[0]).valueOf(vars, ring)).doubleValue();
                    x1 = (((F) arr[0]).valueOf(vars, ring)).doubleValue();
                } else {
                    x0 = (arr[0]).doubleValue();
                    x1 = (arr[0]).doubleValue();
                }
            }
        }
        if (arr[1] instanceof Polynom) {
            y0 = (((Polynom) arr[1]).value(vars, ring)).doubleValue();
            y1 = (((Polynom) arr[1]).value(vars, ring)).doubleValue();
        } else {
            if (arr[1] instanceof F) {
                y0 = (((F) arr[1]).valueOf(vars, ring)).doubleValue();
                y1 = (((F) arr[1]).valueOf(vars, ring)).doubleValue();
            } else {
                if (arr[1] instanceof Fraction) {
                    y0 = (((Fraction) arr[1]).value(vars, ring)).doubleValue();
                    y1 = (((Fraction) arr[1]).value(vars, ring)).doubleValue();
                } else {
                    y0 = (arr[1]).doubleValue();
                    y1 = (arr[1]).doubleValue();
                }
            }
        }
        double i = t0 + a;
        vars[0] = new NumberR64(i);
        while (i < t1) {
            for (int j = 0; j < arr.length; j++) {
                if (j % 2 == 0) {
                    if (arr[j] instanceof Polynom) {
                        if (x0 > (((Polynom) arr[j]).value(vars, ring)).doubleValue()) {
                            x0 = (((Polynom) arr[j]).value(vars, ring)).doubleValue();
                        }
                        if (x1 < (((Polynom) arr[j]).value(vars, ring)).doubleValue()) {
                            x1 = (((Polynom) arr[j]).value(vars, ring)).doubleValue();
                        }
                    } else {
                        if (arr[j] instanceof Fraction) {
                            if (x0 > (((Fraction) arr[j]).value(vars, ring)).doubleValue()) {
                                x0 = (((Fraction) arr[j]).value(vars, ring)).doubleValue();
                            }
                            if (x1 < (((Fraction) arr[j]).value(vars, ring)).doubleValue()) {
                                x1 = (((Fraction) arr[j]).value(vars, ring)).doubleValue();
                            }
                        } else {
                            if (arr[j] instanceof F) {
                                if (x0 > (((F) arr[j]).valueOf(vars, ring)).doubleValue()) {
                                    x0 = (((F) arr[j]).valueOf(vars, ring)).doubleValue();
                                }
                                if (x1 < (((F) arr[j]).valueOf(vars, ring)).doubleValue()) {
                                    x1 = (((F) arr[j]).valueOf(vars, ring)).doubleValue();
                                }
                            } else {
                                if (x0 > (arr[j]).doubleValue()) {
                                    x0 = (arr[j]).doubleValue();
                                }
                                if (x1 < (arr[j]).doubleValue()) {
                                    x1 = (arr[j]).doubleValue();
                                }
                            }
                        }
                    }
                } else {
                    if (arr[j] instanceof Polynom) {
                        if (y0 > (((Polynom) arr[j]).value(vars, ring)).doubleValue()) {
                            y0 = (((Polynom) arr[j]).value(vars, ring)).doubleValue();
                        }
                        if (y1 < (((Polynom) arr[j]).value(vars, ring)).doubleValue()) {
                            y1 = (((Polynom) arr[j]).value(vars, ring)).doubleValue();
                        }
                    } else {
                        if (arr[j] instanceof Fraction) {
                            if (y0 > (((Fraction) arr[j]).value(vars, ring)).doubleValue()) {
                                y0 = (((Fraction) arr[j]).value(vars, ring)).doubleValue();
                            }
                            if (y1 < (((Fraction) arr[j]).value(vars, ring)).doubleValue()) {
                                y1 = (((Fraction) arr[j]).value(vars, ring)).doubleValue();
                            }
                        } else {
                            if (arr[j] instanceof F) {
                                if (y0 > (((F) arr[j]).valueOf(vars, ring)).doubleValue()) {
                                    y0 = (((F) arr[j]).valueOf(vars, ring)).doubleValue();
                                }
                                if (y1 < (((F) arr[j]).valueOf(vars, ring)).doubleValue()) {
                                    y1 = (((F) arr[j]).valueOf(vars, ring)).doubleValue();
                                }
                            } else {
                                if (y0 > (arr[j]).doubleValue()) {
                                    y0 = (arr[j]).doubleValue();
                                }
                                if (y1 < (arr[j]).doubleValue()) {
                                    y1 = (arr[j]).doubleValue();
                                }
                            }
                        }
                    }
                }
                i = i + a;
                vars[0] = new NumberR64(i);
            }
        }
        Y.add(new NumberR64(x0));
        Y.add(new NumberR64(x1));
        Y.add(new NumberR64(y0));
        Y.add(new NumberR64(y1));
        T.add(new NumberR64(t0));
        T.add(new NumberR64(t1));
        try {
            plotException(page);
            if (isShowPlots == true) {
//                Plots b = new Plots(page, arr, x0, x1, y0, y1, t0, t1, parcaPort,
//                        page.ring, arrPointsParam, style);//обработка параметрических функций
            }
        } catch (Exception ex) {
            throw new MathparException("Exception in 2D plot", ex);
        }
    }

    /**
     * Обработка отрисовки графа
     *
     * @param page
     * @param parcaPort
     */
    public void plotGraph(Page page, boolean parcaPort) {
        F f_input1;
        F f_input;
        CanonicForms Cfs = new CanonicForms(Ring.ringR64xyzt, true);
        f_input1 = new F(this.name, X);
        f_input = (F) Cfs.UnconvertToElement(Cfs.substituteValueToFnameAndCorrectPolynoms(f_input1, page.expr));
        if (f_input.X.length == 2) {
            if (f_input.X[0] instanceof MatrixD && f_input.X[1] instanceof MatrixD) {
                //Матрица инцидентности
                MatrixD I = ((MatrixD) f_input.X[0]);
                //Матрица координат
                MatrixD M = ((MatrixD) f_input.X[1]);
                //Набор графиков
                ArrayList<Element> arrayTables = new ArrayList<Element>();
                if (I.M.length == M.M[0].length) {
                    for (int i = 0; i < I.M.length; i++) {
                        for (int j = i; j < I.M.length; j++) {
                            Element el = I.M[i][j];
                            if (!el.isZero(Ring.ringR64xyzt)) {
                                ArrayList<Element> xTable = new ArrayList<Element>();
                                ArrayList<Element> yTable = new ArrayList<Element>();
                                xTable.add(M.M[0][i]);
                                yTable.add(M.M[1][i]);

                                xTable.add(M.M[0][j]);
                                yTable.add(M.M[1][j]);

                                //Номера смежных вершин
                                xTable.add(new NumberZ(i + 1));
                                yTable.add(new NumberZ(j + 1));

                                Element[] arr1 = new Element[xTable.size()];
                                xTable.toArray(arr1);
                                Element[] arr2 = new Element[yTable.size()];
                                yTable.toArray(arr2);
                                Element[][] arrNewTable = new Element[2][];
                                arrNewTable[0] = arr1;
                                arrNewTable[1] = arr2;
                                //Добавление ребра в набор графиков
                                arrayTables.add(new Table(new MatrixD(arrNewTable)));
                            }
                        }
                    }
                }
                Element[] arr = new Element[arrayTables.size()];
                arrayTables.toArray(arr);
                try {
              //      Plots b = new Plots(page, parcaPort, arr);
                } catch (Exception ex) {
                    throw new MathparException("Exception in plotGraph", ex);
                }
            } else {
                if (f_input.X[0] instanceof VectorS && f_input.X[1] instanceof VectorS) {
                    //Матрица инцидентности
                    VectorS I1 = ((VectorS) f_input.X[0]);
                    //Матрица координат
                    VectorS M1 = ((VectorS) f_input.X[1]);
                    MatrixD I = new MatrixD(I1);
                    MatrixD M = new MatrixD(M1);
                    //Набор графиков
                    ArrayList<Element> arrayTables = new ArrayList<Element>();
                    if (I.M.length == M.M[0].length) {
                        for (int i = 0; i < I.M.length; i++) {
                            for (int j = i; j < I.M.length; j++) {
                                Element el = I.M[i][j];
                                if (!el.isZero(Ring.ringR64xyzt)) {
                                    ArrayList<Element> xTable = new ArrayList<Element>();
                                    ArrayList<Element> yTable = new ArrayList<Element>();
                                    xTable.add(M.M[0][i]);
                                    yTable.add(M.M[1][i]);

                                    xTable.add(M.M[0][j]);
                                    yTable.add(M.M[1][j]);

                                    //Номера смежных вершин
                                    xTable.add(new NumberZ(i + 1));
                                    yTable.add(new NumberZ(j + 1));

                                    Element[] arr1 = new Element[xTable.size()];
                                    xTable.toArray(arr1);
                                    Element[] arr2 = new Element[yTable.size()];
                                    yTable.toArray(arr2);
                                    Element[][] arrNewTable = new Element[2][];
                                    arrNewTable[0] = arr1;
                                    arrNewTable[1] = arr2;
                                    //Добавление ребра в набор графиков
                                    arrayTables.add(new Table(new MatrixD(arrNewTable)));
                                }
                            }
                        }
                    }
                    Element[] arr = new Element[arrayTables.size()];
                    arrayTables.toArray(arr);
                    try {
                   //     Plots b = new Plots(page, parcaPort, arr);
                    } catch (Exception ex) {
                        throw new MathparException("Exception in plotGraph", ex);
                    }
                }
            }
        } else {
            if (f_input.X[0] instanceof MatrixD) {
                //Матрица инцидентности
                MatrixD I = ((MatrixD) f_input.X[0]);
                //Матрица координат
                MatrixD M = Element.circl(I.M.length);
                //Набор графиков
                ArrayList<Element> arrayTables = new ArrayList<Element>();
                if (I.M.length == M.M[0].length) {
                    for (int i = 0; i < I.M.length; i++) {
                        for (int j = i; j < I.M.length; j++) {
                            Element el = I.M[i][j];
                            if (!el.isZero(Ring.ringR64xyzt)) {
                                ArrayList<Element> xTable = new ArrayList<Element>();
                                ArrayList<Element> yTable = new ArrayList<Element>();
                                xTable.add(M.M[0][i]);
                                yTable.add(M.M[1][i]);
                                xTable.add(M.M[0][j]);
                                yTable.add(M.M[1][j]);
                                //Номера смежных вершин
                                xTable.add(new NumberZ(i + 1));
                                yTable.add(new NumberZ(j + 1));
                                Element[] arr1 = new Element[xTable.size()];
                                xTable.toArray(arr1);
                                Element[] arr2 = new Element[yTable.size()];
                                yTable.toArray(arr2);
                                Element[][] arrNewTable = new Element[2][];
                                arrNewTable[0] = arr1;
                                arrNewTable[1] = arr2;
                                //Добавление ребра в набор графиков
                                arrayTables.add(new Table(new MatrixD(arrNewTable)));
                            }
                        }
                    }
                }
                Element[] arr = new Element[arrayTables.size()];
                arrayTables.toArray(arr);
                try {
                //    Plots b = new Plots(page, parcaPort, arr);
                } catch (Exception ex) {
                    throw new MathparException("Exception in plotGraph", ex);
                }
            } else {
                if (f_input.X[0] instanceof VectorS) {
                    //Матрица инцидентности
                    VectorS I = ((VectorS) f_input.X[0]);
                    //Матрица координат
                    MatrixD D = new MatrixD(I);
                    MatrixD M = Element.circl(D.M.length);
                    //Набор графиков
                    ArrayList<Element> arrayTables = new ArrayList<Element>();
                    if (D.M.length == M.M[0].length) {
                        for (int i = 0; i < D.M.length; i++) {
                            for (int j = i; j < D.M.length; j++) {
                                Element el = D.M[i][j];
                                if (!el.isZero(Ring.ringR64xyzt)) {
                                    ArrayList<Element> xTable = new ArrayList<Element>();
                                    ArrayList<Element> yTable = new ArrayList<Element>();
                                    xTable.add(M.M[0][i]);
                                    yTable.add(M.M[1][i]);
                                    xTable.add(M.M[0][j]);
                                    yTable.add(M.M[1][j]);
                                    //Номера смежных вершин
                                    xTable.add(new NumberZ(i + 1));
                                    yTable.add(new NumberZ(j + 1));
                                    Element[] arr1 = new Element[xTable.size()];
                                    xTable.toArray(arr1);
                                    Element[] arr2 = new Element[yTable.size()];
                                    yTable.toArray(arr2);
                                    Element[][] arrNewTable = new Element[2][];
                                    arrNewTable[0] = arr1;
                                    arrNewTable[1] = arr2;
                                    //Добавление ребра в набор графиков
                                    arrayTables.add(new Table(new MatrixD(arrNewTable)));
                                }
                            }
                        }
                    }
                    Element[] arr = new Element[arrayTables.size()];
                    arrayTables.toArray(arr);
                    try {
                    //    Plots b = new Plots(page, parcaPort, arr);
                    } catch (Exception ex) {
                        throw new MathparException("Exception in plotGraph", ex);
                    }
                } else {
                    //случай когда пришло число
                    //Матрица инцидентности
                    int n = f_input.X[0].intValue();
                    Element[][] arrs = new Element[n][n];
                    for (int q = 0; q < n; q++) {
                        for (int s = 0; s < n; s++) {
                            if (q == s) {
                                arrs[q][s] = page.ring.numberZERO;
                            } else {
                                arrs[q][s] = page.ring.numberONE;
                            }
                        }
                    }
                    MatrixD I = new MatrixD(arrs);
                    //Матрица координат
                    MatrixD M = Element.circl(n);
                    //Набор графиков
                    ArrayList<Element> arrayTables = new ArrayList<Element>();
                    if (I.M.length == M.M[0].length) {
                        for (int i = 0; i < I.M.length; i++) {
                            for (int j = i; j < I.M.length; j++) {
                                Element el = I.M[i][j];
                                if (!el.isZero(Ring.ringR64xyzt)) {
                                    ArrayList<Element> xTable = new ArrayList<Element>();
                                    ArrayList<Element> yTable = new ArrayList<Element>();
                                    xTable.add(M.M[0][i]);
                                    yTable.add(M.M[1][i]);
                                    xTable.add(M.M[0][j]);
                                    yTable.add(M.M[1][j]);
                                    //Номера смежных вершин
                                    xTable.add(new NumberZ(i + 1));
                                    yTable.add(new NumberZ(j + 1));
                                    Element[] arr1 = new Element[xTable.size()];
                                    xTable.toArray(arr1);
                                    Element[] arr2 = new Element[yTable.size()];
                                    yTable.toArray(arr2);
                                    Element[][] arrNewTable = new Element[2][];
                                    arrNewTable[0] = arr1;
                                    arrNewTable[1] = arr2;
                                    //Добавление ребра в набор графиков
                                    arrayTables.add(new Table(new MatrixD(arrNewTable)));
                                }
                            }
                        }
                    }
                    Element[] arr = new Element[arrayTables.size()];
                    arrayTables.toArray(arr);
                    try {
                     //   Plots b = new Plots(page, parcaPort, arr);
                    } catch (Exception ex) {
                        throw new MathparException("Exception in plotGraph", ex);
                    }
                }
            }
        }
    }

    /**
     * Метод локальной отрисовки графа
     *
     * @param I - Матрица инцидентности
     * @param M - Матрица координат
     */
    public static void drawGraph(MatrixD I, MatrixD M, String path) {
        Ring ring = new Ring("R64[t]");
        Page page = new Page(ring, true);
        //Набор графиков
        ArrayList<Element> arrayTables = new ArrayList<Element>();
        if (I.M.length == M.M[0].length) {
            for (int i = 0; i < I.M.length; i++) {
                for (int j = i; j < I.M.length; j++) {
                    Element el = I.M[i][j];
                    if (!el.isZero(ring)) {
                        ArrayList<Element> xTable = new ArrayList<Element>();
                        ArrayList<Element> yTable = new ArrayList<Element>();
                        xTable.add(M.M[0][i]);
                        yTable.add(M.M[1][i]);
                        xTable.add(M.M[0][j]);
                        yTable.add(M.M[1][j]);
                        //Номера смежных вершин
                        xTable.add(new NumberZ(i + 1));
                        yTable.add(new NumberZ(j + 1));
                        Element[] arr1 = new Element[xTable.size()];
                        xTable.toArray(arr1);
                        Element[] arr2 = new Element[yTable.size()];
                        yTable.toArray(arr2);
                        Element[][] arrNewTable = new Element[2][];
                        arrNewTable[0] = arr1;
                        arrNewTable[1] = arr2;
                        //Добавление ребра в набор графиков
                        arrayTables.add(new Table(new MatrixD(arrNewTable)));
                    }
                }
            }
        }
        Element[] arr = new Element[arrayTables.size()];
        arrayTables.toArray(arr);
        try {
         //   Plots b = new Plots(page, false, arr, path);
        } catch (Exception ex) {
            throw new MathparException("Exception in plotGraph", ex);
        }
    }

    /**
     * Построение график для функций заданных в виде таблицы
     *
     * @param parcaPort
     * @param sect
     * @param x_str
     * @param y_str
     * @param title_str
     */
    public void tablePlot(Page page, Element[] arr, boolean parcaPort,
            double x0, double x1,
            double y0, double y1, double[][] xR, double[][] yR, Vector tables, boolean lineDrow, boolean isShowPlots) {
        F f_input1;
        F f_input;
        CanonicForms Cfs = new CanonicForms(Ring.ringR64xyzt, true);
        f_input1 = new F(this.name, X);
        f_input = (F) Cfs.UnconvertToElement(Cfs.substituteValueToFnameAndCorrectPolynoms(f_input1, page.expr));
        //массив параметров отображения
        Element[] paramRegion = new Element[] {};
        //стиль отображения линий и стрелок
        String style = "";
        if (f_input1.name == F.TABLEPLOT) {
            if (f_input.X.length >= 1 && f_input.X.length <= 2) {
                Table table = null;
//                if (f_input.X[0] instanceof MatrixD) {
//                    table = new Table(((MatrixD) f_input.X[0]));
//                }
//                if (f_input.X[0] instanceof MatrixS) {
//                    table = new Table((MatrixS) f_input.X[0]);
//                }
//                if (f_input.X[0] instanceof MatrixD) {
//                    table = new Table((MatrixD) f_input.X[0]);
//                }
//                if (f_input.X[0] instanceof Table) {
//                    table = new Table((Table) f_input.X[0]);
//                }
//                if (f_input.X[0] instanceof F) {
//                    table = new Table(((MatrixD) ((F) f_input.X[0]).X[0]));
//                }
                if (f_input.X[0] instanceof Table) {
                    table = new Table((Table) f_input.X[0]);
                } else {
                    MatrixD D = new MatrixD();
                    if (f_input.X[0] instanceof MatrixD) {
                        D = (MatrixD) f_input.X[0];
                    } else if (f_input.X[0] instanceof VectorS) {
                        D = new MatrixD((VectorS) f_input.X[0]);
                    } else if (f_input.X[0] instanceof MatrixS) {
                        D = new MatrixD((MatrixS) f_input.X[0]);
                    } else if (f_input.X[0] instanceof F) {
                        D = (MatrixD) (((F) f_input.X[0]).X[0]);
                    }
                    table = new Table(D);
                }
                tables.add(table);
                if (f_input.X.length == 1) {
                    plotException(page);
                    if (isShowPlots == true) {
                    //    Plots b = new Plots(page, true, table, paramRegion, 0, lineDrow, arr, style);
                    }
                }
                if (f_input.X.length == 2) {
                    if (f_input.X[1] != null) {
                        if (f_input.X[1] instanceof F) {
                            style = ((Fname) ((F) f_input.X[1]).X[0]).name;
                        }
                    }
                    plotException(page);
                    if (isShowPlots == true) {
                  //      Plots b = new Plots(page, true, table, paramRegion, 0, lineDrow, arr, style);
                    }
                }
            }
        }
    }

//    /**
//     * Построение график для функций заданных в виде таблицы
//     *
//     * @param parcaPort
//     * @param sect
//     * @param x_str
//     * @param y_str
//     * @param title_str
//     */
//    public void tablePlot(Page page, Element[] arr, boolean parcaPort,
//            double x0, double x1,
//            double y0, double y1, double[][] xR, double[][] yR, Vector tables, boolean lineDrow, boolean isShowPlots) {
//        F f_input1;
//        F f_input;
//        CanonicForms Cfs = new CanonicForms(Ring.ringR64xyzt, true);
//        f_input1 = new F(this.name, X);
//        f_input = (F) Cfs.UnconvertToElement(Cfs.substituteValueToFnameAndCorrectPolynoms(f_input1, page.expr));
//        //массив параметров отображения
//        Element[] paramRegion = new Element[] {};
//        //стиль отображения линий и стрелок
//        String style = "";
//        if (f_input1.name == F.TABLEPLOT) {
//            if (f_input.X.length >= 1 && f_input.X.length <= 2) {
//              Table table = null;
//              if (f_input.X[0] instanceof Table)  
//                table = new Table((Table) f_input.X[0]);
//              else{
//                MatrixD D=new MatrixD();
//                if (f_input.X[0] instanceof MatrixD) D=(MatrixD) f_input.X[0];  else
//                if (f_input.X[0] instanceof VectorS)  D= new MatrixD((VectorS)f_input.X[0]);  else
//                if (f_input.X[0] instanceof MatrixS) D =new MatrixD((MatrixS)f_input.X[0]);  else
//                if (f_input.X[0] instanceof F) D = (MatrixD) (((F)f_input.X[0]).X[0]);
//                table = new Table(D);
//              } 
//                if (f_input.X.length == 1) {
//                    plotException(page);
//                    if (isShowPlots == true) {
//                        Plots b = new Plots(page, true, table, paramRegion, 0, lineDrow, arr, style);
//                    }
//                } else
//                if (f_input.X.length == 2) {
//                    if (f_input.X[1] != null) {
//                        if (f_input.X[1] instanceof F) {
//                            style = ((Fname) ((F) f_input.X[1]).X[0]).name;
//                        }
//                    }
//                    plotException(page);
//                    if (isShowPlots == true) {
//                        Plots b = new Plots(page, true, table, paramRegion, 0, lineDrow, arr, style);
//                    }
//                }
//            }
//        }
//    }
    /**
     * Функция отрисовки графика по заданным точкам
     *
     * @param page
     * @param arr
     * @param parcaPort
     * @param x0
     * @param x1
     * @param y0
     * @param y1
     * @param xR
     * @param yR
     * @param tables
     * @param text1
     * @param k11
     * @param k21
     * @param lineDrow
     */
    public void pointsPlot(Page page, Element[] arr, boolean parcaPort,
            double x0, double x1,
            double y0, double y1, double[][] xR, double[][] yR, Vector tables,
            Vector text1, Vector k11, Vector k21, boolean lineDrow, boolean isShowPlots) {
        CanonicForms Cfs = new CanonicForms(Ring.ringR64xyzt, true);
        F f_input1 = new F(this.name, X);
        F f_input = (F) Cfs.UnconvertToElement(Cfs.substituteValueToFnameAndCorrectPolynoms(f_input1, page.expr));
        Table table = new Table();
        if (f_input.X[0] instanceof Table) {
            table = new Table((Table) f_input.X[0]);
            tables.add(table);
        } else {
            MatrixD D = new MatrixD();
            if (f_input.X[0] instanceof MatrixD) {
                D = (MatrixD) f_input.X[0];
            } else if (f_input.X[0] instanceof VectorS) {
                D = new MatrixD((VectorS) f_input.X[0]);
            } else if (f_input.X[0] instanceof MatrixS) {
                D = new MatrixD((MatrixS) f_input.X[0]);
            } else if (f_input.X[0] instanceof F) {
                D = (MatrixD) (((F) f_input.X[0]).X[0]);
            }
            table = new Table(D);
            tables.add(table);
        }
//        if (f_input.X[0] instanceof MatrixD) {
//            table = new Table(((MatrixD) f_input.X[0]));
//            tables.add(table);
//        }
//        if (f_input.X[0] instanceof MatrixS) {
//            table = new Table((MatrixS) f_input.X[0]);
//            tables.add(table);
//        }
//        if (f_input.X[0] instanceof MatrixD) {
//            table = new Table((MatrixD) f_input.X[0]);
//            tables.add(table);
//        }
//        if (f_input.X[0] instanceof Table) {
//            table = new Table((Table) f_input.X[0]);
//            tables.add(table);
//        }
//        if (f_input.X[0] instanceof F) {
//            table = new Table(((MatrixD) ((F) f_input.X[0]).X[0]));
//            tables.add(table);
//        }
        //массив параметров отображения
        Element[] paramRegion = new Element[] {};
        Element[] text = null;
        if ((f_input.X.length >= 2) && (f_input.X[1] != null)) {
            text = ((VectorS) f_input.X[1]).V;
        }
        if (text != null) {
            for (int i1 = 0; i1 < text.length; i1++) {
                text1.add(text[i1]);
            }
        }

        Element[] k1 = null;
        if ((f_input.X.length >= 3) && (f_input.X[2] != null)) {
            k1 = ((VectorS) f_input.X[2]).V;
        }
        if (k1 != null) {
            for (int i1 = 0; i1 < k1.length; i1++) {
                k11.add(k1[i1]);
            }
        }

        Element[] k2 = null;
        if ((f_input.X.length >= 4) && (f_input.X[3] != null)) {
            k2 = ((VectorS) f_input.X[3]).V;
        }
        if (k2 != null) {
            for (int i1 = 0; i1 < k2.length; i1++) {
                k21.add(k2[i1]);
            }
        }
        if ((f_input.X.length >= 6) && (f_input.X[5] != null)) {
            paramRegion = ((VectorS) f_input.X[5]).V;
        }
        plotException(page);
        if (isShowPlots == true) {
       //     Plots b = new Plots(page, true, table, paramRegion, 0, lineDrow, arr, text, k1, k2);
        }
    }

    /**
     * Метод для отрисовки графиков функций
     *
     * @param page
     * @param parcaPort - true : отображает график в web, fasle : сохраняет
     * построенный график в пользовательскую директорию
     * @param blackColor - цвет графика
     * @param equalScale - равный масштаб по осям
     * @param funcNames - массив имен графиков для showPlots
     */
    public void showGraphics(Page page, boolean parcaPort, boolean blackColor, boolean equalScale, String[] funcNames) {
        //Создание и заполнение массива параметров
        Element[] param = new Element[page.ring.varNames.length + 5];
        for (int i = 0; i < param.length - 5; i++) {
            param[i] = new NumberR64(1);
        }
        //черный цвет графиков
        param[param.length - 5] = blackColor ? new NumberR64(1) : new NumberR64(0);
        //равный масштаб по осям
        param[param.length - 5 + 1] = equalScale ? new NumberR64(1) : new NumberR64(0);
        //размер шрифта
        param[param.length - 5 + 2] = new NumberR64(16);
        //толщина линий графика
        param[param.length - 5 + 3] = new NumberR64(3);
        //толщина осей и рисок
        param[param.length - 5 + 4] = new NumberR64(3);
        switch (name) {
            case TEXTPLOT: {
                textPlot(page, param, parcaPort, new Vector<Element[]>(), true);
                break;
            }
            case SHOWPLOTS: {
                showPlots(page, param, parcaPort, funcNames);
                break;
            }
            case PLOT: {
                plot(page, param, parcaPort, new Vector<Element>(), true);
                break;
            }
            case PARAMPLOT: {
                paramPlot(page, param, parcaPort, 0, 0, 0, 0, 0, 0, new Vector<Element>(), new Vector<Element>(), true);
                break;
            }
            case POINTSPLOT: {
                pointsPlot(page, param, parcaPort, 0, 0, 0, 0, new double[1][], new double[1][], new Vector<Element>(), new Vector<Element>(), new Vector<Element>(), new Vector<Element>(), false, true);
                break;
            }
            case TABLEPLOT: {
                tablePlot(page, param, parcaPort, 0, 0, 0, 0, new double[1][], new double[1][], new Vector<Element>(), true, true);
                break;
            }
        }
    }

    public void showGraf3D(Page page, boolean parcaPort, String x_str, String y_str, String title_str) {
        boolean paramPlot3D = false;
        if (name == PARAMPLOT3D) {
            paramPlot3D = true;
        }
        //if (name == PLOT3D) {
        F f_input;
        Element[] el = new Element[3];
        el[0] = X[0];
        el[1] = X[1];
        el[2] = Polynom.polynom_one(NumberR64.ONE);
        f_input = new F(this.name, el);
        if (X.length > 2) {
            x_str = ((Fname) X[2]).name;
        } else {
            x_str = "x";
        }
        if (X.length > 3) {
            y_str = ((Fname) X[3]).name;
        } else {
            y_str = "y";
        }
        if (X.length > 4) {
            title_str = ((Fname) X[4]).name;
        } else {
            title_str = "";
        }
        int k = 0;
        if (f_input.X[2] instanceof Polynom) {
            if (((Polynom) f_input.X[2]).coeffs.length != 0) {
                k = ((Polynom) f_input.X[2]).constantTerm().intValue();
            } else {
                k = 0;
            }
        }

        if (f_input.X[0] instanceof F) {
            if (((F) f_input.X[0]).name == F.VECTORS) {
                for (int i = 0; i < ((F) f_input.X[0]).X.length; i++) {
                    if (((F) f_input.X[0]).X[i] instanceof F) {
                        if (((F) ((F) f_input.X[0]).X[i]).name == 0) {
                            if (((F) ((F) f_input.X[0]).X[i]).X[0] instanceof Fname) {
                                ((F) f_input.X[0]).X[i] = ((Fname) ((F) ((F) f_input.X[0]).X[i]).X[0]).X[0];
                            }
                        }
                    }
                    if (((F) f_input.X[0]).X[i] instanceof Fname) {
                        ((F) f_input.X[0]).X[i] = ((Fname) ((F) f_input.X[0]).X[i]).X[0];
                    }
                }
            }
        }
        if (f_input.X[0] instanceof Fname) {
            f_input.X[0] = new F(VECTORS, ((Fname) f_input.X[0]).X[0]);
        }
        if (f_input.X[0] instanceof F) {
            if (((F) f_input.X[0]).name == 0) {
                if (((F) f_input.X[0]).X[0] instanceof Fname) {
                    f_input.X[0] = ((Fname) ((F) f_input.X[0]).X[0]).X[0];
                }
            }
        }
        if (f_input.X[0] instanceof Polynom) {
            f_input.X[0] = new F(f_input.X[0]);
        }
        if (f_input.X[0] instanceof F) {
            if (((F) f_input.X[0]).name != F.VECTORS) {
                f_input.X[0] = new F(VECTORS, f_input.X[0]);
            }
        }
        double x0 = 0;
        double x1 = 0;
        double y0 = 0;
        double y1 = 0;
//        FvalOf fvof = new FvalOf(page.ring);
//        if (((F) f_input.X[1]).X[0] instanceof F) {
//            x0 = fvof.valOf(((F) f_input.X[1]).X[0], new Element[0]).doubleValue();
//        } else {
//            if (((Polynom) ((F) f_input.X[1]).X[0]).coeffs.length != 0) {
//                x0 = ((Polynom) ((F) f_input.X[1]).X[0]).constantTerm().doubleValue();
//            }
//        }
//        if (((F) f_input.X[1]).X[1] instanceof F) {
//            x1 = fvof.valOf(((F) f_input.X[1]).X[1], new Element[0]).doubleValue();
//        } else {
//            if (((Polynom) ((F) f_input.X[1]).X[1]).coeffs.length != 0) {
//                x1 = ((Polynom) ((F) f_input.X[1]).X[1]).constantTerm().doubleValue();
//            }
//        }
//        if (((F) f_input.X[1]).X[2] instanceof F) {
//            y0 = fvof.valOf(((F) f_input.X[1]).X[2], new Element[0]).doubleValue();
//        } else {
//            if (((Polynom) ((F) f_input.X[1]).X[2]).coeffs.length != 0) {
//                y0 = ((Polynom) ((F) f_input.X[1]).X[2]).constantTerm().doubleValue();
//            }
//        }
//        if (((F) f_input.X[1]).X[3] instanceof F) {
//            y1 = fvof.valOf(((F) f_input.X[1]).X[3], new Element[0]).doubleValue();
//        } else {
//            if (((Polynom) ((F) f_input.X[1]).X[3]).coeffs.length != 0) {
//                y1 = ((Polynom) ((F) f_input.X[1]).X[3]).constantTerm().doubleValue();
//            }
//        }
        if (k == 1) {
            if (paramPlot3D) {
                String[] x_Arr = new String[((F) ((F) f_input.X[0]).X[0]).X.length];
                String[] y_Arr = new String[((F) ((F) f_input.X[0]).X[1]).X.length];
                String[] z_Arr = new String[((F) ((F) f_input.X[0]).X[2]).X.length];
                for (int i = 0; i < x_Arr.length; i++) {
                    x_Arr[i] = ((F) ((F) f_input.X[0]).X[0]).X[i].toString(page.ring());
                }
                for (int i = 0; i < y_Arr.length; i++) {
                    y_Arr[i] = ((F) ((F) f_input.X[0]).X[1]).X[i].toString(page.ring());
                }
                for (int i = 0; i < z_Arr.length; i++) {
                    z_Arr[i] = ((F) ((F) f_input.X[0]).X[2]).X[i].toString(page.ring());
                }
//                Plot3D p = new Plot3D(null, x_Arr, y_Arr, z_Arr, x0, x1, y0, y1,
//                        30, 30, Color.yellow, false, true, true, page, true);
            } else {
                String[] sf = new String[((F) f_input.X[0]).X.length];
                for (int i = 0; i < ((F) f_input.X[0]).X.length; i++) {
                    if (((F) f_input.X[0]).X[i] != null) {
                        sf[i] = ((F) f_input.X[0]).X[i].toString(page.ring());
                    }
                }
//                if ((sf.length == 1) && (sf[0] == null)) {
//                    Plot3D p = new Plot3D(null, null, null, null, x0, x1, y0, y1,
//                            30, 30, Color.yellow, false, true, true, page, true);
//                } else {
//                    Plot3D p = new Plot3D(sf, null, null, null, x0, x1, y0, y1,
//                            30, 30, Color.yellow, false, true, true, page, true);
//                }
            }
        }
    }

    /**
     * Обход дерева F с подсчетом количества листьев и узлов
     *
     * @return int[]{ Число листьев, Число узлов, Максимальная глубина}
     * возвращает массив из трёх целых чисел: первое - "Число листьев" второе -
     * "Число узлов" третье - "максимальная Глубина".
     */
    public int[] leavesNodesDepth() {
        int[] result = new int[] {0, 1, 1};
        int depth = 1;
        for (int i = 0; i < X.length; i++) {
            if (X[i] instanceof F) {
                int[] temp = ((F) X[i]).leavesNodesDepth();
                result[0] += temp[0];
                result[1] += temp[1];
                if (temp[2] > depth) {
                    depth = temp[2];
                }
            } else {
                result[0] += 1;
                result[1] += 1;
            }
        }
        result[2] += depth;
        return result;
    }

    /**
     * Обход дерева F с подсчетом количества листьев и узлов
     *
     * @return int[]{ Число листьев, Число узлов} возвращает массив из трёх
     * целых чисел: первое - "Число листьев", второе - "Число узлов", третье -
     * "максимальная Глубина".
     */
    public int[] leavesNodesDepthWithFname(Element el) {
        int[] result = new int[] {0, 1, 1};
        int depth = 1;
        switch (el.numbElementType()) {
            case Ring.F:
                for (int i = 0; i < ((F) el).X.length; i++) {
                    if (((F) el).X[i] instanceof F | ((F) el).X[i] instanceof Fname) {
                        int[] temp = leavesNodesDepthWithFname(((F) el).X[i]);
                        result[0] += temp[0];
                        result[1] += temp[1];
                        if (temp[2] > depth) {
                            depth = temp[2];
                        }
                    } else {
                        result[0] += 1;
                        result[1] += 1;
                    }
                }
                break;
            case Ring.Fname:
                if (((Fname) el).X[0] instanceof F | ((Fname) el).X[0] instanceof Fname) {
                    int[] temp = leavesNodesDepthWithFname(((Fname) el).X[0]);
                    result[0] += temp[0];
                    result[1] += temp[1];
                    if (temp[2] > depth) {
                        depth = temp[2];
                    }
                } else {
                    result[0] += 1;
                    result[1] += 1;
                }
                break;
            default:
                result[0] += 1;
                result[1] += 1;
        }
        result[2] += depth;
        return result;
    }

    public NumberZ64 booleanFunction(Ring ring) {
        NumberZ64[] temp = new NumberZ64[X.length];
        if ((name >= FIRST_INEQUAL_BOOL) && (name <= LAST_INEQUAL_BOOL)) {
            Element El0 = X[0];
            Element El1 = X[1];
            int type0 = X[0].numbElementType();
            int type1 = X[1].numbElementType();
            if (type0 > type1) {
                El1 = El1.toNewRing(type0, ring);
            } else if (type0 < type1) {
                El0 = El0.toNewRing(type1, ring);
            }
            return new NumberZ64((El0.compareTo(El1, name - FIRST_INEQUAL_BOOL - 3, ring)));
        }
        for (int i = 0; i < X.length; i++) {
            if (X[i] instanceof F) {
                F f = (F) X[i];
                temp[i] = f.booleanFunction(ring);
            } else {
                temp[i] = (X[i] instanceof NumberZ64) ? (NumberZ64) X[i] : (NumberZ64) X[i].toNumber(Ring.Z64, ring);
            }
        }
        switch (this.name) {
            case B_OR:
                return temp[0].B_OR(temp[1]);
            case B_AND:
                return temp[0].B_AND(temp[1]);
            case B_NOT:
                return temp[0].B_NOT();
        }
        return TRUE;
    }

    /**
     * Удаление совпадающих объектов и фрагментов из функции F. После выполнения
     * этого метода все лишние совпадающие фрагменты будут удалены, а
     * совпадающие фрагменты всегда указывают на один и тот же объект.
     */
    public void cleanOfRepeatingWithNewVectors(Ring ring) {
        Vector<F>[] vectF = new Vector[FUNC_ARR_LEN];
        Vector<Element>[] vectEl = new Vector[Ring.NumberOfElementTypes];
        for (int i = 0; i < Ring.NumberOfElementTypes; i++) {
            vectEl[i] = new Vector<Element>();
        }
        ring.CForm.vectEl = vectEl;
        ring.CForm.vectF = vectF;
        this.X = ((F) cleanOfRepeating(this, ring)).X;
    }

    public static void cleanOfRepeatingWithNewVectors(F[] funcs, Ring ring) {
        Vector<F>[] vectF = new Vector[FUNC_ARR_LEN];
        Vector<Element>[] vectEl = new Vector[Ring.NumberOfElementTypes];
        for (int i = 0; i < Ring.NumberOfElementTypes; i++) {
            vectEl[i] = new Vector<Element>();
        }
        for (int i = 0; i < funcs.length; i++) {
            funcs[i] = ((F) cleanOfRepeating(funcs[i], ring));
        }
    }

    public static void cleanOfRepeating(F[] funcs, Ring ring) {
        for (int i = 0; i < funcs.length; i++) {
            funcs[i] = ((F) cleanOfRepeating(funcs[i], ring));
        }
    }

    public int compareTotal(F f, Ring ring) {
        if (equals(f)) {
            return 0;
        } else {
            return compareTo(f, ring);
        }
    }

    /**
     * Сравнение двух функций Сначала нужно выполнить equals!
     *
     * @param f
     *
     * @return
     */
    public int compareTo(F f, Ring ring) {     // cначала нужно выполнить equals!
        int cT = this.name - f.name;
        if (cT != 0) {
            return (cT > 0) ? 1 : -1;
        }
        int len = this.X.length;
        cT = len - f.X.length;
        if (cT != 0) {
            return (cT > 0) ? 1 : -1;
        }
        for (int i = 0; i < len; i++) {
            int RnET = (X[i]).numbElementType();
            cT = RnET - (f.X[i]).numbElementType();
            if (cT != 0) {
                return (cT > 0) ? 1 : -1;
            } //больше тот у кого больше аргументов
            if (RnET != Ring.F) {
                if (X[i].compareTo(f.X[i], ring) != 0) {
                    cT = X[i].compareTo(f.X[i], ring);
                    if (cT != 0) {
                        return (cT > 0) ? 1 : -1;
                    }
                }
            } else { // Теперь оба Element X[i] имеют тип F
                cT = ((F) X[i]).compareTo((F) f.X[i], ring);
                if (cT != 0) {
                    return (cT > 0) ? 1 : -1;
                }
            }
        }
        return 0;
    }

    public int compareTo(Element o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int compareTo(Element f, Ring ring) {
        int T = this.numbElementType() - f.numbElementType();
        if (T != 0) {
            return (T < 0) ? -1 : 1;
        }
        return (equals((F) f, ring)) ? 0 : compareTo((F) f, ring);
    }

    public boolean equalsWithNewVectors(F f, Ring ring) {
        F[] N = new F[] {this, f};
        cleanOfRepeatingWithNewVectors(N, ring);
        return (N[0] == N[1]);
    }

    public boolean equals(F f, Ring ring) {
        F[] N = new F[] {this, f};
        cleanOfRepeating(N, ring);
        return (N[0] == N[1]);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof F) {
            F f = (F) obj;
            return name == f.name && Arrays.equals(f.X, X);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.name;
        hash = 59 * hash + Arrays.deepHashCode(this.X);
        return hash;
    }

    @Override
    public boolean equals(Element x, Ring r) {
        Element resEq = new F(SUBTRACT, new Element[] {this, x});
        resEq = resEq.expand(r);
        return resEq.isZero(r);
    }

    /**
     * Очистка компоненты функции от повторяющихся фрагментов
     *
     * @param node - компонента, подлежащая очистке
     * @param vectF - массив векторов из компонент пройденной части дерева
     * функции, номер вектора в массиве соответствует имени компоненты функции
     * @param vectEl - массив векторов аргументов пройденной части дерева
     * функции, номер вектора в массиве соответствует классу аргумента:
     * polynom=0, vector=1, matrixS=2, matrixD=3
     *
     * @return - функция без повторяющихся фрагментов
     */
    public static Element cleanOfRepeating(Element node, Ring ring) {
        Vector<F>[] vectF = ring.CForm.vectF;
        Vector<Element>[] vectEl = ring.CForm.vectEl;
        if (node instanceof F) {
            F f_node = new F();
            f_node.name = ((F) node).name;
            f_node.X = ((F) node).X;
            Element[] X_node = f_node.X;
            int len = X_node.length;
            int f_name = f_node.name;
            for (int i = 0; i < len; i++) {
                if (X_node[i] != null) {
                    ((F) node).X[i] = cleanOfRepeating(X_node[i], ring);
                }
            }
            if (vectF[f_node.name] == null) {
                vectF[f_node.name] = new Vector<F>();
            }
            for (F f : vectF[f_node.name]) {
                if (len == f.X.length) {
                    Element[] XX = f.X;
                    Boolean equalsF = true;
                    if ((f_name >= F.FIRST_INFIX_COMMUT_ARG) && (f_name <= F.LAST_INFIX_COMMUT_ARG)) {
                        int[] eq = new int[len];
                        int pos = len;
                        for (int j = 0; j < pos; j++) {
                            eq[j] = j;
                        }
                        search:
                        for (int j = 0; j < len; j++) {
                            for (int j1 = 0; j1 < pos; j1++) {
                                if ((X_node[j] == XX[eq[j1]])) {
                                    eq[j1] = eq[--pos];
                                    continue search;
                                }
                            }
                            equalsF = false;
                            break search;
                        }
                    } else {
                        for (int j = 0; j < len; j++) {
                            if (X_node[j] != XX[j]) {
                                equalsF = false;
                                break;
                            }
                        }
                    }
                    if (equalsF) {
                        return f;
                    }
                }
            }
            vectF[f_node.name].add(f_node);
            return f_node;
        }
        if (node == null) {
            return node;
        }
        node = doEasy(node, ring);
        int myType = node.numbElementType();
        if (vectEl != null) {
            for (Element el : vectEl[myType]) {
                if (node.compareTo(el, ring) == 0) {
                    return el;
                }
            }
        }
        vectEl[myType].add(node);
        return node;
    }

    /**
     * We do complex and fraction element more easy for cleanOfRepeatings
     *
     * @param Element node is a number
     *
     * @return node more easy
     */
    public static Element doEasy(Element node, Ring ring) {
        Element res = node;
        boolean True = true;
        Complex cc = new Complex();
        Element[] ARR;
        boolean PolFl = false;
        if (node instanceof Polynom) {
            ARR = ((Polynom) node).coeffs;
            PolFl = true;
        } else {
            ARR = new Element[] {node};
        }
        for (int i = 0; i < ARR.length; i++) {
            res = ARR[i];
            while (True) {
                True = false;
                if ((res instanceof Polynom) && (res.isItNumber())) {
                    res = ((Polynom) res).coeffs[0];
                    True = true;
                }
                if ((res instanceof Fraction) && (((Fraction) res).denom.isOne(ring))) {
                    res = ((Fraction) res).num;
                    True = true;
                }
                if (res instanceof Complex) {
                    cc.im = ((Complex) res).im;
                    cc.re = ((Complex) res).re;
                    if (cc.im.isZero(ring)) {
                        res = cc.re;
                        True = true;
                    } else {
                        if ((cc.im instanceof Fraction) && (((Fraction) cc.im).denom.isOne(ring))) {
                            cc.im = ((Fraction) cc.im).num;
                            True = true;
                        }
                        if ((cc.re instanceof Fraction) && (((Fraction) cc.re).denom.isOne(ring))) {
                            cc.re = ((Fraction) cc.re).num;
                            True = true;
                        }
                        if ((cc.re instanceof Polynom) && (cc.re.isItNumber())) {
                            cc.re = ((Polynom) cc.re).coeffs[0];
                            True = true;
                        }
                        if ((cc.im instanceof Polynom) && (cc.im.isItNumber())) {
                            cc.im = ((Polynom) cc.im).coeffs[0];
                            True = true;
                        }
                        res = cc;
                    }
                }
            }
            ARR[i] = res;
        }
        return (PolFl) ? new Polynom(((Polynom) node).powers, ARR) : ARR[0];
    }

    public Element valueOf(Ring ring) {
        return this; // new FvalOf(ring).valOf(this, new Element[0]);
    }

    public Element valueOf(Element[] point, Ring ring) {
        return this ;//new FvalOf(ring).valOf(this, point);
    }

    @Override
    public Element value(Element[] point, Ring ring) {
        return valueOf(point, ring);
    }

    /**
     * Процедура нахождения производной функции от первой переменной в кольце
     *
     * @return производная функции
     */
    @Override
    public Element D(Ring r) {
        return D(0, r);
    }

    /**
     * Процедура нахождения производной функции
     *
     * @param num - номер переменной в кольце
     *
     * @return производная функции
     */
    @Override
    public Element D(int num, Ring ring) {
        switch (name) {
            case D:
                Element resD = X[0].D(num, ring);
                if (resD.isZero(ring)) {
                    return resD;
                }
                Element[] newVarD = new Element[((VectorS) X[1]).V.length + 1];
                System.arraycopy(((VectorS) X[1]).V, 0, newVarD, 0, ((VectorS) X[1]).V.length);
                newVarD[((VectorS) X[1]).V.length] = ring.varPolynom[num];
                return new F(F.D, new Element[] {X[0], new VectorS(newVarD)});
            case ID:
                return new F(ID, X[0].D(num, ring));
            case ADD:
                Vector<Element> NewArg = new Vector<Element>();
                for (Element El : X) {
                    Element Dar = El.D(num, ring);
                    if (!Dar.isZero(ring)) {
                        NewArg.add(Dar);
                    }
                }
                return (NewArg.isEmpty()) ? ring.numberZERO : ConvertVecToF(NewArg, name);
            case MULTIPLY:
                Vector<Element> vec = new Vector<Element>();
                boolean flag;
                a:
                for (int i = 0; i < X.length; i++) {
                    flag = true;
                    Element[] Start = new Element[X.length];
                    for (int j = 0; j < X.length; j++) {
                        if (i == j) {
                            Element tempD = X[j].D(num, ring);
                            Start[j] = tempD;
                            if (tempD.isZero(ring)) {
                                continue a;
                            }
                        } else {
                            Start[j] = X[j];
                        }
                    }
                    vec.add(new F(name, Start));
                }
                return (vec.isEmpty()) ? ring.numberZERO : ConvertVecToF(vec, F.ADD);
            case DIVIDE:
                Element VV = X[1].multiply(X[1], ring);
                Element DvMU = (X[0].D(num, ring)).multiply(X[1], ring);
                Element DuMV = (X[1].D(num, ring)).multiply(X[0], ring);
                F TSub = new F(SUBTRACT, new Element[] {DvMU, DuMV});
                return (new F(DIVIDE, new Element[] {TSub, VV}));//.expand(ring);
            case SUBTRACT:
                Element oneAr = X[0].D(num, ring);
                Element twoAr = X[1].D(num, ring);
                if (oneAr.isZero(ring)) {
                    return twoAr.negate(ring);
                }
                if (twoAr.isZero(ring)) {
                    return oneAr;
                }
                return new F(SUBTRACT, new Element[] {oneAr, twoAr});
            case SQRT:
                F SQm = new F(F.MULTIPLY, new Element[] {ring.numberONE.valOf(2, ring), new F(SQRT, X[0])});
                Element DSQ = X[0].D(num, ring);
                return (DSQ.isZero(ring)) ? ring.numberZERO : new F(DIVIDE, new Element[] {DSQ.negate(ring), SQm});
            case SIN:
                Element Dsin = X[0].D(num, ring);
                return (Dsin.isZero(ring)) ? ring.numberZERO : (new F(COS, X[0])).multiply(Dsin, ring); // new F(MULTIPLY, new Element[]{new F(COS, X[0]), Dsin});
            case COS:
                Element Dcos = X[0].D(num, ring);
                return (Dcos.isZero(ring)) ? ring.numberZERO : (new F(SIN, X[0])).multiply(Dcos.negate(ring), ring);           //new F(MULTIPLY, new Element[]{new F(SIN, X[0]), Dcos.negate()});
            case ABS:
                return X[0].D(num, ring);
            case LN:
                Element X0 = X[0];
                Element mult;
                if (X0 instanceof F) {
                    F fnn = (F) X0;
                    Element[] XX = fnn.X;
                    int name1 = fnn.name;
                    switch (name1) {
                        case (F.ABS):
                            return new F(F.LN, fnn.X[0]).D(num, ring);
                        case (F.DIVIDE):
                            F l1 = new F(F.LN, XX[0]);
                            F l2 = new F(F.LN, XX[1]);
                            return (new F(F.SUBTRACT, l1, l2)).D(num, ring);
                        case (F.MULTIPLY):
                            int kk = XX.length;
                            Element[] nX = new Element[kk];
                            for (int i = 0; i < kk; i++) {
                                nX[i] = new F(F.LN, XX[i]);
                            }
                            return (new F(F.ADD, nX)).D(num, ring);
                        case (F.POW):
                        case (F.intPOW):
                            X0 = fnn.X[1];
                            mult = fnn.X[0];
                            return new F(F.LN, X0).D(num, ring).multiply(mult, ring);
                        case (F.SQRT):
                            X0 = fnn.X[0];
                            mult = new Fraction(ring.numberONE, ring.posConst[2]);
                            return new F(F.LN, X0).D(num, ring).multiply(mult, ring);
                        case (F.CUBRT):
                            X0 = fnn.X[0];
                            mult = new Fraction(ring.numberONE, ring.posConst[3]);
                            return new F(F.LN, X0).D(num, ring).multiply(mult, ring);
                        case (F.ROOTOF):
                            X0 = fnn.X[0];
                            mult = new Fraction(ring.numberONE, fnn.X[1]);
                            return new F(F.LN, X0).D(num, ring).multiply(mult, ring);
                    }
                } else if (X0 instanceof Fraction) {
                    Fraction frr = (Fraction) X0;
                    F l1 = new F(F.LN, frr.num);
                    F l2 = new F(F.LN, frr.denom);
                    return (new F(F.SUBTRACT, l1, l2)).D(num, ring);
                }
                return new Fraction(X0.D(num, ring), X0).cancel(ring);
            case LOG:
                F NewLog = new F(F.DIVIDE, new Element[] {new F(LN, X[0]), new F(LN, X[1])});
                return NewLog.D(num, ring);
            case LG:
                F NewLg = new F(LG, ring.numberONE.e(ring));
                F nLg = new F(LN, X[0]);
                Element DnLg = nLg.D(num, ring);
                return (DnLg.isZero(ring)) ? ring.numberZERO : NewLg.multiply(DnLg, ring);
            case EXP:
                Element DExp = X[0].D(num, ring);
                return (DExp.isZero(ring)) ? ring.numberZERO : multiply(DExp, ring);
            case TG:
                F TGd = new F(DIVIDE, new Element[] {ring.numberONE, new F(POW, new Element[] {new F(COS, X[0]), ring.numberONE.valOf(2, ring)})});
                Element DTG = X[0].D(num, ring);
                return (DTG.isZero(ring)) ? ring.numberZERO : TGd.multiply(DTG, ring);
            case CTG:
                F CTd = new F(DIVIDE, new Element[] {ring.numberMINUS_ONE, new F(POW, new Element[] {new F(SIN, X[0]), ring.numberONE.valOf(2, ring)})});
                Element DCTG = X[0].D(num, ring);
                return (DCTG.isZero(ring)) ? ring.numberZERO : CTd.multiply(DCTG, ring);
            case ARCCOS:
                F ACm = new F(POW, new Element[] {X[0], ring.numberONE.valOf(2, ring)});
                F ACs = new F(SUBTRACT, new Element[] {ring.numberMINUS_ONE, ACm});
                F ACsq = new F(SQRT, ACs);
                F ACd = new F(DIVIDE, new Element[] {ring.numberMINUS_ONE, ACsq});
                Element DARCCOS = X[0].D(num, ring);
                return (DARCCOS.isZero(ring)) ? ring.numberZERO : ACd.multiply(DARCCOS, ring);
            case ARCSIN:
                F ASsq = new F(SQRT, new F(SUBTRACT, new Element[] {ring.numberONE, new F(POW, new Element[] {X[0], ring.numberONE.valOf(2, ring)})}));
                F ASd = new F(DIVIDE, new Element[] {ring.numberONE, ASsq});
                Element DARCSIN = X[0].D(num, ring);
                return (DARCSIN.isZero(ring)) ? ring.numberZERO : ASd.multiply(DARCSIN, ring);
            case ARCTG:
                F ATa = new F(ADD, new Element[] {ring.numberONE, new F(POW, new Element[] {X[0], ring.numberONE.valOf(2, ring)})});
                F ATd = new F(DIVIDE, new Element[] {ring.numberONE, ATa});
                Element DARCTG = X[0].D(num, ring);
                return (DARCTG.isZero(ring)) ? ring.numberZERO : ATd.multiply(DARCTG, ring);
            case ARCCTG:
                F ATCd = new F(DIVIDE, new Element[] {ring.numberMINUS_ONE, new F(ADD, new Element[] {ring.numberONE, new F(POW, new Element[] {X[0], ring.numberONE.valOf(2, ring)})})});
                Element DACCTG = X[0].D(num, ring);
                return (DACCTG.isZero(ring)) ? ring.numberZERO : ATCd.multiply(DACCTG, ring);
            case SH:
                Element DSH = X[0].D(num, ring);
                return (DSH.isZero(ring)) ? ring.numberZERO : (new F(CH, X[0])).multiply(DSH, ring);
            case CH:
                Element DCH = X[0].D(num, ring);
                return (DCH.isZero(ring)) ? ring.numberZERO : (new F(SH, X[0])).multiply(DCH, ring);
            case ARCTGH:
                F ATHd = new F(DIVIDE, new Element[] {ring.numberONE, new F(POW, new Element[] {new F(CH, X[0]), ring.numberONE.valOf(2, ring)})});
                Element DARCTGH = X[0].D(num, ring);
                return (DARCTGH.isZero(ring)) ? ring.numberZERO : ATHd.multiply(DARCTGH, ring);
            case ARCCTGH:
                F ATCHd = new F(DIVIDE, new Element[] {ring.numberMINUS_ONE, new F(POW, new Element[] {new F(SH, X[0]), ring.numberONE.valOf(2, ring)})});
                Element DARCCTGH = X[0].D(num, ring);
                return (DARCCTGH.isZero(ring)) ? ring.numberZERO : ATCHd.multiply(DARCCTGH, ring);
            case intPOW:
                Element newPOW;
                Element temp;
                if (X[1] instanceof F) {
                    temp = (((F) X[1]).X[0]).subtract((((F) X[1]).X[0]).one(ring), ring);
                    if (temp.isOne(ring)) {
                        newPOW = X[0];
                    } else {
                        newPOW = new F(intPOW, new Element[] {X[0], temp});
                    }
                } else {
                    temp = X[1].subtract(X[1].myOne(ring), ring);
                    if (temp.isOne(ring)) {
                        newPOW = X[0];
                    } else {
                        newPOW = new F(intPOW, new Element[] {X[0], temp});
                    }
                }
                Element DPOW = X[0].D(num, ring);
                return (DPOW.isZero(ring)) ? ring.numberZERO : new F(MULTIPLY, new Element[] {X[1], newPOW, DPOW});
            case POW:
                if (!(X[1] instanceof F)) {
                    int myType = X[1].numbElementType();
                    if (myType < Ring.NumberOfSimpleTypes) {
                        F newPow = new F(POW, new Element[] {X[0], X[1].subtract(X[1].one(ring), ring)});
                        Element DPow = X[0].D(num, ring);
                        return (DPow.isZero(ring)) ? ring.numberZERO : new F(MULTIPLY, new Element[] {X[1], newPow, DPow});
                    }
                    F Z = new F(MULTIPLY, new Element[] {X[1], new F(LN, X[0])});
                    F EpowZ = new F(EXP, Z);
                    Element DZ = Z.D(num, ring);
                    return (DZ.isZero(ring)) ? ring.numberZERO : new F(MULTIPLY, new Element[] {EpowZ, DZ});
                } else {
                    if (((F) X[1]).name == ID) {
                        int myType = (((F) X[1]).X[0]).numbElementType();
                        if (myType <= Ring.Polynom) {
                            F newPow = new F(POW, new Element[] {X[0], (((F) X[1]).X[0]).subtract((((F) X[1]).X[0]).one(ring), ring)});
                            Element DPow = X[0].D(num, ring);
                            return (DPow.isZero(ring)) ? ring.numberZERO : new F(MULTIPLY, new Element[] {X[1], newPow, DPow});
                        }
                    }
                    F Z = new F(MULTIPLY, new Element[] {X[1], new F(LN, X[0])});
                    F EpowZ = new F(EXP, Z);
                    Element DZ = Z.D(num, ring);
                    return (DZ.isZero(ring)) ? ring.numberZERO : new F(MULTIPLY, new Element[] {EpowZ, DZ});
                }
            case ROOTOF: {
                if (X[0].isItNumber()) {
                    return ring.numberZERO; // если онование число, иначе
                }
                Element mn = new Fraction(ring.numberONE, X[1]);// наша степень
                Element mn_mi_one = new Fraction(ring.numberONE.subtract(X[1], ring), X[1]);
                Element dOsn = X[0].D(num, ring);
                return (dOsn.isZero(ring)) ? ring.numberZERO : new F(MULTIPLY, new Element[] {mn, new F(POW, new Element[] {X[0], mn_mi_one}), dOsn});
            }
        }
        return new F(F.D, this);
    }

    /**
     * Метод для построения функции типа F , из вектора и инта.
     *
     * @param vec - имя будущего F
     * @param name - будущий массив аргументов
     *
     * @return new F(name ,vec);
     */
    public F ConvertVecToF(Vector<Element> vec, int name) {
        if (vec.size() == 1) {
            return (vec.firstElement() instanceof F) ? (F) vec.firstElement() : new F(ID, vec.firstElement());
        }
        Element[] arg = new Element[vec.size()];
        vec.copyInto(arg);
        return new F(name, arg);
    }

    @Override
    public Element multiply(Element f, Ring ring) {
        // if 2 absolute value we get 1 absolute value
        if ((name == F.ABS) && (f instanceof F) && (((F) f).name == F.ABS)) {
            F fNew = CanonMultiply(this.X[0], ((F) f).X[0], MULTIPLY);
            return new F(F.ABS, ring.CForm.simplify_init(fNew));
        }
        if (f.isOne(ring)) {
            return this;
        }
        if (f.isZero(ring)) {
            return ring.numberZERO;
        }
        F res = CanonMultiply(this, f, MULTIPLY);
        int ll = res.X.length - 1;
        Element temp;
        if ((res.X[ll].isItNumber()) && ((!res.X[0].isItNumber()) || (res.X[0] instanceof F))) {
            temp = res.X[ll];
            res.X[ll] = res.X[0];
            res.X[0] = temp;
        }
        return res;
    }

    @Override
    public Element add(Element f, Ring ring) {
        return (f.isZero(ring)) ? this : CanonMultiply(this, f, ADD);
    }

    public F add(F f, Ring ring) {
        return (f.isZero(ring)) ? this : CanonMultiply(this, f, ADD);
    }

    public Element divideSimplify(Element f, Ring ring) {
        return divideExact(f, ring);
    }

    @Override
    public Element divideExact(Element f, Ring ring) {
        // if 2 absolute value we get 1 absolute value
        if ((name == F.ABS) && (f instanceof F) && (((F) f).name == F.ABS)) {
            Element ee = ring.CForm.ElementToPolynom(((F) f).X[0], false);
            Element th = ring.CForm.ElementToPolynom(this.X[0], false);
            ee = (new Fraction(th, ee)).cancel(ring);
            return new F(F.ABS, ring.CForm.UnconvertAllLevels(ee));
        }
        Element ee = ring.CForm.ElementToPolynom(f, false);
        Element th = ring.CForm.ElementToPolynom(this, false);
        ee = (new Fraction(th, ee)).cancel(ring);
        return ring.CForm.UnconvertAllLevels(ee);
    }

    @Override
    public Element divide(Element f, Ring ring) {
        // if 2 absolute value we get 1 absolute value
        if ((name == F.ABS) && (f instanceof F) && (((F) f).name == F.ABS)) {
            F fNew = new F(F.DIVIDE, this.X[0], ((F) f).X[0]);
            return new F(F.ABS, ring.CForm.simplify_init(fNew));
        }
        return (new F(DIVIDE, new Element[] {this, f}));
    }

    @Override
    public Element divideToFraction(Element x, Ring ring) {
        return new Fraction(this, x);
    }

    @Override
    public Element pow(int p, Ring ring) {
        if (p == 1) {
            return this;
        }
        if (p == 0) {
            return ring.numberONE;
        }
        int pI;
        if (name == SQRT) {
            pI = 2;
        } else if (name == CUBRT) {
            pI = 3;
        } else if (name == ROOTOF) {
            pI = X[1].intValue();
        } else {
            switch (name) {
                case intPOW:
                    return new F(intPOW, new Element[] {X[0], ring.numberONE.valOf(p * X[1].intValue(), ring)});
                case POW:
                    return new F(intPOW, new Element[] {X[0], ring.numberONE.valOf(p, ring).multiply(X[1], ring)});
                default:
                    return new F(intPOW, new Element[] {this, ring.numberONE.valOf(p, ring)});
            }
        }
        int pDiv = p / pI;
        int pRem = p % pI;
        Element newN = ring.numberONE.valOf(pDiv, ring);
        Element res = new F(intPOW, X[0], newN);
        return (pI == p) ? X[0] : (pRem == 0) ? res
                : (pDiv == 0) ? new F(intPOW, this, ring.numberONE.valOf(pRem, ring))
                        : (pDiv == 1) ? (pRem == 1) ? X[0].multiply(this, ring)
                                        : X[0].multiply(new F(intPOW, this, ring.numberONE.valOf(pRem, ring)), ring)
                                : (pRem == 1) ? res.multiply(this, ring)
                                        : res.multiply(new F(intPOW, this, ring.numberONE.valOf(pRem, ring)), ring);
    }

    /**
     * Метод складывающий или умножающий элементы(один из которых типа F)
     * ,который не допускает образования ассоциативных скобок
     *
     * @param a - элемент операции
     * @param b - элемент операции
     * @param name_ - имя операции (ADD | MULTIPLY)
     *
     * @return
     */
    public F CanonMultiply(Element a, Element b, int name_) {
        int numA = argsLength(a, name_);
        int numB = argsLength(b, name_);
        Element[] NewX = new Element[numA + numB]; // заводим массив новых аргументов
        if (numA == 1) { //заполняем массив по обстоятельствам
            NewX[0] = a;
            if (numB == 1) {
                NewX[1] = b;
            } else {
                System.arraycopy(((F) b).X, 0, NewX, 1, numB);
            }
        } else {
            System.arraycopy(((F) a).X, 0, NewX, 0, numA);
            if (numB == 1) {
                NewX[numA] = b;
            } else {
                System.arraycopy(((F) b).X, 0, NewX, numA, numB);
            }
        }
        return new F(name_, NewX);
    }

    /**
     * Процедура определяющая длинну массива аргументов X, если только это
     * функция одноименная с name. Byfxt djpdhfoftn 1.
     *
     * @param a - F
     * @param name - нужное нам имя
     *
     * @return длинна
     */
    protected int argsLength(Element a, int name) { //int nn=1;
        return ((a instanceof F) && (((F) a).name == name)) ? ((F) a).X.length : 1;
//            return (((F) a).name != name) ? 1 : ((F) a).X.length;
//        } else {
//            return 1;
//        }
    }

    @Override
    public Element sqrt(Ring r) {
        if (name == F.intPOW) {
            double newPow = X[1].doubleValue() / 2;
            return (Math.abs(newPow - Math.round(newPow)) != 0) ? new F(SQRT, this)
                    : (newPow == 1) ? new F(ABS, X[0]) : new F(intPOW, new Element[] {X[0], r.numberONE.valOf(newPow, r)});
        }
        return new F(SQRT, this);
    }

    @Override
    public Element subtract(Element f, Ring ring) {
        if (isZero(ring)) {
            return f.negate(ring);
        }
        if (f.isZero(ring)) {
            return this;
        }
        if (f.numbElementType() < Ring.F) {
            return new F(SUBTRACT, new Element[] {this, f});
        }
        if (name == SUBTRACT && ((F) f).name == SUBTRACT) {
            Element num = X[0].add(((F) f).X[1], ring);
            Element denum = X[1].add(((F) f).X[0], ring);
            return new F(SUBTRACT, new Element[] {num, denum});
        }
        if (name == SUBTRACT) {
            Element denum = X[1].add(f, ring);
            return new F(SUBTRACT, new Element[] {X[0], denum});
        }
        if (((F) f).name == SUBTRACT) {
            Element num = this.add(((F) f).X[1], ring);
            return new F(SUBTRACT, new Element[] {num, ((F) f).X[0]});
        }
//        if (f instanceof Series) {
//            return (F) (((Series) f).subtract(this, ring)).multiply(NumberR.MINUS_ONE, ring);
//        }
        return new F(SUBTRACT, new Element[] {this, f});
    }

    @Override
    public Element negate(Ring ring) {
        switch (name) {
            case ID:
                return new F(ID, X[0].negate(ring));
            case SUBTRACT:
                return new F(SUBTRACT, new Element[] {X[1], X[0]});
            case DIVIDE:
                return new F(DIVIDE, new Element[] {X[0].negate(ring), X[1]});
            case ADD: {
                Element[] NegeteArg = new Element[X.length];
                for (int i = 0; i < X.length; i++) {
                    NegeteArg[i] = X[i].negate(ring);
                }
                return new F(ADD, NegeteArg);
            }
            case MULTIPLY: {
                ArrayList<Element> negeteArg = new ArrayList<Element>();
                int count = 0;
                for (int i = 0; i < X.length; i++) {
                    if ((X[i].numbElementType() <= Ring.Rational) && (X[i].isNegative())) {
                        Element el = X[i].negate(ring);
                        if (!el.isOne(ring)) {
                            negeteArg.add(el);
                        }
                        count++;
                    } else {
                        negeteArg.add(X[i]);
                    }
                }
                if (count % 2 == 0) {
                    Element el = negeteArg.get(0);
                    negeteArg.remove(0);
                    negeteArg.add(0, el.negate(ring));
                }
                return (negeteArg.size() == 1) ? negeteArg.get(0) : new F(MULTIPLY, negeteArg.toArray(new Element[negeteArg.size()]));
            }
            default:
                return new F(MULTIPLY, new Element[] {ring.numberMINUS_ONE, this});
        }
    }

    @Override
    public Boolean isMinusOne(Ring ring) {
        return name == ID && X[0].isMinusOne(ring);
    }

    @Override
    public Boolean isMinusI(Ring ring) {
        return name == ID && X[0].isMinusI(ring);
    }

    @Override
    public Boolean isI(Ring ring) {
        return name == ID && X[0].isI(ring);
    }

    @Override
    public Boolean isNegative() {
        switch (name) {
            case ID:
                return X[0].isNegative();
            case ADD: {
                for (Element el : X) {
                    if (!el.isNegative()) {
                        return false;
                    }
                }
                return true;
            }
            case MULTIPLY: {
                int ch = 0;
                for (Element el : X) {
                    if (el.isNegative()) {
                        ch++;
                    }
                }
                return (ch % 2 == 0) ? false : true;
            }
            default:
                return false;
        }
    }

    @Override
    public Boolean isOne(Ring ring) {
        return name == ID && X[0].isOne(ring);
    }

    @Override
    public Boolean isZero(Ring ring) {
        if (name == ID) {
            return X[0].isZero(ring);
        }
        return false;
    }

    /**
     * Производит факторизацию функции типа F , на одном уровне. Используется
     * простая факторизация полинома factor. Состоит из 2х частей. Первая factor
     * = head, в торая часть CanonicForm.factorBody
     *
     * @param ring - Ring
     * @param return F as polynomial factorization
     *
     * @return
     */
    @Override
    public F factor(Ring ring) {
        this.cleanOfRepeatingWithNewVectors(ring);
        Element[] ee = ring.CForm.factorBody(this);
        if (ee[1] == null) {
            ring.exception.append("\n ").append("in F: factor something wrong:  ").append(ee[0].toString(ring));
        }
        return (ee[0] instanceof F) ? (F) ee[0] : new F(F.ID, ee[0]);
    }

    @Override
    public Element arg(Ring ring) {
        if (name == F.ID) {
            return X[0].arg(ring);
        }
        Element res = ring.CForm.ElementToPolynom(this, false);
        ring.CForm.makeWorkRing(0);
        if (!(res instanceof Fraction)) {
            return (ring.CForm.UnconvertAllLevels(res.arg(ring))).valueOf(ring);
        }
        Fraction fr = (Fraction) res;
        return (ring.CForm.UnconvertAllLevels(fr.num.arg(ring).subtract(fr.denom.arg(ring), ring))).valueOf(ring);
    }

    /**
     * This function returns the factor of function and virtual polynomials of
     * the result This polynomial is useful for future simplification
     *
     * @param ring
     *
     * @return
     */
    public Element[] factorPlusPolynom(boolean newVect, Ring ring) {
        Element node = this;
        if (newVect) {
            node = cleanOfRepeating(node, ring);
        }
        Element[] ee = ring.CForm.factorBody(node);
        if (ee[1] == null)  System.out.println("in F: factorPlusPolynom-function sais <<something wrong= second=null>>");
        return ee;
    }

    /**
     * Метод производящий полную обработку дерева, а именно : 1) Приводятся
     * подобные 2) Раскрваются все ассоциативные скобки 3) Сокращаются дроби
     * если это возможно РЕЗУЛЬТАТ ----- ОДНО ПРОИЗВЕДЕНИЕ ИЛИ ОДНА ДРОБЬ !!!!
     * Пример : sin(x)+sin(x) = 2*sin(x) (x+1)sin(y)=x*sin(y)+sin(y)
     * (sin(x)/cos(y))+x =(sin(x)+x*cos(y))/cos(y)
     */
    /**
     * Упрощение функции первого арифметического уровня
     *
     * @param ring
     * @param flagMakeVector-- boolean flagMakeVector - if true - do new Vectors
     * Elems and Functs
     *
     * @return
     */
//    public F expand(Ring ring) { 
//        Element res1 = ring.CForm.CleanElement(this);
//        Element res =  ring.CForm.SimplifyWithGether(res1, true);
//        return (res instanceof F) ? (F) res : new F(ID, res);
//    }
    @Override
    public F expand(Ring ring) {//, boolean flagMakeVector) { 
        Element res1 = ring.CForm.CleanElement(this);
        Element res = ring.CForm.SimplifyWithGether(res1, false);
        return (res instanceof F) ? (F) res : new F(ID, res);
    }

    @Override
    public F simplify(Ring ring) {
        Element res = ring.CForm.simplify_init(this);
        return (res instanceof F) ? (F) res : new F(ID, res);
    }

    public String treeToString() {
        StringBuilder res = new StringBuilder("[D 0]" + "{" + FUNC_NAMES[this.name] + "}");
        System.out.println("\n    ELEMENTS   ");
        this.FTtS(res, 1);
        System.out.println("\n    FUNCTION ");
        System.out.println(this);
        System.out.println("\n      TREE   ");
        return res.toString();
    }

    private StringBuilder FTtS(StringBuilder res, int num) {
        int dep = num;
        num++;
        for (Element el : X) {
            if (el instanceof F) {
                res.append(" [D ").append(dep).append("]" + "{").append(FUNC_NAMES[((F) el).name]).append("}");
                ((F) el).FTtS(res, num);
            } else {
                res.append(" [D ").append(dep).append("]" + "{").append(el).append("}");
            }
        }
        return res;
    }

    void Tree_to_String() {
        System.out.println(" Function = " + treeToString());
    }

    @Override
    public Element toNewRing(int Algebra, Ring r) {
        F my = (F) clone(); // чтобы не испортить старую функцию
        my.elementToNewRing(Algebra, r);
        return my;
    }

    public void elementToNewRing(int nameAlgebra, Ring ring) {
        for (int i = 0; i < X.length; i++) {
            switch (X[i].numbElementType()) {
                case Ring.F:
                    ((F) X[i]).elementToNewRing(nameAlgebra, ring);
                    break;
                case Ring.Polynom:
                    X[i] = ((Polynom) X[i]).toPolynom(nameAlgebra, ring);
                    break;
                case Ring.VectorS:
                case Ring.Fname:
                case Ring.MatrixD:
                    break;
                default:
                    X[i] = X[i].toNumber(nameAlgebra, ring);
            }
        }
    }

    public void fromZtoR64(Ring ring) {
        for (int i = 0; i < X.length; i++) {
            if (X[i] instanceof F) {
                ((F) X[i]).fromZtoR64(ring);
            } else {
                if (X[i] instanceof NumberZ) {
                    X[i] = ((NumberZ) X[i]).toNumber(Ring.R64, ring);
                }
            }
        }
    }

    @Override
    public int numbElementType() {
        return Ring.F;
    }

    @Override
    public String toString() {
        Ring ring = Ring.ringR64xyzt;
        ring.setDefaulRing();
        return toString(ring);
    }

    /**
     * Is this int number corresponds to the name of infix function?
     *
     * @param name - name of function
     *
     * @return true (this is an infix name) OR false (this is not an infix name)
     */
    public static boolean isInfixName(int name) {
        return (name >= FIRST_INFIX_NAME) && (name <= LAST_INFIX_NAME);
    }

    /**
     * Is this int number corresponds to the name of infix function with
     * commutative arguments?
     *
     * @param name - name of function
     *
     * @return true (this is an infix name with commutative arguments) OR false
     * in the other case
     */
    public static boolean isInfixCommutName(int name) {
        return (name >= FIRST_INFIX_COMMUT_ARG) && (name <= LAST_INFIX_COMMUT_ARG);
    }

    @Override
    public String toString(Ring r) {
        F f = this;
        StringBuilder res = new StringBuilder();
        String temp = "";
        int Xlen = X.length;
        if (name < F.MAX_F_NUMB) {
            boolean isInfix = isInfixName(name);
            //          boolean  isInfixCommut =  isInfixCommutName(name);
            if (!isInfix) {
                switch (name) {
                    //case d:  res.append("\\d"); break;
                    //   case D:
                    //       res.append("\\D");
                    //       break;
                    case COMPLEMENT:
                    case FACTORIAL:
                    case ID:
                    case POW:
                    case intPOW:
                    //   case d:
                    case VECTORS:
                    case ALGEBRAIC_EQUATION:
                    case TRANSPOSE:
                    case CONJUGATE:
                    case ADJOINT:
                    case INVERSE:
                    case GENINVERSE:
                    case CLOSURE:
                    case MULTIPLY_NC:
                        break;
                    case SERIES:
                        res.append("\\sum");
                        break;
                    case ROOTOF:
                        res.append("\\sqrt");
                        break;
                    case CUBRT:
                        res.append("\\rootof[3]");
                        break;
                    default:
                        res.append("\\").append(FUNC_NAMES[name]);
                }
            } // конец пред-обработки - имя для всех не инфиксных уже стоит
            switch (name) {  //спец-обработка некоторых дальше... 
                case COMPLEMENT:
                    return res.append(X[0].toString(r)).append("'").toString();
                case FACTORIAL:
                    return res.append(X[0].toString(r)).append("!").toString();
                case TRANSPOSE:
                    return res.append(X[0].toString(r)).append("^{T}").toString();
                case CONJUGATE:
                    return res.append(X[0].toString(r)).append("^{\\ast}").toString();
                case ADJOINT:
                    return res.append(X[0].toString(r)).append("^{\\star}").toString();
                case GENINVERSE:
                    return res.append(X[0].toString(r)).append("^{+}").toString();
                case INVERSE:
                    return res.append(X[0].toString(r)).append("^{-1}").toString();
                case CLOSURE:
                    return res.append(X[0].toString(r)).append("^{\\times}").toString();
                case HBOX:
                    return res.append("{").append(X[0].toString(r)).append("}").toString();
                case INT:
                    res.append("(");
                    if (X.length == 1) {
                        return res.append(X[0].toString(r)).append(")d x ").toString();
                    }
                    if (X.length == 2) {//неопределенный интеграл
                        return res.append(X[0].toString(r)).append(" )d").append(X[1].toString(r)).toString();
                    }
                    // определенный интеграл
                    return res.append("_{").append(X[2].toString(r)).append("}^{").append(X[3].toString(r)).append("}").append(X[0].toString(r)).append(" d").append(X[1].toString(r)).toString();//name_{a}^{b}F(x) dx

                case ALGEBRAIC_EQUATION:
                    return res.append(X[0].toString(r)).append("=").append(X[1].toString(r)).toString();
                case LOG:
                    return res.append("_{").append(X[0].toString(r)).append("}(").append(X[1].toString(r)).append(")").toString();
                case SQRT:
                    return res.append("{").append(X[0].toString(r)).append("}").toString();
                case CUBRT:
                    return res.append("{").append(X[0].toString(r)).append("}").toString();
                case ROOTOF:
                    String ssR = X[1].toString(r);
                    int indPo = ssR.indexOf('.');
                    if (indPo != -1) {
                        ssR = ssR.substring(0, indPo);
                    }
                    return res.append("[").append(ssR).append("]{").append(X[0].toString(r)).append("}").toString();
                case IC: {
                    res.append("(");
                    for (int q = 0; q < (X.length - 1); q++) {
                        if (X[q] != null) {
                            res.append(X[q].toString(r));
                        }
                        res.append(",");
                    }
                    res.append(X[X.length - 1].toString(r)).append(")");
                    return res.toString();
                }
                case DOMAIN_OF_FUNC: {
                    res.append(X[0].toString(r)).append("= {");
                    if (((F) X[1]).X.length != 0) {
                        res.append(X[1].toString(r));
                    }
                    if (((F) X[2]).X.length != 0) {
                        res.append(X[2].toString(r));
                    }
                    if (((F) X[3]).X.length != 0) {
                        res.append(X[3].toString(r));
                    }
                    res.append("}");
                    return res.toString();
                }
                case D:
                case d:  // ----------------------- производная начало --------------
                    int xl = X.length;
                    if (xl > 1) {
                        if (X[1] instanceof F) {
                            int i = 0;
                            F ff = (F) X[1];
                            Element[] xx = null;
                            if ((ff).name == F.VECTORS) {
                                xx = (ff).X;
                            } else {
                                xx = X;
                                i = 1;
                            }
                            for (; i < X.length; i++) {
                                temp += xx[i].toString(r);
                            }
                        }
                        Element[] VV;
                        if (X.length == 2) {
                            VV = (X[1] instanceof VectorS) ? ((VectorS) X[1]).V : new Element[] {X[1]};
                            Element varD = VV[0];
                            for (int h = 1; h < VV.length; h++) {
                                varD = varD.multiply(VV[h], r);
                            }
                            if (varD instanceof F) {
                                varD = varD.valueOf(r);
                            }
                            String varS = varD.toString(r);
                            if (varS.length() > 1) {
                                varS = "_{" + varS + "}(";
                            } else {
                                varS = "_" + varS + "(";
                            }
                            return res.append(varS).append(X[0].toString(r)).append(")").toString();
                        }// end of X.length == 2
                        String d;
                        if (X.length == 4) {
                            temp = "{" + temp + "=" + X[2].toString(r) + "}";
                            d = X[3].toString(r);
                        } else {
                            d = X[2].toString(r);
                        }

                        if (d.equals("2")) {
                            d = "{''}";
                        } else if (d.equals("3")) {
                            d = "{'''}";
                        } else {
                            d = "{(" + d + " )}";
                        }
                        return res.append("").append(X[0].toString(r)).append("^").append(d).append("_").append(temp).toString();
                    } // X.length=1 
                    else {
                        return ((X == null) || (X.length == 0) || (X[0] == null))
                                ? res.toString()
                                : res.append("(" + X[0].toString(r) + ")").toString();
                    }  // ----------------------- производная конец --------------
                case SEQUENCE:
                    return res.append("{").append(X[1].toString(r)).append("=0,..,\\infty}").append(X[0]).toString();
                case SHOWPLOTS:
                case SYSTEM:
                    res.append("(");
                    for (int i = 0; i < X.length; i++) {
                        if (i == X.length - 1) {
                            res.append(X[i].toString(r));
                        } else {
                            res.append(X[i].toString(r)).append(",");
                        }
                    }
                    res.append(")");
                    return res.toString();
                case SERIES:
                    return res.append("_{").append(X[1].toString(r)).append("=").append(X[2].toString(r)).append("}^{\\infty} ").append(X[0].toString(r)).toString();
                case PROD:
                case SUM:
                    return res.append("_{").append(X[1].toString(r)).append("=").append(X[2].toString(r)).append("}^{").append(X[3].toString(r)).append("}(").append(X[0]).append(")").toString();
                case POW:
//                    if (X[1].multiply(r.posConst[2], r).isOne(r)) {
//                        return res.append("\\sqrt{").append(X[0].toString(r)).append("}").toString();
//                    } else if (X[1].multiply(r.posConst[3], r).isOne(r)) {
//                        return res.append("\\rootof[3]{").append(X[0].toString(r)).append("}").toString();
//                    }
                case intPOW:
                    temp = X[1].toString(r);
                    if (temp.length() > 1) {
                        temp = "{" + temp + "}";
                    }
                    String base = X[0].toString(r);
                    if (base.length() > 1) {
                        base = "(" + base + ")";
                    }
                    return res.append(base).append("^").append(temp).toString();
                case VECTORS:
                    res.append("[");
                    for (int i = 0; i < X.length - 1; i++) {
                        res.append(X[i] == null ? "? , " : X[i].toString(r) + ", ");
                    }
                    res.append(X[X.length - 1] == null ? "?" : X[X.length - 1].toString(r)).append("]");
                    return res.toString();
                case PLOT:
                case TABLEPLOT:
                case TABLEPLOT2:
                case TABLEPLOT4:
                    if (X.length > 1) {
                        res.append("(").append(X[0].toString(r)).append(",").append(X[1].toString(r)).append(")");
                    } else {
                        res.append("(").append(X[0].toString(r)).append(")");
                    }
                    return res.toString();
                case PARAMPLOT:
                    return res.append("(").append(X[0].toString(r)).append(",").append(X[1].toString(r)).append(")").toString();
                case MULTIPLY_NC:
                    for (int k = 0; k < (Xlen - 1); k++) {
                        res.append(X[k].toString(r)).append("*");
                    }
                    return res.append(X[Xlen - 1]).toString();
                default:
                    if ((X.length == 1) && (X[0] instanceof Polynom)) {
                        String pp = X[0].toString(r);
                        if ((pp.length() != 1) || (res.length() > 0)) {
                            pp = "(" + pp + ")";
                        }
                        res.append(pp);
                        break;
                    }
                    //   if(!isInfixCommut) 
                    res.append("(");
                    if (Xlen == 0) {
                        return res.append(")").toString();
                    }
                    for (int i = 0; i < Xlen - 1; i++) {
                        if ((name == SYSTLDE || name == INITCOND || name == EQUATION || name == SOLVE) && (i % 2 == 0)) {
                            res.append(X[i].toString(r)).append(isInfix ? FUNC_NAMES[name] : " = ");
                        } else {
                            if (!(X[i] instanceof Polynom)) {
                                boolean WB = (name <= LAST_WB) && (name >= FIRST_WB || name == B_LESS || name == B_EQ);
                                //if (X[i] == null) {res.append(X[i]).append(", ");} else {
                                res.append(X[i].toString(r)).append((isInfix)
                                        ? ((WB) ? "" : " \\") + FUNC_NAMES[name] + " "
                                        : ", ");
                            } //} 
                            else {
                                String infNa = ", ";
                                if (isInfix) {
                                    infNa = FUNC_NAMES[name];
                                    infNa = ((infNa.length() > 1) && (infNa.charAt(1) == 'e')) ? " \\\\" + infNa + " " : " " + infNa + " ";
                                }
                                res.append("(").append(X[i].toString(r)).append(")").append(infNa);
                            }
                        }
                    }
                    if (X[Xlen - 1] == null) {
                        res.append("(").append(X[Xlen - 1]).append("))");
                    } else {
                        if ((name == F.ADD) && (!X[Xlen - 1].isNegative())) {
                            res.append(X[Xlen - 1].toString(r));
                        } else {
                            res.append("(").append(X[Xlen - 1].toString(r)).append(")");
                        }
                        // if(!isInfixCommut)
                        res.append(")");
                    }   // end default
            } //---------------------- конец switch (name)
        } // конец обычных функций из F.... остались новые процедуры пользователя -------------------- 
        else {
            return res.append("Procedure_").append(name - F.MAX_F_NUMB).toString();
        }
        return FUtils.deleteDoubledBrackets(res.toString());
    }

    public void showTree(Ring r) {
     //  FuncTreeGraph.show(this, r);
    }

    public void paintGraph() {
//        PaintGraph g = new PaintGraph();
//        g.init(this);

    }

    /**
     * Заполняем массив имеющихся переменных по данному полиному и текущему
     * состоянию массива переменных
     *
     * @param p полином
     * @param m массив переменных
     *
     * @return массив переменных c extnjv gjkbyjvf p
     */
    private static int[] fillMassIOV(Polynom p, int[] m) {
        if (p.isItNumber()) {
            return m;
        }
        int col = p.powers.length / p.coeffs.length;
        for (int j = 0; j < p.powers.length; j += col) {
            for (int i = 0; i < col; i++) {
                if (p.powers[i + j] != 0) {
                    if (m[i] == 0) {
                        m[i] = 1;
                        m[m.length - 1] = m[m.length - 1] + 1;
                    }
                }
            }
        }
        return m;
    }

    /**
     * Заполняем массив имеющихся переменных по данному узлу функции и текущему
     * состоянию массива переменных
     *
     * @param funk - узел фуекции
     * @param m массив переменных
     *
     * @return массив переменных c extnjv gjkbyjvf p
     */
    private static void fillForIOV(F func, int[] mas) {
        if (mas[mas.length - 1] == mas.length - 1) {
            return;
        }
        for (int i = 0; i < func.X.length; i++) {
            if (func.X[i] instanceof F) {
                fillForIOV((F) func.X[i], mas);
            }
            if (func.X[i] instanceof Polynom) {
                mas = fillMassIOV((Polynom) func.X[i], mas);
            }
        }
    }

    /**
     * Массив индексов переменных в этой функции. Например, [0,2], если
     * присутствуют только X и Z. Например, [1], если присутствуют только Y. (и
     * Ring=[X,Y,Z])
     *
     * @param r Ring
     *
     * @return
     */
    public int[] indexsOfVars(Ring r) {
        int[] res = new int[r.varPolynom.length + 1];
        fillForIOV(this, res);
        if (res[res.length - 1] == r.varPolynom.length) {
            int[] all = new int[r.varPolynom.length];
            for (int i = 0; i < all.length; i++) {
                all[i] = i;
            }
            return all;
        }
        ArrayList<Integer> real = new ArrayList<Integer>();
        for (int i = 0; i < res.length - 1; i++) {
            if (res[i] == 1) {
                real.add(i);
            }
        }
        int[] rres = new int[real.size()];
        for (int i = 0; i < rres.length; i++) {
            rres[i] = real.get(i).intValue();
        }
        return rres;
    }

    /**
     * функция фиксирует наличие $\pi$, а как множитель его удаляет для
     * дальнейшего вычисления тригонометрической функции
     *
     * @param f- подаваемая функция
     * @param r- кольцо
     *
     * @return Element
     */
    public Element remindPI(Element f, Ring r) { // see at !!
        if (f instanceof Fname) {
            return ((((Fname) f).name).equals(PI.name)) ? Fraction.Z_ONE : f;
        }
        if (f instanceof F) {
            F f_simply = ((F) f).expand(r);
            switch (f_simply.name) {
                case DIVIDE:
                    Element oneArg = remindPI(f_simply.X[0], r);
                    return (oneArg instanceof Fraction)
                            ? new Fraction(((Fraction) oneArg).num, f_simply.X[1])
                            : new F(F.DIVIDE, new Element[] {oneArg, f_simply.X[1]});
                case MULTIPLY:
                    ArrayList<Element> newMasArg = new ArrayList<Element>();
                    for (int i = 0; i < f_simply.X.length; i++) {
                        if (!isPi(f_simply.X[i])) {
                            newMasArg.add(f_simply.X[i]);
                        }
                    }
                    if (newMasArg.size() == 1
                            && (newMasArg.get(0) instanceof Fraction || newMasArg.get(0) instanceof NumberZ)) {
                        return new Fraction(newMasArg.get(0), r);
                    } else {
                        return new F(MULTIPLY, newMasArg.toArray(new Element[newMasArg.size()]));
                    }
                case ID:
                    return remindPI(f_simply.X[0], r);
            }
        }
        return f;
    }

    /**
     * проверяет на равенство Pi
     *
     * @param Element el- задаваемая функция
     *
     * @return boolean
     */
    private boolean isPi(Element el) {
        if (el instanceof Fname) {
            return ((Fname) el).name.equals(PI.name);
        }
        return false;
    }

    public boolean Proverca_is_Z_Type(F f) {
        for (int i = 0; i < f.X.length; i++) {
            if (X[i].numbElementType() < Ring.Polynom && X[i].numbElementType() > Ring.Q) {
                return false;
            }
        }
        return true;
    }

    /**
     * вычисляет значение Sin
     *
     * @param f -функция, значение от которой хотим посчитать
     * @param ring- кольцо
     *
     * @return значение синуса
     */
    public Element Sin(Element f, Ring ring) {
        Element delPi = new F().remindPI(f, ring);
        return (delPi instanceof Fraction) ? delPi.sin(ring) : new F(F.SIN, f);
    }

    /**
     * вычисляет значение косинуса
     *
     * @param f- функция, значение от которой хотим посчитать
     * @param ring - кольцо
     *
     * @return значение косинуса
     */
    public Element Cos(Element f, Ring ring) {
        Element delPi = new F().remindPI(f, ring);
        return (delPi instanceof Fraction) ? delPi.cos(ring) : new F(F.COS, f);
    }

    /**
     * вычисляет значение тангенса
     *
     * @param f- функция, тангенс от которой хотим посчитать
     * @param ring - кольцо
     *
     * @return значение тангенса
     */
    public Element Tan(Element f, Ring ring) {
        Element delPi = new F().remindPI(f, ring);
        return (delPi instanceof Fraction) ? delPi.tan(ring) : new F(F.TG, f);
    }

    /**
     * To change this function to its value in the field of complex number,
     * using exp and ln function
     *
     * @param ring
     *
     * @return new function (this function is not saved. It is transformed to
     * new one.)
     */
    @Override
    public F toComplex(Ring ring) {
        for (int i = 0; i < X.length; i++) {
            if (X[i] instanceof F) {
                X[i] = ((F) X[i]).toComplex(ring);
            }
        }
      //
        return this;
    }

    /**
     * вычисляет значение котангенса
     *
     * @param f - функцкция, котангенс которой мы хотим посчитать
     * @param ring - кольцо
     *
     * @return значение котангенса
     */
    public Element Ctg(Element f, Ring ring) {
        Element delPi = new F().remindPI(f, ring);
        return (delPi instanceof Fraction) ? delPi.ctg(ring) : new F(F.CTG, f);
    }

    @Override
    public Object clone() {
        F temp = new F(name, new Element[X.length]);
        for (int i = 0; i < X.length; i++) {
            if (X[i].numbElementType() < Ring.Polynom) {
                temp.X[i] = X[i];
            } else {
                temp.X[i] = (Element) X[i].clone();
            }
        }
        return temp;
    }

    /**
     * Root of pow from this. The result is this in the power 1/pow корень
     * степени n числа: rootOf(x,n)
     *
     * @param pow - integer. We get the power 1/pow
     * @param r - Ring
     *
     * @return (this)^{1/pow}
     */
    @Override
    public Element rootOf(int pow, Ring r) {
        if (name == ID) {
            return X[0].rootOf(pow, r);
        }
        return new F(ROOTOF, new Element[] {this, r.numberONE.valOf(pow, r)});
    }

    @Override
    public Element sin(Ring ring) {
        Element res = Sin(this, ring);
        return res;
    }

    @Override
    public Element cos(Ring ring) {
        return Cos(this, ring);
    }

    @Override
    public Element tan(Ring ring) {
        return Tan(this, ring);
    }

    @Override
    public Element ctg(Ring ring) {
        return Ctg(this, ring);
    }

    @Override
    public Element arcsn(Ring ring) {
        Element temp_res = this.expand(ring);
        if (temp_res instanceof F) {
            return null; // new FuncNumberQZ(ring).arc_trigFunc_Rad(temp_res, F.SIN, ring);
        }
        return temp_res.arcsn(ring);
    }

    @Override
    public Element GCD(Element f, Ring ring) {
        CanonicForms Cfs = ring.CForm;//new CanonicForms(ring, true);
        Element _this = Cfs.ElementConvertToPolynom(this);
        Element _f = Cfs.ElementConvertToPolynom(f);
        return Cfs.UnconvertToElement(_this.GCD(_f, ring));
    }

    @Override
    public Element arccs(Ring ring) {
        Element temp_res = this.expand(ring);
        if (temp_res instanceof F) {
            return null; //new FuncNumberQZ(ring).arc_trigFunc_Rad(temp_res, F.COS, ring);
        }
        return temp_res.arccs(ring);
    }

    @Override
    public Element arctn(Ring ring) {
        Element temp_res = this.expand(ring);
        if (temp_res instanceof F) {
            return null;  //new FuncNumberQZ(ring).arc_trigFunc_Rad(temp_res, F.TG, ring);
        }
        return temp_res.arctn(ring);
    }

    @Override
    public Element arcctn(Ring ring) {
        Element temp_res = this.expand(ring);
        if (temp_res instanceof F) {
            return null; //new FuncNumberQZ(ring).arc_trigFunc_Rad(temp_res, F.CTG, ring);
        }
        return temp_res.arcctn(ring);
    }

    @Override
    public Element abs(Ring ring) {
        if (isZero(ring)) {return ring.numberZERO;}
        if ((name == ID)||(name==ABS)) return X[0].abs(ring);
        if (name == MULTIPLY){ for (int i = 0; i < X.length; i++) {
                X[i]=X[i].abs(ring); }return this;   }
        if (name == DIVIDE){X[1]=X[1].abs(ring);X[0]=X[0].abs(ring); return this;}        
        return new F(F.ABS, this);
    }
    @Override
    public Element Re(Ring ring) {int a0=ring.algebra[0]; ring.algebra[0]|=Ring.Complex;
        Element fp=ring.CForm.ElementConvertToPolynom(this);
        Element re=fp.Re(ring);  ring.algebra[0]=a0;
        return ring.CForm.UnconvertAllLevels(re );
    }

    @Override
    public Element Im(Ring ring) {int a0=ring.algebra[0]; ring.algebra[0]|=Ring.Complex;
        Element fp=ring.CForm.ElementConvertToPolynom(this);
        Element im=fp.Im(ring);  ring.algebra[0]=a0;
        return ring.CForm.UnconvertAllLevels(im );
    }
    
    @Override
    public Element ln(Ring ring) {
        if (isZero(ring)) {
            return NEGATIVE_INFINITY;
        }
        if (isOne(ring)) {
            return ring.numberZERO;
        }
        return (name == ID) ? X[0].ln(ring) : new F(F.LN, this);
    }

    @Override
    public Element lg(Ring ring) {
        if (isZero(ring)) {
            return NEGATIVE_INFINITY;
        }
        if (isOne(ring)) {
            return ring.numberZERO;
        }
        return (name == ID) ? X[0].lg(ring) : new F(F.LG, this);
    }

    @Override
    public Element exp(Ring ring) {
        if (isZero(ring)) {
            return ring.numberONE;
        }
        if (isOne(ring)) {
            return this.e(ring);
        }
        return (name == ID) ? X[0].exp(ring) : new F(F.EXP, this);
    }

    /**
     * Factorization of functions with the help of Log and Exp identities.
     *
     * @param ring Ring
     *
     * @return result of factorization
     */
    @Override
    public Element factorLnExp(Ring ring) {
        Element[] f = this.factorPlusPolynom(true, ring);
        if (f[1] == null) {
            return this;
        }
        FactorPol fp = (FactorPol) f[1];
        FactorPol res = null; // new LN_EXP_POW_LOG_LG().factorLogExpHead(fp, 0, ring);
        return (res != null) ? ring.CForm.UnconvertAllLevels(res) : ring.CForm.UnconvertAllLevels(fp);
        //     factorLogBody(this, 0, ring);
    }

    /**
     * Expand of functions with the help of Log and Exp identities.
     *
     * @param ring Ring
     *
     * @return result of expansion
     */
    @Override
    public Element expandLn(Ring ring) {
        return  this; //new LN_EXP_POW_LOG_LG().expandLog(this, ring);
    }

    /**
     * If the function if a Fraction then return numerator
     *
     * @param ring Ring
     *
     * @return numerator of Fraction or return this
     */
    @Override
    public Element num(Ring ring) {
        Element f = this.ExpandForID();
        if (f instanceof Fraction) {
            return ((Fraction) f).num(ring);
        }
        return f;
    }

    /**
     * If the function if a Fraction then return denominator
     *
     * @param ring Ring
     *
     * @return denominator of Fraction or 1.
     */
    @Override
    public Element denom(Ring ring) {
        Element f = this.ExpandForID();
        if (f instanceof Fraction) {
            return ((Fraction) f).denom(ring);
        }
        return ring.numberONE;
    }

    /**
     * Метод генирурующий случайным образом объект типа F или Polynom убирая
     * сразу имя ID
     *
     * @param names -участвующие имена
     * @param op - имена участвующих операций (+,-,/,*,^)
     * @param Depth - глубина( кол-во арифметических уровней) Переменные
     * эквивалентные переменным в random() в классе polynom
     * @param randomType - array of: [maxPowers_1_var,.., maxPowers-last_var,
     * density, nbits] The density is an integer of range 0,1...100. (-15
     * denotes the value 15^(-1) )
     * @param rnd - объект типа Random
     * @param ring - кольцо
     *
     * @return сгенерированную случайным образом объект типа F или Polynom
     * убирая сразу имя ID
     */
    public Element randomFunction(int[] names, int[] op, int Depth, int[] randomType, Random rnd, Ring ring) {
        if (Depth == 0) {
            int n = rnd.nextInt(names.length + 1);
            if (n >= names.length) {
                return new Polynom().random(randomType, rnd, ring);
            }
            switch (names[n]) {
                case LOG:
                case POW:
                    return new F(names[n], new Polynom().random(randomType, rnd, ring), new Polynom().random(randomType, rnd, ring));
                case UNITSTEP:
                    Element rp = ring.numberONE.valOf(rnd.nextInt(10), ring);
                    int rpN = rnd.nextInt(2);
                    if (rpN == 1) {
                        rp = rp.negate(ring);
                    }
                    return new F(UNITSTEP, ring.varPolynom[0].add(rp, ring));
                default:
                    return new F(names[n], new Polynom().random(randomType, rnd, ring));
            }
        } else {
            int n = rnd.nextInt(op.length + 4);
            int nN = rnd.nextInt(names.length + 1);
            if (n >= op.length) {
                return (Depth == 1) ? randomFunction(names, op, Depth - 1, randomType, rnd, ring)
                        : (nN >= names.length) ? randomFunction(names, op, Depth - 1, randomType, rnd, ring)
                                : new F(names[nN], randomFunction(names, op, Depth - 1, randomType, rnd, ring));
            }
            switch (op[n]) {
                case ADD:
                case MULTIPLY:
                    int colNum = rnd.nextInt(3) + 2;
                    Element[] randArg = new Element[colNum];
                    for (int i = 0; i < colNum; i++) {
                        randArg[i] = randomFunction(names, op, Depth - 1, randomType, rnd, ring);
                    }
                    return new F(op[n], randArg);
                case intPOW:
                    return new F(intPOW, new Element[] {
                        randomFunction(names, op, Depth - 1, randomType, rnd, ring),
                        ring.numberONE.valOf(rnd.nextInt(3) + 2, ring)
                    });
                default:
                    return new F(op[n], new Element[] {
                        randomFunction(names, op, Depth - 1, randomType, rnd, ring),
                        randomFunction(names, op, Depth - 1, randomType, rnd, ring)
                    });
            }
        }
    }

    @Override
    public boolean isItNumber() {return false;}
 
    @Override
    public boolean isItNumber(Ring ring) {
      for (int i = 0; i < X.length; i++) {
          if (!X[i].isItNumber(ring))return false;}
      return true;
    }
    
    @Override
    public boolean isEven() {
        return (name != ID) ? false : X[0].isEven();
    }

    @Override
    public Element rootTheFirst(int n, Ring ring) {
        return (name == ID) ? X[0].rootTheFirst(n, ring)
                : new F(F.ROOTOF, new Element[] {this, ring.numberONE.valOf(n, ring)});
    }

    @Override
    public Element Factor(boolean doNewVect, Ring ring) { 
        Element[] f = this.factorPlusPolynom(doNewVect, ring);
        if (f[1] == null) { return this; }
        Element res=null; // = LN_EXP_POW_LOG_LG.fullFactor(f, 1, ring);// pol OR Fract OR factPol
        return (res != null) ? ring.CForm.UnconvertAllLevels(res) : ring.CForm.UnconvertAllLevels(f[1]);
    }

    @Override
    public Element Expand(Ring ring) {
        Element res = null; //LN_EXP_POW_LOG_LG.fullExpand(this, ring);
        return (res == null) ? this : res;
    }

    @Override
    public Element ExpandLog(Ring ring) {
        Element res = null; // new LN_EXP_POW_LOG_LG().expandLog(this, ring);
        return (res == null) ? this : res;
    }

    @Override
    public Element ExpandTrig(Ring ring) {
        Element res = null; // new TrigonometricExpand().ExpandTr(this, ring);
        return (res == null) ? this : res;
    }

    @Override
    public Element Simplify(Ring ring) {
        return    null; //LN_EXP_POW_LOG_LG.Simplify(this, ring);
    }

    /**
     * Замена порядка переменных во всех полиномах этого элемента
     *
     * @param varsMap - правило замены порядка переменных
     * @param flag - куда/откуда
     * @param ring - Ring
     *
     * @return - результат замены порядка переменныхво всех полиномах
     */
    @Override
    public Element changeOrderOfVars(int[] varsMap, boolean flag, Ring ring) {
        for (int i = 0; i < X.length; i++) {
            X[i] = X[i].changeOrderOfVars(varsMap, flag, ring);
        }
        return this;
    }

    @Override
    public Element FACTOR(Ring ring) {
        cleanOfRepeatingWithNewVectors(ring);
        Element res = helpFullFactor(this, ring);
        return res.Factor(false, ring);
    }

    private Element helpFullFactor1(Element el, Ring ring) {
        if (el instanceof F) {
            F f = (F) el;
            switch (f.name) {
                case F.ABS:
                case F.ADD:
                case F.SUBTRACT:
                case F.DIVIDE:
                case F.MULTIPLY:
                    Element[] arithArr = new Element[f.X.length];
                    for (int i = 0; i < arithArr.length; i++)  
                        arithArr[i] = helpFullFactor(f.X[i], ring);
                    return new F(f.name, arithArr);
                default:
                    Element[] newArr = new Element[f.X.length];
                    for (int i = 0; i < newArr.length; i++) {
                        newArr[i] = helpFullFactor(f.X[i], ring).Factor(false, ring);
                        if(newArr[i] instanceof FactorPol) newArr[i]=((FactorPol)newArr[i]).toPolynomOrFraction(ring);
                    }
                    return new F(f.name, newArr);
            }
        } else if (el instanceof Fraction) {
            return new F(F.DIVIDE, new Element[] {
                helpFullFactor(((Fraction) el).num, ring),
                helpFullFactor(((Fraction) el).denom, ring)
            });
        } else {
            return el;
        }
    }

    
        private Element helpFullFactor(Element el, Ring ring) {
        if (el instanceof Fraction) {
            return helpFullExpand(new F(DIVIDE, new Element[] {((Fraction) el).num, ((Fraction) el).denom}), ring);
        }
        if (el instanceof F) {    
                    Element[] NewArg = new Element[((F) el).X.length];
                    for (int i = 0; i < NewArg.length; i++) {                      
                        NewArg[i] = helpFullFactor(((F) el).X[i], ring);
                    }
                    return new F(((F) el).name, NewArg).Factor(false, ring);
            }
        return el;
    }
    
    
    
    @Override
    public Element EXPAND(Ring ring) {
        Element res = helpFullExpand(this, ring);
        return res.expand(ring);
    }
 
    private Element helpFullExpand(Element el, Ring ring) {
        if (el instanceof Fraction) {
            return helpFullExpand(new F(DIVIDE, new Element[] {((Fraction) el).num, ((Fraction) el).denom}), ring);
        }
        if (el instanceof F) {
            Element arg;
            switch (((F) el).name) {
                case SIN:
                case COS:
                case TG:
                case CTG:
                    arg = helpFullExpand(((F) el).X[0], ring);
                    return   null; //  new TrigonometricExpand().ExpandTr(new F(((F) el).name, arg), ring);
                case LN:
                case LG:
                    arg = helpFullExpand(X[0], ring);
                    return null; //new LN_EXP_POW_LOG_LG().expandLog(new F(name, arg), ring);
                case LOG:
                    return null; // helpFullExpand(new LN_EXP_POW_LOG_LG().expandLog(this, ring), ring);
                default:
                    Element[] NewArg = new Element[((F) el).X.length];
                    for (int i = 0; i < NewArg.length; i++) {
                        NewArg[i] = helpFullExpand(((F) el).X[i], ring);
                    }
                    return new F(((F) el).name, NewArg);
            }
        }
        return el;
    }

    @Override
    public boolean isComplex(Ring ring) {
        return isComplexHelp(this, ring);
    }

    public boolean isComplexHelp(Element el, Ring ring) {
        if (el instanceof F) {
            for (Element e : ((F) el).X) {
                if (isComplexHelp(e, ring)) {
                    return true;
                }
            }
        } else {
            return el.isComplex(ring);
        }
        return false;
    }

    public Element round(Ring ring) {
        Element ee = ExpandFnameOrId();
        if ((ee instanceof F) && (((F) ee).name == F.DIVIDE)) {
            return new Fraction(X[0], X[1]).round(ring);
        }
        return (ee instanceof F) ? this : ee.round(ring);
    }

    public Element ceil(Ring ring) {
        Element ee = ExpandFnameOrId();
        if ((ee instanceof F) && (((F) ee).name == F.DIVIDE)) {
            return new Fraction(X[0], X[1]).ceil(ring);
        }
        return (ee instanceof F) ? this : ee.ceil(ring);
    }

    public Element floor(Ring ring) {
        Element ee = ExpandFnameOrId();
        if ((ee instanceof F) && (((F) ee).name == F.DIVIDE)) {
            return new Fraction(X[0], X[1]).floor(ring);
        }
        return (ee instanceof F) ? this : ee.floor(ring);
    }

}
