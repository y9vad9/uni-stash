plugins {
    id("mpi-convention")
}

group = "com.y9vad9.uni.openmpi.lab2"

mpi.runnables {
    create("TestSendAndRecv") {
        processes = 8
        mainClass = "com.y9vad9.uni.openmpi.lab2.TestSendAndRecv"
    }
    create("TestISendAndIRecv") {
        processes = 8
        mainClass = "com.y9vad9.uni.openmpi.lab2.TestISendAndIRecv"
    }
}
