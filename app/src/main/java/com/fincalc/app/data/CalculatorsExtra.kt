package com.fincalc.app.data

import com.fincalc.app.util.Format
import kotlin.math.pow

/**
 * Extra calculators (30) — added at the user's request ("add as many tools as
 * you can"). 5 more per category, taking FinCalc from 60 to 90 calculators.
 * All formulas are the standard textbook ones, verified by hand.
 */
object CalculatorsExtra {

    private const val EXTRA_TAX_NOTE =
        "Estimate only — not official tax advice. Tax law is complex; check the IRS " +
            "or a tax professional before filing."

    // ------------------------------------------------------------------ LOANS

    val loan: List<CalculatorDef> = listOf(
        CalculatorDef(
            id = "student-loan", title = "Student Loan Calculator",
            description = "Monthly payment, total cost and interest for a student loan.",
            category = Category.LOAN, icon = "loan",
            inputs = listOf(
                CalcInput("amount", "Loan Amount ($)", defaultValue = "30000"),
                CalcInput("rate", "Interest Rate (%)", defaultValue = "5.5"),
                CalcInput("years", "Loan Term (years)", defaultValue = "10")
            ),
            compute = { v, s, d ->
                val amount = num(v, "amount")
                val rate = num(v, "rate")
                val years = num(v, "years")
                val monthly = emi(amount, rate, years)
                val total = monthly * years * 12
                CalcResult(
                    title = Format.money(monthly, s, d), subtitle = "Monthly Payment",
                    rows = listOf(
                        ResultRow("Monthly Payment", Format.money(monthly, s, d), true),
                        ResultRow("Loan Amount", Format.money(amount, s, d)),
                        ResultRow("Total Interest", Format.money(total - amount, s, d)),
                        ResultRow("Total Paid", Format.money(total, s, d))
                    ),
                    formula = "EMI = P × r(1+r)^n / ((1+r)^n − 1)",
                    breakdown = listOf(
                        "Loan amount: ${Format.money(amount, s, d)}",
                        "Interest rate: ${Format.num(rate, d)}% per year",
                        "Term: ${Format.num(years, 0)} years (${Format.num(years * 12, 0)} payments)"
                    ),
                    chart = listOf(
                        ChartBar("Principal", amount),
                        ChartBar("Interest", total - amount)
                    ),
                    tip = "Paying even a little extra each month cuts the total interest and shortens the loan."
                )
            }
        ),
        CalculatorDef(
            id = "home-equity-loan", title = "Home Equity Loan Calculator",
            description = "How much you can borrow against your home equity, and the monthly payment.",
            category = Category.LOAN, icon = "home",
            inputs = listOf(
                CalcInput("value", "Home Value ($)", defaultValue = "400000"),
                CalcInput("balance", "Remaining Mortgage Balance ($)", defaultValue = "250000"),
                CalcInput("loan", "Loan Amount ($)", defaultValue = "50000"),
                CalcInput("rate", "Interest Rate (%)", defaultValue = "7"),
                CalcInput("years", "Loan Term (years)", defaultValue = "15")
            ),
            compute = { v, s, d ->
                val value = num(v, "value")
                val balance = num(v, "balance")
                val loan = num(v, "loan")
                val rate = num(v, "rate")
                val years = num(v, "years")
                val equity = (value - balance).coerceAtLeast(0.0)
                val ltv = if (value > 0) (balance + loan) / value * 100 else 0.0
                val maxBorrow = (value * 0.80 - balance).coerceAtLeast(0.0)
                val monthly = emi(loan, rate, years)
                val total = monthly * years * 12
                CalcResult(
                    title = Format.money(monthly, s, d), subtitle = "Monthly Payment",
                    rows = listOf(
                        ResultRow("Monthly Payment", Format.money(monthly, s, d), true),
                        ResultRow("Home Equity", Format.money(equity, s, d)),
                        ResultRow("Loan-to-Value After Loan", Format.pct(ltv, d)),
                        ResultRow("Max Borrowable (80% LTV)", Format.money(maxBorrow, s, d)),
                        ResultRow("Total Interest", Format.money(total - loan, s, d))
                    ),
                    formula = "Equity = Home Value − Mortgage Balance. LTV = (Balance + Loan) / Home Value.",
                    breakdown = listOf(
                        "Most lenders cap total borrowing at 80% loan-to-value.",
                        "Your equity is ${Format.money(equity, s, d)} on a ${Format.money(value, s, d)} home."
                    ),
                    tip = "Borrowing above 80% LTV usually means a higher rate and stricter approval."
                )
            }
        ),
        CalculatorDef(
            id = "refinance", title = "Refinance Calculator",
            description = "Compare your current loan with a refinance — monthly savings and break-even time.",
            category = Category.LOAN, icon = "compare",
            inputs = listOf(
                CalcInput("balance", "Remaining Loan Balance ($)", defaultValue = "250000"),
                CalcInput("curRate", "Current Interest Rate (%)", defaultValue = "7.5"),
                CalcInput("remYears", "Years Remaining", defaultValue = "25"),
                CalcInput("newRate", "New Interest Rate (%)", defaultValue = "6"),
                CalcInput("newTerm", "New Loan Term (years)", defaultValue = "30"),
                CalcInput("costs", "Refinance Closing Costs ($)", defaultValue = "5000")
            ),
            compute = { v, s, d ->
                val balance = num(v, "balance")
                val oldPay = emi(balance, num(v, "curRate"), num(v, "remYears"))
                val newPay = emi(balance, num(v, "newRate"), num(v, "newTerm"))
                val savings = oldPay - newPay
                val costs = num(v, "costs")
                val breakeven = if (savings > 0) costs / savings else Double.NaN
                CalcResult(
                    title = if (savings > 0) Format.money(savings, s, d) else Format.money(0.0, s, d),
                    subtitle = "Monthly Savings",
                    rows = listOf(
                        ResultRow("Current Monthly Payment", Format.money(oldPay, s, d)),
                        ResultRow("New Monthly Payment", Format.money(newPay, s, d)),
                        ResultRow("Monthly Savings", Format.money(savings, s, d), true),
                        ResultRow(
                            "Break-Even Time",
                            if (savings > 0) "${Format.num(breakeven, 1)} months" else "Never — no savings"
                        ),
                        ResultRow("Closing Costs", Format.money(costs, s, d))
                    ),
                    formula = "Savings = Old Payment − New Payment. Break-even = Closing Costs / Monthly Savings.",
                    breakdown = listOf(
                        "Refinancing pays off only if you keep the loan longer than the break-even time.",
                        if (savings > 0) "You recover the closing costs after ${Format.num(breakeven, 1)} months."
                        else "The new rate does not lower your payment — refinancing would cost you money."
                    ),
                    tip = "A shorter new term can save far more interest overall, even if the monthly payment barely drops."
                )
            }
        ),
        CalculatorDef(
            id = "loan-affordability", title = "Loan Affordability Calculator",
            description = "How much can you borrow for a given monthly payment, rate and term?",
            category = Category.LOAN, icon = "loan",
            inputs = listOf(
                CalcInput("payment", "Monthly Payment You Can Afford ($)", defaultValue = "2000"),
                CalcInput("rate", "Interest Rate (%)", defaultValue = "6.5"),
                CalcInput("years", "Loan Term (years)", defaultValue = "30")
            ),
            compute = { v, s, d ->
                val payment = num(v, "payment")
                val rate = num(v, "rate")
                val years = num(v, "years")
                val r = rate / 100.0 / 12.0
                val n = years * 12.0
                val maxLoan = if (r == 0.0) payment * n
                else payment * ((1 + r).pow(n) - 1) / (r * (1 + r).pow(n))
                val total = payment * n
                CalcResult(
                    title = Format.money(maxLoan, s, d), subtitle = "Maximum Loan Amount",
                    rows = listOf(
                        ResultRow("Maximum Loan Amount", Format.money(maxLoan, s, d), true),
                        ResultRow("Monthly Payment", Format.money(payment, s, d)),
                        ResultRow("Total Paid", Format.money(total, s, d)),
                        ResultRow("Total Interest", Format.money(total - maxLoan, s, d))
                    ),
                    formula = "Max Loan = Payment × ((1+r)^n − 1) / (r(1+r)^n)",
                    breakdown = listOf(
                        "At ${Format.num(rate, d)}% over ${Format.num(years, 0)} years, a " +
                            "${Format.money(payment, s, d)} monthly payment supports a " +
                            "${Format.money(maxLoan, s, d)} loan."
                    ),
                    tip = "Lenders usually want your total housing payment under 28% of gross monthly income."
                )
            }
        ),
        CalculatorDef(
            id = "biweekly-mortgage", title = "Biweekly Mortgage Calculator",
            description = "Pay half your mortgage every 2 weeks and see how many years and how much interest you save.",
            category = Category.LOAN, icon = "schedule",
            inputs = listOf(
                CalcInput("amount", "Loan Amount ($)", defaultValue = "300000"),
                CalcInput("rate", "Interest Rate (%)", defaultValue = "6.5"),
                CalcInput("years", "Loan Term (years)", defaultValue = "30")
            ),
            compute = { v, s, d ->
                val amount = num(v, "amount")
                val rate = num(v, "rate")
                val years = num(v, "years")
                val monthly = emi(amount, rate, years)
                val r = rate / 100.0 / 12.0
                var balance = amount
                var months = 0
                var totalPaid = 0.0
                while (balance > 0.0 && months < 720) {
                    months++
                    balance *= (1 + r)
                    val pay = minOf(monthly, balance)
                    balance -= pay
                    totalPaid += pay
                    if (months % 12 == 0 && balance > 0.0) {
                        val extra = minOf(monthly, balance)
                        balance -= extra
                        totalPaid += extra
                    }
                }
                val normalTotal = monthly * years * 12
                val saved = (normalTotal - totalPaid).coerceAtLeast(0.0)
                val yearsSaved = (years * 12 - months) / 12.0
                CalcResult(
                    title = Format.money(saved, s, d), subtitle = "Interest Saved",
                    rows = listOf(
                        ResultRow("Monthly Payment", Format.money(monthly, s, d)),
                        ResultRow("Normal Payoff", "${Format.num(years * 12, 0)} months"),
                        ResultRow("Biweekly Payoff", "$months months", true),
                        ResultRow("Years Saved", "${Format.num(yearsSaved, 1)} years"),
                        ResultRow("Interest Saved", Format.money(saved, s, d), true)
                    ),
                    formula = "26 half-payments a year = 13 full monthly payments — one extra payment per year.",
                    breakdown = listOf(
                        "You make one extra full payment every year without feeling it.",
                        "Total paid biweekly: ${Format.money(totalPaid, s, d)} vs " +
                            "${Format.money(normalTotal, s, d)} on a normal schedule."
                    ),
                    tip = "Make sure your lender applies the extra payment to principal, not to future interest."
                )
            }
        )
    )

