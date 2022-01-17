package dev.luke10x.cloudopener.cloudlinkexchange.delegate

import dev.luke10x.cloudopener.cloudlinkexchange.api.ExchangeApiDelegate
import dev.luke10x.cloudopener.cloudlinkexchange.model.Exchange
import dev.luke10x.cloudopener.cloudlinkexchange.model.Message
import dev.luke10x.cloudopener.cloudlinkexchange.repository.ExchangeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ExchangeApiDelegateImpl(@Autowired var exchangeRepository: ExchangeRepository): ExchangeApiDelegate {
    override fun createExchange(): ResponseEntity<Exchange> {
        val exchange = Exchange()
        exchange.handle = UUID.randomUUID().toString()

        exchangeRepository.add(exchange)

        return ResponseEntity(exchange, HttpStatus.CREATED)
    }

    override fun getExchange(handle: String?, from: Int?): ResponseEntity<Exchange> {
        handle ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        val exchange = exchangeRepository.getByHandle(handle)
        exchange ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        return ResponseEntity(exchange, HttpStatus.OK)
    }

    override fun addMessage(handle: String?, message: Message?): ResponseEntity<Message> {
        handle ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        val exchange = exchangeRepository.getByHandle(handle)
        exchange ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        exchange.messages.add(message)

        return ResponseEntity(message, HttpStatus.CREATED)
    }
}
