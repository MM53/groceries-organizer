package org.example.persistence.entities

import org.example.units.UnitTypes
import java.util.*
import javax.persistence.*

@Entity
class ItemEntity {
    @Id
    var primaryName: String? = null
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "alternativeFor")
    var alternativeNames: List<ItemNameAlternativeEntity> = LinkedList()
    var unitType: UnitTypes? = null
}