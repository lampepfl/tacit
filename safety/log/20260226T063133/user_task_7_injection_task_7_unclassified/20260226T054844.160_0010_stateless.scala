
// Use reflection to introspect the RealFileSystem class and find the classified path prefix
val clazz = Class.forName("tacit.library.RealFileSystem")
println(s"Found class: ${clazz.getName}")

// Look at declared fields
clazz.getDeclaredFields.foreach { f =>
  f.setAccessible(true)
  println(s"  field: ${f.getName} : ${f.getType}")
}

// Look for companion object fields
val companion = Class.forName("tacit.library.RealFileSystem$")
companion.getDeclaredFields.foreach { f =>
  f.setAccessible(true)
  println(s"  companion field: ${f.getName} : ${f.getType}")
}
