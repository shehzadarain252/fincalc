package com.fincalc.app.data

import com.fincalc.app.util.Format
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

/**
 * Category 6 — Currency & Daily Tools (10).
 *
 * The Currency Converter is special: its rates come from a live, free,
 * no-key API (open.er-api.com, with Frankfurter as fallback) and it has a
 * dedicated screen (CurrencyConverterScreen) that fetches, caches and
 * labels them. Its [compute] below works off the shared [RatesHolder]
 * cache so the generic screen can also show a result; when no rates are
 * cached it returns an honest "unavailable" result — never an invented rate.
 */
object RatesHolder {
    var rates: Map<String, Double> = emptyMap()
    var updatedAt: String = ""
    var source: String = ""
}

object CalculatorsDaily {

    /** Convert using USD-base rates: amount / rate[from] * rate[to]. */
    fun convert(amount: Double, from: String, to: String): Double? {
        val rates = RatesHolder.rates
        if (rates.isEmpty()) return null
        if (from == to) return amount
        val fromRate = if (from == "USD") 1.0 else rates[from] ?: return null
        val toRate = if (to == "USD") 1.0 else rates[to] ?: return null
        return amount / fromRate * toRate
    }

    val all: List<CalculatorDef> = listOf(
        CalculatorDef(
            id = "currency-converter", title = "Currency Converter",
            description = "Live exchange rates for 30 major currencies, updated daily.",
            category = Category.DAILY, icon = "currency", popular = true, quick = true,
            inputs = listOf(
                CalcInput("amount", "Amount", defaultValue = "100"),
                CalcInput("from", "From", type = InputType.SELECT,
                    options = RatesRepository.SUPPORTED, defaultValue = "USD"),
                CalcInput("to", "To", type = InputType.SELECT,
                    options = RatesRepository.SUPPORTED, defaultValue = "EUR")
            ),
            compute = { v, s, d ->
                val amount = num(v, "amount")
                val from = str(v, "from", "USD")
                val to = str(v, "to", "EUR")
                val result = convert(amount, from, to)
                if (result == null) {
                    CalcResult(
                        title = "Rates unavailable",
                        subtitle = "Connect to the internet to load live rates",
                        rows = listOf(
                            ResultRow("Status", "No exchange rates loaded yet"),
                            ResultRow("Amount", Format.num(amount, d) + " " + from),
                            ResultRow("From / To", "$from → $to")
                        ),
                        formula = "Converted = Amount ÷ Rate(From) × Rate(To), using USD-base rates from open.er-api.com.",
                        disclaimer = "Live rates are unavailable right now. No estimated rate is shown."
                    )
                } else {
                    val rate = if (amount != 0.0) result / amount else 0.0
                    val fromName = RatesRepository.NAMES[from] ?: from
                    val toName = RatesRepository.NAMES[to] ?: to
                    CalcResult(
                        title = "${Format.num(result, d)} $to",
                        subtitle = "${Format.num(amount, d)} $from = ${Format.num(result, d)} $to",
                        rows = listOf(
                            ResultRow("Converted Amount", "${Format.num(result, d)} $to", highlight = true),
                            ResultRow("Exchange Rate", "1 $from = ${Format.num(rate, 4)} $to"),
                            ResultRow("Inverse Rate", "1 $to = ${Format.num(if (rate != 0.0) 1 / rate else 0.0, 4)} $from"),
                            ResultRow("From", "$from — $fromName"),
                            ResultRow("To", "$to — $toName")
                        ),
                        formula = "Converted = Amount ÷ Rate($from) × Rate($to). Rates are USD-based mid-market rates, updated daily.",
                        breakdown = listOf(
                            "Amount: ${Format.num(amount, d)} $from",
                            "Rate: 1 $from = ${Format.num(rate, 4)} $to",
                            "Result: ${Format.num(result, d)} $to",
                            if (RatesHolder.updatedAt.isNotBlank()) "Rates updated: ${RatesHolder.updatedAt}" else "",
                            if (RatesHolder.source.isNotBlank()) "Source: ${RatesHolder.source}" else ""
                        ).filter { it.isNotBlank() },
                        disclaimer = "Mid-market rates for reference only — banks and exchanges add their own spread and fees."
                    )
                }
            }
        ),
        CalculatorDef(
            id = "percentage", title = "Percentage Calculator",
            description = "Percent of a number, what percent X is of Y, and percent change.",
            category = Category.DAILY, icon = "percent", quick = true,
            inputs = listOf(
                CalcInput("mode", "What to Calculate", type = InputType.SELECT,
                    options = listOf("What is X% of Y?", "X is what % of Y?", "Percentage increase / decrease"),
                    defaultValue = "What is X% of Y?"),
                CalcInput("x", "X", defaultValue = "20"),
                CalcInput("y", "Y", defaultValue = "150")
            ),
            compute = { v, s, d ->
                val x = num(v, "x")
                val y = num(v, "y")
                when {
                    str(v, "mode").startsWith("What is") -> {
                        val result = y * x / 100.0
                        CalcResult(
                            title = Format.num(result, d),
                            subtitle = "${Format.num(x, d)}% of ${Format.num(y, d)}",
                            rows = listOf(
                                ResultRow("Result", Format.num(result, d), highlight = true),
                                ResultRow("Calculation", "${Format.num(x, d)}% × ${Format.num(y, d)}")
                            ),
                            formula = "Result = Y × X ÷ 100."
                        )
                    }
                    str(v, "mode").startsWith("X is what") -> {
                        val result = if (y != 0.0) x / y * 100 else 0.0
                        CalcResult(
                            title = Format.pct(result, d),
                            subtitle = "${Format.num(x, d)} is ${Format.pct(result, d)} of ${Format.num(y, d)}",
                            rows = listOf(
                                ResultRow("Result", Format.pct(result, d), highlight = true),
                                ResultRow("Calculation", "${Format.num(x, d)} ÷ ${Format.num(y, d)} × 100")
                            ),
                            formula = "Percentage = X ÷ Y × 100."
                        )
                    }
                    else -> {
                        val change = y - x
                        val pct = if (x != 0.0) change / x * 100 else 0.0
                        CalcResult(
                            title = Format.pct(pct, d),
                            subtitle = if (pct >= 0) "Increase" else "Decrease",
                            rows = listOf(
                                ResultRow("Change", Format.pct(pct, d), highlight = true),
                                ResultRow("From (X)", Format.num(x, d)),
                                ResultRow("To (Y)", Format.num(y, d)),
                                ResultRow("Difference", Format.num(change, d))
                            ),
                            formula = "Change % = (Y − X) ÷ X × 100."
                        )
                    }
                }
            }
        ),
        CalculatorDef(
            id = "discount", title = "Discount Calculator",
            description = "Sale price after a discount, and how much you save.",
            category = Category.DAILY, icon = "discount",
            inputs = listOf(
                CalcInput("price", "Original Price ($)", defaultValue = "120"),
                CalcInput("discount", "Discount (%)", defaultValue = "25")
            ),
            compute = { v, s, d ->
                val price = num(v, "price")
                val saved = price * num(v, "discount") / 100.0
                CalcResult(
                    title = Format.money(price - saved, s, d),
                    subtitle = "Price After Discount",
                    rows = listOf(
                        ResultRow("Final Price", Format.money(price - saved, s, d), highlight = true),
                        ResultRow("You Save", Format.money(saved, s, d)),
                        ResultRow("Original Price", Format.money(price, s, d)),
                        ResultRow("Discount", Format.pct(num(v, "discount"), d))
                    ),
                    formula = "Discount = Price × Discount% ÷ 100. Final Price = Price − Discount.",
                    breakdown = listOf(
                        "Original: ${Format.money(price, s, d)}",
                        "Discount ${Format.pct(num(v, "discount"), d)}: −${Format.money(saved, s, d)}",
                        "You pay: ${Format.money(price - saved, s, d)}"
                    )
                )
            }
        ),
        CalculatorDef(
            id = "tip", title = "Tip Calculator",
            description = "Tip, total bill and split per person.",
            category = Category.DAILY, icon = "tip", quick = true,
            inputs = listOf(
                CalcInput("bill", "Bill Amount ($)", defaultValue = "85"),
                CalcInput("tip", "Tip (%)", defaultValue = "18"),
                CalcInput("people", "Split Between (people)", defaultValue = "2")
            ),
            compute = { v, s, d ->
                val bill = num(v, "bill")
                val tipAmount = bill * num(v, "tip") / 100.0
                val total = bill + tipAmount
                val people = num(v, "people", 1.0).coerceAtLeast(1.0)
                CalcResult(
                    title = Format.money(tipAmount, s, d),
                    subtitle = "Tip Amount",
                    rows = listOf(
                        ResultRow("Tip", Format.money(tipAmount, s, d), highlight = true),
                        ResultRow("Total Bill", Format.money(total, s, d)),
                        ResultRow("Per Person", Format.money(total / people, s, d)),
                        ResultRow("Tip per Person", Format.money(tipAmount / people, s, d)),
                        ResultRow("Bill Amount", Format.money(bill, s, d))
                    ),
                    formula = "Tip = Bill × Tip% ÷ 100. Total = Bill + Tip. Per Person = Total ÷ People.",
                    breakdown = listOf(
                        "Bill: ${Format.money(bill, s, d)}",
                        "Tip ${Format.pct(num(v, "tip"), d)}: ${Format.money(tipAmount, s, d)}",
                        "Total: ${Format.money(total, s, d)} — ${Format.money(total / people, s, d)} each (${Format.num(people, 0)} people)"
                    )
                )
            }
        ),
        CalculatorDef(
            id = "age", title = "Age Calculator",
            description = "Your exact age in years, months and days — plus days lived.",
            category = Category.DAILY, icon = "age", quick = true,
            inputs = listOf(
                CalcInput("year", "Birth Year", defaultValue = "1995"),
                CalcInput("month", "Birth Month (1-12)", defaultValue = "6"),
                CalcInput("day", "Birth Day (1-31)", defaultValue = "15")
            ),
            compute = { v, s, d ->
                val today = LocalDate.now()
                val birth = try {
                    LocalDate.of(
                        num(v, "year").toInt(), num(v, "month").toInt().coerceIn(1, 12),
                        num(v, "day").toInt().coerceIn(1, 28)
                    )
                } catch (_: Exception) {
                    null
                }
                if (birth == null || birth.isAfter(today)) {
                    CalcResult(
                        title = "Invalid date",
                        subtitle = "Check the birth date you entered",
                        rows = listOf(ResultRow("Error", "Birth date must be a valid date in the past.")),
                        formula = "Age = today − birth date."
                    )
                } else {
                    val period = Period.between(birth, today)
                    val daysLived = ChronoUnit.DAYS.between(birth, today)
                    val nextBirthday = birth.withYear(today.year).let {
                        if (!it.isAfter(today)) it.plusYears(1) else it
                    }
                    val daysToNext = ChronoUnit.DAYS.between(today, nextBirthday)
                    CalcResult(
                        title = "${period.years} years",
                        subtitle = "${period.years} years, ${period.months} months, ${period.days} days old",
                        rows = listOf(
                            ResultRow("Age", "${period.years} years, ${period.months} months, ${period.days} days", highlight = true),
                            ResultRow("Total Days Lived", Format.whole(daysLived.toDouble())),
                            ResultRow("Total Months", Format.whole(period.toTotalMonths().toDouble())),
                            ResultRow("Total Weeks", Format.whole(daysLived / 7.0)),
                            ResultRow("Next Birthday", "in $daysToNext days ($nextBirthday)"),
                            ResultRow("Born", birth.toString())
                        ),
                        formula = "Age = today's date − birth date, counted in full years, months and days.",
                        breakdown = listOf(
                            "Born: $birth",
                            "Today: $today",
                            "Age: ${period.years} years, ${period.months} months, ${period.days} days",
                            "Days lived: ${Format.whole(daysLived.toDouble())}"
                        )
                    )
                }
            }
        ),
        CalculatorDef(
            id = "date-difference", title = "Date Difference Calculator",
            description = "Days, weeks, months and years between two dates.",
            category = Category.DAILY, icon = "date",
            inputs = listOf(
                CalcInput("year1", "Start Year", defaultValue = "2020"),
                CalcInput("month1", "Start Month (1-12)", defaultValue = "1"),
                CalcInput("day1", "Start Day (1-31)", defaultValue = "1"),
                CalcInput("year2", "End Year", defaultValue = "2026"),
                CalcInput("month2", "End Month (1-12)", defaultValue = "11"),
                CalcInput("day2", "End Day (1-31)", defaultValue = "13")
            ),
            compute = { v, s, d ->
                fun date(y: String, m: String, day: String): LocalDate? = try {
                    LocalDate.of(
                        num(v, y).toInt(), num(v, m).toInt().coerceIn(1, 12),
                        num(v, day).toInt().coerceIn(1, 28)
                    )
                } catch (_: Exception) {
                    null
                }
                val start = date("year1", "month1", "day1")
                val end = date("year2", "month2", "day2")
                if (start == null || end == null) {
                    CalcResult(
                        title = "Invalid date",
                        subtitle = "Check the dates you entered",
                        rows = listOf(ResultRow("Error", "Both dates must be valid calendar dates.")),
                        formula = "Difference = end date − start date."
                    )
                } else {
                    val (from, to) = if (end.isBefore(start)) end to start else start to end
                    val days = ChronoUnit.DAYS.between(from, to)
                    val period = Period.between(from, to)
                    CalcResult(
                        title = "${Format.whole(days.toDouble())} days",
                        subtitle = "Between $from and $to",
                        rows = listOf(
                            ResultRow("Total Days", Format.whole(days.toDouble()), highlight = true),
                            ResultRow("Weeks", Format.num(days / 7.0, 1)),
                            ResultRow("Months (approx.)", Format.num(days / 30.44, 1)),
                            ResultRow("Years", Format.num(days / 365.25, 2)),
                            ResultRow("Calendar", "${period.years} years, ${period.months} months, ${period.days} days")
                        ),
                        formula = "Difference = end date − start date, counted in days.",
                        breakdown = listOf(
                            "From: $from",
                            "To: $to",
                            "Days: ${Format.whole(days.toDouble())} — ${Format.num(days / 7.0, 1)} weeks",
                            "Calendar: ${period.years} years, ${period.months} months, ${period.days} days"
                        )
                    )
                }
            }
        ),
        CalculatorDef(
            id = "time", title = "Time Calculator",
            description = "Add or subtract hours and minutes.",
            category = Category.DAILY, icon = "time",
            inputs = listOf(
                CalcInput("hours1", "Hours", defaultValue = "5"),
                CalcInput("minutes1", "Minutes", defaultValue = "30"),
                CalcInput("op", "Operation", type = InputType.SELECT,
                    options = listOf("Add (+)", "Subtract (−)"), defaultValue = "Add (+)"),
                CalcInput("hours2", "Hours", defaultValue = "2"),
                CalcInput("minutes2", "Minutes", defaultValue = "45")
            ),
            compute = { v, s, d ->
                val t1 = num(v, "hours1") * 60 + num(v, "minutes1")
                val t2 = num(v, "hours2") * 60 + num(v, "minutes2")
                val total = if (str(v, "op").startsWith("Add")) t1 + t2 else t1 - t2
                val sign = if (total < 0) "−" else ""
                val abs = kotlin.math.abs(total)
                val h = (abs / 60).toInt()
                val m = (abs % 60).toInt()
                CalcResult(
                    title = "$sign${h}h ${m}m",
                    subtitle = "Result",
                    rows = listOf(
                        ResultRow("Result", "$sign${h} hours ${m} minutes", highlight = true),
                        ResultRow("Total Minutes", Format.num(total, 0)),
                        ResultRow("Total Hours", Format.num(total / 60.0, 2)),
                        ResultRow("First Time", "${Format.num(num(v, "hours1"), 0)}h ${Format.num(num(v, "minutes1"), 0)}m"),
                        ResultRow("Second Time", "${Format.num(num(v, "hours2"), 0)}h ${Format.num(num(v, "minutes2"), 0)}m")
                    ),
                    formula = "Convert both times to minutes, add or subtract, then convert back to hours and minutes."
                )
            }
        ),
        CalculatorDef(
            id = "unit-converter", title = "Unit Converter",
            description = "Length, weight and temperature conversions.",
            category = Category.DAILY, icon = "unit",
            inputs = listOf(
                CalcInput("value", "Value", defaultValue = "10"),
                CalcInput("from", "From Unit", type = InputType.SELECT,
                    options = listOf(
                        "m (metres)", "km (kilometres)", "cm (centimetres)", "ft (feet)", "in (inches)", "mi (miles)",
                        "kg (kilograms)", "g (grams)", "lb (pounds)", "oz (ounces)",
                        "°C (Celsius)", "°F (Fahrenheit)", "K (Kelvin)"
                    ), defaultValue = "km (kilometres)"),
                CalcInput("to", "To Unit", type = InputType.SELECT,
                    options = listOf(
                        "m (metres)", "km (kilometres)", "cm (centimetres)", "ft (feet)", "in (inches)", "mi (miles)",
                        "kg (kilograms)", "g (grams)", "lb (pounds)", "oz (ounces)",
                        "°C (Celsius)", "°F (Fahrenheit)", "K (Kelvin)"
                    ), defaultValue = "mi (miles)")
            ),
            compute = { v, s, d ->
                val value = num(v, "value")
                val from = str(v, "from").substringBefore(" ")
                val to = str(v, "to").substringBefore(" ")
                // Base units: metres, kilograms, Celsius.
                val length = mapOf("m" to 1.0, "km" to 1000.0, "cm" to 0.01, "ft" to 0.3048, "in" to 0.0254, "mi" to 1609.344)
                val weight = mapOf("kg" to 1.0, "g" to 0.001, "lb" to 0.45359237, "oz" to 0.0283495231)
                val temp = setOf("°C", "°F", "K")
                fun toCelsius(x: Double, unit: String): Double = when (unit) {
                    "°F" -> (x - 32) * 5 / 9; "K" -> x - 273.15; else -> x
                }
                fun fromCelsius(c: Double, unit: String): Double = when (unit) {
                    "°F" -> c * 9 / 5 + 32; "K" -> c + 273.15; else -> c
                }
                val result: Double? = when {
                    from in length && to in length -> value * length[from]!! / length[to]!!
                    from in weight && to in weight -> value * weight[from]!! / weight[to]!!
                    from in temp && to in temp -> fromCelsius(toCelsius(value, from), to)
                    else -> null
                }
                if (result == null) {
                    CalcResult(
                        title = "Cannot convert",
                        subtitle = "Units must be the same type",
                        rows = listOf(
                            ResultRow("Error", "Convert length to length, weight to weight, or temperature to temperature.")
                        ),
                        formula = "Length: m, km, cm, ft, in, mi. Weight: kg, g, lb, oz. Temperature: °C, °F, K."
                    )
                } else {
                    CalcResult(
                        title = "${Format.num(result, 4)} $to",
                        subtitle = "${Format.num(value, d)} $from = ${Format.num(result, 4)} $to",
                        rows = listOf(
                            ResultRow("Result", "${Format.num(result, 4)} $to", highlight = true),
                            ResultRow("From", "${Format.num(value, d)} $from"),
                            ResultRow("Conversion", "1 $from = ${Format.num(result / value, 6)} $to")
                        ),
                        formula = "Length converts through metres; weight through kilograms; temperature through Celsius."
                    )
                }
            }
        ),
        CalculatorDef(
            id = "gst", title = "GST Calculator",
            description = "GST amount and total — add GST or remove it from a price.",
            category = Category.DAILY, icon = "gst",
            inputs = listOf(
                CalcInput("price", "Price ($)", defaultValue = "1000"),
                CalcInput("rate", "GST Rate (%)", type = InputType.SELECT,
                    options = listOf("0", "5", "12", "18", "28"), defaultValue = "18"),
                CalcInput("mode", "Price Is", type = InputType.SELECT,
                    options = listOf("Excluding GST (add GST)", "Including GST (remove GST)"),
                    defaultValue = "Excluding GST (add GST)")
            ),
            compute = { v, s, d ->
                val price = num(v, "price")
                val rate = num(v, "rate")
                val addGst = str(v, "mode").startsWith("Excluding")
                val gst: Double
                val total: Double
                val base: Double
                if (addGst) {
                    gst = price * rate / 100.0
                    total = price + gst
                    base = price
                } else {
                    total = price
                    base = price * 100.0 / (100.0 + rate)
                    gst = price - base
                }
                CalcResult(
                    title = Format.money(total, s, d),
                    subtitle = if (addGst) "Total Price (with GST)" else "Price Before GST",
                    rows = listOf(
                        ResultRow("GST Amount", Format.money(gst, s, d), highlight = true),
                        ResultRow("Total Price", Format.money(total, s, d)),
                        ResultRow("Price Before GST", Format.money(base, s, d)),
                        ResultRow("GST Rate", Format.pct(rate, d)),
                        ResultRow("CGST / SGST (each)", Format.money(gst / 2.0, s, d))
                    ),
                    formula = if (addGst) "GST = Price × Rate ÷ 100. Total = Price + GST."
                    else "Base = Price × 100 ÷ (100 + Rate). GST = Price − Base.",
                    breakdown = listOf(
                        "Price: ${Format.money(price, s, d)}",
                        "GST at ${Format.pct(rate, d)}: ${Format.money(gst, s, d)}",
                        "Total: ${Format.money(total, s, d)}"
                    )
                )
            }
        ),
        CalculatorDef(
            id = "vat", title = "VAT Calculator",
            description = "VAT amount and total — add VAT or remove it from a price.",
            category = Category.DAILY, icon = "vat",
            inputs = listOf(
                CalcInput("price", "Price ($)", defaultValue = "500"),
                CalcInput("rate", "VAT Rate (%)", defaultValue = "20"),
                CalcInput("mode", "Price Is", type = InputType.SELECT,
                    options = listOf("Excluding VAT (add VAT)", "Including VAT (remove VAT)"),
                    defaultValue = "Excluding VAT (add VAT)")
            ),
            compute = { v, s, d ->
                val price = num(v, "price")
                val rate = num(v, "rate")
                val addVat = str(v, "mode").startsWith("Excluding")
                val vat: Double
                val total: Double
                val base: Double
                if (addVat) {
                    vat = price * rate / 100.0
                    total = price + vat
                    base = price
                } else {
                    total = price
                    base = price * 100.0 / (100.0 + rate)
                    vat = price - base
                }
                CalcResult(
                    title = Format.money(total, s, d),
                    subtitle = if (addVat) "Total Price (with VAT)" else "Price Before VAT",
                    rows = listOf(
                        ResultRow("VAT Amount", Format.money(vat, s, d), highlight = true),
                        ResultRow("Total Price", Format.money(total, s, d)),
                        ResultRow("Price Before VAT", Format.money(base, s, d)),
                        ResultRow("VAT Rate", Format.pct(rate, d))
                    ),
                    formula = if (addVat) "VAT = Price × Rate ÷ 100. Total = Price + VAT."
                    else "Base = Price × 100 ÷ (100 + Rate). VAT = Price − Base.",
                    breakdown = listOf(
                        "Price: ${Format.money(price, s, d)}",
                        "VAT at ${Format.pct(rate, d)}: ${Format.money(vat, s, d)}",
                        "Total: ${Format.money(total, s, d)}"
                    )
                )
            }
        )
    )
}
