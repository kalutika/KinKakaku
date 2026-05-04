package ministudio.app.kinkakaku.localization

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat

object LanguageManager {
    private const val PREFS_NAME = "language_prefs"
    private const val KEY_LANGUAGE_TAG = "language_tag"
    const val LANGUAGE_ENGLISH = "en"
    const val LANGUAGE_VIETNAMESE = "vi"
    const val LANGUAGE_JAPANESE = "ja"
    private val supportedLanguageTags = setOf(
        LANGUAGE_ENGLISH,
        LANGUAGE_VIETNAMESE,
        LANGUAGE_JAPANESE
    )

    fun applySavedLanguage(context: Context) {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(getSavedLanguageTag(context))
        )
    }

    fun setLanguage(context: Context, languageTag: String) {
        val normalizedTag = normalizeLanguageTag(languageTag)
        saveLanguageTag(context, normalizedTag)
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(normalizedTag))
    }

    fun getSavedLanguageTag(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getString(KEY_LANGUAGE_TAG, null)
        if (saved != null) {
            return normalizeLanguageTag(saved)
        }
        // First launch: use device language if supported, otherwise fall back to English
        val deviceLanguage = java.util.Locale.getDefault().language
        val defaultTag = if (deviceLanguage in supportedLanguageTags) deviceLanguage else LANGUAGE_ENGLISH
        saveLanguageTag(context, defaultTag)
        return defaultTag
    }

    private fun saveLanguageTag(context: Context, languageTag: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(KEY_LANGUAGE_TAG, normalizeLanguageTag(languageTag))
        }
    }

    private fun normalizeLanguageTag(languageTag: String): String {
        return when (languageTag) {
            in supportedLanguageTags -> languageTag
            else -> LANGUAGE_ENGLISH
        }
    }
}

