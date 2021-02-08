package org.example.persistence.entities

import org.example.persistence.annotation.GenerateConverter
import org.example.persistence.annotation.ConvertValue
import java.util.*
import javax.persistence.*
import kotlin.collections.HashSet

@Entity
@GenerateConverter(aggregate = "org.example.aggregates.StoredItem")
class StoredItemEntity {

    @Id
    @GeneratedValue
    @ConvertValue
    var id: UUID? = null

    @OneToOne
    @ConvertValue
    var item: ItemEntity? = null

    @OneToMany(fetch = FetchType.EAGER)
    @ConvertValue(aggregateGetter = "getLocations")
    var itemLocations: Set<ItemLocationEntity> = HashSet()

    @OneToOne
    @ConvertValue
    var minimumAmount: MinimumAmountEntity? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StoredItemEntity

        if (id != other.id) return false
        if (item != other.item) return false
        if (itemLocations != other.itemLocations) return false
        if (minimumAmount != other.minimumAmount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (item?.hashCode() ?: 0)
        result = 31 * result + itemLocations.hashCode()
        result = 31 * result + (minimumAmount?.hashCode() ?: 0)
        return result
    }
}