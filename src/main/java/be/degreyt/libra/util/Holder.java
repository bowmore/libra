package be.degreyt.libra.util;

import java.util.Optional;

public interface Holder<T> {

    Optional<T> get();
}
