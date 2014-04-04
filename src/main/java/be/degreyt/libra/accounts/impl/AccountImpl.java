package be.degreyt.libra.accounts.impl;

import be.degreyt.libra.accounts.Account;
import be.degreyt.libra.accounts.AccountNumber;
import be.degreyt.libra.accounts.Mutation;
import be.degreyt.libra.money.Saldo;
import java.util.Optional;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Set;

class AccountImpl extends AbstractAccount {

    private final Account parent;

    public AccountImpl(Account parent, AccountNumber number, String name) {
        super(number, name);
        this.parent = parent;
    }

    @Override
    public Currency getCurrency() {
        return parent.getCurrency();
    }

    @Override
    public Optional<Account> getParentAccount() {
        return Optional.of(parent);
    }
}
