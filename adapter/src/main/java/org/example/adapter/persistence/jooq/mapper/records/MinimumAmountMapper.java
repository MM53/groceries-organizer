package org.example.adapter.persistence.jooq.mapper.records;

import org.example.entities.MinimumAmount;
import org.example.units.Pieces;
import org.example.units.UnitType;
import org.example.units.Volume;
import org.example.units.Weight;
import org.example.valueObjects.Amount;
import org.jooq.RecordMapper;
import org.example.adapter.persistence.jooq.generated.tables.records.MinimumAmountRecord;

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

    public static MinimumAmountRecord unmap(MinimumAmount minimumAmount) {
        return new MinimumAmountRecord(minimumAmount.getId().toString(),
                                       minimumAmount.getAmount().getValue(),
                                       minimumAmount.getAmount().getUnit().name(),
                                       minimumAmount.getAmount().getUnit().getType().toString());
    }
}
