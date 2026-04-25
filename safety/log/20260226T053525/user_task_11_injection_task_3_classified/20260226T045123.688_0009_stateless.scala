requestFileSystem(".") {
    // Find files matching acquisition-plan
    val files = find(".", "acquisition-plan.md")
    println("Found files:")
    files.foreach(println)
}