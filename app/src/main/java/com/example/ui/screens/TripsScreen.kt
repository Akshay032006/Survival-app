package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GuideItem
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.SurvivalViewModel

@Composable
fun TripsScreen(
  viewModel: SurvivalViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  var selectedSubTab by remember { mutableStateOf(0) } // 0: First 24 Hours, 1: Moving Here, 2: Multi-Planner

  val first24HoursGuide = remember(uiState.currentLocation) {
    viewModel.repository.getFirst24HoursGuide(uiState.currentLocation)
  }

  val movingCitySetup = remember(uiState.currentLocation) {
    viewModel.repository.getMovingCitySetup(uiState.currentLocation)
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
        text = "Survival Plans & Guides",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = ClayTextPrimary
      )
      Text(
        text = "Structured roadmaps for ${uiState.currentLocation}",
        fontSize = 13.sp,
        color = ClayTextSecondary
      )
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Sub-tab switcher
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .shadow(2.dp, RoundedCornerShape(16.dp), ambientColor = ClayShadowAmbient, spotColor = ClayShadowSoft)
        .clip(RoundedCornerShape(16.dp))
        .background(ClaySurfaceElevated)
        .border(1.dp, ClayBorderLight, RoundedCornerShape(16.dp))
        .padding(4.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      val tabs = listOf("First 24 Hours", "Moving Here", "Custom Plan")
      tabs.forEachIndexed { index, title ->
        val isSelected = selectedSubTab == index
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) ClayTerracotta else Color.Transparent)
            .clickable { selectedSubTab = index }
            .padding(vertical = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else ClayTextSecondary
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Content Based on Sub-tab
    when (selectedSubTab) {
      0 -> {
        // FIRST 24 HOURS SURVIVAL GUIDE
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          item {
            ClayCard(
              modifier = Modifier.fillMaxWidth(),
              backgroundColor = ClayTerracottaLight,
              borderColor = ClayTerracotta.copy(alpha = 0.3f)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🧭", fontSize = 24.sp)
                Column {
                  Text("First 24 Hours Checklist", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ClayTextPrimary)
                  Text("The most critical answers you need upon arriving in ${uiState.currentLocation}", fontSize = 12.sp, color = ClayTextSecondary)
                }
              }
            }
          }

          items(first24HoursGuide) { item ->
            GuideCardItem(
              item = item,
              onAction = { targetQuery ->
                viewModel.setTab(AppTab.HOME)
                viewModel.executeSearch(targetQuery)
              }
            )
          }
        }
      }

      1 -> {
        // "I'M MOVING HERE" MODE (Monthly City Setup)
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
                Text("🏠", fontSize = 24.sp)
                Column {
                  Text("My New City Setup", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ClayTextPrimary)
                  Text("Estimated realistic monthly budget & necessities for ${uiState.currentLocation}", fontSize = 12.sp, color = ClayTextSecondary)
                }
              }
            }
          }

          items(movingCitySetup) { setup ->
            ClayCard(modifier = Modifier.fillMaxWidth()) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  Text(setup.emoji, fontSize = 20.sp)
                  Column {
                    Text(setup.category, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ClayTextPrimary)
                    Text(setup.recommendedOption, fontSize = 12.sp, color = ClayTextSecondary)
                  }
                }
                Text(setup.monthlyEstimate, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ClaySage)
              }

              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = setup.detail,
                fontSize = 11.sp,
                color = ClayTextTertiary,
                lineHeight = 15.sp
              )
            }
          }

          item {
            ClayButton(
              onClick = {
                viewModel.setTab(AppTab.HOME)
                viewModel.executeSearch("I'm moving here. Find a cheap hotel, grocery, gym and vegetarian meal near me.")
              },
              modifier = Modifier.fillMaxWidth(),
              containerColor = ClayTerracotta
            ) {
              Text("Generate My Personalized Move Plan", fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      2 -> {
        // CUSTOM MULTI-REQUEST PLANNER
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          item {
            ClayCard(modifier = Modifier.fillMaxWidth()) {
              Text("Custom Multi-Stop Request", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ClayTextPrimary)
              Text("Tell the AI multiple destinations you need to tackle in order:", fontSize = 12.sp, color = ClayTextSecondary)
              Spacer(modifier = Modifier.height(10.dp))

              val complexSamples = listOf(
                "I'm arriving tomorrow at 6 PM. Find a cheap hotel near the railway station, a supermarket nearby, a gym under ₹1,000 and a good vegetarian restaurant.",
                "Find a 24/7 hospital, nearby pharmacy, and an ATM on the way.",
                "Find a salon, mobile recharge store and cheap coffee shop near me."
              )

              complexSamples.forEach { prompt ->
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ClaySurfaceElevated)
                    .border(1.dp, ClayBorderLight, RoundedCornerShape(12.dp))
                    .clickable {
                      viewModel.setTab(AppTab.HOME)
                      viewModel.executeSearch(prompt)
                    }
                    .padding(12.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("💡", fontSize = 16.sp)
                    Text(prompt, fontSize = 12.sp, color = ClayTextPrimary, fontWeight = FontWeight.Medium)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun GuideCardItem(
  item: GuideItem,
  onAction: (String) -> Unit
) {
  ClayCard(
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalAlignment = Alignment.Top
    ) {
      Text(item.icon, fontSize = 24.sp)
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = item.question,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = ClayTextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = item.answer,
          fontSize = 12.sp,
          color = ClayTextSecondary,
          lineHeight = 17.sp
        )

        item.actionLabel?.let { action ->
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(ClayTerracottaLight)
              .clickable { onAction(item.queryTarget ?: item.question) }
              .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(action, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ClayTerracotta)
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = ClayTerracotta, modifier = Modifier.size(12.dp))
          }
        }
      }
    }
  }
}
