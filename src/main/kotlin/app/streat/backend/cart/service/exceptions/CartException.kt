package app.streat.backend.cart.service.exceptions

sealed class CartException(override val message: String) : Exception(message) {
    class ItemFromDifferentShopException(override val message: String) : CartException(message)
    class ItemNotFoundFromShopException(override val message: String) : CartException(message)
    class ItemFetchFromCartException(override val message: String) : CartException(message)
    class EmptyCartException(override val message: String) : CartException(message)
}
