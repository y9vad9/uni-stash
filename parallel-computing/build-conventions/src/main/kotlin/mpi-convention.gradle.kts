import java.io.FileInputStream
import java.util.*

plugins {
    java
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

abstract class MpiRunnableExtension @Inject constructor(private val name: String) : Named {
    abstract val processes: Property<Int>
    abstract val mainClass: Property<String>

    override fun getName(): String {
        return name
    }
}

abstract class MpiExtension @Inject constructor(project: Project) {
    val runnables: NamedDomainObjectContainer<MpiRunnableExtension> =
        project.container(MpiRunnableExtension::class.java) { name ->
            project.objects.newInstance(MpiRunnableExtension::class.java, name)
        }

    fun runnables(configure: NamedDomainObjectContainer<MpiRunnableExtension>.() -> Unit) {
        runnables.configure()
    }
}

val mpiExtension = project.extensions.create("mpi", MpiExtension::class.java, project)

val fatJar = tasks.register<Jar>("fatJar") {
    group = "build"
    archiveClassifier.set("all") // або "" щоб замінити jar

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

afterEvaluate {
    val mpiRun = providers.environmentVariable("MPI_BIN")
        .orElse(provider { localProps.getProperty("mpi.bin") + "/mpirun" })

    val mpiLibPath = providers.environmentVariable("MPI_LIB")
        .orElse(provider { localProps.getProperty("mpi.lib") })

    mpiExtension.runnables.forEach { config ->
        tasks.register<Exec>("runMpi${config.name}") {
            group = "mpi"

            dependsOn(tasks.withType<JavaCompile>().first())
            environment("DYLD_LIBRARY_PATH", mpiLibPath.get())

            val runtimeCp = sourceSets.main.get().runtimeClasspath.asPath

            commandLine(
                mpiRun.get(),
                "-np", "${config.processes.get()}",
                "--hostfile", "${rootProject.file("hostfile").absolutePath}",
                "--bind-to", "none",
                "${javaToolchains.launcherFor(java.toolchain).get().executablePath}",
                "-cp", runtimeCp,
                "-Djava.library.path=${mpiLibPath.get()}",
                "--enable-native-access=ALL-UNNAMED",
                config.mainClass.get()
            )
        }

        tasks.register("buildMpiExecutable${config.name}") {
            group = "mpi"

            dependsOn(fatJar)

            val jarFile = fatJar.flatMap { it.archiveFile }
            val outputFile = project.file("executables/${config.name}")
            val processes = config.processes
            val mainClass = config.mainClass

            doLast {
                val output = outputFile
                output.parentFile.mkdirs()
                val jar = jarFile.get().asFile

                val script = buildString {
                    appendLine("#!/bin/bash")
                    appendLine("TMPDIR=$(mktemp -d)")
                    appendLine("tail -n +11 \"\$0\" > \"\$TMPDIR/app.jar\"")
                    appendLine("HOSTFILE=\"\$HOSTFILE\"")
                    appendLine("env DYLD_LIBRARY_PATH=\"\$DYLD_LIBRARY_PATH\" mpirun -np ${processes.get()} \\")
                    appendLine("    \${HOSTFILE:+--hostfile \$HOSTFILE} \\")
                    appendLine("    --bind-to none java -cp \$TMPDIR/app.jar -Djava.library.path=\$DYLD_LIBRARY_PATH \\")
                    appendLine("    --enable-native-access=ALL-UNNAMED ${mainClass.get()}")
                    appendLine("exit 0")
                    appendLine("__PAYLOAD__")
                }

                output.outputStream().use { out ->
                    out.write(script.toByteArray(Charsets.UTF_8))
                    jar.inputStream().use { input ->
                        input.copyTo(out)
                    }
                }

                output.setExecutable(true)
            }
        }
    }
}

