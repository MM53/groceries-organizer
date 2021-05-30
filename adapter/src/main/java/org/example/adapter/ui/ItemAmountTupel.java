package org.example.adapter.ui;

import org.example.valueObjects.Amount;

public class ItemAmountTupel {

    private final String item;
    private final Amount amount;


    public ItemAmountTupel(String item, Amount amount) {
        this.item = item;
        this.amount = amount;
    }

    public String getItem() {
        return item;
    }

    public Amount getAmount() {
        return amount;
    }
}
