package com.bigPicture.businessreportgenerator.data.domain

enum class Currency(
    val code: String,
    val symbol: String
) {
    KRW("KRW", "₩"),
    USD("USD", "$");

    fun formatAmount(amount: Double): String {
        return when (this) {
            KRW -> "${symbol}${java.text.NumberFormat.getNumberInstance(java.util.Locale.KOREA).format(amount.toLong())}"
            USD -> "${symbol}${String.format("%.2f", amount)}"
        }
    }
}