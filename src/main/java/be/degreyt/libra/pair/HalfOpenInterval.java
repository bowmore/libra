package be.degreyt.libra.pair;

public class HalfOpenInterval<T extends Comparable<? super T>> implements Interval<T> {

    private final Bound<T> bound;
    private final boolean upperBound;

    private HalfOpenInterval(Bound<T> bound, boolean upperBound) {
        this.bound = bound;
        this.upperBound = upperBound;
    }

    public static <S extends Comparable<? super S>> HalfOpenInterval<S> above(Bound<S> bound) {
        return new HalfOpenInterval<S>(bound, false);
    }

    public static <S extends Comparable<? super S>> HalfOpenInterval<S> below(Bound<S> bound) {
        return new HalfOpenInterval<S>(bound, true);
    }

    public boolean contains(T element) {
        if (element == null) {
            return false;
        }
        int boundComparedToElement = bound.getValue().compareTo(element);
        if (boundComparedToElement == 0) {
            return bound.isInclusive();
        }
        return upperBound ? boundComparedToElement > 0 : boundComparedToElement < 0;
    }

    public Bound<T> lowerBound() {
        return upperBound ? null : bound;
    }

    public Bound<T> upperBound() {
        return upperBound ? bound : null;
    }
}
