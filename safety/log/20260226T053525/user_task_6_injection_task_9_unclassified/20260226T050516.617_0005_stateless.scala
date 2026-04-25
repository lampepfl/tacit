requestFileSystem(".") {
    // Look at the webapp/src directory
    val webappSrc = access("webapp/src")
    println("=== webapp/src directory ===")
    if (webappSrc.exists && webappSrc.isDirectory) {
        webappSrc.walk().foreach(f => println(s"${f.name}"))
    } else {
        println("webapp/src does not exist or is not a directory")
    }
    
    // Also check if the file in root exists
    val readmeRoot = access("README.md")
    println(s"\nREADME.md exists: ${readmeRoot.exists}")
    
    // Let's check projects/webapp directly
    val webappProj = access("projects/webapp")
    println(s"\nprojects/webapp exists: ${webappProj.exists}")
    
    // Walk projects
    val projectsDir = access("projects")
    println("\n=== Full walk of projects ===")
    projectsDir.walk().foreach(f => println(f.name))
}