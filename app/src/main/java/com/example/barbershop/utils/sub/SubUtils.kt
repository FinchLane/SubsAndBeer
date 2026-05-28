package com.example.barbershop.utils.sub

import com.example.barbershop.data.model.subscription.Subscription
import com.example.barbershop.viewmodel.subscription.SubViewModel
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

object SubUtils {
    fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault())
        return date.format(formatter)
    }

    fun formatNextPaymentDate(nextPaymentDate: LocalDate, subViewModel: SubViewModel? = null, subscription: Subscription? = null ): String {
        val today = LocalDate.now()
        val period = Period.between(today, nextPaymentDate)

        return when {
            period.years > 0 -> "Через ${period.years} ${getYearWord(period.years)}"
            period.months > 0 -> "Через ${period.months} ${getMonthWord(period.months)}"
            period.days > 0 -> "Через ${period.days} ${getDayWord(period.days)}"
            period.days == 0 -> "Сегодня"
            period.isNegative -> {
                if (subscription == null || subViewModel == null) {
                    return "Ошибка: некорректные данные подписки"
                }
                val nextDate = calculateNextDate(
                    subscription.interval.toString(),
                    subscription.period,
                    subscription.nextPaymentDate
                )
                subViewModel.updateSubscription(subscription.copy(nextPaymentDate = nextDate))
                formatNextPaymentDate(nextDate, subViewModel, subscription)
            }
            else -> "Неизвестная дата"
        }
    }

    fun daysLeft(nextPaymentDate: LocalDate): String {
        val today = LocalDate.now()
        val days = ChronoUnit.DAYS.between(today, nextPaymentDate).toInt()
        return when {
            days < 0 -> "Просрочено"
            days % 10 == 1 && days % 100 != 11 -> "$days день"
            days % 10 in 2..4 && days % 100 !in 12..14 -> "$days дня"
            else -> "$days дней"
        }
    }

    fun aveExpenses(amount: Double, interval: Int, period: String):List<Double>{

        var week = 0.0
        var month = 0.0
        var year = 0.0

        if (interval == 1){
            when(period.lowercase()){
                "день" -> {
                    week = amount * 7
                    month = amount * 30
                    year = amount * 365
                }
                "неделя" -> {
                    week = amount
                    month = amount * 4
                    year = amount * 52
                }
                "месяц" -> {
                    week = amount / 4
                    month = amount
                    year = amount * 12
                }
                "год" -> {
                    week = amount / 52
                    month = amount / 12
                    year = amount
                }
            }
        }
        else{
            when(period.lowercase()){
                "день" -> {
                    week = amount * 7 / interval
                    month = amount * 30 / interval
                    year = amount * 365 / interval
                }
                "неделя" -> {
                    week = amount / interval
                    month = amount * 4 / interval
                    year = amount * 52 / interval
                }
                "месяц" -> {
                    week = amount / 4 * interval
                    month = amount / interval
                    year = amount * 12 * interval
                }
                "год" -> {
                    week = amount/52 * interval
                    month = amount/12 * interval
                    year = amount / interval
                }
            }
        }

        return listOf(week, month, year)
    }

    fun calculateNextDate(
        interval: String,
        selectedPeriod: String,
        startDate: LocalDate
    ): LocalDate {
        val countPeriod = if (interval.isEmpty()) 1 else interval.toLong()

        return when (selectedPeriod) {
            "День" -> startDate.plus(countPeriod, ChronoUnit.DAYS)
            "Неделя" -> startDate.plus(countPeriod, ChronoUnit.WEEKS)
            "Месяц" -> startDate.plus(countPeriod, ChronoUnit.MONTHS)
            "Год" -> startDate.plus(countPeriod, ChronoUnit.YEARS)
            else -> startDate
        }
    }

    fun shouldDisplaySubscriptionOnDate(subscription: Subscription, date: LocalDate): Boolean {

        val today = LocalDate.now()
        if (date.isBefore(today)) return false
        if (date.isBefore(subscription.startDate)) return false

        return when (subscription.period.lowercase(Locale.getDefault())) {
            "день" -> {
                val diffDays = ChronoUnit.DAYS.between(subscription.startDate, date)
                diffDays % subscription.interval == 0L
            }
            "неделя" -> {
                val diffWeeks = ChronoUnit.WEEKS.between(subscription.startDate, date)
                diffWeeks % subscription.interval == 0L && subscription.startDate.dayOfWeek == date.dayOfWeek
            }
            "месяц" -> {

                val normalizedStart = subscription.startDate.withDayOfMonth(1)
                val normalizedDate = date.withDayOfMonth(1)
                val diffMonths = ChronoUnit.MONTHS.between(normalizedStart, normalizedDate)
                diffMonths % subscription.interval == 0L && subscription.startDate.dayOfMonth == date.dayOfMonth
            }
            "год" -> {
                val diffYears = ChronoUnit.YEARS.between(subscription.startDate, date)
                diffYears % subscription.interval == 0L && subscription.startDate.dayOfYear == date.dayOfYear
            }
            else -> false
        }
    }

    fun getYearWord(years: Int): String {
        return when {
            years % 10 == 1 && years % 100 != 11 -> "год"
            years % 10 in 2..4 && years % 100 !in 12..14 -> "года"
            else -> "лет"
        }
    }

    fun getMonthWord(months: Int): String {
        return when {
            months % 10 == 1 && months % 100 != 11 -> "месяц"
            months % 10 in 2..4 && months % 100 !in 12..14 -> "месяца"
            else -> "месяцев"
        }
    }

    fun getDayWord(days: Int): String {
        return when {
            days % 10 == 1 && days % 100 != 11 -> "день"
            days % 10 in 2..4 && days % 100 !in 12..14 -> "дня"
            else -> "дней"
        }
    }

    fun getWeekWord(days: Int): String {
        return when {
            days % 10 == 1 && days % 100 != 11 -> "неделя"
            days % 10 in 2..4 && days % 100 !in 12..14 -> "недели"
            else -> "недель"
        }
    }

    fun getSubWord(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> "$count подписка"
            count % 10 in 2..4 && count % 100 !in 12..14 -> "$count подписки"
            else -> "$count подписок"
        }
    }
}