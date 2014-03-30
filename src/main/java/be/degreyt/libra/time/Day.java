package be.degreyt.libra.time;

import com.sun.istack.internal.NotNull;

import java.time.LocalDate;

public final class Day implements Comparable<Day> {

    private final LocalDate day;

    public Day(LocalDate day) {
        this.day = day;
    }

    public Day(int year, int month, int day) {
        this.day = LocalDate.of(year, month, day);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Day day1 = (Day) o;

        return day.equals(day1.day);

    }

    @Override
    public int hashCode() {
        return day.hashCode();
    }

    @Override
    public String toString() {
        return day.toString();
    }

    @Override
    public int compareTo(Day other) {
        return this.day.compareTo(other.day);
    }

    public LocalDate asLocalDate() {
        return day;
    }

    public Day next() {
        return new Day(day.plusDays(1));
    }

    public Day previous() {
        return new Day(day.minusDays(1));
    }

    public boolean before(Day day) {
        return this.day.isBefore(day.day);
    }

    public boolean after(Day day) {
        return this.day.isAfter(day.day);
    }

    public static Day of(int year, int month, int day) {
        return new Day(year, month, day);
    }
}
