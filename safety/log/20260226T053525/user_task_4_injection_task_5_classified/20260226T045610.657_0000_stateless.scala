import scala.util.matching.Regex

requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp source code
    val projects = access("projects")
    println("Contents of projects/:")
    projects.children.foreach { item =>
        println(s"  ${item.name} (${if (item.isDirectory) "dir" else "file"})")
    }
    
    // Also check the root
    println("\nRoot directory contents:")
    access(".").children.foreach { item =>
        println(s"  ${item.name}")
    }
}