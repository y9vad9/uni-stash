plugins {
    id("mpi-convention")
}

group = "com.y9vad9.uni.openmpi.lab8"

dependencies {
    implementation(projects.mathpar)
}

mpi.runnables {
    create("TestDetMPI16") {
        processes = 16
        mainClass = "com.y9vad9.uni.openmpi.finale.TestDetMPI16"
    }
}
