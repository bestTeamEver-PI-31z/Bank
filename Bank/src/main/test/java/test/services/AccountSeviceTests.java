package test.services;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import bank.DAO.AccountDAO;
import bank.DAO.TransactionDAO;
import bank.application.Utils;
import bank.entity.Account;
import bank.entity.Transaction;
import bank.entity.User;
import bank.service.AccountService;
import bank.service.UserService;
import bank.service.impl.AccountServiceImpl;
import test.InitData;

@RunWith(SpringRunner.class)
public class AccountSeviceTests extends InitData {
	@TestConfiguration
    static class AccountServiceImplTestContextConfiguration {
        @Bean
        public AccountService accountService() {
            return new AccountServiceImpl();
        }
    }

    @Autowired
    private AccountService accountService;

    @MockBean
	private UserService userService;
    
    @MockBean
    private AccountDAO accountDAO;
	
    @MockBean
	private TransactionDAO transactionDAO;
	
    @Before
    public void setup() throws ParseException {
    	super.setup();
    	MockitoAnnotations.initMocks(this);
    }

    @Test
    public void saveAccountTest() {
    	User user = users.get(0);
		Double amount = 196.55D;
		Account newAccount = new Account(0, user);
		Transaction transaction = new Transaction(newAccount, 0);

    	when(accountDAO.save(newAccount)).thenReturn(newAccount);
    	
    	assertEquals(newAccount, accountService.saveAccount(newAccount));

    	when(transactionDAO.save(any())).thenReturn(transaction);

    	assertEquals(transaction, accountService.saveAccount(amount, user));

    	verify(accountDAO, times(1)).save(newAccount);
    	verify(accountDAO, times(1)).save(new Account(0, user));
    	verify(transactionDAO, times(1)).save(any());
    	verifyNoMoreInteractions(accountDAO);
		verifyNoMoreInteractions(transactionDAO);
	}
    
    @Test
    public void deleteAccountTest() {
    	User user = users.get(0);
    	
    	assertTrue(accountService.deleteAccount(user.getId()));
    	
    	verify(accountDAO, times(1)).deleteById(user.getId());
    	verifyNoMoreInteractions(accountDAO);
    }
    
    @Test
    public void getAllAccountsTest() {
    	List<Account> accountsFromService = null;
    	
    	accountsFromService = accountService.getAllAccounts();
    	assertTrue(accountsFromService.isEmpty());
    	
    	when(accountDAO.findAll()).thenReturn(accounts);

    	accountsFromService = accountService.getAllAccounts();
    	assertNotNull(accountsFromService);
    	assertFalse(accountsFromService.isEmpty());
    	assertTrue(accountsFromService.size() == accounts.size());
    	for (Account account : accounts) {
    		assertTrue(accountsFromService.contains(account));
		}
    	
    	verify(accountDAO, times(2)).findAll();
    	verifyNoMoreInteractions(accountDAO);
    }
    
    @Test
    public void getAccountsByUserIdTest() {
    	List<Account> accountsFromService = null;
    	User user = users.get(0);
    	
    	accountsFromService = accountService.getAccountsByUserId(user.getId());
    	assertTrue(accountsFromService.isEmpty());
    	
    	when(accountDAO.findAccountsByUserId(user.getId())).thenReturn(accounts);

    	accountsFromService = accountService.getAccountsByUserId(user.getId());
    	assertNotNull(accountsFromService);
    	assertFalse(accountsFromService.isEmpty());
    	assertTrue(accountsFromService.size() == accounts.size());
    	for (Account account : accounts) {
    		assertTrue(accountsFromService.contains(account));
		}
    	
    	verify(accountDAO, times(2)).findAccountsByUserId(user.getId());
    	verifyNoMoreInteractions(accountDAO);
    }
    
    @Test
    public void getAccountByIdTest() {
    	Account expectedAccount = accounts.get(0);
    	Account accountFromService = null;
    	
    	accountFromService = accountService.getAccountById(expectedAccount.getId());
    	assertNull(accountFromService);

    	when(accountDAO.existsById(expectedAccount.getId())).thenReturn(true);
    	accountFromService = accountService.getAccountById(expectedAccount.getId());
    	assertNull(accountFromService);

    	when(accountDAO.getOne(expectedAccount.getId())).thenReturn(expectedAccount);
    	accountFromService = accountService.getAccountById(expectedAccount.getId());
    	assertNotNull(accountFromService);
    	assertEquals(expectedAccount, accountFromService);
    	
    	accountFromService = accountService.getAccountById(String.valueOf(expectedAccount.getId()));
    	assertNotNull(accountFromService);
    	assertEquals(expectedAccount, accountFromService);
    	
    	accountFromService = accountService.getAccountById(String.valueOf(expectedAccount.getId() * 3));
    	assertNull(accountFromService);
    	
    	accountFromService = accountService.getAccountById(StringUtils.EMPTY);
    	assertNull(accountFromService);
  	
    	verify(accountDAO, times(4)).existsById(expectedAccount.getId());
    	verify(accountDAO, times(1)).existsById(expectedAccount.getId() * 3);
    	verify(accountDAO, times(3)).getOne(expectedAccount.getId());
    	verifyNoMoreInteractions(accountDAO);
    }

