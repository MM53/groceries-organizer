package de.dhbw.application.storage.observer;

import de.dhbw.aggregates.StoredItem;
import de.dhbw.application.shoppingList.UpdateShoppingListEntriesService;
import de.dhbw.valueObjects.Amount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CheckMinimumAmountObserver implements ItemLocationObserver {

    private final UpdateShoppingListEntriesService updateShoppingListEntriesService;

    @Autowired
    public CheckMinimumAmountObserver(UpdateShoppingListEntriesService updateShoppingListEntriesService) {
        this.updateShoppingListEntriesService = updateShoppingListEntriesService;
    }

    @Override
    public void onItemLocationAmountChanged(StoredItem storedItem, UUID itemLocationId) {
        if (storedItem.getMinimumAmount() == null) {
            return;
        }
        Amount minimumAmount = storedItem.getMinimumAmount().getAmount();
        Amount totalAmount = storedItem.getTotalAmount();
        if (minimumAmount.isMoreThan(totalAmount)) {
            updateShoppingListEntriesService.addEntry(storedItem.getItemReference(), minimumAmount.sub(totalAmount));
        }
    }
}
