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

application {
    mainClass.set("")
}

abstract class MpiExtension(project: Project) {
    val processes: Property<Int> = project.objects.property(Int::class.java)
        .convention(4)
    val mainClasses: ListProperty<String> = project.objects.listProperty()
}

val mpiExtension = project.extensions.create<MpiExtension>("mpi")

afterEvaluate {
    val mpiJavaCompiler = providers.environmentVariable("MPI_BIN")
        .orElse(provider { localProps.getProperty("mpi.bin") + "/mpijavac" })

    val mpiRun = providers.environmentVariable("MPI_BIN")
        .orElse(provider { localProps.getProperty("mpi.bin") + "/mpirun" })

    val mpiLibPath = providers.environmentVariable("MPI_LIB")
        .orElse(provider { localProps.getProperty("mpi.lib") })

    val buildDirectory = layout.buildDirectory.get()
    val buildDirMpi = "${buildDirectory.asFile.absolutePath}/classes-mpi"
    val mpiJar = rootProject.file("libs/mpi.jar")

    val classes = mpiExtension.mainClasses.get().ifEmpty {
        listOf(application.mainClass.get()).filter { it.isNotBlank() }
    }

    if (classes.size == 1) {
        val compileMpi by tasks.registering(Exec::class) {
            val srcFiles = sourceSets.main.get().allJava.files.map { it.absolutePath }

            inputs.files(sourceSets.main.get().allJava)
            outputs.dir(buildDirMpi)

            commandLine(
                mpiJavaCompiler.get(),
                "-d", buildDirMpi,
                "-classpath", sourceSets.main.get().compileClasspath.asPath + ":" + mpiJar.absolutePath,
                *srcFiles.toTypedArray()
            )
        }

        tasks.register<Exec>("runMpi") {
            dependsOn(compileMpi)
            environment("DYLD_LIBRARY_PATH", mpiLibPath.get())

            commandLine(
                mpiRun.get(),
                "-np", "${mpiExtension.processes.get()}",
                "${javaToolchains.launcherFor(java.toolchain).get().executablePath}",
                "-cp", "$buildDirMpi:${mpiJar.absolutePath}",
                "-Djava.library.path=${mpiLibPath.get()}",
                "--enable-native-access=ALL-UNNAMED",
                classes.first()
            )
        }
    } else {
        classes.forEach { cls ->
            val name = cls.substringAfterLast('.')

            val compileMpi = tasks.register("compileMpi$name", Exec::class) {
                val srcFiles = sourceSets.main.get().allJava.files.map { it.absolutePath }

                inputs.files(sourceSets.main.get().allJava)
                outputs.dir(buildDirMpi)

                commandLine(
                    mpiJavaCompiler.get(),
                    "-d", buildDirMpi,
                    "-classpath", sourceSets.main.get().compileClasspath.asPath + ":" + mpiJar.absolutePath,
                    *srcFiles.toTypedArray()
                )
            }

            tasks.register<Exec>("runMpi$name") {
                dependsOn(compileMpi)
                environment("DYLD_LIBRARY_PATH", mpiLibPath.get())

                commandLine(
                    mpiRun.get(),
                    "-np", "${mpiExtension.processes.get()}",
                    "${javaToolchains.launcherFor(java.toolchain).get().executablePath}",
                    "-cp", "$buildDirMpi:${mpiJar.absolutePath}",
                    "-Djava.library.path=${mpiLibPath.get()}",
                    "--enable-native-access=ALL-UNNAMED",
                    cls
                )
            }
        }
    }
}
