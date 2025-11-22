#include <iostream>
using namespace std;

int main() {
    // ======================
    // Завдання 1
    // ======================
    int age1;
    cout << "Enter age: ";
    cin >> age1;

    if (age1 >= 16) {
        cout << "Yes - you can drive!" << endl;
    }

    // ======================
    // Завдання 2
    // ======================
    int age;
    bool has_car;

    cout << "\nEnter your age: ";
    cin >> age;

    cout << "Do you own a car? (1 = yes, 0 = no): ";
    cin >> has_car;

    if (age < 16) {
        int years_left = 16 - age;
        cout << "Sorry, come back in " << years_left
             << " years and be sure you own a car when you come back." << endl;
    } else {
        if (!has_car) {
            cout << "Sorry, you need to buy a car before you can drive!" << endl;
        } else {
            cout << "Yes - you can drive!" << endl;
        }
    }

    // ======================
    // Завдання 3
    // ======================
    int day_code;
    cout << "\nEnter day code (0–6): ";
    cin >> day_code;

    switch (day_code) {
        case 0: cout << "Sunday" << endl; break;
        case 1: cout << "Monday" << endl; break;
        case 2: cout << "Tuesday" << endl; break;
        case 3: cout << "Wednesday" << endl; break;
        case 4: cout << "Thursday" << endl; break;
        case 5: cout << "Friday" << endl; break;
        case 6: cout << "Saturday" << endl; break;
        default: cout << "Error - illegal day code" << endl; break;
    }

    return 0;
}
