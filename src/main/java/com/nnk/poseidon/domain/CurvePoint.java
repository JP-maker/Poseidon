package com.nnk.poseidon.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entité JPA représentant un point de données sur une courbe financière (par exemple, une courbe de taux).
 * <p>
 * Chaque instance de cette classe représente un point spécifique sur une courbe, défini par un couple terme/valeur
 * à une date de validité donnée. Elle est mappée à la table `CurvePoint`.
 * Les annotations Lombok ({@link Data}, {@link NoArgsConstructor}, {@link AllArgsConstructor})
 * sont utilisées pour réduire le code standard.
 * </p>
 */
@Entity
@Table(name = "CurvePoint")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurvePoint {

    /**
     * L'identifiant unique (clé primaire) du point de courbe.
     * La valeur est générée automatiquement par la base de données lors de l'insertion.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    /**
     * L'identifiant de la courbe parente à laquelle ce point appartient.
     * <p>
     * Ce champ établit une liaison logique vers une entité `Curve`. Dans un modèle JPA
     * plus complet, il serait généralement remplacé par une relation {@code @ManyToOne}
     * pour assurer l'intégrité référentielle.
     * </p>
     */
    @Column(name = "CurveId")
    private Integer curveId;

    /**
     * La date de validité des données de ce point ("as of date").
     * <p>
     * Indique la date à laquelle la valeur (`value`) pour le terme (`term`) donné est considérée
     * comme exacte. Ne doit pas être confondu avec {@link #creationDate}, qui est une
     * métadonnée d'audit.
     * </p>
     */
    @Column(name = "asOfDate")
    private LocalDateTime asOfDate;

    /**
     * Le terme (ou maturité) du point sur la courbe, généralement exprimé en années.
     * <p>
     * Représente l'axe des abscisses (X) de la courbe. Par exemple : 0.5 pour 6 mois,
     * 2.0 pour 2 ans, 10.0 pour 10 ans.
     * </p>
     */
    @Column(name = "term")
    private Double term;

    /**
     * La valeur du point de courbe pour le terme donné.
     * <p>
     * Représente l'axe des ordonnées (Y) de la courbe. Selon le type de courbe, il peut
     * s'agir d'un taux d'intérêt, d'un prix, ou d'un niveau de volatilité.
     * </p>
     */
    @Column(name = "value")
    private Double value;

    /**
     * La date et l'heure de création de l'enregistrement en base de données (champ d'audit).
     * <p>
     * Ce champ est utilisé pour la traçabilité et indique quand l'entité a été
     * physiquement insérée dans la table.
     * </p>
     */
    @Column(name = "creationDate")
    private LocalDateTime creationDate;
}