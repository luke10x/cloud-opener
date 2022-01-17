package dev.luke10x.cloudopener.cloudlinkexchange.delegate

import dev.luke10x.cloudopener.cloudlinkexchange.api.CloudlinkApiDelegate
import dev.luke10x.cloudopener.cloudlinkexchange.model.Cloudlink
import dev.luke10x.cloudopener.cloudlinkexchange.model.CloudlinkPatch
import dev.luke10x.cloudopener.cloudlinkexchange.repository.CloudlinkRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class CloudlinkApiDelegateImpl(@Autowired var cloudlinkRepository: CloudlinkRepository): CloudlinkApiDelegate {
    override fun createCloudlink(): ResponseEntity<Cloudlink> {
        val cloudlink = Cloudlink()
        cloudlink.code = UUID.randomUUID().toString()

        cloudlinkRepository.add(cloudlink)

        return ResponseEntity(cloudlink, HttpStatus.CREATED)
    }

    override fun getCloudlink(code: String?): ResponseEntity<Cloudlink> {
        code ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        val cloudlink = cloudlinkRepository.getByCode(code)
        cloudlink ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        return ResponseEntity(cloudlink, HttpStatus.OK)
    }

    override fun updateCloudlink(code: String?, cloudlinkPatch: CloudlinkPatch?): ResponseEntity<CloudlinkPatch> {
        code ?: return ResponseEntity(HttpStatus.BAD_REQUEST)
        cloudlinkPatch ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        val cloudlink = cloudlinkRepository.getByCode(code)
        cloudlink ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        cloudlinkPatch.exchangeHandle.let {
            cloudlink.exchangeHandle = it
        }

        return ResponseEntity(cloudlinkPatch, HttpStatus.OK)
    }
}
