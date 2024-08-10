
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
        classpath ("com.android.tools.build:gradle:8.5.1")
        classpath ("com.sendgrid:sendgrid-java:4.8.2")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.50")
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false

}