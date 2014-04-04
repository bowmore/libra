package be.degreyt.libra.accounts.impl;

import be.degreyt.libra.accounts.Account;
import be.degreyt.libra.accounts.Mutation;
import be.degreyt.libra.money.Saldo;

class MutationImpl implements Mutation {

    private final Account account;
    private final Saldo saldo;

    public MutationImpl(Account account, Saldo saldo) {
        this.account = account;
        this.saldo = saldo;
    }

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public Saldo getSaldo() {
        return saldo;
    }
}
