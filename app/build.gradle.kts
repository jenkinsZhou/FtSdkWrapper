plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}
import groovy . util . Node
        import groovy . util . NodeList
        android {
            namespace = "com.fangtian.sdkwrapper"
            compileSdk = 35

            defaultConfig {
                minSdk = 24
                targetSdk = 35
//                versionCode = 1
//                versionName = "1.0"

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
            }
            kotlinOptions {
                jvmTarget = "11"
            }
        }

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}


        afterEvaluate {
            publishing {
                publications {
                    create<MavenPublication>("release") {
                        from(components["release"])
                        groupId = "com.github.jenkinsZhou"
                        artifactId = "FtSdkWrapper"
                        version = "v1.0.8"

                        pom.withXml {
                            val root = asNode()
                            val depsList = root.get("dependencies")
                            if (depsList is List<*> && depsList.firstOrNull() is Node) {
                                val deps = depsList.first() as Node
                                val children = deps.value() as NodeList
                                val toRemove = mutableListOf<Node>()
                                for (child in children) {
                                    if (child is Node) {
                                        val artifactId = child.get("artifactId")?.toString()
                                        val groupId = child.get("groupId")?.toString()
                                        if (artifactId == "tbs" && (groupId == null || groupId.isBlank())) {
                                            toRemove.add(child)
                                        }
                                    }
                                }
                                toRemove.forEach { deps.remove(it) }
                            }
                        }
                    }
                }
            }
        }