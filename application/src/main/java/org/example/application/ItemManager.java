package org.example.application;

import org.example.application.exceptions.ItemAlreadyExistsException;
import org.example.entities.aggregateRoots.Item;
import org.example.exceptions.ItemNotFoundException;
import org.example.repositories.ItemRepository;
import org.example.units.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemManager {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemManager(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public void createItem(String name, UnitType unitType) {
        if (itemRepository.findItemByName(name).isPresent()) {
            throw new ItemAlreadyExistsException(name);
        }
        Item item = new Item(name, unitType);
        itemRepository.save(item);
    }

    public List<String> listItems() {
        return itemRepository.getAll()
                             .stream()
                             .map(Item::getId)
                             .collect(Collectors.toList());
    }

    public Item getItem(String itemName) {
        return itemRepository.findItemByName(itemName)
                             .orElseThrow(() -> new ItemNotFoundException(itemName));
    }

    public void addName(String itemName, String name) {
        Item item = getItem(itemName);
        item.addAlternativeName(name);
        itemRepository.save(item);
    }

    public void removeName(String itemName, String name) {
        Item item = getItem(itemName);
        item.removeAlternativeName(name);
        itemRepository.save(item);
    }

    public void deleteItem(String itemName) {
        itemRepository.delete(getItem(itemName));
    }
}
