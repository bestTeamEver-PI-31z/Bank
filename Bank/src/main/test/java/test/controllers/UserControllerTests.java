package test.controllers;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import bank.DTO.UserDTO;
import bank.application.Utils;
import bank.application.WebApplication;
import bank.controller.UserController;
import bank.entity.Account;
import bank.entity.Transaction;
import bank.entity.User;
import bank.service.AccountService;
import bank.service.UserService;
import test.AuthenticationImitation;
import test.InitData;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebApplication.class)
@Transactional
@DataJpaTest
public class UserControllerTests extends InitData {
	@Mock
    private UserService userService;
 
	@Mock
	private AccountService accountService;
	
    @InjectMocks
    private UserController userController;
 
    private MockMvc mockMvc;

    @Before
    public void setup() throws ParseException {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        super.setup();
    }
    
    @Test
	public void getAllUsersTest() throws Exception {
    	User user = users.get(2);

    	UsernamePasswordAuthenticationToken principal = AuthenticationImitation.getPrincipal(user.getPhoneNumber());
//        SecurityContextHolder.getContext().setAuthentication(principal);        
//        MockHttpSession session = new MockHttpSession();
//        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, new MockSecurityContext(principal));
		when(userService.getUserByPrincipal(principal)).thenReturn(user);
		List<UserDTO> usersDTO = new ArrayList<>();
		users.forEach(u -> usersDTO.add(new UserDTO(u, (int) u.getId(), (100.5 * u.getId()))));
		when(userService.getAllUsers()).thenReturn(usersDTO);
        
		mockMvc.perform(get("/users").principal(principal))
        	.andExpect(status().isOk())
        	.andExpect(view().name("users.All users"))
        	.andExpect(model().attribute("users", hasItems(usersDTO.get(0), usersDTO.get(1), usersDTO.get(2))))
        	.andExpect(model().attribute("users", hasSize(users.size())));
        verify(userService, times(1)).getUserByPrincipal(principal);
        
        user = users.get(1);
        principal = AuthenticationImitation.getPrincipal(user.getPhoneNumber());
		when(userService.getUserByPrincipal(principal)).thenReturn(user);
		
		mockMvc.perform(get("/users").principal(principal))
	    	.andExpect(status().isOk())
	    	.andExpect(forwardedUrl(WebApplication.accessDeniedPage.replace("forward:", "")))
	    	.andExpect(model().attribute("users", nullValue()));
		
		verify(userService, times(1)).getUserByPrincipal(principal);
		verify(userService, times(1)).getAllUsers();
        verifyNoMoreInteractions(userService);
	}
    
    @Test
	public void getUserTest() throws Exception {
    	User user = users.get(0);
    	User userAdmin = users.get(2);
    	Double totalBalance = 345.51;
    	
    	UsernamePasswordAuthenticationToken principal = AuthenticationImitation.getPrincipal(user.getPhoneNumber());
		when(userService.hasAccess(principal, user.getId())).thenReturn(user);
		when(userService.getUserById(user.getId())).thenReturn(user);
		when(accountService.calculateTotalBalanceByUserId(user.getId())).thenReturn(totalBalance);
		
		mockMvc.perform(get("/users/{id}", user.getId()).principal(principal))
      		.andExpect(status().isOk())
      		.andExpect(view().name("users.User"))
      		.andExpect(model().attribute("user", equalTo(user)))
      		.andExpect(model().attribute("totalBalance", equalTo(totalBalance)));

		verify(userService, times(1)).hasAccess(principal, user.getId());
		when(userService.hasAccess(principal, userAdmin.getId())).thenReturn(null);
		
		mockMvc.perform(get("/users/{id}", userAdmin.getId()).principal(principal))
	  		.andExpect(status().isOk())
	  		.andExpect(forwardedUrl(WebApplication.accessDeniedPage.replace("forward:", "")))
	  		.andExpect(model().attribute("user", nullValue()))
	  		.andExpect(model().attribute("totalBalance", nullValue()));

		verify(userService, times(1)).hasAccess(principal, userAdmin.getId());
		principal = AuthenticationImitation.getPrincipal(userAdmin.getPhoneNumber());
		when(userService.hasAccess(principal, user.getId())).thenReturn(userAdmin);

		mockMvc.perform(get("/users/{id}", user.getId()).principal(principal))
	  		.andExpect(status().isOk())
	  		.andExpect(view().name("users.User"))
	  		.andExpect(model().attribute("user", equalTo(user)))
	  		.andExpect(model().attribute("totalBalance", equalTo(totalBalance)));

		verify(userService, times(1)).hasAccess(principal, user.getId());
		verify(userService, times(2)).getUserById(user.getId());
		verify(accountService, times(2)).calculateTotalBalanceByUserId(user.getId());
		verifyNoMoreInteractions(userService);
		verifyNoMoreInteractions(accountService);
	}

