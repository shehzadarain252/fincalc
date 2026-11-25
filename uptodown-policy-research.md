# Uptodown Policy & Rejection Research — FinCalc (v1.0.1, version code 2)

Research-only. Compiled 2026-11-16 from Uptodown's official Help Center, Developer Terms of Service, Transparency Center, and official blog. No console access, no sign-in, nothing submitted. Every claim carries its source URL.

**Headline finding: the exact reason FinCalc v1.0.1 was rejected CANNOT be determined from public sources.** The reason lives only in the Developers Console notification (uptodown.dev/notifications) and/or the rejection email to the developer account. Uptodown's rejection notices are typically terse — one developer's published notification reads only *"We're sorry, but your app doesn't meet the minimum quality standards to be published on Uptodown."* (see §7) — so even the console may not name a specific defect. The rest of this file documents every official criterion and the statistically most likely causes, so the fix work can be targeted.

---

## 1. Official publication criteria (complete list)

Source: Uptodown Help Center — "Uptodown's app publication criteria"
https://support.uptodown.com/hc/en-us/articles/360052792972-Uptodown-s-app-publication-criteria
(Fetched 2026-11-16; article last updated ~27 days before that.)

Uptodown "reserves the right to refuse the admission of apps", and lists these specific rejection criteria (quoted/paraphrased closely from the article):

1. **Apps without permission to be published** — only the author or the holder of distribution rights may submit. The team may request documentation to prove it; attempting to publish someone else's app results in the account being banned.
2. **Apps that don't work properly** — any app that "closes suddenly on a regular basis, causes problems of any kind or is compatible with very few devices".
3. **Apps that don't have a clear and specific function** — the submitted app "must have a clear function that does not contradict the information provided by the author".
4. **Apps with excessive and/or intrusive advertising** — "apps whose main purpose is to generate profits through ads are not accepted, setting aside their value as software".
5. **Apps with malware detected by VirusTotal that we consider harmful to the user** — "any app with indications of being harmful to users, either by jeopardizing their privacy or device features".
6. **Apps that perform any type of phishing or identity theft attempt** — "any app that attempts to obtain user data illegitimately or fraudulently".
7. **Apps that use trademarks or others' iconography in an attempt to impersonate them** — apps using icons, images or names registered by third parties, or that try to resemble them to confuse the user.
8. **IPTV apps that include paid channels lists.**
9. **Cheat apps, infinite coins or hacks.**
10. **Streaming apps that offer paid content for free.**
11. **Apps to download paid content** — apps allowing export/download of media content from paid services or platforms.
12. **Apps that collect links to private groups on social media** — apps distributing user accounts from social media/messaging services without explicit consent.
13. **Apps with explicit erotic or sexual content.**
14. **Apps with an invalid icon** — "only icons with a size larger than 128x128 pixels and a square aspect ratio will be accepted".
15. **Low-quality webviews** — "apps that simply display the content of a web page published on the Internet without offering any added value".
16. **Multiple apps similar to each other** — no maximum number of apps per author, but editors "may reject an app if they consider that the author is trying to publish multiple apps that are very similar to each other".
17. **Apps with not enough information** — "submitted apps must contain additional information attached from the developer. Though recommended, it's not mandatory to fill in all the fields, required or not, nor to attach images and a description of the app's operation."
18. **Unmodified templates** — "apps that have been created using templates and have not been modified to add new functionalities".
19. **Plain text** — "apps that only offer plain text without adding any additional functionality".
20. **Low-quality images** — "if the screenshots or an icon uploaded to an app are not of sufficient quality (i.e. low resolution), the app will be rejected until they are re-uploaded at a suitable resolution".

Plus: "our Content team **may consider other possible rejection reasons** not listed here based on the app's subject matter (terrorism, violence, hatred towards social groups) or on potential malicious use (piracy, scamming)." And: "**Any attempt to circumvent these requirements will be grounds to ban the registered user's account from our Developers Console indefinitely.**" The same criteria "could be applied after publication if an app is updated and any of these practices are detected in new versions."

