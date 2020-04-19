package com.example.service.impl;

import com.example.model.Currency;
import com.example.repository.CurrencyRepository;
import com.example.service.CurrencyService;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private CurrencyRepository currencyRepository;

    @Override
    public ResponseEntity<EntityModel<Currency>> getCurrency(Long id) {

        Currency currency = currencyRepository.findById(id).orElse(null);

        if (currency == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(currency.buildEntityModelWithAffordances());
    }

    @Override
    public ResponseEntity<CollectionModel<EntityModel<Currency>>> getCurrencies() {

        List<EntityModel<Currency>> currencies = new ArrayList<>();
        currencyRepository.findAll().forEach(currency -> currencies.add(currency.buildEntityModelWithAffordances()));
        return ResponseEntity.ok(Currency.buildCollectionModelWithAffordances(currencies));
    }

    @Override
    public ResponseEntity<EntityModel<Currency>> newCurrency(Currency currency) {

        if (!Currency.isCreatable) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (currency.getRate() <= 0) {
            return ResponseEntity.badRequest().body(new EntityModel<>(new Currency(
                    "Unable to create currency: currency rate must be positive.")));
        }

        if (currency.getSymbol().isBlank() || currency.getName().isBlank()) {
            return ResponseEntity.badRequest().body(new EntityModel<>(new Currency(
                    "Unable to create currency: symbol and currency's name must have a value.")));
        }

        if (currencyRepository.findFirstBySymbolOrName(currency.getSymbol(), currency.getName()) != null) {
            return ResponseEntity.badRequest().body(new EntityModel<>(new Currency(
                    "Unable to create currency: currency with that name or symbol already exists.")));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
                currencyRepository.save(currency).buildEntityModelWithAffordances());
    }

    @Override
    public ResponseEntity<EntityModel<Currency>> updateCurrency(Currency currency, Long id) {

        if (!Currency.isUpdatable) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (currency.getRate() <= 0) {
            return ResponseEntity.badRequest().body(new EntityModel<>(new Currency(
                    "Unable to update currency: currency rate must be positive.")));
        }

        if (currency.getSymbol().isBlank() || currency.getName().isBlank()) {
            return ResponseEntity.badRequest().body(new EntityModel<>(new Currency(
                    "Unable to update currency: symbol and currency's name must have a value.")));
        }

        Currency oldCurrency = currencyRepository.findById(id).orElse(null);
        if (oldCurrency != null) {

            // Make sure that currency's new symbol and name are available
            List<Currency> currencies = currencyRepository.findAllBySymbolOrName(currency.getSymbol(), currency.getName());
            if (currencies.size() <= 1) {
                currency.setId(oldCurrency.getId());

                if (!currency.equals(oldCurrency)) {
                    return ResponseEntity.ok(currencyRepository.save(currency).buildEntityModelWithAffordances());

                } else {
                    return ResponseEntity.badRequest().body(new EntityModel<>(new Currency(
                            "Unable to update currency: new currency's properties are identical to old currency's.")));
                }
            } else {
                return ResponseEntity.badRequest().body(new EntityModel<>(new Currency(
                        "Unable to update currency: currency with that name or symbol already exists.")));
            }
        } else {
            return ResponseEntity.badRequest().body(new EntityModel<>(new Currency(
                    "Unable to update currency: couldn't find currency with specified ID.")));
        }
    }

    @Override
    public ResponseEntity<Void> deleteCurrency(Long id) {

        if (!Currency.isDeletable) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Currency currency = currencyRepository.findById(id).orElse(null);
        if (currency == null) {
            return ResponseEntity.notFound().build();
        }

        currencyRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}