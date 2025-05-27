package com.nnk.poseidon.repositories;


import com.nnk.poseidon.domain.Trade;
import com.nnk.poseidon.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TradeRepository extends JpaRepository<Trade, Integer> {
}
