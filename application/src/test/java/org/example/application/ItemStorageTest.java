package org.example.application;

import org.example.configuration.H2Connection;
import org.example.configuration.TestConfig;
import org.example.entities.ItemLocation;
import org.example.entities.aggregateRoots.Item;
import org.example.entities.aggregateRoots.StoredItem;
import org.example.exceptions.ItemNotFoundException;
import org.example.application.exceptions.StoredItemNotFoundException;
import org.example.repositories.StoredItemRepository;
import org.example.units.UnitType;
import org.example.units.Weight;
import org.example.valueObjects.Amount;
import org.example.valueObjects.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.stream.Collectors;

import static org.example.persistence.jooq.generated.Tables.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringJUnitConfig(classes = TestConfig.class)
@ActiveProfiles("testing")
public class ItemStorageTest {

    @Autowired
    private H2Connection jooqConnection;

    @Autowired
    private StoredItemRepository storedItemRepository;

    @Autowired
    private ItemManager itemManager;

    @Autowired
    private ItemStorage itemStorage;

    @AfterEach
    public void cleanup() {
        jooqConnection.getContext().delete(STORED_ITEM).execute();
        jooqConnection.getContext().delete(MINIMUM_AMOUNT).execute();
        jooqConnection.getContext().delete(ITEM).execute();
    }

    @Test
    public void storeNewItem_existingItem() {
        itemManager.createItem("Brot", UnitType.WEIGHT);
        itemStorage.storeItem("Brot", new Location("Regal"), new Amount(1, Weight.KILOGRAM));

        List<StoredItem> storedItems = storedItemRepository.getAll();
        assertEquals(1, storedItems.size());
        assertEquals("Brot", storedItems.get(0).getItemReference());
        assertNull(storedItems.get(0).getMinimumAmount());
        assertEquals(1, storedItems.get(0).getItemLocations().size());
        assertEquals(new Amount(1, Weight.KILOGRAM), storedItems.get(0).getTotalAmount());
    }

    @Test
    public void storeNewItem_itemMissing() {
        assertThrows(ItemNotFoundException.class, () -> {
            itemStorage.storeItem("Brot", new Location("Regal"), new Amount(1, Weight.KILOGRAM));
        });
    }

    @Test
    public void storeNewItem_createNewItem() {
        itemStorage.createAndStoreItem("Brot", new Location("Regal"), new Amount(1, Weight.KILOGRAM));

        Item item = itemManager.viewItem("Brot");
        List<StoredItem> storedItems = storedItemRepository.getAll();

        assertEquals("Brot", item.getId());
        assertEquals(UnitType.WEIGHT, item.getUnitType());

        assertEquals(1, storedItems.size());
        assertEquals("Brot", storedItems.get(0).getItemReference());
        assertNull(storedItems.get(0).getMinimumAmount());
        assertEquals(1, storedItems.get(0).getItemLocations().size());
        assertEquals(new Amount(1, Weight.KILOGRAM), storedItems.get(0).getTotalAmount());
    }

    @Test
    public void storeItem_updateStoredItem() {
        itemStorage.createAndStoreItem("Brot", new Location("Regal"), new Amount(1, Weight.KILOGRAM));
        itemStorage.storeItem("Brot", new Location("Ort"), new Amount(500, Weight.GRAM));

        List<StoredItem> storedItems = storedItemRepository.getAll();
        assertEquals(1, storedItems.size());
        assertEquals("Brot", storedItems.get(0).getItemReference());
        assertNull(storedItems.get(0).getMinimumAmount());
        assertEquals(2, storedItems.get(0).getItemLocations().size());
        assertEquals(new Amount(1.5, Weight.KILOGRAM), storedItems.get(0).getTotalAmount());
    }

    @Test
    public void setMinimumAmount_existingItem() {
        itemManager.createItem("Brot", UnitType.WEIGHT);
        itemStorage.setMinimumAmount("Brot", new Amount(500, Weight.GRAM));

        List<StoredItem> storedItems = storedItemRepository.getAll();
        assertEquals(1, storedItems.size());
        assertEquals("Brot", storedItems.get(0).getItemReference());
        assertEquals(new Amount(500, Weight.GRAM), storedItems.get(0).getMinimumAmount().getAmount());
        assertEquals(0, storedItems.get(0).getItemLocations().size());
        assertEquals(new Amount(0, Weight.GRAM), storedItems.get(0).getTotalAmount());
    }

    @Test
    public void setMinimumAmount_updateStoredItem() {
        itemStorage.createAndStoreItem("Brot", new Location("Regal"), new Amount(1, Weight.KILOGRAM));
        itemStorage.setMinimumAmount("Brot", new Amount(500, Weight.GRAM));

        List<StoredItem> storedItems = storedItemRepository.getAll();
        assertEquals(1, storedItems.size());
        assertEquals("Brot", storedItems.get(0).getItemReference());
        assertEquals(new Amount(500, Weight.GRAM), storedItems.get(0).getMinimumAmount().getAmount());
        assertEquals(1, storedItems.get(0).getItemLocations().size());
        assertEquals(new Amount(1, Weight.KILOGRAM), storedItems.get(0).getTotalAmount());
    }

    @Test
    public void setMinimumAmount_itemMissing() {
        assertThrows(ItemNotFoundException.class, () -> {
            itemStorage.setMinimumAmount("Brot", new Amount(500, Weight.GRAM));
        });
    }

