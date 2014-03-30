package be.degreyt.libra.accounts.impl;

import be.degreyt.libra.accounts.*;
import be.degreyt.libra.money.Saldo;
import be.degreyt.libra.time.Day;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Set;

public class TransactionImpl implements Transaction {

    private final Day day;
    private final TransactionNumber number;
    private final Set<ValidatedMutation> mutations;
    private final String documentReferral;

    public TransactionImpl(TransactionNumberGenerator generator, Day day, Set<Mutation> mutations, String documentReferral) {
        this.documentReferral = documentReferral;
        Saldo saldo = null;
        for (Mutation mutation : mutations) {
            saldo = saldo == null ? mutation.getSaldo() : saldo.add(mutation.getSaldo());
        }
        if (saldo == null || !saldo.isBalanced()) {
            throw new IllegalTransactionException();
        }
        ImmutableSet.Builder<ValidatedMutation> builder = ImmutableSet.builder();
        for (Mutation mutation : mutations) {
            builder.add(new ValidatedMutationImpl(mutation));
        }
        this.mutations = builder.build();
        number = generator.next();
        this.day = day;
    }

    @Override
    public Day getDay() {
        return day;
    }

    @Override
    public TransactionNumber getSequenceNumber() {
        return number;
    }

    @Override
    public Set<ValidatedMutation> getMutations() {
        return mutations;
    }

    @Override
    public String getDocumentReferral() {
        return documentReferral;
    }

    @Override
    public void execute(Account rootAccount) {
        for (ValidatedMutation mutation : mutations) {
            mutation.execute();
        }
    }
}
