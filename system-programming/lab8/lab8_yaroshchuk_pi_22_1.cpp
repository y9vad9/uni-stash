#include <iostream>
#include <string>

using namespace std;

class Cat {
public:
    std::string name;
    int age;
};

class Dog {
private:
    std::string name;
    int age;

public:
    // Exercise 2: Getter and Setter methods
    std::string get_name() const {
        return name;
    }

    void set_name(const std::string& new_name) {
        name = new_name;
    }

    int get_age() const {
        return age;
    }

    void set_age(int new_age) {
        age = new_age;
    }

    // Exercise 3: Additional methods
    int get_human_years() const {
        return age * 7;
    }

    std::string speak() const {
        return "Woof";
    }
};

// Exercise 1: Function to create a Cat object
Cat test_cat() {
    Cat fluffy;
    fluffy.name = "Fluffy";
    fluffy.age = 5;
    return fluffy;
}

int main() {
    // Exercise 1
    Cat my_cat = test_cat();
    cout << "Cat's name: " << my_cat.name << ", age: " << my_cat.age << endl;

    // Exercise 2 & 3
    Dog spot;
    spot.set_name("Spot");
    spot.set_age(7);

    cout << "Dog's name: " << spot.get_name() << ", age: " << spot.get_age() << endl;
    cout << "Dog's age in human years: " << spot.get_human_years() << endl;
    cout << "Dog says: " << spot.speak() << endl;

    return 0;
}
