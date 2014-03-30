package be.degreyt.libra.rules;

import be.degreyt.libra.parsers.BankAccountNumber;
import be.degreyt.libra.time.Day;

import java.math.BigDecimal;
import java.util.Currency;

public interface BankTransactionData {

    String transactionNumber();

    Day executionDay();

    Day currencyDay();

    BigDecimal amount();

    Currency currency();

    String otherParty();

    String details();

    BankAccountNumber bankAccountNumber();
}
