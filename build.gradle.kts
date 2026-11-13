// FinCalc — root build file. Versions pinned to the set proven by the
// EduMind AI CI builds (AGP 8.5.2, Gradle 8.7, Kotlin 1.9.23, JVM 17).
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("com.google.devtools.ksp") version "1.9.23-1.0.19" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
}
