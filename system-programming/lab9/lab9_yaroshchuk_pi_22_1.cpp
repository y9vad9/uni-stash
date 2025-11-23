#include <iostream>
#include <string>

using namespace std;

class Dog {
private:
    std::string name;
    int age;
public:
    // Exercise 1 – Constructor
    Dog(const std::string& dog_name, int dog_age) : name(dog_name), age(dog_age) {}

    // Exercise 2 – Copy Constructor
    Dog(const Dog& other) : name(other.name), age(other.age) {
        cout << "Copy Constructor" << endl;
    }

    std::string get_name() const { return name; }
    int get_age() const { return age; }
};

int main() {
    // Exercise 1
    Dog fido {"Fido", 4};
    Dog spot {"Buddy", 7};    

    cout << fido.get_name() << " " << fido.get_age() << endl;
    cout << spot.get_name() << " " << spot.get_age() << endl;

    // Exercise 2   
    Dog spot2 {"Spot", 5};
    Dog twin {spot2};  

    cout << twin.get_name() << " " << twin.get_age() << endl;

    return 0;
}
