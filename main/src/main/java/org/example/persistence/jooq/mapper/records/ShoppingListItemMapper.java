package org.example.persistence.jooq.mapper.records;

import org.example.entities.ShoppingListItem;
import org.example.persistence.jooq.generated.tables.records.ShoppingListItemRecord;
import org.example.units.Pieces;
import org.example.units.UnitType;
import org.example.units.Volume;
import org.example.units.Weight;
import org.example.valueObjects.Amount;
import org.jooq.RecordMapper;

import java.util.UUID;

public class ShoppingListItemMapper {
    public static RecordMapper<ShoppingListItemRecord, ShoppingListItem> createRecordMapper() {
        return record -> {
            Amount amount = switch (UnitType.valueOf(record.getAmountUnitType())) {
                case PIECES -> new Amount(record.getAmountValue(), Pieces.PIECES);
                case VOLUME -> new Amount(record.getAmountValue(), Volume.valueOf(record.getAmountUnit()));
                case WEIGHT -> new Amount(record.getAmountValue(), Weight.valueOf(record.getAmountUnit()));
            };

            return new ShoppingListItem(UUID.fromString(record.getId()),
                                        record.getShoppingListReference(),
                                        record.getItemReference(),
                                        record.getBought(),
                                        amount);
        };
    }

    public static ShoppingListItemRecord unmap(ShoppingListItem shoppingListItem) {
        return new ShoppingListItemRecord(shoppingListItem.getId().toString(),
                                          shoppingListItem.getShoppingListReference(),
                                          shoppingListItem.getItemReference(),
                                          shoppingListItem.isBought(),
                                          shoppingListItem.getAmount().getValue(),
                                          shoppingListItem.getAmount().getUnit().name(),
                                          shoppingListItem.getAmount().getUnit().getType().toString());
    }
}
