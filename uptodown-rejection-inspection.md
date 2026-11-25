# FinCalc — Uptodown Rejection Inspection Report (v1.0.1, version code 2)

**Inspection only — nothing was edited, fixed, built, or submitted.**
**Date:** 2026-11-16
**Scope:** source project `/home/hatch/workspace/fincalc/` + released APK v1.0.1 downloaded from `https://github.com/shehzadarain252/fincalc/releases/tag/v1.0.1`
**Companion research:** `~/workspace/fincalc/uptodown-policy-research.md` (Uptodown official criteria, fetched 2026-11-16)
**No AdMob ID, API key, or credential value is reproduced anywhere in this report.**

## Verdict in one paragraph

The submitted v1.0.1 APK is a **debug build** (`application-debuggable`, signed with the Android **Debug** certificate, v2-signature only) — the single most concrete defect in the submitted artifact, and a strong candidate for the "minimum quality standards" rejection. The app's code itself is in good shape: all 90 calculators exist with real compute functions, the four known reference results verify exactly, AdMob placement/timing follows policy, there is an in-app privacy policy, and there are no dangerous permissions. The remaining defects are real but smaller: stale in-app text ("60 calculators", "Version 1.0.0") that contradicts the listing, an **unconfirmed AdMob App ID** (the manifest itself warns it must be confirmed in the AdMob console), and **no retry and no logging after ad-load failures** — which is the most likely code-level reason the user reports ads not showing. A signed release build cannot be produced from the current setup: there is no keystore and no `signingConfigs` block, and the CI workflow builds debug only.

---

## 1. Confirmed rejection reason (from the Uptodown console)

This inspection did **not** access the Uptodown Developer Console itself. The rejection reason below is taken from the console check recorded 2026-11-16 ~14:29 PKT (see MEMORY.md):

