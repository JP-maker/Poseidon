package com.nnk.poseidon.services;

import com.nnk.poseidon.domain.CurvePoint;
import com.nnk.poseidon.dto.CurvePointDTO;
import com.nnk.poseidon.repositories.CurvePointRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion de la logique métier liée aux entités {@link CurvePoint}.
 * <p>
 * Cette classe encapsule toute la logique métier pour les points de courbe, y compris les opérations
 * CRUD, la validation des données et la conversion entre les entités JPA ({@link CurvePoint})
 * et les Data Transfer Objects ({@link CurvePointDTO}). Elle agit comme un intermédiaire entre
 * la couche de contrôleur et la couche de persistance.
 * </p>
 *
 * @see CurvePointDTO Le DTO utilisé pour les échanges de données.
 * @see CurvePoint L'entité JPA gérée par ce service.
 * @see CurvePointRepository Le repository utilisé pour l'accès aux données.
 */
@Slf4j
@Service
public class CurvePointService {

    private final CurvePointRepository curvePointRepository;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param curvePointRepository Le repository pour l'accès aux données des CurvePoint, injecté par Spring.
     */
    @Autowired
    public CurvePointService(CurvePointRepository curvePointRepository) {
        this.curvePointRepository = curvePointRepository;
    }

    /**
     * Méthode utilitaire privée pour convertir une entité {@link CurvePoint} en son DTO {@link CurvePointDTO}.
     *
     * @param curvePoint L'entité à convertir.
     * @return Le DTO correspondant, ou {@code null} si l'entité en entrée est nulle.
     */
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

    /**
     * Méthode utilitaire privée pour convertir un {@link CurvePointDTO} en une entité {@link CurvePoint}.
     * <p>
     * Note: Cette méthode ne définit que les champs présents dans le DTO. Le champ d'audit
     * {@code creationDate} est géré dans la méthode {@link #save} lors de la création.
     * </p>
     *
     * @param curvePointDTO Le DTO à convertir.
     * @return L'entité JPA correspondante, ou {@code null} si le DTO en entrée est nul.
     */
    private CurvePoint convertToEntity(CurvePointDTO curvePointDTO) {
        if (curvePointDTO == null) {
            return null;
        }
        CurvePoint curvePoint = new CurvePoint();
        curvePoint.setId(curvePointDTO.getId());
        curvePoint.setCurveId(curvePointDTO.getCurveId());
        curvePoint.setAsOfDate(curvePointDTO.getAsOfDate());
        curvePoint.setTerm(curvePointDTO.getTerm());
        curvePoint.setValue(curvePointDTO.getValue());
        return curvePoint;
    }

    /**
     * Récupère tous les points de courbe et les convertit en une liste de DTOs.
     * L'opération est exécutée dans une transaction en lecture seule pour optimiser les performances.
     *
     * @return Une liste de {@link CurvePointDTO}, potentiellement vide si aucun point n'est trouvé.
     */
    @Transactional(readOnly = true)
    public List<CurvePointDTO> findAll() {
        log.debug("Récupération de tous les CurvePoints et conversion en DTOs");
        return curvePointRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Recherche un point de courbe par son identifiant unique (ID).
     *
     * @param id L'identifiant (clé primaire) du point à rechercher.
     * @return Un {@link Optional} contenant le {@link CurvePointDTO} si le point est trouvé,
     *         ou un {@link Optional#empty()} sinon.
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
     * Sauvegarde un nouveau point de courbe ou met à jour un point existant à partir d'un DTO.
     * <p>
     * La méthode distingue deux cas en fonction de la présence de l'ID dans le DTO :
     * <ul>
     *     <li><b>Création :</b> Si l'ID est nul, une nouvelle entité est créée et la {@code creationDate} est initialisée.</li>
     *     <li><b>Mise à jour :</b> Si l'ID est non nul, l'entité existante est récupérée et ses champs sont mis à jour.
     *     La {@code creationDate} originale est préservée.</li>
     * </ul>
     * L'opération est transactionnelle, garantissant l'atomicité de la sauvegarde.
     *
     * @param curvePointDTO Le DTO contenant les données du point à sauvegarder. Ne doit pas être nul.
     * @return Le DTO représentant l'entité sauvegardée, avec son ID mis à jour si c'était une création.
     * @throws IllegalArgumentException si {@code curvePointDTO} est nul, ou si une mise à jour est tentée
     *                                  pour un ID qui n'existe pas en base de données.
     */
    @Transactional
    public CurvePointDTO save(CurvePointDTO curvePointDTO) {
        if (curvePointDTO == null) {
            log.error("Tentative de sauvegarde d'un objet CurvePointDTO nul");
            throw new IllegalArgumentException("L'objet CurvePointDTO ne peut pas être nul.");
        }

        CurvePoint curvePointToSave;
        if (curvePointDTO.getId() == null) { // Création
            log.info("Création d'un nouveau CurvePoint à partir du DTO : {}", curvePointDTO);
            curvePointToSave = convertToEntity(curvePointDTO);
            curvePointToSave.setCreationDate(LocalDateTime.now());
        } else { // Mise à jour
            log.info("Mise à jour du CurvePoint existant avec id {} à partir du DTO : {}", curvePointDTO.getId(), curvePointDTO);
            CurvePoint existingCurvePoint = curvePointRepository.findById(curvePointDTO.getId())
                    .orElseThrow(() -> {
                        log.warn("CurvePoint non trouvé pour la mise à jour avec id : {}", curvePointDTO.getId());
                        return new IllegalArgumentException("Mise à jour impossible : CurvePoint non trouvé avec id: " + curvePointDTO.getId());
                    });

            existingCurvePoint.setCurveId(curvePointDTO.getCurveId());
            existingCurvePoint.setAsOfDate(curvePointDTO.getAsOfDate());
            existingCurvePoint.setTerm(curvePointDTO.getTerm());
            existingCurvePoint.setValue(curvePointDTO.getValue());
            curvePointToSave = existingCurvePoint;
        }

        CurvePoint savedEntity = curvePointRepository.save(curvePointToSave);
        log.info("CurvePoint sauvegardé avec succès : {}", savedEntity);
        return convertToDTO(savedEntity);
    }

    /**
     * Supprime un point de courbe par son identifiant unique (ID).
     *
     * @param id L'identifiant (clé primaire) du point à supprimer.
     * @throws IllegalArgumentException si l'ID est nul ou si aucun point avec cet ID n'est trouvé
     *                                  dans la base de données.
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