#ifndef _CHECKING_ACCOUNT_H_
#define _CHECKING_ACCOUNT_H_
#include "Account.h"

class Checking_Account: public Account {
    friend std::ostream &operator<<(std::ostream &os, const Checking_Account &account);
private:
    static constexpr const char *def_name = "Безіменний розрахунковий рахунок";
    static constexpr double def_balance = 0.0;
    static constexpr double withdrawal_fee = 1.50;
public:
    virtual ~Checking_Account() = default;
    Checking_Account(std::string name = def_name, double balance = def_balance);
    virtual bool withdraw(double amount) override;
    // Успадковує метод deposit від Account
};

#endif // _CHECKING_ACCOUNT_H_
