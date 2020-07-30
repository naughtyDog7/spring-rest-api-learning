package com.example.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;
import org.springframework.stereotype.Component;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DigestAuthenticationEntryPoint entryPoint;

    @Override
    @Bean
    public UserDetailsService userDetailsServiceBean() {
        UserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.builder()
                .username("user")
                .password("userPass")
                .roles("USER")
        .build());
        return manager;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.userDetailsService(userDetailsServiceBean())
                .authorizeRequests()
                    .anyRequest()
                    .hasRole("USER")
                .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(entryPoint)
                .and()
                    .addFilterAfter(digestAuthenticationFilter(), BasicAuthenticationFilter.class);
    }

    public DigestAuthenticationFilter digestAuthenticationFilter() {
        DigestAuthenticationFilter filter = new DigestAuthenticationFilter();
        filter.setAuthenticationEntryPoint(entryPoint);
        filter.setUserDetailsService(userDetailsServiceBean());
        return filter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence charSequence) {
                return charSequence.toString();
            }

            @Override
            public boolean matches(CharSequence charSequence, String s) {
                return charSequence.toString().equals(s);
            }
        };
    }

    @Component
    public static class CustomDigestAuthenticationEntryPoint extends DigestAuthenticationEntryPoint {

        @Override
        public void afterPropertiesSet() {
            setRealmName("uzspring");
            setKey("6gDXX9jDTiNoXZp24x7X");
            setNonceValiditySeconds(10);
            super.afterPropertiesSet();
        }
    }
}
