package com.nnk.poseidon.controllers;

import com.nnk.poseidon.dto.TradeDTO;
import com.nnk.poseidon.services.TradeService; // Assurez-vous que cet import est correct selon votre nom de classe service
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur MVC pour la gestion des opérations CRUD sur les entités Trade.
 * Gère les requêtes HTTP relatives aux trades (affichage de liste, ajout, mise à jour, suppression)
 * et interagit avec la couche service ({@link TradeService}) pour la logique métier.
 */
@Controller
@RequestMapping("/trade") // Préfixe de base pour toutes les URLs gérées par ce contrôleur
public class TradeController {

    private static final Logger logger = LoggerFactory.getLogger(TradeController.class);

    private final TradeService tradeService;

    /**
     * Constructeur pour l'injection de dépendance du service Trade.
     *
     * @param tradeService Le service {@link TradeService} à injecter, responsable de la logique métier des trades.
     */
    @Autowired
    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    /**
     * Gère les requêtes GET vers "/trade/list".
     * Récupère tous les trades via le {@link TradeService} et les ajoute au modèle
     * pour affichage dans la vue "trade/list".
     *
     * @param model L'objet {@link Model} utilisé pour passer des attributs à la vue.
     * @return Le nom de la vue Thymeleaf à afficher ("trade/list").
     */
    @GetMapping("/list")
    public String home(Model model) {
        List<TradeDTO> trades = tradeService.findAllTrades();
        model.addAttribute("trades", trades); // Ajoute la liste des trades au modèle sous la clé "trades"
        logger.info("Affichage de la liste des trades. Nombre de trades trouvés: {}", trades.size());
        return "trade/list"; // Nom du template Thymeleaf (ex: /resources/templates/trade/list.html)
    }

    /**
     * Gère les requêtes GET vers "/trade/add".
     * Prépare et affiche le formulaire permettant d'ajouter un nouveau trade.
     * Un objet {@link TradeDTO} vide est ajouté au modèle pour le data binding du formulaire.
     *
     * @param model L'objet {@link Model} utilisé pour passer des attributs à la vue.
     * @return Le nom de la vue Thymeleaf à afficher ("trade/add").
     */
    @GetMapping("/add")
    public String addTradeForm(Model model) {
        model.addAttribute("trade", new TradeDTO()); // Objet "trade" pour lier les données du formulaire
        logger.info("Affichage du formulaire d'ajout d'un nouveau trade.");
        return "trade/add"; // Nom du template Thymeleaf (ex: /resources/templates/trade/add.html)
    }

