package com.example.dataloader;

import com.example.model.Currency;
import com.example.repository.CurrencyRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// Sample data for development
@Component
@AllArgsConstructor
public class DataLoader implements CommandLineRunner {

    private CurrencyRepository currencyRepository;

    @Override
    public void run(String... args) throws Exception {

        Currency usd = new Currency();
        usd.setSymbol("USD");
        usd.setName("US dollar");
        usd.setRate(1.0871);

        Currency sek = new Currency();
        sek.setSymbol("SEK");
        sek.setName("Swedish krona");
        sek.setRate(10.9375);

        Currency jpy = new Currency();
        jpy.setSymbol("JPY");
        jpy.setName("Japanese yen");
        jpy.setRate(118.36);

        currencyRepository.save(usd);
        currencyRepository.save(sek);
        currencyRepository.save(jpy);
    }
}
