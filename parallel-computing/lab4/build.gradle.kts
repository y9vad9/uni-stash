plugins {
    id("mpi-convention")
}

mpi {
    processes = 4
    mainClasses.addAll(
        "com.y9vad9.uni.openmpi.lab4.Lab4",
        "com.y9vad9.uni.openmpi.lab4.TestGather",
        "com.y9vad9.uni.openmpi.lab4.TestGatherv",
        "com.y9vad9.uni.openmpi.lab4.TestScatter",
        "com.y9vad9.uni.openmpi.lab4.TestScatterv",
    )
}
