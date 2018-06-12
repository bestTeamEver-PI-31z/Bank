package test.controllers;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import bank.application.WebApplication;
import bank.controller.HomeController;
import bank.entity.User;
import bank.service.UserService;
import test.AuthenticationImitation;
import test.InitData;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebApplication.class)
@Transactional
@DataJpaTest
public class HomeControllerTests extends InitData {
	@Mock
    private UserService userService;
 
    @InjectMocks
    private HomeController homeController;
 
    private MockMvc mockMvc;

    @Before
    public void setup() throws ParseException {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();
        super.setup();
    }
	
	@Test
	public void homeTest() throws Exception {
		User user = users.get(0);
		
		UsernamePasswordAuthenticationToken principal = AuthenticationImitation.getPrincipal(user.getPhoneNumber());
		when(userService.getUserByPrincipal(principal)).thenReturn(user);
		
		mockMvc.perform(get("/").principal(principal))
	  		.andExpect(status().is3xxRedirection())
	  		.andExpect(redirectedUrl("/users/" + user.getId()));
		mockMvc.perform(get("/home").principal(principal))
	  		.andExpect(status().is3xxRedirection())
	  		.andExpect(redirectedUrl("/users/" + user.getId()));
		
		when(userService.getUserByPrincipal(principal)).thenReturn(null);
		
		mockMvc.perform(get("/").principal(principal))
	  		.andExpect(status().is3xxRedirection())
	  		.andExpect(redirectedUrl("/login"));
		mockMvc.perform(get("/home").principal(principal))
	  		.andExpect(status().is3xxRedirection())
	  		.andExpect(redirectedUrl("/login"));
		
		verify(userService, times(4)).getUserByPrincipal(principal);
        verifyNoMoreInteractions(userService);
	}

	@Test
	public void loginTest() throws Exception {
		mockMvc.perform(get("/login"))
	  		.andExpect(status().isOk())
	  		.andExpect(view().name("common.Sign in"));
	}

	@Test
	public void registerGetTest() throws Exception {
		mockMvc.perform(get("/register"))
	  		.andExpect(status().isOk())
	  		.andExpect(view().name("common.Sign up"))
	  		.andExpect(model().attribute("user", equalTo(new User())));
	}
	
	@Test
	public void registerPostTest() throws Exception {
		User user = users.get(0);
		User newUser = new User(0L, user.getPhoneNumber(), user.getFirstName(), user.getLastName(), user.getDateOfBirth(), user.getAddress(), user.getPassword());
		DateFormat dateFormat = new SimpleDateFormat(WebApplication.dateFormat);
		
		when(userService.saveUser(newUser)).thenReturn(user);
		when(userService.existsUserByPhoneNumber(user.getPhoneNumber())).thenReturn(false);
		
		mockMvc.perform(post("/register")
				.param("phoneNumber", newUser.getPhoneNumber())
				.param("firstName", newUser.getFirstName())
				.param("lastName", newUser.getLastName())
				.param("dateOfBirth", dateFormat.format(newUser.getDateOfBirth()))
				.param("address", newUser.getAddress())
				.param("password", newUser.getPassword())
				.param("confirmPassword", newUser.getPassword()))
  			.andExpect(status().is3xxRedirection())
  			.andExpect(redirectedUrl("/login"))
  			.andExpect(model().attribute("user", equalTo(user)))
  			.andExpect(model().attribute("error", nullValue()));

		mockMvc.perform(post("/register")
				.param("phoneNumber", newUser.getPhoneNumber())
				.param("firstName", newUser.getFirstName())
				.param("lastName", newUser.getLastName())
				.param("dateOfBirth", dateFormat.format(newUser.getDateOfBirth()))
				.param("address", newUser.getAddress())
				.param("password", newUser.getPassword())
				.param("confirmPassword", StringUtils.EMPTY))
  			.andExpect(status().isOk())
  			.andExpect(view().name("common.Sign up"))
  			.andExpect(model().attribute("user", equalTo(newUser)))
  			.andExpect(model().attribute("error", equalTo("Password(s) cannot be blank")));
		
		when(userService.existsUserByPhoneNumber(user.getPhoneNumber())).thenReturn(true);
		
		mockMvc.perform(post("/register")
				.param("phoneNumber", newUser.getPhoneNumber())
				.param("firstName", newUser.getFirstName())
				.param("lastName", newUser.getLastName())
				.param("dateOfBirth", dateFormat.format(newUser.getDateOfBirth()))
				.param("address", newUser.getAddress())
				.param("password", newUser.getPassword())
				.param("confirmPassword", newUser.getPassword()))
  			.andExpect(status().isOk())
  			.andExpect(view().name("common.Sign up"))
  			.andExpect(model().attribute("user", equalTo(newUser)))
  			.andExpect(model().attribute("error", equalTo("User with this phone number already exists!")));
		
		verify(userService, times(1)).saveUser(newUser);
		verify(userService, times(2)).existsUserByPhoneNumber(user.getPhoneNumber());
        verifyNoMoreInteractions(userService);
	}
}
