package com.nnk.poseidon.repositories;


import com.nnk.poseidon.domain.Trade;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TradeRepository extends JpaRepository<Trade, Integer> {
}
