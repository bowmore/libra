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
        return parsers.stream().
                filter(p -> p.canParse(rawData)).findFirst().
                orElseThrow(IllegalArgumentException::new).
                parse(rawData);
    }

    @Override
    public boolean canParse(String rawData) {
        return parsers.stream().anyMatch(p -> p.canParse(rawData));
    }
}
