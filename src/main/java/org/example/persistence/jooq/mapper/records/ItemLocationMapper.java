package org.example.persistence.jooq.mapper.records;

import org.example.entities.ItemLocation;
import org.example.persistence.jooq.generated.tables.records.ItemLocationRecord;
import org.example.units.Pieces;
import org.example.units.UnitType;
import org.example.units.Volume;
import org.example.units.Weight;
import org.example.valueObjects.Amount;
import org.example.valueObjects.Location;
import org.jooq.RecordMapper;

import java.util.UUID;

public class ItemLocationMapper {
    public static RecordMapper<ItemLocationRecord, ItemLocation> createRecordMapper() {
        return record -> {
            Amount amount = switch (UnitType.valueOf(record.getAmountUnitType())) {
                case PIECES -> new Amount(record.getAmountValue(), Pieces.PIECES);
                case VOLUME -> new Amount(record.getAmountValue(), Volume.valueOf(record.getAmountUnit()));
                case WEIGHT -> new Amount(record.getAmountValue(), Weight.valueOf(record.getAmountUnit()));
            };

            return new ItemLocation(UUID.fromString(record.getId()),
                                    UUID.fromString(record.getStoredItemReference()),
                                    new Location(record.getLocation()),
                                    amount);
        };
    }

    public static ItemLocationRecord unmap(ItemLocation itemLocation) {
        return new ItemLocationRecord(itemLocation.getId().toString(),
                                      itemLocation.getStoredItemReference().toString(),
                                      itemLocation.getLocation().getLocation(),
                                      itemLocation.getAmount().getValue(),
                                      itemLocation.getAmount().getUnit().name(),
                                      itemLocation.getAmount().getUnit().getType().toString());
    }
}
