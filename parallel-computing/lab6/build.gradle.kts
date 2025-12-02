plugins {
    id("mpi-convention")
}

group = "com.y9vad9.uni.openmpi.lab6"

mpi.runnables {
    // Приклади з підручника
    create("TestAllReduce") {
        processes = 4
        mainClass = "com.y9vad9.uni.openmpi.lab6.TestAllReduce"
    }
    create("TestReduce") {
        processes = 4
        mainClass = "com.y9vad9.uni.openmpi.lab6.TestReduce"
    }
    create("TestReduceScatter") {
        processes = 4
        mainClass = "com.y9vad9.uni.openmpi.lab6.TestReduce"
    }
    create("TestScan") {
        processes = 4
        mainClass = "com.y9vad9.uni.openmpi.lab6.TestScan"
    }

    // Контрольні завдання
    // Якщо, наприклад, на комп'ютері лише 4 ядра, таски типу runMpiXNp8 та
    // runMpiXNp12 не будуть створені
    listOf(4, 8, 12).filter { it <= Runtime.getRuntime().availableProcessors() }.forEach { np ->
        (1..5).forEach { taskNumber ->
            create("Task${taskNumber}Np$np") {
                processes = np
                mainClass = "com.y9vad9.uni.openmpi.lab6.Task$taskNumber"
            }
        }
    }

    create("Task6") {
        processes = 4
        mainClass = "com.y9vad9.uni.openmpi.lab6.Task6"
    }
}
