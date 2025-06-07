package com.nnk.poseidon.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entité JPA représentant la définition d'une règle métier ou de validation.
 * <p>
 * Cette classe sert de référentiel central pour les règles utilisées dans l'application.
 * Une règle peut être définie de plusieurs manières (JSON, SQL, etc.), ce qui permet
 * de modifier dynamiquement la logique métier sans changer le code de l'application.
 * Elle est mappée à la table `RuleName`.
 * </p>
 */
@Entity
@Table(name = "RuleName")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleName {

    /**
     * L'identifiant unique (clé primaire) de la définition de la règle.
     * Sa valeur est auto-générée par la base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    /**
     * Le nom unique et lisible par un humain pour cette règle.
     * Sert d'identifiant métier pour la règle.
     */
    @Column(name = "name", length = 125)
    private String name;

    /**
     * Une description textuelle expliquant le but et le fonctionnement de la règle.
     */
    @Column(name = "description", length = 125)
    private String description;

    /**
     * Une chaîne de caractères contenant la logique de la règle au format JSON.
     * <p>
     * Ceci permet de définir des conditions complexes, des paramètres ou des seuils de manière
     * structurée, qui peuvent être ensuite interprétés par le code de l'application.
     * (ex: {@code {"field": "trade.amount", "operator": ">", "value": 10000}})
     * </p>
     */
    @Column(name = "json", length = 125)
    private String json;

    /**
     * Un modèle (template) de texte utilisé par la règle, par exemple pour générer
     * un message d'alerte ou une description d'erreur.
     * <p>
     * Peut contenir des placeholders qui seront remplacés par des valeurs dynamiques lors de
     * l'exécution de la règle. (ex: "Alerte : Le trade ${tradeId} dépasse le seuil.")
     * </p>
     */
    @Column(name = "template", length = 512)
    private String template;

    /**
     * Une requête SQL complète qui implémente la logique de la règle.
     * <p>
     * Peut être exécutée directement sur la base de données pour identifier les
     * enregistrements qui violent ou satisfont cette règle.
     * </p>
     */
    @Column(name = "sqlStr", length = 125)
    private String sqlStr;

    /**
     * Un fragment de requête SQL (ex: une clause WHERE ou une condition de JOIN).
     * <p>
     * Contrairement à {@link #sqlStr}, ce fragment n'est pas une requête complète.
     * Il est destiné à être combiné avec d'autres parties pour construire dynamiquement
     * une requête plus complexe, favorisant ainsi la réutilisation de la logique SQL.
     * </p>
     */
    @Column(name = "sqlPart", length = 125)
    private String sqlPart;
}