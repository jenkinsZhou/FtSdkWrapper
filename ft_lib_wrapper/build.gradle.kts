import groovy.util.Node
import groovy.util.NodeList
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}
group = "com.github.jenkinsZhou"
version = "1.0.2"
android {
    namespace = "com.fangtian.ftlibwrapper"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")


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
group = "com.github.jenkinsZhou"
version = "v1.0.5" // 版本号写成 tag 的版本
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.jenkinsZhou"
                artifactId = "FtSdkWrapper"
                version = "v1.0.5"

                //  关键：过滤掉非法的 <dependency> 节点
                pom.withXml {
                    val dependenciesNode = asNode().get("dependencies") as? Node
                    dependenciesNode?.let { deps ->
                        val toRemove = mutableListOf<Node>()
                        val children = deps.children() as NodeList
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

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    api(mapOf("name" to "tbs", "ext" to "aar"))
}
