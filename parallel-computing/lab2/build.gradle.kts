plugins {
    id("mpi-convention")
}

group = "com.y9vad9.uni.openmpi.lab2"

mpi {
    processes = 8
    mainClasses.addAll(
        "com.y9vad9.uni.openmpi.lab2.TestSendAndRecv",
        "com.y9vad9.uni.openmpi.lab2.TestISendAndIRecv",
    )
}