    @Test
	public void deleteUserTest() throws Exception {
		User user = users.get(0);
		User userAdmin = users.get(2);
		Double totalBalance = 0D;
		
		UsernamePasswordAuthenticationToken principal = AuthenticationImitation.getPrincipal(user.getPhoneNumber());
		when(userService.hasAccess(principal, user.getId())).thenReturn(user);
		when(userService.deleteUser(user.getId())).thenReturn(true);
		when(accountService.calculateTotalBalanceByUserId(user.getId())).thenReturn(totalBalance);
		
		ResultActions result = mockMvc.perform(delete("/users/{id}", user.getId()).principal(principal))
			.andExpect(status().isOk());
		String responseBody = result.andReturn().getResponse().getContentAsString();
		assertEquals(responseBody, "/users");

		verify(userService, times(1)).hasAccess(principal, user.getId());
		principal = AuthenticationImitation.getPrincipal(userAdmin.getPhoneNumber());
		totalBalance = 90D;
		when(userService.hasAccess(principal, user.getId())).thenReturn(userAdmin);
		when(accountService.calculateTotalBalanceByUserId(user.getId())).thenReturn(totalBalance);

		result = mockMvc.perform(delete("/users/{id}", user.getId()).principal(principal))
			.andExpect(status().isOk());
		responseBody = result.andReturn().getResponse().getContentAsString();
		assertEquals(responseBody, "/users?error");
		
		verify(userService, times(1)).hasAccess(principal, user.getId());
		verify(userService, times(1)).deleteUser(user.getId());
		verify(accountService, times(2)).calculateTotalBalanceByUserId(user.getId());
		verifyNoMoreInteractions(userService);
		verifyNoMoreInteractions(accountService);
	}

	@Test
	public void getUserAccountsTest() throws Exception {
		User user = users.get(0);
		User userAdmin = users.get(2);
		
		UsernamePasswordAuthenticationToken principal = AuthenticationImitation.getPrincipal(user.getPhoneNumber());
		when(userService.hasAccess(principal, user.getId())).thenReturn(user);
		when(userService.getUserById(user.getId())).thenReturn(user);
		
		mockMvc.perform(get("/users/{id}/accounts", user.getId()).principal(principal))
	  		.andExpect(status().is3xxRedirection())
	  		.andExpect(redirectedUrl("/users/" + user.getId() + "/accounts/new"));

		user.setAccounts(accounts);
		when(userService.hasAccess(principal, user.getId())).thenReturn(user);
		when(userService.getUserById(user.getId())).thenReturn(user);
		
		mockMvc.perform(get("/users/{id}/accounts", user.getId()).principal(principal))
	  		.andExpect(status().is3xxRedirection())
	  		.andExpect(redirectedUrl("/users/" + user.getId() + "/accounts/" + user.getAccounts().get(user.getAccounts().size() - 1).getId()));
	
		verify(userService, times(2)).hasAccess(principal, user.getId());
		
		mockMvc.perform(get("/users/{id}/accounts", userAdmin.getId()).principal(principal))
	  		.andExpect(status().isOk())
	  		.andExpect(forwardedUrl(WebApplication.accessDeniedPage.replace("forward:", "")));

		verify(userService, times(1)).hasAccess(principal, userAdmin.getId());
		principal = AuthenticationImitation.getPrincipal(userAdmin.getPhoneNumber());
		when(userService.hasAccess(principal, user.getId())).thenReturn(userAdmin);
		
		mockMvc.perform(get("/users/{id}/accounts", user.getId()).principal(principal))
	  		.andExpect(status().is3xxRedirection())
	  		.andExpect(redirectedUrl("/users/" + user.getId() + "/accounts/" + user.getAccounts().get(user.getAccounts().size() - 1).getId()));

		verify(userService, times(1)).hasAccess(principal, user.getId());
		verify(userService, times(3)).getUserById(user.getId());
		verifyNoMoreInteractions(userService);
	}
	
