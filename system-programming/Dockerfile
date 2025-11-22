# Базовий образ Ubuntu 24.04
FROM ubuntu:24.04

# Встановлюємо інструменти для компіляції та git
RUN apt-get update && \
    apt-get install -y build-essential git && \
    rm -rf /var/lib/apt/lists/*  # очищаємо кеш apt для зменшення розміру образу

# Встановлюємо робочу директорію всередині контейнера
WORKDIR /work

# Копіюємо весь проект у контейнер
COPY . /work

# Компіліруємо всі лабораторні за допомогою Makefile
RUN make

# Відкриваємо інтерактивну оболонку при запуску контейнера
CMD ["bash"]
