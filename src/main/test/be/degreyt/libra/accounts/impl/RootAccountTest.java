package be.degreyt.libra.accounts.impl;

import be.degreyt.libra.accounts.AccountNumber;
import org.junit.Test;

import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Tom on 30/03/2014.
 */
public class RootAccountTest {


    @Test
    public void testGetCurrency() throws Exception {
        Currency currency = Currency.getInstance("EUR");

        RootAccount root = new RootAccount(AccountNumber.of("000000"), "Root", currency);

        assertThat(root.getCurrency()).isEqualTo(currency);
    }

    @Test
    public void testGetParentAccount() throws Exception {

    }
}