    // ------------------------------------------------------------------- TAX

    val tax: List<CalculatorDef> = listOf(
        CalculatorDef(
            id = "self-employment-tax", title = "Self-Employment Tax Calculator",
            description = "Estimate Social Security + Medicare tax on freelance or business income.",
            category = Category.TAX, icon = "tax",
            inputs = listOf(
                CalcInput("profit", "Net Business Profit ($)", defaultValue = "60000"),
                CalcInput("year", "Tax Year", type = InputType.SELECT,
                    options = TaxData.taxYears, defaultValue = "2026")
            ),
            compute = { v, s, d ->
                val profit = num(v, "profit")
                val year = str(v, "year", "2026").toIntOrNull() ?: 2026
                val netEarnings = profit * 0.9235
                val wageBase = if (year >= 2026) 184_500.0 else 176_100.0
                val ss = minOf(netEarnings, wageBase) * 0.124
                val medicare = netEarnings * 0.029 +
                    if (netEarnings > 200_000.0) (netEarnings - 200_000.0) * 0.009 else 0.0
                val total = ss + medicare
                CalcResult(
                    title = Format.money(total, s, d), subtitle = "Self-Employment Tax",
                    rows = listOf(
                        ResultRow("Self-Employment Tax", Format.money(total, s, d), true),
                        ResultRow("Social Security (12.4%)", Format.money(ss, s, d)),
                        ResultRow("Medicare (2.9% + 0.9% over $200k)", Format.money(medicare, s, d)),
                        ResultRow("Net Earnings (92.35% of profit)", Format.money(netEarnings, s, d)),
                        ResultRow("Deductible Half (adjustment to income)", Format.money(total / 2, s, d))
                    ),
                    formula = "SE Tax = 15.3% of 92.35% of net profit (Social Security up to the wage base + Medicare).",
                    breakdown = listOf(
                        "Net earnings are 92.35% of your profit — the IRS gives a 7.65% adjustment first.",
                        "Half of the SE tax is deductible from your income tax."
                    ),
                    tip = "Set aside 25–30% of every payment you receive so quarterly estimated tax never surprises you.",
                    disclaimer = EXTRA_TAX_NOTE
                )
            }
        ),
        CalculatorDef(
            id = "capital-gains-tax", title = "Capital Gains Tax Calculator",
            description = "Tax on selling stocks, property or crypto — short-term vs long-term rates.",
            category = Category.TAX, icon = "stock",
            inputs = listOf(
                CalcInput("buy", "Purchase Price ($)", defaultValue = "10000"),
                CalcInput("sell", "Sale Price ($)", defaultValue = "25000"),
                CalcInput("months", "Holding Period (months)", defaultValue = "18"),
                CalcInput("status", "Filing Status", type = InputType.SELECT,
                    options = TaxData.filingStatuses, defaultValue = "Single"),
                CalcInput("income", "Annual Taxable Income ($)", defaultValue = "75000")
            ),
            compute = { v, s, d ->
                val gain = num(v, "sell") - num(v, "buy")
                val months = num(v, "months")
                val status = str(v, "status", "Single")
                val income = num(v, "income")
                val shortTerm = months < 12
                val rate = if (shortTerm) {
                    TaxData.marginalRate(income, 2026, status)
                } else {
                    // 2026 IRS long-term thresholds (Rev. Proc. 2025-32, §4.03).
                    val t0 = when (status) {
                        "Married Filing Jointly" -> 98_900.0
                        "Married Filing Separately" -> 49_450.0
                        "Head of Household" -> 66_200.0
                        else -> 49_450.0
                    }
                    val t20 = when (status) {
                        "Married Filing Jointly" -> 613_700.0
                        "Married Filing Separately" -> 306_850.0
                        "Head of Household" -> 579_600.0
                        else -> 545_500.0
                    }
                    when {
                        income <= t0 -> 0.0
                        income <= t20 -> 0.15
                        else -> 0.20
                    }
                }
                val tax = gain.coerceAtLeast(0.0) * rate
                CalcResult(
                    title = Format.money(tax, s, d), subtitle = "Capital Gains Tax",
                    rows = listOf(
                        ResultRow("Capital Gain", Format.money(gain, s, d)),
                        ResultRow(
                            "Holding Period",
                            if (shortTerm) "Short-term (< 1 year)" else "Long-term (1+ year)"
                        ),
                        ResultRow("Tax Rate", Format.pct(rate * 100, d)),
                        ResultRow("Tax Owed", Format.money(tax, s, d), true),
                        ResultRow("After-Tax Proceeds", Format.money(num(v, "sell") - tax, s, d))
                    ),
                    formula = if (shortTerm) "Short-term gains are taxed at your ordinary marginal rate."
                    else "Long-term gains: 0% / 15% / 20% by income (2026 IRS thresholds).",
                    breakdown = listOf(
                        if (shortTerm) "Held under 12 months, so the gain is taxed like regular income."
                        else "Held 12+ months, so the lower long-term rate applies."
                    ),
                    tip = "Holding an asset just past 12 months can cut the tax rate dramatically.",
                    disclaimer = EXTRA_TAX_NOTE + " Long-term thresholds are the 2026 IRS figures (Rev. Proc. 2025-32)."
                )
            }
        ),
        CalculatorDef(
            id = "property-tax", title = "Property Tax Calculator",
            description = "Annual and monthly property tax from your home value and local tax rate.",
            category = Category.TAX, icon = "home",
            inputs = listOf(
                CalcInput("value", "Home Value ($)", defaultValue = "350000"),
                CalcInput("rate", "Property Tax Rate (%)", defaultValue = "1.1"),
                CalcInput("exempt", "Exemptions / Deductions ($)", defaultValue = "0")
            ),
            compute = { v, s, d ->
                val assessed = (num(v, "value") - num(v, "exempt")).coerceAtLeast(0.0)
                val annual = assessed * num(v, "rate") / 100.0
                CalcResult(
                    title = Format.money(annual, s, d), subtitle = "Annual Property Tax",
                    rows = listOf(
                        ResultRow("Annual Property Tax", Format.money(annual, s, d), true),
                        ResultRow("Monthly Property Tax", Format.money(annual / 12, s, d)),
                        ResultRow("Assessed Value", Format.money(assessed, s, d)),
                        ResultRow("Tax Rate", Format.pct(num(v, "rate"), d))
                    ),
                    formula = "Annual Tax = (Home Value − Exemptions) × Tax Rate",
                    breakdown = listOf(
                        "US property tax rates typically range from 0.3% to over 2% depending on the county."
                    ),
                    tip = "Many states offer a homestead exemption — check if yours lowers your assessed value.",
                    disclaimer = EXTRA_TAX_NOTE
                )
            }
        ),
        CalculatorDef(
            id = "bonus-tax", title = "Bonus Tax Calculator",
            description = "How much of your bonus survives federal, Social Security, Medicare and state tax?",
            category = Category.TAX, icon = "paycheck",
            inputs = listOf(
                CalcInput("bonus", "Bonus Amount ($)", defaultValue = "5000"),
                CalcInput("state", "State Tax Rate (%)", defaultValue = "5")
            ),
            compute = { v, s, d ->
                val bonus = num(v, "bonus")
                val fed = bonus * 0.22
                val ss = bonus * 0.062
                val med = bonus * 0.0145
                val state = bonus * num(v, "state") / 100.0
                val total = fed + ss + med + state
                CalcResult(
                    title = Format.money(bonus - total, s, d), subtitle = "Bonus Take-Home",
                    rows = listOf(
                        ResultRow("Bonus Take-Home", Format.money(bonus - total, s, d), true),
                        ResultRow("Federal Withholding (22%)", Format.money(fed, s, d)),
                        ResultRow("Social Security (6.2%)", Format.money(ss, s, d)),
                        ResultRow("Medicare (1.45%)", Format.money(med, s, d)),
                        ResultRow("State Tax", Format.money(state, s, d)),
                        ResultRow("Total Withheld", Format.money(total, s, d))
                    ),
                    formula = "Supplemental wages are withheld at a flat 22% federal rate, plus FICA and state tax.",
                    breakdown = listOf(
                        "The 22% is withholding, not your final tax — the real rate depends on your total income."
                    ),
                    tip = "If the withholding is too high, you get the difference back as a tax refund.",
                    disclaimer = EXTRA_TAX_NOTE
                )
            }
        ),
        CalculatorDef(
            id = "tax-refund-estimator", title = "Tax Refund Estimator",
            description = "Will you get a refund or owe money? Compare your federal tax with what was withheld.",
            category = Category.TAX, icon = "takehome",
            inputs = listOf(
                CalcInput("income", "Gross Annual Income ($)", defaultValue = "75000"),
                CalcInput("status", "Filing Status", type = InputType.SELECT,
                    options = TaxData.filingStatuses, defaultValue = "Single"),
                CalcInput("year", "Tax Year", type = InputType.SELECT,
                    options = TaxData.taxYears, defaultValue = "2026"),
                CalcInput("withheld", "Total Tax Withheld ($)", defaultValue = "9000")
            ),
            compute = { v, s, d ->
                val income = num(v, "income")
                val status = str(v, "status", "Single")
                val year = str(v, "year", "2026").toIntOrNull() ?: 2026
                val taxable = (income - TaxData.standardDeduction(year, status)).coerceAtLeast(0.0)
                val tax = TaxData.federalTax(taxable, year, status)
                val withheld = num(v, "withheld")
                val diff = withheld - tax
                CalcResult(
                    title = Format.money(kotlin.math.abs(diff), s, d),
                    subtitle = if (diff >= 0) "Estimated Refund" else "Amount You Owe",
                    rows = listOf(
                        ResultRow(
                            if (diff >= 0) "Estimated Refund" else "Amount You Owe",
                            Format.money(kotlin.math.abs(diff), s, d), true
                        ),
                        ResultRow("Federal Tax", Format.money(tax, s, d)),
                        ResultRow("Total Withheld", Format.money(withheld, s, d)),
                        ResultRow("Taxable Income", Format.money(taxable, s, d))
                    ),
                    formula = "Refund (or amount owed) = Tax Withheld − Federal Tax.",
                    breakdown = listOf(
                        if (diff >= 0) "You overpaid by ${Format.money(diff, s, d)} — that comes back as a refund."
                        else "You underpaid by ${Format.money(-diff, s, d)} — that is due when you file."
                    ),
                    tip = "A big refund means you gave the government an interest-free loan all year — adjust your W-4.",
                    disclaimer = EXTRA_TAX_NOTE
                )
            }
        )
    )

