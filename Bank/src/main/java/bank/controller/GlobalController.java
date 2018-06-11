package bank.controller;

import java.security.Principal;

import org.springframework.ui.Model;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import bank.entity.User;
import bank.service.UserService;

@Component
@ControllerAdvice
public class GlobalController {
	@Autowired
	private UserService userService;

	@ModelAttribute
	public void getUser(Model model, Principal principal) {
		if (principal != null) {
			User user = new User(userService.getUserByPrincipal(principal));
			user.setPassword(null);
			model.addAttribute("principalUser", user);
		}
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String handleNoHandlerFoundException(NoHandlerFoundException e) {
		return "forward:/404";
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleException(Exception e) {
		e.printStackTrace();
		return "forward:/500";
	}
}
