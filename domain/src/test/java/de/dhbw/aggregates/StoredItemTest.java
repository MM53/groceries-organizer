package de.dhbw.aggregates;

import de.dhbw.configuration.TestConfig;
import de.dhbw.entities.ItemLocation;
import de.dhbw.exceptions.ItemLocationAlreadyExistsException;
import de.dhbw.exceptions.ItemNotFoundException;
import de.dhbw.exceptions.UnitMismatchException;
import de.dhbw.repositories.ItemRepository;
import de.dhbw.units.UnitType;
import de.dhbw.units.Volume;
import de.dhbw.units.Weight;
import de.dhbw.valueObjects.Amount;
import de.dhbw.valueObjects.Location;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;


@SpringJUnitConfig(classes = TestConfig.class)
@ActiveProfiles("testing")
class StoredItemTest {

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    public void init() {
        Item item = new Item("Test", UnitType.WEIGHT);
        Mockito.when(itemRepository.findItemById("Test")).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.findItemById(not(eq("Test")))).thenReturn(Optional.empty());
        Mockito.when(itemRepository.getAll()).thenReturn(Arrays.asList(item));
        Mockito.when(itemRepository.checkExistenceById("Test")).thenReturn(true);
    }

    @Test
    public void createStoredItem_success() {
        new StoredItem("Test", null);
    }

    @Test
    public void createStoredItem_itemMissing() {
        assertThrows(ItemNotFoundException.class, () -> new StoredItem("Test2", null));
    }

    @Test
    public void setMinimumAmount_success() {
        StoredItem storedItem = new StoredItem("Test", null);
        Amount amount = new Amount(500, Weight.GRAM);

        storedItem.setMinimumAmount(amount);

        Assertions.assertEquals(amount, storedItem.getMinimumAmount().getAmount());
    }

    @Test
    public void setMinimumAmount_wrongUnit() {
        StoredItem storedItem = new StoredItem("Test", null);
        Amount amount = new Amount(500, Volume.MILLILITER);

        assertThrows(UnitMismatchException.class, () -> storedItem.setMinimumAmount(amount));
    }

    @Test
    public void addItemLocation_success() {
        StoredItem storedItem = new StoredItem("Test", null);
        Amount amount = new Amount(500, Weight.GRAM);
        Location location = new Location("test-location");

        storedItem.addItemLocation(location , amount);

        assertEquals(1, storedItem.getItemLocations().size());
        List<ItemLocation> itemLocations = new ArrayList<>(storedItem.getItemLocations());
        Assertions.assertEquals(amount, itemLocations.get(0).getAmount());
        Assertions.assertEquals(location, itemLocations.get(0).getLocation());
    }

    @Test
    public void addItemLocation_wrongUnit() {
        StoredItem storedItem = new StoredItem("Test", null);
        Amount amount = new Amount(500, Volume.MILLILITER);
        Location location = new Location("test-location");

        assertThrows(UnitMismatchException.class, () -> storedItem.addItemLocation(location , amount));
    }

    @Test
    public void addItemLocation_duplicate() {
        StoredItem storedItem = new StoredItem("Test", null);
        Amount amount = new Amount(500, Weight.GRAM);
        Location location = new Location("test-location");

        storedItem.addItemLocation(location , amount);

        assertThrows(ItemLocationAlreadyExistsException.class, () -> storedItem.addItemLocation(location , amount));
    }

    @Test
    public void updateItemLocation_success() {
        StoredItem storedItem = new StoredItem("Test", null);
        ItemLocation itemLocation = new ItemLocation(new Location("test-location"), new Amount(500, Weight.GRAM));
        Amount amount = new Amount(600, Weight.GRAM);
        storedItem.addItemLocation(itemLocation);

        storedItem.updateItemLocationAmount(itemLocation.getId(), amount);

        Assertions.assertEquals(amount, storedItem.findItemLocation(itemLocation.getId()).get().getAmount());
    }

    @Test
    public void updateItemLocation_wrongUnit() {
        StoredItem storedItem = new StoredItem("Test", null);
        ItemLocation itemLocation = new ItemLocation(new Location("test-location"), new Amount(500, Weight.GRAM));
        Amount amount = new Amount(600, Volume.MILLILITER);
        storedItem.addItemLocation(itemLocation);

        assertThrows(UnitMismatchException.class, () -> storedItem.updateItemLocationAmount(itemLocation.getId(), amount));
    }
}