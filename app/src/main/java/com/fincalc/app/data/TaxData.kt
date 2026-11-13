package com.fincalc.app.data

/**
 * US federal income tax data — verified against IRS Revenue Procedure
 * 2025-32 (tax year 2026) and Revenue Procedure 2024-40 (tax year 2025).
 * Rates are marginal: each rate applies only to the income inside its band.
 *
 * Sources (checked 2026-11-13):
 * - 2026: Rev. Proc. 2025-32 — 7 rates 10/12/22/24/32/35/37;
 *   standard deduction Single $16,100 / MFJ $32,200 / HoH $24,150.
 * - 2025: Rev. Proc. 2024-40 — same 7 rates;
 *   standard deduction Single $15,000 / MFJ $30,000 / HoH $22,500.
 *
 * State rates below are ESTIMATES for the State Tax Calculator: flat rates
 * where the state is flat, otherwise the state's top marginal rate. The UI
 * labels them as estimates with a disclaimer — actual state tax depends on
 * that state's own brackets, deductions and credits.
 */

data class Bracket(val upTo: Double, val rate: Double)

object TaxData {

    private val brackets2026Single = listOf(
        Bracket(12_400.0, 0.10), Bracket(50_400.0, 0.12), Bracket(105_700.0, 0.22),
        Bracket(201_775.0, 0.24), Bracket(256_225.0, 0.32), Bracket(640_600.0, 0.35),
        Bracket(Double.MAX_VALUE, 0.37)
    )
    private val brackets2026Joint = listOf(
        Bracket(24_800.0, 0.10), Bracket(100_800.0, 0.12), Bracket(211_400.0, 0.22),
        Bracket(403_550.0, 0.24), Bracket(512_450.0, 0.32), Bracket(768_700.0, 0.35),
        Bracket(Double.MAX_VALUE, 0.37)
    )
    private val brackets2026HoH = listOf(
        Bracket(17_700.0, 0.10), Bracket(67_450.0, 0.12), Bracket(105_700.0, 0.22),
        Bracket(201_750.0, 0.24), Bracket(256_200.0, 0.32), Bracket(640_600.0, 0.35),
        Bracket(Double.MAX_VALUE, 0.37)
    )
    private val brackets2026Separate = listOf(
        Bracket(12_400.0, 0.10), Bracket(50_400.0, 0.12), Bracket(105_700.0, 0.22),
        Bracket(201_775.0, 0.24), Bracket(256_225.0, 0.32), Bracket(384_350.0, 0.35),
        Bracket(Double.MAX_VALUE, 0.37)
    )

    private val brackets2025Single = listOf(
        Bracket(11_925.0, 0.10), Bracket(48_475.0, 0.12), Bracket(103_350.0, 0.22),
        Bracket(197_300.0, 0.24), Bracket(250_525.0, 0.32), Bracket(626_350.0, 0.35),
        Bracket(Double.MAX_VALUE, 0.37)
    )
    private val brackets2025Joint = listOf(
        Bracket(23_850.0, 0.10), Bracket(96_950.0, 0.12), Bracket(206_700.0, 0.22),
        Bracket(394_600.0, 0.24), Bracket(501_050.0, 0.32), Bracket(751_600.0, 0.35),
        Bracket(Double.MAX_VALUE, 0.37)
    )
    private val brackets2025HoH = listOf(
        Bracket(17_000.0, 0.10), Bracket(64_850.0, 0.12), Bracket(103_350.0, 0.22),
        Bracket(197_300.0, 0.24), Bracket(250_500.0, 0.32), Bracket(626_350.0, 0.35),
        Bracket(Double.MAX_VALUE, 0.37)
    )
    private val brackets2025Separate = listOf(
        Bracket(11_925.0, 0.10), Bracket(48_475.0, 0.12), Bracket(103_350.0, 0.22),
        Bracket(197_300.0, 0.24), Bracket(250_525.0, 0.32), Bracket(375_800.0, 0.35),
        Bracket(Double.MAX_VALUE, 0.37)
    )

