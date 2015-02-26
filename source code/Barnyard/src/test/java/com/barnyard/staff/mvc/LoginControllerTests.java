package com.barnyard.staff.mvc;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test.xml", "file:src/main/webapp/WEB-INF/config/root.xml"})
@WebAppConfiguration
public class LoginControllerTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        SecurityContextHolder.clearContext();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void login_NoLoginErrorAndNotLogout_ShouldRenderLoginViewWithoutAnyMessages() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeDoesNotExist("anyError", "anyMsg"));
    }

    @Test
    public void login_LoginError_ShouldRenderLoginViewWithLoginErrorMessage() throws Exception {
        String error = "Invalid password";

        mockMvc.perform(get("/login").param("error", error))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("anyError", true));
    }

    @Test
    public void login_IsLogout_ShouldRenderLoginViewWithLogoutMessage() throws Exception {
        String logout = "Successfully";

        mockMvc.perform(get("/login").param("logout", logout))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("anyMsg", true));
    }

}