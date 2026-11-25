package com.fincalc.app.data

import com.fincalc.app.util.Format

/**
 * Category 2 — Tax & Salary Calculators (10).
 *
 * Federal brackets and standard deductions are verified against IRS
 * Revenue Procedure 2025-32 (tax year 2026) and 2024-40 (tax year 2025)
 * — see TaxData.kt. FICA: Social Security 6.2% up to the wage base
 * ($184,500 for 2026; $176,100 for 2025), Medicare 1.45% plus 0.9%
 * additional Medicare tax above $200,000 (single) / $250,000 (joint).
 *
 * Every tax result carries the disclaimer that it is an estimate, not
 * official tax advice.
 */
object CalculatorsTax {

    private const val TAX_DISCLAIMER =
        "Estimate only — not official tax advice. Tax law is complex; check the IRS " +
            "or a tax professional before filing. Brackets: IRS Rev. Proc. 2025-32 (2026) / 2024-40 (2025)."

    private fun ssTax(wages: Double, year: Int): Double {
        val wageBase = if (year >= 2026) 184_500.0 else 176_100.0
        return minOf(wages, wageBase) * 0.062
    }

    private fun medicareTax(wages: Double, status: String): Double {
        val threshold = if (status == "Married Filing Jointly") 250_000.0 else 200_000.0
        val base = wages * 0.0145
        val extra = if (wages > threshold) (wages - threshold) * 0.009 else 0.0
        return base + extra
    }