	@Test
	public void addUserAccountsTest() throws Exception {
		User user = users.get(0);
		User userAdmin = users.get(2);
		Double amount = 1004.3;
		Transaction transaction = new Transaction(new Account(0, user), amount);
		transaction.setId(1L);
		
		UsernamePasswordAuthenticationToken principal = AuthenticationImitation.getPrincipal(user.getPhoneNumber());
		when(userService.hasAccess(principal, user.getId())).thenReturn(user);
		when(userService.getUserById(user.getId())).thenReturn(user);
		when(accountService.saveAccount(amount, user)).thenReturn(transaction);
		
		mockMvc.perform(post("/users/{id}/accounts", user.getId()).param("amount", String.valueOf(amount)).principal(principal))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/users/" + user.getId() + "/accounts"));
		
		mockMvc.perform(post("/users/{id}/accounts", userAdmin.getId()).param("amount", String.valueOf(amount)).principal(principal))
			.andExpect(status().isOk())
			.andExpect(forwardedUrl(WebApplication.accessDeniedPage.replace("forward:", "")));
		
		verify(userService, times(1)).hasAccess(principal, user.getId());
		verify(userService, times(1)).hasAccess(principal, userAdmin.getId());
		principal = AuthenticationImitation.getPrincipal(userAdmin.getPhoneNumber());
		when(userService.hasAccess(principal, user.getId())).thenReturn(userAdmin);
		
		mockMvc.perform(post("/users/{id}/accounts", user.getId()).param("amount", String.valueOf(amount)).principal(principal))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/users/" + user.getId() + "/accounts"));
		
		verify(userService, times(1)).hasAccess(principal, user.getId());
		verify(userService, times(2)).getUserById(user.getId());
		verify(accountService, times(2)).saveAccount(amount, user);
		verifyNoMoreInteractions(userService);
		verifyNoMoreInteractions(accountService);
	}

