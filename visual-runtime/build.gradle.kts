plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.lsam.visualruntime"
    compileSdk = 36

    defaultConfig {
        minSdk = 28
        targetSdk = 36
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.core)

    testImplementation(libs.junit4)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

    androidTestImplementation(libs.okhttp)
    androidTestImplementation(libs.mockwebserver)
}

tasks.register("ciCheck") {
    dependsOn("testDebugUnitTest")
    dependsOn("connectedDebugAndroidTest")
}
