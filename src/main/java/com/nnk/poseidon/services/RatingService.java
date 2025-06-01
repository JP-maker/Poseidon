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

    @Transactional
    public Rating saveRating(Rating rating) {
        log.debug("Saving rating: {}", rating);
        Rating savedRating = ratingRepository.save(rating);
        log.debug("Rating saved with ID: {}", savedRating.getId());
        return savedRating;
    }

    @Transactional(readOnly = true)
    public Rating getRatingById(Integer id) {
        log.debug("Fetching rating by ID: {}", id);
        return ratingRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteRating(Integer id) {
        log.debug("Deleting rating with ID: {}", id);
        ratingRepository.deleteById(id);
        log.debug("Rating with ID: {} deleted successfully", id);
    }
}
