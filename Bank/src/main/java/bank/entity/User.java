package bank.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name = "Users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String phoneNumber;
	private String firstName;
	private String lastName;
	private Date dateOfBirth;
	private String address;
	private String password;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	private List<Role> roles;

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "user")
	private List<Account> accounts = new ArrayList<Account>();
	
	public User() {
	}
	
	public User(User user) {
		this.id = user.getId();
		this.phoneNumber = user.getPhoneNumber();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.dateOfBirth = user.getDateOfBirth();
		this.address = user.getAddress();
	}
	
	public User(String phoneNumber, String firstName, String lastName, Date dateOfBirth, String address, String password) {
		this.phoneNumber = phoneNumber;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
		this.address = address;
		this.password = password;
	}

	public User(long id, String phoneNumber, String firstName, String lastName, Date dateOfBirth, String address, String password) {
		this.id = id;
		this.phoneNumber = phoneNumber;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
		this.address = address;
		this.password = password;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public void setRole(Role role) {
		this.roles = new ArrayList<Role>();
		this.roles.add(role);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	public boolean isAdmin() {
		try {
			return (getRoles().stream().filter(r -> r.getName().equalsIgnoreCase("ADMIN")).findAny().get()) != null;
		} catch (NoSuchElementException e) {}
		return false;
	}

	public boolean isValid() {
		return  StringUtils.isNotBlank(getPhoneNumber()) && 
				StringUtils.isNotBlank(getFirstName()) && 
				StringUtils.isNotBlank(getLastName()) && 
				StringUtils.isNotBlank(getPassword());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accounts == null) ? 0 : accounts.hashCode());
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((dateOfBirth == null) ? 0 : dateOfBirth.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
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
		User other = (User) obj;
		if (accounts == null) {
			if (other.accounts != null)
				return false;
		} else if (!accounts.equals(other.accounts))
			return false;
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
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
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
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", phoneNumber=" + phoneNumber + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", dateOfBirth=" + dateOfBirth + ", address=" + address + ", password=" + password + ", roles="
				+ roles + "]";
	}
}
