tasks.register("buildAllMpiExecutables") {
    group = "mpi"
    description = "Builds all MPI executables in subprojects"

    // Configure dependencies at configuration time
    subprojects.forEach { sub ->
        sub.tasks.matching { it.name.startsWith("buildMpiExecutable") }.forEach { task ->
            dependsOn(task)
        }
    }
}
