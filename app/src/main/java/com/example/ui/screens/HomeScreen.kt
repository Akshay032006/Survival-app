package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AiResponseResult
import com.example.data.model.Place
import com.example.data.model.SurvivalPlan
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.SurvivalViewModel

@Composable
fun HomeScreen(
  viewModel: SurvivalViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  val userPrefs by viewModel.userPreferences.collectAsState()
  val savedIds by viewModel.savedPlaceIds.collectAsState()
  val context = LocalContext.current

  val categories = listOf(
    "Food" to "🍛",
    "Stay" to "🏠",
    "Gyms" to "🏋️",
    "Grocery" to "🛒",
    "Transport" to "🚌",
    "Trains" to "🚆",
    "Salon" to "💇",
    "Vehicles" to "🚗",
    "Pharmacy" to "💊",
    "Hospitals" to "🏥",
    "ATM" to "🏧",
    "Mobile" to "📱",
    "Laundry" to "🧺",
    "Emergency" to "🚨"
  )

  val sampleRequests = listOf(
    "I'm new here. I need a cheap gym, supermarket, good food and a place to stay near me.",
    "Find cheap food near me.",
    "Find a gym under ₹1,000 per month.",
    "Where can I get groceries?",
    "I need a hotel near the railway station.",
    "Find a bus to the city center."
  )

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(ClayBackground),
    contentPadding = PaddingValues(bottom = 96.dp, top = 16.dp, start = 16.dp, end = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. TOP GREETING & LOCATION HEADER
    item {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Good day, ${userPrefs.preferredName} 👋",
              fontSize = 22.sp,
              fontWeight = FontWeight.Bold,
              color = ClayTextPrimary
            )
            Text(
              text = "Your AI companion for unfamiliar places",
              fontSize = 13.sp,
              color = ClayTextSecondary
            )
          }

          IconButton(
            onClick = { viewModel.showLocationPicker(true) },
            modifier = Modifier
              .size(44.dp)
              .shadow(3.dp, CircleShape, ambientColor = ClayShadowAmbient, spotColor = ClayShadowSoft)
              .clip(CircleShape)
              .background(ClaySurface)
              .border(1.dp, ClayBorderLight, CircleShape)
              .testTag("location_picker_btn")
          ) {
            Icon(Icons.Outlined.EditLocationAlt, contentDescription = "Change Location", tint = ClayTerracotta)
          }
        }

        // Location Pill
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ClaySurfaceElevated)
            .border(1.dp, ClayBorderLight, RoundedCornerShape(16.dp))
            .clickable { viewModel.showLocationPicker(true) }
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("current_location_pill"),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
          ) {
            Text("📍", fontSize = 16.sp)
            Column {
              Text(
                text = "You are in",
                fontSize = 11.sp,
                color = ClayTextTertiary
              )
              Text(
                text = uiState.currentLocation,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = ClayTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }

          TextButton(
            onClick = { viewModel.autoDetectLocation() },
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.testTag("detect_location_btn")
          ) {
            if (uiState.isLocationDetecting) {
              CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = ClayTerracotta)
            } else {
              Text("Detect", fontSize = 12.sp, color = ClayTerracotta, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // 2. DOMINANT HERO ELEMENT: 🎙️ "I NEED…" TACTILE CLAY VOICE BUTTON
    item {
      TactileVoiceHero(
        isListening = uiState.isListeningVoice,
        onMicClick = { viewModel.toggleVoiceListening() }
      )
    }

    // 3. NATURAL LANGUAGE SEARCH INPUT
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .shadow(4.dp, RoundedCornerShape(20.dp), ambientColor = ClayShadowAmbient, spotColor = ClayShadowSoft)
          .clip(RoundedCornerShape(20.dp))
          .background(ClaySurface)
          .border(1.dp, ClayBorderLight, RoundedCornerShape(20.dp))
          .padding(horizontal = 14.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Outlined.Search, contentDescription = null, tint = ClayTerracotta)
        Spacer(modifier = Modifier.width(8.dp))
        TextField(
          value = uiState.searchQuery,
          onValueChange = { viewModel.onSearchQueryChanged(it) },
          placeholder = { Text("Ask anything naturally...", color = ClayTextTertiary, fontSize = 14.sp) },
          colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
          ),
          modifier = Modifier
            .weight(1f)
            .testTag("main_search_input"),
          singleLine = true
        )
        if (uiState.searchQuery.isNotEmpty()) {
          IconButton(
            onClick = { viewModel.executeSearch() },
            modifier = Modifier.testTag("execute_search_btn")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Search", tint = ClayTerracotta)
          }
        }
      }
    }

    // 4. QUICK CATEGORIES (Horizontally Scrollable Clay Buttons)
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "Quick Categories",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = ClayTextPrimary
        )
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          categories.forEach { (catName, catEmoji) ->
            val isSelected = uiState.selectedCategory == catName
            ClayCategoryPill(
              title = catName,
              icon = catEmoji,
              selected = isSelected,
              onClick = { viewModel.setCategory(catName) },
              testTag = "category_${catName.lowercase()}"
            )
          }
        }
      }
    }

    // 5. SAMPLE AI PROMPTS
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "Try asking the AI Assistant",
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = ClayTextSecondary
        )
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          sampleRequests.forEach { sample ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(ClaySurfaceElevated)
                .border(1.dp, ClayBorderLight, RoundedCornerShape(14.dp))
                .clickable {
                  viewModel.onSearchQueryChanged(sample)
                  viewModel.executeSearch(sample)
                }
                .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
              Text(
                text = "💬 \"$sample\"",
                fontSize = 12.sp,
                color = ClayTextPrimary,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }
    }

    // 6. SPECIAL SURVIVAL MODES BANNERS (New Here? & I'm Moving Here)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // "New Here?" Banner
        ClayCard(
          modifier = Modifier.weight(1f),
          backgroundColor = ClayTerracottaLight,
          borderColor = ClayTerracotta.copy(alpha = 0.2f),
          onClick = { viewModel.setTab(AppTab.TRIPS) },
          testTag = "new_here_mode_card"
        ) {
          Text("📍 FIRST 24 HOURS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ClayTerracotta)
          Spacer(modifier = Modifier.height(4.dp))
          Text("New Here? Mode", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ClayTextPrimary)
          Text("Where to eat, stay, buy SIM, find ATM right now.", fontSize = 11.sp, color = ClayTextSecondary, maxLines = 2)
        }

        // "I'm Moving Here" Banner
        ClayCard(
          modifier = Modifier.weight(1f),
          backgroundColor = ClaySageLight,
          borderColor = ClaySage.copy(alpha = 0.2f),
          onClick = { viewModel.setTab(AppTab.TRIPS) },
          testTag = "moving_here_mode_card"
        ) {
          Text("🏠 RELOCATION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ClaySage)
          Spacer(modifier = Modifier.height(4.dp))
          Text("Moving Here Mode", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ClayTextPrimary)
          Text("Monthly budget for rent, food, gym & commute.", fontSize = 11.sp, color = ClayTextSecondary, maxLines = 2)
        }
      }
    }

    // 7. AI SEARCH RESULTS & STRUCTURED CARDS
    if (uiState.isAiSearching) {
      item {
        ClayCard(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            CircularProgressIndicator(color = ClayTerracotta, strokeWidth = 3.dp, modifier = Modifier.size(24.dp))
            Column {
              Text("AI Orchestrator Analyzing...", fontWeight = FontWeight.Bold, color = ClayTextPrimary)
              Text("Checking location, verified databases, timings and ranking...", fontSize = 12.sp, color = ClayTextSecondary)
            }
          }
        }
      }
    } else {
      uiState.searchResult?.let { result ->
        when (result) {
          is AiResponseResult.SingleCategoryResult -> {
            item {
              SingleCategoryResultView(
                result = result,
                savedIds = savedIds,
                onToggleSave = { viewModel.toggleSavePlace(it) },
                onPlaceClick = { viewModel.openPlaceDetails(it) },
                onDirectionsClick = { place ->
                  val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:${place.latitude},${place.longitude}?q=${Uri.encode(place.name)}"))
                  try { context.startActivity(intent) } catch (e: Exception) {}
                },
                onCallClick = { phone ->
                  val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                  try { context.startActivity(intent) } catch (e: Exception) {}
                }
              )
            }
          }

          is AiResponseResult.MultiRequestPlan -> {
            item {
              MultiRequestPlanView(
                plan = result.plan,
                aiInsight = result.aiInsight,
                savedIds = savedIds,
                onToggleSave = { viewModel.toggleSavePlace(it) },
                onPlaceClick = { viewModel.openPlaceDetails(it) }
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Tactile Clay Voice Hero Button
 */
@Composable
fun TactileVoiceHero(
  isListening: Boolean,
  onMicClick: () -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (isListening) 1.08f else 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
  )

  ClayCard(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("voice_hero_card"),
    backgroundColor = ClaySurfaceElevated,
    elevation = 8.dp,
    shape = RoundedCornerShape(26.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Text(
        text = if (isListening) "Listening to your request..." else "Tell me what you need",
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = ClayTextPrimary
      )

      // Big Round Tactile 3D Clay Mic Button
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .scale(pulseScale)
          .size(108.dp)
          .shadow(
            elevation = if (isListening) 12.dp else 8.dp,
            shape = CircleShape,
            ambientColor = ClayShadowAmbient,
            spotColor = ClayShadowSoft
          )
          .clip(CircleShape)
          .background(
            Brush.verticalGradient(
              listOf(
                if (isListening) Color(0xFFE53935) else ClayTerracotta,
                if (isListening) Color(0xFFC62828) else Color(0xFFBF4824)
              )
            )
          )
          .border(2.dp, ClayBorderHighlight.copy(alpha = 0.4f), CircleShape)
          .clickable(onClick = onMicClick)
          .testTag("main_voice_mic_btn")
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
            contentDescription = "Voice Assistant",
            tint = Color.White,
            modifier = Modifier.size(42.dp)
          )
          Text(
            text = "I NEED…",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = Color.White.copy(alpha = 0.9f)
          )
        }
      }

      Text(
        text = if (isListening) "🎙️ Speak freely: 'Cheap hotel, gym and food near me'" else "Tap to speak, or type above • Ask multiple things in 1 go",
        fontSize = 12.sp,
        color = if (isListening) ClayTerracotta else ClayTextSecondary,
        fontWeight = if (isListening) FontWeight.Bold else FontWeight.Normal
      )
    }
  }
}

/**
 * Single Category AI Result Card View
 */
@Composable
fun SingleCategoryResultView(
  result: AiResponseResult.SingleCategoryResult,
  savedIds: Set<String>,
  onToggleSave: (String) -> Unit,
  onPlaceClick: (Place) -> Unit,
  onDirectionsClick: (Place) -> Unit,
  onCallClick: (String) -> Unit
) {
  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    // Header summary card
    ClayCard(
      modifier = Modifier.fillMaxWidth(),
      backgroundColor = ClaySurfaceElevated,
      elevation = 4.dp
    ) {
      Text(
        text = result.interpretedSummary,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = ClayTextPrimary
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "💡 AI Reasoning: ${result.aiInsight}",
        fontSize = 12.sp,
        color = ClayTextSecondary
      )
    }

    // BEST MATCH CARD
    Text(
      text = "TOP RECOMMENDATION",
      fontSize = 12.sp,
      fontWeight = FontWeight.Black,
      color = ClayTerracotta,
      letterSpacing = 1.sp
    )

    PlaceClayCard(
      place = result.bestMatch,
      isSaved = savedIds.contains(result.bestMatch.id),
      onToggleSave = { onToggleSave(result.bestMatch.id) },
      onClick = { onPlaceClick(result.bestMatch) },
      onDirections = { onDirectionsClick(result.bestMatch) },
      onCall = { onCallClick(result.bestMatch.phone ?: "+91 98765 43210") }
    )

    // OTHER OPTIONS
    if (result.otherOptions.isNotEmpty()) {
      Text(
        text = "${result.otherOptions.size} MORE VERIFIED OPTIONS",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = ClayTextSecondary,
        modifier = Modifier.padding(top = 8.dp)
      )

      result.otherOptions.forEach { place ->
        PlaceClayCard(
          place = place,
          isSaved = savedIds.contains(place.id),
          onToggleSave = { onToggleSave(place.id) },
          onClick = { onPlaceClick(place) },
          onDirections = { onDirectionsClick(place) },
          onCall = { onCallClick(place.phone ?: "+91 98765 43210") }
        )
      }
    }
  }
}

/**
 * Multi-Request Survival Plan View (Signature Feature)
 */
@Composable
fun MultiRequestPlanView(
  plan: SurvivalPlan,
  aiInsight: String,
  savedIds: Set<String>,
  onToggleSave: (String) -> Unit,
  onPlaceClick: (Place) -> Unit
) {
  Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
    ClayCard(
      modifier = Modifier.fillMaxWidth(),
      backgroundColor = ClayTerracottaLight,
      borderColor = ClayTerracotta.copy(alpha = 0.3f),
      elevation = 6.dp
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("🧭", fontSize = 24.sp)
        Column {
          Text(plan.title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = ClayTextPrimary)
          Text(plan.timestamp, fontSize = 12.sp, color = ClayTextSecondary)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "💡 $aiInsight",
        fontSize = 13.sp,
        color = ClayTextPrimary
      )

      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(Color.White.copy(alpha = 0.7f))
          .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("Route Summary", fontSize = 11.sp, color = ClayTextTertiary)
          Text(plan.overallRouteSummary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ClayTextPrimary)
        }
        Column(horizontalAlignment = Alignment.End) {
          Text("Est. Expenses", fontSize = 11.sp, color = ClayTextTertiary)
          Text(plan.estimatedTotalCost, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ClaySage)
        }
      }
    }

    Text(
      text = "YOUR STEP-BY-STEP PLAN",
      fontSize = 12.sp,
      fontWeight = FontWeight.Black,
      color = ClayTerracotta,
      letterSpacing = 1.sp
    )

    plan.steps.forEach { step ->
      ClayCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onPlaceClick(step.place) }
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(ClayTerracottaLight),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "${step.stepNumber}",
                fontWeight = FontWeight.Bold,
                color = ClayTerracotta,
                fontSize = 14.sp
              )
            }
            Column {
              Text(
                text = "${step.categoryEmoji} ${step.title}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ClayTextPrimary
              )
              Text(
                text = step.place.name,
                fontSize = 13.sp,
                color = ClayTextSecondary
              )
            }
          }

          TierBadge(tierName = step.place.tier)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "📌 Tip: ${step.tip}",
          fontSize = 12.sp,
          color = ClayTextPrimary,
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ClaySurfaceElevated)
            .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("➡️ ${step.distanceToNext}", fontSize = 11.sp, color = ClayTextTertiary, fontWeight = FontWeight.Medium)
          Text(step.place.priceRange, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ClaySage)
        }
      }
    }
  }
}

