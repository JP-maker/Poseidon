package com.nnk.poseidon.controllers;

import com.nnk.poseidon.dto.RatingDTO;
import com.nnk.poseidon.services.RatingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Pour les messages flash

import java.util.Collections;
import java.util.List;

/**
 * Contrôleur pour gérer les opérations CRUD pour les {@link RatingDTO}.
 * Gère les requêtes web relatives aux notations et interagit avec le {@link RatingService}.
 */
@Slf4j
@Controller
public class RatingController {

    private final RatingService ratingService;

    /**
     * Construit un nouveau RatingController avec le RatingService donné.
     * @param ratingService Le service pour la gestion des notations.
     */
    @Autowired // Bonne pratique d'ajouter @Autowired sur le constructeur
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    /**
     * Affiche la liste de toutes les notations (DTOs).
     *
     * @param model Le modèle Spring MVC.
     * @return Le nom de la vue "rating/list".
     */
    @RequestMapping("/rating/list") // Peut aussi être @GetMapping
    public String home(Model model) {
        log.info("Requête pour lister tous les DTOs de Rating"); // Log adapté
        List<RatingDTO> RatingDTOs = Collections.emptyList(); // Initialisation
        try {
            RatingDTOs = ratingService.getAllRatings();
            model.addAttribute("ratings", RatingDTOs); // Le nom dans le modèle est "ratings"
            log.debug("Nombre de DTOs de rating récupérés : {}", RatingDTOs.size());
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des DTOs de rating : {}", e.getMessage(), e); // Log de l'exception
            model.addAttribute("errorMessage", "Erreur lors de la récupération des notations."); // Message pour l'utilisateur
        }
        log.debug("Modèle mis à jour avec les DTOs de rating");
        return "rating/list";
    }

    /**
     * Affiche le formulaire pour ajouter une nouvelle notation.
     *
     * @param model Le modèle Spring MVC.
     * @return Le nom de la vue "rating/add".
     */
    @GetMapping("/rating/add")
    public String addRatingForm(Model model) { // Le paramètre RatingDTO est injecté implicitement
        log.info("Requête pour afficher le formulaire d'ajout d'une notation (DTO)"); // Log adapté
        model.addAttribute("RatingDTO", new RatingDTO()); // Le nom dans le modèle est "RatingDTO"
        log.debug("Modèle mis à jour avec un nouveau RatingDTO");
        return "rating/add";
    }

    /**
     * Valide et sauvegarde une nouvelle notation à partir d'un DTO.
     *
     * @param RatingDTO Le {@link RatingDTO} peuplé à partir du formulaire.
     * @param result Le {@link BindingResult} pour les erreurs de validation.
     * @param model Le modèle Spring MVC.
     * @param redirectAttributes Attributs pour la redirection.
     * @return "rating/add" en cas d'erreur, sinon redirection vers "/rating/list".
     */
    @PostMapping("/rating/validate")
    public String validate(@Valid @ModelAttribute("RatingDTO") RatingDTO RatingDTO, // Explicite @ModelAttribute
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        log.info("Requête pour valider et sauvegarder un nouveau DTO de notation : {}", RatingDTO); // Log adapté
        if (result.hasErrors()) {
            log.warn("Erreurs de validation pour le nouveau DTO de notation : {}", result.getAllErrors()); // Log adapté
            // Pas besoin de model.addAttribute("error"), les erreurs sont dans BindingResult
            // model.addAttribute("RatingDTO", RatingDTO); // Spring le fait déjà
            return "rating/add";
        }
        try {
            ratingService.saveRating(RatingDTO); // Le service gère le mapping
            log.info("DTO de notation sauvegardé avec succès : {}", RatingDTO.getId()); // Log l'ID si disponible
            redirectAttributes.addFlashAttribute("successMessage", "Notation ajoutée avec succès !"); // Message flash
            return "redirect:/rating/list";
        } catch (Exception e) {
            log.error("Erreur lors de la sauvegarde du DTO de notation : {}", e.getMessage(), e); // Log de l'exception
            model.addAttribute("errorMessage", "Erreur lors de l'ajout de la notation : " + e.getMessage()); // Message pour l'utilisateur
            // model.addAttribute("RatingDTO", RatingDTO); // Spring le fait déjà
            return "rating/add";
        }
    }