    fun brackets(year: Int, status: String): List<Bracket> {
        val table2026 = when (status) {
            "Married Filing Jointly" -> brackets2026Joint
            "Head of Household" -> brackets2026HoH
            "Married Filing Separately" -> brackets2026Separate
            else -> brackets2026Single
        }
        val table2025 = when (status) {
            "Married Filing Jointly" -> brackets2025Joint
            "Head of Household" -> brackets2025HoH
            "Married Filing Separately" -> brackets2025Separate
            else -> brackets2025Single
        }
        return if (year >= 2026) table2026 else table2025
    }

    fun standardDeduction(year: Int, status: String): Double {
        return if (year >= 2026) {
            when (status) {
                "Married Filing Jointly" -> 32_200.0
                "Head of Household" -> 24_150.0
                else -> 16_100.0
            }
        } else {
            when (status) {
                "Married Filing Jointly" -> 30_000.0
                "Head of Household" -> 22_500.0
                else -> 15_000.0
            }
        }
    }

    /** Federal tax on a taxable income using the marginal bracket table. */
    fun federalTax(taxableIncome: Double, year: Int, status: String): Double {
        var tax = 0.0
        var lower = 0.0
        for (b in brackets(year, status)) {
            if (taxableIncome <= lower) break
            val inBand = minOf(taxableIncome, b.upTo) - lower
            tax += inBand * b.rate
            lower = b.upTo
        }
        return tax
    }

    /** Marginal rate that applies to the next dollar of taxable income. */
    fun marginalRate(taxableIncome: Double, year: Int, status: String): Double {
        for (b in brackets(year, status)) {
            if (taxableIncome < b.upTo) return b.rate
        }
        return 0.37
    }

    /** State -> estimated rate (percent). 0 = no state income tax.
     *  Flat states use their flat rate; progressive states use the top
     *  marginal rate. The UI must label the result an estimate. */
    val stateRates: Map<String, Double> = mapOf(
        "Alabama" to 5.00, "Alaska" to 0.0, "Arizona" to 2.50, "Arkansas" to 3.90,
        "California" to 13.30, "Colorado" to 4.40, "Connecticut" to 6.99,
        "Delaware" to 6.60, "District of Columbia" to 10.75, "Florida" to 0.0,
        "Georgia" to 5.19, "Hawaii" to 11.00, "Idaho" to 5.695, "Illinois" to 4.95,
        "Indiana" to 3.00, "Iowa" to 3.80, "Kansas" to 5.57, "Kentucky" to 4.00,
        "Louisiana" to 3.00, "Maine" to 7.15, "Maryland" to 5.75,
        "Massachusetts" to 5.00, "Michigan" to 4.25, "Minnesota" to 9.85,
        "Mississippi" to 4.00, "Missouri" to 4.70, "Montana" to 5.90,
        "Nebraska" to 5.20, "Nevada" to 0.0, "New Hampshire" to 0.0,
        "New Jersey" to 10.75, "New Mexico" to 5.90, "New York" to 10.90,
        "North Carolina" to 4.50, "North Dakota" to 2.50, "Ohio" to 3.50,
        "Oklahoma" to 4.75, "Oregon" to 9.90, "Pennsylvania" to 3.07,
        "Rhode Island" to 5.99, "South Carolina" to 6.20, "South Dakota" to 0.0,
        "Tennessee" to 0.0, "Texas" to 0.0, "Utah" to 4.55, "Vermont" to 8.75,
        "Virginia" to 5.75, "Washington" to 0.0, "West Virginia" to 4.00,
        "Wisconsin" to 7.65, "Wyoming" to 0.0
    )

    val filingStatuses = listOf(
        "Single", "Married Filing Jointly", "Head of Household", "Married Filing Separately"
    )

    val taxYears = listOf("2026", "2025")
}
