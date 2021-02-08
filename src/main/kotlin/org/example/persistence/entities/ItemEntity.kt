package org.example.persistence.entities

import org.example.persistence.annotation.GenerateConverter
import org.example.persistence.annotation.ConvertValue
import org.example.units.UnitTypes
import javax.persistence.*
import kotlin.collections.HashSet

@Entity
@GenerateConverter(aggregate = "org.example.entities.Item")
class ItemEntity {
    @Id
    @ConvertValue
    var primaryName: String? = null

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "alternativeFor")
    @ConvertValue
    var alternativeNames: Set<ItemNameAlternativeEntity> = HashSet()

    @ConvertValue
    var unitType: UnitTypes? = null
}