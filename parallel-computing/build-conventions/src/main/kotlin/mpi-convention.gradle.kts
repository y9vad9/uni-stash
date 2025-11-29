import java.io.FileInputStream
import java.util.Properties

plugins {
    java
    application
}

val localProps = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(FileInputStream(file))
}

dependencies {
    implementation(files(rootProject.files("libs/mpi.jar")))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}


abstract class MpiExtension(project: Project) {
    val processes: Property<Int> = project.objects.property(Int::class.java)
        .convention(4)
}

val mpiExtension = project.extensions.create<MpiExtension>("mpi")

val mpiJavaCompiler = providers.environmentVariable("MPI_BIN")
    .orElse(provider { localProps.getProperty("mpi.bin") + "/mpijavac" })

val mpiRun = providers.environmentVariable("MPI_BIN")
    .orElse(provider { localProps.getProperty("mpi.bin") + "/mpirun" })

val mpiLibPath = providers.environmentVariable("MPI_LIB")
    .orElse(provider { localProps.getProperty("mpi.lib") })

val buildDirMpi = "${layout.buildDirectory.get().asFile.absolutePath}/classes-mpi"

val compileMpi by tasks.registering(Exec::class) {
    val srcFiles = sourceSets.main.get().allJava.files.map { it.absolutePath }

    inputs.files(sourceSets.main.get().allJava)
    outputs.dir(buildDirMpi)

    commandLine(
        mpiJavaCompiler.get(),
        "-d", buildDirMpi,
        "-classpath", sourceSets.main.get().compileClasspath.asPath,
        *srcFiles.toTypedArray()
    )
}

val mpiProcesses: Provider<Int> = project.providers.gradleProperty("mpiProcesses")
    .map { it.toInt() }
    .orElse(4) // default if not specified

val runMpi by tasks.registering(Exec::class) {
    dependsOn(compileMpi)

    environment("DYLD_LIBRARY_PATH", mpiLibPath.get())

    val mainCls = application.mainClass.get()
    commandLine(
        mpiRun.get(),
        "-np", "${mpiExtension.processes.get()}",
        "${javaToolchains.launcherFor(java.toolchain).get().executablePath}",
        "-cp", "$buildDirMpi:${sourceSets.main.get().runtimeClasspath.asPath}",
        "-Djava.library.path=${mpiLibPath.get()}",
        "--enable-native-access=ALL-UNNAMED",
        mainCls
    )
}
