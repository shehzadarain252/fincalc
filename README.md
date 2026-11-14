# FinCalc — Smart Financial Calculators for Everyone

A complete, professional Android finance calculator app with **90 working calculators**
across 6 categories. No login required. All calculations work offline (except live
currency rates).

## Categories (15 calculators each)

1. **Loan & Mortgage** — Personal Loan, Home Loan, Mortgage, Car Loan, EMI,
   Loan Interest, Loan Payoff, Debt-to-Income, Loan Comparison, Amortization
2. **Tax & Salary** — US Income Tax, Federal Tax, State Tax, Paycheck, Salary,
   Hourly to Annual, Overtime, Sales Tax, Payroll Tax, Take-Home Salary
3. **Investment** — Compound Interest, Simple Interest, Investment Return,
   Stock Profit, Dividend, Retirement, 401(k), Roth IRA, Savings Growth, Inflation
4. **Personal Finance** — Budget Planner, Savings Goal, Emergency Fund,
   Credit Card Payoff, Credit Card Interest, Debt Payoff, Net Worth, Expense,
   Monthly Expense Planner, Financial Goal
5. **Business** — Profit Margin, Markup, ROI, Break-Even, Business Loan,
   Sales Commission, Cash Flow, Revenue, Gross Profit, Net Profit
6. **Currency & Daily Tools** — Currency Converter, Percentage, Discount, Tip,
   Age, Date Difference, Time, Unit Converter, GST, VAT

## Features

- Search, Favorites, Calculation History (saved on-device), Recently Used
- Dark / Light mode, default currency, decimal precision
- Copy, Share, Save to History, Export as PDF
- Live currency rates (free ExchangeRate-API open endpoint, Frankfurter fallback);
  clearly labelled cached rates when offline — never invented rates
- US federal tax brackets verified against IRS Rev. Proc. 2025-32 (2026) and
  2024-40 (2025); every tax result is labelled an estimate
- AdMob: interstitial after every 5th completed calculation (natural transition,
  3-minute cooldown, max 3 per 30 minutes); optional rewarded ad unlocks PDF
  export; no banner ads, no native ads; UMP consent on first launch
- Firebase Analytics + Crashlytics (free Spark plan)
- About, Privacy Policy, Terms & Conditions, Financial Disclaimer, Contact, FAQ

## Tech

- Kotlin + Jetpack Compose (Material 3), minSdk 26, targetSdk 34
- Room (history/favorites), DataStore (settings), Retrofit + Gson (rates)
- Google Mobile Ads 23.0.0 + UMP 2.2.0, Firebase BoM 33.1.2
- No backend — all calculations run on-device

## Build

This project has no Gradle wrapper. GitHub Actions installs Gradle 8.7 and builds
the debug APK (see `.github/workflows/build-apk.yml`):

1. Push to `main` (or run the "Build APK" workflow manually).
2. Download the `fincalc-debug-apk` artifact (`app-debug.apk`).

`app/google-services.json` is the Firebase client config for the FinCalc project
(package `com.fincalc.app`); it is required for the build.

## AdMob

Ad Unit IDs live in `app/src/main/java/com/fincalc/app/ads/AdMobManager.kt` and
the App ID in `app/src/main/AndroidManifest.xml`. Verify in your AdMob account
that the App ID in the manifest belongs to this FinCalc app before a production
release. The `app-ads.txt` file (publisher verification) must be hosted at the
root of the developer website associated with the AdMob account — it does not
belong inside the app.

## License

© 2026 FinCalc. All rights reserved.
