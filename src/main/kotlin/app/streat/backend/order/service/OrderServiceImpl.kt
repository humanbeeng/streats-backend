package app.streat.backend.order.service

import app.streat.backend.auth.service.StreatsUserService
import app.streat.backend.cart.service.CartService
import app.streat.backend.cart.service.exceptions.CartException
import app.streat.backend.order.domain.model.Order
import app.streat.backend.order.domain.model.OrderStatus
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@Service
class OrderServiceImpl(
    private val userService: StreatsUserService, private val cartService: CartService
) : OrderService {
    override fun getAllOrders(userId: String): List<Order> {

        val user = userService.getStreatsCustomer(userId)
        return user.orders

    }

    override fun placeOrder(userId: String): Order {
        val user = userService.getStreatsCustomer(userId)
        val userCart = user.cart

        if (userCart.itemCount == 0) {
            throw CartException.EmptyCartException("No items to place order")
        }

        val newOrder =
            Order(
                shopId = userCart.shopId,
                userId = userId,
                username = user.username,
                itemCount = userCart.itemCount,
                items = userCart.cartItems,
                totalCost = userCart.totalCost,
                orderedTime = LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)),
                orderedDate = LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                arrivalTime = LocalTime.now().plusMinutes(10)
                    .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)),
                orderStatus = OrderStatus.IN_PROGRESS.name
            )
        if (user.orders.size > 2) {
            user.orders.removeLast()
        }
        user.orders.add(newOrder)
        userService.updateStreatsCustomer(user)
        cartService.clearCart(userId)
        return newOrder
    }

    override fun deleteAllOrders(userId: String) {
        val user = userService.getStreatsCustomer(userId)
        user.orders.removeAll(user.orders)
        userService.updateStreatsCustomer(user)
    }

}