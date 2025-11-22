#include <iostream>
#include <iomanip>
#include <string>

int main() {
    std::string name;
    int day, month, year;

    // Введення даних користувача
    std::cout << "Enter your name: ";
    std::getline(std::cin, name);

    std::cout << "Enter your birth date (dd mm yyyy): ";
    std::cin >> day >> month >> year;

    // Привітання
    std::cout << "\nNice to meet you, " << name << "!\n";
    std::cout << "Do you know that your birth date has different formats in different countries?\n";

    // Вивід дати у різних форматах
    std::cout << "-- Poland: " << year << "-" 
              << std::setw(2) << std::setfill('0') << month << "-"
              << std::setw(2) << std::setfill('0') << day << "\n";

    std::cout << "-- Finland: " << day << "." << month << "." << year << "\n";

    std::cout << "-- Spain: " << std::setw(2) << std::setfill('0') << day << "/" 
              << std::setw(2) << std::setfill('0') << month << "/" << year << "\n";

    std::cout << "-- USA: " << std::setw(2) << std::setfill('0') << month << "/" 
              << std::setw(2) << std::setfill('0') << day << "/" << year << "\n";

    std::cout << "-- Japan: " << year << "/" 
              << std::setw(2) << std::setfill('0') << month << "/" 
              << std::setw(2) << std::setfill('0') << day << "\n";

    return 0;
}
