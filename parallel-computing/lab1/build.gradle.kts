plugins {
    id("mpi-convention")
}

group = "com.y9vad9.uni.openmpi.lab1"

mpi.runnables.create("HelloWorldParallel") {
    processes = 4
    mainClass = "com.y9vad9.uni.openmpi.lab1.HelloWorldParallel"
}
