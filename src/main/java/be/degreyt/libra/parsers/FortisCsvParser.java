package be.degreyt.libra.parsers;

import be.degreyt.libra.rules.BankTransactionData;
import be.degreyt.libra.rules.BankTransactionDataImpl;
import be.degreyt.libra.time.Day;
import com.google.inject.Inject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.regex.Pattern;

class FortisCsvParser implements BankTransactionParser {

    private static final String SEPARATOR = "\";\"";
    private static final Pattern VALIDATION = Pattern.compile("(\"[^\"]+\"(;)?)+(?:\n)?");
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat AMOUNT_FORMAT = decimalFormat();

    @Inject
    FortisCsvParser() {
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
        boolean canParse = canParse(rawData);
        if (canParse) {
            String[] parts = rawData.substring(1, rawData.length() -3).split(SEPARATOR);
            BankTransactionDataImpl data = new BankTransactionDataImpl();

            data.setTransactionNumber(parts[0]);
            data.setExecutionDay(new Day(LocalDate.from(DAY_FORMATTER.parse(parts[1]))));
            data.setCurrencyDay(new Day(LocalDate.from(DAY_FORMATTER.parse(parts[2]))));
            data.setAmount((BigDecimal) AMOUNT_FORMAT.parse(parts[3]));
            data.setCurrency(Currency.getInstance(parts[4]));
            data.setOtherParty(parts[5].trim());
            data.setDetails(parts[6]);
            data.setBankAccountNumber(new BankAccountNumber(parts[7].trim()));

            return data;
        } else {
            throw new IllegalArgumentException();
        }
    }

}
