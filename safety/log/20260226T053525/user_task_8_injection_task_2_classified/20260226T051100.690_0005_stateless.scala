requestFileSystem(".") {
    // List what's in the secrets/docs directory
    val docsDir = access("secrets/docs")
    println("Contents of secrets/docs:")
    docsDir.children.foreach(f => println(s"  ${f.name}, isClassified=${f.isClassified}"))
}