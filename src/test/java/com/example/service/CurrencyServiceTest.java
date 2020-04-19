package com.example.service;

import com.example.model.Currency;
import com.example.repository.CurrencyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CurrencyServiceTest {

    @Autowired
    CurrencyService currencyService;

    @Autowired
    CurrencyRepository currencyRepository;

    Currency testCurrency;

    @BeforeEach
    void setUp() {
        testCurrency = currencyRepository.save(Currency.builder().symbol("EEK").name("Estonian kroon").rate(15.6).build());
    }

    @AfterEach
    void tearDown() {
        currencyRepository.delete(testCurrency);
    }

    @Test
    void getCurrency() {
        ResponseEntity<EntityModel<Currency>> responseEntity = currencyService.getCurrency(testCurrency.getId());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertNotNull(responseEntity.getBody().getContent());
        assertEquals(testCurrency.getId(), responseEntity.getBody().getContent().getId());
        assertTrue(responseEntity.getBody().getLink("self").isPresent());
    }

    @Test
    void getCurrencies() {
        AtomicLong currencyCount = new AtomicLong();
        currencyRepository.findAll().forEach(currency -> currencyCount.getAndIncrement());

        ResponseEntity<CollectionModel<EntityModel<Currency>>> responseEntity = currencyService.getCurrencies();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertNotNull(responseEntity.getBody().getContent());
        assertEquals(currencyCount.get(), responseEntity.getBody().getContent().size());
        assertTrue(responseEntity.getBody().getLink("self").isPresent());
    }

    @Test
    void newCurrency() {
        Currency currency = Currency.builder().symbol("NOK").name("Norwegian krone").rate(11.4145).build();
        ResponseEntity<EntityModel<Currency>> responseEntity;

        // POST method disabled
        Currency.isCreatable = false;
        responseEntity = currencyService.newCurrency(currency);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());

        // POST method enabled
        Currency.isCreatable = true;
        responseEntity = currencyService.newCurrency(currency);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertNotNull(responseEntity.getBody().getContent());
        assertEquals(currency, responseEntity.getBody().getContent());
        assertTrue(responseEntity.getBody().getLink("self").isPresent());
    }

    @Test
    void updateCurrency() {
        Currency currency = Currency.builder().symbol("EEK").name("Estonian kroon").rate(15.2).build();
        ResponseEntity<EntityModel<Currency>> responseEntity;

        // PUT method disabled
        Currency.isUpdatable = false;
        responseEntity = currencyService.updateCurrency(currency, testCurrency.getId());

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());

        // PUT method enabled
        Currency.isUpdatable = true;
        responseEntity = currencyService.updateCurrency(currency, testCurrency.getId());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertNotNull(responseEntity.getBody().getContent());
        assertNotEquals(testCurrency, responseEntity.getBody().getContent());
        assertTrue(responseEntity.getBody().getLink("self").isPresent());
    }

    @Test
    void deleteCurrency() {
        ResponseEntity<Void> responseEntity;

        // DELETE method disabled
        Currency.isDeletable = false;
        responseEntity = currencyService.deleteCurrency(testCurrency.getId());

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());

        // DELETE method enabled
        Currency.isDeletable = true;
        responseEntity = currencyService.deleteCurrency(testCurrency.getId());

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }
}