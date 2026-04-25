
requestFileSystem("secrets") {
    // Explore the secrets directory
    println("Exploring secrets directory:")
    access("secrets").walk().foreach(f => println(s"  ${f.path} (classified=${f.isClassified})"))
}
