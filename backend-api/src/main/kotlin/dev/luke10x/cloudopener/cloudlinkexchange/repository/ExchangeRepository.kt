package dev.luke10x.cloudopener.cloudlinkexchange.repository

import dev.luke10x.cloudopener.cloudlinkexchange.model.Exchange
import org.springframework.stereotype.Component
import org.springframework.context.annotation.Scope

@Component
@Scope(value = "singleton")
class ExchangeRepository {
    val exchanges = HashMap<String, Exchange>()

    fun add(new: Exchange) {
        exchanges[new.handle] = new
        println("ADD EXCHANGE at ${new.handle}")
    }

    fun getByHandle(handle: String): Exchange? {
        println("RETRIEVE EXCHANGE at $handle")
        return exchanges[handle]
    }
}
