package be.degreyt.libra.accounts;

import be.degreyt.libra.time.Day;

import java.util.Currency;

public interface AccountService {

    Account getRootAccount();

    Account createRootAccount(Currency currency, String name);

    TransactionBuilder transactionOn(Day day);
}
