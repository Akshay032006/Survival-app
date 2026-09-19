package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun LocationPickerDialog(
  currentLocation: String,
  onDismiss: () -> Unit,
  onLocationSelected: (String) -> Unit
) {
  var customInput by remember { mutableStateOf("") }
  val popularCities = listOf(
    "Indiranagar, Bengaluru",
    "Koramangala, Bengaluru",
    "Kochi Marine Drive, Kerala",
    "Fort Kochi, Kerala",
    "RS Puram, Coimbatore",
    "Gandhipuram, Coimbatore",
    "T. Nagar, Chennai",
    "Bandra West, Mumbai",
    "Connaught Place, New Delhi",
    "Hitech City, Hyderabad"
  )

  Dialog(onDismissRequest = onDismiss) {
    ClayCard(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      shape = RoundedCornerShape(24.dp)
    ) {
      Text(
        text = "Select Location",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = ClayTextPrimary
      )
      Text(
        text = "Choose your destination or enter custom city/locality",
        fontSize = 12.sp,
        color = ClayTextSecondary
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Custom Input
      OutlinedTextField(
        value = customInput,
        onValueChange = { customInput = it },
        placeholder = { Text("e.g. Whitefield, Bengaluru", fontSize = 13.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ClayTerracotta) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
          unfocusedBorderColor = ClayBorderLight,
          focusedBorderColor = ClayTerracotta
        )
      )

      if (customInput.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        ClayButton(
          onClick = { onLocationSelected(customInput.trim()) },
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Set to '$customInput'", fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      Text("Popular Locations:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ClayTextSecondary)
      Spacer(modifier = Modifier.height(6.dp))

      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        popularCities.take(6).forEach { city ->
          val isCurrent = currentLocation.contains(city, ignoreCase = true)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(if (isCurrent) ClayTerracottaLight else ClaySurfaceElevated)
              .border(1.dp, if (isCurrent) ClayTerracotta else ClayBorderLight, RoundedCornerShape(12.dp))
              .clickable { onLocationSelected(city) }
              .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "📍 $city",
              fontSize = 13.sp,
              fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
              color = if (isCurrent) ClayTerracotta else ClayTextPrimary
            )
            if (isCurrent) {
              Text("Active", fontSize = 11.sp, color = ClayTerracotta, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.align(Alignment.End)
      ) {
        Text("Cancel", color = ClayTextSecondary)
      }
    }
  }
}
