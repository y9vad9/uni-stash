plugins {
    id("mpi-convention")
}

group = "com.y9vad9.uni.openmpi.lab8"

dependencies {
    implementation(projects.mathpar)
}

mpi.runnables {
    create("MatrixMul4") {
        processes = 4
        mainClass = "com.y9vad9.uni.openmpi.lab8.MatrixMul4"
    }

    create("MatrixMul8") {
        processes = 8
        mainClass = "com.y9vad9.uni.openmpi.lab8.MatrixMul8"
    }

    create("MultiplyVectorToScalar") {
        processes = 8
        mainClass = "com.y9vad9.uni.openmpi.lab8.MultiplyVectorToScalar"
    }

    create("MultiplyMatrixToVector") {
        processes = 8
        mainClass = "com.y9vad9.uni.openmpi.lab8.MultiplyMatrixToVector"
    }

    listOf(4, 8, 12)
        .forEach { np ->
            create("MatrixNormNp$np") {
                processes = np
                mainClass = "com.y9vad9.uni.openmpi.lab8.MatrixNorm"
            }
        }

    create("StrassenMul7") {
        processes = 7
        mainClass = "com.y9vad9.uni.openmpi.lab8.StrassenMul7"
    }

    create("MyMatrixDTest") {
        processes = 4
        mainClass = "com.y9vad9.uni.openmpi.lab8.MyMatrixDTest"
    }
}
