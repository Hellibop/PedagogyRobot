import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import java.net.URI

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.relay)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.protobuf)
    alias(libs.plugins.dokka)
}

android {
    namespace = "com.example.aida"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.aida"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

protobuf {
    protoc {
        artifact = libs.google.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create( "java" ) {
                    option("lite")
                }

            }
        }
    }
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.google.material)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.exoplayer.dash)
    implementation(libs.media3.ui)
    implementation(libs.media3.common)
    implementation(libs.media3.exoplayer.hls)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.runtime.livedata)
    implementation(libs.datastore)
    implementation(libs.core.splashscreen)
    implementation(libs.compose.material)
    implementation(libs.compose.animation)
    implementation(libs.gson)
    implementation(libs.numberpicker)
    implementation(libs.reorderable)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.google.protobuf.javalite)
    implementation(libs.google.protobuf.kotlin.lite)
    kapt(libs.hilt.compiler)

    testImplementation(libs.junit.test)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.espresso)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.junit4)
    debugImplementation(libs.compose.ui.tooling)

    // Navigation
    implementation(libs.navigation.compose)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.navigation.dynamic.features.fragment)
    implementation(libs.navigation.runtime.ktx)
    androidTestImplementation(libs.navigation.testing)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)


    // CameraX
    implementation(libs.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.core)

// ML Kit barcode scanner
    implementation(libs.barcode.scanning)
}

// *** New Top-Level Dokka V2 Configuration Block ***
dokka {
    // Common configuration for the entire Dokka run
    moduleName.set("AIDA App Documentation") // Give your documentation a title


    // Or using the utility function style shown in the docs:
    // documentedVisibilities(VisibilityModifier.Public)

    // Optional: Configure memory isolation (useful for large projects)
    // dokkaGeneratorIsolation = ProcessIsolation {
    //    maxHeapSize = "4g" // Example: Increase heap size
    // }
    // OR to run in-process (can sometimes help, sometimes cause leaks)
    // dokkaGeneratorIsolation = ClassLoaderIsolation()


    // Configuration specific to the HTML output format (Publication)
    dokkaPublications.html {
        // Output directory for the HTML documentation
        outputDirectory.set(layout.projectDirectory.dir("docs/html"))

        // Optional: Configure plugins for this publication (e.g., DokkaBase)
        // pluginsConfiguration.html { // Check v2 docs for correct configuration of base plugin
        //    footerMessage.set("© 2024 My App") // Example footer
        // }

        // Optional: Suppress inherited members or fail on warning (moved here from v1 tasks.withType)
        // These *can* be set per publication if needed, or globally in the main dokka block
        // suppressInheritedMembers.set(true)
        // failOnWarning.set(true)


    }

    // Configuration for source sets within this module
    dokkaSourceSets {
        // Configure the 'main' source set (where your app code usually lives)
        main {

            // Visibility settings (Document public members) - THIS BELONGS HERE
            documentedVisibilities.set(setOf(VisibilityModifier.Public))
            // Platform for analysis. Use androidJvm for Android projects.
           // platform.set(Platform.androidJvm)

            // Suppress warnings about undocumented public members (set true if you want warnings)
            reportUndocumented.set(false)


            // Add links to Android SDK documentation
            externalDocumentationLinks.register("android-sdk") { // Use register for external links
                // Use String DSL as shown in V2 docs
                url("https://developer.android.com/reference/")
                packageListUrl("https://developer.android.com/reference/package-list")
            }

            // Optional: Link to your source code on GitHub/GitLab/etc.
            sourceLink {
                localDirectory.set(file("src/main/kotlin")) // Adjust if your code is in 'java' folder
                // Replace with your actual repository URL and branch (e.g., 'main')
                remoteUrl.set(URI("https://github.com/YOUR_USERNAME/YOUR_REPO_NAME/blob/main/app/src/main/kotlin"))
                remoteLineSuffix.set("#L") // Common suffix for line numbers on GitHub/GitLab
            }
            perPackageOption {
                matchingRegex.set("com.example.aida.ui.constants") // will match all .internal packages and sub-packages
                suppress.set(true)
            }
            // Optional: Include markdown files (like README.md) in your documentation
            // includes.from("Module.md") // Assumes Module.md is in your app module root
        }

        // If you have other source sets (e.g., 'debug', 'release', custom ones),
        // you can configure them here as well:
        // register("debug") { ... }
    }

    // If you added the dokkaJavadoc plugin, you could configure its publication here:
    // dokkaPublications.javadoc {
    //    outputDirectory.set(layout.buildDirectory.dir("docs/javadoc"))
    //    // ... other javadoc specific configurations ...
    // }
}
