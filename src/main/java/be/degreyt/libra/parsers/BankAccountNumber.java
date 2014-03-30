package be.degreyt.libra.parsers;

import be.degreyt.old.api.BankAccountFormatException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BankAccountNumber {

    private static final Pattern PARSE_PATTERN = Pattern.compile("([A-Za-z]{2}[0-9]{2})([0-9]{4})([0-9]{4})([0-9]{4})");

    private final String canonicalFormat;

    public BankAccountNumber(String number) {
        if (number == null) {
            throw new IllegalArgumentException();
        }
        String canonized = number.replaceAll("\\s", "");
        Matcher matcher = PARSE_PATTERN.matcher(canonized);
        if (matcher.matches()) {
            canonicalFormat = matcher.group(1) + ' ' + matcher.group(2) + ' ' + matcher.group(3) + ' ' + matcher.group(4);
        } else {
            throw new BankAccountFormatException();
        }
    }

    public static boolean isBankAccountNumber(String number) {
        String canonized = number.replaceAll("\\s", "");
        Matcher matcher = PARSE_PATTERN.matcher(canonized);
        return matcher.matches();
    }
    
    @Override
    public String toString() {
        return canonicalFormat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BankAccountNumber that = (BankAccountNumber) o;

        return canonicalFormat.equals(that.canonicalFormat);

    }

    @Override
    public int hashCode() {
        return canonicalFormat.hashCode();
    }
}