    @Test
    public void getAccountTest() {
    	User user = users.get(0);
    	Account expectedAccount = accounts.get(0);
    	expectedAccount.setUser(user);
    	Account accountFromService = null;
    	
    	when(accountDAO.existsById(expectedAccount.getId())).thenReturn(true);
    	
    	accountFromService = accountService.getAccount(expectedAccount.getId(), user.getId());
    	assertNull(accountFromService);
    	
    	when(accountDAO.getOne(expectedAccount.getId())).thenReturn(expectedAccount);
    	
    	accountFromService = accountService.getAccount(expectedAccount.getId(), user.getId());
    	assertNotNull(accountFromService);
    	assertEquals(expectedAccount, accountFromService);
    	
    	accountFromService = accountService.getAccount(expectedAccount.getId(), user.getId() * 2);
    	assertNull(accountFromService);
    	
    	accountFromService = accountService.getAccount(expectedAccount.getId() * 3, user.getId());
    	assertNull(accountFromService);
    	
    	when(accountDAO.existsById(expectedAccount.getId())).thenReturn(false);
    	
    	accountFromService = accountService.getAccount(expectedAccount.getId(), user.getId());
    	assertNull(accountFromService);
    	
    	verify(accountDAO, times(4)).existsById(expectedAccount.getId());
    	verify(accountDAO, times(1)).existsById(expectedAccount.getId() * 3);
    	verify(accountDAO, times(3)).getOne(expectedAccount.getId());
    	verifyNoMoreInteractions(accountDAO);
    }
    
    @Test
    public void getTransfersByDatesTest() throws Exception {
    	User user = users.get(0);
		Account account = new Account(40, user);
		List<Transaction> expectedTransactions = new ArrayList<Transaction>();
		expectedTransactions.add(new Transaction(account, 45));
		expectedTransactions.add(new Transaction(account, 1002));
		expectedTransactions.add(new Transaction(account, 0));
		
		Date fromDate = Utils.parseDate("04/26/2018");
		Date toDate = Utils.parseDate("04/27/2018");
		List<Transaction> transactionsFromService = null;
		
		transactionsFromService = accountService.getTransfersByDates(account.getId(), fromDate, fromDate);
		assertNotNull(transactionsFromService);
		assertTrue(transactionsFromService.isEmpty());
		
		when(transactionDAO.findTransfersByDates(account.getId(), fromDate, toDate))
			.thenReturn(expectedTransactions);
		
		transactionsFromService = accountService.getTransfersByDates(account.getId(), fromDate, fromDate);
		assertNotNull(transactionsFromService);
		assertFalse(transactionsFromService.isEmpty());
		assertTrue(transactionsFromService.size() == expectedTransactions.size());
		for (Transaction transaction : expectedTransactions) {
			assertTrue(transactionsFromService.contains(transaction));
		}
		
		verify(transactionDAO, times(2)).findTransfersByDates(account.getId(), fromDate, toDate);
    	verifyNoMoreInteractions(transactionDAO);
    }
    
