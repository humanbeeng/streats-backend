package app.streat.backend.vendor.service.vendor_management

import app.streat.backend.core.util.CoreConstants.EMPTY
import app.streat.backend.core.util.JWTUtil
import app.streat.backend.vendor.data.dto.auth.VendorAuthRequestDTO
import app.streat.backend.vendor.data.dto.auth.VendorLoginRequestDTO
import app.streat.backend.vendor.data.dto.auth.VendorLoginResponseDTO
import app.streat.backend.vendor.data.repository.StreatsVendorRepository
import app.streat.backend.vendor.domain.models.streats_vendor.StreatsVendor
import com.google.firebase.auth.FirebaseAuth
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service

@Service
class StreatsVendorManagementServiceImpl(
    private val repo: StreatsVendorRepository, private val jwtUtil: JWTUtil
) : StreatsVendorManagementService {

    override fun authenticateVendor(vendorAuthRequestDTO: VendorAuthRequestDTO) {
        val vendorId = jwtUtil.getId(vendorAuthRequestDTO.vendorAccessToken)

        val vendor = getStreatsVendorById(vendorId)

        updateVendorFcmToken(vendorAuthRequestDTO.vendorFcmToken, vendor)

    }


    override fun loginVendor(vendorLoginRequest: VendorLoginRequestDTO): VendorLoginResponseDTO {

        return if (verifyUser(vendorLoginRequest.idToken)) {
            val decodedToken = FirebaseAuth.getInstance().verifyIdToken(vendorLoginRequest.idToken)

            val vendorId = decodedToken.uid

            val streatsVendor = repo.findStreatsVendorByVendorId(vendorId).orElseThrow {
                BadCredentialsException("Vendor not found")
            }

            updateVendorFcmToken(vendorLoginRequest.vendorFcmToken, streatsVendor)

            val vendorAccessToken = jwtUtil.createVendorAccessToken(streatsVendor)

            VendorLoginResponseDTO(vendorAccessToken, true)
        } else
            VendorLoginResponseDTO(EMPTY, false)


    }

    override fun updateStreatsVendor(streatsVendor: StreatsVendor): StreatsVendor {
        if (repo.existsById(streatsVendor.vendorId)) {
            return repo.save(streatsVendor)
        } else throw Exception("Vendor not found")
    }

    override fun onboardStreatsVendor(streatsVendor: StreatsVendor): StreatsVendor {
        return repo.save(streatsVendor)
    }

    override fun getStreatsVendorByVendorId(vendorId: String): StreatsVendor {
        return repo.findStreatsVendorByVendorId(vendorId).orElseThrow { Exception("Vendor not found") }
    }

    fun getStreatsVendorById(vendorId: String): StreatsVendor {
        return repo.findStreatsVendorByVendorId(vendorId).orElseThrow {
            Exception("Vendor not found")
        }
    }

    private fun updateVendorFcmToken(vendorFcmToken: String, streatsVendor: StreatsVendor): StreatsVendor {
        streatsVendor.vendorFcmToken = vendorFcmToken
        return updateStreatsVendor(streatsVendor)
    }

    private fun verifyUser(idToken: String): Boolean {
        return try {
            FirebaseAuth.getInstance().verifyIdToken(idToken)
            true
        } catch (e: Exception) {
            false
        }
    }


}