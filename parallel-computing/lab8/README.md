# Лабораторна робота 8

https://github.com/user-attachments/assets/afb9a15e-e068-45f1-897b-3d0f65973a4b

## Приклади з підручника

### MatrixMul4

Множення блоків матриць.
Запуск:

```bash
gradle lab8:runMpiMatrixMul4
```

### MatrixMul8

Множення блоків матриць.
Запуск:

```bash
gradle lab8:runMpiMatrixMul8
```

### MultiplyMatrixToVector

Множення матриці на вектор.
Запуск:

```bash
gradle lab8:runMpiMultiplyMatrixToVector
```

### MultiplyVectorToScalar

Множення вектора на скаляр.
Запуск:

```bash
gradle lab8:runMpiVectorToScalar
```

## Контрольні завдання

### Завдання 1

Напишiть паралельну програму обчислення норми матрицi, використовуючи колективнi команди. Протестуйте програму на 4, 8, 12 процесорах.

#### Запуск

```bash
gradle lab8:runMpiMatrixNormNp4 # 4 процесора
gradle lab8:runMpiMatrixNormNp8 # 8 процесорів
gradle lab8:runMpiMatrixNormNp12 # 12 процесорів
```

https://github.com/user-attachments/assets/0bcfac2b-94bf-4add-96c3-f47487d17376

### Завдання 2

Напишiть паралельну програму алгоритму множення за Штрассеном на 7 процесорах.

#### Запуск

```bash
gradle lab8:runMpiStrassenMul7
```


https://github.com/user-attachments/assets/347a270f-d2a4-454c-b0c7-43493f53239f


