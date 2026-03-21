package uk.co.lamneth.smsremoterelay

import android.content.ClipboardManager
import android.content.Context // Import for accessing application environment and resources
import android.content.Intent
import androidx.navigation.NavController // Import for navigation between screens
import androidx.compose.foundation.layout.Box // Import for creating a container that can hold other composables
import androidx.compose.foundation.layout.Column // Import for arranging child composables in a vertical column
import androidx.compose.foundation.layout.padding // Import for adding padding around composables
import androidx.compose.foundation.layout.Spacer // Import for creating empty space between composables
import androidx.compose.foundation.layout.fillMaxSize // Import for making a composable fill the maximum size available
import androidx.compose.foundation.layout.height // Import for setting the height of a composable
import androidx.compose.material3.Button // Import for creating buttons
import androidx.compose.material3.MaterialTheme // Import for applying Material Design themes
import androidx.compose.material3.Text // Import for displaying text
import androidx.compose.ui.Alignment // Import for aligning composables within a container
import androidx.compose.ui.Modifier // Import for modifying the appearance and behavior of composables
import androidx.compose.ui.platform.LocalContext // Import for accessing the current context
import androidx.compose.ui.unit.dp // Import for defining dimensions in density-independent pixels
import androidx.compose.runtime.Composable // Import for defining composable functions
import androidx.compose.foundation.text.BasicText // Import for displaying basic text
import androidx.compose.ui.text.buildAnnotatedString // Import for building annotated strings with styles
import androidx.compose.ui.text.withStyle // Import for applying styles to text
import androidx.compose.ui.text.SpanStyle // Import for defining text styles
import androidx.compose.ui.text.font.FontWeight // Import for setting font weight
import androidx.compose.ui.unit.sp // Import for defining font sizes in scale-independent pixels
import androidx.compose.foundation.Image // Import for displaying images
import androidx.compose.foundation.clickable // Import for making a composable clickable
import androidx.compose.foundation.layout.Arrangement // Import for arranging child composables
import androidx.compose.foundation.layout.Row // Import for arranging child composables in a horizontal row
import androidx.compose.foundation.layout.fillMaxWidth // Import for making a composable fill the maximum width available
import androidx.compose.foundation.layout.size // Import for setting the size of a composable
import androidx.compose.foundation.layout.width // Import for setting the width of a composable
import androidx.compose.foundation.rememberScrollState // Import for remembering the scroll state
import androidx.compose.foundation.verticalScroll // Import for enabling vertical scrolling
import androidx.compose.ui.res.painterResource // Import for loading drawable resources
import androidx.activity.ComponentActivity // Import for accessing activity features
import androidx.compose.ui.text.style.TextDecoration
import android.net.Uri
import androidx.compose.ui.graphics.Color

// Function to close the app
private fun closeApp(context: Context) {
    if (context is ComponentActivity) {
        context.finishAffinity() // Closes the current activity and all parent activities
    }
}

private fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

