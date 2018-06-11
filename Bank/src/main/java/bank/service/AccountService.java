package bank.service;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import bank.entity.Account;
import bank.entity.Transaction;
import bank.entity.User;

public interface AccountService {
	public Account saveAccount(Account account);

	public Transaction saveAccount(Double amount, User user);

	public boolean deleteAccount(Long id);

	public List<Account> getAllAccounts();

	public List<Account> getAccountsByUserId(Long userId);

	public Account getAccountById(Long id);

	public Account getAccountById(String id);

	public Account getAccount(Long accountId, Long userId);

	public List<Transaction> getTransfersByDates(Long accountId, Date fromDate, Date toDate);

	public String transfer(String from, String to, Double amount, Principal principal);

	public Transaction transfer(Account accountFrom, Account accountTo, Double amount);

	public List<Transaction> getTransferHistoryByAccountId(Long accountId);

	public Double calculateTotalBalanceByUserId(Long userId);

	public Integer calculateNumberOfAccountsByUserId(Long userId);
}
