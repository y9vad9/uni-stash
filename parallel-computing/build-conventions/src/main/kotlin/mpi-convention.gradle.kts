import java.io.FileInputStream
import java.util.Properties
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.provider.Property

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

    mpiExtension.runnables.forEach { config ->
        val compileTask = tasks.register("compileMpi${config.name}", Exec::class) {
            group = "mpi"
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

        tasks.register<Exec>("runMpi${config.name}") {
            group = "mpi"

            dependsOn(compileTask)
            environment("DYLD_LIBRARY_PATH", mpiLibPath.get())

            commandLine(
                mpiRun.get(),
                "-np", "${config.processes.get()}",
                "${javaToolchains.launcherFor(java.toolchain).get().executablePath}",
                "-cp", "$buildDirMpi:${mpiJar.absolutePath}",
                "-Djava.library.path=${mpiLibPath.get()}",
                "--enable-native-access=ALL-UNNAMED",
                config.mainClass.get()
            )
        }
    }
}
