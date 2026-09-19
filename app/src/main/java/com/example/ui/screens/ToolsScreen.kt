package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.speech.tts.TextToSpeech
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmergencyContact
import com.example.data.model.TransportOption
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.SurvivalViewModel
import java.util.Locale

@Composable
fun ToolsScreen(
  viewModel: SurvivalViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  var toolSubTab by remember { mutableStateOf(0) } // 0: Translator, 1: Camera/OCR, 2: Transport Hub, 3: Emergency Mode
  val context = LocalContext.current

  // Setup TTS
  var tts by remember { mutableStateOf<TextToSpeech?>(null) }
  DisposableEffect(Unit) {
    var textToSpeech: TextToSpeech? = null
    textToSpeech = TextToSpeech(context) { status ->
      if (status == TextToSpeech.SUCCESS) {
        textToSpeech?.language = Locale.ENGLISH
      }
    }
    tts = textToSpeech
    onDispose {
      textToSpeech?.stop()
      textToSpeech?.shutdown()
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(ClayBackground)
      .padding(top = 16.dp)
  ) {
    // Header
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      Text(
        text = "Survival Tools Hub",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = ClayTextPrimary
      )
      Text(
        text = "Voice translation, OCR scanner, transit & emergency",
        fontSize = 13.sp,
        color = ClayTextSecondary
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Tool Selector Tabs
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      val tools = listOf(
        "🗣️ Translator" to 0,
        "📷 Camera OCR" to 1,
        "🚌 Transport" to 2,
        "🚨 Emergency" to 3
      )

      tools.forEach { (label, index) ->
        val isSelected = toolSubTab == index
        Box(
          modifier = Modifier
            .shadow(if (isSelected) 3.dp else 1.dp, RoundedCornerShape(14.dp), ambientColor = ClayShadowAmbient, spotColor = ClayShadowSoft)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) if (index == 3) EmergencyRed else ClayTerracotta else ClaySurface)
            .border(1.dp, ClayBorderLight, RoundedCornerShape(14.dp))
            .clickable { toolSubTab = index }
            .padding(horizontal = 14.dp, vertical = 9.dp)
            .testTag("tool_tab_$index")
        ) {
          Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else ClayTextPrimary
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    when (toolSubTab) {
      0 -> TranslatorToolView(
        viewModel = viewModel,
        onSpeakAloud = { text ->
          tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
      )

      1 -> OcrToolView(
        viewModel = viewModel,
        onSpeakAloud = { text ->
          tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
      )

      2 -> TransportToolView(viewModel = viewModel)

      3 -> EmergencyToolView(
        viewModel = viewModel,
        onDial = { number ->
          val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
          try { context.startActivity(intent) } catch (e: Exception) {}
        },
        onShareLocation = {
          val shareText = "🚨 EMERGENCY: I need assistance. My current location is ${uiState.currentLocation}. Sent via Survival Assistant App."
          val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
          }
          try { context.startActivity(Intent.createChooser(intent, "Share Emergency Location")) } catch (e: Exception) {}
        }
      )
    }
  }
}

/**
 * 1. VOICE-TO-VOICE AND TEXT TRANSLATOR
 */
@Composable
fun TranslatorToolView(
  viewModel: SurvivalViewModel,
  onSpeakAloud: (String) -> Unit
) {
  val uiState by viewModel.uiState.collectAsState()
  val languages = listOf("English", "Malayalam", "Hindi", "Tamil", "Kannada", "Telugu", "Bengali", "Spanish", "French")

  val quickPhrases = listOf(
    "How much is this?",
    "Where is the railway station?",
    "Where is the hospital?",
    "Please help me",
    "Where can I get water?",
    "I need a vegetarian meal"
  )

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Language Selector Bar with Swap Button
    item {
      ClayCard(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("From", fontSize = 11.sp, color = ClayTextTertiary)
            Text(uiState.translateFromLang, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ClayTextPrimary)
          }

          // Swap Button
          IconButton(
            onClick = { viewModel.swapLanguages() },
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(ClayTerracottaLight)
          ) {
            Icon(Icons.Default.SwapHoriz, contentDescription = "Swap Languages", tint = ClayTerracotta)
          }

          Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Text("To", fontSize = 11.sp, color = ClayTextTertiary)
            Text(uiState.translateToLang, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ClayTerracotta)
          }
        }

        // Language Quick Chips
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          languages.forEach { lang ->
            val isTarget = uiState.translateToLang == lang
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isTarget) ClayTerracotta else ClaySurfaceElevated)
                .clickable { viewModel.setTranslateToLang(lang); viewModel.performTranslation() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = lang,
                fontSize = 11.sp,
                color = if (isTarget) Color.White else ClayTextPrimary,
                fontWeight = if (isTarget) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }
      }
    }

    // Input Card
    item {
      ClayCard(modifier = Modifier.fillMaxWidth()) {
        Text("Original Text", fontSize = 12.sp, color = ClayTextTertiary)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = uiState.translateInputText,
          onValueChange = { viewModel.onTranslateInputChanged(it) },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = ClayBorderLight,
            focusedBorderColor = ClayTerracotta
          )
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ClayButton(
            onClick = { viewModel.performTranslation() },
            modifier = Modifier.weight(1f),
            containerColor = ClayTerracotta
          ) {
            if (uiState.isTranslating) {
              CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
              Text("Translate", fontWeight = FontWeight.Bold)
            }
          }

          ClayButton(
            onClick = { onSpeakAloud(uiState.translateInputText) },
            containerColor = ClaySurfaceElevated,
            contentColor = ClayTextPrimary,
            modifier = Modifier.width(52.dp)
          ) {
            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Read Aloud")
          }
        }
      }
    }

    // Translation Output Card
    item {
      ClayCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = ClayTerracottaLight,
        borderColor = ClayTerracotta.copy(alpha = 0.3f)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Translation (${uiState.translateToLang})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ClayTerracotta)
          IconButton(
            onClick = { onSpeakAloud(uiState.translateResultText) },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen Translation", tint = ClayTerracotta)
          }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = uiState.translateResultText,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = ClayTextPrimary
        )
      }
    }

    // Quick Survival Phrases
    item {
      Text("Common Survival Phrases", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ClayTextPrimary)
    }

    items(quickPhrases) { phrase ->
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(ClaySurface)
          .border(1.dp, ClayBorderLight, RoundedCornerShape(12.dp))
          .clickable {
            viewModel.onTranslateInputChanged(phrase)
            viewModel.performTranslation()
          }
          .padding(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("💬 $phrase", fontSize = 13.sp, color = ClayTextPrimary, fontWeight = FontWeight.Medium)
          Icon(Icons.Default.Translate, contentDescription = null, tint = ClayTerracotta, modifier = Modifier.size(16.dp))
        }
      }
    }
  }
}

