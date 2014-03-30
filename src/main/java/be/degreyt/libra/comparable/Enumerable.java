package be.degreyt.libra.comparable;

import be.degreyt.libra.accounts.TransactionNumber;

/**
 * Created by Tom on 30/03/2014.
 */
public interface Enumerable<T> {
    TransactionNumber subsequent();

    TransactionNumber preceding();
}
