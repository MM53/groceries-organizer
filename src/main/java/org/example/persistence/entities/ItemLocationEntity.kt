package org.example.persistence.entities

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

@Entity
class ItemLocationEntity {

    @Id
    @GeneratedValue
    var id: UUID? = null

    var locationRoom: String? = null
    var locationPlace: String? = null
    var locationShelf: String? = null

    var amountUnitType: UnitTypes? = null
    var amountValue: Double? = null
    var amountUnit: String? = null

    fun getLocation(): Location {
        return Location(locationRoom, locationPlace, locationShelf);
    }

    fun setLocation(location: Location) {
        locationPlace = location.place;
        locationRoom = location.room;
        locationShelf = location.shelf;
    }

    fun getAmount(): Amount {
        return when (amountUnitType!!) {
            UnitTypes.PIECES -> Amount(amountValue!!, Pieces.PIECES);
            UnitTypes.VOLUME -> Amount(amountValue!!, Volume.valueOf(amountUnit!!));
            UnitTypes.WEIGHT -> Amount(amountValue!!, Weight.valueOf(amountUnit!!));
        };
    }

    fun setAmount(amount: Amount) {
        amountUnitType = amount.unit.type;
        amountValue = amount.value;
        amountUnit = amount.unit.name();
    }
}