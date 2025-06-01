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
        // TODO: get Rating by Id and to model then show to the form
        return "rating/update";
    }

    @PostMapping("/rating/update/{id}")
    public String updateRating(@PathVariable("id") Integer id, @Valid Rating rating,
                             BindingResult result, Model model) {
        // TODO: check required fields, if valid call service to update Rating and return Rating list
        return "redirect:/rating/list";
    }

    @GetMapping("/rating/delete/{id}")
    public String deleteRating(@PathVariable("id") Integer id, Model model) {
        // TODO: Find Rating by Id and delete the Rating, return to Rating list
        return "redirect:/rating/list";
    }
}
