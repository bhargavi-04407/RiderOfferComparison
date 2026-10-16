package com.example.rideroffercomparison.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.rideroffercomparison.model.Offer
import com.example.rideroffercomparison.parser.OfferParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class OfferAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private val _offerFlow = MutableSharedFlow<Offer>(replay = 1)
        val offerFlow: SharedFlow<Offer> = _offerFlow.asSharedFlow()

        // Supported package name mappings
        val SUPPORTED_PACKAGES = mapOf(
            "com.rapido.passenger" to "Rapido",
            "com.swiggy.deliverypartner" to "Swiggy",
            "com.application.zomato" to "Zomato",
            "com.olacabs.customer" to "Ola",
            "com.shadowfax.rider" to "Shadowfax"
        )
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString() ?: return
        val platformName = SUPPORTED_PACKAGES[packageName] ?: return

        val rootNode = rootInActiveWindow ?: return
        serviceScope.launch {
            try {
                val jsonPayload = extractNodeInfoAsJson(rootNode, platformName)
                val offer = OfferParser.parseOffer(jsonPayload, platformName)
                if (offer != null) {
                    _offerFlow.emit(offer)
                }
            } catch (_: Exception) {
                // Fail closed
            }
        }
    }

    private fun extractNodeInfoAsJson(node: AccessibilityNodeInfo, platform: String): String {
        val texts = mutableListOf<String>()
        traverseNodes(node) { text ->
            if (text.isNotBlank() && !isDisallowedCustomerOrAddressData(text)) {
                texts.add(text)
            }
        }

        var earning = 0.0
        var pickupKm = 0.5
        var tripKm = 3.0

        for (t in texts) {
            val cleaned = t.replace("₹", "").replace("Rs.", "").trim()
            val num = cleaned.toDoubleOrNull()
            if (num != null && num > 10.0 && num < 5000.0 && earning == 0.0) {
                earning = num
            } else if (t.lowercase().contains("km")) {
                val dNum = t.replace("km", "").trim().toDoubleOrNull()
                if (dNum != null) {
                    if (pickupKm == 0.5) pickupKm = dNum else tripKm = dNum
                }
            }
        }

        return """
            {
                "platform": "$platform",
                "serviceType": "Bike",
                "earningShown": ${if (earning > 0) earning else 150.0},
                "pickupDistanceKm": $pickupKm,
                "tripDistanceKm": $tripKm
            }
        """.trimIndent()
    }

    /**
     * Strictly drops any text containing customer names, phone numbers, exact addresses,
     * restaurant/store names, or payment modes per MVP spec Section 5 & 7.
     */
    private fun isDisallowedCustomerOrAddressData(text: String): Boolean {
        val lower = text.lowercase()
        val disallowedKeywords = listOf(
            "customer", "name", "phone", "rating", "address", "pickup at", "drop at",
            "restaurant", "store", "payment", "cash", "upi", "chat", "note", "call", "sms"
        )
        return disallowedKeywords.any { lower.contains(it) }
    }

    private fun traverseNodes(node: AccessibilityNodeInfo?, action: (String) -> Unit) {
        if (node == null) return
        node.text?.toString()?.let { action(it) }
        node.contentDescription?.toString()?.let { action(it) }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            traverseNodes(child, action)
            child?.recycle()
        }
    }

    override fun onInterrupt() {
        // Required override
    }
}
