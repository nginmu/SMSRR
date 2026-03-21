package uk.co.lamneth.smsremoterelay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Alignment
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.layout.Box
import androidx.activity.ComponentActivity

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
    val json = gson.toJson(codeslist)
    editor.putString("codeslist", json)
    editor.apply()
}

// Function to load the codeslist from SharedPreferences
private fun loadCodesList(context: Context): MutableMap<String, String> {
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = sharedPreferences.getString("codeslist", null)
    val type = object : TypeToken<MutableMap<String, String>>() {}.type
    return if (json != null) {
        gson.fromJson(json, type)
    } else {
        mutableMapOf() // Return an empty map if no data is found
    }
}

// Function to save phoneNo to SharedPreferences
private fun savePhoneNo(context: Context, phoneNo: String) {
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("phoneNo", phoneNo)
    editor.apply()
}

// Function to load phoneNo from SharedPreferences
private fun loadPhoneNo(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getString("phoneNo", "Please configure") ?: "Please configure"  // Return empty string if no data is found
}

@Composable
fun SettingsScreen(navController: NavController, context: Context) {
    // Create a mutable map of strings
    var codeslist by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var action by remember { mutableStateOf("") }
    var smscode by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var phoneNo by remember { mutableStateOf("") }

    // Load the codeslist from SharedPreferences when the composable is first launched
    LaunchedEffect(Unit) {
        codeslist = loadCodesList(context).toList() // Convert to list of pairs
        if (codeslist.isEmpty()) {
            // Initialize with a default key-value pair if the list is empty
            codeslist = listOf("Please configure" to "Please configure")
            saveCodesList(context, codeslist.toMap()) // Save the default pair to SharedPreferences
            message = "Initialized with default key-value pair."
        }
    }

    // Load phoneNo from SharedPreferences (or set default) when the composable is first launched
    // Chat GPT - can you suggest code for here?
    phoneNo = loadPhoneNo(context)

    // Use Box to overlay the buttons at the bottom
    Box(modifier = Modifier.fillMaxSize()) {

        // Use LazyColumn to make the entire screen scrollable
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item {
                Text(
                    text = "Configured Actions",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Left,
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            items(codeslist.toList()) { (k, v) ->
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Action: $k")
                            Text(text = "Code: $v")
                        }
                        Button(
                            onClick = {
                                // Update the codeslist by removing the item
                                codeslist = codeslist.filterNot { it.first == k }
                                saveCodesList(context, codeslist.toMap())
                                message = "Item deleted: $k"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFA52A2A)
                            )
                        ) {
                            Text("Remove")
                        }
                    }
                    HorizontalDivider(color = Color.Gray, thickness = 1.dp)
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Receiver phone number",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Left,
                )

                // Input for receiver phone number
                TextField(
                    value = phoneNo,
                    onValueChange = {
                        phoneNo = it
                        savePhoneNo(context, phoneNo)
                    },
                    label = { Text("Enter phone no.") }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Add a new action",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Left,
                )

                // Input for key
                TextField(
                    value = action,
                    onValueChange = { action = it },
                    label = { Text("Description of Action") }
                )

                // Input for value
                TextField(
                    value = smscode,
                    onValueChange = { smscode = it },
                    label = { Text("Corresponding SMS Code") }
                )

                Spacer(modifier = Modifier.height(2.dp))

                Button(onClick = {
                    if (action.isNotBlank() && smscode.isNotBlank()) {
                        // Update the codeslist by adding or modifying the item
                        codeslist = codeslist.filterNot { it.first == action } + (action to smscode)
                        saveCodesList(context, codeslist.toMap())
                        message = "Item added/modified: $action -> $smscode"
                        action = ""
                        smscode = ""
                    } else {
                        message = "Please enter both Action and SMS Code."
                    }
                }) {
                    Text("Add new action")
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Display feedback message
                if (message.isNotBlank()) {
                    Text(text = message)
                }
            }
        }

        // Floating buttons at the bottom
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { navController.navigate("main") },
                //modifier = Modifier.weight(1f)
            ) {
                Text("Home")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { navController.navigate("settings") },
                //modifier = Modifier.weight(1f)
            ) {
                Text("Set")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { navController.navigate("about") },
                //modifier = Modifier.weight(1f)
            ) {
                Text("Info")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { closeApp(context) },
                //modifier = Modifier.weight(1f)
            ) {
                Text("Exit")
            }
        }
    }
}