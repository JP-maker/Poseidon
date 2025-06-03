package com.nnk.poseidon.dto; // Assure-toi que ce package existe ou crée-le

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) pour l'entité BidList.
 * Utilisé pour transférer des données entre les couches, notamment pour les formulaires et la validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidListDTO {

    private Integer bidListId; // Identifiant, peut être null pour la création

    @NotBlank(message = "Le compte ne peut pas être vide.")
    @Size(max = 30, message = "Le compte ne doit pas dépasser 30 caractères.")
    private String account;

    @NotBlank(message = "Le type ne peut pas être vide.")
    @Size(max = 30, message = "Le type ne doit pas dépasser 30 caractères.")
    private String type;

    @NotNull(message = "La quantité de l'offre (bid) ne peut pas être nulle.")
    @PositiveOrZero(message = "La quantité de l'offre (bid) doit être positive ou nulle.")
    private Double bidQuantity;

    // Ces champs ne sont pas dans les formulaires fournis, mais sont dans l'entité.
    // Pour l'instant, je les omets pour correspondre aux formulaires.
    // private Double askQuantity;
    // private Double bid;
    // private Double ask;
    // private String benchmark;
    // @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    // private LocalDateTime bidListDate;
    // private String commentary;
    // etc.

    // Ils peuvent être inclus pour l'affichage si nécessaire.
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime creationDate; // Pour l'affichage, pas pour la saisie

}