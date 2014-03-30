package be.degreyt.libra;

import be.degreyt.libra.pair.Pair;
import be.degreyt.old.api.*;
import be.degreyt.old.batch.*;
import be.degreyt.old.collections.CollectionsModule;
import be.degreyt.old.format.FormatModule;
import be.degreyt.old.format.HierarchyPrinter;
import be.degreyt.old.format.Printer;
import be.degreyt.old.impl.LibraModule;
import be.degreyt.old.numbering.NumberingModule;
import be.degreyt.old.persistence.CurrencySerializer;
import be.degreyt.old.persistence.DaySerializer;
import be.degreyt.old.persistence.PersistenceStoreImpl;
import be.degreyt.old.persistence.journal.JournalDAOImpl;
import be.degreyt.old.persistence.ledger.LedgerDAOImpl;
import be.degreyt.old.persistence.rules.RulesDaoImpl;
import be.degreyt.old.report.result.*;

import java.time.Period;
import java.util.Optional;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

/**
 *
 */
public class Viewer {

    public static void main(String[] args) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Currency.class, new CurrencySerializer());
        gsonBuilder.registerTypeAdapter(Day.class, new DaySerializer());

        PersistenceStoreImpl persistenceStore = new PersistenceStoreImpl("libra");

        final AccountResolutionRuleProxy ownProxy = new AccountResolutionRuleProxy();
        final AccountResolutionRuleProxy otherProxy = new AccountResolutionRuleProxy();

        Injector consoleInjector = Guice.createInjector(new CollectionsModule(), new LibraModule(), new NumberingModule(), new FormatModule(), new BatchModule(), new AbstractModule() {
            @Override
            protected void configure() {
                bind(Key.get(AccountResolutionRule.class, Self.class)).toInstance(ownProxy);
                bind(Key.get(AccountResolutionRule.class, OtherParty.class)).toInstance(otherProxy);
                bind(MonitorPaths.class).toInstance(new MonitorPaths() {
                    @Override
                    public List<Path> pathsToMonitor() {
                        return Arrays.asList(Paths.get("D:/Projects/Work/import/"));
                    }
                });
            }
        });

        RulesDaoImpl rulesDao = new RulesDaoImpl(persistenceStore, gsonBuilder);
        Optional<CompositeRule> own = rulesDao.load("own");
        if (own.isPresent()) {
            ownProxy.setActual(own.get());
        }

        Optional<CompositeRule> other = rulesDao.load("other");
        if (other.isPresent()) {
            otherProxy.setActual(other.get());
        }

        LedgerFactory ledgerFactory = consoleInjector.getInstance(LedgerFactory.class);
        Ledger ledger = null;
        LedgerDAOImpl ledgerDAO = new LedgerDAOImpl(persistenceStore, ledgerFactory, gsonBuilder);
        Optional<Ledger> ledgerMaybeNull = ledgerDAO.load("Jaar 2013");

        if (ledgerMaybeNull.isPresent()) {
            ledger = ledgerMaybeNull.get();
        }

        ActiveLedger activeLedger = consoleInjector.getInstance(ActiveLedger.class);
        activeLedger.set(ledger);

        Journal journal = null;
        JournalDAOImpl journalDAO = new JournalDAOImpl(persistenceStore, gsonBuilder, activeLedger);
        Optional<Journal> testJournal = journalDAO.load("2013");
        if (testJournal.isPresent()) {
            journal = testJournal.get();
        }

        Printer<Ledger> ledgerPrinter = Printer.provider.get().printerFor(Ledger.class);

        System.out.println(ledgerPrinter.print(activeLedger.get()));

        Printer<Ledger> ledgerPrinter1 = new HierarchyPrinter();
        System.out.println(ledgerPrinter1.print(activeLedger.get()));

        List<Account> resultReportAccounts = new ArrayList<>();

        Account account = ledger.getAccountRepository().forNumber(new AccountNumber("300020")).get();
        resultReportAccounts.add(account);


        ResultReportDefinition resultReportDefinition = new ResultReportDefinitionImpl(resultReportAccounts, Period.ofMonths(1));

        boolean v = new Pair<DayRange, Account>(new DayRange(new Day(2013, 1, 1), new Day(2013, 12, 31)), account).equals(new Pair<DayRange, Account>(new DayRange(new Day(2013, 1, 1), new Day(2013, 12, 31)), account));

        ResultReport report = new ResultReportBuilderImpl().build(resultReportDefinition, ledger, new DayRange(new Day(2013, 1, 1), new Day(2013, 12, 31)));

        StringBuffer buffer = new StringBuffer();
        new ResultReportAsStringPrinter(buffer).print(report);
        System.out.println(buffer.toString());

    }
}
