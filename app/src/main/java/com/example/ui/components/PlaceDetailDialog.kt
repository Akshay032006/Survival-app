package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Place
import com.example.ui.theme.*

@Composable
fun PlaceDetailDialog(
  place: Place,
  isSaved: Boolean,
  onDismiss: () -> Unit,
  onToggleSave: () -> Unit
) {
  val context = LocalContext.current

  Dialog(onDismissRequest = onDismiss) {
    ClayCard(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      shape = RoundedCornerShape(26.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        TierBadge(tierName = place.tier)
        IconButton(
          onClick = onDismiss,
          modifier = Modifier.size(32.dp)
        ) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = ClayTextSecondary)
        }
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = place.name,
        fontSize = 19.sp,
        fontWeight = FontWeight.Bold,
        color = ClayTextPrimary
      )
      Text(
        text = "${place.category} • ${place.subcategory}",
        fontSize = 12.sp,
        color = ClayTextSecondary
      )

      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("⭐ ${place.rating} (${place.reviewCount} reviews)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ClayTextPrimary)
        Text("•", fontSize = 12.sp, color = ClayTextTertiary)
        Text("📍 ${place.distanceKm} km", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ClayTerracotta)
        Text("•", fontSize = 12.sp, color = ClayTextTertiary)
        Text(place.priceRange, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ClaySage)
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "📍 ${place.address}",
        fontSize = 12.sp,
        color = ClayTextSecondary
      )

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "🕒 ${place.openStatus}",
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = if (place.isOpenNow) ClaySage else Color.Red
      )

      // Why it matches
      Spacer(modifier = Modifier.height(10.dp))
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(ClaySurfaceElevated)
          .border(1.dp, ClayBorderLight, RoundedCornerShape(12.dp))
          .padding(10.dp)
      ) {
        Column {
          Text("Why this matches your request:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ClayTerracotta)
          Spacer(modifier = Modifier.height(2.dp))
          Text(place.whyItMatches, fontSize = 12.sp, color = ClayTextPrimary, lineHeight = 16.sp)
        }
      }

      // Facilities chips
      if (place.facilities.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        Text("Key Amenities & Features:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ClayTextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          place.facilities.forEach { facility ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(ClaySurfaceElevated)
                .border(1.dp, ClayBorderLight, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text("✓ $facility", fontSize = 11.sp, color = ClayTextPrimary)
            }
          }
        }
      }

      // Special Offer
      place.specialOffer?.let { offer ->
        Spacer(modifier = Modifier.height(10.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ClayAmberLight)
            .border(1.dp, ClayAmber.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(8.dp)
        ) {
          Text("🏷️ $offer", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ClayAmber)
        }
      }

      // Source verification info
      Spacer(modifier = Modifier.height(10.dp))
      SourceVerificationChip(
        source = place.sourceName,
        isVerified = place.isVerified,
        lastChecked = place.lastChecked
      )

      // Actions: Directions, Call, Bookmark
      Spacer(modifier = Modifier.height(14.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ClayButton(
          onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:${place.latitude},${place.longitude}?q=${Uri.encode(place.name)}"))
            try { context.startActivity(intent) } catch (e: Exception) {}
          },
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Directions", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        if (!place.phone.isNullOrBlank()) {
          ClayButton(
            onClick = {
              val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${place.phone}"))
              try { context.startActivity(intent) } catch (e: Exception) {}
            },
            containerColor = ClaySurfaceElevated,
            contentColor = ClayTextPrimary,
            modifier = Modifier.weight(0.8f)
          ) {
            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp), tint = ClaySage)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Call", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
          }
        }

        IconButton(
          onClick = onToggleSave,
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(ClaySurfaceElevated)
            .border(1.dp, ClayBorderLight, RoundedCornerShape(14.dp))
        ) {
          Icon(
            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = "Save",
            tint = if (isSaved) ClayTerracotta else ClayTextTertiary
          )
        }
      }
    }
  }
}
