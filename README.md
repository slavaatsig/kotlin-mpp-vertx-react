## Vert.X Web/React Fullstack Kotlin MPP (not yet working) Example

### Modules

##### Common

Contains sample serializable data class (POJO) that is being reused between JVM backend and JS frontend

##### JVM

A Sample Vert.x verticle with a web-server that serves a sample data class to a frontend

#### JS

A browser client based on Kotlin React wrappers that consumes  a sample data class

## Problem

It is not clear to me how to configure the Kotlin MPP (multiplatform platform project) project using Gradle (Kotlin DSL) to use Vert.x web for on Kotlin/JVM target with Kotlin React on the Kotlin/JS target.

#### You can make it work if you:

* First run any of the *Kotlin browser* Gradle tasks like `browserDevelopentRun` and after browser opens and renders React SPA (single page application) you can
* stop that task and then
* start the Vert.x backend with task `run`.

After that, without refreshing the remaining SPA in the browser, you can confirm that it can communicate with the backend.

#### Question

What are the possible ways/approaches to *glue* these two projects into one Kotlin MPP project?