	@Test
	public void getUserAccountTest() throws Exception {
		User user = users.get(0);
		user.setAccounts(accounts);
		User userAdmin = users.get(2);
		Double totalBalance = 196.55D;
		Account accountFrom = user.getAccounts().stream().findFirst().get();
		Account accountTo = user.getAccounts().get(1);
		accountFrom.setUser(user);
		accountTo.setUser(user);
		Transaction transaction = new Transaction(accountFrom, accountTo, 127);
		List<Transaction> transactions = new ArrayList<>();
		transactions.add(transaction);
	
		UsernamePasswordAuthenticationToken principal = AuthenticationImitation.getPrincipal(user.getPhoneNumber());
		when(userService.hasAccess(principal, user.getId())).thenReturn(user);
		when(userService.getUserById(user.getId())).thenReturn(user);
		when(accountService.calculateTotalBalanceByUserId(user.getId())).thenReturn(totalBalance);
		when(accountService.getAccount(accountFrom.getId(), user.getId())).thenReturn(accountFrom);
		when(accountService.getTransfersByDates(accountFrom.getId(), Utils.parseDate("04/22/2018"), Utils.parseDate("04/23/2018"))).thenReturn(new ArrayList<>());
		when(accountService.getTransferHistoryByAccountId(accountFrom.getId())).thenReturn(transactions);

		mockMvc.perform(get("/users/{userId}/accounts/{accountId}", user.getId(), accountFrom.getId()).principal(principal))
			.andExpect(status().isOk())
			.andExpect(view().name("accounts.Account"))
	  		.andExpect(model().attribute("user", equalTo(user)))
	  		.andExpect(model().attribute("totalBalance", equalTo(totalBalance)))
	  		.andExpect(model().attribute("accounts", hasItems(user.getAccounts().get(0), user.getAccounts().get(1))))
        	.andExpect(model().attribute("accounts", hasSize(user.getAccounts().size())))
	  		.andExpect(model().attribute("addNew", equalTo(false)))
	  		.andExpect(model().attribute("account", equalTo(accountFrom)))
	  		.andExpect(model().attribute("fromDate", nullValue()))
	  		.andExpect(model().attribute("toDate", nullValue()))
	  		.andExpect(model().attribute("history", hasSize(1)));
		
		mockMvc.perform(get("/users/{userId}/accounts/{accountId}", user.getId(), "new").principal(principal))
			.andExpect(status().isOk())
			.andExpect(view().name("accounts.Account"))
	  		.andExpect(model().attribute("user", equalTo(user)))
	  		.andExpect(model().attribute("totalBalance", equalTo(totalBalance)))
	  		.andExpect(model().attribute("accounts", hasItems(user.getAccounts().get(0),  user.getAccounts().get(1))))
	    	.andExpect(model().attribute("accounts", hasSize(user.getAccounts().size())))
	  		.andExpect(model().attribute("addNew", equalTo(true)))
	  		.andExpect(model().attribute("account", nullValue()))
	  		.andExpect(model().attribute("fromDate", nullValue()))
	  		.andExpect(model().attribute("toDate", nullValue()))
	  		.andExpect(model().attribute("history", nullValue()));
		
		verify(userService, times(2)).hasAccess(principal, user.getId());
		verify(userService, times(2)).hasAccess(principal, user.getId());
		when(userService.hasAccess(principal, userAdmin.getId())).thenReturn(null);

		mockMvc.perform(get("/users/{userId}/accounts/{accountId}", userAdmin.getId(), accountFrom.getId()).principal(principal))
			.andExpect(status().isOk())
			.andExpect(forwardedUrl(WebApplication.accessDeniedPage.replace("forward:", "")))
	  		.andExpect(model().attribute("user", nullValue()))
	    	.andExpect(model().attribute("accounts", nullValue()))
	  		.andExpect(model().attribute("addNew", nullValue()))
	  		.andExpect(model().attribute("account", nullValue()))
	  		.andExpect(model().attribute("fromDate", nullValue()))
	  		.andExpect(model().attribute("toDate", nullValue()))
	  		.andExpect(model().attribute("history", nullValue()));
		
		verify(userService, times(1)).hasAccess(principal, userAdmin.getId());
		principal = AuthenticationImitation.getPrincipal(userAdmin.getPhoneNumber());
		when(userService.hasAccess(principal, user.getId())).thenReturn(userAdmin);
		
		mockMvc.perform(get("/users/{userId}/accounts/{accountId}?fromDate=04/22/2018&toDate=04/23/2018", user.getId(), accountFrom.getId()).principal(principal))
			.andExpect(status().isOk())
			.andExpect(view().name("accounts.Account"))
	  		.andExpect(model().attribute("user", equalTo(user)))
	  		.andExpect(model().attribute("totalBalance", equalTo(totalBalance)))
	  		.andExpect(model().attribute("accounts", hasItems(user.getAccounts().get(0), user.getAccounts().get(1))))
	    	.andExpect(model().attribute("accounts", hasSize(user.getAccounts().size())))
	  		.andExpect(model().attribute("addNew", equalTo(false)))
	  		.andExpect(model().attribute("account", equalTo(accountFrom)))
	  		.andExpect(model().attribute("fromDate", equalTo("04/22/2018")))
	  		.andExpect(model().attribute("toDate", equalTo("04/23/2018")))
	  		.andExpect(model().attribute("history", hasSize(0)));
		
		verify(userService, times(1)).hasAccess(principal, user.getId());
		verify(userService, times(3)).getUserById(user.getId());
		verify(accountService, times(3)).calculateTotalBalanceByUserId(accountFrom.getId());
		verify(accountService, times(2)).getAccount(accountFrom.getId(), user.getId());
		verify(accountService, times(1)).getTransfersByDates(accountFrom.getId(), Utils.parseDate("04/22/2018"), Utils.parseDate("04/23/2018"));
		verify(accountService, times(1)).getTransferHistoryByAccountId(accountFrom.getId());
		verifyNoMoreInteractions(userService);
		verifyNoMoreInteractions(accountService);
	}
	
