package app.streat.backend.order.service

import app.streat.backend.auth.service.StreatsUserService
import app.streat.backend.cart.service.CartService
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
 *
 * TODO : 2. Add OrderExceptions while introducing logger
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

        val user = userService.getStreatsCustomer(userId)

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
            userFcmToken = user.fcmTokenOfCurrentLoggedInDevice
        )
    }

    private fun createTokenRequest(order: Order): HttpEntity<CashfreeTokenRequestDTO> {

        val clientId = cashfreeConfig.clientId

        val clientSecret = cashfreeConfig.clientSecret

//        TODO : Refactor orderCurrency and separate out request building to functions
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