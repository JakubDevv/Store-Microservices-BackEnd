package com.example.identity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class AuthConfig {

    public final RequestFilter requestFilter;

    public final CustomUserDetailService customUserDetailService;

    public AuthConfig(RequestFilter requestFilter, CustomUserDetailService customUserDetailService) {
        this.requestFilter = requestFilter;
        this.customUserDetailService = customUserDetailService;
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return customUserDetailService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class);
        http.authorizeHttpRequests(autz -> autz
                .requestMatchers( "/auth/access-token", "/auth/validate-token", "/auth/userName", "/auth/makeSeller", "/auth/company", "/auth/register","/auth/login", "/admin-identity/**","/auth/user/*","/auth/user-id","/auth/company-id","/auth/users","/auth/validateToken").permitAll()
                .requestMatchers("/auth/userInfo", "/auth/validate","/auth/user", "/auth/roles","/auth/balance","/auth/transactions","/admin-identity/**","/auth/createTransaction","/auth/createCompany").authenticated());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
