package be.degreyt.libra.accounts.impl;

import be.degreyt.libra.accounts.TransactionNumber;

interface TransactionNumberGenerator {

    TransactionNumber next();
}
