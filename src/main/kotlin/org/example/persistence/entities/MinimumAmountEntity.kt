package org.example.persistence.entities

import org.example.persistence.annotation.GenerateConverter
import org.example.persistence.annotation.ConvertValue
import org.example.units.Pieces
import org.example.units.UnitTypes
import org.example.units.Volume
import org.example.units.Weight
import org.example.valueObjects.Amount
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Transient

@Entity
@GenerateConverter(aggregate = "org.example.entities.MinimumAmount")
class MinimumAmountEntity {

    @Id
    @ConvertValue
    var itemName: String? = null

    @Transient
    @ConvertValue
    var amount: Amount? = null
        get() = reconstructAmount()
        set(value) {
            amountUnitType = value?.unit?.type
            amountValue = value?.value
            amountUnit = value?.unit?.name()
            field = null
        }

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MinimumAmountEntity

        if (itemName != other.itemName) return false
        if (amountUnitType != other.amountUnitType) return false
        if (amountValue != other.amountValue) return false
        if (amountUnit != other.amountUnit) return false

        return true
    }

    override fun hashCode(): Int {
        var result = itemName?.hashCode() ?: 0
        result = 31 * result + (amountUnitType?.hashCode() ?: 0)
        result = 31 * result + (amountValue?.hashCode() ?: 0)
        result = 31 * result + (amountUnit?.hashCode() ?: 0)
        return result
    }

}