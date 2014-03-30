package be.degreyt.libra.accounts;


import be.degreyt.libra.money.Saldo;

public interface Mutation {

    Account getAccount();

    Saldo getSaldo();


}
