package be.degreyt.libra.util;

import java.util.Optional;

public class SimpleHolder<T> implements Holder<T> {
    private T element;

    public SimpleHolder(T element) {
        this.element = element;
    }

    public void setElement(T element) {
        this.element = element;
    }

    @Override
    public Optional<T> get() {
        return Optional.of(element);
    }
}