- **App:** FinCalc, Uptodown App ID 1000950945, v1.0.1 (version code 2), submitted 14/11/2026.
- **Notification, 13:06 on 16/11/2026:** *"We're sorry, but your app doesn't meet the minimum quality standards to be published on Uptodown."*
- **No specific defect is itemized anywhere in the console.** The same generic message appears for the account's other rejected apps (QuickCalc, Calc Master, FinCalc Pro).
- **Listing state at rejection:** 5 screenshots "Pending approval"; full description 69 words (Uptodown's publish guide asks for 100+ words of full description — not cited as the cause, but thin).
- **Policy context** (`uptodown-policy-research.md` §2): "does not meet our minimum quality standards" is Uptodown's #1 rejection bucket — 55.31% of September 2026 rejections, 48.02% in the latest Transparency Center window. It covers "serious usability problems, an overly confusing interface, excessive advertising, or any other point in our publishing guidelines, assessed by our specialized team of editors." A generic message with no defect list is the normal form of this rejection; getting a specific reason requires a Developer Support ticket.
- **User's order (2026-11-16 ~14:29 PKT):** "App ko update kar do" (fix + build the update) + "Publish nahin karna isko abhi" (do NOT submit/publish yet).

---

## 2. Submitted APK — forensics

Downloaded 2026-11-16 from the v1.0.1 GitHub release (`app-debug.apk`):

| Item | Evidence |
|---|---|
| File size | 26,371,671 bytes (25.1 MB) — matches the release |
| SHA-256 | `88c77dde2f01d7eadcb35840ca2690c27f886de471e737dc9a84b4f6fde8a0e9` |
| Package / version | `com.fincalc.app`, versionCode **2**, versionName **1.0.1** (`aapt2 dump badging`) |
| SDKs | compileSdk **34**, minSdk **26**, targetSdk **34** (badging + `app/build.gradle.kts:11,15,16`) |
| **Debuggable** | **`application-debuggable` present in `aapt2 dump badging`; binary manifest contains `android:debuggable=true`** (`aapt2 dump xmltree --file AndroidManifest.xml`) |
| Signature | `apksigner verify --verbose`: **v1 (JAR signing): false; v2: true; v3: false; v3.1: false; v4: false. 1 signer. Signer #1 certificate DN: C=US, O=Android, CN=Android Debug** (debug certificate, RSA 2048). No `META-INF/MANIFEST.MF` / `CERT.RSA` in the APK. |
| Label | `FinCalc` (badging `application-label`) |
| Launchable activity | `com.fincalc.app.MainActivity` |

**Why this matters:** the artifact submitted to Uptodown is a **debug build**, not a release build. Debug builds are debuggable, signed with the publicly-known Android debug key, and not shrunk/optimized. Uptodown's "minimum quality standards" bucket explicitly covers serious quality problems; a debuggable debug-signed APK is the kind of thing a human editor (or their tooling) flags. The fix is a signed **release APK** (not AAB — Uptodown accepts APK/XAPK only), version code ≥ 3, same package name (see §10).

### Permissions in the merged APK manifest (`aapt2 dump badging`, 10 total)

Declared in source (`app/src/main/AndroidManifest.xml:6-7`): `INTERNET`, `ACCESS_NETWORK_STATE` — both genuinely needed (INTERNET for exchange rates + ads; ACCESS_NETWORK_STATE for connectivity checks). No dangerous permissions are declared, and the app requests no runtime permissions.

Merged from libraries (not declared by the app): `AD_ID`, `ACCESS_ADSERVICES_AD_ID`, `ACCESS_ADSERVICES_ATTRIBUTION`, `ACCESS_ADSERVICES_TOPICS` (play-services-ads), `WAKE_LOCK`, `BIND_GET_INSTALL_REFERRER_SERVICE` (Play install referrer), `FOREGROUND_SERVICE`, `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`. All are normal library additions; none is a user-facing permission and none is unnecessary in a way a reviewer would flag.

---

## 3. Calculator inventory — all 90 present, all functional

`CalculatorRegistry.kt` combines 6 base files (10 each) + `CalculatorsExtra.kt` (30): **10 + 10 + 10 + 10 + 10 + 10 + 30 = 90 `CalculatorDef` entries.** Category counts verified by grep: each base file has 10 defs of its own category; `CalculatorsExtra.kt` adds exactly 5 per category (LOAN 5, TAX 5, INVESTMENT 5, PERSONAL 5, BUSINESS 5, DAILY 5), so every category has 15 — matching `Models.kt` (`"15 Tools"` per category) and the README.

Every def has a real `compute` lambda; a repo-wide grep for `TODO|FIXME|coming soon|placeholder|lorem ipsum|not implemented` returns **no real hits** (only false positives: Compose's `placeholder` TextField parameter and the words "Total Days" in date math). No calculator is a dummy screen.

### 3.1 Loan & Mortgage (15)
`CalculatorsLoan.kt` (10) + `CalculatorsExtra.kt` (5): personal-loan, home-loan, mortgage, car-loan, **emi**, loan-interest, loan-payoff, dti, loan-comparison, amortization, student-loan, home-equity-loan, refinance, loan-affordability, biweekly-mortgage.

### 3.2 Tax & Salary (15)
`CalculatorsTax.kt` (10) + Extra (5): us-income-tax, federal-tax, state-tax, paycheck, salary, hourly-to-annual, overtime, sales-tax, payroll-tax, take-home, self-employment-tax, capital-gains-tax, property-tax, bonus-tax, tax-refund-estimator.

### 3.3 Investment (15)
`CalculatorsInvest.kt` (10) + Extra (5): compound-interest, simple-interest, investment-return, stock-profit, dividend, retirement, 401k, roth-ira, savings-growth, inflation, future-value, present-value, cd-calculator, annuity, crypto-profit.

### 3.4 Personal Finance (15)
`CalculatorsPersonal.kt` (10) + Extra (5): budget-planner, savings-goal, emergency-fund, credit-card-payoff, credit-card-interest, debt-payoff, net-worth, expense, monthly-expense-planner, financial-goal, rent-vs-buy, house-affordability, car-affordability, vacation-savings, wedding-budget.

### 3.5 Business (15)
`CalculatorsBusiness.kt` (10) + Extra (5): profit-margin, markup, roi, break-even, business-loan, sales-commission, cash-flow, revenue, gross-profit, net-profit, npv, payback-period, operating-margin, working-capital, dscr.

### 3.6 Currency & Daily Tools (15)
`CalculatorsDaily.kt` (10) + Extra (5): currency-converter, percentage, discount, tip, age, date-difference, time, unit-converter, gst, vat, fuel-cost, electricity-bill, bmi, speed-distance-time, work-hours.

### 3.7 Verified reference results (recomputed independently in this inspection)

| Check | Expected | Result | Evidence |
|---|---|---|---|
| EMI $25,000 @ 6.5%, 5y | $489.15/mo | **$489.15** ✓ | `emi()` in `Models.kt` (standard formula, guards `n<=0` and `r==0`); EMI def `CalculatorsLoan.kt:174-190` calls `loanResult()` (`CalculatorsLoan.kt:12-18`), which calls `emi()` (`:18`) |
| Compound $10,000 @ 7%, 10y | $19,671.51 | **$19,671.51** ✓ | `compound()` in `Models.kt`; compound-interest def `CalculatorsInvest.kt:17-63` calls it |
| US 2026 federal tax, Single, $75,000 | $7,670.00 | **$7,670.00** ✓ | `TaxData.kt`: 2026 Single brackets + standard deduction $16,100; taxable $58,900 → 12,400×10% + 38,000×12% + 8,500×22% = 1,240 + 4,560 + 1,870. US Income Tax def `CalculatorsTax.kt` uses `TaxData.standardDeduction()` + `TaxData.federalTax()` |
| BMI 75 kg / 175 cm | 24.5 | **24.5** ✓ | BMI def `CalculatorsExtra.kt:1071-1085`, `bmi = weight / (h*h)` with `h > 0` guard (`:1081`) |

### 3.8 Formula/data notes
- **State tax is an estimate by design:** `TaxData.kt` state table uses flat rates or top marginal rates and the code comment requires the UI to label it an estimate.
- **Capital gains uses 2025 thresholds, disclosed in-app:** `CalculatorsExtra.kt:270-330` — long-term 0%/15%/20% thresholds are the 2025 IRS figures; the formula text says "(2025 IRS thresholds)" and the disclaimer adds "Long-term thresholds are the 2025 IRS figures." Not hidden, but the 2026 thresholds are a pending update.
- **Currency converter never invents rates:** `RatesApi.kt` — primary `open.er-api.com` (free, no key), fallback Frankfurter (ECB rates), then a clearly-labelled cached snapshot, then an honest `Unavailable` message ("Live exchange rates are unavailable right now. Check your internet connection and try again — no estimated rate is shown."). All other calculators work offline (`AndroidManifest.xml` comment + About text).
- **Stale comment (cosmetic):** `Models.kt:46` — `CalcResult.title` doc example still says `"$466.01"` (the old wrong mockup EMI figure). The real `emi()` math is correct; only the comment is stale.

---

## 4. AndroidManifest analysis

`app/src/main/AndroidManifest.xml`:
- Package `com.fincalc.app`; `android:name=".FinCalcApp"` (`:10`) — the Application class that initializes AdMob (see §6).
- `android:label="@string/app_name"` (`:13`) — launcher label comes from `strings.xml` (see §12).
- `android:allowBackup="true"` (`:11`) — fine for this app (local Room/DataStore data is backup-eligible; no sensitive data stored).
- `android:usesCleartextTraffic="false"` (`:17`) — all network is HTTPS.
- AdMob App ID meta-data present (`:28`, `com.google.android.gms.ads.APPLICATION_ID`) — also confirmed present in the APK's **binary** manifest via `aapt2 dump xmltree`. (Value not reproduced here; see §6.4 for the unconfirmed-ownership problem.)
- `AD_SERVICES_CONFIG` property with `tools:replace` — resolves the AdMob/Firebase manifest-merger conflict; standard.
- Single exported activity (`.MainActivity`, launcher intent filter); `FileProvider` (`${applicationId}.fileprovider`, not exported) for PDF sharing — correct.
- `configChanges="orientation|screenSize|keyboardHidden|uiMode"` on MainActivity — rotation does not restart the activity.

---

## 5. AdMob audit (general)

Files: `app/src/main/java/com/fincalc/app/ads/AdMobManager.kt`, `BannerAd.kt`; init in `FinCalcApp.kt:10`; consent + banner placement in `MainActivity.kt:87,230`.

### 5.1 Ad units — live production IDs, no test IDs
- All three ad unit IDs (banner, interstitial, rewarded) are **live production-format IDs** under one publisher segment that matches the manifest App ID's publisher segment (verified programmatically; values not reproduced).
- **Zero Google test ad IDs in the shipped APK:** a scan of all 13 dex files for the Google test publisher ID returned 0 matches; 3 distinct production-format unit IDs were found (exactly the 3 the source defines). No test ads in the production build. ✓
- AdMob SDK `play-services-ads:23.0.0` + UMP `2.2.0` (`app/build.gradle.kts`).

### 5.2 Banner — placement is policy-correct
- `BannerAd.kt` — anchored adaptive banner sized to screen width; `AdView` in a `Column` **above** the `NavigationBar` inside the Scaffold `bottomBar` (`MainActivity.kt:230`). It does not cover calculator content or navigation; Scaffold padding accounts for it. Shown only on the 4 tab screens (Home, Favorites, History, Settings — `MainActivity.kt` `showBottomBar`); calculator screens have no banner by design.
- Failure behaviour: if the banner fails to load, the space stays empty and the app keeps working (`BannerAd.kt` header comment). No crash, no placeholder.

### 5.3 Interstitial — timing matches the spec
- Rules (`AdMobManager.kt:68-71`): `CALCS_BEFORE_AD = 5`, `COOLDOWN_MS` = 3 min, `WINDOW_MS` = 30 min, `MAX_PER_WINDOW = 3`.
- Trigger: `maybeShowInterstitial()` (`AdMobManager.kt:131`) is called from `CalculatorScreen.kt:118` inside `handleBack()` — only when a result is showing, i.e. at a natural navigation transition (leaving a result screen), never while entering numbers and never on launch. Counter increments in `onCalculationCompleted()` (`CalculatorScreen.kt:206,396`). Counters persist in DataStore (`SettingsStore.kt` — `calcs_since_ad`, `last_interstitial_at`, `ad_window_start/count`).
- Minor inconsistency (not a violation): the TopAppBar back button in `MainActivity.kt` calls `navController.navigateUp()` directly and bypasses `handleBack()`, so the interstitial only fires on the system back gesture/button, not the toolbar back arrow.

### 5.4 Rewarded — never blocks export
- PDF export is the rewarded unlock (`CalculatorScreen.kt:239,264,432,457`). If no rewarded ad is loaded, export proceeds directly (`exportPdf(r)`). If one is loaded, a dialog offers "Watch Ad to Unlock" (voluntary) or "Cancel"; the reward callback fires only when the SDK confirms completion (`AdMobManager.kt:190-204`). Export is never blocked. ✓
- `showRewarded()` reloads an ad when none is ready (`AdMobManager.kt:194`) and after dismiss/failure (`:199,204`).

### 5.5 UMP consent
- `AdMobManager.requestConsent()` (`AdMobManager.kt:88`) is called in `MainActivity.onCreate` (`MainActivity.kt:87`) — UMP consent form loads if required. Settings → "Privacy Settings" re-opens the consent form (`SettingsScreen.kt`, "Privacy Settings" row) and "Clear App Data" deletes history/favorites/settings. This matches the in-app privacy policy's claims.
- **Defect (ordering):** `canRequestAds()` (`AdMobManager.kt:100`) is defined but **never called anywhere** — consent status is never checked before loading or showing ads. And ad loading starts in `Application.onCreate` (`FinCalcApp.kt:10` → `AdMobManager.init()` → `MobileAds.initialize` → interstitial/rewarded load), which runs **before** `MainActivity.onCreate` requests consent. So the first ad requests fire before UMP consent is determined. For the app's US target audience and a Pakistan-based tester this is a secondary issue (consent is generally not required there), but it is the wrong order against the recommended UMP flow.

---

## 6. Ads NOT showing in v1.0.1 — deep dive (user report 2026-11-16 ~14:31 PKT)

The user reports ads do not show at all in v1.0.1. Findings, ranked by likelihood.

### 6.1 Most likely cause — App ID / ad-unit app-entry mismatch (console-side; cannot be proven from code)

- The manifest itself carries the warning (`AndroidManifest.xml`, comment above the App ID meta-data): two App ID candidates were supplied, the manifest uses the one that is **NOT** the EduMind AI one, and **"The user MUST confirm in their AdMob account that this App ID belongs to the FinCalc app before a production release — if it is the wrong one, ads will not fill."** That confirmation has not happened (recorded as an open item since 2026-11-14).
- The banner unit was supplied separately by the user on 2026-11-14, after the interstitial/rewarded units in the master prompt. If the banner unit was created under a **different AdMob app entry** (e.g. the EduMind AI entry), AdMob returns no fill for the banner even though the interstitial/rewarded units could fill — this was the standing suspicion, and `BannerAd.kt`'s own header comment repeats it ("or the unit belongs to a different AdMob app entry than the manifest App ID").
- What code can and cannot prove: all IDs share one publisher segment and the publisher matches `app-ads.txt` (see §6.5) — so they are all in the **same AdMob account**. But publisher segment does **not** prove app-entry pairing: an App ID and a unit ID under the same publisher can still belong to different app entries. Only the AdMob console (Apps → FinCalc → ad units) can confirm the pairing. **This part can only be fixed inside the AdMob console** — confirm the manifest App ID is the FinCalc entry's ID, and recreate under the FinCalc entry any unit that belongs to another entry.

### 6.2 Most likely code cause — no retry and no logging after a load failure

- **Zero logging in the ads code:** `grep` for `Log.` / `println` across `ads/`, `FinCalcApp.kt` returns nothing. Every load failure is swallowed silently, so there is no on-device diagnostic trail for which format fails or why.
- **Interstitial:** `loadInterstitial()` (`AdMobManager.kt:107-116`) — `onAdFailedToLoad` only sets `interstitial = null` (`:112`). There is **no retry path after a load failure**: `loadInterstitial()` is re-called only after an ad is dismissed (`:158`) or fails to show (`:163`). If the very first load at startup fails (no fill, network not ready, wrong App ID/unit pairing), the interstitial stays `null` for the whole session; `maybeShowInterstitial()` then just runs `onFinished` and never retries.
- **Rewarded:** same pattern (`:173-181`, `onAdFailedToLoad` → `rewarded = null`, `:178`); the only reload is when the user taps PDF export with no ad ready (`:194`) or after dismiss/failure (`:199,204`).
- **Banner:** `BannerAd.kt:35-47` — the `AdView` is created once inside `remember(bannerUnitId, adSize)` and `loadAd()` is called once; `onAdFailedToLoad` (`:43`) does nothing. The `AdView` instance persists across recompositions (same remember keys while the tab Scaffold is alive), so **one failed load = no banner for the entire session**, with no retry on tab switch.
- Net effect: a single transient failure at cold start (or a persistent no-fill from §6.1) makes ads silently absent until the app process is killed and restarted — and if the cause is §6.1, restarts fail the same way.

### 6.3 Expected-behaviour contribution — the pacing rules hide ads by design

Even with everything working, a user will often see no ads: the interstitial requires **5 completed calculations** + back navigation + 3-minute cooldown + fewer than 3 in 30 minutes; the rewarded ad appears only when the user taps PDF export; the banner appears only on the 4 tab screens. A user who does one or two calculations and browses will legitimately see no interstitial. "Ads not showing" should be tested with 5+ completed calculations, then back navigation — and the banner checked on the Home tab specifically.

### 6.4 Ruled out as causes

- **INTERNET permission:** declared (`AndroidManifest.xml:6`) and present in the merged APK manifest. Not the cause.
- **Banner hidden behind navigation:** banner sits above the bottom nav (`MainActivity.kt:230`), not behind it. Not the cause.
- **ProGuard/R8 stripping:** the shipped build is **debug** (`app/build.gradle.kts:36`, `isMinifyEnabled = false` for debug), so R8 does not run on v1.0.1. Not the cause (but note: the release build enables minify + shrinkResources, `app/build.gradle.kts:28-29`, with `-keep class com.fincalc.app.**` in `app/proguard-rules.pro`; AdMob ships its own consumer rules — verify after the first release build).
- **Test ads:** none in the build (§5.1). Not the cause.
- **MobileAds never initialized:** `FinCalcApp` is registered as the Application class (`AndroidManifest.xml:10`) and calls `AdMobManager.init()` in `onCreate` (`FinCalcApp.kt:10`). Not the cause.
- **App ID meta-data missing from the APK:** present in the binary manifest (verified via `aapt2 dump xmltree`). Not the cause.
- **Debug build blocking ads:** AdMob does serve live ads to debug-signed apps in principle; debug signing is a quality/compliance problem (§2), not an ad-serving block.

### 6.5 app-ads.txt — publisher matches; hosting not verifiable from here

- File: `~/workspace/goals/fincalc-finance-calculator-app/files/app-ads.txt` — 1 line, a `DIRECT` entry for google.com.
- **Publisher match verified programmatically (values not reproduced):** the publisher in `app-ads.txt` == the manifest App ID's publisher == the publisher segment of all 3 ad-unit IDs. ✓
- **Hosting not verifiable from this VM:** `curl https://www.appgalaxy.dpdns.org/app-ads.txt` returned HTTP 000 (connection failure from this VM, not a 404 — the VM cannot reach that host). Whether the file is actually served at the root of the developer website linked in the AdMob account is **unconfirmed**. If it is not hosted, AdMob fill can be reduced (it does not zero out fill by itself). The fix phase must verify in a browser that the file is served at the linked website's root. (README.md notes the file "must be hosted at the root of the developer website associated with the AdMob account — it does not belong inside the app.")

### 6.6 What can only be fixed inside the AdMob console

- Confirming the manifest App ID belongs to the **FinCalc** app entry (not EduMind AI's or another entry).
- Confirming each ad unit (banner, interstitial, rewarded) belongs to that same FinCalc entry; recreating any unit that lives under a different entry and updating the code with the new unit.
- Verifying `app-ads.txt` is hosted at the developer website root and that website is linked in AdMob; checking the account's Policy center / app-readiness state if serving is limited.
- Everything else (retry + logging, consent/initialization ordering, pacing) is fixable in code.

---

## 7. Privacy and data handling

- **In-app privacy policy exists** (`LegalScreens.kt`, `PRIVACY_TEXT`): no account, no name/email/contacts/location/financial-account collection; calculations stay on-device; AdMob may collect advertising identifiers; Firebase Analytics + Crashlytics collect anonymous usage; currency-rate requests go to `open.er-api.com` / Frankfurter with no personal data; children's privacy (not directed at under-13s); Clear App Data + consent change paths.
- **Local storage only:** Room database `fincalc.db` (`HistoryDb.kt`) — history items (calc id/title/summary/inputs/timestamp) + favorites; DataStore (`SettingsStore.kt`) — theme, currency, decimals, ad counters, cached rates. No cloud sync, no login. `SettingsScreen.kt` "Clear App Data" deletes history, favorites and settings (`dao.clearHistory()`, `removeFavorite()`, `store.clearAll()`).
- **Third-party data flows:** Firebase Analytics + Crashlytics (`firebase-bom:33.1.2`, `app/google-services.json` present — Firebase project for package `com.fincalc.app`); AdMob + UMP consent. Both are disclosed in the privacy policy.
- **Contact/support:** no support email in the app. Contact Us opens a Google Forms feedback link (`SettingsScreen.kt:44`, `FEEDBACK_FORM_URL`; also printed at the end of the Privacy Policy and Terms texts, `LegalScreens.kt:99,127`). The Uptodown ToS requires "valid and accurate contact information for customer support" — a form link is contact information, but a named support email would be stronger; note for the listing.
- **Offline:** everything works offline except live currency rates, which degrade honestly (§3.8).

---

## 8. Crash / bug risk review (static only — no device run was possible in this inspection)

- **Input validation:** `CalculatorScreen.kt:185-209` — required fields must be filled; NUMBER inputs must parse; otherwise a specific error message names the field. `compute()` is wrapped in try/catch (`:197-202`) → "Could not calculate — please check your inputs." rather than a crash.
- **Division-by-zero guards found:** `emi()` guards `n <= 0` and `r == 0` (`Models.kt`); DTI guards `income > 0` (`CalculatorsLoan.kt:301`); BMI guards `h > 0` (`CalculatorsExtra.kt:1081`); break-even, credit-card payoff and debt payoff return "Never" when the result is infinite (`CalculatorsBusiness.kt`, `CalculatorsPersonal.kt`).
- **Navigation:** 14 routes in `MainActivity.kt` NavHost; every drawer item maps to a real route; unknown category ids fall back to LOAN (`HomeScreens.kt:196`); unknown calculator ids show a "Calculator not found." message instead of crashing (`CalculatorScreen.kt:78-85`).
- **No dead buttons found statically.** No blank-screen paths found statically (every screen renders content from its def/DAO; lists handle empty states — verify on device).
- **Not verifiable here:** actual runtime behaviour, startup time, performance on low-end devices, and any crash on the editors' test devices. A device/emulator run is still owed before resubmission.

---

## 9. Content-accuracy defects (confirmed present in the shipped APK)

These are in the **shipped binary**, confirmed by string counts in the dex files (`"Version 1.0.0"` ×2, `"60 calculators"` ×1, `"90 calculators"` ×1):

| # | Defect | Location | Impact |
|---|---|---|---|
| 1 | About screen says **"60 calculators across 6 categories"** | `LegalScreens.kt:58` | Directly contradicts the listing (90) and the app itself. A reviewer comparing listing vs app sees a mismatch — Uptodown criterion #3 ("function must not contradict the information provided by the author") and #11 in the policy research. |
| 2 | Terms & Conditions say **"FinCalc provides 60 financial calculators"** | `LegalScreens.kt:107` | Same mismatch, in a legal text. |
| 3 | About screen says **"Version 1.0.0"** | `LegalScreens.kt:63` | Wrong version displayed in the shipped 1.0.1 build. |
| 4 | Settings → About App row says **"Version 1.0.0"** | `SettingsScreen.kt:108` | Same as #3, second surface. |
| 5 | README says **"no banner ads"** | `README.md:34` | Stale docs (banner added 2026-11-14). Not user-visible, but wrong for anyone reviewing the project. |
| 6 | `Models.kt:46` doc example `"$466.01"` | Code comment only | The old wrong mockup EMI figure survives as an example. Cosmetic. |

---

## 10. Signing and build — a signed release is not currently possible

- **No keystore anywhere in the project:** `find` for `*.jks` / `*.keystore` / `keystore.properties` returns 0 files.
- **No `signingConfigs` block** in `app/build.gradle.kts` (verified by full read). The `release` build type only sets `isMinifyEnabled = true`, `isShrinkResources = true` + ProGuard files (`:26-33`); without a signing config, `assembleRelease` would produce an unsigned APK.
- **CI builds debug only:** `.github/workflows/build-apk.yml:62` runs `gradle :app:assembleDebug`; the header comment (`:15`) says "This builds the DEBUG apk only (no signing key needed)."
- **Resubmission requirements** (from `uptodown-policy-research.md` §5): same package `com.fincalc.app`, **version code ≥ 3** (higher than the rejected code 2), new version name (e.g. 1.0.2), **APK format (AAB is not supported by Uptodown)**. Creating a keystore + `signingConfigs` (with the keystore password via GitHub secrets, never committed) is a prerequisite fix.
- `google-services.json` is committed in `app/` (Firebase client config — public by design, CI needs it; `.gitignore` documents this). No backend keys exist by design.

---

## 11. Listing-side notes (from the console/policy research, not from the repo)

- **Icon:** the submitted icon should be checked against the 512×512 PNG in the repo — `~/workspace/goals/fincalc-finance-calculator-app/files/fincalc-icon-512.png` is a valid PNG, **512×512**, 236,702 bytes (verified). In-app launcher icons exist at all densities (`mipmap-mdpi` 48px through `mipmap-xxxhdpi` 192px, plus round variants). Uptodown's floor is >128×128 square; the 512px asset clears it. (Whether the submitted icon file is this 512px asset could not be verified from the repo alone.)
- **Screenshots:** 5 were "Pending approval" at rejection; no screenshot files were found in this repo to check resolution — the fix phase must confirm they are real device-resolution screenshots, not downscaled.
- **Description:** 69 words at rejection vs Uptodown's 100+ word guidance for the full description; short description must be ≤ 70 characters. The description must accurately describe all 90 calculators and the features (offline, no login, PDF export, history/favorites).
- **Category:** Finance. **Ads flag** must be set honestly (the app contains ads). Distribution: Free. Development stage for the new version: Final Release. Per-version changelog required.
- **Name change (user order, see §12):** the listing title must also become "Pro FinCalc" at resubmission time (console field, not in the repo).

---

## 12. Display-name rename map — "FinCalc" → "Pro FinCalc" (package stays `com.fincalc.app`)

User order 2026-11-16 ~14:31 PKT: the app's **display name** becomes **"Pro FinCalc"** in the update; package name `com.fincalc.app` stays the same. The Uptodown account already has a **different rejected app called "FinCalc Pro"** — "Pro FinCalc" (word order reversed) stays distinct from it. "FinCalc Pro" does **not** appear anywhere in this project (grep confirms; the only near-matches are the words "FinCalc provides…" in legal text and the "FinCalc ProGuard rules" code comment).

### Must change (user-visible "FinCalc" strings)

| # | Place | File:line | What it controls |
|---|---|---|---|
| 1 | `app_name` string | `app/src/main/res/values/strings.xml:3` | **The launcher display name** — `AndroidManifest.xml:13` sets `android:label="@string/app_name"`, and the APK's `application-label` comes from here. This is the primary change. |
| 2 | TopAppBar title (Home route) | `app/src/main/java/com/fincalc/app/MainActivity.kt:110` | `"FinCalc"` title on the Home screen |
| 3 | TopAppBar fallback title | `MainActivity.kt:127` | `else -> "FinCalc"` |
| 4 | Navigation drawer header | `MainActivity.kt:145` | `Text("FinCalc", …)` at the top of the drawer |
| 5 | About screen title | `app/src/main/java/com/fincalc/app/ui/screens/LegalScreens.kt:48` | `Text("FinCalc", fontSize = 30.sp …)` |
| 6 | About body | `LegalScreens.kt:55,57` | "FinCalc is a powerful and easy-to-use finance calculator app…" |
| 7 | About copyright | `LegalScreens.kt:64` | "© 2026 FinCalc. All rights reserved." |
| 8 | Privacy Policy body | `LegalScreens.kt:73,76,79,91` | 4 mentions ("FinCalc respects your privacy…", "FinCalc does not ask you…", "FinCalc shows ads…", "FinCalc is not directed at children…") |
| 9 | Terms & Conditions body | `LegalScreens.kt:104,106,107,113,119,122` | 6 mentions, incl. "FinCalc provides 60 financial calculators" (also fix the count → 90, §9 #2) |
| 10 | Financial Disclaimer body | `LegalScreens.kt:131` | "FinCalc provides estimates…" |
| 11 | FAQ answers | `LegalScreens.kt:188,191` | "What data does FinCalc collect?" / "Ads keep FinCalc free…" |
| 12 | PDF export footer | `app/src/main/java/com/fincalc/app/util/PdfExport.kt:26` | "Generated by FinCalc — Smart Financial Calculators for Everyone." |
| 13 | PDF export header | `PdfExport.kt:62` | `canvas.drawText("FinCalc", …)` drawn at the top of every exported PDF |
| 14 | PDF email subject | `app/src/main/java/com/fincalc/app/ui/screens/CalculatorScreen.kt:144` | `"${def.title} — FinCalc"` |
| 15 | Copy/share labels | `CalculatorScreen.kt:226,232,411,425` | Clipboard label "FinCalc result"; share text "— Calculated with FinCalc" / "— Converted with FinCalc" |
| 16 | History share text | `app/src/main/java/com/fincalc/app/ui/screens/ListsScreens.kt:125` | "— Calculated with FinCalc" |
| 17 | Uptodown listing title | **Console field, not in the repo** | The listing name shown on Uptodown must also be changed to "Pro FinCalc" at resubmission time (do not publish yet per user order). |

### Must NOT change (identifiers, not display name)
- **Package name** `com.fincalc.app` — manifest, `app/build.gradle.kts` (`applicationId`, `namespace`), all `package com.fincalc.app` declarations, `google-services.json`. Changing it would break the Uptodown update chain (same package required).
- **Class/type names:** `FinCalcApp`, `FinCalcDao`, `FinCalcDatabase`, `FinCalcTheme`, `FinCalcNav`, `FinCalcDatabase` — code identifiers, invisible to users.
- **Theme/color names:** `Theme.FinCalc` (`themes.xml:7`), `FinCalcBlue/Purple/Gold/Green/Background` (`ui/theme/Theme.kt:14-21`) — code identifiers.
- **Local storage names:** Room database `"fincalc.db"` (`HistoryDb.kt`), DataStore `"fincalc_settings"` (`SettingsStore.kt`), PDF filename prefix `fincalc-…` (`PdfExport.kt:100`) — renaming these would wipe existing users' history/settings.
- **Tagline/description strings** (`strings.xml:4-5` — "Smart Financial Calculators for Everyone", "Calculate. Plan. Grow.") — no rename ordered for these; keep unless the user says otherwise.
- **Docs only:** `README.md`, code comments (e.g. `SettingsScreen.kt:43`, `Theme.kt:11`, `Models.kt:4`) — update for consistency, not required for the rename.
- **Icon artwork:** the launcher icon PNGs contain no text that was verified in this inspection — the fix phase should visually confirm the icon has no embedded "FinCalc" wordmark that would contradict the new name.

---

## 13. Ranked likely causes of the rejection + fix list (fixes NOT done — inspection only)

| Rank | Cause | Confidence | Fix |
|---|---|---|---|
| 1 | **Submitted artifact is a debug build** — debuggable, debug-signed (§2) | High | Create keystore + `signingConfigs`; CI builds **signed release APK**; versionCode **3**, versionName 1.0.2; keep package `com.fincalc.app` |
| 2 | **Stale in-app text contradicting the listing** — "60 calculators" (About + Terms), "Version 1.0.0" (About + Settings) (§9) | High | Change to 90 / 1.0.2 everywhere listed in §9 and §12 |
| 3 | **AdMob App ID unconfirmed / possible app-entry mismatch** — manifest warns it must be confirmed; banner unit supplied separately (§6.1) | Medium–High | **Console-only:** confirm the App ID belongs to the FinCalc entry; recreate any unit under the wrong entry |
| 4 | **Ads never recover from a load failure + zero logging** (§6.2) | Medium–High (for the ads complaint) | Retry loads on failure (with backoff) for all 3 formats; log load errors; check `canRequestAds()` and request consent **before** initializing MobileAds |
| 5 | **Thin listing description** (69 words) + screenshots pending approval (§11) | Medium | Full description 100+ words accurately covering all 90 calculators; short description ≤ 70 chars; verify screenshots are device-resolution; set Ads flag; rename listing to "Pro FinCalc" |
| 6 | **Generic "minimum quality standards" bucket** — any usability/crash issue on the editors' devices (§1) | Unknown | Device/emulator run: startup, all 90 calculators, navigation, rotation, offline mode; VirusTotal scan of the exact resubmission APK (keep the report) |
| 7 | **Display name** "FinCalc" → "Pro FinCalc" (§12) | Ordered by user | Rename per the map in §12 (strings.xml first, then all user-visible strings, then the console listing title) |
| 8 | Capital-gains 2025 thresholds | Low | Update to 2026 thresholds (currently disclosed in-app, §3.8) |

---

## 14. What this inspection could NOT verify

1. **Uptodown console / rejection email** — not accessed in this session; the reason in §1 is taken from the console check recorded 2026-11-16 ~14:29 PKT. If the rejection email contains more detail than the console notification, it has not been seen.
2. **Runtime behaviour** — no device or emulator was available here: startup, real ad serving, crashes on real devices, performance, and screenshot quality were not tested. A device run is owed before resubmission.
3. **VirusTotal scan** of the exact submitted APK — not run (no API key in this session). Run it on the resubmission APK and keep the report; any detection must be explained or the build changed.
4. **AdMob console state** — whether the manifest App ID belongs to the FinCalc entry, which entry each unit belongs to, app-readiness/payment state, and whether `app-ads.txt` is hosted at the linked website's root (curl from this VM failed with HTTP 000 — inconclusive, not a 404).
5. **Submitted icon/screenshots** — the repo's 512×512 icon is verified, but whether the console holds that exact file (and the 5 screenshots' resolution) could not be verified from the repo.
6. **Actual ad serving on a device** — the load-failure analysis in §6 is static; the true no-fill error code (e.g. AdMob error 3, no fill) can only be read from logcat on a device after adding logging.

---

## Appendix — evidence index (tools used)

- APK download: `curl` from `github.com/shehzadarain252/fincalc/releases/download/v1.0.1/app-debug.apk` → 26,371,671 bytes, SHA-256 `88c77dde…a84b4f6fde8a0e9` (full value in §2).
- `aapt2 dump badging` + `aapt2 dump xmltree --file AndroidManifest.xml` (`/home/hatch/Android/Sdk/build-tools/34.0.0/aapt2`).
- `apksigner verify --verbose --print-certs` (build-tools 34.0.0, `JAVA_HOME=/home/hatch/jdk17`).
- Dex scans: `grep -a` over `classes.dex` … `classes13.dex` (test publisher ID: 0 matches; production-format unit IDs: 3 distinct).
- Source greps: calculator inventory (`CalculatorDef(` counts per file), `category = Category` counts, `TODO|FIXME|coming soon|placeholder`, `Log.` in ads code (0), `canRequestAds` usages (definition only), `"FinCalc"` occurrences (77 total in `app/src`, incl. code identifiers).
- Reference-result recomputation: Python (EMI, compound, BMI) + manual bracket math (US 2026 tax), all matching §3.7.
