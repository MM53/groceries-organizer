package de.dhbw.application.storage;

import de.dhbw.aggregates.Item;
import de.dhbw.aggregates.StoredItem;
import de.dhbw.application.items.ManageItemsService;
import de.dhbw.repositories.StoredItemRepository;
import de.dhbw.valueObjects.Amount;
import de.dhbw.valueObjects.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ManageStorageService {

    private final StoredItemRepository storedItemRepository;
    private final ManageItemsService manageItemsService;
    private final ReadStorageService readStorageService;

    @Autowired
    public ManageStorageService(StoredItemRepository storedItemRepository, ManageItemsService manageItemsService, ReadStorageService readStorageService) {
        this.storedItemRepository = storedItemRepository;
        this.manageItemsService = manageItemsService;
        this.readStorageService = readStorageService;
    }

    public void createAndStoreItem(String itemName, Location location, Amount amount) {
        manageItemsService.createItem(itemName, amount.getUnit().getType());
        storeItem(itemName, location, amount);
    }

    public void storeItem(String itemName, Location location, Amount amount) {
        StoredItem storedItem = storedItemRepository.findByReferencedItem(new Item(itemName, null))
                                                    .orElse(new StoredItem(itemName, null));
        storedItem.findItemLocation(location)
                  .ifPresentOrElse(itemLocation -> storedItem.updateItemLocationAmount(itemLocation.getId(), amount),
                                   () -> storedItem.addItemLocation(location, amount));
        storedItemRepository.save(storedItem);
    }

    public void setMinimumAmount(String itemName, Amount amount) {
        StoredItem storedItem = storedItemRepository.findByReferencedItem(new Item(itemName, null))
                                                    .map(item -> {
                                                        item.setMinimumAmount(amount);
                                                        return item;
                                                    })
                                                    .orElse(new StoredItem(itemName, amount));
        storedItemRepository.save(storedItem);
    }

    public void deleteStoredItem(String itemName) {
        storedItemRepository.delete(readStorageService.getStoredItem(itemName));
    }

    public void updateAmount(String itemName, UUID itemLocationId, Amount amount) {
        StoredItem storedItem = readStorageService.getStoredItem(itemName);
        storedItem.updateItemLocationAmount(itemLocationId, amount);
        storedItemRepository.save(storedItem);
    }

    public void removeItemLocation(String itemName, UUID itemLocationId) {
        StoredItem storedItem = readStorageService.getStoredItem(itemName);
        storedItem.removeLocation(itemLocationId);
        storedItemRepository.save(storedItem);
    }
}
