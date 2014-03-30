package be.degreyt.libra.pair;

public final class Bound<T extends Comparable<? super T>> {

    private final T value;
    private final boolean inclusive;

    private Bound(T value, boolean inclusive) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        this.value = value;
        this.inclusive = inclusive;
    }

    public static <S extends Comparable<? super S>> Bound<S> inclusive(S value) {
        return new Bound<S>(value, true);
    }

    public static <S extends Comparable<? super S>> Bound<S> exclusive(S value) {
        return new Bound<S>(value, false);
    }

    public T getValue() {
        return value;
    }

    public boolean isInclusive() {
        return inclusive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bound bound = (Bound) o;

        return inclusive == bound.inclusive && value.equals(bound.value);

    }

    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + (inclusive ? 1 : 0);
        return result;
    }
}
