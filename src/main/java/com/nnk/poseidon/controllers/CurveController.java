package com.nnk.poseidon.controllers;

import com.nnk.poseidon.dto.CurvePointDTO;
import com.nnk.poseidon.services.CurvePointService;
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
 * Contrôleur pour gérer les opérations CRUD pour les {@link CurvePointDTO}.
 * Gère les requêtes web relatives aux points de courbe et interagit avec le {@link CurvePointService}.
 */
@Slf4j
@Controller
public class CurveController {

    private final CurvePointService curvePointService; // Injection du service concret

    /**
     * Constructeur pour injecter le service de gestion des points de courbe.
     *
     * @param curvePointService Le service concret pour gérer les opérations sur les points de courbe.
     */
    @Autowired
    public CurveController(CurvePointService curvePointService) {
        this.curvePointService = curvePointService;
    }

    /**
     * Affiche la liste de tous les points de courbe (DTOs).
     *
     * @param model Le modèle Spring MVC.
     * @return Le nom de la vue "curvePoint/list".
     */
    @RequestMapping("/curvePoint/list")
    public String home(Model model) {
        log.info("Requête pour lister tous les DTOs de points de courbe");
        List<CurvePointDTO> curvePointDTOs = curvePointService.findAll();
        model.addAttribute("curvePoints", curvePointDTOs); // Le nom dans le modèle reste "curvePoints" pour la vue
        return "curvePoint/list";
    }

    /**
     * Affiche le formulaire pour ajouter un nouveau point de courbe.
     *
     * @param curvePointDTO Un nouvel objet {@link CurvePointDTO} pour lier les données du formulaire.
     * @param model Le modèle Spring MVC.
     * @return Le nom de la vue "curvePoint/add".
     */
    @GetMapping("/curvePoint/add")
    public String addBidForm(CurvePointDTO curvePointDTO, Model model) { // Utilise CurvePointDTO
        log.info("Requête pour afficher le formulaire d'ajout d'un point de courbe (DTO)");
        model.addAttribute("curvePoint", curvePointDTO); // Le nom dans le modèle peut rester "curvePoint"
        return "curvePoint/add";
    }

    /**
     * Valide et sauvegarde un nouveau point de courbe à partir d'un DTO.
     *
     * @param curvePointDTO Le {@link CurvePointDTO} peuplé à partir du formulaire.
     * @param result Le {@link BindingResult} pour les erreurs de validation.
     * @param model Le modèle Spring MVC.
     * @param redirectAttributes Attributs pour la redirection.
     * @return "curvePoint/add" en cas d'erreur, sinon redirection vers "/curvePoint/list".
     */
    @PostMapping("/curvePoint/validate")
    public String validate(@Valid @ModelAttribute("curvePoint") CurvePointDTO curvePointDTO,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        log.info("Requête pour valider et sauvegarder un nouveau DTO de point de courbe : {}", curvePointDTO);
        if (result.hasErrors()) {
            log.warn("Erreurs de validation pour le nouveau DTO de point de courbe : {}", result.getAllErrors());
            return "curvePoint/add";
        }
        try {
            curvePointService.save(curvePointDTO);
            log.info("DTO de point de courbe sauvegardé avec succès : {}", curvePointDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Point de Courbe ajouté avec succès !");
            return "redirect:/curvePoint/list";
        } catch (Exception e) {
            log.error("Erreur lors de la sauvegarde du DTO de point de courbe : {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Erreur lors de la sauvegarde : " + e.getMessage());
            return "curvePoint/add";
        }
    }

    /**
     * Affiche le formulaire de mise à jour pour un point de courbe existant (DTO).
     *
     * @param id L'ID du point de courbe à mettre à jour.
     * @param model Le modèle Spring MVC.
     * @param redirectAttributes Attributs pour la redirection.
     * @return "curvePoint/update" si trouvé, sinon redirection vers "/curvePoint/list".
     */
    @GetMapping("/curvePoint/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        log.info("Requête pour afficher le formulaire de mise à jour pour le DTO point de courbe id : {}", id);
        try {
            CurvePointDTO curvePointDTO = curvePointService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Point de Courbe (DTO) invalide avec l'Id :" + id));
            model.addAttribute("curvePoint", curvePointDTO);
            return "curvePoint/update";
        } catch (IllegalArgumentException e) {
            log.warn("DTO de point de courbe non trouvé pour la mise à jour avec l'id {} : {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/curvePoint/list";
        }
    }

    /**
     * Valide et met à jour un point de courbe existant à partir d'un DTO.
     *
     * @param id L'ID du point de courbe à mettre à jour.
     * @param curvePointDTO Le {@link CurvePointDTO} peuplé à partir du formulaire.
     * @param result Le {@link BindingResult} pour les erreurs de validation.
     * @param model Le modèle Spring MVC.
     * @param redirectAttributes Attributs pour la redirection.
     * @return "curvePoint/update" en cas d'erreur, sinon redirection vers "/curvePoint/list".
     */
    @PostMapping("/curvePoint/update/{id}")
    public String updateBid(@PathVariable("id") Integer id,
                            @Valid @ModelAttribute("curvePoint") CurvePointDTO curvePointDTO,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        log.info("Requête pour mettre à jour le DTO point de courbe id {} : {}", id, curvePointDTO);
        curvePointDTO.setId(id); // S'assurer que l'ID du DTO est celui du path variable

        if (result.hasErrors()) {
            log.warn("Erreurs de validation lors de la mise à jour du DTO point de courbe id {} : {}", id, result.getAllErrors());
            model.addAttribute("curvePoint", curvePointDTO); // Renvoyer DTO avec erreurs
            return "curvePoint/update";
        }
        try {
            curvePointService.save(curvePointDTO);
            log.info("DTO de point de courbe mis à jour avec succès : {}", curvePointDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Point de Courbe mis à jour avec succès !");
            return "redirect:/curvePoint/list";
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du DTO point de courbe id {} : {}", id, e.getMessage(), e);
            model.addAttribute("curvePoint", curvePointDTO);
            model.addAttribute("errorMessage", "Erreur lors de la mise à jour : " + e.getMessage());
            return "curvePoint/update";
        }
    }

    /**
     * Supprime un point de courbe par son ID.
     *
     * @param id L'ID du point de courbe à supprimer.
     * @param redirectAttributes Attributs pour la redirection.
     * @return Redirection vers "/curvePoint/list".
     */
    @GetMapping("/curvePoint/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) { // Model n'est plus nécessaire ici
        log.info("Requête pour supprimer le point de courbe id : {}", id);
        try {
            curvePointService.deleteById(id);
            log.info("Point de courbe supprimé avec succès, id : {}", id);
            redirectAttributes.addFlashAttribute("successMessage", "Point de Courbe supprimé avec succès !");
        } catch (IllegalArgumentException e) {
            log.warn("Erreur lors de la suppression du point de courbe id {} : {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/curvePoint/list";
    }
}