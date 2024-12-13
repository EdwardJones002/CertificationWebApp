package com.user.certifications;

import com.user.certifications.controllers.CertificationController;
import com.user.certifications.entities.Certification;
import com.user.certifications.repositories.CertificationRepository;
import com.user.certifications.repositories.RequestedCertificationRepository;
import com.user.certifications.repositories.UserFlaggedCertificationRepository;
import com.user.certifications.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(CertificationController.class)
public class CertificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CertificationRepository certificationRepository;

    @MockBean
    private UserFlaggedCertificationRepository userFlaggedCertificationRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RequestedCertificationRepository requestedCertificationRepository;

    @BeforeEach
    public void setUp() {
        // Mock the Authentication object
        Authentication authentication = Mockito.mock(Authentication.class);

        // Return a list of GrantedAuthority without casting issues
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        when(authentication.getAuthorities()).thenReturn(authorities);
        when(authentication.getName()).thenReturn("testUser");

        // Mock the repository response for the certification
        Certification certification = new Certification();
        certification.setId(1L);
        certification.setName("Java Certification");

        // Mock the findById method to return Optional<Certification>
        when(certificationRepository.findById(1L)).thenReturn(Optional.of(certification));

        // Mock the SecurityContext and set the Authentication object
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testViewCertification() throws Exception {
        mockMvc.perform(get("/certification/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("certification"))
                .andExpect(model().attributeExists("certification"))
                .andExpect(model().attribute("certification", org.hamcrest.Matchers.hasProperty("name", org.hamcrest.Matchers.is("Java Certification"))));
    }
}
