package com.nnk.poseidon.services;

import com.nnk.poseidon.domain.Trade;
import com.nnk.poseidon.repositories.TradeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TradeService {

    private final TradeRepository tradeRepository;
    public TradeService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @Transactional(readOnly = true)
    public List<Trade> getAllTrades() {
        log.debug("Fetching all trades from the database");
        List<Trade> trades = tradeRepository.findAll();
        log.debug("Number of trades fetched: {}", trades.size());
        return trades;
    }

}
