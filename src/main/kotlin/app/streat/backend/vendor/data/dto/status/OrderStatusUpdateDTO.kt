package app.streat.backend.vendor.data.dto.status

import app.streat.backend.order.domain.model.status.OrderStatus

data class OrderStatusUpdateDTO(
    val orderId: String,
    val orderStatus: String
) {
    fun toOrderStatus(): OrderStatus {
        return when (orderStatus) {
            OrderStatus.IN_PROGRESS.name -> OrderStatus.IN_PROGRESS
            OrderStatus.FULFILLED.name -> OrderStatus.FULFILLED
            else -> throw Exception("Invalid orderStatus")
        }
    }
}

