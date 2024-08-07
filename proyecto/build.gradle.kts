buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        val navVersion = "2.5.3"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")

        classpath("com.android.tools.build:gradle:8.5.1")
        classpath("com.sendgrid:sendgrid-java:4.8.2")
        classpath("com.google.gms:google-services:4.3.15")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.5")

        val hiltVersion = "2.50"
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
}
