package bank.service.impl;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bank.DAO.AccountDAO;
import bank.DAO.TransactionDAO;
import bank.entity.Account;
import bank.entity.Transaction;
import bank.entity.User;
import bank.service.AccountService;
import bank.service.UserService;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
	@Autowired
	private AccountDAO accountDAO;
	@Autowired
	private TransactionDAO transactionDAO;
	@Autowired
	private UserService userService;

	public Account saveAccount(Account account) {
		return accountDAO.save(account);
	}

	public Transaction saveAccount(Double amount, User user) {
		return transfer(null, saveAccount(new Account(0, user)), amount);
	}
	
	public boolean deleteAccount(Long id) {
		accountDAO.deleteById(id);
		return true;
	}

	public List<Account> getAllAccounts() {
		return accountDAO.findAll();
	}
	
	public List<Account> getAccountsByUserId(Long userId) {
		return accountDAO.findAccountsByUserId(userId);
	}

	public Account getAccountById(String id) {
		if (StringUtils.isNumeric(id)) {
			return getAccountById(Long.valueOf(id));
		}
		return null;
	}
	
	public Account getAccountById(Long id) {
		if (accountDAO.existsById(id)) {
			return accountDAO.getOne(id);
		}
		return null;
	}
	
	public Account getAccount(Long accountId, Long userId) {
		Account account = getAccountById(accountId);
		if (account != null && account.getUser() != null && account.getUser().getId() == userId) {
			return account;
		}
		return null;
	}

	public List<Transaction> getTransfersByDates(Long accountId, Date fromDate, Date toDate) {
		toDate = new Date(toDate.getTime() + TimeUnit.DAYS.toMillis(1));
		return transactionDAO.findTransfersByDates(accountId, fromDate, toDate);
	}

	public String transfer(String fromNum, String toNum, Double amount, Principal principal) {
		User user = userService.getUserByPrincipal(principal);
		Account accountFrom = getAccountById(getClearAccountNumber(fromNum));
		if (accountFrom == null) {
			return "Incorrect account number!\nCannot find account (from)";
		}
		if (user != null && (user.isAdmin() || accountFrom.getUser().getId() == user.getId())) {
			toNum = getClearAccountNumber(toNum);
			if (toNum.length() > 16) {
				return "Incorrect account number!\nCannot find account (to)";
			}
			Account accountTo = getAccountById(toNum);
			if (accountTo == null) {
				return "Incorrect account number!\nCannot find account (to)";
			}
			if (amount == 0) {
				return "Cannot transfer $0!";
			} else if ((accountFrom.getBalance() - amount) < 0) {
				return "Not enough money!";
			}
			transfer(accountFrom, accountTo, amount);
			return null;
		}
		return "Cannot transfer!\nError: 403 Forbidden!";
	}
	
	public Transaction transfer(Account accountFrom, Account accountTo, Double amount) {
		Transaction transaction = null;
		if(accountFrom != null) {
			transaction = transactionDAO.save(new Transaction(accountFrom, accountTo, amount));
			accountFrom.withdraw(amount);
			accountTo.deposit(amount);
			return transaction;
		} else {
			transaction = transactionDAO.save(new Transaction(accountTo, amount));
			accountTo.deposit(amount);
			return transaction;
		}
	}

	private String getClearAccountNumber(String accountNumber) {
		String clearAccountNumber = accountNumber.trim();
		while (clearAccountNumber.startsWith("0") || clearAccountNumber.startsWith(" ")) {
			clearAccountNumber = clearAccountNumber.substring(1, clearAccountNumber.length());
		}
		return clearAccountNumber;
	}
	
	public List<Transaction> getTransferHistoryByAccountId(Long accountId) {
		return transactionDAO.findTransferHistoryByAccountId(accountId);
	}

	public Double calculateTotalBalanceByUserId(Long userId) {
		return accountDAO.calculateTotalBalanceByUserId(userId);
	}

	public Integer calculateNumberOfAccountsByUserId(Long userId) {
		return accountDAO.calculateNumberOfAccountsByUserId(userId);
	}
}