	@Test
	public void deleteUserAccountTest() throws Exception {
		User user = users.get(0);
		user.setAccounts(accounts);
		User userAdmin = users.get(2);
		Account account = user.getAccounts().stream().findFirst().get();
		account.setUser(user);
		
		UsernamePasswordAuthenticationToken principal = AuthenticationImitation.getPrincipal(user.getPhoneNumber());
		when(userService.hasAccess(principal, user.getId())).thenReturn(user);
		when(userService.getUserById(user.getId())).thenReturn(user);
		when(accountService.getAccount(account.getId(), user.getId())).thenReturn(account);
		when(accountService.deleteAccount(account.getId())).thenReturn(true);
		
		mockMvc.perform(get("/users/{userId}/accounts/{accountId}/delete", user.getId(), account.getId()).principal(principal))
	  		.andExpect(status().isOk())
	  		.andExpect(view().name("accounts.Transfer"))
	  		.andExpect(model().attribute("user", equalTo(user)))
	  		.andExpect(model().attribute("info", equalTo("You need transfer your money to close this account!")))
	  		.andExpect(model().attribute("redirectUrl", equalTo("/users/" + user.getId() + "/accounts/" + account.getId() + "/delete")))
	  		.andExpect(model().attribute("selectedAccountId", equalTo(account.getId())));

		account.setBalance(0);
		
		mockMvc.perform(get("/users/{userId}/accounts/{accountId}/delete", user.getId(), account.getId()).principal(principal))
	  		.andExpect(status().is3xxRedirection())
	  		.andExpect(redirectedUrl("/users/" + user.getId() + "/accounts"))
	  		.andExpect(model().attribute("user", equalTo(user)))
	  		.andExpect(model().attribute("info", nullValue()))
	  		.andExpect(model().attribute("redirectUrl", nullValue()))
	  		.andExpect(model().attribute("selectedAccountId", nullValue()));
  	
		verify(userService, times(3)).hasAccess(principal, user.getId());

		mockMvc.perform(get("/users/{userId}/accounts/{accountId}/delete", userAdmin.getId(), account.getId()).principal(principal))
	  		.andExpect(status().isOk())
	  		.andExpect(forwardedUrl(WebApplication.accessDeniedPage.replace("forward:", "")))
	  		.andExpect(model().attribute("user", nullValue()))
	  		.andExpect(model().attribute("info", nullValue()))
	  		.andExpect(model().attribute("redirectUrl", nullValue()))
	  		.andExpect(model().attribute("selectedAccountId", nullValue()));
		
		verify(userService, times(1)).hasAccess(principal, userAdmin.getId());
		principal = AuthenticationImitation.getPrincipal(userAdmin.getPhoneNumber());
		when(userService.hasAccess(principal, user.getId())).thenReturn(userAdmin);
		
		mockMvc.perform(get("/users/{userId}/accounts/{accountId}/delete", user.getId(), account.getId()).principal(principal))
			.andExpect(status().is3xxRedirection())
	  		.andExpect(redirectedUrl("/users/" + user.getId() + "/accounts"))
	  		.andExpect(model().attribute("user", equalTo(user)))
	  		.andExpect(model().attribute("info", nullValue()))
	  		.andExpect(model().attribute("redirectUrl", nullValue()))
	  		.andExpect(model().attribute("selectedAccountId", nullValue()));
		
		verify(userService, times(1)).hasAccess(principal, user.getId());
		verify(userService, times(4)).getUserById(user.getId());
		verify(accountService, times(3)).getAccount(account.getId(), user.getId());
		verify(accountService, times(1)).calculateTotalBalanceByUserId(user.getId());
		verify(accountService, times(2)).deleteAccount(user.getId());
		verifyNoMoreInteractions(userService);
		verifyNoMoreInteractions(accountService);
	}
	
