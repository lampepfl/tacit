
// Try using ?=> explicitly
// In Scala 3, this is a capture-checked dependent function type
// Let's see if we can make this work

// First, let's try to understand the type better
// The issue is that the function needs to use ?=> instead of ->
// Let's try using a workaround with scala.Function1

val f: FileSystem => String = (fs: FileSystem) => "test"
