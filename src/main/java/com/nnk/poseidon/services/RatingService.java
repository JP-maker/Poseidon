package com.nnk.poseidon.services;

import com.nnk.poseidon.domain.Rating;
import com.nnk.poseidon.dto.RatingDTO; // Importer le DTO
import com.nnk.poseidon.repositories.RatingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Classe de service pour la gestion des entités {@link Rating}.
 * Gère la logique métier pour les notations et interagit avec le repository.
 * Opère avec des DTOs pour les échanges avec les couches supérieures (ex: contrôleurs).
 */
@Slf4j
@Service
public class RatingService { // Plus d'interface

    private final RatingRepository ratingRepository;

    /**
     * Construit un nouveau RatingService avec le repository donné.
     * @param ratingRepository Le repository pour les entités Rating.
     */
    @Autowired
    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    // --- Méthodes de mapping privées DTO <-> Entité ---

    private RatingDTO convertToDTO(Rating rating) {
        if (rating == null) {
            return null;
        }
        return new RatingDTO(
                rating.getId(),
                rating.getMoodysRating(),
                rating.getSandPRating(),
                rating.getFitchRating(),
                rating.getOrderNumber()
        );
    }

    private Rating convertToEntity(RatingDTO RatingDTO) {
        if (RatingDTO == null) {
            return null;
        }
        Rating rating = new Rating();
        // L'ID est important pour la mise à jour. S'il est null, c'est une nouvelle entité.
        rating.setId(RatingDTO.getId());
        rating.setMoodysRating(RatingDTO.getMoodysRating());
        rating.setSandPRating(RatingDTO.getSandPRating());
        rating.setFitchRating(RatingDTO.getFitchRating());
        rating.setOrderNumber(RatingDTO.getOrderNumber());
        return rating;
    }

    /**
     * Récupère tous les DTOs de Rating.
     * @return une liste de tous les {@link RatingDTO}.
     */
    @Transactional(readOnly = true)
    public List<RatingDTO> getAllRatings() {
        log.debug("Récupération de tous les Ratings et conversion en DTOs");
        List<Rating> ratings = ratingRepository.findAll();
        log.debug("Nombre de ratings récupérés de la base : {}", ratings.size());
        return ratings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un DTO de Rating par son ID.
     * @param id L'ID du Rating à récupérer.
     * @return un Optional contenant le {@link RatingDTO} si trouvé, ou un Optional vide sinon.
     */
    @Transactional(readOnly = true)
    public Optional<RatingDTO> getRatingById(Integer id) {
        log.debug("Récupération du Rating avec id : {} et conversion en DTO", id);
        if (id == null) {
            log.warn("Tentative de recherche de Rating avec un ID nul");
            return Optional.empty();
        }
        return ratingRepository.findById(id).map(this::convertToDTO);
    }

    /**
     * Sauvegarde un nouveau Rating ou met à jour un existant à partir d'un DTO.
     * @param RatingDTO Le {@link RatingDTO} contenant les données à sauvegarder.
     * @return Le {@link RatingDTO} de l'entité sauvegardée.
     * @throws IllegalArgumentException si le RatingDTO est nul.
     */
    @Transactional
    public RatingDTO saveRating(RatingDTO RatingDTO) {
        if (RatingDTO == null) {
            log.error("Tentative de sauvegarde d'un objet RatingDTO nul");
            throw new IllegalArgumentException("L'objet RatingDTO ne peut pas être nul.");
        }

        Rating ratingToSave;
        if (RatingDTO.getId() == null) { // Création d'une nouvelle entité
            log.info("Création d'un nouveau Rating à partir du DTO : {}", RatingDTO);
            ratingToSave = convertToEntity(RatingDTO);
            // Pas de champs de date de création/modification dans l'entité Rating fournie.
        } else { // Mise à jour d'une entité existante
            log.info("Mise à jour du Rating existant avec id {} à partir du DTO : {}", RatingDTO.getId(), RatingDTO);
            Rating existingRating = ratingRepository.findById(RatingDTO.getId())
                    .orElseThrow(() -> {
                        log.warn("Rating non trouvé pour la mise à jour avec id : {}", RatingDTO.getId());
                        return new IllegalArgumentException("Mise à jour impossible : Rating non trouvé avec id: " + RatingDTO.getId());
                    });
            // Mettre à jour les champs de l'entité existante à partir du DTO
            existingRating.setMoodysRating(RatingDTO.getMoodysRating());
            existingRating.setSandPRating(RatingDTO.getSandPRating());
            existingRating.setFitchRating(RatingDTO.getFitchRating());
            existingRating.setOrderNumber(RatingDTO.getOrderNumber());
            ratingToSave = existingRating;
        }

        Rating savedEntity = ratingRepository.save(ratingToSave);
        log.info("Rating sauvegardé avec succès : {}", savedEntity);
        return convertToDTO(savedEntity);
    }

    /**
     * Supprime un Rating par son ID.
     * @param id L'ID du Rating à supprimer.
     * @throws IllegalArgumentException si l'ID est nul ou si le Rating avec l'ID donné n'est pas trouvé.
     */
    @Transactional
    public void deleteRating(Integer id) {
        if (id == null) {
            log.warn("Tentative de suppression de Rating avec un ID nul");
            throw new IllegalArgumentException("L'ID pour la suppression ne peut pas être nul.");
        }
        if (!ratingRepository.existsById(id)) {
            log.warn("Tentative de suppression d'un Rating non existant avec id : {}", id);
            throw new IllegalArgumentException("Rating non trouvé avec id : " + id + " pour suppression.");
        }
        log.info("Suppression du Rating avec id : {}", id);
        ratingRepository.deleteById(id);
    }
}