package be.degreyt.libra.money;

import java.util.Currency;

public final class Saldo {

    private final DebitCredit debitCredit;
    private final Money money;

    public Saldo(DebitCredit debitCredit, Money money) {
        this.money = money;
        this.debitCredit = debitCredit;
    }

    public static Saldo zero(Currency currency) {
        return new Saldo(DebitCredit.Debit, Money.zero(currency));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Saldo saldo = (Saldo) o;

        return money.equals(saldo.money) && debitCredit == saldo.debitCredit;

    }

    @Override
    public int hashCode() {
        int result = debitCredit.hashCode();
        result = 41 * result + money.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return debitCredit.toString() + ' ' + money.toString();
    }

    public Saldo add(Saldo other) {
        if (this.debitCredit == other.debitCredit) {
            return new Saldo(debitCredit, this.money.plus(other.money));
        }
        if (this.money.compareTo(other.money) >= 0) {
            return new Saldo(this.debitCredit, this.money.minus(other.money));
        }
        return new Saldo(other.debitCredit, other.money.minus(this.money));
    }

    public boolean isBalanced() {
        return money.isZero();
    }

    public DebitCredit getDebitCredit() {
        return debitCredit;
    }

    public Money getMoney() {
        return money;
    }

    public Saldo inverted() {
        return new Saldo(debitCredit.other(), money);
    }
}
