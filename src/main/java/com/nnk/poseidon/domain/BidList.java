package com.nnk.poseidon.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entité JPA représentant une offre (bid) dans le système de trading.
 * <p>
 * Cette classe est mappée à la table `BidList` dans la base de données.
 * Elle contient toutes les informations relatives à une offre sur un instrument
 * financier, y compris les quantités, les prix, et des données d'audit.
 * Les annotations Lombok ({@link Data}, {@link NoArgsConstructor}, {@link AllArgsConstructor})
 * sont utilisées pour générer automatiquement le code standard (getters, setters, constructeurs, etc.).
 * </p>
 */
@Entity
@Table(name = "BidList")
@Data // Génère les getters, setters, toString, equals, et hashCode.
@NoArgsConstructor // Génère un constructeur sans arguments, requis par JPA.
@AllArgsConstructor // Génère un constructeur avec tous les arguments, utile pour les tests.
public class BidList {

    /**
     * L'identifiant unique (clé primaire) pour l'entité BidList.
     * La valeur est auto-générée par la base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BidListId")
    private Integer bidListId;

    /**
     * Le nom du compte ou l'identifiant du client associé à cette offre.
     * Ce champ est obligatoire.
     */
    @Column(name = "account", nullable = false, length = 30)
    private String account;

    /**
     * Le type d'offre ou de produit.
     * Ce champ est obligatoire.
     */
    @Column(name = "type", nullable = false, length = 30)
    private String type;

    /**
     * La quantité d'instruments financiers proposée à l'achat (bid).
     */
    @Column(name = "bidQuantity")
    private Double bidQuantity;

    /**
     * La quantité d'instruments financiers demandée à la vente (ask).
     */
    @Column(name = "askQuantity")
    private Double askQuantity;

    /**
     * Le prix proposé à l'achat (bid price).
     */
    @Column(name = "bid")
    private Double bid;

    /**
     * Le prix demandé à la vente (ask price).
     */
    @Column(name = "ask")
    private Double ask;

    /**
     * Un indice ou une référence de marché (benchmark) pour cette offre.
     */
    @Column(name = "benchmark", length = 125)
    private String benchmark;

    /**
     * La date et l'heure spécifiques à cette liste d'offres.
     */
    @Column(name = "bidListDate")
    private LocalDateTime bidListDate;

    /**
     * Un champ de commentaire libre pour des informations supplémentaires.
     */
    @Column(name = "commentary", length = 125)
    private String commentary;

    /**
     * L'identifiant de l'instrument financier (ex: ticker, ISIN) concerné par l'offre.
     */
    @Column(name = "security", length = 125)
    private String security;

    /**
     * Le statut actuel de l'offre (ex: "Open", "Closed", "Cancelled").
     */
    @Column(name = "status", length = 10)
    private String status;

    /**
     * Le nom ou l'identifiant du trader qui a soumis l'offre.
     */
    @Column(name = "trader", length = 125)
    private String trader;

    /**
     * Le livre de trading ("trading book") auquel cette offre est rattachée.
     */
    @Column(name = "book", length = 125)
    private String book;

    /**
     * Le nom de l'utilisateur ou du système qui a créé l'enregistrement (champ d'audit).
     */
    @Column(name = "creationName", length = 125)
    private String creationName;

    /**
     * La date et l'heure de création de l'enregistrement (champ d'audit).
     */
    @Column(name = "creationDate")
    private LocalDateTime creationDate;

    /**
     * Le nom de l'utilisateur ou du système qui a effectué la dernière révision (champ d'audit).
     */
    @Column(name = "revisionName", length = 125)
    private String revisionName;

    /**
     * La date et l'heure de la dernière révision de l'enregistrement (champ d'audit).
     */
    @Column(name = "revisionDate")
    private LocalDateTime revisionDate;

    /**
     * Le nom de la transaction ou du "deal" plus large auquel cette offre peut appartenir.
     */
    @Column(name = "dealName", length = 125)
    private String dealName;

    /**
     * Le type de la transaction (deal type).
     */
    @Column(name = "dealType", length = 125)
    private String dealType;

    /**
     * Un identifiant liant cette offre à une liste ou un système source externe.
     */
    @Column(name = "sourceListId", length = 125)
    private String sourceListId;

    /**
     * Le côté de l'ordre ("side"), par exemple "Buy" ou "Sell".
     */
    @Column(name = "side", length = 125)
    private String side;
}