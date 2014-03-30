package be.degreyt.libra.util;

import be.degreyt.libra.time.Day;
import java.util.Optional;
import com.google.inject.internal.util.$StackTraceElements;

import java.util.Objects;

public class NonNullHolder<T> implements Holder<T> {
    private T element;

    public NonNullHolder(T element) {
        this.element = Objects.requireNonNull(element);
    }

    public void setElement(T element) {
        this.element = Objects.requireNonNull(element);
    }

    @Override
    public Optional<T> get() {
        return Optional.of(element);
    }
}
