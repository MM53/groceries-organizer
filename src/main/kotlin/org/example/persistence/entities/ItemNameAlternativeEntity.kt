package org.example.persistence.entities

import org.example.persistence.annotation.GenerateConverter
import org.example.persistence.annotation.ConvertValue
import javax.persistence.Entity
import javax.persistence.Id

@Entity
@GenerateConverter(aggregate = "org.example.entities.ItemNameAlternative")
class ItemNameAlternativeEntity {

    @Id
    @ConvertValue
    var name: String? = null
    @ConvertValue
    var alternativeFor: String? = null
}