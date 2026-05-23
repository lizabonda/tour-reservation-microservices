package cz.cvut.fel.nss.booking.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Booking access rules
                        .requestMatchers(HttpMethod.POST, "/api/bookings/**").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/bookings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/bookings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/bookings/**").hasRole("ADMIN")

                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("customer")
                        .password("{noop}password")
                        .roles("CUSTOMER")
                        .build(),
                User.withUsername("admin")
                        .password("{noop}admin")
                        .roles("ADMIN")
                        .build()
        );
    }

    @Bean
    RequestInterceptor feignClientInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                String authHeader = attributes.getRequest().getHeader("Authorization");
                if (authHeader != null) {
                    requestTemplate.header("Authorization", authHeader);
                }
            }
        };
    }
}