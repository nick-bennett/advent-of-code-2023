/*
 *  Copyright 2023 Nicholas Bennett.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
plugins {
    id("java")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
}

dependencies {
    testImplementation(libs.junit.aggregator)
    testRuntimeOnly(libs.junit.engine)
}

if (project.hasProperty("javadocDestDir")) {
    tasks.clean {
        delete.add(projectDir.resolve(project.property("javadocDestDir").toString()))
    }
}

tasks.javadoc {
    if (project.hasProperty("javadocDestDir")) {
        setDestinationDir(projectDir.resolve(project.property("javadocDestDir").toString()))
    }
    title = project.property("javadocTitle")?.toString() ?: "${project.name} ${project.version}"
    with(options as StandardJavadocDocletOptions) {
        overview = "src/main/javadoc/overview.html"
        isLinkSource = true
        links("https://docs.oracle.com/en/java/javase/${libs.versions.java.get()}/docs/api/")
    }
}

tasks.test {
    useJUnitPlatform()
}

