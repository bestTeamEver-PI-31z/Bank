package bank.controller;

import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bank.application.Utils;
import bank.application.WebApplication;
import bank.entity.Account;
import bank.entity.Transaction;
import bank.entity.User;
import bank.service.AccountService;
import bank.service.UserService;

@Controller
@RequestMapping(value = "/users")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private AccountService accountService;

	@RequestMapping(value = { "" }, method = RequestMethod.GET)
	public String getAllUsers(Model model, Principal principal) {
		User user = userService.getUserByPrincipal(principal);
		if (user != null && user.isAdmin()) {
			model.addAttribute("users", userService.getAllUsers());
			return "users" + WebApplication.separator + "All users";
		}
		return WebApplication.accessDeniedPage;
	}

	@RequestMapping(value = { "/{userId}" }, method = RequestMethod.GET)
	public String getUser(Model model, @PathVariable("userId") Long userId, Principal principal) throws IOException {
		if (userService.hasAccess(principal, userId) != null) {
			model.addAttribute("user", userService.getUserById(userId));
			model.addAttribute("totalBalance", accountService.calculateTotalBalanceByUserId(userId));
			return "users" + WebApplication.separator + "User";
		}
		return WebApplication.accessDeniedPage;
	}

	@ResponseBody
	@RequestMapping(value = { "/{userId}" }, method = RequestMethod.DELETE)
	public String deleteUser(@PathVariable("userId") Long userId, Principal principal) {
		if (userService.hasAccess(principal, userId) != null) {
			try {
				Double totalBalance = accountService.calculateTotalBalanceByUserId(userId);
				if (totalBalance == null || totalBalance == 0) {
					userService.deleteUser(userId);
					return "/users";
				}
			} catch (Exception e) {
				System.err.println("Cannot remove user!");
				e.printStackTrace();
			}
			return "/users?error";
		}
		return WebApplication.accessDeniedPage;
	}

	@RequestMapping(value = { "/{userId}/accounts" }, method = RequestMethod.GET)
	public String getUserAccounts(@PathVariable("userId") Long userId, Principal principal) {
		if (userService.hasAccess(principal, userId) != null) {
			User user = userService.getUserById(userId);
			if (user == null) {
				return "redirect:/users/" + userId;
			}
			List<Account> accounts = user.getAccounts();
			if (!accounts.isEmpty()) {
				return "redirect:/users/" + userId + "/accounts/" + accounts.get(accounts.size() - 1).getId();
			} else {
				return "redirect:/users/" + userId + "/accounts/new";
			}
		}
		return WebApplication.accessDeniedPage;
	}

	@RequestMapping(value = { "/{userId}/accounts" }, method = RequestMethod.POST)
	public String addUserAccounts(@PathVariable("userId") Long userId, @RequestParam(value = "amount") Double amount, Principal principal) {
		try {
			if (userService.hasAccess(principal, userId) != null) {
				User user = userService.getUserById(userId);
				if (user == null) {
					return "redirect:/users/" + userId;
				}
				accountService.saveAccount(amount, user);
				return "redirect:/users/" + userId + "/accounts";
			}
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return WebApplication.accessDeniedPage;
	}

	@RequestMapping(value = { "/{userId}/accounts/{accountId}" }, method = RequestMethod.GET)
	public String getUserAccount(
			@PathVariable("userId") Long userId,
			@PathVariable("accountId") String accountId,
			@RequestParam(name = "fromDate", required = false) String fromDate,
			@RequestParam(name = "toDate", required = false) String toDate, 
			Model model, Principal principal) {
		if (userService.hasAccess(principal, userId) != null) {
			User user = userService.getUserById(userId);
			if (user == null) {
				return "redirect:/users/" + userId;
			}
			model.addAttribute("user", user);
			model.addAttribute("totalBalance", accountService.calculateTotalBalanceByUserId(userId));
			List<Account> accounts = new ArrayList<Account>();
			accounts.addAll(user.getAccounts());
			Collections.reverse(accounts);
			model.addAttribute("accounts", accounts);
			boolean addNew = false;
			if (accountId.equalsIgnoreCase("new")) {
				addNew = true;
			} else {
				Account account = accountService.getAccount(Long.valueOf(accountId), userId);
				if (account == null && !addNew) {
					return "redirect:/users/" + userId + "/accounts";
				}
				model.addAttribute("account", account);
				List<Transaction> transactions = new ArrayList<Transaction>();
				if (!addNew) {
					if (StringUtils.isNotBlank(fromDate) && StringUtils.isNotBlank(toDate)) {
						try {
							transactions = accountService.getTransfersByDates(Long.valueOf(accountId), Utils.parseDate(fromDate), Utils.parseDate(toDate));
							model.addAttribute("fromDate", fromDate);
							model.addAttribute("toDate", toDate);
						} catch (ParseException e) {
							System.err.println("Incorrect date!\n" + e);
						}
					} else {
						transactions = accountService.getTransferHistoryByAccountId(Long.valueOf(accountId));
					}
					model.addAttribute("history", transactions);
				}
			}
			model.addAttribute("addNew", addNew);
			return "accounts" + WebApplication.separator + "Account";
		}
		return WebApplication.accessDeniedPage;
	}

	@RequestMapping(value = { "/{userId}/accounts/{accountId}/delete" }, method = RequestMethod.GET)
	public String deleteUserAccount(
			@PathVariable("userId") Long userId,
			@PathVariable("accountId") Long accountId, 
			Model model, Principal principal) {
		if (userService.hasAccess(principal, userId) != null) {
			User user = userService.getUserById(userId);
			if (user == null) {
				return "redirect:/users/" + userId;
			}
			model.addAttribute("user", user);
			Account account = accountService.getAccount(accountId, userId);
			if (account != null)
				if (account.getBalance() > 0) {
					model.addAttribute("info", "You need transfer your money to close this account!");
					model.addAttribute("redirectUrl", "/users/" + userId + "/accounts/" + accountId + "/delete");
					model.addAttribute("selectedAccountId", account.getId());
					return transfer(userId, model, principal);
				} else {
					accountService.deleteAccount(account.getId());
					return "redirect:/users/" + userId + "/accounts";
				}
			return "redirect:/users/" + userId + "/accounts";
		}
		return WebApplication.accessDeniedPage;
	}

	@RequestMapping(value = { "/{userId}/deposit" }, method = RequestMethod.GET)
	public String deposit(@PathVariable("userId") Long userId, Model model, Principal principal) {
		if (userService.hasAccess(principal, userId) != null) {
			User user = userService.getUserById(userId);
			if (user == null) {
				return "redirect:/users/" + userId;
			}
			model.addAttribute("user", user);
			model.addAttribute("totalBalance", accountService.calculateTotalBalanceByUserId(userId));
			List<Account> accounts = new ArrayList<Account>();
			accounts.addAll(user.getAccounts());
			Collections.reverse(accounts);
			model.addAttribute("accounts", accounts);
			return "accounts" + WebApplication.separator + "Deposit";
		}
		return WebApplication.accessDeniedPage;
	}

	@RequestMapping(value = { "/{userId}/deposit" }, method = RequestMethod.POST)
	public String deposit(
			@PathVariable("userId") Long userId, 
			@RequestParam(value = "account") Long accountId,
			@RequestParam(value = "amount") Double amount, 
			Model model, Principal principal) {
		if (userService.hasAccess(principal, userId) != null) {
			User user = userService.getUserById(userId);
			if (user == null) {
				return "redirect:/users/" + userId;
			}
			model.addAttribute("user", user);
			Account account = accountService.getAccount(accountId, userId);
			if (account != null) {
				accountService.transfer(null, account, amount);
				model.addAttribute("success", true);
			} else {
				model.addAttribute("error", "Cannot find account!");
			}
			return deposit(userId, model, principal);
		}
		return WebApplication.accessDeniedPage;
	}

	@RequestMapping(value = { "/{userId}/transfer" }, method = RequestMethod.GET)
	public String transfer(@PathVariable("userId") Long userId, Model model, Principal principal) {
		if (userService.hasAccess(principal, userId) != null) {
			User user = userService.getUserById(userId);
			if (user == null) {
				return "redirect:/users/" + userId;
			}
			model.addAttribute("user", user);
			model.addAttribute("totalBalance", accountService.calculateTotalBalanceByUserId(userId));
			List<Account> accounts = new ArrayList<Account>();
			accounts.addAll(user.getAccounts());
			Collections.reverse(accounts);
			model.addAttribute("accounts", accounts);
			return "accounts" + WebApplication.separator + "Transfer";
		}
		return WebApplication.accessDeniedPage;
	}

	@RequestMapping(value = { "/{userId}/transfer" }, method = RequestMethod.POST)
	public String transfer(
			@PathVariable("userId") Long userId, 
			@RequestParam(value = "from") String from,
			@RequestParam(value = "to") String to, 
			@RequestParam(value = "accountNumber") String accountNumber,
			@RequestParam(value = "amount") Double amount, 
			@RequestParam(value = "redirectUrl") String redirectUrl,
			Model model, Principal principal) {
		if (userService.hasAccess(principal, userId) != null) {
			User user = userService.getUserById(userId);
			if (user == null) {
				return "redirect:/users/" + userId;
			}
			if (to.equals("0")) {
				to = accountNumber;
			}
			String error = accountService.transfer(from, to, amount, principal);
			model.addAttribute("error", error);
			model.addAttribute("success", StringUtils.isBlank(error));
			if (StringUtils.isNotBlank(redirectUrl)) {
				return "redirect:" + redirectUrl;
			}
			return transfer(userId, model, principal);
		}
		return WebApplication.accessDeniedPage;
	}
}
