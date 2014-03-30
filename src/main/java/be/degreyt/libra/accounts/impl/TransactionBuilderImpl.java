package be.degreyt.libra.accounts.impl;

import be.degreyt.libra.accounts.Mutation;
import be.degreyt.libra.accounts.Transaction;
import be.degreyt.libra.accounts.TransactionBuilder;
import be.degreyt.libra.money.Saldo;
import be.degreyt.libra.time.Day;
import be.degreyt.libra.util.Holder;
import be.degreyt.libra.util.NonNullHolder;

import java.util.*;

public class TransactionBuilderImpl implements TransactionBuilder {

    private final Set<Mutation> mutations = new LinkedHashSet<>();
    private final NonNullHolder<Day> dayHolder;
    private final Currency currency;

    public TransactionBuilderImpl(Day day, Currency currency) {
        this.dayHolder = new NonNullHolder(day);
        this.currency = currency;
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
        return mutations.stream().
                map(Mutation::getSaldo).
                reduce(Saldo.zero(currency), (a, b) -> a.add(b)).
                isBalanced();
    }

    @Override
    public Transaction build() {
        Optional<Day> foundDay = dayHolder.get();
        return new TransactionImpl(new TransactionNumberGeneratorImpl(), foundDay.orElseThrow(NoDaySpecifiedException::new), mutations, "", currency);
    }
}
