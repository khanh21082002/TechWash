plugins {
    id("com.android.application") version "8.5.1"
    id("com.google.gms.google-services") version "4.4.2"
}

android {
    namespace = "com.example.techwash"
    compileSdk = 34

    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
    }

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

    implementation("com.google.auth:google-auth-library-oauth2-http:1.3.0")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")


    // Sử dụng Firebase BOM để quản lý phiên bản
    implementation(platform("com.google.firebase:firebase-bom:31.2.3")) // Cập nhật lên phiên bản mới nhất
    implementation ("com.google.firebase:firebase-messaging:23.1.0")
    // Firebase
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // Các phụ thuộc khác...
    implementation("androidx.navigation:navigation-fragment:2.4.2")
    implementation("androidx.navigation:navigation-ui:2.4.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.squareup.picasso:picasso:2.71828") {
        exclude(group = "com.android.support")
        exclude(module = "exifinterface")
        exclude(module = "support-annotations")
    }

    // Google Service
    implementation("com.google.android.gms:play-services-maps:18.0.2")
    implementation("com.google.android.gms:play-services-places:17.0.0")
    implementation("com.google.android.gms:play-services-location:19.0.1")

    // Thư viện khác
    implementation("kr.co.prnd:readmore-textview:1.0.0")
    implementation("com.apachat:swipereveallayout-android:1.1.2")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}