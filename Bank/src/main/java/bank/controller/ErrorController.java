package bank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ErrorController {
	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public String errorPage403() {
		return "errors.403.Error: 403 Forbidden";
	}

	@RequestMapping(value = "/404", method = RequestMethod.GET)
	public String errorPage404() {
		return "errors.404.Error: 404 Page Not Found";
	}

	@RequestMapping(value = "/500", method = RequestMethod.GET)
	public String errorPage500() {
		return "errors.500.Error: 500 Unexpected Error";
	}
}
