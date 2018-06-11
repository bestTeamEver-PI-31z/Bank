package bank.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Roles")
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String name;
	private boolean defaultRole;

	@ManyToMany(mappedBy = "roles")
	private List<User> users;

	public Role() {
	}

	public Role(String name) {
		this.name = name;
	}

	public Role(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Role(long id, String name, boolean defaultRole) {
		this.id = id;
		this.name = name;
		this.defaultRole = defaultRole;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDefaultRole() {
		return defaultRole;
	}

	public void setDefaultRole(boolean defaultRole) {
		this.defaultRole = defaultRole;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return name;
	}
}
