package com.nnk.poseidon.services;

import com.nnk.poseidon.domain.BidList;
import com.nnk.poseidon.dto.BidListDTO;
import com.nnk.poseidon.repositories.BidListRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion de la logique métier liée aux entités {@link BidList}.
 * <p>
 * Cette classe encapsule toute la logique métier pour les offres (bids), y compris les opérations
 * CRUD, la validation des données et la conversion entre les entités JPA ({@link BidList})
 * et les Data Transfer Objects ({@link BidListDTO}). Elle agit comme un intermédiaire entre
 * la couche de contrôleur et la couche de persistance.
 * </p>
 *
 * @see BidListDTO Le DTO utilisé pour les échanges de données.
 * @see BidList L'entité JPA gérée par ce service.
 * @see BidListRepository Le repository utilisé pour l'accès aux données.
 */
@Slf4j
@Service
public class BidListService {

    private final BidListRepository bidListRepository;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param bidListRepository Le repository pour l'accès aux données des BidList, injecté par Spring.
     */
    @Autowired
    public BidListService(BidListRepository bidListRepository) {
        this.bidListRepository = bidListRepository;
    }

    /**
     * Méthode utilitaire privée pour convertir une entité {@link BidList} en son DTO {@link BidListDTO}.
     *
     * @param bidList L'entité à convertir.
     * @return Le DTO correspondant, ou {@code null} si l'entité en entrée est nulle.
     */
    private BidListDTO convertToDTO(BidList bidList) {
        if (bidList == null) {
            return null;
        }
        BidListDTO dto = new BidListDTO();
        dto.setBidListId(bidList.getBidListId());
        dto.setAccount(bidList.getAccount());
        dto.setType(bidList.getType());
        dto.setBidQuantity(bidList.getBidQuantity());
        dto.setCreationDate(bidList.getCreationDate());
        return dto;
    }

    /**
     * Méthode utilitaire privée pour convertir un {@link BidListDTO} en une nouvelle entité {@link BidList}.
     * <p>
     * Note: Cette méthode ne définit que les champs présents dans le DTO. Les champs d'audit
     * comme {@code creationDate} ou {@code revisionDate} sont gérés dans la méthode {@link #save}.
     * </p>
     *
     * @param bidListDTO Le DTO à convertir.
     * @return La nouvelle entité JPA (non persistée), ou {@code null} si le DTO en entrée est nul.
     */
    private BidList convertToEntity(BidListDTO bidListDTO) {
        if (bidListDTO == null) {
            return null;
        }
        BidList entity = new BidList();
        entity.setBidListId(bidListDTO.getBidListId());
        entity.setAccount(bidListDTO.getAccount());
        entity.setType(bidListDTO.getType());
        entity.setBidQuantity(bidListDTO.getBidQuantity());
        return entity;
    }

    /**
     * Récupère toutes les offres (bids) et les convertit en une liste de DTOs.
     * L'opération est exécutée dans une transaction en lecture seule pour des raisons de performance.
     *
     * @return Une liste de {@link BidListDTO}, potentiellement vide si aucune offre n'est trouvée.
     */
    @Transactional(readOnly = true)
    public List<BidListDTO> findAll() {
        log.debug("Récupération de toutes les BidLists et conversion en DTOs");
        return bidListRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Recherche une offre par son identifiant unique (ID).
     *
     * @param id L'identifiant (clé primaire) de l'offre à rechercher.
     * @return Un {@link Optional} contenant le {@link BidListDTO} si l'offre est trouvée,
     *         ou un {@link Optional#empty()} sinon.
     */
    @Transactional(readOnly = true)
    public Optional<BidListDTO> findById(Integer id) {
        log.debug("Récupération du BidList avec id : {} et conversion en DTO", id);
        if (id == null) {
            log.warn("Tentative de recherche de BidList avec un ID nul");
            return Optional.empty();
        }
        return bidListRepository.findById(id).map(this::convertToDTO);
    }

    /**
     * Sauvegarde une nouvelle offre ou met à jour une offre existante à partir d'un DTO.
     * <p>
     * La méthode distingue deux cas en fonction de la présence de l'ID dans le DTO :
     * <ul>
     *     <li><b>Création :</b> Si l'ID est nul, une nouvelle entité est créée et la {@code creationDate} est initialisée.</li>
     *     <li><b>Mise à jour :</b> Si l'ID est non nul, l'entité existante est récupérée, ses champs sont mis à jour,
     *     et la {@code revisionDate} est actualisée.</li>
     * </ul>
     * L'opération est transactionnelle, garantissant l'atomicité de la sauvegarde.
     *
     * @param bidListDTO Le DTO contenant les données de l'offre à sauvegarder. Ne doit pas être nul.
     * @return Le DTO représentant l'entité sauvegardée, avec son ID mis à jour si c'était une création.
     * @throws IllegalArgumentException si {@code bidListDTO} est nul, ou si une mise à jour est tentée
     *                                  pour un ID qui n'existe pas en base de données.
     */
    @Transactional
    public BidListDTO save(BidListDTO bidListDTO) {
        if (bidListDTO == null) {
            log.error("Tentative de sauvegarde d'un objet BidListDTO nul");
            throw new IllegalArgumentException("L'objet BidListDTO ne peut pas être nul.");
        }

        BidList bidListToSave;
        if (bidListDTO.getBidListId() == null) { // Création
            log.info("Création d'un nouveau BidList à partir du DTO : {}", bidListDTO);
            bidListToSave = convertToEntity(bidListDTO);
            bidListToSave.setCreationDate(LocalDateTime.now());
        } else { // Mise à jour
            log.info("Mise à jour du BidList existant avec id {} à partir du DTO : {}", bidListDTO.getBidListId(), bidListDTO);
            BidList existingBidList = bidListRepository.findById(bidListDTO.getBidListId())
                    .orElseThrow(() -> {
                        log.warn("BidList non trouvé pour la mise à jour avec id : {}", bidListDTO.getBidListId());
                        return new IllegalArgumentException("Mise à jour impossible : BidList non trouvé avec id: " + bidListDTO.getBidListId());
                    });

            existingBidList.setAccount(bidListDTO.getAccount());
            existingBidList.setType(bidListDTO.getType());
            existingBidList.setBidQuantity(bidListDTO.getBidQuantity());
            existingBidList.setRevisionDate(LocalDateTime.now());
            bidListToSave = existingBidList;
        }

        BidList savedEntity = bidListRepository.save(bidListToSave);
        log.info("BidList sauvegardé avec succès : {}", savedEntity);
        return convertToDTO(savedEntity);
    }

    /**
     * Supprime une offre par son identifiant unique (ID).
     *
     * @param id L'identifiant (clé primaire) de l'offre à supprimer.
     * @throws IllegalArgumentException si l'ID est nul ou si aucune offre avec cet ID n'est trouvée
     *                                  dans la base de données.
     */
    @Transactional
    public void deleteById(Integer id) {
        if (id == null) {
            log.warn("Tentative de suppression de BidList avec un ID nul");
            throw new IllegalArgumentException("L'ID pour la suppression ne peut pas être nul.");
        }
        if (!bidListRepository.existsById(id)) {
            log.warn("Tentative de suppression d'un BidList non existant avec id : {}", id);
            throw new IllegalArgumentException("BidList non trouvé avec id : " + id + " pour suppression.");
        }
        log.info("Suppression du BidList avec id : {}", id);
        bidListRepository.deleteById(id);
    }
}