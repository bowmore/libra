package be.degreyt.libra;

import be.degreyt.libra.money.Money;
import be.degreyt.libra.money.DebitCredit;
import be.degreyt.old.api.*;
import be.degreyt.old.batch.*;
import be.degreyt.old.collections.CollectionsModule;
import be.degreyt.old.format.FormatModule;
import be.degreyt.old.format.HierarchyPrinter;
import be.degreyt.old.format.Printer;
import be.degreyt.old.impl.*;
import be.degreyt.old.numbering.NumberingModule;
import be.degreyt.old.persistence.CurrencySerializer;
import be.degreyt.old.persistence.DaySerializer;
import be.degreyt.old.persistence.PersistenceStoreImpl;
import be.degreyt.old.persistence.journal.JournalDAOImpl;
import be.degreyt.old.persistence.journal.TransactionSequenceDaoImpl;
import be.degreyt.old.persistence.ledger.LedgerDAOImpl;
import be.degreyt.old.persistence.rules.RulesDaoImpl;
import java.util.Optional;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Console2013 {

    private static final AccountNumber CAPITAL_ACCOUNT_NR = new AccountNumber("100000");
    public static final String JAAR_2013 = "Jaar 2013";
    public static final String JOURNAL = "2013";
    public static final String IMPORT_DIR = "D:/Projects/Work/import/";
    public static final String OWN_RULES = "own";
    public static final String OTHER_RULES = "other";

    public static void main(String[] args) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Currency.class, new CurrencySerializer());
        gsonBuilder.registerTypeAdapter(Day.class, new DaySerializer());

        PersistenceStoreImpl persistenceStore = new PersistenceStoreImpl("libra");

        TransactionSequenceDaoImpl transactionSequenceDao = new TransactionSequenceDaoImpl(persistenceStore, gsonBuilder.create());
        Optional<TransactionNumberSequence> sequence = transactionSequenceDao.load(JAAR_2013);
        TransactionNumberSequence.instance.set(sequence.orElse(new TransactionNumberSequenceImpl(new TransactionNumber(1L))));


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
                        return Arrays.asList(Paths.get(IMPORT_DIR));
                    }
                });
            }
        });

        RulesDaoImpl rulesDao = new RulesDaoImpl(persistenceStore, gsonBuilder);
        Optional<CompositeRule> own = rulesDao.load(OWN_RULES);
        if (own.isPresent()) {
            ownProxy.setActual(own.get());

//            own.get().addRule(new BankAccountNumberBasedRule(new BankAccountNumber("BE93 7470 0413 6267"), new AccountByNumber(new AccountNumber("580010"))));
//            rulesDao.save(OWN_RULES, own.get());
//            persistenceStore.commit();
        } else {
            CompositeRule ownRule = buildOwnRule();
            rulesDao.save(OWN_RULES, ownRule);
            persistenceStore.commit();
            ownProxy.setActual(ownRule);
        }

        Optional<CompositeRule> other = rulesDao.load(OTHER_RULES);
        if (other.isPresent()) {
            otherProxy.setActual(other.get());

//            other.get().addRule(new DetailBasedOtherPartyRule("", "BE44 7370 0297 8145", new AccountByNumber(new AccountNumber("550000"))));
//            rulesDao.save(OTHER_RULES, other.get());
//            persistenceStore.commit();
        } else {
            CompositeRule otherRule = buildOtherRule();
            rulesDao.save(OTHER_RULES, otherRule);
            persistenceStore.commit();
            otherProxy.setActual(otherRule);
        }

        LedgerFactory ledgerFactory = consoleInjector.getInstance(LedgerFactory.class);
        Ledger ledger = null;
        LedgerDAOImpl ledgerDAO = new LedgerDAOImpl(persistenceStore, ledgerFactory, gsonBuilder);
        Optional<Ledger> ledgerMaybeNull = ledgerDAO.load(JAAR_2013);

        boolean performInitial = true;

        if (ledgerMaybeNull.isPresent()) {
            ledger = ledgerMaybeNull.get();
            performInitial = false;
        } else {
            ledger = createLedger(ledgerFactory);
            ledgerDAO.save(ledger.getName(), ledger);
            persistenceStore.commit();
        }


        ActiveLedger activeLedger = consoleInjector.getInstance(ActiveLedger.class);
        activeLedger.set(ledger);

