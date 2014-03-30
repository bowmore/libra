package be.degreyt.libra.accounts;

import java.util.Set;

/**
 *
 */
public interface Journal {

    Set<Transaction> getTransactions();

    TransactionBuilder getTransactionBuilder();
}
