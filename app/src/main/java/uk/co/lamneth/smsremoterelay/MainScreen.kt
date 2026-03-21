package uk.co.lamneth.smsremoterelay

import androidx.navigation.NavController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import android.content.Context
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.ui.platform.LocalContext
import android.os.Handler
import android.os.Looper
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.core.content.ContextCompat
import android.Manifest
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import java.util.regex.Pattern
import androidx.activity.ComponentActivity
import androidx.compose.material3.CircularProgressIndicator

// Function to close the app
private fun closeApp(context: Context) {
    if (context is ComponentActivity) {
        context.finishAffinity() // Closes the current activity and all parent activities
    }
}

// Function to save the codeslist to SharedPreferences
private fun saveCodesList(context: Context, codeslist: Map<String, String>) {
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val gson = Gson()
    val json = gson.toJson(codeslist) // Convert the codeslist map to JSON format
    editor.putString("codeslist", json) // Save the JSON string in SharedPreferences
    editor.apply() // Apply changes asynchronously
}

// Function to load the codeslist from SharedPreferences
private fun loadCodesList(context: Context): MutableMap<String, String> {
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = sharedPreferences.getString("codeslist", null) // Retrieve the JSON string
    val type = object : TypeToken<MutableMap<String, String>>() {}.type // Define the type for deserialization
    return if (json != null) {
        gson.fromJson(json, type) // Deserialize JSON back to a MutableMap
    } else {
        mutableMapOf() // Return an empty map if no data is found
    }
}

// Function to validate phone number
private fun isValidPhoneNumber(phoneNumber: String): Boolean {
    // Check if the phone number is not empty and does not exceed 20 characters
    if (phoneNumber.isEmpty() || phoneNumber.length > 20) {
        return false
    }
    // Regular expression to match valid phone number format
    val pattern = Pattern.compile("^[\\d\\s+]+$")
    return pattern.matcher(phoneNumber).matches() // Return true if the phone number matches the pattern
}

