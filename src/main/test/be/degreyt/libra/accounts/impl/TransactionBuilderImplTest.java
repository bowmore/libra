package be.degreyt.libra.accounts.impl;

import be.degreyt.libra.accounts.AccountNumber;
import be.degreyt.libra.accounts.Transaction;
import be.degreyt.libra.accounts.ValidatedMutation;
import be.degreyt.libra.money.DebitCredit;
import be.degreyt.libra.money.Money;
import be.degreyt.libra.money.Saldo;
import be.degreyt.libra.time.Day;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionBuilderImplTest {

    @Test
    public void testTransactionBuilderNormal() {
        Currency currency = Currency.getInstance("EUR");

        RootAccount root = new RootAccount(AccountNumber.of("000000"), "Root", currency);

        AccountImpl account1 = new AccountImpl(root, AccountNumber.of("000001"), "debitAccount");
        AccountImpl account2 = new AccountImpl(root, AccountNumber.of("000002"), "creditAccount");

        BigDecimal amount = BigDecimal.valueOf(2000, 2);
        Transaction transaction = new TransactionBuilderImpl(Day.of(2014, 3, 30), currency)
                .debit().account(account1).forAmount(amount)
                .credit().account(account2).forAmount(amount)
                .build();

        assertThat(transaction).isNotNull();
        assertThat(transaction.getMutations().stream().map(ValidatedMutation::getSaldo).reduce(Saldo.zero(currency), (a, b) -> a.add(b)).isBalanced()).isTrue();


    }
}