/**
 * Standard Reusable Tactile Place Clay Card
 */
@Composable
fun PlaceClayCard(
  place: Place,
  isSaved: Boolean,
  onToggleSave: () -> Unit,
  onClick: () -> Unit,
  onDirections: () -> Unit,
  onCall: () -> Unit
) {
  ClayCard(
    modifier = Modifier.fillMaxWidth(),
    onClick = onClick,
    testTag = "place_card_${place.id}"
  ) {
    // Header Row: Tier Badge + Category + Save button
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TierBadge(tierName = place.tier)
        Text(
          text = place.subcategory,
          fontSize = 12.sp,
          color = ClayTextSecondary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      IconButton(
        onClick = onToggleSave,
        modifier = Modifier.size(32.dp)
      ) {
        Icon(
          imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
          contentDescription = "Save place",
          tint = if (isSaved) ClayTerracotta else ClayTextTertiary
        )
      }
    }

    Spacer(modifier = Modifier.height(6.dp))

    // Place Title & Rating
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = place.name,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = ClayTextPrimary,
        modifier = Modifier.weight(1f)
      )
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        Text("⭐", fontSize = 12.sp)
        Text(
          text = "${place.rating}",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = ClayTextPrimary
        )
        Text(
          text = "(${place.reviewCount})",
          fontSize = 11.sp,
          color = ClayTextTertiary
        )
      }
    }

    // Distance + Price + Open Status
    Row(
      modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "📍 ${place.distanceKm} km away",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = ClayTerracotta
      )
      Text("•", fontSize = 12.sp, color = ClayTextTertiary)
      Text(
        text = place.priceRange,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = ClaySage
      )
      Text("•", fontSize = 12.sp, color = ClayTextTertiary)
      Text(
        text = place.openStatus,
        fontSize = 12.sp,
        color = if (place.isOpenNow) ClayTextSecondary else Color.Red,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }

    // Why it matches (Transparent Reasoning)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(ClaySurfaceElevated)
        .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
      Text(
        text = "Why it matches: ${place.whyItMatches}",
        fontSize = 12.sp,
        color = ClayTextPrimary,
        lineHeight = 16.sp
      )
    }

    // Source verification
    Spacer(modifier = Modifier.height(6.dp))
    SourceVerificationChip(
      source = place.sourceName,
      isVerified = place.isVerified,
      lastChecked = place.lastChecked
    )

    // Action buttons: Directions & Call
    Spacer(modifier = Modifier.height(10.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      ClayButton(
        onClick = onDirections,
        containerColor = ClayTerracotta,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(14.dp),
        elevation = 3.dp
      ) {
        Icon(Icons.Outlined.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Directions", fontSize = 13.sp, fontWeight = FontWeight.Bold)
      }

      if (!place.phone.isNullOrBlank()) {
        ClayButton(
          onClick = onCall,
          containerColor = ClaySurfaceElevated,
          contentColor = ClayTextPrimary,
          modifier = Modifier.weight(0.8f),
          shape = RoundedCornerShape(14.dp),
          elevation = 2.dp
        ) {
          Icon(Icons.Outlined.Call, contentDescription = null, modifier = Modifier.size(16.dp), tint = ClaySage)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Call", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}