	@Test
	public void depositGetTest() throws Exception {
		User user = users.get(0);
		user.setAccounts(accounts);
		User userAdmin = users.get(2);
		Account account = user.getAccounts().stream().findFirst().get();
		account.setUser(user);
		Double totalBalance = 200D;
		
		UsernamePasswordAuthenticationToken principal = AuthenticationImitation.getPrincipal(user.getPhoneNumber());
		when(userService.hasAccess(principal, user.getId())).thenReturn(user);
		when(userService.getUserById(user.getId())).thenReturn(user);
		when(accountService.calculateTotalBalanceByUserId(user.getId())).thenReturn(totalBalance);
		
		mockMvc.perform(get("/users/{userId}/deposit", user.getId()).principal(principal))
			.andExpect(status().isOk())
	  		.andExpect(view().name("accounts.Deposit"))
	  		.andExpect(model().attribute("user", equalTo(user)))
	  		.andExpect(model().attribute("totalBalance", equalTo(totalBalance)))
	  		.andExpect(model().attribute("accounts", hasItems(user.getAccounts().get(0), user.getAccounts().get(1))))
	    	.andExpect(model().attribute("accounts", hasSize(user.getAccounts().size())));

		mockMvc.perform(get("/users/{userId}/deposit", userAdmin.getId()).principal(principal))
			.andExpect(status().isOk())
	  		.andExpect(forwardedUrl(WebApplication.accessDeniedPage.replace("forward:", "")))
	  		.andExpect(model().attribute("user", nullValue()))
	  		.andExpect(model().attribute("totalBalance", nullValue()))
	  		.andExpect(model().attribute("redirectUrl", nullValue()))
	  		.andExpect(model().attribute("accounts", nullValue()));

		verify(userService, times(1)).hasAccess(principal, user.getId());
		verify(userService, times(1)).hasAccess(principal, userAdmin.getId());
		principal = AuthenticationImitation.getPrincipal(userAdmin.getPhoneNumber());
		when(userService.hasAccess(principal, user.getId())).thenReturn(userAdmin);

		mockMvc.perform(get("/users/{userId}/deposit", user.getId()).principal(principal))
			.andExpect(status().isOk())
	  		.andExpect(view().name("accounts.Deposit"))
	  		.andExpect(model().attribute("user", equalTo(user)))
	  		.andExpect(model().attribute("totalBalance", equalTo(totalBalance)))
	  		.andExpect(model().attribute("accounts", hasItems(user.getAccounts().get(0), user.getAccounts().get(1))))
	    	.andExpect(model().attribute("accounts", hasSize(user.getAccounts().size())));

		verify(userService, times(1)).hasAccess(principal, user.getId());
		verify(userService, times(2)).getUserById(user.getId());
		verify(accountService, times(2)).calculateTotalBalanceByUserId(user.getId());
		verifyNoMoreInteractions(userService);
		verifyNoMoreInteractions(accountService);
	}
	
	@Test
	public void depositPostTest() throws Exception {
		User user = users.get(0);
		user.setAccounts(accounts);
		User userAdmin = users.get(2);
		Double amount = 3452D;
		Account account = user.getAccounts().stream().findFirst().get();
		account.setUser(user);

		UsernamePasswordAuthenticationToken principal = AuthenticationImitation.getPrincipal(user.getPhoneNumber());
		when(userService.hasAccess(principal, user.getId())).thenReturn(user);
		when(userService.getUserById(user.getId())).thenReturn(user);
		when(accountService.getAccount(account.getId(), user.getId())).thenReturn(account);
		
		mockMvc.perform(post("/users/{id}/deposit", user.getId()).param("account", String.valueOf(account.getId())).param("amount", String.valueOf(amount)).principal(principal))
			.andExpect(status().isOk())
			.andExpect(view().name("accounts.Deposit"))
			.andExpect(model().attribute("user", equalTo(user)))
			.andExpect(model().attribute("success", equalTo(true)))
			.andExpect(model().attribute("error", nullValue()));
		
		mockMvc.perform(post("/users/{id}/deposit", userAdmin.getId()).param("account", String.valueOf(account.getId())).param("amount", String.valueOf(amount)).principal(principal))
			.andExpect(status().isOk())
			.andExpect(forwardedUrl(WebApplication.accessDeniedPage.replace("forward:", "")))
			.andExpect(model().attribute("user", nullValue()))
			.andExpect(model().attribute("success", nullValue()))
			.andExpect(model().attribute("error", nullValue()));

		verify(userService, times(1)).hasAccess(principal, userAdmin.getId());

		mockMvc.perform(post("/users/{id}/deposit", user.getId()).param("account", String.valueOf(account.getId() * 2)).param("amount", String.valueOf(amount)).principal(principal))
			.andExpect(status().isOk())
			.andExpect(view().name("accounts.Deposit"))
			.andExpect(model().attribute("user", equalTo(user)))
			.andExpect(model().attribute("success", nullValue()))
			.andExpect(model().attribute("error", equalTo("Cannot find account!")));
		
		verify(userService, times(4)).hasAccess(principal, user.getId());
		principal = AuthenticationImitation.getPrincipal(userAdmin.getPhoneNumber());
		when(userService.hasAccess(principal, user.getId())).thenReturn(userAdmin);
		
		mockMvc.perform(post("/users/{id}/deposit", user.getId()).param("account", String.valueOf(account.getId())).param("amount", String.valueOf(amount)).principal(principal))
			.andExpect(status().isOk())
			.andExpect(view().name("accounts.Deposit"))
			.andExpect(model().attribute("user", equalTo(user)))
			.andExpect(model().attribute("success", equalTo(true)))
			.andExpect(model().attribute("error", nullValue()));

		verify(userService, times(2)).hasAccess(principal, user.getId());
		verify(userService, times(6)).getUserById(user.getId());
		verify(accountService, times(2)).getAccount(account.getId(), user.getId());
		verify(accountService, times(1)).getAccount(account.getId() * 2, user.getId());
		verify(accountService, times(2)).transfer(null, account, amount);
		verify(accountService, times(3)).calculateTotalBalanceByUserId(user.getId());
		verifyNoMoreInteractions(userService);
		verifyNoMoreInteractions(accountService);
	}

