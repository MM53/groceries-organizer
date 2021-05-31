package de.dhbw.plugins.persistence.jooq.mapper.records;

import de.dhbw.aggregates.StoredItem;
import de.dhbw.entities.ItemLocation;
import de.dhbw.plugins.persistence.jooq.generated.tables.records.ItemLocationRecord;
import de.dhbw.units.Pieces;
import de.dhbw.units.UnitType;
import de.dhbw.units.Volume;
import de.dhbw.units.Weight;
import de.dhbw.valueObjects.Amount;
import de.dhbw.valueObjects.Location;
import org.jooq.RecordMapper;

import java.util.List;
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
                                    new Location(record.getLocation()),
                                    amount);
        };
    }

    public static List<ItemLocationRecord> extractRecords(StoredItem storedItem) {
        return storedItem.getItemLocations()
                         .stream()
                         .map(itemLocation -> new ItemLocationRecord(itemLocation.getId().toString(),
                                                                     storedItem.getId().toString(),
                                                                     itemLocation.getLocation().getLocation(),
                                                                     itemLocation.getAmount().getValue(),
                                                                     itemLocation.getAmount().getUnit().name(),
                                                                     itemLocation.getAmount().getUnit().getType().toString()))
                .toList();
    }
}
