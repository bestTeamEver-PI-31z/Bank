package test;

import java.util.NoSuchElementException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;

import bank.entity.User;

public class AuthenticationImitation extends InitData {
	private static UserDetailsService userDetailsService = new UserDetailsService() {
		public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
			User user = null;
			try {
				user = users.stream().filter(u -> u.getPhoneNumber().equals(phoneNumber)).findFirst().get();
			} catch (NoSuchElementException e) {}
			UserBuilder builder = null;
			if (user != null && user.getPhoneNumber().equals(phoneNumber)) {
				builder = org.springframework.security.core.userdetails.User.withUsername(phoneNumber);
				builder.password(user.getPassword());
				builder.roles(user.getRoles().stream().map(r -> r.getName()).toArray(String[]::new));
				return builder.build();
			}
			System.err.println("User \"" + phoneNumber + "\" not found!");
			throw new UsernameNotFoundException("User \"" + phoneNumber + "\" not found!");
		}
	};

	public static UsernamePasswordAuthenticationToken getPrincipal(String username) {
		UserDetails user = userDetailsService.loadUserByUsername(username);
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user,
				user.getPassword(), user.getAuthorities());
		return authentication;
	}
}