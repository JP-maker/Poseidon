package com.nnk.poseidon.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Represents credit ratings from different agencies.
 */
@Entity
@Table(name = "Rating")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "moodysRating", length = 125)
    private String moodysRating;

    @Column(name = "sandPRating", length = 125)
    private String sandPRating;

    @Column(name = "fitchRating", length = 125)
    private String fitchRating;

    @Column(name = "orderNumber")
    private Integer orderNumber; // tinyint mapped to Integer
}
