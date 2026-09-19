package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab

@Composable
fun ClayBottomNavigation(
  currentTab: AppTab,
  onTabSelected: (AppTab) -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 14.dp, vertical = 12.dp)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .shadow(
          elevation = 10.dp,
          shape = RoundedCornerShape(28.dp),
          ambientColor = ClayShadowAmbient,
          spotColor = ClayShadowSoft
        )
        .border(1.dp, ClayBorderLight, RoundedCornerShape(28.dp)),
      shape = RoundedCornerShape(28.dp),
      color = ClaySurface
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 6.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        AppTab.values().forEach { tab ->
          val isSelected = currentTab == tab
          val interactionSource = remember { MutableInteractionSource() }

          val scale by animateFloatAsState(
            targetValue = if (isSelected) 1.04f else 1f,
            animationSpec = spring(stiffness = Spring.StiffnessMedium),
            label = "tab_scale"
          )

          val bgBrush = if (isSelected) {
            Brush.verticalGradient(listOf(ClayTerracotta, Color(0xFFC9532E)))
          } else {
            Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
          }

          Box(
            modifier = Modifier
              .scale(scale)
              .clip(RoundedCornerShape(20.dp))
              .background(bgBrush)
              .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = ClayTerracotta),
                onClick = { onTabSelected(tab) }
              )
              .padding(horizontal = 12.dp, vertical = 8.dp)
              .testTag("nav_tab_${tab.name.lowercase()}"),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(
                text = tab.icon,
                fontSize = if (isSelected) 18.sp else 16.sp
              )
              if (isSelected) {
                Text(
                  text = tab.title,
                  color = Color.White,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }
  }
}
