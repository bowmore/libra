package be.degreyt.libra.parsers;

import be.degreyt.libra.rules.BankTransactionData;
import com.google.inject.Inject;

public class MultiParser implements BankTransactionParser {

    private final BankTransactionParser delegate;

    @Inject
    public MultiParser(FortisCsvParser fortisCsvParser, KbcCenteaCsvParser kbcCenteaCsvParser) {
        delegate = new CompositeParser(fortisCsvParser, kbcCenteaCsvParser);
    }

    @Override
    public BankTransactionData parse(String rawData) {
        return delegate.parse(rawData);
    }

    @Override
    public boolean canParse(String rawData) {
        return delegate.canParse(rawData);
    }
}
