plugins {
    id("mpi-convention")
}

group = "com.y9vad9.uni.openmpi.lab2"

application {
    mainClass.set("com.y9vad9.uni.openmpi.lab2.HelloWorldParallel")
}

mpi {
    processes = 4
}
