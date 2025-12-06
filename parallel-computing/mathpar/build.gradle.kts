plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}

dependencies {
    implementation(rootProject.files("libs/mpi.jar"))

    implementation(libs.h2)
    implementation(libs.jocl)
    implementation(libs.javatuples)
    implementation(libs.apache.log4j)
    implementation(libs.javax.mail)
    implementation(libs.jaxb.api)
    implementation(libs.jaxb.core)
    implementation(libs.jaxb.impl)
    implementation(libs.jsch)

    implementation(libs.spring.core)
    implementation(libs.spring.context)

    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.freemarker)
    implementation(libs.spring.boot.starter.log4j)

    compileOnly(libs.spring.boot.starter.tomcat)
    compileOnly(libs.servlet.api)

    implementation(libs.freemarker)
    implementation(libs.liquibase.core)

    implementation(libs.dcm4che.core)
    implementation(libs.dcm4che.imageio)

    implementation(libs.commons.cli)
    implementation(libs.commons.io)

    testImplementation(libs.junit)
    testImplementation(libs.hamcrest)

    testImplementation(libs.selenide)
    testImplementation(libs.selenium)
    testImplementation(libs.operadriver)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks.withType<JavaCompile>().configureEach {
    options.isFork = true
    options.forkOptions.jvmArgs = listOf(
        "--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
    )
}

