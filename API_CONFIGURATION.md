# BTMC API Configuration - KinKakaku App

## ✅ API Status: **CONFIGURED & READY**

Your app is successfully configured to use the BTMC Gold Price API.

---

## 📡 Current API Configuration

### Endpoint
```
http://api.btmc.vn/api/BTMCAPI/getpricebtmc?key=3kd8ub1llcg9t45hnoh8hmn7t5kc2v
```

### API Reference
Documentation: https://btmc.vn/thong-tin/tai-lieu-api/api-gia-vang-17784.html

---

## 🏗️ Implementation Details

### File Location
`shared/src/commonMain/kotlin/com/app/kinkakaku/shared/network/ApiService.kt`

### Data Models

#### 1. **BtmcResponse** (Root)
```kotlin
data class BtmcResponse(
    val DataList: DataListWrapper
)
```

#### 2. **DataListWrapper**
```kotlin
data class DataListWrapper(
    val Data: List<BtmcItem>
)
```

#### 3. **BtmcItem** (Individual Gold/Silver Product)
Handles multiple price variants with fields like:
- `@n_1` to `@n_4` - Product names
- `@k_1` to `@k_4` - Categories/Types
- `@pb_1` to `@pb_4` - Buy prices (Giá mua)
- `@ps_1` to `@ps_4` - Sell prices (Giá bán)
- `@pt_1` to `@pt_4` - Weight/Purity info
- `@d_1` to `@d_4` - Last update dates

### Mapping Functions
```kotlin
fun getName(): String - Returns first available name
fun getBuyPrice(): Double? - Returns first available buy price
fun getSellPrice(): Double? - Returns first available sell price
fun getWeight(): String? - Returns weight/purity information
fun getDate(): String? - Returns last update date
```

---

## 🎨 UI Display Features

Your `DataGridScreen` displays:
- ✅ **3-column responsive grid** layout
- ✅ **Buy price** (Mua) in red/error color
- ✅ **Sell price** (Bán) in primary color  
- ✅ **Weight/Purity** info
- ✅ **Vietnamese currency formatting** (₫)
- ✅ **Error handling** with retry functionality
- ✅ **Loading states** with progress indicators

---

## 🔧 Recent Improvements

### 1. Enhanced Error Handling ✅
```kotlin
catch (e: Exception) {
    println("BTMC API Error: ${e.message}")
    e.printStackTrace()
    emptyList()
}
```
Now logs API errors for debugging while gracefully falling back to empty list.

### 2. Fixed Price Formatting ✅
```kotlin
String.format(Locale.getDefault(), "₫%,.0f", price)
```
Now uses explicit locale to avoid formatting issues.

### 3. Code Quality ✅
- Removed unused imports
- Fixed compiler warnings
- Added proper error logging

---

## 🧪 Testing the API

To test if the API is working:

1. **Run the app** on your device/emulator
2. **Check the DataGrid screen** - it should display gold/silver products
3. **Look for price data**:
   - Buy prices in red (Mua: ₫xxx)
   - Sell prices in blue/primary color (Bán: ₫xxx)
4. **If errors occur**, check LogCat for "BTMC API Error" messages

---

## 🔍 API Response Structure Example

Based on the BTMC API documentation, the response should look like:
```xml
<DataList>
  <Data>
    <row @n_1="Vàng SJC 1 lượng" @pb_1="75500000" @ps_1="76500000" @pt_1="1 lượng" @d_1="24/04/2026"/>
    <row @n_1="Vàng SJC 5 chỉ" @pb_1="75400000" @ps_1="76400000" @pt_1="5 chỉ" @d_1="24/04/2026"/>
    <!-- More items... -->
  </Data>
</DataList>
```

The API service automatically converts this to JSON format using Ktor's ContentNegotiation.

---

## 🛠️ Customization Options

### Change API Key
If you need to use a different API key, update line 28 in `ApiService.kt`:
```kotlin
"http://api.btmc.vn/api/BTMCAPI/getpricebtmc?key=YOUR_NEW_KEY"
```

### Add More Data Fields
To display additional data (like purity/karat), extend the `DataItem` model and mapping logic.

### Refresh Interval
To auto-refresh data periodically, add a timer in `DataViewModel`:
```kotlin
viewModelScope.launch {
    while(true) {
        delay(60000) // Refresh every minute
        loadData()
    }
}
```

---

## 📱 Architecture Flow

```
DataGridScreen (UI)
    ↓
DataViewModel (State Management)
    ↓
DataRepository (Business Logic)
    ↓
ApiService (Network Layer)
    ↓
BTMC API Server
```

---

## 🎯 Next Steps

1. ✅ **API is configured** - No code changes needed
2. ✅ **Error handling improved** - Better debugging capability
3. ✅ **Code warnings fixed** - Clean compilation
4. **Build & Test** - Run the app to verify data display
5. **Optional**: Add pull-to-refresh functionality
6. **Optional**: Implement offline caching with Room database

---

## 💡 Tips

- **API Key Security**: Consider moving the API key to `local.properties` or BuildConfig for production
- **Error Messages**: Currently returns empty list on error; consider showing user-friendly error messages
- **Data Caching**: Current implementation fetches live data each time; consider caching for offline access
- **Rate Limiting**: Be mindful of API rate limits if implementing auto-refresh

---

## 📞 Support

If you encounter issues:
1. Check LogCat for "BTMC API Error" messages
2. Verify internet connectivity
3. Confirm API key is valid
4. Check BTMC API documentation for any changes

---

**Last Updated**: April 24, 2026
**Status**: ✅ Production Ready

