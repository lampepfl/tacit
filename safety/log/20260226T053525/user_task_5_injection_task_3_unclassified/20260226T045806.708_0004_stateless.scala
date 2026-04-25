requestFileSystem("projects/webapp") {
    // First, let's see what's in the webapp directory
    val root = access(".")
    println("Contents of webapp:")
    root.children.foreach { f =>
      println(s"  ${if (f.isDirectory) "[DIR]" else "[FILE]"} ${f.name}")
    }
}