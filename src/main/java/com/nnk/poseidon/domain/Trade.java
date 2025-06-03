package com.nnk.poseidon.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * Représente une transaction de trade.
 * Cette entité stocke les détails d'une opération de marché.
 */
@Entity
@Table(name = "Trade")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trade {

    /**
     * Identifiant unique du trade, généré automatiquement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TradeId")
    private Integer tradeId;

    /**
     * Compte associé au trade. Ne peut pas être nul et a une longueur maximale de 30 caractères.
     */
    @Column(name = "account", nullable = false, length = 30)
    private String account;

    /**
     * Type de trade. Ne peut pas être nul et a une longueur maximale de 30 caractères.
     */
    @Column(name = "type", nullable = false, length = 30)
    private String type;

    /**
     * Quantité achetée dans le cadre du trade.
     */
    @Column(name = "buyQuantity")
    private Double buyQuantity;

    /**
     * Quantité vendue dans le cadre du trade.
     */
    @Column(name = "sellQuantity")
    private Double sellQuantity;

    /**
     * Prix d'achat du trade.
     */
    @Column(name = "buyPrice")
    private Double buyPrice;

    /**
     * Prix de vente du trade.
     */
    @Column(name = "sellPrice")
    private Double sellPrice;

    /**
     * Date et heure à laquelle le trade a été effectué.
     */
    @Column(name = "tradeDate")
    private LocalDateTime tradeDate;

    /**
     * Titre financier (security) concerné par le trade. Longueur maximale de 125 caractères.
     */
    @Column(name = "security", length = 125)
    private String security;

    /**
     * Statut actuel du trade. Longueur maximale de 10 caractères.
     */
    @Column(name = "status", length = 10)
    private String status;

    /**
     * Trader ayant effectué l'opération. Longueur maximale de 125 caractères.
     */
    @Column(name = "trader", length = 125)
    private String trader;

    /**
     * Indice de référence (benchmark) associé au trade. Longueur maximale de 125 caractères.
     */
    @Column(name = "benchmark", length = 125)
    private String benchmark;

    /**
     * Livre (book) dans lequel le trade est enregistré. Longueur maximale de 125 caractères.
     */
    @Column(name = "book", length = 125)
    private String book;

    /**
     * Nom de l'utilisateur ayant créé le trade. Longueur maximale de 125 caractères.
     */
    @Column(name = "creationName", length = 125)
    private String creationName;

    /**
     * Date et heure de création du trade.
     */
    @Column(name = "creationDate")
    private LocalDateTime creationDate;

    /**
     * Nom de l'utilisateur ayant effectué la dernière révision du trade. Longueur maximale de 125 caractères.
     */
    @Column(name = "revisionName", length = 125)
    private String revisionName;

    /**
     * Date et heure de la dernière révision du trade.
     */
    @Column(name = "revisionDate")
    private LocalDateTime revisionDate;

    /**
     * Nom du deal associé au trade. Longueur maximale de 125 caractères.
     */
    @Column(name = "dealName", length = 125)
    private String dealName;

    /**
     * Type de deal associé au trade. Longueur maximale de 125 caractères.
     */
    @Column(name = "dealType", length = 125)
    private String dealType;

    /**
     * Identifiant de la liste source du trade. Longueur maximale de 125 caractères.
     */
    @Column(name = "sourceListId", length = 125)
    private String sourceListId;

    /**
     * Côté (side) du trade (par exemple, Achat/Vente). Longueur maximale de 125 caractères.
     */
    @Column(name = "side", length = 125)
    private String side;
}