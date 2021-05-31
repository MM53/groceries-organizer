package de.dhbw.plugins.persistence.jooq.mapper.records;

import de.dhbw.aggregates.StoredItem;
import de.dhbw.entities.MinimumAmount;
import de.dhbw.plugins.persistence.jooq.generated.tables.records.MinimumAmountRecord;
import de.dhbw.units.Pieces;
import de.dhbw.units.UnitType;
import de.dhbw.units.Volume;
import de.dhbw.units.Weight;
import de.dhbw.valueObjects.Amount;
import org.jooq.RecordMapper;

import java.util.UUID;

public class MinimumAmountMapper {
    public static RecordMapper<MinimumAmountRecord, MinimumAmount> createRecordMapper() {
        return record -> {
            if (record.getId() == null) {
                return null;
            }
            Amount amount = switch (UnitType.valueOf(record.getAmountUnitType())) {
                case PIECES -> new Amount(record.getAmountValue(), Pieces.PIECES);
                case VOLUME -> new Amount(record.getAmountValue(), Volume.valueOf(record.getAmountUnit()));
                case WEIGHT -> new Amount(record.getAmountValue(), Weight.valueOf(record.getAmountUnit()));
            };

            return new MinimumAmount(UUID.fromString(record.getId()), amount);
        };
    }

    public static MinimumAmountRecord extractRecord(StoredItem storedItem) {
        MinimumAmount minimumAmount = storedItem.getMinimumAmount();
        return new MinimumAmountRecord(minimumAmount.getId().toString(),
                                       minimumAmount.getAmount().getValue(),
                                       minimumAmount.getAmount().getUnit().name(),
                                       minimumAmount.getAmount().getUnit().getType().toString());
    }
}
