package org.example.adapter.ui;

import org.example.aggregates.StoredItem;
import org.example.valueObjects.Amount;
import org.json.JSONObject;

public class StoredItemWeb {
    private final String itemName;
    private final Amount totalAmount;
    private final Amount minimumAmount;
    private final int locationCount;
    private final String locationsJson;

    public StoredItemWeb(StoredItem storedItem) {
        this.itemName = storedItem.getItemReference();
        this.totalAmount = storedItem.getTotalAmount();
        this.minimumAmount = storedItem.getMinimumAmount() != null ? storedItem.getMinimumAmount().getAmount() : null;
        this.locationCount = storedItem.getItemLocations().size();
        JSONObject locationsJson = new JSONObject();
        storedItem.getItemLocations().forEach(itemLocation -> {
            locationsJson.put(itemLocation.getId().toString(), new JSONObject().put("amount", itemLocation.getAmount().toString())
                                                                               .put("name", itemLocation.getLocation().getLocation()));
        });
        this.locationsJson = locationsJson.toString();
    }

    public String getItemName() {
        return itemName;
    }

    public String getAmountText() {
        if (minimumAmount != null) {
            return totalAmount.toString() + "/" + minimumAmount.toString();
        } else {
            return totalAmount.toString();
        }
    }

    public int getLocationCount() {
        return locationCount;
    }

    public String getLocationsJson() {
        return locationsJson;
    }
}
