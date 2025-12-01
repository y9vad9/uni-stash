plugins {
    id("mpi-convention")
}

mpi.runnables {
    create("Lab4") {
        processes = 4
        mainClass = "com.y9vad9.uni.openmpi.lab4.Lab4"
    }
    create("TestGather") {
        processes = 4
        mainClass = "com.y9vad9.uni.openmpi.lab4.TestGather"
    }
    create("TestGatherv") {
        processes = 4
        mainClass = "com.y9vad9.uni.openmpi.lab4.TestGatherv"
    }
    create("TestScatter") {
        processes = 4
        mainClass = "com.y9vad9.uni.openmpi.lab4.TestScatter"
    }
    create("TestScatterv") {
        processes = 4
        mainClass = "com.y9vad9.uni.openmpi.lab4.TestScatterv"
    }
}