    @Test
    public void viewStoredItem_success() {
        itemStorage.createAndStoreItem("Brot", new Location("Regal"), new Amount(1, Weight.KILOGRAM));

        StoredItem storedItem = itemStorage.viewStoredItem("Brot");

        assertEquals("Brot", storedItem.getItemReference());
        assertNull(storedItem.getMinimumAmount());
        assertEquals(1, storedItem.getItemLocations().size());
        assertEquals(new Amount(1, Weight.KILOGRAM), storedItem.getTotalAmount());
    }

    @Test
    public void viewStoredItem_notFound() {
        assertThrows(StoredItemNotFoundException.class, () -> {
            itemStorage.viewStoredItem("Brot");
        });
    }

    @Test
    public void viewItemLocationsList_success() {
        itemStorage.createAndStoreItem("Brot", new Location("Regal"), new Amount(1, Weight.KILOGRAM));
        itemStorage.storeItem("Brot", new Location("Ort"), new Amount(500, Weight.GRAM));

        List<ItemLocation> itemLocations = itemStorage.listItemLocations("Brot");
        List<Location> locations = itemLocations.stream().map(ItemLocation::getLocation).collect(Collectors.toList());

        assertEquals(2, itemLocations.size());
        assertTrue(locations.contains(new Location("Regal")));
        assertTrue(locations.contains(new Location("Ort")));
    }

    @Test
    public void viewItemLocationsList_storedItemMissing() {
        assertThrows(StoredItemNotFoundException.class, () -> {
            itemStorage.listItemLocations("Brot");
        });
    }

    @Test
    public void takeFromStoredItem_success() {
        itemStorage.createAndStoreItem("Brot", new Location("Regal"), new Amount(1, Weight.KILOGRAM));

        Amount requiredLeft = itemStorage.takeAmount("Brot", itemStorage.listItemLocations("Brot").get(0), new Amount(200, Weight.GRAM));

        StoredItem storedItem = itemStorage.viewStoredItem("Brot");
        List<ItemLocation> itemLocations = itemStorage.listItemLocations("Brot");

        assertEquals(new Amount(0, Weight.GRAM), requiredLeft);
        assertEquals(new Amount(800, Weight.GRAM), storedItem.getTotalAmount());
        assertEquals(new Amount(800, Weight.GRAM), itemLocations.get(0).getAmount());
    }

    @Test
    public void takeFromStoredItem_notEnough() {
        itemStorage.createAndStoreItem("Brot", new Location("Regal"), new Amount(1, Weight.KILOGRAM));

        Amount requiredLeft = itemStorage.takeAmount("Brot", itemStorage.listItemLocations("Brot").get(0), new Amount(2, Weight.KILOGRAM));

        StoredItem storedItem = itemStorage.viewStoredItem("Brot");
        List<ItemLocation> itemLocations = itemStorage.listItemLocations("Brot");
        assertEquals(new Amount(1, Weight.KILOGRAM), requiredLeft);
        assertEquals(new Amount(0, Weight.GRAM), storedItem.getTotalAmount());
        assertEquals(0, itemLocations.size());
    }

    @Test
    public void takeFromStoredItem_multipleItemLocations() {
        itemStorage.createAndStoreItem("Brot", new Location("Regal"), new Amount(1, Weight.KILOGRAM));
        itemStorage.storeItem("Brot", new Location("Ort"), new Amount(1, Weight.KILOGRAM));
        ItemLocation firstItemLocation = itemStorage.listItemLocations("Brot").get(0);
        ItemLocation secondItemLocation = itemStorage.listItemLocations("Brot").get(1);

        Amount requiredLeft = itemStorage.takeAmount("Brot", firstItemLocation, new Amount(1.2, Weight.KILOGRAM));
        requiredLeft = itemStorage.takeAmount("Brot", secondItemLocation, requiredLeft);

        StoredItem storedItem = itemStorage.viewStoredItem("Brot");
        List<ItemLocation> itemLocations = itemStorage.listItemLocations("Brot");
        assertEquals(new Amount(0, Weight.GRAM), requiredLeft);
        assertEquals(new Amount(800, Weight.GRAM), storedItem.getTotalAmount());
        assertEquals(1, itemLocations.size());
        assertEquals(secondItemLocation, itemLocations.get(0));
        assertEquals(new Amount(800, Weight.GRAM), itemLocations.get(0).getAmount());
    }

    @Test
    public void takeFromStoredItem_storedItemMissing() {
        assertThrows(StoredItemNotFoundException.class, () -> {
            itemStorage.takeAmount("Brot", itemStorage.listItemLocations("Brot").get(0), new Amount(200, Weight.GRAM));
        });
    }

    @Test
    public void deleteStoredItem_success() {
        itemStorage.createAndStoreItem("Brot", new Location("Regal"), new Amount(1, Weight.KILOGRAM));

        StoredItem storedItem = itemStorage.viewStoredItem("Brot");
        assertEquals("Brot", storedItem.getItemReference());

        itemStorage.deleteStoredItem("Brot");

        assertThrows(StoredItemNotFoundException.class, () -> {
            itemStorage.viewStoredItem("Brot");
        });
    }

    @Test
    public void deleteStoredItem_storedItemMissing() {
        assertThrows(StoredItemNotFoundException.class, () -> {
            itemStorage.deleteStoredItem("Brot");
        });
    }
}
