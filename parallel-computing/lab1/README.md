# Лабораторна робота №1 – Hello World Parallel (MPI)

## Короткий опис

Мета роботи – ознайомитися з використанням **MPI для Java**. Клас `HelloWorldParallel` запускається на кількох
процесорах, і кожен процес виводить свій номер та повідомлення "Hello World".

https://github.com/user-attachments/assets/ad88122a-4b22-4d70-b4ec-3ae6d536c20e

## Завдання

Запустити клас з репозиторію:
[HelloWorldParallel.java](https://bitbucket.org/mathpar/dap01/src/master/src/main/java/com/mathpar/NAUKMA/examples/HelloWorldParallel.java)

## Налаштування процесів

За завданням визначено **4 процеси**:

```kotlin
mpi {
    processes = 4
}
```

* `mpi` доступна через **Gradle Convention Plugin**, який був написаний спеціально для цього проекту – [mpi-convention.gradle.kts](../build-conventions/src/main/kotlin/mpi-convention.gradle.kts).

## Як запустити

Виконати команду у модулі `lab1`:

```bash
gradle lab1:runMpi
```

Це запустить програму на **4 процесах**, кожен з яких виведе свій номер.

## Код прикладу

```java
package com.y9vad9.uni.openmpi.lab1;

import mpi.MPI;
import mpi.MPIException;

public class HelloWorldParallel {
    static void main(String[] args) throws MPIException {
        // Ініціалізація паралельної частини
        MPI.Init(args);

        // Визначення номера процесора
        int myRank = MPI.COMM_WORLD.getRank();
        System.out.println("Proc num " + myRank + " Hello World");

        // Завершення паралельної частини
        MPI.Finalize();
    }
}
```
