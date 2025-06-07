package com.nnk.poseidon.controllers;

import com.nnk.poseidon.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller gérant l'authentification et les accès sécurisés.
 * <p>
 * Ce contrôleur est responsable de la gestion des points d'entrée liés à la
 * connexion de l'utilisateur, de l'affichage de certaines pages sécurisées
 * et de la gestion des erreurs d'accès. Il utilise {@link ModelAndView} pour
 * renvoyer à la fois le modèle et la vue dans certains cas.
 * </p>
 *
 * @see org.springframework.security.config.annotation.web.builders.HttpSecurity Pour la configuration de la sécurité qui redirige vers ces points d'entrée.
 */
@Slf4j
@Controller
public class LoginController {

    /**
     * Injection du repository pour interagir avec les données des utilisateurs.
     * Utilisé ici pour récupérer la liste des utilisateurs.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Affiche la page de connexion personnalisée.
     * <p>
     * Ce point d'entrée gère les requêtes GET sur "/login". Il est généralement
     * configuré dans Spring Security comme le formulaire de connexion par défaut.
     * </p>
     *
     * @return Le nom logique de la vue "login" à afficher.
     */
    @GetMapping("/login")
    public String login() {
        log.debug("Accès à la page de login");
        return "login";
    }

    /**
     * Affiche la liste de tous les utilisateurs enregistrés.
     * <p>
     * <b>Note:</b> Bien que le chemin URL soit "secure/article-details", cette méthode
     * affiche en réalité la liste des <b>utilisateurs</b> et non des articles.
     * Le préfixe "secure/" indique que cette URL est protégée et nécessite une
     * authentification préalable.
     * </p>
     *
     * @return Un objet {@link ModelAndView} contenant la liste des utilisateurs sous
     *         l'attribut "users" et le nom de la vue "user/list".
     */
    @GetMapping("secure/article-details")
    public ModelAndView getAllUserArticles() {
        log.debug("Accès à la page sécurisée listant les utilisateurs");
        ModelAndView mav = new ModelAndView();
        mav.addObject("users", userRepository.findAll());
        mav.setViewName("user/list");
        return mav;
    }

    /**
     * Gère et affiche la page d'erreur pour accès non autorisé (Erreur 403 Forbidden).
     * <p>
     * Ce point d'entrée est généralement la cible d'une redirection configurée dans
     * Spring Security lorsqu'un utilisateur authentifié tente d'accéder à une ressource
     * pour laquelle il n'a pas les droits nécessaires.
     * </p>
     *
     * @return Un objet {@link ModelAndView} contenant un message d'erreur sous
     *         l'attribut "errorMsg" et le nom de la vue "403".
     */
    @GetMapping("error")
    public ModelAndView error() {
        log.warn("Affichage de la page d'erreur 403 pour accès non autorisé");
        ModelAndView mav = new ModelAndView();
        String errorMessage = "You are not authorized for the requested data.";
        mav.addObject("errorMsg", errorMessage);
        mav.setViewName("403");
        return mav;
    }
}