    /**
     * Gère les requêtes POST vers "/trade/validate" (soumission du formulaire d'ajout).
     * Valide les données du trade soumises. Si la validation réussit, le trade est sauvegardé
     * via le {@link TradeService} et l'utilisateur est redirigé vers la liste des trades.
     * En cas d'erreur de validation, le formulaire d'ajout est ré-affiché avec les messages d'erreur.
     *
     * @param tradeDTO Le {@link TradeDTO} peuplé avec les données du formulaire et annoté avec {@link Valid} pour la validation.
     * @param result Le {@link BindingResult} contenant le résultat de la validation (erreurs éventuelles).
     * @param model L'objet {@link Model} (non utilisé directement ici si succès, mais disponible).
     * @return Une chaîne de redirection vers "/trade/list" en cas de succès, ou "trade/add" en cas d'erreur.
     */
    @PostMapping("/validate")
    public String validate(@Valid @ModelAttribute("trade") TradeDTO tradeDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            logger.warn("Erreurs de validation lors de la soumission du formulaire d'ajout de trade: {}", result.getAllErrors());
            // Pas besoin de rajouter tradeDTO au model, @ModelAttribute le fait.
            return "trade/add"; // Retourne à la page d'ajout si erreurs
        }
        tradeService.saveTrade(tradeDTO);
        logger.info("Nouveau trade sauvegardé avec succès. Compte: {}, Type: {}", tradeDTO.getAccount(), tradeDTO.getType());
        return "redirect:/trade/list"; // Redirige vers la liste après succès
    }

    /**
     * Gère les requêtes GET vers "/trade/update/{id}".
     * Affiche le formulaire de mise à jour pour un trade spécifique, identifié par son ID.
     * Le trade existant est récupéré via le {@link TradeService} et ajouté au modèle.
     * Si le trade n'est pas trouvé, redirige vers la liste des trades.
     *
     * @param id L'identifiant du trade à mettre à jour, extrait de l'URL ({@link PathVariable}).
     * @param model L'objet {@link Model} utilisé pour passer le trade à la vue.
     * @return Le nom de la vue Thymeleaf "trade/update" si le trade est trouvé, sinon une redirection vers "/trade/list".
     */
    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        Optional<TradeDTO> tradeDTOOptional = tradeService.findTradeById(id);
        if (tradeDTOOptional.isPresent()) {
            model.addAttribute("trade", tradeDTOOptional.get()); // Ajoute le trade trouvé au modèle
            logger.info("Affichage du formulaire de mise à jour pour le trade ID: {}", id);
            return "trade/update"; // Nom du template Thymeleaf (ex: /resources/templates/trade/update.html)
        } else {
            logger.warn("Tentative d'accès au formulaire de mise à jour pour un trade non existant. ID: {}", id);
            return "redirect:/trade/list"; // Ou une page d'erreur 404
        }
    }

    /**
     * Gère les requêtes POST vers "/trade/update/{id}" (soumission du formulaire de mise à jour).
     * Valide les données du trade soumises. Si la validation réussit, le trade existant est mis à jour
     * via le {@link TradeService} et l'utilisateur est redirigé vers la liste des trades.
     * En cas d'erreur de validation, le formulaire de mise à jour est ré-affiché avec les messages d'erreur.
     *
     * @param id L'identifiant du trade à mettre à jour, extrait de l'URL ({@link PathVariable}).
     * @param tradeDTO Le {@link TradeDTO} peuplé avec les données du formulaire et annoté avec {@link Valid}.
     * @param result Le {@link BindingResult} contenant le résultat de la validation.
     * @param model L'objet {@link Model} (non utilisé directement ici si succès, mais disponible).
     * @return Une chaîne de redirection vers "/trade/list" en cas de succès, ou "trade/update" en cas d'erreur.
     */
    @PostMapping("/update/{id}")
    public String updateTrade(@PathVariable("id") Integer id, @Valid @ModelAttribute("trade") TradeDTO tradeDTO,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            logger.warn("Erreurs de validation lors de la soumission du formulaire de mise à jour pour le trade ID {}: {}", id, result.getAllErrors());
            // L'ID doit être dans l'objet trade pour que th:action dans le formulaire fonctionne correctement si on retourne à la vue.
            // @ModelAttribute("trade") s'assure que l'objet trade est remis dans le modèle.
            // Il est bon de s'assurer que l'ID est correctement défini si on retourne au formulaire.
            tradeDTO.setTradeId(id); // Assure que l'ID est présent pour le rendu du formulaire en cas d'erreur
            return "trade/update"; // Retourne à la page de mise à jour si erreurs
        }

        // Il est prudent de s'assurer que l'ID du DTO correspond à l'ID du chemin
        tradeDTO.setTradeId(id);

        Optional<TradeDTO> updatedTrade = tradeService.updateTrade(id, tradeDTO);
        if(updatedTrade.isPresent()){
            logger.info("Trade avec ID {} mis à jour avec succès.", id);
        } else {
            // Ce cas peut se produire si le trade a été supprimé entre le GET et le POST,
            // ou si la logique de updateTrade retourne Optional.empty() pour une autre raison.
            logger.error("Échec de la mise à jour du trade avec ID {}. Le trade n'a pas été trouvé ou la mise à jour a échoué.", id);
            // Rediriger ou afficher une erreur spécifique. Pour l'instant, on redirige vers la liste.
        }
        return "redirect:/trade/list"; // Redirige vers la liste après succès
    }

    /**
     * Gère les requêtes GET vers "/trade/delete/{id}".
     * Supprime le trade spécifié par son ID via le {@link TradeService}.
     * Redirige ensuite l'utilisateur vers la liste des trades.
     *
     * @param id L'identifiant du trade à supprimer, extrait de l'URL ({@link PathVariable}).
     * @param model L'objet {@link Model} (non utilisé activement ici, mais disponible par Spring MVC).
     * @return Une chaîne de redirection vers "/trade/list".
     */
    @GetMapping("/delete/{id}")
    public String deleteTrade(@PathVariable("id") Integer id, Model model) {
        // Optionnel: Vérifier si le trade existe avant suppression pour un logging plus précis
        if (tradeService.findTradeById(id).isPresent()) {
            tradeService.deleteTradeById(id);
            logger.info("Trade avec ID {} supprimé avec succès.", id);
        } else {
            logger.warn("Tentative de suppression d'un trade non existant. ID: {}", id);
        }
        return "redirect:/trade/list"; // Redirige toujours vers la liste
    }
}