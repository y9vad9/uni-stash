#include <iostream>

int main() {
    // ======================
    //  Завдання 1
    // ======================
    int num1 = 13;
    int num2 = 0;

    num1 = 5;
    num2 = num1;

    std::cout << "[Task 1]\n";
    std::cout << "num1: " << num1 << "\n";
    std::cout << "num2: " << num2 << "\n\n";


    // ======================
    //  Завдання 2
    // ======================
    int original_number = 4;
    int number = original_number;

    std::cout << "[Task 2]\n";

    number = number * 2;
    std::cout << "After *2: " << number << "\n";

    number = number + 9;
    std::cout << "After +9: " << number << "\n";

    number = number - 3;
    std::cout << "After -3: " << number << "\n";

    number = number / 2;
    std::cout << "After /2: " << number << "\n";

    number = number - original_number;
    std::cout << "After -original_number: " << number << "\n";

    number = number % 3;
    std::cout << "After %3: " << number << "\n\n";


    // ======================
    //  Завдання 3
    // ======================
    int age = 18;
    bool parental_consent = false;
    bool ssn = true;
    bool accidents = false;

    bool age_ok = (age >= 18) || (age > 15 && parental_consent);
    bool hire = age_ok && ssn && !accidents;

    std::cout << "[Task 3]\n";
    if (hire) {
        std::cout << "You are hired!\n\n";
    } else {
        std::cout << "Sorry, we cannot hire you!\n\n";
    }


    // ======================
    //  Завдання 4
    // ======================
    std::cout << "[Task 4]\n";
    std::cout << "Enter sum in cents: ";

    int cents {};
    std::cin >> cents;

    int dollars = cents / 100;
    cents = cents % 100;

    int quarters = cents / 25;
    cents %= 25;

    int dimes = cents / 10;
    cents %= 10;

    int nickels = cents / 5;
    cents %= 5;

    int pennies = cents;

    std::cout << "You can get this amount with:\n";
    std::cout << "dollars: "  << dollars  << "\n";
    std::cout << "quarters: " << quarters << "\n";
    std::cout << "dimes: "    << dimes    << "\n";
    std::cout << "nickels: "  << nickels  << "\n";
    std::cout << "pennies: "  << pennies  << "\n";

    return 0;
}