    val all: List<CalculatorDef> = listOf(
        CalculatorDef(
            id = "us-income-tax", title = "US Income Tax Calculator",
            description = "Estimate your federal income tax for 2025 or 2026, with standard or itemized deductions.",
            category = Category.TAX, icon = "tax", popular = true,
            inputs = listOf(
                CalcInput("income", "Gross Annual Income ($)", defaultValue = "75000"),
                CalcInput("status", "Filing Status", type = InputType.SELECT,
                    options = TaxData.filingStatuses, defaultValue = "Single"),
                CalcInput("year", "Tax Year", type = InputType.SELECT,
                    options = TaxData.taxYears, defaultValue = "2026"),
                CalcInput("itemized", "Itemized Deductions ($, 0 = use standard)", defaultValue = "0")
            ),
            compute = { v, s, d ->
                val income = num(v, "income")
                val status = str(v, "status", "Single")
                val year = str(v, "year", "2026").toIntOrNull() ?: 2026
                val standard = TaxData.standardDeduction(year, status)
                val itemized = num(v, "itemized")
                val deduction = maxOf(standard, itemized)
                val taxable = (income - deduction).coerceAtLeast(0.0)
                val tax = TaxData.federalTax(taxable, year, status)
                val effective = if (income > 0) tax / income * 100 else 0.0
                val marginal = TaxData.marginalRate(taxable, year, status) * 100
                CalcResult(
                    title = Format.money(tax, s, d),
                    subtitle = "Estimated Federal Tax ($year)",
                    rows = listOf(
                        ResultRow("Federal Tax", Format.money(tax, s, d), highlight = true),
                        ResultRow("Taxable Income", Format.money(taxable, s, d)),
                        ResultRow("Deduction Used", Format.money(deduction, s, d)),
                        ResultRow("Effective Tax Rate", Format.pct(effective, d)),
                        ResultRow("Marginal Tax Rate", Format.pct(marginal, d)),
                        ResultRow("After-Tax Income", Format.money(income - tax, s, d))
                    ),
                    formula = "Taxable Income = Gross Income − Deduction (${Format.money(deduction, s, d)}). " +
                        "Tax is calculated bracket by bracket at 10%, 12%, 22%, 24%, 32%, 35%, 37%.",
                    breakdown = listOf(
                        "Gross income: ${Format.money(income, s, d)}",
                        "Standard deduction ($year, $status): ${Format.money(standard, s, d)}",
                        "Deduction used: ${Format.money(deduction, s, d)}",
                        "Taxable income: ${Format.money(taxable, s, d)}",
                        "Federal tax: ${Format.money(tax, s, d)}",
                        "Effective rate: ${Format.pct(effective, d)} — marginal rate: ${Format.pct(marginal, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Tax", tax),
                        ChartBar("After Tax", income - tax)
                    ),
                    tip = "Your marginal rate is the tax on your NEXT dollar — not the rate you pay on all income.",
                    disclaimer = TAX_DISCLAIMER
                )
            }
        ),
        CalculatorDef(
            id = "federal-tax", title = "Federal Tax Calculator",
            description = "Federal tax on any taxable income, broken down by bracket.",
            category = Category.TAX, icon = "federal",
            inputs = listOf(
                CalcInput("taxable", "Taxable Income ($)", defaultValue = "60000"),
                CalcInput("status", "Filing Status", type = InputType.SELECT,
                    options = TaxData.filingStatuses, defaultValue = "Single"),
                CalcInput("year", "Tax Year", type = InputType.SELECT,
                    options = TaxData.taxYears, defaultValue = "2026")
            ),
            compute = { v, s, d ->
                val taxable = num(v, "taxable")
                val status = str(v, "status", "Single")
                val year = str(v, "year", "2026").toIntOrNull() ?: 2026
                val tax = TaxData.federalTax(taxable, year, status)
                val lines = mutableListOf<String>()
                var lower = 0.0
                for (b in TaxData.brackets(year, status)) {
                    if (taxable <= lower) break
                    val inBand = minOf(taxable, b.upTo) - lower
                    lines.add(
                        "${Format.pct(b.rate * 100, 0)} on ${Format.money(lower, s, d)} – " +
                            "${if (b.upTo == Double.MAX_VALUE) "above" else Format.money(b.upTo, s, d)}: " +
                            Format.money(inBand * b.rate, s, d)
                    )
                    lower = b.upTo
                }
                CalcResult(
                    title = Format.money(tax, s, d),
                    subtitle = "Federal Tax on ${Format.money(taxable, s, d)}",
                    rows = listOf(
                        ResultRow("Federal Tax", Format.money(tax, s, d), highlight = true),
                        ResultRow("Taxable Income", Format.money(taxable, s, d)),
                        ResultRow("Effective Rate",
                            Format.pct(if (taxable > 0) tax / taxable * 100 else 0.0, d)),
                        ResultRow("Marginal Rate",
                            Format.pct(TaxData.marginalRate(taxable, year, status) * 100, d))
                    ),
                    formula = "Tax is the sum of each bracket: income inside a band is taxed only at that band's rate.",
                    breakdown = lines,
                    chart = listOf(
                        ChartBar("Tax", tax),
                        ChartBar("Kept", (taxable - tax).coerceAtLeast(0.0))
                    ),
                    disclaimer = TAX_DISCLAIMER
                )
            }
        ),
        CalculatorDef(
            id = "state-tax", title = "State Tax Calculator",
            description = "Estimated state income tax for any US state.",
            category = Category.TAX, icon = "state",
            inputs = listOf(
                CalcInput("income", "Annual Income ($)", defaultValue = "75000"),
                CalcInput("state", "State", type = InputType.SELECT,
                    options = TaxData.stateRates.keys.sorted(), defaultValue = "California")
            ),
            compute = { v, s, d ->
                val income = num(v, "income")
                val state = str(v, "state", "California")
                val rate = TaxData.stateRates[state] ?: 0.0
                val tax = income * rate / 100.0
                CalcResult(
                    title = Format.money(tax, s, d),
                    subtitle = "Estimated $state Tax",
                    rows = listOf(
                        ResultRow("State Tax", Format.money(tax, s, d), highlight = true),
                        ResultRow("Rate Used", Format.pct(rate, d)),
                        ResultRow("Annual Income", Format.money(income, s, d)),
                        ResultRow("Monthly (approx.)", Format.money(tax / 12.0, s, d)),
                        ResultRow("After State Tax", Format.money(income - tax, s, d))
                    ),
                    formula = "Estimated State Tax = Income × State Rate. " +
                        "Flat states use their flat rate; progressive states use the top marginal rate.",
                    breakdown = listOf(
                        "State: $state — rate used ${Format.pct(rate, d)}",
                        "Income: ${Format.money(income, s, d)}",
                        "Estimated state tax: ${Format.money(tax, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("State Tax", tax),
                        ChartBar("After Tax", income - tax)
                    ),
                    tip = if (rate == 0.0) "$state has no state income tax."
                    else "Most progressive states tax lower income at lower rates, so your real tax may be less than this estimate.",
                    disclaimer = "Estimate only — uses the state's flat rate, or its top marginal rate for progressive states. " +
                        "Actual state tax depends on that state's brackets, deductions and credits."
                )
            }
        ),
        CalculatorDef(
            id = "paycheck", title = "Paycheck Calculator",
            description = "Gross-to-net pay per paycheck, with federal tax and FICA.",
            category = Category.TAX, icon = "paycheck", popular = true,
            inputs = listOf(
                CalcInput("salary", "Gross Annual Salary ($)", defaultValue = "65000"),
                CalcInput("frequency", "Pay Frequency", type = InputType.SELECT,
                    options = listOf("Weekly (52)", "Biweekly (26)", "Semimonthly (24)", "Monthly (12)"),
                    defaultValue = "Biweekly (26)"),
                CalcInput("status", "Filing Status", type = InputType.SELECT,
                    options = TaxData.filingStatuses, defaultValue = "Single"),
                CalcInput("year", "Tax Year", type = InputType.SELECT,
                    options = TaxData.taxYears, defaultValue = "2026"),
                CalcInput("pretax", "Pre-Tax Deductions per Year ($)", defaultValue = "0")
            ),
            compute = { v, s, d ->
                val salary = num(v, "salary")
                val periods = when {
                    str(v, "frequency").startsWith("Weekly") -> 52.0
                    str(v, "frequency").startsWith("Biweekly") -> 26.0
                    str(v, "frequency").startsWith("Semimonthly") -> 24.0
                    else -> 12.0
                }
                val status = str(v, "status", "Single")
                val year = str(v, "year", "2026").toIntOrNull() ?: 2026
                val pretax = num(v, "pretax")
                val taxableIncome = (salary - pretax - TaxData.standardDeduction(year, status)).coerceAtLeast(0.0)
                val fedAnnual = TaxData.federalTax(taxableIncome, year, status)
                val ssAnnual = ssTax(salary - pretax, year)
                val medAnnual = medicareTax(salary - pretax, status)
                val grossPer = salary / periods
                val fedPer = fedAnnual / periods
                val ssPer = ssAnnual / periods
                val medPer = medAnnual / periods
                val pretaxPer = pretax / periods
                val netPer = grossPer - fedPer - ssPer - medPer - pretaxPer
                CalcResult(
                    title = Format.money(netPer, s, d),
                    subtitle = "Net Pay per Paycheck",
                    rows = listOf(
                        ResultRow("Net Pay", Format.money(netPer, s, d), highlight = true),
                        ResultRow("Gross Pay", Format.money(grossPer, s, d)),
                        ResultRow("Federal Tax", Format.money(fedPer, s, d)),
                        ResultRow("Social Security (6.2%)", Format.money(ssPer, s, d)),
                        ResultRow("Medicare (1.45%)", Format.money(medPer, s, d)),
                        ResultRow("Pre-Tax Deductions", Format.money(pretaxPer, s, d)),
                        ResultRow("Annual Net Pay", Format.money(netPer * periods, s, d))
                    ),
                    formula = "Net = Gross − Federal Tax − Social Security − Medicare − Pre-Tax Deductions. " +
                        "Federal tax uses $year brackets and the standard deduction for $status.",
                    breakdown = listOf(
                        "Gross annual: ${Format.money(salary, s, d)} (${Format.num(periods, 0)} paychecks)",
                        "Federal tax per year: ${Format.money(fedAnnual, s, d)}",
                        "Social Security per year: ${Format.money(ssAnnual, s, d)}",
                        "Medicare per year: ${Format.money(medAnnual, s, d)}",
                        "Net per paycheck: ${Format.money(netPer, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Net", netPer),
                        ChartBar("Federal", fedPer),
                        ChartBar("FICA", ssPer + medPer)
                    ),
                    tip = "State tax, health insurance and retirement contributions are not included — add them to pre-tax deductions if known.",
                    disclaimer = TAX_DISCLAIMER
                )
            }
        ),
        CalculatorDef(
            id = "salary", title = "Salary Calculator",
            description = "Convert between hourly, weekly, monthly and annual pay.",
            category = Category.TAX, icon = "salary",
            inputs = listOf(
                CalcInput("hourly", "Hourly Wage ($)", defaultValue = "25"),
                CalcInput("hours", "Hours per Week", defaultValue = "40"),
                CalcInput("weeks", "Weeks per Year", defaultValue = "52")
            ),
            compute = { v, s, d ->
                val hourly = num(v, "hourly")
                val hours = num(v, "hours")
                val weeks = num(v, "weeks")
                val weekly = hourly * hours
                val annual = weekly * weeks
                val monthly = annual / 12.0
                val daily = hourly * hours / 5.0
                CalcResult(
                    title = Format.money(annual, s, d),
                    subtitle = "Annual Salary",
                    rows = listOf(
                        ResultRow("Annual Salary", Format.money(annual, s, d), highlight = true),
                        ResultRow("Monthly Salary", Format.money(monthly, s, d)),
                        ResultRow("Weekly Pay", Format.money(weekly, s, d)),
                        ResultRow("Daily Pay (5-day week)", Format.money(daily, s, d)),
                        ResultRow("Hourly Wage", Format.money(hourly, s, d))
                    ),
                    formula = "Annual = Hourly Wage × Hours per Week × Weeks per Year. Monthly = Annual ÷ 12.",
                    breakdown = listOf(
                        "Hourly: ${Format.money(hourly, s, d)} × ${Format.num(hours, 0)} hrs/week",
                        "Weekly: ${Format.money(weekly, s, d)}",
                        "Annual (${Format.num(weeks, 0)} weeks): ${Format.money(annual, s, d)}",
                        "Monthly: ${Format.money(monthly, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Weekly", weekly),
                        ChartBar("Monthly", monthly)
                    )
                )
            }
        ),
        CalculatorDef(
            id = "hourly-to-annual", title = "Hourly to Annual Salary Calculator",
            description = "What an hourly wage is worth per year.",
            category = Category.TAX, icon = "hourly",
            inputs = listOf(
                CalcInput("hourly", "Hourly Wage ($)", defaultValue = "20"),
                CalcInput("hours", "Hours per Week", defaultValue = "40")
            ),
            compute = { v, s, d ->
                val hourly = num(v, "hourly")
                val hours = num(v, "hours")
                val annual = hourly * hours * 52.0
                CalcResult(
                    title = Format.money(annual, s, d),
                    subtitle = "Annual Salary (52 weeks)",
                    rows = listOf(
                        ResultRow("Annual Salary", Format.money(annual, s, d), highlight = true),
                        ResultRow("Monthly Salary", Format.money(annual / 12.0, s, d)),
                        ResultRow("Weekly Pay", Format.money(hourly * hours, s, d)),
                        ResultRow("Biweekly Pay", Format.money(hourly * hours * 2, s, d))
                    ),
                    formula = "Annual = Hourly Wage × Hours per Week × 52 weeks.",
                    breakdown = listOf(
                        "Hourly: ${Format.money(hourly, s, d)}",
                        "Weekly (${Format.num(hours, 0)} hrs): ${Format.money(hourly * hours, s, d)}",
                        "Annual: ${Format.money(annual, s, d)}"
                    ),
                    tip = "This is gross pay before taxes and deductions."
                )
            }
        ),
        CalculatorDef(
            id = "overtime", title = "Overtime Calculator",
            description = "Weekly pay with overtime at time-and-a-half (or your multiplier).",
            category = Category.TAX, icon = "overtime",
            inputs = listOf(
                CalcInput("hourly", "Hourly Wage ($)", defaultValue = "20"),
                CalcInput("regular", "Regular Hours", defaultValue = "40"),
                CalcInput("overtime", "Overtime Hours", defaultValue = "10"),
                CalcInput("multiplier", "Overtime Multiplier (e.g. 1.5)", defaultValue = "1.5")
            ),
            compute = { v, s, d ->
                val hourly = num(v, "hourly")
                val regular = hourly * num(v, "regular")
                val otRate = hourly * num(v, "multiplier")
                val otPay = otRate * num(v, "overtime")
                val total = regular + otPay
                CalcResult(
                    title = Format.money(total, s, d),
                    subtitle = "Total Weekly Pay",
                    rows = listOf(
                        ResultRow("Total Weekly Pay", Format.money(total, s, d), highlight = true),
                        ResultRow("Regular Pay", Format.money(regular, s, d)),
                        ResultRow("Overtime Pay", Format.money(otPay, s, d)),
                        ResultRow("Overtime Rate", Format.money(otRate, s, d) + " / hour"),
                        ResultRow("Effective Hourly Rate",
                            Format.money(if (num(v, "regular") + num(v, "overtime") > 0) total / (num(v, "regular") + num(v, "overtime")) else 0.0, s, d))
                    ),
                    formula = "Overtime Pay = Overtime Hours × Hourly Wage × Multiplier. Total = Regular Pay + Overtime Pay.",
                    breakdown = listOf(
                        "Regular: ${Format.money(regular, s, d)}",
                        "Overtime rate: ${Format.money(otRate, s, d)}/hr × ${Format.num(num(v, "overtime"), 0)} hrs = ${Format.money(otPay, s, d)}",
                        "Total weekly: ${Format.money(total, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Regular", regular),
                        ChartBar("Overtime", otPay)
                    ),
                    tip = "In the US, non-exempt workers usually earn 1.5× after 40 hours a week (FLSA)."
                )
            }
        ),
        CalculatorDef(
            id = "sales-tax", title = "Sales Tax Calculator",
            description = "Tax amount and total price for any purchase.",
            category = Category.TAX, icon = "sales",
            inputs = listOf(
                CalcInput("price", "Price Before Tax ($)", defaultValue = "100"),
                CalcInput("rate", "Sales Tax Rate (%)", defaultValue = "8.25")
            ),
            compute = { v, s, d ->
                val price = num(v, "price")
                val tax = price * num(v, "rate") / 100.0
                CalcResult(
                    title = Format.money(price + tax, s, d),
                    subtitle = "Total Price (with tax)",
                    rows = listOf(
                        ResultRow("Total Price", Format.money(price + tax, s, d), highlight = true),
                        ResultRow("Sales Tax", Format.money(tax, s, d)),
                        ResultRow("Price Before Tax", Format.money(price, s, d)),
                        ResultRow("Tax Rate", Format.pct(num(v, "rate"), d))
                    ),
                    formula = "Sales Tax = Price × Rate ÷ 100. Total = Price + Sales Tax.",
                    breakdown = listOf(
                        "Price: ${Format.money(price, s, d)}",
                        "Tax (${Format.pct(num(v, "rate"), d)}): ${Format.money(tax, s, d)}",
                        "Total: ${Format.money(price + tax, s, d)}"
                    )
                )
            }
        ),
        CalculatorDef(
            id = "payroll-tax", title = "Payroll Tax Calculator",
            description = "Employee Social Security and Medicare tax on wages.",
            category = Category.TAX, icon = "payroll",
            inputs = listOf(
                CalcInput("wages", "Gross Wages ($)", defaultValue = "5000"),
                CalcInput("status", "Filing Status", type = InputType.SELECT,
                    options = TaxData.filingStatuses, defaultValue = "Single"),
                CalcInput("year", "Tax Year", type = InputType.SELECT,
                    options = TaxData.taxYears, defaultValue = "2026")
            ),
            compute = { v, s, d ->
                val wages = num(v, "wages")
                val year = str(v, "year", "2026").toIntOrNull() ?: 2026
                val status = str(v, "status", "Single")
                val ss = ssTax(wages, year)
                val med = medicareTax(wages, status)
                val total = ss + med
                CalcResult(
                    title = Format.money(total, s, d),
                    subtitle = "Total Payroll Tax (FICA)",
                    rows = listOf(
                        ResultRow("Total Payroll Tax", Format.money(total, s, d), highlight = true),
                        ResultRow("Social Security (6.2%)", Format.money(ss, s, d)),
                        ResultRow("Medicare (1.45%)", Format.money(med, s, d)),
                        ResultRow("Net After Payroll Tax", Format.money(wages - total, s, d)),
                        ResultRow("Effective Rate",
                            Format.pct(if (wages > 0) total / wages * 100 else 0.0, d))
                    ),
                    formula = "Social Security = 6.2% of wages up to the wage base " +
                        "(${Format.money(if (year >= 2026) 184500.0 else 176100.0, s, 0)} for $year). " +
                        "Medicare = 1.45% of all wages (+0.9% above ${Format.money(if (status == "Married Filing Jointly") 250000.0 else 200000.0, s, 0)}).",
                    breakdown = listOf(
                        "Gross wages: ${Format.money(wages, s, d)}",
                        "Social Security: ${Format.money(ss, s, d)}",
                        "Medicare: ${Format.money(med, s, d)}",
                        "Total FICA: ${Format.money(total, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Social Security", ss),
                        ChartBar("Medicare", med),
                        ChartBar("Net Pay", wages - total)
                    ),
                    tip = "Employers pay a matching 6.2% + 1.45% on top — the total FICA cost is roughly double the employee share.",
                    disclaimer = TAX_DISCLAIMER
                )
            }
        ),
        CalculatorDef(
            id = "take-home", title = "Take-Home Salary Calculator",
            description = "Your real take-home pay after federal tax, state tax and FICA.",
            category = Category.TAX, icon = "takehome", popular = true,
            inputs = listOf(
                CalcInput("salary", "Gross Annual Salary ($)", defaultValue = "80000"),
                CalcInput("status", "Filing Status", type = InputType.SELECT,
                    options = TaxData.filingStatuses, defaultValue = "Single"),
                CalcInput("year", "Tax Year", type = InputType.SELECT,
                    options = TaxData.taxYears, defaultValue = "2026"),
                CalcInput("state", "State", type = InputType.SELECT,
                    options = TaxData.stateRates.keys.sorted(), defaultValue = "Texas"),
                CalcInput("pretax", "Pre-Tax Deductions per Year ($)", defaultValue = "0")
            ),
            compute = { v, s, d ->
                val salary = num(v, "salary")
                val status = str(v, "status", "Single")
                val year = str(v, "year", "2026").toIntOrNull() ?: 2026
                val state = str(v, "state", "Texas")
                val pretax = num(v, "pretax")
                val taxableIncome = (salary - pretax - TaxData.standardDeduction(year, status)).coerceAtLeast(0.0)
                val fed = TaxData.federalTax(taxableIncome, year, status)
                val stateRate = TaxData.stateRates[state] ?: 0.0
                val stateTax = salary * stateRate / 100.0
                val ss = ssTax(salary - pretax, year)
                val med = medicareTax(salary - pretax, status)
                val totalTax = fed + stateTax + ss + med
                val net = salary - pretax - totalTax
                CalcResult(
                    title = Format.money(net, s, d),
                    subtitle = "Annual Take-Home Pay",
                    rows = listOf(
                        ResultRow("Take-Home Pay (Annual)", Format.money(net, s, d), highlight = true),
                        ResultRow("Take-Home Pay (Monthly)", Format.money(net / 12.0, s, d)),
                        ResultRow("Federal Tax", Format.money(fed, s, d)),
                        ResultRow("State Tax ($state, est.)", Format.money(stateTax, s, d)),
                        ResultRow("Social Security", Format.money(ss, s, d)),
                        ResultRow("Medicare", Format.money(med, s, d)),
                        ResultRow("Total Tax", Format.money(totalTax, s, d))
                    ),
                    formula = "Take-Home = Gross − Pre-Tax Deductions − Federal Tax − State Tax − Social Security − Medicare.",
                    breakdown = listOf(
                        "Gross: ${Format.money(salary, s, d)}",
                        "Federal tax: ${Format.money(fed, s, d)}",
                        "State tax ($state): ${Format.money(stateTax, s, d)}",
                        "FICA: ${Format.money(ss + med, s, d)}",
                        "Take-home: ${Format.money(net, s, d)} / year — ${Format.money(net / 12.0, s, d)} / month"
                    ),
                    chart = listOf(
                        ChartBar("Take-Home", net),
                        ChartBar("Federal", fed),
                        ChartBar("State", stateTax),
                        ChartBar("FICA", ss + med)
                    ),
                    tip = "State tax here is an estimate (flat rate or top marginal rate). Your real state tax may be lower.",
                    disclaimer = TAX_DISCLAIMER
                )
            }
        )
    )
}
