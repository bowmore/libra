package be.degreyt.libra.pair;

public final class EmptyInterval<T extends Comparable<? super T>> implements Interval<T> {

    private final T phantomBound;

    public EmptyInterval(T phantomBound) {
        if (phantomBound == null) {
            throw new IllegalArgumentException();
        }
        this.phantomBound = phantomBound;
    }

    public boolean contains(T element) {
        return false;
    }

    public Bound<T> lowerBound() {
        return Bound.exclusive(phantomBound);
    }

    public Bound<T> upperBound() {
        return Bound.exclusive(phantomBound);
    }
}
