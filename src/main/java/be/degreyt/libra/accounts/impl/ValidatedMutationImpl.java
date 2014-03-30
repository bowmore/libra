package be.degreyt.libra.accounts.impl;

import be.degreyt.libra.accounts.Account;
import be.degreyt.libra.accounts.Mutation;
import be.degreyt.libra.accounts.ValidatedMutation;
import be.degreyt.libra.money.Saldo;

public class ValidatedMutationImpl implements ValidatedMutation {

    private final Mutation mutation;

    public ValidatedMutationImpl(Mutation mutation) {
        this.mutation = mutation;
    }

    @Override
    public Account getAccount() {
        return mutation.getAccount();
    }

    @Override
    public Saldo getSaldo() {
        return mutation.getSaldo();
    }

    @Override
    public void execute() {
        getAccount().addMutation(this);
    }
}
