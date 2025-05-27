package com.nnk.poseidon.config;

import com.nnk.poseidon.filter.AutoLoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// Pas besoin de UserDetailsService ou PasswordEncoder si vous n'avez que l'auto-login

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public AutoLoginFilter autoLoginFilter() {
        // Vous pouvez rendre le nom d'utilisateur et le rôle configurables (via @Value par exemple)
        return new AutoLoginFilter("guestUser", "ROLE_GUEST");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AutoLoginFilter autoLoginFilter) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/*", "/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                                .anyRequest().authenticated() // Toutes les requêtes nécessitent une authentification (qui sera fournie par AutoLoginFilter)
                )
                // Ajoutez votre filtre avant le UsernamePasswordAuthenticationFilter (ou un autre filtre pertinent)
                .addFilterBefore(autoLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable()); // Désactivez CSRF si vous n'utilisez pas de formulaires soumis par l'utilisateur
        // ou si ce mode d'auto-login ne le justifie pas.

        // Puisque l'authentification est automatique, le formLogin et httpBasic ne sont plus nécessaires
        // à moins que vous ne vouliez les garder comme fallback ou pour des admins.
        http.formLogin(formLogin -> formLogin.disable());
        http.httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }
}