// Composable function to display the About screen
@Composable
fun About(navController: NavController) {
    // Use LocalContext to get the current context for displaying Toast messages
    val context = LocalContext.current

    // Box is a container that allows stacking of child composables
    Box(
        modifier = Modifier.fillMaxSize(), // Make the Box fill the maximum size available
        contentAlignment = Alignment.TopCenter // Align content to the top center of the Box
    ) {
        // Column arranges its children vertically
        Column(
            modifier = Modifier
                .fillMaxSize() // Make the Column fill the maximum size available
                .verticalScroll(rememberScrollState()), // Enable vertical scrolling for the Column
            horizontalAlignment = Alignment.CenterHorizontally // Center align children horizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // Add vertical space at the top

            // Define the title text
            val text = "SMS Remote Relay"
            // Build an annotated string to style the title text
            val annotatedString = buildAnnotatedString {
                text.split(" ").forEach { word ->
                    // Add the first letter of each word with a larger font size and bold weight
                    withStyle(style = SpanStyle(fontSize = 48.sp, fontWeight = FontWeight.Bold)) {
                        append(word[0].toString()) // Append the first letter
                    }
                    // Add the rest of the word with a smaller font size
                    withStyle(style = SpanStyle(fontSize = 16.sp)) {
                        append(word.substring(1)) // Append the rest of the word
                    }
                    // Add a space after each word except the last one
                    append(" ")
                }
            }

            // Display the styled title text
            BasicText(text = annotatedString)

            Spacer(modifier = Modifier.height(16.dp)) // Add space below the title

            // Display an image with a specified resource ID and size
            Image(
                painter = painterResource(id = R.drawable.happyfone), // Load the image resource for the happy phone icon
                contentDescription = "Happy Phone Icon", // Provide a description for accessibility
                modifier = Modifier.size(100.dp) // Set the size of the image
            )

            Spacer(modifier = Modifier.height(32.dp)) // Add space below the header image

            // Display a description of the app's functionality
            Text(
                text = "This app is designed to " +
                        "issue multiple preconfigured SMS commands to GSM/LTE-driven multichannel relay boxes in " +
                        "a straightforward user-friendly way. " +
                        "It is free, asks for no permissions beyond the ability to send SMS, and is open-source.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp // Set the font size for the description
                ),
                modifier = Modifier.padding(horizontal = 16.dp) // Add horizontal padding
            )

            Spacer(modifier = Modifier.height(8.dp)) // Add space below the description

            // Provide instructions for adding a new action
            Text(
                text = "On the settings page, under 'Add a new action', enter a name for an action you wish a button in the app to make happen. " +
                        "Then, in the box below, enter the SMS code which will cause your relay box to flip the right " +
                        "switches to make that action happen. " +
                        "A working example might be.. Action: YARD LIGHTS ON; Code: SN1186COMXNXX... this would send the " +
                        "password 1186 to the relay box, and tell it to turn on the second relay whilst ignoring the state " +
                        "of the unit's other 3 relays. If you're not sure, ask the person who installed your relay box for the right codes.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp // Set the font size for the instructions
                ),
                modifier = Modifier.padding(horizontal = 16.dp) // Add horizontal padding
            )

            Spacer(modifier = Modifier.height(8.dp)) // Add space below the instructions

            // Explain how to add the new action to the list
            Text(
                text = "Next, click 'Add new action' and you'll see it appear in the list at the top under 'Configured Actions'. " +
                        "Now, when you go to the Home page, it will appear as a button you can press to easily send the SMS command to the relay box. " +
                        "You can go back to the settings page and add as many more actions, with their corresponding codes, as you like.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp // Set the font size for the explanation
                ),
                modifier = Modifier.padding(horizontal = 16.dp) // Add horizontal padding
            )

            Spacer(modifier = Modifier.height(8.dp)) // Add space below the explanation

            // Provide information about entering the telephone number
            Text(
                text = "You will also need to enter the telephone number of the SIM card in your relay box, " +
                        "on the settings page.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp // Set the font size for the information
                ),
                modifier = Modifier.padding(horizontal = 16.dp) // Add horizontal padding
            )

            Spacer(modifier = Modifier.height(8.dp)) // Add space below the information

            // Describe the types of devices the app can control
            Text(
                text = "The app is designed to issue SMS commands to devices such as the SM4-WLTE 4G 4 Channel " +
                        "Remote Switch Controller ('relay box') (pictured below) and many similar devices.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp // Set the font size for the description
                ),
                modifier = Modifier.padding(horizontal = 16.dp) // Add horizontal padding
            )

            Spacer(modifier = Modifier.height(24.dp)) // Add space below the device description

// Use a Box to center the image horizontally
            Box(
                modifier = Modifier
                    .fillMaxWidth() // Make the Box fill the maximum width available
                    .padding(horizontal = 16.dp), // Add horizontal padding
                contentAlignment = Alignment.Center // Center the content within the Box
            ) {
                // Display a single image of the relay switch controller
                Image(
                    painter = painterResource(id = R.drawable.relay), // Load the image resource for the relay switch controller
                    contentDescription = "Relay Switch Controller", // Provide a description for accessibility
                    modifier = Modifier.size(100.dp) // Set the size of the image
                )
            }


            Spacer(modifier = Modifier.height(24.dp)) // Add space below the image row

            // Donation info
            Text(
                text = "I wrote this for fun rather than profit, but donations " +
                        "(entirely voluntary) to support the private developer are " +
                        "very welcome via Bitcoin or Paypal:\n\n",

                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp // Set the font size for the description
                ),
                modifier = Modifier.padding(horizontal = 16.dp) // Add horizontal padding
            )

