package bank.DTO;

import java.util.Date;
import java.util.List;

import bank.entity.Role;
import bank.entity.User;

public class UserDTO {
	private long id;
	private String phoneNumber;
	private String firstName;
	private String lastName;
	private Date dateOfBirth;
	private String address;
	private int numberOfAccounts;
	private double totalBalance;
	private List<Role> roles;

	public UserDTO() {
	}

	public UserDTO(User user, int numberOfAccounts, double totalBalance) {
		this.id = user.getId();
		this.phoneNumber = user.getPhoneNumber();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.dateOfBirth = user.getDateOfBirth();
		this.address = user.getAddress();
		this.numberOfAccounts = numberOfAccounts;
		this.totalBalance = totalBalance;
		this.roles = user.getRoles();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getNumberOfAccounts() {
		return numberOfAccounts;
	}

	public void setNumberOfAccounts(int numberOfAccounts) {
		this.numberOfAccounts = numberOfAccounts;
	}

	public double getTotalBalance() {
		return totalBalance;
	}

	public void setTotalBalance(double totalBalance) {
		this.totalBalance = totalBalance;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((dateOfBirth == null) ? 0 : dateOfBirth.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + numberOfAccounts;
		result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		long temp;
		temp = Double.doubleToLongBits(totalBalance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		UserDTO other = (UserDTO) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (dateOfBirth == null) {
			if (other.dateOfBirth != null)
				return false;
		} else if (!dateOfBirth.equals(other.dateOfBirth))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (id != other.id)
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (numberOfAccounts != other.numberOfAccounts)
			return false;
		if (phoneNumber == null) {
			if (other.phoneNumber != null)
				return false;
		} else if (!phoneNumber.equals(other.phoneNumber))
			return false;
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
			return false;
		if (Double.doubleToLongBits(totalBalance) != Double.doubleToLongBits(other.totalBalance))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserDTO [id=" + id + ", phoneNumber=" + phoneNumber + ", firstName=" + firstName + ", lastName="
				+ lastName + ", dateOfBirth=" + dateOfBirth + ", address=" + address + ", numberOfAccounts="
				+ numberOfAccounts + ", totalBalance=" + totalBalance + ", roles=" + roles + "]";
	}
}
