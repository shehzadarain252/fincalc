package com.fincalc.app.data

import com.fincalc.app.util.Format

/** Category 4 — Personal Finance Calculators (10). */
object CalculatorsPersonal {

    val all: List<CalculatorDef> = listOf(
        CalculatorDef(
            id = "budget-planner", title = "Monthly Budget Planner",
            description = "Plan your month with the 50/30/20 rule and see what is left.",
            category = Category.PERSONAL, icon = "budget", popular = true,
            inputs = listOf(
                CalcInput("income", "Monthly Income ($)", defaultValue = "5000"),
                CalcInput("housing", "Housing ($)", defaultValue = "1500"),
                CalcInput("food", "Food & Groceries ($)", defaultValue = "600"),
                CalcInput("transport", "Transport ($)", defaultValue = "400"),
                CalcInput("utilities", "Utilities & Bills ($)", defaultValue = "300"),
                CalcInput("entertainment", "Entertainment ($)", defaultValue = "300"),
                CalcInput("other", "Other Expenses ($)", defaultValue = "200"),
                CalcInput("savings", "Savings ($)", defaultValue = "500")
            ),
            compute = { v, s, d ->
                val income = num(v, "income")
                val housing = num(v, "housing")
                val food = num(v, "food")
                val transport = num(v, "transport")
                val utilities = num(v, "utilities")
                val ent = num(v, "entertainment")
                val other = num(v, "other")
                val savings = num(v, "savings")
                val needs = housing + food + transport + utilities
                val wants = ent + other
                val totalOut = needs + wants + savings
                val remaining = income - totalOut
                CalcResult(
                    title = Format.money(remaining, s, d),
                    subtitle = if (remaining >= 0) "Left Over Each Month" else "Over Budget by",
                    rows = listOf(
                        ResultRow("Left Over", Format.money(remaining, s, d), highlight = true),
                        ResultRow("Needs (50% target)", Format.money(needs, s, d) + " — " + Format.pct(if (income > 0) needs / income * 100 else 0.0, 0)),
                        ResultRow("Wants (30% target)", Format.money(wants, s, d) + " — " + Format.pct(if (income > 0) wants / income * 100 else 0.0, 0)),
                        ResultRow("Savings (20% target)", Format.money(savings, s, d) + " — " + Format.pct(if (income > 0) savings / income * 100 else 0.0, 0)),
                        ResultRow("Total Spending", Format.money(totalOut, s, d)),
                        ResultRow("Monthly Income", Format.money(income, s, d))
                    ),
                    formula = "50/30/20 rule: 50% of income for needs, 30% for wants, 20% for savings and debt payoff.",
                    breakdown = listOf(
                        "Needs: housing ${Format.money(housing, s, d)}, food ${Format.money(food, s, d)}, transport ${Format.money(transport, s, d)}, utilities ${Format.money(utilities, s, d)}",
                        "Wants: ${Format.money(wants, s, d)}",
                        "Savings: ${Format.money(savings, s, d)}",
                        "Left over: ${Format.money(remaining, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Needs", needs),
                        ChartBar("Wants", wants),
                        ChartBar("Savings", savings),
                        ChartBar("Left Over", remaining.coerceAtLeast(0.0))
                    ),
                    tip = "If needs take more than 50%, trim wants first — and automate savings on payday."
                )
            }
        ),
        CalculatorDef(
            id = "savings-goal", title = "Savings Calculator",
            description = "How long until you reach your savings goal?",
            category = Category.PERSONAL, icon = "goal",
            inputs = listOf(
                CalcInput("goal", "Savings Goal ($)", defaultValue = "10000"),
                CalcInput("current", "Current Savings ($)", defaultValue = "2000"),
                CalcInput("monthly", "Monthly Contribution ($)", defaultValue = "300"),
                CalcInput("rate", "Annual Interest Rate (%)", defaultValue = "4")
            ),
            compute = { v, s, d ->
                val goal = num(v, "goal")
                val current = num(v, "current")
                val monthly = num(v, "monthly")
                val r = num(v, "rate") / 100.0 / 12.0
                var months = 0.0
                var balance = current
                if (monthly > 0 || r > 0) {
                    // Solve FV: goal = current*(1+r)^n + monthly*((1+r)^n - 1)/r
                    var n = 0
                    var b = current
                    while (b < goal && n < 1200) {
                        b = b * (1 + r) + monthly
                        n++
                    }
                    months = n.toDouble()
                    balance = b
                }
                val years = months / 12.0
                val totalSaved = current + monthly * months
                CalcResult(
                    title = if (months >= 1200) "Over 100 years" else "${Format.num(months, 0)} months",
                    subtitle = "Time to Reach Your Goal",
                    rows = listOf(
                        ResultRow("Time Needed",
                            if (months >= 1200) "Over 100 years — raise the monthly amount"
                            else "${Format.num(months, 0)} months (${Format.num(years, 1)} years)", highlight = true),
                        ResultRow("Savings Goal", Format.money(goal, s, d)),
                        ResultRow("You Will Have", Format.money(balance, s, d)),
                        ResultRow("Total Deposited", Format.money(totalSaved, s, d)),
                        ResultRow("Interest Earned", Format.money(balance - totalSaved, s, d))
                    ),
                    formula = "Each month the balance grows by interest, then your contribution is added — repeated until the goal is reached.",
                    breakdown = listOf(
                        "Goal: ${Format.money(goal, s, d)}",
                        "Starting: ${Format.money(current, s, d)}",
                        "Monthly: ${Format.money(monthly, s, d)} at ${Format.pct(num(v, "rate"), d)}",
                        "Months needed: ${Format.num(months, 0)}"
                    ),
                    chart = listOf(
                        ChartBar("Deposited", totalSaved),
                        ChartBar("Interest", (balance - totalSaved).coerceAtLeast(0.0))
                    ),
                    tip = "Raising the monthly amount even a little cuts the time sharply."
                )
            }
        ),
        CalculatorDef(
            id = "emergency-fund", title = "Emergency Fund Calculator",
            description = "How big your safety net should be.",
            category = Category.PERSONAL, icon = "emergency",
            inputs = listOf(
                CalcInput("expenses", "Monthly Essential Expenses ($)", defaultValue = "3000"),
                CalcInput("months", "Months of Cover", type = InputType.SELECT,
                    options = listOf("3", "6", "9", "12"), defaultValue = "6")
            ),
            compute = { v, s, d ->
                val expenses = num(v, "expenses")
                val months = num(v, "months")
                val target = expenses * months
                CalcResult(
                    title = Format.money(target, s, d),
                    subtitle = "Emergency Fund Target (${Format.num(months, 0)} months)",
                    rows = listOf(
                        ResultRow("Fund Target", Format.money(target, s, d), highlight = true),
                        ResultRow("Monthly Expenses", Format.money(expenses, s, d)),
                        ResultRow("Months of Cover", Format.num(months, 0)),
                        ResultRow("Weekly Saving (1 year)", Format.money(target / 52.0, s, d))
                    ),
                    formula = "Emergency Fund = Monthly Essential Expenses × Months of Cover.",
                    breakdown = listOf(
                        "Monthly essentials: ${Format.money(expenses, s, d)}",
                        "Cover: ${Format.num(months, 0)} months",
                        "Target: ${Format.money(target, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("1 Month", expenses),
                        ChartBar("Target", target)
                    ),
                    tip = "3 months is the minimum; 6 months is the usual recommendation — 9–12 if income is irregular."
                )
            }
        ),
        CalculatorDef(
            id = "credit-card-payoff", title = "Credit Card Payoff Calculator",
            description = "How long to clear a card balance — and what it really costs.",
            category = Category.PERSONAL, icon = "card",
            inputs = listOf(
                CalcInput("balance", "Card Balance ($)", defaultValue = "5000"),
                CalcInput("apr", "APR (%)", defaultValue = "22.0"),
                CalcInput("payment", "Monthly Payment ($)", defaultValue = "200")
            ),
            compute = { v, s, d ->
                val balance = num(v, "balance")
                val r = num(v, "apr") / 100.0 / 12.0
                val pay = num(v, "payment")
                val months = if (pay <= 0) Double.POSITIVE_INFINITY
                else if (r == 0.0) balance / pay
                else if (pay <= balance * r) Double.POSITIVE_INFINITY
                else -Math.log(1 - balance * r / pay) / Math.log(1 + r)
                val totalInterest = if (months.isInfinite()) Double.POSITIVE_INFINITY else pay * months - balance
                CalcResult(
                    title = if (months.isInfinite()) "Never" else "${Format.num(months, 0)} months",
                    subtitle = "Time to Pay Off",
                    rows = listOf(
                        ResultRow("Time to Pay Off",
                            if (months.isInfinite()) "Never — payment is below the monthly interest"
                            else "${Format.num(months, 0)} months (${Format.num(months / 12.0, 1)} years)", highlight = true),
                        ResultRow("Total Interest",
                            if (totalInterest.isInfinite()) "—" else Format.money(totalInterest, s, d)),
                        ResultRow("Total Paid",
                            if (months.isInfinite()) "—" else Format.money(pay * months, s, d)),
                        ResultRow("Card Balance", Format.money(balance, s, d))
                    ),
                    formula = "Months n = −ln(1 − r·B / P) / ln(1 + r) — B = balance, r = monthly APR, P = monthly payment.",
                    breakdown = listOf(
                        "Balance: ${Format.money(balance, s, d)} at ${Format.pct(num(v, "apr"), d)} APR",
                        "Monthly payment: ${Format.money(pay, s, d)}",
                        "Months: ${if (months.isInfinite()) "never (payment too low)" else Format.num(months, 0)}",
                        "Total interest: ${if (totalInterest.isInfinite()) "—" else Format.money(totalInterest, s, d)}"
                    ),
                    tip = "Paying only the minimum can take decades — pay more than the minimum whenever you can."
                )
            }
        ),
        CalculatorDef(
            id = "credit-card-interest", title = "Credit Card Interest Calculator",
            description = "What your card balance costs you in interest each month.",
            category = Category.PERSONAL, icon = "cardinterest",
            inputs = listOf(
                CalcInput("balance", "Card Balance ($)", defaultValue = "3000"),
                CalcInput("apr", "APR (%)", defaultValue = "22.0")
            ),
            compute = { v, s, d ->
                val balance = num(v, "balance")
                val monthly = balance * num(v, "apr") / 100.0 / 12.0
                CalcResult(
                    title = Format.money(monthly, s, d),
                    subtitle = "Interest Charged per Month",
                    rows = listOf(
                        ResultRow("Monthly Interest", Format.money(monthly, s, d), highlight = true),
                        ResultRow("Annual Interest", Format.money(monthly * 12.0, s, d)),
                        ResultRow("Daily Interest", Format.money(monthly * 12.0 / 365.0, s, d)),
                        ResultRow("Card Balance", Format.money(balance, s, d)),
                        ResultRow("APR", Format.pct(num(v, "apr"), d))
                    ),
                    formula = "Monthly Interest = Balance × APR ÷ 100 ÷ 12.",
                    breakdown = listOf(
                        "Balance: ${Format.money(balance, s, d)}",
                        "APR: ${Format.pct(num(v, "apr"), d)}",
                        "Monthly interest: ${Format.money(monthly, s, d)}",
                        "Yearly interest: ${Format.money(monthly * 12.0, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Balance", balance),
                        ChartBar("Yearly Interest", monthly * 12.0)
                    ),
                    tip = "Interest is charged on the average daily balance — paying mid-month reduces it."
                )
            }
        ),
        CalculatorDef(
            id = "debt-payoff", title = "Debt Payoff Calculator",
            description = "Your debt-free date with a fixed monthly payment.",
            category = Category.PERSONAL, icon = "debt",
            inputs = listOf(
                CalcInput("debt", "Total Debt ($)", defaultValue = "15000"),
                CalcInput("apr", "Interest Rate (%)", defaultValue = "15.0"),
                CalcInput("payment", "Monthly Payment ($)", defaultValue = "500")
            ),
            compute = { v, s, d ->
                val debt = num(v, "debt")
                val r = num(v, "apr") / 100.0 / 12.0
                val pay = num(v, "payment")
                val months = if (pay <= 0) Double.POSITIVE_INFINITY
                else if (r == 0.0) debt / pay
                else if (pay <= debt * r) Double.POSITIVE_INFINITY
                else -Math.log(1 - debt * r / pay) / Math.log(1 + r)
                val totalInterest = if (months.isInfinite()) Double.POSITIVE_INFINITY else pay * months - debt
                CalcResult(
                    title = if (months.isInfinite()) "Never" else "${Format.num(months, 0)} months",
                    subtitle = "Time to Become Debt-Free",
                    rows = listOf(
                        ResultRow("Debt-Free In",
                            if (months.isInfinite()) "Never — payment is below the monthly interest"
                            else "${Format.num(months, 0)} months (${Format.num(months / 12.0, 1)} years)", highlight = true),
                        ResultRow("Total Interest",
                            if (totalInterest.isInfinite()) "—" else Format.money(totalInterest, s, d)),
                        ResultRow("Total Paid",
                            if (months.isInfinite()) "—" else Format.money(pay * months, s, d)),
                        ResultRow("Total Debt", Format.money(debt, s, d))
                    ),
                    formula = "Months n = −ln(1 − r·D / P) / ln(1 + r) — D = debt, r = monthly rate, P = monthly payment.",
                    breakdown = listOf(
                        "Debt: ${Format.money(debt, s, d)} at ${Format.pct(num(v, "apr"), d)}",
                        "Monthly payment: ${Format.money(pay, s, d)}",
                        "Total interest: ${if (totalInterest.isInfinite()) "—" else Format.money(totalInterest, s, d)}"
                    ),
                    tip = "Debt snowball: pay the smallest balance first for momentum. Debt avalanche: pay the highest rate first to save the most interest."
                )
            }
        ),
        CalculatorDef(
            id = "net-worth", title = "Net Worth Calculator",
            description = "What you own minus what you owe.",
            category = Category.PERSONAL, icon = "networth",
            inputs = listOf(
                CalcInput("cash", "Cash & Bank ($)", defaultValue = "8000"),
                CalcInput("investments", "Investments ($)", defaultValue = "25000"),
                CalcInput("property", "Property Value ($)", defaultValue = "300000"),
                CalcInput("vehicles", "Vehicles & Other Assets ($)", defaultValue = "15000"),
                CalcInput("mortgage", "Mortgage Owed ($)", defaultValue = "220000"),
                CalcInput("loans", "Loans Owed ($)", defaultValue = "10000"),
                CalcInput("cards", "Credit Cards Owed ($)", defaultValue = "3000")
            ),
            compute = { v, s, d ->
                val assets = num(v, "cash") + num(v, "investments") + num(v, "property") + num(v, "vehicles")
                val debts = num(v, "mortgage") + num(v, "loans") + num(v, "cards")
                val net = assets - debts
                CalcResult(
                    title = Format.money(net, s, d),
                    subtitle = "Your Net Worth",
                    rows = listOf(
                        ResultRow("Net Worth", Format.money(net, s, d), highlight = true),
                        ResultRow("Total Assets", Format.money(assets, s, d)),
                        ResultRow("Total Liabilities", Format.money(debts, s, d)),
                        ResultRow("Cash & Bank", Format.money(num(v, "cash"), s, d)),
                        ResultRow("Investments", Format.money(num(v, "investments"), s, d)),
                        ResultRow("Property", Format.money(num(v, "property"), s, d))
                    ),
                    formula = "Net Worth = Total Assets − Total Liabilities.",
                    breakdown = listOf(
                        "Assets: ${Format.money(assets, s, d)}",
                        "Liabilities: ${Format.money(debts, s, d)}",
                        "Net worth: ${Format.money(net, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Assets", assets),
                        ChartBar("Debts", debts)
                    ),
                    tip = "Track net worth every few months — the trend matters more than any single number."
                )
            }
        ),
        CalculatorDef(
            id = "expense", title = "Expense Calculator",
            description = "Add up all monthly expenses in one place.",
            category = Category.PERSONAL, icon = "expense",
            inputs = listOf(
                CalcInput("housing", "Housing ($)", defaultValue = "1200"),
                CalcInput("food", "Food ($)", defaultValue = "500"),
                CalcInput("transport", "Transport ($)", defaultValue = "300"),
                CalcInput("utilities", "Utilities ($)", defaultValue = "250"),
                CalcInput("insurance", "Insurance ($)", defaultValue = "200"),
                CalcInput("entertainment", "Entertainment ($)", defaultValue = "150"),
                CalcInput("other", "Other ($)", defaultValue = "100")
            ),
            compute = { v, s, d ->
                val items = listOf(
                    "Housing" to num(v, "housing"), "Food" to num(v, "food"),
                    "Transport" to num(v, "transport"), "Utilities" to num(v, "utilities"),
                    "Insurance" to num(v, "insurance"), "Entertainment" to num(v, "entertainment"),
                    "Other" to num(v, "other")
                )
                val monthly = items.sumOf { it.second }
                CalcResult(
                    title = Format.money(monthly, s, d),
                    subtitle = "Total Monthly Expenses",
                    rows = listOf(
                        ResultRow("Monthly Expenses", Format.money(monthly, s, d), highlight = true),
                        ResultRow("Annual Expenses", Format.money(monthly * 12.0, s, d)),
                        ResultRow("Weekly Expenses", Format.money(monthly * 12.0 / 52.0, s, d))
                    ) + items.map { (label, value) -> ResultRow(label, Format.money(value, s, d)) },
                    formula = "Total Expenses = the sum of every expense category.",
                    breakdown = items.map { (label, value) -> "$label: ${Format.money(value, s, d)}" } +
                        listOf("Monthly total: ${Format.money(monthly, s, d)}", "Annual total: ${Format.money(monthly * 12.0, s, d)}"),
                    chart = items.map { (label, value) -> ChartBar(label, value) },
                    tip = "The biggest category is usually housing — that is where savings have the most impact."
                )
            }
        ),
        CalculatorDef(
            id = "monthly-expense-planner", title = "Monthly Expense Planner",
            description = "Income vs expenses — your savings rate at a glance.",
            category = Category.PERSONAL, icon = "planner",
            inputs = listOf(
                CalcInput("income", "Monthly Income ($)", defaultValue = "5000"),
                CalcInput("housing", "Housing ($)", defaultValue = "1500"),
                CalcInput("food", "Food ($)", defaultValue = "600"),
                CalcInput("transport", "Transport ($)", defaultValue = "400"),
                CalcInput("bills", "Bills & Subscriptions ($)", defaultValue = "350"),
                CalcInput("fun", "Fun & Shopping ($)", defaultValue = "300"),
                CalcInput("other", "Other ($)", defaultValue = "150")
            ),
            compute = { v, s, d ->
                val income = num(v, "income")
                val expenses = num(v, "housing") + num(v, "food") + num(v, "transport") +
                    num(v, "bills") + num(v, "fun") + num(v, "other")
                val remaining = income - expenses
                val savingsRate = if (income > 0) remaining / income * 100 else 0.0
                CalcResult(
                    title = Format.money(remaining, s, d),
                    subtitle = "Left Over Each Month",
                    rows = listOf(
                        ResultRow("Left Over", Format.money(remaining, s, d), highlight = true),
                        ResultRow("Savings Rate", Format.pct(savingsRate, d)),
                        ResultRow("Total Expenses", Format.money(expenses, s, d)),
                        ResultRow("Monthly Income", Format.money(income, s, d)),
                        ResultRow("Annual Savings", Format.money(remaining * 12.0, s, d))
                    ),
                    formula = "Left Over = Income − All Expenses. Savings Rate = Left Over ÷ Income × 100.",
                    breakdown = listOf(
                        "Income: ${Format.money(income, s, d)}",
                        "Expenses: ${Format.money(expenses, s, d)}",
                        "Left over: ${Format.money(remaining, s, d)} (${Format.pct(savingsRate, d)} savings rate)"
                    ),
                    chart = listOf(
                        ChartBar("Expenses", expenses),
                        ChartBar("Left Over", remaining.coerceAtLeast(0.0))
                    ),
                    tip = "A 20% savings rate is a strong target — automate it on payday so it happens first."
                )
            }
        ),
        CalculatorDef(
            id = "financial-goal", title = "Financial Goal Calculator",
            description = "How much to save each month to hit a goal on time.",
            category = Category.PERSONAL, icon = "flag",
            inputs = listOf(
                CalcInput("goal", "Goal Amount ($)", defaultValue = "20000"),
                CalcInput("current", "Already Saved ($)", defaultValue = "5000"),
                CalcInput("years", "Years to Reach Goal", defaultValue = "3"),
                CalcInput("rate", "Annual Interest Rate (%)", defaultValue = "4")
            ),
            compute = { v, s, d ->
                val goal = num(v, "goal")
                val current = num(v, "current")
                val years = num(v, "years")
                val r = num(v, "rate") / 100.0 / 12.0
                val n = years * 12.0
                val fvCurrent = current * Math.pow(1 + r, n)
                val needed = (goal - fvCurrent).coerceAtLeast(0.0)
                val monthly = if (n <= 0) needed
                else if (r == 0.0) needed / n
                else needed * r / (Math.pow(1 + r, n) - 1)
                CalcResult(
                    title = Format.money(monthly, s, d),
                    subtitle = "Monthly Saving Needed",
                    rows = listOf(
                        ResultRow("Save per Month", Format.money(monthly, s, d), highlight = true),
                        ResultRow("Save per Week", Format.money(monthly * 12.0 / 52.0, s, d)),
                        ResultRow("Goal Amount", Format.money(goal, s, d)),
                        ResultRow("Already Saved", Format.money(current, s, d)),
                        ResultRow("Time", "${Format.num(years, 1)} years")
                    ),
                    formula = "Monthly Saving = (Goal − Current Savings grown with interest) ÷ annuity factor for the remaining months.",
                    breakdown = listOf(
                        "Goal: ${Format.money(goal, s, d)} in ${Format.num(years, 1)} years",
                        "Current savings will grow to: ${Format.money(fvCurrent, s, d)}",
                        "Still needed: ${Format.money(needed, s, d)}",
                        "Monthly saving: ${Format.money(monthly, s, d)}"
                    ),
                    chart = listOf(
                        ChartBar("Already Saved", current),
                        ChartBar("Still to Save", needed)
                    ),
                    tip = "Break the goal into monthly milestones and track them — small wins keep you going."
                )
            }
        )
    )
}
