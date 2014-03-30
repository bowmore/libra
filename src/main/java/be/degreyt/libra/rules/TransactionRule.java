package be.degreyt.libra.rules;

import be.degreyt.libra.accounts.Account;
import be.degreyt.libra.accounts.Ledger;
import be.degreyt.libra.accounts.Transaction;

public interface TransactionRule {

    Transaction convert(Ledger ledger, BankTransactionData bankTransactionData);
}
