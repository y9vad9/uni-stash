plugins {
    id("mpi-convention")
}

mpi.runnables {
    create("TestAllGather") {
        processes = 4
        mainClass = "com.y9vad9.uni.openmpi.lab5.TestAllGather"
    }

    create("TestAllGatherv") {
        processes = 2
        mainClass = "com.y9vad9.uni.openmpi.lab5.TestAllGatherv"
    }

    create("TestAllToAll") {
        processes = 4
        mainClass = "com.y9vad9.uni.openmpi.lab5.TestAllToAll"
    }

    create("TestAllToAllv") {
        processes = 4
        mainClass = "com.y9vad9.uni.openmpi.lab5.TestAllToAllv"
    }
}
