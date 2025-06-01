package com.nnk.poseidon.controllers;


import com.nnk.poseidon.domain.Rating;
import com.nnk.poseidon.domain.Trade;
import com.nnk.poseidon.dto.RatingDto;
import com.nnk.poseidon.services.RatingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
public class RatingController {

    private final RatingService ratingService;
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @RequestMapping("/rating/list")
    public String home(Model model)
    {
        log.debug("Accès à la liste des ratings");

        List<Rating> ratings = Collections.emptyList();

        try {
            ratings = ratingService.getAllRatings();
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des ratings : {}", e.getMessage());
            model.addAttribute("error", "Erreur lors de la récupération des ratings");
        }

        log.debug("Nombre de ratings récupérés : {}", ratings.size());
        model.addAttribute("ratings", ratings);
        log.debug("Modèle mis à jour avec les ratings");
        return "rating/list";
    }

    @GetMapping("/rating/add")
    public String addRatingForm(Model model) {
        log.debug("Affichage du formulaire d'ajout de rating");
        model.addAttribute("ratingDto", new RatingDto());
        log.debug("Modèle mis à jour avec un nouveau RatingDto");
        return "rating/add";
    }

    @PostMapping("/rating/validate")
    public String validate(@Valid @ModelAttribute("ratingDto") RatingDto ratingDto,
                           BindingResult result,
                           Model model) {
        log.debug("Validation du RatingDto : {}", ratingDto);
        if (result.hasErrors()) {
            log.error("Erreur de validation : {}", result.getAllErrors());
            model.addAttribute("error", "Erreur de validation des données");
            return "rating/add";
        }
        try {
            Rating rating = new Rating();
            rating.setMoodysRating(ratingDto.getMoodysRating());
            rating.setSandPRating(ratingDto.getSandPRating());
            rating.setFitchRating(ratingDto.getFitchRating());
            rating.setOrderNumber(ratingDto.getOrderNumber());

            ratingService.saveRating(rating);
            log.debug("Rating ajouté avec succès : {}", rating);
        } catch (Exception e) {
            log.error("Erreur lors de l'ajout du rating : {}", e.getMessage());
            model.addAttribute("error", "Erreur lors de l'ajout du rating");
            return "rating/add";
        }
        return "redirect:/rating/list";
    }

    @GetMapping("/rating/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        log.debug("Affichage du formulaire de mise à jour pour le rating avec ID : {}", id);
        Rating rating = ratingService.getRatingById(id);
        RatingDto ratingDto = new RatingDto();
        if (rating != null) {
            ratingDto.setId(rating.getId());
            ratingDto.setMoodysRating(rating.getMoodysRating());
            ratingDto.setSandPRating(rating.getSandPRating());
            ratingDto.setFitchRating(rating.getFitchRating());
            ratingDto.setOrderNumber(rating.getOrderNumber());
        } else {
            log.error("Rating avec ID {} non trouvé", id);
            model.addAttribute("error", "Rating non trouvé");
            return "redirect:/rating/list";
        }
        log.debug("Rating trouvé : {}", rating);
        model.addAttribute("ratingDto", ratingDto);
        log.debug("Modèle mis à jour avec le rating pour la mise à jour");
        return "rating/update";
    }

    @PostMapping("/rating/update/{id}")
    public String updateRating(@PathVariable("id") Integer id, @Valid RatingDto ratingDto,
                             BindingResult result, Model model) {
        log.debug("Mise à jour du rating avec ID : {}", id);
        if (result.hasErrors()) {
            log.error("Erreur de validation lors de la mise à jour du rating : {}", result.getAllErrors());
            model.addAttribute("error", "Erreur de validation des données");
            model.addAttribute("ratingDto", ratingDto);
            return "rating/update";
        }
        try {
            Rating rating = ratingService.getRatingById(ratingDto.getId());
            if (rating == null) {
                log.error("Rating avec ID {} non trouvé pour mise à jour", ratingDto.getId());
                model.addAttribute("error", "Rating non trouvé");
                return "redirect:/rating/list";
            }
            rating.setMoodysRating(ratingDto.getMoodysRating());
            rating.setSandPRating(ratingDto.getSandPRating());
            rating.setFitchRating(ratingDto.getFitchRating());
            rating.setOrderNumber(ratingDto.getOrderNumber());
            ratingService.saveRating(rating);
            log.debug("Rating mis à jour avec succès : {}", rating);
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du rating : {}", e.getMessage());
            model.addAttribute("error", "Erreur lors de la mise à jour du rating");
            return "rating/update";
        }
        return "redirect:/rating/list";
    }

    @GetMapping("/rating/delete/{id}")
    public String deleteRating(@PathVariable("id") Integer id, Model model) {
        log.debug("Suppression du rating avec ID : {}", id);
        try {
            Rating rating = ratingService.getRatingById(id);
            if (rating == null) {
                log.error("Rating avec ID {} non trouvé pour suppression", id);
                model.addAttribute("error", "Rating non trouvé");
                return "redirect:/rating/list";
            }
            ratingService.deleteRating(rating.getId()); // Suppression logique, si nécessaire
            log.debug("Rating supprimé avec succès : {}", rating);
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du rating : {}", e.getMessage());
            model.addAttribute("error", "Erreur lors de la suppression du rating");
            return "redirect:/rating/list";
        }
        return "redirect:/rating/list";
    }
}
