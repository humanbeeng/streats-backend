package app.streat.backend.order.service

import app.streat.backend.auth.service.StreatsUserService
import app.streat.backend.cart.service.CartService
import app.streat.backend.cart.service.exceptions.CartException
import app.streat.backend.core.config.CashfreeConfig
import app.streat.backend.core.util.Hmac256Util
import app.streat.backend.order.data.dto.cashfree_token.CashfreeTokenRequestDTO
import app.streat.backend.order.data.dto.cashfree_token.CashfreeTokenResponseDTO
import app.streat.backend.order.data.dto.order_verification.OrderVerificationRequestDTO
import app.streat.backend.order.domain.model.Order
import app.streat.backend.order.domain.model.OrderStatus
import app.streat.backend.order.domain.model.OrderWithToken
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@Service
class OrderServiceImpl(
    private val userService: StreatsUserService,
    private val cartService: CartService,
    private val cashfreeConfig: CashfreeConfig,
    private val hmac256Util: Hmac256Util
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

    override fun deleteAllOrders(userId: String) {
        val user = userService.getStreatsCustomer(userId)
        user.orders.removeAll(user.orders)
        userService.updateStreatsCustomer(user)
    }

    override fun verifyOrderPayment(userId: String, orderVerificationRequestDTO: OrderVerificationRequestDTO): Boolean {

        val dataString = orderVerificationRequestDTO.getDataString()
        val key = cashfreeConfig.clientSecret

        val calculatedSignature = hmac256Util.createBase64EncodedSignature(dataString, key)
        val givenSignature = orderVerificationRequestDTO.signature

        return calculatedSignature == givenSignature

    }

    private fun createOrder(userId: String): Order {
        val user = userService.getStreatsCustomer(userId)
        val userCart = user.cart

        if (userCart.itemCount == 0) {
            throw CartException.EmptyCartException("No items in cart to create order")
        }

        return Order(
            shopId = userCart.shopId,
            userId = userId,
            username = user.username,
            itemCount = userCart.itemCount,
            items = userCart.cartItems,
            totalCost = userCart.totalCost,
            orderedTime = LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)),
            orderedDate = LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
            arrivalTime = LocalTime.now().plusMinutes(10).format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)),
            orderStatus = OrderStatus.IN_PROGRESS.name
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
}