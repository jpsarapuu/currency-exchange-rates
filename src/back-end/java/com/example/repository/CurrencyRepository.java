package com.example.repository;

import com.example.model.Currency;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CurrencyRepository extends CrudRepository<Currency, Long> {

    Currency findFirstBySymbolOrName(String symbol, String name);

    List<Currency> findAllBySymbolOrName(String symbol, String name);
}
