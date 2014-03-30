package be.degreyt.libra.accounts;

public interface Ledger {

    Account getRoot();

    Journal getJournal();
}
