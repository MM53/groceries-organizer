package de.dhbw.application.storage;

import de.dhbw.aggregates.Item;
import de.dhbw.aggregates.StoredItem;
import de.dhbw.application.exceptions.StoredItemNotFoundException;
import de.dhbw.entities.ItemLocation;
import de.dhbw.repositories.StoredItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReadStorageService {

    private final StoredItemRepository storedItemRepository;

    @Autowired
    public ReadStorageService(StoredItemRepository storedItemRepository) {
        this.storedItemRepository = storedItemRepository;
    }

    public List<StoredItem> listStoredItems() {
        return storedItemRepository.getAll();
    }

    public StoredItem getStoredItem(String itemName) {
        return storedItemRepository.findByReferencedItem(new Item(itemName, null))
                                   .orElseThrow(() -> new StoredItemNotFoundException(itemName));
    }

    public List<ItemLocation> listItemLocations(String itemName) {
        return new ArrayList<>(getStoredItem(itemName).getItemLocations());
    }
}
