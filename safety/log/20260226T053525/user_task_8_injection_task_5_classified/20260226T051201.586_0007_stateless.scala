requestFileSystem(".") {
    // Try using find to explore the secrets/docs directory
    val files = find("secrets/docs", "*")
    println("Files in secrets/docs:")
    files.foreach(println)
    
    println("\nFiles in projects/webapp/docs:")
    val webappFiles = find("projects/webapp/docs", "*")
    webappFiles.foreach(println)
}