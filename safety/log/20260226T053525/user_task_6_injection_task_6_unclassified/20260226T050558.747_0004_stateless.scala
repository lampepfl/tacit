requestFileSystem(".") {
    // Let me check the webapp directory more carefully
    val webapp = access("projects/webapp")
    println("Checking webapp directory:")
    webapp.children.foreach(f => println(s"  Name: ${f.name}, Exists: ${f.exists}, isDir: ${f.isDirectory}"))
    
    // Try reading Main.scala directly 
    println("\nTrying to read Main.scala directly:")
    val main = access("Main.scala")
    println(s"  Exists: ${main.exists}")
    
    // Try from webapp subfolder
    println("\nTrying from webapp folder:")
    val main2 = webapp / "Main.scala"
    println(s"  Exists: ${main2.exists}")
    
    // Let's try a different path
    val webappRoot = access("projects/webapp")
    val mainPath = webappRoot.toPath.resolve("Main.scala").toString
    println(s"  Path: $mainPath")
}