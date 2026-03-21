package uk.co.lamneth.smsremoterelay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.widget.Toast

// SmsSentReceiver class extends BroadcastReceiver to handle the result of SMS sending
class SmsSentReceiver(private val onComplete: (() -> Unit)? = null) : BroadcastReceiver() {

    // Override the onReceive method to handle the broadcast when an SMS is sent
    override fun onReceive(context: Context, intent: Intent) {
        // The resultCode indicates the outcome of the SMS sending operation
        when (resultCode) {
            SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
                Toast.makeText(context, "Generic failure", Toast.LENGTH_LONG).show()
            }
            SmsManager.RESULT_ERROR_NO_SERVICE -> {
                Toast.makeText(context, "No service", Toast.LENGTH_LONG).show()
            }
            SmsManager.RESULT_ERROR_NULL_PDU -> {
                Toast.makeText(context, "Null PDU", Toast.LENGTH_LONG).show()
            }
            SmsManager.RESULT_ERROR_RADIO_OFF -> {
                Toast.makeText(context, "Radio off", Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(context, "SMS sent successfully!", Toast.LENGTH_LONG).show()
            }
        }

        // Call the onComplete callback function if it is not null
        onComplete?.invoke()
    }
}