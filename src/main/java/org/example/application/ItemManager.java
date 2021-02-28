package org.example.application;

import org.example.entities.aggregateRoots.Item;
import org.example.exceptions.ItemNotFoundException;
import org.example.repositories.ItemRepository;
import org.example.units.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemManager {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemManager(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public void createItem(String name, UnitType unitType) {
        Item item = new Item(name, unitType);
        itemRepository.save(item);
    }

    public Item viewItem(String itemName) throws ItemNotFoundException {
        return itemRepository.findItemByName(itemName)
                             .orElseThrow(() -> new ItemNotFoundException(itemName));
    }

    public void addName(String itemName, String name) throws ItemNotFoundException {
        Item item = itemRepository.findItemByName(itemName)
                                  .orElseThrow(() -> new ItemNotFoundException(itemName));
        item.addAlternativeName(name);
        itemRepository.save(item);
    }

    public void removeName(String itemName, String name) throws ItemNotFoundException {
        Item item = itemRepository.findItemByName(itemName)
                                  .orElseThrow(() -> new ItemNotFoundException(itemName));
        item.removeAlternativeName(name);
        itemRepository.save(item);
    }

    public void deleteItem(String itemName) throws ItemNotFoundException {
        Item item = itemRepository.findItemByName(itemName)
                                  .orElseThrow(() -> new ItemNotFoundException(itemName));
        itemRepository.delete(item);
    }
}
