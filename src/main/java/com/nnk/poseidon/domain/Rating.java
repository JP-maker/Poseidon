package com.nnk.poseidon.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entité JPA représentant la notation de crédit d'une contrepartie ou d'un instrument financier.
 * <p>
 * Cette classe stocke les notations fournies par les trois principales agences de notation
 * (Moodys, Standard Poors, Fitch). Elle est mappée à la table `Rating` et est
 * probablement conçue pour être associée à d'autres entités métier (comme un `Trade` ou
 * un `RuleName`) pour évaluer leur qualité de crédit.
 * </p>
 */
@Entity
@Table(name = "Rating")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    /**
     * L'identifiant unique (clé primaire) de l'entité Rating.
     * Sa valeur est auto-générée par la base de données lors de l'insertion.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    /**
     * La note de crédit attribuée par l'agence Moody's.
     * Les valeurs sont typiquement des chaînes de caractères alphanumériques (ex: "Aaa", "Baa1", "C").
     */
    @Column(name = "moodysRating", length = 125)
    private String moodysRating;

    /**
     * La note de crédit attribuée par l'agence Standard & Poor's.
     * Les valeurs sont typiquement des chaînes de caractères alphanumériques (ex: "AAA", "BB+", "D").
     */
    @Column(name = "sandPRating", length = 125)
    private String sandPRating;

    /**
     * La note de crédit attribuée par l'agence Fitch Ratings.
     * Les valeurs sont typiquement des chaînes de caractères alphanumériques (ex: "AAA", "BB-", "C").
     */
    @Column(name = "fitchRating", length = 125)
    private String fitchRating;

    /**
     * Un indice numérique représentant la qualité de crédit pour faciliter le tri et la comparaison.
     * <p>
     * Les notations textuelles (ex: "AAA" vs "AA+") ne sont pas directement comparables par
     * ordre alphabétique. Ce champ fournit une représentation ordinale où une valeur plus
     * faible indique généralement une meilleure qualité de crédit (ex: 1 pour "AAA", 2 pour "AA+", etc.).
     * Le type {@code tinyint} de la base de données est mappé à un {@link Integer} en Java.
     * </p>
     */
    @Column(name = "orderNumber")
    private Integer orderNumber;
}