package com.nnk.poseidon.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class AutoLoginFilter extends OncePerRequestFilter {

    private final String defaultUsername;
    private final List<GrantedAuthority> defaultAuthorities;

    public AutoLoginFilter(String defaultUsername, String role) {
        this.defaultUsername = defaultUsername;
        this.defaultAuthorities = Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Vérifier si l'utilisateur n'est pas déjà authentifié
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        if (existingAuth == null || !existingAuth.isAuthenticated() || "anonymousUser".equals(existingAuth.getPrincipal())) {
            // Créer un UserDetails pour l'utilisateur par défaut
            UserDetails defaultUser = new User(defaultUsername, "", defaultAuthorities); // Le mot de passe n'est pas pertinent ici

            // Créer un token d'authentification
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    defaultUser,
                    null, // Pas de credentials car c'est un auto-login
                    defaultUser.getAuthorities());

            // Définir l'authentification dans le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);

            logger.info("Automatically logged in user: " + defaultUsername);
        }

        filterChain.doFilter(request, response);
    }
}
