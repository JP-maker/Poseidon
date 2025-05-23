package com.nnk.poseidon.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Represents a rule definition.
 */
@Entity
@Table(name = "RuleName")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "name", length = 125)
    private String name;

    @Column(name = "description", length = 125)
    private String description;

    @Column(name = "json", length = 125)
    private String json;

    @Column(name = "template", length = 512)
    private String template;

    @Column(name = "sqlStr", length = 125)
    private String sqlStr;

    @Column(name = "sqlPart", length = 125)
    private String sqlPart;
}
