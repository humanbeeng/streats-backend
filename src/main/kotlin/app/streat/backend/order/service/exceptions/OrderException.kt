package app.streat.backend.order.service.exceptions

sealed class OrderException(override val message : String) : Exception(message){
}
