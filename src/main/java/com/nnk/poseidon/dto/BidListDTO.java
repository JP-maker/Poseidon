package com.nnk.poseidon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import com.nnk.poseidon.domain.BidList; // Import pour la référence dans Javadoc

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) pour l'entité {@link BidList}.
 * <p>
 * Cet objet est utilisé pour transporter les données entre la couche de présentation (vues, formulaires)
 * et la couche de service. Il est spécifiquement conçu pour les opérations de création et de mise à jour,
 * en n'exposant que les champs modifiables par l'utilisateur et en y appliquant des règles de validation
 * via les annotations de Jakarta Bean Validation.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BidListDTO {

    /**
     * L'identifiant unique de l'entité BidList.
     * Ce champ est utilisé pour les opérations de mise à jour afin d'identifier
     * l'enregistrement à modifier. Il est nul lors de la création d'une nouvelle offre.
     */
    private Integer bidListId;

    /**
     * Le nom du compte associé à l'offre.
     * Ce champ est obligatoire et ne peut pas dépasser 30 caractères.
     */
    @NotBlank(message = "Account is mandatory.")
    @Size(max = 30, message = "Account must not exceed 30 characters.")
    private String account;

    /**
     * Le type d'offre ou de produit.
     * Ce champ est obligatoire et ne peut pas dépasser 30 caractères.
     */
    @NotBlank(message = "Type is mandatory.")
    @Size(max = 30, message = "Type must not exceed 30 characters.")
    private String type;

    /**
     * La quantité d'instruments financiers proposée à l'achat.
     * Ce champ est obligatoire et sa valeur doit être un nombre positif ou nul.
     */
    @NotNull(message = "Bid quantity is mandatory.")
    @PositiveOrZero(message = "Bid quantity must be zero or positive.")
    private Double bidQuantity;

    /**
     * La date de création de l'enregistrement.
     * <p>
     * Ce champ est principalement utilisé pour l'affichage (en lecture seule) dans les listes
     * ou les formulaires de mise à jour. Il n'est généralement pas modifiable par l'utilisateur.
     * L'annotation {@link DateTimeFormat} assure un formatage correct de la date.
     * </p>
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime creationDate;

    /*
     * Note sur les champs omis :
     * Les autres champs de l'entité `BidList` (comme `askQuantity`, `bid`, `ask`, `benchmark`, etc.)
     * ne sont pas inclus dans ce DTO car ils ne sont pas destinés à être saisis ou modifiés
     * via les formulaires de création/mise à jour standard. Cette omission est intentionnelle
     * et constitue une bonne pratique pour n'exposer que les données strictement nécessaires.
     */
}