    /**
     * Affiche le formulaire de mise à jour pour une notation existante (DTO).
     *
     * @param id L'ID de la notation à mettre à jour.
     * @param model Le modèle Spring MVC.
     * @param redirectAttributes Attributs pour la redirection.
     * @return "rating/update" si trouvé, sinon redirection vers "/rating/list".
     */
    @GetMapping("/rating/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        log.info("Requête pour afficher le formulaire de mise à jour pour le DTO notation id : {}", id); // Log adapté
        try {
            RatingDTO RatingDTO = ratingService.getRatingById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Notation (DTO) invalide avec l'Id :" + id));
            model.addAttribute("RatingDTO", RatingDTO);
            log.debug("DTO de notation trouvé : {}", RatingDTO);
        } catch (IllegalArgumentException e) {
            log.warn("DTO de notation non trouvé pour la mise à jour avec l'id {} : {}", id, e.getMessage()); // Log adapté
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage()); // Message flash
            return "redirect:/rating/list";
        }
        log.debug("Modèle mis à jour avec le DTO de notation pour la mise à jour");
        return "rating/update";
    }

    /**
     * Valide et met à jour une notation existante à partir d'un DTO.
     *
     * @param id L'ID de la notation à mettre à jour.
     * @param RatingDTO Le {@link RatingDTO} peuplé à partir du formulaire.
     * @param result Le {@link BindingResult} pour les erreurs de validation.
     * @param model Le modèle Spring MVC.
     * @param redirectAttributes Attributs pour la redirection.
     * @return "rating/update" en cas d'erreur, sinon redirection vers "/rating/list".
     */
    @PostMapping("/rating/update/{id}")
    public String updateRating(@PathVariable("id") Integer id,
                               @Valid @ModelAttribute("RatingDTO") RatingDTO RatingDTO, // Explicite @ModelAttribute
                               BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        log.info("Requête pour mettre à jour le DTO notation id {} : {}", id, RatingDTO); // Log adapté
        RatingDTO.setId(id); // S'assurer que l'ID du DTO est celui du path variable

        if (result.hasErrors()) {
            log.warn("Erreurs de validation lors de la mise à jour du DTO notation id {} : {}", id, result.getAllErrors()); // Log adapté
            // model.addAttribute("RatingDTO", RatingDTO); // Spring le fait déjà
            return "rating/update";
        }
        try {
            ratingService.saveRating(RatingDTO); // Le service gère le mapping et la recherche de l'entité existante
            log.info("DTO de notation mis à jour avec succès : {}", RatingDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Notation mise à jour avec succès !"); // Message flash
            return "redirect:/rating/list";
        } catch (Exception e) { // Attraper une exception plus large ou spécifique si le service en lance
            log.error("Erreur lors de la mise à jour du DTO de notation id {} : {}", id, e.getMessage(), e); // Log de l'exception
            model.addAttribute("errorMessage", "Erreur lors de la mise à jour de la notation : " + e.getMessage()); // Message pour l'utilisateur
            // model.addAttribute("RatingDTO", RatingDTO); // Spring le fait déjà
            return "rating/update";
        }
    }

    /**
     * Supprime une notation par son ID.
     *
     * @param id L'ID de la notation à supprimer.
     * @param redirectAttributes Attributs pour la redirection.
     * @return Redirection vers "/rating/list".
     */
    @GetMapping("/rating/delete/{id}")
    public String deleteRating(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) { // Model n'est plus nécessaire
        log.info("Requête pour supprimer la notation id : {}", id); // Log adapté
        try {
            ratingService.deleteRating(id); // Le service gère la vérification d'existence
            log.info("Notation supprimée avec succès, id : {}", id);
            redirectAttributes.addFlashAttribute("successMessage", "Notation supprimée avec succès !"); // Message flash
        } catch (IllegalArgumentException e) { // Attraper l'exception spécifique du service
            log.warn("Erreur lors de la suppression de la notation id {} : {}", id, e.getMessage()); // Log adapté
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage()); // Message flash
        }
        return "redirect:/rating/list";
    }
}