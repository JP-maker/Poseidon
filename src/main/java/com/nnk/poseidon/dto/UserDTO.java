package com.nnk.poseidon.dto; // Créez ce package si nécessaire

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO (Data Transfer Object) pour les opérations sur les utilisateurs.
 * Utilisé pour transférer les données utilisateur entre les couches de l'application,
 * en particulier pour la validation des entrées et pour éviter d'exposer directement l'entité.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    /**
     * Identifiant unique de l'utilisateur. Utilisé pour les mises à jour.
     */
    private Integer id;

    /**
     * Nom d'utilisateur. Obligatoire, longueur max 125.
     */
    @NotBlank(message = "Username is mandatory")
    @Size(max = 125, message = "Username must be less than 125 characters")
    private String username;

    /**
     * Mot de passe de l'utilisateur (en clair lors de la saisie).
     * Obligatoire pour la création. Peut être optionnel pour la mise à jour
     * si on ne souhaite pas le changer.
     * La validation de la complexité du mot de passe peut être ajoutée ici.
     */
    @NotBlank(message = "Password is mandatory") // Conserver pour l'ajout. Pour la mise à jour, la logique pourrait être différente.
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{8,}$",
            message = "Password must be at least 8 characters long and contain at least one number and one special character"
    )
    private String password;

    /**
     * Nom complet de l'utilisateur. Obligatoire, longueur max 125.
     */
    @NotBlank(message = "FullName is mandatory")
    @Size(max = 125, message = "FullName must be less than 125 characters")
    private String fullname;

    /**
     * Rôle de l'utilisateur (ex: "USER", "ADMIN"). Obligatoire.
     */
    @NotBlank(message = "Role is mandatory")
    @Size(max = 125, message = "Role must be less than 125 characters") // Bien que souvent plus court (USER, ADMIN)
    private String role;
}