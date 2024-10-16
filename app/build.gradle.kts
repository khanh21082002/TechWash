plugins {
    alias(libs.plugins.android.application);
    id("com.google.gms.google-services") version "4.3.15"
}

android {
    namespace = "com.example.techwash"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.techwash"
        minSdk = 24
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
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")


    //firebase

    implementation("com.google.firebase:firebase-database:19.2.1")
    implementation("com.google.firebase:firebase-firestore:24.3.1")
    implementation("com.google.firebase:firebase-auth:21.0.8")
    implementation("com.google.firebase:firebase-storage:20.0.2")
    implementation(platform("com.google.firebase:firebase-bom:26.3.0"))
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("androidx.navigation:navigation-fragment:2.4.2")
    implementation("androidx.navigation:navigation-ui:2.4.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.squareup.picasso:picasso:2.71828") {
        exclude(group = "com.android.support")
        exclude(module = "exifinterface")
        exclude(module = "support-annotations")
    }

    //google service
    implementation ("com.google.android.gms:play-services-maps:18.0.2")
    implementation ("com.google.android.gms:play-services-places:17.0.0")
    implementation ("com.google.android.gms:play-services-location:19.0.1")
    implementation ("kr.co.prnd:readmore-textview:1.0.0")
}