#ifndef _TRUST_ACCOUNT_H_
#define _TRUST_ACCOUNT_H_

#include "Savings_Account.h"
#include <string>

class Trust_Account : public Savings_Account {
    friend std::ostream &operator<<(std::ostream &os, const Trust_Account &account);
private:
    static constexpr const char *def_name = "Безіменний довірчий рахунок";
    static constexpr double def_balance = 0.0;
    static constexpr double def_int_rate = 0.0;
    static constexpr double bonus_amount = 50.0;
    static constexpr double bonus_threshold = 5000.0;
    static constexpr int max_withdrawals = 3;
    static constexpr double max_withdraw_percent = 0.20;
protected:
    int num_withdrawals;
public:
    Trust_Account(std::string name = def_name, double balance = def_balance, double int_rate = def_int_rate);
    
    // Депозити на суму $5000.00 або більше отримають бонус у розмірі $50
    virtual bool deposit(double amount) override;
    
    // Дозволено лише 3 зняття, кожне з яких не може перевищувати 20% від балансу
    virtual bool withdraw(double amount) override;
};

#endif // _TRUST_ACCOUNT_H_
