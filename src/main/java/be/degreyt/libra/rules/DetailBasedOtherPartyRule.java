package be.degreyt.libra.rules;

import be.degreyt.libra.accounts.Account;
import be.degreyt.libra.accounts.AccountNumber;
import be.degreyt.libra.accounts.Ledger;
import java.util.Optional;

public class DetailBasedOtherPartyRule implements AccountResolutionRule {

    private final String otherParty;
    private final String containedInDetail;
    private final AccountNumber accountNumber;

    public DetailBasedOtherPartyRule(String otherParty, String containedInDetail, AccountNumber accountNumber) {
        this.accountNumber = accountNumber;
        this.containedInDetail = containedInDetail;
        this.otherParty = otherParty;
    }

    @Override
    public Optional<Account> resolve(Ledger ledger, BankTransactionData bankTransactionData) {
        if (bankTransactionData.otherParty().contains(otherParty) && bankTransactionData.details().contains(containedInDetail)) {
            return ledger.getRoot().findChild(accountNumber);
        }
        return Optional.empty();
    }

    public String getOtherParty() {
        return otherParty;
    }

    public String getContainedInDetail() {
        return containedInDetail;
    }

}
