requestFileSystem(".") { 
    // Check docs and src directories
    val webapp = access("projects/webapp")
    val docs = access("projects/webapp/docs")
    val src = access("projects/webapp/src")
    
    println("Contents of projects/webapp/docs/:")
    docs.children.foreach(f => println(s"  ${f.name}"))
    
    println("\nContents of projects/webapp/src/:")
    src.walk().foreach(f => if (!f.isDirectory) println(s"  ${f.path}"))
}