# Лабораторна робота №1

## Короткий опис

Мета роботи – ознайомитися з використанням **MPI для Java**. Клас `HelloWorldParallel` запускається на кількох
процесорах, і кожен процес виводить свій номер та повідомлення "Hello World".

https://github.com/user-attachments/assets/f3f11f9a-6a02-439e-b808-49bd3cf3989f


## Завдання

Запустити клас з репозиторію:
[HelloWorldParallel.java](https://bitbucket.org/mathpar/dap01/src/master/src/main/java/com/mathpar/NAUKMA/examples/HelloWorldParallel.java)

## Налаштування процесів

За завданням визначено **16 процесів**:

```kotlin
mpi.runnables {
    create(/*...*/) {
        processes = 16
    }
}
```

* `mpi` доступна через **Gradle Convention Plugin**, який був написаний спеціально для цього проекту – [mpi-convention.gradle.kts](../build-conventions/src/main/kotlin/mpi-convention.gradle.kts).

## Як запустити

Виконати команду у модулі `lab1`:

```bash
gradle lab1:runMpiHelloWorldParallel
```

Це запустить програму на **4 процесах**, кожен з яких виведе свій номер.
