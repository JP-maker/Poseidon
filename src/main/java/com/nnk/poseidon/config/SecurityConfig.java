package com.nnk.poseidon.config;

import com.nnk.poseidon.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico", "/error").permitAll()
                                .requestMatchers("/user/**").hasAuthority("ADMIN")
                                .anyRequest().authenticated() // Toutes les requêtes nécessitent une authentification (qui sera fournie par AutoLoginFilter)
                )
                .formLogin(form -> form
                        .loginPage("/login") // URL de la page de connexion personnalisée
                        .loginProcessingUrl("/login") // URL où Spring Security traite le formulaire (par défaut)
                        .defaultSuccessUrl("/home", true) // Rediriger vers /home après succès
                        .failureUrl("/login?error=true") // Rediriger en cas d'échec
                        .permitAll() // Autoriser l'accès à la page de login
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL pour déclencher la déconnexion
                        .logoutSuccessUrl("/login?logout=true") // Rediriger après déconnexion
                        .invalidateHttpSession(true) // Invalider la session
                        .deleteCookies("JSESSIONID") // Supprimer les cookies
                        .permitAll() // Autoriser l'accès à l'URL de déconnexion
                )
                .userDetailsService(userDetailsService); // Utiliser notre service custom pour charger les users

        return http.build();
    }
}
