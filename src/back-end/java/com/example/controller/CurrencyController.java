package com.example.controller;

import com.example.model.Currency;
import com.example.service.CurrencyService;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("currencies")
@AllArgsConstructor
public class CurrencyController {

    private CurrencyService currencyService;

    @GetMapping("{id}")
    public ResponseEntity<EntityModel<Currency>> getCurrency(@PathVariable Long id) {
        return currencyService.getCurrency(id);
    }

    @GetMapping("")
    public ResponseEntity<CollectionModel<EntityModel<Currency>>> getCurrencies() {
        return currencyService.getCurrencies();
    }

    @PostMapping("")
    public ResponseEntity<EntityModel<Currency>> newCurrency(@RequestBody Currency currency) {
        return currencyService.newCurrency(currency);
    }

    @PutMapping("{id}")
    public ResponseEntity<EntityModel<Currency>> updateCurrency(@RequestBody Currency currency, @PathVariable Long id) {
        return currencyService.updateCurrency(currency, id);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCurrency(@PathVariable Long id) {
        return currencyService.deleteCurrency(id);
    }

    @GetMapping("info")
    public List<String> getPropertiesInfo() {
        return Currency.propertiesInfo;
    }
}
