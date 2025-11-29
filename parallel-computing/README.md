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

Це дозволить Gradle Convention Plugin знайти потрібні шляхи для запуску та компіляції MPI Java.
