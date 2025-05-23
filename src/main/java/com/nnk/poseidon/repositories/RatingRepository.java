package com.nnk.poseidon.repositories;

import com.nnk.poseidon.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Integer> {

}
