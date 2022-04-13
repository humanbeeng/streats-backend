package app.streat.backend.order.data.dto.order_verification

data class OrderVerificationRequestDTO(
    val orderId: String,
    val orderAmount: String,
    val referenceId: String,
    val txStatus: String,
    val paymentMode: String,
    val txMsg: String,
    val txTime: String,
    val signature: String
) {
    fun getDataString(): String {
        return orderId + orderAmount + referenceId + txStatus + paymentMode + txMsg + txTime
    }
}
