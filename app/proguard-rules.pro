# FinCalc ProGuard rules (release builds only; debug builds skip shrinking).
-keep class com.fincalc.app.** { *; }
# Keep Room entities/DAOs and Retrofit models from obfuscation issues.
-keep class androidx.room.** { *; }
-dontwarn com.squareup.okhttp3.**
-dontwarn retrofit2.**
