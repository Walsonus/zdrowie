import java.io.File
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android) // Teraz będzie używać wersji z libs.versions.toml (np. 2.0.0)
    id("kotlin-kapt")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")

if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { stream ->
        localProperties.load(stream)
    }
}

val mapsApiKey = localProperties.getProperty("MAPS_API_KEY")
if (mapsApiKey == null || mapsApiKey == "KLUCZ_NIE_ZNALEZIONY_W_LOCAL_PROPERTIES") {
    println("OSTRZEŻENIE: Klucz MAPS_API_KEY nie został znaleziony w local.properties. Mapa Google może nie działać poprawnie.")
}
val finalMapsApiKey = mapsApiKey ?: "KLUCZ_NIE_ZNALEZIONY_W_LOCAL_PROPERTIES"

android {
    namespace = "pack.zdrowie"
    compileSdk = 35

    defaultConfig {
        applicationId = "pack.zdrowie"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["mapsApiKeyPlaceholder"] = finalMapsApiKey
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
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("com.google.android.gms:play-services-ads:24.3.0")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("com.github.sad-adnan:customToast:v1.3")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity)
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation ("com.google.dagger:hilt-android:2.48")
    kapt ("com.google.dagger:hilt-compiler:2.48")
    implementation ("androidx.room:room-runtime:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")
    //implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test:runner:1.5.2")
    testImplementation("androidx.test:rules:1.5.0")
    testImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("org.mockito:mockito-core:4.11.0")
    testImplementation("org.mockito:mockito-inline:4.11.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation("androidx.fragment:fragment-testing:1.6.0")
    testImplementation(libs.testng)
    testImplementation(kotlin("test"))

    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.espresso.intents)
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test:core-ktx:1.5.0")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")

    // Enforce consistent Kotlin stdlib versions
    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib: 2.0.0") {
            because("Wymuszenie spójnej wersji Kotlin stdlib z kompilatorem 2.0.0")
        }
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.0.0") {
            because("Wymuszenie spójnej wersji Kotlin stdlib-jdk7 z kompilatorem 2.0.0")
        }
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.0") {
            because("Wymuszenie spójnej wersji Kotlin stdlib-jdk8 z kompilatorem 2.0.0")
        }
        implementation("org.jetbrains.kotlin:kotlin-stdlib-common:2.0.0") {
            because("Wymuszenie spójnej wersji Kotlin stdlib-common z kompilatorem 2.0.0")
        }
    }
}