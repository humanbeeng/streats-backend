package app.streat.backend.order.service

import app.streat.backend.auth.service.StreatsUserService
import app.streat.backend.cart.service.exceptions.CartException
import app.streat.backend.cart.util.CartConstants.PARAM_ORDER_AMOUNT
import app.streat.backend.cart.util.CartConstants.PARAM_ORDER_ID
import app.streat.backend.cart.util.CartConstants.PARAM_PAYMENT_MODE
import app.streat.backend.cart.util.CartConstants.PARAM_REFERENCE_ID
import app.streat.backend.cart.util.CartConstants.PARAM_SIGNATURE
import app.streat.backend.cart.util.CartConstants.PARAM_TRANSACTION_MESSAGE
import app.streat.backend.cart.util.CartConstants.PARAM_TRANSACTION_STATUS
import app.streat.backend.cart.util.CartConstants.PARAM_TRANSACTION_TIME
import app.streat.backend.core.config.CashfreeConfig
import app.streat.backend.core.util.CoreConstants.CASHFREE_STAGE_TEST
import app.streat.backend.core.util.CoreConstants.CURRENCY_RUPEES
import app.streat.backend.core.util.CoreConstants.EMPTY
import app.streat.backend.core.util.Hmac256Util
import app.streat.backend.core.util.NetworkConstants.HEADER_CLIENT_ID
import app.streat.backend.core.util.NetworkConstants.HEADER_CLIENT_SECRET
import app.streat.backend.notification.service.NotificationService
import app.streat.backend.order.data.dto.cashfree_token.CashfreeTokenRequestDTO
import app.streat.backend.order.data.dto.cashfree_token.CashfreeTokenResponseDTO
import app.streat.backend.order.data.dto.order_verification.OrderPaymentVerificationRequest
import app.streat.backend.order.data.repository.OrderRepository
import app.streat.backend.order.domain.model.order.Order
import app.streat.backend.order.domain.model.order.OrderWithToken
import app.streat.backend.order.domain.model.status.OrderStatus
import app.streat.backend.order.domain.model.status.PaymentStatus
import app.streat.backend.shop.data.repositories.StreatsShopRepository
import app.streat.backend.vendor.service.vendor_management.StreatsVendorManagementService
import org.bson.types.ObjectId
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * TODO : 1. Wrap with try catch and introduce logging instead of spitting out on console
 *
 * TODO : 2. Add OrderExceptions while introducing logger
 */

