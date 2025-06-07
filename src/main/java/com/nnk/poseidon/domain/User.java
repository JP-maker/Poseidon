package com.nnk.poseidon.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entité JPA représentant un utilisateur de l'application.
 * <p>
 * Cette classe est au cœur du système de sécurité, gérant les informations
 * d'identification et les permissions des utilisateurs. Elle est conçue pour
 * fonctionner avec des frameworks de sécurité comme Spring Security pour
 'authentification et l'autorisation.
 * Elle est mappée à la table `Users`.
 * </p>
 */
@Data
@Entity
@Table(name = "Users")
@NoArgsConstructor // Lombok: génère un constructeur sans arguments (requis par JPA)
@AllArgsConstructor // Lombok: génère un constructeur avec tous les arguments (utile pour les tests)
public class User {

    /**
     * L'identifiant unique (clé primaire) de l'utilisateur.
     * Sa valeur est auto-générée par la base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    /**
     * Le nom d'utilisateur unique utilisé pour la connexion.
     * Ce champ est obligatoire et doit être unique dans tout le système pour
     * garantir une identification sans ambiguïté.
     */
    @NotBlank(message = "Username is mandatory")
    @Column(name = "username", length = 125, unique = true) // 'unique = true' est une contrainte de BDD fortement recommandée
    private String username;

    /**
     * Le mot de passe de l'utilisateur.
     * <p>
     * <b>Important :</b> Ce champ ne doit <b>jamais</b> contenir le mot de passe en clair.
     * Il est destiné à stocker la version hachée (encodée) du mot de passe, généralement
     * générée via un {@code PasswordEncoder} de Spring Security.
     * </p>
     */
    @NotBlank(message = "Password is mandatory")
    @Column(name = "password", length = 125)
    private String password;

    /**
     * Le nom complet de l'utilisateur (prénom et nom).
     * Ce champ est utilisé à des fins d'affichage dans l'interface utilisateur.
     */
    @NotBlank(message = "FullName is mandatory")
    @Column(name = "fullname", length = 125)
    private String fullname;

    /**
     * Le rôle attribué à l'utilisateur, qui détermine ses droits d'accès.
     * <p>
     * Par convention avec Spring Security, cette valeur est souvent préfixée
     * par 'ROLE_' (ex: "ROLE_USER", "ROLE_ADMIN").
     * </p>
     */
    @NotBlank(message = "Role is mandatory")
    @Column(name = "role", length = 125)
    private String role;
}