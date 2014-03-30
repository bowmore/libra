package be.degreyt.libra.rules;

import be.degreyt.libra.accounts.Account;
import be.degreyt.libra.accounts.AccountNumber;
import be.degreyt.libra.accounts.Ledger;
import be.degreyt.libra.parsers.BankAccountNumber;
import java.util.Optional;

public class BankAccountNumberBasedRule implements AccountResolutionRule {

    private final BankAccountNumber bankAccountNumber;
    private final AccountNumber accountNumber;

    public BankAccountNumberBasedRule(BankAccountNumber bankAccountNumber, AccountNumber accountNumber) {
        this.bankAccountNumber = bankAccountNumber;
        this.accountNumber = accountNumber;
    }

    @Override
    public Optional<Account> resolve(Ledger ledger, BankTransactionData bankTransactionData) {
        if (bankTransactionData.bankAccountNumber().equals(bankAccountNumber)) {
            return ledger.getRoot().findChild(accountNumber);
        }
        return Optional.empty();
    }

    public BankAccountNumber getBankAccountNumber() {
        return bankAccountNumber;
    }

}
