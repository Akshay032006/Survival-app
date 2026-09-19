package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiOrchestrator
import com.example.ai.AiResponseResult
import com.example.ai.OcrResult
import com.example.data.model.*
import com.example.data.repository.SurvivalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppTab(val title: String, val icon: String) {
  HOME("Home", "🧭"),
  EXPLORE("Explore", "🗺️"),
  TRIPS("Plans", "📋"),
  TOOLS("Survival", "🧰"),
  PROFILE("Profile", "👤")
}

data class UiState(
  val currentTab: AppTab = AppTab.HOME,
  val currentLocation: String = "Indiranagar, Bengaluru",
  val isLocationDetecting: Boolean = false,
  val searchQuery: String = "",
  val isListeningVoice: Boolean = false,
  val isAiSearching: Boolean = false,
  val searchResult: AiResponseResult? = null,
  val selectedCategory: String = "All",
  val selectedTierFilter: String = "ALL", // ALL, GOOD, BETTER, BEST, PREMIUM
  val isMapView: Boolean = false,
  // Translation
  val translateFromLang: String = "English",
  val translateToLang: String = "Malayalam",
  val translateInputText: String = "How much does this cost?",
  val translateResultText: String = "ഇതിന് എത്ര രൂപയാണ്? (Ithinu ethra roopayaanu?)",
  val isTranslating: Boolean = false,
  // OCR
  val ocrDetectedText: String = "ശ്രദ്ധിക്കുക: പ്ലാറ്റ്ഫോം നമ്പർ 2 ലേക്ക് പോകുന്ന വഴിയാണ്.\nNotice: Way to Platform No. 2\nMeals Available 11:30 AM - 3:30 PM",
  val ocrResult: OcrResult? = null,
  val isProcessingOcr: Boolean = false,
  // Onboarding
  val showOnboarding: Boolean = false,
  val onboardingStep: Int = 1,
  // Info Dialog
  val activeDetailPlace: Place? = null,
  val showLocationPicker: Boolean = false,
  val toastMessage: String? = null
)