	@Test
	public void transferGetTest() throws Exception {
		User user = users.get(0);
		user.setAccounts(accounts);
		User userAdmin = users.get(2);
		Double totalBalance = 196.55D;
		
		UsernamePasswordAuthenticationToken principal = AuthenticationImitation.getPrincipal(user.getPhoneNumber());
		when(userService.hasAccess(principal, user.getId())).thenReturn(user);
		when(userService.getUserById(user.getId())).thenReturn(user);
		when(accountService.calculateTotalBalanceByUserId(user.getId())).thenReturn(totalBalance);
		
		mockMvc.perform(get("/users/{id}/transfer", user.getId()).principal(principal))
			.andExpect(status().isOk())
			.andExpect(view().name("accounts.Transfer"))
			.andExpect(model().attribute("user", equalTo(user)))
			.andExpect(model().attribute("totalBalance", equalTo(totalBalance)))
	  		.andExpect(model().attribute("accounts", hasItems(user.getAccounts().get(0), user.getAccounts().get(1))))
	    	.andExpect(model().attribute("accounts", hasSize(user.getAccounts().size())));
		
		mockMvc.perform(get("/users/{id}/transfer", userAdmin.getId()).principal(principal))
			.andExpect(status().isOk())
			.andExpect(forwardedUrl(WebApplication.accessDeniedPage.replace("forward:", "")))
			.andExpect(model().attribute("user", nullValue()))
			.andExpect(model().attribute("totalBalance", nullValue()))
	  		.andExpect(model().attribute("accounts", nullValue()));
		
		verify(userService, times(1)).hasAccess(principal, user.getId());
		verify(userService, times(1)).hasAccess(principal, userAdmin.getId());
		principal = AuthenticationImitation.getPrincipal(userAdmin.getPhoneNumber());
		when(userService.hasAccess(principal, user.getId())).thenReturn(userAdmin);
		
		mockMvc.perform(get("/users/{id}/transfer", user.getId()).principal(principal))
			.andExpect(status().isOk())
			.andExpect(view().name("accounts.Transfer"))
			.andExpect(model().attribute("user", equalTo(user)))
			.andExpect(model().attribute("totalBalance", equalTo(totalBalance)))
	  		.andExpect(model().attribute("accounts", hasItems(user.getAccounts().get(0), user.getAccounts().get(1))))
	    	.andExpect(model().attribute("accounts", hasSize(user.getAccounts().size())));
		
		verify(userService, times(1)).hasAccess(principal, user.getId());
		verify(userService, times(2)).getUserById(user.getId());
		verify(accountService, times(2)).calculateTotalBalanceByUserId(user.getId());
		verifyNoMoreInteractions(userService);
		verifyNoMoreInteractions(accountService);
	}
	
