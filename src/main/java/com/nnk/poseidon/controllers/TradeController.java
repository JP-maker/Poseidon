package com.nnk.poseidon.controllers;

import com.nnk.poseidon.domain.Trade;
import com.nnk.poseidon.services.TradeService;
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

/**
 * Contrôleur gérant les trade.
 */
@Slf4j
@Controller
public class TradeController {

    private final TradeService tradeService;
    /**
     * Constructeur du TradeController.
     *
     * @param tradeService Le service pour les opérations liées aux trades.
     */
    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
        log.debug("TradeController initialisé avec TradeService");
    }

    /**
     * Gère les requêtes GET vers "/trade/list" pour afficher la liste des trades.
     * Cette méthode doit récupérer tous les trades depuis la base de données
     * et les ajouter au modèle pour être affichés dans la vue.
     *
     * @param model L'objet Model de Spring pour passer des données à la vue.
     * @return Le nom de la vue (template Thymeleaf) pour la liste des trades ("trade/list").
     */
    @RequestMapping("/trade/list")
    public String home(Model model)
    {
        log.debug("Accès à la liste des trades");

        List<Trade> trades = Collections.emptyList();

        try {
            trades = tradeService.getAllTrades();
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des trades : {}", e.getMessage());
            model.addAttribute("error", "Erreur lors de la récupération des trades");
        }

        log.debug("Nombre de trades récupérés : {}", trades.size());
        model.addAttribute("trades", trades);
        log.debug("Modèle mis à jour avec les trades");

        return "trade/list";
    }

    @GetMapping("/trade/add")
    public String addUser(Trade bid) {
        return "trade/add";
    }

    @PostMapping("/trade/validate")
    public String validate(@Valid Trade trade, BindingResult result, Model model) {
        // TODO: check data valid and save to db, after saving return Trade list
        return "trade/add";
    }

    @GetMapping("/trade/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        // TODO: get Trade by Id and to model then show to the form
        return "trade/update";
    }

    @PostMapping("/trade/update/{id}")
    public String updateTrade(@PathVariable("id") Integer id, @Valid Trade trade,
                             BindingResult result, Model model) {
        // TODO: check required fields, if valid call service to update Trade and return Trade list
        return "redirect:/trade/list";
    }

    @GetMapping("/trade/delete/{id}")
    public String deleteTrade(@PathVariable("id") Integer id, Model model) {
        // TODO: Find Trade by Id and delete the Trade, return to Trade list
        return "redirect:/trade/list";
    }
}