class SurvivalViewModel(
  val repository: SurvivalRepository = SurvivalRepository(),
  val aiOrchestrator: AiOrchestrator = AiOrchestrator(repository)
) : ViewModel() {

  private val _uiState = MutableStateFlow(UiState())
  val uiState: StateFlow<UiState> = _uiState.asStateFlow()

  val userPreferences: StateFlow<UserPreferences> = repository.userPreferences
  val savedPlaceIds: StateFlow<Set<String>> = repository.savedPlaceIds

  init {
    viewModelScope.launch {
      repository.currentLocationName.collect { loc ->
        _uiState.update { it.copy(currentLocation = loc) }
      }
    }
  }

  fun setTab(tab: AppTab) {
    _uiState.update { it.copy(currentTab = tab) }
  }

  fun onSearchQueryChanged(q: String) {
    _uiState.update { it.copy(searchQuery = q) }
  }

  fun executeSearch(query: String? = null) {
    val q = query ?: _uiState.value.searchQuery
    if (q.isBlank()) return

    _uiState.update { it.copy(searchQuery = q, isAiSearching = true, isListeningVoice = false) }
    viewModelScope.launch {
      val result = aiOrchestrator.processQuery(
        query = q,
        currentLocation = _uiState.value.currentLocation,
        userPreferences = repository.userPreferences.value
      )
      _uiState.update {
        it.copy(
          isAiSearching = false,
          searchResult = result
        )
      }
    }
  }

  fun toggleVoiceListening() {
    val current = _uiState.value.isListeningVoice
    _uiState.update { it.copy(isListeningVoice = !current) }
    if (!current) {
      // Simulate quick natural speech recognition input
      viewModelScope.launch {
        kotlinx.coroutines.delay(1800)
        val speechSamples = listOf(
          "I need a cheap gym, supermarket, good food and a place to stay near me.",
          "Find cheap vegetarian food nearby under ₹150.",
          "Find a gym under ₹1,000 per month.",
          "Where can I get groceries and bottled water?",
          "I'm new here. What do I need nearby?"
        )
        val randomSpeech = speechSamples.random()
        _uiState.update { it.copy(isListeningVoice = false, searchQuery = randomSpeech) }
        executeSearch(randomSpeech)
      }
    }
  }

  fun setCategory(category: String) {
    _uiState.update { it.copy(selectedCategory = category) }
    if (category != "All") {
      executeSearch("Find $category in ${_uiState.value.currentLocation}")
    }
  }

  fun setTierFilter(tier: String) {
    _uiState.update { it.copy(selectedTierFilter = tier) }
  }

  fun toggleMapView() {
    _uiState.update { it.copy(isMapView = !it.isMapView) }
  }

  fun setLocation(newLocation: String) {
    repository.updateLocation(newLocation)
    _uiState.update { it.copy(currentLocation = newLocation, showLocationPicker = false) }
    executeSearch("Find nearby essentials in $newLocation")
  }

  fun autoDetectLocation() {
    _uiState.update { it.copy(isLocationDetecting = true) }
    viewModelScope.launch {
      kotlinx.coroutines.delay(1200)
      val detectedCities = listOf(
        "Indiranagar, Bengaluru",
        "Kochi Marine Drive, Kerala",
        "RS Puram, Coimbatore",
        "T. Nagar, Chennai"
      )
      val newLoc = detectedCities.random()
      repository.updateLocation(newLoc)
      _uiState.update {
        it.copy(
          isLocationDetecting = false,
          currentLocation = newLoc,
          toastMessage = "📍 Location detected: $newLoc"
        )
      }
    }
  }

  fun openPlaceDetails(place: Place) {
    _uiState.update { it.copy(activeDetailPlace = place) }
  }

  fun closePlaceDetails() {
    _uiState.update { it.copy(activeDetailPlace = null) }
  }

  fun showLocationPicker(show: Boolean) {
    _uiState.update { it.copy(showLocationPicker = show) }
  }

  fun toggleSavePlace(placeId: String) {
    repository.toggleSavePlace(placeId)
    val isSaved = repository.savedPlaceIds.value.contains(placeId)
    _uiState.update {
      it.copy(toastMessage = if (isSaved) "Saved to your Survival list" else "Removed from saved")
    }
  }

  fun clearToast() {
    _uiState.update { it.copy(toastMessage = null) }
  }

  // TRANSLATOR
  fun setTranslateFromLang(lang: String) {
    _uiState.update { it.copy(translateFromLang = lang) }
  }

  fun setTranslateToLang(lang: String) {
    _uiState.update { it.copy(translateToLang = lang) }
  }

  fun swapLanguages() {
    _uiState.update {
      it.copy(
        translateFromLang = it.translateToLang,
        translateToLang = it.translateFromLang,
        translateInputText = it.translateResultText,
        translateResultText = it.translateInputText
      )
    }
  }

  fun onTranslateInputChanged(text: String) {
    _uiState.update { it.copy(translateInputText = text) }
  }

  fun performTranslation() {
    val text = _uiState.value.translateInputText
    if (text.isBlank()) return
    _uiState.update { it.copy(isTranslating = true) }
    viewModelScope.launch {
      val res = aiOrchestrator.translateText(
        text = text,
        fromLang = _uiState.value.translateFromLang,
        toLang = _uiState.value.translateToLang
      )
      _uiState.update {
        it.copy(
          isTranslating = false,
          translateResultText = res
        )
      }
    }
  }

  // OCR
  fun performOcrScan(sampleText: String? = null) {
    val text = sampleText ?: _uiState.value.ocrDetectedText
    _uiState.update { it.copy(isProcessingOcr = true, ocrDetectedText = text) }
    viewModelScope.launch {
      val result = aiOrchestrator.processOcrImage(
        detectedText = text,
        targetLanguage = _uiState.value.translateToLang
      )
      _uiState.update {
        it.copy(
          isProcessingOcr = false,
          ocrResult = result
        )
      }
    }
  }

  // Preferences
  fun updatePreferences(newPrefs: UserPreferences) {
    repository.updatePreferences(newPrefs)
    _uiState.update { it.copy(toastMessage = "Survival preferences updated") }
  }

  fun finishOnboarding() {
    _uiState.update { it.copy(showOnboarding = false) }
  }
}
