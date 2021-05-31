package de.dhbw.aggregates;

import de.dhbw.entities.ItemName;
import de.dhbw.exceptions.RemoveDefaultNameException;
import de.dhbw.units.UnitType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    public void addName_success() {
        Item item = new Item("Test", UnitType.WEIGHT);

        item.addAlternativeName("Test2");

        assertEquals(2, item.getNames().size());
        assertTrue(item.getNames().contains(new ItemName("Test", "Test")));
        assertTrue(item.getNames().contains(new ItemName("Test2", "Test")));
    }

    @Test
    public void addName_alreadyExists() {
        Item item = new Item("Test", UnitType.WEIGHT);

        item.addAlternativeName("Test2");
        item.addAlternativeName("Test2");

        assertEquals(2, item.getNames().size());
        assertTrue(item.getNames().contains(new ItemName("Test", "Test")));
        assertTrue(item.getNames().contains(new ItemName("Test2", "Test")));
    }

    @Test
    public void addName_wrongReference() {
        Item item = new Item("Test", UnitType.WEIGHT);

        item.addAlternativeName(new ItemName("Test2", "Test2"));

        assertEquals(1, item.getNames().size());
        assertTrue(item.getNames().contains(new ItemName("Test", "Test")));
        assertFalse(item.getNames().contains(new ItemName("Test2", "Test2")));
    }

    @Test
    public void removeName_success() {
        Item item = new Item("Test", UnitType.WEIGHT);
        item.addAlternativeName("Test2");

        item.removeAlternativeName("Test2");

        assertEquals(1, item.getNames().size());
        assertFalse(item.getNames().contains(new ItemName("Test2", "Test")));
    }

    @Test
    public void removeName_defaultName() {
        Item item = new Item("Test", UnitType.WEIGHT);

        assertThrows(RemoveDefaultNameException.class, () ->item.removeAlternativeName("Test"));
    }

}