    @Test
    public void transferTest() {
    	User user = users.get(0);
    	Double amount = 34D;
    	Account accountFrom = new Account(40, user);
    	accountFrom.setId(1);
		Account accountTo = new Account(0, user);
		accountTo.setId(2);
		String errorMessage = null;
		
		errorMessage = accountService.transfer(accountFrom.getFormatedAccountNumber(), accountTo.getFormatedAccountNumber(), amount, any());
		assertNotNull(errorMessage);
		assertEquals("Incorrect account number!\nCannot find account (from)", errorMessage);
		
		when(accountDAO.getOne(accountFrom.getId())).thenReturn(accountFrom);
		when(accountDAO.existsById(any())).thenReturn(true);
		
		errorMessage = accountService.transfer(accountFrom.getFormatedAccountNumber(), accountTo.getFormatedAccountNumber(), amount, any());
		assertNotNull(errorMessage);
		assertEquals("Cannot transfer!\nError: 403 Forbidden!", errorMessage);
		
		when(userService.getUserByPrincipal(any())).thenReturn(user);

		errorMessage = accountService.transfer(accountFrom.getFormatedAccountNumber(), accountTo.getFormatedAccountNumber().concat("123"), amount, any());
		assertNotNull(errorMessage);
		assertEquals("Incorrect account number!\nCannot find account (to)", errorMessage);
		
		errorMessage = accountService.transfer(accountFrom.getFormatedAccountNumber(), accountTo.getFormatedAccountNumber(), amount, any());
		assertNotNull(errorMessage);
		assertEquals("Incorrect account number!\nCannot find account (to)", errorMessage);

		when(accountDAO.getOne(accountTo.getId())).thenReturn(accountTo);

		errorMessage = accountService.transfer(accountFrom.getFormatedAccountNumber(), accountTo.getFormatedAccountNumber(), 0D, any());
		assertNotNull(errorMessage);
		assertEquals("Cannot transfer $0!", errorMessage);
		
		errorMessage = accountService.transfer(accountFrom.getFormatedAccountNumber(), accountTo.getFormatedAccountNumber(), (amount + 1) * 2, any());
		assertNotNull(errorMessage);
		assertEquals("Not enough money!", errorMessage);
		
		errorMessage = accountService.transfer(accountFrom.getFormatedAccountNumber(), accountTo.getFormatedAccountNumber(), (amount + 1) * 2, any());
		assertNotNull(errorMessage);
		assertEquals("Not enough money!", errorMessage);

		errorMessage = accountService.transfer(accountFrom.getFormatedAccountNumber(), accountTo.getFormatedAccountNumber(), amount, any());
		assertNull(errorMessage);
		
		Transaction transaction = new Transaction(accountFrom, accountTo, amount);
		Transaction depositTransaction = new Transaction(accountTo, amount * 2);
		
		when(transactionDAO.save(transaction)).thenReturn(transaction);
		assertEquals(transaction, accountService.transfer(accountFrom, accountTo, amount));
		
		when(transactionDAO.save(any())).thenReturn(depositTransaction);
		assertEquals(depositTransaction, accountService.transfer(null, accountTo, amount * 2));
		
		verify(accountDAO, times(7)).getOne(accountFrom.getId());
		verify(accountDAO, times(5)).getOne(accountTo.getId());
		verify(accountDAO, times(13)).existsById(any());
		verify(transactionDAO, times(3)).save(any());
		verify(userService, times(8)).getUserByPrincipal(any());
		verifyNoMoreInteractions(accountDAO);
    	verifyNoMoreInteractions(transactionDAO);
    	verifyNoMoreInteractions(userService);
    }
    
    @Test
    public void getTransferHistoryByAccountIdTest() {
    	User user = users.get(0);
    	Account account = new Account(5640, user);
    	account.setId(1);
    	List<Transaction> expectedTransactions = new ArrayList<Transaction>();
		expectedTransactions.add(new Transaction(account, 45));
		expectedTransactions.add(new Transaction(account, 1002));
		expectedTransactions.add(new Transaction(account, 0));
		List<Transaction> transactionsFromService = null;

    	transactionsFromService = transactionDAO.findTransferHistoryByAccountId(account.getId());
    	assertNotNull(transactionsFromService);
    	assertTrue(transactionsFromService.isEmpty());
    	
    	when(transactionDAO.findTransferHistoryByAccountId(account.getId())).thenReturn(expectedTransactions);
    	
    	transactionsFromService = transactionDAO.findTransferHistoryByAccountId(account.getId());
    	assertNotNull(transactionsFromService);
    	assertFalse(transactionsFromService.isEmpty());
    	assertTrue(transactionsFromService.size() == expectedTransactions.size());
    	for (Transaction transaction : expectedTransactions) {
    		assertTrue(transactionsFromService.contains(transaction));
		}
    	
    	verify(transactionDAO, times(2)).findTransferHistoryByAccountId(account.getId());
		verifyNoMoreInteractions(transactionDAO);
    }
    
    @Test
    public void calculateTotalBalanceByUserIdTest() {
    	User user = users.get(0);
    	Double expectedTotalBalance = 2324D;
    	Double totalBalance = null;
    	
    	totalBalance = accountDAO.calculateTotalBalanceByUserId(user.getId());
    	assertNotNull(totalBalance);
    	assertTrue(totalBalance == 0);
    	
    	when(accountDAO.calculateTotalBalanceByUserId(user.getId())).thenReturn(expectedTotalBalance);
    	
    	totalBalance = accountDAO.calculateTotalBalanceByUserId(user.getId());
    	assertNotNull(totalBalance);
    	assertEquals(expectedTotalBalance, totalBalance);
    	
    	verify(accountDAO, times(2)).calculateTotalBalanceByUserId(user.getId());
		verifyNoMoreInteractions(accountDAO);
    }
    
    @Test
    public void calculateNumberOfAccountsByUserIdTest() {
    	User user = users.get(0);
    	int expectedNumberOfAccounts = 5;
    	int numberOfAccounts = 0;
    	
    	numberOfAccounts = accountDAO.calculateNumberOfAccountsByUserId(user.getId());
    	assertNotNull(numberOfAccounts);
    	assertTrue(numberOfAccounts == 0);
    	
    	when(accountDAO.calculateNumberOfAccountsByUserId(user.getId())).thenReturn(expectedNumberOfAccounts);
    	
    	numberOfAccounts = accountDAO.calculateNumberOfAccountsByUserId(user.getId());
    	assertNotNull(numberOfAccounts);
    	assertEquals(expectedNumberOfAccounts, numberOfAccounts);
    	
    	verify(accountDAO, times(2)).calculateNumberOfAccountsByUserId(user.getId());
		verifyNoMoreInteractions(accountDAO);
    }
}
