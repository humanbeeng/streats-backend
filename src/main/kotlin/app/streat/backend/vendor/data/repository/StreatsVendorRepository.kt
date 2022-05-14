package app.streat.backend.vendor.data.repository

import app.streat.backend.vendor.domain.models.streats_vendor.StreatsVendor
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface StreatsVendorRepository : MongoRepository<StreatsVendor, String> {

    fun findStreatsVendorByVendorId(vendorId: String): Optional<StreatsVendor>

    fun findByShopId(shopId: String): Optional<StreatsVendor>

}