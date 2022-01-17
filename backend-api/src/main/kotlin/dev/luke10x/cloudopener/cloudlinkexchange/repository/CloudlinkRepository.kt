package dev.luke10x.cloudopener.cloudlinkexchange.repository

import dev.luke10x.cloudopener.cloudlinkexchange.model.Cloudlink
import org.springframework.stereotype.Component
import org.springframework.context.annotation.Scope

@Component
@Scope(value = "singleton")
class CloudlinkRepository {
    val cloudlinks = HashMap<String, Cloudlink>()

    fun add(new: Cloudlink) {
        cloudlinks[new.code] = new
        println("ADD CLOUDLINK at ${new.code}")
    }

    fun getByCode(code: String): Cloudlink? {
        println("RETRIEVE CLOUDLINK at $code")
        return cloudlinks[code]
    }
}
