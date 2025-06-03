import groovy.util.Node
import groovy.util.NodeList
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

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
version = "v1.1.0" // 版本号写成 tag 的版本



dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    api(mapOf("name" to "tbs", "ext" to "aar"))
}


afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.jenkinsZhou"
                artifactId = "FtSdkWrapper"
                version = "v1.1.4" // 👈 修改版本号

                pom.withXml {
                    val root = asNode()
                    val dependenciesList = root.get("dependencies")
                    if (dependenciesList is List<*> && dependenciesList.firstOrNull() is Node) {
                        val dependenciesNode = dependenciesList.first() as Node
                        val children = dependenciesNode.value() as? MutableList<*> ?: return@withXml
                        val toRemove = mutableListOf<Node>()

                        println("【JitPack】开始检查 POM 中的依赖项...")

                        for (child in children) {
                            if (child is Node) {
                                val artifactId = child.get("artifactId")?.toString()
                                val groupId = child.get("groupId")?.toString()
                                val version = child.get("version")?.toString()

                                println("发现依赖项：artifactId = $artifactId, groupId = $groupId, version = $version")

                                if (artifactId == "tbs" && groupId.isNullOrBlank() && version.isNullOrBlank()) {
                                    println("发现非法依赖：$artifactId（缺少 groupId 和 version），准备移除")
                                    toRemove.add(child)
                                }
                            }
                        }

                        toRemove.forEach { dependenciesNode.remove(it) }

                        if (toRemove.isEmpty()) {
                            println("未发现非法依赖，POM 不需要修改")
                        } else {
                            println("已成功移除 ${toRemove.size} 个非法依赖项")
                        }
                    } else {
                        println("未找到 <dependencies> 节点，POM 文件结构可能异常")
                    }
                }
            }
        }
    }
}