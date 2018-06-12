package test;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import bank.application.Utils;
import bank.application.WebApplication;
import bank.entity.Account;
import bank.entity.User;

public class InitData {
	public static List<User> users;
	public static List<Account> accounts;
	
	public void setup() throws ParseException {
		users = new ArrayList<>();
		User user = new User(1L, "1", "Oleh", "Batih", Utils.parseDate("07/22/2000"), "address1", "1");
		user.setRole(WebApplication.roles.get(0));
		users.add(user);
		user = new User(2L, "2", "Ivan", "Petrov", Utils.parseDate("01/02/2005"), "address2", "2");
		user.setRole(WebApplication.roles.get(0));
		users.add(user);
		user = new User(3L, "3", "Petro", "Huk", Utils.parseDate("10/12/1995"), "address3", "3");
		user.setRoles(WebApplication.roles);
		users.add(user);

		accounts = new ArrayList<>();
		accounts.add(new Account(1L, 1452.93));
		accounts.add(new Account(2L, 5));
	}
}
