package com.fincalc.app.data

import com.fincalc.app.util.Format

/** Category 5 — Business Calculators (10). */
object CalculatorsBusiness {

    val all: List<CalculatorDef> = listOf(
        CalculatorDef(
            id = "profit-margin", title = "Profit Margin Calculator",
            description = "Gross and net profit margin on revenue.",
            category = Category.BUSINESS, icon = "margin",
            inputs = listOf(
                CalcInput("revenue", "Revenue ($)", defaultValue = "100000"),
                CalcInput("cost", "Cost of Goods Sold ($)", defaultValue = "60000"),
                CalcInput("expenses", "Operating Expenses ($)", defaultValue = "20000")
            ),
            compute = { v, s, d ->
                val revenue = num(v, "revenue")
                val gross = revenue - num(v, "cost")
                val net = gross - num(v, "expenses")
                val grossMargin = if (revenue > 0) gross / revenue * 100 else 0.0
                val netMargin = if (revenue > 0) net / revenue * 100 else 0.0
                CalcResult(
                    title = Format.pct(netMargin, d),
                    subtitle = "Net Profit Margin",
                    rows = listOf(
                        ResultRow("Net Profit Margin", Format.pct(netMargin, d), highlight = true),
                        ResultRow("Gross Profit Margin", Format.pct(grossMargin, d)),
                        ResultRow("Gross Profit", Format.money(gross, s, d)),
                        ResultRow("Net Profit", Format.money(net, s, d)),
                        ResultRow("Revenue", Format.money(revenue, s, d))
                    ),
                    formula = "Gross Margin = (Revenue − COGS) ÷ Revenue × 100. Net Margin = (Revenue − COGS − Expenses) ÷ Revenue × 100.",
                    breakdown = listOf(
                        "Revenue: ${Format.money(revenue, s, d)}",
                        "Gross profit: ${Format.money(gross, s, d)} (${Format.pct(grossMargin, d)})",
                        "Net profit: ${Format.money(net, s, d)} (${Format.pct(netMargin, d)})"
                    ),
                    chart = listOf(
                        ChartBar("COGS", num(v, "cost")),
                        ChartBar("Expenses", num(v, "expenses")),
                        ChartBar("Net Profit", net.coerceAtLeast(0.0))
                    ),
                    tip = "A healthy net margin is usually 10–20% depending on the industry."
                )
            }
        ),
        CalculatorDef(
            id = "markup", title = "Markup Calculator",
            description = "Selling price from cost and markup — and the margin it gives.",
            category = Category.BUSINESS, icon = "markup",
            inputs = listOf(
                CalcInput("cost", "Cost Price ($)", defaultValue = "50"),
                CalcInput("markup", "Markup (%)", defaultValue = "40")
            ),
            compute = { v, s, d ->
                val cost = num(v, "cost")
                val markupPct = num(v, "markup")
                val price = cost * (1 + markupPct / 100.0)
                val profit = price - cost
                val margin = if (price > 0) profit / price * 100 else 0.0
                CalcResult(
                    title = Format.money(price, s, d),
                    subtitle = "Selling Price",
                    rows = listOf(
                        ResultRow("Selling Price", Format.money(price, s, d), highlight = true),
                        ResultRow("Profit per Unit", Format.money(profit, s, d)),
                        ResultRow("Profit Margin", Format.pct(margin, d)),
                        ResultRow("Markup", Format.pct(markupPct, d)),
                        ResultRow("Cost Price", Format.money(cost, s, d))
                    ),
                    formula = "Selling Price = Cost × (1 + Markup ÷ 100). Margin = Profit ÷ Selling Price × 100.",
                    breakdown = listOf(
                        "Cost: ${Format.money(cost, s, d)}",
                        "Markup ${Format.pct(markupPct, d)} adds ${Format.money(profit, s, d)}",
                        "Selling price: ${Format.money(price, s, d)} (margin ${Format.pct(margin, d)})"
                    ),
                    tip = "Markup and margin are different: 40% markup on cost is about 28.6% margin on price."
                )
            }
        ),
        CalculatorDef(
            id = "roi", title = "ROI Calculator",
            description = "Return on investment for any project or purchase.",
            category = Category.BUSINESS, icon = "roi",
            inputs = listOf(
                CalcInput("invested", "Amount Invested ($)", defaultValue = "10000"),
                CalcInput("returned", "Amount Returned ($)", defaultValue = "13500"),
                CalcInput("years", "Investment Period (Years)", defaultValue = "2")
            ),
            compute = { v, s, d ->
                val invested = num(v, "invested")
                val returned = num(v, "returned")
                val gain = returned - invested
                val roi = if (invested > 0) gain / invested * 100 else 0.0
                val years = num(v, "years")
                val annualized = if (invested > 0 && years > 0) (Math.pow(returned / invested, 1.0 / years) - 1) * 100 else 0.0
                CalcResult(
                    title = Format.pct(roi, d),
                    subtitle = "Return on Investment",
                    rows = listOf(
                        ResultRow("ROI", Format.pct(roi, d), highlight = true),
                        ResultRow("Annualized ROI", Format.pct(annualized, d)),
                        ResultRow("Net Gain", Format.money(gain, s, d)),
                        ResultRow("Amount Invested", Format.money(invested, s, d)),
                        ResultRow("Amount Returned", Format.money(returned, s, d))
                    ),
                    formula = "ROI = (Returned − Invested) ÷ Invested × 100. Annualized ROI = (Returned ÷ Invested)^(1 ÷ Years) − 1.",
                    breakdown = listOf(
                        "Invested: ${Format.money(invested, s, d)}",
                        "Returned: ${Format.money(returned, s, d)}",
                        "Gain: ${Format.money(gain, s, d)} — ROI ${Format.pct(roi, d)} (${Format.pct(annualized, d)} per year)"
                    ),
                    chart = listOf(
                        ChartBar("Invested", invested),
                        ChartBar("Returned", returned)
                    ),
                    tip = "Compare ROI with the time it took — a 35% return in 2 years beats 40% in 5 years."
                )
            }
        ),
        CalculatorDef(
            id = "break-even", title = "Break-Even Calculator",
            description = "How many units you must sell to cover all costs.",
            category = Category.BUSINESS, icon = "breakeven",
            inputs = listOf(
                CalcInput("fixed", "Fixed Costs ($ per month)", defaultValue = "5000"),
                CalcInput("price", "Price per Unit ($)", defaultValue = "50"),
                CalcInput("variable", "Variable Cost per Unit ($)", defaultValue = "30")
            ),
            compute = { v, s, d ->
                val fixed = num(v, "fixed")
                val price = num(v, "price")
                val variable = num(v, "variable")
                val contribution = price - variable
                val units = if (contribution > 0) fixed / contribution else Double.POSITIVE_INFINITY
                val revenue = if (units.isInfinite()) 0.0 else units * price
                CalcResult(
                    title = if (units.isInfinite()) "Never" else "${Format.whole(units)} units",
                    subtitle = "Break-Even Point (per month)",
                    rows = listOf(
                        ResultRow("Break-Even Units",
                            if (units.isInfinite()) "Price must exceed variable cost"
                            else Format.whole(units), highlight = true),
                        ResultRow("Break-Even Revenue", Format.money(revenue, s, d)),
                        ResultRow("Contribution per Unit", Format.money(contribution, s, d)),
                        ResultRow("Contribution Margin",
                            Format.pct(if (price > 0) contribution / price * 100 else 0.0, d)),
                        ResultRow("Fixed Costs", Format.money(fixed, s, d))
                    ),
                    formula = "Break-Even Units = Fixed Costs ÷ (Price per Unit − Variable Cost per Unit).",
                    breakdown = listOf(
                        "Fixed costs: ${Format.money(fixed, s, d)}",
                        "Contribution per unit: ${Format.money(contribution, s, d)}",
                        "Break-even: ${if (units.isInfinite()) "not possible at this price" else Format.whole(units) + " units / month"}",
                        "Break-even revenue: ${Format.money(revenue, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Fixed Costs", fixed),
                        ChartBar("Break-Even Revenue", revenue)
                    ),
                    tip = "Every sale above break-even is profit — lowering fixed costs moves break-even down fastest."
                )
            }
        ),
        CalculatorDef(
            id = "business-loan", title = "Business Loan Calculator",
            description = "Monthly payment and total cost of a business loan.",
            category = Category.BUSINESS, icon = "businessloan",
            inputs = listOf(
                CalcInput("amount", "Loan Amount ($)", defaultValue = "50000"),
                CalcInput("rate", "Annual Interest Rate (%)", defaultValue = "8.0"),
                CalcInput("years", "Loan Term (Years)", type = InputType.SELECT,
                    options = listOf("1", "2", "3", "4", "5", "7", "10"), defaultValue = "5")
            ),
            compute = { v, s, d ->
                val principal = num(v, "amount")
                val monthly = emi(principal, num(v, "rate"), num(v, "years"))
                val total = monthly * num(v, "years") * 12.0
                val interest = total - principal
                CalcResult(
                    title = Format.money(monthly, s, d),
                    subtitle = "Monthly Payment",
                    rows = listOf(
                        ResultRow("Monthly Payment", Format.money(monthly, s, d), highlight = true),
                        ResultRow("Total Interest", Format.money(interest, s, d)),
                        ResultRow("Total Payment", Format.money(total, s, d)),
                        ResultRow("Loan Amount", Format.money(principal, s, d))
                    ),
                    formula = "M = P × r(1+r)ⁿ / ((1+r)ⁿ − 1) — the standard loan payment formula.",
                    breakdown = listOf(
                        "Loan amount: ${Format.money(principal, s, d)}",
                        "Monthly payment: ${Format.money(monthly, s, d)}",
                        "Total interest: ${Format.money(interest, s, d)}",
                        "Total payment: ${Format.money(total, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Principal", principal),
                        ChartBar("Interest", interest)
                    ),
                    tip = "Check whether interest is tax-deductible for your business — it often is."
                )
            }
        ),
        CalculatorDef(
            id = "sales-commission", title = "Sales Commission Calculator",
            description = "Commission earned on sales, with tiered rates.",
            category = Category.BUSINESS, icon = "commission",
            inputs = listOf(
                CalcInput("sales", "Total Sales ($)", defaultValue = "50000"),
                CalcInput("rate", "Commission Rate (%)", defaultValue = "5"),
                CalcInput("base", "Base Salary ($)", defaultValue = "3000")
            ),
            compute = { v, s, d ->
                val sales = num(v, "sales")
                val commission = sales * num(v, "rate") / 100.0
                val total = num(v, "base") + commission
                CalcResult(
                    title = Format.money(commission, s, d),
                    subtitle = "Commission Earned",
                    rows = listOf(
                        ResultRow("Commission", Format.money(commission, s, d), highlight = true),
                        ResultRow("Total Pay (Base + Commission)", Format.money(total, s, d)),
                        ResultRow("Base Salary", Format.money(num(v, "base"), s, d)),
                        ResultRow("Total Sales", Format.money(sales, s, d)),
                        ResultRow("Commission Rate", Format.pct(num(v, "rate"), d))
                    ),
                    formula = "Commission = Total Sales × Commission Rate ÷ 100. Total Pay = Base Salary + Commission.",
                    breakdown = listOf(
                        "Sales: ${Format.money(sales, s, d)}",
                        "Commission at ${Format.pct(num(v, "rate"), d)}: ${Format.money(commission, s, d)}",
                        "Total pay: ${Format.money(total, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Base Salary", num(v, "base")),
                        ChartBar("Commission", commission)
                    )
                )
            }
        ),
        CalculatorDef(
            id = "cash-flow", title = "Cash Flow Calculator",
            description = "Net cash flow from operating, investing and financing.",
            category = Category.BUSINESS, icon = "cashflow",
            inputs = listOf(
                CalcInput("inflows", "Cash Inflows ($)", defaultValue = "80000"),
                CalcInput("outflows", "Cash Outflows ($)", defaultValue = "60000"),
                CalcInput("opening", "Opening Balance ($)", defaultValue = "10000")
            ),
            compute = { v, s, d ->
                val net = num(v, "inflows") - num(v, "outflows")
                val closing = num(v, "opening") + net
                CalcResult(
                    title = Format.money(net, s, d),
                    subtitle = if (net >= 0) "Positive Net Cash Flow" else "Negative Net Cash Flow",
                    rows = listOf(
                        ResultRow("Net Cash Flow", Format.money(net, s, d), highlight = true),
                        ResultRow("Closing Balance", Format.money(closing, s, d)),
                        ResultRow("Cash Inflows", Format.money(num(v, "inflows"), s, d)),
                        ResultRow("Cash Outflows", Format.money(num(v, "outflows"), s, d)),
                        ResultRow("Opening Balance", Format.money(num(v, "opening"), s, d))
                    ),
                    formula = "Net Cash Flow = Cash Inflows − Cash Outflows. Closing Balance = Opening Balance + Net Cash Flow.",
                    breakdown = listOf(
                        "Inflows: ${Format.money(num(v, "inflows"), s, d)}",
                        "Outflows: ${Format.money(num(v, "outflows"), s, d)}",
                        "Net flow: ${Format.money(net, s, d)}",
                        "Closing balance: ${Format.money(closing, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Inflows", num(v, "inflows")),
                        ChartBar("Outflows", num(v, "outflows"))
                    ),
                    tip = if (net < 0) "Negative cash flow burns reserves — cut costs or speed up collections."
                    else "Positive cash flow — consider building a buffer for slow months."
                )
            }
        ),
        CalculatorDef(
            id = "revenue", title = "Revenue Calculator",
            description = "Total revenue from units sold and price.",
            category = Category.BUSINESS, icon = "revenue",
            inputs = listOf(
                CalcInput("units", "Units Sold", defaultValue = "1000"),
                CalcInput("price", "Price per Unit ($)", defaultValue = "49.99")
            ),
            compute = { v, s, d ->
                val revenue = num(v, "units") * num(v, "price")
                CalcResult(
                    title = Format.money(revenue, s, d),
                    subtitle = "Total Revenue",
                    rows = listOf(
                        ResultRow("Total Revenue", Format.money(revenue, s, d), highlight = true),
                        ResultRow("Units Sold", Format.whole(num(v, "units"))),
                        ResultRow("Price per Unit", Format.money(num(v, "price"), s, d)),
                        ResultRow("Monthly (÷12)", Format.money(revenue / 12.0, s, d))
                    ),
                    formula = "Revenue = Units Sold × Price per Unit.",
                    breakdown = listOf(
                        "${Format.whole(num(v, "units"))} units × ${Format.money(num(v, "price"), s, d)} = ${Format.money(revenue, s, d)}"
                    ),
                    chart = listOf(ChartBar("Revenue", revenue))
                )
            }
        ),
        CalculatorDef(
            id = "gross-profit", title = "Gross Profit Calculator",
            description = "Gross profit and gross margin from revenue and COGS.",
            category = Category.BUSINESS, icon = "gross",
            inputs = listOf(
                CalcInput("revenue", "Revenue ($)", defaultValue = "120000"),
                CalcInput("cogs", "Cost of Goods Sold ($)", defaultValue = "70000")
            ),
            compute = { v, s, d ->
                val revenue = num(v, "revenue")
                val gross = revenue - num(v, "cogs")
                val margin = if (revenue > 0) gross / revenue * 100 else 0.0
                CalcResult(
                    title = Format.money(gross, s, d),
                    subtitle = "Gross Profit",
                    rows = listOf(
                        ResultRow("Gross Profit", Format.money(gross, s, d), highlight = true),
                        ResultRow("Gross Margin", Format.pct(margin, d)),
                        ResultRow("Revenue", Format.money(revenue, s, d)),
                        ResultRow("Cost of Goods Sold", Format.money(num(v, "cogs"), s, d))
                    ),
                    formula = "Gross Profit = Revenue − Cost of Goods Sold. Margin = Gross Profit ÷ Revenue × 100.",
                    breakdown = listOf(
                        "Revenue: ${Format.money(revenue, s, d)}",
                        "COGS: ${Format.money(num(v, "cogs"), s, d)}",
                        "Gross profit: ${Format.money(gross, s, d)} (${Format.pct(margin, d)} margin)"
                    ),
                    chart = listOf(
                        ChartBar("COGS", num(v, "cogs")),
                        ChartBar("Gross Profit", gross.coerceAtLeast(0.0))
                    )
                )
            }
        ),
        CalculatorDef(
            id = "net-profit", title = "Net Profit Calculator",
            description = "Bottom-line profit after all costs, expenses and tax.",
            category = Category.BUSINESS, icon = "netprofit",
            inputs = listOf(
                CalcInput("revenue", "Revenue ($)", defaultValue = "150000"),
                CalcInput("cogs", "Cost of Goods Sold ($)", defaultValue = "70000"),
                CalcInput("expenses", "Operating Expenses ($)", defaultValue = "40000"),
                CalcInput("tax", "Tax ($)", defaultValue = "8000")
            ),
            compute = { v, s, d ->
                val revenue = num(v, "revenue")
                val net = revenue - num(v, "cogs") - num(v, "expenses") - num(v, "tax")
                val margin = if (revenue > 0) net / revenue * 100 else 0.0
                CalcResult(
                    title = Format.money(net, s, d),
                    subtitle = "Net Profit",
                    rows = listOf(
                        ResultRow("Net Profit", Format.money(net, s, d), highlight = true),
                        ResultRow("Net Profit Margin", Format.pct(margin, d)),
                        ResultRow("Revenue", Format.money(revenue, s, d)),
                        ResultRow("Total Costs", Format.money(revenue - net, s, d))
                    ),
                    formula = "Net Profit = Revenue − COGS − Operating Expenses − Tax.",
                    breakdown = listOf(
                        "Revenue: ${Format.money(revenue, s, d)}",
                        "COGS: ${Format.money(num(v, "cogs"), s, d)}",
                        "Expenses: ${Format.money(num(v, "expenses"), s, d)}",
                        "Tax: ${Format.money(num(v, "tax"), s, d)}",
                        "Net profit: ${Format.money(net, s, d)} (${Format.pct(margin, d)} margin)"
                    ),
                    chart = listOf(
                        ChartBar("Costs", revenue - net),
                        ChartBar("Net Profit", net.coerceAtLeast(0.0))
                    ),
                    tip = "Net profit is the bottom line — the money the business actually keeps."
                )
            }
        )
    )
}
