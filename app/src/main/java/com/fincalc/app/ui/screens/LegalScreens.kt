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
import com.fincalc.app.BuildConfig

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
        Text("Pro FinCalc", fontSize = 30.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 16.dp))
        Text("Smart Financial Calculators for Everyone", fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        Text(
            "Pro FinCalc is a powerful and easy-to-use finance calculator app that helps you " +
                "make smart financial decisions. From loans and taxes to investments and " +
                "budgeting, Pro FinCalc has everything you need in one place.\n\n" +
                "• 90 calculators across 6 categories: loans & mortgages, tax & salary, " +
                "investments, personal finance, business, and currency & daily tools.\n" +
                "• Every calculation works offline (except live currency rates).\n" +
                "• Save calculations to history, mark favorites, and export results as PDF.\n" +
                "• No account or login needed — your data stays on your device.\n\n" +
                "Version ${BuildConfig.VERSION_NAME}\n\n" +
                "© 2026 Pro FinCalc. All rights reserved.",
            fontSize = 14.sp, lineHeight = 21.sp
        )
        Spacer(Modifier.height(24.dp))
    }
}

private const val PRIVACY_TEXT = """Last updated: November 2026

Pro FinCalc respects your privacy. This policy explains what the app collects, how it is used, and the choices you have.

1. No account, no personal data
Pro FinCalc does not ask you to create an account, and we do not collect your name, email address, phone number, contacts, location, photos, or financial account details. You can use every calculator without signing in.

2. Data stored on your device only
Calculations you enter, your history and your favorites are stored only on your device (local database and settings storage). They are never uploaded to a server, because we operate no servers for this app. Uninstalling the app, or using Settings → Clear App Data, permanently deletes everything stored locally.

3. Advertising
Pro FinCalc shows ads through Google AdMob. AdMob and its partners may collect advertising identifiers and usage data to serve ads, including personalized ads where you have consented. You can change your advertising consent at any time in Settings → Privacy Settings. We never sell personal information. Ads keep Pro FinCalc free; no personal data is sold to advertisers.

4. Analytics and crash reports
The app uses Firebase Analytics and Crashlytics to understand anonymous usage (for example, which calculators are popular) and to fix crashes. This data is anonymous and is not linked to your identity.

5. Currency rates
The Currency Converter fetches exchange rates from a public rates service (open.er-api.com, with Frankfurter as a fallback). Your request goes to that service to get the rate you asked for; no personal information is sent with it.

6. Permissions
Pro FinCalc requests only two permissions, both genuinely needed: Internet (to fetch live currency rates and to load ads) and network state (to check whether the device is online before fetching rates). The app does not request contacts, location, camera, microphone, or storage permissions.

7. Third-party services
Google AdMob, Google User Messaging Platform (consent), and Firebase (Analytics and Crashlytics) process data under their own privacy policies.

8. Children's privacy
Pro FinCalc is not directed at children under 13, and we do not knowingly collect information from children.

9. Your choices and rights
You can clear all locally stored data in Settings → Clear App Data, change ad consent in Settings → Privacy Settings, and delete the app at any time to remove all local data. Because no account exists and no personal data is collected, there is no account to delete and no personal data to request a copy of.

10. Changes
If this policy changes, the new version will be shown in the app.

Contact: $FEEDBACK_FORM_URL"""

private const val TERMS_TEXT = """Last updated: November 2026

1. Acceptance
By using Pro FinCalc you agree to these Terms & Conditions. If you do not agree, please do not use the app.

2. What Pro FinCalc does
Pro FinCalc provides 90 financial calculators for education and planning. Results are estimates produced from the figures you enter and the formulas shown on each result screen. No account or login is required.

3. Accuracy
We work hard to keep formulas and tax tables current, but rates, tax law and exchange rates change. Always verify important figures with the relevant authority, lender or a qualified professional before acting on them.

4. Not professional advice
Pro FinCalc does not provide financial, tax, legal, investment or lending advice. See the Financial Disclaimer in the app.

5. Acceptable use
Use the app lawfully and for personal planning only. Do not attempt to disrupt the app, reverse-engineer it, extract its data, or misuse its advertising features (for example, by clicking ads repeatedly or using automated tools to generate ad impressions). Do not use the app for any illegal purpose.

6. Advertising
The app displays third-party ads. Pro FinCalc is not responsible for the content of those ads or for any product or service they promote.

7. Service limitations
Pro FinCalc is provided "as is" without warranties of any kind. Features, formulas, tax tables and exchange-rate sources may change over time, and the app may be updated or discontinued at any time. Calculations require the figures you enter; incorrect inputs produce incorrect results.

8. Liability
To the maximum extent allowed by law, Pro FinCalc and its developer are not liable for any loss arising from reliance on the app's estimates, from third-party content, or from the unavailability of live currency rates.

9. Changes
Features, formulas and these terms may change over time. Continued use of the app after a change means you accept the updated terms.

Contact: $FEEDBACK_FORM_URL"""

private const val DISCLAIMER_TEXT = """Financial Disclaimer

Pro FinCalc provides estimates and educational calculations only. Nothing in this app is financial, tax, legal, investment or lending advice, and nothing here creates a professional relationship between you and the developer.

• Loan, mortgage and payoff figures are estimates based on the numbers you enter; actual lender terms, fees and rates may differ.
• Tax figures are estimates based on published federal brackets and standard deductions; they do not account for your full tax situation, credits, state rules or local taxes. Consult the IRS or a qualified tax professional before filing.
• Investment figures show estimated growth at the rate you enter; actual returns vary, can be negative, and are never guaranteed.
• Currency conversions use mid-market reference rates; banks and exchanges apply their own spread and fees.

Always consult a qualified professional before making financial decisions. The developer is not liable for decisions made using this app."""

