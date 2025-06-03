package com.nnk.poseidon.dto; // Assure-toi que ce package existe ou crée-le

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) pour l'entité RuleName.
 * Utilisé pour transférer des données entre les couches, notamment pour les formulaires et la validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleNameDTO {

    private Integer id; // Identifiant, peut être null pour la création

    @NotBlank(message = "Le nom ne peut pas être vide.")
    @Size(max = 125, message = "Le nom ne doit pas dépasser 125 caractères.")
    private String name;

    @Size(max = 125, message = "La description ne doit pas dépasser 125 caractères.")
    private String description; // Peut être optionnel selon tes règles métier

    @Size(max = 125, message = "Le JSON ne doit pas dépasser 125 caractères.")
    private String json; // Peut être optionnel

    @Size(max = 512, message = "Le template ne doit pas dépasser 512 caractères.")
    private String template; // Peut être optionnel

    // J'utilise "sql" pour correspondre au formulaire. Si tu veux garder "sqlStr",
    @Size(max = 125, message = "Le SQL ne doit pas dépasser 125 caractères.")
    private String sql; // Anciennement sqlStr, adapté au template

    @Size(max = 125, message = "La partie SQL ne doit pas dépasser 125 caractères.")
    private String sqlPart; // Peut être optionnel

}