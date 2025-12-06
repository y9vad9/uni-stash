# Лабораторна робота №7

## Контрольні завдання

### 1

Напишiть програму для пересилання масиву об’єктiв iз
процесора номер 1 iншим процесорам групи за допомогою
процедур sendArrayOfObjects i recvArrayOfObjects.
Протестуйте програму на 4, 8, 12 процесорах.

```bash
gradle lab7:runMpiTask1Np4
gradle lab7:runMpiTask1Np8
gradle lab7:runMpiTask1Np12
```

> Примітка: якщо вказана кількість ядер недоступна, таска не буде існувати.

https://github.com/user-attachments/assets/962d67e8-e5b9-414a-aa32-d73e89ffe259

### 2

Напишiть програму для пересилання масиву об’єктiв iз
процесора номер 3 iншим процесорам групи за допомогою
процедури bcastObject. Протестуйте програму на 4, 8, 12
процесорах.

```bash
gradle lab7:runMpiTask2Np4
gradle lab7:runMpiTask2Np8
gradle lab7:runMpiTask2Np12
```

> Примітка: якщо вказана кількість ядер недоступна, таска не буде існувати.

https://github.com/user-attachments/assets/21e41687-7895-4e7f-ac7b-00406a20651f
