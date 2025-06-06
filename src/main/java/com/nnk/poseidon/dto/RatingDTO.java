package com.nnk.poseidon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objet de Transfert de Données (DTO) pour représenter les données d'une notation (Rating).
 * Cette classe est utilisée pour transférer les informations de notation entre les différentes couches de l'application,
 * typiquement pour les charges utiles (payloads) des requêtes et réponses d'API, ainsi que pour les soumissions de formulaires.
 * Elle inclut des annotations de validation pour assurer l'intégrité des données.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingDTO {

    /**
     * L'identifiant unique de la notation.
     * Ce champ est généralement optionnel pour les opérations de création (car il est auto-généré)
     * mais requis pour les opérations de mise à jour ou de récupération.
     * Il sera renseigné par le service lors de la récupération ou après la création.
     */
    private Integer id;

    /**
     * La note de crédit attribuée par Moody's.
     * Ce champ est obligatoire et ne doit pas être vide (blank).
     * Sa longueur est limitée à 125 caractères.
     */
    @NotBlank(message = "Moody's rating cannot be empty")
    @Size(max = 125, message = "Moody's rating must be at most 125 characters")
    private String moodysRating;

    /**
     * La note de crédit attribuée par Standard & Poor's (S&P).
     * Ce champ est obligatoire et ne doit pas être vide (blank).
     * Sa longueur est limitée à 125 caractères.
     */
    @NotBlank(message = "S&P rating cannot be empty")
    @Size(max = 125, message = "S&P rating must be at most 125 characters")
    private String sandPRating;

    /**
     * La note de crédit attribuée par Fitch Ratings.
     * Ce champ est obligatoire et ne doit pas être vide (blank).
     * Sa longueur est limitée à 125 caractères.
     */
    @NotBlank(message = "Fitch rating cannot be empty")
    @Size(max = 125, message = "Fitch rating must be at most 125 characters")
    private String fitchRating;

    /**
     * Un numéro d'ordre associé à la notation, potentiellement utilisé pour le tri ou le séquençage.
     * Ce champ est obligatoire et ne doit pas être nul.
     * Dans la base de données, cela pourrait être représenté par un {@code tinyint}.
     */
    @NotNull(message = "Order number cannot be null")
    private Integer orderNumber;

}