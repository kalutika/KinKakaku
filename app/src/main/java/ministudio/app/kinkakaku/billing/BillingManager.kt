package ministudio.app.kinkakaku.billing

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.lang.reflect.Proxy

private const val TAG = "BillingManager"
private const val PREFS_NAME = "billing_prefs"
private const val KEY_REMOVE_ADS = "remove_ads_premium"
private const val BILLING_CLIENT_CLASS = "com.android.billingclient.api.BillingClient"
private const val BILLING_FLOW_PARAMS_CLASS = "com.android.billingclient.api.BillingFlowParams"
private const val QUERY_PRODUCT_DETAILS_PARAMS_CLASS = "com.android.billingclient.api.QueryProductDetailsParams"
private const val QUERY_PURCHASES_PARAMS_CLASS = "com.android.billingclient.api.QueryPurchasesParams"
private const val ACKNOWLEDGE_PURCHASE_PARAMS_CLASS = "com.android.billingclient.api.AcknowledgePurchaseParams"
private const val PRODUCT_TYPE_INAPP = "inapp"
private const val RESPONSE_OK = 0
private const val RESPONSE_USER_CANCELED = 1
private const val PURCHASE_STATE_PURCHASED = 1

data class BillingUiState(
    val isPremium: Boolean = false,
    val isReady: Boolean = false,
    val isLoading: Boolean = true,
    val isPurchasing: Boolean = false,
    val productAvailable: Boolean = false,
    val error: String? = null
)

object BillingManager {
    const val REMOVE_ADS_PRODUCT_ID = "remove_ads"

    private val _uiState = MutableStateFlow(BillingUiState())
    val uiState: StateFlow<BillingUiState> = _uiState.asStateFlow()

    private lateinit var appContext: Context
    private lateinit var prefs: SharedPreferences
    private var billingClient: Any? = null
    private var removeAdsProductDetails: Any? = null
    private var initialized = false

    fun initialize(context: Context) {
        if (initialized) return
        initialized = true

        appContext = context.applicationContext
        prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val cachedPremium = prefs.getBoolean(KEY_REMOVE_ADS, false)
        _uiState.value = BillingUiState(
            isPremium = cachedPremium,
            isReady = false,
            isLoading = true,
            isPurchasing = false,
            productAvailable = false,
            error = null
        )

        createBillingClient()
        connectBillingClient()
    }

    fun buyRemoveAds(context: Context) {
        if (_uiState.value.isPremium) return

        val client = billingClient ?: run {
            connectBillingClient()
            _uiState.update { it.copy(error = "Google Play Billing is not ready yet.") }
            return
        }

        if (!isBillingClientReady(client)) {
            connectBillingClient()
            _uiState.update { it.copy(error = "Google Play Billing is not ready yet.") }
            return
        }

        val details = removeAdsProductDetails ?: run {
            refreshProductDetails()
            _uiState.update { it.copy(error = "Remove Ads is not available right now.") }
            return
        }

        val activity = context.findActivity() ?: run {
            _uiState.update { it.copy(error = "Activity not found for purchase flow.") }
            return
        }

        _uiState.update { it.copy(isPurchasing = true, error = null) }

        val flowParams = buildBillingFlowParams(details)
        val result = invokeMethod(client, "launchBillingFlow", activity, flowParams)
        if (responseCode(result) != RESPONSE_OK) {
            _uiState.update {
                it.copy(
                    isPurchasing = false,
                    error = debugMessage(result)
                )
            }
        }
    }

