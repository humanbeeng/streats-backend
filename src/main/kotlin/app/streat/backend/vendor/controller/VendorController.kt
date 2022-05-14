package app.streat.backend.vendor.controller

import app.streat.backend.core.util.JWTUtil
import app.streat.backend.order.domain.model.order.Order
import app.streat.backend.vendor.data.dto.auth.VendorAuthRequestDTO
import app.streat.backend.vendor.data.dto.auth.VendorLoginRequestDTO
import app.streat.backend.vendor.data.dto.auth.VendorLoginResponseDTO
import app.streat.backend.vendor.data.dto.status.OrderStatusUpdateDTO
import app.streat.backend.vendor.data.dto.status.ShopStatusUpdateDTO
import app.streat.backend.vendor.service.vendor_management.StreatsVendorManagementService
import app.streat.backend.vendor.service.vendor_operations.VendorOperationsService
import com.google.common.net.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/vendor")
class VendorController(
    private val vendorOperationsService: VendorOperationsService,
    private val streatsVendorManagementService: StreatsVendorManagementService,
    private val jwtUtil: JWTUtil
) {

    @PostMapping("/auth")
    fun authenticateVendor(
        @RequestBody vendorAuthRequestDTO: VendorAuthRequestDTO
    ): ResponseEntity<Unit> {

        return try {
            streatsVendorManagementService.authenticateVendor(vendorAuthRequestDTO)
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }

    }

    @PostMapping("/login")
    fun loginVendor(
        @RequestBody vendorLoginRequestDTO: VendorLoginRequestDTO
    ): ResponseEntity<VendorLoginResponseDTO> {
        return try {
            val loginResponse = streatsVendorManagementService.loginVendor(vendorLoginRequestDTO)

            if (loginResponse.isVerified) {
                ResponseEntity.ok(loginResponse)
            } else ResponseEntity.badRequest().build()

        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/ongoing")
    fun fetchOngoingOrders(
        @RequestHeader(HttpHeaders.AUTHORIZATION) accessToken: String
    ): ResponseEntity<List<Order>> {

        return try {
            val vendorId = jwtUtil.getId(accessToken)
            return ResponseEntity.ok(vendorOperationsService.fetchCurrentDayOrders(vendorId))
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/shop")
    fun updateShopStatus(
        @RequestHeader(HttpHeaders.AUTHORIZATION) accessToken: String,
        @RequestBody shopStatusUpdateDTO: ShopStatusUpdateDTO,
    ): ResponseEntity<Unit> {
        return try {
            val vendorId = jwtUtil.getId(accessToken)
            vendorOperationsService.updateShopStatus(vendorId, shopStatusUpdateDTO.toShopStatus())
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }


    }

    @PostMapping("/order")
    fun updateOrderStatus(
        @RequestHeader(HttpHeaders.AUTHORIZATION) accessToken: String,
        @RequestBody orderStatusUpdateDTO: OrderStatusUpdateDTO
    ): ResponseEntity<Order> {
        return try {
            val updatedOrder =
                vendorOperationsService.updateOrderStatus(orderStatusUpdateDTO.orderId, orderStatusUpdateDTO.toOrderStatus())
            ResponseEntity.ok(updatedOrder)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }

    }


}