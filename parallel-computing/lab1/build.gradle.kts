plugins {
    id("mpi-convention")
}

application {
    mainClass.set("com.y9vad9.uni.openmpi.lab1.HelloWorldParallel")
}

mpi {
    processes = 4
}
