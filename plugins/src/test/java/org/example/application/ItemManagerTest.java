package org.example.application;

import org.example.configuration.TestConfig;
import org.example.application.exceptions.ItemAlreadyExistsException;
import org.example.entities.aggregateRoots.Item;
import org.example.exceptions.ItemNotFoundException;
import org.example.plugins.jooq.configuration.JooqConnection;
import org.example.repositories.ItemRepository;
import org.example.units.UnitType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;

import static org.example.adapter.persistence.jooq.generated.Tables.ITEM;
import static org.junit.jupiter.api.Assertions.*;


@SpringJUnitConfig(classes = TestConfig.class)
@ActiveProfiles("testing")
public class ItemManagerTest {

    @Autowired
    private JooqConnection jooqConnection;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemManager itemManager;

    @AfterEach
    public void cleanup() {
        jooqConnection.getContext().delete(ITEM).execute();
    }

    @Test
    public void createNewItem_success() {
        itemManager.createItem("Brot", UnitType.WEIGHT);

        List<Item> items = itemRepository.getAll();

        assertEquals(1, items.size());
        assertEquals("Brot", items.get(0).getId());
        assertEquals(UnitType.WEIGHT, items.get(0).getUnitType());
    }

    @Test
    public void createNewItem_duplicate() {
        itemManager.createItem("Brot", UnitType.WEIGHT);

        Item item = itemManager.viewItem("Brot");

        assertEquals("Brot", item.getId());
        assertEquals(UnitType.WEIGHT, item.getUnitType());

        assertThrows(ItemAlreadyExistsException.class, () -> {
            itemManager.createItem("Brot", UnitType.WEIGHT);
        });
    }

    @Test
    public void addAlternativeName_success() {
        itemManager.createItem("Brot", UnitType.WEIGHT);
        itemManager.addName("Brot", "Test");

        Optional<Item> item = itemRepository.findItemByName("Test");

        assertTrue(item.isPresent());
        assertEquals("Brot", item.get().getId());
        assertEquals(UnitType.WEIGHT, item.get().getUnitType());
    }

    @Test
    public void addAlternativeName_notFound() {
        assertThrows(ItemNotFoundException.class, () -> {
            itemManager.addName("Brot", "Test");
        });
    }

    @Test
    public void removeAlternativeName_success() {
        itemManager.createItem("Brot", UnitType.WEIGHT);
        itemManager.addName("Brot", "Test");

        Optional<Item> item = itemRepository.findItemByName("Test");

        assertTrue(item.isPresent());
        assertEquals("Brot", item.get().getId());
        assertEquals(UnitType.WEIGHT, item.get().getUnitType());

        itemManager.removeName("Brot", "Test");

        item = itemRepository.findItemByName("Test");

        assertTrue(item.isEmpty());
    }

    @Test
    public void removeAlternativeName_notFound() {
        assertThrows(ItemNotFoundException.class, () -> {
            itemManager.removeName("Brot", "Test");
        });
    }

    @Test
    public void viewItem_success() {
        itemManager.createItem("Brot", UnitType.WEIGHT);

        Item item = itemManager.viewItem("Brot");

        assertEquals("Brot", item.getId());
        assertEquals(UnitType.WEIGHT, item.getUnitType());
    }

    @Test
    public void viewItem_notFound() {
        assertThrows(ItemNotFoundException.class, () -> {
            itemManager.viewItem("Brot");
        });
    }

    @Test
    public void viewListItems_success() {
        itemManager.createItem("Brot", UnitType.WEIGHT);
        itemManager.createItem("Butter", UnitType.WEIGHT);

        List<String> items = itemManager.listItems();

        assertEquals(2, items.size());
        assertTrue(items.contains("Brot"));
        assertTrue(items.contains("Butter"));
    }

    @Test
    public void deleteItem_success() {
        itemManager.createItem("Brot", UnitType.WEIGHT);
        itemManager.createItem("Butter", UnitType.WEIGHT);

        List<String> items = itemManager.listItems();

        assertEquals(2, items.size());
        assertTrue(items.contains("Brot"));
        assertTrue(items.contains("Butter"));

        itemManager.deleteItem("Butter");

        items = itemManager.listItems();

        assertEquals(1, items.size());
        assertTrue(items.contains("Brot"));
        assertFalse(items.contains("Butter"));
    }

    @Test
    public void deleteItem_notFound() {
        assertThrows(ItemNotFoundException.class, () -> {
            itemManager.deleteItem("Butter");
        });
    }
}
