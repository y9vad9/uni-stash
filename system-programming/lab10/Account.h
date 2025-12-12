// Простий рахунок 
#ifndef _ACCOUNT_H_
#define _ACCOUNT_H_
#include <iostream>
#include <string>

class Account {
    friend std::ostream &operator<<(std::ostream &os, const Account &account);
private:   
    static constexpr const char *def_name = "Безіменний рахунок";
    static constexpr double def_balance = 0.0;
protected:
    std::string name;
    double balance;
public:
    virtual ~Account() = default;
    Account(std::string name = def_name, double balance = def_balance);
    // Account(std::string name = "Безіменний рахунок", double balance = 0.0);
    virtual bool deposit(double amount);
    virtual bool withdraw(double amount);
    double get_balance() const;
};
#endif