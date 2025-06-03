pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        flatDir {
            dirs("ft_lib_wrapper/libs")
        }
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "FtSdkWrapper"
include(":app")
include(":ft_lib_wrapper")