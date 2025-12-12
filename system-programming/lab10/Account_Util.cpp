#include <iostream>
#include "Account_Util.h"

// Відображає об'єкти Account у векторі об'єктів Account

void display(const std::vector<Account> &accounts) {

    std::cout << "\n=== Рахунки ==========================================" << std::endl;

    for (const auto &acc: accounts) 

        std::cout << acc << std::endl;

}

// Вносить задану суму на кожен об'єкт Account у векторі

void deposit(std::vector<Account> &accounts, double amount) {
    std::cout << "\n=== Внесення на рахунки ==========================================" << std::endl;
    for (auto &acc: accounts) {
        if (acc.deposit(amount))
            std::cout << "Внесено " << amount << " на " << acc << std::endl;
        else
            std::cout << "Не вдалося внести " << amount << " на " << acc << std::endl;
    }
}

void withdraw(std::vector<Account> &accounts, double amount) {
    std::cout << "\n=== Зняття з рахунків ==========================================" << std::endl;
    for (auto &acc: accounts) {
        if (acc.withdraw(amount))
            std::cout << "Знято " << amount << " з " << acc << std::endl;
        else
            std::cout << "Не вдалося зняти " << amount << " з " << acc << std::endl;
    }
}

void display(const std::vector<Savings_Account> &accounts) {
    std::cout << "\n=== Ощадні рахунки ====================================" << std::endl;
    for (const auto &acc: accounts)
        std::cout << acc << std::endl;
}

void deposit(std::vector<Savings_Account> &accounts, double amount) {
    std::cout << "\n=== Внесення на ощадні рахунки ===============================" << std::endl;
    for (auto &acc: accounts) {
        if (acc.deposit(amount))
            std::cout << "Внесено " << amount << " на " << acc << std::endl;
        else
            std::cout << "Не вдалося внести " << amount << " на " << acc << std::endl;
    }
}

void withdraw(std::vector<Savings_Account> &accounts, double amount) {
    std::cout << "\n=== Зняття з ощадних рахунків ================================" << std::endl;
    for (auto &acc: accounts) {
        if (acc.withdraw(amount))
            std::cout << "Знято " << amount << " з " << acc << std::endl;
        else
            std::cout << "Не вдалося зняти " << amount << " з " << acc << std::endl;
    }
}
