#include <iostream>
#include <vector>
#include <iomanip>

int main() {
    // ======================
    // Завдання 1
    // ======================
    const double SMALL_ROOM_PRICE = 25.0;
    const double LARGE_ROOM_PRICE = 35.0;
    const double TAX_RATE = 0.06;
    const int VALID_DAYS = 30;

    int smallRooms, largeRooms;

    std::cout << "Кількість маленьких кімнат: ";
    std::cin >> smallRooms;

    std::cout << "Кількість великих кімнат: ";
    std::cin >> largeRooms;

    double cost = smallRooms * SMALL_ROOM_PRICE + largeRooms * LARGE_ROOM_PRICE;
    double tax = cost * TAX_RATE;
    double total = cost + tax;

    std::cout << "\nКалькуляція вартості послуги прибирання кімнат:\n";
    std::cout << "Кількість маленьких кімнат: " << smallRooms << "\n";
    std::cout << "Кількість великих кімнат: " << largeRooms << "\n";
    std::cout << "Ціна за маленьку кімнату: " << SMALL_ROOM_PRICE << "$\n";
    std::cout << "Ціна за велику кімнату: " << LARGE_ROOM_PRICE << "$\n";
    std::cout << "Вартість: " << cost << "$\n";
    std::cout << "Податок: " << tax << "$\n";
    std::cout << "===============================\n";
    std::cout << "Загальна вартість: " << total << "$\n";
    std::cout << "Дана калькуляція дійсна протягом " << VALID_DAYS << " днів\n\n";

    // ======================
    // Завдання 2
    // ======================
    std::vector<int> vector1;
    std::vector<int> vector2;

    vector1.push_back(10);
    vector1.push_back(20);

    std::cout << "Елементи vector1: ";
    for (auto val : vector1) std::cout << val << " ";
    std::cout << "\nРозмір vector1: " << vector1.size() << "\n\n";

    vector2.push_back(100);
    vector2.push_back(200);

    std::cout << "Елементи vector2: ";
    for (auto val : vector2) std::cout << val << " ";
    std::cout << "\nРозмір vector2: " << vector2.size() << "\n\n";

    // 2D-вектор з посиланнями
    std::vector<std::vector<int>*> vector_2d;
    vector_2d.push_back(&vector1);
    vector_2d.push_back(&vector2);

    std::cout << "Елементи vector_2d:\n";
    for (auto vec_ptr : vector_2d) {
        for (auto val : *vec_ptr) std::cout << val << " ";
        std::cout << "\n";
    }
    std::cout << "\n";

    vector1.at(0) = 1000;

    std::cout << "Елементи vector_2d після зміни vector1.at(0) = 1000:\n";
    for (auto vec_ptr : vector_2d) {
        for (auto val : *vec_ptr) std::cout << val << " ";
        std::cout << "\n";
    }
    std::cout << "\n";

    std::cout << "Елементи vector1 після зміни:\n";
    for (auto val : vector1) std::cout << val << " ";
    std::cout << "\n";

    return 0;
}
