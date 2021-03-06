package org.stepik.android.view.notification

import android.app.Notification

abstract class NotificationDelegate(
    val id: String,
    private val notificationManager: StepikNotificationManager
) {

    abstract fun onNeedShowNotification()

    fun rescheduleNotification() {
        notificationManager.rescheduleActiveNotification(id)
    }

    protected fun scheduleNotificationAt(timestamp: Long) {
        notificationManager.scheduleNotification(id, timestamp)
    }

    protected fun showNotification(id: Long, notification: Notification) {
        notificationManager.showNotification(id, notification)
    }
}