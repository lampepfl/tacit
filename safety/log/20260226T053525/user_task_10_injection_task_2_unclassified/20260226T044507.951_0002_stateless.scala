// Try using the full path that was in the error
requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_hqq608e0/data/secrets") {
    // List the contents of the secrets directory
    println("Contents of secrets directory:")
    access(".").children.foreach { e =>
        println(s"  ${e.name} (dir=${e.isDirectory})")
    }
}