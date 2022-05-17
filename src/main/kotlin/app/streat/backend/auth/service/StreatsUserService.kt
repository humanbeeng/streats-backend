package app.streat.backend.auth.service

import app.streat.backend.auth.data.repository.AuthRepository
import app.streat.backend.auth.domain.models.user.StreatsCustomer
import app.streat.backend.core.util.CoreConstants
import app.streat.backend.order.domain.model.order.Order
import app.streat.backend.order.domain.model.status.OrderStatus
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class StreatsUserService(private val repo: AuthRepository) : UserDetailsService {

    override fun loadUserByUsername(firebaseId: String): UserDetails {
        val streatsUser = repo.findStreatsCustomerByFirebaseUID(firebaseId)

        return User
            .withUsername(streatsUser.firebaseUID)
            .authorities(streatsUser.roles.map { SimpleGrantedAuthority(it) }.toMutableList())
            .build()

    }

    fun createStreatsCustomer(streatsCustomer: StreatsCustomer): StreatsCustomer {
        return repo.save(streatsCustomer)
    }

    fun updateStreatsCustomer(streatsCustomer: StreatsCustomer): StreatsCustomer {
        return repo.save(streatsCustomer)
    }

    fun getStreatsCustomerById(id: String): StreatsCustomer {
        return repo.findStreatsCustomerByFirebaseUID(id)
    }

    fun checkUserExists(id: String): Boolean {
        return repo.existsStreatsCustomerByFirebaseUID(id)
    }

    fun addOrderToOrderHistory(order: Order): Order {
        val userId = order.userId
        val user = getStreatsCustomerById(userId)
        user.orders.add(order)
        updateStreatsCustomer(user)
        return order
    }

    fun updateOrderStatusInOrderHistory(userId: String, orderId: String, orderStatus: OrderStatus): StreatsCustomer {
        val user = getStreatsCustomerById(userId)

        user.orders.find { it.orderId == orderId }?.orderStatus = orderStatus.name

        return updateStreatsCustomer(user)
    }

    fun clearCart(userId: String) {
        val user = getStreatsCustomerById(userId)
        user.cart.cartItems = mutableMapOf()
        user.cart.totalCost = 0.00
        user.cart.itemCount = 0
        user.cart.shopId = CoreConstants.EMPTY

        updateStreatsCustomer(user)

    }

    fun pushToUserOrderHistory(order: Order) {
        val user = getStreatsCustomerById(order.userId)
        user.orders.add(order)
        updateStreatsCustomer(user)
    }

}