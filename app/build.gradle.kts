plugins {
    id("com.android.application")
}

android {
    namespace = "com.capstone.codingbug"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.capstone.codingbug"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("io.github.ParkSangGwon:tedpermission-normal:3.3.0") // 권한 설정을 위한 라이브러리
    implementation("mysql:mysql-connector-java:5.1.49")
    //implementation("mysql:mysql-connector-java:8.0.33") // mysql연결을 위한 라이브러리

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(files("libs\\[Android]TmapSDK_1.75\\lib\\com.skt.Tmap_1.75.jar"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("org.json:json:20230618")


}