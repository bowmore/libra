package be.degreyt.libra.rules;

import be.degreyt.libra.accounts.Account;
import be.degreyt.libra.accounts.Ledger;
import java.util.Optional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CompositeRule implements AccountResolutionRule {

    private final List<AccountResolutionRule> rules;

    public CompositeRule(List<AccountResolutionRule> rules) {
        this.rules = new ArrayList<>(rules);
    }

    public CompositeRule(AccountResolutionRule... rules) {
        this.rules = new ArrayList<>(rules.length);
        Collections.addAll(this.rules, rules);
    }

    @Override
    public Optional<Account> resolve(Ledger ledger, BankTransactionData bankTransactionData) {
        for (AccountResolutionRule rule : rules) {
            Optional<Account> account = rule.resolve(ledger, bankTransactionData);
            if (account.isPresent()) {
                return account;
            }
        }
        return Optional.empty();
    }

    public void addRule(AccountResolutionRule rule) {
        rules.add(rule);
    }

    public void removeRule(AccountResolutionRule rule) {
        rules.remove(rule);
    }

    public List<AccountResolutionRule> getRules() {
        return Collections.unmodifiableList(rules);
    }
}
