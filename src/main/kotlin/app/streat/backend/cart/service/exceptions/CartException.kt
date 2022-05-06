package app.streat.backend.cart.service.exceptions

sealed class CartException(override val message: String) : Exception(message) {

    object ItemFromDifferentShopException : CartException("Item from different shop")

    object ItemNotFoundFromShopException : CartException("No dish found from given shop ID")

    object ItemFetchFromCartException : CartException("Something went wrong while fetching cart items")

    object EmptyCartException : CartException("No items in cart")
}
