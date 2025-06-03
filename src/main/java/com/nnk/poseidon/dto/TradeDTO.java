package com.nnk.poseidon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) pour les opérations sur les Trades.
 * Utilisé pour transférer les données de trade entre les couches de l'application,
 * en particulier pour la validation des entrées utilisateur.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeDTO {

    /**
     * Identifiant unique du trade. Utilisé pour les mises à jour.
     */
    private Integer tradeId;

    /**
     * Compte associé au trade. Obligatoire et longueur max 30.
     */
    @NotBlank(message = "Account is mandatory")
    @Size(max = 30, message = "Account must be less than 30 characters")
    private String account;

    /**
     * Type de trade. Obligatoire et longueur max 30.
     */
    @NotBlank(message = "Type is mandatory")
    @Size(max = 30, message = "Type must be less than 30 characters")
    private String type;

    /**
     * Quantité achetée. Obligatoire et doit être un nombre positif ou nul.
     * Bien que l'entité permette null, le formulaire d'ajout le rend implicitement requis.
     * Si une quantité d'achat peut être nulle, considérez d'enlever @NotNull.
     */
    @NotNull(message = "Buy quantity is mandatory")
    @PositiveOrZero(message = "Buy quantity must be a positive number or zero")
    private Double buyQuantity;

    // Les autres champs de l'entité Trade ne sont pas inclus ici
    // car ils ne sont pas gérés par les formulaires fournis (add.html, update.html).
    // Si d'autres champs devenaient modifiables, il faudrait les ajouter ici
    // avec leurs validations appropriées.

    // Champs de l'entité qui pourraient être utiles à afficher mais non modifiables via ces formulaires
    private Double sellQuantity;
    private Double buyPrice;
    private Double sellPrice;
    private LocalDateTime tradeDate;
    private String security;
    private String status;
    private String trader;
    private String benchmark;
    private String book;
    private String creationName;
    private LocalDateTime creationDate;
    private String revisionName;
    private LocalDateTime revisionDate;
    private String dealName;
    private String dealType;
    private String sourceListId;
    private String side;
}