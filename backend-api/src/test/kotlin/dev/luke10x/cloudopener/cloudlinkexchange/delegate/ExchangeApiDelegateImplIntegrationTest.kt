package dev.luke10x.cloudopener.cloudlinkexchange.delegate

import dev.luke10x.cloudopener.cloudlinkexchange.model.Exchange
import dev.luke10x.cloudopener.cloudlinkexchange.model.Message
import dev.luke10x.cloudopener.cloudlinkexchange.repository.ExchangeRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.hamcrest.Matchers.hasSize
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*


@RunWith(value = SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class ExchangeApiDelegateImplIntegrationTest {
    private val EXCHANGE_PATH = "/v1/exchange/"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var exchangeRepository: ExchangeRepository

    @Test
    @DisplayName("""
        When a client POSTs an exchange;
        Then the exchange should be CREATED,
        and (in the response) the exchange should have a handle,
        and it should have 0 messages.
    """)
    fun whenPost_thenIsCreated() {
        val req = post(EXCHANGE_PATH)

        mockMvc.perform(req)
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.handle").isNotEmpty)
            .andExpect(jsonPath("$.messages").isArray)
            .andExpect(jsonPath("$.messages").isEmpty)
    }

    @Test
    @DisplayName("""
        Given a new exchange is created;
        When a client GETs the exchange;
        Then the exchange should be retrieved OK,
        and the exchange should have a handle,
        and it should have 0 messages.
    """)
    fun whenGet_thenIsOk() {
        val exchangeHandle = "72773929-bb0f-4cd2-8458-850fd38984b8"

        val existingExchange = Exchange()
        existingExchange.handle = exchangeHandle
        exchangeRepository.add(existingExchange)

        val request = get("${EXCHANGE_PATH}/${exchangeHandle}")

        mockMvc.perform(request)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.handle").value(exchangeHandle))
            .andExpect(jsonPath("$.messages").isArray)
            .andExpect(jsonPath("$.messages").isEmpty)
    }

    @Test
    @DisplayName("""
        Given a new exchange is there,
        and it has 2 messages;
        When a client GETs the exchange;
        Then the exchange should be retrieved OK,
        and it should have 2 messages.
    """)
    fun whenGetAndHasMoreMessages_thenIsOk() {
        val exchangeHandle = "72773929-bb0f-4cd2-8458-850fd38984b8"

        val existingExchange = Exchange()
        existingExchange.handle = exchangeHandle
        existingExchange.messages.add(Message())
        existingExchange.messages.add(Message())

        exchangeRepository.add(existingExchange)

        val request = get("${EXCHANGE_PATH}/${exchangeHandle}")

        mockMvc.perform(request)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.messages").isArray)
            .andExpect(jsonPath("$.messages", hasSize<List<Message>>(2)))
    }

    @Test
    @DisplayName("""
        Given no exchange is created;
        When a client GETs an exchange;
        Then the exchange should be NOT FOUND.
    """)
    fun whenGetNotExisting_thenIsNotFound() {
        val exchangeHandle = "0bde0e30-d692-43fc-b38e-7bfc624b6c7a"

        val request = get("${EXCHANGE_PATH}/${exchangeHandle}")

        mockMvc.perform(request)
            .andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("""
        Given a new exchange is there;
        When a client POSTS a message to the exchange;
        Then the exchange should have 1 message.
    """)
    fun whenPostMessate_thenIsNotFound() {
        val exchangeHandle = "91129d54-f195-40c1-8e1e-e30bf62eb808"

        val existingExchange = Exchange()
        existingExchange.handle = exchangeHandle
        exchangeRepository.add(existingExchange)

        val request = post("${EXCHANGE_PATH}/${exchangeHandle}/messages")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "type": "text/plain",
                    "body": "hello",
                    "timestamp": 1643588190
                }
            """.trimIndent())

        mockMvc.perform(request)
            .andExpect(status().isCreated)

        val savedExchange = exchangeRepository.getByHandle(exchangeHandle)

        assertThat(savedExchange!!.messages).hasSize(1);
    }
}
