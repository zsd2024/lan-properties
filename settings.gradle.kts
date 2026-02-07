pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://maven.architectury.dev/")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.legacyfabric.net/")
        maven("https://maven.minecraftforge.net/")
        maven("https://repo.spongepowered.org/maven/")
        maven("https://maven.wagyourtail.xyz/releases")
        maven("https://maven.wagyourtail.xyz/snapshots")
        maven("https://maven.ornithemc.net/releases")
        maven("https://maven.ornithemc.net/snapshots")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "lan-properties"

// CI 环境变量控制目标项目
val target = System.getenv("TARGET_PROJECT")

if (target != null) {
    println("Including only target project: $target")
    include(target)

    // 自动 include 对应的 common 模块
    val version = target.substringAfter("v")
    val common = "common-v$version"
    include(common)
    project(":$common").projectDir = file("common/$version")

    // 设置 projectDir
    val loader = target.substringBefore("-v")
    project(":$target").projectDir = file("$loader/$version")
} else {
    // 本地开发时 include 全部
    val supported = mapOf(
        "v1_21_6" to listOf("common", "fabric", "forge", "neoforge", "quilt"),
                          "v1_21"   to listOf("common", "fabric", "forge", "neoforge", "quilt"),
                          "v1_20_3" to listOf("common", "fabric", "forge", "neoforge", "quilt"),
                          "v1_20"   to listOf("common", "fabric", "forge", "neoforge", "quilt"),
                          "v1_12"   to listOf("common", "fabric", "forge", "ornithe"),
    )

    supported.forEach { (version, loaders) ->
        loaders.forEach { loader ->
            include("$loader-$version")
            project(":$loader-$version").projectDir = file("$loader/$version")
        }
    }
}
