package bank.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import bank.application.Utils;

@Entity
@Table(name = "Accounts")
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private double balance;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
	private User user;

	public Account() {
	}

	public Account(double balance, User user) {
		this.balance = balance;
		this.user = user;
	}

	public Account(long id, double balance) {
		this.id = id;
		this.balance = balance;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFormatedAccountNumber() {
		return Utils.formatAccountNumber(String.valueOf(this.id));
	}

	public String getShortAccountNumber() {
		return Utils.formatShortAccountNumber(String.valueOf(this.id));
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean withdraw(double amount) {
		if (amount > 0 && (balance - amount) >= 0) {
			this.balance -= amount;
			return true;
		}
		return false;
	}

	public boolean deposit(double amount) {
		if (amount > 0) {
			this.balance += amount;
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(balance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (Double.doubleToLongBits(balance) != Double.doubleToLongBits(other.balance))
			return false;
		if (id != other.id)
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Account [id=" + id + ", balance=" + balance + ", user=" + user + "]";
	}
}
