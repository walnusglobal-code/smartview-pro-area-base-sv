package com.walnus.smartview.tooling

import java.lang.reflect.InvocationTargetException

/**
 * Minimal JVM test runner for environments without Gradle or JUnit.
 *
 * The test sources themselves use standard `kotlin.test` annotations and assertions, so
 * the same classes run unchanged under JUnit once the Gradle build is available. This
 * runner only discovers and invokes them so the domain layer can be verified where a
 * full Android toolchain is unavailable.
 *
 * Usage: TestRunnerKt <fully.qualified.TestClass> [...]
 */
object TestRunner {

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isEmpty()) {
            System.err.println("No test classes supplied")
            kotlin.system.exitProcess(2)
        }

        var passed = 0
        val failures = mutableListOf<String>()

        for (className in args) {
            val testClass = Class.forName(className)
            val instance = testClass.getDeclaredConstructor().newInstance()
            val testMethods = testClass.methods
                .filter { method -> method.isTestAnnotated() }
                .sortedBy { it.name }

            if (testMethods.isEmpty()) {
                failures += "$className: no @Test methods discovered"
                continue
            }

            for (method in testMethods) {
                val label = "${testClass.simpleName}.${method.name}"
                try {
                    method.invoke(instance)
                    passed++
                    println("PASS  $label")
                } catch (error: InvocationTargetException) {
                    val cause = error.targetException
                    failures += "$label: ${cause::class.java.simpleName}: ${cause.message}"
                    println("FAIL  $label")
                } catch (error: Throwable) {
                    failures += "$label: ${error::class.java.simpleName}: ${error.message}"
                    println("FAIL  $label")
                }
            }
        }

        println()
        println("Tests run: ${passed + failures.size}, passed: $passed, failed: ${failures.size}")
        if (failures.isNotEmpty()) {
            println()
            failures.forEach { println("FAILURE: $it") }
            kotlin.system.exitProcess(1)
        }
        kotlin.system.exitProcess(0)
    }

    private fun java.lang.reflect.Method.isTestAnnotated(): Boolean =
        annotations.any {
            val name = it.annotationClass.qualifiedName
            name == "kotlin.test.Test" || name == "org.junit.Test" || name == "org.junit.jupiter.api.Test"
        }
}