// This next bit presents a bitcoin address for donations
// it's blue, underlined and clickable so it works kinda like a weblink
// but it just copies the bitcoin address to the clipboard for ease.

            Text(
                text = "Developer's Bitcoin address:",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp // Set the font size for the information
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp) // Add horizontal padding
                    .align(Alignment.Start) // Align the text to the left
            )

            val context = LocalContext.current
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

// Bitcoin address
            val bitcoinAddress = "********************************"

// Create an annotated string for styling
            val bitcoinAnnotatedString = buildAnnotatedString {
                // Apply the style to the Bitcoin address: blue color, underlined, font size
                withStyle(style = SpanStyle(fontSize = 16.sp, color = Color.Blue, textDecoration = TextDecoration.Underline)) {
                    append(bitcoinAddress) // Append the Bitcoin address only once here
                }
            }

// Use BasicText to display the address, ensuring it is clickable
            BasicText(
                text = bitcoinAnnotatedString, // Use the annotated string with styling
                modifier = Modifier
                    .clickable {
                        // Copy the Bitcoin address to the clipboard when clicked
                        val clip = android.content.ClipData.newPlainText("Bitcoin Address", bitcoinAddress)
                        clipboardManager.setPrimaryClip(clip)
                    }
                    .padding(horizontal = 16.dp) // Padding around the text
                    .align(Alignment.Start) // Align the text to the left
            )

            Text(
                text = "(click the address to copy to clipboard)",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp // Set the font size for the information
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp) // Add horizontal padding
                    .align(Alignment.Start) // Align the text to the left
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = buildAnnotatedString {
                    append("Developer's PayPal:\n")

                    // Apply the style to the link text "Click here to donate" all at once
                    withStyle(style = SpanStyle(fontSize = 16.sp, color = Color.Blue, textDecoration = TextDecoration.Underline)) {
                        append("Click here to donate") // Append it only once with the correct style
                    }
                },
                modifier = Modifier
                    .clickable {
                        // Click action
                        openUrl(
                            context,
                            "https://www.paypal.com/donate/?" +
                                    "business=*************&" +
                                    "no_recurring=1&" +
                                    "item_name=Message+&" +
                                    "currency_code=***"
                        )
                    }
                    .padding(horizontal = 16.dp) // Add horizontal padding
                    .align(Alignment.Start) // Align the text to the left
            )

            Spacer(modifier = Modifier.height(24.dp)) // Add space below the donation info

            // Button to view the license
            Button(
                onClick = { navController.navigate("license") }, // Navigate to the license screen when clicked
            ) {
                Text("View license") // Button label
            }

            Spacer(modifier = Modifier.height(96.dp)) // Add space below the row of images

        }

        // Floating buttons at the bottom of the screen for navigation
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter) // Align the Row to the bottom center of the Box
                .padding(16.dp) // Add padding around the Row
                .fillMaxWidth(), // Make the Row fill the maximum width available
            horizontalArrangement = Arrangement.SpaceEvenly // Space buttons evenly within the Row
        ) {
            // Button to navigate to the Home screen
            Button(
                onClick = { navController.navigate("main") }, // Navigate to the main screen when clicked
            ) {
                Text("Home") // Button label
            }
            Spacer(modifier = Modifier.width(8.dp)) // Add space between buttons
            // Button to navigate to the Settings screen
            Button(
                onClick = { navController.navigate("settings") }, // Navigate to the settings screen when clicked
            ) {
                Text("Set") // Button label
            }
            Spacer(modifier = Modifier.width(8.dp)) // Add space between buttons
            // Button to navigate to the About screen
            Button(
                onClick = { navController.navigate("about") }, // Navigate to the about screen when clicked
            ) {
                Text("Info") // Button label
            }
            Spacer(modifier = Modifier.width(8.dp)) // Add space between buttons
            // Button to close the app
            Button(
                onClick = { closeApp(context) }, // Close the app when clicked
            ) {
                Text("Exit") // Button label
            }
        }
    }
}
