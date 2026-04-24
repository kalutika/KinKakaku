# BTMC API Troubleshooting Guide

## 🔧 Common Issues and Solutions

---

## ❌ Problem: "No data available" message

### Possible Causes:
1. API returned empty response
2. Network connectivity issues
3. API key invalid or expired
4. API server down

### Solutions:

#### Step 1: Check LogCat for Errors
```bash
# In Android Studio LogCat, filter by:
BTMC API Error
```

#### Step 2: Test API Directly
Use a browser or curl to test:
```bash
curl "http://api.btmc.vn/api/BTMCAPI/getpricebtmc?key=3kd8ub1llcg9t45hnoh8hmn7t5kc2v"
```

#### Step 3: Verify Internet Permission
Check `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

#### Step 4: Check API Response Format
The API might have changed format. Update `BtmcResponse` model if needed.

---

## ❌ Problem: App crashes on launch

### Possible Causes:
1. Missing Koin dependency initialization
2. Network call on main thread
3. Serialization issues

### Solutions:

#### Check Koin Initialization
In your `Application` class:
```kotlin
class KinKakakuApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()  // Must be called
    }
}
```

#### Verify Coroutine Usage
API calls should be in coroutine scope:
```kotlin
viewModelScope.launch {
    // API call here
}
```

---

## ❌ Problem: Prices not displaying correctly

### Possible Causes:
1. Price fields are null
2. String to Double conversion failed
3. API response structure changed

### Solutions:

#### Add Logging to Check Data
In `ApiService.kt`:
```kotlin
response.DataList.Data.forEach { item ->
    println("Product: ${item.getName()}")
    println("Buy: ${item.`@pb_1`}, ${item.`@pb_2`}")
    println("Sell: ${item.`@ps_1`}, ${item.`@ps_2`}")
}
```

#### Check for String Format Issues
Prices might have spaces or special characters:
```kotlin
fun getBuyPrice(): Double? {
    val priceStr = `@pb_1` ?: `@pb_2` ?: `@pb_3` ?: `@pb_4`
    return priceStr?.replace(",", "")
                   ?.replace(" ", "")
                   ?.toDoubleOrNull()
}
```

---

## ❌ Problem: Error: "Unresolved reference: HttpClient"

### Possible Causes:
1. Missing Ktor dependencies
2. Wrong package import

### Solutions:

#### Check build.gradle.kts (shared module)
```kotlin
dependencies {
    implementation("io.ktor:ktor-client-core:2.3.0")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.0")
    implementation("io.ktor:ktor-client-android:2.3.0") // For Android
}
```

#### Sync Gradle
```bash
./gradlew clean build --refresh-dependencies
```

---

## ❌ Problem: "SSL/TLS handshake failed"

### Possible Causes:
1. Android security policy blocking HTTP
2. Certificate issues

### Solutions:

#### Option 1: Use HTTPS (Recommended)
Update API URL to use HTTPS:
```kotlin
"https://api.btmc.vn/api/BTMCAPI/getpricebtmc?key=..."
```

#### Option 2: Allow Cleartext Traffic (Debug only)
In `AndroidManifest.xml`:
```xml
<application
    android:usesCleartextTraffic="true"
    ...>
```

⚠️ **Warning**: Only use cleartext for debugging. Use HTTPS in production!

---

## ❌ Problem: Data loads but UI doesn't update

### Possible Causes:
1. State not being collected properly
2. UI not observing state changes
3. Compose recomposition issues

### Solutions:

#### Verify State Collection
In `DataGridScreen.kt`:
```kotlin
val uiState by viewModel.uiState.collectAsState()
```

#### Add Logging
In `DataViewModel.kt`:
```kotlin
_uiState.value = _uiState.value.copy(
    isLoading = false,
    data = items
)
println("Data updated: ${items.size} items")
```

#### Force Recomposition
```kotlin
// Add key to force recomposition
LazyVerticalGrid(
    key = { it.id }
) {
    items(data) { item ->
        DataItemCard(item = item)
    }
}
```

---

## ❌ Problem: "Parameter 'e' is never used" Warning

### This is a FALSE WARNING!

The exception IS being used (we log it). This warning can be safely ignored or suppressed:

```kotlin
@Suppress("UNUSED_VARIABLE")
catch (e: Exception) {
    println("BTMC API Error: ${e.message}")
    e.printStackTrace()
    emptyList()
}
```

---

## ❌ Problem: JSON parsing error

### Possible Causes:
1. API response format changed from XML to JSON
2. Response structure different than expected

### Solutions:

#### Test API Response Format
```bash
curl -I "http://api.btmc.vn/api/BTMCAPI/getpricebtmc?key=3kd8ub1llcg9t45hnoh8hmn7t5kc2v"
# Check Content-Type header
```

#### Handle Both XML and JSON
The current setup uses JSON serialization. If API returns XML, you need to parse it differently.

#### Add Response Logging
```kotlin
val responseText = httpClient.get(
    "http://api.btmc.vn/api/BTMCAPI/getpricebtmc?key=..."
).bodyAsText()

