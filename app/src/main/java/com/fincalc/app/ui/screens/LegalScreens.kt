package com.fincalc.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LegalPage(title: String, body: String) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
    ) {
        Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp))
        Text(body, fontSize = 14.sp, lineHeight = 21.sp,
            color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun AboutScreen() {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
    ) {
        Text("FinCalc", fontSize = 30.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 16.dp))
        Text("Smart Financial Calculators for Everyone", fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        Text(
            "FinCalc is a powerful and easy-to-use finance calculator app that helps you " +
                "make smart financial decisions. From loans and taxes to investments and " +
                "budgeting, FinCalc has everything you need in one place.\n\n" +
                "• 60 calculators across 6 categories: loans & mortgages, tax & salary, " +
                "investments, personal finance, business, and currency & daily tools.\n" +
                "• Every calculation works offline (except live currency rates).\n" +
                "• Save calculations to history, mark favorites, and export results as PDF.\n" +
                "• No account or login needed — your data stays on your device.\n\n" +
                "Version 1.0.0\n\n" +
                "© 2026 FinCalc. All rights reserved.",
            fontSize = 14.sp, lineHeight = 21.sp
        )
        Spacer(Modifier.height(24.dp))
    }
}

private const val PRIVACY_TEXT = """Last updated: November 2026

FinCalc respects your privacy. This policy explains what the app collects and how it is used.

1. Information we collect
FinCalc does not ask you to create an account, and we do not collect your name, email address, contacts, location, or financial account details. Calculations you enter stay on your device in local storage (history and favorites) unless you delete them.

2. Advertising
FinCalc shows ads through Google AdMob. AdMob and its partners may collect advertising identifiers and usage data to serve ads, including personalized ads where you have consented. You can change your advertising consent at any time in Settings → Privacy Settings. We never sell personal information.

3. Analytics and crash reports
The app uses Firebase Analytics and Crashlytics to understand anonymous usage (for example, which calculators are popular) and to fix crashes. This data is anonymous and is not linked to your identity.

4. Currency rates
The Currency Converter fetches exchange rates from a public rates service (open.er-api.com, with Frankfurter as a fallback). Your request goes to that service; no personal information is sent with it.

5. Third-party services
Google AdMob, Google User Messaging Platform (consent), and Firebase (Analytics and Crashlytics) process data under their own privacy policies.

6. Children's privacy
FinCalc is not directed at children under 13, and we do not knowingly collect information from children.

7. Your choices
You can clear all locally stored data in Settings → Clear App Data, and change ad consent in Settings → Privacy Settings. Uninstalling the app removes all local data.

8. Changes
If this policy changes, the new version will be shown in the app.

Contact: $SUPPORT_EMAIL"""

private const val TERMS_TEXT = """Last updated: November 2026

1. Acceptance
By using FinCalc you agree to these Terms & Conditions. If you do not agree, please do not use the app.

2. What FinCalc does
FinCalc provides 60 financial calculators for education and planning. Results are estimates produced from the figures you enter and the formulas shown on each result screen.

3. Accuracy
We work hard to keep formulas and tax tables current, but rates, tax law and exchange rates change. Always verify important figures with the relevant authority, lender or a qualified professional before acting on them.

4. Not professional advice
FinCalc does not provide financial, tax, legal, investment or lending advice. See the Financial Disclaimer in the app.

5. Acceptable use
Use the app lawfully and do not attempt to disrupt it, reverse-engineer it, or misuse its advertising features (for example, by clicking ads repeatedly).

6. Advertising
The app displays third-party ads. FinCalc is not responsible for the content of those ads or for any product or service they promote.

7. Liability
To the maximum extent allowed by law, FinCalc and its developer are not liable for any loss arising from reliance on the app's estimates or from third-party content.

8. Changes
Features, formulas and these terms may change over time. Continued use of the app after a change means you accept the updated terms.

Contact: $SUPPORT_EMAIL"""

