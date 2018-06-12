package test.services;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import bank.DAO.UserDAO;
import bank.DTO.UserDTO;
import bank.entity.User;
import bank.service.AccountService;
import bank.service.UserService;
import bank.service.impl.UserServiceImpl;
import test.AuthenticationImitation;
import test.InitData;

@RunWith(SpringRunner.class)
public class UserSeviceTests extends InitData {
	@TestConfiguration
    static class UserServiceImplTestContextConfiguration {
        @Bean
        public UserService userService() {
            return new UserServiceImpl();
        }
    }

	@Autowired
	private UserService userService;
	
	@MockBean
    private AccountService accountService;
  
    @MockBean
    private UserDAO userDAO;

    @MockBean
	private BCryptPasswordEncoder encoder;
    
    @Before
    public void setup() throws ParseException {
    	super.setup();
    	MockitoAnnotations.initMocks(this);
    }

    @Test
    public void saveUserTest() {
    	User expectedUser = users.get(0);
    	User user = new User(expectedUser.getPhoneNumber(), expectedUser.getFirstName(), expectedUser.getLastName(), expectedUser.getDateOfBirth(), expectedUser.getAddress(), expectedUser.getPassword());

    	when(userDAO.save(user)).thenReturn(expectedUser);
    	when(encoder.encode(user.getPassword())).thenReturn(user.getPassword());
    	
    	assertEquals(expectedUser, userService.saveUser(user));

    	verify(userDAO, times(1)).save(user);
    	verify(encoder, times(1)).encode(user.getPassword());
		verifyNoMoreInteractions(userDAO);
		verifyNoMoreInteractions(encoder);
	}
    
    @Test
    public void deleteUserTest() {
    	User user = users.get(0);
    	
    	assertFalse(userService.deleteUser(user.getId()));
    	
    	when(userDAO.getOne(user.getId())).thenReturn(user);
    	
    	assertTrue(userService.deleteUser(user.getId()));

    	verify(userDAO, times(2)).getOne(user.getId());
    	verify(userDAO, times(1)).delete(user);
    	verifyNoMoreInteractions(userDAO);
    }
    
    @Test
    public void getAllUsersTest() {
    	List<UserDTO> expectedUsers = new ArrayList<>();
		List<UserDTO> usersFromService = null;
		Integer numberOfAccounts = 2;
		Double totalBalance = 4D;
		users.forEach(u -> expectedUsers.add(new UserDTO(u, (int) (u.getId() * numberOfAccounts), (u.getId() * totalBalance))));
		
		usersFromService = userService.getAllUsers();
		assertNotNull(usersFromService);
		assertTrue(usersFromService.isEmpty());

		for (User user : users) {
			when(accountService.calculateNumberOfAccountsByUserId(user.getId())).thenReturn((int) (user.getId() * numberOfAccounts));
			when(accountService.calculateTotalBalanceByUserId(user.getId())).thenReturn(user.getId() * totalBalance);
		}
		when(userDAO.findAll()).thenReturn(users);
		
		usersFromService = userService.getAllUsers();
		assertNotNull(usersFromService);
		assertFalse(usersFromService.isEmpty());
		assertTrue(usersFromService.size() == expectedUsers.size());
		for (UserDTO userDTO : expectedUsers) {
			assertTrue(usersFromService.contains(userDTO));
		}
		
		for (User user : users) {
			verify(accountService, times(1)).calculateNumberOfAccountsByUserId(user.getId());
			verify(accountService, times(1)).calculateTotalBalanceByUserId(user.getId());
		}
    	verify(userDAO, times(2)).findAll();
		verifyNoMoreInteractions(accountService);
		verifyNoMoreInteractions(userDAO);
    }
    
    
    @Test
    public void toUserDTOTest() {
    	User user = users.get(0);
    	int numberOfAccounts = 2;
    	double totalBalance = 3532.6;
    	UserDTO userDTO = new UserDTO(user, 0, 0);
    	
    	assertEquals(userDTO, userService.toUserDTO(user));
    	
    	when(accountService.calculateNumberOfAccountsByUserId(user.getId())).thenReturn(numberOfAccounts);
		when(accountService.calculateTotalBalanceByUserId(user.getId())).thenReturn(totalBalance);
		userDTO = new UserDTO(user, numberOfAccounts, totalBalance);

		assertEquals(userDTO, userService.toUserDTO(user));
		
		verify(accountService, times(2)).calculateNumberOfAccountsByUserId(user.getId());
		verify(accountService, times(2)).calculateTotalBalanceByUserId(user.getId());
		verifyNoMoreInteractions(accountService);
    }
    
