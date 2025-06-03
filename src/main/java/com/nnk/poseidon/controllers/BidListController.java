package com.nnk.poseidon.controllers;

import com.nnk.poseidon.dto.BidListDTO; // Utiliser le DTO
import com.nnk.poseidon.services.BidListService; // Le service concret
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Contrôleur pour gérer les opérations CRUD pour les {@link BidListDTO}.
 * Gère les requêtes web relatives aux offres (bids) et interagit avec le {@link BidListService}.
 */
@Slf4j
@Controller
public class BidListController {

    private final BidListService bidListService;

    @Autowired
    public BidListController(BidListService bidListService) {
        this.bidListService = bidListService;
    }

    /**
     * Affiche la liste de toutes les offres (DTOs).
     *
     * @param model Le modèle Spring MVC.
     * @return Le nom de la vue "bidList/list".
     */
    @RequestMapping("/bidList/list")
    public String home(Model model) {
        log.info("Requête pour lister tous les DTOs de BidList");
        List<BidListDTO> bidListDTOs = bidListService.findAll();
        model.addAttribute("bidLists", bidListDTOs); // Le nom dans le modèle est "bidLists"
        return "bidList/list";
    }

    /**
     * Affiche le formulaire pour ajouter une nouvelle offre.
     *
     * @param bidListDTO Un nouvel objet {@link BidListDTO} pour lier les données du formulaire.
     * @param model Le modèle Spring MVC.
     * @return Le nom de la vue "bidList/add".
     */
    @GetMapping("/bidList/add")
    public String addBidForm(BidListDTO bidListDTO, Model model) {
        log.info("Requête pour afficher le formulaire d'ajout d'une offre (DTO)");
        model.addAttribute("bidList", bidListDTO); // Le nom dans le modèle est "bidList"
        return "bidList/add";
    }

    /**
     * Valide et sauvegarde une nouvelle offre à partir d'un DTO.
     *
     * @param bidListDTO Le {@link BidListDTO} peuplé à partir du formulaire.
     * @param result Le {@link BindingResult} pour les erreurs de validation.
     * @param model Le modèle Spring MVC.
     * @param redirectAttributes Attributs pour la redirection.
     * @return "bidList/add" en cas d'erreur, sinon redirection vers "/bidList/list".
     */
    @PostMapping("/bidList/validate")
    public String validate(@Valid BidListDTO bidListDTO, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        log.info("Requête pour valider et sauvegarder un nouveau DTO d'offre : {}", bidListDTO);
        if (result.hasErrors()) {
            log.warn("Erreurs de validation pour le nouveau DTO d'offre : {}", result.getAllErrors());
            model.addAttribute("bidList", bidListDTO); // Renvoyer le DTO pour afficher les erreurs
            return "bidList/add";
        }
        try {
            bidListService.save(bidListDTO);
            log.info("DTO d'offre sauvegardé avec succès : {}", bidListDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Offre ajoutée avec succès !");
            return "redirect:/bidList/list";
        } catch (Exception e) {
            log.error("Erreur lors de la sauvegarde du DTO d'offre : {}", e.getMessage(), e);
            model.addAttribute("bidList", bidListDTO);
            model.addAttribute("errorMessage", "Erreur lors de la sauvegarde : " + e.getMessage());
            return "bidList/add";
        }
    }

    /**
     * Affiche le formulaire de mise à jour pour une offre existante (DTO).
     *
     * @param id L'ID de l'offre à mettre à jour.
     * @param model Le modèle Spring MVC.
     * @param redirectAttributes Attributs pour la redirection.
     * @return "bidList/update" si trouvé, sinon redirection vers "/bidList/list".
     */
    @GetMapping("/bidList/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        log.info("Requête pour afficher le formulaire de mise à jour pour le DTO d'offre id : {}", id);
        try {
            BidListDTO bidListDTO = bidListService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Offre (DTO) invalide avec l'Id :" + id));
            model.addAttribute("bidList", bidListDTO); // Le nom dans le modèle est "bidList"
            return "bidList/update";
        } catch (IllegalArgumentException e) {
            log.warn("DTO d'offre non trouvé pour la mise à jour avec l'id {} : {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/bidList/list";
        }
    }

    /**
     * Valide et met à jour une offre existante à partir d'un DTO.
     *
     * @param id L'ID de l'offre à mettre à jour.
     * @param bidListDTO Le {@link BidListDTO} peuplé à partir du formulaire.
     * @param result Le {@link BindingResult} pour les erreurs de validation.
     * @param model Le modèle Spring MVC.
     * @param redirectAttributes Attributs pour la redirection.
     * @return "bidList/update" en cas d'erreur, sinon redirection vers "/bidList/list".
     */
    @PostMapping("/bidList/update/{id}")
    public String updateBid(@PathVariable("id") Integer id, @Valid BidListDTO bidListDTO,
                            BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        log.info("Requête pour mettre à jour le DTO d'offre id {} : {}", id, bidListDTO);
        bidListDTO.setBidListId(id); // S'assurer que l'ID du DTO est celui du path variable

        if (result.hasErrors()) {
            log.warn("Erreurs de validation lors de la mise à jour du DTO d'offre id {} : {}", id, result.getAllErrors());
            model.addAttribute("bidList", bidListDTO); // Renvoyer DTO avec erreurs
            return "bidList/update";
        }
        try {
            bidListService.save(bidListDTO);
            log.info("DTO d'offre mis à jour avec succès : {}", bidListDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Offre mise à jour avec succès !");
            return "redirect:/bidList/list";
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du DTO d'offre id {} : {}", id, e.getMessage(), e);
            model.addAttribute("bidList", bidListDTO);
            model.addAttribute("errorMessage", "Erreur lors de la mise à jour : " + e.getMessage());
            return "bidList/update";
        }
    }

    /**
     * Supprime une offre par son ID.
     *
     * @param id L'ID de l'offre à supprimer.
     * @param redirectAttributes Attributs pour la redirection.
     * @return Redirection vers "/bidList/list".
     */
    @GetMapping("/bidList/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        log.info("Requête pour supprimer l'offre id : {}", id);
        try {
            bidListService.deleteById(id);
            log.info("Offre supprimée avec succès, id : {}", id);
            redirectAttributes.addFlashAttribute("successMessage", "Offre supprimée avec succès !");
        } catch (IllegalArgumentException e) {
            log.warn("Erreur lors de la suppression de l'offre id {} : {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/bidList/list";
    }
}