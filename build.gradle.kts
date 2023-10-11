import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.20"
    id("org.jetbrains.compose") version "1.5.1"
}

group = "top.myrest"
version = "1.0.0"
val entry = "$name.jar"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

val myflowVersion = "1.0.0"

dependencies {
    compileOnly(compose.desktop.currentOs)
    compileOnly("top.myrest:myflow-kit:$myflowVersion")
    testImplementation("top.myrest:myflow-baseimpl:$myflowVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.jar {
    archiveFileName.set(entry)
    val exists = mutableSetOf<String>()
    val files = mutableListOf<Any>()
    configurations.runtimeClasspath.get().allDependencies.forEach { dependency ->
        configurations.runtimeClasspath.get().files(dependency).forEach { file ->
            if (exists.add(file.name)) {
                files.add(if (file.isDirectory) file else zipTree(file))
            }
        }
    }
    from(files)

    exclude(
        "module-info.class",
        "META-INF/NOTICE",
        "META-INF/LICENSE",
        "META-INF/versions/9/module-info.class",
    )
}

tasks.build {
    doLast {
        copy {
            from("./build/libs/$entry")
            into(".")
        }
        val specFile = file("./plugin-spec.yml")
        val specContent = specFile.readLines(Charsets.UTF_8).joinToString(separator = System.lineSeparator()) {
            if (it.startsWith("version:")) {
                "version: $version"
            } else if (it.startsWith("entry:")) {
                "entry: ./$entry"
            } else it
        }
        specFile.writeText(specContent, Charsets.UTF_8)
        specFile.appendText(System.lineSeparator())
    }
}
