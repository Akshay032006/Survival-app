package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserPreferences
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.SurvivalViewModel

@Composable
fun ProfileScreen(
  viewModel: SurvivalViewModel,
  modifier: Modifier = Modifier
) {
  val userPrefs by viewModel.userPreferences.collectAsState()
  val savedPlaceIds by viewModel.savedPlaceIds.collectAsState()
  val uiState by viewModel.uiState.collectAsState()
  val allPlaces = remember(uiState.currentLocation) { viewModel.repository.getAllPlaces(uiState.currentLocation) }
  val savedPlaces = remember(savedPlaceIds, allPlaces) {
    allPlaces.filter { savedPlaceIds.contains(it.id) }
  }
  val context = LocalContext.current

  var profileTab by remember { mutableStateOf(0) } // 0: Saved Places, 1: Preferences, 2: Privacy

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(ClayBackground),
    contentPadding = PaddingValues(bottom = 96.dp, top = 16.dp, start = 16.dp, end = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Box(
          modifier = Modifier
            .size(60.dp)
            .shadow(4.dp, CircleShape, ambientColor = ClayShadowAmbient, spotColor = ClayShadowSoft)
            .clip(CircleShape)
            .background(ClayTerracottaLight)
            .border(2.dp, ClayTerracotta, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text("🧭", fontSize = 28.sp)
        }

        Column {
          Text(
            text = userPrefs.preferredName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ClayTextPrimary
          )
          Text(
            text = "${userPrefs.budgetTier} • ${userPrefs.foodPreference} • In ${uiState.currentLocation}",
            fontSize = 12.sp,
            color = ClayTextSecondary
          )
        }
      }
    }

    // Tab switcher
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .shadow(2.dp, RoundedCornerShape(16.dp), ambientColor = ClayShadowAmbient, spotColor = ClayShadowSoft)
          .clip(RoundedCornerShape(16.dp))
          .background(ClaySurfaceElevated)
          .border(1.dp, ClayBorderLight, RoundedCornerShape(16.dp))
          .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        val tabs = listOf("Saved Places (${savedPlaces.size})", "Personalization", "Privacy & Safety")
        tabs.forEachIndexed { idx, title ->
          val isSelected = profileTab == idx
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) ClayTerracotta else Color.Transparent)
              .clickable { profileTab = idx }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = title,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else ClayTextSecondary
            )
          }
        }
      }
    }

    when (profileTab) {
      0 -> {
        // SAVED PLACES LIST
        if (savedPlaces.isEmpty()) {
          item {
            ClayCard(modifier = Modifier.fillMaxWidth()) {
              Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text("🔖", fontSize = 32.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("No saved places yet", fontWeight = FontWeight.Bold, color = ClayTextPrimary)
                Text("Tap the bookmark icon on any recommendation card to save it here.", fontSize = 12.sp, color = ClayTextSecondary)
              }
            }
          }
        } else {
          items(savedPlaces) { place ->
            PlaceClayCard(
              place = place,
              isSaved = true,
              onToggleSave = { viewModel.toggleSavePlace(place.id) },
              onClick = { viewModel.openPlaceDetails(place) },
              onDirections = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:${place.latitude},${place.longitude}?q=${Uri.encode(place.name)}"))
                try { context.startActivity(intent) } catch (e: Exception) {}
              },
              onCall = {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${place.phone ?: "+91 98765 43210"}"))
                try { context.startActivity(intent) } catch (e: Exception) {}
              }
            )
          }
        }
      }

      1 -> {
        // PERSONALIZATION PREFERENCES
        item {
          ClayCard(modifier = Modifier.fillMaxWidth()) {
            Text("Tailor AI Reasoning to Your Lifestyle", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ClayTextPrimary)
            Text("Survival prioritizes options matching your habits:", fontSize = 12.sp, color = ClayTextSecondary)

            Spacer(modifier = Modifier.height(14.dp))
            Text("Preferred Budget Tier", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ClayTextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              listOf("Budget", "Moderate", "Premium").forEach { b ->
                val active = userPrefs.budgetTier == b
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (active) ClayTerracotta else ClaySurfaceElevated)
                    .clickable { viewModel.updatePreferences(userPrefs.copy(budgetTier = b)) }
                    .padding(vertical = 8.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(b, color = if (active) Color.White else ClayTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text("Food & Dietary Preference", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ClayTextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              listOf("Vegetarian", "Non-Vegetarian", "Vegan").forEach { diet ->
                val active = userPrefs.foodPreference == diet
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (active) ClaySage else ClaySurfaceElevated)
                    .clickable { viewModel.updatePreferences(userPrefs.copy(foodPreference = diet)) }
                    .padding(vertical = 8.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(diet, color = if (active) Color.White else ClayTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text("Preferred Stay Type", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ClayTextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              listOf("PG / Hostel", "Budget Hotel", "Rental Flat").forEach { st ->
                val active = userPrefs.stayType == st
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (active) ClayBlue else ClaySurfaceElevated)
                    .clickable { viewModel.updatePreferences(userPrefs.copy(stayType = st)) }
                    .padding(vertical = 8.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(st, color = if (active) Color.White else ClayTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text("Max Walking Distance (${userPrefs.maxDistanceKm} km)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ClayTextSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              listOf(1.0, 2.5, 5.0).forEach { dist ->
                val active = userPrefs.maxDistanceKm == dist
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (active) ClayAmber else ClaySurfaceElevated)
                    .clickable { viewModel.updatePreferences(userPrefs.copy(maxDistanceKm = dist)) }
                    .padding(vertical = 8.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text("${dist} km", color = if (active) Color.White else ClayTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }

      2 -> {
        // PRIVACY & SAFETY
        item {
          ClayCard(modifier = Modifier.fillMaxWidth()) {
            Text("Privacy First Principles", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ClayTextPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            val principles = listOf(
              "🔒 Zero Background Location Tracking: GPS is only queried when you explicitly search or tap Detect.",
              "🛡️ No Private Keys in Frontend: AI synthesis and data requests route through secure server endpoints.",
              "💡 Source Transparency: Verified official registries are highlighted and distinguished from unverified entries.",
              "📱 Emergency Location: Coordinates are shared strictly on user demand via standard SMS/system intent."
            )

            principles.forEach { p ->
              Text(
                text = p,
                fontSize = 12.sp,
                color = ClayTextSecondary,
                modifier = Modifier.padding(vertical = 4.dp),
                lineHeight = 17.sp
              )
            }
          }
        }
      }
    }
  }
}
