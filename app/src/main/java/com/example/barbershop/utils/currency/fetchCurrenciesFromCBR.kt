package com.example.barbershop.utils.currency

import android.util.Log
import com.example.barbershop.data.model.subscription.Currency
import com.example.barbershop.data.model.subscription.ExchangeRate
import com.example.barbershop.viewmodel.subscription.SubViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import timber.log.Timber
import java.net.URL
import java.time.LocalDateTime

// завернуть в object и переименовать файл

suspend fun fetchCurrenciesFromCBR(): List<Pair<Currency, ExchangeRate>> = withContext(Dispatchers.IO) {
    val url = "http://www.cbr.ru/scripts/XML_daily.asp"
    val currencies = mutableListOf<Pair<Currency, ExchangeRate>>()
    try {
        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        val inputStream = URL(url).openStream()
        parser.setInput(inputStream, null)

        var eventType = parser.eventType
        var currentCurrency: Currency? = null
        var numCode: String? = null
        var charCode: String? = null
        var nominal: Int? = null
        var name: String? = null
        var value: Double? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "Valute" -> currentCurrency = null
                        "NumCode" -> numCode = parser.nextText()
                        "CharCode" -> charCode = parser.nextText()
                        "Nominal" -> nominal = parser.nextText().toInt()
                        "Name" -> name = parser.nextText()
                        "Value" -> value = parser.nextText().replace(",", ".").toDouble()
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == "Valute" && charCode != null && name != null && nominal != null && value != null) {
                        val currency = Currency(
                            id = charCode,
                            name = name,
                            symbol = getSymbolForCurrency(charCode),
                            nominal = nominal
                        )
                        val exchangeRate = ExchangeRate(
                            fromCurrencyId = "RUB",
                            toCurrencyId = charCode,
                            rate = value / nominal,
                            source = "cbr",
                            date = LocalDateTime.now(),
                            isCustom = false
                        )
                        currencies.add(currency to exchangeRate)
                        numCode = null
                        charCode = null
                        nominal = null
                        name = null
                        value = null
                    }
                }
            }
            eventType = parser.next()
        }
        inputStream.close()
        Timber.tag("CurrencyFetcher").d("Fetched ${currencies.size} currencies from CBR")
    } catch (e: Exception) {
        Timber.tag("CurrencyFetcher").e(e, "Error fetching CBR data: ${e.message}")
    }
    currencies.add(
        Currency("RUB", "Российский рубль", "₽", 1) to
                ExchangeRate(fromCurrencyId = "RUB", toCurrencyId = "RUB", rate = 1.0, source = "cbr", date = LocalDateTime.now(), isCustom = false)
    )
    return@withContext currencies
}

fun convertSubscriptionAmount(
    subscriptionAmount: Double,
    subscriptionCurrency: String,
    defaultCurrency: String,
    subViewModel: SubViewModel
): Double {
    if (subscriptionCurrency == defaultCurrency) {
        return subscriptionAmount
    }

    subViewModel.getExchangeRate(subscriptionCurrency, defaultCurrency)?.let { directRate ->
        return subscriptionAmount * directRate.rate
    }

    subViewModel.getExchangeRate(defaultCurrency, subscriptionCurrency)?.let { reverseRate ->
        return subscriptionAmount * reverseRate.rate
    }

    return 0.0
}

fun getSymbolForCurrency(charCode: String): String {
    return when (charCode) {
        "AUD" -> "A$"
        "AZN" -> "₼"
        "GBP" -> "£"
        "AMD" -> "֏"
        "BYN" -> "Br"
        "BGN" -> "лв"
        "BRL" -> "R$"
        "HUF" -> "Ft"
        "VND" -> "₫"
        "HKD" -> "HK$"
        "GEL" -> "₾"
        "DKK" -> "kr"
        "AED" -> "AED"
        "USD" -> "$"
        "EUR" -> "€"
        "EGP" -> "E£"
        "INR" -> "₹"
        "IDR" -> "Rp"
        "KZT" -> "₸"
        "CAD" -> "C$"
        "QAR" -> "QR"
        "KGS" -> "с"
        "CNY" -> "¥"
        "MDL" -> "L"
        "NZD" -> "NZ$"
        "NOK" -> "kr"
        "PLN" -> "zł"
        "RON" -> "lei"
        "XDR" -> "XDR"
        "SGD" -> "S$"
        "TJS" -> "SM"
        "THB" -> "฿"
        "TRY" -> "₺"
        "TMT" -> "TMT"
        "UZS" -> "so'm"
        "UAH" -> "₴"
        "CZK" -> "Kč"
        "SEK" -> "kr"
        "CHF" -> "CHF"
        "RSD" -> "дин"
        "ZAR" -> "R"
        "KRW" -> "₩"
        "JPY" -> "¥"
        "RUB" -> "₽"
        else -> charCode
    }
}