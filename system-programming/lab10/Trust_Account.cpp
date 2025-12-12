#include "Trust_Account.h"

Trust_Account::Trust_Account(std::string name, double balance, double int_rate)
    : Savings_Account{name, balance, int_rate}, num_withdrawals{0} {
}

// Депозит:
//      Сума, що вноситься, буде збільшена на (сума * int_rate/100)
//      і потім оновлена сума буде внесена на рахунок.
//      Якщо депозит >= $5000.00, нараховується бонус у розмірі $50.
bool Trust_Account::deposit(double amount) {
    if (amount >= bonus_threshold)
        amount += bonus_amount;
    return Savings_Account::deposit(amount);
}

// Зняття:
//      Дозволяється лише 3 зняття на рік, кожне на суму менше 20% від залишку.
bool Trust_Account::withdraw(double amount) {
    if (num_withdrawals >= max_withdrawals || (amount > balance * max_withdraw_percent)) {
        return false;
    } else {
        num_withdrawals++;
        return Savings_Account::withdraw(amount);
    }
}

std::ostream &operator<<(std::ostream &os, const Trust_Account &account) {
    os << "[Довірчий рахунок: " << account.name << ": " << account.balance << ", " << account.int_rate << "%, зняття: " << account.num_withdrawals << "]";
    return os;
}
