// FinCalc — root build file. Versions pinned to the set proven by the
// EduMind AI CI builds (AGP 8.5.2, Gradle 8.7, Kotlin 1.9.24, JVM 17).
// NOTE: Compose Compiler 1.5.14 REQUIRES Kotlin 1.9.24 — Kotlin 1.9.23
// fails the build with a version-compatibility error (run #28).
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
}
