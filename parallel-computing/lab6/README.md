# Лабораторна робота №6 — Колективні та розподілені операції в MPI

Модуль містить приклади з підручника та контрольні завдання, реалізовані мовою Java з використанням OpenMPI.
Усі запускані модулі описані через `mpi.runnables` і виконуються через `gradle lab6:runMpi<Name>`.

## Приклади з підручника

### TestAllReduce

Показує використання `allReduce` з операцією множення. Кожен процес формує масив, усі отримують однаковий результат.

#### Запуск

```bash
gradle lab6:runMpiTestAllReduce
```

https://github.com/user-attachments/assets/e74c49ef-3e9f-4556-8358-eb4184bfc289

### TestReduce

Використання `reduce` з операцією підсумовування. Результат збирається на процесі rank 0.

#### Запуск

```bash
gradle lab6:runMpiTestReduce
```

https://github.com/user-attachments/assets/b93f5271-4cd1-47bc-a488-945f4d223120

### TestReduceScatter

Поєднання підсумовування та розсилання частин результату. Застосовується `reduceScatter`.

#### Запуск

```bash
gradle lab6:runMpiTestReduceScatter
```

https://github.com/user-attachments/assets/b52a8a69-abf6-45a2-989a-037a344bf20d

### TestScan

Префіксна операція `scan`. Кожен процес отримує часткову суму попередніх.

#### Запуск

```bash
gradle lab6:runMpiTestScan
```

https://github.com/user-attachments/assets/38517b72-5546-49b7-8450-c96230aea965

## Контрольні завдання (оригінальні формулювання)

### 1

#### Завдання

Напишіть програму для пересилання масиву чисел з процесора
номер 2 іншим процесорам групи. Протестуйте програму на 4,
8, 12 процесорах.

#### Запуск

```bash
gradle lab6:runMpiTask1Np4
gradle lab6:runMpiTask1Np8
gradle lab6:runMpiTask1Np12
```

*Якщо в системі менше ядер, відповідна задача не буде створена. У моєму випадку, у мене лише 8 ядер, тож lab6:runMpiTask1Np12 таска недоступна.*

https://github.com/user-attachments/assets/8349fff7-e050-4d8e-9ac6-15064fc0b478

### 2

#### Завдання

Напишiть програму для збору масиву чисел з усiх процесорiв
на процесорi номер 1. Протестуйте програму на 4, 8, 12
процесорах.

#### Запуск

```bash
gradle lab6:runMpiTask2Np4
gradle lab6:runMpiTask2Np8
gradle lab6:runMpiTask2Np12
```

*Якщо в системі менше ядер, відповідна задача не буде створена. У моєму випадку, у мене лише 8 ядер, тож lab6:runMpiTask1Np12 таска недоступна.*

https://github.com/user-attachments/assets/a6cde61d-f93f-443f-9c39-55c93ccee288

### 3

#### Завдання

Напишiть програму для збору масиву чисел з усiх процесорiв
на процесорi номер 3. Причому процесор 0 пересилає п’ять
чисел, процесор 1 – десять чисел, процесор 2 – п’ятнадцять
чисел i так далi. Протестуйте програму на 4, 8, 12 процесорах.

#### Запуск

```bash
gradle lab6:runMpiTask3Np4
gradle lab6:runMpiTask3Np8
gradle lab6:runMpiTask3Np12
```

*Якщо в системі менше ядер, відповідна задача не буде створена. У моєму випадку, у мене лише 8 ядер, тож lab6:runMpiTask1Np12 таска недоступна.*

https://github.com/user-attachments/assets/cf1e7890-f3af-4d38-bfd5-8913e8d2dea4

### 4

#### Завдання

Напишiть програму для пересилання масиву чисел iз
процесора номер 2 iншим процесорам групи. Причому
процесор 0 повинен отримати одне число, процесор 1 – два
числа, процесор 2 – чотири числа i так далi. Протестуйте
програму на 4, 8, 12 процесорах.

#### Запуск

```bash
gradle lab6:runMpiTask4Np4
gradle lab6:runMpiTask4Np8
gradle lab6:runMpiTask4Np12
```

*Якщо в системі менше ядер, відповідна задача не буде створена. У моєму випадку, у мене лише 8 ядер, тож lab6:runMpiTask1Np12 таска недоступна.*

https://github.com/user-attachments/assets/8a346cbf-c11b-4d25-a3cd-1f6397b38f10



### 5

#### Завдання

Напишiть програму для визначення мiнiмального значення
з усiх значень процесорiв. Отримати значення на процесорi
номер 2. Протестуйте програму на 4, 8, 16 процесорах.

#### Запуск

```bash
gradle lab6:runMpiTask5Np4
gradle lab6:runMpiTask5Np8
gradle lab6:runMpiTask5Np16
```

*Якщо в системі менше ядер, відповідна задача не буде створена. У моєму випадку, у мене лише 8 ядер, тож lab6:runMpiTask1Np12 таска недоступна.*

https://github.com/user-attachments/assets/2616a0be-8f20-47b3-8d7e-869362b81aa5

### 6

#### Завдання

Напишiть програму для обчислення середнього значення
великого масиву чисел, використовуючи колективнi команди.

#### Запуск

```bash
gradle lab6:runMpiTask6Np4
gradle lab6:runMpiTask6Np8
gradle lab6:runMpiTask6Np12
```

*Якщо в системі менше ядер, відповідна задача не буде створена. У моєму випадку, у мене лише 8 ядер, тож lab6:runMpiTask1Np12 таска недоступна.*

https://github.com/user-attachments/assets/7c235d82-805a-4827-b391-036f64853269

________

* Np[число] відображає кількість процесорів, які будуть використані. Якщо на комп'ютері не доступно
  певна кількість ядер (наприклад, 12) – таска, наприклад, `gradle lab6:runMpiTask3Np12` не буде доступна.
