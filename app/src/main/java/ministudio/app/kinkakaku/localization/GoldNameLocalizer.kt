package ministudio.app.kinkakaku.localization

import android.content.Context
import ministudio.app.kinkakaku.R

object GoldNameLocalizer {

    private val keyToStringRes = mapOf(
        "BT9999NTT"   to R.string.gold_name_bt9999ntt,
        "BTSJC"       to R.string.gold_name_btsjc,
        "DOHCML"      to R.string.gold_name_dohcml,
        "DOHNL"       to R.string.gold_name_dohnl,
        "DOJINHTV"    to R.string.gold_name_dojinhtv,
        "PQHN24NTT"   to R.string.gold_name_pqhn24ntt,
        "PQHNVM"      to R.string.gold_name_pqhnvm,
        "SJ9999"      to R.string.gold_name_sj9999,
        "SJL1L10"     to R.string.gold_name_sjl1l10,
        "VIETTINMSJC" to R.string.gold_name_viettinmsjc,
        "VNGSJC"      to R.string.gold_name_vngsjc,
        "XAUUSD"      to R.string.gold_name_xauusd
    )

    /**
     * Returns the localized display name for a given gold key.
     * The locale is determined automatically by [context] (respects AppCompatDelegate locale).
     * Falls back to [englishName] if the key is not found.
     *
     * @param context     Android context (locale-aware)
     * @param key         The API key (e.g. "XAUUSD")
     * @param englishName The default English name from the API response
     */
    fun getLocalizedName(context: Context, key: String, englishName: String): String {
        val resId = keyToStringRes[key.uppercase()] ?: return englishName
        return context.getString(resId)
    }
}
