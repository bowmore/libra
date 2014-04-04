package be.degreyt.libra.accounts.impl;

import be.degreyt.libra.accounts.*;
import be.degreyt.libra.money.Saldo;
import be.degreyt.libra.time.Day;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Currency;
import java.util.Set;

class TransactionImpl implements Transaction {

    private final Day day;
    private final TransactionNumber number;
    private final Set<ValidatedMutation> mutations;
    private final String documentReferral;

    public TransactionImpl(TransactionNumberGenerator generator, Day day, Set<Mutation> mutations, String documentReferral, Currency currency) {
        this.documentReferral = documentReferral;
        Saldo saldo = mutations.stream().
                map(Mutation::getSaldo).
                reduce(Saldo.zero(currency), (a, b) -> a.add(b));
        if (saldo == null || !saldo.isBalanced()) {
            throw new IllegalTransactionException();
        }
        ImmutableSet.Builder<ValidatedMutation> builder = ImmutableSet.builder();
        mutations.stream().map(ValidatedMutationImpl::new).forEach(builder::add);
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
        mutations.forEach(ValidatedMutation::execute);
    }
}
