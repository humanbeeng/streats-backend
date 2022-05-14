package app.streat.backend.notification.service

import app.streat.backend.order.domain.model.order.Order
import com.google.firebase.messaging.Message
import org.springframework.stereotype.Service


@Service
interface NotificationService {

    fun sendPushNotification(fcmToken: String, message: Message): Boolean

    fun notifyOrderToUser(order: Order): Boolean

    fun notifyOrderToVendor(order: Order): Boolean

}