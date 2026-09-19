package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Standard Clay Card with tactile soft 3D elevation, rounded corners, and subtle contour border.
 */
@Composable
fun ClayCard(
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(22.dp),
  backgroundColor: Color = ClaySurface,
  elevation: Dp = 6.dp,
  borderColor: Color = ClayBorderLight,
  onClick: (() -> Unit)? = null,
  testTag: String? = null,
  content: @Composable ColumnScope.() -> Unit
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val scale by animateFloatAsState(
    targetValue = if (isPressed && onClick != null) 0.985f else 1f,
    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
    label = "clay_press"
  )

  val currentElevation = if (isPressed && onClick != null) (elevation / 2) else elevation

  var baseModifier = modifier
    .scale(scale)
    .shadow(
      elevation = currentElevation,
      shape = shape,
      ambientColor = ClayShadowAmbient,
      spotColor = ClayShadowSoft
    )
    .clip(shape)
    .background(
      brush = Brush.verticalGradient(
        colors = listOf(
          backgroundColor,
          backgroundColor.copy(alpha = 0.96f)
        )
      )
    )
    .border(width = 1.dp, color = borderColor, shape = shape)

  if (testTag != null) {
    baseModifier = baseModifier.testTag(testTag)
  }

  if (onClick != null) {
    baseModifier = baseModifier.clickable(
      interactionSource = interactionSource,
      indication = ripple(),
      onClick = onClick
    )
  }

  Column(
    modifier = baseModifier.padding(18.dp),
    content = content
  )
}

/**
 * Tactile Clay Button with raised tactile look and spring-loaded touch response.
 */
@Composable
fun ClayButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  containerColor: Color = ClayTerracotta,
  contentColor: Color = Color.White,
  shape: Shape = RoundedCornerShape(18.dp),
  elevation: Dp = 5.dp,
  testTag: String? = null,
  content: @Composable RowScope.() -> Unit
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val scale by animateFloatAsState(
    targetValue = if (isPressed && enabled) 0.96f else 1f,
    animationSpec = spring(stiffness = Spring.StiffnessMedium),
    label = "btn_press"
  )

  val currentElevation = if (isPressed) 1.dp else elevation

  var buttonModifier = modifier
    .scale(scale)
    .shadow(
      elevation = if (enabled) currentElevation else 0.dp,
      shape = shape,
      ambientColor = ClayShadowAmbient,
      spotColor = ClayShadowSoft
    )
    .clip(shape)
    .background(
      if (enabled) {
        Brush.verticalGradient(
          colors = listOf(
            containerColor,
            containerColor.copy(alpha = 0.92f)
          )
        )
      } else {
        Brush.linearGradient(listOf(ClayBorderLight, ClayBorderLight))
      }
    )
    .border(
      width = 1.dp,
      color = if (enabled) ClayBorderHighlight.copy(alpha = 0.35f) else Color.Transparent,
      shape = shape
    )

  if (testTag != null) {
    buttonModifier = buttonModifier.testTag(testTag)
  }

  Surface(
    modifier = buttonModifier,
    color = Color.Transparent,
    onClick = onClick,
    enabled = enabled,
    interactionSource = interactionSource
  ) {
    Row(
      modifier = Modifier
        .defaultMinSize(minHeight = 48.dp)
        .padding(horizontal = 20.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      CompositionLocalProvider(LocalContentColor provides if (enabled) contentColor else ClayTextTertiary) {
        content()
      }
    }
  }
}

/**
 * Category Pill with clay tactile surface
 */
@Composable
fun ClayCategoryPill(
  title: String,
  icon: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String? = null
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.94f else 1f,
    animationSpec = spring(stiffness = Spring.StiffnessMedium),
    label = "pill_press"
  )

  val bgBrush = if (selected) {
    Brush.verticalGradient(listOf(ClayTerracotta, ClayTerracotta.copy(alpha = 0.9f)))
  } else {
    Brush.verticalGradient(listOf(ClaySurface, ClaySurfaceElevated))
  }

  val textColor = if (selected) Color.White else ClayTextPrimary
  val elevation = if (selected) 4.dp else 3.dp

  var pillModifier = modifier
    .scale(scale)
    .shadow(
      elevation = elevation,
      shape = RoundedCornerShape(16.dp),
      ambientColor = ClayShadowAmbient,
      spotColor = ClayShadowSoft
    )
    .clip(RoundedCornerShape(16.dp))
    .background(bgBrush)
    .border(
      width = 1.dp,
      color = if (selected) ClayBorderHighlight.copy(alpha = 0.3f) else ClayBorderLight,
      shape = RoundedCornerShape(16.dp)
    )

  if (testTag != null) {
    pillModifier = pillModifier.testTag(testTag)
  }

  Surface(
    modifier = pillModifier,
    color = Color.Transparent,
    onClick = onClick,
    interactionSource = interactionSource
  ) {
    Row(
      modifier = Modifier
        .defaultMinSize(minHeight = 44.dp)
        .padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text(
        text = icon,
        fontSize = 18.sp
      )
      Text(
        text = title,
        color = textColor,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        fontSize = 14.sp
      )
    }
  }
}

/**
 * Good / Better / Best / Premium Recommendation Tier Badge
 */
@Composable
fun TierBadge(
  tierName: String,
  modifier: Modifier = Modifier
) {
  val (color, bgColor, label) = when (tierName.uppercase()) {
    "GOOD" -> Triple(TierGood, TierGoodBg, "🟢 GOOD")
    "BETTER" -> Triple(TierBetter, TierBetterBg, "🔵 BETTER")
    "BEST" -> Triple(TierBest, TierBestBg, "⭐ BEST MATCH")
    "PREMIUM" -> Triple(TierPremium, TierPremiumBg, "👑 PREMIUM")
    else -> Triple(ClayTerracotta, ClayTerracottaLight, tierName)
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(bgColor)
      .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
      .padding(horizontal = 10.dp, vertical = 4.dp)
  ) {
    Text(
      text = label,
      color = color,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold
    )
  }
}

/**
 * Source Verification Chip
 */
@Composable
fun SourceVerificationChip(
  source: String,
  isVerified: Boolean,
  lastChecked: String? = null,
  modifier: Modifier = Modifier
) {
  val icon = if (isVerified) "✓" else "ℹ"
  val color = if (isVerified) ClaySage else ClayAmber
  val bgColor = if (isVerified) ClaySageLight else ClayAmberLight

  Row(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(bgColor)
      .padding(horizontal = 8.dp, vertical = 3.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Text(
      text = "$icon $source",
      color = color,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold
    )
    if (!lastChecked.isNullOrBlank()) {
      Text(
        text = "• $lastChecked",
        color = ClayTextTertiary,
        fontSize = 10.sp
      )
    }
  }
}
