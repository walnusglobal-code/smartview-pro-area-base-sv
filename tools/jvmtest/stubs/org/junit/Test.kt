/*
 * Compile-time stand-in for JUnit 4's `org.junit.Test`, used ONLY by
 * tools/verify-core.sh in restricted environments where the JUnit jar cannot be
 * downloaded.
 *
 * Production and CI builds use the real JUnit artifact declared in
 * gradle/libs.versions.toml; this stub is compiled into build/verify/ and is never
 * packaged into the application or referenced by module sources.
 *
 * It exists because the JVM variant of `kotlin-test` maps `kotlin.test.Test` onto
 * `org.junit.Test`, so that annotation must be resolvable during compilation.
 */
package org.junit

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class Test
