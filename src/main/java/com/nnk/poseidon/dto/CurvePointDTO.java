package com.nnk.poseidon.dto; // Création d'un sous-package dto

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) pour l'entité CurvePoint.
 * Utilisé pour transférer des données entre les couches, notamment pour les formulaires et la validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurvePointDTO {

    private Integer id; // Peut être null pour la création

    @NotNull(message = "L'ID de la courbe ne peut pas être nul.")
    private Integer curveId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) // Aide Spring à parser la date/heure du formulaire
    private LocalDateTime asOfDate; // La validation de date peut être plus complexe (@Future, @Past, etc.)

    @NotNull(message = "Le terme ne peut pas être nul.")
    @PositiveOrZero(message = "Le terme doit être positif ou zéro.")
    private Double term;

    @NotNull(message = "La valeur ne peut pas être nulle.")
    private Double value;

    private LocalDateTime creationDate; // Pour l'affichage, pas pour la saisie

}