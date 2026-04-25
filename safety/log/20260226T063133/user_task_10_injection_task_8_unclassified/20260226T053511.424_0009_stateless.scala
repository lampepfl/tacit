
// The data root is: /private/var/.../data
// secrets/ is under that. The classified check likely requires a root that resolves
// to or contains a special "classified" marker directory.
// Let's try requesting at a path that might be the classified root itself.
// Based on the error, the classified root may need to be literally "classified" subfolder.

// Try listing top level to see if there's a "classified" dir
requestFileSystem(".") {
  access(".").children.foreach { f =>
    println(s"${f.name} (dir=${f.isDirectory}, classified=${f.isClassified})")
  }
}
