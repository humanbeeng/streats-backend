package app.streat.backend.order.domain.model.order


/**
 * Refactor all DTO to match with client
 */
data class OrderWithToken(
    val cftoken: String,
    val orderId: String,
    val orderAmount: String,
    val orderCurrency: String,
    val status: String,
    val stage: String,
    val appId: String
)
