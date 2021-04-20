package org.example.application;

import org.example.application.exceptions.ItemAlreadyExistsException;
import org.example.application.exceptions.ShoppingListAlreadyExistsException;
import org.example.application.exceptions.ShoppingListNotFoundException;
import org.example.configuration.H2Connection;
import org.example.configuration.TestConfig;
import org.example.entities.ShoppingListItem;
import org.example.entities.aggregateRoots.Item;
import org.example.entities.aggregateRoots.ShoppingList;
import org.example.exceptions.ItemNotFoundException;
import org.example.exceptions.ShoppingListItemNotFoundException;
import org.example.repositories.ShoppingListRepository;
import org.example.repositories.StoredItemRepository;
import org.example.units.UnitType;
import org.example.units.Volume;
import org.example.units.Weight;
import org.example.valueObjects.Amount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.example.persistence.jooq.generated.Tables.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = TestConfig.class)
@ActiveProfiles("testing")
class ShoppingListManagerTest {

    @Autowired
    private H2Connection jooqConnection;

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    private ShoppingListManager shoppingListManager;

    @Autowired
    private ItemManager itemManager;

    @AfterEach
    public void cleanup() {
        jooqConnection.getContext().delete(SHOPPING_LIST).execute();
        jooqConnection.getContext().delete(ITEM).execute();
    }

    @Test
    public void createNewShoppingList_success() {
        shoppingListManager.createShoppingList("Lebensmittel");

        List<ShoppingList> shoppingLists = shoppingListRepository.getAll();

        assertEquals(1, shoppingLists.size());
        assertEquals("Lebensmittel", shoppingLists.get(0).getName());
        assertEquals(0, shoppingLists.get(0).getShoppingListItems().size());
    }

    @Test
    public void createNewShoppingList_duplicate() {
        shoppingListManager.createShoppingList("Lebensmittel");

        List<ShoppingList> shoppingLists = shoppingListRepository.getAll();

        assertEquals(1, shoppingLists.size());
        assertEquals("Lebensmittel", shoppingLists.get(0).getName());

        assertThrows(ShoppingListAlreadyExistsException.class, () -> {
            shoppingListManager.createShoppingList("Lebensmittel");
        });
    }

    @Test
    public void addEntry_success() {
        itemManager.createItem("Brot", UnitType.WEIGHT);
        shoppingListManager.createShoppingList("Lebensmittel");

        shoppingListManager.addEntry("Lebensmittel", "Brot", new Amount(500, Weight.GRAM));

        ShoppingList shoppingList = shoppingListManager.viewShoppingList("Lebensmittel");
        List<ShoppingListItem> items = shoppingListManager.listEntries("Lebensmittel");
        assertEquals(1, items.size());
        assertEquals("Brot", items.get(0).getItemReference());
        assertEquals(new Amount(500, Weight.GRAM), items.get(0).getAmount());
    }

    @Test
    public void addEntry_missingList() {
        itemManager.createItem("Brot", UnitType.WEIGHT);

        assertThrows(ShoppingListNotFoundException.class, () -> {
            shoppingListManager.addEntry("Lebensmittel", "Brot", new Amount(500, Weight.GRAM));
        });
    }

    @Test
    public void addEntry_missingItem() {
        shoppingListManager.createShoppingList("Lebensmittel");

        assertThrows(ItemNotFoundException.class, () -> {
            shoppingListManager.addEntry("Lebensmittel", "Brot", new Amount(500, Weight.GRAM));
        });
    }

    @Test
    public void removeEntry_success() {
        itemManager.createItem("Brot", UnitType.WEIGHT);
        shoppingListManager.createShoppingList("Lebensmittel");
        shoppingListManager.addEntry("Lebensmittel", "Brot", new Amount(500, Weight.GRAM));

        List<ShoppingListItem> items = shoppingListManager.listEntries("Lebensmittel");
        assertEquals(1, items.size());

        shoppingListManager.removeEntry("Lebensmittel", "Brot");

        items = shoppingListManager.listEntries("Lebensmittel");
        assertEquals(0, items.size());
    }

    @Test
    public void removeEntry_entryNotExisting() {
        itemManager.createItem("Brot", UnitType.WEIGHT);
        shoppingListManager.createShoppingList("Lebensmittel");

        List<ShoppingListItem> items = shoppingListManager.listEntries("Lebensmittel");
        assertEquals(0, items.size());

        assertThrows(ShoppingListItemNotFoundException.class, () -> {
            shoppingListManager.removeEntry("Lebensmittel", "Brot");
        });
        items = shoppingListManager.listEntries("Lebensmittel");
        assertEquals(0, items.size());
    }

    @Test
    public void buyEntry_success() {
        itemManager.createItem("Brot", UnitType.WEIGHT);
        shoppingListManager.createShoppingList("Lebensmittel");

        shoppingListManager.addEntry("Lebensmittel", "Brot", new Amount(500, Weight.GRAM));

        List<ShoppingListItem> items = shoppingListManager.listEntries("Lebensmittel");
        assertFalse(items.get(0).isBought());

        shoppingListManager.buyEntry("Lebensmittel", "Brot");

        items = shoppingListManager.listEntries("Lebensmittel");
        assertTrue(items.get(0).isBought());
    }

    @Test
    public void buyEntry_entryMissing() {
        itemManager.createItem("Brot", UnitType.WEIGHT);
        shoppingListManager.createShoppingList("Lebensmittel");

        assertThrows(ShoppingListItemNotFoundException.class, () -> {
            shoppingListManager.buyEntry("Lebensmittel", "Brot");
        });
    }

    @Test
    public void unbuyEntry_success() {
        itemManager.createItem("Brot", UnitType.WEIGHT);
        shoppingListManager.createShoppingList("Lebensmittel");

        shoppingListManager.addEntry("Lebensmittel", "Brot", new Amount(500, Weight.GRAM));

        List<ShoppingListItem> items = shoppingListManager.listEntries("Lebensmittel");
        assertFalse(items.get(0).isBought());

        shoppingListManager.buyEntry("Lebensmittel", "Brot");

        items = shoppingListManager.listEntries("Lebensmittel");
        assertTrue(items.get(0).isBought());

        shoppingListManager.unbuyEntry("Lebensmittel", "Brot");

        items = shoppingListManager.listEntries("Lebensmittel");
        assertFalse(items.get(0).isBought());
    }

    @Test
    public void updateAmount_success() {
        itemManager.createItem("Brot", UnitType.WEIGHT);
        shoppingListManager.createShoppingList("Lebensmittel");

        shoppingListManager.addEntry("Lebensmittel", "Brot", new Amount(500, Weight.GRAM));

        List<ShoppingListItem> items = shoppingListManager.listEntries("Lebensmittel");
        assertEquals(new Amount(500, Weight.GRAM), items.get(0).getAmount());

        shoppingListManager.updateAmount("Lebensmittel", "Brot", new Amount(250, Weight.GRAM));

        items = shoppingListManager.listEntries("Lebensmittel");
        assertEquals(new Amount(250, Weight.GRAM), items.get(0).getAmount());
    }

    @Test
    public void updateAmount_entryMissing() {
        itemManager.createItem("Brot", UnitType.WEIGHT);
        shoppingListManager.createShoppingList("Lebensmittel");

        assertThrows(ShoppingListItemNotFoundException.class, () -> {
            shoppingListManager.updateAmount("Lebensmittel", "Brot", new Amount(250, Weight.GRAM));
        });
    }
}