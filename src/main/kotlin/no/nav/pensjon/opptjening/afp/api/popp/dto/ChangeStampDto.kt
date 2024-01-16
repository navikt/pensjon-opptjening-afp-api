package no.nav.pensjon.opptjening.afp.api.popp.dto

import java.util.*

class ChangeStampDto {
    var createdBy: String? = null
    var createdDate: Date? = null
    var updatedBy: String? = null
    var updatedDate: Date? = null

    constructor() {}

    constructor(createdBy: String?, createdDate: Date?, updatedBy: String?, updatedDate: Date?) {
        this.createdBy = createdBy
        this.createdDate = createdDate
        this.updatedBy = updatedBy
        this.updatedDate = updatedDate
    }
}