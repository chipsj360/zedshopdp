package com.ecommerce.zedshop.config;


import com.ecommerce.zedshop.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
@EnableWebSecurity
@ComponentScan
public class SecurityConfig {

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;
   @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return new CustomUserDetailsService();
    }
    @Bean
    public ServletWebServerFactory servletWebServerFactory() {
        return new TomcatServletWebServerFactory();
    }
@Bean
public  AuthenticationProvider authenticationProvider(){
       DaoAuthenticationProvider authProvider=new DaoAuthenticationProvider();
       authProvider.setUserDetailsService(userDetailsService());
       authProvider.setPasswordEncoder(bCryptPasswordEncoder());
       return authProvider;
}

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)throws Exception{
       return config.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
                .authorizeRequests()
                .requestMatchers("/css/**","/images/**","/","/register", "/process_register","register_success","/js/**","/product_detail/**")
                .permitAll()
                .and()
                .authorizeRequests().requestMatchers("/dashboard").hasAuthority("ADMIN")
                .and().formLogin()
                .loginPage("/login")
                .usernameParameter("email")
                .successHandler(loginSuccessHandler)
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/login?logout")
                .and()
                .exceptionHandling().accessDeniedPage("/403")
                .and()
                .build();


    }

}
