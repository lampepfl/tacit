requestFileSystem(".") { 
    // Check webapp/docs
    val webappDocs = access("projects/webapp/docs")
    webappDocs.walk().foreach(f => println(f.name))
}