    // ------------------------------------------------------------- INVESTMENT

    val investment: List<CalculatorDef> = listOf(
        CalculatorDef(
            id = "future-value", title = "Future Value Calculator",
            description = "What will your savings be worth in the future, with monthly contributions?",
            category = Category.INVESTMENT, icon = "compound",
            inputs = listOf(
                CalcInput("present", "Current Amount ($)", defaultValue = "10000"),
                CalcInput("monthly", "Monthly Contribution ($)", defaultValue = "500"),
                CalcInput("rate", "Annual Return (%)", defaultValue = "7"),
                CalcInput("years", "Years", defaultValue = "20")
            ),
            compute = { v, s, d ->
                val present = num(v, "present")
                val monthly = num(v, "monthly")
                val rate = num(v, "rate")
                val years = num(v, "years")
                val fv = compound(present, rate, years, 1.0) + futureValueAnnuity(monthly, rate, years)
                val invested = present + monthly * years * 12
                CalcResult(
                    title = Format.money(fv, s, d), subtitle = "Future Value",
                    rows = listOf(
                        ResultRow("Future Value", Format.money(fv, s, d), true),
                        ResultRow("Total Invested", Format.money(invested, s, d)),
                        ResultRow("Growth", Format.money(fv - invested, s, d))
                    ),
                    formula = "FV = PV(1+r)^n + PMT × (((1+r)^n − 1) / r)",
                    breakdown = listOf(
                        "Starting amount grows to ${Format.money(compound(present, rate, years, 1.0), s, d)}.",
                        "Monthly contributions grow to ${Format.money(futureValueAnnuity(monthly, rate, years), s, d)}."
                    ),
                    chart = listOf(
                        ChartBar("Invested", invested),
                        ChartBar("Growth", fv - invested)
                    ),
                    tip = "Starting early beats saving more later — time in the market is the biggest lever."
                )
            }
        ),
        CalculatorDef(
            id = "present-value", title = "Present Value Calculator",
            description = "How much do you need to invest today to reach a future amount?",
            category = Category.INVESTMENT, icon = "simple",
            inputs = listOf(
                CalcInput("future", "Future Amount Needed ($)", defaultValue = "100000"),
                CalcInput("rate", "Annual Return (%)", defaultValue = "5"),
                CalcInput("years", "Years From Now", defaultValue = "10")
            ),
            compute = { v, s, d ->
                val future = num(v, "future")
                val rate = num(v, "rate")
                val years = num(v, "years")
                val pv = future / (1 + rate / 100.0).pow(years)
                CalcResult(
                    title = Format.money(pv, s, d), subtitle = "Amount to Invest Today",
                    rows = listOf(
                        ResultRow("Present Value", Format.money(pv, s, d), true),
                        ResultRow("Future Amount", Format.money(future, s, d)),
                        ResultRow("Discount (growth needed)", Format.money(future - pv, s, d))
                    ),
                    formula = "PV = FV / (1 + r)^n",
                    breakdown = listOf(
                        "Invest ${Format.money(pv, s, d)} today at ${Format.num(rate, d)}% and it grows to " +
                            "${Format.money(future, s, d)} in ${Format.num(years, 0)} years."
                    ),
                    tip = "Money today is worth more than the same money later — inflation and returns both work on it."
                )
            }
        ),
        CalculatorDef(
            id = "cd-calculator", title = "CD Calculator",
            description = "Maturity value and interest for a Certificate of Deposit.",
            category = Category.INVESTMENT, icon = "savings",
            inputs = listOf(
                CalcInput("deposit", "Deposit Amount ($)", defaultValue = "10000"),
                CalcInput("rate", "APY (%)", defaultValue = "4.5"),
                CalcInput("years", "Term (years)", defaultValue = "3"),
                CalcInput("compounding", "Compounding", type = InputType.SELECT,
                    options = listOf("Annually", "Semi-Annually", "Quarterly", "Monthly"),
                    defaultValue = "Monthly")
            ),
            compute = { v, s, d ->
                val deposit = num(v, "deposit")
                val rate = num(v, "rate")
                val years = num(v, "years")
                val n = when (str(v, "compounding", "Monthly")) {
                    "Annually" -> 1.0
                    "Semi-Annually" -> 2.0
                    "Quarterly" -> 4.0
                    else -> 12.0
                }
                val maturity = compound(deposit, rate, years, n)
                CalcResult(
                    title = Format.money(maturity, s, d), subtitle = "Maturity Value",
                    rows = listOf(
                        ResultRow("Maturity Value", Format.money(maturity, s, d), true),
                        ResultRow("Interest Earned", Format.money(maturity - deposit, s, d)),
                        ResultRow("Deposit", Format.money(deposit, s, d)),
                        ResultRow("APY", Format.pct(rate, d))
                    ),
                    formula = "A = P(1 + r/n)^(n×t)",
                    breakdown = listOf(
                        "Compounding ${str(v, "compounding", "Monthly").lowercase()} over " +
                            "${Format.num(years, 0)} years at ${Format.num(rate, d)}% APY."
                    ),
                    tip = "CDs are FDIC-insured up to $250,000 — safe, but your money is locked for the term."
                )
            }
        ),
        CalculatorDef(
            id = "annuity", title = "Annuity Calculator",
            description = "Future value of regular payments, and the monthly income it can pay.",
            category = Category.INVESTMENT, icon = "retirement",
            inputs = listOf(
                CalcInput("monthly", "Monthly Payment ($)", defaultValue = "500"),
                CalcInput("rate", "Annual Return (%)", defaultValue = "6"),
                CalcInput("years", "Years of Payments", defaultValue = "20")
            ),
            compute = { v, s, d ->
                val monthly = num(v, "monthly")
                val rate = num(v, "rate")
                val years = num(v, "years")
                val fv = futureValueAnnuity(monthly, rate, years)
                val r = rate / 100.0 / 12.0
                val n = years * 12.0
                val payout = if (r == 0.0) fv / n else fv * r / (1 - (1 + r).pow(-n))
                CalcResult(
                    title = Format.money(fv, s, d), subtitle = "Future Value",
                    rows = listOf(
                        ResultRow("Future Value", Format.money(fv, s, d), true),
                        ResultRow("Total Paid In", Format.money(monthly * n, s, d)),
                        ResultRow("Monthly Payout (over ${Format.num(years, 0)} yrs)", Format.money(payout, s, d)),
                        ResultRow("Growth", Format.money(fv - monthly * n, s, d))
                    ),
                    formula = "FV = PMT × (((1+r)^n − 1) / r)",
                    breakdown = listOf(
                        "Paying ${Format.money(monthly, s, d)} a month for ${Format.num(years, 0)} years " +
                            "at ${Format.num(rate, d)}% builds ${Format.money(fv, s, d)}."
                    ),
                    tip = "Annuities trade liquidity for guaranteed income — read the fees before buying one."
                )
            }
        ),
        CalculatorDef(
            id = "crypto-profit", title = "Crypto Profit Calculator",
            description = "Profit, loss and ROI on a cryptocurrency trade, after fees.",
            category = Category.INVESTMENT, icon = "return",
            inputs = listOf(
                CalcInput("coins", "Amount of Crypto", defaultValue = "0.5"),
                CalcInput("buy", "Buy Price per Coin ($)", defaultValue = "40000"),
                CalcInput("sell", "Sell Price per Coin ($)", defaultValue = "60000"),
                CalcInput("fees", "Total Fees ($)", defaultValue = "100")
            ),
            compute = { v, s, d ->
                val coins = num(v, "coins")
                val invested = coins * num(v, "buy")
                val proceeds = coins * num(v, "sell") - num(v, "fees")
                val profit = proceeds - invested
                val roi = if (invested > 0) profit / invested * 100 else 0.0
                CalcResult(
                    title = Format.money(profit, s, d),
                    subtitle = if (profit >= 0) "Profit" else "Loss",
                    rows = listOf(
                        ResultRow(if (profit >= 0) "Profit" else "Loss", Format.money(profit, s, d), true),
                        ResultRow("Amount Invested", Format.money(invested, s, d)),
                        ResultRow("Proceeds After Fees", Format.money(proceeds, s, d)),
                        ResultRow("Return on Investment", Format.pct(roi, d))
                    ),
                    formula = "Profit = (Sell Price − Buy Price) × Coins − Fees",
                    breakdown = listOf(
                        "You bought ${Format.num(coins, 4)} coins for ${Format.money(invested, s, d)} " +
                            "and sold them for ${Format.money(proceeds, s, d)} after fees."
                    ),
                    tip = "Crypto is volatile — never invest money you cannot afford to lose, and track fees on both sides.",
                    disclaimer = "Estimate only — taxes on crypto gains depend on your country and holding period."
                )
            }
        )
    )

