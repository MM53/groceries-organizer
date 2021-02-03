package org.example;

import org.example.entities.Item;
import org.example.entities.ItemLocation;
import org.example.entities.ItemNameAlternative;
import org.example.repositories.ItemLocationRepository;
import org.example.repositories.ItemNameRepository;
import org.example.repositories.ItemRepository;
import org.example.units.Pieces;
import org.example.units.UnitTypes;
import org.example.units.Weight;
import org.example.valueObjects.Amount;
import org.example.valueObjects.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class Application {

    @Autowired
    public ItemRepository itemRepository;

    @Autowired
    public ItemNameRepository itemNameRepository;

    @Autowired
    public ItemLocationRepository itemLocationRepository;

    public static void main(String[] args) {

        var context = new AnnotationConfigApplicationContext();
        context.scan("org.example");
        context.refresh();

        var bean = context.getBean(Application.class);
        bean.run();

        context.close();
    }

    public void run() {
//        itemLocationRepository.save(new ItemLocation(new Location("Küche", "Kühlschrank", "Fach 3"), new Amount(500, Weight.GRAM)));
//        itemLocationRepository.save(new ItemLocation(new Location("Küche", "Kühlschrank", "Fach 2"), new Amount(0.5, Weight.KILOGRAM)));
//        itemLocationRepository.save(new ItemLocation(new Location("Küche", "Kühlschrank", "Fach 1"), new Amount(5, Pieces.PIECES)));
//        itemLocationRepository.getAll();

        itemRepository.save(new Item("Brot", Collections.emptyList(), UnitTypes.WEIGHT));
        itemNameRepository.save(new ItemNameAlternative("Foo", "Brot"));

        itemRepository.getAll();
    }
}