/**
 * 2. CAMERA OCR SIGNBOARD & MENU TRANSLATOR
 */
@Composable
fun OcrToolView(
  viewModel: SurvivalViewModel,
  onSpeakAloud: (String) -> Unit
) {
  val uiState by viewModel.uiState.collectAsState()

  val sampleScans = listOf(
    "Notice: Way to Platform No. 2\nMeals Available 11:30 AM - 3:30 PM",
    "Special Malabar Dum Biryani - ₹160\nMeals with Sambar & Payasam - ₹90\nFiltered Hot Water Free",
    "Doctor Consultation Timings: 9:00 AM - 1:00 PM\nEmergency Casualty Open 24 Hours Ground Floor",
    "No Parking Outside Gate • Towing Zone • Fine ₹500"
  )

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      ClayCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = ClaySageLight,
        borderColor = ClaySage.copy(alpha = 0.3f)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("📷", fontSize = 24.sp)
          Column {
            Text("Camera & Image OCR Translator", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ClayTextPrimary)
            Text("Extract text from street signboards, restaurant menus & notices", fontSize = 12.sp, color = ClayTextSecondary)
          }
        }
      }
    }

    // Camera Scan Action Card
    item {
      ClayCard(modifier = Modifier.fillMaxWidth()) {
        Text("Scan a Signboard or Menu", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ClayTextPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        // Scanner viewfinder simulation box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2B2824))
            .border(2.dp, Brush.linearGradient(listOf(ClayTerracotta, ClaySage)), RoundedCornerShape(16.dp))
            .padding(12.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.QrCodeScanner, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text("Point camera at sign or tap sample below", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ClayButton(
            onClick = { viewModel.performOcrScan(sampleScans.random()) },
            modifier = Modifier.weight(1f),
            containerColor = ClayTerracotta
          ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Simulate Live Scan", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Preset Signboards to test
    item {
      Text("Sample Street Signboards to Scan:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ClayTextSecondary)
    }

    items(sampleScans) { scan ->
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(ClaySurface)
          .border(1.dp, ClayBorderLight, RoundedCornerShape(12.dp))
          .clickable { viewModel.performOcrScan(scan) }
          .padding(12.dp)
      ) {
        Column {
          Text("📜 \"$scan\"", fontSize = 12.sp, color = ClayTextPrimary)
          Spacer(modifier = Modifier.height(4.dp))
          Text("Tap to OCR & translate", fontSize = 10.sp, color = ClayTerracotta, fontWeight = FontWeight.Bold)
        }
      }
    }

    // OCR Result Card
    uiState.ocrResult?.let { result ->
      item {
        ClayCard(
          modifier = Modifier.fillMaxWidth(),
          backgroundColor = ClaySurfaceElevated
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Extracted OCR Result", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ClayTextPrimary)
            IconButton(
              onClick = { onSpeakAloud(result.translatedText) },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Read Aloud", tint = ClayTerracotta)
            }
          }

          Spacer(modifier = Modifier.height(6.dp))
          Text("Original Text:", fontSize = 11.sp, color = ClayTextTertiary)
          Text(result.originalText, fontSize = 13.sp, color = ClayTextPrimary, fontWeight = FontWeight.Medium)

          Spacer(modifier = Modifier.height(8.dp))
          Text("Translated (${uiState.translateToLang}):", fontSize = 11.sp, color = ClayTerracotta, fontWeight = FontWeight.Bold)
          Text(result.translatedText, fontSize = 14.sp, color = ClayTerracotta, fontWeight = FontWeight.Bold)

          Spacer(modifier = Modifier.height(8.dp))
          Text("💡 AI Context Summary: ${result.summary}", fontSize = 11.sp, color = ClayTextSecondary)
        }
      }
    }
  }
}

/**
 * 3. DEDICATED TRANSPORT HUB
 */
@Composable
fun TransportToolView(viewModel: SurvivalViewModel) {
  val uiState by viewModel.uiState.collectAsState()
  val transportOptions = remember(uiState.currentLocation) {
    viewModel.repository.getTransportOptions(uiState.currentLocation)
  }

  var selectedMode by remember { mutableStateOf("All") }
  val modes = listOf("All", "Metro", "Bus", "Train", "Auto")

  val filteredOptions = remember(selectedMode, transportOptions) {
    if (selectedMode == "All") transportOptions else transportOptions.filter { it.mode.equals(selectedMode, ignoreCase = true) }
  }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        modes.forEach { mode ->
          val isSelected = selectedMode == mode
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) ClayTerracotta else ClaySurface)
              .border(1.dp, ClayBorderLight, RoundedCornerShape(12.dp))
              .clickable { selectedMode = mode }
              .padding(horizontal = 14.dp, vertical = 8.dp)
          ) {
            Text(
              text = mode,
              color = if (isSelected) Color.White else ClayTextPrimary,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              fontSize = 12.sp
            )
          }
        }
      }
    }

    items(filteredOptions) { opt ->
      ClayCard(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(opt.modeEmoji, fontSize = 22.sp)
            Column {
              Text(opt.routeNumber, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ClayTextPrimary)
              Text(opt.mode, fontSize = 11.sp, color = ClayTextSecondary)
            }
          }
          Text(opt.price, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ClaySage)
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Departure", fontSize = 10.sp, color = ClayTextTertiary)
            Text(opt.departureTime, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ClayTextPrimary)
            Text(opt.fromStation, fontSize = 11.sp, color = ClayTextSecondary, maxLines = 1)
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⏳ ${opt.duration}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ClayTerracotta)
            Text("• ${opt.stops} stops •", fontSize = 10.sp, color = ClayTextTertiary)
            Text(opt.distance, fontSize = 10.sp, color = ClayTextTertiary)
          }
          Column(horizontalAlignment = Alignment.End) {
            Text("Arrival", fontSize = 10.sp, color = ClayTextTertiary)
            Text(opt.arrivalTime, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ClayTextPrimary)
            Text(opt.toStation, fontSize = 11.sp, color = ClayTextSecondary, maxLines = 1)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        SourceVerificationChip(
          source = opt.source,
          isVerified = opt.isVerified,
          lastChecked = opt.lastUpdated
        )
      }
    }
  }
}

