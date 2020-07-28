package com.example.api.config;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("AuthEntryPoint")
    private BasicAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user")
                .password(passwordEncoder().encode("userPass"))
                .authorities("ROLE_USER");
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                    .anyRequest()
                    .authenticated()
                .and()
                    .httpBasic()
                    .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterAfter(new CustomFilter(), BasicAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static class CustomFilter extends GenericFilterBean {
        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Bean(name = "AuthEntryPoint")
    public BasicAuthenticationEntryPoint authenticationEntryPoint() {
        return new BasicAuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
                response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"" + getRealmName() + "\"");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                PrintWriter writer = response.getWriter();
                writer.println("HTTP Status 401 - " + authException.getMessage());
            }

            @Override
            public void afterPropertiesSet() {
                setRealmName("uzspring");
                super.afterPropertiesSet();
            }
        };
    }
}
