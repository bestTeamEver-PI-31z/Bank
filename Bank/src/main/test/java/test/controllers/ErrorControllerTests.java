package test.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.text.ParseException;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import bank.application.WebApplication;
import bank.controller.ErrorController;
import test.InitData;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebApplication.class)
@Transactional
@DataJpaTest
public class ErrorControllerTests extends InitData {
	@InjectMocks
    private ErrorController errorController;
 
    private MockMvc mockMvc;

    @Before
    public void setup() throws ParseException {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(errorController).build();
        super.setup();
    }
    
    @Test
	public void errorPage403Test() throws Exception {
    	mockMvc.perform(get("/403"))
	  		.andExpect(status().isOk())
	  		.andExpect(view().name("errors.403.Error: 403 Forbidden"));
	}

    @Test
	public void errorPage404Test() throws Exception {
    	mockMvc.perform(get("/404"))
	  		.andExpect(status().isOk())
	  		.andExpect(view().name("errors.404.Error: 404 Page Not Found"));
	}
    
    @Test
	public void errorPage500Test() throws Exception {
    	mockMvc.perform(get("/500"))
	  		.andExpect(status().isOk())
	  		.andExpect(view().name("errors.500.Error: 500 Unexpected Error"));
	}
}
