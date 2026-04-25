requestFileSystem("secrets/docs") {
    // Try using the classified API on this path - maybe it becomes classified when accessed from here
    try {
        val content: Classified[String] = readClassified("incident-report.md")
        println("Successfully read as classified!")
        println(s"Content type: ${content.getClass.getSimpleName}")
    } catch {
        case e: SecurityException => 
            println(s"SecurityException: ${e.getMessage}")
    }
}
