package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Place
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.SurvivalViewModel

@Composable
fun ExploreScreen(
  viewModel: SurvivalViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  val savedIds by viewModel.savedPlaceIds.collectAsState()
  val allPlaces = remember(uiState.currentLocation) { viewModel.repository.getAllPlaces(uiState.currentLocation) }
  val context = LocalContext.current

  var selectedCategory by remember { mutableStateOf("All") }
  var selectedTier by remember { mutableStateOf("ALL") }
  var filterOnlyOpenNow by remember { mutableStateOf(false) }
  var filterVegOnly by remember { mutableStateOf(false) }
  var filterUnder2km by remember { mutableStateOf(false) }
  var selectedMapPlace by remember { mutableStateOf<Place?>(null) }

  val categories = listOf("All", "Food", "Stay", "Gyms", "Grocery", "Pharmacy", "Hospitals", "ATM", "Mobile", "Laundry")
  val tiers = listOf("ALL", "GOOD", "BETTER", "BEST", "PREMIUM")

  // Filtered list
  val filteredPlaces = remember(selectedCategory, selectedTier, filterOnlyOpenNow, filterVegOnly, filterUnder2km, allPlaces) {
    allPlaces.filter { place ->
      val matchesCat = selectedCategory == "All" || place.category.equals(selectedCategory, ignoreCase = true)
      val matchesTier = selectedTier == "ALL" || place.tier.equals(selectedTier, ignoreCase = true)
      val matchesOpen = !filterOnlyOpenNow || place.isOpenNow
      val matchesVeg = !filterVegOnly || place.facilities.any { it.contains("Veg", ignoreCase = true) }
      val matchesDist = !filterUnder2km || place.distanceKm <= 2.0
      matchesCat && matchesTier && matchesOpen && matchesVeg && matchesDist
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(ClayBackground)
      .padding(top = 16.dp)
  ) {
    // Header & Map/List Toggle
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Explore Nearby",
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = ClayTextPrimary
        )
        Text(
          text = "Verified places around ${uiState.currentLocation}",
          fontSize = 13.sp,
          color = ClayTextSecondary
        )
      }

      // List <-> Map Toggle Button
      Row(
        modifier = Modifier
          .shadow(3.dp, RoundedCornerShape(16.dp), ambientColor = ClayShadowAmbient, spotColor = ClayShadowSoft)
          .clip(RoundedCornerShape(16.dp))
          .background(ClaySurfaceElevated)
          .border(1.dp, ClayBorderLight, RoundedCornerShape(16.dp))
          .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (!uiState.isMapView) ClayTerracotta else Color.Transparent)
            .clickable { if (uiState.isMapView) viewModel.toggleMapView() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("toggle_list_view")
        ) {
          Text(
            text = "List",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (!uiState.isMapView) Color.White else ClayTextSecondary
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (uiState.isMapView) ClayTerracotta else Color.Transparent)
            .clickable { if (!uiState.isMapView) viewModel.toggleMapView() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("toggle_map_view")
        ) {
          Text(
            text = "Map",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (uiState.isMapView) Color.White else ClayTextSecondary
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Categories Horizontal Scroll
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      categories.forEach { cat ->
        val isSelected = selectedCategory == cat
        Box(
          modifier = Modifier
            .shadow(if (isSelected) 3.dp else 1.dp, RoundedCornerShape(14.dp), ambientColor = ClayShadowAmbient, spotColor = ClayShadowSoft)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) ClayTerracotta else ClaySurface)
            .border(1.dp, if (isSelected) ClayBorderHighlight.copy(alpha = 0.3f) else ClayBorderLight, RoundedCornerShape(14.dp))
            .clickable { selectedCategory = cat }
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("explore_cat_$cat")
        ) {
          Text(
            text = cat,
            color = if (isSelected) Color.White else ClayTextPrimary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Tier Selector (ALL / GOOD / BETTER / BEST / PREMIUM)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      tiers.forEach { tier ->
        val isSelected = selectedTier == tier
        val (activeColor, label) = when (tier) {
          "GOOD" -> TierGood to "🟢 GOOD"
          "BETTER" -> TierBetter to "🔵 BETTER"
          "BEST" -> TierBest to "⭐ BEST MATCH"
          "PREMIUM" -> TierPremium to "👑 PREMIUM"
          else -> ClayTextPrimary to "All Tiers"
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) ClaySurfacePressed else ClaySurfaceElevated)
            .border(1.dp, if (isSelected) activeColor else ClayBorderLight, RoundedCornerShape(12.dp))
            .clickable { selectedTier = tier }
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) activeColor else ClayTextSecondary
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Smart Quick Filter Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      FilterToggleChip(
        label = "Open Now",
        active = filterOnlyOpenNow,
        onToggle = { filterOnlyOpenNow = !filterOnlyOpenNow }
      )
      FilterToggleChip(
        label = "Pure Veg",
        active = filterVegOnly,
        onToggle = { filterVegOnly = !filterVegOnly }
      )
      FilterToggleChip(
        label = "< 2.0 km",
        active = filterUnder2km,
        onToggle = { filterUnder2km = !filterUnder2km }
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Content: List View OR Tactile Clay Map View
    if (uiState.isMapView) {
      // Tactile Map View
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp)
          .padding(bottom = 96.dp)
      ) {
        TactileClayMapView(
          places = filteredPlaces,
          selectedPlace = selectedMapPlace,
          onSelectPlace = { selectedMapPlace = it }
        )

        // Selected pin preview card overlay
        selectedMapPlace?.let { place ->
          Box(
            modifier = Modifier
              .align(Alignment.BottomCenter)
              .padding(bottom = 8.dp)
          ) {
            ClayCard(
              modifier = Modifier.fillMaxWidth(),
              backgroundColor = ClaySurface
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(place.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ClayTextPrimary)
                  Text("📍 ${place.distanceKm} km • ${place.priceRange}", fontSize = 12.sp, color = ClayTerracotta)
                }
                TierBadge(tierName = place.tier)
              }
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                ClayButton(
                  onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:${place.latitude},${place.longitude}?q=${Uri.encode(place.name)}"))
                    try { context.startActivity(intent) } catch (e: Exception) {}
                  },
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Text("Directions", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                ClayButton(
                  onClick = { viewModel.openPlaceDetails(place) },
                  containerColor = ClaySurfaceElevated,
                  contentColor = ClayTextPrimary,
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Text("View Details", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
              }
            }
          }
        }
      }
    } else {
      // List View
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        item {
          Text(
            text = "Showing ${filteredPlaces.size} verified locations",
            fontSize = 12.sp,
            color = ClayTextTertiary,
            fontWeight = FontWeight.Medium
          )
        }

        items(filteredPlaces, key = { it.id }) { place ->
          PlaceClayCard(
            place = place,
            isSaved = savedIds.contains(place.id),
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
  }
}

@Composable
fun FilterToggleChip(
  label: String,
  active: Boolean,
  onToggle: () -> Unit
) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .background(if (active) ClaySageLight else ClaySurfaceElevated)
      .border(1.dp, if (active) ClaySage else ClayBorderLight, RoundedCornerShape(12.dp))
      .clickable(onClick = onToggle)
      .padding(horizontal = 10.dp, vertical = 5.dp)
  ) {
    Text(
      text = if (active) "✓ $label" else label,
      fontSize = 11.sp,
      fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
      color = if (active) ClaySage else ClayTextSecondary
    )
  }
}

/**
 * Tactile Clay Map View with simulated radar radius and interactive pins
 */
@Composable
fun TactileClayMapView(
  places: List<Place>,
  selectedPlace: Place?,
  onSelectPlace: (Place) -> Unit
) {
  ClayCard(
    modifier = Modifier.fillMaxSize(),
    backgroundColor = Color(0xFFF3EFE6),
    elevation = 6.dp,
    shape = RoundedCornerShape(24.dp)
  ) {
    Box(
      modifier = Modifier.fillMaxSize()
    ) {
      // Map Grid & Concentric Radius Lines
      Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        // Concentric distance rings: 500m, 1km, 2km
        drawCircle(
          color = Color(0xFFE2DCD0),
          radius = size.minDimension * 0.18f,
          center = center,
          style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
        )
        drawCircle(
          color = Color(0xFFE2DCD0),
          radius = size.minDimension * 0.34f,
          center = center,
          style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
        )
        drawCircle(
          color = Color(0xFFE2DCD0),
          radius = size.minDimension * 0.46f,
          center = center,
          style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
        )

        // Center You Are Here marker
        drawCircle(
          color = Color(0xFFD9603B),
          radius = 12f,
          center = center
        )
        drawCircle(
          color = Color.White,
          radius = 5f,
          center = center
        )
      }

      // "You Are Here" Label
      Box(
        modifier = Modifier
          .align(Alignment.Center)
          .offset(y = 18.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(Color.White.copy(alpha = 0.85f))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text("You Are Here", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ClayTerracotta)
      }

      // Map Pins distributed around the center
      places.take(8).forEachIndexed { index, place ->
        val angle = (index * 45) * (Math.PI / 180)
        val radiusFactor = 0.15 + (index % 3) * 0.12
        val offsetX = (Math.cos(angle) * 120 * (radiusFactor * 3)).dp
        val offsetY = (Math.sin(angle) * 110 * (radiusFactor * 3)).dp

        val isSelected = selectedPlace?.id == place.id

        Box(
          modifier = Modifier
            .align(Alignment.Center)
            .offset(x = offsetX, y = offsetY)
            .shadow(4.dp, CircleShape, ambientColor = ClayShadowAmbient, spotColor = ClayShadowSoft)
            .clip(CircleShape)
            .background(if (isSelected) ClayTerracotta else Color.White)
            .border(2.dp, if (isSelected) Color.White else ClayBorderLight, CircleShape)
            .clickable { onSelectPlace(place) }
            .padding(8.dp)
        ) {
          Text(
            text = when (place.category) {
              "Food" -> "🍛"
              "Stay" -> "🏠"
              "Gyms" -> "🏋️"
              "Grocery" -> "🛒"
              "Hospitals" -> "🏥"
              "Pharmacy" -> "💊"
              "ATM" -> "🏧"
              else -> "📍"
            },
            fontSize = if (isSelected) 18.sp else 14.sp
          )
        }
      }

      // Compass Pill at Top-Right
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(8.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color.White.copy(alpha = 0.9f))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text("🧭 North 340°", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ClayTextSecondary)
      }
    }
  }
}
