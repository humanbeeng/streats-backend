package app.streat.backend.order.data.dto.cashfree_token

data class CashfreeTokenRequestDTO(
    val orderId: String,
    val orderAmount: String,
    val orderCurrency: String
)
