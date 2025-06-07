package com.nnk.poseidon.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller gérant les points d'entrée principaux de l'application.
 * <p>
 * Ce contrôleur est responsable de l'affichage de la page d'accueil de l'application
 * Poseidon Trading. Il est annoté avec {@link Controller} pour être détecté par le
 * DispatcherServlet de Spring MVC.
 * </p>
 */
@Controller
public class HomeController {

	/**
	 * Affiche la page d'accueil de l'application.
	 * <p>
	 * Cette méthode gère les requêtes HTTP GET pour les URL racine ("/") et "/home".
	 * Elle ne nécessite aucune logique métier particulière et retourne simplement le nom
	 * logique de la vue "home", qui sera ensuite résolue par le ViewResolver de Spring
	 * pour afficher le template correspondant (par exemple, home.html).
	 * </p>
	 *
	 * @param model L'objet {@link Model} fourni par Spring, utilisé pour passer des
	 *              attributs à la vue. Dans ce cas, il n'est pas utilisé mais reste
	 *              disponible par le framework.
	 * @return Une chaîne de caractères représentant le nom logique de la vue à afficher ("home").
	 */
	@GetMapping({"/", "/home"})
	public String home(Model model) {
		return "home";
	}

}
