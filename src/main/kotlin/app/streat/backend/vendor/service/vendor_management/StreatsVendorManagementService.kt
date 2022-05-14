package app.streat.backend.vendor.service.vendor_management

import app.streat.backend.vendor.data.dto.auth.VendorAuthRequestDTO
import app.streat.backend.vendor.data.dto.auth.VendorLoginRequestDTO
import app.streat.backend.vendor.data.dto.auth.VendorLoginResponseDTO
import app.streat.backend.vendor.domain.models.streats_vendor.StreatsVendor

interface StreatsVendorManagementService {
    fun authenticateVendor(vendorAuthRequestDTO: VendorAuthRequestDTO)

    fun loginVendor(vendorLoginRequest: VendorLoginRequestDTO): VendorLoginResponseDTO

    fun updateStreatsVendor(streatsVendor: StreatsVendor): StreatsVendor

    fun onboardStreatsVendor(streatsVendor: StreatsVendor): StreatsVendor

    fun getStreatsVendorByVendorId(vendorId: String): StreatsVendor
}