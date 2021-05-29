package org.example.adapter;

import org.example.application.items.ReadItemsService;
import org.example.valueObjects.Amount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemAmountTupelMapper {

    private final ReadItemsService readItemsService;

    @Autowired
    public ItemAmountTupelMapper(ReadItemsService readItemsService) {
        this.readItemsService = readItemsService;
    }

    public ItemAmountTupel extractFromString(String input) {
        String[] parts = input.split(":");
        String item = parts[0];
        Amount amount;
        if (parts.length > 1) {
            amount = AmountAdapter.ExtractFromString(input.split(":")[1]);
        } else {
            amount = new Amount(0, readItemsService.getItem(item).getUnitType().getBase());
        }
        return new ItemAmountTupel(item, amount);
    }
}
