package org.example.persistence.entities

import org.example.persistence.annotation.GenerateConverter
import org.example.persistence.annotation.ConvertValue
import org.example.units.Pieces
import org.example.units.UnitTypes
import org.example.units.Volume
import org.example.units.Weight
import org.example.valueObjects.Amount
import org.example.valueObjects.Location
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Transient

@Entity
@GenerateConverter(aggregate = "org.example.entities.ItemLocation")
class ItemLocationEntity {

    @Id
    @GeneratedValue
    @ConvertValue
    var id: UUID? = null

    @ConvertValue
    @Transient
    var location: Location? = null
        get() = Location(locationRoom, locationPlace, locationShelf)
        set(value) {
            locationPlace = value?.place;
            locationRoom = value?.room;
            locationShelf = value?.shelf;
            field = null
        }

    @ConvertValue
    @Transient
    var amount: Amount? = null
        get() = reconstructAmount()
        set(value) {
            amountUnitType = value?.unit?.type
            amountValue = value?.value
            amountUnit = value?.unit?.name()
            field = null
        }

    var locationRoom: String? = null
    var locationPlace: String? = null
    var locationShelf: String? = null

    var amountUnitType: UnitTypes? = null
    var amountValue: Double? = null
    var amountUnit: String? = null

    private fun reconstructAmount(): Amount {
        return when (amountUnitType!!) {
            UnitTypes.PIECES -> Amount(amountValue!!, Pieces.PIECES);
            UnitTypes.VOLUME -> Amount(amountValue!!, Volume.valueOf(amountUnit!!));
            UnitTypes.WEIGHT -> Amount(amountValue!!, Weight.valueOf(amountUnit!!));
        };
    }
}