package org.example.persistence.jooq.mapper;

import org.example.entities.MinimumAmount;
import org.example.persistence.jooq.generated.tables.records.MinimumAmountRecord;
import org.example.units.Pieces;
import org.example.units.UnitTypes;
import org.example.units.Volume;
import org.example.units.Weight;
import org.example.valueObjects.Amount;
import org.jooq.RecordMapper;

import java.util.UUID;

public class MinimumAmountMapper {
    public static RecordMapper<MinimumAmountRecord, MinimumAmount> createRecordMapper() {
        return record -> {
            Amount amount = switch (UnitTypes.valueOf(record.getAmountUnitType())) {
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
