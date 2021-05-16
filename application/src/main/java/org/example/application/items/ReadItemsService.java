package org.example.application.items;

import org.example.entities.aggregateRoots.Item;
import org.example.exceptions.ItemNotFoundException;
import org.example.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
                             .collect(Collectors.toList());
    }

    public Item getItem(String itemName) {
        return itemRepository.findItemByName(itemName)
                             .orElseThrow(() -> new ItemNotFoundException(itemName));
    }
}
