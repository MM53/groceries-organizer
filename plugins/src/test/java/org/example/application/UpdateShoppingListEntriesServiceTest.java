package org.example.application;

import org.example.application.items.ManageItemsService;
import org.example.application.shoppingList.ManageShoppingListService;
import org.example.application.shoppingList.ReadShoppingListService;
import org.example.application.shoppingList.UpdateShoppingListEntriesService;
import org.example.configuration.H2Connection;
import org.example.configuration.TestConfig;
import org.example.application.exceptions.ShoppingListAlreadyExistsException;
import org.example.application.exceptions.ShoppingListNotFoundException;
import org.example.entities.ShoppingListItem;
import org.example.entities.aggregateRoots.ShoppingList;
import org.example.exceptions.ItemNotFoundException;
import org.example.exceptions.ShoppingListItemNotFoundException;
import org.example.repositories.ShoppingListRepository;
import org.example.units.UnitType;
import org.example.units.Weight;
import org.example.valueObjects.Amount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.example.adapter.persistence.jooq.generated.Tables.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = TestConfig.class)
@ActiveProfiles("testing")
class UpdateShoppingListEntriesServiceTest {

    @Autowired
    private H2Connection jooqConnection;

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    private ManageShoppingListService manageShoppingListService;

    @Autowired
    private UpdateShoppingListEntriesService updateShoppingListEntriesService;

    @Autowired
    private ReadShoppingListService readShoppingListService;

    @Autowired
    private ManageItemsService manageItemsService;

    @AfterEach
    public void cleanup() {
        jooqConnection.getContext().delete(SHOPPING_LIST).execute();
        jooqConnection.getContext().delete(ITEM).execute();
    }

    @Test
    public void createNewShoppingList_success() {
        manageShoppingListService.createShoppingList("Lebensmittel");

        List<ShoppingList> shoppingLists = shoppingListRepository.getAll();

        assertEquals(1, shoppingLists.size());
        assertEquals("Lebensmittel", shoppingLists.get(0).getName());
        assertEquals(0, shoppingLists.get(0).getShoppingListItems().size());
    }

    @Test
    public void createNewShoppingList_duplicate() {
        manageShoppingListService.createShoppingList("Lebensmittel");

        List<ShoppingList> shoppingLists = shoppingListRepository.getAll();

        assertEquals(1, shoppingLists.size());
        assertEquals("Lebensmittel", shoppingLists.get(0).getName());

        assertThrows(ShoppingListAlreadyExistsException.class, () -> {
            manageShoppingListService.createShoppingList("Lebensmittel");
        });
    }

    @Test
    public void addEntry_success() {
        manageItemsService.createItem("Brot", UnitType.WEIGHT);
        manageShoppingListService.createShoppingList("Lebensmittel");

        updateShoppingListEntriesService.addEntry("Lebensmittel", "Brot", new Amount(500, Weight.GRAM));

        ShoppingList shoppingList = readShoppingListService.getShoppingList("Lebensmittel");
        List<ShoppingListItem> items = readShoppingListService.listEntries("Lebensmittel");
        assertEquals(1, items.size());
        assertEquals("Brot", items.get(0).getItemReference());
        assertEquals(new Amount(500, Weight.GRAM), items.get(0).getAmount());
    }

    @Test
    public void addEntry_missingList() {
        manageItemsService.createItem("Brot", UnitType.WEIGHT);

        assertThrows(ShoppingListNotFoundException.class, () -> {
            updateShoppingListEntriesService.addEntry("Lebensmittel", "Brot", new Amount(500, Weight.GRAM));
        });
    }

    @Test
    public void addEntry_missingItem() {
        manageShoppingListService.createShoppingList("Lebensmittel");

        assertThrows(ItemNotFoundException.class, () -> {
            updateShoppingListEntriesService.addEntry("Lebensmittel", "Brot", new Amount(500, Weight.GRAM));
        });
    }

