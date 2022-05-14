package app.streat.backend.notification.service

import app.streat.backend.auth.utils.AuthConstants.JWT_ISSUER
import app.streat.backend.order.domain.model.order.Order
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.springframework.stereotype.Service


@Service
class NotificationServiceImpl(
    private val firebaseMessaging: FirebaseMessaging
) : NotificationService {

    override fun sendPushNotification(fcmToken: String, message: Message): Boolean {

        return try {
            firebaseMessaging.send(message)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun notifyOrderToUser(order: Order): Boolean {

        return try {
            val message = Message.builder().setToken(order.userFcmToken).setNotification(
                Notification.builder().setTitle(JWT_ISSUER).setBody("Your order has been placed !").build()
            ).build()

            sendPushNotification(order.userFcmToken, message)

            true

        } catch (e: Exception) {
            false
        }

    }

    //    TODO : Refactor this
    override fun notifyOrderToVendor(order: Order): Boolean {
        return try {
            val message = Message.builder().setToken(order.vendorFcmToken).setNotification(
                Notification.builder().setTitle(JWT_ISSUER).setBody("New order has been placed!").build()
            ).build()

            sendPushNotification(order.vendorFcmToken, message)

            true

        } catch (e: Exception) {
            false
        }
    }
}