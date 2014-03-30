package be.degreyt.libra.comparable;

/**
 *
 */
public class Max {

    @SafeVarargs
    public static <T extends Comparable<? super T>> T of(T... elements) {
        T max = null;
        for (T element : elements) {
            if (max == null || max.compareTo(element) < 0) {
                max = element;
            }
        }
        return max;
    }
}
