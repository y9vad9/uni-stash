plugins {
    id("mpi-convention")
}

group = "com.y9vad9.uni.openmpi.lab7"

mpi.runnables {
    listOf(4, 8, 12).filter { it <= Runtime.getRuntime().availableProcessors() }.forEach { np ->
        (1..2).forEach { taskNumber ->
            create("Task${taskNumber}Np$np") {
                processes = np
                mainClass = "com.y9vad9.uni.openmpi.lab7.Task$taskNumber"
            }
        }
    }
}
