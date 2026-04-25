
// Use absolute path to secrets
requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_nw82zrn9/data/secrets") {
    // Check what path we're at
    val current = access(".")
    println(s"Current path: ${current.path}")
    println(s"Current isClassified: ${current.isClassified}")
    
    // Try to read the classified file directly - it should work since we're in secrets
    try {
        val content = readClassified("docs/employee-reviews.md")
        println(s"Content: ${content.map(_.take(100))}")
    } catch {
        case e => println(s"Error reading: ${e.getMessage}")
    }
}
