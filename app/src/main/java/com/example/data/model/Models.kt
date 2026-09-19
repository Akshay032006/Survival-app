package com.example.data.model

data class Place(
  val id: String,
  val name: String,
  val category: String,
  val subcategory: String,
  val rating: Float,
  val reviewCount: Int,
  val distanceKm: Double,
  val address: String,
  val priceRange: String,
  val openStatus: String,
  val isOpenNow: Boolean = true,
  val facilities: List<String> = emptyList(),
  val tier: String = "BEST", // "GOOD", "BETTER", "BEST", "PREMIUM"
  val whyItMatches: String,
  val sourceName: String = "Local Verified Registry",
  val isVerified: Boolean = true,
  val lastChecked: String = "Verified today",
  val phone: String? = "+91 98765 43210",
  val websiteUrl: String? = "https://maps.google.com",
  val latitude: Double = 12.9716,
  val longitude: Double = 77.5946,
  val specialOffer: String? = null
)

data class SurvivalPlanStep(
  val stepNumber: Int,
  val categoryEmoji: String,
  val title: String,
  val place: Place,
  val distanceToNext: String,
  val tip: String
)

data class SurvivalPlan(
  val title: String,
  val query: String,
  val timestamp: String,
  val steps: List<SurvivalPlanStep>,
  val overallRouteSummary: String,
  val estimatedTotalCost: String
)

data class TransportOption(
  val id: String,
  val mode: String, // "Bus", "Train", "Metro", "Cab", "Auto", "Bike"
  val modeEmoji: String,
  val routeNumber: String,
  val fromStation: String,
  val toStation: String,
  val departureTime: String,
  val arrivalTime: String,
  val duration: String,
  val stops: Int,
  val transfers: Int,
  val price: String,
  val distance: String,
  val source: String,
  val isVerified: Boolean,
  val lastUpdated: String
)

data class EmergencyContact(
  val id: String,
  val name: String,
  val number: String,
  val category: String,
  val distance: String,
  val address: String,
  val isNational: Boolean = false
)

data class UserPreferences(
  val preferredName: String = "Traveler",
  val budgetTier: String = "Budget", // "Budget", "Moderate", "Premium"
  val foodPreference: String = "Vegetarian", // "Vegetarian", "Non-Vegetarian", "Vegan", "High Protein"
  val stayType: String = "PG / Hostel", // "PG / Hostel", "Budget Hotel", "Rental Flat"
  val transportPreference: String = "Metro / Bus",
  val maxDistanceKm: Double = 3.0,
  val primaryLanguage: String = "English",
  val targetLanguage: String = "Malayalam",
  val fitnessGoal: String = "General Gym Access",
  val onboardingCompleted: Boolean = true
)

data class GuideItem(
  val icon: String,
  val question: String,
  val answer: String,
  val actionLabel: String? = null,
  val queryTarget: String? = null
)

data class CitySetupItem(
  val category: String,
  val emoji: String,
  val recommendedOption: String,
  val monthlyEstimate: String,
  val detail: String
)
