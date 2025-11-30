plugins {
    id("mpi-convention")
}

group = "com.y9vad9.uni.openmpi.lab1"

application {
    mainClass.set("com.y9vad9.uni.openmpi.lab1.HelloWorldParallel")
}

mpi {
    processes = 4
}