    @Test
    public void removeEntry_success() {
        manageItemsService.createItem("Brot", UnitType.WEIGHT);
        manageShoppingListService.createShoppingList("Lebensmittel");
        updateShoppingListEntriesService.addEntry("Lebensmittel", "Brot", new Amount(500, Weight.GRAM));

        List<ShoppingListItem> items = readShoppingListService.listEntries("Lebensmittel");
        assertEquals(1, items.size());

        updateShoppingListEntriesService.removeEntry("Lebensmittel", "Brot");

        items = readShoppingListService.listEntries("Lebensmittel");
        assertEquals(0, items.size());
    }

    @Test
    public void removeEntry_entryNotExisting() {
        manageItemsService.createItem("Brot", UnitType.WEIGHT);
        manageShoppingListService.createShoppingList("Lebensmittel");

        List<ShoppingListItem> items = readShoppingListService.listEntries("Lebensmittel");
        assertEquals(0, items.size());

        assertThrows(ShoppingListItemNotFoundException.class, () -> {
            updateShoppingListEntriesService.removeEntry("Lebensmittel", "Brot");
        });
        items = readShoppingListService.listEntries("Lebensmittel");
        assertEquals(0, items.size());
    }

    @Test
    public void buyEntry_success() {
        manageItemsService.createItem("Brot", UnitType.WEIGHT);
        manageShoppingListService.createShoppingList("Lebensmittel");

        updateShoppingListEntriesService.addEntry("Lebensmittel", "Brot", new Amount(500, Weight.GRAM));

        List<ShoppingListItem> items = readShoppingListService.listEntries("Lebensmittel");
        assertFalse(items.get(0).isBought());

        updateShoppingListEntriesService.buyEntry("Lebensmittel", "Brot");

        items = readShoppingListService.listEntries("Lebensmittel");
        assertTrue(items.get(0).isBought());
    }

    @Test
    public void buyEntry_entryMissing() {
        manageItemsService.createItem("Brot", UnitType.WEIGHT);
        manageShoppingListService.createShoppingList("Lebensmittel");

        assertThrows(ShoppingListItemNotFoundException.class, () -> {
            updateShoppingListEntriesService.buyEntry("Lebensmittel", "Brot");
        });
    }

    @Test
    public void unbuyEntry_success() {
        manageItemsService.createItem("Brot", UnitType.WEIGHT);
        manageShoppingListService.createShoppingList("Lebensmittel");

        updateShoppingListEntriesService.addEntry("Lebensmittel", "Brot", new Amount(500, Weight.GRAM));

        List<ShoppingListItem> items = readShoppingListService.listEntries("Lebensmittel");
        assertFalse(items.get(0).isBought());

        updateShoppingListEntriesService.buyEntry("Lebensmittel", "Brot");

        items = readShoppingListService.listEntries("Lebensmittel");
        assertTrue(items.get(0).isBought());

        updateShoppingListEntriesService.unbuyEntry("Lebensmittel", "Brot");

        items = readShoppingListService.listEntries("Lebensmittel");
        assertFalse(items.get(0).isBought());
    }

    @Test
    public void updateAmount_success() {
        manageItemsService.createItem("Brot", UnitType.WEIGHT);
        manageShoppingListService.createShoppingList("Lebensmittel");

        updateShoppingListEntriesService.addEntry("Lebensmittel", "Brot", new Amount(500, Weight.GRAM));

        List<ShoppingListItem> items = readShoppingListService.listEntries("Lebensmittel");
        assertEquals(new Amount(500, Weight.GRAM), items.get(0).getAmount());

        updateShoppingListEntriesService.updateAmount("Lebensmittel", "Brot", new Amount(250, Weight.GRAM));

        items = readShoppingListService.listEntries("Lebensmittel");
        assertEquals(new Amount(250, Weight.GRAM), items.get(0).getAmount());
    }

    @Test
    public void updateAmount_entryMissing() {
        manageItemsService.createItem("Brot", UnitType.WEIGHT);
        manageShoppingListService.createShoppingList("Lebensmittel");

        assertThrows(ShoppingListItemNotFoundException.class, () -> {
            updateShoppingListEntriesService.updateAmount("Lebensmittel", "Brot", new Amount(250, Weight.GRAM));
        });
    }
}