---

## 2. What "does not meet our minimum quality standards" means — the #1 rejection bucket

Source: Uptodown official blog — "Publishing software in the age of AI: how we filter the apps we receive at Uptodown" (published 2026-10-08)
https://blog.en.uptodown.com/uptodown-apps-ai-publishing/

Key quotes (verbatim):

- "What we look at during review is the same as always: **is the app useful, is it unique, and is it safe?** A human-written app that fails those three tests is rejected. An AI-assisted app that passes them is published."
- "What we reject is the indiscriminate use of AI-assisted tools with no review and nothing added on top. Every app we publish has to bring real value to the user."
- "**More than half of the rejected apps didn't meet our minimum quality standards.** This is an internal standard that covers many factors not included in the other rejection reasons, such as serious usability problems, an overly confusing interface, excessive advertising, or any other point in our publishing guidelines, assessed by our specialized team of editors."
- On template clones: "In September, new listings that still carried the default package identifiers of popular app builders and generators were rejected **99% of the time**. We don't reject these apps because a tool helped build them, but because nobody added anything of their own."
- On webviews: "The problem arises when the app is nothing more than a website, with no offline mode, no notifications, no integration with the device, and nothing to justify installing it instead of opening a browser tab."
- On security: "Every file we host is analyzed by **VirusTotal**, which checks it against more than 75 antivirus engines, including Kaspersky, Bitdefender, Avast, McAfee, and AVG. … Detections aren't automatically a verdict. … That's why our team has the final word on every publication."
- "When we get it wrong, our support team is in direct contact with developers to fix any mistaken rejection or review an app again after resubmission."

September 2026 rejection breakdown (new apps + updates from registered developers; 3,321 rejected of 11,834 decided):

| Rejection reason | Share |
|---|---|
| Does not meet our minimum quality standards | 55.31% |
| Low-quality webview that adds no value | 20.99% |
| Uses a template that clones an existing app | 7.14% |
| Dangerous app | 1.78% |
| Other reasons in our publishing criteria | 14.78% |

And for **new apps only** in September 2026: 2,498 rejected (68.3%), 905 waiting for review (24.7%), 254 published (6.9%). "Of the new apps that have already received a decision, about **one in eleven was approved**."

Latest window — Transparency Center, metrics extracted 2026-10-17 to 2026-11-16 (fetched 2026-11-16):
https://en.uptodown.com/about-us/stats

