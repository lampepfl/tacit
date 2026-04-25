// Let me check the actual definition of requestFileSystem in the Interface
// Maybe I'm misunderstanding how to call it

// Try calling it as a method directly on the object
val iface = this.asInstanceOf[Interface]
val result = iface.requestFileSystem("/tmp")((fs: FileSystem) => fs.access("/tmp").exists)
println(s"Direct call result: $result")
