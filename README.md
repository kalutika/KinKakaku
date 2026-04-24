# Kin Kakaku - Android KMM App

A modern Android application built with **Kotlin Multiplatform Mobile (KMM)**, **MVVM architecture**, and **Jetpack Compose**.

## 🚀 Features

- **Splash Screen**: 3-second animated loading screen
- **Data Grid**: Responsive 3-column grid displaying API data with images
- **Settings Screen**: Configurable app settings
- **Modern UI**: Material Design 3 with dark/light theme support
- **Network Integration**: Real-time API data fetching
- **Cross-platform Ready**: KMM structure ready for iOS development

## 🏗️ Architecture

### Tech Stack
- **Kotlin Multiplatform Mobile (KMM)** - Shared business logic
- **MVVM Pattern** - Clean architecture with reactive UI
- **Jetpack Compose** - Modern Android UI toolkit
- **Ktor** - Multiplatform HTTP client for API calls
- **Koin** - Dependency injection framework
- **Coroutines & Flow** - Asynchronous programming and reactive streams

### Project Structure
```
kinkakaku/
├── shared/                          # KMM shared module
│   └── src/commonMain/kotlin/
│       └── com/app/kinkakaku/shared/
│           ├── model/               # Data models
│           ├── network/             # API service layer
│           ├── repository/          # Repository pattern implementation
│           └── di/                  # Shared dependency injection
├── app/                            # Android application module
│   └── src/main/java/com/app/kinkakaku/
│       ├── ui/screens/             # Compose UI screens
│       ├── ui/viewmodel/           # MVVM ViewModels
│       ├── ui/theme/               # Material Design theme
│       ├── navigation/             # Navigation setup
│       └── di/                     # Android-specific DI
└── gradle/                         # Gradle configuration
```

## 📱 Screenshots

*Screenshots will be added once the app is running*

## 🔧 Setup & Installation

### Prerequisites
- Android Studio (latest version)
- JDK 11 or higher
- Android SDK API level 31+

### Building the Project
1. Clone the repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/kinkakaku.git
   cd kinkakaku
   ```

2. Open in Android Studio

3. Build the project:
   ```bash
   ./gradlew build
   ```

4. Run on device/emulator:
   ```bash
   ./gradlew assembleDebug
   ```

## 🌐 API Integration

The app currently uses JSONPlaceholder API for demonstration. To integrate your own API:

1. Update `ApiServiceImpl` in the shared module:
   ```kotlin
   // shared/src/commonMain/kotlin/com/app/kinkakaku/shared/network/ApiService.kt
   override suspend fun getDataItems(): List<DataItem> {
       return httpClient.get("YOUR_API_ENDPOINT").body()
   }
   ```

2. Modify `DataItem` model to match your API response:
   ```kotlin
   // shared/src/commonMain/kotlin/com/app/kinkakaku/shared/model/DataItem.kt
   @Serializable
   data class DataItem(
       // Add your fields here
   )
   ```

## 🎯 Key Features Implementation

### Splash Screen
- 3-second timer with loading animation
- Smooth navigation to main screen
- Material Design loading indicators

### Data Grid
- Responsive 3-column layout
- Async image loading with Coil
- Error handling with retry functionality
- Pull-to-refresh capability (can be added)

### MVVM with State Management
```kotlin
data class DataUiState(
    val isLoading: Boolean = false,
    val data: List<DataItem> = emptyList(),
    val error: String? = null
)
```

### Dependency Injection
- Shared module dependencies (Koin)
- Platform-specific implementations
- ViewModel injection with Compose

## 🔮 Roadmap

- [ ] iOS app development (KMM ready)
- [ ] Offline data caching
- [ ] User authentication
- [ ] Advanced filtering and search
- [ ] Push notifications
- [ ] Unit and UI testing

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern UI toolkit
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) - Cross-platform development
- [Ktor](https://ktor.io/) - Multiplatform HTTP client
- [Koin](https://insert-koin.io/) - Dependency injection framework

---

**Built with ❤️ using Kotlin Multiplatform Mobile**
