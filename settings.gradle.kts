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

// 支持的版本与其对应的 loader
val supported = mapOf(
    "v1_21_6" to listOf("common", "fabric", "forge", "neoforge", "quilt"),
    "v25w14craftmine" to listOf("common", "fabric"),
    "v1_21" to listOf("common", "fabric", "forge", "neoforge", "quilt"),
    "v1_20_3" to listOf("common", "fabric", "forge", "neoforge", "quilt"),
    "v1_20" to listOf("common", "fabric", "forge", "neoforge", "quilt"),
    "v1_12" to listOf("common", "fabric", "forge", "ornithe"),
)

if (target != null) {
    println("Including only target project: $target")

    include(target)

    val version = target.substringAfter("v")   // e.g. "1_12"
    val commonVersioned = "common-v$version"
    include(commonVersioned)
    project(":$commonVersioned").projectDir = file("common/v$version")

    val loader = target.substringBefore("-v")
    project(":$target").projectDir = file("$loader/v$version")

    // include 该版本支持的 loader
    val versionKey = "v$version"
    supported[versionKey]?.forEach { loaderName ->
        include(loaderName)
        project(":$loaderName").projectDir = file(loaderName)
    }
} else {
    // 本地开发 include 全部
    sequenceOf(
        "common",
        "fabric",
        "forge",
        "neoforge",
        "quilt",
        "ornithe"
    ).forEach {
        include(it)
        project(":$it").projectDir = file(it)
    }

    supported.forEach { (version, loaders) ->
        loaders.forEach { loader ->
            include("$loader-$version")
            project(":$loader-$version").projectDir = file("$loader/$version")
        }
    }
}
