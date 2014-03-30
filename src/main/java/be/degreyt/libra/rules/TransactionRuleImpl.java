package be.degreyt.libra.rules;

import be.degreyt.libra.accounts.Account;
import be.degreyt.libra.accounts.Ledger;
import be.degreyt.libra.accounts.Transaction;
import be.degreyt.libra.accounts.impl.TransactionBuilderImpl;
import be.degreyt.libra.money.DebitCredit;
import be.degreyt.libra.money.Money;
import be.degreyt.libra.money.Saldo;

import java.math.BigDecimal;

public class TransactionRuleImpl implements TransactionRule {

    private AccountResolutionRule ownAccountRule;

    private AccountResolutionRule otherAccountRule;

    @Override
    public Transaction convert(Ledger ledger, BankTransactionData bankTransactionData) {
        BigDecimal value = bankTransactionData.amount();
        DebitCredit debitCredit = (value.compareTo(BigDecimal.ZERO) < 0) ? DebitCredit.Debit : DebitCredit.Credit;
        Saldo saldo = new Saldo(debitCredit, new Money(value, ledger.getRoot().getCurrency()));

        Account ownAccount = ownAccountRule.resolve(ledger, bankTransactionData).get(); // TODO nullsafe
        Account otherAccount = otherAccountRule.resolve(ledger, bankTransactionData).get();

        Transaction transaction = ledger.getJournal().getTransactionBuilder()
                .day(bankTransactionData.executionDay())
                .addMutation(ownAccount.createMutation(saldo))
                .addMutation(otherAccount.createMutation(saldo.inverted()))
                .build();

        return transaction;
    }
}
