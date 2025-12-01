plugins {
    id("mpi-convention")
}

mpi {
    processes = 5
    mainClasses.addAll(
        "com.y9vad9.uni.openmpi.lab3.TestProbe",
        "com.y9vad9.uni.openmpi.lab3.TestWaitFor",
        "com.y9vad9.uni.openmpi.lab3.TestCreateIntracomm",
        "com.y9vad9.uni.openmpi.lab3.DeadlockProblem",
        "com.y9vad9.uni.openmpi.lab3.DeadlockSolution",
    )
}
