package bank.service.impl;

import org.springframework.security.core.userdetails.User.UserBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import bank.entity.User;
import bank.service.UserService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
		User user = userService.getUserByPhoneNumber(phoneNumber);
		if (user != null) {
			UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(phoneNumber);
			builder.password(user.getPassword());
			builder.roles(user.getRoles().stream().map(r -> r.getName()).toArray(String[]::new));
			return builder.build();
		}
		System.err.println("User \"" + phoneNumber + "\" not found!");
		throw new UsernameNotFoundException("User \"" + phoneNumber + "\" not found!");
	}
}