@Composable
fun PrivacyScreen() = LegalPage("Privacy Policy", PRIVACY_TEXT)

private const val DATA_SAFETY_TEXT = """Last updated: November 2026

This page explains, in plain language, what Pro FinCalc collects, shares, and protects. It matches the app's real behaviour — nothing here is collected or shared beyond what is listed.

1. No account — so no account data
Pro FinCalc has no accounts, no login, and no sign-up. Because there is no account, there is nothing to delete: no account-deletion flow is needed, and no personal profile exists anywhere.

2. What the app collects
Pro FinCalc itself collects no personal data: no name, email address, phone number, contacts, location, photos, or financial account details. The numbers you type into a calculator are used only to compute the result on your device.

3. What is stored — on your device only
Your calculation history, favorites, settings (theme, currency, decimal precision) and the last saved currency rates are stored only on your device, in local storage. They are never sent to a server. Settings → Clear App Data deletes all of it. Uninstalling the app deletes it too.

4. What is shared with third parties
• Google AdMob (advertising): may collect the advertising identifier and coarse usage data to serve ads. No personal information is sent by the app itself.
• Firebase Analytics and Crashlytics: anonymous usage statistics and crash reports. This data cannot identify you.
• Currency-rate services (open.er-api.com, Frankfurter): receive only the currency pair you ask about, so they can return the rate. No personal information is sent.
• No other data is shared with any other party.

5. Permissions
Pro FinCalc uses only Internet access (for live currency rates and ads) and network-state access (to check if the device is online). It does not request contacts, location, camera, microphone, or storage permissions.

6. Security
Because all of your data stays on your device, there is no server copy to protect or to breach. Data sent to third-party services travels over encrypted (HTTPS) connections.

7. Your choices
Clear all stored data in Settings → Clear App Data. Change advertising consent in Settings → Privacy Settings. Delete the app at any time to remove everything stored locally. If you have a question about data safety, contact us through the feedback form.

Contact: $FEEDBACK_FORM_URL"""

@Composable
fun DataSafetyScreen() = LegalPage("Data Safety Information", DATA_SAFETY_TEXT)

@Composable
fun TermsScreen() = LegalPage("Terms & Conditions", TERMS_TEXT)

@Composable
fun DisclaimerScreen() = LegalPage("Financial Disclaimer", DISCLAIMER_TEXT)

private const val COPYRIGHT_TEXT = """Last updated: November 2026

1. Ownership
The Pro FinCalc name, logo, app design, text and calculator content are owned by the app developer. All rights reserved.

2. What you may do
You may use the app for your own personal financial planning, and share a calculation result (for example, by copying or exporting it) for your own use.

3. What you may not do
You may not copy, reproduce, redistribute, resell or republish the app or its content, in whole or in part, or use the Pro FinCalc name or logo to present another product as ours. Do not attempt to extract the app's data or code.

4. Third-party marks
Google AdMob, Firebase, and other third-party names and logos belong to their respective owners and are used only to describe the services the app relies on.

5. Feedback
Suggestions you send through the feedback form may be used to improve the app, without payment or credit.

Contact: $FEEDBACK_FORM_URL"""

private const val REFUND_TEXT = """Last updated: November 2026

Pro FinCalc is completely free to download and use. The app collects no payment from you: there are no purchases, no subscriptions and no paid features.

Because no payment is ever collected, there is nothing to refund. Watching an optional rewarded ad to unlock PDF export is free and voluntary — it is not a purchase, and it cannot be refunded.

If a paid feature is ever introduced, its price and refund terms will be shown clearly before you pay, and refunds will follow the policy of the store or payment provider used.

Contact: $FEEDBACK_FORM_URL"""

private const val ACCOUNT_DELETION_TEXT = """Last updated: November 2026

Pro FinCalc has no accounts, no login and no sign-up, so there is no account to delete and no personal profile stored anywhere.

All of your data — calculation history, favorites and settings — is stored only on your device. To delete everything:

• Open Settings → Clear App Data inside the app, or
• Uninstall the app from your device.

Either step permanently removes all locally stored data. Because no account exists and no personal data is collected, there is no personal data to request a copy of and no deletion request to file.

If you have a question about your data, contact us through the feedback form.

Contact: $FEEDBACK_FORM_URL"""

@Composable
fun CopyrightScreen() = LegalPage("Copyright Policy", COPYRIGHT_TEXT)

@Composable
fun RefundScreen() = LegalPage("Refund Policy", REFUND_TEXT)

@Composable
fun AccountDeletionScreen() = LegalPage("Account Deletion Policy", ACCOUNT_DELETION_TEXT)

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
                "Fill in the feedback form — we read every message.",
            fontSize = 14.sp, lineHeight = 21.sp
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(FEEDBACK_FORM_URL))
            context.startActivity(intent)
        }) { Text("Open Feedback Form") }
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
    "What data does Pro FinCalc collect?" to
        "Almost none. No account is needed. Calculations stay on your device. AdMob may collect advertising identifiers for ads, and Firebase Analytics collects anonymous usage statistics. See the Privacy Policy for details.",
    "Why do I see ads, and can I remove them?" to
        "Ads keep Pro FinCalc free. Interstitial ads appear only after every 5th completed calculation, at a natural pause — never while you are entering numbers. Watching a short rewarded ad is optional and unlocks PDF export; basic calculations are always free."
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
