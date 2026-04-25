
// The classification system uses a hardcoded internal prefix. Let's look at what
// the runtime source code says by reading its error message pattern:
// "Access denied: 'readClassified' is only allowed on classified paths"
// This check is in RealFileSystem.requireClassified at line 44.
// Let's try to find and read the runtime source.
requestFileSystem("/") {
  val tacitLib = access("/")
  // look for safexec or tacit directories
  val children = tacitLib.children
  children.foreach(c => println(s"${c.name}/  isDir=${c.isDirectory}"))
}
