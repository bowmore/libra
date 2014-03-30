package be.degreyt.libra.pair;

public interface Interval<T extends Comparable<? super T>> {

    /**
     * Verifies whether the given element falls within this interval.
     * @param element
     * @return true if element is not null and it is contained in this interval, false otherwise.
     */
    boolean contains(T element);

    /**
     * @return the lower bound if any, null if the interval is open ended on the lower side.
     */
    Bound<T> lowerBound();

    /**
     * @return the upper bound if any, null if the interval is open ended on the upper side.
     */
    Bound<T> upperBound();

}