package com.fincalc.app.data

import com.fincalc.app.util.Format

/**
 * Category 1 — Loan & Mortgage Calculators (10).
 * All formulas verified against the standard amortisation formula:
 *   M = P × r(1+r)^n / ((1+r)^n − 1),  r = annual rate / 12, n = years × 12.
 */
object CalculatorsLoan {

    private fun loanResult(
        principal: Double, rate: Double, years: Double,
        s: String, d: Int, headlineLabel: String,
        extraRows: List<ResultRow> = emptyList(),
        tip: String? = null
    ): CalcResult {
        val monthly = emi(principal, rate, years)
        val n = years * 12.0
        val total = monthly * n
        val interest = total - principal
        val rows = mutableListOf(
            ResultRow("Monthly Payment", Format.money(monthly, s, d), highlight = true),
            ResultRow("Total Interest", Format.money(interest, s, d)),
            ResultRow("Total Payment", Format.money(total, s, d)),
            ResultRow("Loan Amount", Format.money(principal, s, d)),
            ResultRow("Loan Duration", "${Format.num(years, 0)} years (${Format.whole(n)} payments)")
        )
        rows.addAll(extraRows)
        return CalcResult(
            title = Format.money(monthly, s, d),
            subtitle = headlineLabel,
            rows = rows,
            formula = "M = P × r(1 + r)ⁿ / ((1 + r)ⁿ − 1)  —  P = ${Format.money(principal, s, d)}, " +
                "r = ${Format.num(rate, d)}% ÷ 12, n = ${Format.whole(n)} months",
            breakdown = listOf(
                "Principal: ${Format.money(principal, s, d)}",
                "Annual interest rate: ${Format.num(rate, d)}%",
                "Term: ${Format.num(years, 0)} years",
                "Monthly payment: ${Format.money(monthly, s, d)}",
                "Total interest: ${Format.money(interest, s, d)}",
                "Total of all payments: ${Format.money(total, s, d)}"
            ),
            chart = listOf(
                ChartBar("Principal", principal),
                ChartBar("Interest", interest)
            ),
            tip = tip
        )
    }

