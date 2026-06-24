package uk.ac.bradford.imagegalleryweb.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Allow public pages, static files, uploaded files and the H2 console.
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()

                // Public gallery viewing
                .requestMatchers(HttpMethod.GET, "/galleries/public").permitAll()
                .requestMatchers(HttpMethod.GET, "/galleries/public/*/photos").permitAll()

                // Everything else requires the user to be logged in.
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf
                // CSRF is ignored for the H2 console so it can still be used during development.
                .ignoringRequestMatchers(PathRequest.toH2Console())
            )
            .headers(headers -> headers
                // Needed so the H2 console can be displayed in a frame.
                .frameOptions(frame -> frame.sameOrigin())
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt is used so passwords are stored securely instead of plain text.
        return new BCryptPasswordEncoder();
    }
}