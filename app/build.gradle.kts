plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.fincalc.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.fincalc.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 3
        versionName = "1.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // Release signing (added 2026-11-16, v1.0.2; fixed 2026-11-16): the
    // release keystore is NEVER committed. CI creates keystore.properties
    // from GitHub secrets (KEYSTORE_BASE64, KEYSTORE_PASSWORD, KEY_ALIAS,
    // KEY_PASSWORD) before building; local builds read the same file from
    // the project root. Without keystore.properties the release build is
    // left unsigned. NOTE: the file is read with plain Kotlin stdlib
    // (readLines/associate) — java.util.Properties and
    // java.io.FileInputStream do NOT resolve in this Kotlin DSL script
    // (CI run #49 failed on them with "Unresolved reference").
    val keystorePropsFile = rootProject.file("keystore.properties")
    val keystoreProps: Map<String, String> = if (keystorePropsFile.exists()) {
        keystorePropsFile.readLines()
            .filter { it.contains("=") }
            .associate { line ->
                val parts = line.split("=", limit = 2)
                parts[0].trim() to parts[1].trim()
            }
    } else {
        emptyMap()
    }

    signingConfigs {
        create("release") {
            keystoreProps["storeFile"]?.let { storeFile = file(it) }
            keystoreProps["storePassword"]?.let { storePassword = it }
            keystoreProps["keyAlias"]?.let { keyAlias = it }
            keystoreProps["keyPassword"]?.let { keyPassword = it }
        }
    }

    buildTypes {
        release {
            if (keystorePropsFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = "2024.09.00"
    implementation(platform("androidx.compose:compose-bom:$composeBom"))
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Material Components — provides the Theme.Material3.DayNight.NoActionBar
    // XML theme used as the Activity theme parent in res/values/themes.xml.
    // (The Compose material3 library does NOT include that XML theme,
    // so the resource linker fails without this dependency.)
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Networking (Retrofit + OkHttp + Gson) — exchange rates for the
    // Currency Converter (free no-key API, see RatesApi.kt).
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Room (local persistence: calculation history + favorites)
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // DataStore (settings: theme, currency, decimal precision, ad counters)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Google Mobile Ads (AdMob) + UMP consent
    implementation("com.google.android.gms:play-services-ads:23.0.0")
    implementation("com.google.android.ump:user-messaging-platform:2.2.0")

    // Firebase Analytics + Crashlytics (anonymous usage stats + crash reports)
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
