package de.dhbw.plugins.persistence.jooq.mapper.records;

import de.dhbw.aggregates.ShoppingList;
import de.dhbw.entities.ShoppingListItem;
import de.dhbw.plugins.persistence.jooq.generated.tables.records.ShoppingListItemRecord;
import de.dhbw.units.Pieces;
import de.dhbw.units.UnitType;
import de.dhbw.units.Volume;
import de.dhbw.units.Weight;
import de.dhbw.valueObjects.Amount;
import org.jooq.RecordMapper;

import java.util.List;
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
                                        record.getItemReference(),
                                        record.getBought(),
                                        amount);
        };
    }

    public static List<ShoppingListItemRecord> extractRecords(ShoppingList shoppingList) {
        return shoppingList.getShoppingListItems()
                           .stream()
                           .map(shoppingListItem -> new ShoppingListItemRecord(shoppingListItem.getId().toString(),
                                                                               shoppingList.getName(),
                                                                               shoppingListItem.getItemReference(),
                                                                               shoppingListItem.isBought(),
                                                                               shoppingListItem.getAmount().getValue(),
                                                                               shoppingListItem.getAmount().getUnit().name(),
                                                                               shoppingListItem.getAmount().getUnit().getType().toString()))
                           .toList();
    }
}