@Service
class OrderServiceImpl(
    private val userService: StreatsUserService,
    private val cashfreeConfig: CashfreeConfig,
    private val hmac256Util: Hmac256Util,
    private val orderRepository: OrderRepository,
    private val notificationService: NotificationService,
    private val shopRepository: StreatsShopRepository,
    private val streatsVendorManagementService: StreatsVendorManagementService
) : OrderService {

    override fun findOrderByOrderId(orderId: String): Order {
        return try {
            orderRepository.findOrderByOrderId(orderId)
        } catch (e: Exception) {
            throw NoSuchElementException("Order with given orderId not found")
        }
    }

    override fun getAllOrders(userId: String): List<Order> {
        val user = userService.getStreatsCustomerById(userId)
        return user.orders
    }


    /**
     * Initiate Order
     *
     * Step 1: Create an Order object with all values calculated
     *
     * Step 2 : Model a request to send Cashfree in exchange for cftoken
     *
     * Step 3 : Add the cftoken to response for /initiate/order
     *
     * Step 4 : Add exception handling
     */

    override fun initiateOrder(userId: String): OrderWithToken {

        val order = createOrder(userId)

        val shopId = order.shopId

        val shopOptional = shopRepository.findStreatsShopByShopId(shopId)

        if (shopOptional.isEmpty) {
            throw Exception("Shop not found")
        }
        val shop = shopOptional.get()

        if (shop.isShopOpen.not()) {
            throw Exception("Shop is closed")
        }

        addOrderToOrderRepo(order)

        val tokenRequest = createTokenRequest(order)

        val tokenResponse = getCftoken(tokenRequest)

        val user = userService.getStreatsCustomerById(userId)

        return OrderWithToken(
            user.username,
            user.email,
            tokenResponse.cftoken,
            order.orderId,
            orderCurrency = CURRENCY_RUPEES,
            orderAmount = order.totalCost.toString(),
            appId = cashfreeConfig.clientId,
            stage = CASHFREE_STAGE_TEST,
            status = tokenResponse.status
        )

    }

    /**
     * TODO : Delete this method or move to Admin service
     */
    override fun deleteAllOrders(userId: String) {
        val user = userService.getStreatsCustomerById(userId)
        user.orders.removeAll(user.orders)
        userService.updateStreatsCustomer(user)
    }

    private fun addOrderToOrderRepo(order: Order): Order {
        if (orderRepository.existsById(order.orderId)) {
            throw Exception("Order already exists")
        }
        return orderRepository.save(order)
    }

    override fun updateOrderStatusInOrderRepo(orderId: String, orderStatus: OrderStatus): Order {
        val order = findOrderByOrderId(orderId)
        order.orderStatus = orderStatus.name
        return orderRepository.save(order)
    }


    /**
     * Verify Order Payment
     *
     * This method handles callback from Cashfree and needs to be idempotent to deal with duplicate callbacks
     *
     * Step 1: Extraction of Request Params and modelling into a data class (OrderPaymentVerificationRequest)
     *
     * Step 2: Validate Signature
     *
     * Step 3: Get the Order Item from UserDB and check if orderPaymentStatus and orderVerificationStatus is SUCCESS
     * (To handle duplicate callbacks), if not then, set orderPaymentStatus and orderVerificationStatus flags is
     * IN_PROGRESS. If those flags are set to SUCCESS/FAILURE, then ignore
     *
     * Step 4: If the signatures are valid, then update orderPaymentStatus and orderVerificationStatus flags to SUCCESS.
     * If signature are invalid, then set to FAILURE
     *
     * TODO : Add fallback if any of the steps failed.
     */
    override fun verifyOrderPaymentAndPlaceOrder(
        orderPaymentVerificationRequestParams: LinkedHashMap<String, String>
    ): Boolean {
        val paymentVerificationData =
            extractOrderPaymentVerificationRequestParams(orderPaymentVerificationRequestParams)


        val isSignatureValid = hmac256Util.verifySignature(
            paymentVerificationData.getDataString(), paymentVerificationData.signature, cashfreeConfig.clientSecret
        )

        val orderId: String = paymentVerificationData.orderId

        return if (isSignatureValid) {
            placeOrder(orderId)

        } else {
            failOrder(orderId)
        }

    }


    //    TODO : Add concurrency patterns
    private fun placeOrder(orderId: String): Boolean {
        return try {
            val paymentSuccessOrder = updatePaymentStatusInOrderRepo(orderId, PaymentStatus.SUCCESS)

            userService.clearCart(paymentSuccessOrder.userId)

            userService.pushToUserOrderHistory(paymentSuccessOrder)

            addNewOrderToOngoingOrdersList(paymentSuccessOrder)

            notificationService.notifyOrderToUser(paymentSuccessOrder)

            notificationService.notifyOrderToVendor(paymentSuccessOrder)
        } catch (e: Exception) {
            false
        }

    }

    private fun failOrder(orderId: String): Boolean {
        return try {
            val paymentFailedOrder = updatePaymentStatusInOrderRepo(orderId, PaymentStatus.FAILURE)
            userService.pushToUserOrderHistory(paymentFailedOrder)
            notificationService.notifyOrderToUser(paymentFailedOrder)
        } catch (e: Exception) {
            false
        }

    }

    private fun createOrder(userId: String): Order {
        val user = userService.getStreatsCustomerById(userId)
        val userCart = user.cart

        val shopId = userCart.shopId
        val shop = shopRepository.findStreatsShopByShopId(shopId)
        if (shop.isEmpty) {
            throw Exception("Shop not found")
        }
        val vendorId = shop.get().vendorId
        val vendorFcmToken = streatsVendorManagementService.getStreatsVendorByVendorId(vendorId).vendorFcmToken
        if (userCart.itemCount == 0) {
            throw CartException.EmptyCartException
        }
        return Order(
            orderId = "STREATS_${userId}_${ObjectId()}",
            shopId = userCart.shopId,
            userId = userId,
            username = user.username,
            itemCount = userCart.itemCount,
            items = userCart.cartItems,
            totalCost = userCart.totalCost,
            orderedTime = LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)),
            orderedDate = LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
            arrivalTime = LocalTime.now().plusMinutes(10).format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)),
            orderStatus = OrderStatus.IN_PROGRESS.name,
            paymentStatus = PaymentStatus.IN_PROGRESS.name,
            userFcmToken = user.fcmTokenOfCurrentLoggedInDevice,
            vendorFcmToken = vendorFcmToken
        )
    }


    /**
     * Update Order Status
     */
    private fun updatePaymentStatusInOrderRepo(orderId: String, paymentStatus: PaymentStatus): Order {
        val order = orderRepository.findOrderByOrderId(orderId)
        order.paymentStatus = paymentStatus.name

        orderRepository.save(order)

        return order
    }


    private fun addNewOrderToOngoingOrdersList(order: Order): Order {
        return try {
            val shopOptional = shopRepository.findStreatsShopByShopId(order.shopId)
            if (shopOptional.isEmpty) {
                throw Exception("Shop doesn't exist")
            }
            val shop = shopOptional.get()

            if (shop.ongoingOrders.contains(order)) {
                throw Exception("Order already exists")
            } else {
                shop.ongoingOrders.add(order)
                shopRepository.save(shop)
                order
            }

        } catch (e: Exception) {
            throw Exception("Something went wrong while adding new order to ongoing orders list")
        }

    }

    private fun extractOrderPaymentVerificationRequestParams(
        orderPaymentVerificationRequestParams: LinkedHashMap<String, String>
    ): OrderPaymentVerificationRequest {
        return OrderPaymentVerificationRequest(
            orderId = orderPaymentVerificationRequestParams[PARAM_ORDER_ID] ?: EMPTY,
            orderAmount = orderPaymentVerificationRequestParams[PARAM_ORDER_AMOUNT] ?: EMPTY,
            referenceId = orderPaymentVerificationRequestParams[PARAM_REFERENCE_ID] ?: EMPTY,
            txStatus = orderPaymentVerificationRequestParams[PARAM_TRANSACTION_STATUS] ?: EMPTY,
            paymentMode = orderPaymentVerificationRequestParams[PARAM_PAYMENT_MODE] ?: EMPTY,
            txMsg = orderPaymentVerificationRequestParams[PARAM_TRANSACTION_MESSAGE] ?: EMPTY,
            txTime = orderPaymentVerificationRequestParams[PARAM_TRANSACTION_TIME] ?: EMPTY,
            signature = orderPaymentVerificationRequestParams[PARAM_SIGNATURE] ?: EMPTY
        )
    }

    private fun createTokenRequest(order: Order): HttpEntity<CashfreeTokenRequestDTO> {

        val clientId = cashfreeConfig.clientId

        val clientSecret = cashfreeConfig.clientSecret

        val cashfreeTokenRequestDTO =
            CashfreeTokenRequestDTO(order.orderId, order.totalCost.toString(), orderCurrency = CURRENCY_RUPEES)

        val headers = HttpHeaders()
        headers.set(HEADER_CLIENT_ID, clientId)
        headers.set(HEADER_CLIENT_SECRET, clientSecret)

        return HttpEntity(cashfreeTokenRequestDTO, headers)

    }

    private fun getCftoken(tokenRequest: HttpEntity<CashfreeTokenRequestDTO>): CashfreeTokenResponseDTO {
        val restTemplate = RestTemplate()

        val tokenUrl = cashfreeConfig.tokenUrl

        val response = restTemplate.postForEntity(tokenUrl, tokenRequest, CashfreeTokenResponseDTO::class.java)

        if (response.statusCode == HttpStatus.OK && response.hasBody()) {
            return response.body!!
        } else throw Exception("Something went wrong while fetching cftoken ")

    }
}