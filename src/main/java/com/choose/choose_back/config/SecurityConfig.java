package com.choose.choose_back.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.choose.choose_back.model.Permission.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers(HttpMethod.GET,"/api/users/**").hasAuthority(USER_READ.getPermission())
                .antMatchers(HttpMethod.POST,"/api/users/**").hasAuthority(USER_WRITE.getPermission())
                .antMatchers(HttpMethod.DELETE,"/api/users/**").hasAuthority(USER_WRITE.getPermission())
                .antMatchers(HttpMethod.PUT,"/api/users/**").hasAuthority(USER_WRITE.getPermission())
                .antMatchers(HttpMethod.POST, "/api/registration/username").permitAll()
                .antMatchers("/api/registration/**").hasAuthority(REGISTRATION.getPermission())
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic()
                .and()
                .formLogin()
                    .failureHandler(new SimpleUrlAuthenticationFailureHandler())
                    .successHandler(new SimpleUrlAuthenticationSuccessHandler());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .authenticationProvider(daoAuthenticationProvider());
    }

    @Override
    @Bean("soska")
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    protected DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(getPasswordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }
}
