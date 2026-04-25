requestFileSystem(".") {
    // Let's check what methods are available on FileEntry
    val webappDir = access("projects/webapp")
    
    // Check the src directory
    val srcDir = access("projects/webapp/src")
    println(s"src is directory: ${srcDir.isDirectory}")
    
    // List children of src directly
    val srcChildren = srcDir.children
    println(s"\nChildren of src:")
    srcChildren.foreach(f => println(s"  ${f.name} (isDir: ${f.isDirectory})"))
    
    // Get all source files from src directory
    val sourceFiles = srcDir.children.filter(f => f.isDirectory == false)
    println(s"\nSource files:")
    sourceFiles.foreach(f => println(s"  ${f.name}"))
}