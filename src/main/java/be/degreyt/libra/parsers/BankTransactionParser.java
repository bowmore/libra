package be.degreyt.libra.parsers;

import be.degreyt.libra.rules.BankTransactionData;

public interface BankTransactionParser {

    public BankTransactionData parse(String rawData);

    public boolean canParse(String rawData);

}
