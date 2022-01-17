package dev.luke10x.cloudopener.cloudlinkexchange.delegate

import dev.luke10x.cloudopener.cloudlinkexchange.model.Cloudlink
import dev.luke10x.cloudopener.cloudlinkexchange.repository.CloudlinkRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(value = SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class CloudlinkApiDelegateImplIntegrationTest {
    private val CLOUDLINK_PATH = "/v1/cloudlink/"

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var cloudlinkRepository: CloudlinkRepository

    @Test
    @DisplayName("""
        when a client POSTs a cloudlink
        then the cloudlink should be CREATED
        and (in the response) the cloudlink should have a code
        but it should not have exchangeHandle
    """)
    fun whenPost_thenIsCreated() {
        val req = post(CLOUDLINK_PATH)

        mockMvc.perform(req)
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").isNotEmpty)
            .andExpect(jsonPath("$.exchangeHandle").doesNotExist());
    }

    @Test
    @DisplayName("""
        given a new cloudlink is created
        when a client GETs the cloudlink
        then the cloudlink should be retrieved OK
        and the cloudlink should have a code
        but it should not have exchangeHandle
    """)
    fun whenGet_thenIsOk() {
        val cloudlinkCode = "72773929-bb0f-4cd2-8458-850fd38984b8"

        val existingCloudlink = Cloudlink()
        existingCloudlink.code = cloudlinkCode
        cloudlinkRepository.add(existingCloudlink)

        val request = get("${CLOUDLINK_PATH}/${cloudlinkCode}")

        mockMvc.perform(request)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value(cloudlinkCode))
            .andExpect(jsonPath("$.exchangeHandle").doesNotExist());
    }

    @Test
    @DisplayName("""
        given no cloudlink is created
        when a client GETs a cloudlink
        then the cloudlink should be NOT FOUND
    """)
    fun whenGetNotExisting_thenIsNotFound() {
        val cloudlinkCode = "91129d54-f195-40c1-8e1e-e30bf62eb808"

        val request = get("${CLOUDLINK_PATH}/${cloudlinkCode}")

        mockMvc.perform(request)
            .andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("""
        given a new cloudlink is there
        when a client sends a PATCH to the cloudlink
        then the exchange should have exchange handle set
    """)
    fun whenPatch_thenOk() {
        val cloudlinkCode = "91129d54-f195-40c1-8e1e-e30bf62eb808"
        val exchangeHandle = "72773929-bb0f-4cd2-8458-850fd38984b8"

        val existingCloudlink = Cloudlink()
        existingCloudlink.code = cloudlinkCode
        cloudlinkRepository.add(existingCloudlink)

        val request = patch("${CLOUDLINK_PATH}/${cloudlinkCode}")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "exchangeHandle": "${exchangeHandle}"
                }
            """.trimIndent())

        mockMvc.perform(request)
            .andExpect(status().isOk)

        val savedCloudlink = cloudlinkRepository.getByCode(cloudlinkCode)
        Assertions.assertThat(savedCloudlink!!.exchangeHandle).isEqualTo(exchangeHandle);
    }
}
