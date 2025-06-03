package com.nnk.poseidon.services; // Assure-toi que ce package existe

import com.nnk.poseidon.domain.BidList;
import com.nnk.poseidon.dto.BidListDTO;
import com.nnk.poseidon.repositories.BidListRepository; // Tu auras besoin de ce repository
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Classe de service pour la gestion des entités {@link BidList}.
 * Gère la logique métier pour les offres (bids) et interagit avec le repository.
 * Opère avec des DTOs pour les échanges avec les couches supérieures.
 */
@Slf4j
@Service
public class BidListService {

    private final BidListRepository bidListRepository;

    @Autowired
    public BidListService(BidListRepository bidListRepository) {
        this.bidListRepository = bidListRepository;
    }


    private BidListDTO convertToDTO(BidList bidList) {
        if (bidList == null) {
            return null;
        }
        BidListDTO dto = new BidListDTO();
        dto.setBidListId(bidList.getBidListId());
        dto.setAccount(bidList.getAccount());
        dto.setType(bidList.getType());
        dto.setBidQuantity(bidList.getBidQuantity());
        dto.setCreationDate(bidList.getCreationDate()); // Inclure pour l'affichage

        return dto;
    }

    private BidList convertToEntity(BidListDTO bidListDTO) {
        if (bidListDTO == null) {
            return null;
        }
        BidList entity = new BidList();
        // L'ID est important pour la mise à jour. S'il est null, c'est une nouvelle entité.
        entity.setBidListId(bidListDTO.getBidListId());
        entity.setAccount(bidListDTO.getAccount());
        entity.setType(bidListDTO.getType());
        entity.setBidQuantity(bidListDTO.getBidQuantity());
        // Les autres champs de l'entité (askQuantity, bid, ask, etc.) ne sont pas dans ce DTO simple.
        // La creationDate sera gérée lors de la sauvegarde.
        return entity;
    }


    /**
     * Récupère tous les DTOs de BidList.
     * @return une liste de tous les {@link BidListDTO}.
     */
    @Transactional(readOnly = true)
    public List<BidListDTO> findAll() {
        log.debug("Récupération de toutes les BidLists et conversion en DTOs");
        return bidListRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un DTO de BidList par son ID.
     * @param id L'ID du BidList à récupérer.
     * @return un Optional contenant le {@link BidListDTO} si trouvé, ou un Optional vide sinon.
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
     * Sauvegarde un nouveau BidList ou met à jour un existant à partir d'un DTO.
     * @param bidListDTO Le {@link BidListDTO} contenant les données à sauvegarder.
     * @return Le {@link BidListDTO} de l'entité sauvegardée.
     * @throws IllegalArgumentException si le bidListDTO est nul.
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
            // Tu pourrais aussi vouloir initialiser revisionDate, creationName, etc. ici
            // bidListToSave.setCreationName(getCurrentUsername()); // Exemple si tu as la gestion des utilisateurs
        } else { // Mise à jour
            log.info("Mise à jour du BidList existant avec id {} à partir du DTO : {}", bidListDTO.getBidListId(), bidListDTO);
            BidList existingBidList = bidListRepository.findById(bidListDTO.getBidListId())
                    .orElseThrow(() -> {
                        log.warn("BidList non trouvé pour la mise à jour avec id : {}", bidListDTO.getBidListId());
                        return new IllegalArgumentException("Mise à jour impossible : BidList non trouvé avec id: " + bidListDTO.getBidListId());
                    });

            // Mettre à jour les champs de l'entité existante à partir du DTO
            existingBidList.setAccount(bidListDTO.getAccount());
            existingBidList.setType(bidListDTO.getType());
            existingBidList.setBidQuantity(bidListDTO.getBidQuantity());
            existingBidList.setRevisionDate(LocalDateTime.now()); // Mettre à jour la date de révision
            // existingBidList.setRevisionName(getCurrentUsername()); // Exemple
            // Les autres champs non présents dans le DTO restent inchangés sur existingBidList.
            bidListToSave = existingBidList;
        }

        BidList savedEntity = bidListRepository.save(bidListToSave);
        log.info("BidList sauvegardé avec succès : {}", savedEntity);
        return convertToDTO(savedEntity);
    }

    /**
     * Supprime un BidList par son ID.
     * @param id L'ID du BidList à supprimer.
     * @throws IllegalArgumentException si l'ID est nul ou si le BidList avec l'ID donné n'est pas trouvé.
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