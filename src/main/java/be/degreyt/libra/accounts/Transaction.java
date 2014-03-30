package be.degreyt.libra.accounts;


import be.degreyt.libra.accounts.Mutation;
import be.degreyt.libra.time.Day;

import java.util.Set;

public interface Transaction {

    Day getDay();

    TransactionNumber getSequenceNumber();

    Set<ValidatedMutation> getMutations();

    String getDocumentReferral();

    void execute(Account rootAccount);
}