println("Raw response: $responseText")

// Then parse
val response = Json.decodeFromString<BtmcResponse>(responseText)
```

---

## ❌ Problem: App works in emulator but not on device

### Possible Causes:
1. Network security configuration
2. Device-specific issues
3. ProGuard/R8 obfuscating serialization classes

### Solutions:

#### Add Network Security Config
Create `res/xml/network_security_config.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">api.btmc.vn</domain>
    </domain-config>
</network-security-config>
```

Reference in `AndroidManifest.xml`:
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ...>
```

#### ProGuard Rules
Add to `proguard-rules.pro`:
```proguard
# Keep data models for serialization
-keep class com.app.kinkakaku.shared.network.** { *; }
-keep class com.app.kinkakaku.shared.model.** { *; }

# Keep Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
```

---

## ❌ Problem: Slow loading or timeouts

### Possible Causes:
1. API server slow
2. Network timeout too short
3. Large response size

### Solutions:

#### Increase Timeout
```kotlin
private val httpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 15000  // 15 seconds
        connectTimeoutMillis = 15000
        socketTimeoutMillis = 15000
    }
}
```

#### Add Loading Indicator
Already implemented in `DataGridScreen.kt` ✅

---

## 🔍 Debugging Checklist

When something goes wrong, check these in order:

- [ ] 1. Internet connectivity on device
- [ ] 2. LogCat for error messages (filter: "BTMC API Error")
- [ ] 3. API endpoint accessible in browser
- [ ] 4. API key is valid
- [ ] 5. Internet permission in AndroidManifest.xml
- [ ] 6. Koin initialized in Application class
- [ ] 7. Network call is in coroutine scope
- [ ] 8. State collection in UI screen
- [ ] 9. Gradle sync completed successfully
- [ ] 10. No ProGuard issues with serialization

---

## 📊 Testing Tools

### Test API Response
```bash
# Using curl
curl -v "http://api.btmc.vn/api/BTMCAPI/getpricebtmc?key=3kd8ub1llcg9t45hnoh8hmn7t5kc2v"

# Using wget
wget -O - "http://api.btmc.vn/api/BTMCAPI/getpricebtmc?key=3kd8ub1llcg9t45hnoh8hmn7t5kc2v"
```

### Mock API Response for Testing
Create a mock implementation:
```kotlin
class MockApiService : ApiService {
    override suspend fun getDataItems(): List<DataItem> {
        return listOf(
            DataItem(
                id = 1,
                title = "Test Gold SJC 1L",
                description = "Gold Product",
                imageUrl = null,
                price = 75500000.0,
                category = "Gold",
                buyPrice = 75500000.0,
                sellPrice = 76500000.0,
                weight = "1 lượng",
                lastUpdate = "24/04/2026"
            )
        )
    }
}
```

Replace in Koin module for testing:
```kotlin
val testModule = module {
    single<ApiService> { MockApiService() }
    single<DataRepository> { DataRepositoryImpl(get()) }
}
```

---

## 🆘 Still Having Issues?

### 1. Check API Documentation
Visit: https://btmc.vn/thong-tin/tai-lieu-api/api-gia-vang-17784.html

### 2. Contact BTMC Support
They may have updated the API or changed access requirements.

### 3. Enable Verbose Logging
Add more detailed logs:
```kotlin
override suspend fun getDataItems(): List<DataItem> {
    println("⏳ Starting API call...")
    return try {
        println("📡 Fetching data from BTMC API")
        val response = httpClient.get(
            "http://api.btmc.vn/api/BTMCAPI/getpricebtmc?key=3kd8ub1llcg9t45hnoh8hmn7t5kc2v"
        ).body<BtmcResponse>()
        
        println("✅ Response received: ${response.DataList.Data.size} items")
        
        val items = response.DataList.Data.mapIndexed { index, item ->
            println("  Item $index: ${item.getName()} - Buy: ${item.getBuyPrice()}")
            DataItem(...)
        }
        
        println("✅ Successfully mapped ${items.size} items")
        items
    } catch (e: Exception) {
        println("❌ BTMC API Error: ${e.message}")
        println("❌ Stack trace: ${e.stackTraceToString()}")
        emptyList()
    }
}
```

### 4. Check Android Version Compatibility
Some network features require minimum Android API levels:
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

---

## 📝 Report Template

When reporting issues, include:

```
**Environment:**
- Device: [e.g., Pixel 6, Emulator]
- Android Version: [e.g., 13]
- App Version: [e.g., 1.0.0]

**Issue Description:**
[Describe the problem]

**Steps to Reproduce:**
1. [First step]
2. [Second step]
3. [Result]

**Expected Behavior:**
[What should happen]

**Actual Behavior:**
[What actually happens]

**Logs:**
```
[Paste relevant LogCat output]
```

**API Response:**
```
[Paste curl output if available]
```
```

---

**Last Updated**: April 24, 2026

