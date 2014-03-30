package be.degreyt.libra.accounts.impl;

import be.degreyt.libra.accounts.AccountNumber;
import org.junit.Test;

import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Tom on 30/03/2014.
 */
public class AccountImplTest {

    @Test
    public void testGetCurrency() {
        Currency currency = Currency.getInstance("EUR");

        RootAccount root = new RootAccount(AccountNumber.of("000000"), "Root", currency);

        AccountImpl account = new AccountImpl(root, AccountNumber.of("000001"), "myAccount");

        assertThat(account.getCurrency()).isEqualTo(currency);
    }

    @Test
    public void testGetParent() {
        Currency currency = Currency.getInstance("EUR");

        RootAccount root = new RootAccount(AccountNumber.of("000000"), "Root", currency);

        AccountImpl account = new AccountImpl(root, AccountNumber.of("000001"), "myAccount");

        assertThat(account.getParentAccount().isPresent()).isTrue();
        assertThat(account.getParentAccount().get()).isEqualTo(root);
    }
}
