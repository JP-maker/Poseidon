package com.nnk.poseidon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.nnk.poseidon.domain.RuleName; // Import pour la référence Javadoc

/**
 * DTO (Data Transfer Object) pour l'entité {@link RuleName}.
 * <p>
 * Cet objet sert de contrat de données pour les formulaires de création et de mise à jour
 * des définitions de règles. Il expose uniquement les champs modifiables par l'utilisateur
 * et applique des règles de validation pour garantir la cohérence des données soumises.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleNameDTO {

    /**
     * L'identifiant unique de la règle.
     * Utilisé pour les opérations de mise à jour. Doit être nul lors de la création d'une nouvelle règle.
     */
    private Integer id;

    /**
     * Le nom unique de la règle, servant d'identifiant métier.
     * Ce champ est obligatoire et sa longueur ne doit pas dépasser 125 caractères.
     */
    @NotBlank(message = "Le nom ne peut pas être vide.")
    @Size(max = 125, message = "Le nom ne doit pas dépasser 125 caractères.")
    private String name;

    /**
     * Une description textuelle du but et du fonctionnement de la règle.
     * Ce champ est optionnel et sa longueur est limitée à 125 caractères.
     */
    @Size(max = 125, message = "La description ne doit pas dépasser 125 caractères.")
    private String description;

    /**
     * La logique de la règle exprimée au format JSON.
     * <p>
     * Permet de définir des conditions ou des paramètres de manière structurée.
     * Ce champ est optionnel et limité à 125 caractères.
     * </p>
     */
    @Size(max = 125, message = "Le JSON ne doit pas dépasser 125 caractères.")
    private String json;

    /**
     * Un modèle de texte (template) pour générer des messages ou des descriptions.
     * Peut contenir des placeholders pour des valeurs dynamiques. Ce champ est optionnel
     * et limité à 512 caractères.
     */
    @Size(max = 512, message = "Le template ne doit pas dépasser 512 caractères.")
    private String template;

    /**
     * Une requête SQL complète qui implémente la logique de la règle.
     * <p>
     * Note: Ce champ correspond à `sqlStr` dans l'entité {@link RuleName}.
     * </p>
     */
    @Size(max = 125, message = "Le SQL ne doit pas dépasser 125 caractères.")
    private String sql;

    /**
     * Un fragment de requête SQL (ex: une clause WHERE) destiné à être réutilisé.
     * Contrairement à {@link #sql}, ce n'est pas une requête exécutable de manière autonome.
     * Ce champ est optionnel.
     */
    @Size(max = 125, message = "La partie SQL ne doit pas dépasser 125 caractères.")
    private String sqlPart;

}