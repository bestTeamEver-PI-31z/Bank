package bank.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Transactions")
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private Date date;
	private double amount;
	private double balanceBeforeTransferFrom;
	private double balanceBeforeTransferTo;
	private long accountNumberFrom;
	private long accountNumberTo;
	private String descriptionFrom;
	private String descriptionTo;
	private long userId;

	public Transaction() {
		this.date = new Date();
	}

	public Transaction(Account accountFrom, Account accountTo, double amount) {
		this.balanceBeforeTransferFrom = accountFrom.getBalance();
		this.balanceBeforeTransferTo = accountTo.getBalance();
		this.accountNumberFrom = accountFrom.getId();
		this.accountNumberTo = accountTo.getId();
		this.amount = amount;
		this.date = new Date();
		this.userId = accountFrom.getUser().getId();
		this.descriptionFrom = "from " + accountFrom.getFormatedAccountNumber() + " (" + accountFrom.getUser().getFirstName() + " " + accountFrom.getUser().getLastName() + ")";
		this.descriptionTo = "to " + accountTo.getFormatedAccountNumber() + " (" + accountTo.getUser().getFirstName() + " " + accountTo.getUser().getLastName() + ")";
	}

	public Transaction(Account accountTo, double amount) {
		this.balanceBeforeTransferTo = accountTo.getBalance();
		this.accountNumberTo = accountTo.getId();
		this.amount = amount;
		this.date = new Date();
		this.userId = accountTo.getUser().getId();
		this.descriptionTo = "to " + accountTo.getFormatedAccountNumber() + " (" + accountTo.getUser().getFirstName() + " " + accountTo.getUser().getLastName() + ")";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public long getAccountNumberFrom() {
		return accountNumberFrom;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setAccountNumberFrom(long accountNumberFrom) {
		this.accountNumberFrom = accountNumberFrom;
	}

	public long getAccountNumberTo() {
		return accountNumberTo;
	}

	public void setAccountNumberTo(long accountNumberTo) {
		this.accountNumberTo = accountNumberTo;
	}

	public double getBalanceBeforeTransferFrom() {
		return balanceBeforeTransferFrom;
	}

	public void setBalanceBeforeTransferFrom(double balanceBeforeTransferFrom) {
		this.balanceBeforeTransferFrom = balanceBeforeTransferFrom;
	}

	public double getBalanceBeforeTransferTo() {
		return balanceBeforeTransferTo;
	}

	public void setBalanceBeforeTransferTo(double balanceBeforeTransferTo) {
		this.balanceBeforeTransferTo = balanceBeforeTransferTo;
	}

	public String getDescriptionFrom() {
		return descriptionFrom;
	}

	public void setDescriptionFrom(String descriptionFrom) {
		this.descriptionFrom = descriptionFrom;
	}

	public String getDescriptionTo() {
		return descriptionTo;
	}

	public void setDescriptionTo(String descriptionTo) {
		this.descriptionTo = descriptionTo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (accountNumberFrom ^ (accountNumberFrom >>> 32));
		result = prime * result + (int) (accountNumberTo ^ (accountNumberTo >>> 32));
		long temp;
		temp = Double.doubleToLongBits(amount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(balanceBeforeTransferFrom);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(balanceBeforeTransferTo);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (userId ^ (userId >>> 32));
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
		Transaction other = (Transaction) obj;
		if (accountNumberFrom != other.accountNumberFrom)
			return false;
		if (accountNumberTo != other.accountNumberTo)
			return false;
		if (Double.doubleToLongBits(amount) != Double.doubleToLongBits(other.amount))
			return false;
		if (Double.doubleToLongBits(balanceBeforeTransferFrom) != Double
				.doubleToLongBits(other.balanceBeforeTransferFrom))
			return false;
		if (Double.doubleToLongBits(balanceBeforeTransferTo) != Double.doubleToLongBits(other.balanceBeforeTransferTo))
			return false;
		if (id != other.id)
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Transaction [id=" + id + ", date=" + date + ", amount=" + amount + ", balanceBeforeTransferFrom="
				+ balanceBeforeTransferFrom + ", balanceBeforeTransferTo=" + balanceBeforeTransferTo
				+ ", accountNumberFrom=" + accountNumberFrom + ", accountNumberTo=" + accountNumberTo
				+ ", descriptionFrom=" + descriptionFrom + ", descriptionTo=" + descriptionTo + ", userId=" + userId
				+ "]";
	}
}
