package com.nnk.poseidon.services;

import com.nnk.poseidon.domain.Rating;
import com.nnk.poseidon.repositories.RatingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @Transactional(readOnly = true)
    public List<Rating> getAllRatings() {
        log.debug("Fetching all ratings from the database");
        List<Rating> ratings = ratingRepository.findAll();
        log.debug("Number of ratings fetched: {}", ratings.size());
        return ratings;
    }
}
