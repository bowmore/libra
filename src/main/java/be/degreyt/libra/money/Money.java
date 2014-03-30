package be.degreyt.libra.money;


import be.degreyt.old.api.CurrencyFormatter;
import be.degreyt.old.api.CurrencyMismatchException;

import java.math.BigDecimal;
import java.util.Currency;

public final class Money implements Comparable<Money> {

    private final BigDecimal value;
    private final Currency currency;

    public Money(BigDecimal value, Currency currency) {
        if (value == null || currency == null) {
            throw new IllegalArgumentException();
        }
        this.currency = currency;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Money money = (Money) o;

        return currency.equals(money.currency) && value.equals(money.value);

    }

    @Override
    public int hashCode() {
        return 37 * value.hashCode() + currency.hashCode();
    }

    @Override
    public String toString() {
        return CurrencyFormatter.instance.get().forCurrency(currency).format(value);
    }

    public Money plus(Money money) {
        validateForArithmetic(money);
        return new Money(this.value.add(money.value), this.currency);
    }

    private void validateForArithmetic(Money money) {
        if (!this.currency.equals(money.currency)) {
            throw new CurrencyMismatchException();
        }
    }

    public Money minus(Money money) {
        validateForArithmetic(money);
        return new Money(this.value.subtract(money.value), this.currency);
    }

    public Money negated() {
        return new Money(this.value.negate(), this.currency);
    }

    @Override
    public int compareTo(Money o) {
        validateForArithmetic(o);
        return this.value.compareTo(o.value);
    }

    public boolean isZero() {
        return BigDecimal.ZERO.compareTo(value) == 0;
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO.setScale(currency.getDefaultFractionDigits()), currency);
    }
}
