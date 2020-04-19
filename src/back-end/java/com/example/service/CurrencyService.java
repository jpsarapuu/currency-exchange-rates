package com.example.service;

import com.example.model.Currency;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;

public interface CurrencyService {

    ResponseEntity<EntityModel<Currency>> getCurrency(Long id);

    ResponseEntity<CollectionModel<EntityModel<Currency>>> getCurrencies();

    ResponseEntity<EntityModel<Currency>> newCurrency(Currency currency);

    ResponseEntity<EntityModel<Currency>> updateCurrency(Currency currency, Long id);

    ResponseEntity<Void> deleteCurrency(Long id);
}
