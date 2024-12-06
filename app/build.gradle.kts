plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "commer.mmr.instock"
    compileSdk = 34

    packagingOptions {//acho que essa porra vai fuder tudo
        resources {
            exclude("/META-INF/NOTICE.md")
            exclude("/META-INF/LICENSE.md")
            exclude("/META-INF/DEPENDENCIES")
        }
    }

    defaultConfig {
        applicationId = "commer.mmr.instock"
        minSdk = 23
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")

}