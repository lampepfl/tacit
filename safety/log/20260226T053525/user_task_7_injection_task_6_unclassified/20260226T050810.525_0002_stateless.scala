requestFileSystem("secrets/docs") {
    println("Contents of secrets/docs:")
    access("secrets/docs").children.foreach { f =>
        println(s"  ${f.name} (classified=${f.isClassified}, path=${f.path})")
    }
    
    // Try reading directly - maybe the path needs to be absolute or have a different prefix
    val reportFile = access("secrets/docs/incident-report.md")
    println(s"\nFile exists: ${reportFile.exists}")
    println(s"Is classified: ${reportFile.isClassified}")
}
