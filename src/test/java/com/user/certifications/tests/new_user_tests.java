package com.user.certifications.tests;
import com.user.certifications.controllers.RegistrationController;
import com.user.certifications.entities.User;
import com.user.certifications.repositories.CertificationRepository;
import com.user.certifications.repositories.UserCertificationRepository;
import com.user.certifications.repositories.UserRepository;
import com.user.certifications.servicesImp.EmailService;
import com.user.certifications.servicesImp.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WebMvcTest(controllers = RegistrationController.class)
public class new_user_tests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private EmailService emailService;

    @MockBean
    private RedirectAttributes redirectAttributes;
    @Autowired
    private RegistrationController registrationController;
    @Autowired
    private ObjectProvider<HttpSession> sessionProvider;
    HttpSession session = sessionProvider.getIfAvailable();
    @Test
    void testSuccessRegistration() throws Exception {

        when(userRepository.findByUsername("Edward1@gmail.com")).thenReturn(Optional.empty());
        when(session.getAttribute("verificationCode")).thenReturn("123456");

        mockMvc.perform(post("/register")
                        .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "Edward")
                .param("email", "Edward1@gmail.com")
                .param("password", "Edward123!"));

        assertEquals("123456", session.getAttribute("verificationCode"));
        assertEquals("Edward1@gmail.com", session.getAttribute("email"));

    }
}
