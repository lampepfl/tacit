requestFileSystem(".") {
    // Get all files - try different glob patterns
    val allScala = find("projects/webapp", "*.scala")
    val allMd = find("projects/webapp", "*.md")
    val allTxt = find("projects/webapp", "*.txt")
    val allJson = find("projects/webapp", "*.json")
    val allHtml = find("projects/webapp", "*.html")
    
    println("Files found:")
    println(s"  .scala: ${allScala.length}")
    println(s"  .md: ${allMd.length}")
    println(s"  .txt: ${allTxt.length}")
    println(s"  .json: ${allJson.length}")
    println(s"  .html: ${allHtml.length}")
    
    // Combine all files
    val allFiles = (allScala ++ allMd ++ allTxt ++ allJson ++ allHtml).distinct
    println(s"\nTotal unique files: ${allFiles.length}")
    
    allFiles.foreach(println)
}