package be.degreyt.libra.rules;

import be.degreyt.libra.accounts.Account;
import be.degreyt.libra.accounts.Ledger;
import be.degreyt.libra.accounts.impl.RootAccount;
import java.util.Optional;

public interface AccountResolutionRule {

    public Optional<Account> resolve(Ledger ledger, BankTransactionData bankTransactionData);
}