    // ---------------------------------------------------------- PERSONAL FINANCE

    val personal: List<CalculatorDef> = listOf(
        CalculatorDef(
            id = "rent-vs-buy", title = "Rent vs Buy Calculator",
            description = "Compare renting with buying a home over the years you plan to stay.",
            category = Category.PERSONAL, icon = "home",
            inputs = listOf(
                CalcInput("price", "Home Price ($)", defaultValue = "350000"),
                CalcInput("down", "Down Payment ($)", defaultValue = "70000"),
                CalcInput("rate", "Mortgage Rate (%)", defaultValue = "6.5"),
                CalcInput("years", "Mortgage Term (years)", defaultValue = "30"),
                CalcInput("rent", "Monthly Rent ($)", defaultValue = "1800"),
                CalcInput("compare", "Years to Compare", defaultValue = "5"),
                CalcInput("other", "Annual Tax + Insurance + Maintenance ($)", defaultValue = "6000")
            ),
            compute = { v, s, d ->
                val price = num(v, "price")
                val down = num(v, "down")
                val loanAmt = price - down
                val rate = num(v, "rate")
                val years = num(v, "years")
                val rent = num(v, "rent")
                val compare = num(v, "compare")
                val other = num(v, "other")
                val mortgage = emi(loanAmt, rate, years)
                val r = rate / 100.0 / 12.0
                val n = compare * 12.0
                val balanceAfter = loanAmt * (1 + r).pow(n) -
                    mortgage * (((1 + r).pow(n) - 1) / r)
                val principalPaid = loanAmt - balanceAfter
                val owningCost = mortgage * n + (other / 12) * n - principalPaid
                val rentingCost = rent * n
                val diff = kotlin.math.abs(owningCost - rentingCost)
                val buyingCheaper = owningCost < rentingCost
                CalcResult(
                    title = Format.money(diff, s, d),
                    subtitle = if (buyingCheaper) "Buying Saves You (over ${Format.num(compare, 0)} yrs)"
                    else "Renting Saves You (over ${Format.num(compare, 0)} yrs)",
                    rows = listOf(
                        ResultRow("Monthly Mortgage", Format.money(mortgage, s, d)),
                        ResultRow("Monthly Owning Cost", Format.money(mortgage + other / 12, s, d)),
                        ResultRow("${Format.num(compare, 0)}-Year Owning Cost", Format.money(owningCost, s, d)),
                        ResultRow("${Format.num(compare, 0)}-Year Renting Cost", Format.money(rentingCost, s, d)),
                        ResultRow(
                            "Verdict",
                            if (buyingCheaper) "Buying is cheaper" else "Renting is cheaper", true
                        )
                    ),
                    formula = "Owning cost = mortgage + taxes/insurance/maintenance − principal paid. Renting cost = rent × months.",
                    breakdown = listOf(
                        "After ${Format.num(compare, 0)} years you would have paid " +
                            "${Format.money(principalPaid, s, d)} of principal on the mortgage.",
                        "The down payment (${Format.money(down, s, d)}) is not counted as a cost — it becomes home equity."
                    ),
                    tip = "Buying usually wins if you stay 5+ years; renting wins if you may move soon."
                )
            }
        ),
        CalculatorDef(
            id = "house-affordability", title = "House Affordability Calculator",
            description = "How much house can you afford? Based on the 28/36 rule lenders use.",
            category = Category.PERSONAL, icon = "home",
            inputs = listOf(
                CalcInput("income", "Annual Gross Income ($)", defaultValue = "90000"),
                CalcInput("debts", "Monthly Debt Payments ($)", defaultValue = "500"),
                CalcInput("down", "Down Payment ($)", defaultValue = "50000"),
                CalcInput("rate", "Mortgage Rate (%)", defaultValue = "6.5"),
                CalcInput("years", "Mortgage Term (years)", defaultValue = "30")
            ),
            compute = { v, s, d ->
                val income = num(v, "income")
                val debts = num(v, "debts")
                val down = num(v, "down")
                val rate = num(v, "rate")
                val years = num(v, "years")
                val maxHousing = income / 12 * 0.28
                val maxTotalDebt = income / 12 * 0.36
                val avail = maxOf(0.0, minOf(maxHousing, maxTotalDebt - debts))
                val r = rate / 100.0 / 12.0
                val n = years * 12.0
                val maxLoan = if (r == 0.0) avail * n
                else avail * ((1 + r).pow(n) - 1) / (r * (1 + r).pow(n))
                CalcResult(
                    title = Format.money(maxLoan + down, s, d), subtitle = "Maximum Home Price",
                    rows = listOf(
                        ResultRow("Maximum Home Price", Format.money(maxLoan + down, s, d), true),
                        ResultRow("Maximum Loan Amount", Format.money(maxLoan, s, d)),
                        ResultRow("Max Monthly Housing Payment", Format.money(avail, s, d)),
                        ResultRow("Down Payment", Format.money(down, s, d))
                    ),
                    formula = "28% rule: housing ≤ 28% of gross monthly income. 36% rule: all debts ≤ 36%.",
                    breakdown = listOf(
                        "Lenders want housing under ${Format.money(maxHousing, s, d)} a month and all debts under " +
                            "${Format.money(maxTotalDebt, s, d)} a month on your income."
                    ),
                    tip = "Taxes and insurance are part of the housing payment — budget for them too."
                )
            }
        ),
        CalculatorDef(
            id = "car-affordability", title = "Car Affordability Calculator",
            description = "How expensive a car can you buy for a monthly payment you can afford?",
            category = Category.PERSONAL, icon = "car",
            inputs = listOf(
                CalcInput("payment", "Max Monthly Payment ($)", defaultValue = "500"),
                CalcInput("down", "Down Payment ($)", defaultValue = "5000"),
                CalcInput("rate", "Interest Rate (%)", defaultValue = "7"),
                CalcInput("years", "Loan Term (years)", defaultValue = "5")
            ),
            compute = { v, s, d ->
                val payment = num(v, "payment")
                val down = num(v, "down")
                val rate = num(v, "rate")
                val years = num(v, "years")
                val r = rate / 100.0 / 12.0
                val n = years * 12.0
                val maxLoan = if (r == 0.0) payment * n
                else payment * ((1 + r).pow(n) - 1) / (r * (1 + r).pow(n))
                CalcResult(
                    title = Format.money(maxLoan + down, s, d), subtitle = "Maximum Car Price",
                    rows = listOf(
                        ResultRow("Maximum Car Price", Format.money(maxLoan + down, s, d), true),
                        ResultRow("Maximum Loan Amount", Format.money(maxLoan, s, d)),
                        ResultRow("Monthly Payment", Format.money(payment, s, d)),
                        ResultRow("Total Interest", Format.money(payment * n - maxLoan, s, d))
                    ),
                    formula = "Max Loan = Payment × ((1+r)^n − 1) / (r(1+r)^n), plus your down payment.",
                    breakdown = listOf(
                        "A ${Format.money(payment, s, d)} payment at ${Format.num(rate, d)}% over " +
                            "${Format.num(years, 0)} years supports a ${Format.money(maxLoan, s, d)} loan."
                    ),
                    tip = "The 20/4/10 rule: 20% down, 4-year loan max, payment under 10% of gross income."
                )
            }
        ),
        CalculatorDef(
            id = "vacation-savings", title = "Vacation Savings Calculator",
            description = "How much to save each month and week to reach your trip budget on time.",
            category = Category.PERSONAL, icon = "goal",
            inputs = listOf(
                CalcInput("cost", "Trip Cost ($)", defaultValue = "3000"),
                CalcInput("months", "Months Until Trip", defaultValue = "6"),
                CalcInput("saved", "Already Saved ($)", defaultValue = "500")
            ),
            compute = { v, s, d ->
                val needed = (num(v, "cost") - num(v, "saved")).coerceAtLeast(0.0)
                val months = num(v, "months").coerceAtLeast(1.0)
                val monthly = needed / months
                CalcResult(
                    title = Format.money(monthly, s, d), subtitle = "Save Per Month",
                    rows = listOf(
                        ResultRow("Save Per Month", Format.money(monthly, s, d), true),
                        ResultRow("Save Per Week", Format.money(monthly / 4.33, s, d)),
                        ResultRow("Still Needed", Format.money(needed, s, d)),
                        ResultRow("Trip Cost", Format.money(num(v, "cost"), s, d))
                    ),
                    formula = "Monthly Saving = (Trip Cost − Already Saved) / Months",
                    breakdown = listOf(
                        "Put ${Format.money(monthly, s, d)} aside every month and you reach " +
                            "${Format.money(num(v, "cost"), s, d)} in ${Format.num(months, 0)} months."
                    ),
                    tip = "Open a separate savings account for the trip so the money does not get spent."
                )
            }
        ),
        CalculatorDef(
            id = "wedding-budget", title = "Wedding Budget Calculator",
            description = "Split your wedding budget across venue, catering, photos and more.",
            category = Category.PERSONAL, icon = "budget",
            inputs = listOf(
                CalcInput("total", "Total Wedding Budget ($)", defaultValue = "20000")
            ),
            compute = { v, s, d ->
                val total = num(v, "total")
                val venue = total * 0.40
                val catering = total * 0.25
                val photo = total * 0.10
                val attire = total * 0.08
                val music = total * 0.05
                val flowers = total * 0.07
                val misc = total * 0.05
                CalcResult(
                    title = Format.money(total, s, d), subtitle = "Total Wedding Budget",
                    rows = listOf(
                        ResultRow("Venue (40%)", Format.money(venue, s, d)),
                        ResultRow("Catering (25%)", Format.money(catering, s, d)),
                        ResultRow("Photography (10%)", Format.money(photo, s, d)),
                        ResultRow("Attire (8%)", Format.money(attire, s, d)),
                        ResultRow("Flowers & Decor (7%)", Format.money(flowers, s, d)),
                        ResultRow("Music (5%)", Format.money(music, s, d)),
                        ResultRow("Miscellaneous (5%)", Format.money(misc, s, d))
                    ),
                    formula = "Standard split: Venue 40%, Catering 25%, Photography 10%, Attire 8%, Flowers 7%, Music 5%, Misc 5%.",
                    breakdown = listOf(
                        "The venue and catering together take about two-thirds of most wedding budgets."
                    ),
                    chart = listOf(
                        ChartBar("Venue", venue), ChartBar("Catering", catering),
                        ChartBar("Photo", photo), ChartBar("Attire", attire),
                        ChartBar("Flowers", flowers), ChartBar("Music", music),
                        ChartBar("Misc", misc)
                    ),
                    tip = "Keep a 5–10% buffer for surprise costs — every wedding has them."
                )
            }
        )
    )

