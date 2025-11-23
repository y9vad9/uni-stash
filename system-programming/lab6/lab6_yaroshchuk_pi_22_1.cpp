#include <iostream>
#include <cmath>
#include <string>

using namespace std;

// Завдання 2
double fahrenheit_to_celsius(double f);
double fahrenheit_to_kelvin(double f);

// Завдання 3
void print_guest_list(const string guest_list[], size_t guest_list_size);
void clear_guest_list(string guest_list[], size_t guest_list_size);

int sum_of_digits(int n); // Завдання 4

int main()
{

  // ============================
  //         ЗАВДАННЯ 1
  // ============================
  double bill_total;
  int number_of_guests;

  cout << "Enter bill total: ";
  cin >> bill_total;

  cout << "Enter number of guests: ";
  cin >> number_of_guests;

  double individual_bill = bill_total / number_of_guests;

  double individual_bill_1 = floor(individual_bill);
  double individual_bill_2 = round(individual_bill);
  double individual_bill_3 = ceil(individual_bill);

  cout << "\nLocation 1 (floor): " << individual_bill_1 << endl;
  cout << "Location 2 (round): " << individual_bill_2 << endl;
  cout << "Location 3 (ceil): " << individual_bill_3 << endl;

  // ============================
  //         ЗАВДАННЯ 2
  // ============================

  double temp_f;
  cout << "\nEnter temperature in Fahrenheit: ";
  cin >> temp_f;

  cout << "Celsius: " << fahrenheit_to_celsius(temp_f) << endl;
  cout << "Kelvin: " << fahrenheit_to_kelvin(temp_f) << endl;

  // ============================
  //         ЗАВДАННЯ 3
  // ============================
  cout << "\nGuest list exercise:\n";

  string guest_list[]{"Alice", "Bob", "Charlie", "Diana"};
  size_t guest_list_size = 4;

  print_guest_list(guest_list, guest_list_size);
  clear_guest_list(guest_list, guest_list_size);
  print_guest_list(guest_list, guest_list_size);

  // ============================
  //         ЗАВДАННЯ 4
  // ============================
  int n;
  cout << "\nEnter number for digit sum: ";
  cin >> n;

  cout << "Sum of digits: " << sum_of_digits(n) << endl;

  return 0;
}

// Завдання 2
double fahrenheit_to_celsius(double f)
{
  return (f - 32.0) * 5.0 / 9.0;
}

double fahrenheit_to_kelvin(double f)
{
  return (f - 32.0) * 5.0 / 9.0 + 273.15;
}

// Завдання 3
void print_guest_list(const string guest_list[], size_t guest_list_size)
{
  for (size_t i = 0; i < guest_list_size; i++)
  {
    cout << guest_list[i] << endl;
  }
}

void clear_guest_list(string guest_list[], size_t guest_list_size)
{
  for (size_t i = 0; i < guest_list_size; i++)
  {
    guest_list[i] = " ";
  }
}

// Завдання 4 (рекурсія)
int sum_of_digits(int n)
{
  if (n < 10)
    return n;
  return n % 10 + sum_of_digits(n / 10);
}
