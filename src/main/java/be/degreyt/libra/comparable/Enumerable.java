package be.degreyt.libra.comparable;

import be.degreyt.libra.accounts.TransactionNumber;

/**
 * Created by Tom on 30/03/2014.
 */
public interface Enumerable<T> {
    default TransactionNumber subsequent() {
        return new TransactionNumber(number + 1);
    }

    default TransactionNumber preceding() {
        return new TransactionNumber(number - 1);
    }
}
