package app.streat.backend.order.service

import app.streat.backend.auth.service.StreatsUserService
import app.streat.backend.cart.service.CartService
import app.streat.backend.cart.service.exceptions.CartException
import app.streat.backend.core.config.CashfreeConfig
import app.streat.backend.core.util.Hmac256Util
import app.streat.backend.order.data.dto.cashfree_token.CashfreeTokenRequestDTO
import app.streat.backend.order.data.dto.cashfree_token.CashfreeTokenResponseDTO
import app.streat.backend.order.data.dto.order_verification.OrderPaymentVerificationRequest
import app.streat.backend.order.data.repository.OrderRepository
import app.streat.backend.order.domain.model.order.Order
import app.streat.backend.order.domain.model.order.OrderWithToken
import app.streat.backend.order.domain.model.status.OrderStatus
import app.streat.backend.order.domain.model.status.PaymentStatus
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
 */

@Service
class OrderServiceImpl(
    private val userService: StreatsUserService,
    private val cartService: CartService,
    private val cashfreeConfig: CashfreeConfig,
    private val hmac256Util: Hmac256Util,
    private val orderRepository: OrderRepository
) : OrderService {
    override fun getAllOrders(userId: String): List<Order> {

        val user = userService.getStreatsCustomer(userId)
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

        orderRepository.save(order)

        val tokenRequest = createTokenRequest(order)

        val tokenResponse = getCftoken(tokenRequest)

        return OrderWithToken(
            tokenResponse.cftoken,
            order.orderId,
            orderCurrency = "INR",
            orderAmount = order.totalCost.toString(),
            appId = cashfreeConfig.clientId,
            stage = "TEST",
            status = tokenResponse.status
        )

    }

//    TODO : Remove this method
    override fun placeOrder(userId: String): Order {

        val user = userService.getStreatsCustomer(userId)

        val order = createOrder(userId)

        if (user.orders.size > 2) {
            user.orders.removeLast()
        }
        user.orders.add(order)
        userService.updateStreatsCustomer(user)
        cartService.clearCart(userId)
        return order
    }

    /**
     * TODO : Delete this method or move to Admin service
     */
    override fun deleteAllOrders(userId: String) {
        val user = userService.getStreatsCustomer(userId)
        user.orders.removeAll(user.orders)
        userService.updateStreatsCustomer(user)
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
     */
    override fun verifyOrderPayment(orderPaymentVerificationRequestParams: LinkedHashMap<String, String>): Boolean {
        val paymentVerificationData =
            extractOrderPaymentVerificationRequestParams(orderPaymentVerificationRequestParams)


        val isSignatureValid =
            hmac256Util.verifySignature(
                paymentVerificationData.getDataString(),
                paymentVerificationData.signature,
                cashfreeConfig.clientSecret
            )

//        Update OrderDB about success or failure
        val orderId: String = paymentVerificationData.orderId

        if (isSignatureValid) {
            updateOrderStatus(orderId, PaymentStatus.SUCCESS)
        } else {
            updateOrderStatus(orderId, PaymentStatus.FAILURE)
        }

        return isSignatureValid
    }

    private fun createOrder(userId: String): Order {
        val user = userService.getStreatsCustomer(userId)
        val userCart = user.cart

        if (userCart.itemCount == 0) {
            throw CartException.EmptyCartException("No items in cart to create order")
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
            userFcmToken = user.fcmTokenOfCurrentLoggedInDevice
        )
    }

    private fun createTokenRequest(order: Order): HttpEntity<CashfreeTokenRequestDTO> {

        val clientId = cashfreeConfig.clientId

        val clientSecret = cashfreeConfig.clientSecret

//        TODO : Refactor orderCurrency and separate out request building to functions
        val cashfreeTokenRequestDTO =
            CashfreeTokenRequestDTO(order.orderId, order.totalCost.toString(), orderCurrency = "INR")

        val headers = HttpHeaders()
        headers.set("x-client-id", clientId)
        headers.set("x-client-secret", clientSecret)

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

    private fun extractOrderPaymentVerificationRequestParams(
        orderPaymentVerificationRequestParams: LinkedHashMap<String, String>
    ): OrderPaymentVerificationRequest {
        return OrderPaymentVerificationRequest(
            orderId = orderPaymentVerificationRequestParams["orderId"] ?: "",
            orderAmount = orderPaymentVerificationRequestParams["orderAmount"] ?: "",
            referenceId = orderPaymentVerificationRequestParams["referenceId"] ?: "",
            txStatus = orderPaymentVerificationRequestParams["txStatus"] ?: "",
            paymentMode = orderPaymentVerificationRequestParams["paymentMode"] ?: "",
            txMsg = orderPaymentVerificationRequestParams["txMsg"] ?: "",
            txTime = orderPaymentVerificationRequestParams["txTime"] ?: "",
            signature = orderPaymentVerificationRequestParams["signature"] ?: ""
        )
    }


    /**
     * Update Order Status
     */
    private fun updateOrderStatus(orderId: String, paymentStatus: PaymentStatus) {
        val order = orderRepository.findOrderByOrderId(orderId)
        val userId = order.userId

        order.paymentStatus = paymentStatus.name

        saveOrder(userId, order)

        cartService.clearCart(userId)

        orderRepository.save(order)

    }

    private fun saveOrder(userId: String, order: Order) {
        val user = userService.getStreatsCustomer(userId)
        user.orders.add(order)
        userService.updateStreatsCustomer(user)
    }

}