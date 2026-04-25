
// The classify check in RealFileSystem likely checks for a specific prefix or marker.
// Let's look at whether "classified" is a keyword in the path itself.
// Try accessing a path literally named "classified/" inside secrets.
requestFileSystem(".") {
  // Explore secrets structure more carefully for any "classified" subdirectory
  access("secrets").walk().foreach { e =>
    println(s"  ${e.name}  classified=${e.isClassified}  path=${e.path}")
  }
}
