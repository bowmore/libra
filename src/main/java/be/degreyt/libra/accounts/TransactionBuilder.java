package be.degreyt.libra.accounts;

import be.degreyt.libra.time.Day;

import java.math.BigDecimal;

public interface TransactionBuilder {

    TransactionBuilder addMutation(Mutation mutation);

    TransactionBuilder day(Day day);

    boolean isBalanced();

    Transaction build();

    MutationBuilder credit();

    MutationBuilder debit();

    public static interface MutationBuilder {

        MutationBuilder account(Account account);

        TransactionBuilder forAmount(BigDecimal amount);
    }
}
