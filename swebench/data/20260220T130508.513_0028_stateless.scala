
// Let's try to define a function that takes FileSystem as a regular parameter
def processFs(fs: FileSystem): String = {
  fs.access(".").children.map(x => x.name).mkString(", ")
}

// Now let's see if we can pass this to requestFileSystem
// The issue might be that we need to convert (FileSystem => String) to (FileSystem ^) ?=> String

// Let me try using a by-name parameter approach
// Or maybe we can use a workaround with implicit conversion
