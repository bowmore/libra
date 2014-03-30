package be.degreyt.libra.accounts;

import java.util.regex.Pattern;

public final class AccountNumber implements Comparable<AccountNumber> {

    public static final Pattern VALID = Pattern.compile("\\d{6}");

    private final String value;

    public AccountNumber(String value) {
        if (!VALID.matcher(value).matches()) {
            throw new IllegalArgumentException();
        }
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountNumber that = (AccountNumber) o;

        return value.equals(that.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int compareTo(AccountNumber o) {
        return this.value.compareTo(o.value);
    }

    public static AccountNumber of(String s) {
        return new AccountNumber(s);
    }
}
