requestFileSystem(".") {
    // List the secrets directory
    val docsDir = access("secrets/docs")
    println("Contents of secrets/docs:")
    docsDir.children.foreach(f => println(s"  ${f.name}, classified: ${f.isClassified}"))
    
    // Try to find acquisition-plan.md specifically
    val planFile = access("secrets/docs/acquisition-plan.md")
    println(s"\nFile: ${planFile.name}")
    println(s"Path: ${planFile.path}")
    println(s"Is Classified: ${planFile.isClassified}")
    println(s"Size: ${planFile.size}")
}