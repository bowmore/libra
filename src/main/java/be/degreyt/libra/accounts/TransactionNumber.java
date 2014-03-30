package be.degreyt.libra.accounts;

import be.degreyt.old.numbering.Enumerable;

public final class TransactionNumber implements Comparable<TransactionNumber>, Enumerable<TransactionNumber> {

    private final long number;


    public TransactionNumber(long number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransactionNumber that = (TransactionNumber) o;

        return number == that.number;

    }

    @Override
    public int hashCode() {
        return (int) (number ^ (number >>> 32));
    }

    @Override
    public String toString() {
        return Long.toString(number);
    }

    @Override
    public int compareTo(TransactionNumber o) {
        return this.number == o.number ? 0 : (this.number > o.number ? 1 : -1);
    }

    @Override
    public TransactionNumber subsequent() {
        return new TransactionNumber(number + 1);
    }

    @Override
    public TransactionNumber preceding() {
        return new TransactionNumber(number - 1);
    }

    public long toLong() {
        return number;
    }
}
