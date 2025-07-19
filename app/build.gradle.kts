import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.gradle.kotlin.dsl.coreLibraryDesugaring
import java.util.UUID

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    //id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.mynotes"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mynotes"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

        // ✅ This line enables desugaring
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

}

tasks.register("addUUID") {
    doLast {
        val uuid = UUID.randomUUID().toString()
        val outputDir = File("$projectDir/src/main/assets/model_en_us")
        val uuidFile = File(outputDir, "uuid")

        outputDir.mkdirs()
        uuidFile.writeText(uuid)
    }
}

tasks.named("preBuild").configure {
    dependsOn("addUUID")
}


dependencies {
    val nav_version = "2.9.0" // Latest as of June 2025
    val compose_version = "1.8.2" // Replace with latest stable
    val room = "2.7.1" // Latest stable
    val core_ktx_version = "1.16.0" // Latest core-ktx

    // Room
    implementation("androidx.room:room-runtime:$room")
    implementation("androidx.room:room-ktx:$room")
    kapt("androidx.room:room-compiler:$room")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:$nav_version")
    implementation ("com.google.accompanist:accompanist-navigation-animation:0.36.0")

    // Core KTX
    implementation("androidx.core:core-ktx:$core_ktx_version")

    // Jetpack Compose
    implementation("androidx.compose.ui:ui:$compose_version")
    implementation("androidx.compose.material:material:$compose_version")
    implementation("androidx.compose.ui:ui-tooling-preview:$compose_version")

    //icon
    implementation("br.com.devsrsouza.compose.icons:tabler-icons:1.1.1")
    implementation("io.coil-kt.coil3:coil-compose:3.2.0")

    implementation("io.coil-kt:coil-compose:2.7.0")

    //phone-lock
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("androidx.biometric:biometric-ktx:1.4.0-alpha02") // Or latest stable



    implementation ("androidx.compose.foundation:foundation:1.8.3")


    implementation("com.google.code.gson:gson:2.13.1")
    implementation("io.coil-kt.coil3:coil-gif:3.2.0")

    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation ("com.google.android.gms:play-services-auth:21.3.0")
    implementation("com.google.firebase:firebase-firestore:25.1.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")



    implementation ("androidx.credentials:credentials:1.6.0-alpha03")
    implementation ("androidx.credentials:credentials-play-services-auth:1.6.0-alpha03")
    implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.1")


    implementation("com.alphacephei:vosk-android:0.3.47")

    implementation("com.composables:icons-lucide-android:1.1.0")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
    implementation("com.google.android.material:material:1.14.0-alpha02")
}