package be.degreyt.libra.money;

public enum DebitCredit {
    Debit, Credit;

    public DebitCredit other() {
        return this == Debit ? Credit : Debit;
    }
}
