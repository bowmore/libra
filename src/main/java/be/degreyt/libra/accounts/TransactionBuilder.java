package be.degreyt.libra.accounts;

import be.degreyt.libra.time.Day;

public interface TransactionBuilder {

    TransactionBuilder addMutation(Mutation mutation);

    TransactionBuilder day(Day day);

    boolean isBalanced();

    Transaction build();
}
