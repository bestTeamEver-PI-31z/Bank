package bank.controller;

import java.security.Principal;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bank.application.WebApplication;
import bank.entity.User;
import bank.service.UserService;

@Controller
public class HomeController {
	@Autowired
	private UserService userService;

	@RequestMapping(value = { "/", "/home" }, method = RequestMethod.GET)
	public String home(Model model, Principal principal) {
		User user = userService.getUserByPrincipal(principal);
		if (user != null) {
			return "redirect:/users/" + user.getId();
		}
		return "redirect:/login";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		return "common" + WebApplication.separator + "Sign in";
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String register(Model model) {
		model.addAttribute("user", new User());
		return "common" + WebApplication.separator + "Sign up";
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(
			@ModelAttribute("user") User user, 
			@RequestParam(value = "confirmPassword") String confirmPassword, 
			Model model) {
		String error = null;
		if (StringUtils.isBlank(user.getPhoneNumber())) {
			error = "Phone number cannot be blank";
		} else if (StringUtils.isBlank(user.getPassword()) || StringUtils.isBlank(confirmPassword)) {
			error = "Password(s) cannot be blank";
		} else if (!user.getPassword().equals(confirmPassword)) {
			error = "Please make sure you enter the new password twice and identical";
		} else if (userService.existsUserByPhoneNumber(user.getPhoneNumber())) {
			error = "User with this phone number already exists!";
		}
		if (error == null) {
			model.addAttribute("user", userService.saveUser(user));
			return "redirect:/login";
		}
		model.addAttribute("error", error);
		return "common" + WebApplication.separator + "Sign up";
	}
}
