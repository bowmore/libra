package be.degreyt.libra.accounts.impl;

import be.degreyt.libra.accounts.Account;
import be.degreyt.libra.accounts.AccountNumber;
import be.degreyt.libra.accounts.Mutation;
import be.degreyt.libra.accounts.ValidatedMutation;
import be.degreyt.libra.money.Saldo;
import java.util.Optional;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Set;

abstract class AbstractAccount implements Account {
    protected final AccountNumber number;
    protected final String name;
    private final List<ValidatedMutation> mutations = new ArrayList<>();
    private final List<Account> children = new ArrayList<>();

    public AbstractAccount(AccountNumber number, String name) {
        this.number = number;
        this.name = name;
    }

    @Override
    public Saldo getSaldo() {
        Saldo saldo = Saldo.zero(getCurrency());
        mutations.stream().map(ValidatedMutation::getSaldo).forEach(saldo::add);
        children.stream().map(Account::getSaldo).forEach(saldo::add);
        return saldo;
    }

    @Override
    public AccountNumber getNumber() {
        return number;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<ValidatedMutation> getMutations() {
        return ImmutableSet.copyOf(mutations);
    }

    @Override
    public abstract Currency getCurrency();

    @Override
    public Set<Account> getAccounts() {
        return ImmutableSet.copyOf(children);
    }

    @Override
    public Account addChild(AccountNumber number, String name) {
        AccountImpl account = new AccountImpl(this, number, name);
        children.add(account);
        return account;
    }

    @Override
    public Mutation createMutation(Saldo saldo) {
        return new MutationImpl(this, saldo);
    }

    @Override
    public void addMutation(ValidatedMutation mutation) {
        mutations.add(mutation);
    }

    @Override
    public Optional<Account> findChild(AccountNumber accountNumber) {

        children.stream().filter(c -> c.getNumber().equals(accountNumber)).findAny();

        for (Account child : children) {
            if (child.getNumber().equals(accountNumber)) {
                return Optional.of(child);
            }
        }
        for (Account child : children) {
            Optional<Account> found = child.findChild(accountNumber);
            if (found.isPresent()) {
                return found;
            }
        }
        return Optional.empty();
    }
}
