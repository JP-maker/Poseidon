package com.nnk.poseidon.services;

import com.nnk.poseidon.domain.Trade;
import com.nnk.poseidon.dto.TradeDTO;
import com.nnk.poseidon.repositories.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des opérations CRUD sur les entités {@link com.nnk.poseidon.domain.Trade}.
 * Gère la logique métier et interagit avec {@link TradeRepository} pour l'accès aux données.
 */
@Service
public class TradeService { // Le nom de la classe est maintenant TradeService

    private final TradeRepository tradeRepository;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param tradeRepository le repository pour les entités Trade.
     */
    @Autowired
    public TradeService(TradeRepository tradeRepository) { // Le constructeur reflète le nouveau nom de classe
        this.tradeRepository = tradeRepository;
    }

    /**
     * Récupère tous les trades.
     *
     * @return une liste de {@link TradeDTO} représentant tous les trades.
     */
    @Transactional(readOnly = true)
    public List<TradeDTO> findAllTrades() {
        return tradeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un trade par son identifiant.
     *
     * @param id l'identifiant du trade à récupérer.
     * @return un {@link Optional} contenant le {@link TradeDTO} si trouvé, sinon {@link Optional#empty()}.
     */
    @Transactional(readOnly = true)
    public Optional<TradeDTO> findTradeById(Integer id) {
        return tradeRepository.findById(id)
                .map(this::convertToDTO);
    }

    /**
     * Sauvegarde un nouveau trade ou met à jour un trade existant.
     * Pour un nouveau trade, la date de création est initialisée.
     * La date de révision est mise à jour à chaque sauvegarde.
     *
     * @param tradeDTO le {@link TradeDTO} contenant les informations du trade à sauvegarder.
     * @return le {@link TradeDTO} sauvegardé.
     */
    @Transactional
    public TradeDTO saveTrade(TradeDTO tradeDTO) {
        Trade trade = convertToEntity(tradeDTO);
        if (trade.getTradeId() == null) { // Nouveau trade
            trade.setCreationDate(LocalDateTime.now());
            // Vous pourriez aussi vouloir définir creationName ici si vous avez l'utilisateur courant
            // trade.setCreationName(SecurityContextHolder.getContext().getAuthentication().getName());
        }
        trade.setRevisionDate(LocalDateTime.now());
        // Et revisionName
        // trade.setRevisionName(SecurityContextHolder.getContext().getAuthentication().getName());

        Trade savedTrade = tradeRepository.save(trade);
        return convertToDTO(savedTrade);
    }

    /**
     * Met à jour un trade existant identifié par son ID.
     * Seuls les champs modifiables via le DTO sont mis à jour.
     * La date de révision et potentiellement le nom du réviseur sont mis à jour.
     *
     * @param id l'identifiant du trade à mettre à jour.
     * @param tradeDTO le {@link TradeDTO} contenant les nouvelles informations pour le trade.
     * @return un {@link Optional} contenant le {@link TradeDTO} mis à jour si le trade original a été trouvé,
     *         sinon {@link Optional#empty()}.
     */
    @Transactional
    public Optional<TradeDTO> updateTrade(Integer id, TradeDTO tradeDTO) {
        return tradeRepository.findById(id)
                .map(existingTrade -> {
                    existingTrade.setAccount(tradeDTO.getAccount());
                    existingTrade.setType(tradeDTO.getType());
                    existingTrade.setBuyQuantity(tradeDTO.getBuyQuantity());
                    // Les autres champs (sellQuantity, prices, etc.) restent inchangés
                    // s'ils ne sont pas dans le DTO ou gérés par ce flux.

                    existingTrade.setRevisionDate(LocalDateTime.now());
                    // existingTrade.setRevisionName(SecurityContextHolder.getContext().getAuthentication().getName());

                    Trade updatedTrade = tradeRepository.save(existingTrade);
                    return convertToDTO(updatedTrade);
                });
    }

    /**
     * Supprime un trade par son identifiant.
     * Si le trade n'existe pas, l'opération n'a aucun effet et aucune erreur n'est levée.
     *
     * @param id l'identifiant du trade à supprimer.
     */
    @Transactional
    public void deleteTradeById(Integer id) {
        if (!tradeRepository.existsById(id)) {
            // Optionnel: logger un avertissement si l'ID n'est pas trouvé
            // logger.warn("Tentative de suppression d'un trade non existant avec ID: {}", id);
            return;
        }
        tradeRepository.deleteById(id);
    }

    /**
     * Convertit une entité {@link Trade} en {@link TradeDTO}.
     *
     * @param trade l'entité à convertir.
     * @return le DTO résultant.
     */
    private TradeDTO convertToDTO(Trade trade) {
        TradeDTO dto = new TradeDTO();
        dto.setTradeId(trade.getTradeId());
        dto.setAccount(trade.getAccount());
        dto.setType(trade.getType());
        dto.setBuyQuantity(trade.getBuyQuantity());
        dto.setSellQuantity(trade.getSellQuantity());
        dto.setBuyPrice(trade.getBuyPrice());
        dto.setSellPrice(trade.getSellPrice());
        dto.setTradeDate(trade.getTradeDate());
        dto.setSecurity(trade.getSecurity());
        dto.setStatus(trade.getStatus());
        dto.setTrader(trade.getTrader());
        dto.setBenchmark(trade.getBenchmark());
        dto.setBook(trade.getBook());
        dto.setCreationName(trade.getCreationName());
        dto.setCreationDate(trade.getCreationDate());
        dto.setRevisionName(trade.getRevisionName());
        dto.setRevisionDate(trade.getRevisionDate());
        dto.setDealName(trade.getDealName());
        dto.setDealType(trade.getDealType());
        dto.setSourceListId(trade.getSourceListId());
        dto.setSide(trade.getSide());
        return dto;
    }

    /**
     * Convertit un {@link TradeDTO} en entité {@link Trade}.
     *
     * @param tradeDTO le DTO à convertir.
     * @return l'entité {@link Trade} résultante.
     */
    private Trade convertToEntity(TradeDTO tradeDTO) {
        Trade trade = new Trade();
        trade.setTradeId(tradeDTO.getTradeId());
        trade.setAccount(tradeDTO.getAccount());
        trade.setType(tradeDTO.getType());
        trade.setBuyQuantity(tradeDTO.getBuyQuantity());
        trade.setSellQuantity(tradeDTO.getSellQuantity());
        trade.setBuyPrice(tradeDTO.getBuyPrice());
        trade.setSellPrice(tradeDTO.getSellPrice());
        trade.setTradeDate(tradeDTO.getTradeDate());
        trade.setSecurity(tradeDTO.getSecurity());
        trade.setStatus(tradeDTO.getStatus());
        trade.setTrader(tradeDTO.getTrader());
        trade.setBenchmark(tradeDTO.getBenchmark());
        trade.setBook(tradeDTO.getBook());
        trade.setCreationName(tradeDTO.getCreationName());
        trade.setCreationDate(tradeDTO.getCreationDate());
        trade.setRevisionName(tradeDTO.getRevisionName());
        trade.setRevisionDate(tradeDTO.getRevisionDate());
        trade.setDealName(tradeDTO.getDealName());
        trade.setDealType(tradeDTO.getDealType());
        trade.setSourceListId(tradeDTO.getSourceListId());
        trade.setSide(tradeDTO.getSide());
        return trade;
    }
}