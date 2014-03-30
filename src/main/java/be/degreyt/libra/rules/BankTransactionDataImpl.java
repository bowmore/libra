package be.degreyt.libra.rules;


import be.degreyt.libra.parsers.BankAccountNumber;
import be.degreyt.libra.time.Day;

import java.math.BigDecimal;
import java.util.Currency;

public class BankTransactionDataImpl implements BankTransactionData {

    private String transactionNumber;
    private Day executionDay;
    private Currency currency;
    private BigDecimal amount;
    private Day currencyDay;
    private BankAccountNumber bankAccountNumber;
    private String details;
    private String otherParty;

    public BankTransactionDataImpl() {
    }

    @Override
    public String transactionNumber() {
        return transactionNumber;
    }

    @Override
    public Day executionDay() {
        return executionDay;
    }

    @Override
    public Currency currency() {
        return currency;
    }

    @Override
    public BigDecimal amount() {
        return amount;
    }

    @Override
    public Day currencyDay() {
        return currencyDay;
    }

    @Override
    public BankAccountNumber bankAccountNumber() {
        return bankAccountNumber;
    }

    @Override
    public String details() {
        return details;
    }

    @Override
    public String otherParty() {
        return otherParty;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setBankAccountNumber(BankAccountNumber bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setCurrencyDay(Day currencyDay) {
        this.currencyDay = currencyDay;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setExecutionDay(Day executionDay) {
        this.executionDay = executionDay;
    }

    public void setOtherParty(String otherParty) {
        this.otherParty = otherParty;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }
}
