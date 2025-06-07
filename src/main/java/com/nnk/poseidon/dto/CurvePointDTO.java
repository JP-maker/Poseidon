package com.nnk.poseidon.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import com.nnk.poseidon.domain.CurvePoint; // Import pour la référence Javadoc

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) pour l'entité {@link CurvePoint}.
 * <p>
 * Cet objet sert de contrat de données pour les formulaires de création et de mise à jour
 * des points de courbe. Il encapsule les données saisies par l'utilisateur et applique
 * des règles de validation spécifiques via les annotations de Jakarta Bean Validation avant
 * que les données ne soient traitées par la couche service.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurvePointDTO {

    /**
     * L'identifiant unique du point de courbe.
     * Utilisé pour la mise à jour d'un point existant. Il est nul lors de la création.
     */
    private Integer id;

    /**
     * L'identifiant de la courbe parente à laquelle ce point est rattaché.
     * Ce champ est obligatoire et sa valeur doit être un entier positif.
     */
    @NotNull(message = "Curve ID cannot be null.")
    @Min(value = 1, message = "Curve ID must be a positive number.")
    private Integer curveId;

    /**
     * La date de validité des données du point ("as of date").
     * <p>
     * Indique à quel moment la {@code value} est considérée comme exacte pour le {@code term} donné.
     * L'annotation {@link DateTimeFormat} aide Spring MVC à convertir correctement la chaîne de caractères
     * provenant du formulaire en objet {@link LocalDateTime}.
     * </p>
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime asOfDate;

    /**
     * Le terme (ou la maturité) du point sur la courbe, généralement exprimé en années (ex: 0.5 pour 6 mois).
     * Représente l'axe des abscisses (X) de la courbe. Ce champ est obligatoire et doit être positif ou nul.
     */
    @NotNull(message = "Term cannot be null.")
    @PositiveOrZero(message = "Term must be zero or positive.")
    private Double term;

    /**
     * La valeur du point de courbe pour le terme correspondant (ex: un taux d'intérêt).
     * Représente l'axe des ordonnées (Y) de la courbe. Ce champ est obligatoire.
     */
    @NotNull(message = "Value cannot be null.")
    private Double value;

    /**
     * La date de création de l'enregistrement.
     * <p>
     * Ce champ est destiné à un usage en lecture seule (affichage) et n'est pas rempli par
     * l'utilisateur dans le formulaire. Sa valeur est gérée par la couche de service ou la base de données.
     * </p>
     */
    private LocalDateTime creationDate;

}