- Apps uploaded from registered authors: 12,297; apps rejected: 4,061 (includes new submissions and update requests).
- Main rejection causes: Not meeting minimum quality standards **48.02%**; Low-quality webviews **26.55%**; Using templates that clone existing apps **6.23%**; Dangerous apps **0.59%**; Other reasons **18.62%**.
- VirusTotal results in the same window: No detections 97.13%; 1–3 detections 2.59%; 4 or more detections 0.28%. Files larger than 650 MB cannot be auto-scanned by VirusTotal — the Editorial team then does a manual review (FinCalc's ~25 MB APK is well under this).
- The Transparency Center also states: "Users cannot upload apps to Uptodown unless they are the developers or publishers themselves or have obtained legal authorization to do so."

---

## 3. Publication stages — what "Rejected" and "Pending" mean, and resubmission

Source: Uptodown Help Center — "Potential stages during the app publication process"
https://support.uptodown.com/hc/en-us/articles/12516112067597-Potential-stages-during-the-app-publication-process

Official stages (verbatim):

- **Draft**: app created but not yet sent for review; any field can be edited; can be deleted.
- **Pending**: shown "from the moment the developer sends it using the *Submit for review* until the request is solved (whether approved or rejected)".
- **Waiting for Publication**: approved; publicly available "in a matter of minutes".
- **Published**: published and available for download.
- **Rejected**: "The app has been rejected for not meeting the publication criteria. While at this stage, all the fields can be edited again. You can also add a new app version and request its review; in such case, its status will be reverted to *Draft*."
- **DMCA**: disabled and unavailable for download due to a legal (DMCA) requirement.

**Resubmission path (official):** at the Rejected stage, edit any/all fields, add a new app version, and request review again — status reverts to Draft, then Submit for review → Pending. There is no separate "appeal" flow documented in the Help Center; clarification is via Developer Support (see §8).

**UNVERIFIED — "Pending Revision" status:** FinCalc's console status was reported as "PENDING REVISION". That exact label does **not** appear in the official stages article above (the under-review state is called "Pending"). It may be a console UI label for a version under review, or an older label. Do not assume it equals "Rejected" — check the console's status panel and notifications to confirm the current state.

---

## 4. Publication requirements — listing metadata, icon, screenshots

Source: Uptodown Help Center — "How to publish an app on Uptodown"
https://support.uptodown.com/hc/en-us/articles/360053260491-How-to-publish-an-app-on-Uptodown
(Fetched 2026-11-16; last updated ~40 days before that.)

- **File upload:** "Choose the local file to be published. It should be in one of Uptodown's supported file formats. If the file size exceeds 4 GB, you need to use an external URL." The "Use URL" option must be a direct download link, not a landing page (a Google Play Store page link is explicitly not valid). "Important: The app to be published must comply with our publication criteria."
- **Icon:** "We recommend a square aspect ratio, a minimum resolution of 256x256, and a PNG file format." (The criteria article sets the hard floor: larger than 128x128, square.)
- **Name:** "trademarks of other apps, or combinations of words that may confuse users about the app's purpose, are not allowed."
- **Category:** "Choose the category that best fits your app's nature."
- **Package name:** auto-populated when an Android file is uploaded.
- **Official website:** the app's or developer's website; editors "may not include it if it seems dangerous, offensive, or links to content that does not meet the publication criteria".
- **PEGI:** recommended age range per PEGI code or equivalent.
- **Author:** name of the company, studio, or individual who created the app. **Nationality** of the creators is also a field.
- **Country restriction:** by default no regional restrictions; distribution can be limited per country.
- **Screenshots and videos:** screenshots, featured images, and videos "though in some cases they will be used only as documentation. Our content team reserves the right to create their own screenshots to improve your app's SEO". English-language screenshots are visible in all languages; other languages only in that language. YouTube videos can be linked in the Video tab. **Featured images must be exactly 1024 x 500 pixels**, "display a promotional banner-style image, and, where possible, include the app's logo".
- **Description:** per language. **Short description: no longer than 70 characters. Full body text description: minimum 50 words** — "must provide all relevant information about the app: uses, how it works, key features, etc. Responsible use of formatting is recommended (bold, italic, use of emojis)." The content team "may modify or rewrite the text if it does not meet our minimum quality standards".
- **App versions:** each version shows its status (Draft, Published, or Rejected). Per-version fields: Version (display name), Development stage (Final Release, RTM, RC, Beta, Alpha, Pre-Alpha), **Min/Max SDK Version (Android only)**, Languages, and a per-language changelog. "As a general rule, [the changelog] will be published by our Content team without changes."
- **License and distribution:** Distribution model (Free / Trial / Demo / Freemium), IAP flag, Subscription flag, **Ads flag** ("Indicates whether your app contains in-app ads. If your app is for Android and includes ads, you can optimize that revenue with Uptodown Ads"), License type (Proprietary by default; MIT/BSD/Apache/GPL etc. with License Text / Source Code / Notice URLs if required).
- After all fields: click **Submit for review**; "our Content team will evaluate and publish it as quickly as possible."

### Supported file formats (Android)

Source: Uptodown Help Center — "Supported file formats on Uptodown"
https://support.uptodown.com/hc/en-us/articles/360053260051-Supported-file-formats-on-Uptodown

- Android: **APK** (Android Package) and **XAPK** (package container: APK, OBB, split-APK, etc.).
- **AAB (Android App Bundle) is NOT in the supported-formats list.** Resubmission must be an APK (or XAPK), not an AAB.

---

## 5. Updating an app — version rules for the resubmission build

Source: Uptodown Help Center — "How to update an app that you already published on Uptodown"
https://support.uptodown.com/hc/en-us/articles/360061457991-How-to-update-an-app-that-you-already-published-on-Uptodown

- Apps section → select the app → Edit → Files tab → **+ Add new version**, then fill in the same information as a new publication.
- **"The packagename must be the same as that used by other previously released versions of the app."** (If a package-name change is needed, contact support to request it manually with reasons.)
- **"The version code of the file must be different and higher than the previously published versions.** Otherwise, that update will be listed as an older version and will not be offered as a primary download by default."
- Editable Information-tab fields: icon, author's name, official website, category, recommended age, nationality.
- "The publication time for app updates is much shorter than for new releases. Therefore, unless there are technical issues, **the updated file will be available for download on Uptodown within 24 hours**."
- Note: Uptodown's editors autonomously update apps that released a new version unless "Disable uploads by Uptodown editors" is checked.

**Applied to FinCalc:** the rejected build is v1.0.1 / version code 2, package com.fincalc.app. The resubmission build must keep package name `com.fincalc.app` and use version code **3 or higher** (and a new version name, e.g. 1.0.2). A build with the same version code 2 would be treated as an older version.

### Review time

Source: Uptodown Help Center — "How long does it take to publish an app on Uptodown?"
https://support.uptodown.com/hc/en-us/articles/360061046792-How-long-does-it-take-to-publish-an-app-on-Uptodown

- "Each app received through the Developer Console is reviewed by our team of editors, who have complete power to **decide whether an app will be published or not** based on our own publication criteria."
- "Although a first-come, first-served basis is applied, we may decide to delay or speed up the publication time depending on our own editorial interests. Therefore, **it is not possible to guarantee a specific publication date.**"
- Uptodown also creates "unique material like reviews, screenshots, and videos to accompany the downloaded app" but has "no commitment to make such material".
- "Publishing apps on Uptodown is a free service, and we make the utmost effort to **answer all publishing requests in the shortest amount of time**."

---

## 6. Developer Terms of Service — obligations relevant to a rejection

Source: Uptodown Terms of Service for Developers (Last Update: November 2026)
https://en.uptodown.com/developers-zone/services
(Fetched in full 2026-11-16.)

Relevant clauses (paraphrased closely; the ToS is the binding text):

- **Clause 3 — Use of Uptodown:** the developer is responsible for providing files, descriptions, images, icons, URLs and videos, "providing all the necessary information about each Application", and "accurately representing and conveying the required authorizations for the Applications to properly work with the Users' Devices". The developer "shall ensure that they comply with the policies and publication criteria in effect in Uptodown and **shall not hide or misrepresent any features of the Applications from such review**."
- Applications are "subject to the Uptodown evaluation and quality assurance process". "If the Developer changes any Application after providing it to Uptodown, they must resubmit it for the aforementioned evaluation purposes." "**If the Application is rejected after the above process, the Developer will be duly notified.**"
- The developer "shall provide accurate age ratings" and information required to comply with laws including "laws on the protection of minors, age verification and privacy".
- "The Applications must be installable and operable on Devices in accordance with their descriptions, without causing any adverse effects." The developer is responsible for the app's proper operation, warranties, user support, and for providing users "with valid and accurate contact information for customer support and legal purposes".
- **Clause 5 — Termination/removal:** UTD may reject/remove/suspend/reclassify an app if it (a) infringes laws or third-party rights, (b) breaches the Agreement (including policies/criteria), (c) infringes agreements with device manufacturers/providers, or (d) "otherwise negatively impacts UTD" or creates complaints by users "about the content or quality of the Application". UTD may suspend or permanently terminate a developer account for breach, repeated violations, misuse, or risk of harm; "UTD shall, when reasonably practicable, notify the Developer of the action taken."
- **Clause 6 — Representations and warranties:** apps "do not contain any viruses, hidden content, hacking capabilities … or other malicious applications … and do not collect any User information without their consent"; apps "do not contain any unauthorized third-party content or prohibited content"; "The Applications shall protect the privacy and data protection of Users."
- **Clause 8 — Personal data:** the developer is "solely responsible for protecting the privacy and legal rights of Users", collection/processing/storage/transmission must follow applicable law, user information may be used "only for the limited purposes for which the User has given consent", and personal/sensitive information must be stored "securely and only for as long as necessary". "The Applications must comply with all applicable privacy laws and regulations."
- **Clause 7 — Economic conditions:** in-app products must be made clear to users "clearly and transparently before purchasing"; during payment the developer must ensure the user consents to pay.
- **Clause 10:** UTD may change the Agreement; changes take effect for pre-existing developers "no sooner than 30 days after the changes are posted" (except legally required changes, effective immediately). Notices may be given "by email, regular mail, or postings on Uptodown."
- Governing law: Spain; courts of Málaga.

---

## 7. What developers actually report about rejections (third-party reports, 2024–2026)

These are developer reports, **not** official Uptodown statements. Included because they show what the console/email actually says in practice.

1. **Rejection notification wording (Instagram, 2026-08-31):** a screen recording of the Developers Console notifications page (uptodown.dev/notifications) shows a notification with subject "<App name> - has been rejected" and description **"We're sorry, but your app doesn't meet the minimum quality standards to be published on Uptodown."** The developer ("Nova Chat") contacted support asking for the specific reason and received a reply that treated him as an end-user seeking app support rather than a developer seeking clarification (ticket #132397, reply from Álvaro Toledo, 2026-08-31). Source: https://www.instagram.com/reel/Dct6TX1Mjyp/
   - Takeaway: expect a **generic** rejection message, not a defect list; getting a specific reason from support is difficult and may need a correctly-routed developer ticket.
2. **Resubmission after rejection works:** a developer blog (2026-10-25) notes their app's "Uptodown submission is not live yet: an earlier submission was rejected, we resubmitted it, and it is currently under review." Source: https://itch.io/devlog/1728658/blog-two-words-on-launch.amp
3. **No dedicated public "appeal" channel was found** in Uptodown's Help Center during this research. The route for clarification is the Developer Support ticket system (see §8).

---

## 8. Contacting Developer Support

Source: Uptodown Help Center — "How can I contact the Developer Support team?"
https://support.uptodown.com/hc/en-us/articles/360061525211-How-can-I-contact-the-Developer-Support-team

- "We have a support team specializing in **solving any issue or query that the users of our Developers Console may have**. To access the contact panel, just click on the *Contact Us* option located at the bottom right of the screen — although there are other sections of the Developer Console where you can send messages to our team."
- Three ticket types; choose by query (the fetched excerpt lists "Issue" — for a technical issue or trouble using the Developers Console to publish an app).
- "**There is no minimum response time for tickets**, and our support team may decide not to respond to a ticket if they feel it's not a valid question."
- The claim-authorship article also gives a support email for manual confirmations: support@uptodown.zendesk.com (for authorship claims; not confirmed as the channel for rejection queries — use the in-console Contact Us ticket first).
  Source: https://support.uptodown.com/hc/en-us/articles/360061653071

---

## 9. FinCalc-specific risk mapping (assessment against the official criteria — NOT confirmed causes)

The actual rejection reason is unknown (see headline). The table below maps FinCalc v1.0.1's known properties against the official criteria, as hypotheses to check during the APK/source inspection. **None of these is confirmed as the reason.**

| # | Official criterion / risk | FinCalc v1.0.1 facts | Risk level |
|---|---|---|---|
| 1 | "Does not meet minimum quality standards" (48–55% of all rejections) | The catch-all bucket: "serious usability problems, an overly confusing interface, excessive advertising". A finance app with 90 calculators must prove real, polished functionality; any crash, dead button, or confusing flow can land here. | **Highest — most likely bucket** |
| 2 | Excessive / intrusive advertising | Banner (anchored adaptive, above bottom nav on Home/Favorites/History/Settings) + interstitial after every 5th completed calculation + rewarded for PDF export. If the interstitial cadence or banner placement reads as ad-first, this criterion applies. Note the listing must also tick the "Ads" flag honestly. | Medium–High |
| 3 | Apps that don't work properly / crashes / narrow compatibility | minSdk 26, targetSdk 34, Kotlin/Compose. Needs device testing; any crash on the editors' test devices is a direct rejection. | Medium |
| 4 | Unmodified templates / template clones | FinCalc is custom Kotlin code (not an app-builder template), package com.fincalc.app (not a builder default). Low risk on the "default package identifier" test (99% rejection rate for those). But: the AI blog warns that AI-assisted apps with "nothing added on top" are rejected — the listing must show genuine, differentiated value (90 calculators, offline use, PDF export, history/favorites). | Low–Medium |
| 5 | Multiple similar apps | The catalog already has many finance/loan/EMI calculator apps; Uptodown editors may view another calculator as duplicative unless the listing clearly differentiates (90 calculators in one app, offline, no login). Also: the same developer account has EduMind AI published/pending — different app, different purpose, so not "similar apps" by the same author. | Low–Medium |
| 6 | Invalid icon / low-quality images | Criteria floor: icon >128x128, square. Publish guide recommends ≥256x256 PNG. Screenshots must not be low-resolution. Verify the submitted icon is 512x512 PNG (fincalc-icon-512.png exists) and screenshots are device-resolution, not downscaled. | Check |
| 7 | Not enough information | Description must be ≥50 words full text, short description ≤70 chars, and accurately describe the app. A thin or generic description can trigger this. | Check |
| 8 | VirusTotal / "dangerous app" | FinCalc 1.0.1 is a standard Kotlin app with AdMob + Firebase. AdMob/Firebase SDKs do not normally trigger detections, but **run the exact submitted APK through VirusTotal before resubmission** and keep the report — any detection must be explained or the build changed. (Dangerous apps are only ~0.6% of rejections.) | Low, verify |
| 9 | Privacy / data collection without consent | ToS Clause 8: apps must protect user privacy; data only for consented purposes. FinCalc stores data locally (Room/DataStore) and has no login. The AdMob + Firebase Analytics/Crashlytics data flows must be disclosed accurately. | Low–Medium |
| 10 | Trademark / impersonation | FinCalc name, icon and branding are original. Low risk. | Low |
| 11 | Unclear function / function contradicts listing | Listing must match the app exactly (90 calculators, categories as built). Any mismatch (e.g. listing claims features not in the build) is a direct rejection. | Check |
| 12 | File format / versioning | Resubmission must be an **APK** (AAB not supported), same package `com.fincalc.app`, version code **≥3**. | Procedural — must-do |

**UNVERIFIED items (could not be confirmed from official sources — do not treat as requirements):**
- Whether Uptodown requires a privacy-policy URL in the listing. The official publish guide's field list (icon, name, OS, category, package name, official website, PEGI, country restriction, nationality, author) contains **no privacy-policy field**, and the criteria article does not mention privacy policies. The ToS requires the *app* to protect user privacy (Clause 8), but a listing privacy-policy link was **not found** as a Uptodown requirement. (Google Play does require one — do not confuse the two stores.)
- The exact meaning of the "PENDING REVISION" console label (see §3).
- Whether Uptodown enforces a specific target-API / SDK level for Android (no such requirement was found in the fetched official pages; the publish form has Min/Max SDK fields the developer fills in).
- Whether the rejection email contains any more detail than the console notification (cannot verify without access to the developer mailbox).

---

## 10. Recommended pre-resubmission checklist (derived from the sources above)

1. **Read the actual rejection notice first** — Developers Console → Notifications (uptodown.dev/notifications) and the developer mailbox. Note the exact wording; if it is only the generic "minimum quality standards" message, proceed with the full checklist below.
2. **Fix and test the app** — no crashes, every calculator works, navigation complete, no dead buttons; test on more than one device/Android version.
3. **Ads audit** — banner must not cover content/navigation; interstitial only at natural breaks (current: 5th calculation) and never on launch; rewarded only voluntary; production ad units, never test ads in the release build.
4. **VirusTotal scan of the exact resubmission APK** — keep the report; investigate any detection.
5. **New build:** same package `com.fincalc.app`, version code **≥3**, new version name (e.g. 1.0.2), signed release **APK** (not AAB).
6. **Listing:** icon ≥256x256 PNG square (512x512 available); high-resolution real screenshots; short description ≤70 characters; full description ≥50 words accurately describing all 90 calculators and features; correct category (Finance); Ads flag set; distribution model Free; development stage Final Release; changelog for the new version.
7. **Resubmit:** console → app → Files → + Add new version → fill fields → Submit for review. Status goes Draft → Pending. Updates are normally available within 24 hours if approved; new-review timing is not guaranteed.
8. **If rejected again with only the generic message:** open a Developer Support ticket via the console's Contact Us (bottom right), ticket type "Issue", asking for the specific criterion failed; note there is no guaranteed response time.

---

## Sources (all fetched 2026-11-16)

1. Uptodown Help Center — App publication criteria: https://support.uptodown.com/hc/en-us/articles/360052792972-Uptodown-s-app-publication-criteria
2. Uptodown Help Center — How to publish an app: https://support.uptodown.com/hc/en-us/articles/360053260491-How-to-publish-an-app-on-Uptodown
3. Uptodown Help Center — How to update an app: https://support.uptodown.com/hc/en-us/articles/360061457991-How-to-update-an-app-that-you-already-published-on-Uptodown
4. Uptodown Help Center — Publication stages: https://support.uptodown.com/hc/en-us/articles/12516112067597-Potential-stages-during-the-app-publication-process
5. Uptodown Help Center — How long does it take to publish: https://support.uptodown.com/hc/en-us/articles/360061046792-How-long-does-it-take-to-publish-an-app-on-Uptodown
6. Uptodown Help Center — Supported file formats: https://support.uptodown.com/hc/en-us/articles/360053260051-Supported-file-formats-on-Uptodown
7. Uptodown Help Center — Contact Developer Support: https://support.uptodown.com/hc/en-us/articles/360061525211-How-can-I-contact-the-Developer-Support-team
8. Uptodown Help Center — Basic guide to register and publish: https://support.uptodown.com/hc/en-us/articles/4424141383181-Basic-guide-to-register-and-publish-apps-on-Uptodown
9. Uptodown Terms of Service for Developers (Nov 2026): https://en.uptodown.com/developers-zone/services
10. Uptodown official blog — Publishing software in the age of AI (2026-10-08): https://blog.en.uptodown.com/uptodown-apps-ai-publishing/
11. Uptodown Transparency Center (metrics 2026-10-17 → 2026-11-16): https://en.uptodown.com/about-us/stats
12. Uptodown Developers Console (login page; not accessed): https://uptodown.dev/apps/
13. Developer rejection report (Instagram, 2026-08-31): https://www.instagram.com/reel/Dct6TX1Mjyp/
14. Resubmission-after-rejection developer report (2026-10-25): https://itch.io/devlog/1728658/blog-two-words-on-launch.amp
