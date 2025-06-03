package com.nnk.poseidon.controllers;

import com.nnk.poseidon.dto.RuleNameDTO; // Utiliser le DTO
import com.nnk.poseidon.services.RuleNameService; // Le service concret
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Contrôleur pour gérer les opérations CRUD pour les {@link RuleNameDTO}.
 * Gère les requêtes web relatives aux définitions de règles et interagit avec le {@link RuleNameService}.
 */
@Slf4j
@Controller
public class RuleNameController {

    private final RuleNameService ruleNameService;

    @Autowired
    public RuleNameController(RuleNameService ruleNameService) {
        this.ruleNameService = ruleNameService;
    }

    /**
     * Affiche la liste de toutes les règles (DTOs).
     *
     * @param model Le modèle Spring MVC.
     * @return Le nom de la vue "ruleName/list".
     */
    @RequestMapping("/ruleName/list")
    public String home(Model model) {
        log.info("Requête pour lister tous les DTOs de RuleName");
        try {
            List<RuleNameDTO> RuleNameDTOs = ruleNameService.findAll();
            model.addAttribute("ruleNames", RuleNameDTOs); // Le nom dans le modèle est "ruleNames"
            log.debug("Nombre de DTOs de RuleName récupérés : {}", RuleNameDTOs.size());
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des DTOs de RuleName : {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Erreur lors de la récupération des règles.");
        }
        return "ruleName/list";
    }

    /**
     * Affiche le formulaire pour ajouter une nouvelle règle.
     *
     * @param model Le modèle Spring MVC.
     * @return Le nom de la vue "ruleName/add".
     */
    @GetMapping("/ruleName/add")
    public String addRuleForm(Model model) { // Le DTO sera injecté avec @ModelAttribute si besoin
        log.info("Requête pour afficher le formulaire d'ajout d'une règle (DTO)");
        model.addAttribute("ruleName", new RuleNameDTO()); // Le nom dans le modèle est "ruleName"
        return "ruleName/add";
    }

    /**
     * Valide et sauvegarde une nouvelle règle à partir d'un DTO.
     *
     * @param RuleNameDTO Le {@link RuleNameDTO} peuplé à partir du formulaire.
     * @param result Le {@link BindingResult} pour les erreurs de validation.
     * @param model Le modèle Spring MVC.
     * @param redirectAttributes Attributs pour la redirection.
     * @return "ruleName/add" en cas d'erreur, sinon redirection vers "/ruleName/list".
     */
    @PostMapping("/ruleName/validate")
    public String validate(@Valid @ModelAttribute("ruleName") RuleNameDTO RuleNameDTO, // Explicite @ModelAttribute
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        log.info("Requête pour valider et sauvegarder un nouveau DTO de règle : {}", RuleNameDTO);
        if (result.hasErrors()) {
            log.warn("Erreurs de validation pour le nouveau DTO de règle : {}", result.getAllErrors());
            // model.addAttribute("ruleName", RuleNameDTO); // Spring le fait déjà
            return "ruleName/add";
        }
        try {
            ruleNameService.save(RuleNameDTO);
            log.info("DTO de règle sauvegardé avec succès : {}", RuleNameDTO.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Règle ajoutée avec succès !");
            return "redirect:/ruleName/list";
        } catch (Exception e) {
            log.error("Erreur lors de la sauvegarde du DTO de règle : {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Erreur lors de l'ajout de la règle : " + e.getMessage());
            // model.addAttribute("ruleName", RuleNameDTO); // Spring le fait déjà
            return "ruleName/add";
        }
    }

    /**
     * Affiche le formulaire de mise à jour pour une règle existante (DTO).
     *
     * @param id L'ID de la règle à mettre à jour.
     * @param model Le modèle Spring MVC.
     * @param redirectAttributes Attributs pour la redirection.
     * @return "ruleName/update" si trouvé, sinon redirection vers "/ruleName/list".
     */
    @GetMapping("/ruleName/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        log.info("Requête pour afficher le formulaire de mise à jour pour le DTO règle id : {}", id);
        try {
            RuleNameDTO RuleNameDTO = ruleNameService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Règle (DTO) invalide avec l'Id :" + id));
            model.addAttribute("ruleName", RuleNameDTO);
            log.debug("DTO de règle trouvé : {}", RuleNameDTO);
        } catch (IllegalArgumentException e) {
            log.warn("DTO de règle non trouvé pour la mise à jour avec l'id {} : {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/ruleName/list";
        }
        return "ruleName/update";
    }

    /**
     * Valide et met à jour une règle existante à partir d'un DTO.
     *
     * @param id L'ID de la règle à mettre à jour.
     * @param RuleNameDTO Le {@link RuleNameDTO} peuplé à partir du formulaire.
     * @param result Le {@link BindingResult} pour les erreurs de validation.
     * @param model Le modèle Spring MVC.
     * @param redirectAttributes Attributs pour la redirection.
     * @return "ruleName/update" en cas d'erreur, sinon redirection vers "/ruleName/list".
     */
    @PostMapping("/ruleName/update/{id}")
    public String updateRuleName(@PathVariable("id") Integer id,
                                 @Valid @ModelAttribute("ruleName") RuleNameDTO RuleNameDTO, // Explicite @ModelAttribute
                                 BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        log.info("Requête pour mettre à jour le DTO règle id {} : {}", id, RuleNameDTO);
        RuleNameDTO.setId(id); // S'assurer que l'ID du DTO est celui du path variable

        if (result.hasErrors()) {
            log.warn("Erreurs de validation lors de la mise à jour du DTO règle id {} : {}", id, result.getAllErrors());
            // model.addAttribute("ruleName", RuleNameDTO); // Spring le fait déjà
            return "ruleName/update";
        }
        try {
            ruleNameService.save(RuleNameDTO);
            log.info("DTO de règle mis à jour avec succès : {}", RuleNameDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Règle mise à jour avec succès !");
            return "redirect:/ruleName/list";
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du DTO de règle id {} : {}", id, e.getMessage(), e);
            model.addAttribute("errorMessage", "Erreur lors de la mise à jour de la règle : " + e.getMessage());
            // model.addAttribute("ruleName", RuleNameDTO); // Spring le fait déjà
            return "ruleName/update";
        }
    }

    /**
     * Supprime une règle par son ID.
     *
     * @param id L'ID de la règle à supprimer.
     * @param redirectAttributes Attributs pour la redirection.
     * @return Redirection vers "/ruleName/list".
     */
    @GetMapping("/ruleName/delete/{id}")
    public String deleteRuleName(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        log.info("Requête pour supprimer la règle id : {}", id);
        try {
            ruleNameService.deleteById(id);
            log.info("Règle supprimée avec succès, id : {}", id);
            redirectAttributes.addFlashAttribute("successMessage", "Règle supprimée avec succès !");
        } catch (IllegalArgumentException e) {
            log.warn("Erreur lors de la suppression de la règle id {} : {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/ruleName/list";
    }
}