/**
 * 4. EMERGENCY MODE 🚨
 */
@Composable
fun EmergencyToolView(
  viewModel: SurvivalViewModel,
  onDial: (String) -> Unit,
  onShareLocation: () -> Unit
) {
  val uiState by viewModel.uiState.collectAsState()
  val emergencyContacts = remember { viewModel.repository.getEmergencyContacts() }

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Top Critical SOS Action
    item {
      ClayCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = EmergencyRedBg,
        borderColor = EmergencyRed.copy(alpha = 0.4f),
        elevation = 8.dp
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("🚨 EMERGENCY ASSISTANCE", fontWeight = FontWeight.Black, fontSize = 16.sp, color = EmergencyRed)
          Text(
            text = "Instant 24/7 help in ${uiState.currentLocation}. Tap to call immediately or share live location coordinates.",
            fontSize = 12.sp,
            color = ClayTextPrimary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
          )

          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            ClayButton(
              onClick = { onDial("112") },
              containerColor = EmergencyRed,
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(14.dp),
              elevation = 4.dp
            ) {
              Text("DIAL 112 NOW", fontWeight = FontWeight.Black)
            }

            ClayButton(
              onClick = onShareLocation,
              containerColor = ClaySurface,
              contentColor = EmergencyRed,
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(14.dp),
              elevation = 3.dp
            ) {
              Icon(Icons.Default.ShareLocation, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Share Location", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        }
      }
    }

    item {
      Text("Verified Emergency Helplines", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ClayTextPrimary)
    }

    items(emergencyContacts) { contact ->
      ClayCard(
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(contact.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ClayTextPrimary)
            Text(contact.category, fontSize = 12.sp, color = ClayTextSecondary)
            Text("📍 ${contact.distance} • ${contact.address}", fontSize = 11.sp, color = ClayTextTertiary)
          }

          ClayButton(
            onClick = { onDial(contact.number.split("/")[0].trim()) },
            containerColor = EmergencyRed,
            shape = RoundedCornerShape(12.dp),
            elevation = 2.dp
          ) {
            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(contact.number.split("/")[0].trim(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }
    }
  }
}
