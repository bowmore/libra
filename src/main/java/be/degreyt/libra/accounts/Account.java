package be.degreyt.libra.accounts;


import be.degreyt.libra.money.Saldo;
import java.util.Optional;

import java.util.Currency;
import java.util.Set;

public interface Account {

    Saldo getSaldo();

    AccountNumber getNumber();

    String getName();

    Set<ValidatedMutation> getMutations();

    Currency getCurrency();

    Set<Account> getAccounts();

    Account addChild(AccountNumber number, String name);

    Optional<Account> getParentAccount();

    Mutation createMutation(Saldo saldo);

    void addMutation(ValidatedMutation mutation);

    Optional<Account> findChild(AccountNumber accountNumber);
}
