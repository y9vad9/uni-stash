#include <iostream>
using namespace std;

// ======================
// Завдання 1
// ======================
void swap_pointers(int* ptr1, int* ptr2) {
    // без тимчасової змінної (арифметичний трюк)
    *ptr1 = *ptr1 + *ptr2;
    *ptr2 = *ptr1 - *ptr2;
    *ptr1 = *ptr1 - *ptr2;
}

// ======================
// Завдання 2
// ======================
void multiply_with_pointer(int* ptr, int multiplier) {
    *ptr = (*ptr) * multiplier;
}

// ======================
// Завдання 3
// ======================
int findMaxElement(int* arr, int size) {
    int* current = arr;
    int maxValue = *current;

    for (int i = 1; i < size; i++) {
        current++;
        if (*current > maxValue) {
            maxValue = *current;
        }
    }
    return maxValue;
}

int main() {
    // Task 1
    int a = 5, b = 10;
    swap_pointers(&a, &b);

    cout << "After swap: a = " << a << ", b = " << b << endl;

    // Task 2
    int value = 7;
    multiply_with_pointer(&value, 3);
    cout << "After multiply: value = " << value << endl;

    // Task 3
    int arr[] = {12, 45, 67, 23, 9};
    int size = sizeof(arr) / sizeof(arr[0]);

    int maxElem = findMaxElement(arr, size);
    cout << "Max element: " << maxElem << endl;

    return 0;
}
