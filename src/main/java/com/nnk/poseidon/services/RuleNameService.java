package com.nnk.poseidon.services; // Assure-toi que ce package existe

import com.nnk.poseidon.domain.RuleName;
import com.nnk.poseidon.dto.RuleNameDTO;
import com.nnk.poseidon.repositories.RuleNameRepository; // Tu auras besoin de ce repository
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Classe de service pour la gestion des entités {@link RuleName}.
 * Gère la logique métier pour les définitions de règles et interagit avec le repository.
 * Opère avec des DTOs pour les échanges avec les couches supérieures.
 */
@Slf4j
@Service
public class RuleNameService {

    private final RuleNameRepository ruleNameRepository;

    @Autowired
    public RuleNameService(RuleNameRepository ruleNameRepository) {
        this.ruleNameRepository = ruleNameRepository;
    }

    // --- Méthodes de mapping privées DTO <-> Entité ---

    private RuleNameDTO convertToDTO(RuleName ruleName) {
        if (ruleName == null) {
            return null;
        }
        RuleNameDTO dto = new RuleNameDTO();
        dto.setId(ruleName.getId());
        dto.setName(ruleName.getName());
        dto.setDescription(ruleName.getDescription());
        dto.setJson(ruleName.getJson());
        dto.setTemplate(ruleName.getTemplate());
        dto.setSql(ruleName.getSqlStr()); // Mapper sqlStr de l'entité vers sql du DTO
        dto.setSqlPart(ruleName.getSqlPart());
        return dto;
    }

    private RuleName convertToEntity(RuleNameDTO RuleNameDTO) {
        if (RuleNameDTO == null) {
            return null;
        }
        RuleName entity = new RuleName();
        entity.setId(RuleNameDTO.getId()); // Important pour la mise à jour
        entity.setName(RuleNameDTO.getName());
        entity.setDescription(RuleNameDTO.getDescription());
        entity.setJson(RuleNameDTO.getJson());
        entity.setTemplate(RuleNameDTO.getTemplate());
        entity.setSqlStr(RuleNameDTO.getSql()); // Mapper sql du DTO vers sqlStr de l'entité
        entity.setSqlPart(RuleNameDTO.getSqlPart());
        return entity;
    }


    /**
     * Récupère tous les DTOs de RuleName.
     * @return une liste de tous les {@link RuleNameDTO}.
     */
    @Transactional(readOnly = true)
    public List<RuleNameDTO> findAll() {
        log.debug("Récupération de toutes les RuleNames et conversion en DTOs");
        return ruleNameRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un DTO de RuleName par son ID.
     * @param id L'ID du RuleName à récupérer.
     * @return un Optional contenant le {@link RuleNameDTO} si trouvé, ou un Optional vide sinon.
     */
    @Transactional(readOnly = true)
    public Optional<RuleNameDTO> findById(Integer id) {
        log.debug("Récupération du RuleName avec id : {} et conversion en DTO", id);
        if (id == null) {
            log.warn("Tentative de recherche de RuleName avec un ID nul");
            return Optional.empty();
        }
        return ruleNameRepository.findById(id).map(this::convertToDTO);
    }

    /**
     * Sauvegarde un nouveau RuleName ou met à jour un existant à partir d'un DTO.
     * @param RuleNameDTO Le {@link RuleNameDTO} contenant les données à sauvegarder.
     * @return Le {@link RuleNameDTO} de l'entité sauvegardée.
     * @throws IllegalArgumentException si le RuleNameDTO est nul.
     */
    @Transactional
    public RuleNameDTO save(RuleNameDTO RuleNameDTO) {
        if (RuleNameDTO == null) {
            log.error("Tentative de sauvegarde d'un objet RuleNameDTO nul");
            throw new IllegalArgumentException("L'objet RuleNameDTO ne peut pas être nul.");
        }

        RuleName ruleNameToSave;
        if (RuleNameDTO.getId() == null) { // Création
            log.info("Création d'un nouveau RuleName à partir du DTO : {}", RuleNameDTO);
            ruleNameToSave = convertToEntity(RuleNameDTO);
            // Pas de champs de date de création/modification dans l'entité RuleName
        } else { // Mise à jour
            log.info("Mise à jour du RuleName existant avec id {} à partir du DTO : {}", RuleNameDTO.getId(), RuleNameDTO);
            RuleName existingRuleName = ruleNameRepository.findById(RuleNameDTO.getId())
                    .orElseThrow(() -> {
                        log.warn("RuleName non trouvé pour la mise à jour avec id : {}", RuleNameDTO.getId());
                        return new IllegalArgumentException("Mise à jour impossible : RuleName non trouvé avec id: " + RuleNameDTO.getId());
                    });

            // Mettre à jour les champs de l'entité existante à partir du DTO
            existingRuleName.setName(RuleNameDTO.getName());
            existingRuleName.setDescription(RuleNameDTO.getDescription());
            existingRuleName.setJson(RuleNameDTO.getJson());
            existingRuleName.setTemplate(RuleNameDTO.getTemplate());
            existingRuleName.setSqlStr(RuleNameDTO.getSql()); // Mapper sql du DTO vers sqlStr de l'entité
            existingRuleName.setSqlPart(RuleNameDTO.getSqlPart());
            ruleNameToSave = existingRuleName;
        }

        RuleName savedEntity = ruleNameRepository.save(ruleNameToSave);
        log.info("RuleName sauvegardé avec succès : {}", savedEntity);
        return convertToDTO(savedEntity);
    }

    /**
     * Supprime un RuleName par son ID.
     * @param id L'ID du RuleName à supprimer.
     * @throws IllegalArgumentException si l'ID est nul ou si le RuleName avec l'ID donné n'est pas trouvé.
     */
    @Transactional
    public void deleteById(Integer id) {
        if (id == null) {
            log.warn("Tentative de suppression de RuleName avec un ID nul");
            throw new IllegalArgumentException("L'ID pour la suppression ne peut pas être nul.");
        }
        if (!ruleNameRepository.existsById(id)) {
            log.warn("Tentative de suppression d'un RuleName non existant avec id : {}", id);
            throw new IllegalArgumentException("RuleName non trouvé avec id : " + id + " pour suppression.");
        }
        log.info("Suppression du RuleName avec id : {}", id);
        ruleNameRepository.deleteById(id);
    }
}