package ministudio.app.kinkakaku.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Gold = Color(0xFFC8A24A)
private val GoldSoft = Color(0xFFF3E5B5)
private val GoldDeep = Color(0xFF8E6A1E)
private val SurfaceLight = Color(0xFFFBF8F1)
private val SurfaceVariantLight = Color(0xFFF1E8D8)
private val OutlineLight = Color(0xFFD8C7A3)

private val SurfaceDark = Color(0xFF171411)
private val SurfaceVariantDark = Color(0xFF24201A)
private val OutlineDark = Color(0xFF4B4232)

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = Color(0xFF1D1608),
    primaryContainer = Color(0xFF4F3C10),
    onPrimaryContainer = GoldSoft,
    secondary = Color(0xFFD7C08A),
    onSecondary = Color(0xFF241A08),
    secondaryContainer = Color(0xFF3A2F19),
    onSecondaryContainer = Color(0xFFF1E5C9),
    tertiary = Color(0xFFB9A26C),
    onTertiary = Color(0xFF221A0B),
    background = SurfaceDark,
    onBackground = Color(0xFFF4EEDF),
    surface = SurfaceDark,
    onSurface = Color(0xFFF4EEDF),
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = Color(0xFFCABFAE),
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = GoldDeep,
    onPrimary = Color.White,
    primaryContainer = GoldSoft,
    onPrimaryContainer = Color(0xFF3D2C06),
    secondary = Color(0xFF8F7450),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E8D6),
    onSecondaryContainer = Color(0xFF3E3120),
    tertiary = Color(0xFF7C6948),
    onTertiary = Color.White,
    background = SurfaceLight,
    onBackground = Color(0xFF2B261D),
    surface = Color.White,
    onSurface = Color(0xFF2B261D),
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = Color(0xFF635645),
    outline = OutlineLight
)

@Composable
fun KinKakakuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