//        Optional<Account> accountOptional = ledger.getAccountRepository().forNumber(new AccountNumber("300030"));
//        OperatingAccount newAccount = ledger.createOperatingAccount((CategoryAccount) accountOptional.get(), new AccountNumber("440019"), "Floréac");
//        ledgerDAO.save(JAAR_2013, ledger);
//        persistenceStore.commit();

        JournalDAOImpl journalDAO = new JournalDAOImpl(persistenceStore, gsonBuilder, activeLedger);


        Optional<Journal> journalOptional = journalDAO.load(JOURNAL);

        Journal journal = journalOptional.orElse(new JournalImpl(ledger, new DayRange(new Day(2013, 1, 1), new Day(2013, 12, 31)), Validity.Real));

        activeLedger.add(journal);

        // ============ initial =======================


        if (performInitial) {
            performInitial(ledger, journal);
        }
        // ============ initial - end =======================

        ImporterMonitor importerMonitor = consoleInjector.getInstance(ImporterMonitor.class);

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Future<?> future = executorService.submit(importerMonitor);

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {


//        } catch (ExecutionException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        executorService.shutdownNow();

        journalDAO.save(JOURNAL, journal);
        transactionSequenceDao.save(JAAR_2013, TransactionNumberSequence.instance.get());
        persistenceStore.commit();

        Printer<Ledger> ledgerPrinter = Printer.provider.get().printerFor(Ledger.class);

        System.out.println(ledgerPrinter.print(activeLedger.get()));

        Printer<Account> accountPrinter = Printer.provider.get().printerFor(Account.class);
        Printer<Ledger> ledgerPrinter1 = new HierarchyPrinter();
        System.out.println(ledgerPrinter1.print(activeLedger.get()));
    }

    private static void performInitial(Ledger ledger, Journal journal) {
        DefaultTransactionBuilder.startTransaction(journal, new Day(2013, 1, 1), "Initialization.")
                .add(account(ledger, "580009"), saldo(DebitCredit.Debit, "641.32"))
                .add(account(ledger, "100002"), saldo(DebitCredit.Credit, "641.32"))
        .toTransaction();

        DefaultTransactionBuilder.startTransaction(journal, new Day(2013, 1, 1), "Initialization.")
                .add(account(ledger, "580010"), saldo(DebitCredit.Debit, "5088.84"))
                .add(account(ledger, "100002"), saldo(DebitCredit.Credit, "5088.84"))
        .toTransaction();

        DefaultTransactionBuilder.startTransaction(journal, new Day(2013, 1, 1), "Initialization.")
                .add(account(ledger, "580002"), saldo(DebitCredit.Debit, "2214.05"))
                .add(account(ledger, "100002"), saldo(DebitCredit.Credit, "2214.05"))
        .toTransaction();

        DefaultTransactionBuilder.startTransaction(journal, new Day(2013, 1, 1), "Initialization.")
                .add(account(ledger, "580008"), saldo(DebitCredit.Debit, "284.43"))
                .add(account(ledger, "100002"), saldo(DebitCredit.Credit, "284.43"))
        .toTransaction();

        DefaultTransactionBuilder.startTransaction(journal, new Day(2013, 1, 1), "Initialization.")
                .add(account(ledger, "580001"), saldo(DebitCredit.Debit, "9863.78"))
                .add(account(ledger, "100002"), saldo(DebitCredit.Credit, "9863.78"))
        .toTransaction();

        DefaultTransactionBuilder.startTransaction(journal, new Day(2013, 1, 1), "Initialization.")
                .add(account(ledger, "580011"), saldo(DebitCredit.Debit, "575.57"))
                .add(account(ledger, "100002"), saldo(DebitCredit.Credit, "575.57"))
        .toTransaction();

        DefaultTransactionBuilder.startTransaction(journal, new Day(2013, 1, 1), "Initialization.")
                .add(account(ledger, "580006"), saldo(DebitCredit.Debit, "4712.27"))
                .add(account(ledger, "100002"), saldo(DebitCredit.Credit, "4712.27"))
        .toTransaction();

        DefaultTransactionBuilder.startTransaction(journal, new Day(2013, 1, 1), "Initialization.")
                .add(account(ledger, "580005"), saldo(DebitCredit.Debit, "1403.49"))
                .add(account(ledger, "100002"), saldo(DebitCredit.Credit, "1403.49"))
        .toTransaction();

        DefaultTransactionBuilder.startTransaction(journal, new Day(2013, 1, 1), "Initialization.")
                .add(account(ledger, "580004"), saldo(DebitCredit.Debit, "701.34"))
                .add(account(ledger, "100002"), saldo(DebitCredit.Credit, "701.34"))
        .toTransaction();

        /*
        Cindy zicht : 	641,32
        Cindy Spaar	5088,84
        T&C zicht 	2214,05
        T & C Spaar	284,43
        Tom zicht	9863,78
        Tom Spaar	575,57
        Tom Spaar Online	4712,27
        Nele Spaar	1403,49
        Nils spaar	701,34

         */
    }

    private static Saldo saldo(DebitCredit debitCredit, String euroAmount) {
        return new Saldo(debitCredit, new Money(new BigDecimal(euroAmount), Currency.getInstance("EUR")));
    }

    private static OperatingAccount account(Ledger ledger, String accountNumberString) {
        AccountRepository accountRepository = ledger.getAccountRepository();
        return accountRepository.operatingAccountForNumber(new AccountNumber(accountNumberString)).get();
    }

    private static CompositeRule buildOtherRule() {
        final OtherBankAccountNumberBasedRule eictRekening = new OtherBankAccountNumberBasedRule(new BankAccountNumber("BE15 8260 0063 1830"), new AccountByNumber(new AccountNumber("400001")));
        final OtherBankAccountNumberBasedRule scarletRekening = new OtherBankAccountNumberBasedRule(new BankAccountNumber("BE43 0013 6602 2001"), new AccountByNumber(new AccountNumber("440001")));
        final OtherBankAccountNumberBasedRule huisArtsRekening = new OtherBankAccountNumberBasedRule(new BankAccountNumber("BE86 4480 7465 6150"), new AccountByNumber(new AccountNumber("440002")));
        final OtherBankAccountNumberBasedRule electrabelRekening = new OtherBankAccountNumberBasedRule(new BankAccountNumber("BE46 0003 2544 8336"), new AccountByNumber(new AccountNumber("440003")));
        final OtherBankAccountNumberBasedRule proveaRekening = new OtherBankAccountNumberBasedRule(new BankAccountNumber("BE88 0003 2507 0541"), new AccountByNumber(new AccountNumber("440008")));
        final OtherBankAccountNumberBasedRule cm = new OtherBankAccountNumberBasedRule(new BankAccountNumber("BE61 8908 8200 0217"), new AccountByNumber(new AccountNumber("440010")));
        final OtherBankAccountNumberBasedRule centeaRekeningRegelTransfer = new OtherBankAccountNumberBasedRule(new BankAccountNumber("BE62 8508 3082 1061"), new AccountByNumber(new AccountNumber("550000")));
        final OtherBankAccountNumberBasedRule cindyKBCRekeningRegelTransfer = new OtherBankAccountNumberBasedRule(new BankAccountNumber("BE44 7370 0297 8145"), new AccountByNumber(new AccountNumber("550000")));
        final OtherBankAccountNumberBasedRule tomOnlineSavingsTransfer = new OtherBankAccountNumberBasedRule(new BankAccountNumber("BE96 0355 9742 9105"), new AccountByNumber(new AccountNumber("550000")));
        final OtherBankAccountNumberBasedRule tomsCheckingAccountTransfer = new OtherBankAccountNumberBasedRule(new BankAccountNumber("BE34 2930 1376 7890"), new AccountByNumber(new AccountNumber("550000")));
        final DetailBasedOtherPartyRule tomsCheckingAccountTransfer2 = new DetailBasedOtherPartyRule("", "BE34 2930 1376 7890", new AccountByNumber(new AccountNumber("550000")));
        final DetailBasedOtherPartyRule cindySavingsAccountTransfer = new DetailBasedOtherPartyRule("", "BE93 7470 0413 6267", new AccountByNumber(new AccountNumber("550000")));
        final DetailBasedOtherPartyRule apothekerRegel = new DetailBasedOtherPartyRule("BETALING MET BANKKAART", "HALET-VAN ZANDW", new AccountByNumber(new AccountNumber("440004")));
        final DetailBasedOtherPartyRule drankRegel = new DetailBasedOtherPartyRule("BETALING MET BANKKAART", "DE CLERCQ - ZOON", new AccountByNumber(new AccountNumber("440005")));
        final DetailBasedOtherPartyRule etenRegel = new DetailBasedOtherPartyRule("BETALING MET BANKKAART", "COLRUYT", new AccountByNumber(new AccountNumber("440006")));
        final DetailBasedOtherPartyRule mediaRegel = new DetailBasedOtherPartyRule("BETALING MET BANKKAART", "MEDIA MARKT", new AccountByNumber(new AccountNumber("440007")));
        final DetailBasedOtherPartyRule centeaRekeningRegel = new DetailBasedOtherPartyRule("850-8308210-61", "", new AccountByNumber(new AccountNumber("550000")));
        final DetailBasedOtherPartyRule privateSales = new DetailBasedOtherPartyRule("750-0010098-71", "", new AccountByNumber(new AccountNumber("400002")));
        final DetailBasedOtherPartyRule huisArtsRekening2 = new DetailBasedOtherPartyRule("BETALING MET BANKKAART", "DOKTER DIERICKX", new AccountByNumber(new AccountNumber("440002")));
        final DetailBasedOtherPartyRule administratieRegel = new DetailBasedOtherPartyRule("BETALING MET BANKKAART", "ST AMANDSBERG", new AccountByNumber(new AccountNumber("490001")));
        final DetailBasedOtherPartyRule ccc = new DetailBasedOtherPartyRule("BETALING AAN BANK CARD COMPANY", "", new AccountByNumber(new AccountNumber("580003")));
        final DetailBasedOtherPartyRule centeaRekeningNilsRegel = new DetailBasedOtherPartyRule("863-7260507-73", "", new AccountByNumber(new AccountNumber("550000")));
        final DetailBasedOtherPartyRule centeaRekeningNeleRegel = new DetailBasedOtherPartyRule("863-7056009-51", "", new AccountByNumber(new AccountNumber("550000")));
        final DetailBasedOtherPartyRule onlineSpaarRekening = new DetailBasedOtherPartyRule("035-5974291-05", "", new AccountByNumber(new AccountNumber("550000")));
        final DetailBasedOtherPartyRule variaUitgaven = new DetailBasedOtherPartyRule("GELDOPNAME AAN ANDERE AUTOMATEN", "", new AccountByNumber(new AccountNumber("613001")));
        final DetailBasedOtherPartyRule interesten = new DetailBasedOtherPartyRule("NETTO INTERESTEN :DETAILS ZIE BI", "", new AccountByNumber(new AccountNumber("656001")));
        final DetailBasedOtherPartyRule rekeningKosten = new DetailBasedOtherPartyRule("MAANDELIJKSE BIJDRAGE", "SERVICE PACK", new AccountByNumber(new AccountNumber("656002")));
        final DetailBasedOtherPartyRule aveve = new DetailBasedOtherPartyRule("BETALING MET BANKKAART", "BOGHAERT RUDI", new AccountByNumber(new AccountNumber("440009")));
        final DetailBasedOtherPartyRule weba = new DetailBasedOtherPartyRule("BETALING MET BANKKAART", "MEUBELEN WEBA", new AccountByNumber(new AccountNumber("440011")));
        final DetailBasedOtherPartyRule cm2 = new DetailBasedOtherPartyRule("890-8820003-18", "", new AccountByNumber(new AccountNumber("440010")));
        final DetailBasedOtherPartyRule eandis = new DetailBasedOtherPartyRule("091-0131287-75", "", new AccountByNumber(new AccountNumber("440012")));
        final DetailBasedOtherPartyRule quick = new DetailBasedOtherPartyRule("BETALING MET BANKKAART", "QUICK", new AccountByNumber(new AccountNumber("440013")));
        final DetailBasedOtherPartyRule deschacht = new DetailBasedOtherPartyRule("BETALING MET BANKKAART", "MEGA DESCHACHT", new AccountByNumber(new AccountNumber("440014")));
        final DetailBasedOtherPartyRule eict2 = new DetailBasedOtherPartyRule("826-0006318-30", "", new AccountByNumber(new AccountNumber("400001")));
        final DetailBasedOtherPartyRule apoWestveld = new DetailBasedOtherPartyRule("BETALING MET BANKKAART", "APO. WESTVELD", new AccountByNumber(new AccountNumber("440004")));
        final DetailBasedOtherPartyRule wijnHoek = new DetailBasedOtherPartyRule("BETALING MET BANKKAART", "DE WIJNHOEK", new AccountByNumber(new AccountNumber("440015")));
        final DetailBasedOtherPartyRule amex = new DetailBasedOtherPartyRule("310-1592207-38", "", new AccountByNumber(new AccountNumber("580007")));
        final DetailBasedOtherPartyRule transferFromTomRekening = new DetailBasedOtherPartyRule("", "293-0137678-90", new AccountByNumber(new AccountNumber("550000")));
        final DetailBasedOtherPartyRule leningHuis = new DetailBasedOtherPartyRule("", "859-4847781-15", new AccountByNumber(new AccountNumber("200001")));
        final DetailBasedOtherPartyRule leningAuto = new DetailBasedOtherPartyRule("", "860-2081383-36", new AccountByNumber(new AccountNumber("200002")));
        final DetailBasedOtherPartyRule autoVerzekering = new DetailBasedOtherPartyRule("", "861-1182528-59", new AccountByNumber(new AccountNumber("440016")));
        final DetailBasedOtherPartyRule rekeningKostenCentea = new DetailBasedOtherPartyRule("", "AANREKENING KOSTEN", new AccountByNumber(new AccountNumber("656003")));
        final DetailBasedOtherPartyRule rekeningKaartCentea = new DetailBasedOtherPartyRule("", "AANREKENING KAARTBIJDRAGE", new AccountByNumber(new AccountNumber("656003")));
        final DetailBasedOtherPartyRule schuldSaldoTomCentea = new DetailBasedOtherPartyRule("", "850-1600716-20", new AccountByNumber(new AccountNumber("656004")));
        final DetailBasedOtherPartyRule schuldSaldoCindyCentea = new DetailBasedOtherPartyRule("", "850-1600717-21", new AccountByNumber(new AccountNumber("656005")));
        final DetailBasedOtherPartyRule funOostakker = new DetailBasedOtherPartyRule("", "FUN OOSTAKKER", new AccountByNumber(new AccountNumber("440017")));
        final DetailBasedOtherPartyRule renteSpaarrekeningNele = new DetailBasedOtherPartyRule("", "CREDITRENTE VAN BE75 8637 0560 0951", new AccountByNumber(new AccountNumber("756005")));
        final DetailBasedOtherPartyRule renteSpaarrekeningCentea = new DetailBasedOtherPartyRule("", "CREDITRENTE VAN BE88 8500 4544 8841", new AccountByNumber(new AccountNumber("756008")));
        final DetailBasedOtherPartyRule renteSpaarrekeningNils = new DetailBasedOtherPartyRule("", "CREDITRENTE VAN BE27 8637 2605 0773", new AccountByNumber(new AccountNumber("756004")));
        final DetailBasedOtherPartyRule rekeningVerzekering = new DetailBasedOtherPartyRule("REKENINGVERZEKERING", "", new AccountByNumber(new AccountNumber("656002")));
        final DetailBasedOtherPartyRule uz = new DetailBasedOtherPartyRule("", "BE80 3751 1174 3877", new AccountByNumber(new AccountNumber("440018")));
        final DetailBasedOtherPartyRule cm3 = new DetailBasedOtherPartyRule("", "740-9401611-60", new AccountByNumber(new AccountNumber("440010")));
        final DetailBasedOtherPartyRule floreac = new DetailBasedOtherPartyRule("", "440-0343501-96", new AccountByNumber(new AccountNumber("400019")));
        final DetailBasedOtherPartyRule aveve2 = new DetailBasedOtherPartyRule("", "BOGHAERT RUDI", new AccountByNumber(new AccountNumber("440009")));
        final DetailBasedOtherPartyRule centeaSavingsTransfer = new DetailBasedOtherPartyRule("", "BE88 8500 4544 8841", new AccountByNumber(new AccountNumber("550000")));
        final DetailBasedOtherPartyRule centeaCheckingTransfer = new DetailBasedOtherPartyRule("", "BE62 8508 3082 1061", new AccountByNumber(new AccountNumber("550000")));
        final DetailBasedOtherPartyRule cindyCheckingTransfer = new DetailBasedOtherPartyRule("", "BE44 7370 0297 8145", new AccountByNumber(new AccountNumber("550000")));


        return new CompositeRule(cindyCheckingTransfer, centeaCheckingTransfer, centeaSavingsTransfer, cindySavingsAccountTransfer, aveve2, floreac, cm3, uz, renteSpaarrekeningNils, renteSpaarrekeningCentea, renteSpaarrekeningNele, funOostakker, schuldSaldoCindyCentea, schuldSaldoTomCentea, rekeningKaartCentea, rekeningKostenCentea, autoVerzekering, leningAuto, leningHuis, transferFromTomRekening, amex, wijnHoek, apoWestveld, eict2, deschacht, quick, eandis, weba, cm, cm2, aveve, proveaRekening, rekeningKosten, interesten, variaUitgaven, onlineSpaarRekening, centeaRekeningNeleRegel, centeaRekeningNilsRegel, ccc, administratieRegel, eictRekening, scarletRekening, huisArtsRekening, electrabelRekening, apothekerRegel, drankRegel, centeaRekeningRegel, privateSales, etenRegel, huisArtsRekening2, mediaRegel, centeaRekeningRegelTransfer, cindyKBCRekeningRegelTransfer, tomOnlineSavingsTransfer, tomsCheckingAccountTransfer, rekeningVerzekering, tomsCheckingAccountTransfer2);
    }

    private static CompositeRule buildOwnRule() {
        final BankAccountNumberBasedRule tomsRekening = new BankAccountNumberBasedRule(new BankAccountNumber("BE34 2930 1376 7890"), new AccountByNumber(new AccountNumber("580001")));
        final BankAccountNumberBasedRule tomCindyCenteaRekening = new BankAccountNumberBasedRule(new BankAccountNumber("BE62 8508 3082 1061"), new AccountByNumber(new AccountNumber("580002")));
        final BankAccountNumberBasedRule neleCenteaRekening = new BankAccountNumberBasedRule(new BankAccountNumber("BE75 8637 0560 0951"), new AccountByNumber(new AccountNumber("580005")));
        final BankAccountNumberBasedRule nilsCenteaRekening = new BankAccountNumberBasedRule(new BankAccountNumber("BE27 8637 2605 0773"), new AccountByNumber(new AccountNumber("580004")));
        final BankAccountNumberBasedRule centeaSpaarRekening = new BankAccountNumberBasedRule(new BankAccountNumber("BE88 8500 4544 8841"), new AccountByNumber(new AccountNumber("580008")));
        final BankAccountNumberBasedRule tomOnlineSavingsRekening = new BankAccountNumberBasedRule(new BankAccountNumber("BE96 0355 9742 9105"), new AccountByNumber(new AccountNumber("580006")));
        final BankAccountNumberBasedRule tomSavingsRekening = new BankAccountNumberBasedRule(new BankAccountNumber("BE55 2937 3377 4844"), new AccountByNumber(new AccountNumber("580011")));
        final BankAccountNumberBasedRule cindyKbcRekening = new BankAccountNumberBasedRule(new BankAccountNumber("BE44 7370 0297 8145"), new AccountByNumber(new AccountNumber("580009")));
        final BankAccountNumberBasedRule cindyKbcSpaarRekening = new BankAccountNumberBasedRule(new BankAccountNumber("BE93 7470 0413 6267"), new AccountByNumber(new AccountNumber("580010")));


        return new CompositeRule(cindyKbcSpaarRekening, cindyKbcRekening, tomsRekening, tomCindyCenteaRekening, neleCenteaRekening, centeaSpaarRekening, nilsCenteaRekening, tomOnlineSavingsRekening, tomSavingsRekening);
    }

    private static Ledger createLedger(LedgerFactory ledgerFactory) {
        Ledger ledger = ledgerFactory.createNewLedger(JAAR_2013, Currency.getInstance("EUR"));

        CategoryAccount rootAccount = ledger.getRootAccount();

        CategoryAccount passivaAccount = ledger.createCategoryAccount(rootAccount, new AccountNumber("000002"), "Passiva");
        CategoryAccount capitalAccount = ledger.createCategoryAccount(passivaAccount, CAPITAL_ACCOUNT_NR, "Capital");
        OperatingAccount realEstateAccount = ledger.createOperatingAccount(capitalAccount, new AccountNumber("100001"), "Real Estate");
        OperatingAccount liquidityAccount = ledger.createOperatingAccount(capitalAccount, new AccountNumber("100002"), "Liquidity");

        CategoryAccount activaAccount = ledger.createCategoryAccount(rootAccount, new AccountNumber("000001"), "Activa");
        CategoryAccount allFinancials = ledger.createCategoryAccount(activaAccount, new AccountNumber("500000"), "All financial resources");
        CategoryAccount allPensionFunds = ledger.createCategoryAccount(allFinancials, new AccountNumber("500100"), "All pension funds");
        OperatingAccount tomsPensionFund = ledger.createOperatingAccount(allPensionFunds, new AccountNumber("500101"), "Tom's pension fund");
        OperatingAccount cindysPensionFund = ledger.createOperatingAccount(allPensionFunds, new AccountNumber("500102"), "Cindy's pension fund");

        CategoryAccount allCheckingAccounts = ledger.createCategoryAccount(allFinancials, new AccountNumber("570000"), "All Checking accounts");
        OperatingAccount checkingAccount = ledger.createOperatingAccount(allCheckingAccounts, new AccountNumber("580001"), "Rekening Tom");
        OperatingAccount centeaAccount = ledger.createOperatingAccount(allCheckingAccounts, new AccountNumber("580002"), "Rekening Tom - Cindy Centea");
        OperatingAccount cindyKbcCheckingAccount = ledger.createOperatingAccount(allCheckingAccounts, new AccountNumber("580009"), "Rekening Cindy");

        CategoryAccount allSavingsAccounts = ledger.createCategoryAccount(allFinancials, new AccountNumber("570100"), "All Savings accounts");
        CategoryAccount allParentsSavingsAccounts = ledger.createCategoryAccount(allSavingsAccounts, new AccountNumber("570101"), "All Parents Savings accounts");
        CategoryAccount allTomsSavingsAccounts = ledger.createCategoryAccount(allParentsSavingsAccounts, new AccountNumber("570102"), "All Toms Savings accounts");
        OperatingAccount onlineSavingsAccountTom = ledger.createOperatingAccount(allTomsSavingsAccounts, new AccountNumber("580006"), "Online Spaarrekening Tom");
        OperatingAccount classicSavingsAccountTom = ledger.createOperatingAccount(allTomsSavingsAccounts, new AccountNumber("580011"), "Classic Spaarrekening Tom");
        OperatingAccount centeaSavingsAccount = ledger.createOperatingAccount(allParentsSavingsAccounts, new AccountNumber("580008"), "Centea savings Account");
        CategoryAccount allCindySavingsAccounts = ledger.createCategoryAccount(allParentsSavingsAccounts, new AccountNumber("570104"), "All Toms Savings accounts");
        OperatingAccount cindyKbcSavingsAccount = ledger.createOperatingAccount(allCindySavingsAccounts, new AccountNumber("580010"), "Cindy savings Account");

        CategoryAccount allKidsSavingsAccounts = ledger.createCategoryAccount(allSavingsAccounts, new AccountNumber("570103"), "All Kids Savings accounts");
        OperatingAccount rekeningNilsAccount = ledger.createOperatingAccount(allKidsSavingsAccounts, new AccountNumber("580004"), "Rekening Nils");
        OperatingAccount rekeningNeleAccount = ledger.createOperatingAccount(allKidsSavingsAccounts, new AccountNumber("580005"), "Rekening Nele");


        OperatingAccount transferAccount = ledger.createOperatingAccount(allFinancials, new AccountNumber("550000"), "Transfer Account");

        CategoryAccount resultsAccount = ledger.createCategoryAccount(rootAccount, new AccountNumber("300000"), "Results");

        CategoryAccount remunerations = ledger.createCategoryAccount(resultsAccount, new AccountNumber("300010"), "Remunerations");
        CategoryAccount remunerationsTom = ledger.createCategoryAccount(remunerations, new AccountNumber("300020"), "Remunerations Tom");
        OperatingAccount customerAccount = ledger.createOperatingAccount(remunerationsTom, new AccountNumber("400001"), "EICT");
        CategoryAccount remunerationsCindy = ledger.createCategoryAccount(remunerations, new AccountNumber("300030"), "Remunerations Cindy");
        OperatingAccount floreacAccount = ledger.createOperatingAccount(remunerationsCindy, new AccountNumber("400019"), "Floréac");

        CategoryAccount multiMedia = ledger.createCategoryAccount(resultsAccount, new AccountNumber("300040"), "Multimedia");
        OperatingAccount mediaMarktAccount = ledger.createOperatingAccount(multiMedia, new AccountNumber("440007"), "Media Markt");

        CategoryAccount communications = ledger.createCategoryAccount(resultsAccount, new AccountNumber("300050"), "Communications");
        OperatingAccount scarletAccount = ledger.createOperatingAccount(communications, new AccountNumber("440001"), "Scarlet");

        CategoryAccount medicals = ledger.createCategoryAccount(resultsAccount, new AccountNumber("300060"), "Medicals");
        OperatingAccount personalPhysicianAccount = ledger.createOperatingAccount(medicals, new AccountNumber("440002"), "Huisarts");
        OperatingAccount pharmacyAccount = ledger.createOperatingAccount(medicals, new AccountNumber("440004"), "Apotheker");
        OperatingAccount cmAccount = ledger.createOperatingAccount(medicals, new AccountNumber("440010"), "CM");
        OperatingAccount uzAccount = ledger.createOperatingAccount(medicals, new AccountNumber("440018"), "UZ");

        CategoryAccount energy = ledger.createCategoryAccount(resultsAccount, new AccountNumber("300070"), "Energy");
        OperatingAccount electrabelAccount = ledger.createOperatingAccount(energy, new AccountNumber("440003"), "Electrabel");
        OperatingAccount eandisAccount = ledger.createOperatingAccount(energy, new AccountNumber("440012"), "Eandis");

        CategoryAccount sustencance = ledger.createCategoryAccount(resultsAccount, new AccountNumber("300080"), "Sustenance");
        CategoryAccount drinks = ledger.createCategoryAccount(sustencance, new AccountNumber("300082"), "Drinks");
        OperatingAccount drinksAccount = ledger.createOperatingAccount(drinks, new AccountNumber("440005"), "De Clercq");
        CategoryAccount food = ledger.createCategoryAccount(sustencance, new AccountNumber("300084"), "Food");
        OperatingAccount colruytAccount = ledger.createOperatingAccount(food, new AccountNumber("440006"), "Colruyt");
        OperatingAccount aveveAccount = ledger.createOperatingAccount(food, new AccountNumber("440009"), "Aveve Lochristi");
        OperatingAccount quickAccount = ledger.createOperatingAccount(food, new AccountNumber("440013"), "Quick");

        CategoryAccount variousSpending = ledger.createCategoryAccount(resultsAccount, new AccountNumber("300090"), "Various");
        OperatingAccount creditCardAccount = ledger.createOperatingAccount(variousSpending, new AccountNumber("580003"), "Credit Card");
        OperatingAccount cashSpendingAccount = ledger.createOperatingAccount(variousSpending, new AccountNumber("613001"), "Cash uitgaven");
        OperatingAccount amexAccount = ledger.createOperatingAccount(variousSpending, new AccountNumber("580007"), "Amex Credit Card");

        CategoryAccount exceptionalProfits = ledger.createCategoryAccount(resultsAccount, new AccountNumber("300100"), "Exceptional profits");
        OperatingAccount privateSalesAccount = ledger.createOperatingAccount(exceptionalProfits, new AccountNumber("400002"), "Private Sales");

        CategoryAccount fiscals = ledger.createCategoryAccount(resultsAccount, new AccountNumber("300110"), "Fiscals");
        CategoryAccount administration = ledger.createCategoryAccount(fiscals, new AccountNumber("300111"), "Administration");
        OperatingAccount administrationAccount = ledger.createOperatingAccount(administration, new AccountNumber("490001"), "Stad Gent");

        CategoryAccount financialCosts = ledger.createCategoryAccount(resultsAccount, new AccountNumber("300120"), "Financial Costs");
        OperatingAccount bankForfaitCostAccount = ledger.createOperatingAccount(financialCosts, new AccountNumber("656002"), "Kost van zichtrekeningen");
        OperatingAccount bankCenteaCostAccount = ledger.createOperatingAccount(financialCosts, new AccountNumber("656003"), "Centea account costs");

        CategoryAccount financialProfits = ledger.createCategoryAccount(resultsAccount, new AccountNumber("300130"), "Financial Profits");
        OperatingAccount intrestAccount = ledger.createOperatingAccount(financialProfits, new AccountNumber("656001"), "Interesten op zichtRekening");
        OperatingAccount intrestsSavingsAccountNilsAccount = ledger.createOperatingAccount(financialProfits, new AccountNumber("756004"), "Rente spaarrekening Nils");
        OperatingAccount intrestsSavingsAccountNeleAccount = ledger.createOperatingAccount(financialProfits, new AccountNumber("756005"), "Rente spaarrekening Nele");
        OperatingAccount intrestsSavingsAccountCenteaAccount = ledger.createOperatingAccount(financialProfits, new AccountNumber("756008"), "Rente spaarrekening Centea");

        CategoryAccount clothing = ledger.createCategoryAccount(resultsAccount, new AccountNumber("300140"), "Clothing");
        CategoryAccount clothingTom = ledger.createCategoryAccount(clothing, new AccountNumber("300141"), "Clothing");
        OperatingAccount proveaAccount = ledger.createOperatingAccount(clothingTom, new AccountNumber("440008"), "Provea");

        CategoryAccount housing = ledger.createCategoryAccount(resultsAccount, new AccountNumber("300150"), "Housing");
        CategoryAccount furniture = ledger.createCategoryAccount(housing, new AccountNumber("300151"), "Furniture");
        OperatingAccount webaAccount = ledger.createOperatingAccount(furniture, new AccountNumber("440011"), "Weba");
        OperatingAccount deschachtAccount = ledger.createOperatingAccount(housing, new AccountNumber("440014"), "Mega De Schacht");

        CategoryAccount luxuries = ledger.createCategoryAccount(resultsAccount, new AccountNumber("300160"), "Luxuries");
        OperatingAccount wijnhoekAccount = ledger.createOperatingAccount(luxuries, new AccountNumber("440015"), "De Wijnhoek (sigaren)");

        CategoryAccount domicily = ledger.createCategoryAccount(resultsAccount, new AccountNumber("300170"), "Domicily");
        OperatingAccount mortgageAccount = ledger.createOperatingAccount(domicily, new AccountNumber("200001"), "Mortgage house");
        OperatingAccount schuldSaldoTomAccount = ledger.createOperatingAccount(domicily, new AccountNumber("656004"), "SchuldSaldo Tom");
        OperatingAccount schuldSaldoCindyAccount = ledger.createOperatingAccount(domicily, new AccountNumber("656005"), "SchuldSaldo Cindy");

        CategoryAccount transport = ledger.createCategoryAccount(resultsAccount, new AccountNumber("300180"), "Transport");
        OperatingAccount carLoanAccount = ledger.createOperatingAccount(transport, new AccountNumber("200002"), "Car Loan");
        OperatingAccount carInsuranceAccount = ledger.createOperatingAccount(transport, new AccountNumber("440016"), "Car Insurance");

        CategoryAccount hobby = ledger.createCategoryAccount(resultsAccount, new AccountNumber("300190"), "Hobby");
        OperatingAccount funOostakkerAccount = ledger.createOperatingAccount(hobby, new AccountNumber("440017"), "Fun Oostakker");
        return ledger;
    }

}