    val all: List<CalculatorDef> = listOf(
        CalculatorDef(
            id = "personal-loan", title = "Personal Loan Calculator",
            description = "Monthly payment, total interest and total cost of a personal loan.",
            category = Category.LOAN, icon = "loan", popular = true,
            inputs = listOf(
                CalcInput("amount", "Loan Amount ($)", defaultValue = "25000"),
                CalcInput("rate", "Annual Interest Rate (%)", defaultValue = "6.5"),
                CalcInput("years", "Loan Term (Years)", type = InputType.SELECT,
                    options = listOf("1", "2", "3", "4", "5", "6", "7", "8", "10", "12", "15", "20", "25", "30"),
                    defaultValue = "5")
            ),
            compute = { v, s, d ->
                loanResult(
                    num(v, "amount"), num(v, "rate"), num(v, "years"), s, d,
                    "Monthly Payment",
                    tip = "A shorter term raises the monthly payment but cuts total interest."
                )
            }
        ),
        CalculatorDef(
            id = "home-loan", title = "Home Loan Calculator",
            description = "Monthly payment for a home loan after your down payment.",
            category = Category.LOAN, icon = "home",
            inputs = listOf(
                CalcInput("price", "Home Price ($)", defaultValue = "350000"),
                CalcInput("down", "Down Payment ($)", defaultValue = "70000"),
                CalcInput("rate", "Annual Interest Rate (%)", defaultValue = "6.5"),
                CalcInput("years", "Loan Term (Years)", type = InputType.SELECT,
                    options = listOf("10", "15", "20", "25", "30"),
                    defaultValue = "30")
            ),
            compute = { v, s, d ->
                val price = num(v, "price")
                val down = num(v, "down")
                val principal = (price - down).coerceAtLeast(0.0)
                loanResult(
                    principal, num(v, "rate"), num(v, "years"), s, d, "Monthly Payment",
                    extraRows = listOf(
                        ResultRow("Home Price", Format.money(price, s, d)),
                        ResultRow("Down Payment", Format.money(down, s, d))
                    ),
                    tip = "A 20% down payment usually avoids private mortgage insurance (PMI)."
                )
            }
        ),
        CalculatorDef(
            id = "mortgage", title = "Mortgage Calculator",
            description = "Full monthly mortgage cost: principal, interest, taxes, insurance and HOA.",
            category = Category.LOAN, icon = "mortgage", popular = true,
            inputs = listOf(
                CalcInput("price", "Home Price ($)", defaultValue = "350000"),
                CalcInput("down", "Down Payment ($)", defaultValue = "70000"),
                CalcInput("rate", "Annual Interest Rate (%)", defaultValue = "6.5"),
                CalcInput("years", "Loan Term (Years)", type = InputType.SELECT,
                    options = listOf("10", "15", "20", "25", "30"), defaultValue = "30"),
                CalcInput("tax", "Property Tax (per year, $)", defaultValue = "4200"),
                CalcInput("insurance", "Home Insurance (per year, $)", defaultValue = "1800"),
                CalcInput("hoa", "HOA Dues (per month, $)", defaultValue = "0")
            ),
            compute = { v, s, d ->
                val principal = (num(v, "price") - num(v, "down")).coerceAtLeast(0.0)
                val pi = emi(principal, num(v, "rate"), num(v, "years"))
                val taxM = num(v, "tax") / 12.0
                val insM = num(v, "insurance") / 12.0
                val hoa = num(v, "hoa")
                val total = pi + taxM + insM + hoa
                val n = num(v, "years") * 12.0
                val totalInterest = pi * n - principal
                CalcResult(
                    title = Format.money(total, s, d),
                    subtitle = "Total Monthly Payment",
                    rows = listOf(
                        ResultRow("Principal & Interest", Format.money(pi, s, d), highlight = true),
                        ResultRow("Property Tax", Format.money(taxM, s, d)),
                        ResultRow("Home Insurance", Format.money(insM, s, d)),
                        ResultRow("HOA Dues", Format.money(hoa, s, d)),
                        ResultRow("Total Interest", Format.money(totalInterest, s, d)),
                        ResultRow("Loan Amount", Format.money(principal, s, d))
                    ),
                    formula = "Monthly = P&I + Tax/12 + Insurance/12 + HOA — P&I uses M = P × r(1+r)ⁿ / ((1+r)ⁿ − 1)",
                    breakdown = listOf(
                        "Loan amount: ${Format.money(principal, s, d)}",
                        "Principal & interest: ${Format.money(pi, s, d)} / month",
                        "Property tax: ${Format.money(taxM, s, d)} / month",
                        "Insurance: ${Format.money(insM, s, d)} / month",
                        "HOA: ${Format.money(hoa, s, d)} / month",
                        "Total interest over the loan: ${Format.money(totalInterest, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("P & I", pi), ChartBar("Tax", taxM),
                        ChartBar("Insurance", insM), ChartBar("HOA", hoa)
                    ),
                    tip = "Taxes and insurance are estimates — your lender's escrow amount may differ."
                )
            }
        ),
        CalculatorDef(
            id = "car-loan", title = "Car Loan Calculator",
            description = "Monthly car payment after down payment and trade-in.",
            category = Category.LOAN, icon = "car",
            inputs = listOf(
                CalcInput("price", "Car Price ($)", defaultValue = "30000"),
                CalcInput("down", "Down Payment ($)", defaultValue = "5000"),
                CalcInput("trade", "Trade-In Value ($)", defaultValue = "2000"),
                CalcInput("rate", "Annual Interest Rate (%)", defaultValue = "7.0"),
                CalcInput("years", "Loan Term (Years)", type = InputType.SELECT,
                    options = listOf("2", "3", "4", "5", "6", "7"), defaultValue = "5")
            ),
            compute = { v, s, d ->
                val principal = (num(v, "price") - num(v, "down") - num(v, "trade")).coerceAtLeast(0.0)
                loanResult(
                    principal, num(v, "rate"), num(v, "years"), s, d, "Monthly Payment",
                    extraRows = listOf(
                        ResultRow("Car Price", Format.money(num(v, "price"), s, d)),
                        ResultRow("Down + Trade-In", Format.money(num(v, "down") + num(v, "trade"), s, d))
                    ),
                    tip = "A bigger down payment lowers both the monthly payment and total interest."
                )
            }
        ),
        CalculatorDef(
            id = "emi", title = "EMI Calculator",
            description = "Equated Monthly Instalment for any loan amount, rate and term.",
            category = Category.LOAN, icon = "emi", popular = true,
            inputs = listOf(
                CalcInput("amount", "Loan Amount ($)", defaultValue = "10000"),
                CalcInput("rate", "Annual Interest Rate (%)", defaultValue = "9.0"),
                CalcInput("years", "Loan Term (Years)", type = InputType.SELECT,
                    options = listOf("1", "2", "3", "4", "5", "6", "7", "8", "10", "15", "20"),
                    defaultValue = "5")
            ),
            compute = { v, s, d ->
                loanResult(
                    num(v, "amount"), num(v, "rate"), num(v, "years"), s, d, "Monthly EMI",
                    tip = "EMI stays the same every month; the interest part shrinks as the balance falls."
                )
            }
        ),
        CalculatorDef(
            id = "loan-interest", title = "Loan Interest Calculator",
            description = "How much interest you will pay over the life of a loan.",
            category = Category.LOAN, icon = "interest",
            inputs = listOf(
                CalcInput("amount", "Loan Amount ($)", defaultValue = "20000"),
                CalcInput("rate", "Annual Interest Rate (%)", defaultValue = "8.0"),
                CalcInput("years", "Loan Term (Years)", type = InputType.SELECT,
                    options = listOf("1", "2", "3", "4", "5", "7", "10", "15", "20", "30"),
                    defaultValue = "5")
            ),
            compute = { v, s, d ->
                val principal = num(v, "amount")
                val monthly = emi(principal, num(v, "rate"), num(v, "years"))
                val totalInterest = monthly * num(v, "years") * 12.0 - principal
                CalcResult(
                    title = Format.money(totalInterest, s, d),
                    subtitle = "Total Interest Paid",
                    rows = listOf(
                        ResultRow("Total Interest", Format.money(totalInterest, s, d), highlight = true),
                        ResultRow("Monthly Payment", Format.money(monthly, s, d)),
                        ResultRow("Total Payment", Format.money(monthly * num(v, "years") * 12.0, s, d)),
                        ResultRow("Loan Amount", Format.money(principal, s, d)),
                        ResultRow("Interest Share", Format.pct(totalInterest / (monthly * num(v, "years") * 12.0) * 100, d) + " of all payments")
                    ),
                    formula = "Total Interest = (Monthly Payment × Number of Payments) − Loan Amount",
                    breakdown = listOf(
                        "Loan amount: ${Format.money(principal, s, d)}",
                        "Monthly payment: ${Format.money(monthly, s, d)}",
                        "Total interest: ${Format.money(totalInterest, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Principal", principal),
                        ChartBar("Interest", totalInterest)
                    ),
                    tip = "Paying even a little extra each month cuts total interest."
                )
            }
        ),
        CalculatorDef(
            id = "loan-payoff", title = "Loan Payoff Calculator",
            description = "How fast extra payments pay off a loan — and how much interest they save.",
            category = Category.LOAN, icon = "payoff",
            inputs = listOf(
                CalcInput("balance", "Current Balance ($)", defaultValue = "10000"),
                CalcInput("rate", "Annual Interest Rate (%)", defaultValue = "18.0"),
                CalcInput("payment", "Monthly Payment ($)", defaultValue = "300"),
                CalcInput("extra", "Extra Payment per Month ($)", defaultValue = "50")
            ),
            compute = { v, s, d ->
                val balance = num(v, "balance")
                val r = num(v, "rate") / 100.0 / 12.0
                val pay = num(v, "payment")
                val extra = num(v, "extra")
                fun monthsToPayoff(p: Double): Double {
                    if (p <= 0) return Double.POSITIVE_INFINITY
                    if (r == 0.0) return balance / p
                    if (p <= balance * r) return Double.POSITIVE_INFINITY
                    return -Math.log(1 - balance * r / p) / Math.log(1 + r)
                }
                fun totalInterest(p: Double, months: Double): Double =
                    if (months.isInfinite()) Double.POSITIVE_INFINITY else p * months - balance
                val m1 = monthsToPayoff(pay)
                val m2 = monthsToPayoff(pay + extra)
                val i1 = totalInterest(pay, m1)
                val i2 = totalInterest(pay + extra, m2)
                val monthsSaved = if (m1.isInfinite() || m2.isInfinite()) 0.0 else m1 - m2
                val interestSaved = if (i1.isInfinite() || i2.isInfinite()) 0.0 else i1 - i2
                val payoffText = if (m2.isInfinite()) "Never — payment is below the monthly interest"
                else "${Format.num(m2, 0)} months (${Format.num(m2 / 12.0, 1)} years)"
                CalcResult(
                    title = payoffText,
                    subtitle = "Time to Pay Off (with extra payment)",
                    rows = listOf(
                        ResultRow("With Extra Payment", payoffText, highlight = true),
                        ResultRow("Without Extra Payment",
                            if (m1.isInfinite()) "Never — payment is below the monthly interest"
                            else "${Format.num(m1, 0)} months (${Format.num(m1 / 12.0, 1)} years)"),
                        ResultRow("Months Saved", Format.num(monthsSaved, 0)),
                        ResultRow("Interest Saved", Format.money(interestSaved, s, d)),
                        ResultRow("Interest Without Extra",
                            if (i1.isInfinite()) "—" else Format.money(i1, s, d)),
                        ResultRow("Interest With Extra",
                            if (i2.isInfinite()) "—" else Format.money(i2, s, d))
                    ),
                    formula = "Months n = −ln(1 − r·B / P) / ln(1 + r)  —  B = balance, r = monthly rate, P = monthly payment",
                    breakdown = listOf(
                        "Balance: ${Format.money(balance, s, d)}",
                        "Monthly payment: ${Format.money(pay, s, d)} + ${Format.money(extra, s, d)} extra",
                        "Months saved: ${Format.num(monthsSaved, 0)}",
                        "Interest saved: ${Format.money(interestSaved, s, d)}"
                    ),
                    tip = "If the monthly payment is below the monthly interest, the balance never falls — raise the payment."
                )
            }
        ),
        CalculatorDef(
            id = "dti", title = "Debt-to-Income Calculator",
            description = "Your debt-to-income ratio — the number lenders check first.",
            category = Category.LOAN, icon = "dti",
            inputs = listOf(
                CalcInput("income", "Gross Monthly Income ($)", defaultValue = "6000"),
                CalcInput("rent", "Rent / Mortgage ($ per month)", defaultValue = "1500"),
                CalcInput("car", "Car Payment ($ per month)", defaultValue = "400"),
                CalcInput("cards", "Credit Card Payments ($ per month)", defaultValue = "200"),
                CalcInput("other", "Other Debt Payments ($ per month)", defaultValue = "100")
            ),
            compute = { v, s, d ->
                val income = num(v, "income")
                val debts = num(v, "rent") + num(v, "car") + num(v, "cards") + num(v, "other")
                val dti = if (income > 0) debts / income * 100.0 else 0.0
                val rating = when {
                    dti <= 36 -> "Excellent — most lenders prefer DTI at or below 36%."
                    dti <= 43 -> "Good — many lenders accept up to 43%."
                    dti <= 50 -> "High — borrowing may be harder and costlier."
                    else -> "Very high — focus on paying down debt before new borrowing."
                }
                CalcResult(
                    title = Format.pct(dti, d),
                    subtitle = "Debt-to-Income Ratio",
                    rows = listOf(
                        ResultRow("DTI Ratio", Format.pct(dti, d), highlight = true),
                        ResultRow("Total Monthly Debt", Format.money(debts, s, d)),
                        ResultRow("Gross Monthly Income", Format.money(income, s, d)),
                        ResultRow("Money Left After Debt", Format.money(income - debts, s, d))
                    ),
                    formula = "DTI = Total Monthly Debt Payments ÷ Gross Monthly Income × 100",
                    breakdown = listOf(
                        "Monthly debts: ${Format.money(debts, s, d)}",
                        "Gross income: ${Format.money(income, s, d)}",
                        "DTI: ${Format.pct(dti, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Debt", debts),
                        ChartBar("Left Over", (income - debts).coerceAtLeast(0.0))
                    ),
                    tip = rating
                )
            }
        ),
        CalculatorDef(
            id = "loan-comparison", title = "Loan Comparison Calculator",
            description = "Compare two loan offers side by side and see which costs less.",
            category = Category.LOAN, icon = "compare",
            inputs = listOf(
                CalcInput("amountA", "Loan A — Amount ($)", defaultValue = "25000"),
                CalcInput("rateA", "Loan A — Annual Rate (%)", defaultValue = "6.5"),
                CalcInput("yearsA", "Loan A — Term (Years)", type = InputType.SELECT,
                    options = listOf("1", "2", "3", "4", "5", "7", "10", "15", "20", "30"), defaultValue = "5"),
                CalcInput("amountB", "Loan B — Amount ($)", defaultValue = "25000"),
                CalcInput("rateB", "Loan B — Annual Rate (%)", defaultValue = "7.5"),
                CalcInput("yearsB", "Loan B — Term (Years)", type = InputType.SELECT,
                    options = listOf("1", "2", "3", "4", "5", "7", "10", "15", "20", "30"), defaultValue = "5")
            ),
            compute = { v, s, d ->
                val pA = num(v, "amountA"); val pB = num(v, "amountB")
                val mA = emi(pA, num(v, "rateA"), num(v, "yearsA"))
                val mB = emi(pB, num(v, "rateB"), num(v, "yearsB"))
                val tA = mA * num(v, "yearsA") * 12.0
                val tB = mB * num(v, "yearsB") * 12.0
                val iA = tA - pA; val iB = tB - pB
                val cheaper = if (tA <= tB) "Loan A" else "Loan B"
                val saved = kotlin.math.abs(tA - tB)
                CalcResult(
                    title = "$cheaper is cheaper",
                    subtitle = "You save ${Format.money(saved, s, d)} in total",
                    rows = listOf(
                        ResultRow("Loan A — Monthly", Format.money(mA, s, d)),
                        ResultRow("Loan A — Total Interest", Format.money(iA, s, d)),
                        ResultRow("Loan A — Total Payment", Format.money(tA, s, d)),
                        ResultRow("Loan B — Monthly", Format.money(mB, s, d)),
                        ResultRow("Loan B — Total Interest", Format.money(iB, s, d)),
                        ResultRow("Loan B — Total Payment", Format.money(tB, s, d)),
                        ResultRow("Difference", Format.money(saved, s, d), highlight = true)
                    ),
                    formula = "Each loan uses M = P × r(1+r)ⁿ / ((1+r)ⁿ − 1); the cheaper loan has the lower total payment.",
                    breakdown = listOf(
                        "Loan A total: ${Format.money(tA, s, d)} (interest ${Format.money(iA, s, d)})",
                        "Loan B total: ${Format.money(tB, s, d)} (interest ${Format.money(iB, s, d)})",
                        "Saving with $cheaper: ${Format.money(saved, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Loan A Total", tA),
                        ChartBar("Loan B Total", tB)
                    ),
                    tip = "Compare total payment, not just the monthly figure — a longer term can look cheaper monthly but cost far more overall."
                )
            }
        ),
        CalculatorDef(
            id = "amortization", title = "Amortization Calculator",
            description = "Year-by-year schedule: how each payment splits into principal and interest.",
            category = Category.LOAN, icon = "schedule",
            inputs = listOf(
                CalcInput("amount", "Loan Amount ($)", defaultValue = "25000"),
                CalcInput("rate", "Annual Interest Rate (%)", defaultValue = "6.5"),
                CalcInput("years", "Loan Term (Years)", type = InputType.SELECT,
                    options = listOf("1", "2", "3", "4", "5", "7", "10", "15", "20", "25", "30"),
                    defaultValue = "5")
            ),
            compute = { v, s, d ->
                val principal = num(v, "amount")
                val rate = num(v, "rate")
                val years = num(v, "years")
                val monthly = emi(principal, rate, years)
                val r = rate / 100.0 / 12.0
                val totalMonths = (years * 12).toInt()
                var balance = principal
                var totalInterest = 0.0
                val lines = mutableListOf<String>()
                val chart = mutableListOf<ChartBar>()
                var year = 1
                var yearPrincipal = 0.0
                var yearInterest = 0.0
                for (m in 1..totalMonths) {
                    val interest = balance * r
                    val principalPart = (monthly - interest).coerceAtLeast(0.0)
                    balance = (balance - principalPart).coerceAtLeast(0.0)
                    yearPrincipal += principalPart
                    yearInterest += interest
                    totalInterest += interest
                    if (m % 12 == 0 || m == totalMonths) {
                        lines.add(
                            "Year $year: paid ${Format.money((yearPrincipal + yearInterest), s, d)} " +
                                "(principal ${Format.money(yearPrincipal, s, d)}, " +
                                "interest ${Format.money(yearInterest, s, d)}), " +
                                "balance ${Format.money(balance, s, d)}"
                        )
                        chart.add(ChartBar("Y$year", balance))
                        year++
                        yearPrincipal = 0.0
                        yearInterest = 0.0
                    }
                }
                CalcResult(
                    title = Format.money(monthly, s, d),
                    subtitle = "Monthly Payment",
                    rows = listOf(
                        ResultRow("Monthly Payment", Format.money(monthly, s, d), highlight = true),
                        ResultRow("Total Interest", Format.money(totalInterest, s, d)),
                        ResultRow("Total Payment", Format.money(monthly * totalMonths, s, d)),
                        ResultRow("Loan Amount", Format.money(principal, s, d)),
                        ResultRow("Payments", "$totalMonths monthly payments")
                    ),
                    formula = "Each month: Interest = Balance × (Rate ÷ 12); Principal part = Payment − Interest; Balance falls by the principal part.",
                    breakdown = lines,
                    chart = chart,
                    tip = "Early payments are mostly interest; the principal share grows every year."
                )
            }
        )
    )
}
