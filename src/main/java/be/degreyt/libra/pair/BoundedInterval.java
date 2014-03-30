package be.degreyt.libra.pair;

/**
 * Interval implementation that has both en upper and lower bound.
 *
 * @param <T> type of the elements in the range
 */
public final class BoundedInterval<T extends Comparable<? super T>> implements Interval<T> {

    Pair<Bound<T>, Bound<T>> bounds;

    public BoundedInterval(Bound<T> lower, Bound<T> upper) {
        if (lower.getValue().compareTo(upper.getValue()) > 0) {
            throw new IllegalArgumentException();
        }
        if (lower.getValue().compareTo(upper.getValue()) == 0 && lower.isInclusive() != upper.isInclusive()) {
            throw new IllegalArgumentException();
        }
        bounds = new Pair<Bound<T>, Bound<T>>(lower, upper);
    }

    public boolean contains(T element) {
        if (element == null) {
            return false;
        }
        int lowerComparison = lowerComparison(element);
        int upperComparison = upperComparison(element);
        if (isBetweenBounds(lowerComparison, upperComparison)) {
            return true;
        }
        return bounds.getX().isInclusive() && lowerComparison == 0 || bounds.getY().isInclusive() && upperComparison == 0;
    }

    private boolean isBetweenBounds(int lowerComparison, int upperComparison) {
        return lowerComparison < 0 && upperComparison > 0;
    }

    private int upperComparison(T element) {
        return bounds.getY().getValue().compareTo(element);
    }

    private int lowerComparison(T element) {
        return bounds.getX().getValue().compareTo(element);
    }

    public Bound<T> lowerBound() {
        return bounds.getX();
    }

    public Bound<T> upperBound() {
        return bounds.getY();
    }

    @Override
    public String toString() {
        return (lowerBound().isInclusive() ? '[' : ']') + lowerBound().getValue().toString() + " - " + upperBound().getValue().toString() + (upperBound().isInclusive() ? ']' : '[');
    }
}
