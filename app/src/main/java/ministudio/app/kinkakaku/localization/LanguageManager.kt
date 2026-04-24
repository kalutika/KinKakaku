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
        return normalizeLanguageTag(
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE_TAG, LANGUAGE_ENGLISH)
            ?: LANGUAGE_ENGLISH
        )
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

