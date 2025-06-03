package com.nnk.poseidon.services;

import com.nnk.poseidon.domain.CurvePoint;
import com.nnk.poseidon.dto.CurvePointDTO; // Importer le DTO
import com.nnk.poseidon.repositories.CurvePointRepository;
import lombok.extern.slf4j.Slf4j; // Utilisation de @Slf4j de Lombok
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Classe de service pour la gestion des entités {@link CurvePoint}.
 * Gère la logique métier pour les points de courbe et interagit avec le repository.
 * Opère avec des DTOs pour les échanges avec les couches supérieures (ex: contrôleurs).
 */
@Slf4j
@Service
public class CurvePointService { // Plus d'interface

    private final CurvePointRepository curvePointRepository;

    /**
     * Construit un nouveau CurvePointService avec le repository donné.
     * @param curvePointRepository Le repository pour les entités CurvePoint.
     */
    @Autowired
    public CurvePointService(CurvePointRepository curvePointRepository) {
        this.curvePointRepository = curvePointRepository;
    }

    // --- Méthodes de mapping privées DTO <-> Entité ---

    private CurvePointDTO convertToDTO(CurvePoint curvePoint) {
        if (curvePoint == null) {
            return null;
        }
        return new CurvePointDTO(
                curvePoint.getId(),
                curvePoint.getCurveId(),
                curvePoint.getAsOfDate(),
                curvePoint.getTerm(),
                curvePoint.getValue(),
                curvePoint.getCreationDate()
        );
    }

    private CurvePoint convertToEntity(CurvePointDTO curvePointDTO) {
        if (curvePointDTO == null) {
            return null;
        }
        CurvePoint curvePoint = new CurvePoint();
        curvePoint.setId(curvePointDTO.getId()); // Important pour la mise à jour
        curvePoint.setCurveId(curvePointDTO.getCurveId());
        curvePoint.setAsOfDate(curvePointDTO.getAsOfDate());
        curvePoint.setTerm(curvePointDTO.getTerm());
        curvePoint.setValue(curvePointDTO.getValue());
        // creationDate n'est pas défini depuis le DTO pour la création/maj,
        // il sera géré lors de la sauvegarde d'une nouvelle entité.
        // Si l'entité est existante, sa creationDate sera préservée.
        return curvePoint;
    }

    /**
     * Récupère tous les DTOs de CurvePoint.
     * @return une liste de tous les {@link CurvePointDTO}.
     */
    @Transactional(readOnly = true)
    public List<CurvePointDTO> findAll() {
        log.debug("Récupération de tous les CurvePoints et conversion en DTOs");
        return curvePointRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un DTO de CurvePoint par son ID.
     * @param id L'ID du CurvePoint à récupérer.
     * @return un Optional contenant le {@link CurvePointDTO} si trouvé, ou un Optional vide sinon.
     */
    @Transactional(readOnly = true)
    public Optional<CurvePointDTO> findById(Integer id) {
        log.debug("Récupération du CurvePoint avec id : {} et conversion en DTO", id);
        if (id == null) {
            log.warn("Tentative de recherche de CurvePoint avec un ID nul");
            return Optional.empty();
        }
        return curvePointRepository.findById(id).map(this::convertToDTO);
    }

    /**
     * Sauvegarde un nouveau CurvePoint ou met à jour un existant à partir d'un DTO.
     * @param curvePointDTO Le {@link CurvePointDTO} contenant les données à sauvegarder.
     * @return Le {@link CurvePointDTO} de l'entité sauvegardée.
     * @throws IllegalArgumentException si le curvePointDTO est nul.
     */
    @Transactional
    public CurvePointDTO save(CurvePointDTO curvePointDTO) {
        if (curvePointDTO == null) {
            log.error("Tentative de sauvegarde d'un objet CurvePointDTO nul");
            throw new IllegalArgumentException("L'objet CurvePointDTO ne peut pas être nul.");
        }

        CurvePoint curvePointToSave;
        if (curvePointDTO.getId() == null) { // Création d'une nouvelle entité
            log.info("Création d'un nouveau CurvePoint à partir du DTO : {}", curvePointDTO);
            curvePointToSave = convertToEntity(curvePointDTO);
            curvePointToSave.setCreationDate(LocalDateTime.now()); // Définir la date de création
        } else { // Mise à jour d'une entité existante
            log.info("Mise à jour du CurvePoint existant avec id {} à partir du DTO : {}", curvePointDTO.getId(), curvePointDTO);
            CurvePoint existingCurvePoint = curvePointRepository.findById(curvePointDTO.getId())
                    .orElseThrow(() -> {
                        log.warn("CurvePoint non trouvé pour la mise à jour avec id : {}", curvePointDTO.getId());
                        return new IllegalArgumentException("Mise à jour impossible : CurvePoint non trouvé avec id: " + curvePointDTO.getId());
                    });
            // Mettre à jour les champs de l'entité existante à partir du DTO
            existingCurvePoint.setCurveId(curvePointDTO.getCurveId());
            existingCurvePoint.setAsOfDate(curvePointDTO.getAsOfDate());
            existingCurvePoint.setTerm(curvePointDTO.getTerm());
            existingCurvePoint.setValue(curvePointDTO.getValue());
            // La creationDate de existingCurvePoint est préservée
            curvePointToSave = existingCurvePoint;
        }

        CurvePoint savedEntity = curvePointRepository.save(curvePointToSave);
        log.info("CurvePoint sauvegardé avec succès : {}", savedEntity);
        return convertToDTO(savedEntity);
    }

    /**
     * Supprime un CurvePoint par son ID.
     * @param id L'ID du CurvePoint à supprimer.
     * @throws IllegalArgumentException si l'ID est nul ou si le CurvePoint avec l'ID donné n'est pas trouvé.
     */
    @Transactional
    public void deleteById(Integer id) {
        if (id == null) {
            log.warn("Tentative de suppression de CurvePoint avec un ID nul");
            throw new IllegalArgumentException("L'ID pour la suppression ne peut pas être nul.");
        }
        if (!curvePointRepository.existsById(id)) {
            log.warn("Tentative de suppression d'un CurvePoint non existant avec id : {}", id);
            throw new IllegalArgumentException("CurvePoint non trouvé avec id : " + id + " pour suppression.");
        }
        log.info("Suppression du CurvePoint avec id : {}", id);
        curvePointRepository.deleteById(id);
    }
}