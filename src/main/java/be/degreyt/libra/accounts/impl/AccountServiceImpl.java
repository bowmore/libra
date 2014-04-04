package be.degreyt.libra.accounts.impl;

import be.degreyt.libra.accounts.Account;
import be.degreyt.libra.accounts.AccountNumber;
import be.degreyt.libra.accounts.AccountService;
import be.degreyt.libra.accounts.TransactionBuilder;
import be.degreyt.libra.time.Day;

import java.util.Currency;

public class AccountServiceImpl implements AccountService {

    private RootAccount rootAccount;

    @Override
    public Account getRootAccount() {
        return rootAccount;
    }

    @Override
    public Account createRootAccount(Currency currency, String name) {
        if (rootAccount != null) {
            throw new IllegalStateException();
        }
        rootAccount = new RootAccount(AccountNumber.of("000000"), name, currency);
        return rootAccount;
    }

    @Override
    public TransactionBuilder transactionOn(Day day) {
        return new TransactionBuilderImpl(day, getRootAccount().getCurrency());
    }
}