    fun restorePurchases() {
        val client = billingClient ?: run {
            connectBillingClient()
            return
        }

        if (!isBillingClientReady(client)) {
            connectBillingClient()
            return
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                error = null
            )
        }
        queryPurchases()
    }

    private fun createBillingClient() {
        val billingClientClass = Class.forName(BILLING_CLIENT_CLASS)
        val builder = billingClientClass.getMethod("newBuilder", Context::class.java).invoke(null, appContext)

        val setListenerMethod = builder.javaClass.methods.firstOrNull {
            it.name == "setListener" && it.parameterCount == 1
        } ?: error("BillingClient builder does not expose setListener")

        val purchasesUpdatedListener = createProxy(setListenerMethod.parameterTypes[0]) { _, args ->
            handlePurchasesUpdated(args)
        }
        setListenerMethod.invoke(builder, purchasesUpdatedListener)

        if (builder != null) {
            invokeEnablePendingPurchases(builder)
        }
        billingClient = invokeMethod(builder, "build")
    }

    private fun connectBillingClient() {
        val client = billingClient ?: run {
            createBillingClient()
            billingClient
        } ?: return

        _uiState.update {
            it.copy(
                isLoading = true,
                error = null
            )
        }

        val startConnectionMethod = client.javaClass.methods.firstOrNull {
            it.name == "startConnection" && it.parameterCount == 1
        } ?: error("BillingClient does not expose startConnection")

        val stateListener = createProxy(startConnectionMethod.parameterTypes[0]) { _, args ->
            handleBillingSetupFinished(args)
        }
        startConnectionMethod.invoke(client, stateListener)
    }

    private fun handleBillingSetupFinished(args: Array<out Any?>?) {
        val billingResult = args?.firstOrNull()
        if (responseCode(billingResult) == RESPONSE_OK) {
            _uiState.update {
                it.copy(
                    isReady = true,
                    isLoading = true,
                    error = null
                )
            }
            refreshProductDetails()
            queryPurchases()
        } else {
            _uiState.update {
                it.copy(
                    isReady = false,
                    isLoading = false,
                    error = debugMessage(billingResult)
                )
            }
        }
    }

    private fun handlePurchasesUpdated(args: Array<out Any?>?) {
        val billingResult = args?.firstOrNull()
        val purchases = args?.getOrNull(1) as? List<*> ?: emptyList<Any?>()

        when (responseCode(billingResult)) {
            RESPONSE_OK -> {
                _uiState.update { it.copy(isPurchasing = false, error = null) }
                handlePurchases(purchases.filterNotNull())
            }

            RESPONSE_USER_CANCELED -> {
                _uiState.update { it.copy(isPurchasing = false) }
            }

            else -> {
                _uiState.update {
                    it.copy(
                        isPurchasing = false,
                        error = debugMessage(billingResult)
                    )
                }
            }
        }
    }

    private fun refreshProductDetails() {
        val client = billingClient ?: return
        val params = buildQueryProductDetailsParams()
        val queryMethod = client.javaClass.methods.firstOrNull {
            it.name == "queryProductDetailsAsync" && it.parameterCount == 2
        } ?: error("BillingClient does not expose queryProductDetailsAsync")

        val listener = createProxy(queryMethod.parameterTypes[1]) { _, args ->
            val billingResult = args?.firstOrNull()
            val productDetailsList = args?.getOrNull(1) as? List<*> ?: emptyList<Any?>()

            if (responseCode(billingResult) == RESPONSE_OK) {
                removeAdsProductDetails = productDetailsList.firstOrNull()
                _uiState.update {
                    it.copy(
                        productAvailable = removeAdsProductDetails != null,
                        isLoading = false,
                        error = if (removeAdsProductDetails == null) {
                            "Remove Ads product not found in Play Console."
                        } else {
                            null
                        }
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        productAvailable = false,
                        isLoading = false,
                        error = debugMessage(billingResult)
                    )
                }
            }
        }

        queryMethod.invoke(client, params, listener)
    }

    private fun queryPurchases() {
        val client = billingClient ?: return
        val params = buildQueryPurchasesParams()
        val queryMethod = client.javaClass.methods.firstOrNull {
            it.name == "queryPurchasesAsync" && it.parameterCount == 2
        } ?: error("BillingClient does not expose queryPurchasesAsync")

        val listener = createProxy(queryMethod.parameterTypes[1]) { _, args ->
            val billingResult = args?.firstOrNull()
            val purchases = args?.getOrNull(1) as? List<*> ?: emptyList<Any?>()

            if (responseCode(billingResult) == RESPONSE_OK) {
                handlePurchases(purchases.filterNotNull())
                _uiState.update { it.copy(isLoading = false, error = null) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = debugMessage(billingResult)
                    )
                }
            }
        }

        queryMethod.invoke(client, params, listener)
    }

    private fun handlePurchases(purchases: List<Any>) {
        val removeAdsPurchase = purchases.firstOrNull { purchase: Any ->
            purchaseProducts(purchase).contains(REMOVE_ADS_PRODUCT_ID) &&
                purchaseState(purchase) == PURCHASE_STATE_PURCHASED
        }

        if (removeAdsPurchase != null) {
            setPremium(true)
            acknowledgePurchaseIfNeeded(removeAdsPurchase)
        } else {
            setPremium(false)
        }
    }

    private fun acknowledgePurchaseIfNeeded(purchase: Any) {
        if (isAcknowledged(purchase)) return

        val client = billingClient ?: return
        val acknowledgeParams = buildAcknowledgePurchaseParams(purchaseToken(purchase))
        val acknowledgeMethod = client.javaClass.methods.firstOrNull {
            it.name == "acknowledgePurchase" && it.parameterCount == 2
        } ?: error("BillingClient does not expose acknowledgePurchase")

        val listener = createProxy(acknowledgeMethod.parameterTypes[1]) { _, args ->
            val billingResult = args?.firstOrNull()
            if (responseCode(billingResult) != RESPONSE_OK) {
                Log.w(TAG, "Acknowledge failed: ${debugMessage(billingResult)}")
            }
        }

        acknowledgeMethod.invoke(client, acknowledgeParams, listener)
    }

    private fun setPremium(isPremium: Boolean) {
        prefs.edit { putBoolean(KEY_REMOVE_ADS, isPremium) }
        _uiState.update { current ->
            current.copy(isPremium = isPremium)
        }
    }

    private fun buildQueryProductDetailsParams(): Any {
        val paramsClass = Class.forName(QUERY_PRODUCT_DETAILS_PARAMS_CLASS)
        val productClass = Class.forName("$QUERY_PRODUCT_DETAILS_PARAMS_CLASS\$Product")
        val productBuilder = productClass.getMethod("newBuilder").invoke(null)
        invokeMethod(productBuilder, "setProductId", REMOVE_ADS_PRODUCT_ID)
        invokeMethod(productBuilder, "setProductType", PRODUCT_TYPE_INAPP)
        val product = invokeMethod(productBuilder, "build")

        val builder = paramsClass.getMethod("newBuilder").invoke(null)
        invokeMethod(builder, "setProductList", listOf(product))
        return invokeMethod(builder, "build") ?: error("Unable to build QueryProductDetailsParams")
    }

    private fun buildQueryPurchasesParams(): Any {
        val paramsClass = Class.forName(QUERY_PURCHASES_PARAMS_CLASS)
        val builder = paramsClass.getMethod("newBuilder").invoke(null)
        invokeMethod(builder, "setProductType", PRODUCT_TYPE_INAPP)
        return invokeMethod(builder, "build") ?: error("Unable to build QueryPurchasesParams")
    }

    private fun buildBillingFlowParams(productDetails: Any): Any {
        val paramsClass = Class.forName(BILLING_FLOW_PARAMS_CLASS)
        val detailsParamsClass = Class.forName("$BILLING_FLOW_PARAMS_CLASS\$ProductDetailsParams")
        val detailsBuilder = detailsParamsClass.getMethod("newBuilder").invoke(null)
        invokeMethod(detailsBuilder, "setProductDetails", productDetails)
        val detailsParams = invokeMethod(detailsBuilder, "build")

        val builder = paramsClass.getMethod("newBuilder").invoke(null)
        invokeMethod(builder, "setProductDetailsParamsList", listOf(detailsParams))
        return invokeMethod(builder, "build") ?: error("Unable to build BillingFlowParams")
    }

    private fun buildAcknowledgePurchaseParams(purchaseToken: String): Any {
        val paramsClass = Class.forName(ACKNOWLEDGE_PURCHASE_PARAMS_CLASS)
        val builder = paramsClass.getMethod("newBuilder").invoke(null)
        invokeMethod(builder, "setPurchaseToken", purchaseToken)
        return invokeMethod(builder, "build") ?: error("Unable to build AcknowledgePurchaseParams")
    }

    private fun invokeEnablePendingPurchases(builder: Any) {
        builder.javaClass.methods.firstOrNull { it.name == "enablePendingPurchases" && it.parameterCount == 0 }
            ?.invoke(builder)
            ?: runCatching {
                val paramsClass = Class.forName("com.android.billingclient.api.PendingPurchasesParams")
                val paramsBuilder = paramsClass.getMethod("newBuilder").invoke(null)
                paramsBuilder.javaClass.methods.firstOrNull { it.name == "enableOneTimeProducts" && it.parameterCount == 0 }
                    ?.invoke(paramsBuilder)
                val params = invokeMethod(paramsBuilder, "build")
                builder.javaClass.methods.firstOrNull { it.name == "enablePendingPurchases" && it.parameterCount == 1 }
                    ?.invoke(builder, params)
            }
    }

    private fun isBillingClientReady(client: Any): Boolean {
        return invokeBoolean(client, "isReady")
    }

    private fun responseCode(result: Any?): Int {
        return invokeInt(result, "getResponseCode") ?: -1
    }

    private fun debugMessage(result: Any?): String {
        return invokeString(result, "getDebugMessage") ?: "Google Play Billing error"
    }

    private fun purchaseProducts(purchase: Any): List<String> {
        @Suppress("UNCHECKED_CAST")
        return (invokeMethod(purchase, "getProducts") as? List<String>).orEmpty()
    }

    private fun purchaseState(purchase: Any): Int {
        return invokeInt(purchase, "getPurchaseState") ?: -1
    }

    private fun isAcknowledged(purchase: Any): Boolean {
        return invokeBoolean(purchase, "isAcknowledged", "getIsAcknowledged")
    }

    private fun purchaseToken(purchase: Any): String {
        return invokeString(purchase, "getPurchaseToken") ?: ""
    }

    private fun createProxy(interfaceClass: Class<*>, handler: (methodName: String, args: Array<out Any?>?) -> Unit): Any {
        return Proxy.newProxyInstance(interfaceClass.classLoader, arrayOf(interfaceClass)) { _, method, args ->
            handler(method.name, args)
            null
        }
    }

    private fun invokeMethod(target: Any?, name: String, vararg args: Any?): Any? {
        val method = target?.javaClass?.methods?.firstOrNull {
            it.name == name && it.parameterCount == args.size
        } ?: return null
        return method.invoke(target, *args)
    }

    private fun invokeBoolean(target: Any?, vararg names: String): Boolean {
        val method = target?.javaClass?.methods?.firstOrNull { candidate ->
            candidate.name in names && candidate.parameterCount == 0
        } ?: return false
        return (method.invoke(target) as? Boolean) ?: false
    }

    private fun invokeInt(target: Any?, vararg names: String): Int? {
        val method = target?.javaClass?.methods?.firstOrNull { candidate ->
            candidate.name in names && candidate.parameterCount == 0
        } ?: return null
        return (method.invoke(target) as? Number)?.toInt()
    }

    private fun invokeString(target: Any?, vararg names: String): String? {
        val method = target?.javaClass?.methods?.firstOrNull { candidate ->
            candidate.name in names && candidate.parameterCount == 0
        } ?: return null
        return method.invoke(target)?.toString()
    }

    private fun Context.findActivity(): Activity? {
        return when (this) {
            is Activity -> this
            is ContextWrapper -> baseContext.findActivity()
            else -> null
        }
    }
}