private const val DISCLAIMER_TEXT = """Financial Disclaimer

FinCalc provides estimates and educational calculations only. Nothing in this app is financial, tax, legal, investment or lending advice, and nothing here creates a professional relationship between you and the developer.

• Loan, mortgage and payoff figures are estimates based on the numbers you enter; actual lender terms, fees and rates may differ.
• Tax figures are estimates based on published federal brackets and standard deductions; they do not account for your full tax situation, credits, state rules or local taxes. Consult the IRS or a qualified tax professional before filing.
• Investment figures show estimated growth at the rate you enter; actual returns vary, can be negative, and are never guaranteed.
• Currency conversions use mid-market reference rates; banks and exchanges apply their own spread and fees.

Always consult a qualified professional before making financial decisions. The developer is not liable for decisions made using this app."""

@Composable
fun PrivacyScreen() = LegalPage("Privacy Policy", PRIVACY_TEXT)

@Composable
fun TermsScreen() = LegalPage("Terms & Conditions", TERMS_TEXT)

@Composable
fun DisclaimerScreen() = LegalPage("Financial Disclaimer", DISCLAIMER_TEXT)

@Composable
fun ContactScreen() {
    val context = LocalContext.current
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
    ) {
        Text("Contact Us", fontSize = 22.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp))
        Text(
            "Questions, suggestions or a calculator that looks wrong? " +
                "Send us an email — we read every message.\n\nEmail: $SUPPORT_EMAIL",
            fontSize = 14.sp, lineHeight = 21.sp
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$SUPPORT_EMAIL")
                putExtra(Intent.EXTRA_SUBJECT, "FinCalc — Feedback")
            }
            context.startActivity(intent)
        }) { Text("Email Support") }
        Spacer(Modifier.height(24.dp))
    }
}

private val FAQS = listOf(
    "How accurate are the loan calculations?" to
        "Loan payments use the standard amortisation formula (M = P × r(1+r)ⁿ / ((1+r)ⁿ − 1)). Results are exact for the numbers you enter, but real lenders add fees, insurance and their own rounding — treat the figure as a close estimate.",
    "How does the Mortgage Calculator work?" to
        "It adds principal & interest to monthly property tax, home insurance and HOA dues, so you see the full monthly cost — not just the loan payment.",
    "Are the tax calculations up to date?" to
        "Yes. Federal brackets and standard deductions use IRS Revenue Procedure 2025-32 (tax year 2026) and 2024-40 (2025). State tax is an estimate: flat states use their flat rate and progressive states use the top marginal rate. Tax results are estimates, not official tax advice.",
    "Can I trust the investment projections?" to
        "They are estimates at the return rate you enter. Markets vary and returns are never guaranteed — the app always labels investment results as estimates.",
    "How does compound interest work?" to
        "Interest is earned on both the original amount and the interest already earned: A = P(1 + r/n)^(nt). Starting early matters more than starting big, because compounding rewards time.",
    "How do I save for a goal?" to
        "Use the Financial Goal Calculator: enter the goal amount, what you already have, and the time available — it tells you the monthly saving needed. The Savings Calculator shows how long a fixed monthly amount takes.",
    "Where do currency rates come from?" to
        "Live mid-market rates from ExchangeRate-API's free open endpoint (open.er-api.com), with Frankfurter (ECB rates) as a fallback. Rates update daily; the app shows the update time. If both sources are unreachable, the converter says so — it never invents a rate.",
    "How accurate are the calculators?" to
        "Each result shows the formula it used, so you can check it. Results are only as accurate as the numbers you enter — double-check inputs for big decisions.",
    "What data does FinCalc collect?" to
        "Almost none. No account is needed. Calculations stay on your device. AdMob may collect advertising identifiers for ads, and Firebase Analytics collects anonymous usage statistics. See the Privacy Policy for details.",
    "Why do I see ads, and can I remove them?" to
        "Ads keep FinCalc free. Interstitial ads appear only after every 5th completed calculation, at a natural pause — never while you are entering numbers. Watching a short rewarded ad is optional and unlocks PDF export; basic calculations are always free."
)

@Composable
fun FaqScreen() {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
    ) {
        Text("Frequently Asked Questions", fontSize = 22.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp))
        FAQS.forEach { (q, a) ->
            var expanded by remember { mutableStateOf(false) }
            Card(
                Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    .clickable { expanded = !expanded },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(q, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    if (expanded) {
                        Spacer(Modifier.height(6.dp))
                        Text(a, fontSize = 13.sp, lineHeight = 19.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
