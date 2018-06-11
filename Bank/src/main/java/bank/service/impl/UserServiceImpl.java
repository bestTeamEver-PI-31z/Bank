package bank.service.impl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bank.DAO.UserDAO;
import bank.DTO.UserDTO;
import bank.application.WebApplication;
import bank.entity.Role;
import bank.entity.User;
import bank.service.AccountService;
import bank.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private AccountService accountService;
	@Autowired
	private BCryptPasswordEncoder encoder;

	public User saveUser(User user) {
		if (user != null && user.isValid()) {
			if (user.getRoles() == null || (user.getRoles() != null && user.getRoles().isEmpty())) {
				Role role = WebApplication.roles.stream().filter(r -> r.isDefaultRole()).findFirst().get();
				user.setRole(role);
			}
			user.setPassword(encoder.encode(user.getPassword()));
			return userDAO.save(user);
		}
		return null;
	}

	public boolean deleteUser(Long id) {
		User user = userDAO.getOne(id);
		if (user != null) {
			user.setRole(null);
			userDAO.delete(user);
			return true;
		}
		return false;
	}

	public List<UserDTO> getAllUsers() {
		List<User> users = userDAO.findAll();
		List<UserDTO> usersDTO = new ArrayList<UserDTO>();
		for (User user : users) {
			usersDTO.add(toUserDTO(user));
		}
		return usersDTO;
	}

	public UserDTO toUserDTO(User user) {
		Integer numberOfAccounts = accountService.calculateNumberOfAccountsByUserId(user.getId());
		Double totalBalance = accountService.calculateTotalBalanceByUserId(user.getId());
		return new UserDTO(user, numberOfAccounts == null ? 0 : numberOfAccounts, totalBalance == null ? 0 : totalBalance);
	}

	public User getUserById(Long id) {
		return userDAO.getOne(id);
	}

	@Cacheable("principalUser")
	public User getUserByPrincipal(Principal principal) {
		if (principal != null) {
			return getUserByPhoneNumber(principal.getName());
		}
		return null;
	}

	public User getUserByPhoneNumber(String phoneNumber) {
		return userDAO.findUserByPhoneNumber(phoneNumber);
	}

	public boolean existsUserByPhoneNumber(String phoneNumber) {
		return getUserByPhoneNumber(phoneNumber) != null;
	}

	public User hasAccess(Principal principal, Long userId) {
		User user = getUserByPrincipal(principal);
		if (user != null && (user.isAdmin() || user.getId() == userId)) {
			return user;
		}
		return null;
	}
}
