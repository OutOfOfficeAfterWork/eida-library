package com.example.springapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .mvcMatchers(GET,
                "/signup", "/login")
            .permitAll()
            .mvcMatchers(POST,
                "/signup")
            .permitAll()
            .anyRequest().authenticated()
        ;

        http.formLogin()
            .loginPage("/login")
            .successHandler(new LoginSuccessHandler("/"))
        ;

        http.logout()
            .logoutSuccessUrl("/")
        ;
    }

}