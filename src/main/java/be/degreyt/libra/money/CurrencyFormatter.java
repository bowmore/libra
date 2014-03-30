package be.degreyt.libra.money;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Formatter;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Tom on 30/03/2014.
 */
public interface CurrencyFormatter {
    public static AtomicReference<CurrencyFormatter> instance = new AtomicReference<>();

    CurrencyFormatter forCurrency(Currency currency);

    String format(BigDecimal value);
}
