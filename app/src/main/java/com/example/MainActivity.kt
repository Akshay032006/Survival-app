package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ClayBottomNavigation
import com.example.ui.components.LocationPickerDialog
import com.example.ui.components.PlaceDetailDialog
import com.example.ui.screens.*
import com.example.ui.theme.ClayBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.SurvivalViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        SurvivalAppRoot()
      }
    }
  }
}

@Composable
fun SurvivalAppRoot(
  viewModel: SurvivalViewModel = viewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  val savedPlaceIds by viewModel.savedPlaceIds.collectAsState()
  val context = LocalContext.current

  // Permission Launcher for Location and Audio
  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    if (locationGranted) {
      viewModel.autoDetectLocation()
    }
  }

  // Request permissions once at launch
  LaunchedEffect(Unit) {
    val hasFineLocation = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val hasAudio = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    if (!hasFineLocation || !hasAudio) {
      permissionLauncher.launch(
        arrayOf(
          Manifest.permission.ACCESS_FINE_LOCATION,
          Manifest.permission.ACCESS_COARSE_LOCATION,
          Manifest.permission.RECORD_AUDIO
        )
      )
    }

    // Populate initial survival multi-request search so the user sees immediate results
    viewModel.executeSearch("I'm new here. I need a cheap gym, supermarket, good food and a place to stay near me.")
  }

  // Toast message trigger
  LaunchedEffect(uiState.toastMessage) {
    uiState.toastMessage?.let { msg ->
      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
      viewModel.clearToast()
    }
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .statusBarsPadding()
      .navigationBarsPadding(),
    containerColor = ClayBackground
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Screen View
      Crossfade(
        targetState = uiState.currentTab,
        label = "tab_crossfade"
      ) { tab ->
        when (tab) {
          AppTab.HOME -> HomeScreen(viewModel = viewModel)
          AppTab.EXPLORE -> ExploreScreen(viewModel = viewModel)
          AppTab.TRIPS -> TripsScreen(viewModel = viewModel)
          AppTab.TOOLS -> ToolsScreen(viewModel = viewModel)
          AppTab.PROFILE -> ProfileScreen(viewModel = viewModel)
        }
      }

      // Floating Clay Bottom Navigation Bar
      ClayBottomNavigation(
        currentTab = uiState.currentTab,
        onTabSelected = { viewModel.setTab(it) },
        modifier = Modifier.align(Alignment.BottomCenter)
      )

      // Place Details Modal Dialog
      uiState.activeDetailPlace?.let { place ->
        PlaceDetailDialog(
          place = place,
          isSaved = savedPlaceIds.contains(place.id),
          onDismiss = { viewModel.closePlaceDetails() },
          onToggleSave = { viewModel.toggleSavePlace(place.id) }
        )
      }

      // Location Picker Modal Dialog
      if (uiState.showLocationPicker) {
        LocationPickerDialog(
          currentLocation = uiState.currentLocation,
          onDismiss = { viewModel.showLocationPicker(false) },
          onLocationSelected = { loc -> viewModel.setLocation(loc) }
        )
      }
    }
  }
}
