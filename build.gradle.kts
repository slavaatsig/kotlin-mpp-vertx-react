plugins {
    kotlin("multiplatform") version "1.4.0-rc"
    kotlin("plugin.serialization") version "1.4.0-rc"
    application
}

group = "com.example"
version = "1.0-SNAPSHOT"

application { mainClassName = "com.example.MainKt" }


repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://dl.bintray.com/kotlin/kotlin-js-wrappers")
    maven("https://dl.bintray.com/kotlin/kotlinx")
    maven("https://dl.bintray.com/kotlin/ktor")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    js {
        browser {
            binaries.executable()
            webpackTask {
                cssSupport.enabled = true
            }
            runTask {
                cssSupport.enabled = true
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }
    sourceSets {
        val kotlinVersion = "1.4.0-rc"

        val commonMain by getting {
            dependencies {
                kotlinX("serialization-runtime", "1.0-M1-$kotlinVersion") // JVM dependency
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))

                // Logger support vert.x web stack
                implementation("org.slf4j:slf4j-jdk14:1.7.7")

                // KotlinX dependencies for JVM
                kotlinX("coroutines-jdk8", "1.3.8-$kotlinVersion")

                // Vert.x
                vertx("core")
                vertx("web")
                vertx("lang-kotlin")
                vertx("lang-kotlin-coroutines")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-testng"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js", kotlinVersion))

                // KotlinX artifacts
                kotlinX("html-js", "0.7.1-$kotlinVersion")
                kotlinX("serialization-runtime-js", "1.0-M1-$kotlinVersion")
                kotlinX("io-js", "0.1.16")

                // Ktor artifacts
                ktor("client-js", "1.3.2-$kotlinVersion")
                ktor("client-serialization-js", "1.3.2-$kotlinVersion")
                ktor("client-json-js", "1.3.2-$kotlinVersion")

                // Kotlin JS wrappers
                kotlinJs("react", "16.13.1-pre.110-kotlin-$kotlinVersion")
                kotlinJs("react-dom", "16.13.1-pre.110-kotlin-$kotlinVersion")
                kotlinJs("styled", "1.0.0-pre.110-kotlin-$kotlinVersion")

            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

tasks.named<Wrapper>("wrapper") {
    gradleVersion = "6.6"
}


tasks.withType<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack>() {
    outputFileName = "spa.js"
}

/** A place that Vert.x expects static resources by default (where built frontend must present) */
val jvmWebroot by extra { "${project.sourceSets.main.get().resources.srcDirs.first()}/webroot" }

/** A place where Kotlin/JS produces frontend */
val jsDistribution by extra { "$buildDir/distributions" }

// Tasks that frontend integration depends upon
val jsBrowserDevelopmentWebpack = tasks.getByName("jsBrowserDevelopmentWebpack")
val jsBrowserProductionWebpack = tasks.getByName("jsBrowserProductionWebpack")

// Custom webroot cleanup task
val cleanWebroot by tasks.register<Delete>("cleanWebroot") {
    group = "frontend integration"
    delete(file(jvmWebroot))
}

// Hook custom cleanup task into build clean task
tasks.named("clean") {
    dependsOn(cleanWebroot)
}

/** Copy latest js distribution into into webroot */
val embedCurrentFrontendIntoWebroot by tasks.register<Copy>("embedCurrentFrontendIntoWebroot") {
    group = "frontend integration"
    from(jsDistribution)
    into(jvmWebroot)
    exclude { it.isDirectory && it.file.name == "webroot" }
}
embedCurrentFrontendIntoWebroot.mustRunAfter(cleanWebroot)

/** Copy development frontend distribution into webroot */
val embedDevelopmentFrontendIntoWebroot by tasks.register<Copy>("embedDevelopmentFrontendIntoWebroot") {
    group = "frontend integration"
    dependsOn(jsBrowserDevelopmentWebpack)
    dependsOn(embedCurrentFrontendIntoWebroot)
}
embedDevelopmentFrontendIntoWebroot.mustRunAfter(jsBrowserDevelopmentWebpack)

/** Copy production frontend distribution into webroot */
val embedProductionFrontendIntoWebroot by tasks.register<Copy>("embedProductionFrontendIntoWebroot") {
    group = "frontend integration"
    dependsOn(jsBrowserProductionWebpack)
    dependsOn(embedCurrentFrontendIntoWebroot)
}
embedProductionFrontendIntoWebroot.mustRunAfter(jsBrowserProductionWebpack)

// Create task to start fullstack backend with development distribution
tasks.register("runDevelopmentFullStack") {
    group = "frontend integration"
    dependsOn(embedDevelopmentFrontendIntoWebroot)
    dependsOn(tasks.named("run"))
}

// Create task to start fullstack backend with production distribution
tasks.register("runProductionFullStack") {
    group = "frontend integration"
    dependsOn(embedProductionFrontendIntoWebroot)
    dependsOn(tasks.named("run"))
}


// Shorthand helpers for declaring common artifacts

fun org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler.vertx(artifact: String, version: String = "3.9.2") {
    implementation("io.vertx:vertx-$artifact:$version")
}

fun org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler.ktor(artifact: String, version: String) {
    implementation("io.ktor:ktor-$artifact:$version")
}

fun org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler.kotlinJs(artifact: String, version: String) {
    implementation("org.jetbrains:kotlin-$artifact:$version")
}

fun org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler.kotlinX(artifact: String, version: String) {
    implementation("org.jetbrains.kotlinx:kotlinx-$artifact:$version")
}