package org.example.application;

import org.example.application.items.ManageItemsService;
import org.example.application.items.ReadItemsService;
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
public class ManageItemsServiceTest {

    @Autowired
    private JooqConnection jooqConnection;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ManageItemsService manageItemsService;

    @Autowired
    private ReadItemsService readItemsService;

    @AfterEach
    public void cleanup() {
        jooqConnection.getContext().delete(ITEM).execute();
    }

    @Test
    public void createNewItem_success() {
        manageItemsService.createItem("Brot", UnitType.WEIGHT);

        List<Item> items = itemRepository.getAll();

        assertEquals(1, items.size());
        assertEquals("Brot", items.get(0).getId());
        assertEquals(UnitType.WEIGHT, items.get(0).getUnitType());
    }

    @Test
    public void createNewItem_duplicate() {
        manageItemsService.createItem("Brot", UnitType.WEIGHT);

        Item item = readItemsService.getItem("Brot");

        assertEquals("Brot", item.getId());
        assertEquals(UnitType.WEIGHT, item.getUnitType());

        assertThrows(ItemAlreadyExistsException.class, () -> {
            manageItemsService.createItem("Brot", UnitType.WEIGHT);
        });
    }

    @Test
    public void addAlternativeName_success() {
        manageItemsService.createItem("Brot", UnitType.WEIGHT);
        manageItemsService.addName("Brot", "Test");

        Optional<Item> item = itemRepository.findItemByName("Test");

        assertTrue(item.isPresent());
        assertEquals("Brot", item.get().getId());
        assertEquals(UnitType.WEIGHT, item.get().getUnitType());
    }

    @Test
    public void addAlternativeName_notFound() {
        assertThrows(ItemNotFoundException.class, () -> {
            manageItemsService.addName("Brot", "Test");
        });
    }

    @Test
    public void removeAlternativeName_success() {
        manageItemsService.createItem("Brot", UnitType.WEIGHT);
        manageItemsService.addName("Brot", "Test");

        Optional<Item> item = itemRepository.findItemByName("Test");

        assertTrue(item.isPresent());
        assertEquals("Brot", item.get().getId());
        assertEquals(UnitType.WEIGHT, item.get().getUnitType());

        manageItemsService.removeName("Brot", "Test");

        item = itemRepository.findItemByName("Test");

        assertTrue(item.isEmpty());
    }

    @Test
    public void removeAlternativeName_notFound() {
        assertThrows(ItemNotFoundException.class, () -> {
            manageItemsService.removeName("Brot", "Test");
        });
    }

    @Test
    public void viewItem_success() {
        manageItemsService.createItem("Brot", UnitType.WEIGHT);

        Item item = readItemsService.getItem("Brot");

        assertEquals("Brot", item.getId());
        assertEquals(UnitType.WEIGHT, item.getUnitType());
    }

    @Test
    public void viewItem_notFound() {
        assertThrows(ItemNotFoundException.class, () -> {
            readItemsService.getItem("Brot");
        });
    }

    @Test
    public void viewListItems_success() {
        manageItemsService.createItem("Brot", UnitType.WEIGHT);
        manageItemsService.createItem("Butter", UnitType.WEIGHT);

        List<String> items = readItemsService.listItems();

        assertEquals(2, items.size());
        assertTrue(items.contains("Brot"));
        assertTrue(items.contains("Butter"));
    }

    @Test
    public void deleteItem_success() {
        manageItemsService.createItem("Brot", UnitType.WEIGHT);
        manageItemsService.createItem("Butter", UnitType.WEIGHT);

        List<String> items = readItemsService.listItems();

        assertEquals(2, items.size());
        assertTrue(items.contains("Brot"));
        assertTrue(items.contains("Butter"));

        manageItemsService.deleteItem("Butter");

        items = readItemsService.listItems();

        assertEquals(1, items.size());
        assertTrue(items.contains("Brot"));
        assertFalse(items.contains("Butter"));
    }

    @Test
    public void deleteItem_notFound() {
        assertThrows(ItemNotFoundException.class, () -> {
            manageItemsService.deleteItem("Butter");
        });
    }
}
