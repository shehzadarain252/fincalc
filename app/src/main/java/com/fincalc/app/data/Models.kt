package com.fincalc.app.data

/**
 * Core data model for FinCalc.
 *
 * Every calculator in the app (90 total) is declared as a [CalculatorDef]:
 * a list of typed inputs plus a pure compute function. One generic
 * CalculatorScreen renders any definition, so all 90 tools share the same
 * validation, result, copy/share/save/PDF and favourite behaviour.
 */

enum class Category(val id: String, val title: String, val subtitle: String) {
    LOAN("loan", "Loan & Mortgage Calculators", "15 Tools"),
    TAX("tax", "Tax & Salary Calculators", "15 Tools"),
    INVESTMENT("investment", "Investment Calculators", "15 Tools"),
    PERSONAL("personal", "Personal Finance Calculators", "15 Tools"),
    BUSINESS("business", "Business Calculators", "15 Tools"),
    DAILY("daily", "Currency & Daily Tools", "15 Tools")
}

enum class InputType { NUMBER, SELECT, TEXT, DATE }

data class CalcInput(
    val id: String,
    val label: String,
    val type: InputType = InputType.NUMBER,
    val defaultValue: String = "",
    val hint: String = "",
    val suffix: String = "",
    /** For SELECT inputs: the options shown in the dropdown. */
    val options: List<String> = emptyList(),
    val min: Double? = null,
    val max: Double? = null,
    val required: Boolean = true
)

data class ResultRow(
    val label: String,
    val value: String,
    val highlight: Boolean = false
)

data class ChartBar(val label: String, val value: Double)

data class CalcResult(
    /** Main headline value, e.g. "$466.01". */
    val title: String,
    /** Headline caption, e.g. "Monthly Payment". */
    val subtitle: String,
    val rows: List<ResultRow>,
    val formula: String,
    val breakdown: List<String> = emptyList(),
    val chart: List<ChartBar> = emptyList(),
    val tip: String? = null,
    /** Shown under the result when the numbers are estimates. */
    val disclaimer: String? = null
)

data class CalculatorDef(
    val id: String,
    val title: String,
    val description: String,
    val category: Category,
    /** Material icon key used by the UI (see Icons.kt). */
    val icon: String,
    val inputs: List<CalcInput>,
    val popular: Boolean = false,
    val quick: Boolean = false,
    /**
     * Pure calculation. [inputs] are raw field values keyed by input id;
     * [symbol] is the user's currency symbol (default "$") and [decimals]
     * the chosen decimal precision — results must be formatted with them.
     */
    val compute: (inputs: Map<String, String>, symbol: String, decimals: Int) -> CalcResult
)

/** Parse a numeric input; blank / invalid input becomes [fallback]. */
fun num(values: Map<String, String>, id: String, fallback: Double = 0.0): Double =
    values[id]?.replace(",", "")?.toDoubleOrNull() ?: fallback

fun str(values: Map<String, String>, id: String, fallback: String = ""): String =
    values[id]?.takeIf { it.isNotBlank() } ?: fallback

/** Standard EMI: P = principal, annualRatePct, years. Returns monthly payment. */
fun emi(principal: Double, annualRatePct: Double, years: Double): Double {
    val n = years * 12.0
    if (n <= 0) return 0.0
    val r = annualRatePct / 100.0 / 12.0
    if (r == 0.0) return principal / n
    val factor = Math.pow(1 + r, n)
    return principal * r * factor / (factor - 1)
}

/** Future value of a present amount compounded [periodsPerYear] times. */
fun compound(principal: Double, annualRatePct: Double, years: Double, periodsPerYear: Double = 1.0): Double {
    val r = annualRatePct / 100.0 / periodsPerYear
    return principal * Math.pow(1 + r, periodsPerYear * years)
}

/** Future value of a series of equal monthly contributions. */
fun futureValueAnnuity(monthly: Double, annualRatePct: Double, years: Double): Double {
    val n = years * 12.0
    val r = annualRatePct / 100.0 / 12.0
    if (r == 0.0) return monthly * n
    return monthly * (Math.pow(1 + r, n) - 1) / r
}
