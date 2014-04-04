package be.degreyt.libra.accounts.impl;

import be.degreyt.libra.accounts.TransactionNumber;

import java.util.UUID;

class TransactionNumberGeneratorImpl implements TransactionNumberGenerator {
    @Override
    public TransactionNumber next() {
        return new TransactionNumber(UUID.randomUUID().getLeastSignificantBits());
    }
}
