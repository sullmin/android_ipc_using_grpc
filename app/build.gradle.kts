plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.protobuf")
    id("androidx.room")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.android_ipc_grpc"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.android_ipc_grpc"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "option_name" to "option_value",
                )
            }
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/*"
        }
    }
    room {
        schemaDirectory("$projectDir/db/schemas")
    }

    flavorDimensions += "version"
    productFlavors {
        create("process_1") {
            dimension = "version"
            applicationIdSuffix = ".process_1"
            versionNameSuffix = "-1"
        }
        create("process_2") {
            dimension = "version"
            applicationIdSuffix = ".process_2"
            versionNameSuffix = "-2"
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.19.2"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.47.0"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc") { options += "lite" }
            }
            it.builtins {
                create("java") { options += "lite" }
            }
        }
    }
}

dependencies {
    // GRPC - PROTOBUF
    implementation("io.grpc:grpc-okhttp:1.47.0")
    implementation("io.grpc:grpc-netty:1.47.0")
    implementation("io.grpc:grpc-protobuf:1.47.0")
    implementation("io.grpc:grpc-stub:1.47.0")
    implementation("com.google.protobuf:protobuf-java:3.21.1")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("io.grpc:grpc-netty:1.47.0")

    // ROOM
    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    // ANDROID
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.0")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // TEST
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}