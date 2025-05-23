package com.nnk.poseidon.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * Represents a trade transaction.
 */
@Entity
@Table(name = "Trade")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TradeId")
    private Integer tradeId;

    @Column(name = "account", nullable = false, length = 30)
    private String account;

    @Column(name = "type", nullable = false, length = 30)
    private String type;

    @Column(name = "buyQuantity")
    private Double buyQuantity;

    @Column(name = "sellQuantity")
    private Double sellQuantity;

    @Column(name = "buyPrice")
    private Double buyPrice;

    @Column(name = "sellPrice")
    private Double sellPrice;

    @Column(name = "tradeDate")
    private LocalDateTime tradeDate;

    @Column(name = "security", length = 125)
    private String security;

    @Column(name = "status", length = 10)
    private String status;

    @Column(name = "trader", length = 125)
    private String trader;

    @Column(name = "benchmark", length = 125)
    private String benchmark;

    @Column(name = "book", length = 125)
    private String book;

    @Column(name = "creationName", length = 125)
    private String creationName;

    @Column(name = "creationDate")
    private LocalDateTime creationDate;

    @Column(name = "revisionName", length = 125)
    private String revisionName;

    @Column(name = "revisionDate")
    private LocalDateTime revisionDate;

    @Column(name = "dealName", length = 125)
    private String dealName;

    @Column(name = "dealType", length = 125)
    private String dealType;

    @Column(name = "sourceListId", length = 125)
    private String sourceListId;

    @Column(name = "side", length = 125)
    private String side;
}
