package com.user.certifications.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((requests) -> requests

                .requestMatchers("/css/**","/js/**", "/templates/**", "/login", "/register")
                        .permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())

                .formLogin((form) -> form.loginPage("/login").defaultSuccessUrl("/home", true).failureUrl("/login?error=true").permitAll())

                .logout((logout) -> logout.logoutUrl("/").logoutSuccessUrl("/login?logout").invalidateHttpSession(true) // Invalidate the session upon logout
                .deleteCookies("JSESSIONID") // Delete session cookies
                .permitAll())

                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Session creation policy
                .maximumSessions(1) // Allow only one session per user
                .expiredUrl("/login?sessionExpired") // Redirect to a specific URL if the session expires
        );

        return http.build();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
