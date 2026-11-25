package com.fincalc.app.data

import com.fincalc.app.util.Format

/**
 * Category 3 — Investment Calculators (10).
 * All returns are estimates — markets vary and no return is guaranteed.
 * That distinction is shown on every result (see [INV_DISCLAIMER]).
 */
object CalculatorsInvest {

    private const val INV_DISCLAIMER =
        "Estimated returns only — not guaranteed and not investment advice. " +
            "Actual investment results depend on market performance, fees and taxes."

    val all: List<CalculatorDef> = listOf(
        CalculatorDef(
            id = "compound-interest", title = "Compound Interest Calculator",
            description = "How your money grows when interest earns interest.",
            category = Category.INVESTMENT, icon = "compound", popular = true,
            inputs = listOf(
                CalcInput("principal", "Principal Amount ($)", defaultValue = "10000"),
                CalcInput("rate", "Annual Interest Rate (%)", defaultValue = "7"),
                CalcInput("years", "Time (Years)", defaultValue = "10"),
                CalcInput("frequency", "Compounding", type = InputType.SELECT,
                    options = listOf("Annually", "Semiannually", "Quarterly", "Monthly", "Daily"),
                    defaultValue = "Annually")
            ),
            compute = { v, s, d ->
                val p = num(v, "principal")
                val rate = num(v, "rate")
                val years = num(v, "years")
                val n = when (str(v, "frequency")) {
                    "Semiannually" -> 2.0; "Quarterly" -> 4.0; "Monthly" -> 12.0; "Daily" -> 365.0
                    else -> 1.0
                }
                val total = compound(p, rate, years, n)
                val interest = total - p
                CalcResult(
                    title = Format.money(total, s, d),
                    subtitle = "Total Value",
                    rows = listOf(
                        ResultRow("Total Value", Format.money(total, s, d), highlight = true),
                        ResultRow("Principal", Format.money(p, s, d)),
                        ResultRow("Interest Earned", Format.money(interest, s, d)),
                        ResultRow("Rate", Format.pct(rate, d)),
                        ResultRow("Time", "${Format.num(years, 0)} years")
                    ),
                    formula = "A = P(1 + r/n)^(nt) — P = principal, r = annual rate, n = compounding periods per year, t = years.",
                    breakdown = listOf(
                        "Principal: ${Format.money(p, s, d)}",
                        "Interest earned: ${Format.money(interest, s, d)}",
                        "Total value after ${Format.num(years, 0)} years: ${Format.money(total, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Principal", p),
                        ChartBar("Interest", interest)
                    ),
                    tip = "Start early to maximize your returns — compounding rewards time more than timing.",
                    disclaimer = INV_DISCLAIMER
                )
            }
        ),
        CalculatorDef(
            id = "simple-interest", title = "Simple Interest Calculator",
            description = "Interest calculated only on the original principal.",
            category = Category.INVESTMENT, icon = "simple",
            inputs = listOf(
                CalcInput("principal", "Principal Amount ($)", defaultValue = "10000"),
                CalcInput("rate", "Annual Interest Rate (%)", defaultValue = "5"),
                CalcInput("years", "Time (Years)", defaultValue = "3")
            ),
            compute = { v, s, d ->
                val p = num(v, "principal")
                val interest = p * num(v, "rate") / 100.0 * num(v, "years")
                CalcResult(
                    title = Format.money(p + interest, s, d),
                    subtitle = "Total Amount",
                    rows = listOf(
                        ResultRow("Total Amount", Format.money(p + interest, s, d), highlight = true),
                        ResultRow("Principal", Format.money(p, s, d)),
                        ResultRow("Interest Earned", Format.money(interest, s, d)),
                        ResultRow("Rate", Format.pct(num(v, "rate"), d)),
                        ResultRow("Time", "${Format.num(num(v, "years"), 0)} years")
                    ),
                    formula = "Interest = P × r × t ÷ 100. Total = Principal + Interest.",
                    breakdown = listOf(
                        "Principal: ${Format.money(p, s, d)}",
                        "Interest: ${Format.money(interest, s, d)}",
                        "Total: ${Format.money(p + interest, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Principal", p),
                        ChartBar("Interest", interest)
                    ),
                    disclaimer = INV_DISCLAIMER
                )
            }
        ),
        CalculatorDef(
            id = "investment-return", title = "Investment Return Calculator",
            description = "Total return and annualized (CAGR) return on an investment.",
            category = Category.INVESTMENT, icon = "return",
            inputs = listOf(
                CalcInput("initial", "Initial Investment ($)", defaultValue = "10000"),
                CalcInput("final", "Final Value ($)", defaultValue = "16105"),
                CalcInput("years", "Investment Period (Years)", defaultValue = "10")
            ),
            compute = { v, s, d ->
                val initial = num(v, "initial")
                val final = num(v, "final")
                val years = num(v, "years")
                val gain = final - initial
                val totalReturn = if (initial > 0) gain / initial * 100 else 0.0
                val cagr = if (initial > 0 && years > 0) (Math.pow(final / initial, 1.0 / years) - 1) * 100 else 0.0
                CalcResult(
                    title = Format.pct(totalReturn, d),
                    subtitle = "Total Return",
                    rows = listOf(
                        ResultRow("Total Return", Format.pct(totalReturn, d), highlight = true),
                        ResultRow("Annualized Return (CAGR)", Format.pct(cagr, d)),
                        ResultRow("Gain / Loss", Format.money(gain, s, d)),
                        ResultRow("Initial Investment", Format.money(initial, s, d)),
                        ResultRow("Final Value", Format.money(final, s, d))
                    ),
                    formula = "Total Return = (Final − Initial) ÷ Initial × 100. CAGR = (Final ÷ Initial)^(1 ÷ Years) − 1.",
                    breakdown = listOf(
                        "Invested: ${Format.money(initial, s, d)}",
                        "Now worth: ${Format.money(final, s, d)}",
                        "Gain: ${Format.money(gain, s, d)} (${Format.pct(totalReturn, d)})",
                        "Annualized: ${Format.pct(cagr, d)} per year"
                    ),
                    chart = listOf(
                        ChartBar("Invested", initial),
                        ChartBar("Final Value", final)
                    ),
                    disclaimer = INV_DISCLAIMER
                )
            }
        ),
        CalculatorDef(
            id = "stock-profit", title = "Stock Profit Calculator",
            description = "Profit or loss on a stock trade, after commissions.",
            category = Category.INVESTMENT, icon = "stock",
            inputs = listOf(
                CalcInput("buy", "Buy Price per Share ($)", defaultValue = "50"),
                CalcInput("shares", "Number of Shares", defaultValue = "100"),
                CalcInput("sell", "Sell Price per Share ($)", defaultValue = "65"),
                CalcInput("buyFee", "Buy Commission ($)", defaultValue = "0"),
                CalcInput("sellFee", "Sell Commission ($)", defaultValue = "0")
            ),
            compute = { v, s, d ->
                val buyTotal = num(v, "buy") * num(v, "shares") + num(v, "buyFee")
                val sellTotal = num(v, "sell") * num(v, "shares") - num(v, "sellFee")
                val profit = sellTotal - buyTotal
                val ret = if (buyTotal > 0) profit / buyTotal * 100 else 0.0
                CalcResult(
                    title = Format.money(profit, s, d),
                    subtitle = if (profit >= 0) "Profit" else "Loss",
                    rows = listOf(
                        ResultRow(if (profit >= 0) "Profit" else "Loss", Format.money(profit, s, d), highlight = true),
                        ResultRow("Return", Format.pct(ret, d)),
                        ResultRow("Total Cost", Format.money(buyTotal, s, d)),
                        ResultRow("Total Proceeds", Format.money(sellTotal, s, d)),
                        ResultRow("Break-Even Sell Price",
                            Format.money(if (num(v, "shares") > 0) buyTotal / num(v, "shares") else 0.0, s, d))
                    ),
                    formula = "Profit = (Sell Price × Shares − Sell Fee) − (Buy Price × Shares + Buy Fee).",
                    breakdown = listOf(
                        "Bought ${Format.num(num(v, "shares"), 0)} shares at ${Format.money(num(v, "buy"), s, d)}: ${Format.money(buyTotal, s, d)}",
                        "Sold at ${Format.money(num(v, "sell"), s, d)}: ${Format.money(sellTotal, s, d)}",
                        "Profit: ${Format.money(profit, s, d)} (${Format.pct(ret, d)})"
                    ),
                    chart = listOf(
                        ChartBar("Cost", buyTotal),
                        ChartBar("Proceeds", sellTotal)
                    ),
                    tip = "Commissions and taxes reduce real profit — this result is before tax.",
                    disclaimer = INV_DISCLAIMER
                )
            }
        ),
        CalculatorDef(
            id = "dividend", title = "Dividend Calculator",
            description = "Annual dividend income and yield from your shares.",
            category = Category.INVESTMENT, icon = "dividend",
            inputs = listOf(
                CalcInput("shares", "Number of Shares", defaultValue = "200"),
                CalcInput("dividend", "Dividend per Share (per year, $)", defaultValue = "2.50"),
                CalcInput("price", "Current Share Price ($)", defaultValue = "62.50")
            ),
            compute = { v, s, d ->
                val shares = num(v, "shares")
                val annual = shares * num(v, "dividend")
                val invested = shares * num(v, "price")
                val yield = if (num(v, "price") > 0) num(v, "dividend") / num(v, "price") * 100 else 0.0
                CalcResult(
                    title = Format.money(annual, s, d),
                    subtitle = "Annual Dividend Income",
                    rows = listOf(
                        ResultRow("Annual Dividend Income", Format.money(annual, s, d), highlight = true),
                        ResultRow("Monthly Income", Format.money(annual / 12.0, s, d)),
                        ResultRow("Dividend Yield", Format.pct(yield, d)),
                        ResultRow("Investment Value", Format.money(invested, s, d)),
                        ResultRow("Quarterly Income", Format.money(annual / 4.0, s, d))
                    ),
                    formula = "Annual Income = Shares × Dividend per Share. Yield = Dividend per Share ÷ Share Price × 100.",
                    breakdown = listOf(
                        "Shares: ${Format.num(shares, 0)} × ${Format.money(num(v, "dividend"), s, d)} = ${Format.money(annual, s, d)} / year",
                        "Yield: ${Format.pct(yield, d)}",
                        "Monthly: ${Format.money(annual / 12.0, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Annual Income", annual),
                        ChartBar("Invested", invested)
                    ),
                    tip = "Reinvesting dividends compounds your income over time.",
                    disclaimer = INV_DISCLAIMER
                )
            }
        ),
        CalculatorDef(
            id = "retirement", title = "Retirement Calculator",
            description = "What your savings could be worth when you retire.",
            category = Category.INVESTMENT, icon = "retirement",
            inputs = listOf(
                CalcInput("age", "Current Age", defaultValue = "30"),
                CalcInput("retireAge", "Retirement Age", defaultValue = "65"),
                CalcInput("current", "Current Savings ($)", defaultValue = "20000"),
                CalcInput("monthly", "Monthly Contribution ($)", defaultValue = "500"),
                CalcInput("rate", "Expected Annual Return (%)", defaultValue = "7")
            ),
            compute = { v, s, d ->
                val years = (num(v, "retireAge") - num(v, "age")).coerceAtLeast(0.0)
                val fvCurrent = compound(num(v, "current"), num(v, "rate"), years)
                val fvContrib = futureValueAnnuity(num(v, "monthly"), num(v, "rate"), years)
                val total = fvCurrent + fvContrib
                val contributed = num(v, "monthly") * years * 12.0 + num(v, "current")
                val growth = total - contributed
                CalcResult(
                    title = Format.money(total, s, d),
                    subtitle = "Estimated Savings at Retirement",
                    rows = listOf(
                        ResultRow("At Retirement", Format.money(total, s, d), highlight = true),
                        ResultRow("Your Contributions", Format.money(contributed, s, d)),
                        ResultRow("Investment Growth", Format.money(growth, s, d)),
                        ResultRow("Years to Retirement", Format.num(years, 0)),
                        ResultRow("Monthly Income (4% rule)", Format.money(total * 0.04 / 12.0, s, d))
                    ),
                    formula = "Future Value = Current Savings × (1+r)^t + Monthly Contribution × ((1+r)^n − 1) ÷ r.",
                    breakdown = listOf(
                        "Years to retirement: ${Format.num(years, 0)}",
                        "Contributions: ${Format.money(contributed, s, d)}",
                        "Growth: ${Format.money(growth, s, d)}",
                        "Total at retirement: ${Format.money(total, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Contributions", contributed),
                        ChartBar("Growth", growth)
                    ),
                    tip = "The 4% rule: withdrawing 4% a year is a common retirement planning guide — not a guarantee.",
                    disclaimer = INV_DISCLAIMER
                )
            }
        ),
        CalculatorDef(
            id = "401k", title = "401(k) Calculator",
            description = "Your 401(k) balance with contributions and employer match.",
            category = Category.INVESTMENT, icon = "401k",
            inputs = listOf(
                CalcInput("salary", "Annual Salary ($)", defaultValue = "70000"),
                CalcInput("contrib", "Your Contribution (%)", defaultValue = "6"),
                CalcInput("match", "Employer Match (%)", defaultValue = "3"),
                CalcInput("current", "Current 401(k) Balance ($)", defaultValue = "15000"),
                CalcInput("rate", "Expected Annual Return (%)", defaultValue = "7"),
                CalcInput("years", "Years Until Retirement", defaultValue = "30")
            ),
            compute = { v, s, d ->
                val salary = num(v, "salary")
                val yourAnnual = salary * num(v, "contrib") / 100.0
                val matchAnnual = salary * num(v, "match") / 100.0
                val monthlyTotal = (yourAnnual + matchAnnual) / 12.0
                val years = num(v, "years")
                val fvCurrent = compound(num(v, "current"), num(v, "rate"), years)
                val fvContrib = futureValueAnnuity(monthlyTotal, num(v, "rate"), years)
                val total = fvCurrent + fvContrib
                val contributed = (yourAnnual + matchAnnual) * years + num(v, "current")
                CalcResult(
                    title = Format.money(total, s, d),
                    subtitle = "Estimated 401(k) at Retirement",
                    rows = listOf(
                        ResultRow("At Retirement", Format.money(total, s, d), highlight = true),
                        ResultRow("Your Contribution / Year", Format.money(yourAnnual, s, d)),
                        ResultRow("Employer Match / Year", Format.money(matchAnnual, s, d)),
                        ResultRow("Total Contributed", Format.money(contributed, s, d)),
                        ResultRow("Investment Growth", Format.money(total - contributed, s, d))
                    ),
                    formula = "Future Value = Current Balance × (1+r)^t + (Your + Employer Monthly) × ((1+r)^n − 1) ÷ r.",
                    breakdown = listOf(
                        "You contribute: ${Format.money(yourAnnual, s, d)}/year",
                        "Employer matches: ${Format.money(matchAnnual, s, d)}/year",
                        "Total contributed over ${Format.num(years, 0)} years: ${Format.money(contributed, s, d)}",
                        "Projected balance: ${Format.money(total, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Contributed", contributed),
                        ChartBar("Growth", total - contributed)
                    ),
                    tip = "Always contribute at least enough to get the full employer match — it is free money.",
                    disclaimer = INV_DISCLAIMER
                )
            }
        ),
        CalculatorDef(
            id = "roth-ira", title = "Roth IRA Calculator",
            description = "Tax-free growth of a Roth IRA over time.",
            category = Category.INVESTMENT, icon = "roth",
            inputs = listOf(
                CalcInput("annual", "Annual Contribution ($)", defaultValue = "6500"),
                CalcInput("current", "Current Balance ($)", defaultValue = "10000"),
                CalcInput("rate", "Expected Annual Return (%)", defaultValue = "7"),
                CalcInput("years", "Years Until Withdrawal", defaultValue = "30")
            ),
            compute = { v, s, d ->
                val years = num(v, "years")
                val monthly = num(v, "annual") / 12.0
                val fvCurrent = compound(num(v, "current"), num(v, "rate"), years)
                val fvContrib = futureValueAnnuity(monthly, num(v, "rate"), years)
                val total = fvCurrent + fvContrib
                val contributed = num(v, "annual") * years + num(v, "current")
                CalcResult(
                    title = Format.money(total, s, d),
                    subtitle = "Tax-Free Value at Withdrawal",
                    rows = listOf(
                        ResultRow("Tax-Free Value", Format.money(total, s, d), highlight = true),
                        ResultRow("Total Contributions", Format.money(contributed, s, d)),
                        ResultRow("Tax-Free Growth", Format.money(total - contributed, s, d)),
                        ResultRow("Annual Contribution", Format.money(num(v, "annual"), s, d))
                    ),
                    formula = "Future Value = Current Balance × (1+r)^t + Monthly Contribution × ((1+r)^n − 1) ÷ r. Qualified withdrawals are tax-free.",
                    breakdown = listOf(
                        "Contributions: ${Format.money(contributed, s, d)}",
                        "Growth (tax-free): ${Format.money(total - contributed, s, d)}",
                        "Value at withdrawal: ${Format.money(total, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Contributions", contributed),
                        ChartBar("Growth", total - contributed)
                    ),
                    tip = "Roth IRA contributions grow tax-free — check the current IRS annual contribution limit before contributing.",
                    disclaimer = INV_DISCLAIMER
                )
            }
        ),
        CalculatorDef(
            id = "savings-growth", title = "Savings Growth Calculator",
            description = "How regular deposits grow your savings.",
            category = Category.INVESTMENT, icon = "savings",
            inputs = listOf(
                CalcInput("initial", "Initial Deposit ($)", defaultValue = "5000"),
                CalcInput("monthly", "Monthly Deposit ($)", defaultValue = "200"),
                CalcInput("rate", "Annual Interest Rate (%)", defaultValue = "4"),
                CalcInput("years", "Time (Years)", defaultValue = "10")
            ),
            compute = { v, s, d ->
                val years = num(v, "years")
                val fvInitial = compound(num(v, "initial"), num(v, "rate"), years)
                val fvMonthly = futureValueAnnuity(num(v, "monthly"), num(v, "rate"), years)
                val total = fvInitial + fvMonthly
                val deposited = num(v, "initial") + num(v, "monthly") * years * 12.0
                CalcResult(
                    title = Format.money(total, s, d),
                    subtitle = "Total Savings",
                    rows = listOf(
                        ResultRow("Total Savings", Format.money(total, s, d), highlight = true),
                        ResultRow("Total Deposited", Format.money(deposited, s, d)),
                        ResultRow("Interest Earned", Format.money(total - deposited, s, d)),
                        ResultRow("Monthly Deposit", Format.money(num(v, "monthly"), s, d))
                    ),
                    formula = "Future Value = Initial × (1+r)^t + Monthly × ((1+r)^n − 1) ÷ r.",
                    breakdown = listOf(
                        "Deposited: ${Format.money(deposited, s, d)}",
                        "Interest earned: ${Format.money(total - deposited, s, d)}",
                        "Total after ${Format.num(years, 0)} years: ${Format.money(total, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Deposited", deposited),
                        ChartBar("Interest", total - deposited)
                    ),
                    tip = "Automatic monthly deposits make saving effortless.",
                    disclaimer = INV_DISCLAIMER
                )
            }
        ),
        CalculatorDef(
            id = "inflation", title = "Inflation Calculator",
            description = "What money will be worth after inflation — or what it was worth before.",
            category = Category.INVESTMENT, icon = "inflation",
            inputs = listOf(
                CalcInput("amount", "Amount ($)", defaultValue = "10000"),
                CalcInput("rate", "Average Inflation Rate (%)", defaultValue = "3"),
                CalcInput("years", "Years", defaultValue = "10"),
                CalcInput("direction", "Calculate", type = InputType.SELECT,
                    options = listOf("Future value (what it will cost)", "Past value (what it was worth)"),
                    defaultValue = "Future value (what it will cost)")
            ),
            compute = { v, s, d ->
                val amount = num(v, "amount")
                val rate = num(v, "rate")
                val years = num(v, "years")
                val future = str(v, "direction").startsWith("Future")
                val result = if (future) amount * Math.pow(1 + rate / 100.0, years)
                else amount / Math.pow(1 + rate / 100.0, years)
                val change = result - amount
                val powerLoss = if (future) (1 - amount / result) * 100 else (1 - result / amount) * 100
                CalcResult(
                    title = Format.money(result, s, d),
                    subtitle = if (future) "Future Equivalent Value" else "Past Equivalent Value",
                    rows = listOf(
                        ResultRow(if (future) "Future Value" else "Past Value", Format.money(result, s, d), highlight = true),
                        ResultRow("Original Amount", Format.money(amount, s, d)),
                        ResultRow("Change", Format.money(change, s, d)),
                        ResultRow("Purchasing Power Change", Format.pct(powerLoss, d)),
                        ResultRow("Inflation Rate", Format.pct(rate, d))
                    ),
                    formula = if (future) "Future Value = Amount × (1 + Inflation Rate)^Years."
                    else "Past Value = Amount ÷ (1 + Inflation Rate)^Years.",
                    breakdown = listOf(
                        "Amount: ${Format.money(amount, s, d)}",
                        "Inflation: ${Format.pct(rate, d)} for ${Format.num(years, 0)} years",
                        "Equivalent value: ${Format.money(result, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Today", amount),
                        ChartBar(if (future) "Future" else "Past", result)
                    ),
                    tip = "At 3% inflation, money loses about half its purchasing power in 24 years.",
                    disclaimer = "Estimate only — future inflation is unknown."
                )
            }
        )
    )
}
