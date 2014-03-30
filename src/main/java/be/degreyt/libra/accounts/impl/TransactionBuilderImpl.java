package be.degreyt.libra.accounts.impl;

import be.degreyt.libra.accounts.Mutation;
import be.degreyt.libra.accounts.Transaction;
import be.degreyt.libra.accounts.TransactionBuilder;
import be.degreyt.libra.money.Saldo;
import be.degreyt.libra.time.Day;
import be.degreyt.libra.util.Holder;
import be.degreyt.libra.util.NonNullHolder;
import java.util.Optional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TransactionBuilderImpl implements TransactionBuilder {

    private final Set<Mutation> mutations = new LinkedHashSet<>();
    private final NonNullHolder<Day> dayHolder;

    public TransactionBuilderImpl(Day day) {
        this.dayHolder = new NonNullHolder(day);
    }

    @Override
    public TransactionBuilder addMutation(Mutation mutation) {
        mutations.add(mutation);
        return this;
    }

    @Override
    public TransactionBuilder day(Day day) {
        this.dayHolder.setElement(day);
        return this;
    }

    @Override
    public boolean isBalanced() {
        Saldo saldo = null;
        for (Mutation mutation : mutations) {
            saldo = saldo == null ? mutation.getSaldo() : saldo.add(mutation.getSaldo());
        }
        return false;
    }

    @Override
    public Transaction build() {
        Optional<Day> foundDay = dayHolder.get();
        if (foundDay.isPresent()) {
            return new TransactionImpl(new TransactionNumberGeneratorImpl(), foundDay.get(), mutations, "");
        }
        throw new NoDaySpecifiedException();
    }
}
