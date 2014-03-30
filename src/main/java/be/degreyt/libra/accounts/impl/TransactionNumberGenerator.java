package be.degreyt.libra.accounts.impl;

import be.degreyt.libra.accounts.TransactionNumber;

public interface TransactionNumberGenerator {

    TransactionNumber next();
}