    @Test
    public void getUserByIdTest() {
    	User user = users.get(0);
    	
    	assertNull(userService.getUserById(user.getId()));
    	
    	when(userDAO.getOne(user.getId())).thenReturn(user);
    	
    	assertEquals(user, userService.getUserById(user.getId()));
    	
    	verify(userDAO, times(2)).getOne(user.getId());
		verifyNoMoreInteractions(userDAO);
    }
    
    @Test
    public void getUserByPrincipalTest() {
    	User user = users.get(0);
    	UsernamePasswordAuthenticationToken principal = AuthenticationImitation.getPrincipal(user.getPhoneNumber());
    	
    	assertNull(userService.getUserByPrincipal(principal));
    	
    	when(userDAO.findUserByPhoneNumber(user.getPhoneNumber())).thenReturn(user);
    	
    	assertEquals(user, userService.getUserByPrincipal(principal));
    	
    	verify(userDAO, times(2)).findUserByPhoneNumber(user.getPhoneNumber());
		verifyNoMoreInteractions(userDAO);
    }
    
    @Test
    public void getUserByPhoneNumberTest() {
    	User user = users.get(0);
    	
    	assertNull(userService.getUserByPhoneNumber(user.getPhoneNumber()));
    	
    	when(userDAO.findUserByPhoneNumber(user.getPhoneNumber())).thenReturn(user);
    	
    	assertEquals(user, userService.getUserByPhoneNumber(user.getPhoneNumber()));

    	verify(userDAO, times(2)).findUserByPhoneNumber(user.getPhoneNumber());
		verifyNoMoreInteractions(userDAO);
    }
    
    @Test
    public void existsUserByPhoneNumberTest() {
    	User user = users.get(0);
    	
    	assertFalse(userService.existsUserByPhoneNumber(user.getPhoneNumber()));
    	
    	when(userDAO.findUserByPhoneNumber(user.getPhoneNumber())).thenReturn(user);
    	
    	assertTrue(userService.existsUserByPhoneNumber(user.getPhoneNumber()));

    	verify(userDAO, times(2)).findUserByPhoneNumber(user.getPhoneNumber());
		verifyNoMoreInteractions(userDAO);
    }
    
    @Test
    public void hasAccessTest() {
    	User user = users.get(0);
    	User userAdmin = users.get(2);
    	UsernamePasswordAuthenticationToken principal = AuthenticationImitation.getPrincipal(userAdmin.getPhoneNumber());
    	
    	assertNull(userService.hasAccess(principal, user.getId()));
    	
    	when(userDAO.findUserByPhoneNumber(userAdmin.getPhoneNumber())).thenReturn(userAdmin);
    	
    	assertEquals(userAdmin, userService.hasAccess(principal, user.getId()));
    	
    	principal = AuthenticationImitation.getPrincipal(user.getPhoneNumber());
    	when(userDAO.findUserByPhoneNumber(user.getPhoneNumber())).thenReturn(user);
    	
    	assertNull(userService.hasAccess(principal, userAdmin.getId()));
    	
    	verify(userDAO, times(2)).findUserByPhoneNumber(userAdmin.getPhoneNumber());
    	verify(userDAO, times(1)).findUserByPhoneNumber(user.getPhoneNumber());
		verifyNoMoreInteractions(userDAO);
    }
}