    // ---------------------------------------------------------------- BUSINESS

    val business: List<CalculatorDef> = listOf(
        CalculatorDef(
            id = "npv", title = "NPV Calculator",
            description = "Net Present Value — is this project worth the investment?",
            category = Category.BUSINESS, icon = "return",
            inputs = listOf(
                CalcInput("initial", "Initial Investment ($)", defaultValue = "50000"),
                CalcInput("cashflow", "Annual Cash Flow ($)", defaultValue = "12000"),
                CalcInput("rate", "Discount Rate (%)", defaultValue = "10"),
                CalcInput("years", "Project Life (years)", defaultValue = "5")
            ),
            compute = { v, s, d ->
                val initial = num(v, "initial")
                val cf = num(v, "cashflow")
                val rate = num(v, "rate") / 100.0
                val years = num(v, "years").toInt()
                var pvSum = 0.0
                for (t in 1..years) pvSum += cf / (1 + rate).pow(t.toDouble())
                val npv = pvSum - initial
                CalcResult(
                    title = Format.money(npv, s, d), subtitle = "Net Present Value",
                    rows = listOf(
                        ResultRow("Net Present Value", Format.money(npv, s, d), true),
                        ResultRow("Present Value of Inflows", Format.money(pvSum, s, d)),
                        ResultRow("Initial Investment", Format.money(initial, s, d)),
                        ResultRow(
                            "Verdict",
                            if (npv > 0) "Accept — adds value" else if (npv == 0.0) "Break-even"
                            else "Reject — destroys value"
                        )
                    ),
                    formula = "NPV = Σ (Cash Flow / (1+r)^t) − Initial Investment",
                    breakdown = listOf(
                        "Future cash flows are worth less today, so each year is discounted at " +
                            "${Format.num(num(v, "rate"), d)}%."
                    ),
                    tip = "A positive NPV means the project earns more than its cost of capital."
                )
            }
        ),
        CalculatorDef(
            id = "payback-period", title = "Payback Period Calculator",
            description = "How long until an investment pays for itself?",
            category = Category.BUSINESS, icon = "schedule",
            inputs = listOf(
                CalcInput("initial", "Initial Investment ($)", defaultValue = "50000"),
                CalcInput("cashflow", "Annual Cash Flow ($)", defaultValue = "12000")
            ),
            compute = { v, s, d ->
                val initial = num(v, "initial")
                val cf = num(v, "cashflow")
                val years = if (cf > 0) initial / cf else 0.0
                CalcResult(
                    title = "${Format.num(years, 1)} years", subtitle = "Payback Period",
                    rows = listOf(
                        ResultRow("Payback Period", "${Format.num(years, 1)} years", true),
                        ResultRow("Payback in Months", "${Format.num(years * 12, 0)} months"),
                        ResultRow("Initial Investment", Format.money(initial, s, d)),
                        ResultRow("Annual Cash Flow", Format.money(cf, s, d))
                    ),
                    formula = "Payback Period = Initial Investment / Annual Cash Flow",
                    breakdown = listOf(
                        "At ${Format.money(cf, s, d)} a year, the ${Format.money(initial, s, d)} " +
                            "investment is recovered in ${Format.num(years, 1)} years."
                    ),
                    tip = "Payback ignores the time value of money — use NPV for the full picture."
                )
            }
        ),
        CalculatorDef(
            id = "operating-margin", title = "Operating Margin Calculator",
            description = "Operating income and margin from revenue, costs and expenses.",
            category = Category.BUSINESS, icon = "margin",
            inputs = listOf(
                CalcInput("revenue", "Revenue ($)", defaultValue = "500000"),
                CalcInput("cogs", "Cost of Goods Sold ($)", defaultValue = "300000"),
                CalcInput("opex", "Operating Expenses ($)", defaultValue = "120000")
            ),
            compute = { v, s, d ->
                val revenue = num(v, "revenue")
                val gross = revenue - num(v, "cogs")
                val opIncome = gross - num(v, "opex")
                val margin = if (revenue > 0) opIncome / revenue * 100 else 0.0
                CalcResult(
                    title = Format.pct(margin, d), subtitle = "Operating Margin",
                    rows = listOf(
                        ResultRow("Operating Margin", Format.pct(margin, d), true),
                        ResultRow("Operating Income", Format.money(opIncome, s, d)),
                        ResultRow("Gross Profit", Format.money(gross, s, d)),
                        ResultRow("Revenue", Format.money(revenue, s, d))
                    ),
                    formula = "Operating Income = Revenue − COGS − Operating Expenses. Margin = Operating Income / Revenue.",
                    breakdown = listOf(
                        "Every ${Format.money(100.0, s, d)} of sales leaves " +
                            "${Format.money(opIncome / revenue * 100, s, d)} of operating profit."
                    ),
                    tip = "A rising operating margin means the business is getting more efficient, not just bigger."
                )
            }
        ),
        CalculatorDef(
            id = "working-capital", title = "Working Capital Calculator",
            description = "Can the business pay its short-term bills? Working capital and current ratio.",
            category = Category.BUSINESS, icon = "cashflow",
            inputs = listOf(
                CalcInput("assets", "Current Assets ($)", defaultValue = "150000"),
                CalcInput("liabilities", "Current Liabilities ($)", defaultValue = "90000")
            ),
            compute = { v, s, d ->
                val assets = num(v, "assets")
                val liabilities = num(v, "liabilities")
                val wc = assets - liabilities
                val ratio = if (liabilities > 0) assets / liabilities else 0.0
                CalcResult(
                    title = Format.money(wc, s, d), subtitle = "Working Capital",
                    rows = listOf(
                        ResultRow("Working Capital", Format.money(wc, s, d), true),
                        ResultRow("Current Ratio", Format.num(ratio, d)),
                        ResultRow("Current Assets", Format.money(assets, s, d)),
                        ResultRow("Current Liabilities", Format.money(liabilities, s, d))
                    ),
                    formula = "Working Capital = Current Assets − Current Liabilities. Current Ratio = Assets / Liabilities.",
                    breakdown = listOf(
                        if (ratio >= 1.5) "A current ratio of ${Format.num(ratio, d)} is healthy — the business can cover its bills."
                        else if (ratio >= 1.0) "A current ratio of ${Format.num(ratio, d)} is tight — watch cash flow closely."
                        else "A current ratio below 1.0 means short-term bills exceed short-term assets — a warning sign."
                    ),
                    tip = "Too much working capital can also be a problem — idle cash earns nothing."
                )
            }
        ),
        CalculatorDef(
            id = "dscr", title = "Debt Service Coverage Ratio",
            description = "Can the business cover its loan payments? The DSCR lenders check.",
            category = Category.BUSINESS, icon = "businessloan",
            inputs = listOf(
                CalcInput("noi", "Net Operating Income ($ per year)", defaultValue = "120000"),
                CalcInput("debt", "Annual Debt Service ($ per year)", defaultValue = "80000")
            ),
            compute = { v, s, d ->
                val noi = num(v, "noi")
                val debt = num(v, "debt")
                val dscr = if (debt > 0) noi / debt else 0.0
                val verdict = when {
                    dscr >= 1.25 -> "Strong — most lenders want 1.25 or higher"
                    dscr >= 1.0 -> "Acceptable — income covers debt, with little cushion"
                    else -> "Weak — income does not cover debt payments"
                }
                CalcResult(
                    title = Format.num(dscr, d), subtitle = "Debt Service Coverage Ratio",
                    rows = listOf(
                        ResultRow("DSCR", Format.num(dscr, d), true),
                        ResultRow("Net Operating Income", Format.money(noi, s, d)),
                        ResultRow("Annual Debt Service", Format.money(debt, s, d)),
                        ResultRow("Verdict", verdict)
                    ),
                    formula = "DSCR = Net Operating Income / Annual Debt Service",
                    breakdown = listOf(
                        "A DSCR of ${Format.num(dscr, d)} means the business earns " +
                            "${Format.num(dscr, d)}× its annual loan payments."
                    ),
                    tip = "Lenders usually require a DSCR of at least 1.25 before approving a business loan."
                )
            }
        )
    )

