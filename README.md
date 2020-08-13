## Vert.X Web/React Fullstack Kotlin MPP Example

An example of Kotlin MPP (multiplatform project) having [Vert.x](https://vertx.io/docs/vertx-web/kotlin/) for a
Kotlin/JVM backend, and a [React](https://github.com/JetBrains/kotlin-wrappers) on Kotlin/JS target.

Based on [Gradle](https://gradle.org/kotlin/) Kotlin DSL and cutting edge available versions.

## How It Works

You develop a backend on Kotlin/JVM target using Vert.x web and a frontend using React Kotlin wrappers on Kotlin/JS
target while having shared common code (like data classes and/or buisnes domain logic) in Kotlin/Common target.


Gradle configuration provide a group of tasks (Frontend integration) to start backend in a full stack mode integrated
with the frontend built from the Kotlin/JS target.

**NOTE:** _Directory `src/jvmMain/resources/webroot` must be ignored, and you should not author it as it is fully managed
by the Gradle_

## Modules

##### Common

Contains sample serializable data class (POJO) that is being reused between JVM backend and JS frontend

##### JVM

A Sample Vert.x verticle with a web-server that serves a sample data class to a frontend
Actual frontend served from the default `resources/webroot` location that is managed automatically via Gradle tasks

#### JS

A browser client based on Kotlin React wrappers that consumes a sample data class on a button click using ktor client
asynchronously with kotlin serialization support 

## Gradle Tasks

Integration occurs in the task group: Frontend integration tasks

* cleanWebroot - Delete current `src/jvmMain/resources/webroot` directory
* embedCurrentFrontendIntoWebroot - Copy current `build/distributions` content into `src/jvmMain/resources/webroot`
* embedDevelopmentFrontendIntoWebroot - Run `embedCurrentFrontendIntoWebroot` after `jsBrowserDevelopmentWebpack` task
* embedProductionFrontendIntoWebroot - Run `embedCurrentFrontendIntoWebroot` after `jsBrowserProductionWebpack` task
* runDevelopmentFullStack - Start backend after task `embedDevelopmentFrontendIntoWebroot`
* runProductionFullStack - Start backend after task `embedProductionFrontendIntoWebroot`


## Known Issues

Because integration tasks modifying `resources/webroot` Gradle cannot determine if there was an actual change so it
causes Gradle to re-run `jsBrowser*Webpack` on each invocation.

You're welcome criticize and contribute to improve this example via issues and pull requests here on GitHub.

## Resources

**Vert.x**

* [React Tutorial](https://how-to.vertx.io/single-page-react-vertx-howto/)

**Gradle**

* [Working With Files](https://docs.gradle.org/current/userguide/working_with_files.html)
* [More About Tasks](https://docs.gradle.org/current/userguide/more_about_tasks.html)

**Stack Overflow**

* [Question that started all of this](https://stackoverflow.com/q/63348915/7598113)