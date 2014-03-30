package be.degreyt.libra.pair;

public class FullInterval<T extends Comparable<? super T>> implements Interval<T> {
    public boolean contains(T element) {
        return element != null;
    }

    public Bound<T> lowerBound() {
        return null;
    }

    public Bound<T> upperBound() {
        return null;
    }
}
