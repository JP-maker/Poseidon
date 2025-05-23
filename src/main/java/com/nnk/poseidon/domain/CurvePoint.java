package com.nnk.poseidon.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * Represents a point on a curve.
 */
@Entity
@Table(name = "CurvePoint")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurvePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "CurveId") // This might be a foreign key to a Curve entity not defined here
    private Integer curveId;

    @Column(name = "asOfDate")
    private LocalDateTime asOfDate;

    @Column(name = "term")
    private Double term;

    @Column(name = "value")
    private Double value;

    @Column(name = "creationDate")
    private LocalDateTime creationDate;
}
