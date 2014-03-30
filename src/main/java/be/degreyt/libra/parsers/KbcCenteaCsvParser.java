package be.degreyt.libra.parsers;

import be.degreyt.libra.rules.BankTransactionData;
import be.degreyt.libra.rules.BankTransactionDataImpl;
import be.degreyt.libra.time.Day;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KbcCenteaCsvParser implements BankTransactionParser {

    private static final String SEPARATOR = ";";
    private static final Pattern VALIDATION = Pattern.compile("([^;]*;){10}(?:\\s*)");
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat AMOUNT_FORMAT = decimalFormat();

    @Override
    public BankTransactionData parse(String rawData) {
        try {
            return doParse(rawData);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public boolean canParse(String rawData) {
        return VALIDATION.matcher(rawData).matches();
    }

    private BankTransactionData doParse(String rawData) throws ParseException {
        if (rawData == null) {
            throw new IllegalArgumentException();
        }
        Matcher matcher = VALIDATION.matcher(rawData);
        if (matcher.matches()) {
            String[] parts = rawData.split(SEPARATOR);
            BankTransactionDataImpl data = new BankTransactionDataImpl();

            data.setBankAccountNumber(new BankAccountNumber(parts[0]));
            data.setCurrency(Currency.getInstance(parts[3]));
            data.setTransactionNumber(parts[4]);
            data.setExecutionDay(new Day(LocalDate.from(DAY_FORMATTER.parse(parts[5]))));
            data.setDetails(parts[6]);
            data.setCurrencyDay(new Day(LocalDate.from(DAY_FORMATTER.parse(parts[7]))));
            data.setAmount((BigDecimal) AMOUNT_FORMAT.parse(parts[8]));
            data.setOtherParty("");

            return data;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static DecimalFormat decimalFormat() {
        DecimalFormat decimalFormat = new DecimalFormat("#0.00;-#0,00", decimalFormatSymbols());
        decimalFormat.setParseBigDecimal(true);
        return decimalFormat;
    }

    private static DecimalFormatSymbols decimalFormatSymbols() {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator(',');
        decimalFormatSymbols.setGroupingSeparator('.');
        return decimalFormatSymbols;
    }

}
