package org.example.application;

import org.example.application.items.ManageItemsService;
import org.example.application.items.ReadItemsService;
import org.example.application.storage.ReadStorageService;
import org.example.application.storage.TakeAmountService;
import org.example.application.storage.UpdateStorageService;
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

import static org.example.adapter.persistence.jooq.generated.Tables.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringJUnitConfig(classes = TestConfig.class)
@ActiveProfiles("testing")
public class UpdateStorageServiceTest {

    @Autowired
    private H2Connection jooqConnection;

    @Autowired
    private StoredItemRepository storedItemRepository;

    @Autowired
    private ManageItemsService manageItemsService;

    @Autowired
    private ReadItemsService readItemsService;

    @Autowired
    private UpdateStorageService updateStorageService;

    @Autowired
    private ReadStorageService readStorageService;

    @Autowired
    private TakeAmountService takeAmountService;

    @AfterEach
    public void cleanup() {
        jooqConnection.getContext().delete(STORED_ITEM).execute();
        jooqConnection.getContext().delete(MINIMUM_AMOUNT).execute();
        jooqConnection.getContext().delete(ITEM).execute();
    }

    @Test
    public void storeNewItem_existingItem() {
        manageItemsService.createItem("Brot", UnitType.WEIGHT);
        updateStorageService.storeItem("Brot", new Location("Regal"), new Amount(1, Weight.KILOGRAM));

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
            updateStorageService.storeItem("Brot", new Location("Regal"), new Amount(1, Weight.KILOGRAM));
        });
    }

    @Test
    public void storeNewItem_createNewItem() {
        updateStorageService.createAndStoreItem("Brot", new Location("Regal"), new Amount(1, Weight.KILOGRAM));

        Item item = readItemsService.getItem("Brot");
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
        updateStorageService.createAndStoreItem("Brot", new Location("Regal"), new Amount(1, Weight.KILOGRAM));
        updateStorageService.storeItem("Brot", new Location("Ort"), new Amount(500, Weight.GRAM));

        List<StoredItem> storedItems = storedItemRepository.getAll();
        assertEquals(1, storedItems.size());
        assertEquals("Brot", storedItems.get(0).getItemReference());
        assertNull(storedItems.get(0).getMinimumAmount());
        assertEquals(2, storedItems.get(0).getItemLocations().size());
        assertEquals(new Amount(1.5, Weight.KILOGRAM), storedItems.get(0).getTotalAmount());
    }

    @Test
    public void setMinimumAmount_existingItem() {
        manageItemsService.createItem("Brot", UnitType.WEIGHT);
        updateStorageService.setMinimumAmount("Brot", new Amount(500, Weight.GRAM));

        List<StoredItem> storedItems = storedItemRepository.getAll();
        assertEquals(1, storedItems.size());
        assertEquals("Brot", storedItems.get(0).getItemReference());
        assertEquals(new Amount(500, Weight.GRAM), storedItems.get(0).getMinimumAmount().getAmount());
        assertEquals(0, storedItems.get(0).getItemLocations().size());
        assertEquals(new Amount(0, Weight.GRAM), storedItems.get(0).getTotalAmount());
    }

    @Test
    public void setMinimumAmount_updateStoredItem() {
        updateStorageService.createAndStoreItem("Brot", new Location("Regal"), new Amount(1, Weight.KILOGRAM));
        updateStorageService.setMinimumAmount("Brot", new Amount(500, Weight.GRAM));

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
            updateStorageService.setMinimumAmount("Brot", new Amount(500, Weight.GRAM));
        });
    }

    @Test
    public void viewStoredItem_success() {
        updateStorageService.createAndStoreItem("Brot", new Location("Regal"), new Amount(1, Weight.KILOGRAM));

        StoredItem storedItem = readStorageService.getStoredItem("Brot");

        assertEquals("Brot", storedItem.getItemReference());
        assertNull(storedItem.getMinimumAmount());
        assertEquals(1, storedItem.getItemLocations().size());
        assertEquals(new Amount(1, Weight.KILOGRAM), storedItem.getTotalAmount());
    }

    @Test
    public void viewStoredItem_notFound() {
        assertThrows(StoredItemNotFoundException.class, () -> {
            readStorageService.getStoredItem("Brot");
        });
    }

    @Test
    public void viewItemLocationsList_success() {
        updateStorageService.createAndStoreItem("Brot", new Location("Regal"), new Amount(1, Weight.KILOGRAM));
        updateStorageService.storeItem("Brot", new Location("Ort"), new Amount(500, Weight.GRAM));

        List<ItemLocation> itemLocations = readStorageService.listItemLocations("Brot");
        List<Location> locations = itemLocations.stream().map(ItemLocation::getLocation).toList();

        assertEquals(2, itemLocations.size());
        assertTrue(locations.contains(new Location("Regal")));
        assertTrue(locations.contains(new Location("Ort")));
    }

    @Test
    public void viewItemLocationsList_storedItemMissing() {
        assertThrows(StoredItemNotFoundException.class, () -> {
            readStorageService.listItemLocations("Brot");
        });
    }

    @Test
    public void takeFromStoredItem_success() {
        updateStorageService.createAndStoreItem("Brot", new Location("Regal"), new Amount(1, Weight.KILOGRAM));

        Amount requiredLeft = takeAmountService.takeAmount("Brot", new Amount(200, Weight.GRAM), readStorageService.listItemLocations("Brot").get(0).getId());

        StoredItem storedItem = readStorageService.getStoredItem("Brot");
        List<ItemLocation> itemLocations = readStorageService.listItemLocations("Brot");

        assertEquals(new Amount(0, Weight.GRAM), requiredLeft);
        assertEquals(new Amount(800, Weight.GRAM), storedItem.getTotalAmount());
        assertEquals(new Amount(800, Weight.GRAM), itemLocations.get(0).getAmount());
    }

    @Test
    public void takeFromStoredItem_notEnough() {
        updateStorageService.createAndStoreItem("Brot", new Location("Regal"), new Amount(1, Weight.KILOGRAM));

        Amount requiredLeft = takeAmountService.takeAmount("Brot", new Amount(2, Weight.KILOGRAM), readStorageService.listItemLocations("Brot").get(0).getId());

        StoredItem storedItem = readStorageService.getStoredItem("Brot");
        List<ItemLocation> itemLocations = readStorageService.listItemLocations("Brot");
        assertEquals(new Amount(1, Weight.KILOGRAM), requiredLeft);
        assertEquals(new Amount(0, Weight.GRAM), storedItem.getTotalAmount());
        assertEquals(0, itemLocations.size());
    }

    @Test
    public void takeFromStoredItem_multipleItemLocations() {
        updateStorageService.createAndStoreItem("Brot", new Location("Regal"), new Amount(1, Weight.KILOGRAM));
        updateStorageService.storeItem("Brot", new Location("Ort"), new Amount(1, Weight.KILOGRAM));
        ItemLocation firstItemLocation = readStorageService.listItemLocations("Brot").get(0);
        ItemLocation secondItemLocation = readStorageService.listItemLocations("Brot").get(1);

        Amount requiredLeft = takeAmountService.takeAmount("Brot", new Amount(1.2, Weight.KILOGRAM), firstItemLocation.getId());
        requiredLeft = takeAmountService.takeAmount("Brot", requiredLeft, secondItemLocation.getId());

        StoredItem storedItem = readStorageService.getStoredItem("Brot");
        List<ItemLocation> itemLocations = readStorageService.listItemLocations("Brot");
        assertEquals(new Amount(0, Weight.GRAM), requiredLeft);
        assertEquals(new Amount(800, Weight.GRAM), storedItem.getTotalAmount());
        assertEquals(1, itemLocations.size());
        assertEquals(secondItemLocation, itemLocations.get(0));
        assertEquals(new Amount(800, Weight.GRAM), itemLocations.get(0).getAmount());
    }

    @Test
    public void takeFromStoredItem_storedItemMissing() {
        assertThrows(StoredItemNotFoundException.class, () -> {
            takeAmountService.takeAmount("Brot", new Amount(200, Weight.GRAM), readStorageService.listItemLocations("Brot").get(0).getId());
        });
    }

    @Test
    public void deleteStoredItem_success() {
        updateStorageService.createAndStoreItem("Brot", new Location("Regal"), new Amount(1, Weight.KILOGRAM));

        StoredItem storedItem = readStorageService.getStoredItem("Brot");
        assertEquals("Brot", storedItem.getItemReference());

        updateStorageService.deleteStoredItem("Brot");

        assertThrows(StoredItemNotFoundException.class, () -> {
            readStorageService.getStoredItem("Brot");
        });
    }

    @Test
    public void deleteStoredItem_storedItemMissing() {
        assertThrows(StoredItemNotFoundException.class, () -> {
            updateStorageService.deleteStoredItem("Brot");
        });
    }
}
