package com.nnk.poseidon.controllers;


import com.nnk.poseidon.domain.Rating;
import com.nnk.poseidon.domain.Trade;
import com.nnk.poseidon.services.RatingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String addRatingForm(Rating rating) {
        return "rating/add";
    }

    @PostMapping("/rating/validate")
    public String validate(@Valid Rating rating, BindingResult result, Model model) {
        // TODO: check data valid and save to db, after saving return Rating list
        return "rating/add";
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
