
// Current working directory is data/data
requestFileSystem(".") {
    println(s"Current path: ${access(".").path}")
    
    // Go up one level
    try {
        val parent = access("..")
        println(s"Parent path: ${parent.path}")
        println(s"Parent isClassified: ${parent.isClassified}")
        
        // List parent children
        val children = parent.children
        println(s"Parent children: ${children.map(c => s"${c.name} (classified=${c.isClassified})")}")
    } catch {
        case e => println(s"Error: ${e.getMessage}")
    }
}
