package ministudio.app.kinkakaku.localization

object GoldNameLocalizer {

    private val vietnameseNames = mapOf(
        "XAUUSD" to "Vàng Thế Giới (XAU/USD)",
        "SJL1L10" to "Vàng 9999 SJC",
        "SJ9999" to "Vàng Nhẫn SJC",
        "DOHNL" to "DOJI Hà Nội",
        "DOHCML" to "DOJI HCM",
        "DOJINHTV" to "DOJI Nữ Trang",
        "BTSJC" to "Bảo Tín Minh Châu",
        "BT9999NTT" to "Bảo Tín Minh Châu 9999",
        "PQHNVM" to "PNJ Hà Nội",
        "PQHN24NTT" to "PNJ 24K",
        "VNGSJC" to "Vàng SJC",
        "VIETTINMSJC" to "Viettin SJC"
    )

    private val japaneseNames = mapOf(
        "XAUUSD" to "国際金価格 (XAU/USD)",
        "SJL1L10" to "SJC 9999金",
        "SJ9999" to "SJCリング金",
        "DOHNL" to "DOJIハノイ",
        "DOHCML" to "DOJIホーチミン",
        "DOJINHTV" to "DOJIジュエリー",
        "BTSJC" to "バオティンミンチャウ",
        "BT9999NTT" to "バオティンミンチャウ 9999",
        "PQHNVM" to "PNJハノイ",
        "PQHN24NTT" to "PNJ 24K",
        "VNGSJC" to "SJC金",
        "VIETTINMSJC" to "ビエティン SJC"
    )

    /**
     * Returns the localized display name for a given gold key.
     * Falls back to [englishName] if no translation is found.
     *
     * @param key        The API key (e.g. "XAUUSD")
     * @param languageTag The active language tag ("vi", "ja", or "en")
     * @param englishName The default English name from the API response
     */
    fun getLocalizedName(key: String, languageTag: String, englishName: String): String {
        return when (languageTag) {
            "vi" -> vietnameseNames[key] ?: englishName
            "ja" -> japaneseNames[key] ?: englishName
            else -> englishName
        }
    }
}

