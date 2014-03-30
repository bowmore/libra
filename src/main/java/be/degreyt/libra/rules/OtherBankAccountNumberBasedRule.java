package be.degreyt.libra.rules;

import be.degreyt.libra.accounts.Account;
import be.degreyt.libra.accounts.AccountNumber;
import be.degreyt.libra.accounts.Ledger;
import be.degreyt.libra.parsers.BankAccountNumber;
import java.util.Optional;

import static be.degreyt.old.api.BankAccountNumber.isBankAccountNumber;

public class OtherBankAccountNumberBasedRule implements AccountResolutionRule {

    private final BankAccountNumber bankAccountNumber;
    private final AccountNumber account;

    public OtherBankAccountNumberBasedRule(BankAccountNumber bankAccountNumber, AccountNumber account) {
        this.bankAccountNumber = bankAccountNumber;
        this.account = account;
    }

    @Override
    public Optional<Account> resolve(Ledger ledger, BankTransactionData bankTransactionData) {
        if (matches(bankTransactionData.otherParty())) {
            return ledger.getRoot().findChild(account);
        }
        return Optional.empty();
    }

    private boolean matches(String otherParty) {
        return isBankAccountNumber(otherParty.trim()) && bankAccountNumber.equals(new BankAccountNumber(otherParty.trim()));
    }

    public BankAccountNumber getBankAccountNumber() {
        return bankAccountNumber;
    }

}