// Function to send SMS
private fun sendSms(context: Context, phoneNumber: String, message: String, onComplete: () -> Unit) {
    // Validate the phone number
    if (!isValidPhoneNumber(phoneNumber)) {
        Toast.makeText(context, "Invalid phone number format.", Toast.LENGTH_SHORT).show()
        onComplete() // Call onComplete even if sending fails
        return
    }

    if (phoneNumber.isNotEmpty() && message.isNotEmpty()) {
        try {
            val smsManager: SmsManager = context.getSystemService(SmsManager::class.java) // Get the SMS manager
            val sentIntent = PendingIntent.getBroadcast(
                context,
                0,
                Intent("SMS_SENT"), // Intent to broadcast when SMS is sent
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Create a receiver to handle the SMS sent result
            val smsSentReceiver = SmsSentReceiver { onComplete() } // Pass the callback to be called on completion
            context.registerReceiver(smsSentReceiver, IntentFilter("SMS_SENT"), Context.RECEIVER_NOT_EXPORTED)

            // Send the SMS message
            smsManager.sendTextMessage(phoneNumber, null, message, sentIntent, null)

            // Unregister the receiver after a delay to avoid memory leaks
            Handler(Looper.getMainLooper()).postDelayed({
                context.unregisterReceiver(smsSentReceiver)
            }, 5000) // Delay of 5 seconds
        } catch (e: Exception) {
            Toast.makeText(context, "${e.message}", Toast.LENGTH_LONG).show() // Show error message if sending fails
            onComplete() // Call onComplete even if sending fails
        }
    } else {
        Toast.makeText(context, "Phone number or message is empty", Toast.LENGTH_SHORT).show() // Notify if inputs are invalid
        onComplete() // Call onComplete if inputs are invalid
    }
}

// Function to load phoneNo from SharedPreferences
private fun loadPhoneNo(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getString("phoneNo", "Please configure") ?: "Please configure" // Return default message if no data is found
}

@Composable
fun MainScreen(navController: NavController, context: Context) {

    // For keeping track of button status indicators
    var sendingIndex by remember { mutableStateOf(-1) } // -1 means no button is currently sending

    var phoneNo by remember { mutableStateOf("") }
    phoneNo = loadPhoneNo(context) // Load the phone number from SharedPreferences
    var hasSmsPermission by remember { mutableStateOf(false) } // State to track SMS permission

    // Create a launcher to request SMS permission
    val smsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasSmsPermission = isGranted // Update permission state based on user response
    }

    // Create a mutable map of strings to hold action-code pairs
    var codeslist by remember { mutableStateOf(mutableMapOf<String, String>()) }

    // Load the codeslist from SharedPreferences when the composable is first launched
    LaunchedEffect(Unit) {
        val loadedCodesList = loadCodesList(context) // Load existing codes
        if (loadedCodesList.isEmpty()) {
            // Initialize with a default key-value pair if the map is empty
            val defaultCodesList = mutableMapOf("Please configure" to "Please configure")
            saveCodesList(context, defaultCodesList) // Save the default pair to SharedPreferences
            codeslist = defaultCodesList // Update the state immediately
        } else {
            codeslist = loadedCodesList // Update the state with loaded data
        }
    }

    // Check for SMS permission when the composable is first launched
    LaunchedEffect(Unit) {
        hasSmsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
        if (!hasSmsPermission) {
            smsPermissionLauncher.launch(Manifest.permission.SEND_SMS) // Request SMS permission if not granted
        }
    }

    var ctxt = LocalContext.current // Get the current context

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Use LazyColumn to make the entire screen scrollable
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp)) // Spacer for vertical spacing
                Text("Remote Controller", style = MaterialTheme.typography.headlineMedium) // Title of the screen
                Spacer(modifier = Modifier.height(32.dp)) // Spacer for vertical spacing
            }

            // This wasn't firing on the very first load because k and v were empty.
            // See 'hammer' above.
            items(codeslist.toList()) { (action, code) -> // Use action for label and code for SMS
                Column(modifier = Modifier.padding(18.dp)) { // Adding padding for better spacing
                    Button(
                        onClick = {
                            if (hasSmsPermission) {
                                sendingIndex = codeslist.toList().indexOfFirst { it.first == action } // Set the index of the sending button
                                sendSms(ctxt, phoneNo, code) { // Use the code for sending SMS
                                    sendingIndex = -1 // Reset sending index after sending
                                }
                            } else {
                                Toast.makeText(ctxt, "SMS permission is required to send messages.", Toast.LENGTH_SHORT).show() // Notify if permission is not granted
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA52A2A)), // Set button color
                        modifier = Modifier
                            .fillMaxWidth() // Make the button fill the width
                            .height(64.dp) // Set the height of the button
                    ) {
                        // Show a progress indicator if this button is currently sending an SMS
                        if (sendingIndex == codeslist.toList().indexOfFirst { it.first == action }) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp), // Size of the progress indicator
                                color = Color.White // Color of the progress indicator
                            )
                        } else {
                            Text(
                                text = action, // Display the action label
                                style = TextStyle(
                                    fontSize = 30.sp, // Adjust the font size as needed
                                    fontWeight = FontWeight.Bold // Set the font weight to bold
                                ),
                                maxLines = 1, // Prevents text from wrapping
                                overflow = TextOverflow.Ellipsis // Adds ellipsis if text is too long
                            )
                        }
                    }
                }
            }
        }

        // Floating buttons at the bottom for navigation
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter) // Align the row to the bottom center
                .padding(16.dp) // Add padding around the row
                .fillMaxWidth(), // Make the row fill the width
            horizontalArrangement = Arrangement.SpaceEvenly // Space buttons evenly
        ) {
            Button(
                onClick = { navController.navigate("main") }, // Navigate to the main screen
            ) {
                Text("Home") // Button label
            }
            Spacer(modifier = Modifier.width(8.dp)) // Spacer for horizontal spacing
            Button(
                onClick = { navController.navigate("settings") }, // Navigate to the settings screen
            ) {
                Text("Set") // Button label
            }
            Spacer(modifier = Modifier.width(8.dp)) // Spacer for horizontal spacing
            Button(
                onClick = { navController.navigate("about") }, // Navigate to the about screen
            ) {
                Text("Info") // Button label
            }
            Spacer(modifier = Modifier.width(8.dp)) // Spacer for horizontal spacing
            Button(
                onClick = { closeApp(context) }, // Close the app
            ) {
                Text("Exit") // Button label
            }
        }
    }
}