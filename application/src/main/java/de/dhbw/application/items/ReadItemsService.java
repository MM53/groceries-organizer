package de.dhbw.application.items;

import de.dhbw.aggregates.Item;
import de.dhbw.exceptions.ItemNotFoundException;
import de.dhbw.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReadItemsService {

    private final ItemRepository itemRepository;

    @Autowired
    public ReadItemsService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<String> listItems() {
        return itemRepository.getAll()
                             .stream()
                             .map(Item::getId)
                             .toList();
    }

    public Item getItem(String itemName) {
        return itemRepository.findItemByName(itemName)
                             .orElseThrow(() -> new ItemNotFoundException(itemName));
    }
}
