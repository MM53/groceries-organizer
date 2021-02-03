package org.example.persistence.entities

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class ItemNameAlternativeEntity {

    @Id
    var name: String? = null
    var alternativeFor: String? = null
}