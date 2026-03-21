package uk.co.lamneth.smsremoterelay

import android.content.Context // Import for accessing application environment and resources
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
import androidx.compose.ui.unit.sp // Import for defining font sizes in scale-independent pixels
import androidx.compose.foundation.layout.Arrangement // Import for arranging child composables
import androidx.compose.foundation.layout.Row // Import for arranging child composables in a horizontal row
import androidx.compose.foundation.layout.fillMaxWidth // Import for making a composable fill the maximum width available
import androidx.compose.foundation.layout.width // Import for setting the width of a composable
import androidx.compose.foundation.rememberScrollState // Import for remembering the scroll state
import androidx.compose.foundation.verticalScroll // Import for enabling vertical scrolling
import androidx.activity.ComponentActivity // Import for accessing activity features
import androidx.compose.runtime.remember
import java.io.BufferedReader
import java.io.InputStreamReader

// Function to close the app
private fun closeApp(context: Context) {
    if (context is ComponentActivity) {
        context.finishAffinity() // Closes the current activity and all parent activities
    }
}

// Function to read the license file from assets
fun readLicenseFile(context: Context): String {
    val inputStream = context.assets.open("LICENSE.txt")
    val reader = BufferedReader(InputStreamReader(inputStream))
    return reader.use { it.readText() } // Read the entire file content
}

// Composable function to display the License screen
@Composable
fun License(navController: NavController) {
    // Use LocalContext to get the current context for displaying Toast messages
    val context = LocalContext.current

    val licenseText = remember { readLicenseFile(context) } // Read the license text

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

            // Display the license text
            Text(
                text = licenseText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 12.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

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

