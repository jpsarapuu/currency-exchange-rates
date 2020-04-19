package com.example.model;

import com.example.controller.CurrencyController;
import lombok.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@Relation(collectionRelation = "currencies")
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // -----------------------------------------------------------------------------------------------------------------
    // CHANGING ANY OF THE VALUES IN THIS BOX DOESN'T REQUIRE ANY REFACTORING ON FRONT-END
    // Refactoring these 3 values requires minor changes in CurrencyServiceImpl and CurrencyRepository
    private String symbol;
    private String name;
    private double rate;

    // New custom properties created must also be added to propertiesInfo list below in order of preference
    // (They will appear in same order on front-end)
    // No further adjustments needed
//    private String convertibility = " "; // Random example

    // Specify supported operations
    public static boolean isCreatable = true;
    public static boolean isUpdatable = true;
    public static boolean isDeletable = true;

    // Specify values and their order for dynamic HTML creation
    public static List<String> propertiesInfo = Arrays.asList("symbol", "name", "rate");
    // -----------------------------------------------------------------------------------------------------------------

    public EntityModel<Currency> buildEntityModelWithAffordances() {
        EntityModel<Currency> entityModel = new EntityModel<>(this);

        Link self = linkTo(methodOn(CurrencyController.class).getCurrency(id)).withSelfRel();

        if (isUpdatable) {
            self = self.andAffordance(afford(methodOn(CurrencyController.class).updateCurrency(null, id)));
        }

        if (isDeletable) {
            self = self.andAffordance(afford(methodOn(CurrencyController.class).deleteCurrency(id)));
        }

        return entityModel.add(self);
    }

    public static CollectionModel<EntityModel<Currency>> buildCollectionModelWithAffordances(List<EntityModel<Currency>> currencies) {
        CollectionModel<EntityModel<Currency>> collectionModel = new CollectionModel<>(currencies);

        Link self = linkTo(methodOn(CurrencyController.class).getCurrencies()).withSelfRel();
        Link propertiesInfo = linkTo(methodOn(CurrencyController.class).getPropertiesInfo()).withRel("propertiesInfo");

        if (isCreatable) {
            self = self.andAffordance(afford(methodOn(CurrencyController.class).newCurrency(null)));
        }

        return collectionModel.add(self, propertiesInfo);
    }

    // For returning error messages in response body (can be any field)
    public Currency(String errorMessage) {
        this.symbol = errorMessage;
    }
}