	@Test
	public void transferPostTest() throws Exception {
		User user = users.get(0);
		user.setAccounts(accounts);
		User userAdmin = users.get(2);
		Double amount = 9655D;
		Account accountFrom = user.getAccounts().stream().findFirst().get();
		Account accountTo = user.getAccounts().get(1);
		accountFrom.setUser(user);
		accountTo.setUser(user);
		String incorrectAccountNumber = "0000 0000 0000 0099";
		String redirectUrl = "/redirectUrlTest";
		
		UsernamePasswordAuthenticationToken principal = AuthenticationImitation.getPrincipal(user.getPhoneNumber());
		when(userService.hasAccess(principal, user.getId())).thenReturn(user);
		when(userService.getUserById(user.getId())).thenReturn(user);

		mockMvc.perform(post("/users/{id}/transfer", user.getId())
				.param("from", String.valueOf(accountFrom.getFormatedAccountNumber()))
				.param("to", String.valueOf(accountTo.getFormatedAccountNumber()))
				.param("accountNumber", StringUtils.EMPTY)
				.param("amount", String.valueOf(amount))
				.param("redirectUrl", StringUtils.EMPTY)
				.principal(principal))
			.andExpect(status().isOk())
			.andExpect(view().name("accounts.Transfer"))
			.andExpect(model().attribute("error", nullValue()))
			.andExpect(model().attribute("success", equalTo(true)));
	
		verify(accountService, times(1)).transfer(accountFrom.getFormatedAccountNumber(), accountTo.getFormatedAccountNumber(), amount, principal);
		String errorMessage = "Incorrect account number!\\nCannot find account (to)";
		when(accountService.transfer(accountFrom.getFormatedAccountNumber(), incorrectAccountNumber, amount, principal)).thenReturn(errorMessage);

		mockMvc.perform(post("/users/{id}/transfer", user.getId())
				.param("from", String.valueOf(accountFrom.getFormatedAccountNumber()))
				.param("to", incorrectAccountNumber)
				.param("accountNumber", StringUtils.EMPTY)
				.param("amount", String.valueOf(amount))
				.param("redirectUrl", redirectUrl)
				.principal(principal))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl(redirectUrl + "?error=" + URLEncoder.encode(errorMessage, "UTF-8") + "&success=false"))
			.andExpect(model().attribute("error", equalTo(errorMessage)))
			.andExpect(model().attribute("success", equalTo(false)));
	
		mockMvc.perform(post("/users/{id}/transfer", userAdmin.getId())
				.param("from", String.valueOf(accountFrom.getFormatedAccountNumber()))
				.param("to", String.valueOf(accountTo.getFormatedAccountNumber()))
				.param("accountNumber", StringUtils.EMPTY)
				.param("amount", String.valueOf(amount))
				.param("redirectUrl", redirectUrl)
				.principal(principal))
			.andExpect(status().isOk())
			.andExpect(forwardedUrl(WebApplication.accessDeniedPage.replace("forward:", "")))
			.andExpect(model().attribute("error", nullValue()))
			.andExpect(model().attribute("success", nullValue()));
		
		verify(userService, times(3)).hasAccess(principal, user.getId());
		verify(userService, times(1)).hasAccess(principal, userAdmin.getId());
		verify(accountService, times(1)).transfer(accountFrom.getFormatedAccountNumber(), accountTo.getFormatedAccountNumber(), amount, principal);
		verify(accountService, times(1)).transfer(accountFrom.getFormatedAccountNumber(), incorrectAccountNumber, amount, principal);
		principal = AuthenticationImitation.getPrincipal(userAdmin.getPhoneNumber());
		when(userService.hasAccess(principal, user.getId())).thenReturn(userAdmin);
		
		mockMvc.perform(post("/users/{id}/transfer", user.getId())
				.param("from", String.valueOf(accountTo.getFormatedAccountNumber()))
				.param("to", "0")
				.param("accountNumber", String.valueOf(accountFrom.getFormatedAccountNumber()))
				.param("amount", String.valueOf(amount * 5))
				.param("redirectUrl", StringUtils.EMPTY)
				.principal(principal))
			.andExpect(status().isOk())
			.andExpect(view().name("accounts.Transfer"))
			.andExpect(model().attribute("error", nullValue()))
			.andExpect(model().attribute("success", equalTo(true)));
		
		verify(userService, times(2)).hasAccess(principal, user.getId());
		verify(userService, times(5)).getUserById(user.getId());
		verify(accountService, times(1)).transfer(accountTo.getFormatedAccountNumber(), accountFrom.getFormatedAccountNumber(), amount * 5, principal);
		verify(accountService, times(2)).calculateTotalBalanceByUserId(user.getId());
		verifyNoMoreInteractions(userService);
		verifyNoMoreInteractions(accountService);
	}
}
