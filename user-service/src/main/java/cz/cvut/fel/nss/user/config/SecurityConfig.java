package cz.cvut.fel.nss.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll())
//                        // Booking access rules
//                        .requestMatchers(HttpMethod.POST, "/api/bookings/**").hasRole("CUSTOMER")
//                        .requestMatchers(HttpMethod.PUT,  "/api/bookings/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.DELETE,"/api/bookings/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.GET,  "/api/bookings/**").authenticated()
//
//                        // H2 console
//                        .requestMatchers("/h2-console/**").permitAll()
//
//                        .anyRequest().denyAll()
//                )
//                .httpBasic(Customizer.withDefaults())
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
}
