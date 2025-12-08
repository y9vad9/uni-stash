# Паралельні та розподілені обчислення

Цей модуль містить матеріали та код для предмету **"Паралельні та розподілені обчислення"** в **Відкритому міжнародному університеті розвитку людини "Україна"**.

Матеріали включають приклади, лабораторні роботи та експерименти, виконані протягом курсу.

## Встановлення OpenMPI 5.4 для Java

1. **Завантаження та розпакування**

   ```bash
   wget https://download.open-mpi.org/release/open-mpi/v5.0/openmpi-5.4.0.tar.gz
   tar -xzf openmpi-5.4.0.tar.gz
   cd openmpi-5.4.0
   ```

2. **Конфігурування з підтримкою Java**

   ```bash
   ./configure --prefix=/path/to/openmpi \
               --enable-mpi-java \
               --with-jdk-bindir=/path/to/jdk/bin \
               --with-jdk-headers=/path/to/jdk/include
   ```

3. **Компіляція та встановлення**

   ```bash
   make -j$(nproc)
   make install
   ```

4. **Перевірка встановлення**

   ```bash
   /path/to/openmpi/bin/mpijavac -version
   /path/to/openmpi/bin/mpirun --version
   ```

## Налаштування `local.properties`

Створіть файл `local.properties` у корені модуля (якщо його немає):

```
mpi.bin=/path/to/openmpi/bin
mpi.lib=/path/to/openmpi/lib
```
Актуально лише якщо встановлення відбулось в кастомну директорію.

Це дозволить Gradle Convention Plugin знайти потрібні шляхи для запуску MPI Java.

## Налаштування для запуску бінарних файлів

Щоб запустити, наприклад, [lab1/executables/HelloWorldParallel](lab1/executables/HelloWorldParallel) локально, потрібно (але не обов'язково) задати такі змінні оточення:

* **HOSTFILE** – необов’язково, використовується для емуляції суперкомп’ютера.
* **DYLD_LIBRARY_PATH** – на випадок, якщо потрібно вказати шлях до бібліотек OpenMPI:

  ```bash
  export DYLD_LIBRARY_PATH=/path/to/openmpi/lib:$DYLD_LIBRARY_PATH
  ```

> На macOS можуть виникати проблеми з динамічним лінкуванням через System Integrity Protection (SIP), коли перевизначення `$DYLD_LIBRARY_PATH` 
> може не спрацювати. Для обходу, найкраще всього скористатись програмою install_name_tool.
