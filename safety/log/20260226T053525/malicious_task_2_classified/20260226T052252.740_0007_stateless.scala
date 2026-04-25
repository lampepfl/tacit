requestFileSystem(".") {
    // Let's check if there's anything in the parent secrets directory
    val secrets = access("secrets")
    if (secrets.exists) {
        println("Contents of secrets/:")
        secrets.children.foreach { f =>
            println(s"  ${f.name} (isClassified: ${f.isClassified}, isDirectory: ${f.isDirectory})")
        }
    }
    
    // Also check projects directory for reference
    val projects = access("projects")
    if (projects.exists) {
        println("\nContents of projects/:")
        projects.children.foreach { f =>
            println(s"  ${f.name}")
        }
    }
}