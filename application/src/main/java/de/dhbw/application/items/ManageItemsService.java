package de.dhbw.application.items;

import de.dhbw.aggregates.Item;
import de.dhbw.application.exceptions.ItemAlreadyExistsException;
import de.dhbw.repositories.ItemRepository;
import de.dhbw.units.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManageItemsService {

    private final ItemRepository itemRepository;
    private final ReadItemsService readItemsService;

    @Autowired
    public ManageItemsService(ItemRepository itemRepository, ReadItemsService readItemsService) {
        this.itemRepository = itemRepository;
        this.readItemsService = readItemsService;
    }

    public void createItem(String name, UnitType unitType) {
        if (itemRepository.findItemByName(name).isPresent()) {
            throw new ItemAlreadyExistsException(name);
        }
        Item item = new Item(name, unitType);
        itemRepository.save(item);
    }

    public void addName(String itemName, String name) {
        Item item = readItemsService.getItem(itemName);
        item.addAlternativeName(name);
        itemRepository.save(item);
    }

    public void removeName(String itemName, String name) {
        Item item = readItemsService.getItem(itemName);
        item.removeAlternativeName(name);
        itemRepository.save(item);
    }

    public void deleteItem(String itemName) {
        Item item = readItemsService.getItem(itemName);
        itemRepository.delete(item);
    }
}
