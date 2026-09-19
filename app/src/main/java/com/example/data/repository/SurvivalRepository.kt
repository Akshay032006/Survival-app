package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SurvivalRepository {

  private val _userPreferences = MutableStateFlow(
    UserPreferences(
      preferredName = "Traveler",
      budgetTier = "Budget",
      foodPreference = "Vegetarian",
      stayType = "PG / Hostel",
      transportPreference = "Metro / Bus",
      maxDistanceKm = 2.5,
      primaryLanguage = "English",
      targetLanguage = "Malayalam",
      fitnessGoal = "General Gym Access",
      onboardingCompleted = true
    )
  )
  val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()

  private val _savedPlaceIds = MutableStateFlow<Set<String>>(setOf("food_1", "stay_1"))
  val savedPlaceIds: StateFlow<Set<String>> = _savedPlaceIds.asStateFlow()

  private val _currentLocationName = MutableStateFlow("Indiranagar, Bengaluru")
  val currentLocationName: StateFlow<String> = _currentLocationName.asStateFlow()

  fun updateLocation(newLocation: String) {
    _currentLocationName.value = newLocation
  }

  fun updatePreferences(preferences: UserPreferences) {
    _userPreferences.value = preferences
  }

  fun toggleSavePlace(placeId: String) {
    _savedPlaceIds.update { set ->
      if (set.contains(placeId)) set - placeId else set + placeId
    }
  }

  fun getAllPlaces(cityName: String = _currentLocationName.value): List<Place> {
    val isCoimbatore = cityName.contains("Coimbatore", ignoreCase = true)
    val isKochi = cityName.contains("Kochi", ignoreCase = true) || cityName.contains("Kollam", ignoreCase = true)
    val prefix = when {
      isCoimbatore -> "Coimbatore"
      isKochi -> "Kochi"
      else -> "Indiranagar"
    }

    return listOf(
      // FOOD
      Place(
        id = "food_1",
        name = "$prefix Udupi Grand Pure Veg",
        category = "Food",
        subcategory = "Pure Veg South Indian & Thali",
        rating = 4.6f,
        reviewCount = 1420,
        distanceKm = 0.6,
        address = "12th Main Road, near Metro Pillar 42, $cityName",
        priceRange = "₹80 - ₹180",
        openStatus = "Open now • Closes 10:30 PM",
        isOpenNow = true,
        facilities = listOf("Pure Vegetarian", "AC Dining", "Quick Service", "Filter Coffee"),
        tier = "BEST",
        whyItMatches = "Under ₹200 budget + pure vegetarian + highly rated crispy dosas & meals + 600m walk.",
        sourceName = "FSSAI & Local Food Directory",
        isVerified = true,
        lastChecked = "Verified today 8:00 AM",
        phone = "+91 80 2521 4455",
        specialOffer = "Lunch mini-thali with buttermilk ₹120"
      ),
      Place(
        id = "food_2",
        name = "Annapoorna Mess & Tiffin",
        category = "Food",
        subcategory = "Budget Homestyle Meals",
        rating = 4.3f,
        reviewCount = 830,
        distanceKm = 0.4,
        address = "Cross Road 4, Market Junction, $cityName",
        priceRange = "₹60 - ₹110",
        openStatus = "Open now • Closes 9:30 PM",
        isOpenNow = true,
        facilities = listOf("Affordable", "Unlimited Rice", "Vegetarian", "Takeaway"),
        tier = "GOOD",
        whyItMatches = "Most economical authentic meal in the vicinity. Freshly steamed idlis & meals under ₹100.",
        sourceName = "Local Street Food Council",
        isVerified = true,
        lastChecked = "Yesterday",
        phone = "+91 94471 23450"
      ),
      Place(
        id = "food_3",
        name = "Spice Coastal & Malabar Kitchen",
        category = "Food",
        subcategory = "Kerala & Chettinad Cuisine",
        rating = 4.5f,
        reviewCount = 2100,
        distanceKm = 1.4,
        address = "100 Ft Road, Opposite Reliance, $cityName",
        priceRange = "₹220 - ₹450",
        openStatus = "Open now • Closes 11:00 PM",
        isOpenNow = true,
        facilities = listOf("Biryani", "Seafood", "AC Family Hall", "Card / UPI"),
        tier = "BETTER",
        whyItMatches = "Balanced pricing with high ratings for Malabar Parotta, Dum Biryani & Veg Stew.",
        sourceName = "Verified Food Guide",
        isVerified = true,
        lastChecked = "Verified today"
      ),
      Place(
        id = "food_4",
        name = "The Heritage Bistro & Fine Dining",
        category = "Food",
        subcategory = "Continental & Artisanal",
        rating = 4.8f,
        reviewCount = 950,
        distanceKm = 2.1,
        address = "Club Road, $cityName",
        priceRange = "₹600 - ₹1,400",
        openStatus = "Open now • Closes Midnight",
        isOpenNow = true,
        facilities = listOf("Valet Parking", "Artisanal Coffee", "Quiet Workspace", "Premium Ambience"),
        tier = "PREMIUM",
        whyItMatches = "Top-tier dining experience with organic farm ingredients, quiet ambience for meetings.",
        sourceName = "Zomato Verified Partner",
        isVerified = true,
        lastChecked = "Verified 2 days ago"
      ),

      // STAY / ACCOMMODATION
      Place(
        id = "stay_1",
        name = "Zostel & Nomad Backpacker Stay",
        category = "Stay",
        subcategory = "Hostel / Co-living",
        rating = 4.7f,
        reviewCount = 1860,
        distanceKm = 1.1,
        address = "Defense Colony, 2nd Stage, $cityName",
        priceRange = "₹550 - ₹950 / night",
        openStatus = "24/7 Reception",
        isOpenNow = true,
        facilities = listOf("High-Speed Wi-Fi", "AC Dorms", "Private Lockers", "Community Kitchen", "RO Water"),
        tier = "BEST",
        whyItMatches = "Top pick for newcomers: reliable 100Mbps Wi-Fi, helpful staff for local city tips, safe and social.",
        sourceName = "Verified Booking Network",
        isVerified = true,
        lastChecked = "Real-time room availability checked",
        phone = "+91 80 4112 8899",
        specialOffer = "Weekly stay discount: 10% off for >5 nights"
      ),
      Place(
        id = "stay_2",
        name = "Sri Balaji Executive PG & Rooms",
        category = "Stay",
        subcategory = "Monthly PG & Coliving",
        rating = 4.1f,
        reviewCount = 420,
        distanceKm = 0.8,
        address = "Near Post Office, $cityName",
        priceRange = "₹6,500 - ₹8,500 / month",
        openStatus = "Manager available 8 AM - 9 PM",
        isOpenNow = true,
        facilities = listOf("3 Times Food Included", "Wi-Fi", "Washing Machine", "Geyser"),
        tier = "GOOD",
        whyItMatches = "Budget-friendly long term stay with home food included, ideal for students and fresh job joiners.",
        sourceName = "Local Housing Registry",
        isVerified = true,
        lastChecked = "Verified 1 day ago",
        phone = "+91 98450 11223"
      ),
      Place(
        id = "stay_3",
        name = "Treebo Trend Transit Inn",
        category = "Stay",
        subcategory = "Budget Hotel near Station",
        rating = 4.4f,
        reviewCount = 920,
        distanceKm = 1.8,
        address = "Station Road, near Railway North Gate, $cityName",
        priceRange = "₹1,400 - ₹2,100 / night",
        openStatus = "24/7 Front Desk",
        isOpenNow = true,
        facilities = listOf("Attached Bath", "Complimentary Breakfast", "Lift", "AC"),
        tier = "BETTER",
        whyItMatches = "Dependable standard hotel right next to railway/bus terminals, clean linen & fast check-in.",
        sourceName = "Official Hotel Chain API",
        isVerified = true,
        lastChecked = "Verified today"
      ),
      Place(
        id = "stay_4",
        name = "The Grand Sanctuary Serviced Suites",
        category = "Stay",
        subcategory = "Luxury Serviced Apartments",
        rating = 4.9f,
        reviewCount = 640,
        distanceKm = 2.8,
        address = "Skyline Boulevard, $cityName",
        priceRange = "₹3,800 - ₹6,500 / night",
        openStatus = "24/7 Concierge",
        isOpenNow = true,
        facilities = listOf("Swimming Pool", "Gym", "Kitchenette", "Airport Shuttle"),
        tier = "PREMIUM",
        whyItMatches = "Executive level comfort with full kitchenette, daily housekeeping, and premium security.",
        sourceName = "Verified Luxury Portal",
        isVerified = true,
        lastChecked = "Verified today"
      ),

      // GYMS
      Place(
        id = "gym_1",
        name = "IronForge Fitness & Strength Hub",
        category = "Gyms",
        subcategory = "Unisex Fitness & Gym",
        rating = 4.8f,
        reviewCount = 620,
        distanceKm = 0.9,
        address = "3rd Floor, Above More Supermarket, $cityName",
        priceRange = "₹900 - ₹1,200 / month",
        openStatus = "Open now • 5:30 AM - 10:00 PM",
        isOpenNow = true,
        facilities = listOf("AC", "Modern Cardio & Free Weights", "Certified Trainer Included", "Locker Room"),
        tier = "BEST",
        whyItMatches = "Exactly fits budget under ₹1,000/mo + only 900m away + excellent equipment maintenance.",
        sourceName = "Local Fitness Association",
        isVerified = true,
        lastChecked = "Verified membership rate yesterday",
        phone = "+91 80 4333 7777",
        specialOffer = "Quarterly plan ₹2,500 with free diet chart"
      ),
      Place(
        id = "gym_2",
        name = "Community Health Club Gym",
        category = "Gyms",
        subcategory = "Basic Weightlifting Gym",
        rating = 4.2f,
        reviewCount = 210,
        distanceKm = 0.5,
        address = "Old Municipality Lane, $cityName",
        priceRange = "₹500 - ₹700 / month",
        openStatus = "Open now • 6 AM - 12 PM, 4 PM - 9 PM",
        isOpenNow = true,
        facilities = listOf("Free Weights", "Dumbbells up to 40kg", "Heavy Bags", "No AC"),
        tier = "GOOD",
        whyItMatches = "Most economical barebones gym for daily iron pumping without expensive frills.",
        sourceName = "Verified Local Business Record",
        isVerified = true,
        lastChecked = "Verified 3 days ago"
      ),
      Place(
        id = "gym_3",
        name = "Cult.fit Elite Center",
        category = "Gyms",
        subcategory = "Premium Group Classes & Functional Gym",
        rating = 4.9f,
        reviewCount = 2400,
        distanceKm = 1.9,
        address = "CMH Road Junction, $cityName",
        priceRange = "₹2,200 / month (Annual pass)",
        openStatus = "Open now • 6:00 AM - 10:30 PM",
        isOpenNow = true,
        facilities = listOf("Yoga", "Boxing", "Strength & Conditioning", "Shower Suites", "Steam Bath"),
        tier = "PREMIUM",
        whyItMatches = "Nationwide access with group workouts, app-based locker booking, and high-end instructors.",
        sourceName = "Cult Official Portal",
        isVerified = true,
        lastChecked = "Verified today"
      ),

      // GROCERY
      Place(
        id = "grocery_1",
        name = "More Hypermarket & Fresh",
        category = "Grocery",
        subcategory = "Supermarket & Daily Essentials",
        rating = 4.5f,
        reviewCount = 1950,
        distanceKm = 0.4,
        address = "Corner of 10th Main, $cityName",
        priceRange = "MRP with 5-15% discounts",
        openStatus = "Open now • 7:30 AM - 10:30 PM",
        isOpenNow = true,
        facilities = listOf("Fresh Produce", "Dairy & Bread", "Toiletries & Buckets", "UPI & Cards", "Wheelchair Accessible"),
        tier = "BEST",
        whyItMatches = "One-stop destination for new arrivals: buy pillows, toiletries, water bottles, and vegetables in 1 go.",
        sourceName = "Official Retail Registry",
        isVerified = true,
        lastChecked = "Verified today",
        phone = "+91 80 2520 1100"
      ),
      Place(
        id = "grocery_2",
        name = "Lakshmi Kirana & Essentials Store",
        category = "Grocery",
        subcategory = "Local Corner Kirana & Milk Booth",
        rating = 4.6f,
        reviewCount = 380,
        distanceKm = 0.2,
        address = "Next to Bus Stop 14, $cityName",
        priceRange = "Affordable local pricing",
        openStatus = "Open now • 6:30 AM - 11:00 PM",
        isOpenNow = true,
        facilities = listOf("Quick 2-minute purchase", "Loose provisions", "Cold Drinks", "Curd & Milk 24L"),
        tier = "GOOD",
        whyItMatches = "Just 200m away, open late for midnight water, bread, biscuits, and phone recharges.",
        sourceName = "Local Merchants DB",
        isVerified = true,
        lastChecked = "Verified yesterday"
      ),

      // PHARMACY & HOSPITALS
      Place(
        id = "pharmacy_1",
        name = "Apollo 24/7 Pharmacy & Clinic",
        category = "Pharmacy",
        subcategory = "24-Hour Chemist & First Aid",
        rating = 4.7f,
        reviewCount = 1120,
        distanceKm = 0.5,
        address = "Main Ring Road near Metro Exit 2, $cityName",
        priceRange = "Standard Government MRP",
        openStatus = "Open 24 Hours",
        isOpenNow = true,
        facilities = listOf("24/7 Open", "Prescription & OTC", "First Aid Dressing", "Free BP Check", "Digital Receipts"),
        tier = "BEST",
        whyItMatches = "Crucial survival resource: guaranteed 24/7 open status, genuine medicines, emergency ORS & painkillers.",
        sourceName = "Apollo Verified Database",
        isVerified = true,
        lastChecked = "Verified 24/7 status today",
        phone = "+91 80 2424 0000"
      ),
      Place(
        id = "hospital_1",
        name = "City Care Multi-Specialty Government & Trust Hospital",
        category = "Hospitals",
        subcategory = "24/7 Emergency & Trauma Care",
        rating = 4.4f,
        reviewCount = 2800,
        distanceKm = 1.3,
        address = "Hospital Road, Civil Station, $cityName",
        priceRange = "Affordable / Subsidized Emergency Care",
        openStatus = "24/7 Emergency Department Active",
        isOpenNow = true,
        facilities = listOf("ICU", "24/7 Casualty", "Blood Bank", "Ambulance fleet", "Jan Aushadhi Store"),
        tier = "BEST",
        whyItMatches = "Closest licensed trauma & emergency facility with active round-the-clock doctor on duty.",
        sourceName = "District Health Administration",
        isVerified = true,
        lastChecked = "Verified emergency status today",
        phone = "108 / +91 80 2297 5000"
      ),

      // ATM & MOBILE & LAUNDRY
      Place(
        id = "atm_1",
        name = "SBI & HDFC 24/7 ATM Hub",
        category = "ATM",
        subcategory = "Cash Dispenser & Deposit Machine",
        rating = 4.3f,
        reviewCount = 490,
        distanceKm = 0.3,
        address = "Opposite Metro Station, $cityName",
        priceRange = "Free Inter-Bank Withdrawals",
        openStatus = "Open 24 Hours • Guard on duty",
        isOpenNow = true,
        facilities = listOf("Air Conditioned", "24/7 Cash Available", "Cash Deposit", "Security Guard"),
        tier = "BEST",
        whyItMatches = "Frequently replenished with currency, dual machines reduce waiting time, well-lit street location.",
        sourceName = "National Payments Network",
        isVerified = true,
        lastChecked = "Cash status active 1 hour ago"
      ),
      Place(
        id = "mobile_1",
        name = "Airtel & Jio Express SIM & Telecom Store",
        category = "Mobile",
        subcategory = "SIM Activation, eSIM & Mobile Accessories",
        rating = 4.5f,
        reviewCount = 670,
        distanceKm = 0.5,
        address = "1st Floor, City Center Arcade, $cityName",
        priceRange = "SIM Activation ₹0 - ₹299 (Plan included)",
        openStatus = "Open now • 9:30 AM - 9:00 PM",
        isOpenNow = true,
        facilities = listOf("Instant Biometric KYC", "Foreign Passport Support", "eSIM setup", "Adapters & Powerbanks"),
        tier = "BEST",
        whyItMatches = "Essential for out-of-state/international visitors. Issues verified local SIM within 15 minutes with Aadhaar/Passport.",
        sourceName = "DoT Licensed Retailer",
        isVerified = true,
        lastChecked = "Verified today"
      ),
      Place(
        id = "laundry_1",
        name = "SpeedWash Dry Clean & Wash-Fold Laundromat",
        category = "Laundry",
        subcategory = "Kilo Laundry & Express Ironing",
        rating = 4.6f,
        reviewCount = 310,
        distanceKm = 0.7,
        address = "5th Cross, Residential Layout, $cityName",
        priceRange = "₹60 / kg wash & fold",
        openStatus = "Open now • 8:00 AM - 8:30 PM",
        isOpenNow = true,
        facilities = listOf("Same-day express delivery", "Doorstep pickup", "Stain removal", "Steam press"),
        tier = "BEST",
        whyItMatches = "Solves the biggest hassle for newcomers without a washing machine: clean hygienic per-kilo clothes washing.",
        sourceName = "Verified Service Provider",
        isVerified = true,
        lastChecked = "Verified yesterday",
        phone = "+91 97412 88334"
      )
    )
  }

  fun getTransportOptions(cityName: String = _currentLocationName.value): List<TransportOption> {
    val isKerala = cityName.contains("Kochi", ignoreCase = true) || cityName.contains("Kollam", ignoreCase = true)
    return if (isKerala) {
      listOf(
        TransportOption(
          id = "tr_1",
          mode = "Train",
          modeEmoji = "🚆",
          routeNumber = "16343 Amritha Express",
          fromStation = "Kollam Junction (QLN)",
          toStation = "Coimbatore Junction (CBE)",
          departureTime = "6:30 AM",
          arrivalTime = "9:15 AM",
          duration = "2h 45m",
          stops = 4,
          transfers = 0,
          price = "₹120 (2S) / ₹420 (CC)",
          distance = "182 km",
          source = "Indian Railways (NTES Live)",
          isVerified = true,
          lastUpdated = "Today 06:10 AM"
        ),
        TransportOption(
          id = "tr_2",
          mode = "Bus",
          modeEmoji = "🚌",
          routeNumber = "KSRTC Swift Super Fast",
          fromStation = "Kollam KSRTC Bus Stand",
          toStation = "Ernakulam South Hub",
          departureTime = "7:15 AM",
          arrivalTime = "10:30 AM",
          duration = "3h 15m",
          stops = 8,
          transfers = 0,
          price = "₹185",
          distance = "142 km",
          source = "KSRTC Official Portal",
          isVerified = true,
          lastUpdated = "Live Timetable"
        ),
        TransportOption(
          id = "tr_3",
          mode = "Metro",
          modeEmoji = "🚇",
          routeNumber = "KMRL Blue Line",
          fromStation = "Aluva Terminal",
          toStation = "MG Road / Petta",
          departureTime = "Every 7 mins",
          arrivalTime = "Frequent",
          duration = "28m",
          stops = 14,
          transfers = 0,
          price = "₹35 - ₹50",
          distance = "18 km",
          source = "Kochi Metro Rail Limited",
          isVerified = true,
          lastUpdated = "Schedule Verified"
        ),
        TransportOption(
          id = "tr_4",
          mode = "Auto",
          modeEmoji = "🛺",
          routeNumber = "Local Metered Auto Stand",
          fromStation = "Railway Station Stand",
          toStation = "Any City Destination (<5km)",
          departureTime = "Available 24/7",
          arrivalTime = "Immediate",
          duration = "10-20m",
          stops = 0,
          transfers = 0,
          price = "₹30 minimum + ₹15/km",
          distance = "Local",
          source = "Motor Vehicles Dept Govt Rate",
          isVerified = true,
          lastUpdated = "Official Tariff 2026"
        )
      )
    } else {
      listOf(
        TransportOption(
          id = "tr_10",
          mode = "Metro",
          modeEmoji = "🚇",
          routeNumber = "Namma Metro Purple Line",
          fromStation = "Indiranagar Metro Station",
          toStation = "Majestic Inter-City Railway & Bus Hub",
          departureTime = "Every 4 mins",
          arrivalTime = "Frequent (05:00 - 23:30)",
          duration = "18m",
          stops = 8,
          transfers = 0,
          price = "₹28",
          distance = "8.4 km",
          source = "BMRCL Official Transit Feed",
          isVerified = true,
          lastUpdated = "Verified Live Frequency"
        ),
        TransportOption(
          id = "tr_11",
          mode = "Bus",
          modeEmoji = "🚌",
          routeNumber = "BMTC V-335E AC Airport Express",
          fromStation = "Indiranagar 100ft Road",
          toStation = "Kempegowda Int'l Airport (BLR)",
          departureTime = "06:15 AM (Every 30m)",
          arrivalTime = "07:35 AM",
          duration = "1h 20m",
          stops = 6,
          transfers = 0,
          price = "₹240",
          distance = "38 km",
          source = "BMTC Transit Authority",
          isVerified = true,
          lastUpdated = "Updated 30 mins ago"
        ),
        TransportOption(
          id = "tr_12",
          mode = "Train",
          modeEmoji = "🚆",
          routeNumber = "12678 InterCity SuperFast",
          fromStation = "Bengaluru City (SBC)",
          toStation = "Coimbatore Main (CBE)",
          departureTime = "06:10 AM",
          arrivalTime = "12:50 PM",
          duration = "6h 40m",
          stops = 7,
          transfers = 0,
          price = "₹170 (2S) / ₹580 (CC)",
          distance = "379 km",
          source = "Indian Railways Confirmed",
          isVerified = true,
          lastUpdated = "Today 05:00 AM"
        ),
        TransportOption(
          id = "tr_13",
          mode = "Auto",
          modeEmoji = "🛺",
          routeNumber = "Prepaid Auto & Namma Yatri Stand",
          fromStation = "Metro Exit Gate 1",
          toStation = "Local Radius",
          departureTime = "On Demand",
          arrivalTime = "Immediate",
          duration = "Variable",
          stops = 0,
          transfers = 0,
          price = "₹36 base (2km) + ₹18/km",
          distance = "Local",
          source = "Karnataka Transport Dept",
          isVerified = true,
          lastUpdated = "Verified Gazette Tariff"
        )
      )
    }
  }

  fun getEmergencyContacts(): List<EmergencyContact> {
    return listOf(
      EmergencyContact(
        id = "em_1",
        name = "National Emergency Integrated Response System",
        number = "112",
        category = "Police, Fire, Ambulance & Rescue",
        distance = "Toll-Free Nationwide",
        address = "Unified Dispatch Central Operations",
        isNational = true
      ),
      EmergencyContact(
        id = "em_2",
        name = "State Medical & Ambulance Dispatch",
        number = "108",
        category = "Medical Emergency & Life Support",
        distance = "Nearest Emergency Van (<10 mins)",
        address = "Govt Emergency Ambulance Service",
        isNational = true
      ),
      EmergencyContact(
        id = "em_3",
        name = "Women Safety & Immediate Helpline",
        number = "1091",
        category = "Women Assistance & Rapid Response",
        distance = "24/7 Dedicated Patrol",
        address = "State Police Women Protection Cell",
        isNational = true
      ),
      EmergencyContact(
        id = "em_4",
        name = "Nearest Police Station",
        number = "100 / +91 80 2294 2200",
        category = "Law Enforcement & Local Beat",
        distance = "0.7 km away",
        address = "12th Cross, Station Road, Local Division",
        isNational = false
      ),
      EmergencyContact(
        id = "em_5",
        name = "24/7 Government Trauma & Poison Center",
        number = "+91 80 2297 5000",
        category = "Hospital Casualty & ICU",
        distance = "1.3 km away",
        address = "Civil Hospital Compound, Casualty Block A",
        isNational = false
      )
    )
  }

  fun getFirst24HoursGuide(cityName: String): List<GuideItem> {
    return listOf(
      GuideItem(
        icon = "📍",
        question = "Where am I & how is the area?",
        answer = "You are currently in $cityName. This is a secure, well-connected commercial and residential belt with metro/bus lines, 24/7 medical access, and active street lighting until midnight.",
        actionLabel = "View Safety & Map",
        queryTarget = "Where am I"
      ),
      GuideItem(
        icon = "🏨",
        question = "Where can I safely check in right now?",
        answer = "Trusted options with 24/7 front desk check-in include Zostel (budget backpacker from ₹550) or Treebo Transit Inn (private rooms from ₹1,400).",
        actionLabel = "Find Stay",
        queryTarget = "I need a place to stay near me"
      ),
      GuideItem(
        icon = "🍛",
        question = "Where can I eat hygienic, budget food?",
        answer = "Udupi Grand offers fresh piping hot vegetarian meals, dosas, and filter coffee (₹80-₹150). For homestyle unlimited meals, head to Annapoorna Mess (₹70).",
        actionLabel = "Find Food",
        queryTarget = "Cheap food nearby"
      ),
      GuideItem(
        icon = "💧",
        question = "Where can I get bottled water & essentials?",
        answer = "More Supermarket and Lakshmi Kirana are within 400m for packaged drinking water, soap, toothpaste, towels, and chargers.",
        actionLabel = "Find Grocery",
        queryTarget = "Where can I get groceries and essentials"
      ),
      GuideItem(
        icon = "🏧",
        question = "Where is a safe ATM with cash available?",
        answer = "SBI & HDFC 24/7 ATM hub is located 300m away opposite the metro station, equipped with dual machines and security guard.",
        actionLabel = "Find ATM",
        queryTarget = "Where is an ATM nearby"
      ),
      GuideItem(
        icon = "📱",
        question = "Where can I get a local SIM or data plan?",
        answer = "Airtel / Jio store at City Center Arcade issues instant activated SIM cards using Aadhaar or Passport KYC in 15 minutes.",
        actionLabel = "Find Mobile Store",
        queryTarget = "Where can I get a SIM card"
      ),
      GuideItem(
        icon = "🚆",
        question = "How do I get around without getting overcharged?",
        answer = "Use the Metro line for fastest transit, or use metered autos with Govt tariff (₹36 for first 2km). Avoid unmetered private taxis outside the station gate.",
        actionLabel = "Transport Hub",
        queryTarget = "Find transport nearby"
      ),
      GuideItem(
        icon = "🗣️",
        question = "What language is commonly used here?",
        answer = "The regional language is widely spoken alongside English and Hindi for transit, groceries, and dining. Survival app provides instant Voice-to-Voice translation.",
        actionLabel = "Open Translator",
        queryTarget = "Translate"
      )
    )
  }

  fun getMovingCitySetup(cityName: String): List<CitySetupItem> {
    return listOf(
      CitySetupItem(
        category = "Accommodation",
        emoji = "🏠",
        recommendedOption = "Single/Double Shared PG with 3 Meals & Wi-Fi",
        monthlyEstimate = "₹7,500 - ₹9,500 / mo",
        detail = "Includes electricity, daily housekeeping, Wi-Fi, and 3 vegetarian/non-veg meals."
      ),
      CitySetupItem(
        category = "Food & Dining Out",
        emoji = "🍛",
        recommendedOption = "Local Mess + Occasional Weekend Cafes",
        monthlyEstimate = "₹3,500 - ₹4,800 / mo",
        detail = "Standard breakfast ₹50, lunch thali ₹80, dinner ₹80 + ₹1,000 recreation buffer."
      ),
      CitySetupItem(
        category = "Groceries & Toiletries",
        emoji = "🛒",
        recommendedOption = "Supermarket + Local Vegetable Market",
        monthlyEstimate = "₹1,800 - ₹2,500 / mo",
        detail = "Fruits, dry fruits, snacks, laundry detergents, and personal care supplies."
      ),
      CitySetupItem(
        category = "Gym & Wellness",
        emoji = "🏋️",
        recommendedOption = "IronForge Fitness Monthly Membership",
        monthlyEstimate = "₹900 - ₹1,100 / mo",
        detail = "Full cardio & weights access with locker and personal trainer floor support."
      ),
      CitySetupItem(
        category = "Daily Commute",
        emoji = "🚌",
        recommendedOption = "Metro Smart Card + Local BMTC/KSRTC Bus Pass",
        monthlyEstimate = "₹1,200 - ₹1,800 / mo",
        detail = "Unlimited daily bus travel or daily 8km round-trip metro commute."
      ),
      CitySetupItem(
        category = "Estimated Total Monthly Survival Budget",
        emoji = "💰",
        recommendedOption = "Comprehensive First-Month Setup",
        monthlyEstimate = "₹15,000 - ₹19,500 / mo",
        detail = "Complete living setup in $cityName with all necessities and zero hidden surprises."
      )
    )
  }
}