    // ------------------------------------------------------------------- DAILY

    private fun parseTime(text: String): Int? {
        val parts = text.trim().split(":")
        if (parts.size != 2) return null
        val h = parts[0].toIntOrNull() ?: return null
        val m = parts[1].toIntOrNull() ?: return null
        if (h !in 0..23 || m !in 0..59) return null
        return h * 60 + m
    }

    val daily: List<CalculatorDef> = listOf(
        CalculatorDef(
            id = "fuel-cost", title = "Fuel Cost Calculator",
            description = "Trip fuel cost from distance, mileage and fuel price.",
            category = Category.DAILY, icon = "car",
            inputs = listOf(
                CalcInput("distance", "Distance (km)", defaultValue = "100"),
                CalcInput("mileage", "Mileage (km per litre)", defaultValue = "15"),
                CalcInput("price", "Fuel Price (per litre)", defaultValue = "2.5")
            ),
            compute = { v, s, d ->
                val distance = num(v, "distance")
                val mileage = num(v, "mileage")
                val price = num(v, "price")
                val fuel = if (mileage > 0) distance / mileage else 0.0
                val cost = fuel * price
                CalcResult(
                    title = Format.money(cost, s, d), subtitle = "Trip Fuel Cost",
                    rows = listOf(
                        ResultRow("Trip Fuel Cost", Format.money(cost, s, d), true),
                        ResultRow("Fuel Needed", "${Format.num(fuel, 2)} litres"),
                        ResultRow("Cost per km", Format.money(if (distance > 0) cost / distance else 0.0, s, d)),
                        ResultRow("Distance", "${Format.num(distance, 0)} km")
                    ),
                    formula = "Fuel = Distance / Mileage. Cost = Fuel × Price per litre.",
                    breakdown = listOf(
                        "A ${Format.num(distance, 0)} km trip at ${Format.num(mileage, 1)} km/l uses " +
                            "${Format.num(fuel, 2)} litres."
                    ),
                    tip = "Steady speeds and correct tyre pressure can improve mileage by 10% or more."
                )
            }
        ),
        CalculatorDef(
            id = "electricity-bill", title = "Electricity Bill Calculator",
            description = "Monthly electricity cost for any appliance from its watts and usage.",
            category = Category.DAILY, icon = "unit",
            inputs = listOf(
                CalcInput("watts", "Appliance Power (watts)", defaultValue = "1500"),
                CalcInput("hours", "Hours Used Per Day", defaultValue = "4"),
                CalcInput("days", "Days Per Month", defaultValue = "30"),
                CalcInput("rate", "Electricity Rate (per kWh)", defaultValue = "0.15")
            ),
            compute = { v, s, d ->
                val kwh = num(v, "watts") * num(v, "hours") * num(v, "days") / 1000.0
                val cost = kwh * num(v, "rate")
                CalcResult(
                    title = Format.money(cost, s, d), subtitle = "Monthly Cost",
                    rows = listOf(
                        ResultRow("Monthly Cost", Format.money(cost, s, d), true),
                        ResultRow("Energy Used", "${Format.num(kwh, 2)} kWh"),
                        ResultRow("Daily Cost", Format.money(cost / num(v, "days").coerceAtLeast(1.0), s, d)),
                        ResultRow("Rate", "${Format.money(num(v, "rate"), s, d)} per kWh")
                    ),
                    formula = "kWh = Watts × Hours × Days / 1000. Cost = kWh × Rate.",
                    breakdown = listOf(
                        "A ${Format.num(num(v, "watts"), 0)} W appliance running " +
                            "${Format.num(num(v, "hours"), 1)} hours a day uses ${Format.num(kwh, 2)} kWh a month."
                    ),
                    tip = "The biggest home energy users are usually heating, cooling and water heating."
                )
            }
        ),
        CalculatorDef(
            id = "bmi", title = "BMI Calculator",
            description = "Body Mass Index from your weight and height, with the health category.",
            category = Category.DAILY, icon = "simple",
            inputs = listOf(
                CalcInput("weight", "Weight (kg)", defaultValue = "75"),
                CalcInput("height", "Height (cm)", defaultValue = "175")
            ),
            compute = { v, s, d ->
                val weight = num(v, "weight")
                val h = num(v, "height") / 100.0
                val bmi = if (h > 0) weight / (h * h) else 0.0
                val category = when {
                    bmi < 18.5 -> "Underweight"
                    bmi < 25.0 -> "Normal weight"
                    bmi < 30.0 -> "Overweight"
                    else -> "Obese"
                }
                val low = 18.5 * h * h
                val high = 24.9 * h * h
                CalcResult(
                    title = Format.num(bmi, 1), subtitle = "Body Mass Index — $category",
                    rows = listOf(
                        ResultRow("BMI", Format.num(bmi, 1), true),
                        ResultRow("Category", category),
                        ResultRow("Healthy Weight Range", "${Format.num(low, 1)} – ${Format.num(high, 1)} kg"),
                        ResultRow("Your Weight", "${Format.num(weight, 1)} kg")
                    ),
                    formula = "BMI = Weight (kg) / Height (m)²",
                    breakdown = listOf(
                        "Underweight: below 18.5 · Normal: 18.5–24.9 · Overweight: 25–29.9 · Obese: 30 and above."
                    ),
                    tip = "BMI is a screening tool, not a diagnosis — muscle mass and body type also matter.",
                    disclaimer = "For general information only — not medical advice. Talk to a doctor about your health."
                )
            }
        ),
        CalculatorDef(
            id = "speed-distance-time", title = "Speed, Distance & Time Calculator",
            description = "How long will the trip take? From distance and speed.",
            category = Category.DAILY, icon = "time",
            inputs = listOf(
                CalcInput("distance", "Distance (km)", defaultValue = "120"),
                CalcInput("speed", "Speed (km per hour)", defaultValue = "60")
            ),
            compute = { v, s, d ->
                val distance = num(v, "distance")
                val speed = num(v, "speed")
                val hours = if (speed > 0) distance / speed else 0.0
                val pace = if (speed > 0) 60.0 / speed else 0.0
                CalcResult(
                    title = "${Format.num(hours, 2)} hrs", subtitle = "Travel Time",
                    rows = listOf(
                        ResultRow("Travel Time", "${Format.num(hours, 2)} hours (${Format.num(hours * 60, 0)} min)", true),
                        ResultRow("Distance", "${Format.num(distance, 0)} km"),
                        ResultRow("Speed", "${Format.num(speed, 0)} km/h"),
                        ResultRow("Pace", "${Format.num(pace, 1)} min per km")
                    ),
                    formula = "Time = Distance / Speed",
                    breakdown = listOf(
                        "At ${Format.num(speed, 0)} km/h, ${Format.num(distance, 0)} km takes " +
                            "${Format.num(hours, 2)} hours."
                    ),
                    tip = "Add 10–15% extra time for stops, traffic and fuel breaks on long trips."
                )
            }
        ),
        CalculatorDef(
            id = "work-hours", title = "Work Hours Calculator",
            description = "Hours worked between start and end time, minus your break.",
            category = Category.DAILY, icon = "overtime",
            inputs = listOf(
                CalcInput("start", "Start Time (HH:MM)", type = InputType.TEXT, defaultValue = "09:00"),
                CalcInput("end", "End Time (HH:MM)", type = InputType.TEXT, defaultValue = "17:30"),
                CalcInput("break", "Break (minutes)", defaultValue = "30")
            ),
            compute = { v, s, d ->
                val start = parseTime(str(v, "start", "09:00"))
                val end = parseTime(str(v, "end", "17:30"))
                val breakMin = num(v, "break")
                val worked = if (start != null && end != null) {
                    var diff = end - start
                    if (diff < 0) diff += 24 * 60
                    (diff - breakMin).coerceAtLeast(0.0)
                } else 0.0
                val hours = worked / 60.0
                CalcResult(
                    title = "${Format.num(hours, 2)} hours", subtitle = "Hours Worked",
                    rows = listOf(
                        ResultRow("Hours Worked", "${Format.num(hours, 2)} hours", true),
                        ResultRow("Total Minutes", "${Format.num(worked, 0)} min"),
                        ResultRow("Start Time", str(v, "start", "09:00")),
                        ResultRow("End Time", str(v, "end", "17:30")),
                        ResultRow("Break", "${Format.num(breakMin, 0)} min")
                    ),
                    formula = "Hours Worked = (End Time − Start Time) − Break",
                    breakdown = listOf(
                        if (start == null || end == null)
                            "Times must be in 24-hour HH:MM format, e.g. 09:00 and 17:30."
                        else "From ${str(v, "start", "")} to ${str(v, "end", "")} minus a " +
                            "${Format.num(breakMin, 0)}-minute break."
                    ),
                    tip = "Overnight shifts work too — if the end time is before the start time, it counts as next day."
                )
            }
        )
    )

    /** All 30 extra calculators (5 per category). */
    val all: List<CalculatorDef> = loan + tax + investment + personal + business + daily
}
