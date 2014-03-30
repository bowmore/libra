package be.degreyt.libra.pair;

public final class SingletonInterval<T extends Comparable<? super T>> implements Interval<T> {

    private final T element;

    public SingletonInterval(T element) {
        this.element = element;
    }

    public boolean contains(T element) {
        return element == null ? false : this.element.compareTo(element) == 0;
    }

    public Bound<T> lowerBound() {
        return Bound.inclusive(element);
    }

    public Bound<T> upperBound() {
        return Bound.inclusive(element);
    }
}
