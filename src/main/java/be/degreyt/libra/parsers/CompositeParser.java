package be.degreyt.libra.parsers;


import be.degreyt.libra.rules.BankTransactionData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompositeParser implements BankTransactionParser {

    private final List<BankTransactionParser> parsers;

    public CompositeParser(BankTransactionParser... parsers) {
        this.parsers = new ArrayList<>(parsers.length);
        Collections.addAll(this.parsers, parsers);
    }

    @Override
    public BankTransactionData parse(String rawData) {
        for (BankTransactionParser parser : parsers) {
            if (parser.canParse(rawData)) {
                return parser.parse(rawData);
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public boolean canParse(String rawData) {
        for (BankTransactionParser parser : parsers) {
            if (parser.canParse(rawData)) {
                return true;
            }
        }
        return false;
    }
}
