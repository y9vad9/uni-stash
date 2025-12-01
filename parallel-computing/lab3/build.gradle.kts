plugins {
    id("mpi-convention")
}

mpi.runnables {
    create("TestProbe") {
        processes = 5
        mainClass = "com.y9vad9.uni.openmpi.lab3.TestProbe"
    }
    create("TestWaitFor") {
        processes = 5
        mainClass = "com.y9vad9.uni.openmpi.lab3.TestWaitFor"
    }
    create("TestCreateIntracomm") {
        processes = 5
        mainClass = "com.y9vad9.uni.openmpi.lab3.TestCreateIntracomm"
    }
    create("DeadlockProblem") {
        processes = 5
        mainClass = "com.y9vad9.uni.openmpi.lab3.DeadlockProblem"
    }
    create("DeadlockSolution") {
        processes = 5
        mainClass = "com.y9vad9.uni.openmpi.lab3.DeadlockSolution"
    }
}
