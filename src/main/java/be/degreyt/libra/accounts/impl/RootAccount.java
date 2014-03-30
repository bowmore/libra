package be.degreyt.libra.accounts.impl;

import be.degreyt.libra.accounts.Account;
import be.degreyt.libra.accounts.AccountNumber;
import java.util.Optional;

import java.util.Currency;

public class RootAccount extends AbstractAccount {

    private final Currency currency;

    public RootAccount(AccountNumber number, String name, Currency currency) {
        super(number, name);
        this.currency = currency;
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public Optional<Account> getParentAccount() {
        return Optional.empty();
    }
}
